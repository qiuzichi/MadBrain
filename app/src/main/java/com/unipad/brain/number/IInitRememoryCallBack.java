package com.unipad.brain.number;

/**
 * Created by gongkan on 2016/7/5.
 */
public interface IInitRememoryCallBack {
    void begin();

    void loading(int progress);

    void finish();

    void onItemClick(int position);
}
