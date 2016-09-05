package com.unipad.brain.personal.dao;

import android.text.TextUtils;

import com.unipad.AppContext;
import com.unipad.AuthEntity;
import com.unipad.ICoreService;
import com.unipad.UserDetailEntity;
import com.unipad.common.MobileInfo;
import com.unipad.http.HitopApplyed;
import com.unipad.http.HitopAuth;
import com.unipad.http.HitopAuthInfo;
import com.unipad.http.HitopAuthUploadFile;
import com.unipad.http.HitopFeedback;
import com.unipad.http.HitopFollow;
import com.unipad.http.HitopGetRule;
import com.unipad.http.HitopHistRecord;
import com.unipad.http.HitopLogin;
import com.unipad.http.HitopMatchStart;
import com.unipad.http.HitopQuitLogin;
import com.unipad.http.HitopTopList;
import com.unipad.http.HitopUpPhoto;
import com.unipad.http.HitopUpdataPwd;
import com.unipad.http.HitopUserInfoUpdate;
import com.unipad.observer.GlobleObserService;
import com.unipad.utils.MD5Utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by gongkan on 2016/6/6.
 */
public class PersonCenterService extends GlobleObserService implements ICoreService {

    public void loginIn(String userName, String pwd) {
        HitopLogin httpLogin = new HitopLogin();
        httpLogin.buildRequestParams("user_name", userName);
        httpLogin.buildRequestParams("user_password", MD5Utils.MD5_two(pwd));
        httpLogin.buildRequestParams("device_name", MobileInfo.getDeviceName());
        httpLogin.buildRequestParams("device_did", MobileInfo.getDeviceId());
        httpLogin.setSevice(this);
        httpLogin.post();

        /** HitopDownLoad httpdownload = new HitopDownLoad();
         httpdownload.buildRequestParams("questionId", "2AB5D7C647ED4A768CAF9258A1A0EAC6");
         httpdownload.setService((HeadService) AppContext.instance().getService(Constant.HEADSERVICE));
         httpdownload.downLoad("333.zip");

         HitopGetQuestion httpGetQuestion =new HitopGetQuestion();
         httpGetQuestion.buildRequestParams("questionId", "2AB5D7C647ED4A768CAF9258A1A0EAC6");
         httpGetQuestion.setService((IGameHand) AppContext.instance().getService(Constant.HEADSERVICE));
         httpGetQuestion.setService((HeadService) AppContext.instance().getService(Constant.HEADSERVICE));
         httpGetQuestion.post();
         */
    }

    @Override
    public boolean init() {
        return false;
    }

    @Override
    public void clear() {

    }
    /*
        实名认证
     */
    public void userAuth(AuthEntity authBean) {
        HitopAuth hitopAuth = new HitopAuth();
        hitopAuth.buildRequestParams("user_id", authBean.getId());
        hitopAuth.buildRequestParams("user_category", authBean.getType());
        hitopAuth.buildRequestParams("user_reaName", authBean.getName());
        hitopAuth.buildRequestParams("user_identity", authBean.getIdentity());
        hitopAuth.buildRequestParams("user_born", authBean.getBirthDate());
        hitopAuth.buildRequestParams("user_onphoto", authBean.getIdFrontUrl());
        hitopAuth.buildRequestParams("user_idephoto", authBean.getIdReverseUrl());
        hitopAuth.buildRequestParams("sex", authBean.getSex());
        hitopAuth.buildRequestParams("user_gradcert", authBean.getRating_certificate1() + (authBean.getRating_certificate2() == "" ? "" : "," + authBean.getRating_certificate2()));
        hitopAuth.setSevice(this);
        hitopAuth.post();
    }
    /**
     * 上传实名认证所需图片
     *
     * @param path 本地文件地址
     */
    public void uploadAuthFile(String path) {
        HitopAuthUploadFile hitopAuthUploadFile = new HitopAuthUploadFile();
        hitopAuthUploadFile.setSevice(this);
        Map<String, File> mapFile = new HashMap<String, File>();
        mapFile.put("image", new File(path));
        hitopAuthUploadFile.UpLoadFile(mapFile);}



    public void uploadAuthFile(String path,int index){
        if(index == 0){
            HitopAuthUploadFile hitopAuthUploadFile = new HitopAuthUploadFile();
            hitopAuthUploadFile.setSevice(this);
            Map<String,File> mapFile = new HashMap<String,File>();
            mapFile.put("image", new File(path));
            hitopAuthUploadFile.UpLoadFile(mapFile);
        } else {
            HitopUpPhoto hitopUpPhoto = new HitopUpPhoto();
            hitopUpPhoto.setSevice(this);
            Map<String,File> mapFile = new HashMap<String,File>();
            mapFile.put("image", new File(path));
            hitopUpPhoto.UpLoadFile(mapFile);
        }
    }

    /**
     * @param userId
     * @描述： 得到实名认证信息
     */
    public void getAuthInfo(String userId) {
        HitopAuthInfo hitopAuthInfo = new HitopAuthInfo();
        hitopAuthInfo.buildRequestParams("user_id", userId);
        hitopAuthInfo.setSevice(this);
        hitopAuthInfo.post();
    }

