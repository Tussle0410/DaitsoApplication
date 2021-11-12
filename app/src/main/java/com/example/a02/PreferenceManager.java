package com.example.a02;

import android.content.Context;
import android.content.SharedPreferences;
public class PreferenceManager {
    private static final String Preference_Name = "Login_Preference";
    private static final String DEFAULT_VALUES_STRING = "";
    private static final Boolean DEFAULT_VALUES_BOOLEAN = false;
    private static SharedPreferences getPreference(Context context){
        return context.getSharedPreferences(Preference_Name,context.MODE_PRIVATE);
    }
    public static void setString(Context context, String key,String value){
        SharedPreferences pref = getPreference(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key,value);
        editor.commit();
    }
    public static void setBoolean(Context context,String key, boolean value){
        SharedPreferences pref = getPreference(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key,value);
        editor.commit();
    }
    public static String getString(Context context,String key){
        SharedPreferences pref = getPreference(context);
        String value = pref.getString(key,DEFAULT_VALUES_STRING);
        return value;
    }
    public static Boolean getBoolean(Context context,String key){
        SharedPreferences pref = getPreference(context);
        Boolean value = pref.getBoolean(key,DEFAULT_VALUES_BOOLEAN);
        return value;
    }
    public static void removeKey(Context context,String key){
        SharedPreferences pref = getPreference(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        editor.commit();
    }
    public static void clear(Context context,String key){
        SharedPreferences pref = getPreference(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
}
