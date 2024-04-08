package com.erplus.sync.entity.es;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class ComponentEsEntity {

    @JSONField(serialize = false, deserialize = false)
    private Integer request_id;

    private Integer id;

    private Integer num;

    private String value;

    private String date_value;

    private Float float_value;


    private Integer type;

    private Integer unique_id;

    private String create_time;

}