    /**
     * @param userDetailEntity 用户基本信息
     * @描述： 保存用户基本信息
     */
    public void saveUserInfo(UserDetailEntity userDetailEntity) {
        if (userDetailEntity == null)
            return;
        HitopUserInfoUpdate userInfoUpdate = new HitopUserInfoUpdate();
        userInfoUpdate.buildRequestParams("id", AppContext.instance().loginUser.getUserId());
        if (!TextUtils.isEmpty(userDetailEntity.getPhoto()))
            userInfoUpdate.buildRequestParams("photo", userDetailEntity.getPhoto());
        if (!TextUtils.isEmpty(userDetailEntity.getUserName()))
            userInfoUpdate.buildRequestParams("name", userDetailEntity.getUserName());
        if (!TextUtils.isEmpty(userDetailEntity.getAddr()))
            userInfoUpdate.buildRequestParams("address", userDetailEntity.getAddr());
        if (!TextUtils.isEmpty(userDetailEntity.getSchool()))
            userInfoUpdate.buildRequestParams("scho", userDetailEntity.getSchool());
        if (!TextUtils.isEmpty(userDetailEntity.getCountry()))
            userInfoUpdate.buildRequestParams("country", userDetailEntity.getCountry());
        if (!TextUtils.isEmpty(userDetailEntity.getTel()))
            userInfoUpdate.buildRequestParams("phone", userDetailEntity.getTel());
        if (!TextUtils.isEmpty(userDetailEntity.getMail()))
            userInfoUpdate.buildRequestParams("mail", userDetailEntity.getMail());
        userInfoUpdate.setSevice(this);
        userInfoUpdate.post();
    }

    /**
     * 获取
     *
     * @param uid
     */
    public void getApplyList(String uid) {
        HitopApplyed hitopApplyed = new HitopApplyed();
        hitopApplyed.buildRequestParams("userId", uid);
        hitopApplyed.setSevice(this);
        hitopApplyed.post();
    }

    /**
     * 检查是否可以进入比赛
     *
     * @param matchId
     */
    public void checkMatchStart(String matchId, String projectId) {
        HitopMatchStart hitopMatchStart = new HitopMatchStart();
        hitopMatchStart.buildRequestParams("matchId", matchId); //
        hitopMatchStart.buildRequestParams("projectId", projectId);
        hitopMatchStart.setSevice(this);
        hitopMatchStart.post();
    }

    /**
     * 更改用户密码
     *
     * @param uid         用户
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    public void updataLoginPwd(String uid, String oldPassword, String newPassword) {
        HitopUpdataPwd updataPwd = new HitopUpdataPwd();
        updataPwd.buildRequestParams("userId", uid);
        updataPwd.buildRequestParams("oldPassword", MD5Utils.MD5_two(oldPassword));
        updataPwd.buildRequestParams("newPassword", MD5Utils.MD5_two(newPassword));
        updataPwd.setSevice(this);
        updataPwd.post();
    }

    /**
     * @param uid
     * @param send_text
     * @描述：提交意见反馈
     */
    public void feedback(String uid, String send_text) {
        HitopFeedback hitopFeedback = new HitopFeedback();
        hitopFeedback.buildRequestParams("user_id", uid);
        hitopFeedback.buildRequestParams("send_text", send_text);
        hitopFeedback.setSevice(this);
        hitopFeedback.post();
    }

    /**
     * @param
     * @param
     * @描述：查询历史战绩
     */
    public void getHistoryRecord(String startTime, String endTime, int curPage, int totalPage) {
        HitopHistRecord hitopRecord = new HitopHistRecord(-1, startTime, endTime, curPage, totalPage);
        //  hitopRecord.buildRequestParams("startTime",startTime);
        // hitopRecord.buildRequestParams("endTime",endTime);
        // hitopRecord.buildRequestParams("userId", uid);
        hitopRecord.post();
    }


    /**
     * @param
     * @param
     * @描述：查询练习模式成绩
     */
    public void getHistoryExerRecord(int requestPager, int pageSize) {
        HitopHistRecord hitopRecord = new HitopHistRecord(requestPager, pageSize);
        hitopRecord.post();
    }

    /*
    * 1.赛事关注列表
    */
    public void getFollwList(String uid) {
        HitopFollow hitopFollow=new HitopFollow();
        hitopFollow.buildRequestParams("user_id",uid);
        hitopFollow.setSevice(this);
        hitopFollow.post();
    }
    /*
    *排名列表
    */
    public void getTopList(String matchId){
        HitopTopList hitopTopList = new HitopTopList();
//        hitopTopList.addBodyParameter("matchId",matchId);
        hitopTopList.buildRequestParams("matchId", matchId);
//        hitopTopList.setSevice(this);
        hitopTopList.post();
    }

    /**
     * 获取赛事规则
     * @param id
     */
    public void getRule(String id){
        HitopGetRule httpGetRule = new HitopGetRule(id,null,this);
        httpGetRule.post();
    }

    public void getQuitApplication(){
        HitopQuitLogin httpQuit = new HitopQuitLogin();
        httpQuit.get();
    }
}

