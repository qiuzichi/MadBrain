package com.unipad.brain.longPoker.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by gongkan on 2016/7/14.
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration{

    private int space1;
    private int space2;
    private int space3;
    private int items;

    public void setItems(int items) {
        this.items = items;
    }

    public SpaceItemDecoration(int space1,int space2,int space3,int items) {
        this.space1 = space1;
        this.space2 = space2;
        this.space3 = space3;
        this.items = items;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        if(parent.getChildPosition(view)%53 == 0 || parent.getChildPosition(view)%53 ==1) {
            outRect.left = space1;
        }else if (items == 0){
            outRect.left = space2;
        }else if ((parent.getChildPosition(view)%53-1)%items == 0){
            outRect.left = space3;
        } else {
            outRect.left = space2;
        }
    }
}
