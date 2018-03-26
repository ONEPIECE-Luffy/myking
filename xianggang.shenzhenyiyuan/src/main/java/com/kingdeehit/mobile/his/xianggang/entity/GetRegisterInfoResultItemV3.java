package com.kingdeehit.mobile.his.xianggang.entity;

import javax.xml.bind.annotation.XmlElement;

import com.thoughtworks.xstream.annotations.XStreamAlias;
/**
 * @author chenly
 *
 */
@XStreamAlias("orderInfo")
public class GetRegisterInfoResultItemV3 {
	//是否已取号：（当处理结果代码为0时，该字段必输）
	//0：未取号
	//1：已取号
	//2：已退费(已取号)
	//3: 已就诊
	//4：未付费取消
	//5: 已付费已取消
	//6: 允许预约报到
	@XmlElement(name="status", required=true, nillable=false)
	private String status="";

	//是否可以取消，0-不可以，1-可以，2-未定义，移动平台可根据自己的规则控制。
	@XmlElement(name="isCancelabe", required=true, nillable=false)
	private String isCancelabe="";

	//移动订单号
	@XmlElement(name="orderId", required=true, nillable=false)
	private String orderId="";

	//HIS预约订单号
	@XmlElement(name="bookingNo", required=false, nillable=false)
	private String bookingNo;

	//就诊流水号
	@XmlElement(name="clinicSeq", required=true, nillable=false)
	private String clinicSeq="";


	//科室代码
	@XmlElement(name="deptId", required=true, nillable=false)
	private String deptId="";


	//科室名称
	@XmlElement(name="deptName", required=true, nillable=false)
	private String deptName="";


	//接诊医生代码
	@XmlElement(name="doctorId", required=false, nillable=false)
	private String doctorId;

	//接诊医生姓名
	@XmlElement(name="doctorName", required=false, nillable=false)
	private String doctorName;

	//排队序号
	@XmlElement(name="queueNo", required=true, nillable=false)
	private String queueNo;

	//前面就诊人数，如果患者已就诊，则设为-1
	@XmlElement(name="waitingCount", required=false, nillable=false)
	private String waitingCount;

	//预计候诊等待时间(分钟)例:1患者就诊设置3分分钟，10个患者预计候诊30分钟
	@XmlElement(name="waitingTime", required=false, nillable=false)
	private String waitingTime;


	//取号时间格式： YYYY-MM-DD HI24:MI:SS
	@XmlElement(name="infoTime", required=false, nillable=false)
	private String infoTime;


	//就诊时间YYYY-MM-DD HI24:MI:SS
	@XmlElement(name="visitTime", required=false, nillable=false)
	private String visitTime;

	//挂号来源
	@XmlElement(name="orderType", required=true, nillable=false)
	private String orderType;

	//备注
	@XmlElement(name="remark", required=false, nillable=false)
	private String remark;

	@XmlElement(name="healthCardNo", required=false, nillable=false)
	private String healthCardNo;

	@XmlElement(name="patientId", required=false, nillable=false)
	private String patientId;

	@XmlElement(name="patientName", required=false, nillable=false)
	private String patientName;
	
	/**
	 * 是否允许支付
	 */
	@XmlElement(name="isPayment", required=false, nillable=false)
	private String isPayment;
	
	/**
	 * 支付状态0-未支付  1-已支付  2-已退费
	 */
	@XmlElement(name="paySatus", required=false, nillable=false)
	private String paySatus;
	
	/**
	 * 是否为医保结算，0否， 1是；默认为0
	 */
	@XmlElement(name="isInsuran", required=false, nillable=false)
	private String isInsuran; 
	
	/**
	 * 医保预结算参数（目前仅支持线上医保结算）
	 */
	@XmlElement(name="medicareSettleLogId", required=false, nillable=false)
	private String medicareSettleLogId; 
	
	/**
	 * 医保挂号的个人现金支付金额(单位“分”)
	 */
	@XmlElement(name="cashFee", required=false, nillable=false)
	private String cashFee; 
	
	/**
	 * 医保挂号的个人医保账户金额(单位“分”)
	 */
	@XmlElement(name="insuranFee", required=false, nillable=false)
	private String insuranFee;
	
	/**
	 * 是否允许选择服务对象  0-不允许，1-允许
	 * 默认值为0该参数只针对第三方渠道的挂号订单，在支付前选择服务对象。
	 */
	private String isSelectSvObject;
	
	private String	regDate;//挂号日期
	private String	shiftCode;
	private String	shiftName;
	private String	startTime;
	private String	endTime;
	private String	svObjectId;
	private String	svObjectName;
	private String registerType;
	private String orderTypeName;
	private String orderTime;
	private String payStatus;
	private String regFee;
	private String treatFee;
	private String yhFee;
	private String payFee;

	

	private String	hospitalId;
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIsCancelabe() {
		return isCancelabe;
	}

	public void setIsCancelabe(String isCancelabe) {
		this.isCancelabe = isCancelabe;
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

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getQueueNo() {
		return queueNo;
	}

	public void setQueueNo(String queueNo) {
		this.queueNo = queueNo;
	}

	public String getWaitingCount() {
		return waitingCount;
	}

	public void setWaitingCount(String waitingCount) {
		this.waitingCount = waitingCount;
	}

	public String getWaitingTime() {
		return waitingTime;
	}

	public void setWaitingTime(String waitingTime) {
		this.waitingTime = waitingTime;
	}

	public String getInfoTime() {
		return infoTime;
	}

	public void setInfoTime(String infoTime) {
		this.infoTime = infoTime;
	}

	public String getVisitTime() {
		return visitTime;
	}

	public void setVisitTime(String visitTime) {
		this.visitTime = visitTime;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

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

	public String getIsPayment() {
		return isPayment;
	}

	public void setIsPayment(String isPayment) {
		this.isPayment = isPayment;
	}

	public String getRegisterType() {
		return registerType;
	}

	public void setRegisterType(String registerType) {
		this.registerType = registerType;
	}

	public String getOrderTypeName() {
		return orderTypeName;
	}

	public void setOrderTypeName(String orderTypeName) {
		this.orderTypeName = orderTypeName;
	}

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public String getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}

	public String getRegFee() {
		return regFee;
	}

	public void setRegFee(String regFee) {
		this.regFee = regFee;
	}

	public String getTreatFee() {
		return treatFee;
	}

	public void setTreatFee(String treatFee) {
		this.treatFee = treatFee;
	}

	public String getYhFee() {
		return yhFee;
	}

	public void setYhFee(String yhFee) {
		this.yhFee = yhFee;
	}

	public String getPayFee() {
		return payFee;
	}

	public void setPayFee(String payFee) {
		this.payFee = payFee;
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

	public String getShiftName() {
		return shiftName;
	}

	public void setShiftName(String shiftName) {
		this.shiftName = shiftName;
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

	public String getSvObjectId() {
		return svObjectId;
	}

	public void setSvObjectId(String svObjectId) {
		this.svObjectId = svObjectId;
	}

	public String getSvObjectName() {
		return svObjectName;
	}

	public void setSvObjectName(String svObjectName) {
		this.svObjectName = svObjectName;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getIsSelectSvObject() {
		return isSelectSvObject;
	}

	public void setIsSelectSvObject(String isSelectSvObject) {
		this.isSelectSvObject = isSelectSvObject;
	}
}
