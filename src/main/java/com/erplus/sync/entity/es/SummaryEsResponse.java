package com.erplus.sync.entity.es;

import lombok.Data;

@Data
public class SummaryEsResponse {

    private SummaryField summaryField;

    private Integer requestId;
}
