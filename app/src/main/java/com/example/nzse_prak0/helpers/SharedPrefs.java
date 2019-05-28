package com.example.nzse_prak0.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import java.util.Collection;

import static android.content.Context.MODE_PRIVATE;

public class SharedPrefs {
    private SharedPrefs() {
    }

    // TODO: gleichen Code in verschiedenen Methoden vermeiden

    // TODO: besserer Datentyp als Pair?

    public static void setValues(Context context, String filename, Collection<Pair<String, Object>> prefList) {
        Context c = context.getApplicationContext();
        SharedPreferences sharedPrefs = c.getSharedPreferences(filename, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        for (Pair<String, Object> p : prefList) {
            setValue(editor, p.first, p.second);
        }
        editor.apply();
    }

    public static void setValue(Context context, String filename, String key, Object value) {
        Context c = context.getApplicationContext();
        SharedPreferences sharedPrefs = c.getSharedPreferences(filename, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        setValue(editor, key, value);
        editor.apply();
    }

    private static void setValue(SharedPreferences.Editor editor, String key, Object value) {
        Class valueClass = value.getClass();
        if (valueClass == String.class) {
            editor.putString(key, (String) value);
        } else if (valueClass == Integer.class) {
            editor.putInt(key, (Integer) value);
        } else if (valueClass == Boolean.class) {
            editor.putBoolean(key, (Boolean) value);
        } else if (valueClass == Float.class) {
            editor.putFloat(key, (Float) value);
        } else if (valueClass == Long.class) {
            editor.putLong(key, (Long) value);
        } else {
            Log.w("SharedPrefs", "invalider Typ übergeben");
        }
    }

    public static String getString(Context c, String filename, String key, String defaultValue) {
        SharedPreferences sharedPrefs = c.getSharedPreferences(filename, MODE_PRIVATE);
        return sharedPrefs.getString(key, defaultValue);
    }

    public static int getInt(Context c, String filename, String key, int defaultValue) {
        SharedPreferences sharedPrefs = c.getSharedPreferences(filename, MODE_PRIVATE);
        return sharedPrefs.getInt(key, defaultValue);
    }
}
