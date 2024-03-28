package com.erplus.sync.entity.es;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class ApprovalFlowEsEntity {

    @JSONField(serialize = false, deserialize = false)
    private Integer request_id;

    private Integer id;

    private Integer state;

    private Integer is_approved;

    private String is_approved_at;

    private Integer company_info_id;

    private Integer contact_id;
}
