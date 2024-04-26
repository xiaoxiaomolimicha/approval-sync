package com.erplus.sync.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erplus.sync.entity.RequestContent;

import java.util.List;

public interface RequestContentService extends IService<RequestContent> {
    List<RequestContent> getRequestContentListByRequestId(Integer requestId);

    JSONObject getSingleTimeRangeContent();

}
