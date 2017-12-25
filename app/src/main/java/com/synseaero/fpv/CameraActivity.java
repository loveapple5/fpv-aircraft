package com.synseaero.fpv;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.synseaero.util.DJIUtils;
import com.synseaero.view.ActionSheetDialog;

import dji.common.camera.DJICameraSettingsDef;
import dji.common.error.DJIError;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.camera.DJICamera;
import dji.sdk.products.DJIAircraft;

public class CameraActivity extends FragmentActivity implements View.OnClickListener {

    private static final int MSG_PHOTO_RATIO = 1;
    private static final int MSG_PHOTO_FORMAT = 2;
    private static final int MSG_WHITE_BALANCE = 3;
    private static final int MSG_VIDEO_RESOLVE_FRAME_RATE = 4;
    private static final int MSG_VIDEO_FORMAT = 5;
    private static final int MSG_VIDEO_STANDARD = 6;

    private View btnBack;

    private TextView tvPhotoMode;
    private TextView tvPhotoResolve;
    private TextView tvPhotoFormat;
    private TextView tvWhiteBalance;
    private TextView tvVideoResolve;
    private TextView tvFrameRate;
    private TextView tvVideoFormat;
    private TextView tvVideoSelect;

    private DJICamera djiCamera;

    private DJICameraSettingsDef.CameraPhotoAspectRatio photoRatio = DJICameraSettingsDef.CameraPhotoAspectRatio.AspectRatio_4_3;
    private DJICameraSettingsDef.CameraPhotoFileFormat photoFormat = DJICameraSettingsDef.CameraPhotoFileFormat.JPEG;
    private DJICameraSettingsDef.CameraWhiteBalance whiteBalance = DJICameraSettingsDef.CameraWhiteBalance.Auto;
    private DJICameraSettingsDef.CameraVideoFrameRate frameRate = DJICameraSettingsDef.CameraVideoFrameRate.FrameRate_24FPS;
    private DJICameraSettingsDef.CameraVideoResolution videoResolution = DJICameraSettingsDef.CameraVideoResolution.Resolution_4096x2160;
    private DJICameraSettingsDef.CameraVideoFileFormat videoFormat = DJICameraSettingsDef.CameraVideoFileFormat.MOV;
    private DJICameraSettingsDef.CameraVideoStandard videoStandard = DJICameraSettingsDef.CameraVideoStandard.PAL;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PHOTO_RATIO:
                    int photoResolutionId = DJIUtils.getMapValue(DJIUtils.photoResolutionMap, photoRatio);
                    tvPhotoResolve.setText(photoResolutionId);
                    break;
                case MSG_PHOTO_FORMAT:
                    int photoFormatId = DJIUtils.getMapValue(DJIUtils.photoFormatMap, photoFormat);
                    tvPhotoFormat.setText(photoFormatId);
                    break;
                case MSG_WHITE_BALANCE:
                    int whiteBalanceId = DJIUtils.getMapValue(DJIUtils.whiteBalanceMap, whiteBalance);
                    tvWhiteBalance.setText(whiteBalanceId);
                    break;
                case MSG_VIDEO_RESOLVE_FRAME_RATE:
                    int resolutionId = DJIUtils.getMapValue(DJIUtils.videoResolutionMap, videoResolution);
                    tvVideoResolve.setText(getString(resolutionId));
                    int rateId = DJIUtils.getMapValue(DJIUtils.frameRateMap, frameRate);
                    tvFrameRate.setText(getString(rateId));

