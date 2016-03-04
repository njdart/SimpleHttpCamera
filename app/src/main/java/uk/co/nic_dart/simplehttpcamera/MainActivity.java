package uk.co.nic_dart.simplehttpcamera;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    NanoRestServer nrs;
    CameraPreview cp;
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

//        cp = (CameraPreview)findViewById(R.id.cameraPreview);

        // Get the surface View for the image
        surfaceView = (SurfaceView)findViewById(R.id.cameraSurfaceView);
        System.out.println("Surface " + surfaceView);

        // Get it's holder
        surfaceHolder = surfaceView.getHolder();
        System.out.println("Surface Holder " + surfaceHolder);

        //Get the camera
        System.out.println("There are " + Camera.getNumberOfCameras() + " cameras available");
        camera = Camera.open(0);
        System.out.println("Using Camera " + camera);

        // Add an events callback to the holder
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                System.out.println("Surface Created");

                try {
                    camera.setPreviewDisplay(surfaceHolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                camera.startPreview();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                System.out.println("Surface Changed");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                System.out.println("Surface Destroyed");
            }
        });


//        cp.setCamera(camera);

//        try {
//            this.nrs = new NanoRestServer("127.0.0.1", 8080, camera);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
