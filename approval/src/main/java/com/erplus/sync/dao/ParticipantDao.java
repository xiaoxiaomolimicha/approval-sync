package com.erplus.sync.dao;


import com.erplus.sync.entity.es.ParticipantEsEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ParticipantDao {

    List<ParticipantEsEntity> selectParticipantByRequestId(Integer requestId) throws SQLException;
    Map<Integer, List<ParticipantEsEntity>> selectOneCompanyAllParticipant(Integer companyId, String createTime) throws SQLException;

    Map<Integer, List<ParticipantEsEntity>> selectParticipantByRequestIds(String requestIds) throws SQLException;
}
