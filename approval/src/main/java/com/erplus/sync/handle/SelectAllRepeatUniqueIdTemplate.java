package com.erplus.sync.handle;

import com.alibaba.fastjson.JSONObject;
import com.erplus.sync.dao.TemplateDao;
import com.erplus.sync.dao.impl.TemplateDaoImpl;
import com.erplus.sync.entity.template.SimpleTemplate;
import com.erplus.sync.entity.template.TemplateComponent;
import com.erplus.sync.utils.JschSessionUtils;
import com.erplus.sync.utils.MysqlConnectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class SelectAllRepeatUniqueIdTemplate {

    /**
     * 查询所有重复uniqueId的模板
     */
    @Test
    public void selectAllRepeatUniqueIdRepeatTemplate() {
        try {
            Connection connection = MysqlConnectionUtils.getMysqlConnection();
            TemplateDao templateDao = new TemplateDaoImpl(connection);
            List<Integer> templateIds = new ArrayList<>();
            List<Integer> ancestorIds = new ArrayList<>();
            List<Integer> companyIds = new ArrayList<>();
            List<SimpleTemplate> simpleTemplates = templateDao.selectAllCompanyTemplate();
            for (SimpleTemplate simpleTemplate : simpleTemplates) {
                List<TemplateComponent> templateComponentList = simpleTemplate.getTemplateComponentList();
                Map<Integer, List<TemplateComponent>> uniqueIdMap = templateComponentList.stream().collect(Collectors.groupingBy(TemplateComponent::getUniqueId));
                for (Integer uniqueId : uniqueIdMap.keySet()) {
                    List<TemplateComponent> templateComponents = uniqueIdMap.get(uniqueId);
                    if (templateComponents.size() > 1) {
                        //时间段组件且数量为2是正常数据
                        if (templateComponents.stream().allMatch(templateComponent -> templateComponent.getType() == 17) && templateComponents.size() == 2) {
                            continue;
                        }
                        log.info("有问题的组件:{}", JSONObject.toJSONString(templateComponents));
                        log.info("templateId:{}, ", simpleTemplate.getTemplateId());
                        log.info("companyId:{}, ", simpleTemplate.getCompanyId());
                        templateIds.add(simpleTemplate.getTemplateId());
                        ancestorIds.add(simpleTemplate.getAncestorId());
                        companyIds.add(simpleTemplate.getCompanyId());
                    }
                }
            }
            List<String> templateIdsStrList = templateIds.stream().distinct().sorted((o1, o2) -> o2 - o1).map(String::valueOf).collect(Collectors.toList());
            List<String> ancestorIdStr = ancestorIds.stream().distinct().sorted((o1, o2) -> o2 - o1).map(String::valueOf).collect(Collectors.toList());
            List<String> companyIdsStr = companyIds.stream().distinct().sorted((o1, o2) -> o2 - o1).map(String::valueOf).collect(Collectors.toList());
            log.info("templateIds数量:{}", templateIdsStrList.size());
            log.info("templateIds:{}", String.join(",", templateIdsStrList));
            log.info("ancestorIds数量:{}", ancestorIdStr.size());
            log.info("ancestorIds:{}", String.join(",", ancestorIdStr));
            log.info("companyIds数量:{}", companyIdsStr.size());
            log.info("companyIds:{}", String.join(",", companyIdsStr));
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        } finally {
            MysqlConnectionUtils.closeConnection();
            JschSessionUtils.closeSession(MysqlConnectionUtils.MYSQL_LOCAL_PORT);
        }
    }

}
