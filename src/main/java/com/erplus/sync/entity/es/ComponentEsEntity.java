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

    private String dateValue;

    private Float floatValue;


    private Integer type;

    private Integer unique_id;

    private String create_time;

}
