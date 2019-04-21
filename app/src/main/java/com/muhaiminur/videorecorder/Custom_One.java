package com.muhaiminur.videorecorder;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Custom_One extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback, MediaRecorder.OnInfoListener {

    public static final String LOGTAG = "VIDEOCAPTURE";
    @BindView(R.id.progress_horizontal)
    ProgressBar progressHorizontal;
    @BindView(R.id.camera_start)
    Button cameraStart;
    Timer timer = new Timer();
    boolean stopped = true;

    private MediaRecorder recorder;
    private SurfaceHolder holder;
    private CamcorderProfile camcorderProfile;
    private Camera camera;
    boolean recording = false;
    boolean usecamera = true;
    boolean previewRunning = false;

    String video_source;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        setContentView(R.layout.activity_custom__one);
        ButterKnife.bind(this);
        SurfaceView cameraView =findViewById(R.id.CameraView);
        holder = cameraView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        cameraView.setClickable(true);
        cameraView.setOnClickListener(this);
        progressHorizontal.bringToFront();
    }

    private void prepareRecorder() {
        recorder = new MediaRecorder();
        // Both are required for Portrait Video
        camera.setDisplayOrientation(90);
        recorder.setOrientationHint(90);

        recorder.setPreviewDisplay(holder.getSurface());

        if (usecamera) {
            camera.unlock();
            recorder.setCamera(camera);
        }

        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

        recorder.setProfile(camcorderProfile);

        // This is all very sloppy
        if (camcorderProfile.fileFormat == MediaRecorder.OutputFormat.THREE_GPP) {
            try {
                File newFile = File.createTempFile("videocapture", ".3gp", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
                recorder.setOutputFile(newFile.getAbsolutePath());
                video_source=newFile.getAbsolutePath();
            } catch (IOException e) {
                Log.v(LOGTAG, "Couldn't create file");
                e.printStackTrace();
                finish();
            }
        } else if (camcorderProfile.fileFormat == MediaRecorder.OutputFormat.MPEG_4) {
            try {
                File newFile = File.createTempFile("videocapture", ".mp4", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
                recorder.setOutputFile(newFile.getAbsolutePath());
                video_source=newFile.getAbsolutePath();
            } catch (IOException e) {
                Log.v(LOGTAG, "Couldn't create file");
                e.printStackTrace();
                finish();
            }
        } else {
            try {
                File newFile = File.createTempFile("videocapture", ".mp4", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
                recorder.setOutputFile(newFile.getAbsolutePath());
                video_source=newFile.getAbsolutePath();
            } catch (IOException e) {
                Log.v(LOGTAG, "Couldn't create file");
                e.printStackTrace();
                finish();
            }

        }
        recorder.setMaxDuration(10000); // 50 seconds
        recorder.setOnInfoListener(this);
        //recorder.setMaxFileSize(5000000); // Approximately 5 megabytes
        //progressHorizontal.setMax(10000);
        //progressHorizontal.setProgress(0);
        startProgress();

        try {
            recorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            finish();
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }
    }

    public void onClick(View v) {
        /*if (recording) {
            stopped=true;
            progressHorizontal.setProgress(0);
            recorder.stop();
            if (usecamera) {
                try {
                    camera.reconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // recorder.release();
            recording = false;
            Log.v(LOGTAG, "Recording Stopped");
            // Let's prepareRecorder so we can record again
            prepareRecorder();
        } else {
            stopped=false;
            recording = true;
            recorder.start();
            Log.v(LOGTAG, "Recording Started");
        }*/
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.v(LOGTAG, "surfaceCreated");

        if (usecamera) {
            camera = Camera.open();

            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
                previewRunning = true;
            } catch (IOException e) {
                Log.e(LOGTAG, e.getMessage());
                e.printStackTrace();
            }
        }

    }


    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.v(LOGTAG, "surfaceChanged");

        if (!recording && usecamera) {
            if (previewRunning) {
                camera.stopPreview();
            }

            try {
                Camera.Parameters p = camera.getParameters();

                p.setPreviewSize(camcorderProfile.videoFrameWidth, camcorderProfile.videoFrameHeight);
                p.setPreviewFrameRate(camcorderProfile.videoFrameRate);

                camera.setParameters(p);

                camera.setPreviewDisplay(holder);
                camera.startPreview();
                previewRunning = true;
            } catch (IOException e) {
                Log.e(LOGTAG, e.getMessage());
                e.printStackTrace();
            }

            prepareRecorder();
        }
    }


    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v(LOGTAG, "surfaceDestroyed");
        if (recording) {
            recorder.stop();
            recording = false;
        }
        recorder.release();
        if (usecamera) {
            previewRunning = false;
            //camera.lock();
            camera.release();
        }
        //finish();
    }

    public void onInfo(MediaRecorder mr, int what, int extra) {
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            Log.d("VIDEOCAPTURE", "Maximum Duration Reached");
            Toast.makeText(this, "Maximum Duration Reached", Toast.LENGTH_SHORT).show();
            if (video_source!=null){
                Log.d("Result",video_source);
            }
            finish();
        }
    }

    @OnClick(R.id.camera_start)
    public void onViewClicked() {
        if (recording) {
            stopped = true;
            progressHorizontal.setProgress(0);
            recorder.stop();
            if (usecamera) {
                try {
                    camera.reconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // recorder.release();
            recording = false;
            Log.v(LOGTAG, "Recording Stopped");
            // Let's prepareRecorder so we can record again
            prepareRecorder();
        } else {
            stopped = false;
            recording = true;
            recorder.start();
            Log.v(LOGTAG, "Recording Started");
        }
    }

    void startProgress() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!stopped) {// call ui only when  the progress is not stopped
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                progressHorizontal.setProgress(progressHorizontal.getProgress() + 100);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }, 1, 100);
    }
}
