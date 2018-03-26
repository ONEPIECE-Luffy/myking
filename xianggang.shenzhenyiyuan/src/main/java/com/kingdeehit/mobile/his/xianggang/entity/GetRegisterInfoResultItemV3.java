package com.kingdeehit.mobile.his.xianggang.entity;

import javax.xml.bind.annotation.XmlElement;

import com.thoughtworks.xstream.annotations.XStreamAlias;
/**
 * @author chenly
 *
 */
@XStreamAlias("orderInfo")
public class GetRegisterInfoResultItemV3 {
	//�Ƿ���ȡ�ţ���������������Ϊ0ʱ�����ֶα��䣩
	//0��δȡ��
	//1����ȡ��
	//2�����˷�(��ȡ��)
	//3: �Ѿ���
	//4��δ����ȡ��
	//5: �Ѹ�����ȡ��
	//6: ����ԤԼ����
	@XmlElement(name="status", required=true, nillable=false)
	private String status="";

	//�Ƿ����ȡ����0-�����ԣ�1-���ԣ�2-δ���壬�ƶ�ƽ̨�ɸ����Լ��Ĺ�����ơ�
	@XmlElement(name="isCancelabe", required=true, nillable=false)
	private String isCancelabe="";

	//�ƶ�������
	@XmlElement(name="orderId", required=true, nillable=false)
	private String orderId="";

	//HISԤԼ������
	@XmlElement(name="bookingNo", required=false, nillable=false)
	private String bookingNo;

	//������ˮ��
	@XmlElement(name="clinicSeq", required=true, nillable=false)
	private String clinicSeq="";


	//���Ҵ���
	@XmlElement(name="deptId", required=true, nillable=false)
	private String deptId="";


	//��������
	@XmlElement(name="deptName", required=true, nillable=false)
	private String deptName="";


	//����ҽ������
	@XmlElement(name="doctorId", required=false, nillable=false)
	private String doctorId;

	//����ҽ������
	@XmlElement(name="doctorName", required=false, nillable=false)
	private String doctorName;

	//�Ŷ����
	@XmlElement(name="queueNo", required=true, nillable=false)
	private String queueNo;

	//ǰ�������������������Ѿ������Ϊ-1
	@XmlElement(name="waitingCount", required=false, nillable=false)
	private String waitingCount;

	//Ԥ�ƺ���ȴ�ʱ��(����)��:1���߾�������3�ַ��ӣ�10������Ԥ�ƺ���30����
	@XmlElement(name="waitingTime", required=false, nillable=false)
	private String waitingTime;


	//ȡ��ʱ���ʽ�� YYYY-MM-DD HI24:MI:SS
	@XmlElement(name="infoTime", required=false, nillable=false)
	private String infoTime;


	//����ʱ��YYYY-MM-DD HI24:MI:SS
	@XmlElement(name="visitTime", required=false, nillable=false)
	private String visitTime;

	//�Һ���Դ
	@XmlElement(name="orderType", required=true, nillable=false)
	private String orderType;

	//��ע
	@XmlElement(name="remark", required=false, nillable=false)
	private String remark;

	@XmlElement(name="healthCardNo", required=false, nillable=false)
	private String healthCardNo;

	@XmlElement(name="patientId", required=false, nillable=false)
	private String patientId;

	@XmlElement(name="patientName", required=false, nillable=false)
	private String patientName;
	
	/**
	 * �Ƿ�����֧��
	 */
	@XmlElement(name="isPayment", required=false, nillable=false)
	private String isPayment;
	
	/**
	 * ֧��״̬0-δ֧��  1-��֧��  2-���˷�
	 */
	@XmlElement(name="paySatus", required=false, nillable=false)
	private String paySatus;
	
	/**
	 * �Ƿ�Ϊҽ�����㣬0�� 1�ǣ�Ĭ��Ϊ0
	 */
	@XmlElement(name="isInsuran", required=false, nillable=false)
	private String isInsuran; 
	
	/**
	 * ҽ��Ԥ���������Ŀǰ��֧������ҽ�����㣩
	 */
	@XmlElement(name="medicareSettleLogId", required=false, nillable=false)
	private String medicareSettleLogId; 
	
	/**
	 * ҽ���Һŵĸ����ֽ�֧�����(��λ���֡�)
	 */
	@XmlElement(name="cashFee", required=false, nillable=false)
	private String cashFee; 
	
	/**
	 * ҽ���Һŵĸ���ҽ���˻����(��λ���֡�)
	 */
	@XmlElement(name="insuranFee", required=false, nillable=false)
	private String insuranFee;
	
	/**
	 * �Ƿ�����ѡ��������  0-������1-����
	 * Ĭ��ֵΪ0�ò���ֻ��Ե����������ĹҺŶ�������֧��ǰѡ��������
	 */
	private String isSelectSvObject;
	
	private String	regDate;//�Һ�����
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
