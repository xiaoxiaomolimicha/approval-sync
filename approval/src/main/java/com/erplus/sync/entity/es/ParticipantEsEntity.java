package com.erplus.sync.entity.es;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class ParticipantEsEntity {

    @JSONField(serialize = false, deserialize = false)
    private Integer request_id;

    private Integer id;

    private Integer contact_id;

    private Integer company_info_id;

    private String create_time;

}
