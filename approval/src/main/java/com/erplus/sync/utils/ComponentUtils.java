package com.erplus.sync.utils;

import com.alibaba.fastjson.JSONObject;
import com.erplus.sync.entity.Constants;
import com.erplus.sync.entity.ContentType;
import com.erplus.sync.entity.es.ComponentEsEntity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class ComponentUtils {

    private static final Logger logger = LoggerFactory.getLogger(ComponentUtils.class);

    public static String formatEsComponentValue(String value, Integer contentType) {
        try {
            if (StringUtils.isBlank(value)) {
                return null;
            }
            switch (contentType) {
                case ContentType.SINGLE_LINE:
                case ContentType.MONEY:
                case ContentType.DATE:
                    //报销事项中这三个组件是数组
                    if (isJSONArray(value)) {
                        List<String> special = JSONObject.parseArray(value, String.class);
                        if (special == null || special.isEmpty()) {
                            return null;
                        }
                        return String.join("||", special);
                    }
                    return value;
                case ContentType.TEXT:
                case ContentType.NUMBER:
                case ContentType.TIME:
                case ContentType.SINGLE_CHOSEN:
                case ContentType.GROUP_SINGLE_CHOSEN:
                case ContentType.TIME_RANGE:
                case ContentType.TOTAL_MONEY:
                    return value;
                case ContentType.LOCATION:
                case ContentType.GROUP_LOCATION:
                    if (isJSONObject(value)) {
                        JSONObject location = JSONObject.parseObject(value);
                        if (location == null || location.isEmpty()) {
                            return null;
                        }
                        return location.getString("location");
                    }
                    if (isJSONArray(value)) {
                        List<String> locations = JSONObject.parseArray(value, String.class);
                        if (locations == null || locations.isEmpty()) {
                            return null;
                        }
                        return String.join("||", locations);
                    }
                    return value;
                case ContentType.RELATED_TASK:
                case ContentType.RELATED_APPROVAL:
                case ContentType.RELATED_CLIENT:
                case ContentType.RELATE_SUPPLIER:
                case ContentType.PRODUCT:
                case ContentType.PROGRAM:
                case ContentType.TALENT:
                case ContentType.BUSINESS_OPPORTUNITY:
                case ContentType.CRM_CONTACT:
                case ContentType.RELATE_ORDER:
                case ContentType.RELATE_PURCHASE:
                case ContentType.OUT_STORE:
                    if (isJSONObject(value)) {
                        String objectTitlesStr = JSONObject.parseObject(value).getString("objectTitles");
                        if (StringUtils.isBlank(objectTitlesStr)) {
                            return null;
                        }
                        if (isJSONArray(objectTitlesStr)) {
                            List<String> objectTitles = JSONObject.parseArray(objectTitlesStr, String.class);
                            if (objectTitles == null || objectTitles.isEmpty()) {
                                return null;
                            }
                            return String.join("||", objectTitles);
                        }
                    }
                    return value;
                case ContentType.MULTI_CHOSEN:
                case ContentType.GROUP_MULTI_CHOSEN:
                    if (isJSONArray(value)) {
                        List<String> multiChosen = JSONObject.parseArray(value, String.class);
                        if (multiChosen == null || multiChosen.isEmpty()) {
                            return null;
                        }
                        return String.join("||", multiChosen);
                    }
                    return value;
                case ContentType.USER:
                case ContentType.DEPARTMENT:
                    if (isJSONObject(value)) {
                        JSONObject relation = JSONObject.parseObject(value);
                        if (relation == null || relation.isEmpty()) {
                            return null;
                        }
                        String nameListStr = relation.getString("nameList");
                        if (isJSONArray(nameListStr)) {
                            List<String> nameList = JSONObject.parseArray(nameListStr, String.class);
                            if (nameList == null || nameList.isEmpty()) {
                                return null;
                            }
                            return String.join("||", nameList);
                        }
                        if (StringUtils.isBlank(nameListStr)) {
                            return null;
                        }
                        return nameListStr.replace(",", "||");
                    }
                    return value;
                default:
                    return value;
            }
        } catch (Exception e) {
            //防止某些乱七八糟的值把程序搞崩了
            logger.error(e.getMessage(), e);
            return value;
        }
    }

    public static String getDateValue(String value, Integer type) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            switch (type) {
                case ContentType.DATE:
                    if (isJSONArray(value)) {
                        return null;
                    }
                    return value;
                case ContentType.TIME:
                    return value;
                default:
                    return null;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static String getFloatValue(String value, Integer type) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            switch (type) {
                case ContentType.MONEY:
                    if (isJSONArray(value)) {
                        return null;
                    }
                    return parseFloatValue(value);
                case ContentType.TOTAL_MONEY:
                case ContentType.NUMBER:
                    return parseFloatValue(value);
                default:
                    return null;
            }
        } catch (Exception e) {
            logger.warn("errorDate:{}", value);
            logger.warn(e.getMessage());
            return null;
        }
    }

    public static String parseFloatValue(String value) {
        try {
            BigDecimal decimal = new BigDecimal(value);
            return decimal.setScale(6, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString(); //只保留6位小数，四舍五入
        } catch (Exception e) {
            logger.error("errorNum:{}", value);
            logger.error(e.getMessage(), e);
            return null;
        }
    }


    public static boolean needSyncComponentToEs(String value, Integer type) {
        try {
            if (StringUtils.isBlank(value) || Utils.isNull(type)) {
                return false;
            }
            //员工、部门组件
            if (Constants.nameListTypes.contains(type)) {
                if ("-1".equals(value)) {
                    return false;
                }
                if (isJSONObject(value)) {
                    String nameListStr = JSONObject.parseObject(value).getString("nameList");
                    if (StringUtils.isBlank(nameListStr)) {
                        return false;
                    }
                    if (isJSONArray(nameListStr)) {
                        List<String> nameList = JSONObject.parseArray(nameListStr, String.class);
                        if (nameList == null || nameList.isEmpty()) {
                            return false;
                        }
                    }
                    return true;
                }
            }
            //关联组件
            if (Constants.objectTitleTypes.contains(type)) {
                if ("-1".equals(value)) {
                    return false;
                }
                if (isJSONObject(value)) {
                    String v = JSONObject.parseObject(value).getString("objectTitles");
                    if (StringUtils.isBlank(v)) {
                        return false;
                    }
                    if (isJSONArray(v)) {
                        List<String> objectTitles = JSONObject.parseArray(v, String.class);
                        if (objectTitles == null || objectTitles.isEmpty()) {
                            return false;
                        }
                    }
                    return true;
                }
            }
            //多选
            if (Objects.equals(ContentType.MULTI_CHOSEN, type) || Objects.equals(ContentType.GROUP_MULTI_CHOSEN, type)) {
                if (isJSONArray(value)) {
                    List<String> multiChosen = JSONObject.parseArray(value, String.class);
                    if (multiChosen == null || multiChosen.isEmpty()) {
                        return false;
                    }
                }
            }
            //地点
            if (Objects.equals(ContentType.GROUP_LOCATION, type) || Objects.equals(ContentType.LOCATION, type)) {
                if (isJSONObject(value)) {
                    JSONObject location = JSONObject.parseObject(value);
                    if (StringUtils.isBlank(location.getString("location"))) {
                        return false;
                    }
                }
                if (isJSONArray(value)) {
                    List<String> locations = JSONObject.parseArray(value, String.class);
                    if (locations == null || locations.isEmpty()) {
                        return false;
                    }
                }
            }
            //报销中三个特殊组件
            if (Objects.equals(ContentType.SINGLE_LINE, type) || Objects.equals(ContentType.DATE, type) || Objects.equals(ContentType.MONEY, type)) {
                if (isJSONArray(value)) {
                    List<String> list = JSONObject.parseArray(value, String.class);
                    if (list == null || list.isEmpty()) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return true;
        }
    }

    public static boolean isJSONArray(String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        try {
            JSONObject.parseArray(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isJSONObject(String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        try {
            JSONObject.parseObject(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //需要汇总的组件
    public static List<ComponentEsEntity> needSummaryComponent(List<ComponentEsEntity> components, Integer defaultType) {
        List<ComponentEsEntity> summaryComponentList = new ArrayList<>();
        if (Utils.isEmpty(components)) {
            return null;
        }
        for (ComponentEsEntity component : components) {
            if (StringUtils.isBlank(component.getValue())) {
                continue;
            }
            Integer type = component.getType();
            if (Objects.equals(type, ContentType.MONEY) || (Objects.equals(type, ContentType.TOTAL_MONEY))) {
                if (Objects.equals(defaultType, 1) && Objects.equals(component.getNum(), 2)) {
                    //过滤掉报销模板中的报销金额组件，报销模板的汇总数据在sys_approval_expense中
                    continue;
                }
                ComponentEsEntity needSummaryComponent = new ComponentEsEntity();
                needSummaryComponent.setId(component.getId());
                needSummaryComponent.setNum(component.getNum());
                needSummaryComponent.setValue(ComponentUtils.formatEsComponentValue(component.getValue(), type));
                needSummaryComponent.setType(type);
                needSummaryComponent.setUnique_id(component.getUnique_id());
                summaryComponentList.add(needSummaryComponent);
            }
        }
        if (summaryComponentList.isEmpty()) {
            return null;
        } else {
            return summaryComponentList;
        }
    }

}
