package com.unipad.brain.consult.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.unipad.brain.R;
import com.unipad.brain.consult.entity.ConsultTab;
import com.unipad.common.ViewHolder;
import com.unipad.common.adapter.CommonAdapter;
import com.unipad.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

;

/**
 * Created by hasee on 2016/10/9.
 */

public class CustomSearchView extends LinearLayout implements View.OnClickListener {

    /**
     * 输入框
     */
    private EditText etInput;
   /**
     * 选择款
     */
    private TextView txtSelect;

    /**
     * 删除键
     */
    private ImageView ivDelete;

    /**
     * 返回按钮
     */
    private Button btnSearch;

    /**
     * 上下文对象
     */
    private Context mContext;

    /**
     * 弹出列表
     */
    private ListView lvTips;

    /**
     * 提示adapter （推荐adapter）
     */
    private ArrayAdapter<String> mHintAdapter;

    /**
     * 自动补全adapter 只显示名字
     */
    private ArrayAdapter<String> mAutoCompleteAdapter;

    /**
     * 搜索回调接口
     */
    private SearchViewListener mListener;
    private PopupWindow popupWindow;
    private ItemAdapter adapter;
    private int mCurrentPosition ;

    /**
     * 设置搜索回调接口
     *
     * @param listener 监听者
     */
    public void setSearchViewListener(SearchViewListener listener) {
        mListener = listener;
    }




