package com.example.nzse_prak0.helpers;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.graphics.drawable.DrawableCompat;

import java.util.ArrayList;
import java.util.List;

public final class ViewHelper {
    private ViewHelper() {}

    public static List<View> getChildrenByClass(ViewGroup v, Class c) {
        List<View> returnList = new ArrayList<>();
        int x = v.getChildCount();
        if (x == 0)
            return returnList;

        for (int i=0; i<x; i++) {
            View child = v.getChildAt(i);
            if (child.getClass() == c) {
                returnList.add(child);
            }
        }

        return returnList;
    }

    public static void setViewBackgroundTint(View v, int c) {
        Drawable wrappedDrawable = DrawableCompat.wrap(v.getBackground());
        wrappedDrawable.setColorFilter(c, PorterDuff.Mode.DST);
    }

    public static void clearBackgroundTint(View v) {
        Drawable wrappedDrawable = DrawableCompat.wrap(v.getBackground());
        wrappedDrawable.clearColorFilter();
    }
}
