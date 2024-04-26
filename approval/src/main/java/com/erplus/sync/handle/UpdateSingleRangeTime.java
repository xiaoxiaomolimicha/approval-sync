package com.erplus.sync.handle;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
    public void updateUniqueId() {
        try {
            RequestContentMapper requestContentMapper = MybatisManager.getMapper(RequestContentMapper.class);
            LambdaQueryWrapper<RequestContent> wrapper = new LambdaQueryWrapper<>();
            wrapper.gt(RequestContent::getRequestId, 5664178);
            List<RequestContent> requestContents = requestContentMapper.selectList(wrapper);
            for (RequestContent content : requestContents) {
                content.setUniqueId(content.getComponentNum());
            }
            for (RequestContent requestContent : requestContents) {
                requestContentMapper.updateById(requestContent);
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
        try {
            TemplateMapper templateDao = MybatisManager.getMapper(TemplateMapper.class);
            LambdaQueryWrapper<SimpleTemplate> wrapper = new LambdaQueryWrapper<>();
            List<Integer> templateIds = new ArrayList<>();
            List<Integer> ancestorIds = new ArrayList<>();
            List<Integer> companyIds = new ArrayList<>();
            wrapper.gt(SimpleTemplate::getAncestorId, 0);
            wrapper.gt(SimpleTemplate::getCompanyId, 0);
            List<SimpleTemplate> simpleTemplates = templateDao.selectList(wrapper);
            for (SimpleTemplate simpleTemplate : simpleTemplates) {
                List<TemplateComponent> templateComponentList = simpleTemplate.getTemplateComponentList();
                Map<Integer, List<TemplateComponent>> typeMap = templateComponentList.stream().collect(Collectors.groupingBy(TemplateComponent::getType));
                List<TemplateComponent> templateComponents = typeMap.get(17);
                if (Utils.isEmpty(templateComponents)) {
                    continue;
                }
                if (templateComponents.size() % 2 != 0 ) {
                    templateIds.add(simpleTemplate.getTemplateId());
                    ancestorIds.add(simpleTemplate.getAncestorId());
                    companyIds.add(simpleTemplate.getCompanyId());
                }
            }
            log.info("templateIds:{}", templateIds.stream().sorted((o1, o2) -> o2 - o1).distinct().collect(Collectors.toList()));
            log.info("templateIdSize:{}", templateIds.stream().distinct().count());
            log.info("ancestorIds:{}", ancestorIds.stream().sorted((o1, o2) -> o2 - o1).distinct().collect(Collectors.toList()));
            log.info("ancestorIdSize:{}", ancestorIds.stream().distinct().count());
            log.info("companyIds:{}", companyIds.stream().sorted((o1, o2) -> o2 - o1).distinct().collect(Collectors.toList()));
            log.info("companyIds:{}", companyIds.stream().distinct().count());
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
            List<Integer> templateIds = Stream.of(121079, 121074, 121068, 89103, 86427, 85519, 83469, 83316, 82023, 81870, 81687, 81685, 81678, 81567, 81358, 80697, 80490, 80447).collect(Collectors.toList());
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

}
