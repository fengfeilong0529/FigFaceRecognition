package com.fig.figfacerecognition.util;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fig.figfacerecognition.MyApplication;
import com.fig.figfacerecognition.R;


/**
 * Created by sfm on 2017-10-11.
 */

public class RecogToastUtil {
    private Toast mToast;
    private TextView mTextView;

    private RecogToastUtil() {
        mToast = Toast.makeText(MyApplication.getInstance(), "", Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 100);

        LayoutInflater inflater = LayoutInflater.from(MyApplication.getInstance());
        View view = inflater.inflate(R.layout.toast_recog_result, null);
        mTextView = (TextView) view.findViewById(R.id.message);
        mTextView.setTextSize(30);
        mToast.setView(view);
    }

    private static final class Holder {
        private static final RecogToastUtil INSTANCE = new RecogToastUtil();
    }

    public static RecogToastUtil getInstance() {
        return Holder.INSTANCE;
    }

    public static void show(CharSequence msg) {
        getInstance().mToast.show();
        getInstance().mTextView.setText(msg);
    }
}
