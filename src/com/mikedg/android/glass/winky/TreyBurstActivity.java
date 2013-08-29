package com.mikedg.android.glass.winky;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TreyBurstActivity extends Activity {
    public static final String EXTRA_TIMELINE = "TIMELINE";

    private Preview mPreview;
    Camera mCamera;
    int numberOfCameras;
    int cameraCurrentlyLocked;

    // The first rear facing camera
    int defaultCameraId;

    private static final String TAG = "Test";
    private int burst = 0;
    private static final int MAX_BURST = 5;
    private Camera.Parameters cameraParameters;
    private long startTime;
    private boolean isTakingPicture = false;
    private boolean startedBurst = false;
    private boolean saveToTimeline = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );

        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mPreview = new Preview(this);
        setContentView(mPreview);

        // Find the total number of cameras available
        numberOfCameras = Camera.getNumberOfCameras();

        // Find the ID of the default camera
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                defaultCameraId = i;
            }
        }
        
        saveToTimeline = getIntent().getBooleanExtra(EXTRA_TIMELINE, false);
        
        // Open the default i.e. the first rear facing camera.
        mCamera = Camera.open();
        cameraCurrentlyLocked = defaultCameraId;
        mPreview.setCamera(mCamera);
    }

    private void tryBurstStandard() {
        // screen doesnt turn on wtf
        // FIXME: turn screen on
        // FIXME: turn audio off
        // I picked 5 because auto-awesome is awesome :)
        // I'd say this is definitely slower than my auto attempt :/
        tryPicture(false, false);
        tryPicture(false, false);
        tryPicture(false, false);
        tryPicture(false, false);
        tryPicture(false, false);
    }

    private void tryGetGazeService() {

        String str = getConstantFromClass("GLASS_GESTURE_SERVICE", Context.class);
        if (str == null)
            return;
        Object o = this.getSystemService(str);
        // FIXME: export this methods
    }

    private static String getConstantFromClass(String paramString, Class<?> paramClass) {
        try {
            String str = (String) paramClass.getDeclaredField(paramString).get(null);
            return str;
        } catch (IllegalAccessException localIllegalAccessException) {
            Log.e(TAG, "Unable to get " + paramString + " from " + paramClass.getSimpleName(),
                    localIllegalAccessException);
            return null;
        } catch (NoSuchFieldException localNoSuchFieldException) {
            Log.e(TAG, "Unable to get " + paramString + " from " + paramClass.getSimpleName(),
                    localNoSuchFieldException);
        }
        return null;
    }

    private void tryPicture(boolean turnScreenOff, boolean playInitialSound) {

        // Intent i = new Intent("com.google.glass.action.RECORD_CLIPLET");
        // Intent i = new Intent("com.google.glass.action.TAKE_PICTURE_FROM_SCREEN_OFF");
        Intent i = new Intent("com.google.glass.action.TAKE_PICTURE"); // Seems to be pretty damn
                                                                       // quick! but doesnt turn
                                                                       // screen on, so maybe take
                                                                       // from screen off then this?
        i.putExtra("should_finish_turn_screen_off", turnScreenOff); // Maybe do this last?
        i.putExtra("should_play_initial_sound", playInitialSound); // Maybe only do this first!
        startActivity(i);

    }

    private void tryPictureFromScreenOff(boolean turnScreenOff, boolean playInitialSound) {
        // Intent i = new Intent("com.google.glass.action.RECORD_CLIPLET");
        Intent i = new Intent("com.google.glass.action.TAKE_PICTURE_FROM_SCREEN_OFF");
        // Intent i = new Intent("com.google.glass.action.TAKE_PICTURE"); //Seems to be pretty damn
        // quick! but doesnt turn screen on, so maybe take from screen off then this?
        i.putExtra("should_finish_turn_screen_off", turnScreenOff); // Maybe do this last?
        i.putExtra("should_play_initial_sound", playInitialSound); // Maybe only do this first!
        startActivity(i);
    }

    private void setParams() {
        // set color efects to none
        cameraParameters = mCamera.getParameters();

        cameraParameters.setColorEffect(Camera.Parameters.EFFECT_NONE);

        // set antibanding to none
        if (cameraParameters.getAntibanding() != null) {
            cameraParameters.setAntibanding(Camera.Parameters.ANTIBANDING_OFF);
        }

        // set white ballance
        if (cameraParameters.getWhiteBalance() != null) {
            cameraParameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_FLUORESCENT);
        }

        // set flash
        if (cameraParameters.getFlashMode() != null) {
            cameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }

        // set zoom
        if (cameraParameters.isZoomSupported()) {
            cameraParameters.setZoom(0);
        }

        // set focus mode
        cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);

        for (Camera.Size size : mCamera.getParameters().getSupportedPictureSizes()) {
            Log.d("trey", "x" + size.width + " y" + size.height);
        }
        //cameraParameters.setPictureSize(3264, 2176); //Note 2?
        cameraParameters.setPictureSize(2592, 1944); //2nd to highest here
        mCamera.setParameters(cameraParameters);
    }

    private void nextBurst() {
        burst++;
        if (burst <= MAX_BURST) {
            if (burst == MAX_BURST && saveToTimeline) {
                finish(); //Trying to catch last split second before we can take a picture elsewhere
                //Maybe need to use shared service?
                burst = 0;
                tryPicture(true, true);
            } else {
                startBurst();
            }
        } else {
            burst = 0;
            // tryBurstStandard();
            finish();
        }

    }

    private void tryTimeBurst() {
        // FIXME: manually repeat the take picture every .x seconds?
    }

    private void startBurst() {
        isTakingPicture = true;
        
        mCamera.takePicture(new Camera.ShutterCallback() {
            @Override
            public void onShutter() {
                Log.d("trey", "onshutter");
            }
        }, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                // isTakingPicture = false;

                Log.d("trey", "onpicturetaken" + (bytes == null)); // bytes always seem to be null,
                                                                   // so no raw
                // nextBurst(); //Don't think this works in startBurst, not ready yet

            }

        }, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                // Maybe try this?
                isTakingPicture = false;

                Log.d("trey", "onpicture taken postview" + (bytes == null)); // bytes always seem to
                                                                             // be null, so no raw
                // next burst here doesnt really work
            }
        },

        getJpegCallback());
    }

    private Camera.PictureCallback getJpegCallback() {
        Camera.PictureCallback jpeg = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                // mCamera.stopPreview();
                // mPreviewRunning = false;

                if (data != null) {
                    FileOutputStream fos;
                    try {
                        // if (fileName.equals("")) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmssS");
                        String date = dateFormat.format(new Date());
                        String fileName =
                                Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/camera/b"
                                        + date + ".jpg";
                        // }

                        fos = new FileOutputStream(fileName);
                        fos.write(data);
                        fos.close();
                        Log.d("trey", "after file write");

                    } catch (IOException e) {
                        // do something about it
                        Log.d("trey", "write exp" + e);
                        e.printStackTrace();
                    }
                }

                // mCamera.release();
                mCamera.startPreview();
                // mCamera = null;
                nextBurst(); // have to burst after startpreview?

                Log.d("trey", "end of jpeg callback " + startTime);

            }
        };

        return jpeg;
    }

    private void stopBurst() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("trey", "onResume");
