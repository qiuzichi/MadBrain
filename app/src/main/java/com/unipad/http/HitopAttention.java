package com.unipad.http;

import com.unipad.observer.GlobleObserService;

/**
 * Created by gongjiebin on 2016/6/25.
 */
public class HitopAttention extends HitopRequest<Object> {

    private GlobleObserService sevice;
    private int level;

    public HitopAttention(String path) {
        super(path);
    }

    public HitopAttention(){
        super(HttpConstant.ATTENTION_HTTP);
    }

    public HitopAttention(int level) {
        super(HttpConstant.ATTENTION_HTTP);
        this.level = level;
    }

    @Override
    public String buildRequestURL() {
        return getHost();
    }

    @Override
    public Object handleJsonData(String json) {
        // 处理返回结果
        switch (level){
            case 0 :
                this.sevice.noticeDataChange(HttpConstant.CITY_ATTENTION,json);
                break;
            case 1:
                this.sevice.noticeDataChange(HttpConstant.CHINA_ATTENTION,json);
                break;
            case 2:
                this.sevice.noticeDataChange(HttpConstant.WORLD_ATTENTION,json);
                break;
            default:
                break;
        }


        return null;
    }

    public void setSevice(GlobleObserService sevice) {
        this.sevice = sevice;
    }
}

