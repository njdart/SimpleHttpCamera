package uk.co.nic_dart.simplehttpcamera;

import android.hardware.Camera;

import java.io.IOException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by nic on 04/03/16.
 */
public class NanoRestServer extends NanoHTTPD {

    Camera camera;

    public NanoRestServer(String hostname, int port, Camera camera) throws IOException {
        super(hostname, port);
        this.camera = camera;

        start();
        System.out.println("\nRunning! Point your browers to http://localhost:8080/ \n");
    }

    @Override
    public Response serve(IHTTPSession session) {
        Map<String, String> parms = session.getParms();

        System.out.println("Camera Object " + this.camera);

//        camera.takePicture(null, null, new Camera.PictureCallback() {
//            @Override
//            public void onPictureTaken(byte[] data, Camera camera) {
//                System.out.println("Took Picture jpg");
//            }
//        });

        return new Response(Response.Status.OK, "application/json", "{}");
    }
}
