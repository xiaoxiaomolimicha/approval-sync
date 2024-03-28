package com.erplus.sync.entity.es;

import java.util.List;

public class RelationEntity {
    private List<String> objectTitles;

    private Integer objectType;

    private String objectIds;

    private String nameList;

    private String selectIdList;

    public RelationEntity() {
    }

    public List<String> getObjectTitles() {
        return objectTitles;
    }

    public void setObjectTitles(List<String> objectTitles) {
        this.objectTitles = objectTitles;
    }

    public Integer getObjectType() {
        return objectType;
    }

    public void setObjectType(Integer objectType) {
        this.objectType = objectType;
    }

    public String getObjectIds() {
        return objectIds;
    }

    public void setObjectIds(String objectIds) {
        this.objectIds = objectIds;
    }

    public String getNameList() {
        return nameList;
    }

    public void setNameList(String nameList) {
        this.nameList = nameList;
    }

    public String getSelectIdList() {
        return selectIdList;
    }

    public void setSelectIdList(String selectIdList) {
        this.selectIdList = selectIdList;
    }

    @Override
    public String toString() {
        return "RelationEntity{" +
                "objectTitles=" + objectTitles +
                ", objectType=" + objectType +
                ", objectIds='" + objectIds + '\'' +
                ", nameList='" + nameList + '\'' +
                ", selectIdList='" + selectIdList + '\'' +
                '}';
    }
}
