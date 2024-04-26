package com.erplus.sync.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erplus.sync.mapper.RequestContentMapper;
import com.erplus.sync.entity.ContentType;
import com.erplus.sync.entity.RequestContent;
import com.erplus.sync.service.RequestContentService;
import com.erplus.sync.utils.DateTimeHelper;
import com.erplus.sync.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RequestContentServiceImpl extends ServiceImpl<RequestContentMapper, RequestContent> implements RequestContentService {
    @Override
    public List<RequestContent> getRequestContentListByRequestId(Integer requestId) {
        LambdaQueryWrapper<RequestContent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RequestContent::getRequestId, requestId);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public JSONObject getSingleTimeRangeContent() {
        List<RequestContent> requestContents = baseMapper.selectSingleTimeRangeContent();
        Map<Integer, List<RequestContent>> requestIdRequestContentMap = requestContents.stream().collect(Collectors.groupingBy(RequestContent::getRequestId));
        List<Integer> requestIds = new ArrayList<>();
        List<Integer> ambiguityIds = new ArrayList<>();
        List<Integer> templateIds = new ArrayList<>();
        List<Integer> companyIds = new ArrayList<>();
        List<Integer> filter = Arrays.asList(14479647);
        for (Integer requestId : requestIdRequestContentMap.keySet()) {
            List<RequestContent> sameRequestContent = requestIdRequestContentMap.get(requestId);
            for (int i = 0; i < sameRequestContent.size(); i++) {
                RequestContent requestContent = sameRequestContent.get(i);
                Integer contentType = requestContent.getContentType();
                if (contentType == 17 && i == sameRequestContent.size() - 1) {
                    continue;
                }
                if (contentType == 17 && isTimeRange(requestContent, sameRequestContent.get(i + 1), requestIds, ambiguityIds)) {
                    log.info(JSONObject.toJSONString(sameRequestContent));
                }
            }
        }
        JSONObject result = new JSONObject();
        requestIds = requestIds.stream().filter(o -> !filter.contains(o)).sorted((o1, o2) -> o2 - o1).distinct().collect(Collectors.toList());
        for (Integer requestId : requestIds) {
            templateIds.add(requestIdRequestContentMap.get(requestId).get(0).getTemplateId());
            companyIds.add(requestIdRequestContentMap.get(requestId).get(0).getCompanyId());
        }
        result.put("requestIds", requestIds);
        result.put("ambiguityIds", ambiguityIds);
        log.info("requestIds:{}", JSONObject.toJSONString(requestIds));
        log.info("templateIds:{}", JSONObject.toJSONString(templateIds.stream().sorted((o1, o2) -> o2 - o1).distinct().collect(Collectors.toList())));
        log.info("companyIds:{}", JSONObject.toJSONString(companyIds.stream().sorted((o1, o2) -> o2 - o1).distinct().collect(Collectors.toList())));
        log.info("ambiguityIds:{}", JSONObject.toJSONString(ambiguityIds));
        return result;
    }

    private boolean isTimeRange(RequestContent before, RequestContent after, List<Integer> requestIds, List<Integer> ambiguityIds) {
        if (Utils.isNotNull(before.getUniqueId()) && Utils.isNotNull(after.getUniqueId()) && Objects.equals(before.getUniqueId(), after.getUniqueId())) {
            requestIds.add(before.getRequestId());
            return true;
        }
        Integer contentType = after.getContentType();
        if (contentType == ContentType.TIME || contentType == ContentType.DATE ) {
            ambiguityIds.add(before.getRequestId());
            return true;
        }

        if (StringUtils.isNotBlank(after.getContent()) && after.getContent().length() == 16) {
            Date parse = DateTimeHelper.parse(after.getContent(), DateTimeHelper.YEAR_MONTH_DAY_HOUR_MINUTE_PATTERN);
            if (parse != null) {
                requestIds.add(before.getRequestId());
                return true;
            }
        }
        return false;
    }


}
