package com.erplus.sync.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erplus.sync.entity.RequestContent;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RequestContentMapper extends BaseMapper<RequestContent> {

    List<RequestContent> selectSingleTimeRangeContent();

}
