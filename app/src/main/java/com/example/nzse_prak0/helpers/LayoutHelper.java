package com.example.nzse_prak0.helpers;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public final class LayoutHelper {
    private LayoutHelper() {}

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
}
