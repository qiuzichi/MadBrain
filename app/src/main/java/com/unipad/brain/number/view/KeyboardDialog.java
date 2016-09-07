package com.unipad.brain.number.view;

import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.unipad.brain.App;
import com.unipad.brain.R;
import com.unipad.utils.LogUtil;

public class KeyboardDialog extends RelativeLayout implements View.OnClickListener {
    // private GestureDetector mGestureDetector;
    private String TAG = "KeyboardDialog";
    private KeyboardClickListener mKeyboardClickListener;
    /**
     * 拖动的时候对应ITEM的VIEW
     */
    private View dragImageView = null;
    /**
     * WindowManager管理器
     */
    private WindowManager windowManager = null;
    private Point touchPoint;


    private WindowManager.LayoutParams lp = null;

    private static SparseArray<String> mNumberArray = new SparseArray<>();
    private boolean isShow;
    private int statusBarHeight;

    static {
        mNumberArray.put(R.id.wbj_keyboard_0, "0".trim());
        mNumberArray.put(R.id.wbj_keyboard_1, "1".trim());
        mNumberArray.put(R.id.wbj_keyboard_2, "2".trim());
        mNumberArray.put(R.id.wbj_keyboard_3, "3".trim());
        mNumberArray.put(R.id.wbj_keyboard_4, "4".trim());
        mNumberArray.put(R.id.wbj_keyboard_5, "5".trim());
        mNumberArray.put(R.id.wbj_keyboard_6, "6".trim());
        mNumberArray.put(R.id.wbj_keyboard_7, "7".trim());
        mNumberArray.put(R.id.wbj_keyboard_8, "8".trim());
        mNumberArray.put(R.id.wbj_keyboard_9, "9".trim());
    }

    public KeyboardDialog(final Context context) {
        super(context);
        // mGestureDetector = new GestureDetector(this);
        touchPoint = new Point();
        statusBarHeight = getStatusBarHeight();
        ImageView dragView = new ImageView(context);
        dragView.setClickable(true);
        setBackground(null);
        View view = LayoutInflater.from(context).inflate(R.layout.keyboard_dialog, null);
        //setBackground(null);
        dragView.setBackgroundResource(R.drawable.cycle_bg);
        dragView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchPoint.set((int) event.getX(), (int) event.getY());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        lp.x = (int) (event.getRawX() - touchPoint.x);
                        lp.y = (int) (event.getRawY() - touchPoint.y - statusBarHeight);
                        windowManager.updateViewLayout(KeyboardDialog.this, lp);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        lp.x = (int) (event.getRawX() - touchPoint.x);
                        lp.y = (int) (event.getRawY() - touchPoint.y - statusBarHeight);
                        windowManager.updateViewLayout(KeyboardDialog.this, lp);
                        touchPoint.set(0, 0);
                        break;
                }
                return false;
            }
        });

        lp = new WindowManager.LayoutParams();
        lp.type = WindowManager.LayoutParams.TYPE_TOAST;
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //dialogWindow.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.alpha = 0.7f;
        lp.format = PixelFormat.RGBA_8888;
        lp.x = App.screenWidth - 200;
        lp.y = App.screenHeight - 200;
        lp.gravity = Gravity.LEFT | Gravity.TOP;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        addView(dragView);
        addView(view);
        LayoutParams pa = (LayoutParams) view.getLayoutParams();
        pa.setMargins(25, 25, 10, 10);
        initViews(view);
    }

    public void show() {
        isShow = true;
        windowManager.addView(this, lp);
    }

    public boolean isShowing() {
        return isShow;
    }

    public void dismiss() {
        isShow = false;
        windowManager.removeView(this);
    }

    private void initViews(View view) {
        for (int id = R.id.wbj_keyboard_1; id <= R.id.wbj_keyboard_right; ++id) {
            view.findViewById(id).setOnClickListener(this);
        }
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onClick(View v) {
        if (mKeyboardClickListener == null) {
            return;
        }

        switch (v.getId()) {
            case R.id.wbj_keyboard_0:
            case R.id.wbj_keyboard_1:
            case R.id.wbj_keyboard_2:
            case R.id.wbj_keyboard_3:
            case R.id.wbj_keyboard_4:
            case R.id.wbj_keyboard_5:
            case R.id.wbj_keyboard_6:
            case R.id.wbj_keyboard_7:
            case R.id.wbj_keyboard_8:
            case R.id.wbj_keyboard_9:
                mKeyboardClickListener.numberKey(mNumberArray.get(v.getId()));
                break;
            case R.id.wbj_keyboard_up:
                mKeyboardClickListener.upKey();
                break;
            case R.id.wbj_keyboard_down:
                mKeyboardClickListener.downKey();
                break;
            case R.id.wbj_keyboard_left:
                mKeyboardClickListener.leftKey();
                break;
            case R.id.wbj_keyboard_right:
                mKeyboardClickListener.rightKey();

                break;
            case R.id.wbj_keyboard_delete:
                mKeyboardClickListener.deleteKey();

                break;
            default:
                break;
        }
    }

    public void setKeyboardClickListener(KeyboardClickListener keyboardClickListener) {
        this.mKeyboardClickListener = keyboardClickListener;
    }

    public interface KeyboardClickListener {
        /**
         * 数字键
         *
         * @param keyValue 键值
         */
        void numberKey(String keyValue);

        /**
         * 上键
         */
        void upKey();

        /**
         * 下键
         */
        void downKey();

        /**
         * 左键
         */
        void leftKey();

        /**
         * 右键
         */
        void rightKey();

        /**
         * 删除键
         */
        void deleteKey();
    }

}
