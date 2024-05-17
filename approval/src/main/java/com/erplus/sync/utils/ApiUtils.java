package com.erplus.sync.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ApiUtils {
    public static final String GET_NOT_DEATH_COMPANY_IDS = "https://www.erplus.cn/appreq/getNotDeathCompanyIds";

    public static List<Integer> getNotDeathCompanyIds(String token) {
        try {
            String result = HttpUtilsWithoutLog.get(GET_NOT_DEATH_COMPANY_IDS, token);
            JSONObject jo = JSONObject.parseObject(result);
            return jo.getJSONArray("erpData").toJavaList(Integer.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ArrayList<>();
        }
    }

}
