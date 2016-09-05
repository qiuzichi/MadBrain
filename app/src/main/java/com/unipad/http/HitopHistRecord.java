package com.unipad.http;
import android.util.Log;

import com.unipad.AppContext;
import com.unipad.brain.App;
import com.unipad.brain.R;
import com.unipad.brain.home.bean.HisRecord;
import com.unipad.brain.personal.dao.PersonCenterService;
import com.unipad.common.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Administrator on 2016/6/23 0023.
 */
public class HitopHistRecord extends HitopRequest<List<HisRecord>>{
    private int key;
    public HitopHistRecord(String path) {
        super(path);
    }
    public HitopHistRecord(int status ,String startTime,String endDate,int page,int size) {
        super(HttpConstant.HisRecord);
        key = HttpConstant.HISRECORD_OK;
        mParams.addBodyParameter("userId", AppContext.instance().loginUser.getUserId());

        if (status == 0 || status == 1||status == 2||status == 3) {
            mParams.addBodyParameter("status", "" + status);
        }
        if (null != startTime) {
            //startTime=startTime.replaceAll("/","|");
            mParams.addBodyParameter("startDate", startTime);
        }
        if (null != endDate) {
            //endDate=endDate.replaceAll("/","|");
            mParams.addBodyParameter("endDate", endDate);
        }
        mParams.addBodyParameter("page", "" + page);
        mParams.addBodyParameter("size", "" + size);

    }

    public HitopHistRecord(int requestPager, int pageSize) {
        super(HttpConstant.ExerRecord);
        key = HttpConstant.EXECISE_DATA;
        mParams.addBodyParameter("page", requestPager + "");
        mParams.addBodyParameter("pageSize", pageSize + "");
        mParams.addBodyParameter("userId", AppContext.instance().loginUser.getUserId());
    }

    @Override
    public String buildRequestURL() {
        return getHost();
    }
    @Override
    public List<HisRecord> handleJsonData(String json) throws JSONException {
        List<HisRecord> hisRecords=null;
        JSONObject jsObj=null;
        String response = null;
        try {
            response = new String(json.getBytes(), "utf-8");
            jsObj = new JSONObject(response);
            Log.e("", "" + json);
            if (jsObj != null && jsObj.toString().length() != 0)
                if (jsObj.getInt("ret_code") == 0) {

                    JSONObject data = jsObj.getJSONObject("data");
                    JSONArray jsonArray = data.getJSONArray("resultList");
                    int totalPage = data.optInt("totalPage");
                    int iSize = jsonArray.length();
                    hisRecords = new ArrayList<>();
                    for (int i = 0; i < iSize; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        HisRecord bean = new HisRecord();
                        //{"memoryTime":7,"rank":1,"recallTime":155,"score":100}  "memoryTime":7,"rank":1,"recallTime":155,"score":100}
                        if (key == HttpConstant.HISRECORD_OK) {
                            bean.setMatchId(jsonObject.optString("matchId"));
                            bean.setGroupId(jsonObject.optString("groupId"));
                            bean.setGradeId(jsonObject.optString("gradeId"));
                            bean.setRanking(jsonObject.optString("ranking"));
                        }
                        if (i == 0) {
                            bean.setTotalPage(totalPage);
                        }
                        bean.setProjectId(jsonObject.optString("projectId"));
                        bean.setStartDate(jsonObject.optString("startDate"));
                        bean.setRectime(jsonObject.optString("rectime"));
                        bean.setMemtime(jsonObject.optString("memtime"));
                        bean.setScore(jsonObject.optString("score"));
                        hisRecords.add(bean);
                    }
                } else {
                    ((PersonCenterService)AppContext.instance().getService
                            (Constant.PERSONCENTER)).noticeDataChange(key,jsObj.getString("ret_msg"));
                    return null;
                }


        } catch (Exception e) {
            e.printStackTrace();
            ((PersonCenterService)AppContext.instance().getService
                    (Constant.PERSONCENTER)).noticeDataChange(key,  App.getContext().getString(R.string.string_json_error));
            return null;
        }
        ((PersonCenterService)AppContext.instance().getService
                (Constant.PERSONCENTER)).noticeDataChange(key,hisRecords);
        return null;
    }
}
