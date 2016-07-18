package com.unipad;

/**
 * Created by gongkan on 2016/4/12.
 */
public interface  ICoreService {
    boolean init();
    void clear();
    public interface IGameHand extends ICoreService{
        void parseData(String data);
        boolean IsALlAready();
        void initResourse(String soursePath);

        void startMemory();
        void starRememory();

        void pauseGame();

        void reStartGame();
        void initDataFinished();
    }
}
