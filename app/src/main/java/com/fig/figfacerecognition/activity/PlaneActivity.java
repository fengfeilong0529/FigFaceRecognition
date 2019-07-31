package com.fig.figfacerecognition.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.fig.figfacerecognition.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaneActivity extends AppCompatActivity implements View.OnTouchListener {

    @BindView(R.id.ivPlane)
    ImageView mIvPlane;
    @BindView(R.id.ivMonster)
    ImageView mIvMonster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plane);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        sp = this.getSharedPreferences("config", Context.MODE_PRIVATE);
        mIvPlane.setOnTouchListener(this);
    }

    private int sx;
    private int sy;
    private SharedPreferences sp;

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        // event.getRawX(); //获取手指第一次接触屏幕在x方向的坐标
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:// 获取手指第一次接触屏幕
                sx = (int) event.getRawX();
                sy = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:// 手指在屏幕上移动对应的事件
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                // 获取手指移动的距离
                int dx = x - sx;
                int dy = y - sy;
                // 得到imageView最开始的各顶点的坐标
                int l = mIvPlane.getLeft();
                int r = mIvPlane.getRight();
                int t = mIvPlane.getTop();
                int b = mIvPlane.getBottom();
                // 更改imageView在窗体的位置
                mIvPlane.layout(l + dx, t + dy, r + dx, b + dy);
                // 获取移动后的位置
                sx = (int) event.getRawX();
                sy = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:// 手指离开屏幕对应事件
                // 记录最后图片在窗体的位置
//                int lasty = mIvPlane.getTop();
//                int lastx = mIvPlane.getLeft();
//                mIvPlane.setImageResource(R.mipmap.ic_plane);
//                SharedPreferences.Editor editor = sp.edit();
//                editor.putInt("lasty", lasty);
//                editor.putInt("lastx", lastx);
//                editor.commit();
                break;
        }
        return true;
    }
}
