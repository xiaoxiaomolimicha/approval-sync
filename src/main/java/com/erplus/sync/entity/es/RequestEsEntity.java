package com.erplus.sync.entity.es;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class RequestEsEntity {
    private Integer request_id;

    private Integer company_id;

    private String approval_num;

    private String request_name;

    private Integer finished;

    private Integer applicant;

    private Integer applicant_ciid;

    private Integer proxy_contact_id;

    private Integer financial_status;

    private Integer invoice_status;

    private Integer template_ancestor_id;

    private Integer template_id;

    private String create_time;

    private String finally_confirmed_time;

    private String finally_cc_time;

    private SummaryField summary_field;

    @JSONField(serialize = false, deserialize = false)
    private Integer default_type;

    List<ComponentEsEntity> request_content;

    List<GroupComponentEsEntity> sys_approval_component_group_value;

    List<CommentEsEntity> request_comment;

    List<ApprovalFlowEsEntity> sys_approval_flow;

    List<ParticipantEsEntity> sys_approval_participant;

    List<RequestFieldEsEntity> request_filed;

}
