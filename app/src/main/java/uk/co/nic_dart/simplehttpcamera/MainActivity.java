package uk.co.nic_dart.simplehttpcamera;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    SharedPreferences preferences;

    public static String appTag = "SimpleHttpCamera";

    String TAG = appTag + "::MainActivity";

    //    private static NanoRestServer nanoRestServer;
    CameraManager cameraManager;

    private final int PERMISSION_REQUEST_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

//        try {
//            nanoRestServer = new NanoRestServer("0.0.0.0", 8080);
//        } catch (IOException e) {
//            Toast.makeText(MainActivity.this, "Failed to enable the REST Client", Toast.LENGTH_LONG).show();
//            e.printStackTrace();
//        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get the surface View for the image and it's holder
        surfaceView = (SurfaceView) findViewById(R.id.cameraSurfaceView);
        surfaceHolder = surfaceView.getHolder();
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        // Check if the permission is already Granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission Not granted

            // ShouldShowRequestPermissionRationale returns true if the user has already denied the permission
            //   (and not checked do-not-ask-again
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                Log.e(TAG, "Not Implemented: Explain to user why permission required");

            }

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CAMERA);
        }

//        else {
//
//            Log.d(TAG, "Using Camera " + camera);
//
//            int rotation = this.getWindowManager().getDefaultDisplay()
//                    .getRotation();
//            int degrees = 0;
//            switch (rotation) {
//                case Surface.ROTATION_0: degrees = 0; break;
//                case Surface.ROTATION_90: degrees = 90; break;
//                case Surface.ROTATION_180: degrees = 180; break;
//                case Surface.ROTATION_270: degrees = 270; break;
//            }
//
//            Log.d(TAG, "Setting orientation to " + degrees + " degrees");
//
//            camera.setDisplayOrientation(degrees);
//
//            Camera.Parameters params = camera.getParameters();
//            System.out.println("Camera Params " + params);
//            // 4208x3120
//            for(Camera.Size size : params.getSupportedPictureSizes()) {
//                System.out.println("Supported Pic Size " + size.height + "," + size.width);
//            }
//
//            Camera.Size size = params.getSupportedPictureSizes().get(0);
//            params.setPictureSize(size.width, size.height);
//
//            System.out.println("Using Camera Size " + size.width + "," + size.height);
//
//            camera.setParameters(params);
//
//            // Add an events callback to the holder
//            surfaceHolder.addCallback(new SurfaceHolder.Callback() {
//                @Override
//                public void surfaceCreated(SurfaceHolder holder) {
//                    Log.d(TAG, "Surface Created");
//
//                    try {
//                        camera.setPreviewDisplay(surfaceHolder);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    camera.startPreview();
//                    nanoRestServer.enable(camera);
//                }
//
//                @Override
//                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//                    Log.d(TAG, "Surface Changed");
//                }
//
//                @Override
//                public void surfaceDestroyed(SurfaceHolder holder) {
//                    Log.d(TAG, "Surface Destroyed");
//                }
//            });
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();

//        nanoRestServer.disable();
//        releaseCameraAndPreview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("App Destroyed");
//        this.nanoRestServer.destroy();
//        this.nanoRestServer = null;
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

        if (id == R.id.action_about) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case PERMISSION_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "User Granted Camera Permission Request");
                } else {
                    Log.d(TAG, "User Denied Camera Permission Request");
                }
            }
        }
    }

    CameraDevice.StateCallback cameraStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {

        }

        @Override
        public void onDisconnected(CameraDevice camera) {

        }

        @Override
        public void onError(CameraDevice camera, int error) {

        }
    };


}
