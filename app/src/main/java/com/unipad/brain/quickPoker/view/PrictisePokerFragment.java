package com.unipad.brain.quickPoker.view;

import android.os.Bundle;

/**
 * Created by gongkan on 2016/8/4.
 */
public class PrictisePokerFragment extends QuickPokerRightFragment{

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected boolean isMatchMode() {
        return false;
    }
}