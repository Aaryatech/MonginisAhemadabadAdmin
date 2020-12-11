package com.ats.adminpanel.model;

public class FrTcsBillReport {

	private int billNo;
	private String invoiceNo;
	private String billDate;
	private int frId;
	private float taxableAmt;
	private float totalTax;
	private float grandTotal;
	private float sgstSum;
	private float cgstSum;
	private float igstSum;
	private float vehNo;
	private String frName;
	private String frCode;
	
	public int getBillNo() {
		return billNo;
	}
	public void setBillNo(int billNo) {
		this.billNo = billNo;
	}
	public String getInvoiceNo() {
		return invoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public String getBillDate() {
		return billDate;
	}
	public void setBillDate(String billDate) {
		this.billDate = billDate;
	}
	public int getFrId() {
		return frId;
	}
	public void setFrId(int frId) {
		this.frId = frId;
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
	public float getGrandTotal() {
		return grandTotal;
	}
	public void setGrandTotal(float grandTotal) {
		this.grandTotal = grandTotal;
	}
	public float getSgstSum() {
		return sgstSum;
	}
	public void setSgstSum(float sgstSum) {
		this.sgstSum = sgstSum;
	}
	public float getCgstSum() {
		return cgstSum;
	}
	public void setCgstSum(float cgstSum) {
		this.cgstSum = cgstSum;
	}
	public float getIgstSum() {
		return igstSum;
	}
	public void setIgstSum(float igstSum) {
		this.igstSum = igstSum;
	}
	public float getVehNo() {
		return vehNo;
	}
	public void setVehNo(float vehNo) {
		this.vehNo = vehNo;
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
	@Override
	public String toString() {
		return "FrTcsBillReport [billNo=" + billNo + ", invoiceNo=" + invoiceNo + ", billDate=" + billDate + ", frId="
				+ frId + ", taxableAmt=" + taxableAmt + ", totalTax=" + totalTax + ", grandTotal=" + grandTotal
				+ ", sgstSum=" + sgstSum + ", cgstSum=" + cgstSum + ", igstSum=" + igstSum + ", vehNo=" + vehNo
				+ ", frName=" + frName + ", frCode=" + frCode + "]";
	}
	
	
	
}
