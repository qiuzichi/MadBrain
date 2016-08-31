package com.unipad.brain.words.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.unipad.AppContext;
import com.unipad.brain.AbsBaseGameService;
import com.unipad.brain.R;
import com.unipad.brain.words.bean.WordEntity;
import com.unipad.brain.words.dao.WordsService;
import com.unipad.common.BasicCommonFragment;
import com.unipad.common.Constant;
import com.unipad.utils.LogUtil;

import java.util.List;

/**
 * liupeng
 * 随机词语
 */
public class WordRightFragment extends BasicCommonFragment {
    private WordsService service;
    private ViewStub mStubShade;
    private RecyclerView wordRv;
    private List<WordEntity> mData;
    private WordRvAdapter wordRvAdapter;
    private GridLayoutManager manager;

    @Override
    public void onClick(View view) {
    }

    @Override
    public int getLayoutId() {
        return R.layout.word_frg_right;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        service = (WordsService) AppContext.instance().getService(Constant.WORDS_SERVICE);
        mStubShade = (ViewStub) mViewParent.findViewById(R.id.view_shade);
        mStubShade.setVisibility(View.VISIBLE);
        wordRv = (RecyclerView) mViewParent.findViewById(R.id.word_rv);
    }

    @Override
    public void startMemory() {
        mStubShade.setVisibility(View.GONE);
    }

    @Override
    public void pauseGame() {
        super.pauseGame();
        mStubShade.setVisibility(View.VISIBLE);
    }

    @Override
    public void reStartGame() {
        super.reStartGame();
        mStubShade.setVisibility(View.GONE);
    }

    @Override
    public void initDataFinished() {
        super.initDataFinished();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        mData = service.entitys;
        //mStubShade.setVisibility(View.GONE);

        manager = new GridLayoutManager(getActivity(), 20);
        manager.setOrientation(GridLayoutManager.HORIZONTAL);
        wordRv.setLayoutManager(manager);
        wordRvAdapter = new WordRvAdapter();
        wordRv.setAdapter(wordRvAdapter);
        wordRv.setHasFixedSize(true);
        mStubShade.setVisibility(View.VISIBLE);
    }

    @Override
    public void memoryTimeToEnd(int memoryTime) {
        super.memoryTimeToEnd(memoryTime);
        wordRvAdapter.notifyDataSetChanged();
        sendMsgToPreper();
    }

    @Override
    public void rememoryTimeToEnd(final int answerTime) {
        super.rememoryTimeToEnd(answerTime);
        if (isMatchMode()){

        }else {
            wordRvAdapter.notifyDataSetChanged();
        }
    }

    class WordRvAdapter extends RecyclerView.Adapter<WordRvAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(
                    getActivity()).inflate(R.layout.list_item_random_words, parent,
                    false),new MyCustomEditTextListener());
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.textNum.setText(mData.get(position).getNumber() + "");
            switch (service.state) {
                case AbsBaseGameService.GO_IN_MATCH_DOWNLOADED:
                case AbsBaseGameService.GO_IN_MATCH_START_MEMORY:
                    holder.userAnswerEdit.setVisibility(View.GONE);
                    holder.textWord.setText(mData.get(position).getWord());
                    break;
                case AbsBaseGameService.GO_IN_MATCH_END_MEMORY:
                case AbsBaseGameService.GO_IN_MATCH_START_RE_MEMORY:
                    holder.textWord.setVisibility(View.GONE);
                    holder.userAnswerEdit.setVisibility(View.VISIBLE);
                    holder.myCustomEditTextListener.updatePosition(position);
                    holder.userAnswerEdit.setText(mData.get(position).getAnswer());
                    holder.userAnswerEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            System.out.println(holder.getLayoutPosition() + "---" + holder.getAdapterPosition()
                                    + "----" + position + "--first==" + manager.findFirstVisibleItemPosition());
                            wordRv.smoothScrollToPosition(position + 20 < wordRvAdapter.getItemCount() ? position + 20 : wordRvAdapter.getItemCount() - 1);
                            if (position < wordRvAdapter.getItemCount() - 1) {
                                System.out.println("" + position + "--" + (wordRvAdapter.getItemCount() - 1));
                                wordRv.getChildAt(position + 1 - manager.findFirstVisibleItemPosition()).requestFocus();
                            }
                            progress = 100 + (position + 1 ) * 100 / wordRvAdapter.getItemCount();
                            if (progress <= 101){
                                progress = 101;
                            }else if (progress >= 199){
                                progress = 199;
                            }
                            if(position==wordRvAdapter.getItemCount()-1){
                                holder.userAnswerEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);
                                closeSofeInputMothed(holder.userAnswerEdit);
                            }
                            LogUtil.e("", "word:" + progress);
                            return true;
                        }
                    });
                    break;
                case AbsBaseGameService.GO_IN_MATCH_END_RE_MEMORY:
                    holder.userAnswerEdit.setVisibility(View.GONE);
                    holder.textWord.setVisibility(View.VISIBLE);
                    if (TextUtils.isEmpty(mData.get(position).getAnswer()) || !mData.get(position).getAnswer().equals(mData.get(position).getWord())) {
                        holder.textWord.setTextColor(getResources().getColor(R.color.red));
                    }
                    holder.textWord.setText(mData.get(position).getWord() + "/" + mData.get(position).getAnswer());
                    break;
                default:
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView textNum, textWord;
            EditText userAnswerEdit;
            MyCustomEditTextListener myCustomEditTextListener;

            public MyViewHolder(View itemView,MyCustomEditTextListener myCustomEditTextListener) {
                super(itemView);
                textNum = (TextView) itemView.findViewById(R.id.words_num);
                textWord = (TextView) itemView.findViewById(R.id.words);
                userAnswerEdit = (EditText) itemView.findViewById(R.id.words_answer);
                this.myCustomEditTextListener = myCustomEditTextListener;
                userAnswerEdit.addTextChangedListener(myCustomEditTextListener);
            }
        }

        private class MyCustomEditTextListener implements TextWatcher {
            private int position;

            public void updatePosition(int position) {
                this.position = position;
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                mData.get(position).setAnswer(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        }
    }

    private void closeSofeInputMothed(View view){
        InputMethodManager imm =(InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
