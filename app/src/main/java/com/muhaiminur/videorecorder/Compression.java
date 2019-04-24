package com.muhaiminur.videorecorder;

import android.app.ProgressDialog;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.github.tcking.giraffecompressor.GiraffeCompressor;
import com.github.tcking.viewquery.ViewQuery;
import com.madhavanmalolan.ffmpegandroidlibrary.Controller;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class Compression extends AppCompatActivity {

    @BindView(R.id.example_one)
    Button exampleOne;
    @BindView(R.id.example_two)
    Button exampleTwo;
    @BindView(R.id.example_three)
    Button exampleThree;
    @BindView(R.id.example_four)
    Button exampleFour;

    private ViewQuery $;

    String input = "";
    String outputFile;
    String bitRate;

    FFmpeg ffmpeg;

    nl.bravobit.ffmpeg.FFmpeg ffmpeg_bravo;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compression);
        ButterKnife.bind(this);
        GiraffeCompressor.init(this);

        $ = new ViewQuery(this);
        input = "/storage/emulated/0/Pictures/videocapture1717204762.mp4";
        outputFile = "/sdcard/test_compress_" + System.currentTimeMillis() + ".mp4";
        //bitRate="" + ($.screenHeight() * $.screenWidth());
        //bitRate = readMetaData();
        bitRate = 2800000 + "";

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(null);

        loadffmpeg();

        ffmpeg_bravo = nl.bravobit.ffmpeg.FFmpeg.getInstance(this);
        if (nl.bravobit.ffmpeg.FFmpeg.getInstance(this).isSupported()) {
            // ffmpeg is supported
            Log.d("TWO", "Supported");
        } else {
            // ffmpeg is not supported
            Log.d("TWO", "NOT Supported");
        }
    }

    @OnClick({R.id.example_one, R.id.example_two, R.id.example_three, R.id.example_four})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.example_one:
                final File inputFile = new File(input);
                if (!inputFile.exists()) {
                    Toast.makeText(getApplication(), "input file not exists", Toast.LENGTH_SHORT).show();
                    return;
                }
                GiraffeCompressor.create() //two implementations: mediacodec and ffmpeg,default is mediacodec
                        .input(inputFile) //set video to be compressed
                        .output(outputFile) //set compressed video output
                        .bitRate(Integer.parseInt(bitRate))//set bitrate 码率
                        .resizeFactor(Float.parseFloat("1.0"))//set video resize factor 分辨率缩放,默认保持原分辨率
                        /*.watermark("/sdcard/videoCompressor/watermarker.png")*///add watermark(take a long time) 水印图片(需要长时间处理)
                        .ready()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<GiraffeCompressor.Result>() {
                            @Override
                            public void onCompleted() {
                                //$.id(R.id.btn_start).enabled(true).text("start compress");
                                Log.d("check1", "start compress");
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                //$.id(R.id.btn_start).enabled(true).text("start compress");
                                //$.id(R.id.tv_console).text("error:"+e.getMessage());
                                Log.d("check1", "start compress");
                                Log.d("check2", "error:" + e.getMessage());

                            }

                            @Override
                            public void onNext(GiraffeCompressor.Result s) {
                                String msg = String.format("compress completed \ntake time:%s \nout put file:%s", s.getCostTime(), s.getOutput());
                                msg = msg + "\ninput file size:" + Formatter.formatFileSize(getApplication(), inputFile.length());
                                msg = msg + "\nout file size:" + Formatter.formatFileSize(getApplication(), new File(s.getOutput()).length());
                                System.out.println(msg);
                                //$.id(R.id.tv_console).text(msg);
                                Log.d("check3", "error:" + msg);

                            }
                        });
                break;
            case R.id.example_two:
                //readMetaData();
                //String cmd = "-version";
                //String cmd = "-i /storage/emulated/0/Pictures/videocapture1717204762.mp4 -vcodec h264 -b:v 1000k -acodec mp3 /sdcard/test_compress_abir.mp4";
                //String cmd = "-i /storage/emulated/0/Pictures/videocapture1717204762.mp4 -vcodec h264 -b:v 250k -acodec mp3 /sdcard/test_compress_abir_250k.mp4";
                //String cmd = "-i /storage/emulated/0/Pictures/videocapture1717204762.mp4 -vcodec libx264 -b:v 250k -acodec mp3 /sdcard/test_compress_abir_h265.mp4";
                String currentInputVideoPath = "/storage/emulated/0/Pictures/videocapture1717204762.mp4";
                String currentOutputVideoPath = "/sdcard/test_compress_abir_one.mp4";
                String cmd = "-y -i " + currentInputVideoPath + " -strict experimental -vcodec libx264 -preset ultrafast " +
                        "-crf 24 -acodec aac -ar 44100 -ac 2 -r 10 -b:a 96k -s 640x480 -aspect 16:9 " + currentOutputVideoPath;
                String[] command = cmd.split(" ");
                if (command.length != 0) {
                    excute_compress(command);
                } else {
                    Toast.makeText(Compression.this, "Empty", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.example_three:

                String cmd_string = "-i /storage/emulated/0/Pictures/videocapture1717204762.mp4 -vcodec h264 -b:v 250k -acodec mp3 /sdcard/test_compress_abir_three.mp4";
                String[] command_two = cmd_string.split(" ");
                if (command_two.length != 0) {
                    excute_compress_two(command_two);
                } else {
                    Toast.makeText(Compression.this, "Empty", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.example_four:

                String cmd_string_four = "-i /storage/emulated/0/Pictures/videocapture1717204762.mp4 -vcodec h264 -b:v 250k -acodec mp3 /sdcard/test_compress_abir_four.mp4";
                String[] command_four = cmd_string_four.split(" ");
                if (command_four.length != 0) {
                    Controller.getInstance().run(command_four);
                } else {
                    Toast.makeText(Compression.this, "Empty", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    public String readMetaData() {
        String result = "";
        File file = new File(input);

        MediaExtractor mex = new MediaExtractor();
        try {
            mex.setDataSource(input);// the adresss location of the sound on sdcard.
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        MediaFormat mf = mex.getTrackFormat(0);

        int bitRate = mf.getInteger(MediaFormat.KEY_BIT_RATE);
        Log.d("Bitrate", bitRate + "");
        return result = "" + bitRate;
    }

    public void loadffmpeg() {
        if (ffmpeg == null) {
            ffmpeg = FFmpeg.getInstance(this);
            try {
                ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                    @Override
                    public void onStart() {
                        Log.d("FFMPEG", "Start");
                    }

                    @Override
                    public void onFailure() {
                        Log.d("FFMPEG", "Failure");
                    }

                    @Override
                    public void onSuccess() {
                        Log.d("FFMPEG", "Success");
                    }

                    @Override
                    public void onFinish() {
                        Log.d("FFMPEG", "Finish");
                    }
                });
            } catch (FFmpegNotSupportedException e) {
                // Handle if FFmpeg is not supported by device
                e.printStackTrace();
            }
        } else {
            Log.d("FFMPEG", "Not Nulll In Initial");
        }
    }

    public void excute_compress(final String[] cmd) {
        try {
            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    //super.onStart();
                    Log.d("FFMPEG", "Started command : ffmpeg " + cmd);
                    progressDialog.setMessage("Processing...");
                    progressDialog.show();
                }

                @Override
                public void onProgress(String message) {
                    //super.onProgress(message);
                    Log.d("FFMPEG", "Started with COMMAND : " + cmd);
                    Log.d("FFMPEG", "PROGRESS output : " + message);
                    progressDialog.setMessage("Processing\n" + message);
                }

                @Override
                public void onFailure(String message) {
                    //super.onFailure(message);
                    Log.d("FFMPEG", "FAILED with output : " + message);
                }

                @Override
                public void onSuccess(String message) {
                    //super.onSuccess(message);
                    Log.d("FFMPEG", "SUCCESS with output : " + message);
                }

                @Override
                public void onFinish() {
                    //super.onFinish();
                    Log.d("FFMPEG", "Finished with output : " + cmd);
                    progressDialog.dismiss();
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
            e.printStackTrace();
        }
    }

    public void excute_compress_two(final String[] cmd) {
        try {
            ffmpeg_bravo.execute(cmd, new nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.d("FFMPEG", "Started command : ffmpeg " + cmd);
                    progressDialog.setMessage("Processing...");
                    progressDialog.show();
                }

                @Override
                public void onProgress(String message) {
                    Log.d("FFMPEG", "Started with COMMAND : " + cmd);
                    Log.d("FFMPEG", "PROGRESS output : " + message);
                    progressDialog.setMessage("Processing\n" + message);
                }

                @Override
                public void onFailure(String message) {
                    Log.d("FFMPEG", "FAILED with output : " + message);
                }

                @Override
                public void onSuccess(String message) {
                    Log.d("FFMPEG", "SUCCESS with output : " + message);
                }

                @Override
                public void onFinish() {
                    Log.d("FFMPEG", "Finished with output : " + cmd);
                    progressDialog.dismiss();
                }

            });
        } catch (Exception e) {
            // Handle if FFmpeg is already running
            e.printStackTrace();
        }
    }
}
