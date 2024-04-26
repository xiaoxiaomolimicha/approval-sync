package com.erplus.sync.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("request_flow")
public class RequestFlow {
    @TableId("Frequest_id")
    private Integer requestId;

    @TableField("Frequest_name")
    private String requestName;

    @TableField("Frequest_templet")
    private Integer templateId;

    @TableField("Frequest_step")
    private Integer requestStep;

    @TableField("Fcompany_id")
    private Integer companyId;

    @TableField("Fapplicant")
    private Integer applicant;

    @TableField("Fapplicant_ciid")
    private Integer applicantCiid;

    @TableField("Fcreate_time")
    private Date createTime;

    @TableField(exist = false)
    private String createTimeStr;

    @TableField("Fchecked_time")
    private String checkedTime;
    @TableField(exist = false)
    private String checkedTimeStr;

    @TableField("Fcall_back_time")
    private Date callBackTime;
    @TableField(exist = false)
    private String callBackTimeStr;

    @TableField("Fdisable_time")
    private Date disableTime;
    @TableField(exist = false)
    private String disableTimeStr;

    @TableField("Foperator_id")
    private Integer operatorId;

    @TableField("Fall_judger")
    private String allJudger;

    @TableField("Fall_judger_ciid")
    private String allJudgerCiid;

    @TableField("Fwho_confirm")
    private String whoConfirm;

    @TableField("Fwho_confirm_ciid")
    private String whoConfirmCiid;

    @TableField("Fwho_the_next")
    private String whoTheNext;

    @TableField("Fwho_the_next_ciid")
    private String whoTheNextCiid;

    @TableField("Fwho_refused")
    private String whoRefused;

    @TableField("Fwho_refused_ciid")
    private String whoRefusedCiid;

    @TableField("Fcontent_ids")
    private String contentIds;

    @TableField("Fcontent_nums")
    private String contentNums;

    @TableField("Fcc")
    private String cc;

    @TableField("Fcc_ciid")
    private String ccCiid;

    @TableField("Ffinished")
    private Integer finished;

    @TableField("Fversion")
    private Integer version;

    @TableField("Fouter_position")
    private String outerPosition;

    @TableField("Fis_resubmit")
    private Integer isResubmit;

    @TableField("Frefuse_chain")
    private String refuseChain;

    @TableField("Frequest_template_type")
    private Integer requestTemplateType;

    @TableField("Frela_people")
    private String relaPeople;

    @TableField("Frela_people_ciid")
    private String relaPeopleCiid;

    @TableField("Fmanual_ending_time")
    private Date manualEndingTime;
    @TableField(exist = false)
    private String manualEndingTimeStr;

    @TableField("Frequest_content_last")
    private String requestContentLast;

    @TableField("Ffinally_confirmed_time")
    private Date finallyConfirmedTime;
    @TableField(exist = false)
    private String finallyConfirmedTimeStr;

    @TableField("Ffinally_cc_time")
    private Date finallyCcTime;
    @TableField(exist = false)
    private String finallyCcTimeStr;

    @TableField("Frequest_ancestor_id")
    private Integer requestAncestorId;

    @TableField("Frequest_content_last_total_second")
    private String requestContentLastTotalSecond;

    @TableField("Fnatural_content_time_last")
    private String naturalContentTimeLast;

    @TableField("Fis_annual_leave")
    private Integer isAnnualLeave;

    @TableField("Flatest_approved_time")
    private Date latestApprovedTime;
    @TableField(exist = false)
    private String latestApprovedTimeStr;

    @TableField("Fapproval_num")
    private String approvalNum;

    @TableField("Ftotal_money")
    private String totalMoney;

    @TableField("Fgeneration")
    private Integer generation;

    @TableField("Fcontact_submit_time")
    private String contactSubmitTime;

    @TableField("Fstate")
    private Integer state;

    @TableField("Fproxy_contact_id")
    private Integer proxyContactId;

    @TableField("Fversion_info")
    private String versionInfo;

    @TableField("Ffinancial_status")
    private Integer financialStatus;

    @TableField("Finvoice_status")
    private Integer invoiceStatus;

    @TableField("Ftemplate_ancestor_id")
    private Integer templateAncestorId;

    @TableField("Fprint_count")
    private Integer printCount;
}

