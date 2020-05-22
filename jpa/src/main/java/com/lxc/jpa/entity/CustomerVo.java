package com.lxc.jpa.entity;

public class CustomerVo {

	private Long custId;

	private String custName;

	private String custSource;

	private String custIndustry;

	private String custLevel;

	private String custAddress;

	private String custPhone;

	private int age;

	public Long getCustId() {
		return custId;
	}

	public void setCustId(Long newCustId) {
		custId = newCustId;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String newCustName) {
		custName = newCustName;
	}

	public String getCustSource() {
		return custSource;
	}

	public void setCustSource(String newCustSource) {
		custSource = newCustSource;
	}

	public String getCustIndustry() {
		return custIndustry;
	}

	public void setCustIndustry(String newCustIndustry) {
		custIndustry = newCustIndustry;
	}

	public String getCustLevel() {
		return custLevel;
	}

	public void setCustLevel(String newCustLevel) {
		custLevel = newCustLevel;
	}

	public String getCustAddress() {
		return custAddress;
	}

	public void setCustAddress(String newCustAddress) {
		custAddress = newCustAddress;
	}

	public String getCustPhone() {
		return custPhone;
	}

	public void setCustPhone(String newCustPhone) {
		custPhone = newCustPhone;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int newAge) {
		age = newAge;
	}

}
