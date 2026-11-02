package com.tracalait.app.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.List;

public class UiHelper {

    public static final int COLOR_MILK = Color.parseColor("#F8F9FB");
    public static final int COLOR_WHITE = Color.parseColor("#FFFFFF");
    public static final int COLOR_NAVY = Color.parseColor("#1A3A5C");
    public static final int COLOR_BLUE = Color.parseColor("#2563EB");
    public static final int COLOR_BLUE_LT = Color.parseColor("#DBEAFE");
    public static final int COLOR_GREEN = Color.parseColor("#16A34A");
    public static final int COLOR_GREEN_LT = Color.parseColor("#DCFCE7");
    public static final int COLOR_AMBER = Color.parseColor("#D97706");
    public static final int COLOR_AMBER_LT = Color.parseColor("#FEF3C7");
    public static final int COLOR_RED = Color.parseColor("#DC2626");
    public static final int COLOR_RED_LT = Color.parseColor("#FEE2E2");
    public static final int COLOR_GRAY_50 = Color.parseColor("#F9FAFB");
    public static final int COLOR_GRAY_100 = Color.parseColor("#F3F4F6");
    public static final int COLOR_GRAY_200 = Color.parseColor("#E5E7EB");
    public static final int COLOR_GRAY_400 = Color.parseColor("#9CA3AF");
    public static final int COLOR_GRAY_600 = Color.parseColor("#4B5563");
    public static final int COLOR_GRAY_800 = Color.parseColor("#1F2937");

