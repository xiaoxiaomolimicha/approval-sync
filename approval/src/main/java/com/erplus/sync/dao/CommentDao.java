package com.erplus.sync.dao;

import com.erplus.sync.entity.es.CommentEsEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface CommentDao {

    Map<Integer, List<CommentEsEntity>> selectOneCompanyAllComment(Integer companyId, String createTime) throws SQLException;

    Map<Integer, List<CommentEsEntity>> selectCommentByRequestIds(String requestIds) throws SQLException;

    List<CommentEsEntity> selectCommentByRequestId(Integer requestId) throws SQLException;
}