//        if (!startedBurst) {
//            initialStart();
//        }
    }

    private void initialStart() {
        //FIXME: this would be problematic if we left and came back or if something popped over this, so come back later
        startedBurst = true;
        setParams();
        startBurst();
    }
    
    @Override
    protected void onPause() {
        super.onPause();


    }

    // ----------------------------------------------------------------------

    @Override
    public void finish() {
        // Because the Camera object is a shared resource, it's very
        // important to release it when the activity is done with it
        // This might cause issues
        //Getting the camera in onresume was causing issues with having it from wake.
        
        //FIXME: do this in ondestroy
        if (mCamera != null) {
            mPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
        
        super.finish();
    }

    /**
     * A simple wrapper around a Camera and a SurfaceView that renders a centered preview of the
     * Camera to the surface. We need to center the SurfaceView because not all devices have cameras
     * that support preview sizes at the same aspect ratio as the device's display.
     */
    class Preview extends ViewGroup implements SurfaceHolder.Callback {
        private final String TAG = "Preview";

        SurfaceView mSurfaceView;
        SurfaceHolder mHolder;
        Camera.Size mPreviewSize;
        List<Camera.Size> mSupportedPreviewSizes;
        Camera mCamera;

        Preview(Context context) {
            super(context);

            mSurfaceView = new SurfaceView(context);
            addView(mSurfaceView);

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = mSurfaceView.getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public void setCamera(Camera camera) {
            mCamera = camera;
            if (mCamera != null) {
                mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
                requestLayout();
            }
        }

        public void switchCamera(Camera camera) {
            setCamera(camera);
            try {
                camera.setPreviewDisplay(mHolder);
            } catch (IOException exception) {
                Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
            }
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            requestLayout();

            camera.setParameters(parameters);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            // We purposely disregard child measurements because act as a
            // wrapper to a SurfaceView that centers the camera preview instead
            // of stretching it.
            final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
            setMeasuredDimension(width, height);

            if (mSupportedPreviewSizes != null) {
                mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
            }
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            if (changed && getChildCount() > 0) {
                final View child = getChildAt(0);

                final int width = r - l;
                final int height = b - t;

                int previewWidth = width;
                int previewHeight = height;
                if (mPreviewSize != null) {
                    previewWidth = mPreviewSize.width;
                    previewHeight = mPreviewSize.height;
                }

                // Center the child SurfaceView within the parent.
                if (width * previewHeight > height * previewWidth) {
                    final int scaledChildWidth = previewWidth * height / previewHeight;
                    child.layout((width - scaledChildWidth) / 2, 0, (width + scaledChildWidth) / 2,
                            height);
                } else {
                    final int scaledChildHeight = previewHeight * width / previewWidth;
                    child.layout(0, (height - scaledChildHeight) / 2, width,
                            (height + scaledChildHeight) / 2);
                }
            }
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, acquire the camera and tell it where
            // to draw.
            try {
                if (mCamera != null) {
                    mCamera.setPreviewDisplay(holder);
                }
            } catch (IOException exception) {
                Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
            }
            //initialStart();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // Surface will be destroyed when we return, so stop the preview.
            if (mCamera != null) {
                mCamera.stopPreview();
            }
            stopBurst();
        }

        private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
            final double ASPECT_TOLERANCE = 0.1;
            double targetRatio = (double) w / h;
            if (sizes == null)
                return null;

            Camera.Size optimalSize = null;
            double minDiff = Double.MAX_VALUE;

            int targetHeight = h;

            // Try to find an size match aspect ratio and size
            for (Camera.Size size : sizes) {
                double ratio = (double) size.width / size.height;
                if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                    continue;
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }

            // Cannot find the one match the aspect ratio, ignore the requirement
            if (optimalSize == null) {
                minDiff = Double.MAX_VALUE;
                for (Camera.Size size : sizes) {
                    if (Math.abs(size.height - targetHeight) < minDiff) {
                        optimalSize = size;
                        minDiff = Math.abs(size.height - targetHeight);
                    }
                }
            }
            return optimalSize;
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // Now that the size is known, set up the camera parameters and begin
            // the preview.
            Log.d("trey", "surfaceChanged");
            if (mCamera != null) { //null if we have the screen off initially
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
                requestLayout();
                
                mCamera.setParameters(parameters);
                mCamera.startPreview();
                
                initialStart(); //FIXME: is this the right spot?
            }
        }
    }
}
