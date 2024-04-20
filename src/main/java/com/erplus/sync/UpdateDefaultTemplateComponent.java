package com.erplus.sync;

import com.alibaba.fastjson.JSONObject;
import com.erplus.sync.dao.TemplateDao;
import com.erplus.sync.dao.impl.TemplateDaoImpl;
import com.erplus.sync.entity.ContentType;
import com.erplus.sync.entity.template.SimpleTemplate;
import com.erplus.sync.entity.template.TemplateComponent;
import com.erplus.sync.utils.JschSessionUtils;
import com.erplus.sync.utils.MysqlConnectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;

@Slf4j
public class UpdateDefaultTemplateComponent {

    @Test
    public void updateDefaultTemplateTemplateComponent() {
        try {
            Connection connection = MysqlConnectionUtils.getMysqlConnection();
            TemplateDao templateDao = new TemplateDaoImpl(connection);
            List<SimpleTemplate> simpleTemplates = templateDao.selectAllDefaultTemplate();
            for (SimpleTemplate simpleTemplate : simpleTemplates) {
                List<TemplateComponent> templateComponentList = simpleTemplate.getTemplateComponentList();
                for (TemplateComponent templateComponent : templateComponentList) {
                    if (templateComponent.getType() == ContentType.TIME_RANGE) {
                        Integer uniqueId = templateComponent.getUniqueId();
                        if (uniqueId == 2) {
                            templateComponent.setUniqueId(1);
                        }
                    }
                }
                String templateComponentJsonStr = JSONObject.toJSONString(templateComponentList);
                log.info(templateComponentJsonStr);
                templateDao.updateDefaultTemplateComponents(simpleTemplate.getTemplateId(), templateComponentJsonStr);
            }
        } catch (Throwable e) {
            MysqlConnectionUtils.closeConnection();
            JschSessionUtils.closeSession(MysqlConnectionUtils.MYSQL_LOCAL_PORT);
        }
    }

}
