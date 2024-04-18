package com.erplus.sync.entity.template;

import lombok.Data;

import java.util.List;

@Data
public class MaxUniqueIdEntity {

    private Integer templateId;

    private Integer ancestorId;

    private String templateComponents;

    private List<TemplateComponent> templateComponentList;

}
