package com.arashaltafi.piano;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

public class Preferences {

    private static final String TAG = "Preferences";
    private static final String DEFAULT_SOUND_SET = "piano";
    private final static String PREF_SELECTED_SOUND_SET = "selectedSoundSet";

    /**
     * The sound set is the name of the folder in assets/sounds/soundSet_[NAME]
     * (note that the soundSet_ prefix is stripped from the directory name before being recorded here).
     */
    public static String selectedSoundSet(Context context) {
        final String soundSetName = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_SELECTED_SOUND_SET, DEFAULT_SOUND_SET);

        // Should never return null, but the linter has picked up that getString() can strictly speaking
        // return null if a null was saved into preferences in the past, so may as well be defensive here.
        // When fixing issue #25, the preference was always prefixed with the directory name.
        // This will not play any sound, so lets take the liberty of updating the preference to the correct
        // format for them. This can be removed in the future if we like after most people will have migrated
        // to the newer version.
        if (soundSetName.startsWith(SettingsActivity.SOUND_SET_DIR_PREFIX)) {
            String updatedSoundSetName = soundSetName.substring(SettingsActivity.SOUND_SET_DIR_PREFIX.length());
            Log.i(TAG, "Migrating from existing soundSet \"" + soundSetName + "\" to new format: \"" + updatedSoundSetName + "\"");
            setSelectedSoundSet(context, updatedSoundSetName);
            return updatedSoundSetName;
        }

        return soundSetName;
    }

    public static void setSelectedSoundSet(Context context, String soundSet) {
        Log.d(TAG, "Selecting soundSet \"" + soundSet + "\"");
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_SELECTED_SOUND_SET, soundSet)
                .apply();
    }

}
