package com.erplus.sync.entity.es;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class CommentEsEntity {

    @JSONField(serialize = false, deserialize = false)
    private Integer request_id;

    private Integer id;

    private String crt_at;

    private String message;

}
