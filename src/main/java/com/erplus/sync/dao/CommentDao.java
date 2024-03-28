package com.erplus.sync.dao;

import com.erplus.sync.entity.es.CommentEsEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface CommentDao {

    Map<Integer, List<CommentEsEntity>> selectOneCompanyAllComment(Integer companyId) throws SQLException;
}