    public CustomSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mCurrentPosition = 0;
//        view = View.inflate(context, R.layout.custom_search_view, this);
//        addView(view);
        LayoutInflater.from(context).inflate(R.layout.custom_search_view, this);
        initViews();
    }

    private void initViews() {
        etInput = (EditText) findViewById(R.id.search_et_input);
        txtSelect = (TextView) findViewById(R.id.txt_select_item);
        txtSelect.setOnClickListener(this);
        ivDelete = (ImageView) findViewById(R.id.search_iv_delete);
        lvTips = (ListView) findViewById(R.id.search_lv_tips);
        btnSearch = (Button) findViewById(R.id.search_btn_start);

        lvTips.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //set edit text
                String text = lvTips.getAdapter().getItem(i).toString();
                etInput.setText(text);
                etInput.setSelection(text.length());
                etInput.clearFocus();
                //hint list view gone and result list view show
//                lvTips.setVisibility(View.GONE);
                ivDelete.setVisibility(View.VISIBLE);
                notifyStartSearching(text);
            }
        });

        ivDelete.setOnClickListener(this);
        btnSearch.setOnClickListener(this);

        etInput.addTextChangedListener(new EditChangedListener());
        etInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    lvTips.setVisibility(GONE);
                    notifyStartSearching(etInput.getText().toString());
                }
                return true;
            }
        });
    }
    /**
     * 通知监听者 进行搜索操作
     * @param text
     */
    private void notifyStartSearching(String text){
        if (mListener != null) {
            mListener.onSearch(etInput.getText().toString());
        }
        //隐藏软键盘
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etInput.getWindowToken(), 0);
    }

    /**
     * 设置热搜版提示 adapter
     */
    public void setTipsHintAdapter(ArrayAdapter<String> adapter) {
        this.mHintAdapter = adapter;
        if (lvTips.getAdapter() == null) {
            lvTips.setAdapter(mHintAdapter);
        }
    }

    /**
     * 设置自动补全adapter
     */
    public void setAutoCompleteAdapter(ArrayAdapter<String> adapter) {
        this.mAutoCompleteAdapter = adapter;
    }

    private class EditChangedListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (!"".equals(charSequence.toString())) {
                ivDelete.setVisibility(VISIBLE);
                lvTips.setVisibility(VISIBLE);
                if (mAutoCompleteAdapter != null && lvTips.getAdapter() != mAutoCompleteAdapter) {
                    lvTips.setAdapter(mAutoCompleteAdapter);
                }
                //更新autoComplete数据
                if (mListener != null) {
                    mListener.onRefreshAutoComplete(charSequence + "");
                }
            } else {
                ivDelete.setVisibility(GONE);
//                if (mHintAdapter != null) {
//                    lvTips.setAdapter(mHintAdapter);
//                }
                //显示搜索历史记录；
                if (mListener != null) {
                    mListener.onShowHistoryDatas();
                }
                lvTips.setVisibility(GONE);
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.search_iv_delete:
                etInput.setText("");
                ivDelete.setVisibility(GONE);
                lvTips.setVisibility(GONE);
                setFouceEditText(null);
                //显示历史搜索；
                if (mListener != null) {
                    mListener.onShowHistoryDatas();
                }
                break;
            case R.id.search_btn_start:
//                lvTips.setVisibility(GONE);
                String content = etInput.getText().toString().trim();
                notifyStartSearching(content);
                break;

            case R.id.txt_select_item:
                ConsultTab[] mConsultTabs = ConsultTab.values();
                List<String> item = new ArrayList<String>();
                if(mConsultTabs != null) {
                    for (int i = 0; i < mConsultTabs.length; i++) {
                        ConsultTab enumTab = mConsultTabs[i];
                        if (enumTab == null) {
                            continue;
                        }
                        item.add(enumTab.getType());
                    }
                }
                if(adapter == null) {
                    adapter = new ItemAdapter(mContext, item, R.layout.list_item_newsitem);
                } else {
                    adapter.notifyDataSetChanged();
                }
                showSelectDDialog(adapter);
                break;

        }
    }

    /**
     * 弹出选择部门
     */
    private void showSelectDDialog(ItemAdapter adapter) {
        ListView lv = new ListView(mContext);
        lv.setBackgroundResource(R.drawable.shapee_popup_window_bg);
        // 隐藏滚动条
        lv.setVerticalScrollBarEnabled(false);

        lv.setCacheColorHint(mContext.getResources().getColor(R.color.search_bg_color));
        // 让listView没有分割线
//        lv.setDividerHeight(0);
//        lv.setDivider(null);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == mCurrentPosition){
                    closePopup();
                    return;
                }
                mCurrentPosition = position;
                TextView selectItemView = (TextView) parent.getChildAt(mCurrentPosition);
                txtSelect.setText(selectItemView.getText().toString().trim());
                if(mListener != null) {
                    mListener.onChangeCurrent(mCurrentPosition);
                }
                closePopup();
            }
        });
        lv.setAdapter(adapter);
        popupWindow = new PopupWindow(lv, DensityUtil.px2dip(mContext,200), DensityUtil.px2dip(mContext,240));
        // 设置点击外部可以被关闭
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置popupWindow可以得到焦点
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(txtSelect, DensityUtil.px2dip(mContext, -20), DensityUtil.px2dip(mContext, 20));	 // 显示
    }

    //关闭弹出窗体
    private void closePopup(){
        if(popupWindow != null && popupWindow.isShowing()){
            popupWindow.dismiss();
        }
    }



    private class ItemAdapter extends CommonAdapter<String> {

        public ItemAdapter(Context context, List<String> datas, int layoutId) {
            super(context, datas, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, String s) {
            TextView itemView = (TextView)holder.getView(R.id.txt_history_item);
            itemView.setText(s);
            if(holder.getPosition() == mCurrentPosition) {
                itemView.setTextColor(mContext.getResources().getColor(R.color.light_blue2));
            } else {
                itemView.setTextColor(mContext.getResources().getColor(R.color.memory_order));
            }
        }
    }

    public void setFouceEditText(String defaultContent){
        if(!TextUtils.isEmpty(defaultContent)){
            etInput.setText(defaultContent);
            etInput.setSelection(defaultContent.length());
        }
        etInput.requestFocus();
    }


    public  void setTipListGone(){
        lvTips.setVisibility(View.GONE);
    }

    public void setEditInputContent(String history){
        etInput.setText(history);
        etInput.setSelection(history.length());
        etInput.clearFocus();
//        etInput.setCursorVisible(false);
        //hint list view gone and result list view show
//        lvTips.setVisibility(View.GONE);
        ivDelete.setVisibility(View.VISIBLE);
        notifyStartSearching(history);
    }
    /**
     * search view回调方法
     */
    public interface SearchViewListener {

        /**
         * 更新自动补全内容
         *
         * @param text 传入补全后的文本
         */
        void onRefreshAutoComplete(String text);

        /**
         * 开始搜索
         *
         * @param text 传入输入框的文本
         */
        void onSearch(String text);

        //        /**
        //         * 提示列表项点击时回调方法 (提示/自动补全)
        //         */
        //        void onTipsItemClick(String text);

        void onShowHistoryDatas();
        /*

        修改搜索的页面标签
         */
        void onChangeCurrent(int currentPosition);
    }

}