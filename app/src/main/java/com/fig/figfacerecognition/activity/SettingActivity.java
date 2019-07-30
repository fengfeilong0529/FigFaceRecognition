package com.fig.figfacerecognition.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fig.figfacerecognition.R;
import com.fig.figfacerecognition.faceserver.FaceServer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.btn_register_face)
    Button mBtnRegisterFace;
    @BindView(R.id.btn_clear_face)
    Button mBtnClearFace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_register_face, R.id.btn_clear_face})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_register_face:
                //注册人脸
                startActivity(new Intent(SettingActivity.this, AddFaceActivity.class));
                break;
            case R.id.btn_clear_face:
                //清空人脸库
                clearFaces();
                break;
        }
    }

    /**
     * 清空人脸库
     */
    public void clearFaces() {
        try {
            int faceNum = FaceServer.getInstance().getFaceNumber(this);
            if (faceNum == 0) {
                Toast.makeText(this, R.string.no_face_need_to_delete, Toast.LENGTH_SHORT).show();
            } else {
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(R.string.notification)
                        .setMessage(getString(R.string.confirm_delete, faceNum))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int deleteCount = FaceServer.getInstance().clearAllFaces(SettingActivity.this);
                                Toast.makeText(SettingActivity.this, deleteCount + " 个人脸已删除！", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create();
                dialog.show();
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }
}
