package com.unipad.brain.longPoker.entity;

import android.support.v7.widget.StaggeredGridLayoutManager;

import com.unipad.brain.R;
import com.unipad.brain.quickPoker.entity.ChannelItem;
import com.unipad.utils.LogUtil;

/**
 * Created by gongkan on 2016/7/13.
 */
public class LongPokerEntity extends ChannelItem {
    private int userAnswer;
    private int num;
    public LongPokerEntity(int id, int orderId, String name,int num) {
        super(id, orderId, name,null);
        this.num = num;
    }
    public LongPokerEntity() {

    }
    public LongPokerEntity(ChannelItem longPokerEntity) {
        this.name = longPokerEntity.name;

        this.id = longPokerEntity.id;

        this.resId = longPokerEntity.resId;
    }
    public int getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(int userAnswer) {
        LogUtil.e("","setUserAnswer:"+userAnswer);
        this.userAnswer = userAnswer;
    }

    public int getNum() {
        return num;
    }

    private int huaseId = R.drawable.heitao;


    public int getHuaseId() {
        return huaseId;
    }

    public void setHuaseId(int huaseId) {
        this.huaseId = huaseId;
    }

}
