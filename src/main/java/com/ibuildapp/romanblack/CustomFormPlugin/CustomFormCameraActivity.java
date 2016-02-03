package com.ibuildapp.romanblack.CustomFormPlugin;


import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.appbuilder.sdk.android.AppBuilderModuleMain;
import com.appbuilder.sdk.android.Widget;

import java.io.File;
import java.io.FileOutputStream;

/**
 * This activity provides take a picture functionality.
 */
public class CustomFormCameraActivity extends AppBuilderModuleMain {
    private final int CAMERA_REQUEST = 1888;
    private final int EMAIL_SEND = 1889;
    private final int INITIALIZATION_FAILED = 0;
    private final int NEED_INTERNET_CONNECTION = 1;
    private final int HAVE_NO_CAMERA = 6;
    private final int CAMERA_HARDWARE_ERROR = 8;
    private ImageView imageView = null;
    private LinearLayout photoButton = null;
    private Bitmap image = null;
    private Widget widget = null;
    private String imagePath = "";
    private File imageFile = null;
    private File pictureFile = null;
    private Camera camera = null;
    private RelativeLayout previewLayout = null;
    private CameraPreview preview = null;
    private boolean wasCameraClick = false;
    private State cameraState = State.CAPTURING;
    private Uri fileName;

    private enum State {

        CAPTURING, SHARING
    }

    /**
     * This callback is calling when picture was taken from device camera.
     */
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            if (data == null) {
                handler.sendEmptyMessage(CAMERA_HARDWARE_ERROR);
                return;
            }

            pictureFile = new File(fileName.getPath());
            if (pictureFile == null) {
                return;
            }
            if (pictureFile.mkdirs()) {
            }
            if (pictureFile.exists()) {
                pictureFile.delete();
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                try {
                    camera.stopPreview();
                } catch (Exception e) {
                }
                camera.release();
            } catch (Exception e) {
                Log.w("CameraPlugin", "");
            }

            Intent resIt = new Intent();
            setResult(RESULT_OK, resIt);

            finish();
        }
    };
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INITIALIZATION_FAILED: {
                    Toast.makeText(CustomFormCameraActivity.this,
                            R.string.custom_form_alert_cannot_init_camera,
                            Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            finish();
                        }
                    }, 5000);
                }
                break;
                case NEED_INTERNET_CONNECTION: {
                    Toast.makeText(CustomFormCameraActivity.this, R.string.alert_no_internet,
                            Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            finish();
                        }
                    }, 5000);
                }
                break;
                case HAVE_NO_CAMERA: {
                    Toast.makeText(CustomFormCameraActivity.this,
                            R.string.custom_form_alert_no_camera,
                            Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
                case CAMERA_HARDWARE_ERROR: {
                    Toast.makeText(CustomFormCameraActivity.this,
                            R.string.custom_form_alert_camera_error,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            }
        }
    };

    @Override
    public void create() {
        try {

            setContentView(R.layout.custom_form_camera_preview);
            hideTopBar();

            Intent currentIntent = getIntent();
            fileName = currentIntent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
            photoButton = (LinearLayout) findViewById(R.id.romanblack_fanwall_btn_camera_capture);
            photoButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (!wasCameraClick) {
                        wasCameraClick = true;
                        camera.takePicture(null, null, mPicture);
                    }
                }
            });

            LinearLayout panel = (LinearLayout) findViewById(R.id.custom_form_camera_panel);

            try {
                camera = Camera.open();
            } catch (Exception ex) {
            }

            preview = new CameraPreview(this, camera);
            previewLayout = (RelativeLayout) findViewById(R.id.custom_form_camera_preview);
            previewLayout.addView(preview, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            configureCamera();


        } catch (Exception e) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST) {
            try {
                Uri uri = data.getData();

                image = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));

                imageView.setImageBitmap(image);

                imagePath = widget.getCachePath() + "/images/";
                File imageFilePath = new File(imagePath);
                imageFilePath.mkdirs();
                imageFile = new File(imagePath + "img.jpg");
                if (imageFile.exists()) {
                    imageFile.delete();
                }
                if (imageFile.createNewFile()) {

                    FileOutputStream outStream = new FileOutputStream(imagePath
                            + "img.jpg");
                    image.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                    outStream.flush();
                    outStream.close();
                }
            } catch (Exception e) {
            }
        } else if (requestCode == EMAIL_SEND) {
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (cameraState == State.CAPTURING) {
            configureCamera();
        }

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void destroy() {
        if (camera != null) {
            try {
                camera.stopPreview();
                camera.release();
            } catch (Exception ex) {
            }
        }
        super.destroy();
    }

    /**
     * Configures surface and camera rotation depending on device orientation.
     */
    private void configureCamera() {
        try {//ErrorLogging

            if (cameraState == State.CAPTURING) {
                camera.stopPreview();
                int surfRotation = ((WindowManager) getSystemService(WINDOW_SERVICE))
                        .getDefaultDisplay().getRotation();
                int degrees = 0;
                int rotation = 0;
                if (surfRotation == Surface.ROTATION_0) {
                    rotation = 90;
                    degrees = 90;
                } else if (surfRotation == Surface.ROTATION_90) {
                    rotation = 0;
                    degrees = 0;
                } else if (surfRotation == Surface.ROTATION_180) {
                    rotation = 270;
                    degrees = 270;
                } else if (surfRotation == Surface.ROTATION_270) {
                    rotation = 180;
                    degrees = 180;
                }

                camera.setDisplayOrientation(degrees);
                Camera.Parameters cameraParams = camera.getParameters();
                cameraParams.setRotation(rotation);
                camera.setParameters(cameraParams);

                int displayHeight = 0;
                int displayWidth = 0;
                Display display = getWindowManager().getDefaultDisplay();
                displayHeight = display.getHeight() - 20;
                displayWidth = display.getWidth() - 10;
                if (hasAdView()) {
                    displayHeight = displayHeight - 50;
                }

                int cameraHeight = 0;
                int cameraWidth = 0;
                Camera.Size cameraSize = camera.getParameters().getPictureSize();
                cameraHeight = cameraSize.height;
                cameraWidth = cameraSize.width;

                if ((surfRotation == Surface.ROTATION_0
                        || surfRotation == Surface.ROTATION_180)
                        && (cameraHeight < cameraWidth)) {
                    int exchg = cameraHeight;
                    cameraHeight = cameraWidth;
                    cameraWidth = exchg;
                }

                float hightCoef = (float) displayHeight / (float) cameraHeight;
                float widthCoef = (float) displayWidth / (float) cameraWidth;

                int layuotHeight = 0;
                int layoutWidth = 0;
                if (hightCoef < widthCoef) {
                    layuotHeight = displayHeight;
                    layoutWidth = (int) (cameraWidth * hightCoef);
                } else {
                    layoutWidth = displayWidth;
                    layuotHeight = (int) (cameraHeight * widthCoef);
                }

                preview.setLayoutParams(new RelativeLayout.LayoutParams(layoutWidth, layuotHeight));
            }
        } catch (Exception e) {
        }
    }
}
