package me.ccrama.redditslide.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;

import me.ccrama.redditslide.Fragments.SettingsHandlingFragment;
import me.ccrama.redditslide.PostMatch;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.SettingValues;


/**
 * Created by l3d00m on 11/13/2015.
 */
public class SettingsHandling extends BaseActivityAnim {

    private final SettingsHandlingFragment fragment = new SettingsHandlingFragment(this);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyColorTheme();
        setContentView(R.layout.activity_settings_handling);
        setupAppBar(R.id.toolbar, R.string.settings_link_handling, true, true);

        ((ViewGroup) findViewById(R.id.settings_handling)).addView(
                getLayoutInflater().inflate(R.layout.activity_settings_handling_child, null));

        fragment.Bind();
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences.Editor e = SettingValues.prefs.edit();

        e.putString(SettingValues.PREF_ALWAYS_EXTERNAL, Reddit.arrayToString(fragment.domains));
        e.apply();

        PostMatch.externalDomain = null;

        SettingValues.alwaysExternal =
                SettingValues.prefs.getString(SettingValues.PREF_ALWAYS_EXTERNAL, "");
    }

}