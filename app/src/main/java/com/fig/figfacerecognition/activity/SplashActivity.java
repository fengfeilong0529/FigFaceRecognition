package com.fig.figfacerecognition.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.arcsoft.face.ActiveFileInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.fig.figfacerecognition.MainActivity;
import com.fig.figfacerecognition.R;
import com.fig.figfacerecognition.common.Constants;
import com.fig.figfacerecognition.util.MultiClickListener;
import com.fig.figfacerecognition.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SplashActivity extends Activity {
    private static final String TAG = "SplashActivity";

    @BindView(R.id.tv_slogan)
    TextView mTvSlogan;

    private static final int CAMERA_PERMISSION_CODE = 10;
    String[] NEED_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    private FaceEngine faceEngine = new FaceEngine();
    /**
     * 是否默认开启后置摄像头
     */
    private boolean mBackCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkCameraPermission();
        } else {
            initTimer();
        }
        activeEngine();
    }

    private void initView() {
        mTvSlogan.setOnClickListener(new MultiClickListener(2) {
            @Override
            protected void clickOverMaxCount() {
                ToastUtil.showToastShort(SplashActivity.this, "切换成功");
                mBackCamera = true;
            }
        });
    }

    private void initTimer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                toMainActivity();
            }
        }, 1000);
    }

    private void toMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.putExtra("isBackCamera", mBackCamera);
        startActivity(intent);
        finish();
    }

    private void checkCameraPermission() {
        boolean isAllGranted = checkPermissionAllGranted(NEED_PERMISSIONS);
        if (!isAllGranted) {
            ActivityCompat.requestPermissions(this, NEED_PERMISSIONS, CAMERA_PERMISSION_CODE);
        } else {
            initTimer();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isAllGranted = true;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                isAllGranted = false;
            }
        }
        if (isAllGranted) {
            toMainActivity();
        } else {
            checkCameraPermission();
        }
    }

    /**
     * 检查是否拥有指定的所有权限
     */
    protected boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    /**
     * 激活引擎
     */
    public void activeEngine() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                int activeCode = faceEngine.activeOnline(SplashActivity.this, Constants.APP_ID, Constants.SDK_KEY);
                emitter.onNext(activeCode);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer activeCode) {
                        if (activeCode == ErrorInfo.MOK) {
                            ToastUtil.showToastShort(SplashActivity.this, getString(R.string.active_success));
                        } else if (activeCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
                            ToastUtil.showToastShort(SplashActivity.this, getString(R.string.already_activated));
                        } else if (activeCode == ErrorInfo.MERR_ASF_SIGN_ERROR) {
                            faceEngine.activeOnline(SplashActivity.this, Constants.APP_ID, Constants.SDK_KEY);
                        } else {
                            ToastUtil.showToastShort(SplashActivity.this, getString(R.string.active_failed, activeCode));
                        }
                        ActiveFileInfo activeFileInfo = new ActiveFileInfo();
                        int res = faceEngine.getActiveFileInfo(SplashActivity.this, activeFileInfo);
                        if (res == ErrorInfo.MOK) {
                            Log.i(TAG, activeFileInfo.toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
