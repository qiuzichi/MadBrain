package com.unipad.brain.virtual;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unipad.AppContext;
import com.unipad.brain.AbsBaseGameService;
import com.unipad.brain.R;
import com.unipad.brain.virtual.bean.DividerGridItemDecoration;
import com.unipad.brain.virtual.bean.VirtualEntity;
import com.unipad.brain.virtual.dao.VirtualTimeService;
import com.unipad.common.BasicCommonFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * yzj----项目:虚拟事件
 */
public class VirtualRightFragment extends BasicCommonFragment {

    private static final String TAG = VirtualRightFragment.class.getSimpleName();
    /**
     * 最大年份
     */
    private int maxYear = 2099;
    /**
     * 最小年份
     */
    private int minYear = 1000;
    /**
     * 存储回忆界面的数据
     */
    private VirtualTimeService service;

    private VirtualMemoryAdapter memoryAdapter;
    /**
     * 记忆界面
     */
    private RecyclerView memoryRv;
    /**
     * 回忆界面底部按钮
     */
    private ImageButton numButton_0, numButton_1, numButton_2, numButton_3, numButton_4, numButton_5,
            numButton_6, numButton_7, numButton_8, numButton_9, numButton_clear;
    /**
     * 回忆界面下方的数字布局
     */
    private View view;
    private LinearLayout jianpan_linlayout;
    private RecyclerView.LayoutManager mLayoutmanager;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        jianpan_linlayout = (LinearLayout) mViewParent.findViewById(R.id.jianpan_linlayout);
        jianpan_linlayout.setVisibility(View.GONE);
        view = mViewParent.findViewById(R.id.viewstub);
        numButton_1 = (ImageButton) mViewParent.findViewById(R.id.numButton_1);
        numButton_2 = (ImageButton) mViewParent.findViewById(R.id.numButton_2);
        numButton_3 = (ImageButton) mViewParent.findViewById(R.id.numButton_3);
        numButton_4 = (ImageButton) mViewParent.findViewById(R.id.numButton_4);
        numButton_5 = (ImageButton) mViewParent.findViewById(R.id.numButton_5);
        numButton_6 = (ImageButton) mViewParent.findViewById(R.id.numButton_6);
        numButton_7 = (ImageButton) mViewParent.findViewById(R.id.numButton_7);
        numButton_8 = (ImageButton) mViewParent.findViewById(R.id.numButton_8);
        numButton_9 = (ImageButton) mViewParent.findViewById(R.id.numButton_9);
        numButton_0 = (ImageButton) mViewParent.findViewById(R.id.numButton_0);
        numButton_clear = (ImageButton) mViewParent.findViewById(R.id.numButton_clear);

        numButton_1.setOnClickListener(this);
        numButton_2.setOnClickListener(this);
        numButton_3.setOnClickListener(this);
        numButton_4.setOnClickListener(this);
        numButton_5.setOnClickListener(this);
        numButton_6.setOnClickListener(this);
        numButton_7.setOnClickListener(this);
        numButton_8.setOnClickListener(this);
        numButton_9.setOnClickListener(this);
        numButton_0.setOnClickListener(this);
        numButton_clear.setOnClickListener(this);

        memoryRv = (RecyclerView) mViewParent.findViewById(R.id.memoryRv);
        memoryRv.setLayoutManager(mLayoutmanager = new GridLayoutManager(mActivity, 2));

