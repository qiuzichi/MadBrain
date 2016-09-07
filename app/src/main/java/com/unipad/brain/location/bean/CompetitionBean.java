package com.unipad.brain.location.bean;
/**
 * @描述：  赛事Bean
 * @author gongjiebin
 */
public class CompetitionBean {
    // 赛事ID
	private String comId;
	// 比赛日期
	private String competitionDate;
	// 所需费用
	private String cost;
	// 是否关注
	private boolean attention;
	// 报名的状态 0报名中 1报名截止 2比赛进行心中 3结束
	private int applyState;
	// 0 城市赛 | 1中国赛  | 2 世界赛
	private int competitionLevel;

	private String addr; // 比赛地址
	// 赛事名称
	private String name;
	// 参赛项目名称
	private String projecNname;
   // 已经报名的项目ID
	private String projectId;
	// 是否已经支付
	private int isApply;

	public int getIsApply() {
		return isApply;
	}

	public void setIsApply(int isApply) {
		this.isApply = isApply;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getComId() {
		return comId;
	}

	public void setComId(String comId) {
		this.comId = comId;
	}

	public String getProjecNname() {
		return projecNname;
	}

	public void setProjecNname(String projecNname) {
		this.projecNname = projecNname;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCompetitionLevel() {
		return competitionLevel;
	}


	public void setCompetitionLevel(int competitionLevel) {
		this.competitionLevel = competitionLevel;
	}


	public String getCompetitionDate() {
		return competitionDate;
	}


	public void setCompetitionDate(String competitionDate) {
		this.competitionDate = competitionDate;
	}


	public String getCost() {
		return cost;
	}


	public void setCost(String cost) {
		this.cost = cost;
	}


	public boolean isAttention() {
		return attention;
	}


	public void setAttention(boolean attention) {
		this.attention = attention;
	}


	public int getApplyState() {
		return applyState;
	}


	public void setApplyState(int applyState) {
		this.applyState = applyState;
	}


	@Override
	public String toString() {
		return "CompetitionBean [competitionDate=" + competitionDate
				+  ", cost=" + cost
				+ ", attention=" + attention + ", applyState=" + applyState
				+ "]";
	}

	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(this == o) return true;
		if(getClass() != o.getClass()) return false;
		if(o instanceof CompetitionBean){
			CompetitionBean bean = (CompetitionBean)o;
			if(bean.getComId().equals(this.getComId())){
				return true;
			}
		}
		return false;
	}

}
