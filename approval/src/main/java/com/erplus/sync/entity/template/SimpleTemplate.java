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
