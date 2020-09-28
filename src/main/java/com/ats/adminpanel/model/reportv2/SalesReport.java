package com.ats.adminpanel.model.reportv2;

public class SalesReport {
	
	private int frId;
	private	String frName;
	private	String frCode;
	private	String frCity;
	
	private float saleValue;
	private float grnValue;
	private float gvnValue;	
	
	private float taxableAmt;
	private float totalTax;	
	
	private float grnTotalTax;
	private float grnTaxableAmt;	
	
	private float gvnTotalTax;
	private float gvnTaxableAmt;
	
	
	
	public int getFrId() {
		return frId;
	}
	public void setFrId(int frId) {
		this.frId = frId;
	}
	public String getFrName() {
		return frName;
	}
	public void setFrName(String frName) {
		this.frName = frName;
	}
	public String getFrCode() {
		return frCode;
	}
	public void setFrCode(String frCode) {
		this.frCode = frCode;
	}
	public String getFrCity() {
		return frCity;
	}
	public void setFrCity(String frCity) {
		this.frCity = frCity;
	}
	public float getSaleValue() {
		return saleValue;
	}
	public void setSaleValue(float saleValue) {
		this.saleValue = saleValue;
	}
	public float getGrnValue() {
		return grnValue;
	}
	public void setGrnValue(float grnValue) {
		this.grnValue = grnValue;
	}
	public float getGvnValue() {
		return gvnValue;
	}
	public void setGvnValue(float gvnValue) {
		this.gvnValue = gvnValue;
	}
	public float getTaxableAmt() {
		return taxableAmt;
	}
	public void setTaxableAmt(float taxableAmt) {
		this.taxableAmt = taxableAmt;
	}
	public float getTotalTax() {
		return totalTax;
	}
	public void setTotalTax(float totalTax) {
		this.totalTax = totalTax;
	}
	public float getGrnTotalTax() {
		return grnTotalTax;
	}
	public void setGrnTotalTax(float grnTotalTax) {
		this.grnTotalTax = grnTotalTax;
	}
	public float getGrnTaxableAmt() {
		return grnTaxableAmt;
	}
	public void setGrnTaxableAmt(float grnTaxableAmt) {
		this.grnTaxableAmt = grnTaxableAmt;
	}
	public float getGvnTotalTax() {
		return gvnTotalTax;
	}
	public void setGvnTotalTax(float gvnTotalTax) {
		this.gvnTotalTax = gvnTotalTax;
	}
	public float getGvnTaxableAmt() {
		return gvnTaxableAmt;
	}
	public void setGvnTaxableAmt(float gvnTaxableAmt) {
		this.gvnTaxableAmt = gvnTaxableAmt;
	}
	@Override
	public String toString() {
		return "SalesReport [frId=" + frId + ", frName=" + frName + ", frCode=" + frCode + ", frCity=" + frCity
				+ ", saleValue=" + saleValue + ", grnValue=" + grnValue + ", gvnValue=" + gvnValue + ", taxableAmt="
				+ taxableAmt + ", totalTax=" + totalTax + ", grnTotalTax=" + grnTotalTax + ", grnTaxableAmt="
				+ grnTaxableAmt + ", gvnTotalTax=" + gvnTotalTax + ", gvnTaxableAmt=" + gvnTaxableAmt + "]";
	}

}
