package com.muhaiminur.videorecorder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.otaliastudios.cameraview.CameraView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Custom_Three extends AppCompatActivity {

    @BindView(R.id.camera)
    CameraView camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom__three);
        ButterKnife.bind(this);
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        camera.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        camera.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        camera.destroy();
    }*/
}
