package com.tracalait.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatusBadge(text: String) {
    val (bgColor, textColor) = when (text) {
        "Accepté", "Distribué", "Livrée", "Responsable Qualité", "Actif", "Connexion", "En stock" ->
            GreenLight to GreenSuccess
        "Refusé", "Rappelé", "Annulée", "Inactif" ->
            RedLight to RedDanger
        "En attente", "En production", "Responsable Production" ->
            AmberLight to AmberWarning
        "Stocké", "Planifiée", "Administrateur" ->
            BlueLight to BlueAccent
        else -> Gray100 to Gray600
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun SectionHeaderCard(
    title: String,
    actionButtonText: String? = null,
    onActionClick: (() -> Unit)? = null,
    actionTag: String = "add_button",
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NavyPrimary
                )
                if (actionButtonText != null && onActionClick != null) {
                    Button(
                        onClick = onActionClick,
                        colors = ButtonDefaults.buttonColors(containerColor = BlueAccent),
                        shape = RoundedCornerShape(7.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.testTag(actionTag)
                    ) {
                        Text(text = actionButtonText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            HorizontalDivider(color = Gray100)
            Column(modifier = Modifier.fillMaxWidth(), content = content)
        }
    }
}

@Composable
fun EmptyStateView(iconEmoji: String, message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = iconEmoji, fontSize = 38.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = message,
            fontSize = 14.sp,
            color = Gray400,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ItemActionButtons(
    canWrite: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    extraLeadingButton: (@Composable () -> Unit)? = null
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        extraLeadingButton?.invoke()
        if (canWrite) {
            OutlinedIconButton(
                onClick = onEdit,
                modifier = Modifier.size(36.dp),
                shape = RoundedCornerShape(7.dp),
                border = BorderStroke(1.dp, Gray200)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Modifier",
                    tint = Gray600,
                    modifier = Modifier.size(16.dp)
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(RedDanger)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun DropdownSelector(
    label: String,
    options: List<Pair<String, String>>, // Pair(value, label)
    selectedValue: String,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val currentLabel = options.find { it.first == selectedValue }?.second
        ?: options.firstOrNull()?.second
        ?: ""

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = Gray600
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                shape = RoundedCornerShape(7.dp),
                border = BorderStroke(1.5.dp, Gray200),
                colors = CardDefaults.outlinedCardColors(containerColor = WhiteSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = currentLabel, fontSize = 14.sp, color = Gray800)
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Sélectionner",
                        tint = Gray600
                    )
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { (value, display) ->
                    DropdownMenuItem(
                        text = { Text(display) },
                        onClick = {
                            onValueChange(value)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LabeledTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    singleLine: Boolean = true,
    testTag: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = Gray600
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 13.5.sp, color = Gray400) },
            singleLine = singleLine,
            shape = RoundedCornerShape(7.dp),
            modifier = Modifier
                .fillMaxWidth()
                .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
        )
    }
}
