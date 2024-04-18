package com.erplus.sync.entity.template;

import lombok.Data;

import java.util.List;

@Data
public class SelectionSetting {

    private String name;

    private List<Integer> relatedIds;
}
