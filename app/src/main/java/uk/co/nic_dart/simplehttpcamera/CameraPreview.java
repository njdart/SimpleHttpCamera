package uk.co.nic_dart.simplehttpcamera;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

/**
 * Created by nic on 04/03/16.
 */
class CameraPreview extends ViewGroup implements SurfaceHolder.Callback {

    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Camera camera;
    List<Camera.Size> supportedPreviewSizes;
    Camera.Size previewSize;

    public CameraPreview(Context context) {
        super(context);

        surfaceView = new SurfaceView(context);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        addView(surfaceView);

        System.out.println("Surface Holder " + surfaceHolder);

    }

    public void setCamera(Camera camera) {
        if (this.camera == camera) {
            return;
        }

        stopPreviewAndFreeCamera();

        this.camera = camera;

        if (this.camera != null) {
            supportedPreviewSizes = this.camera.getParameters().getSupportedPreviewSizes();

            if(supportedPreviewSizes.size() > 0) {
                previewSize = supportedPreviewSizes.get(supportedPreviewSizes.size() - 1);
                System.out.println("Setting PReview Size to " + previewSize.width + "x" + previewSize.height);
            } else {
                System.out.println("No Preview size Available, setting to 1024x768");
            }

            System.out.println("Using size preference of " + previewSize.toString());
            requestLayout();

            try {
                this.camera.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Important: Call startPreview() to start updating the preview
            // surface. Preview must be started before you can take a picture.
            camera.startPreview();
            System.out.println("Staring Camera Preview");
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        System.out.println("Layout Changed");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        System.out.println("Surface Created");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        System.out.println("Surface Changed");
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(1024, 768);
        requestLayout();
        camera.setParameters(parameters);

        // Important: Call startPreview() to start updating the preview surface.
        // Preview must be started before you can take a picture.
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        System.out.println("Surface Destroyed");
        // Surface will be destroyed when we return, so stop the preview.
        if (camera != null) {
            // Call stopPreview() to stop updating the preview surface.
            camera.stopPreview();
        }
    }

    /**
     * When this function returns, mCamera will be null.
     */
    private void stopPreviewAndFreeCamera() {
        if (camera != null) {
            // Call stopPreview() to stop updating the preview surface.
            camera.stopPreview();

            // Important: Call release() to release the camera for use by other
            // applications. Applications should release the camera immediately
            // during onPause() and re-open() it during onResume()).
            camera.release();

            camera = null;
        }
    }
}
