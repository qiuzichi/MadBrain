package com.unipad.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

/**
 * Activity收集器
 * Created by Wbj on 2016/4/7.
 */
public class ActivityCollector {

    private static List<Activity> mActivityList = new ArrayList<>();

    /**
     * 向集合中添加Activity
     */
    public static void addActivity(Activity activity) {
        mActivityList.add(activity);
    }

    public static Activity getTopActivity(){
        if (mActivityList.size()!=0){
            return mActivityList.get(mActivityList.size()-1);
        }else{
            LogUtil.e("ActivityCollector","topActivity is null"+new RuntimeException("topActivity is null"));
            return null;
        }
    }
    public static List<Activity> getmActivityList(){
        return mActivityList;
    }
    /**
     * 从集合中移除Activity
     */
    public static void removeActivity(Activity activity) {
        mActivityList.remove(activity);
    }

    /**
     * 清除集合中所有的Activity
     */
    public static void finishAllActivity() {
        for (Activity activity : mActivityList) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
    public static void finishAllActivityExceptTop(){
        for(int i = 0;i<mActivityList.size();i++){
            mActivityList.get(i).finish();
        }
    }
}
