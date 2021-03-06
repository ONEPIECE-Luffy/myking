package com.kingdeehit.mobile.his.xianggang.service.register.other;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;

import com.kingdeehit.mobile.his.entities.V3.param.register.LockRegParamV3;
import com.kingdeehit.mobile.his.utils.IDCardHelper;
import com.kingdeehit.mobile.his.xianggang.constant.ParamConstants;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.DateUtils;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * 当天挂号号源锁定接口
 * @author tangfulin
 *
 */
public class lockReg extends AbstractService{
	
	private static String hisInterface="appointmentBooking";

	@Override
	public String execute(String reqXml) throws Exception {	
		String deptId=UtilXml.getValueByAllXml(reqXml, "deptId");
		String idCardNo=UtilXml.getValueByAllXml(reqXml, "idCardNo");
		String patientNameXml="";
		try {
			String patientName=UtilXml.getValueByAllXml(reqXml, "patientName");
			String healthCardNo=UtilXml.getValueByAllXml(reqXml, "healthCardNo");
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
		logger.error("当天挂号号源锁定接口【register.lockReg】-->【"+hisInterface+"】入参：user="+ParamConstants.USER+";password="+ParamConstants.PASSWORD+";parameter="+inputString);		
		inputString=CommonUtils.convertHisInputParam(hisInterface, inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();					
		String resultXml= xmlRequest.request(inputString);	
		resultXml=CommonUtils.convertABSOutputParam(resultXml);		
		logger.error("当天挂号号源锁定接口【register.lockReg】-->【"+hisInterface+"】出参："+resultXml);
		String resCode=UtilXml.getValueByAllXml(resultXml, "STATE");
		if(StringUtils.isBlank(resCode)||Integer.parseInt(resCode)<=0){
			String err=UtilXml.getValueByAllXml(resultXml, "MSG");
			logger.error("锁号失败："+err);
			return CommonUtils.getErrorMsg(err);
		}	
		CommonUtils.insertMongodb(reqXml,resultXml);
		return convertHisStringToV3Object(resultXml);
	}
	
	/**
	 * 预约挂号his入参字符串构造
	 * @param reqXml
	 * @return
	 * @throws Exception 
	 */
	private String getInputParamString(String patientNameXml,String reqXml) throws Exception{
		XStream xStream = new XStream(new DomDriver("UTF-8"));
		xStream.alias("req", LockRegParamV3.class);		
		LockRegParamV3 regParamV3=(LockRegParamV3)xStream.fromXML(reqXml);		
		StringBuilder str=new StringBuilder(400);
		str.append("<PARAMETER>");
		str.append("<DEPTID>"+regParamV3.getDeptId()+"</DEPTID>");
		str.append("<ORDERNO>"+regParamV3.getLockId()+"</ORDERNO>");
		str.append("<TRUENAME>"+regParamV3.getPatientName()+"</TRUENAME>");			
		str.append("<IDTYPE>1</IDTYPE>");
		str.append("<IDNO>"+regParamV3.getIdCardNo()+"</IDNO>");
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
		str.append("<PHONE>"+regParamV3.getPhone()+"</PHONE>");		
		str.append("<DOCTORNO></DOCTORNO>");
		str.append("<REGLEVELID>"+regParamV3.getDoctorLevelCode()+"</REGLEVELID>");
		str.append("<TODATE>"+regParamV3.getRegDate()+"</TODATE>");
		str.append("<STARTTIME>"+regParamV3.getStartTime()+"</STARTTIME>");
		str.append("<ENDTIME>"+regParamV3.getEndTime()+"</ENDTIME>");
		str.append("<BOOKTIME>"+DateUtils.getChineseTime()+"</BOOKTIME>");
		str.append("<OPERATOR>Kingdee</OPERATOR>");
		str.append("<CHNCODE>9</CHNCODE>");//9
		str.append("<VENDORID>5</VENDORID>");//5
		String svObjectId=regParamV3.getSvObjectId();
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
		str.append("<ISTODAY>1</ISTODAY>");		
		str.append("</PARAMETER>");			
		return str.toString();
	}
	
	/**
	 * his出参转V3出参
	 * @param orderString
	 * @return
	 * @throws DocumentException 
	 */
	@SuppressWarnings("unchecked")
	private String convertHisStringToV3Object(String resultXml) throws DocumentException{	
		String apptno=UtilXml.getValueByAllXml(resultXml, "APPTNO");
		String insuranceSeq=UtilXml.getValueByAllXml(resultXml, "OUTPATIENTFEENO");
		
//		Document document=DocumentHelper.parseText(resultXml);
//		Element root=document.getRootElement();		
//		Element res=root.element("DATA");		
//		Element element=res.element("RECORD");
		StringBuilder str=new StringBuilder(200);
		str.append("<res>");
		str.append("<resultCode>0</resultCode>");
		str.append("<resultDesc>成功</resultDesc>");
		//insuranceSeq不为空，说明是医保订单，则传入医保流水号，但在支付的时候需要转换为his的预约号
		if(StringUtils.isNotBlank(insuranceSeq)){
			str.append("<infoSeq>"+insuranceSeq+"</infoSeq>");
		}else{
			str.append("<infoSeq>"+apptno+"</infoSeq>");
		}
		str.append("</res>");
		return str.toString();
	}
	
	
	
}
