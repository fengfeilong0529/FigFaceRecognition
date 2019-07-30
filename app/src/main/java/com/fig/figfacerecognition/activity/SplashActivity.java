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
import android.view.WindowManager;

import com.fig.figfacerecognition.MainActivity;
import com.fig.figfacerecognition.R;

public class SplashActivity extends Activity {

    private static final int CAMERA_PERMISSION_CODE = 10;
    String[] NEED_PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkCameraPermission();
        } else {
            initTimer();
        }
    }

    private void initTimer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                toMainActivity();
            }
        }, 3000);
    }

    private void toMainActivity() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
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
}
