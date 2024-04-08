package com.erplus.sync.entity;

import lombok.Data;

@Data
public class RequestFiled {
    private Integer Fid;  // 自增ID
    private Integer Frequest_id;  // 审批流
    private Integer Fwho_filed;  // 归档人
    private Integer Fwho_filed_mcid;  // 归档人主ID
    private Integer Fwho_filed_ciid;  // 归档人
    private String Ffiled_time;  // 归档时间
    private Integer Fcompany_id;  // 公司ID
    private Integer Fstatus;  // 默认为0有效，1无效
    private String Fdisable_time;  // 撤回归档操作时间
    private String Fcreate_time;  // 创建时间
    private Integer Ftemplate_type;  // 模板类型
    private Integer Fis_temp;  // 是否为临时抄送人 0.不是; 1.是
    private Integer Fcrt_by;  // 添加人（默认为空，添加临时抄送人才有值）
    private Integer Ffiled_status;  // 归档状态:0无需归档, 1未归档，2已归档
    private Integer Fis_read;  // 0未读,1已读,只有Filed_status=1和status=0才生效
}
