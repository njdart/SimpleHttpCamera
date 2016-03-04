package uk.co.nic_dart.simplehttpcamera;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by nic on 04/03/16.
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
