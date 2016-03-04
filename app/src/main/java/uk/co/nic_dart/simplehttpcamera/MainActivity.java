package uk.co.nic_dart.simplehttpcamera;

import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    public static String appTag = "SimpleHttpCamera";
    String TAG = appTag + "::MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get the surface View for the image
        surfaceView = (SurfaceView)findViewById(R.id.cameraSurfaceView);
        Log.d(TAG, "Surface " + surfaceView);

        // Get it's holder
        surfaceHolder = surfaceView.getHolder();
        Log.d(TAG, "Surface Holder " + surfaceHolder);

        //Get the camera, will set this.camera
        if(!getCameraSafe(0)) {
            Log.w(TAG, "Failed to get Camera Object Safely, Camera was either busy of non-existant");
            Toast.makeText(this, "Could not find Camera", Toast.LENGTH_LONG).show();
        } else {
            int orientation = ((getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) ? 0 : 90);

            Log.d(TAG, "Setting orientation to " + orientation + " degrees");

            camera.setDisplayOrientation(orientation);

            Log.d(TAG, "Using Camera " + camera);

            // Add an events callback to the holder
            surfaceHolder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    Log.d(TAG, "Surface Created");

                    try {
                        camera.setPreviewDisplay(surfaceHolder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    camera.startPreview();
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    Log.d(TAG, "Surface Changed");
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    Log.d(TAG, "Surface Destroyed");
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        releaseCameraAndPreview();
    }

    /**
     * Tries to get the camera object instace (hardware camera access). Camera.Open(id) will cloud throw an exception if
     * the camera is in use by another app.
     * @param id the id of the camera to open
     * @return returns True if the camera was opened successfully (and is not null). False otherwise
     */
    public boolean getCameraSafe(int id) {
        boolean opened = false;

        try {
            releaseCameraAndPreview();
            camera = Camera.open(id);
            opened = (camera != null);
        } catch (Exception e){
            Log.e(TAG, "Failed to get Camera. camera.open(id) throw exception");
            e.printStackTrace();
        }

        return opened;
    }

    /**
     * Releases the camera, the camera object will allways be null after this method is called.
     */
    private void releaseCameraAndPreview() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if(id == R.id.action_about) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
