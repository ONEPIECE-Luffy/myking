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
 * ԤԼ�ҺŽӿ�
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
		//��ʿ�������Ů�Կ���
		if (ConfigUtils.getFemaleDeptMap().containsKey(deptId) && "M".equals(gender)) {
			return CommonUtils.getErrorMsg("4114", "�����Ա����ƣ������ʺϹҴ˿���");
		}
		
		/*//�Էѿ��Ҳ�����ҽ������
		if (ConfigUtils.getSelfPayDeptMap().containsKey(deptId) && "02".equals(svObjectId)) {
			return CommonUtils.getErrorMsg("4171", "�ÿ���ֻ֧���ԷѹҺţ�������ѡ��������");
		}	*/	
		String insuranceflag= CommonUtils.getInsuranceflagByDeptId(deptId);
		logger.info("��ǰ�����ǣ�"+ deptId + "���ڻ������ҵ���insuranceflagΪ��" + insuranceflag);
		//�Էѿ��Ҳ�����ҽ������
		if ("0".equals(CommonUtils.getInsuranceflagByDeptId(deptId)) && "02".equals(svObjectId)) {
			return CommonUtils.getErrorMsg("4171", "�ÿ���ֻ֧���ԷѹҺţ�������ѡ��������");
		}		
		//ҽ�����Ҳ�����ѡ���ԷѸ���
		if ("1".equals(CommonUtils.getInsuranceflagByDeptId(deptId)) && "01".equals(svObjectId)) {
			return CommonUtils.getErrorMsg("4175", "�ÿ���ֻ֧��ҽ���Һţ�������ѡ��������");
		}		
		
		try {
			String patientName=UtilXml.getValueByAllXml(reqXml, "patientName");
			patientNameXml=CommonUtils.getPatientInfoFromHis(patientName,healthCardNo);				
			String birthDay=UtilXml.getValueByAllXml(patientNameXml, "patBirth");							
			int age=IDCardHelper.getAge(DateUtils.parseDateFromString(birthDay));	
			String msg=CommonUtils.isNotAppointment(deptId,age);		
			if(StringUtils.isNotBlank(msg)){					
				logger.error("����ID��"+deptId+";���֤�ţ�"+idCardNo+";�������䣺"+age+";��ʾ��"+msg);			
				return msg;			
			}			
		} catch (Exception e) {
			logger.error("��ȡ������Ϣ����",e);
		}
		
		String inputString=getInputParamString(patientNameXml,reqXml);	
		String convertInputString=CommonUtils.convertHisInputParam(hisInterface, inputString);
		logger.error("ԤԼ�ҺŽӿڡ�appointment.addOrder��-->��"+hisInterface+"����Σ�user="+ParamConstants.USER+";password="+ParamConstants.PASSWORD+";parameter="+inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();					
		String resultXml= xmlRequest.request(convertInputString);	
		resultXml=CommonUtils.convertABSOutputParam(resultXml);		
		logger.error("ԤԼ�ҺŽӿڡ�appointment.addOrder��-->��"+hisInterface+"�����Σ�"+resultXml);
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
	 * ԤԼ�Һ�his����ַ�������
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
	 * his����תV3����
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
		str.append("<resultDesc>�ɹ�</resultDesc>");
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
		str.append("<remark>"+apptno+"</remark>");//��Ҫ���ǵ����浽�������м�������ֶΣ�bookingNoû�ڹҺű��д�
		str.append("<queueNo>"+apptno+"</queueNo>");
		str.append("<validTime></validTime>");
		str.append("</res>");
		
		
		return str.toString();
	}
	/**
	 * ����ҺŹ����е�his���ص���������
	 * @author YJB
	 * @date 2017��9��18�� ����2:56:55
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
