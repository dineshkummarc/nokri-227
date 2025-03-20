package com.scriptsbundle.nokri.utils;
import android.graphics.Color;

public enum PasswordStrength {

    WEAK("Weak", Color.RED),
    MEDIUM("Medium", Color.parseColor("#f59700")),
    STRONG("Strong", Color.parseColor("#4BB543")),
    VERY_STRONG("Strong", Color.parseColor("#4BB543"));

    //--------REQUIREMENTS--------
    static int REQUIRED_LENGTH = 4;
    static int MAXIMUM_LENGTH = 8;
    static boolean REQUIRE_SPECIAL_CHARACTERS = false;
    static boolean REQUIRE_DIGITS = true;
    static boolean REQUIRE_LOWER_CASE = true;
    static boolean REQUIRE_UPPER_CASE = true;

    String resId;
    int color;

    PasswordStrength(String resId, int color) {
        this.resId = resId;
        this.color = color;
    }

    public CharSequence getText(android.content.Context ctx) {
        return resId;
    }

    public int getColor() {
        return color;
    }

    public static PasswordStrength calculateStrength(String password) {
        int currentScore = 0;
        boolean sawUpper = false;
        boolean sawLower = false;
        boolean sawDigit = false;
        boolean sawSpecial = false;


        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);

            if (!sawSpecial && !Character.isLetterOrDigit(c)) {
                currentScore += 1;
                sawSpecial = true;
            } else {
                if (!sawDigit && Character.isDigit(c)) {
                    currentScore += 1;
                    sawDigit = true;
                } else {
                    if (!sawUpper || !sawLower) {
                        if (Character.isUpperCase(c))
                            sawUpper = true;
                        else
                            sawLower = true;
                        if (sawUpper && sawLower)
                            currentScore += 1;
                    }
                }
            }

        }

        if (password.length() > REQUIRED_LENGTH) {
            if ((REQUIRE_SPECIAL_CHARACTERS && !sawSpecial)
                    || (REQUIRE_UPPER_CASE && !sawUpper)
                    || (REQUIRE_LOWER_CASE && !sawLower)
                    || (REQUIRE_DIGITS && !sawDigit)) {
                currentScore = 1;
            }else{
                currentScore = 2;
                if (password.length() > MAXIMUM_LENGTH) {
                    currentScore = 3;
                }
            }
        }else{
            currentScore = 0;
        }

        switch (currentScore) {
            case 0:
                return WEAK;
            case 1:
            case 2:
                return MEDIUM;
            case 3:
                return STRONG;
            default:
        }

        return STRONG;
    }

}