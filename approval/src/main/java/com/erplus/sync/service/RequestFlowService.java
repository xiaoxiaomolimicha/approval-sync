package com.erplus.sync.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.erplus.sync.entity.RequestFlow;

import java.util.List;

public interface RequestFlowService extends IService<RequestFlow> {

    List<RequestFlow> getRequestFLowListByIds(List<Integer> requestIds);

}
