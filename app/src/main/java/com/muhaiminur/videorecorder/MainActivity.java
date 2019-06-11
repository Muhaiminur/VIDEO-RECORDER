package com.muhaiminur.videorecorder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.iceteck.silicompressorr.SiliCompressor;
import com.jmolsmobile.landscapevideocapture.VideoCaptureActivity;
import com.jmolsmobile.landscapevideocapture.configuration.CaptureConfiguration;
import com.jmolsmobile.landscapevideocapture.configuration.PredefinedCaptureConfigurations;
import com.karan.churi.PermissionManager.PermissionManager;

import java.io.File;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    PermissionManager permissionManager;
    @BindView(R.id.example_one)
    Button exampleOne;
    @BindView(R.id.example_two)
    Button exampleTwo;
    @BindView(R.id.example_three)
    Button exampleThree;
    @BindView(R.id.example_four)
    Button exampleFour;
    @BindView(R.id.example_five)
    Button exampleFive;
    @BindView(R.id.example_six)
    Button exampleSix;

    String compare_source;
    String compare_destination;
    File source_file;
    @BindView(R.id.example_seven)
    Button exampleSeven;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        permissionManager = new PermissionManager() {
        };
        permissionManager.checkAndRequestPermissions(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        permissionManager.checkResult(requestCode, permissions, grantResults);
    }

    @OnClick({R.id.example_one, R.id.example_two, R.id.example_three, R.id.example_four, R.id.example_five, R.id.example_six, R.id.example_seven})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.example_one:
                startActivity(new Intent(this, MediaRecorder_Example.class));
                break;
            case R.id.example_two:
                startActivity(new Intent(this, Custom_One.class));
                break;
            case R.id.example_three:
                startActivity(new Intent(this, FFmpegVideoRecorder.class));
                break;
            case R.id.example_four:
                startActivity(new Intent(this, CameraKit_Example.class));
                break;
            case R.id.example_five:

                /*compare_source=new File("/storage/emulated/0/Pictures/videocapture1557585091.mp4").getAbsolutePath();
                if (compare_source!= null) {
                    //create destination directory
                    File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getPackageName() + "/media/videos");
                    if (f.mkdirs() || f.isDirectory()) {
                        //compress and output new video specs
                        Log.d("VIDEOCAPTURE","Both Have");
                        Log.d("Source Path",compare_source);
                        Log.d("Destination Path",f.getPath());
                        compare_destination=f.getPath();
                        try{
                            new VideoCompressAsyncTask(this).execute(compare_source, f.getPath());
                        }catch(Exception e){
                            Log.d("Error visited outlet",e.getMessage());
                            Log.d("Error Line Number",Log.getStackTraceString(e));
                        }

                    }else {
                        Log.d("VIDEOCAPTURE","No destination");
                    }
                }else {
                    Log.d("VIDEOCAPTURE","No Source");
                }*/
                startActivity(new Intent(this, Custom_Two.class));
                break;
            case R.id.example_six:
                startActivity(new Intent(this, Compression.class));
                break;
            case R.id.example_seven:
                //startActivity(new Intent(this, Custom_Three.class));

                final CaptureConfiguration config = createCaptureConfiguration();
                final String filename = "ABir_seven.mp4";
                final Intent intent = new Intent(MainActivity.this, VideoCaptureActivity.class);
                intent.putExtra(VideoCaptureActivity.EXTRA_CAPTURE_CONFIGURATION, config);
                intent.putExtra(VideoCaptureActivity.EXTRA_OUTPUT_FILENAME, filename);
                //intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0);
                //intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT,200000);
                //intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,10000);
                startActivityForResult(intent, 101);
                break;
        }
    }


    class VideoCompressAsyncTask extends AsyncTask<String, String, String> {

        Context mContext;

        public VideoCompressAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_photo_camera_white_48px));
            compressionMsg.setVisibility(View.VISIBLE);
            picDescription.setVisibility(View.GONE);*/
        }

        @Override
        protected String doInBackground(String... paths) {
            String filePath = null;
            try {
                //Log.d("Source Path 1", FileProvider.getUriForFile(MainActivity.this,"com.muhaiminur.videorecorder.provider",source_file).toString());
                Log.d("Destination Path", compare_destination);

                Log.d("Destination Path", paths[0] + " skn " + paths[1]);
                filePath = SiliCompressor.with(MainActivity.this).compressVideo(compare_source, compare_destination);
                //filePath = SiliCompressor.with(mContext).compressVideo(FileProvider.getUriForFile(getApplicationContext(),"com.muhaiminur.videorecorder.provider",source_file).toString(), compare_destination);
                Log.d("VIDEOCAPTURE", "Filepath" + filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return filePath;

        }


        @Override
        protected void onPostExecute(String compressedFilePath) {
            super.onPostExecute(compressedFilePath);
            File imageFile = new File(compressedFilePath);
            float length = imageFile.length() / 1024f; // Size in KB
            String value;
            if (length >= 1024)
                value = length / 1024f + " MB";
            else
                value = length + " KB";
            String text = String.format(Locale.US, "%s\nName: %s\nSize: %s", "Complete", imageFile.getName(), value);
            /*compressionMsg.setVisibility(View.GONE);
            picDescription.setVisibility(View.VISIBLE);
            picDescription.setText(text);*/
            Log.i("Silicompressor", "Path: " + compressedFilePath);
        }
    }

    private CaptureConfiguration createCaptureConfiguration() {
        final PredefinedCaptureConfigurations.CaptureResolution resolution = PredefinedCaptureConfigurations.CaptureResolution.RES_480P;
        final PredefinedCaptureConfigurations.CaptureQuality quality = PredefinedCaptureConfigurations.CaptureQuality.LOW;

        CaptureConfiguration.Builder builder = new CaptureConfiguration.Builder(resolution, quality);

        try {
            int maxDuration = Integer.valueOf("10");
            builder.maxDuration(maxDuration);
        } catch (final Exception e) {
            //NOP
        }
        try {
            int maxFileSize = Integer.valueOf("2000000");
            builder.maxFileSize(maxFileSize);
        } catch (final Exception e) {
            //NOP
        }
        /*try {
            int fps = Integer.valueOf(fpsEt.getEditableText().toString());
            builder.frameRate(fps);
        } catch (final Exception e) {
            //NOP
        }*/
        if (/*showTimerCb.isChecked()*/true) {
            builder.showRecordingTime();
        }
        if (/*!allowFrontCameraCb.isChecked()*/false) {
            builder.noCameraToggle();
        }

        return builder.build();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        String filename;
        String statusMessage;
        if (resultCode == Activity.RESULT_OK) {
            filename = data.getStringExtra(VideoCaptureActivity.EXTRA_OUTPUT_FILENAME);
            Log.d("File Name",filename);
            statusMessage = String.format("Abir Success", filename);
        } else if (resultCode == Activity.RESULT_CANCELED) {
            filename = null;
            statusMessage = "ABir Cancel";
        } else if (resultCode == VideoCaptureActivity.RESULT_ERROR) {
            filename = null;
            statusMessage = "ABir Fail";
        }
        //updateStatusAndThumbnail();

        super.onActivityResult(requestCode, resultCode, data);
    }
}
