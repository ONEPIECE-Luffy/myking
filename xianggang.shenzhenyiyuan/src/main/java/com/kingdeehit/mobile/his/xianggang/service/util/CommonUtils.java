package com.kingdeehit.mobile.his.xianggang.service.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bson.conversions.Bson;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.kingdeehit.mobile.his.entities.V3.result.appointment.DeptInfoV3;
import com.kingdeehit.mobile.his.utils.MongoDBHelper;
import com.kingdeehit.mobile.his.xianggang.constant.ParamConstants;
import com.kingdeehit.mobile.his.xianggang.entity.table.ChannelAppointmentLockInfo;
import com.kingdeehit.mobile.utils.DateUtils;
import com.kingdeehit.mobile.utils.StringUtil;
import com.kingdeehit.mobile.utils.UtilXml;
import com.mongodb.client.model.Filters;

/**
 * ͨ�ù�����
 * 
 * @author tangfulin
 *
 */
public class CommonUtils {

	private static Logger logger = Logger.getLogger(CommonUtils.class);

	// ����age<18
	public static String DEPT_CODE;
	// ����age>=18
	public static String DEPT_CODE2;
	// ����age>=10
	public static String DEPT_CODE3;
	//INSURANCEFLAG��0��ֻ���Էѣ�1��ֻ��ҽ����2�������Էѿ���ҽ��������Ҫ���ݸ��ֶ��ж�ĳ�������Ƿ�Ϊȫ�Էѻ���ȫҽ������
	private static final Map<String, String> DEPTINFO = new ConcurrentHashMap<String, String>();
	
	public static final void cacheDeptInfo(String deptId, String insuranceFlag){
		if(null == deptId) return;
		DEPTINFO.put(deptId, insuranceFlag);
	}
	
	public static final String getInsuranceflagByDeptId(String deptId){
		return DEPTINFO.get(deptId);
	}
	
	static {
		DEPT_CODE = ConfigUtils.getInstance().getDeptSmaller18();
		DEPT_CODE2 = ConfigUtils.getInstance().getDeptBigger18();
		DEPT_CODE3 = ConfigUtils.getInstance().getDeptBigger10();

	}

	/**
	 * Ԫ--->��
	 * 
	 * @param minute
	 * @return
	 */
	public static int convertUnitToMinute(String unit) {
		if (StringUtils.isBlank(unit) || "0".equals(unit) || "0.0".equals(unit)) {
			return 0;
		}
		Float fa = Float.parseFloat(unit) * 100;
		return fa.intValue();
	}

	/**
	 * ��--->Ԫ
	 * 
	 * @param minute
	 * @return
	 */
	public static double convertMinuteToUnit(String minute) {
		if (StringUtils.isBlank(minute) || "0".equals(minute)) {
			return 0;
		}
		return Double.parseDouble(minute) / 100;
	}

	/**
	 * ��ȡָ�����ڵ�������ֵ
	 * 
	 * @param date
	 * @return
	 */
	public static String getNoon(Date date) {
		if (date == null) {
			return "";
		}
		GregorianCalendar ca = new GregorianCalendar();
		ca.setTimeInMillis(date.getTime());
		int res = ca.get(GregorianCalendar.AM_PM);
		String noon = "";
		if (res == 0) {
			noon = "����";
		} else if (res == 1) {
			noon = "����";
		}
		return noon;
	}

	/**
	 * ����ָ�����ڸ�ʽ�ַ�����ȡ������ֵ
	 * 
	 * @param date
	 * @return
	 */
	public static String getNoon(String dateStr) {
		if (StringUtils.isBlank(dateStr)) {
			return "";
		}
		String tmp = dateStr.substring(11, 13);
		int time = Integer.parseInt(tmp);
		if (time >= 12) {
			return "����";
		} else {
			return "����";
		}
	}

	/**
	 * ��ȡָ�����ڸ�ʽ�ַ�����ʱ��ֵ
	 * 
	 * @param dateStr
	 * @return
	 */
	public static String getHourByString(String dateStr) {
		if (StringUtils.isBlank(dateStr)) {
			return "";
		}
		return dateStr.substring(14);
	}

