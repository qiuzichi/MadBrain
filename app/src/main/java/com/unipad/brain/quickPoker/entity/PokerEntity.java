package com.unipad.brain.quickPoker.entity;



import java.util.ArrayList;

public class PokerEntity {
    private int pokerWith = 0;
    /** the lrucache for diy resource */

    private int pokerHeigth;
    /**
     * 多少副扑克牌，默认至少一副
     */
    public static int pairs = 1;
    /**
     * 一副扑克牌有多少张，规定是52张
     */
    public static int pairNums = 52;
    /**
     * 扑克牌分行排布规则：分三行显示：第一行和第三行显示16张，第二行显示20张
     */
    public static int[] basic_order = {16, 20, 16};//
    private ArrayList<ChannelItem> pokerSortArray;// 用于排布扑克后，保存扑克的顺序
    private static PokerEntity instance ;

    private PokerEntity() {
    }


    public synchronized static PokerEntity getInstance() {
        if (instance == null) {
            instance = new PokerEntity();
        }
        return instance;
    }


    public ArrayList<ChannelItem> getPokerSortArray() {
        if (pokerSortArray == null) {
            pokerSortArray = new ArrayList<>();
        }
        return pokerSortArray;
    }


    public int getPokerWith() {
        return pokerWith;
    }

    public void setPokerWith(int pokerWith) {
        this.pokerWith = pokerWith;
    }

    public int getPokerHeigth() {
        return pokerHeigth;
    }

    public void setPokerHeigth(int pokerHeigth) {
        this.pokerHeigth = pokerHeigth;
    }


    public void clear(){
        instance = null;
    }
}
