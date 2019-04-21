package com.muhaiminur.videorecorder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.wonderkiln.camerakit.CameraView;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CameraKit_Example extends AppCompatActivity {
    @BindView(R.id.recordButton)
    Button recordButton;
    @BindView(R.id.camera)
    CameraView cameraView;

    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_kit);
        ButterKnife.bind(this);
        setUpCameraView();
    }

    @OnClick(R.id.recordButton)
    public void onViewClicked() {
        Log.d("Camerakit1", isRecording + "");
        if (isRecording) {
            cameraView.stopVideo();
            isRecording = false;
            return;
        }

        cameraView.captureVideo();
        isRecording = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }

    private void setUpCameraView() {
        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {
            }

            @Override
            public void onError(CameraKitError cameraKitError) {
            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {
                // The File parameter is an MP4 file.
                Log.d("testing-tag", String.valueOf(cameraKitVideo.getVideoFile()));
            }
        });
    }
}
