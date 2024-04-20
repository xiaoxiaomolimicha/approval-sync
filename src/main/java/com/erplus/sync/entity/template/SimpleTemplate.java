package com.erplus.sync.entity.template;

import lombok.Data;

import java.util.List;

@Data
public class SimpleTemplate {

    private Integer templateId;

    private Integer ancestorId;

    private String templateComponents;

    private Integer maxUniqueId;

    private Integer defaultType;

    private List<TemplateComponent> templateComponentList;

}
