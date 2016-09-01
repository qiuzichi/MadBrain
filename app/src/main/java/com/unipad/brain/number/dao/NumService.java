package com.unipad.brain.number.dao;

import android.util.SparseArray;
import android.widget.ListView;

import com.unipad.brain.AbsBaseGameService;
import com.unipad.http.HitopGetQuestion;
import com.unipad.utils.LogUtil;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 二进制数字项目实体
 */
public class NumService extends AbsBaseGameService {
    /**
     * 文字0
     */
    public static String TEXT_ZERO = "0";
    /**
     * 文字1
     */
    public static String TEXT_ONE = "1";
    public static String TEXT_TWO = "2";
    public static String TEXT_THREE = "3";
    public static String TEXT_FOUR = "4";
    public static String TEXT_FIVE = "5";
    public static String TEXT_SIX = "6";
    public static String TEXT_SEVEN = "7";
    public static String TEXT_EIGHT = "8";
    public static String TEXT_NINE = "9";

    /**
     * 发消息开启线程准备加载界面
     */
    public static final int MSG_OPEN_THREAD = 105;
    /**
     * 发消息更新界面
     */
    public static final int MSG_REFRESH_UI = 106;
    /**
     * 行数
     */
    public  int lines = 25;
    /**
     * 列数
     */
    public  int rows = 30;
    /**
     * 以行号为key值保存题目的每一行数字的出现顺序
     */
    public  SparseArray<String> lineNumbers = new SparseArray<>();

    private String[] allQuestion;

    public SparseArray<String> answer = new SparseArray<>();

    ListView ls;

    @Override
    public void downloadingQuestion(Map<String, String> mData) {
        super.downloadingQuestion(mData);
        HitopGetQuestion httpGetQuestion = new HitopGetQuestion();
        httpGetQuestion.buildRequestParams("questionId",  mData.get("QUESTIONID"));
        httpGetQuestion.setService(this);
        httpGetQuestion.post();
    };

    @Override
    public void parseData(String data) {
        super.parseData(data);
        allQuestion  = data.split("&");
        LogUtil.e("", "data == " + data);
        round = 0;
        parseDataByNextRound();
    }

    public boolean parseDataByNextRound(){
        LogUtil.e("NumService","解析下一轮的数据+isLastRound = " + isLastRound());
        if (isLastRound()){
            return false;
        }
        round++;
        lineNumbers.clear();
        String [] persData = allQuestion[round-1].split(",");
        lines = persData.length;
        for (int i = 0;i<lines;i++){
            String[] detail = persData[i].split("\\^");
            lineNumbers.put(Integer.valueOf(detail[0]),detail[1]);
        }
        initDataFinished();
        return true;
    }
    @Override
    public double getScore() {
        return getNumberScore(40f,20f,2f,1f,1)[0];
    }


    /**
     * 马拉松数字计分
     *
     * 一行如果出现两处错误这一行将不计分
     *
     * 最后一行的处理如下：
     * 如果数字刚好能铺满并且完全正确，按照正常计分。 （40分）
     * 如果数字没有铺满，但是所填答案完全正确 所得分数是最后一行的正确的个数 * lastCorrectScore.
     * 如果数字没有铺满，所填答案出现了一处错误，所得分数为正确答案的个数 / lastErrorScore
     *
     *
     * @param fullCorrectScore 一行铺满正确所得分数
     * @param fullErrorScore  一行铺满但是只出现了一处错误或者一处露空所得分数
     * @param lastErrorScore  最后一行没有写满并且出现错误时所扣分数的倍数
     * @param lastCorrectScore 最后一行完全正确但是没有铺满 一个数字所得分数
     * @param outRangeScore   一行如果出现两个错误的情况这一行将不得分。 累积不得分行中正确答案的个数。 此参数为不得分行中一个正确答案所得分数（用于服务器区分相同得分的情况）
     * @return [0]为总得分  [1]不得分行中正确答案总计
     */
    public int[] getNumberScore(float fullCorrectScore, float fullErrorScore, float lastErrorScore,float lastCorrectScore,int outRangeScore){
        float countScore = 0;
        int outRangeNum = 0; // 累积不得分行中的正确数字的个数
        if(answer != null && answer.size() > 0){
            if(answer.size() != lineNumbers.size()){
                 return new int[]{0,0};  // 答题卷与试卷数目行数不对
            } else {
                for(int i = 0 ; i < lineNumbers.size(); i ++ ){
                    // 取出试卷（正确的答案）
                    String lineStr = lineNumbers.valueAt(i);
                    // 取出选手所做答案
                    String answerStr = answer.valueAt(i);
                    if(lineStr.equals(answerStr)){
                         // 一行完全正确
                        if((lineNumbers.size() - 1) == i){
                            // 如果是最后一行 计分方法有不同
                            countScore = countScore + (answerStr.trim().length() * lastCorrectScore);
                        } else {
                            countScore = countScore + fullCorrectScore;
                        }
                    } else {
                        // 出现错误，分别进行分割成char数组
                        char[] lineCs = lineStr.toCharArray();
                        char[] answerCs = answerStr.toCharArray();
                        int errorNum = 0; // 累积错误次数
                        int correctNum =0;  // 累积正确次数
                        int recordBlank = 0; // 纪录用户这一行所填空格
                        for(int j = 0 ; j < lineCs.length; j ++ ){
                            if(lineCs[j] != answerCs[j]){
                                if(' ' == answerCs[j]){
                                    recordBlank ++;
                                }
                                errorNum ++;
                            } else {
                                correctNum ++;
                            }
                        }
                          if(answerCs.length == recordBlank){
                              // 本行全部没有写，将不计分。
                            continue;
                          }
                          if(errorNum == 1){
                              //  本行只出现了一次错
                              if((lineNumbers.size() - 1) == i){
                                  // 最后一行计分方法
                                  countScore = countScore + (answerStr.trim().length() / lastErrorScore);
                              } else {
                                  countScore = countScore + fullErrorScore;
                              }
                          } else {
                              // 本行出现多次错误 不计分，但是需要将正确次数累积
                              outRangeNum = outRangeNum + correctNum;
                          }
                    }
                }
            }
        } else
        //选手未作答 得0分
            return new int[]{0,0};

        // 得到最后得分
        BigDecimal bigDecimal =  new BigDecimal(countScore).setScale(0, BigDecimal.ROUND_HALF_UP);
        //int lastScore = bigDecimal. 四舍五入
        Integer lastScore = bigDecimal.intValue();
        return new int[]{lastScore,outRangeNum * outRangeScore};
    }


    @Override
    public String getAnswerData() {
        StringBuilder answerData = new StringBuilder();
        for (int i = 0;i<answer.size();i++) {
            answerData.append(answer.keyAt(i))
                    .append("^")
                    .append(answer.valueAt(i))
                    .append(",");
        }
        int length = answerData.length();
        if (length > 0)
        answerData.deleteCharAt(length-1);
        LogUtil.e("answer","answer ;"+answerData.toString());
        return answerData.toString();
    }
}
