package com.erplus.sync;

import com.alibaba.fastjson.JSONObject;
import com.erplus.sync.dao.TemplateDao;
import com.erplus.sync.dao.impl.TemplateDaoImpl;
import com.erplus.sync.utils.JschSessionUtils;
import com.erplus.sync.utils.ListHelper;
import com.erplus.sync.utils.MysqlConnectionUtils;
import com.erplus.sync.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class UpdateTemplateStatus {

    //把已停用的审批状态改成2
    @Test
    public void updateTemplateStatus() {
        try {
            List<Integer> companyIds = Arrays.asList(9500,9491,9453,9444,9423,9396,9380,9371,9361,9359,9358,9316,9315,9309,9308,9302,9247,9214,9182,9179,9167,9161,9151,9145,9144,9112,9090,9056,9035,9017);
            log.info("总公司数size:{}", companyIds.size());
            for (Integer companyId : companyIds) {
                Connection connection = MysqlConnectionUtils.getMysqlConnection();
                TemplateDao templateDao = new TemplateDaoImpl(connection);
                log.info("正在查询companyId:{}公司以前所有被停用的模板", companyId);
                List<Integer> templateIds = templateDao.selectOneCompanyOldStopTemplate(companyId);
                if (Utils.isEmpty(templateIds)) {
                    log.info("该公司不存在旧状态为0停用的模板");
                }
                log.info("该公司需要更新状态的size:{}", templateIds.size());
                log.info("该公司需要更新状态的templateIds:{}", JSONObject.toJSONString(templateIds));

                List<List<Integer>> divideList = ListHelper.divideList(templateIds, 100);
                log.info("该公司一共需要执行update更新的次数size:{}", divideList.size());
                for (int i = 0; i < divideList.size(); i++) {
                    log.info("==============正在更新第{}页==============", i + 1);
                    List<Integer> divide = divideList.get(i);
                    log.info("这是templateIds是:{}", JSONObject.toJSONString(divide));
                    templateDao.updateNewStopStatusByTemplateId(divide, companyId);
                }
                log.info("公司companyId:{}数据已处理完毕！！", companyId);
                log.info("==============开始处理下一个公司==============");
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        } finally {
            MysqlConnectionUtils.closeConnection();
            JschSessionUtils.closeSession(MysqlConnectionUtils.MYSQL_LOCAL_PORT);
        }

    }

}
