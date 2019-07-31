package com.fig.figfacerecognition.util;

import android.os.SystemClock;
import android.view.View;

/**
 * Created by Administrator on 2018-08-21.
 */

public abstract class MultiClickListener implements View.OnClickListener {
    private long[] mHints = new long[5];
    private boolean isStart = false;

    public MultiClickListener(int maxCount) {
        isStart = false;
        mHints = new long[maxCount];
    }

    @Override
    public void onClick(View v) {
        if (isStart) {
        } else {
            System.arraycopy(mHints, 1, mHints, 0, mHints.length - 1);//把从第二位至最后一位之间的数字复制到第一位至倒数第一位
            mHints[mHints.length - 1] = SystemClock.uptimeMillis();//从开机到现在的时间毫秒数
            if (SystemClock.uptimeMillis() - mHints[0] <= 1000) {//连续点击之间间隔小于一秒，有效
                clickOverMaxCount();
                isStart = true;
            }
        }
    }

    protected abstract void clickOverMaxCount();
}
