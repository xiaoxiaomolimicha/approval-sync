package com.erplus.sync.handle;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

@Slf4j
public class UpdateSingleRangeTime {


    /**
     * 补全所有时间段组件的uniqueId
     */
    @Test
    public void updateRangeTimeUniqueId() {
        SqlSession sqlSession = null;
        try {
            sqlSession = MybatisManager.getSqlSession();
            RequestContentMapper contentMapper = MybatisManager.getMapper(RequestContentMapper.class);
            TemplateMapper templateMapper = MybatisManager.getMapper(TemplateMapper.class);
            LambdaQueryWrapper<RequestContent> contentWrapper = new LambdaQueryWrapper<>();
            LambdaQueryWrapper<SimpleTemplate> templateWrapper = new LambdaQueryWrapper<>();
            templateWrapper.gt(SimpleTemplate::getTemplateId, 0);
            contentWrapper.eq(RequestContent::getContentType, 17);
            List<RequestContent> requestContents = contentMapper.selectList(contentWrapper);
            List<SimpleTemplate> simpleTemplates = templateMapper.selectList(templateWrapper);
            Map<Integer, SimpleTemplate> templateIdMap = simpleTemplates.stream().collect(Collectors.toMap(SimpleTemplate::getTemplateId, o -> o));
            Set<Integer> companyIdSet = new HashSet<>();
            log.info("有问题的公司id:{}", companyIdSet);
            Map<Integer, List<RequestContent>> requestIdMap = requestContents.stream().collect(Collectors.groupingBy(RequestContent::getRequestId));
            for (Integer requestId : requestIdMap.keySet()) {
                List<RequestContent> timeRange = requestIdMap.get(requestId);
                if (Utils.isEmpty(timeRange)) {
                    log.error("怎么可能会空？");
                    continue;
                }
                if (timeRange.stream().allMatch(o -> Utils.isNotNull(o.getUniqueId()))) {
                    continue;
                }
                SimpleTemplate simpleTemplate = templateIdMap.get(timeRange.get(0).getTemplateId());
                Map<Integer, TemplateComponent> numToCompnentMap = simpleTemplate.getTemplateComponentList().stream().collect(Collectors.toMap(TemplateComponent::getNum, o -> o));
                for (RequestContent requestContent : timeRange) {
                    TemplateComponent templateComponent = numToCompnentMap.get(requestContent.getComponentNum());
                    requestContent.setUniqueId(templateComponent.getUniqueId());
                    contentMapper.updateById(requestContent);
                    sqlSession.commit();
                }
            }
        } catch (Exception e) {
            if (sqlSession != null) {
                sqlSession.rollback();
            }
            log.error(e.getMessage(), e);
        } finally {
            JschSessionUtils.closeAll();
        }
    }


}
