package com.kingdeehit.mobile.his.xianggang.entity.table;

public class QueueInfo {
	private String _id; 
	private String deptId; 
	private String deptName; 
	
	private String patientId;
	private String patientName;
	private String healthCardNo;
	private String visitIndicator;//�����ʶ��	A��һ�η��B�����η��C�ݲ�����������D�����ţ�E���к�
	private String queueName;//��������
	private String queueNo;//�ŶӺ�
	private String time;
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
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
	public String getHealthCardNo() {
		return healthCardNo;
	}
	public void setHealthCardNo(String healthCardNo) {
		this.healthCardNo = healthCardNo;
	}
	public String getVisitIndicator() {
		return visitIndicator;
	}
	public void setVisitIndicator(String visitIndicator) {
		this.visitIndicator = visitIndicator;
	}
	public String getQueueName() {
		return queueName;
	}
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	public String getQueueNo() {
		return queueNo;
	}
	public void setQueueNo(String queueNo) {
		this.queueNo = queueNo;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
}
