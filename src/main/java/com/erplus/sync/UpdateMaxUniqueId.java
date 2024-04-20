package com.erplus.sync;

import com.erplus.sync.dao.TemplateDao;
import com.erplus.sync.dao.impl.TemplateDaoImpl;
import com.erplus.sync.entity.template.SimpleTemplate;
import com.erplus.sync.entity.template.TemplateComponent;
import com.erplus.sync.utils.JschSessionUtils;
import com.erplus.sync.utils.MysqlConnectionUtils;
import com.erplus.sync.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class UpdateMaxUniqueId {

    @Test
    public void completeMaxUniqueId() {
        try {
            Connection connection = MysqlConnectionUtils.getMysqlConnection();
            TemplateDao templateDao = new TemplateDaoImpl(connection);
            List<Integer> companyIds = templateDao.selectAllCompanyIdInTemplate();
            log.info("总共需要更新的公司size:{}", companyIds.size());
            for (Integer companyId : companyIds) {
                log.info("正在查询companyId:{}公司所有的模板组件", companyId);
                List<SimpleTemplate> maxUniqueIdEntities = templateDao.selectOneCompanyAllTemplateComponents(companyId);
                if (Utils.isEmpty(maxUniqueIdEntities)) {
                    log.error("该公司没有任何审批模板信息！！！");
                    continue;
                }
                Map<Integer, List<SimpleTemplate>> ancestorIdMap = maxUniqueIdEntities.stream().collect(Collectors.groupingBy(SimpleTemplate::getAncestorId));
                for (Integer ancestorId : ancestorIdMap.keySet()) {
                    List<SimpleTemplate> sameAncestorIdEntities = ancestorIdMap.get(ancestorId);
                    int maxUniqueId = 0;
                    for (SimpleTemplate simpleTemplate : sameAncestorIdEntities) {
                        int templateMaxUniqueId = Utils.isNull(simpleTemplate.getMaxUniqueId()) ? 0 : simpleTemplate.getMaxUniqueId();
                        if (templateMaxUniqueId > maxUniqueId) {
                            maxUniqueId = templateMaxUniqueId;
                        }
                        List<TemplateComponent> templateComponentList = simpleTemplate.getTemplateComponentList();
                        if (Utils.isEmpty(templateComponentList)) {
                            log.error("当前模板templateId:{}没有组件！！！", simpleTemplate.getTemplateId());
                            continue;
                        }
                        for (TemplateComponent templateComponent : templateComponentList) {
                            Integer uniqueId = templateComponent.getUniqueId();
                            if (Utils.isNull(uniqueId)) {
                                log.error("组件:{}的uniqueId为空", templateComponent.getName());
                                continue;
                            }
                            if (uniqueId > maxUniqueId) {
                                maxUniqueId = uniqueId;
                            }
                        }
//                        if (templateComponentList.size() > maxUniqueId) {
//                            log.error("组件的数量大于maxUniqueId！！！templateId:{}，之前maxUniqueId:{}，现在maxUniqueId:{}", maxUniqueIdEntity.getTemplateId(), maxUniqueId, templateComponentList.size());
//                            maxUniqueId = templateComponentList.size();
//                        }
                    }
                    log.info("模板ancestorId:{}的maxUniqueId是:{}", ancestorId, maxUniqueId);
                    templateDao.updateMaxUniqueIdByAncestorId(ancestorId, maxUniqueId);
                    Thread.sleep(100);
                }
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        } finally {
            MysqlConnectionUtils.closeConnection();
            JschSessionUtils.closeSession(MysqlConnectionUtils.MYSQL_LOCAL_PORT);
        }
    }

}
