package com.erplus.sync.entity;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Constants {


    //无需同步的组件类型
    public static Set<Integer> noNeedSyncTypes = Stream.of(ContentType.ARTICLE, ContentType.ATTACHMENT, ContentType.COMPONENT_GROUP, ContentType.NEW_ADD_CLOCK_CARD, ContentType.TIME_RANGE)
            .collect(Collectors.toSet());
    public static Set<Integer> nameListTypes = Stream.of(ContentType.USER, ContentType.DEPARTMENT).collect(Collectors.toSet());
    public static Set<Integer> objectTitleTypes = Stream.of(ContentType.RELATED_TASK, ContentType.RELATED_APPROVAL, ContentType.RELATED_CLIENT, ContentType.RELATE_SUPPLIER,
            ContentType.PRODUCT, ContentType.PROGRAM, ContentType.TALENT, ContentType.BUSINESS_OPPORTUNITY, ContentType.CRM_CONTACT, ContentType.RELATE_ORDER,
            ContentType.RELATE_PURCHASE, ContentType.OUT_STORE).collect(Collectors.toSet());

}
