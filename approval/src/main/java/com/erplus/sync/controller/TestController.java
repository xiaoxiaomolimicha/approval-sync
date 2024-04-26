package com.erplus.sync.controller;

import com.alibaba.fastjson.JSONObject;
import com.erplus.sync.service.RequestContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private RequestContentService requestContentService;

    @GetMapping("/get/{requestId}")
    public String test(@PathVariable Integer requestId) {
        String jsonString = JSONObject.toJSONString(requestContentService.getRequestContentListByRequestId(requestId));
        return jsonString;
    }

    @GetMapping("/getSingle")
    public String getSingle() {
        return JSONObject.toJSONString(requestContentService.getSingleTimeRangeContent());
    }

}
