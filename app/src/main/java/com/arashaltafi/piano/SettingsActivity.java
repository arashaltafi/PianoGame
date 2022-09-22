package com.arashaltafi.piano;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    public static final String SOUND_SET_DIR_PREFIX = "soundset_";

    public static final int RESULT_QUIT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment(getAvailableSoundSets()))
                    .commit();
        }

        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private ArrayList<String> getAvailableSoundSets() {
        AssetManager am = getApplicationContext().getAssets();
        String[] lst = null;
        try {
            lst = am.list("sounds");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (lst == null) {
            lst = new String[0];
        }

        ArrayList<String> filterList = new ArrayList<>();
        for (final String s : lst) {
            if (s.startsWith(SOUND_SET_DIR_PREFIX)) {
                // User display should be the asset name without the prefix
                filterList.add(s.substring(SOUND_SET_DIR_PREFIX.length()));
            }
        }

        if (filterList.size() == 0) {
            final String msg = "No sounds found, the keyboard won't work!";
            Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
            toast.show();

            Log.d("Piano::Activity", "Sound assets not available: piano will have no sound!");
        }

        return filterList;
    }

    private void onQuit() {
        setResult(RESULT_QUIT);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_config, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_quit_app) {
            onQuit();
            return true;
        }

        return false;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private final List<String> availableSoundSets;

        public SettingsFragment(List<String> availableSoundSets) {
            this.availableSoundSets = availableSoundSets;
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            ListPreference soundSets = findPreference("selectedSoundSet");
            if (soundSets != null) {
                String[] soundSetEntries = new String[availableSoundSets.size()];
                String[] soundSetEntryValues = new String[availableSoundSets.size()];
                for (int i = 0; i < availableSoundSets.size(); i++) {
                    soundSetEntryValues[i] = availableSoundSets.get(i);

                    String name = SOUND_SET_DIR_PREFIX + availableSoundSets.get(i);
                    @SuppressLint("DiscouragedApi") int stringId = getResources().getIdentifier(name, "string", requireContext().getPackageName());
                    soundSetEntries[i] = stringId > 0 ? getString(stringId) : availableSoundSets.get(i);
                }

                soundSets.setEntries(soundSetEntries);
                soundSets.setEntryValues(soundSetEntryValues);
            }
        }

    }
}
