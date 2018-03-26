package com.kingdeehit.mobile.his.xianggang.entity.table;

/**
 * 预约挂号锁号信息
 * @author tangfulin
 * 
 */

public class ChannelAppointmentLockInfo{

	private String orderId;
	private String bookingNo;		//his预约号(预约号源平台的流水号)
	private String hisOrderNo;		//his流水号(his系统中的流水号)
	private String clinicSeq;		//医保流水号
	private String startTime;   //入院时间
	private String regDate;
	private String shiftCode;
	private String scheduleId;
	private String svObjectId;
	private String patientId;
	private String patientName;
	private String hospitalId;
	private String doctorId;
	private String healthCardNo;
	private String endTime;		//出院时间
	private String cardNo;		//身份证
	private String deptId;
	private String deptName;
	private String doctorName;
	private String cancelSeq;	//医保退费流水号
	private String calcelBill;	//医保退费单据号
	private String _id;
	
	
	
	public String getHisOrderNo() {
		return hisOrderNo;
	}
	public void setHisOrderNo(String hisOrderNo) {
		this.hisOrderNo = hisOrderNo;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
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
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public String getDoctorName() {
		return doctorName;
	}
	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}
	public String getCancelSeq() {
		return cancelSeq;
	}
	public void setCancelSeq(String cancelSeq) {
		this.cancelSeq = cancelSeq;
	}
	public String getCalcelBill() {
		return calcelBill;
	}
	public void setCalcelBill(String calcelBill) {
		this.calcelBill = calcelBill;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getRegDate() {
		return regDate;
	}
	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}
	public String getShiftCode() {
		return shiftCode;
	}
	public void setShiftCode(String shiftCode) {
		this.shiftCode = shiftCode;
	}
	public String getScheduleId() {
		return scheduleId;
	}
	public void setScheduleId(String scheduleId) {
		this.scheduleId = scheduleId;
	}
	public String getSvObjectId() {
		return svObjectId;
	}
	public void setSvObjectId(String svObjectId) {
		this.svObjectId = svObjectId;
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
	public String getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}
	public String getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}
	public String getHealthCardNo() {
		return healthCardNo;
	}
	public void setHealthCardNo(String healthCardNo) {
		this.healthCardNo = healthCardNo;
	}
	
}
