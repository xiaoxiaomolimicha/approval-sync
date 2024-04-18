package com.erplus.sync.entity.template;


import lombok.Data;

import java.util.Objects;

@Data
public class MoneyAnalysis {

    public static int INCOME_TYPE = 1;

    public static int EXPENDITURE_TYPE = 2;
    /**
     * 子组件位置（组件集才有）
     */
    private Integer num;
    /**
     * 企业收支类型： 1.收入; 2.支出
     */
    private Integer ledgerFlowType;
    /**
     * 项目收支类型 1.收入; 2.支出
     */
    private Integer projectAnalysisType;
    /**
     * 是否开启项目分摊
     */
    public Integer isProjectApportionment;
    /**
     * 项目必填
     */
    private Integer isProjectRequire;

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getLedgerFlowType() {
        return ledgerFlowType;
    }

    public void setLedgerFlowType(Integer ledgerFlowType) {
        this.ledgerFlowType = ledgerFlowType;
    }

    public Integer getProjectAnalysisType() {
        return projectAnalysisType;
    }

    public void setProjectAnalysisType(Integer projectAnalysisType) {
        this.projectAnalysisType = projectAnalysisType;
    }

    public boolean projectIncome() {
        return Objects.equals(projectAnalysisType, INCOME_TYPE);
    }

    public boolean projectExpenditure() {
        return Objects.equals(projectAnalysisType, EXPENDITURE_TYPE);
    }
}
