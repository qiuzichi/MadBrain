package com.unipad.brain.portraits.view;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.unipad.AppContext;
import com.unipad.brain.AbsBaseGameService;
import com.unipad.brain.R;
import com.unipad.brain.portraits.bean.Person;
import com.unipad.brain.portraits.control.HeadService;
import com.unipad.common.BasicCommonFragment;
import com.unipad.common.Constant;
import com.unipad.common.ViewHolder;
import com.unipad.common.adapter.CommonAdapter;
import com.unipad.utils.LogUtil;

import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

;

/**
 * Created by gongkan on 2016/4/11.
 */
public class HeadPortraitFragment extends BasicCommonFragment {
    private HeadAdapter adapter;
    private GridView mListView;
    private HeadService service;
    private View mStubShade;
    private ArrayList<Person> mData;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LogUtil.e(" HeadPortraitFragment", "--..--onActivityCreated--");
        mListView = (GridView) mViewParent.findViewById(R.id.gridview);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        service = (HeadService) (AppContext.instance().getService(Constant.HEADSERVICE));
        mData = ((HeadService) service).data;
        adapter = new HeadAdapter(mActivity, mData, R.layout.list_portrait);
        mListView.setAdapter(adapter);
        mStubShade = mViewParent.findViewById(R.id.view_shade);
    }

    @Override
    public void initDataFinished() {
        super.initDataFinished();
        adapter.notifyDataSetChanged();
        mStubShade.setVisibility(View.VISIBLE);
    }

    @Override
    public void pauseGame() {

        mStubShade.setVisibility(View.VISIBLE);
    }

    @Override
    public void startMemory() {
        mStubShade.setVisibility(View.GONE);
    }

    @Override
    public void reStartGame() {
        super.reStartGame();
        if (service.state == AbsBaseGameService.GO_IN_MATCH_END_MEMORY || service.state == AbsBaseGameService.GO_IN_MATCH_DOWNLOADED) {
            mStubShade.setVisibility(View.VISIBLE);
        } else {
            mStubShade.setVisibility(View.GONE);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.portrait_fragment;
    }

    @Override
    public void memoryTimeToEnd(int memoryTime) {
        super.memoryTimeToEnd(memoryTime);
        showAnserView();
        sendMsgToPreper();
        mStubShade.setVisibility(View.VISIBLE);
    }

    private void showAnserView() {
        ((HeadService) service).shuffData();
        adapter.notifyDataSetChanged();

    }


    @Override
    public void rememoryTimeToEnd(final int answerTime) {
        super.rememoryTimeToEnd(answerTime);
        if (isMatchMode()) {

        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void startRememory() {
        super.startRememory();
        mStubShade.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        x.image().clearMemCache();
        x.image().clearCacheFiles();
        AppContext.instance().clearService(service);
    }

    private class HeadAdapter extends CommonAdapter<Person> {
        public HeadAdapter(Context context, List<Person> datas, int layoutId) {
            super(context, datas, layoutId);
            Log.e("", "size=" + mDatas.size());
        }

        /**
         * @param holder
         * @param person
         */
        @Override
        public void convert(final ViewHolder holder, final Person person) {
            ImageView headView = (ImageView) holder.getView(R.id.icon_head);

            x.image().bind(headView, person.getHeadPortraitPath());
            final EditText firstName = (EditText) holder.getView(R.id.first_name);
            final EditText lastName = (EditText) holder.getView(R.id.last_name);
            TextView holeName = (TextView) holder.getView(R.id.name_text);

            switch (service.state) {
                case AbsBaseGameService.GO_IN_MATCH_START_RE_MEMORY:
                case AbsBaseGameService.GO_IN_MATCH_END_MEMORY:
                    holeName.setVisibility(View.GONE);
                    firstName.setVisibility(View.VISIBLE);
                    lastName.setVisibility(View.VISIBLE);

                    Person bean = mData.get(holder.getPosition());
                    firstName.setText(bean.getAnswerFirstName());
                    lastName.setText(bean.getAnswerLastName());
                    firstName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                                lastName.requestFocus();
                                return true;
                            }
                            return false;
                        }
                    });

                    firstName.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String first_name = firstName.getText().toString().trim();
                            mData.get(holder.getPosition()).setAnswerFirstName(first_name);
                        }
                    });

                    lastName.addTextChangedListener(new TextWatcher() {
                        @Override

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String last_name = lastName.getText().toString().trim();
                            mData.get(holder.getPosition()).setAnswerLastName(last_name);
                        }
                    });
                    lastName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            int visiblePosition = mListView.getFirstVisiblePosition();
                            View Preview = mListView.getChildAt(holder.getPosition() + 1 - visiblePosition);

                            if (null != Preview) {
                            /*  下一个item 的firstName*/
                                EditText firstName = (EditText) Preview.findViewById(R.id.first_name);
                                LogUtil.e("", "first:" + firstName.getText().toString());
                                firstName.requestFocus();
                                if ((holder.getPosition() + 1) % mListView.getNumColumns() == 0) {
                                    mListView.smoothScrollBy(mListView.getVerticalSpacing() + mListView.getChildAt(0).getHeight(), 0);
                                }
                                return true;

                            } else if (mDatas.size() - 1 == mListView.getLastVisiblePosition()) {
                             /*到最后完成一个item  关闭软键盘*/
                                lastName.setImeOptions(EditorInfo.IME_ACTION_DONE);
                                closeSofeInputMothed(lastName);
                                return true;
                            }
                            return false;
                        }
                    });


                    break;
                case AbsBaseGameService.GO_IN_MATCH_DOWNLOADED :
                case AbsBaseGameService.GO_IN_MATCH_START_MEMORY :
                    firstName.setVisibility(View.GONE);
                    lastName.setVisibility(View.GONE);
                    holeName.setVisibility(View.VISIBLE);
                    holeName.setText(person.getFirstName() + "·" + person.getLastName());
                    break;
                case AbsBaseGameService.GO_IN_MATCH_END_RE_MEMORY :
                    lastName.setVisibility(View.GONE);
                    firstName.setVisibility(View.GONE);
                    holeName.setVisibility(View.VISIBLE);
                    TextView answerHoleName = (TextView) holder.getView(R.id.answer_name_text);
                    if (!person.isAnswerRight()) {
                        answerHoleName.setTextColor(mContext.getResources().getColor(R.color.red));
                    }
                    holeName.setText(person.getFirstName() + "·" + person.getLastName());
                    answerHoleName.setVisibility(View.VISIBLE);
                    answerHoleName.setText(person.getAnswerFirstName() + "·" + person.getAnswerLastName());
                    break;
                }
            }

        private void closeSofeInputMothed(View view) {
            InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
