package com.erplus.sync.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erplus.sync.mapper.RequestContentMapper;
import com.erplus.sync.entity.RequestContent;
import com.erplus.sync.service.RequestContentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class RequestContentServiceImpl extends ServiceImpl<RequestContentMapper, RequestContent> implements RequestContentService {
}
