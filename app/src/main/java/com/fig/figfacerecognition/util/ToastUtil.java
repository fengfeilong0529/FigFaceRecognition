package com.fig.figfacerecognition.util;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/7/24.
 */

public class ToastUtil {
    private static Toast toast;
    private static Handler mHandler = new Handler();

    private ToastUtil(){}

    //显示短toast
    public static void showToastShort(final Context context, final String content) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(context,content,  Toast.LENGTH_SHORT);
                } else {
                    toast.setText(content);
                }
                toast.show();
            }
        });

    }


    //显示长toast
    public static void showToastLong(final Context context, final String content) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(context,content,  Toast.LENGTH_LONG);
                } else {
                    toast.setText(content);
                }
                toast.show();
            }
        });
    }
}