        //添加分割线
        memoryRv.addItemDecoration(new DividerGridItemDecoration(mActivity));
        service = (VirtualTimeService) AppContext.instance().getGameServiceByProject(mActivity.getProjectId());
        memoryRv.setAdapter(memoryAdapter = new VirtualMemoryAdapter(service.virtualList));
    }

    @Override
    public void initDataFinished() {
        super.initDataFinished();
        view.setVisibility(View.VISIBLE);
        memoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void startMemory() {
        super.startMemory();
        view.setVisibility(View.GONE);
    }

    @Override
    public void pauseGame() {
        super.pauseGame();
        view.setVisibility(View.VISIBLE);

    }

    @Override
    public void reStartGame() {
        super.reStartGame();
        view.setVisibility(View.GONE);

    }

    /**
     * 记忆的adapter
     */
    class VirtualMemoryAdapter extends RecyclerView.Adapter<VirtualMemoryAdapter.MyViewHolder> {
        public List<VirtualEntity> virtualList;

        private int itemPosition;

        public VirtualMemoryAdapter(List<VirtualEntity> virtualList) {
            if (virtualList == null) {
                this.virtualList = new ArrayList<>();

            } else {

                this.virtualList = virtualList;
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //将创建的View注册点击事件
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.virtual_memory_line, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            itemPosition = position;
            final VirtualEntity entity = virtualList.get(position);
            holder.tv_num.setText(entity.getNumber() + "");
            holder.tv_event.setText(entity.getEvent() + "");
            switch (service.state) {
                case AbsBaseGameService.GO_IN_MATCH_DOWNLOADED:
                case AbsBaseGameService.GO_IN_MATCH_START_MEMORY:
                    //记忆模式
                    jianpan_linlayout.setVisibility(View.GONE);
                    holder.tv_date.setText(entity.getDate() + "");
                    holder.editNUmView.setVisibility(View.GONE);
                    break;
                case AbsBaseGameService.GO_IN_MATCH_END_MEMORY:
                case AbsBaseGameService.GO_IN_MATCH_START_RE_MEMORY:

                    //回忆模式
                    if (position == index) {
                        holder.editNUmView.requestFocus();
                    }
                    jianpan_linlayout.setVisibility(View.VISIBLE);
                    entity.itemId = holder.editNUmView.getId();
                    entity.shuffNum = position + 1;
                    holder.tv_date.setVisibility(View.GONE);
                    holder.editNUmView.setVisibility(View.VISIBLE);
                    holder.editNUmView.setText(entity.getAnswerDate());
                    holder.editNUmView.setInputType(InputType.TYPE_NULL);
                    holder.tv_num.setText(entity.shuffNum + "");
                    holder.editNUmView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            index = position;
                            holder.editNUmView.requestFocus();
                            entity.setAnswerDate("");
                            holder.editNUmView.setText("");
                            return false;
                        }
                    });
                    break;
                case AbsBaseGameService.GO_IN_MATCH_END_RE_MEMORY:
                    //回忆结束
                    jianpan_linlayout.setVisibility(View.GONE);
                    holder.editNUmView.setVisibility(View.GONE);
                    holder.tv_date.setVisibility(View.VISIBLE);
                    if (entity.getDate().equals(entity.getAnswerDate() + "")) {
                        holder.tv_date.setText(entity.getDate() + "\n" + entity.getAnswerDate());

                    } else {

                        holder.tv_date.setText(entity.getDate() + "\n" + entity.getAnswerDate());
                        holder.tv_date.setTextColor(getResources().getColor(R.color.red));

                    }
                    break;

        }
    }

    public int getItem() {

        return itemPosition;
    }

    @Override
    public int getItemCount() {
        return virtualList.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_num, tv_date, tv_event;
        public EditText editNUmView;

        public MyViewHolder(final View view) {
            super(view);
            tv_num = (TextView) view.findViewById(R.id.id_num);
            tv_date = (TextView) view.findViewById(R.id.id_datetxt);
            tv_event = (TextView) view.findViewById(R.id.event_text);
            editNUmView = (EditText) view.findViewById(R.id.id_text_num);

        }

    }

}

    /**
     * 开始答题
     */
    public void inAnswerMode() {
        service.shuffData();
        memoryAdapter.notifyDataSetChanged();

    }

    /**
     * 结束答题
     */
    public void endAnswerMode() {
        if (isMatchMode()) {
            memoryAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        if (null != service) {
            service.clear();
            service = null;
        }
        super.onDestroyView();
    }

/**
 * 输入的索引
 */
int index = 0;

    /**
     * 输入的年份
     */
    @Override
    public void onClick(View v) {
        String text = service.virtualList.get(index).getAnswerDate();
        VirtualMemoryAdapter.MyViewHolder holder = (VirtualMemoryAdapter.MyViewHolder) (memoryRv.findViewHolderForAdapterPosition(index));
        switch (v.getId()) {
            case R.id.numButton_1:
            case R.id.numButton_2:
            case R.id.numButton_3:
            case R.id.numButton_4:
            case R.id.numButton_5:
            case R.id.numButton_6:
            case R.id.numButton_7:
            case R.id.numButton_9:
            case R.id.numButton_8:
            case R.id.numButton_0:
                if (v.getId() == R.id.numButton_1) {
                    if (text.length() < 4) {
                        text = text + "1";
                    }
                } else if (v.getId() == R.id.numButton_2) {
                    if (text.length() < 4) {
                        text = text + "2";
                    }
                } else if (v.getId() == R.id.numButton_3) {
                    if (text.length() < 4) {
                        text = text + "3";
                    }
                } else if (v.getId() == R.id.numButton_4) {
                    if (text.length() < 4) {
                        text = text + "4";
                    }
                } else if (v.getId() == R.id.numButton_5) {
                    if (text.length() < 4) {
                        text = text + "5";
                    }
                } else if (v.getId() == R.id.numButton_6) {
                    if (text.length() < 4) {
                        text = text + "6";
                    }
                } else if (v.getId() == R.id.numButton_7) {
                    if (text.length() < 4) {
                        text = text + "7";
                    }
                } else if (v.getId() == R.id.numButton_8) {
                    if (text.length() < 4) {
                        text = text + "8";
                    }
                } else if (v.getId() == R.id.numButton_9) {
                    if (text.length() < 4) {
                        text = text + "9";
                    }
                } else if (v.getId() == R.id.numButton_0) {
                    if (text.length() < 4) {
                        text = text + "0";
                    }
                }
                if (holder != null) {
                    service.virtualList.get(index).setAnswerDate(text);

                    holder.editNUmView.setText(text);
                } else {
                    service.virtualList.get(index).setAnswerDate(text.substring(0, text.length() - 1));
                }
                if (index == service.virtualList.size() - 1) {
                    break;
                }
                progress = 100 + index * 100 / service.virtualList.size();
                if (progress < 101) {
                    progress = 101;
                } else if (progress > 199) {
                    progress = 199;
                }
                if (text.length() == 4) {
                    index++;
                    VirtualMemoryAdapter.MyViewHolder nextHolder = (VirtualMemoryAdapter.MyViewHolder) (memoryRv.findViewHolderForAdapterPosition(index));
                    if (null == nextHolder) {
                        int lastVisibleItem = ((GridLayoutManager) mLayoutmanager).findLastVisibleItemPosition() - 2;
                        moveToPosition(lastVisibleItem, (GridLayoutManager) mLayoutmanager);
                    } else {
                        nextHolder.editNUmView.requestFocus();
                    }
                }

                break;
            case R.id.numButton_clear:
                //在第一个格子时index不做处理
                if (index == 0 && TextUtils.isEmpty(text)) {

                } else {
                    if (!TextUtils.isEmpty(text)) {
                        if (holder != null) {
                            holder.editNUmView.requestFocus();
                            text = text.substring(0, text.length() - 1);
                            service.virtualList.get(index).setAnswerDate(text);
                            holder.editNUmView.setText(text);
                        }
                    }
                    //格子里的数为空且不在第一个position时 index--
                }
                if (holder != null && holder.editNUmView.getText().length() == 0 && index != 0) {
                    VirtualMemoryAdapter.MyViewHolder nextHolder = (VirtualMemoryAdapter.MyViewHolder) (memoryRv.findViewHolderForAdapterPosition(index));
                    if (null == nextHolder) {

                        int lastVisibleItem = ((GridLayoutManager) mLayoutmanager).findLastVisibleItemPosition() - 2;
                        moveToPosition(lastVisibleItem, (GridLayoutManager) mLayoutmanager);
                    } else {
                        nextHolder.editNUmView.requestFocus();
                    }
                    index--;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.virtual_frg_right;
    }

    private void moveToPosition(int n, GridLayoutManager mLinearLayoutManager) {
        //先从RecyclerView的LayoutManager中获取第一项和最后一项的Position
        int firstItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        int lastItem = mLinearLayoutManager.findLastVisibleItemPosition();
        //然后区分情况
        if (n <= firstItem) {
            //当要置顶的项在当前显示的第一个项的前面时
            memoryRv.scrollToPosition(n);
        } else if (n <= lastItem) {
            //当要置顶的项已经在屏幕上显示时
            int top = memoryRv.getChildAt(n - firstItem).getTop();
            memoryRv.scrollBy(0, top);
        } else {
            //当要置顶的项在当前显示的最后一项的后面时
            memoryRv.scrollToPosition(n);
            //这里这个变量是用在RecyclerView滚动监听里面的
            // move = true;
        }

    }

    @Override
    public void memoryTimeToEnd(int memoryTime) {
        super.memoryTimeToEnd(memoryTime);
        index = 0;
        this.inAnswerMode();
        sendMsgToPreper();
    }

    @Override
    public void rememoryTimeToEnd(int answerTime) {
        super.rememoryTimeToEnd(answerTime);
        this.endAnswerMode();
    }
}