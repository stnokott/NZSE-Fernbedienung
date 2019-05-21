package com.example.nzse_prak0.helpers;

import android.content.Context;

public final class Units {
    public static int dpToPx(int dp, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
