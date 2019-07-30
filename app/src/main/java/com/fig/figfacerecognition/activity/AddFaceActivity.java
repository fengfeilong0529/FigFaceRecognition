package com.fig.figfacerecognition.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.arcsoft.face.util.ImageUtils;
import com.fig.figfacerecognition.R;
import com.fig.figfacerecognition.faceserver.FaceServer;
import com.fig.figfacerecognition.util.FileUtil;
import com.fig.figfacerecognition.util.ToastUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 注册人脸
 */
public class AddFaceActivity extends AppCompatActivity {

    private static final int ALBUM_RESULT_CODE = 11;

    @BindView(R.id.ivAvatar)
    ImageView mIvAvatar;
    @BindView(R.id.etName)
    EditText mEtName;
    @BindView(R.id.btnRegister)
    Button mBtnRegister;
    /**
     * 头像照片
     */
    private Bitmap mHeadBmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_face);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.ivAvatar, R.id.btnRegister})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivAvatar:
                //选择头像
                openSysAlbum();
                break;
            case R.id.btnRegister:
                //注册人脸
                if (TextUtils.isEmpty(mEtName.getText().toString().trim())) {
                    ToastUtil.showToastShort(this, "姓名不能为空！");
                    return;
                }
                registerFaceFromGallery();
                break;
        }
    }

    /**
     * 打开系统相册
     */
    private void openSysAlbum() {
        Intent albumIntent = new Intent(Intent.ACTION_PICK);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(albumIntent, ALBUM_RESULT_CODE);
    }

    /**
     * 获取照片
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ALBUM_RESULT_CODE:
                    Uri uri = data.getData();
                    String filePath = FileUtil.getFilePathByUri(this, uri);

                    if (!TextUtils.isEmpty(filePath)) {
                        File jpgFile = new File(filePath);
                        Bitmap bitmap = BitmapFactory.decodeFile(jpgFile.getAbsolutePath());
                        if (bitmap != null) {
                            mIvAvatar.setImageBitmap(bitmap);
                            mHeadBmp = bitmap;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 照片注册人脸
     */
    public void registerFaceFromGallery() {
        try {
            if (mHeadBmp != null) {
                mHeadBmp = ImageUtils.alignBitmapForBgr24(mHeadBmp);
                byte[] bgr24 = ImageUtils.bitmapToBgr24(mHeadBmp);
                boolean success = FaceServer.getInstance().registerBgr24(AddFaceActivity.this, bgr24, mHeadBmp.getWidth(), mHeadBmp.getHeight(), mEtName.getText().toString().trim());
                ToastUtil.showToastLong(AddFaceActivity.this, success ? "注册成功" : "注册失败");
            } else {
                ToastUtil.showToastLong(AddFaceActivity.this, "获取图片失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
