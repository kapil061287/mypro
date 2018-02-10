package com.depex.odepto.util;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.depex.odepto.Label;
import com.depex.odepto.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by we on 2/8/2018.
 */

public class LabelUtils {

    private static final String BLACK = "black";
    private static final String BLUE = "blue";

    private static final String GREEN = "green";
    private static final String LIME = "lime";
    private static final String NONE = "none";
    private static final String ORANGE = "orange";
    private static final String PINK = "pink";
    private static final String PURPLE = "purple";
    private static final String RED = "red";
    private static final String SKY = "sky";
    private static final String YELLOW = "yellow";
    public static final String[] COLOR_NAMES = new String[]{GREEN, YELLOW, ORANGE, RED, PURPLE, BLUE, SKY, LIME, PINK, BLACK, ""};
    private static LabelUtils instance;
    private final Map<String, Integer> colorMap = new HashMap(10);
    private final Map<String, Integer> displayNameMap;
    private final Map<String, Integer> drawableColorBlindMap;
    private final Map<String, Integer> drawableMap;
    private final Map<String, ColorBlindPattern> patternMap;

    public static void init(Context context) {
        instance = new LabelUtils(context);
    }

    private LabelUtils(Context context) {
        Resources res = context.getResources();
        this.colorMap.put(GREEN, Integer.valueOf(res.getColor(R.color.label_green)));
        this.colorMap.put(YELLOW, Integer.valueOf(res.getColor(R.color.label_yellow)));
        this.colorMap.put(ORANGE, Integer.valueOf(res.getColor(R.color.label_orange)));
        this.colorMap.put(RED, Integer.valueOf(res.getColor(R.color.label_red)));
        this.colorMap.put(PURPLE, Integer.valueOf(res.getColor(R.color.label_purple)));
        this.colorMap.put(BLUE, Integer.valueOf(res.getColor(R.color.label_blue)));
        this.colorMap.put(SKY, Integer.valueOf(res.getColor(R.color.label_sky)));
        this.colorMap.put(LIME, Integer.valueOf(res.getColor(R.color.label_lime)));
        this.colorMap.put(PINK, Integer.valueOf(res.getColor(R.color.label_pink)));
        this.colorMap.put(BLACK, Integer.valueOf(res.getColor(R.color.label_black)));
        this.colorMap.put(NONE, Integer.valueOf(res.getColor(R.color.label_none)));
        this.drawableMap = new HashMap(10);
        this.drawableMap.put(GREEN, Integer.valueOf(R.drawable.label_green));
        this.drawableMap.put(YELLOW, Integer.valueOf(R.drawable.label_yellow));
        this.drawableMap.put(ORANGE, Integer.valueOf(R.drawable.label_orange));
        this.drawableMap.put(RED, Integer.valueOf(R.drawable.label_red));
        this.drawableMap.put(PURPLE, Integer.valueOf(R.drawable.label_purple));
        this.drawableMap.put(BLUE, Integer.valueOf(R.drawable.label_blue));
        this.drawableMap.put(SKY, Integer.valueOf(R.drawable.label_sky));
        this.drawableMap.put(LIME, Integer.valueOf(R.drawable.label_lime));
        this.drawableMap.put(PINK, Integer.valueOf(R.drawable.label_pink));
        this.drawableMap.put(BLACK, Integer.valueOf(R.drawable.label_black));
        this.drawableMap.put(NONE, Integer.valueOf(R.drawable.label_none));
        this.drawableColorBlindMap = new HashMap();
        this.drawableColorBlindMap.put(GREEN, R.drawable.label_green_colorblind);
        this.drawableColorBlindMap.put(YELLOW, Integer.valueOf(R.drawable.label_yellow_colorblind));
        this.drawableColorBlindMap.put(ORANGE, Integer.valueOf(R.drawable.label_orange_colorblind));
        this.drawableColorBlindMap.put(RED, Integer.valueOf(R.drawable.label_red_colorblind));
        this.drawableColorBlindMap.put(PURPLE, Integer.valueOf(R.drawable.label_purple_colorblind));
        this.drawableColorBlindMap.put(BLUE, Integer.valueOf(R.drawable.label_blue));
        this.drawableColorBlindMap.put(SKY, Integer.valueOf(R.drawable.label_sky));
        this.drawableColorBlindMap.put(LIME, Integer.valueOf(R.drawable.label_lime_colorblind));
        this.drawableColorBlindMap.put(PINK, Integer.valueOf(R.drawable.label_pink_colorblind));
        this.drawableColorBlindMap.put(BLACK, Integer.valueOf(R.drawable.label_black_colorblind));
        this.drawableColorBlindMap.put(NONE, Integer.valueOf(R.drawable.label_none));
        this.displayNameMap = new HashMap(10);
        this.displayNameMap.put(GREEN, Integer.valueOf(R.string.label_green));
        this.displayNameMap.put(YELLOW, Integer.valueOf(R.string.label_yellow));
        this.displayNameMap.put(ORANGE, Integer.valueOf(R.string.label_orange));
        this.displayNameMap.put(RED, Integer.valueOf(R.string.label_red));
        this.displayNameMap.put(PURPLE, Integer.valueOf(R.string.label_purple));
        this.displayNameMap.put(BLUE, Integer.valueOf(R.string.label_blue));
        this.displayNameMap.put(SKY, Integer.valueOf(R.string.label_sky));
        this.displayNameMap.put(LIME, Integer.valueOf(R.string.label_lime));
        this.displayNameMap.put(PINK, Integer.valueOf(R.string.label_pink));
        this.displayNameMap.put(BLACK, Integer.valueOf(R.string.label_black));
        this.displayNameMap.put(NONE, Integer.valueOf(R.string.label_none));
        this.patternMap = new HashMap();
        this.patternMap.put(GREEN, ColorBlindPattern.DIAGONAL_LINES_1);
        this.patternMap.put(YELLOW, ColorBlindPattern.DIAMOND);
        this.patternMap.put(ORANGE, ColorBlindPattern.VERTICAL_LINES);
        this.patternMap.put(RED, ColorBlindPattern.DIAMOND);
        this.patternMap.put(PURPLE, ColorBlindPattern.DIAGONAL_LINES_2);
        this.patternMap.put(BLUE, ColorBlindPattern.NONE);
        this.patternMap.put(SKY, ColorBlindPattern.NONE);
        this.patternMap.put(LIME, ColorBlindPattern.DIAGONAL_LINES_2);
        this.patternMap.put(PINK, ColorBlindPattern.DIAGONAL_LINES_1);
        this.patternMap.put(BLACK, ColorBlindPattern.VERTICAL_LINES);
        this.patternMap.put(NONE, ColorBlindPattern.NONE);
    }

