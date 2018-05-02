package com.example.fatoumeh.shumanatorguardianfootballfeed;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 *
 * shumanator: extending using AppCompatPreferenceActivity didnt save the prefs
 * so i used AppCompatActivity
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

  public static class FootballFeedPreferenceFragment extends PreferenceFragment
          implements Preference.OnPreferenceChangeListener{

      @Override
      public void onCreate(@Nullable Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          addPreferencesFromResource(R.xml.settings_main);

          Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
          bindPreferenceSummaryToValue(orderBy);

          Preference filterByFootball=findPreference(getString(R.string.settings_filter_by_football_key));
          bindPreferenceSummaryToValue(filterByFootball);

          Preference pageSize = findPreference(getString(R.string.settings_max_news_key));
          bindPreferenceSummaryToValue(pageSize);

      }

      @Override
      public boolean onPreferenceChange(Preference preference, Object value) {
          String stringValue = value.toString();
          if (preference instanceof ListPreference) {
              ListPreference listPreference = (ListPreference)preference;
              int prefIndex=listPreference.findIndexOfValue(stringValue);
              if (prefIndex>=0) {
                  CharSequence[] labels=listPreference.getEntries();
                  preference.setSummary(labels[prefIndex]);
              }
          } else if (preference instanceof CheckBoxPreference) {
              /*we dont want to see true/false or yes/no for checkbox because it's
              obvious so setting it to blank*/
              preference.setSummary("");
          } else {
              preference.setSummary(stringValue);
          }
          return true;
      }

      private void bindPreferenceSummaryToValue(Preference preference) {
          preference.setOnPreferenceChangeListener(this);
          SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());

          if (preference instanceof CheckBoxPreference) {
              boolean preferenceBoolean=preferences.getBoolean(preference.getKey(),true);
              onPreferenceChange(preference, preferenceBoolean);
          } else {
              String preferenceString = preferences.getString(preference.getKey(), "");
              onPreferenceChange(preference, preferenceString);
          }

      }
  }

}
