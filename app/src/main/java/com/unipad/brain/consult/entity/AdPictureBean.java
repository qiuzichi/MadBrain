package com.unipad.brain.consult.entity;

/**
 * Created by jiangLu on 2016/6/24.
 */
public class AdPictureBean {

   public AdPictureBean(){}

   public AdPictureBean(String advertPath){
       this.advertPath = advertPath;
   }
   //广告id
    private String advertId;
    //广告标题
    private String advertName;
   //图片路径
    private String advertPath;
    //跳转方式
    private String jumpType;
    //jumpUrl
    private String jumpUrl;
    //广告描述
    private String advertInfo;

    public String getJumpType() {
        return jumpType;
    }

    public void setJumpType(String jumpType) {
        this.jumpType = jumpType;
    }

    public String getAdvertId() {
        return advertId;
    }

    public void setAdvertId(String advertId) {
        this.advertId = advertId;
    }

    public String getAdvertName() {
        return advertName;
    }

    public void setAdvertName(String advertName) {
        this.advertName = advertName;
    }

    public String getAdvertPath() {
        return advertPath;
    }

    public void setAdvertPath(String advertPath) {
        this.advertPath = advertPath;
    }

    public String getAdvertInfo() {
        return advertInfo;
    }

    public void setAdvertInfo(String advertInfo) {
        this.advertInfo = advertInfo;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

}
