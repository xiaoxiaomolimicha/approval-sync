package com.erplus.sync.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.erplus.sync.utils.DateTimeHelper;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@TableName("request_filed")
public class RequestFiled {
    @TableField("Fid")
    private Integer id;  // 自增ID

    @TableField("Frequest_id")
    private Integer requestId;  // 审批流

    @TableField("Fwho_filed")
    private Integer whoFiled;  // 归档人

    @TableField("Fwho_filed_mcid")
    private Integer whoFiledMcid;  // 归档人主ID

    @TableField("Fwho_filed_ciid")
    private Integer whoFiledCiid;  // 归档人

    @TableField("Ffiled_time")
    private String filedTime;  // 归档时间

    @TableField("Fcompany_id")
    private Integer companyId;  // 公司ID

    @TableField("Fstatus")
    private Integer status;  // 默认为0有效，1无效

    @TableField("Fdisable_time")
    private Date disableTime;  // 撤回归档操作时间
    @TableField(exist = false)
    private String disableTimeStr;  // 撤回归档操作时间的字符串形式

    @TableField("Fcreate_time")
    private Date createTime;  // 创建时间
    @TableField(exist = false)
    private String createTimeStr;  // 创建时间的字符串形式

    @TableField("Ftemplate_type")
    private Integer templateType;  // 模板类型

    @TableField("Fis_temp")
    private Integer isTemp;  // 是否为临时抄送人 0.不是; 1.是

    @TableField("Fcrt_by")
    private Integer crtBy;  // 添加人（默认为空，添加临时抄送人才有值）

    @TableField("Ffiled_status")
    private Integer filedStatus;  // 归档状态:0无需归档, 1未归档，2已归档

    @TableField("Fis_read")
    private Integer isRead;  // 0未读,1已读,只有FiledStatus=1和status=0才生效

    public void setDisableTime(Date disableTime) {
        this.disableTime = disableTime;
        this.disableTimeStr = DateTimeHelper.format(disableTime, DateTimeHelper.DEFAULT_PATTERN);
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
        this.createTimeStr = DateTimeHelper.format(disableTime, DateTimeHelper.DEFAULT_PATTERN);
    }

}
