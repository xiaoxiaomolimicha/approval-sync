package com.erplus.sync.entity.template;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

//组件
@Data
public class TemplateComponent {
    public static final int SHOW_TYPE_DESCRIBE = 1;
    public static final int SHOW_TYPE_TIME = 2;
    public static final int SHOW_TYPE_COST = 3;

//    @JSONField(serialize = false, deserialize = false)
    private Integer id;

    private Integer uniqueId;
    /**
     * 新增字段,排序组件显示序号
     */
    private Integer sort;

    private Integer num;
    private Integer type;
    private String name;
    private Integer need;
    private String value;
    private Integer dbkey;
    private Integer requestId;
    private String requestTime;
    private Integer templateId;
    @Deprecated
    private String inputType;

    private Integer companyId;

    private List<GroupContent> groupContents;
    /**
     * 组件集id
     */
    private Integer groupId;
    /**
     * 汇总公式  默认为f(x) = num(a) + num(b) + group(i,j) + ……
     */
    @Deprecated
    private String summaryFormula;
    /**
     * 公式 e.g.  "{\"isFormula\":1,\"formulaValue\":\"f(x) = #金额# ^ #数字#\",\"realFormula\":\"f(x) = var1 ^ var2\"}"
     */
    private String formula;
    /**
     * 多选项的可选值
     */
    private List<String> multiChosen;
    /**
     * 选项设置
     */
    private List<SelectionSetting> selectionSettings;
    /**
     * 列表展示位置: 1.原来的describeNum; 2.原来的timeNum; 3.原来的summaryNum
     */
    private Integer showType;
    /**
     * 组件集内展示组件的num
     */
    private Integer groupShowNum;

    private Integer isAnalysis;

    private Integer templateAncestorId;
    /**
     * 企业收支类型 1.收入; 2.支出
     */
    private Integer ledgerFlowType;

    private String groupLedgerFlowType;
    /**
     * 收支类型id
     */
    private String ledgerFlowTypeId;

    private MoneyAnalysis moneyAnalysis;

    private List<MoneyAnalysis> groupMoneyAnalyses;

    private Integer singleLimited;
    /**
     * 是否显示大小写金额
     */
    private Integer showAmountInWords;
    /**
     * 是否允许添加多个
     */
    private Integer isAddComponentGroup;

    private Integer isRelate;

    private String extra;

    private Integer edit;

    private Integer state;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUniqueId() {
        return uniqueId == null ? num : uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = (uniqueId == null ? num : uniqueId);
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getNum() {
        return num == null ? 0 : num;
    }

    public void setNum(Integer num) {
        this.num = num;
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
        return need == null ? 0 : need;
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

    public Integer getDbkey() {
        return dbkey;
    }

    public void setDbkey(Integer dbkey) {
        this.dbkey = dbkey;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    @Deprecated
    public String getInputType() {
        return inputType;
    }

    @Deprecated
    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public List<GroupContent> getGroupContents() {
        return groupContents;
    }

    public void setGroupContents(List<GroupContent> groupContents) {
        this.groupContents = groupContents;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getSummaryFormula() {
        return summaryFormula;
    }

    public void setSummaryFormula(String summaryFormula) {
        this.summaryFormula = summaryFormula;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public List<String> getMultiChosen() {
        return multiChosen;
    }

    public void setMultiChosen(List<String> multiChosen) {
        this.multiChosen = multiChosen;
    }

    public Integer getShowType() {
        return showType;
    }

    public void setShowType(Integer showType) {
        this.showType = showType;
    }

    public Integer getGroupShowNum() {
        return groupShowNum;
    }

    public void setGroupShowNum(Integer groupShowNum) {
        this.groupShowNum = groupShowNum;
    }

    public Integer getIsAnalysis() {
        return isAnalysis;
    }

    public void setIsAnalysis(Integer isAnalysis) {
        this.isAnalysis = isAnalysis;
    }

    public Integer getTemplateAncestorId() {
        return templateAncestorId;
    }

    public void setTemplateAncestorId(Integer templateAncestorId) {
        this.templateAncestorId = templateAncestorId;
    }

    public Integer getLedgerFlowType() {
        return ledgerFlowType;
    }

    public void setLedgerFlowType(Integer ledgerFlowType) {
        this.ledgerFlowType = ledgerFlowType;
    }

    public String getGroupLedgerFlowType() {
        return groupLedgerFlowType;
    }

    public void setGroupLedgerFlowType(String groupLedgerFlowType) {
        this.groupLedgerFlowType = groupLedgerFlowType;
    }

    public String getLedgerFlowTypeId() {
        return ledgerFlowTypeId;
    }

    public void setLedgerFlowTypeId(String ledgerFlowTypeId) {
        this.ledgerFlowTypeId = ledgerFlowTypeId;
    }

    public MoneyAnalysis getMoneyAnalysis() {
        return moneyAnalysis;
    }

    public void setMoneyAnalysis(MoneyAnalysis moneyAnalysis) {
        this.moneyAnalysis = moneyAnalysis;
    }

    public List<MoneyAnalysis> getGroupMoneyAnalyses() {
        return groupMoneyAnalyses;
    }

    public void setGroupMoneyAnalyses(List<MoneyAnalysis> groupMoneyAnalyses) {
        this.groupMoneyAnalyses = groupMoneyAnalyses;
    }

    public Integer getSingleLimited() {
        return singleLimited;
    }

    public void setSingleLimited(Integer singleLimited) {
        this.singleLimited = singleLimited;
    }

    public Integer getShowAmountInWords() {
        return showAmountInWords;
    }

    public void setShowAmountInWords(Integer showAmountInWords) {
        this.showAmountInWords = showAmountInWords;
    }

    public Integer getIsAddComponentGroup() {
        return isAddComponentGroup;
    }

    public void setIsAddComponentGroup(Integer isAddComponentGroup) {
        this.isAddComponentGroup = isAddComponentGroup == null ? 1 : isAddComponentGroup;
    }

    public Integer getIsRelate() {
        return isRelate;
    }

    public void setIsRelate(Integer isRelate) {
        this.isRelate = isRelate;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
