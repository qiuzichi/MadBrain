package com.unipad.brain.absPic.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.unipad.AppContext;
import com.unipad.brain.AbsBaseGameService;
import com.unipad.brain.R;
import com.unipad.brain.absPic.bean.Figure;
import com.unipad.brain.absPic.dao.FigureService;
import com.unipad.common.BasicCommonFragment;
import com.unipad.common.Constant;
import com.unipad.common.ViewHolder;
import com.unipad.common.adapter.CommonAdapter;
import com.unipad.common.widget.HIDDialog;
import com.unipad.utils.ToastUtil;

import org.xutils.x;

import java.util.List;

/**
 * Created by gongkan on 2016/4/15.
 */
public class AbsFigureFragment extends BasicCommonFragment {
    private GridView gridView;
    private FigureService service;
    private FigureAdapter adapter;

    private int current;
    private int preAnswer;
    private View buttonArea;
    private View mStubShade;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gridView = (GridView) mViewParent.findViewById(R.id.abs_figure_gridview);
        buttonArea = mViewParent.findViewById(R.id.button_area);
        mViewParent.findViewById(R.id.answer_1).setOnClickListener(this);
        mViewParent.findViewById(R.id.answer_2).setOnClickListener(this);
        mViewParent.findViewById(R.id.answer_3).setOnClickListener(this);
        mViewParent.findViewById(R.id.answer_4).setOnClickListener(this);
        mViewParent.findViewById(R.id.answer_5).setOnClickListener(this);
        mStubShade = mViewParent.findViewById(R.id.view_shade);
        service = (FigureService) (AppContext.instance().getService(Constant.ABS_FIGURE));
        adapter = new FigureAdapter(getActivity(), service.allFigures, R.layout.list_item_abs_figure);
        gridView.setAdapter(adapter);
        current = gridView.getFirstVisiblePosition();
        setButtonArea();
    }

    private void setButtonArea() {
        if (service.state == AbsBaseGameService.GO_IN_MATCH_END_MEMORY || service.state == AbsBaseGameService.GO_IN_MATCH_START_RE_MEMORY) {
            buttonArea.setVisibility(View.VISIBLE);
        } else {
            buttonArea.setVisibility(View.GONE);
        }
    }

    /**
     * 结束记忆后由管控端统一开始
     */
    @Override
    public void memoryTimeToEnd(int memory) {
        super.memoryTimeToEnd(memory);
        current = 0;
        service.shuffle();
        setButtonArea();
        //ToastUtil.createTipDialog(mActivity,Constant.SHOW_GAME_PAUSE,"等待裁判开始").show();
        //mStubShade.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
        sendMsgToPreper();

    }

    @Override
    public void rememoryTimeToEnd(final int answerTime) {
        super.rememoryTimeToEnd(answerTime);
        if (isMatchMode()) {

        } else {
            setButtonArea();
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        x.image().clearMemCache();
        x.image().clearCacheFiles();
        AppContext.instance().clearService(service);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_abs_figure;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.answer_1:
                setText(1);
                break;
            case R.id.answer_2:
                setText(2);
                break;
            case R.id.answer_3:
                setText(3);
                break;
            case R.id.answer_4:
                setText(4);
                break;
            case R.id.answer_5:
                setText(5);
                break;

        }
    }

    private void setText(int i) {
        int visiblePosition = gridView.getFirstVisiblePosition();
        View pre = gridView.getChildAt(current - visiblePosition);
        if (null != pre) {
            TextView tv = (TextView) pre.findViewById(R.id.answer_num);
            tv.setBackgroundColor(getResources().getColor(R.color.white));
            tv.setText(String.valueOf(i));
            adapter.getItem(current).setAnswerId(i);
        }
        preAnswer = current;
        progress = 100 + current * 100 / service.allFigures.size();
        if (progress < 101) {
            progress = 101;
        } else if (progress > 199) {
            progress = 199;
        }
        if (current == service.allFigures.size() - 1) {//已经是最后一个，就不需要再往下设置背景了
            return;
        }
        current++;
        View curr = gridView.getChildAt(current - visiblePosition);
        if (curr != null) {
            TextView currTv = (TextView) curr.findViewById(R.id.answer_num);
            currTv.setBackgroundColor(getResources().getColor(R.color.blue));
        } else {
            gridView.smoothScrollBy(gridView.getVerticalSpacing() + 100, 0);
        }
        if (current % 5 == 0)
            gridView.smoothScrollBy(gridView.getVerticalSpacing() + 100, 0);
        // gridView.smoothScrollToPosition(current+5,);
    }

    @Override
    public void initDataFinished() {
        adapter.notifyDataSetChanged();
        mStubShade.setVisibility(View.VISIBLE);
    }

    @Override
    public void startMemory() {
        super.startMemory();
        mStubShade.setVisibility(View.GONE);
        //  HIDDialog.dismissAll();
    }

    @Override
    public void startRememory() {
        super.startRememory();
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

    private class FigureAdapter extends CommonAdapter<Figure> {
        public FigureAdapter(Context context, List<Figure> datas, int layoutId) {
            super(context, datas, layoutId);
        }

        /**
         * @param holder
         * @param figure
         */
        @Override
        public void convert(final ViewHolder holder, final Figure figure) {
            ImageView headView = (ImageView) holder.getView(R.id.icon_absfigure);
            x.image().bind(headView, figure.getPath());
            //Log.e("---", "path = " + person.getHeadPortraitPath() + ",name=" + person.getFirstName() + person.getLastName());
            final TextView orginNum = (TextView) holder.getView(R.id.orgin_num);
            final TextView answerNum = (TextView) holder.getView(R.id.answer_num);
            switch (service.state) {
                case AbsBaseGameService.GO_IN_MATCH_DOWNLOADED:
                case AbsBaseGameService.GO_IN_MATCH_START_MEMORY:
                    orginNum.setVisibility(View.GONE);
                    answerNum.setVisibility(View.GONE);
                    break;
                case AbsBaseGameService.GO_IN_MATCH_END_MEMORY:
                case AbsBaseGameService.GO_IN_MATCH_START_RE_MEMORY:
                    holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            int visiblePosition = gridView.getFirstVisiblePosition();
                            View Preview = gridView.getChildAt(current - visiblePosition);
                            if (null != Preview) {
                                TextView tv = (TextView) Preview.findViewById(R.id.answer_num);
                                tv.setBackgroundColor(getResources().getColor(R.color.white));
                            }
                            v.findViewById(R.id.answer_num).setBackgroundColor(getResources().getColor(R.color.blue));
                            preAnswer = current;
                            current = holder.getPosition();

                        }
                    });
                    orginNum.setVisibility(View.GONE);
                    answerNum.setVisibility(View.VISIBLE);
                    if (holder.getPosition() == current) {
                        answerNum.setBackgroundColor(getResources().getColor(R.color.blue));
                    } else {
                        answerNum.setBackgroundColor(getResources().getColor(R.color.white));
                    }
                    break;
                case AbsBaseGameService.GO_IN_MATCH_END_RE_MEMORY:
                    orginNum.setVisibility(View.VISIBLE);
                    answerNum.setVisibility(View.VISIBLE);
                    orginNum.setText("" + figure.getRawId());
                    holder.getConvertView().setOnClickListener(null);
                    answerNum.setBackgroundColor(getResources().getColor(R.color.white));
                    if (figure.getRawId() == figure.getAnswerId()) {
                        answerNum.setTextColor(getResources().getColor(R.color.black));
                    } else {
                        answerNum.setTextColor(getResources().getColor(R.color.red));
                    }
                    break;
            }
        }
    }
}
