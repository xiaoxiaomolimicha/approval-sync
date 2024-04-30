package com.erplus.sync.entity;


import java.util.Arrays;
import java.util.List;

/**
 * 模板组件的类型
 *
 * @author xq
 */
public interface ContentType {
    List<Integer> relateTypes = Arrays.asList(8, 9, 14, 30, 36, 37, 38, 39, 101, 102, 103);
    /**
     * 单行
     */
    int SINGLE_LINE = 1;
    /**
     * 多行
     */
    int TEXT = 2;
    /**
     * 数字
     */
    int NUMBER = 3;
    /**
     * 日期
     */
    int DATE = 4;
    /**
     * 时间
     */
    int TIME = 5;
    /**
     * 地点
     */
    int LOCATION = 6;
    /**
     * 单选
     */
    int SINGLE_CHOSEN = 7;
    /**
     * 关联任务
     */
    int RELATED_TASK = 8;
    /**
     * 关联审批
     */
    int RELATED_APPROVAL = 9;


    /**
     * 关联申购
     */
    @Deprecated
    int RELATED_NEED_MONEY = 10;
    /**
     * 附件
     */
    int ATTACHMENT = 11;
    /**
     * 多选
     */
    int MULTI_CHOSEN = 12;
    /**
     * 金额
     */
    int MONEY = 13;
    /**
     * 关联客户
     */
    int RELATED_CLIENT = 14;
    /**
     * 考勤方案: 方案名,方案Id
     */
    @Deprecated
    int ATTENDANCE_PLAN = 15;
    /**
     * 新增一条打卡记录
     */
    int NEW_ADD_CLOCK_CARD = 16;
    /**
     * 时间段组件
     */
    int TIME_RANGE = 17;
    /**
     * 新时间段组件
     * 暂时用不到
     */
    int NEW_TIME_RANGE = 18;
    /**
     * 请假时长
     */
    @Deprecated
    int LEAVE_DAYS = 19;
    /**
     * 全天
     */
    @Deprecated
    int ALL_DAY = 20;
    /**
     * 补卡班次
     */
    int RECEIVE_CLOCK_TICK = 21;
    /**
     * 补卡类型
     */
    int RECEIVE_CLOCK_TYPE = 22;
    /**
     * 补卡时段
     */
    int RECEIVE_CLOCK_TIME = 23;
    /**
     * 组件集类型
     */
    int COMPONENT_GROUP = 24;
    /**
     * 金额汇总
     */
    int TOTAL_MONEY = 25;
    /**
     * 组件集单选项
     */
    int GROUP_SINGLE_CHOSEN = 26;
    /**
     * 组件集地点类型
     */
    int GROUP_LOCATION = 27;

    /**
     * 调休总时长
     */
    @Deprecated
    int DAYS_OFF_TOTAL_TIME = 28;
    /**
     * 调休类型
     */
    @Deprecated
    int DAYS_OFF_TYPE = 29;
    /**
     * 关联供应商
     */
    int RELATE_SUPPLIER = 30;

    /**
     * 调休类型: 普通调休
     */
    @Deprecated
    int DAYS_OFF_TYPE_NORMAL = 31;


    /**
     * 休息时段
     */
    @Deprecated
    int REST_TIME_FRAME = 32;

    /**
     * 工作时段
     */
    @Deprecated
    int WORK_TIME_FRAME = 33;
    /**
     * 员工选择
     */
    int USER = 34;

    /**
     * 组件集多选项
     */
    int GROUP_MULTI_CHOSEN = 35;
    /**
     * 产品组件
     */
    int PRODUCT = 36;
    /**
     * 关联项目
     */
    int PROGRAM = 37;
    /**
     * 人才档案
     */
    int TALENT = 38;
    /**
     * 关联商机
     */
    int BUSINESS_OPPORTUNITY = 39;
    /**
     * 部门选择
     */
    int DEPARTMENT = 40;
    /**
     * 文章（制度与文化）
     */
    int ARTICLE = 41;

    /**
     * 联系人
     */
    int CRM_CONTACT = 42;
    /**
     * 订单
     */
    int RELATE_ORDER = 101;
    /**
     * 采购单
     */
    int RELATE_PURCHASE = 102;
    /**
     * 出库单
     */
    int OUT_STORE = 103;


}
