package com.easycodebox.auth.core.pojo.sys;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.easycodebox.auth.core.util.mybatis.GeneratedValue;
import com.easycodebox.auth.core.util.mybatis.GeneratorEnum;
import com.easycodebox.common.enums.entity.OpenClose;
import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.jdbc.entity.AbstractOperateEntity;

/**
 * 合作商 - 合作商调用接口配置
 * @author WangXiaoJin
 *
 */
@Entity
@Table(name="sys_partner")
public class Partner extends AbstractOperateEntity {

	private static final long serialVersionUID = 5454155825314635342L;
	
	/**
	 * 主键
	 */
	@Id
	@GeneratedValue(GeneratorEnum.PARTNER_ID)
	private String id;
	
	/**
	 * 合作商名
	 */
	private String name;
	
	/**
	 * 密钥 - 加密解密数据的密钥值
	 */
	private String partnerKey;
	
	/**
	 * 网址
	 */
	private String website;
	
	/**
	 * 状态
	 */
	private OpenClose status;
	
	/**
	 * 是否删除
	 */
	private YesNo deleted;
	
	/**
	 * 排序值
	 */
	private Integer sort;
	
	/**
	 * 合同
	 */
	private String contract;
	
	/**
	 * 备注
	 */
	private String remark;
	

	public Partner(){
	
	}

	public Partner(String id){
		this.id = id;
	}
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPartnerKey() {
		return partnerKey;
	}
	
	public void setPartnerKey(String partnerKey) {
		this.partnerKey = partnerKey;
	}
	
	public String getWebsite() {
		return website;
	}
	
	public void setWebsite(String website) {
		this.website = website;
	}
	
	public OpenClose getStatus() {
		return status;
	}
	
	public void setStatus(OpenClose status) {
		this.status = status;
	}
	
	public YesNo getDeleted() {
		return deleted;
	}

	public void setDeleted(YesNo deleted) {
		this.deleted = deleted;
	}

	public Integer getSort() {
		return sort;
	}
	
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
	public String getContract() {
		return contract;
	}
	
	public void setContract(String contract) {
		this.contract = contract;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}

