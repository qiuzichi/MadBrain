package com.unipad.http;

import com.unipad.observer.GlobleObserService;

/**
 * Created by gongjiebin on 2016/6/17.
 */
public class HitopMatchStart extends HitopRequest<Object> {
    private GlobleObserService sevice;

    public HitopMatchStart(String path) {
        super(path);
    }

    public HitopMatchStart(){
        super(HttpConstant.USER_IN_GAME_HTTP);
    }

    @Override
    public String buildRequestURL() {
        return getHost();
    }

    @Override
    public Object handleJsonData(String json) {
        // 处理返回结果
        this.sevice.noticeDataChange(HttpConstant.USER_IN_GAEM,json);
        return null;
    }

    @Override
    public void buildRequestParams() {

    }

    public void setSevice(GlobleObserService sevice) {
        this.sevice = sevice;
    }
}
