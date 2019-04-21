package com.muhaiminur.videorecorder;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.amosyuen.videorecorder.activity.FFmpegRecorderActivity;
import com.amosyuen.videorecorder.activity.params.FFmpegRecorderActivityParams;
import com.amosyuen.videorecorder.camera.CameraControllerI;
import com.amosyuen.videorecorder.recorder.common.ImageFit;
import com.amosyuen.videorecorder.recorder.common.ImageScale;
import com.amosyuen.videorecorder.recorder.common.ImageSize;
import com.amosyuen.videorecorder.recorder.params.EncoderParamsI;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FFmpegVideoRecorder extends AppCompatActivity {

    @BindView(R.id.start_recording)
    Button startRecording;

    private static final int RECORD_VIDEO_REQUEST = 2000;
    static final String FILE_PREFIX = "recorder-";
    static final String THUMBNAIL_FILE_EXTENSION = "jpg";
    private File mVideoFile;
    private File mThumbnailFile;
    private OnVideoRecorderListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ffmpeg_video_recorder);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.start_recording)
    public void onViewClicked() {
        createTempFiles();
        FFmpegRecorderActivityParams.Builder paramsBuilder =
                FFmpegRecorderActivityParams.builder(this)
                        .setVideoOutputFileUri(mVideoFile)
                        .setVideoThumbnailOutputFileUri(mThumbnailFile);

        paramsBuilder.recorderParamsBuilder()
                .setVideoSize(new ImageSize(640, 480))
                .setVideoCodec(EncoderParamsI.VideoCodec.H264)
                .setVideoBitrate(400000)
                .setVideoFrameRate(30)
                .setVideoImageFit(ImageFit.FILL)
                .setVideoImageScale(ImageScale.DOWNSCALE)
                .setShouldCropVideo(true)
                .setShouldPadVideo(true)
                .setVideoCameraFacing(CameraControllerI.Facing.BACK)
                .setAudioCodec(EncoderParamsI.AudioCodec.AAC)
                .setAudioSamplingRateHz(44100)
                .setAudioBitrate(400000)
                .setAudioChannelCount(2)
                .setOutputFormat(EncoderParamsI.OutputFormat.MP4);

        paramsBuilder.interactionParamsBuilder().setMaxRecordingMillis(
                (int) TimeUnit.SECONDS.toMillis(30));
        /*if (minRecordingSeconds.isPresent()) {
            paramsBuilder.interactionParamsBuilder().setMinRecordingMillis(
                    (int) TimeUnit.SECONDS.toMillis(minRecordingSeconds.get()));
        }
        if (maxRecordingSeconds.isPresent()) {
            paramsBuilder.interactionParamsBuilder().setMaxRecordingMillis(
                    (int) TimeUnit.SECONDS.toMillis(maxRecordingSeconds.get()));
        }
        if (maxFileSizeMegaBytes.isPresent()) {
            paramsBuilder.interactionParamsBuilder()
                    .setMaxFileSizeBytes(1024 * 1024 * maxFileSizeMegaBytes.get());
        }*/

        // Start the intent for the activity so that the activity can handle the requestCode
        Intent intent = new Intent(this, FFmpegRecorderActivity.class);
        intent.putExtra(FFmpegRecorderActivity.REQUEST_PARAMS_KEY, paramsBuilder.build());
        startActivityForResult(intent, RECORD_VIDEO_REQUEST);
    }
    private void createTempFiles() {
        if (mVideoFile != null && mThumbnailFile != null) {
            return;
        }

        File dir = getExternalCacheDir();
        try {
            String videoExt = EncoderParamsI.OutputFormat.MP4.getFileExtension();
            while (true) {
                int n = (int) (Math.random() * Integer.MAX_VALUE);
                String videoFileName = FILE_PREFIX + Integer.toString(n) + "." + videoExt;
                mVideoFile = new File(dir, videoFileName);
                if (!mVideoFile.exists() && mVideoFile.createNewFile()) {
                    String thumbnailFileName =
                            FILE_PREFIX + Integer.toString(n) + "." + THUMBNAIL_FILE_EXTENSION;
                    mThumbnailFile = new File(dir, thumbnailFileName);
                    if (!mThumbnailFile.exists() && mThumbnailFile.createNewFile()) {
                        return;
                    }
                    mVideoFile.delete();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RECORD_VIDEO_REQUEST:
                switch (resultCode) {
                    case RESULT_OK:
                        Uri videoUri = data.getData();
                        Uri thumbnailUri =
                                data.getParcelableExtra(FFmpegRecorderActivity.RESULT_THUMBNAIL_URI_KEY);
                        mListener.onVideoRecorded(VideoFile.create(
                                new File(videoUri.getPath()), new File(thumbnailUri.getPath())));
                        mVideoFile = null;
                        mThumbnailFile = null;
                        Log.d("Record","OK");
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.d("Record","CANCEL");
                        break;
                    case FFmpegRecorderActivity.RESULT_ERROR:
                        Exception error = (Exception)
                                data.getSerializableExtra(FFmpegRecorderActivity.RESULT_ERROR_PATH_KEY);
                        new AlertDialog.Builder(this)
                                .setCancelable(false)
                                .setTitle(R.string.app_name)
                                .setMessage(error.getLocalizedMessage())
                                .setPositiveButton("ok", null)
                                .show();
                        Log.d("Error",error.getLocalizedMessage());
                        break;
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
    public interface OnVideoRecorderListener {
        void onVideoRecorded(VideoFile videoFile);
    }
}
