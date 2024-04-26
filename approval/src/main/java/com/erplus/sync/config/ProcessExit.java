package com.erplus.sync.config;

import com.erplus.sync.utils.JschSessionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
@Slf4j
public class ProcessExit{

    @PreDestroy
    public void preDestroy() {
        JschSessionUtils.closeSession();
    }

}
