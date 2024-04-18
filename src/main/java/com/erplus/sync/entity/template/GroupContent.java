package com.erplus.sync.entity.template;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Data
public class GroupContent {

	private Integer id;

	private Integer companyId;

	private Integer groupId;

	private Integer sort;

	private List<Component> value;

	private Integer requestId;

	private Integer contentNum;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public List<Component> getValue() {
		return value;
	}

	public void setValue(List<Component> value) {
		this.value = value;
	}

	public void setValue(String value) {
		if (StringUtils.isNotBlank(value)) {
			this.value = JSONObject.parseArray(value, Component.class);
		}
	}
}