                    break;
                case MSG_VIDEO_FORMAT:
                    int videoFormatId = DJIUtils.getMapValue(DJIUtils.videoFormatMap, videoFormat);
                    tvVideoFormat.setText(videoFormatId);
                    break;
                case MSG_VIDEO_STANDARD:
                    int videoStandardId = DJIUtils.getMapValue(DJIUtils.videoStandardMap, videoStandard);
                    tvVideoSelect.setText(videoStandardId);
                    break;
            }
        }
    };


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //禁止锁屏

        setContentView(R.layout.activity_camera);

        btnBack = findViewById(R.id.iv_back);
        btnBack.setOnClickListener(this);

        tvPhotoResolve = (TextView) findViewById(R.id.tv_photo_resolve);
        tvPhotoFormat = (TextView) findViewById(R.id.tv_photo_format);
        tvWhiteBalance = (TextView) findViewById(R.id.tv_white_balance);
        tvVideoResolve = (TextView) findViewById(R.id.tv_video_resolve);
        tvVideoResolve.setTag(R.string.resolution_4k);
        tvFrameRate = (TextView) findViewById(R.id.tv_frame_rate);
        tvFrameRate.setTag(R.string.frame_rate_24);
        tvVideoFormat = (TextView) findViewById(R.id.tv_video_format);
        tvVideoSelect = (TextView) findViewById(R.id.tv_video_select);

        findViewById(R.id.ll_camera_mode).setOnClickListener(this);
        findViewById(R.id.ll_photo_resolve).setOnClickListener(this);
        findViewById(R.id.ll_photo_format).setOnClickListener(this);
        findViewById(R.id.ll_white_balance).setOnClickListener(this);
        findViewById(R.id.ll_style).setOnClickListener(this);
        findViewById(R.id.ll_color).setOnClickListener(this);
        findViewById(R.id.ll_video_resolve).setOnClickListener(this);
        findViewById(R.id.ll_frame_rate).setOnClickListener(this);
        findViewById(R.id.ll_video_format).setOnClickListener(this);
        findViewById(R.id.ll_video_select).setOnClickListener(this);

        DJIAircraft djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        if (djiAircraft != null) {
            djiCamera = djiAircraft.getCamera();
            djiCamera.getPhotoRatio(new PhotoRatioCallback());
            djiCamera.getPhotoFileFormat(new PhotoFormatCallback());
            djiCamera.getWhiteBalanceAndColorTemperature(new WhiteBalanceCallback());
            djiCamera.getVideoResolutionAndFrameRate(new VideoResolutionCallback());
            djiCamera.getVideoFileFormat(new VideoFormatCallback());
            djiCamera.getVideoStandard(new VideoStandardCallback());
        }
    }

    class PhotoRatioCallback implements DJICommonCallbacks.DJICompletionCallbackWith<DJICameraSettingsDef.CameraPhotoAspectRatio> {

        @Override
        public void onSuccess(DJICameraSettingsDef.CameraPhotoAspectRatio cameraPhotoAspectRatio) {
            if (DJIUtils.photoResolutionMap.containsKey(cameraPhotoAspectRatio)) {
                photoRatio = cameraPhotoAspectRatio;
            }
            Message msg = Message.obtain();
            msg.what = MSG_PHOTO_RATIO;
            handler.sendMessage(msg);
        }

        @Override
        public void onFailure(DJIError djiError) {

        }
    }

    class PhotoFormatCallback implements DJICommonCallbacks.DJICompletionCallbackWith<DJICameraSettingsDef.CameraPhotoFileFormat> {

        @Override
        public void onSuccess(DJICameraSettingsDef.CameraPhotoFileFormat cameraPhotoFileFormat) {
            if (DJIUtils.photoFormatMap.containsKey(cameraPhotoFileFormat)) {
                photoFormat = cameraPhotoFileFormat;
            }
            Message msg = Message.obtain();
            msg.what = MSG_PHOTO_FORMAT;
            handler.sendMessage(msg);
        }

        @Override
        public void onFailure(DJIError djiError) {

        }
    }

    class WhiteBalanceCallback implements DJICommonCallbacks.DJICompletionCallbackWithTwoParam<DJICameraSettingsDef.CameraWhiteBalance, Integer> {

        @Override
        public void onSuccess(DJICameraSettingsDef.CameraWhiteBalance cameraWhiteBalance, Integer integer) {
            if (DJIUtils.whiteBalanceMap.containsKey(cameraWhiteBalance)) {
                whiteBalance = cameraWhiteBalance;
            }
            Message msg = Message.obtain();
            msg.what = MSG_WHITE_BALANCE;
            handler.sendMessage(msg);
        }

        @Override
        public void onFailure(DJIError djiError) {

        }
    }

    class VideoResolutionCallback implements DJICommonCallbacks.DJICompletionCallbackWithTwoParam<DJICameraSettingsDef.CameraVideoResolution, DJICameraSettingsDef.CameraVideoFrameRate> {


        @Override
        public void onSuccess(DJICameraSettingsDef.CameraVideoResolution cameraVideoResolution, DJICameraSettingsDef.CameraVideoFrameRate cameraVideoFrameRate) {
            if (DJIUtils.videoResolutionMap.containsKey(cameraVideoResolution)) {
                videoResolution = cameraVideoResolution;
            }
            if (DJIUtils.frameRateMap.containsKey(cameraVideoFrameRate)) {
                frameRate = cameraVideoFrameRate;
            }
            Message msg = Message.obtain();
            msg.what = MSG_VIDEO_RESOLVE_FRAME_RATE;
            handler.sendMessage(msg);
        }

        @Override
        public void onFailure(DJIError djiError) {

        }
    }

    class VideoFormatCallback implements DJICommonCallbacks.DJICompletionCallbackWith<DJICameraSettingsDef.CameraVideoFileFormat> {

        @Override
        public void onSuccess(DJICameraSettingsDef.CameraVideoFileFormat cameraVideoFileFormat) {
            if (DJIUtils.videoFormatMap.containsKey(cameraVideoFileFormat)) {
                videoFormat = cameraVideoFileFormat;
            }
            Message msg = Message.obtain();
            msg.what = MSG_VIDEO_FORMAT;
            handler.sendMessage(msg);
        }

        @Override
        public void onFailure(DJIError djiError) {

        }
    }

    class VideoStandardCallback implements DJICommonCallbacks.DJICompletionCallbackWith<DJICameraSettingsDef.CameraVideoStandard> {

        @Override
        public void onSuccess(DJICameraSettingsDef.CameraVideoStandard cameraVideoStandard) {
            if (DJIUtils.videoStandardMap.containsKey(cameraVideoStandard)) {
                videoStandard = cameraVideoStandard;
            }
            Message msg = Message.obtain();
            msg.what = MSG_VIDEO_STANDARD;
            handler.sendMessage(msg);
        }

        @Override
        public void onFailure(DJIError djiError) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.ll_camera_mode:
                //showCameraModeMenu();
                break;
            case R.id.ll_photo_resolve:
                showPhotoResolveMenu();
                break;
            case R.id.ll_photo_format:
                showPhotoFormatMenu();
                break;
            case R.id.ll_white_balance:
                showWhiteBalanceMenu();
                break;
            case R.id.ll_style:
                //showStyleMenu();
                break;
            case R.id.ll_color:
                //showColorMenu();
                break;
            case R.id.ll_video_resolve:
                showVideoResolveMenu();
                break;
            case R.id.ll_frame_rate:
                showFrameRateMenu();
                break;
            case R.id.ll_video_format:
                showVideoFormatMenu();
                break;
            case R.id.ll_video_select:
                showVideoSelectMenu();
                break;
        }
    }

    private void showCameraModeMenu() {

        ActionSheetDialog.OnSheetItemClickListener listener = new ActionSheetDialog.OnSheetItemClickListener() {

            @Override
            public void onClick(TextView v, int which) {
                Toast.makeText(CameraActivity.this, which + "", Toast.LENGTH_SHORT).show();
            }
        };

        new ActionSheetDialog(this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
                .addSheetItem(getString(R.string.single_photo), ActionSheetDialog.SheetItemColor.Black, listener)
                .addSheetItem(getString(R.string.hdr), ActionSheetDialog.SheetItemColor.Black, listener)
                .addSheetItem(getString(R.string.series_photo), ActionSheetDialog.SheetItemColor.Black, listener)
                .addSheetItem(getString(R.string.aeb_series_photo), ActionSheetDialog.SheetItemColor.Black, listener)
                .addSheetItem(getString(R.string.internal_photo), ActionSheetDialog.SheetItemColor.Black, listener).show();
    }

    private void showPhotoResolveMenu() {


        ActionSheetDialog.OnSheetItemClickListener listener = new ActionSheetDialog.OnSheetItemClickListener() {

            @Override
            public void onClick(TextView v, int which) {
                DJICameraSettingsDef.CameraPhotoAspectRatio ratio = DJICameraSettingsDef.CameraPhotoAspectRatio.AspectRatio_4_3;
                if (which == 2) {
                    ratio = DJICameraSettingsDef.CameraPhotoAspectRatio.AspectRatio_16_9;
                }
                djiCamera.setPhotoRatio(ratio, null);

                if (DJIUtils.photoResolutionMap.containsKey(ratio)) {
                    photoRatio = ratio;
                }
                Message msg = Message.obtain();
                msg.what = MSG_PHOTO_RATIO;
                handler.sendMessage(msg);
            }
        };

        new ActionSheetDialog(this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
                .addSheetItem(getString(R.string.ratio43), ActionSheetDialog.SheetItemColor.Black, listener)
                .addSheetItem(getString(R.string.ratio169), ActionSheetDialog.SheetItemColor.Black, listener).show();
    }

    private void showPhotoFormatMenu() {


        ActionSheetDialog.OnSheetItemClickListener listener = new ActionSheetDialog.OnSheetItemClickListener() {

            @Override
            public void onClick(TextView v, int which) {

                DJICameraSettingsDef.CameraPhotoFileFormat format = DJICameraSettingsDef.CameraPhotoFileFormat.JPEG;
                if (which == 2) {
                    format = DJICameraSettingsDef.CameraPhotoFileFormat.RAW;
                }
                djiCamera.setPhotoFileFormat(format, null);

                if (DJIUtils.photoFormatMap.containsKey(format)) {
                    photoFormat = format;
                }
                Message msg = Message.obtain();
                msg.what = MSG_PHOTO_FORMAT;
                handler.sendMessage(msg);
            }
        };

        new ActionSheetDialog(this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
                .addSheetItem(getString(R.string.jpeg), ActionSheetDialog.SheetItemColor.Black, listener)
                .addSheetItem(getString(R.string.raw), ActionSheetDialog.SheetItemColor.Black, listener).show();
    }

    private void showWhiteBalanceMenu() {
        ActionSheetDialog.OnSheetItemClickListener listener = new ActionSheetDialog.OnSheetItemClickListener() {

            @Override
            public void onClick(TextView v, int which) {
                DJICameraSettingsDef.CameraWhiteBalance whiteBalance = DJICameraSettingsDef.CameraWhiteBalance.Auto;
                if (which == 2) {
                    whiteBalance = DJICameraSettingsDef.CameraWhiteBalance.Sunny;
                } else if (which == 3) {
                    whiteBalance = DJICameraSettingsDef.CameraWhiteBalance.Cloudy;
                }
                djiCamera.setWhiteBalanceAndColorTemperature(whiteBalance, 0, null);


                CameraActivity.this.whiteBalance = whiteBalance;
                Message msg = Message.obtain();
                msg.what = MSG_WHITE_BALANCE;
                handler.sendMessage(msg);
            }
        };

        new ActionSheetDialog(this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
                .addSheetItem(getString(R.string.auto), ActionSheetDialog.SheetItemColor.Black, listener)
                .addSheetItem(getString(R.string.sunny), ActionSheetDialog.SheetItemColor.Black, listener)
                .addSheetItem(getString(R.string.cloudy), ActionSheetDialog.SheetItemColor.Black, listener).show();
    }

    private void showVideoResolveMenu() {

        final int[] strResIds = new int[]{R.string.resolution_4k, R.string.resolution_1080p, R.string.resolution_720p};

        ActionSheetDialog.OnSheetItemClickListener listener = new ActionSheetDialog.OnSheetItemClickListener() {

            @Override
            public void onClick(TextView v, int which) {

                Object key = DJIUtils.getMapKey(DJIUtils.videoResolutionMap, strResIds[which - 1]);

                if (key != null && key instanceof DJICameraSettingsDef.CameraVideoResolution) {
                    DJICameraSettingsDef.CameraVideoResolution resolution = (DJICameraSettingsDef.CameraVideoResolution) key;
                    djiCamera.setVideoResolutionAndFrameRate(resolution, frameRate, null);

                    videoResolution = resolution;
                    Message msg = Message.obtain();
                    msg.what = MSG_VIDEO_RESOLVE_FRAME_RATE;
                    handler.sendMessage(msg);

                }

            }
        };

        new ActionSheetDialog(this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
                .addSheetItem(getString(strResIds[0]), ActionSheetDialog.SheetItemColor.Black, listener)
                .addSheetItem(getString(strResIds[1]), ActionSheetDialog.SheetItemColor.Black, listener)
                .addSheetItem(getString(strResIds[2]), ActionSheetDialog.SheetItemColor.Black, listener).show();
    }

    private void showFrameRateMenu() {

        final int[] strResIds = new int[]{R.string.frame_rate_24, R.string.frame_rate_30, R.string.frame_rate_60};

        ActionSheetDialog.OnSheetItemClickListener listener = new ActionSheetDialog.OnSheetItemClickListener() {

            @Override
            public void onClick(TextView v, int which) {
                Object key = DJIUtils.getMapKey(DJIUtils.frameRateMap, strResIds[which - 1]);

                if (key != null && key instanceof DJICameraSettingsDef.CameraVideoFrameRate) {
                    DJICameraSettingsDef.CameraVideoFrameRate rate = (DJICameraSettingsDef.CameraVideoFrameRate) key;
                    djiCamera.setVideoResolutionAndFrameRate(videoResolution, rate, null);

                    frameRate = rate;
                    Message msg = Message.obtain();
                    msg.what = MSG_VIDEO_RESOLVE_FRAME_RATE;
                    handler.sendMessage(msg);


                }
            }
        };

        new ActionSheetDialog(this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
                .addSheetItem(getString(strResIds[0]), ActionSheetDialog.SheetItemColor.Black, listener)
                .addSheetItem(getString(strResIds[1]), ActionSheetDialog.SheetItemColor.Black, listener)
                .addSheetItem(getString(strResIds[2]), ActionSheetDialog.SheetItemColor.Black, listener).show();
    }

    private void showVideoFormatMenu() {

        final int[] strResIds = new int[]{R.string.mov, R.string.mp4};

        ActionSheetDialog.OnSheetItemClickListener listener = new ActionSheetDialog.OnSheetItemClickListener() {

            @Override
            public void onClick(TextView v, int which) {
                Object key = DJIUtils.getMapKey(DJIUtils.videoFormatMap, strResIds[which - 1]);
                if (key instanceof DJICameraSettingsDef.CameraVideoFileFormat) {
                    DJICameraSettingsDef.CameraVideoFileFormat format = (DJICameraSettingsDef.CameraVideoFileFormat) key;
                    djiCamera.setVideoFileFormat(format, null);

                    videoFormat = format;
                    Message msg = Message.obtain();
                    msg.what = MSG_VIDEO_FORMAT;
                    handler.sendMessage(msg);
                }
            }
        };

        new ActionSheetDialog(this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
                .addSheetItem(getString(strResIds[0]), ActionSheetDialog.SheetItemColor.Black, listener)
                .addSheetItem(getString(strResIds[1]), ActionSheetDialog.SheetItemColor.Black, listener).show();
    }

    private void showVideoSelectMenu() {

        final int[] strResIds = new int[]{R.string.pal, R.string.ntsc};

        ActionSheetDialog.OnSheetItemClickListener listener = new ActionSheetDialog.OnSheetItemClickListener() {

            @Override
            public void onClick(TextView v, int which) {

                Object key = DJIUtils.getMapKey(DJIUtils.videoStandardMap, strResIds[which - 1]);
                if (key instanceof DJICameraSettingsDef.CameraVideoStandard) {
                    DJICameraSettingsDef.CameraVideoStandard standard = (DJICameraSettingsDef.CameraVideoStandard) key;
                    djiCamera.setVideoStandard(standard, null);

                    videoStandard = standard;
                    Message msg = Message.obtain();
                    msg.what = MSG_VIDEO_STANDARD;
                    handler.sendMessage(msg);
                }
            }
        };

        new ActionSheetDialog(this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
                .addSheetItem(getString(strResIds[0]), ActionSheetDialog.SheetItemColor.Black, listener)
                .addSheetItem(getString(strResIds[1]), ActionSheetDialog.SheetItemColor.Black, listener).show();
    }


}