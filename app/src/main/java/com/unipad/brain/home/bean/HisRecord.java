package com.unipad.brain.home.bean;
/**
 * Created by Administrator on 2016/6/23 0023.
 */
public class HisRecord extends MyFollow {
    private String matchId;
    /*历史战绩*/
    private String projectId;
    /*项目*/
    private String gradeId;
    /*级别*/
    private String groupId;
    /*组别*/
    private String startDate;
    /*比赛日期*/
    private String rectime;
    /*记忆时间*/
    private String memtime;
    /*回忆时间*/
    /*得分*/
    private String score;
    /*排名*/
    private String ranking;
    /*总页数*/
    private int totalPage;

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }



    public String getMatchId(){
        return matchId;
    }
    public void setMatchId(String matchId){
        this.matchId=matchId;
    }
    public String getGroupId(){
        return groupId;
    }
    public void setGroupId(String groupId){
        this.groupId=groupId;
    }
    public String getGradeId(){
        return gradeId;
    }
    public void setGradeId(String gradeId){
        this.gradeId=gradeId;
    }
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getRectime() {
        return rectime;
    }

    public void setRectime(String rectime) {
        this.rectime = rectime;
    }

    public String getMemtime() {
        return memtime;
    }

    public void setMemtime(String memtime) {
        this.memtime = memtime;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }
    public String getProjectId() {
        return projectId;
    }
    public void setProjectId(String id) {
        this.projectId = id;
    }


}

