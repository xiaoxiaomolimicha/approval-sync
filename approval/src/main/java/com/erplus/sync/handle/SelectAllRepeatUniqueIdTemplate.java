package com.erplus.sync.handle;

import com.alibaba.fastjson.JSONObject;
import com.erplus.sync.dao.TemplateDao;
import com.erplus.sync.dao.impl.TemplateDaoImpl;
import com.erplus.sync.entity.ContentType;
import com.erplus.sync.entity.template.SimpleTemplate;
import com.erplus.sync.entity.template.TemplateComponent;
import com.erplus.sync.utils.JschSessionUtils;
import com.erplus.sync.utils.MysqlConnectionUtils;
import com.erplus.sync.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class SelectAllRepeatUniqueIdTemplate {


    //查所有重复uniqueId的模板
    @Test
    public void selectAllRepeatUniqueIdTemplate() {
        try {
            Connection connection = MysqlConnectionUtils.getMysqlConnection();
            TemplateDao templateDao = new TemplateDaoImpl(connection);
            List<Integer> templateIds = new ArrayList<>();
            List<Integer> ancestorIds = new ArrayList<>();
            Set<Integer> companyIds = new HashSet<>();
            List<SimpleTemplate> simpleTemplates = templateDao.selectAllCompanyTemplate();
            for (SimpleTemplate simpleTemplate : simpleTemplates) {
                List<TemplateComponent> templateComponentList = simpleTemplate.getTemplateComponentList();
                Map<Integer, List<TemplateComponent>> uniqueIdMap = templateComponentList.stream().collect(Collectors.groupingBy(TemplateComponent::getUniqueId));
                for (Integer uniqueId : uniqueIdMap.keySet()) {
                    List<TemplateComponent> templateComponents = uniqueIdMap.get(uniqueId);
                    if (templateComponents.stream().allMatch(templateComponent -> templateComponent.getType() == 17)) {
                        continue;
                    }
                    if (templateComponents.size() > 1) {
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
            log.info("templateIdSize:{}", templateIdsStrList.size());
            String templateIdsStr = String.join(",", templateIdsStrList);
            log.info("companyIds:{}", companyIds.size());
            log.info("templateIds:{}", templateIdsStr);
            log.info("ancestorIds:{}", String.join(",", ancestorIdStr));
            log.info("companyIds:{}", companyIds);
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(Files.newOutputStream(new File("/Users/macos/Desktop/重复的模板templateIds.txt").toPath()), StandardCharsets.UTF_8))) {
                writer.write(templateIdsStr);
                writer.newLine(); // 换行
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                log.info("写入文件时发生错误: " + e.getMessage());
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        } finally {
            MysqlConnectionUtils.closeConnection();
            JschSessionUtils.closeSession(MysqlConnectionUtils.MYSQL_LOCAL_PORT);
        }
    }



}
