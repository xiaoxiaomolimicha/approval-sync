package com.erplus.sync.entity;

import lombok.Data;

@Data
public class RequestFlow {
    private Integer Frequest_id;  // 审批id
    private String Frequest_name;  // 审批标题，仅仅是为了人工维护好看，外链模板标题
    private Integer Frequest_templet;  // 审批模板id，外连
    private Integer Frequest_step;  // 审批步骤，0表示新的，-2表示已作废,-99表示已删除,-1表示已撤回，1表示进行到第一个人，以此类推
    private Integer Fcompany_id;  // 公司id
    private Integer Fapplicant;  // 申请人
    private Integer Fapplicant_ciid;  // 自然人在公司里的id
    private String Fcreate_time;  // 申请时间
    private String Fchecked_time;  // 每一次的审批时间，以xxxx-xx-xx xx:xx:xx,xxxx-xx-xx xx:xx:xx这种形式保存
    private String Fcall_back_time;  // 撤回时间
    private String Fdisable_time;  // 作废时间
    private Integer Foperator_id;  // 操作人id（删除、作废审批的compnayInfoId）
    private String Fall_judger;  // 所有审批人xx,xx,xx,xx
    private String Fall_judger_ciid;
    private String Fwho_confirm;  // 已经审批人xx,xx,xx,xx
    private String Fwho_confirm_ciid;
    private String Fwho_the_next;  // 下一个审批人
    private String Fwho_the_next_ciid;
    private String Fwho_refused;  // xx
    private String Fwho_refused_ciid;
    private String Fcontent_ids;  // xxxxx,xxxxx,xxx,xx,xxxxx
    private String Fcontent_nums;
    private String Fcc;  // 抄送人
    private String Fcc_ciid;
    private Integer Ffinished;  // -2表示已作废,-99表示已删除,-1表示已撤回，0表示进行中，1表示已同意，2表示已全部归档，3表示已拒绝
    private Integer Fversion;  // 模板版本
    private String Fouter_position;  // 外出经纬度，xxx.xxxxx,xxx.xxxxx#xxx.xxxxx,xxx.xxxxx
    private Integer Fis_resubmit;  // 被拒绝后是否重新发起过，1代表是
    private String Frefuse_chain;  // 拒绝链
    private Integer Frequest_template_type;  // 审批的类型，其实就是模板类型
    private String Frela_people;  // 参与人
    private String Frela_people_ciid;
    private String Fmanual_ending_time;  // 手动终止时间
    private String Frequest_content_last;  // 用于记录比如请假等审批的时间跨度，单位为秒
    private String Ffinally_confirmed_time;  // 最终同意时间
    private String Ffinally_cc_time;  // 最终归档时间
    private Integer Frequest_ancestor_id;  // 审批的最老id
    private String Frequest_content_last_total_second;  // 存放计算好了的时长，单位为秒
    private String Fnatural_content_time_last;  // 存放前端显示的自然时间
    private Integer Fis_annual_leave;  // 是否是年假，1代表是
    private String Flatest_approved_time;  // 最后一次审批时间
    private String Fapproval_num;  // 审批编号
    private String Ftotal_money;  // 报销总额,不为null表示新版本,为null为老版本
    private Integer Fgeneration;  // 审批迭代版本,配合模板类型判断,例如：报销类型,generation=1,就是多项目报销; generation=2,补打卡类型，就是考勤3.0补卡
    private String Fcontact_submit_time;  // 提交时间,用于校验重复提交
    private Integer Fstate;
    private Integer Fproxy_contact_id;  // 代申请人id
    private String Fversion_info;
    private Integer Ffinancial_status;  // 0.默认状态;1.待付款;2.已付款
    private Integer Finvoice_status;  // 开票状态: 0.无需开票 1.未开票 2.已开票
    private Integer Ftemplate_ancestor_id;
    private Integer Fprint_count;

    // Getters and Setters (省略)
}

