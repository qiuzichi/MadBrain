package com.unipad.http;

/**
 * Created by gongkan on 2016/5/30.
 */
public class HttpConstant {

    public static final String LOGIN = "/api/user/login";
    public static final String GET_USER_GAME_LIST = "/api/match/getMatchByUser";
    public static final String FORGET_PWD = "/api/user/forget";
    public static final String DOWNLOAD_QUESTION ="/api/file/download" ;
    public static final String GET_QUESTION = "/api/match/getQuestion";


    public static String Regist = "/api/user/regist";
    public static String UPLOAD = "/api/file/upload";
    public static String HisRecord="/api/match/history";
    public static String ExerRecord="/api/practice/history";
    public static String GET_NEWS_LIST = "/api/news/list";
    public static String GET_NEWS_VERSION = "/pub/apk/last";
    //赛事排名
    public static String  GAME_TOP="/api/match/getResult";
    //    我的关注
    public static String  GET_USER_FOLLOW="/api/user/follow";
    //点赞 评论的 url
    public static String GET_NEWS_OPERATE = "/api/news/operate";
    //广告轮播图的 url
    public static String GET_NEWS_ADVERTPIC = "//api/advert/get";

    //页面详情 url
    public static String GET_NEWS_DETAIL = "/api/news/articles";

    //评论详情 url
    public static String GET_NEWS_COMMENT = "/api/news/review";
    //最新赛事 url
    public static String GET_NEW_COMPETITION = "/api/match/newmatch";
    // 实名认证
    public static String AUTH_PATH = "/api/user/auth";
    // 调取实名认证信息 ----在用户已经实名认证之后 调取。
    public static String AUTH_INFO_PATH = "/api/user/data";

    public static String GET_RULE = "/api/match/getRule";
    /**
     * 申请报名
     */
    public static String APPLY_GAME = "/api/match/apply";
    // 获取图片路径 http://192.168.0.104:8090/crazybrain-mng
    public static final String url = "http://211.149.174.33:8090/crazybrain-mng";

    public static String PATH_FILE_URL  = url +  "/image/getFile?filePath=";
    // 更新用户信息
    public static String UPDATE_USERINFO = "/api/user/modify";
    // 得到用户已经报名列表
    public static String USER_APPLYED_HTTP= "/api/user/myApply";
    // 进入比赛时间判断
    public static String USER_IN_GAME_HTTP = "/api/match/checkMatchStart";
    // 修改用户密码
    public static String UPDATA_PWD_HTTP = "/api/user/modifyPassword";
    // 意见提交
     public static String SUBMIT_FEED_HTTP = "/api/user/feedback";
    // 获取省份列表
    public static String GET_PROVINCE_HTTP = "/pub/province/list";
    // 根据省份ID 获取城市列表
    public static String GET_CITY_HTTP="/pub/city/list";
    // 根据城市ID 获取赛事列表
    public static String GET_CITY_GAME = "/api/match/getMatchByCity";
    // 关注 取消关注
    public static String ATTENTION_HTTP = "/api/match/operate";
    /**随机获取题目*/
    public static String RANDOM_GET_QUESTION = "/api/practice/getQuestion";
    //退出应用；
    public static String USER_QUIT_APPLICATION = "/api/user/quit";


    public static int JSON_ERREO = -2;
    public static final int LOGIN_UPDATE_UI = 0x10000;
    public static final int LOGIN_WRONG_MSG = 0x10001;


    public static final int CITY_GET_HOME_GAME_LIST = 0x10002;
    public static final int CHINA_GET_HOME_GAME_LIST = 0x10003;
    public static final int WORD_GET_HOME_GAME_LIST = 0x10004;

    public static final int CITY_APPLY_GAME = 0x10005;
    public static final int CHINA_APPLY_GAME = 0x10006;
    public static final int WORD_APPLY_GAME = 0x10007;
    public static final int GET_RULE_NOTIFY = 0x10008;
    public static final int GET_RANDOM_QUESTION_ERR = 0x10009;
    public static final int GET_RANDOM_QUESTION_OK = 0x10010;

