package com.erplus.sync.entity.es;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class ExpenseEsEntity {

    @JSONField(serialize = false, deserialize = false)
    private Integer request_id;

    private Integer id;

    private String pay_amount;

    @JSONField(serialize = false, deserialize = false)
    private String total_amount;

    private String pay_date;

}
