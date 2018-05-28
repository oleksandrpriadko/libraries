package com.android.oleksandrpriadko.utils;

import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public class ManagerSharedPreferences {
    private static ManagerSharedPreferences instance;
    private SharedPreferences sharedPreferences;

    //region Initialization
    private ManagerSharedPreferences(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static ManagerSharedPreferences with(Context context) {
        if (instance == null) {
            instance = new ManagerSharedPreferences(context);
        }
        return instance;
    }

    public static ManagerSharedPreferences with(Fragment fragment) {
        if (instance == null) {
            instance = new ManagerSharedPreferences(fragment.getContext());
        }
        return instance;
    }

    public static ManagerSharedPreferences with(AppCompatActivity activity) {
        if (instance == null) {
            instance = new ManagerSharedPreferences(activity);
        }
        return instance;
    }
    //endregion

    //region Actions
    public void saveInfo(Object info) {
//        sharedPreferences.edit().putStringSet(INFO.CLASSES.getName(), info.getClasses()).apply();
    }

    public Set<String> getInfo() {
        return sharedPreferences.getStringSet("", null);
    }
    //endregion
}
