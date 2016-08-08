package com.unipad.http;

import com.unipad.observer.GlobleObserService;

/**
 * Created by gongjiebin on 2016/6/25.
 */
public class HitopAttention extends HitopRequest<Object> {

    private GlobleObserService sevice;

    public HitopAttention(String path) {
        super(path);
    }

    public HitopAttention(){
        super(HttpConstant.ATTENTION_HTTP);
    }

    @Override
    public String buildRequestURL() {
        return getHost();
    }

    @Override
    public Object handleJsonData(String json) {
        // 处理返回结果
        this.sevice.noticeDataChange(HttpConstant.ATTENTION,json);
        return null;
    }

    public void setSevice(GlobleObserService sevice) {
        this.sevice = sevice;
    }
}

