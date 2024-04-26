package com.erplus.sync.entity.es;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class GroupComponentEsEntity {

    @JSONField(serialize = false, deserialize = false)
    private Integer request_id;

    private Integer id;

    private Integer component_group_id;

    private List<GroupNestedComponent> component;
}