	public static String getDateStrByPattern(String date) {
		if (StringUtils.isBlank(date) || date.length() < 10) {
			return date;
		}
		return date.substring(0, 10);
	}

	/**
	 * ��ȡ��׼���й�ʱ��,��ʽ��yyyy-MM-dd
	 *
	 * @param date
	 *            ��Ҫ��ʽ����ʱ�����
	 *
	 * @return �Ѹ�ʽ����ʱ��
	 */
	public static String getYMDTime(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(date);
	}

	/**
	 * ����ƽ̨hospitalId��ȡhisҽԺid
	 * 
	 * @return
	 */
	public static String getHospitalByBranchCode(String hospitalId) {
		if (StringUtils.isBlank(hospitalId)) {
			logger.info("����hospitalId=" + hospitalId + "Ϊ�գ�");
			return "";
		}
		if ("100099001".equals(hospitalId) || "100336001".equals(hospitalId)) {
			return "";
		} else {
			return hospitalId;
		}
	}

	/**
	 * ������Ϣ���ؽ��
	 * 
	 * @return
	 */
	public static String getErrorMsg() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><res><resultCode>-1</resultCode><resultDesc></resultDesc></res>";
	}

	/**
	 * ������Ϣ���ؽ��
	 * 
	 * @return
	 */
	public static String getErrorMsg(String msg) {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><res><resultCode>-1</resultCode><resultDesc>" + msg
				+ "</resultDesc></res>";
	}

	/**
	 * �ɹ���Ϣ���ؽ��
	 * 
	 * @return
	 */
	public static String getSuccessMsg() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><res><resultCode>0</resultCode><resultDesc>�ɹ�</resultDesc></res>";
	}

	/**
	 * ��ȡ��ǰʱ��ı�׼���й�ʱ��,��ʽ��yyyy-MM-dd HH:mm:ss
	 *
	 * @return �Ѹ�ʽ����ʱ��
	 */
	public static String getYYYYMMDDTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(new Date());
	}

	/**
	 * ��ȡ��ȡ��ʱ���ǰ����ʱ��
	 * 
	 * @return �Ѹ�ʽ��������
	 */
	public static String getBeforeHalfYearDate() {
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.add(Calendar.MONTH, -6);
		return getYMDTime(tomorrow.getTime());
	}

	/**
	 * his������ת��
	 * 
	 * @param code
	 * @return
	 */
	public static String covertNoonByCode(String code) {
		if (StringUtils.isBlank(code)) {
			return "";
		}
		if ("1".equals(code)) {
			return "����";
		} else if ("2".equals(code)) {
			return "����";
		} else if ("3".equals(code)) {
			return "����";
		}
		return "";
	}

	/**
	 * his�Ա����ת��
	 * 
	 * @param code
	 * @return
	 */
	public static String covertSexByCode(String code) {
		if (StringUtils.isBlank(code)) {
			return "";
		}
		if ("M".equals(code)) { // ��
			return "0";
		} else if ("F".equals(code)) { // Ů
			return "1";
		} else {
			return "2";
		}
	}

	/**
	 * ��ȡ���ڼ������
	 *
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public static long getDaysBetweenDate(String beginDateStr, String endDateStr, String pattern) {
		long day = 0;
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		Date beginDate;
		Date endDate;
		try {
			beginDate = format.parse(beginDateStr);
			endDate = format.parse(endDateStr);
			day = (endDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000);
			day += 1;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return day;
	}

	/**
	 * ���û�������his��ι���
	 * 
	 * @param operationName
	 * @param inputString
	 * @return
	 */
	public static String convertHisInputParamWithOutUserInfo(String operationName, String inputString) {
		StringBuffer param = new StringBuffer();
		param.append(
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:alip=\"http://www.sinodata.net.cn/AlipayService\">");
		param.append("<soapenv:Header/>");
		param.append("<soapenv:Body>");
		param.append("<alip:" + operationName + ">");
		param.append("<alip:payload><![CDATA[" + inputString + "]]></alip:payload>");
		param.append("</alip:" + operationName + ">");
		param.append("</soapenv:Body>");
		param.append("</soapenv:Envelope>");
		return param.toString();
	}

	/**
	 * �����б�his��ι���
	 * 
	 * @param operationName
	 * @param inputString
	 * @return
	 */
	public static String convertPaceReportHisInputString(String patCardNo, String beginDate, String endDate) {
		StringBuffer param = new StringBuffer();
		param.append(
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://www.PKU-HIT.com/hie/dsg/service\">");
		param.append("<soapenv:Header/>");
		param.append("<soapenv:Body>");
		param.append("<ser:examresultlist>");
		param.append("<ser:patCardNo>" + patCardNo + "</ser:patCardNo>");
		param.append("<ser:beginDate>" + beginDate + "</ser:beginDate>");
		param.append("<ser:endDate>" + endDate + "</ser:endDate>");
		param.append("<ser:userId>" + ParamConstants.USER_ID + "</ser:userId>");
		param.append("<ser:password>" + ParamConstants.USER_PASSWORD + "</ser:password>");
		param.append("</ser:examresultlist>");
		param.append("</soapenv:Body>");
		param.append("</soapenv:Envelope>");
		return param.toString();
	}

	/**
	 * �����б���ϸhis��ι���
	 * 
	 * @param operationName
	 * @param inputString
	 * @return
	 */
	public static String convertPaceReportDetailHisInputString(String checkId) {
		StringBuffer param = new StringBuffer();
		param.append(
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://www.PKU-HIT.com/hie/dsg/service\">");
		param.append("<soapenv:Header/>");
		param.append("<soapenv:Body>");
		param.append("<ser:examresultdetail>");
		param.append("<ser:checkId>" + checkId + "</ser:checkId>");
		param.append("<ser:userId>" + ParamConstants.USER_ID + "</ser:userId>");
		param.append("<ser:password>" + ParamConstants.USER_PASSWORD + "</ser:password>");
		param.append("</ser:examresultdetail>");
		param.append("</soapenv:Body>");
		param.append("</soapenv:Envelope>");
		return param.toString();
	}

	/**
	 * ����б�his��ι���
	 * 
	 * @param operationName
	 * @param inputString
	 * @return
	 */
	public static String convertLisReportHisInputString(String patCardNo, String beginDate, String endDate) {
		StringBuffer param = new StringBuffer();
		param.append(
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://www.PKU-HIT.com/hie/dsg/service\">");
		param.append("<soapenv:Header/>");
		param.append("<soapenv:Body>");
		param.append("<ser:labresultlist>");
		param.append("<ser:patCardNo>" + patCardNo + "</ser:patCardNo>");
		param.append("<ser:beginDate>" + beginDate + "</ser:beginDate>");
		param.append("<ser:endDate>" + endDate + "</ser:endDate>");
		param.append("<ser:userId>" + ParamConstants.USER_ID + "</ser:userId>");
		param.append("<ser:password>" + ParamConstants.USER_PASSWORD + "</ser:password>");
		param.append("</ser:labresultlist>");
		param.append("</soapenv:Body>");
		param.append("</soapenv:Envelope>");
		return param.toString();
	}

	/**
	 * ����б���ϸhis��ι���
	 * 
	 * @param operationName
	 * @param inputString
	 * @return
	 */
	public static String convertLisReportDetailHisInputString(String inspectId) {
		StringBuffer param = new StringBuffer();
		param.append(
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://www.PKU-HIT.com/hie/dsg/service\">");
		param.append("<soapenv:Header/>");
		param.append("<soapenv:Body>");
		param.append("<ser:jindielabresultitem>");
		param.append("<ser:inspectId>" + inspectId + "</ser:inspectId>");
		param.append("<ser:userId>" + ParamConstants.USER_ID + "</ser:userId>");
		param.append("<ser:password>" + ParamConstants.USER_PASSWORD + "</ser:password>");
		param.append("</ser:jindielabresultitem>");
		param.append("</soapenv:Body>");
		param.append("</soapenv:Envelope>");
		return param.toString();
	}

	/**
	 * �����б�his��ι���(��ѯҽԺҽѧӰ�񱨸� ���ڼ�鱨�����ͽӿ�)
	 * 
	 * @author YJB
	 * @date 2017��7��19�� ����11:04:10
	 * @param time
	 * @return
	 */
	public static String convertPacsReportCompletedHisInputString(String time) {
		StringBuffer param = new StringBuffer();
		param.append(
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://www.PKU-HIT.com/hie/dsg/service\">");
		param.append("<soapenv:Header/>");
		param.append("<soapenv:Body>");
		param.append("<ser:jindieexamreport>");
		param.append("<ser:time>" + time + "</ser:time>");
		param.append("<ser:userId>" + ParamConstants.USER_ID + "</ser:userId>");
		param.append("<ser:password>" + ParamConstants.USER_PASSWORD + "</ser:password>");
		param.append("</ser:jindieexamreport>");
		param.append("</soapenv:Body>");
		param.append("</soapenv:Envelope>");
		return param.toString();
	}

	/**
	 * �����б�his��ι���(��ѯlis���� ���ڼ�鱨�����ͽӿ�)
	 * 
	 * @author YJB
	 * @date 2017��7��19�� ����11:04:10
	 * @param time
	 * @return
	 */
	public static String convertLisReportCompletedHisInputString(String time) {
		StringBuffer param = new StringBuffer();
		param.append(
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://www.PKU-HIT.com/hie/dsg/service\">");
		param.append("<soapenv:Header/>");
		param.append("<soapenv:Body>");
		param.append("<ser:jindielabreport>"); // ��PACS��ͬ�ĵط�
		param.append("<ser:time>" + time + "</ser:time>");
		param.append("<ser:userId>" + ParamConstants.USER_ID + "</ser:userId>");
		param.append("<ser:password>" + ParamConstants.USER_PASSWORD + "</ser:password>");
		param.append("<ser:jindielabreport>");
		param.append("</soapenv:Body>");
		param.append("</soapenv:Envelope>");
		return param.toString();
	}

	/**
	 * ���û�������his��ι���
	 * 
	 * @param operationName
	 * @param userName
	 * @param password
	 * @param inputString
	 * @return
	 */
	public static String convertHisInputParam(String operationName, String inputString) {
		StringBuffer param = new StringBuffer();
		param.append(
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:alip=\"http://www.sinodata.net.cn/AlipayService\">");
		param.append("<soapenv:Header/>");
		param.append("<soapenv:Body>");
		param.append("<alip:" + operationName + ">");
		param.append("<alip:user><![CDATA[" + ParamConstants.USER + "]]></alip:user>");
		param.append("<alip:password><![CDATA[" + ParamConstants.PASSWORD + "]]></alip:password>");
		param.append("<alip:parameter><![CDATA[" + inputString + "]]></alip:parameter>");
		param.append("</alip:" + operationName + ">");
		param.append("</soapenv:Body>");
		param.append("</soapenv:Envelope>");
		return param.toString();
	}

	/**
	 * ABS�������ݽӿ���ι���
	 * 
	 * @param operationName
	 * @param userName
	 * @param password
	 * @param inputString
	 * @return
	 */
	public static String convertABSBaseInputParam(String operationName, String inputString) {
		StringBuffer param = new StringBuffer();
		param.append(
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:qry=\"http://qrycomdataservice.server.as.hkuszh.org\">");
		param.append("<soapenv:Header/>");
		param.append("<soapenv:Body>");
		param.append("<qry:" + operationName + ">");
		param.append("<qry:" + operationName + "><![CDATA[" + inputString + "]]></qry:" + operationName + ">");
		param.append("</qry:" + operationName + ">");
		param.append("</soapenv:Body>");
		param.append("</soapenv:Envelope>");
		return param.toString();
	}

	/**
	 * ABSԤԼ�ӿ���ι���
	 * 
	 * @param operationName
	 * @param userName
	 * @param password
	 * @param inputString
	 * @return
	 */
	public static String convertABSAppInputParam(String operationName, String inputString) {
		StringBuffer param = new StringBuffer();
		param.append(
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:alip=\"http://www.sinodata.net.cn/AlipayService\">");
		param.append("<soapenv:Header/>");
		param.append("<soapenv:Body>");
		param.append("<alip:" + operationName + ">");
		param.append("<alip:user><![CDATA[" + ParamConstants.USER + "]]></alip:user>");
		param.append("<alip:password><![CDATA[" + ParamConstants.PASSWORD + "]]></alip:password>");
		param.append("<alip:parameter><![CDATA[" + inputString + "]]></alip:parameter>");
		param.append("</alip:" + operationName + ">");
		param.append("</soapenv:Body>");
		param.append("</soapenv:Envelope>");
		return param.toString();
	}

	/**
	 * his���ν�ȡ
	 * 
	 * @param outputString
	 * @return
	 */
	public static String convertHisOutputParam(String outputString) {
		if (outputString.indexOf("<response>") > 0 && outputString.indexOf("</response>") > 0) {
			int startIndex = outputString.indexOf("<response>");
			int endIndex = outputString.indexOf("</response>");
			return outputString.substring(startIndex, endIndex + "</response>".length());
		}
		return outputString;
	}

	/**
	 * his���ν�ȡ
	 * 
	 * @param outputString
	 * @return
	 */
	public static String convertABSOutputParam(String outputString) {
		int startIndex = outputString.indexOf("<RESULT>");
		int endIndex = outputString.indexOf("</RESULT>");
		return outputString.substring(startIndex, endIndex + "</RESULT>".length());
	}

	/**
	 * �ж������ڴ�С
	 *
	 * @param DATE1
	 * @param DATE2
	 * @return 1:DATE1>DATE2 -1:DATE1<DATE2 0:DATE1==DATE2
	 */
	public static int compareDate(String DATE1, String DATE2) {
		try {
			Date dt1 = DateUtils.parseChineseDate(DATE1);
			Date dt2 = DateUtils.parseChineseDate(DATE2);
			if (dt1.getTime() > dt2.getTime()) {
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
				return -1;
			} else {
				return 0;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}

	/**
	 * ���ؽ��
	 * 
	 * @return
	 */
	public static String getErrorMsg(String code, String msg) {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><res><resultCode>" + code + "</resultCode><resultDesc>" + msg
				+ "</resultDesc></res>";
	}

	public static String ORDER_ID = "w003392017070600034,w003392017070600037,w003392017070600039,w003392017070600041,w003392017070600051,w003392017070600062,w003392017070600065,w003392017070600070,w003392017070600072,w003392017070600081,w003392017070600088,w003392017070600090,w003392017070600091,w003392017070600092,w003392017070600093,w003392017070600094,w003392017070600095,w003392017070600098,w003392017070600114,w003392017070600116,w003392017070600119,w003392017070600120,w003392017070600129,w003392017070600130";

	public static boolean isNotPaymentOrder(String orderId) {
		if (StringUtils.isBlank(orderId)) {
			return false;
		}
		if (ORDER_ID.indexOf(orderId) >= 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * �жϻ������ҵĺ��Ƿ���Ͽ���Ҫ��
	 * 
	 * @date 2017��10��26�� ����2:00:47
	 * @param deptId
	 * @param age
	 * @return
	 */
	public static String isNotAppointment(String deptId, int age) {

		logger.info(" ����������  " + DEPT_CODE);
		logger.info(" ����������  " + DEPT_CODE2);
		logger.info(" ����������  " + DEPT_CODE3);
		if (StringUtils.isBlank(deptId)) {
			return "";
		}
		if (DEPT_CODE.indexOf(deptId) >= 0) {
			if (age >= 18) {
				return CommonUtils.getErrorMsg("4155", "ҽԺ�涨:����ֻ����18�����»���");
			}
		} else if (DEPT_CODE2.indexOf(deptId) >= 0) {
			if (age < 18) {
				return CommonUtils.getErrorMsg("4115", "�����������ƣ������ʺϹҴ˿���");
			}
		} else if (DEPT_CODE3.indexOf(deptId) >= 0) {
			if (age < 10) {
				return CommonUtils.getErrorMsg("4115", "�����������ƣ������ʺϹҴ˿���");
			}
		}
		return "";
	}

	/**
	 * mongodb���ݱ������
	 * 
	 * @param resultXml
	 * @throws DocumentException
	 * @throws ParseException
	 */
	public static void insertMongodb(String reqXml, String resultXml) throws Exception {
		String apptno = UtilXml.getValueByAllXml(resultXml, "APPTNO");
		String hisOrderNo = UtilXml.getValueByAllXml(resultXml, "HISORDERNO");
		String insuranceSeq = UtilXml.getValueByAllXml(resultXml, "OUTPATIENTFEENO");
		String cancelSeq = UtilXml.getValueByAllXml(resultXml, "CANCELSERIALNO");
		String cancelBill = UtilXml.getValueByAllXml(resultXml, "CANCELBILLNO");

		String orderId = UtilXml.getValueByAllXml(reqXml, "orderId");
		if (StringUtils.isBlank(orderId)) {
			orderId = UtilXml.getValueByAllXml(reqXml, "lockId");
		}

		ChannelAppointmentLockInfo inpatientInfo = new ChannelAppointmentLockInfo();
		inpatientInfo.setOrderId(orderId);
		inpatientInfo.setBookingNo(apptno);
		inpatientInfo.setHisOrderNo(hisOrderNo);
		inpatientInfo.setClinicSeq(insuranceSeq);
		inpatientInfo.setCancelSeq(cancelSeq);
		inpatientInfo.setCalcelBill(cancelBill);
		inpatientInfo.setCardNo(UtilXml.getValueByAllXml(reqXml, "idCardNo"));
		inpatientInfo.setDeptId(UtilXml.getValueByAllXml(reqXml, "deptId"));
		inpatientInfo.setDeptName("");
		inpatientInfo.setStartTime(UtilXml.getValueByAllXml(reqXml, "startTime"));
		inpatientInfo.setEndTime(UtilXml.getValueByAllXml(reqXml, "endTime"));
		inpatientInfo.setRegDate(UtilXml.getValueByAllXml(reqXml, "regDate"));
		inpatientInfo.setShiftCode(UtilXml.getValueByAllXml(reqXml, "shiftCode"));
		inpatientInfo.setScheduleId(UtilXml.getValueByAllXml(reqXml, "scheduleId"));
		inpatientInfo.setSvObjectId(UtilXml.getValueByAllXml(reqXml, "svObjectId"));
		inpatientInfo.setPatientId(UtilXml.getValueByAllXml(reqXml, "patientId"));
		inpatientInfo.setPatientName(UtilXml.getValueByAllXml(reqXml, "patientName"));
		inpatientInfo.setHospitalId(UtilXml.getValueByAllXml(reqXml, "hospitalId"));
		inpatientInfo.setDoctorId(UtilXml.getValueByAllXml(reqXml, "doctorId"));
		inpatientInfo.setHealthCardNo(UtilXml.getValueByAllXml(reqXml, "healthCardNo"));
		inpatientInfo.set_id(StringUtil.generateUuid());

		Bson filter = Filters.and(Filters.eq("orderId", inpatientInfo.getOrderId()));
		List<org.bson.Document> recordList = new MongoDBHelper("channel_appointment_lock_info").query(filter);
		if (recordList == null || recordList.size() == 0) {
			org.bson.Document doc = org.bson.Document.parse(JSONObject.fromObject(inpatientInfo).toString());
			new MongoDBHelper("channel_appointment_lock_info").insert(doc);
			logger.error("mongodb�������ż�¼�ɹ���");
		} else {
			org.bson.Document tmp = recordList.get(0);
			inpatientInfo.set_id(tmp.getString("_id"));
			org.bson.Document doc = org.bson.Document.parse(JSONObject.fromObject(inpatientInfo).toString());
			new MongoDBHelper("channel_appointment_lock_info").updateById(inpatientInfo.get_id(), doc);
		}
	}

	private static String[] str = new String[] { "200804", "100303", "10050304", "100514", "101311", "10150101" };

	/**
	 * �����ӿ��ҽڵ㣨Ϊ���и�����Ҳ�ܹҺţ�
	 * 
	 * @param deptInfoList
	 * @param deptMap
	 * @param deptDescMap
	 */
	public static void addDeptNode(List<DeptInfoV3> deptInfoList, Map<String, String> deptMap,
			Map<String, String> deptDescMap) {
		if (deptInfoList == null || deptMap == null || deptDescMap == null || deptMap.size() == 0
				|| deptDescMap.size() == 0) {
			return;
		}
		for (int i = 0, len = str.length; i < len; i++) {
			DeptInfoV3 deptInfoV3 = new DeptInfoV3();
			deptInfoV3.setDeptId(str[i]);
			deptInfoV3.setDeptName(deptMap.get(str[i]));
			deptInfoV3.setParentId(str[i]);
			deptInfoV3.setDescription(deptDescMap.get(str[i]));
			deptInfoList.add(deptInfoV3);
		}
	}

	/**
	 * ��his��ȡ������Ϣ
	 * 
	 * @param patientName
	 * @param healthCardNo
	 * @return
	 * @throws Exception
	 */
	public static String getPatientInfoFromHis(String patientName, String healthCardNo) throws Exception {
		StringBuilder str = new StringBuilder(200);
		str.append("<request>");
		str.append("<params>");
		str.append("<branchCode></branchCode>");
		str.append("<patName>" + patientName + "</patName>");
		str.append("<patCardType>1</patCardType>");
		str.append("<patCardNo>" + healthCardNo + "</patCardNo>");
		str.append("</params>");
		str.append("</request>");
		logger.error("������Ϣ��ѯ�ӿڡ�getMZPatient����Σ�" + str.toString());
		String inputString = CommonUtils.convertHisInputParamWithOutUserInfo("getMZPatient", str.toString());
		HttpRequestService xmlRequest = HttpRequestService.getInstance();
		String resultXml = xmlRequest.request(inputString);
		resultXml = CommonUtils.convertHisOutputParam(resultXml);
		logger.error("������Ϣ��ѯ�ӿڡ�getMZPatient�����Σ�" + resultXml);
		String resCode = UtilXml.getValueByAllXml(resultXml, "resultCode");
		if ("0".equals(resCode)) {
			return resultXml;
		} else {
			logger.error("����������" + patientName + ";���ţ�" + healthCardNo + "��his��ȡ������Ϣʧ�ܣ�");
			return "";
		}
	}

	/**
	 * �жϵ�ǰʱ���Ƿ�����ָ��ʱ��
	 * 
	 * @param hourMinute
	 *            �̶���ʽ��HH:mm:ss
	 * @return
	 */
	public static boolean isBeforeFixedDate(String hourMinute) {
		if (StringUtils.isBlank(hourMinute)) {
			return false;
		}
		Date curDate = new Date();
		Date deadlineDate = DateUtils.parseChineseDate(DateUtils.getYMDTime(curDate) + " " + hourMinute);
		if (deadlineDate == null) {
			return false;
		}
		if (curDate.getTime() < deadlineDate.getTime()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ��ȡ��ȥ�ڼ��������
	 * 
	 * @param past
	 * @return
	 */
	public static String getPastDate(int past) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
		Date today = calendar.getTime();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String result = format.format(today);
		return result;
	}

	/**
	 * ��ȡδ�� �� past �������
	 * 
	 * @param past
	 * @return
	 */
	public static String getFetureDate(int past) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
		Date today = calendar.getTime();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String result = format.format(today);
		return result;
	}

	/**
	 * ���ؽ������ڣ���ʽyyyy-MM-dd
	 * 
	 * @author YJB
	 * @date 2017��9��19�� ����3:50:48
	 * @return
	 */
	public static String getToday() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date());
	}

	/**
	 * ��������id ��ѯ���ж���������Ƿ��ǽ��������
	 * 
	 * @author YJB
	 * @date 2017��9��8�� ����12:59:34
	 * @param bookingNo
	 * @return
	 */
	public static boolean isOtherPlatform(String bookingNo) {

		ChannelAppointmentLockInfo info = BusinessDBHelper.getLockInfoByBookingNo(bookingNo);
		if (info != null) {
			logger.error("ԤԼ�Һ���ˮ��Ϊ " + bookingNo + " Ϊ������������Ķ�����");
			return false;
		} else {
			logger.error("ԤԼ�Һ���ˮ��Ϊ " + bookingNo + " �ǡ��ǽ�������Ķ�����");
			return true;
		}

	}

	/**
	 * ���ݶ���id ��ѯ���ж���������Ƿ��ǽ��������
	 * 
	 * @author YJB
	 * @date 2017��10��13�� ����9:31:51
	 * @param lockId
	 * @return
	 */
	public static boolean isOtherPlatformByOrderId(String lockId) {

		ChannelAppointmentLockInfo info = BusinessDBHelper.getLockInfoByOrderId(lockId);
		if (info != null) {
			logger.error("������Ϊ " + lockId + " Ϊ������������Ķ�����");
			return false;
		} else {
			logger.error("������Ϊ " + lockId + " �ǡ��ǽ�������Ķ�����");
			return true;
		}

	}

	/**
	 * ȫ�Էѿ���
	 * 
	 * @return
	 */
	public static List<String> getPassDept() {
		List<String> list = new ArrayList<String>();
		list.add("10021201");// �����ڿ�10021201
		list.add("10021203");// ��������10021203
		list.add("10141203");// ���ƽ���10141203
		list.add("10141204");// ������������10141204
		list.add("10141205");// ����������������10141205
		list.add("101301");// Ƥ���ƣ����⣩101301
		list.add("1007010301");// ���в�������1007010301
		list.add("1007010302");// ���п�����1007010302
		return list;
	}

	/**
	 * ת�����ڸ�ʽ20171219000000
	 * 
	 * @param IDStr
	 * @return
	 * @throws Exception
	 */
	public static String getBirthday(String IDStr) {
		if (StringUtils.isBlank(IDStr)) {
			return "";
		}
		String strYear = "";
		String strMonth = "";
		String strDay = "";
		if (IDStr.length() == 18) {
			strYear = IDStr.substring(6, 10);// ���
			strMonth = IDStr.substring(10, 12);// �·�
			strDay = IDStr.substring(12, 14);// �·�
		} else if (IDStr.length() == 15) {
			strYear = "19" + IDStr.substring(6, 8);// ���
			strMonth = IDStr.substring(8, 10);// �·�
			strDay = IDStr.substring(10, 12);// �·�
		}
		return strYear + strMonth + strDay + "000000";
	}

	 /**
	 * @Title: getAge 
	 * @author Luffy-CXM 
	 * @Description: �ɳ������ڻ������      
	 * @return int
	  */
	public static int getAge(String birthdayStr) throws Exception {  
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
		Date birthDay = sdf.parse(birthdayStr);
        Calendar cal = Calendar.getInstance();  
  
        if (cal.before(birthDay)) {  
            throw new IllegalArgumentException(  
                    "The birthDay is before Now.It's unbelievable!");  
        }  
        int yearNow = cal.get(Calendar.YEAR);  
        int monthNow = cal.get(Calendar.MONTH);  
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);  
        cal.setTime(birthDay);  
  
        int yearBirth = cal.get(Calendar.YEAR);  
        int monthBirth = cal.get(Calendar.MONTH);  
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);  
  
        int age = yearNow - yearBirth;  
  
        if (monthNow <= monthBirth) {  
            if (monthNow == monthBirth) {  
                if (dayOfMonthNow < dayOfMonthBirth) age--;  
            }else{  
                age--;  
            }  
        }  
        return age;  
    } 
	
	/**
	* @Title: getHisRespone 
	* @Description: ��ȡxml��node�ڵ�ļ��� 
	* @return    �������� 
	* @throws
	 */
	@SuppressWarnings("unchecked")
	public static List<Element> getNodeList(String resultXml, String node) throws DocumentException {
		Document document = DocumentHelper.parseText(resultXml);
		Element response = document.getRootElement();
		List<Element> list = response.elements(node);
		return list;
	}
	public static void main(String[] args) {
		/*try {
			System.out.println(getAge("2018-01-19"));;
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
	}
}
