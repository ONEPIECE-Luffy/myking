package com.kingdeehit.mobile.his.xianggang.entity.table;

import com.kingdeehit.mobile.his.entities.table.ChannelBaseInfo;

/**
 * 将锁号的id存入mongoDB
 * @author YJB
 * @date 2017年9月18日 下午2:47:31
 */
public class ChannelLockInfo extends ChannelBaseInfo {

	private String orderId;
	private String hospitalId;
	private String deptId;
	private String clinicUnitId;
	private String bookingNo;
	private String clinicSeq;
	private String healthCardNo;
	private String patientId;
	private String patientName;
	/*private String regDate;
	private String scheduleId;//排班id
	private String periodId;//分时id
	private String shiftCode;
	private String startTime;
	private String endTime;

	private String idCardNo;
	private String phone;

	private String orderTime;
	private String svObjectId;
	private String fee;
	private String treatfee;*/
	private String remark;
	
	
	
	public String getHealthCardNo() {
		return healthCardNo;
	}
	public void setHealthCardNo(String healthCardNo) {
		this.healthCardNo = healthCardNo;
	}
	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	public String getClinicUnitId() {
		return clinicUnitId;
	}
	public void setClinicUnitId(String clinicUnitId) {
		this.clinicUnitId = clinicUnitId;
	}
	public String getBookingNo() {
		return bookingNo;
	}
	public void setBookingNo(String bookingNo) {
		this.bookingNo = bookingNo;
	}
	public String getClinicSeq() {
		return clinicSeq;
	}
	public void setClinicSeq(String clinicSeq) {
		this.clinicSeq = clinicSeq;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
	
	
}