    public static int getColor(String colorName) {
        return ((Integer) instance.colorMap.get(ensureValidColorName(colorName))).intValue();
    }

    public static int getDrawable(String colorName, boolean colorBlind) {
        colorName = ensureValidColorName(colorName);
        return colorBlind ? ((Integer) instance.drawableColorBlindMap.get(colorName)).intValue() : ((Integer) instance.drawableMap.get(colorName)).intValue();
    }

    public static String getLocalizedColorName(Context context, String colorName) {
        return context.getString(((Integer) instance.displayNameMap.get(ensureValidColorName(colorName))).intValue());
    }

    public static ColorBlindPattern getColorBlindPattern(String colorName) {
        return (ColorBlindPattern) instance.patternMap.get(ensureValidColorName(colorName));
    }

    static String ensureValidColorName(String colorName) {
        if (MiscUtils.isNullOrEmpty(colorName)) {
            return NONE;
        }
        if (instance.patternMap.containsKey(colorName)) {
            return colorName;
        }
        throw new IllegalArgumentException("\"" + colorName + "\" is not a valid color");
    }

    public static String getDisplayName(Context context, Label label) {
        String name = label.getName();
        return !MiscUtils.isNullOrEmpty(name) ? name : getLocalizedColorName(context, label.getColor());
    }

    public static void setupShadowForLabelTextView(Context context, TextView textView) {
        Resources res = context.getResources();
        textView.setShadowLayer(res.getDimension(R.dimen.label_text_shadow), res.getDimension(R.dimen.label_text_shadow_dx), res.getDimension(R.dimen.label_text_shadow_dy), ContextCompat.getColor(context, R.color.black_87));
    }

    public static int getColorNameOrdinal(String colorName) {
        if (colorName == null) {
            return 100;
        }
        int i = -1;
        switch (colorName.hashCode()) {
            case -1008851410:
                if (colorName.equals(ORANGE)) {
                    i = 2;
                    break;
                }
                break;
            case -976943172:
                if (colorName.equals(PURPLE)) {
                    i = 4;
                    break;
                }
                break;
            case -734239628:
                if (colorName.equals(YELLOW)) {
                    i = 1;
                    break;
                }
                break;
            case 112785:
                if (colorName.equals(RED)) {
                    i = 3;
                    break;
                }
                break;
            case 113953:
                if (colorName.equals(SKY)) {
                    i = 6;
                    break;
                }
                break;
            case 3027034:
                if (colorName.equals(BLUE)) {
                    i = 5;
                    break;
                }
                break;
            case 3321813:
                if (colorName.equals(LIME)) {
                    i = 7;
                    break;
                }
                break;
            case 3441014:
                if (colorName.equals(PINK)) {
                    i = 8;
                    break;
                }
                break;
            case 93818879:
                if (colorName.equals(BLACK)) {
                    i = 9;
                    break;
                }
                break;
            case 98619139:
                if (colorName.equals(GREEN)) {
                    i = 0;
                    break;
                }
                break;
        }
        switch (i) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
                return 6;
            case 7:
                return 7;
            case 8:
                return 8;
            case 9:
                return 9;
            default:
                return 1000;
        }
    }
}
