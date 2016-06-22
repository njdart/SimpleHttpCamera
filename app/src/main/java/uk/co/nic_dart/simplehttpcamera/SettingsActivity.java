package uk.co.nic_dart.simplehttpcamera;

import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by nic on 04/03/16.
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        ListPreference camerasListPreference = (ListPreference) findPreference("pref_camera");

        int numCameras = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            numCameras = Camera.getNumberOfCameras();
        }

        List<String> entries = new LinkedList<>();
        List<String> entryValues = new LinkedList<>();

        for (int i = 0; i < numCameras; i++) {
            Camera.CameraInfo ci = new Camera.CameraInfo();
            Camera.getCameraInfo(i, ci);

            entryValues.add(String.format("%d", i));

            System.out.println(String.format("Camera %d has orientation %d, facing %d", i, ci.orientation, ci.facing));

            switch(ci.facing){
                case Camera.CameraInfo.CAMERA_FACING_BACK:
                    entries.add("Rear Facing");
                    break;

                case Camera.CameraInfo.CAMERA_FACING_FRONT:
                    entries.add("Front Facing");
                    break;

                default:
                    entries.add("Unknown");
                    break;
            }
        }

        CharSequence[] entriesListSequence = entries.toArray(new CharSequence[entries.size()]);
        CharSequence[] entryValuesListSequence = entryValues.toArray(new CharSequence[entryValues.size()]);

        camerasListPreference.setEntries(entriesListSequence);
        camerasListPreference.setEntryValues(entryValuesListSequence);

    }
}
