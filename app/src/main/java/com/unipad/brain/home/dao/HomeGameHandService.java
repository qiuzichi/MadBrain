package com.unipad.brain.home.dao;

import com.unipad.AppContext;
import com.unipad.ICoreService;
import com.unipad.http.HitopAttention;
import com.unipad.http.HitopGetRule;
import com.unipad.http.HittopGetUserGame;
import com.unipad.observer.GlobleObserService;

/**
 * Created by gongkan on 2016/6/15.
 */
public class HomeGameHandService extends GlobleObserService implements ICoreService {

    @Override
    public boolean init() {
        return false;
    }

    @Override
    public void clear() {

    }

    public void sendMsgGetGame(String projectId,String group,int page,int size){
        HittopGetUserGame httpGame = new HittopGetUserGame(AppContext.instance().loginUser.getUserId(), projectId,group,page,size);
        httpGame.post();
    }

    public void getRule(String id){
        HitopGetRule httpGetRule = new HitopGetRule(id,null,this);
        httpGetRule.post();
    }

    /**
     * 关注  取消关注
     * @param match_id
     * @param user_id
     * @param method
     * @param method_type true 关注  false 取消关注
     */
    public void attention(String match_id,String user_id,String method,String method_type,String index, int level){
        HitopAttention hitopAttention = new HitopAttention(level);
        hitopAttention.buildRequestParams("matchId",match_id);
        hitopAttention.buildRequestParams("userId",user_id);
        hitopAttention.buildRequestParams("method","0");
        hitopAttention.buildRequestParams("method_type",method_type);
        hitopAttention.buildRequestParams("index",index);
        hitopAttention.setSevice(this);
        hitopAttention.post();
    }
}
