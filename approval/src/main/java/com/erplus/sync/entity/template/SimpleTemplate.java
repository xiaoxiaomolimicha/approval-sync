package com.erplus.sync.entity.template;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Data
@Slf4j
@TableName("request_template")
public class SimpleTemplate {

    /**
     * 报销
     */
    public final static int EXPENSES = 1;
    /**
     * 请假
     */
    public final static int LEAVE = 2;
    /**
     * 外出
     */
    public final static int OUTDOOR = 3;
    /**
     * 申购
     */
    public final static int PURCHASE = 4;
    /**
     * 未打卡
     */
    public final static int APPEND_SIGN = 5;
    /**
     * 其他自定义
     */
    public final static int OTHER = 6;
    /**
     * 出差
     */
    public final static int BUSINESS_TRIP = 7;
    /**
     * 补假
     */
    public final static int COMPENSATORY_LEAVE = 8;
    /**
     * 加班
     */
    public final static int WORK_OVERTIME = 9;

    /**
     * 借款
     */
    public final static int LEND_MONEY = 10;
    /**
     * 调休
     */
    public final static int DAYS_OFF = 12;

    @TableId("Ftemplate_id")
    private Integer templateId;

    @TableField("Fancestor_id")
    private Integer ancestorId;

    @TableField("Ftemplate_component")
    private String templateComponents;

    @TableField("Fmax_unique_id")
    private Integer maxUniqueId;

    @TableField("Ftemplate_default_type")
    private Integer defaultType;

    @TableField(exist = false)
    private List<TemplateComponent> templateComponentList;

    @TableField("Fcompany_id")
    private Integer companyId;

    public void setTemplateComponents(String templateComponents) {
        this.templateComponents = templateComponents;
        try {
            this.templateComponentList = JSONObject.parseArray(templateComponents, TemplateComponent.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
