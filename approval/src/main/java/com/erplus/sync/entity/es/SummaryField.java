package com.erplus.sync.entity.es;

import lombok.Data;

import java.util.List;

@Data
public class SummaryField {

    private List<ComponentEsEntity> component;

    private List<LeaveOvertimeOutdoorEsEntity> overtime;

    private List<ExpenseEsEntity> expense;

    private List<LeaveOvertimeOutdoorEsEntity> leave;

    private List<LeaveOvertimeOutdoorEsEntity> outdoor;
}