    public static int dp(Context context, float value) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                context.getResources().getDisplayMetrics()
        );
    }

    public static GradientDrawable roundedBg(Context context, int fillColor, float radiusDp, int strokeWidthDp, int strokeColor) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(fillColor);
        gd.setCornerRadius(dp(context, radiusDp));
        if (strokeWidthDp > 0) {
            gd.setStroke(dp(context, strokeWidthDp), strokeColor);
        }
        return gd;
    }

    public static TextView createBadge(Context context, String text) {
        int bg;
        int fg;
        switch (text != null ? text : "") {
            case "Accepté":
            case "Distribué":
            case "Livrée":
            case "Responsable Qualité":
            case "Actif":
            case "Connexion":
            case "En stock":
                bg = COLOR_GREEN_LT;
                fg = COLOR_GREEN;
                break;
            case "Refusé":
            case "Rappelé":
            case "Annulée":
            case "Inactif":
                bg = COLOR_RED_LT;
                fg = COLOR_RED;
                break;
            case "En attente":
            case "En production":
            case "Responsable Production":
                bg = COLOR_AMBER_LT;
                fg = COLOR_AMBER;
                break;
            case "Stocké":
            case "Planifiée":
            case "Administrateur":
                bg = COLOR_BLUE_LT;
                fg = COLOR_BLUE;
                break;
            default:
                bg = COLOR_GRAY_100;
                fg = COLOR_GRAY_600;
                break;
        }

        TextView tv = new TextView(context);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.5f);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setTextColor(fg);
        tv.setPadding(dp(context, 10), dp(context, 4), dp(context, 10), dp(context, 4));
        tv.setBackground(roundedBg(context, bg, 999f, 0, 0));
        return tv;
    }

    public static Button createPrimaryButton(Context context, String text, View.OnClickListener listener) {
        Button btn = new Button(context);
        btn.setText(text);
        btn.setAllCaps(false);
        btn.setTextColor(COLOR_WHITE);
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13.5f);
        btn.setTypeface(Typeface.DEFAULT_BOLD);
        btn.setBackground(roundedBg(context, COLOR_BLUE, 7f, 0, 0));
        btn.setPadding(dp(context, 14), dp(context, 8), dp(context, 14), dp(context, 8));
        btn.setMinimumHeight(dp(context, 40));
        btn.setMinHeight(dp(context, 40));
        btn.setOnClickListener(listener);
        return btn;
    }

    public static Button createOutlineButton(Context context, String text, View.OnClickListener listener) {
        Button btn = new Button(context);
        btn.setText(text);
        btn.setAllCaps(false);
        btn.setTextColor(COLOR_GRAY_600);
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.5f);
        btn.setBackground(roundedBg(context, COLOR_WHITE, 7f, 1, COLOR_GRAY_200));
        btn.setPadding(dp(context, 10), dp(context, 6), dp(context, 10), dp(context, 6));
        btn.setMinimumHeight(dp(context, 38));
        btn.setMinHeight(dp(context, 38));
        btn.setMinimumWidth(dp(context, 40));
        btn.setMinWidth(dp(context, 40));
        btn.setOnClickListener(listener);
        return btn;
    }

    public static Button createDangerButton(Context context, String text, View.OnClickListener listener) {
        Button btn = new Button(context);
        btn.setText(text);
        btn.setAllCaps(false);
        btn.setTextColor(COLOR_WHITE);
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.5f);
        btn.setBackground(roundedBg(context, COLOR_RED, 7f, 0, 0));
        btn.setPadding(dp(context, 10), dp(context, 6), dp(context, 10), dp(context, 6));
        btn.setMinimumHeight(dp(context, 38));
        btn.setMinHeight(dp(context, 38));
        btn.setMinimumWidth(dp(context, 40));
        btn.setMinWidth(dp(context, 40));
        btn.setOnClickListener(listener);
        return btn;
    }

    public static LinearLayout createCard(Context context) {
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackground(roundedBg(context, COLOR_WHITE, 10f, 1, COLOR_GRAY_200));
        card.setElevation(dp(context, 2));
        return card;
    }

    public static LinearLayout createSectionCard(Context context, String title, String actionText, View.OnClickListener onAction) {
        LinearLayout card = createCard(context);

        LinearLayout header = new LinearLayout(context);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(context, 18), dp(context, 14), dp(context, 18), dp(context, 14));

        TextView titleTv = new TextView(context);
        titleTv.setText(title);
        titleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15.5f);
        titleTv.setTypeface(Typeface.DEFAULT_BOLD);
        titleTv.setTextColor(COLOR_NAVY);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        header.addView(titleTv, titleLp);

        if (actionText != null && onAction != null) {
            Button actionBtn = createPrimaryButton(context, actionText, onAction);
            header.addView(actionBtn);
        }

        card.addView(header);
        card.addView(createDivider(context));
        return card;
    }

    public static View createDivider(Context context) {
        View v = new View(context);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(context, 1)));
        v.setBackgroundColor(COLOR_GRAY_100);
        return v;
    }

    public static View createEmptyState(Context context, String emoji, String message) {
        LinearLayout box = new LinearLayout(context);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);
        box.setPadding(dp(context, 24), dp(context, 40), dp(context, 24), dp(context, 40));

        TextView iconTv = new TextView(context);
        iconTv.setText(emoji);
        iconTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 38f);
        iconTv.setGravity(Gravity.CENTER);
        box.addView(iconTv);

        TextView msgTv = new TextView(context);
        msgTv.setText(message);
        msgTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        msgTv.setTextColor(COLOR_GRAY_400);
        msgTv.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = dp(context, 8);
        box.addView(msgTv, lp);

        return box;
    }

    public static EditText addFormInput(Context context, LinearLayout parent, String label, String initialValue, String hint, boolean numeric) {
        TextView labelTv = new TextView(context);
        labelTv.setText(label);
        labelTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.5f);
        labelTv.setTypeface(Typeface.DEFAULT_BOLD);
        labelTv.setTextColor(COLOR_GRAY_600);
        LinearLayout.LayoutParams labelLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        labelLp.topMargin = dp(context, 10);
        parent.addView(labelTv, labelLp);

        EditText input = new EditText(context);
        input.setText(initialValue != null ? initialValue : "");
        input.setHint(hint);
        input.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        input.setTextColor(COLOR_GRAY_800);
        input.setHintTextColor(COLOR_GRAY_400);
        input.setBackground(roundedBg(context, COLOR_WHITE, 7f, 1, COLOR_GRAY_200));
        input.setPadding(dp(context, 12), dp(context, 10), dp(context, 12), dp(context, 10));
        if (numeric) {
            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        } else {
            input.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        LinearLayout.LayoutParams inputLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        inputLp.topMargin = dp(context, 4);
        parent.addView(input, inputLp);
        return input;
    }

    public static Spinner addFormSpinner(Context context, LinearLayout parent, String label, List<String> items, int selectedIndex) {
        TextView labelTv = new TextView(context);
        labelTv.setText(label);
        labelTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.5f);
        labelTv.setTypeface(Typeface.DEFAULT_BOLD);
        labelTv.setTextColor(COLOR_GRAY_600);
        LinearLayout.LayoutParams labelLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        labelLp.topMargin = dp(context, 10);
        parent.addView(labelTv, labelLp);

        Spinner spinner = new Spinner(context);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);
        if (selectedIndex >= 0 && selectedIndex < items.size()) {
            spinner.setSelection(selectedIndex);
        }
        spinner.setBackground(roundedBg(context, COLOR_WHITE, 7f, 1, COLOR_GRAY_200));
        LinearLayout.LayoutParams spinLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        spinLp.topMargin = dp(context, 4);
        parent.addView(spinner, spinLp);
        return spinner;
    }
}
