package com.unipad.common;

import android.content.Context;

import com.unipad.brain.App;
import com.unipad.brain.R;
import com.unipad.brain.home.util.SharedPreferencesUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gongkan on 2016/4/12.
 */
public class Constant {

    public static final  String GAME_FILE_PATH = App.getContext().getAppFilePath()+"/game";

    public static final String HEADSERVICE = "headService";
    public static final String ABS_FIGURE = "absFigure";
    public static final String WORDS_SERVICE = "words_service";
    public static final String BINARYNUMSERVICE = "binarynumservice";
    public static final String PERSONCENTER = "personcenterservice";
    public static final String NEWS_SERVICE = "news_service";
    public static final String LOCATION_SERVICE = "location_service";
    public static final String HISRECORD_SERVICE="hisrecord_service";


    public static final String LOGIN_WAIT_DLG = "com.unipad.login_wait_dlg";
    public static final String SHOW_RULE_DIG = "com.unipad.show_rule_dlg";
    public static final String SHOW_GAME_PAUSE = "com.unipad.show_game_pause";
    public static final String INIT_REMEMORY_DLG = "com.unipad.init_rememory_dlg";
    public static final String GET_RANDOM_QUESTION_ERROR_DLG = "get_random_question_error_dlg";
    public static final String CREATE_SETTING_WATI_DLG = "create_setting_wati_dlg";
    public static final String MATCH_RESULT_DLG = "match_result_dlg";
    public static final String MATCH_OVER_DLG = "match_over_dlg";
    public static final String HOME_GAME_HAND_SERVICE = "com.unipad.brain.home.dao.HomeGameHandService";
    public static final String LONG_SERVICE = "com.unipad.brain.number.dao.NumService";

    public static final String CITY_GAME = "00001";

    public static final String CHIMA_GAME = "00002";

    public static final String WORD_GAME = "00003";
    public static final String VIRTUAL_TIME_SERVICE = "virtual_time_service";
    public static final String QUICK_POKER_SERVICE = "quick_poker_service";
    public static final String LONG_POKER_SERVICE = "long_poker_service";
    public static final String QUICK_RANDOM_NUM_SERVICE ="quick_random_num_service" ;
    public static final String LISTENTOWRITE_NUM_SERVICE="listentowrite_num_service";

    public static final String KEY_SET_BINARY_MEMORY_TIME = "key_set_binary_memory_time";
    public static final String KEY_SET_BINARY_ANSWER_TIME = "key_set_binary_answer_time";



    private static Map<String, String> projectNames;
    private static Map<String,String> GradeId;
    private static Map<String, String> projectIds;

    public static final String GAME_PORTRAITS = "00001";

    public static final String GAME_BINARY_NUM = "00002";

    public static final String GAME_LONG_NUM = "00003";

    public static final String GAME_ABS_PICTURE = "00004";

    public static final String GAME_RANDOM_NUM = "00005";

    public static final String GAME_VIRTUAL_DATE = "00006";

    public static final String GAME_LONG_POCKER = "00007";

    public static final String GAME_RANDOM_WORDS = "00008";

    public static final String GAME_LISTON_AND_MEMORY_WORDS = "00009";

    public static final String GAME_QUICKIY_POCKER = "00010";
    public static final int POKER_NUM = 52;

    public static String getProjectName(String projectId) {
        if (null == projectNames) {
            projectNames = new HashMap<String, String>();
            projectNames.put(GAME_PORTRAITS, App.getContext().getString(R.string.project_1));
            projectNames.put(GAME_BINARY_NUM, App.getContext().getString(R.string.project_2));
            projectNames.put(GAME_LONG_NUM, App.getContext().getString(R.string.project_3));
            projectNames.put(GAME_ABS_PICTURE,App.getContext().getString(R.string.project_4));
            projectNames.put(GAME_RANDOM_NUM, App.getContext().getString(R.string.project_5));
            projectNames.put(GAME_VIRTUAL_DATE, App.getContext().getString(R.string.project_6));
            projectNames.put(GAME_LONG_POCKER, App.getContext().getString(R.string.project_7));
            projectNames.put(GAME_RANDOM_WORDS, App.getContext().getString(R.string.project_8));
            projectNames.put(GAME_LISTON_AND_MEMORY_WORDS, App.getContext().getString(R.string.project_9));
            projectNames.put(GAME_QUICKIY_POCKER, App.getContext().getString(R.string.project_10));
        }
        return projectNames.get(projectId);
    }
    public static String getGradeId(String gradId) {
        if (CITY_GAME.equals(gradId)) {
            return App.getContext().getString(R.string.city_game);
        }
        if (CHIMA_GAME.equals(gradId)){
            return App.getContext().getString(R.string.china_game);
        }
        if (WORD_GAME.equals(gradId)){
            return App.getContext().getString(R.string.world_game);
        }
        return gradId;
    }
    public static final float LEFT_RATIO = 1.0f / 5.8f;
    public static final float RIGHT_RATIO = 1 - LEFT_RATIO;
    public static String getProjectId(String projectName) {
        if (null == projectIds) {
            projectIds = new HashMap<String, String>();
            projectIds.put(App.getContext().getString(R.string.project_1), GAME_PORTRAITS);
            projectIds.put(App.getContext().getString(R.string.project_2), GAME_BINARY_NUM);
            projectIds.put(App.getContext().getString(R.string.project_3), GAME_LONG_NUM);
            projectIds.put(App.getContext().getString(R.string.project_4), GAME_ABS_PICTURE);
            projectIds.put(App.getContext().getString(R.string.project_5), GAME_RANDOM_NUM);
            projectIds.put(App.getContext().getString(R.string.project_6), GAME_VIRTUAL_DATE);
            projectIds.put(App.getContext().getString(R.string.project_7), GAME_LONG_POCKER);
            projectIds.put(App.getContext().getString(R.string.project_8), GAME_RANDOM_WORDS);
            projectIds.put(App.getContext().getString(R.string.project_9), GAME_LISTON_AND_MEMORY_WORDS);
            projectIds.put(App.getContext().getString(R.string.project_10), GAME_QUICKIY_POCKER);
        }
        return projectIds.get(projectName);
    }


    /**
     * 判断程序是否为第一次运行
     *
     * @return true 是 false 否
     */
    public static Boolean isfirstRun(Context context, SharedPreferencesUtil sharedPreferencesUtil) {
        boolean isFirstRun = sharedPreferencesUtil.getBoolean("isFirstRun", true);
        if (isFirstRun) {
            sharedPreferencesUtil.saveBoolean("isFirstRun", false);
            return true;
        } else {
            return false;
        }
    }
}
