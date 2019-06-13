package com.example.nzse_prak0.helpers;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.graphics.drawable.DrawableCompat;

public final class ViewHelper {
    private ViewHelper() {}

    public static void setViewBackgroundTint(View v, int c) {
        Drawable wrappedDrawable = DrawableCompat.wrap(v.getBackground());
        wrappedDrawable.setColorFilter(c, PorterDuff.Mode.SRC_IN);
    }

    public static void clearBackgroundTint(View v) {
        Drawable wrappedDrawable = DrawableCompat.wrap(v.getBackground());
        wrappedDrawable.clearColorFilter();
    }
}
