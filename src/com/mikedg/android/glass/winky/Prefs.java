package com.mikedg.android.glass.winky;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Prefs {
    private static final String BURST_KEY = "burst";
    private static final boolean BURST_DEFAULT = false;

    private static final String TIMELINE_KEY = "savetoTimeline";
    private static final boolean TIMELINE_DEFAULT = false;
    
    private SharedPreferences mPrefs;
    private static Prefs sPrefs;
    
    public static final Prefs getInstance(Context context) {
        if (sPrefs == null) {
            sPrefs = new Prefs(context);
        }
        return sPrefs;
    }
    
    private Prefs(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context); 
    }
    
    public boolean getBurst() {
        return mPrefs.getBoolean(Prefs.BURST_KEY, Prefs.BURST_DEFAULT);
    }
    
    public void setBurst(boolean val) {
        mPrefs.edit().putBoolean(Prefs.BURST_KEY, val).apply();
    }
    
    public void setSaveToTimeline(boolean val) {
        mPrefs.edit().putBoolean(Prefs.TIMELINE_KEY, val).apply();
    }

    public boolean getSaveToTimeline() {
        return mPrefs.getBoolean(Prefs.TIMELINE_KEY, Prefs.TIMELINE_DEFAULT);
    }
}
