package com.erplus.sync.entity.template;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class Component implements Serializable {

    private Integer uniqueId;

    private Integer type;

    private String name;

    private Integer need;

    private String value;

    private Integer num;

    private Integer showAmountInWords;

    private List<String> singleChosen;

    private List<String> multipleChosen;

    private String ledgerFlowTypeId;

    private String formula;

    private List<SelectionSetting> selectionSettings;

    private BigDecimal amountTotal;


    public Component() {
    }

    public Integer getUniqueId() {
        return uniqueId == null ? num : uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = (uniqueId == null ? num : uniqueId);
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNeed() {
        return need;
    }

    public void setNeed(Integer need) {
        this.need = need;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getShowAmountInWords() {
        return showAmountInWords;
    }

    public void setShowAmountInWords(Integer showAmountInWords) {
        this.showAmountInWords = showAmountInWords;
    }

    public List<String> getSingleChosen() {
        return singleChosen;
    }

    public void setSingleChosen(List<String> singleChosen) {
        this.singleChosen = singleChosen;
    }

    public List<String> getMultipleChosen() {
        return multipleChosen;
    }

    public void setMultipleChosen(List<String> multipleChosen) {
        this.multipleChosen = multipleChosen;
    }

    public String getLedgerFlowTypeId() {
        return ledgerFlowTypeId;
    }

    public void setLedgerFlowTypeId(String ledgerFlowTypeId) {
        this.ledgerFlowTypeId = ledgerFlowTypeId;
    }
}