    // 实名认证
    public static final int USER_AUTH=0x10100;
    // 上传文件
    public static final int UOLOAD_AUTH_FILE =0x10200;
    // 获取用户基本信息
    public static final int PERSONALDATA=0x10300;
    // 获取用户认证信息
    public static final int USER_AUTH_INFO=0x10400;
    // 获取用户已经报名的信息
    public static final int USER_APPLYED = 0x10500;
    // 进入比赛时间判断
    public static final int USER_IN_GAEM = 0x10600;
    // 修改登录密码
    public static final int UPDATA_LOGIN_PWD = 0x10700;

    // 退出应用key
    public static final int QUIT_APPLICATION = 0x10701;
    // 提交意见
    public static final int SUBMIT_FEEDBACK=0x10800;
    // 上传文件（头像）
    public static final int UOLOAD_PHOTO_FILE=0x10900;
    // 关注比赛
    public static final int CITY_ATTENTION = 0x11000;
    public static final int CHINA_ATTENTION = 0x11001;
    public static final int WORLD_ATTENTION = 0x11002;
    // 获取规则
    public static final int GETRULE = 0x11100;


    public static final int REGIST_OK = 0x18000;
    public static final int REGIST_FILED = 0x18001;
    public static final int MODIFY_OK = 0x18002;
    public static final int MODIFY_FILED = 0x18003;
    public static final int HISRECORD_OK=0x18004;
    public static final int USER_FOLLOW = 0x18007;
    public static final int LIST_TOP = 0x18008;
    public static final int VIRTUAL_TIME=0x18009;

    public static final int VIRTUAL_RIGHT=0x18010;
    public static final int STRAT_MEMORY=0x18011;
    public static final int START_ANSWER = 0x18012;
    public static final int END_ANSWER=0x18013;
    public static final int EXECISE_DATA=0x18014;

    // 获取省份列表
    public static final int GET_PROVINCE = 0x10900;
    // 根据城市列表
    public static final int GET_CITY = 0x11000;
    // 根据城市id 获取比赛列表
    public static final int CITY_GAME=0x11100;

    // 赛事id  报名比赛
    public static final int LOCATION_APPLY_GAME=0x11101;

    //获取news数据的 key
    public static final int NOTIFY_GET_NEWS = 0x20000;
    //获取评论 收藏 点赞数据的 key
    public static final int NOTIFY_GET_OPERATE = 0x20001;
    //获取新闻详情
    public static final int NOTIFY_GET_DETAIL = 0x20002;
    //获取推荐广告轮播图
    public static final int NOTIFY_GET_ADVERT = 0x20003;
    //获取赛事资讯
    public static final int NOTIFY_GET_COMPETITION = 0x20004;
    //获取热点
    public static final int NOTIFY_GET_HOTSPOT = 0x20005;
    //获取热点广告轮播图
    public static final int NOTIFY_GET_HOTADVERT = 0x20006;

    //获取评论列表
    public static final int NOTIFY_GET_COMMENT = 0x20007;
    //最新赛事
    public static final int NOTIFY_GET_NEWCOMPETITION = 0x20008;
    //获取资讯页面的结果
    public static final int NOTIFY_GET_SEARCH_RUSULT = 0x20009;
    //获取赛事页面的结果
    public static final int NOTIFY_GET_SEARCH_OCCSION = 0x20011;
    //获取热点页面的结果
    public static final int NOTIFY_GET_SEARCH_HOTSPOT = 0x20012;
    //下载apk 请求
    public static final int NOTIFY_DOWNLOAD_APK = 0x20010;

    //最新赛事报名
    public static final int NOTIFY_APPLY_NEWCOMPETITION = 0x20011;

}
