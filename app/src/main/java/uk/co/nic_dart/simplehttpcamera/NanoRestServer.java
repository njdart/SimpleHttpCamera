package uk.co.nic_dart.simplehttpcamera;

import android.hardware.Camera;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import fi.iki.elonen.NanoHTTPD;
import com.google.gson.Gson;

/**
 * Created by nic on 04/03/16.
 */
public class NanoRestServer extends NanoHTTPD {

    Camera camera;
    String TAG = MainActivity.appTag + "::NanoRESTServer";

    Boolean started = false;

    Gson gson = new Gson();

    public NanoRestServer(String hostname, int port) throws IOException {
        super(hostname, port);

        start();
        System.out.println("\nRunning! Point your browers to http://localhost:8080/ \n");
    }

    public void enable(Camera camera) {
        this.camera = camera;
        this.started = true;
    }

    public void disable() {
        this.started = false;
        this.camera = null;
    }

    @Override
    public Response serve(IHTTPSession session) {
        Map<String, String> params = session.getParms();
        System.out.println("URI " + session.getUri());
        System.out.println("Params " + params);

        if(session.getUri().equals("/focus")) {
            final boolean focused[] = {false};
            System.out.println("FOCUS!");
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    focused[0] = true;
                }
            });

            for(int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return new Response(Response.Status.INTERNAL_ERROR, "application/json", "{BAD_FOCUS}");
                }
                if(focused[0]) {
                    return new Response(Response.Status.NO_CONTENT, "application/json", "");
                }
            }
            return new Response(Response.Status.OK, "application/json", "{TIMEOUT}");
        }

        if(session.getUri().equals("/properties")) {

            if(session.getMethod() == Method.GET) {
                return new Response(Response.Status.OK, "application/json", this.gson.toJson(this.camera.getParameters()));
            }

            if(session.getMethod() == Method.POST) {

                final HashMap<String, String> map = new HashMap<>();
                try {
                    session.parseBody(map);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new Response(Response.Status.INTERNAL_ERROR, "application/json", e.getStackTrace().toString());
                }

                String json = map.get("postData");
                Log.d(TAG, "BODY: " + json);

                this.camera.setParameters(this.gson.fromJson(json, Camera.Parameters.class));

                return new Response(Response.Status.OK, "application/json", "{POST SETTINGS}");
            }
        }

        System.out.println("Getting Image");

        if(!this.started) {
            return new Response(Response.Status.FORBIDDEN, "application/json", "{\"error\":\"AppNotOpen\",\"message\":\"The SimpleHttpCamera App was not open. The app needs to be open to take images\"}");
        }

        System.out.println("Camera Object " + this.camera);

        try {
            final boolean[] taken = {false};
            final byte[][] image = new byte[1][1];
            camera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    System.out.println("Took Picture jpg, length " + data.length);
                    image[0] = data;
                    taken[0] = true;
                    camera.startPreview();
                }
            });
            for(int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return new Response(Response.Status.INTERNAL_ERROR, "application/json", "{BAD}");
                }
                if(taken[0]) {
                    System.out.println("Image is " + image[0].length);
                    return new Response(Response.Status.OK, "image/jpeg", new ByteArrayInputStream(image[0]));
                }
            }
            return new Response(Response.Status.OK, "application/json", "{TIMEOUT}");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Response(Response.Status.INTERNAL_ERROR, "application/json", "{\"error\":\"TakePicture\",\"message\":\"An Error Occured trying to take a picture\"}");
        }

    }

    public void destroy() {
        this.disable();
        this.stop();
    }
}
