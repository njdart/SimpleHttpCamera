package uk.co.nic_dart.simplehttpcamera;

import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
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
    private static NanoRestServer nanoRestServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        try {
            nanoRestServer = new NanoRestServer("0.0.0.0", 8080);
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "Failed to enable the REST Client", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

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

            Log.d(TAG, "Using Camera " + camera);

            int rotation = this.getWindowManager().getDefaultDisplay()
                    .getRotation();
            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0: degrees = 0; break;
                case Surface.ROTATION_90: degrees = 90; break;
                case Surface.ROTATION_180: degrees = 180; break;
                case Surface.ROTATION_270: degrees = 270; break;
            }

            Log.d(TAG, "Setting orientation to " + degrees + " degrees");

            camera.setDisplayOrientation(degrees);

            Camera.Parameters params = camera.getParameters();
            System.out.println("Camera Params " + params);
            // 4208x3120
            for(Camera.Size size : params.getSupportedPictureSizes()) {
                System.out.println("Supported Pic Size " + size.height + "," + size.width);
            }

            Camera.Size size = params.getSupportedPictureSizes().get(0);
            params.setPictureSize(size.width, size.height);

            System.out.println("Using Camera Size " + size.width + "," + size.height);

            camera.setParameters(params);

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
                    nanoRestServer.enable(camera);
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

        nanoRestServer.disable();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("App Destroyed");
        this.nanoRestServer.destroy();
        this.nanoRestServer = null;
    }
}
