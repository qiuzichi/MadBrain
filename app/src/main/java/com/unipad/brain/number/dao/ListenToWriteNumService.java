package com.unipad.brain.number.dao;

/**
 * 听记数字service
 */
public class ListenToWriteNumService extends NumService {

    @Override
    public double getScore() {
        if (answer != null && answer.size() > 0) {
            if (answer.size() != lineNumbers.size()) {
                return 0;  // 答题卷与试卷数目行数不对
            } else {
                StringBuilder answerData = new StringBuilder();
                StringBuilder lineData = new StringBuilder();
                for (int i = 0; i < lineNumbers.size(); i++) {
                    // 取出试卷（正确的答案）
                    lineData.append(lineNumbers.valueAt(i));
                    // 取出选手所做答案
                    answerData.append(answer.valueAt(i));
                }
                for (int i = 0; i < answerData.length(); i++) {
                    if (answerData.charAt(i) != lineData.charAt(i)) {
                        return i;
                    }
                }
                return answerData.length();
            }
        } else {
            //选手未作答 得0分
            return 0;
        }

    }
}
