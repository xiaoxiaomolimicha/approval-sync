package com.erplus.sync;


import com.alibaba.fastjson.JSONObject;
import com.erplus.sync.entity.RequestContent;
import com.erplus.sync.service.RequestContentService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class ApplicationTest {

    @Autowired
    private RequestContentService requestContentService;

    @Test
    public void test() {
        List<RequestContent> requestContentListByRequestId = requestContentService.getRequestContentListByRequestId(19820497);
        log.info(JSONObject.toJSONString(requestContentListByRequestId));
    }
}
