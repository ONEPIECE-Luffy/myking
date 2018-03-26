package com.kingdeehit.mobile.his.xianggang.service.appointment;

import org.apache.commons.lang.StringUtils;

import com.kingdeehit.mobile.his.entities.V3.param.appointment.AddOrderParamV3;
import com.kingdeehit.mobile.his.entities.table.ChannelBindingCard;
import com.kingdeehit.mobile.his.utils.BusinessDBHelper;
import com.kingdeehit.mobile.his.utils.IDCardHelper;
import com.kingdeehit.mobile.his.xianggang.constant.ParamConstants;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.ConfigUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.DateUtils;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * 预约挂号接口
 * @author tangfulin
 *
 */
public class addOrder extends AbstractService{
	
	private static String hisInterface="appointmentBooking";
	
	String reqStr = null;

	@Override
	public String execute(String reqXml) throws Exception {
		reqStr = reqXml;
		String deptId=UtilXml.getValueByAllXml(reqXml, "deptId");
		String idCardNo=UtilXml.getValueByAllXml(reqXml, "idCardNo");
		String patientNameXml="";
		String svObjectId=UtilXml.getValueByAllXml(reqXml, "svObjectId");
		String healthCardNo=UtilXml.getValueByAllXml(reqXml, "healthCardNo");
		
		ChannelBindingCard patientInfo = BusinessDBHelper.getPatientInfo(healthCardNo);
		String gender = "";
		if (patientInfo != null) {
			gender = patientInfo.getGender();
		}
		//男士不允许挂女性科室
		if (ConfigUtils.getFemaleDeptMap().containsKey(deptId) && "M".equals(gender)) {
			return CommonUtils.getErrorMsg("4114", "由于性别限制，您不适合挂此科室");
		}
		
		/*//自费科室不允许医保付费
		if (ConfigUtils.getSelfPayDeptMap().containsKey(deptId) && "02".equals(svObjectId)) {
			return CommonUtils.getErrorMsg("4171", "该科室只支持自费挂号，请重新选择服务对象。");
		}	*/	
		String insuranceflag= CommonUtils.getInsuranceflagByDeptId(deptId);
		logger.info("当前科室是："+ deptId + "，在缓存中找到的insuranceflag为：" + insuranceflag);
		//自费科室不允许医保付费
		if ("0".equals(CommonUtils.getInsuranceflagByDeptId(deptId)) && "02".equals(svObjectId)) {
			return CommonUtils.getErrorMsg("4171", "该科室只支持自费挂号，请重新选择服务对象。");
		}		
		//医保科室不允许选择自费付费
		if ("1".equals(CommonUtils.getInsuranceflagByDeptId(deptId)) && "01".equals(svObjectId)) {
			return CommonUtils.getErrorMsg("4175", "该科室只支持医保挂号，请重新选择服务对象。");
		}		
		
		try {
			String patientName=UtilXml.getValueByAllXml(reqXml, "patientName");
			patientNameXml=CommonUtils.getPatientInfoFromHis(patientName,healthCardNo);				
			String birthDay=UtilXml.getValueByAllXml(patientNameXml, "patBirth");							
			int age=IDCardHelper.getAge(DateUtils.parseDateFromString(birthDay));	
			String msg=CommonUtils.isNotAppointment(deptId,age);		
			if(StringUtils.isNotBlank(msg)){					
				logger.error("科室ID："+deptId+";身份证号："+idCardNo+";患者年龄："+age+";提示："+msg);			
				return msg;			
			}			
		} catch (Exception e) {
			logger.error("获取患者信息出错！",e);
		}
		
		String inputString=getInputParamString(patientNameXml,reqXml);	
		String convertInputString=CommonUtils.convertHisInputParam(hisInterface, inputString);
		logger.error("预约挂号接口【appointment.addOrder】-->【"+hisInterface+"】入参：user="+ParamConstants.USER+";password="+ParamConstants.PASSWORD+";parameter="+inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();					
		String resultXml= xmlRequest.request(convertInputString);	
		resultXml=CommonUtils.convertABSOutputParam(resultXml);		
		logger.error("预约挂号接口【appointment.addOrder】-->【"+hisInterface+"】出参："+resultXml);
		String resCode=UtilXml.getValueByAllXml(resultXml, "STATE");
		if(StringUtils.isBlank(resCode)){
			return CommonUtils.getErrorMsg();
		}
		if("1".equals(resCode)){
			CommonUtils.insertMongodb(reqXml,resultXml);
			String v3String=convertHisStringToV3String(svObjectId,resultXml);						
			return v3String;			
		}else{
			String err=UtilXml.getValueByAllXml(resultXml, "MSG");
			return CommonUtils.getErrorMsg(err);
		}		
	}
	
	/**
	 * 预约挂号his入参字符串构造
	 * @param reqXml
	 * @return
	 * @throws Exception 
	 */
	private String getInputParamString(String patientNameXml,String reqXml) throws Exception{		
		XStream xStream = new XStream(new DomDriver("UTF-8"));
		xStream.alias("req", AddOrderParamV3.class);		
		AddOrderParamV3 addOrderParamV3=(AddOrderParamV3)xStream.fromXML(reqXml);	
		String svObjectId=addOrderParamV3.getSvObjectId();		
		StringBuilder str=new StringBuilder(400);
		str.append("<PARAMETER>");
		str.append("<DEPTID>"+addOrderParamV3.getDeptId()+"</DEPTID>");
		str.append("<ORDERNO>"+addOrderParamV3.getOrderId()+"</ORDERNO>");
		str.append("<TRUENAME>"+addOrderParamV3.getPatientName()+"</TRUENAME>");		
		if(StringUtils.isNotBlank(patientNameXml)){
			String birthDay=UtilXml.getValueByAllXml(patientNameXml, "patBirth");
			String sex=UtilXml.getValueByAllXml(patientNameXml, "patSex");
			if("F".equalsIgnoreCase(sex)){
				str.append("<SEX>1</SEX>");
			}else if("M".equalsIgnoreCase(sex)){
				str.append("<SEX>0</SEX>");
			}
			str.append("<BIRTHDAY>"+birthDay+"</BIRTHDAY>");
		}else{
			str.append("<BIRTHDAY></BIRTHDAY>");
			str.append("<SEX></SEX>");	
		}		
		str.append("<IDTYPE>1</IDTYPE>");
		str.append("<IDNO>"+addOrderParamV3.getIdCardNo()+"</IDNO>");	
		str.append("<PHONE>"+addOrderParamV3.getPhone()+"</PHONE>");		
		str.append("<DOCTORNO></DOCTORNO>");
		str.append("<REGLEVELID>"+addOrderParamV3.getDoctorLevelCode()+"</REGLEVELID>");
		str.append("<TODATE>"+addOrderParamV3.getRegDate()+"</TODATE>");
		str.append("<STARTTIME>"+addOrderParamV3.getStartTime()+"</STARTTIME>");
		str.append("<ENDTIME>"+addOrderParamV3.getEndTime()+"</ENDTIME>");
		str.append("<BOOKTIME>"+addOrderParamV3.getOrderTime()+"</BOOKTIME>");
		str.append("<OPERATOR>Kingdee</OPERATOR>");
		str.append("<CHNCODE>9</CHNCODE>");//9
		str.append("<VENDORID>5</VENDORID>");//5
		if("02".equals(svObjectId)){
			str.append("<ISINSURANCE>1</ISINSURANCE>");
			str.append("<MEDICALCARDNO></MEDICALCARDNO>");
			str.append("<COMPUTERNO></COMPUTERNO>");
			str.append("<SOCIALCARD></SOCIALCARD>");
		}else{
			str.append("<ISINSURANCE>0</ISINSURANCE>");
			str.append("<MEDICALCARDNO></MEDICALCARDNO>");
			str.append("<COMPUTERNO></COMPUTERNO>");
			str.append("<SOCIALCARD></SOCIALCARD>");
		}		
		str.append("<ISTODAY>0</ISTODAY>");		
		str.append("</PARAMETER>");			
		return str.toString();
	}	
	
	/**
	 * his出参转V3出参
	 * @param orderString
	 * @return
	 * @throws Exception 
	 */
	private String convertHisStringToV3String(String svObjectId,String orderString) throws Exception{		
		String apptno=UtilXml.getValueByAllXml(orderString, "APPTNO");
//		String clinicSeq=UtilXml.getValueByAllXml(orderString, "HISORDERNO");
		String insuranceSeq=UtilXml.getValueByAllXml(orderString, "OUTPATIENTFEENO");
		 saveRegisterInfo(reqStr,orderString);
		StringBuilder str=new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		str.append("<res>");
		str.append("<resultCode>0</resultCode>");
		str.append("<resultDesc>成功</resultDesc>");
		str.append("<bookingNo>"+apptno+"</bookingNo>");
		str.append("<roomAddress></roomAddress>");
		str.append("<oppatNo></oppatNo>");
		if("01".equals(svObjectId)){
			str.append("<clinicSeq>"+apptno+"</clinicSeq>");
		}else{
			if(StringUtils.isNotBlank(insuranceSeq)){
				str.append("<clinicSeq>"+insuranceSeq+"</clinicSeq>");
			}else{
				str.append("<clinicSeq>"+apptno+"</clinicSeq>");
			}
		}		
		str.append("<remark>"+apptno+"</remark>");//主要考虑到保存到数据中中加了这个字段，bookingNo没在挂号表中存
		str.append("<queueNo>"+apptno+"</queueNo>");
		str.append("<validTime></validTime>");
		str.append("</res>");
		
		
		return str.toString();
	}
	/**
	 * 保存挂号过程中的his返回的锁号数据
	 * @author YJB
	 * @date 2017年9月18日 下午2:56:55
	 * @param reqStr
	 * @param orderString
	 * @throws Exception 
	 */
	public  void saveRegisterInfo(String reqStr, String resultXml) throws Exception {
		/*String orderId = UtilXml.getValueByAllXml(reqStr, "orderId");
		String bookingNo = UtilXml.getValueByAllXml(orderString, "APPTNO");
		String patientId = UtilXml.getValueByAllXml(reqStr, "patientId");
		String patientName = UtilXml.getValueByAllXml(reqStr, "patientName");
		String healthCardNo = UtilXml.getValueByAllXml(reqStr, "healthCardNo"); 
		ChannelLockInfo info = new ChannelLockInfo();
		info.setBookingNo(bookingNo);
		info.setPatientId(patientId);
		info.setPatientName(patientName);
		info.setOrderId(orderId);
		info.setHealthCardNo(healthCardNo);
		BusinessDBHelper.saveAndUpdateRegisterInfo(info);*/
		CommonUtils.insertMongodb(reqStr, resultXml);
	}
	
	
	
	
	public static void main(String[] args) {
		
		CommonUtils.cacheDeptInfo("111", "0");
		CommonUtils.cacheDeptInfo("222", "1");
		System.out.println("value==" +CommonUtils.getInsuranceflagByDeptId("222"));
	}
}
