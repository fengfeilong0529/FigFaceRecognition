package com.fig.figfacerecognition.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
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
public class AddFaceActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int ALBUM_RESULT_CODE = 11;
    private static final int CAMERA_RESULT_CODE = 12;

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
    private ProgressDialog mProgressDialog;
    private BottomSheetDialog mBottomSheetDialog;
    private String mPhotoName;

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
                showBottomSheet();
                break;
            case R.id.btnRegister:
                //注册人脸
                if (mHeadBmp == null) {
                    ToastUtil.showToastShort(this, "请选择图片");
                    return;
                }
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
     * 打开系统相机
     */
    private void openSysCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mPhotoName = "Pic" + System.currentTimeMillis() + ".jpg";
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(
                new File(Environment.getExternalStorageDirectory(), mPhotoName)));
        startActivityForResult(cameraIntent, CAMERA_RESULT_CODE);
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
                case CAMERA_RESULT_CODE:
                    try {
                        File photo = new File(Environment.getExternalStorageDirectory(), mPhotoName);
                        Bitmap bitmap = BitmapFactory.decodeFile(photo.getAbsolutePath());
                        if (bitmap != null) {
                            mIvAvatar.setImageBitmap(bitmap);
                            mHeadBmp = bitmap;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mHeadBmp = ImageUtils.alignBitmapForBgr24(mHeadBmp);
                    byte[] bgr24 = ImageUtils.bitmapToBgr24(mHeadBmp);
                    final boolean success = FaceServer.getInstance().registerBgr24(AddFaceActivity.this, bgr24, mHeadBmp.getWidth(), mHeadBmp.getHeight(), mEtName.getText().toString().trim());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cancelProgressDailog();
                            ToastUtil.showToastLong(AddFaceActivity.this, success ? "注册成功" : "注册失败");
                            if (success) finish();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void showBottomSheet() {
        View view = View.inflate(this, R.layout.dialog_select_photo_style, null);
        View takePhoto = view.findViewById(R.id.tv_takephoto);
        View fromGallery = view.findViewById(R.id.tv_from_gallery);
        View cancel = view.findViewById(R.id.tv_cancel);

        takePhoto.setOnClickListener(this);
        fromGallery.setOnClickListener(this);
        cancel.setOnClickListener(this);

        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.show();
    }

    public void cancelBottomSheet() {
        if (mBottomSheetDialog != null) {
            mBottomSheetDialog.dismiss();
        }
    }

    public void showProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("注册中...");
        mProgressDialog.show();
    }

    public void cancelProgressDailog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelProgressDailog();
    }

    @Override
    public void onClick(View view) {
        cancelBottomSheet();
        switch (view.getId()) {
            case R.id.tv_takephoto:
                openSysCamera();
                break;
            case R.id.tv_from_gallery:
                openSysAlbum();
                break;
            default:
                break;
        }
    }
}
