package com.example.nzse_prak0.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import com.example.nzse_prak0.R;

import java.util.Collection;

import static android.content.Context.MODE_PRIVATE;

public class SharedPrefs {
    private SharedPrefs() {
    }

    public static void setPreferences(Context context, Collection<Pair<String, Object>> prefList) {
        Context c = context.getApplicationContext();
        SharedPreferences sharedPrefs = c.getSharedPreferences(c.getString(R.string.preferences_file_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        for (Pair<String, Object> p : prefList) {
            setPreference(editor, p.first, p.second);
        }
        editor.apply();
    }

    private static void setPreference(SharedPreferences.Editor editor, String key, Object value) {
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

    public static void setPreference(Context context, String key, Object value) {
        Context c = context.getApplicationContext();
        SharedPreferences sharedPrefs = c.getSharedPreferences(c.getString(R.string.preferences_file_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        setPreference(editor, key, value);
        editor.apply();
    }

    public static String getString(Context c, String key, String defaultValue) {
        SharedPreferences sharedPrefs = c.getSharedPreferences(c.getApplicationContext().getString(R.string.preferences_file_name), MODE_PRIVATE);
        return sharedPrefs.getString(key, defaultValue);
    }

    public static int getInt(Context c, String key, int defaultValue) {
        SharedPreferences sharedPrefs = c.getSharedPreferences(c.getApplicationContext().getString(R.string.preferences_file_name), MODE_PRIVATE);
        return sharedPrefs.getInt(key, defaultValue);
    }
}
