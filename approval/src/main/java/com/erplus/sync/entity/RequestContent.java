package com.erplus.sync.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("request_content")
public class RequestContent {

    @TableId(value = "Fid", type = IdType.AUTO)
    private Integer id;

    @TableField("Ftemplate_id")
    private Integer templateId;

    @TableField("Fcompany_id")
    private Integer companyId;

    @TableField("Fcomponent_num")
    private Integer componentNum;

    @TableField("Fcontent")
    private String content;

    @TableField("Frequest_id")
    private Integer requestId;

    @TableField("Fcreate_time")
    private Date createTime;

    @TableField("Fcontent_type")
    private Integer contentType;

    @TableField("Fsort")
    private Integer sort;

    @TableField("Funique_id")
    private Integer uniqueId;

    @TableField("Fledger_flow_type_id")
    private String ledgerFlowTypeId;
}
