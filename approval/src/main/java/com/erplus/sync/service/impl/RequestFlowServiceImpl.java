package com.erplus.sync.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erplus.sync.dao.RequestFlowDao;
import com.erplus.sync.entity.RequestFlow;
import com.erplus.sync.service.RequestFlowService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestFlowServiceImpl extends ServiceImpl<RequestFlowDao, RequestFlow>  implements RequestFlowService {


    @Override
    public List<RequestFlow> getRequestFLowListByIds(List<Integer> requestIds) {
        return getBaseMapper().selectBatchIds(requestIds);
    }
}
