package com.erplus.sync.handle;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erplus.sync.entity.Constants;
import com.erplus.sync.entity.RequestFlow;
import com.erplus.sync.mapper.RequestFlowMapper;
import com.erplus.sync.mapper.TemplateMapper;
import com.erplus.sync.mapper.RequestContentMapper;
import com.erplus.sync.entity.RequestContent;
import com.erplus.sync.entity.template.SimpleTemplate;
import com.erplus.sync.entity.template.TemplateComponent;
import com.erplus.sync.mybatis.MybatisManager;
import com.erplus.sync.utils.JschSessionUtils;
import com.erplus.sync.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class UpdateSingleRangeTime {

    @Test
    public void updateSingleRangeTime() {
        try {
            TemplateMapper templateDao = MybatisManager.getMapper(TemplateMapper.class);
            RequestContentMapper requestContentMapper = MybatisManager.getMapper(RequestContentMapper.class);
            List<Integer> templateIds = Arrays.asList(82840);
            for (Integer templateId : templateIds) {
                SimpleTemplate simpleTemplate = templateDao.selectById(templateId);
                List<TemplateComponent> newTemplateComponentList = new ArrayList<>();
                List<TemplateComponent> templateComponentList = simpleTemplate.getTemplateComponentList();
                boolean addNum = false;
                for (TemplateComponent templateComponent : templateComponentList) {
                    TemplateComponent one = JSONObject.parseObject(JSONObject.toJSONString(templateComponent), TemplateComponent.class);
                    if (addNum) {
                        one.setNum(one.getNum() + 1);
                    }
                    newTemplateComponentList.add(one);
                    if (templateComponent.getType() == 17) {
                        TemplateComponent two = JSONObject.parseObject(JSONObject.toJSONString(templateComponent), TemplateComponent.class);
                        two.setNum(two.getNum() + 1);
                        newTemplateComponentList.add(two);
                        addNum = true;
                    }
                }

                simpleTemplate.setTemplateComponents(JSONObject.toJSONString(newTemplateComponentList));
                templateDao.updateById(simpleTemplate);

                LambdaQueryWrapper<RequestContent> contentWrapper = new LambdaQueryWrapper<>();
                contentWrapper.eq(RequestContent::getTemplateId, templateId);
                List<RequestContent> requestContents = requestContentMapper.selectList(contentWrapper);
                Map<Integer, List<RequestContent>> requestIdMap = requestContents.stream().collect(Collectors.groupingBy(RequestContent::getRequestId));
                log.info("requestIdMpaSize:{}", requestIdMap.size());

                for (Integer requestId : requestIdMap.keySet()) {
                    List<RequestContent> sameRequestIds = requestIdMap.get(requestId);
                    Map<Integer, List<RequestContent>> sameUniqueMap = sameRequestIds.stream().collect(Collectors.groupingBy(RequestContent::getUniqueId));
                    boolean notHandle = true;
                    for (TemplateComponent templateComponent : newTemplateComponentList) {
                        List<RequestContent> findSameUniqueComponent = sameUniqueMap.get(templateComponent.getUniqueId());
                        if (Utils.isEmpty(findSameUniqueComponent)) {
                            continue;
                        }

                        if (templateComponent.getType() == 17) {
                            if (notHandle) {
                                notHandle = false;
                                findSameUniqueComponent.sort(Comparator.comparingInt(RequestContent::getComponentNum));
                                if (findSameUniqueComponent.size() != 1) {
                                    RequestContent requestContent2 = findSameUniqueComponent.get(1);
                                    requestContent2.setComponentNum(templateComponent.getUniqueId() + 1);
                                    for (RequestContent requestContent : findSameUniqueComponent) {
                                        requestContentMapper.updateById(requestContent);
                                    }
                                }
                            }
                        } else {
                            RequestContent requestContent = findSameUniqueComponent.get(0);
                            int originNum = requestContent.getComponentNum();
                            requestContent.setComponentNum(templateComponent.getNum());
                            if (originNum != requestContent.getComponentNum()) {
                                log.info("originNum:{}", originNum);
                                log.info("nowComponent:{}", JSONObject.toJSONString(requestContent));
                            }
                            requestContentMapper.updateById(requestContent);
                        }
                    }
                }
            }
            MybatisManager.getSqlSession().commit();
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        } finally {
            JschSessionUtils.closeAll();
        }
    }

    @Test
    public void find() {
        SqlSession sqlSession = MybatisManager.getSqlSession();
        try {
            TemplateMapper templateDao = MybatisManager.getMapper(TemplateMapper.class);
            LambdaQueryWrapper<SimpleTemplate> wrapper = new LambdaQueryWrapper<>();
            List<Integer> templateIds = new ArrayList<>();
            List<Integer> ancestorIds = new ArrayList<>();
            List<Integer> companyIds = new ArrayList<>();
            Set<Integer> defaultTypes = new HashSet<>();
            wrapper.gt(SimpleTemplate::getAncestorId, 0);
            wrapper.gt(SimpleTemplate::getCompanyId, 0);
            List<SimpleTemplate> simpleTemplates = templateDao.selectList(wrapper);
            for (SimpleTemplate simpleTemplate : simpleTemplates) {
                List<TemplateComponent> templateComponentList = simpleTemplate.getTemplateComponentList();
                if (Utils.isEmpty(templateComponentList)) {
                    log.error("有模板不存在组件！");
                    continue;
                }
                Map<Integer, List<TemplateComponent>> typeMpa = templateComponentList.stream().collect(Collectors.groupingBy(TemplateComponent::getType));
                List<TemplateComponent> templateComponents = typeMpa.get(17);
                if (Utils.isEmpty(templateComponents)) {
                    continue;
                }
                int size = templateComponents.size() / 2;
                for (int i = 1; i <= size; i++) {
                    int beforeIndex = i * 2 - 2;
                    int afterIndex = i * 2 - 1;
                    TemplateComponent before = templateComponents.get(beforeIndex);
                    TemplateComponent after = templateComponents.get(afterIndex);
                    Integer num1 = before.getNum();
                    Integer num2 = after.getNum();
                    if (num1 + 1 != num2) {
                        log.error("前一个是时间段，但是后一个不是！");
                        templateIds.add(simpleTemplate.getTemplateId());
                        ancestorIds.add(simpleTemplate.getAncestorId());
                        companyIds.add(simpleTemplate.getCompanyId());
                        defaultTypes.add(simpleTemplate.getDefaultType());
                    }
//                    if ((simpleTemplate.getDefaultType() == 9 || simpleTemplate.getDefaultType() == 8) && num1 == 1 && num2 == 2) {
//                        continue;
//                    }
                    if (!Objects.equals(before.getUniqueId(), after.getUniqueId())) {
                        log.error("同时间段，但是UniqueId不相同");
                        Integer companyId = simpleTemplate.getCompanyId();
                        templateIds.add(simpleTemplate.getTemplateId());
                        ancestorIds.add(simpleTemplate.getAncestorId());
                        companyIds.add(companyId);
                        defaultTypes.add(simpleTemplate.getDefaultType());
                        after.setUniqueId(before.getUniqueId());
                    }
                }

            }


//            List<Integer> sameContentIds = new ArrayList<>();
//            List<Integer> sameNumContentIds = new ArrayList<>();
//            List<Integer> needDeleteIds = new ArrayList<>();
            Map<Integer, SimpleTemplate> templateIdMap = simpleTemplates.stream().collect(Collectors.toMap(SimpleTemplate::getTemplateId, o -> o, (a, b) -> a));
            for (Integer templateId : templateIds) {
                RequestContentMapper contentMapper = MybatisManager.getMapper(RequestContentMapper.class);
                LambdaQueryWrapper<RequestContent> contentWrapper = new LambdaQueryWrapper<>();
                contentWrapper.eq(RequestContent::getTemplateId, templateId);
                List<RequestContent> requestContents = contentMapper.selectList(contentWrapper);
                Map<Integer, List<RequestContent>> requestIdMap = requestContents.stream().collect(Collectors.groupingBy(RequestContent::getRequestId));

//                for (Integer requestId : requestIdMap.keySet()) {
//                    List<RequestContent> sameRequestIdContentList = requestIdMap.get(requestId);
//                    Map<Integer, List<RequestContent> > numMap = sameRequestIdContentList.stream().collect(Collectors.groupingBy(RequestContent::getComponentNum));
//                    for (Integer num : numMap.keySet()) {
//                        List<RequestContent> findSameNumComponentList = numMap.get(num);
//                        if (findSameNumComponentList.size() > 1) {
//                            if (findSameNumComponentList.stream().allMatch(o -> Constants.objectTitleTypes.contains(o.getContentType()))) {
//                                continue;
//                            }
//                            RequestContent first = findSameNumComponentList.get(0);
//                            String content = first.getContent();
//                            Integer contentType = first.getContentType();
//                            Integer componentNum = first.getComponentNum();
//                            for (int i = 1; i < findSameNumComponentList.size(); i++) {
//                                RequestContent other = findSameNumComponentList.get(i);
//                                if (!(Objects.equals(other.getContent(), content) && Objects.equals(contentType, other.getContentType()) && Objects.equals(componentNum, other.getComponentNum()))) {
//                                    log.error("组件数据出现问题，出现了同num但是不是相同的数据！");
//                                    sameNumContentIds.add(other.getRequestId());
//                                } else {
//                                    sameContentIds.add(other.getRequestId());
//                                    needDeleteIds.add(other.getId());
//                                    log.error("出现了重复数据！！！");
//                                }
//                            }
//                        }
//                    }
//                }

                SimpleTemplate simpleTemplate = templateIdMap.get(templateId);
                List<TemplateComponent> templateComponentList = simpleTemplate.getTemplateComponentList();
                Map<Integer, List<TemplateComponent>> uniqueIdMap = templateComponentList.stream().collect(Collectors.groupingBy(TemplateComponent::getUniqueId));
                for (Integer uniqueId : uniqueIdMap.keySet()) {
                    List<TemplateComponent> templateComponents = uniqueIdMap.get(uniqueId);
                    if (templateComponents.size() > 1 && templateComponents.stream().allMatch(o -> o.getType() == 17)) {
                        for (TemplateComponent templateComponent : templateComponents) {
                            for (Integer requestId : requestIdMap.keySet()) {
                                List<RequestContent> sameRequestIdContentList = requestIdMap.get(requestId);
                                List<RequestContent> findSameNumComponentList = sameRequestIdContentList.stream().filter(o -> Objects.equals(templateComponent.getNum(), o.getComponentNum())).collect(Collectors.toList());
                                if (Utils.isEmpty(findSameNumComponentList)) {
                                    continue;
                                }

                                RequestContent first = findSameNumComponentList.get(0);
                                if (findSameNumComponentList.size() > 1) {
                                    String content = first.getContent();
                                    Integer contentType = first.getContentType();
                                    Integer componentNum = first.getComponentNum();
                                    if (findSameNumComponentList.stream().allMatch(o -> Objects.equals(o.getContent(), content) && Objects.equals(o.getContentType(), contentType) && Objects.equals(o.getComponentNum(), componentNum))) {
                                        log.error("出现了重复数据！！！");
                                    } else {
                                        log.error("组件数据出现问题，出现了同num但是不是相同的数据！");
                                        log.error("requestId:{}", first.getRequestId());
                                    }
                                    continue;
                                }
                                first.setUniqueId(templateComponent.getUniqueId());
                                contentMapper.updateById(first);
                            }
                        }
                    } else if (templateComponents.size() > 1) {
                        log.info("出现了同uniqueId，但不是时间段！！");
                        log.info("templateId:{}", templateId);
                        log.info("componentList:{}", JSONObject.toJSONString(templateComponentList));
                    }
                }
                simpleTemplate.setTemplateComponents(JSONObject.toJSONString(templateComponentList));
                templateDao.updateById(simpleTemplate);
                sqlSession.commit();
            }


            templateIds = templateIds.stream().sorted((o1, o2) -> o2 - o1).distinct().collect(Collectors.toList());
            log.info("templateIds:{}", templateIds);
            log.info("templateIdSize:{}", templateIds.size());
            ancestorIds = ancestorIds.stream().sorted((o1, o2) -> o2 - o1).distinct().collect(Collectors.toList());
            log.info("ancestorIds:{}", ancestorIds);
            log.info("ancestorIdSize:{}", ancestorIds.size());
            companyIds = companyIds.stream().sorted((o1, o2) -> o2 - o1).distinct().collect(Collectors.toList());
            log.info("companyIds:{}", companyIds);
            log.info("companyIds:{}", companyIds.size());
            log.info("defaultType:{}", defaultTypes);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JschSessionUtils.closeAll();
        }
    }

    @Test
    public void getRequestIds() {
        try {
            RequestFlowMapper requestFlowMapper = MybatisManager.getMapper(RequestFlowMapper.class);
            LambdaQueryWrapper<RequestFlow> wrapper = new LambdaQueryWrapper<>();
            List<Integer> templateIds = Stream.of(80787, 80788, 80789, 80790, 80793, 80795, 81391, 101898, 81396, 91480, 81407, 81408, 81409, 81775, 81776, 81779, 81796, 81798, 81799, 81802, 81804, 81806, 81903, 81904, 81906, 101281, 115892, 106407, 106408, 107198, 107513, 107515, 107559, 107560).collect(Collectors.toList());
            wrapper.in(RequestFlow::getTemplateId, templateIds);
            List<RequestFlow> list = requestFlowMapper.selectList(wrapper);
            for (RequestFlow requestFlow : list) {
                log.info("companyId:{}", requestFlow.getCompanyId());
            }
            log.info("requestIds:{}", list.stream().map(o -> String.valueOf(o.getRequestId())).collect(Collectors.toList()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JschSessionUtils.closeAll();
        }
    }

    @Test
    public void getTemplateIds() {
        try {
            TemplateMapper mapper = MybatisManager.getMapper(TemplateMapper.class);
            LambdaQueryWrapper<SimpleTemplate> wrapper = new LambdaQueryWrapper<>();
            List<Integer> ancestorIds = Stream.of(107559, 107513, 106407, 101281, 81903, 81802, 81798, 81779, 81775, 81409, 81408, 81407, 81396, 81391, 80795, 80793, 80790, 80788, 80787).collect(Collectors.toList());
            wrapper.in(SimpleTemplate::getAncestorId, ancestorIds);
            List<SimpleTemplate> simpleTemplates = mapper.selectList(wrapper);
            log.info("templateIds:{}", simpleTemplates.stream().map(SimpleTemplate::getTemplateId).collect(Collectors.toList()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JschSessionUtils.closeAll();
        }
    }

}
