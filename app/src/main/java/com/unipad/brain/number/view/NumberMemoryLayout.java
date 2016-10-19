package com.unipad.brain.number.view;

import android.content.Context;
import android.support.v4.widget.Space;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unipad.brain.R;
import com.unipad.utils.LogUtil;
import com.unipad.utils.StringUtil;

/**
 * 数据记忆界面
 */
public class NumberMemoryLayout extends LinearLayout {
    private Context mContext;
    private LayoutInflater mInflater;
    /**
     * 比赛项目：二进制数字、快速随机数字、马拉松数字
     */
    private SparseArray<String> lineNumbers;
    /**
     * 已经加载了多少行
     */
    private int mLoadedItem = 0;

    private int mRows;

    private int num_line;
    public NumberMemoryLayout(Context context) {
        super(context);
    }

    public NumberMemoryLayout(Context context, SparseArray<String> lineNumbers,int num_line) {
        super(context);
        this.lineNumbers = lineNumbers;
        if (lineNumbers != null){
            mRows = lineNumbers.valueAt(0).length();
        }
        this.num_line = num_line;
        this.initGridView(context);
    }

    private void initGridView(Context context) {
        mContext = context;
        this.initData();
    }

    private void initData() {
        setOrientation(VERTICAL);
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initChidView();

    }
    private void initChidView(){
        if (null != lineNumbers && lineNumbers.size()!=0){
            for (int i =0;i<lineNumbers.size();i++) {
                addLineLayout(lineNumbers.keyAt(i),lineNumbers.valueAt(i));
            }
        }
    }
    /**
     * 每次添加一行数据的视图
     *
     * @param index 数据所在父视图的索引
     */
    private void addLineLayout(int index,String value) {
        ViewGroup parent = (ViewGroup) mInflater.inflate(
                R.layout.number_v_line, null);
        LinearLayout layoutNums = (LinearLayout) parent.getChildAt(0);
        this.addNumText(layoutNums, index);

        TextView textNum = (TextView) parent.findViewById(R.id.tv_row);
        textNum.setText("Row" + StringUtil.addZero(index, 2));

        addView(parent, index-1);
    }

    /**
     * 为第一行显示"0"，"1"的TextView布局
     *
     * @param parent TextView所在的父视图
     * @param index  TextView所在的父视图的索引
     */
    private void addNumText(LinearLayout parent, int index) {
        String numList = lineNumbers.get(index);
        LayoutParams params;
        params = new LayoutParams(0,
                LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        params.gravity = Gravity.CENTER;
        int length = numList.length();
        for (int i = 0; i < mRows; i++) {
            if (i >= length) {
                Space pace = new Space(mContext);
                pace.setLayoutParams(params);
                parent.addView(pace, i);
            } else {
                TextView textNum = new TextView(mContext);
                textNum.setLayoutParams(params);
                textNum.setText(String.valueOf(numList.charAt(i)));
                textNum.setTextSize(26.0f);
                if (num_line != 0 && (i+1)%num_line == 0){
                    textNum.setBackgroundResource(R.drawable.line_ver);
                }
                textNum.setTextColor(mContext.getResources().getColor(
                        R.color.main_lf_bg));
                textNum.setGravity(Gravity.CENTER);
                parent.addView(textNum, i);
            }
        }
    }
    public void updateView(int pre, int next) {
        for (int i = 0; i < lineNumbers.size(); i++) {
            ViewGroup viewGroup = (ViewGroup) getChildAt(i);
            viewGroup = (ViewGroup) viewGroup.getChildAt(0);
            int count = viewGroup.getChildCount();
            for (int j = 1; j <= count; j++) {
                if (pre !=0 && j%pre==0){
                    viewGroup.getChildAt(j-1).setBackground(null);
                }
                if (next !=0 && j%next == 0){
                    viewGroup.getChildAt(j-1).setBackgroundResource(R.drawable.line_ver);
                }
            }
        }
    }
}
