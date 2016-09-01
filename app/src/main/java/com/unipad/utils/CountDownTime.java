package com.unipad.utils;

import android.os.Message;

import com.unipad.brain.App;


/**
 * 倒计时器
 * Created by Weibj on 2016/4/7.
 */
public class CountDownTime implements App.HandlerCallback {
    private static final int MSG_ADD_TIME = 0X1001;
    private static final int MSG_END_TIME = 0X1002;
    /**
     * 倒时的总时间，单位：秒
     */
    private int seconds = 0;
    /**
     * 已经耗时，单位：秒
     */
    private int takeTime = 0;
    /**
     * 是否已经开始倒计时，默认为false(还没开始)
     */
    private boolean mStarted;
    /**
     * 是否处于暂停状态，默认为false
     */
    private boolean mPausing;
    private int hour = 0, minute = 0, second = 0;//时、分、秒
    private App.AppHandler mHandler = new App.AppHandler(this);
    private TimeListener countTimeListener = null;


    /**
     * @param seconds   倒时的总时间，单位：秒
     * @param autoStart 是否自动开始倒计时
     */
    public CountDownTime(int seconds, boolean autoStart) {
        if (seconds < 0) {
            seconds = 0;
        }
        this.seconds = seconds;
        hour = seconds % (24 * 3600) / 3600;
        minute = seconds % 3600 / 60;
        second = seconds % 60;

        if (autoStart) {
            this.startCountTime();
        }
    }

    /**
     * 开始倒计时
     */
    public void startCountTime() {
        if (!mStarted) {
            mStarted = true;
            LogUtil.e("","---startCountTime");
            mHandler.removeMessages(MSG_END_TIME);
            mHandler.removeMessages(MSG_ADD_TIME);
            mHandler.sendEmptyMessageDelayed(MSG_ADD_TIME, 1000);
        }
    }

    /**
     * 暂停倒计时
     */
    public void pauseCountTime() {
        if (!mPausing && mStarted) {
            LogUtil.e("","---pauseCountTime");
            mPausing = true;
            mHandler.removeMessages(MSG_END_TIME);
            mHandler.removeMessages(MSG_ADD_TIME);
        }
    }

    /**
     * 暂停后，恢复倒计时
     */
    public void resumeCountTime() {
        if (mPausing && mStarted) {
            LogUtil.e("","---resumeCountTime");
            mPausing = false;
            mHandler.sendEmptyMessageDelayed(MSG_ADD_TIME, 1000);
        }
    }

    /**
     * 停止倒计时
     *
     * @return 耗时，单位：秒
     */
    public int stopCountTime() {
        if (mStarted) {
            LogUtil.e("","---stopCountTime");
            mStarted = false;
            mHandler.removeMessages(MSG_END_TIME);
            mHandler.removeMessages(MSG_ADD_TIME);
        }
        return takeTime;
    }

    /**
     * 设置新的倒计时时间并重新开始计时
     *
     * @param newSeconds 新的倒计时时间，单位：秒
     * @return “XX:XX:XX”格式的时间
     */
    public String setNewSeconds(int newSeconds,boolean isAuto) {
        this.stopCountTime();

        if (newSeconds < 0) {
            newSeconds = 0;
        }
        this.seconds = newSeconds;
        hour = newSeconds % (24 * 3600) / 3600;
        minute = newSeconds % 3600 / 60;
        second = newSeconds % 60;

        mStarted = false;
        mPausing = false;
        takeTime = 0;
        mHandler.removeMessages(MSG_END_TIME);
        mHandler.removeMessages(MSG_ADD_TIME);
        if (isAuto) {
            this.startCountTime();
        }

        return this.getTimeString();
    }
    public String setNewSeconds(int newSeconds) {
        return setNewSeconds(newSeconds,true);
    }
    /**
     * @return “XX:XX:XX”格式的时间
     */
    public String getTimeString() {
        return StringUtil.addZero(hour, 2) + ":" + StringUtil.addZero(minute, 2) + ":"
                + StringUtil.addZero(second, 2);
    }
    public int getTime(){
        return hour*3600+minute*60+second;
    }
    @Override
    public void dispatchMessage(Message msg) {
        if (countTimeListener != null) {
            switch (msg.what) {
                case MSG_ADD_TIME:
                    second--;
                    takeTime++;
                    if (takeTime < seconds) {
                        if (second <= -1) {
                            second = 59;
                            minute--;
                            if (minute <= -1) {
                                minute = 59;
                                hour--;
                                if (hour <= -1) {
                                    hour = 23;
                                }
                            }
                        }

                        countTimeListener.getTime(StringUtil.addZero(hour, 2), StringUtil.addZero(minute, 2),
                                StringUtil.addZero(second, 2));

                        mHandler.sendEmptyMessageDelayed(MSG_ADD_TIME, 1000);
                    } else {
                        mHandler.sendEmptyMessage(MSG_END_TIME);
                    }
                    break;
                case MSG_END_TIME:
                    this.stopCountTime();
                    countTimeListener.endOfTime();
                    break;
                default:
                    break;
            }
        }
    }

    public void setTimeListener(TimeListener timeListener) {
        this.countTimeListener = timeListener;
    }

    /**
     * 倒计时监听接口
     */
    public interface TimeListener {

        /**
         * 倒计时结束
         */
        void endOfTime();

        /**
         * 正在倒计时中，以"XX"格式返回当前时、分、秒
         */
        void getTime(String hour, String minute, String second);
    }

}
