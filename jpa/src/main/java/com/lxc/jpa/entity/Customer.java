package com.lxc.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 实体类 1.主要建立实体类和数据表之间的映射关系：
 * 
 * @Entity 指明当前类为实体类
 * @Table 指定实体类和哪个数据表建立映射关系
 * 
 *        2.建立实体类成员变量和数据表字段之间的映射关系
 * @Id 声明当前成员变量对应数据表中的主键
 * @GeneratedValue 指定主键的生成策略
 * @column 指明当前成员变量具体和数据表中哪个字段建立映射关系 以上注解都来自javax.persistence包
 */

/**
 * @author liuxiancang
 *
 */
@Entity
@Table(name = "cst_customer")
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cust_id")
	private Long custId;

	@Column(name = "cust_name")
	private String custName;

	@Column(name = "cust_source")
	private String custSource;

	@Column(name = "cust_industry")
	private String custIndustry;

	@Column(name = "cust_level")
	private String custLevel;

	@Column(name = "cust_address")
	private String custAddress;

	@Column(name = "cust_phone")
	private String custPhone;

	public Long getCustId() {
		return custId;
	}

	public void setCustId(Long custId) {
		this.custId = custId;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCustSource() {
		return custSource;
	}

	public void setCustSource(String custSource) {
		this.custSource = custSource;
	}

	public String getCustIndustry() {
		return custIndustry;
	}

	public void setCustIndustry(String custIndustry) {
		this.custIndustry = custIndustry;
	}

	public String getCustLevel() {
		return custLevel;
	}

	public void setCustLevel(String custLevel) {
		this.custLevel = custLevel;
	}

	public String getCustAddress() {
		return custAddress;
	}

	public void setCustAddress(String custAddress) {
		this.custAddress = custAddress;
	}

	public String getCustPhone() {
		return custPhone;
	}

	public void setCustPhone(String custPhone) {
		this.custPhone = custPhone;
	}

	@Override
	public String toString() {
		return "Customer [custId=" + custId + ", custName=" + custName + ", custSource=" + custSource
				+ ", custIndustry=" + custIndustry + ", custLevel=" + custLevel + ", custAddress=" + custAddress
				+ ", custPhone=" + custPhone + "]";
	}

}
