package com.erplus.sync.handle;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erplus.sync.dao.ComponentDao;
import com.erplus.sync.mapper.RequestContentMapper;
import com.erplus.sync.dao.TemplateDao;
import com.erplus.sync.dao.impl.ComponentImpl;
import com.erplus.sync.dao.impl.TemplateDaoImpl;
import com.erplus.sync.entity.RequestContent;
import com.erplus.sync.entity.template.SimpleTemplate;
import com.erplus.sync.entity.template.TemplateComponent;
import com.erplus.sync.mybatis.MybatisManager;
import com.erplus.sync.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class TestConnection {


    @Test
    public void test() {
        try {
            Connection connection = MysqlConnectionUtils.getMysqlConnection();
            ComponentDao componentDao = new ComponentImpl(connection);
            List<Integer> templateIds = Arrays.asList(165364,140385,138486,138327,136096,135410,135409,135405,130293,129605,128632,123228,122771,119897,118254,118248,1936);
            for (Integer templateId : templateIds) {
                List<TemplateComponent> templateComponents = componentDao.selectComponentByTemplateId(templateId);
                Map<Integer, List<TemplateComponent>> collect = templateComponents.stream().collect(Collectors.groupingBy(TemplateComponent::getRequestId));
                for (Integer requestId : collect.keySet()) {
                    List<TemplateComponent> sameRequestComponents = collect.get(requestId);
                    Map<Integer, List<TemplateComponent>> sameNumComponentMap = sameRequestComponents.stream().collect(Collectors.groupingBy(TemplateComponent::getUniqueId));
                    for (Integer uniqueId : sameNumComponentMap.keySet()) {
                        List<TemplateComponent> sameNumComponents = sameNumComponentMap.get(uniqueId);
                        if (sameNumComponents.size() > 1 ) {
                            TemplateComponent first = sameNumComponents.get(0);
                            TemplateComponent second = sameNumComponents.get(1);
                            if (first.getType() == 17) {
                                second.setType(17);
                                componentDao.updateNumTypeUniqueIdById(second.getNum(), second.getUniqueId(), second.getType(), second.getId());
//                            log.info("first:{}", JSONObject.toJSONString(first));
//                            log.info("second:{}", JSONObject.toJSONString(second));
                            } else {
                                log.info("其他组件出现了同uniqueId的情况！");
                                for (TemplateComponent sameNumComponent : sameNumComponents) {
                                    componentDao.updateNumTypeUniqueIdById(sameNumComponent.getNum(), sameNumComponent.getNum(),sameNumComponent.getType(), sameNumComponent.getId());
                                }
                                log.info(JSONObject.toJSONString(sameNumComponents));
                            }
//                        if (first.getType() == second.getType() && first.getUniqueId() == second.getUniqueId() && first.getNum() == second.getNum()) {
//                            log.info("second:{}", JSONObject.toJSONString(second));
//                            needDeleteIds.add(second.getId());
//                        }
                        }
                    }
                }
            }

//            String sql = "delete from request_content where Fid in (" + ListHelper.list2string(needDeleteIds) + ")";
//            log.info(sql);
//            PreparedStatement ps = connection.prepareStatement(sql);
//            ps.executeUpdate();

        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        } finally {
            MysqlConnectionUtils.closeConnection();
            JschSessionUtils.closeSession(MysqlConnectionUtils.MYSQL_LOCAL_PORT);
        }
    }

    @Test
    public void update() {
        try {
            Connection connection = MysqlConnectionUtils.getMysqlConnection();
            String sql = "update request_template set Ftemplate_component = '[{\"name\":\"关联任务\",\"need\":2,\"num\":1,\"type\":8,\"uniqueId\":2},{\"name\":\"关联客户\",\"need\":2,\"num\":2,\"type\":14,\"uniqueId\":2},{\"name\":\"关联审批\",\"need\":2,\"num\":3,\"type\":9,\"uniqueId\":3},{\"name\":\"日期\",\"need\":1,\"num\":4,\"type\":4,\"uniqueId\":4},{\"name\":\"加班开始时间\",\"need\":1,\"num\":5,\"type\":5,\"uniqueId\":5},{\"name\":\"加班结束时间\",\"need\":1,\"num\":6,\"type\":5,\"uniqueId\":6},{\"name\":\"加班地点\",\"need\":1,\"num\":7,\"type\":6,\"uniqueId\":7},{\"multiChosen\":[\"工作日\",\"法定假期\"],\"name\":\"加班类型\",\"need\":1,\"num\":8,\"type\":7,\"uniqueId\":8},{\"name\":\"附件（加班类型为法定假期的需要提供纸质审批文件）\",\"need\":0,\"num\":9,\"type\":11,\"uniqueId\":9}]' where Ftemplate_id = 143890;";
//            String sql = "update request_content set Fcontent = '' where Fid = 57188;";
            log.info(sql);
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.executeUpdate();
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        } finally {
            MysqlConnectionUtils.closeConnection();
            JschSessionUtils.closeSession(MysqlConnectionUtils.MYSQL_LOCAL_PORT);
        }
    }

    @Test
    public void test2() {
        try {
            Connection connection = MysqlConnectionUtils.getMysqlConnection();
            ComponentDao componentDao = new ComponentImpl(connection);
            TemplateDao templateDao = new TemplateDaoImpl(connection);
            SimpleTemplate simpleTemplate = templateDao.selectSimpleTemplateById(1936);
            List<TemplateComponent> templateComponentsList = simpleTemplate.getTemplateComponentList();
            templateComponentsList.sort(Comparator.comparingInt(TemplateComponent::getNum));
            log.info("处理前模板中的组件信息:");
            for (TemplateComponent templateComponent : templateComponentsList) {
                log.info("name:{}, num:{}, uniqueId:{}, type:{}", templateComponent.getName(), templateComponent.getNum(), templateComponent.getUniqueId(), templateComponent.getType());
            }

            Set<Integer> repeatNumSet = new HashSet<>();
            int startHandleNum = 0;
            int startUniqueId = 0;
            int startType = 0;
            for (TemplateComponent templateComponent : templateComponentsList) {
                Integer uniqueId = templateComponent.getUniqueId();
                Integer num = templateComponent.getNum();
                Integer type = templateComponent.getType();
                if (!repeatNumSet.contains(num)) {
                    repeatNumSet.add(num);
                } else {
                    startHandleNum = num;
                    startUniqueId = uniqueId;
                    startType = type;
                    break;
                }
            }

            log.info("开始处理的num:{}", startHandleNum);
            log.info("开始处理的uniqueId:{}", startUniqueId);
            log.info("开始处理的type:{}", startType);
            List<TemplateComponent> needModifyTypeTemplateComponent = new ArrayList<>();

            for (TemplateComponent templateComponent : templateComponentsList) {
                if (templateComponent.getNum() > startHandleNum || (templateComponent.getNum() == startHandleNum && templateComponent.getType() == startType && templateComponent.getUniqueId() == startUniqueId)) {
                    templateComponent.setNum(templateComponent.getNum()  + 1);
                    needModifyTypeTemplateComponent.add(templateComponent);
                }
            }

            log.info("处理前模板中的组件信息:");
            for (TemplateComponent templateComponent : templateComponentsList) {
                log.info("name:{}, num:{}, uniqueId:{}, type:{}", templateComponent.getName(), templateComponent.getNum(), templateComponent.getUniqueId(), templateComponent.getType());
            }

            log.info("需要修改num的组件:");
            for (TemplateComponent templateComponent : needModifyTypeTemplateComponent) {
                log.info("name:{}, num:{}, uniqueId:{}, type:{}", templateComponent.getName(), templateComponent.getNum(), templateComponent.getUniqueId(), templateComponent.getType());
            }


            List<TemplateComponent> componentValueList = componentDao.selectComponentByTemplateId(simpleTemplate.getTemplateId());
            Map<Integer, List<TemplateComponent>> sameUniqueIdComponentValueList = componentValueList.stream().collect(Collectors.groupingBy(TemplateComponent::getUniqueId));
            for (TemplateComponent templateComponent : needModifyTypeTemplateComponent) {

                List<TemplateComponent> templateComponents = sameUniqueIdComponentValueList.get(templateComponent.getUniqueId());
                if (Utils.isNull(templateComponents)) {
                    log.info("content表中没有uniqueId:{},type:{}的数据", templateComponent.getUniqueId(), templateComponent.getType());
                    continue;
                }
                log.info("现在num:{},uniqueId:{},type:{}", templateComponent.getNum(), templateComponent.getUniqueId(), templateComponent.getType());
                for (TemplateComponent component : templateComponents) {
                    log.info("需要修改组件的num:{},uniqueId:{},type:{}", component.getNum(), component.getUniqueId(), component.getType());
                }
            }


        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        } finally {
            MysqlConnectionUtils.closeConnection();
            JschSessionUtils.closeSession(MysqlConnectionUtils.MYSQL_LOCAL_PORT);
        }
    }

    @Test
    public void test3() throws Throwable {

        Connection connection = MysqlConnectionUtils.getMysqlConnection();
        try {
            connection.setAutoCommit(false);
            ComponentDao componentDao = new ComponentImpl(connection);
            TemplateDao templateDao = new TemplateDaoImpl(connection);
            List<Integer> templateIds = Arrays.asList(143890);
            for (Integer templateId : templateIds) {
                SimpleTemplate simpleTemplate = templateDao.selectSimpleTemplateById(templateId);
                List<TemplateComponent> templateComponentsList = simpleTemplate.getTemplateComponentList();
                templateComponentsList.sort(Comparator.comparingInt(TemplateComponent::getNum));
                Set<Integer> repeatNumSet = new HashSet<>();
                int startHandleNum = 0;
                int startUniqueId = 0;
                int startType = 0;
                for (TemplateComponent templateComponent : templateComponentsList) {
                    Integer uniqueId = templateComponent.getUniqueId();
                    Integer num = templateComponent.getNum();
                    Integer type = templateComponent.getType();
                    if (!repeatNumSet.contains(num)) {
                        repeatNumSet.add(num);
                    } else {
                        startHandleNum = num;
                        startUniqueId = uniqueId;
                        startType = type;
                        break;
                    }
                }

                log.info("开始处理的num:{}", startHandleNum);
                log.info("开始处理的uniqueId:{}", startUniqueId);
                log.info("开始处理的type:{}", startType);
                List<TemplateComponent> needModifyTypeTemplateComponent = new ArrayList<>();

                for (TemplateComponent templateComponent : templateComponentsList) {
                    if (templateComponent.getNum() > startHandleNum || (templateComponent.getNum() == startHandleNum && templateComponent.getType() == startType && templateComponent.getUniqueId() == startUniqueId)) {
                        templateComponent.setNum(templateComponent.getNum()  + 1);
                        needModifyTypeTemplateComponent.add(templateComponent);
                    }
                }

                String sql = "update request_template set Ftemplate_component = ? where Ftemplate_id = ?";
                try (PreparedStatement ps = connection.prepareStatement(sql)){
                    ps.setString(1, JSONObject.toJSONString(templateComponentsList));
                    ps.setInt(2, simpleTemplate.getTemplateId());
                    log.info(sql, JSONObject.toJSONString(templateComponentsList), simpleTemplate.getTemplateId());
                    ps.executeUpdate();
                }

                List<TemplateComponent> componentValueList = componentDao.selectComponentByTemplateId(simpleTemplate.getTemplateId());
                Map<Integer, List<TemplateComponent>> sameUniqueIdComponentValueList = componentValueList.stream().collect(Collectors.groupingBy(TemplateComponent::getUniqueId));
                for (TemplateComponent templateComponent : needModifyTypeTemplateComponent) {

                    List<TemplateComponent> templateComponents = sameUniqueIdComponentValueList.get(templateComponent.getUniqueId());
                    if (Utils.isNull(templateComponents)) {
                        log.info("content表中没有uniqueId:{},type:{}的数据", templateComponent.getUniqueId(), templateComponent.getType());
                        continue;
                    }
                    log.info("现在num:{},uniqueId:{},type:{}", templateComponent.getNum(), templateComponent.getUniqueId(), templateComponent.getType());
                    for (TemplateComponent component : templateComponents) {
                        log.info("需要修改组件的num:{},uniqueId:{},type:{}", component.getNum(), component.getUniqueId(), component.getType());
                        String updateNumSql = "update request_content set Fcomponent_num = ? where Fid = ?";
                        try (PreparedStatement ps = connection.prepareStatement(updateNumSql)){
                            ps.setInt(1, templateComponent.getNum());
                            ps.setInt(2, component.getId());
                            log.info(SQLLogger.logSQL(updateNumSql, templateComponent.getNum(), component.getId()));
                            ps.executeUpdate();
                        }
                    }
                }
            }

            connection.commit();

        } catch (Throwable e) {
            connection.rollback();
            log.error(e.getMessage(), e);
        } finally {
            MysqlConnectionUtils.closeConnection();
            JschSessionUtils.closeSession(MysqlConnectionUtils.MYSQL_LOCAL_PORT);
        }

    }

    @Test
    public void test4() {
        try {
            Connection connection = MysqlConnectionUtils.getMysqlConnection();
            ComponentDao componentDao = new ComponentImpl(connection);
            TemplateDao templateDao = new TemplateDaoImpl(connection);
            List<Integer> requestIds = new ArrayList<>();
            Set<Integer> companyIds = new HashSet<>();
            Set<Integer> haveRepeatTemplateId = new HashSet<>();
            List<Integer> templateIds = Arrays.asList(142673,121397,23556,23555,23554,23553,23552,23551,2513,2380,1956,796,681,680,679,678,677);
            for (Integer templateId : templateIds) {
                SimpleTemplate simpleTemplate = templateDao.selectSimpleTemplateById(templateId);
                List<TemplateComponent> templateComponentList = simpleTemplate.getTemplateComponentList();
                int num = 1;
                int uniqueId = 1;
                int uniqueIdFlag = 0;
                for (TemplateComponent templateComponent : templateComponentList) {
                    templateComponent.setNum(num);
                    templateComponent.setUniqueId(uniqueId);
                    if (templateComponent.getType() == 17) {
                        uniqueIdFlag++;
                        if (uniqueIdFlag == 2) {
                            uniqueId++;
                            uniqueIdFlag = 0;
                        }
                    } else {
                        uniqueId++;
                    }
                    num++;
                }

                String sql = "update request_template set Ftemplate_component = '" + JSONObject.toJSONString(templateComponentList ) + "' where Ftemplate_id = " + templateId;
                log.info(sql);
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.executeUpdate();

            }

        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            MysqlConnectionUtils.closeConnection();
            JschSessionUtils.closeSession();
        }
    }

    @Test
    public void updateTemplateIdAncestorIdCompanyId() {
        try {
            Connection connection = MysqlConnectionUtils.getMysqlConnection();
            String sql = "select Ftemplate_id, Fancestor_id, Fcompany_id from request_template where Ftemplate_id = Fancestor_id and Ftemplate_id < 0";
            List<SimpleTemplate> simpleTemplateList = new ArrayList<>();
            try (PreparedStatement ps = connection.prepareStatement(sql)){
                try (ResultSet rs = ps.executeQuery()){
                    while (rs.next()) {
                        SimpleTemplate simpleTemplate = new SimpleTemplate();
                        simpleTemplate.setTemplateId(rs.getInt(1));
                        simpleTemplate.setAncestorId(rs.getInt(2));
                        simpleTemplate.setCompanyId(rs.getInt(3));
                        simpleTemplateList.add(simpleTemplate);
                    }
                }
            }

            for (List<SimpleTemplate> simpleTemplates : ListHelper.divideList(simpleTemplateList, 300)) {
                String templateIds = simpleTemplates.stream().map(o -> String.valueOf(o.getTemplateId())).collect(Collectors.joining(","));
                log.info("templateIds:{}", templateIds);
                String updateSql = "update request_template set Ftemplate_id = -Ftemplate_id, Fancestor_id = -Fancestor_id, Fcompany_id = -Fcompany_id where Ftemplate_id in (" + templateIds + ");";
                log.info(updateSql);
                try (PreparedStatement ps = connection.prepareStatement(updateSql)){
                    ps.executeUpdate();
                }
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        } finally {
            MysqlConnectionUtils.closeConnection();
            JschSessionUtils.closeSession();
        }
    }

    @Test
    public void deleteData() {
        try {
            Connection connection = MysqlConnectionUtils.getMysqlConnection();
            List<Integer> requestIds = Stream.of(105228,105282,108499,113970,113974,114035,964297,964415,9453419,9898343,9898492,9898496,9898584,9898588,9898592,9898602,9898705,9931740,9931975,9944809,9956959,9957595,9957615,9958368,9958777,9959867,10042850,10264949,10264956,10277093,10277131,10277143,10304876,10304901,10304925,10304940,10306427,10306440,10349139,10349277,10349387,10894950,10895785,10864359,10864431,10864439,10864570,10865107,10865633,10865642,10866563,10903396,10866940,10867049,10867258,10875545,10875559,10867308,10867336,10867376,11598641,10867540,10867665,10867704,10867764,10867843,10867923,10867969,10903659,10868004,10958307,10958394,10868034,10868028,10868076,10868135,10868208,10868218,10868241,10866364,10868488,10892416,10892577,10895029,10895469,10915123,10915132,10916066,11127029,10899884,10901280,10916102,10928094,11525002,11591271,11592500,11636971,11598931,11918472,11918491,11918528,11918578,12143845,12143985,12160850,12273337,12275580,12276558,12276677,12276781,12384870,12384874,12909710,13613494,13613509,13614002,13616326,13621149,13611880,13597466,13597549,13597578,13597608,13597674,13597701,13597715,13597755,13597804,13597834,13597864,13597875,13613221,13613227,13613987,13614030,13614062,13614068,13628183,13631635,13631666,13616591,13622272,13622278,13734560,13713229,13713286,13714686,13714933,13739200,13665830,13674239,13675771,13675809,13678473,13678661,13678681,13678991,13679021,13679041,13705225,13705300,13705328,13662601,13984070).collect(Collectors.toList());
            List<Integer> templateIds = Stream.of(151284,151283,141992,141990,140589,140472,140417,140416,140415,140388,140386,140277,140258,140169,140168,140167,140166,140165,138550,136715,136714,136713,136260,136259,135678,135677,135169,135138,131131,126349,126281,126280,126279,125818,121805,121675,121546,121533,121522,121508,121480,121376,121362,121270,121266,121262,121258,121256,121255,121254,121253,121195,121193,121191,121190,121181,121166,121165,121163,121107,121095,121092,121091,121089,121075,121063,121051,121049,121047,121028,117025,113913,113130,113020,112904,111385,108329,23825,23821,6577,6026).collect(Collectors.toList());
            String requestIdsStr = ListHelper.list2string(requestIds);
            String templateIdsStr = ListHelper.list2string(templateIds);
            String sql = "delete from sys_approval_participant where request_id in (" + requestIdsStr + ")";
            try (PreparedStatement ps = connection.prepareStatement(sql)){
                ps.executeUpdate();
            }
            log.info("我执行完了");
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            MysqlConnectionUtils.closeConnection();
            JschSessionUtils.closeSession();
        }
    }

    @Test
    public void testMybatis() {
        try {
            RequestContentMapper requestContentMapper = MybatisManager.getMapper(RequestContentMapper.class);
            LambdaQueryWrapper<RequestContent> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RequestContent::getRequestId, 19280535);
            List<RequestContent> requestContents = requestContentMapper.selectList(wrapper);
            log.info(JSONObject.toJSONString(requestContents));
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        } finally {
            JschSessionUtils.closeAll();
        }
    }


}
