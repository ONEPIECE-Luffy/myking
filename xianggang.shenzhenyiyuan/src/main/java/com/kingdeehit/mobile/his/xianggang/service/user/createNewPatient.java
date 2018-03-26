package com.kingdeehit.mobile.his.xianggang.service.user;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Node;

import com.kingdeehit.mobile.his.consts.ErrorCode;
import com.kingdeehit.mobile.his.entities.V3.result.user.CreateNewPatientResultV3;
import com.kingdeehit.mobile.his.utils.Consts;
import com.kingdeehit.mobile.his.utils.HL7ToXmlConverter;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.his.xianggang.service.util.IDCardHelper;
import com.kingdeehit.mobile.his.xianggang.service.utilhl7.HL7ModelUtil;
import com.kingdeehit.mobile.utils.StringUtil;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;

/**
 * 在线建卡
 * @author wudigang
 * @date 2018年3月21日
 */
public class createNewPatient extends AbstractService {

	//"http://10.2.200.222:6060/services/AlipayService.AlipayServiceHttpSoap11Endpoint"
	private HttpRequestService service = HttpRequestService.getInstance(Consts.HIS_SERVICE_URL5);
	
	private final static String empi_patient_get = "QBP^ZP7";
	private final static String empi_patient_create = "ADT^ZP1";
	
	@Override
	public String execute(String reqXml) throws Exception {
		XStream xStream = UtilXml.getSingletonXStream(CreateNewPatientResultV3.class);
		CreateNewPatientResultV3 resultV3 = new CreateNewPatientResultV3();	
		
		String idCardNo = UtilXml.getValueByAllXml(reqXml, "idCardNo");
		String patientName = UtilXml.getValueByAllXml(reqXml, "patientName");
		String phone = UtilXml.getValueByAllXml(reqXml, "phone");
		String create = UtilXml.getValueByAllXml(reqXml, "create");
		
		String verify = IDCardHelper.IDCardValidate(idCardNo);
		if(StringUtil.isNotEmpty(verify)){
			resultV3.setResultCode("4010");
			resultV3.setResultDesc(verify);
			return xStream.toXML(resultV3);
		}
		
		String gender = IDCardHelper.getSex(idCardNo);
		String birthday = IDCardHelper.getBirthday(idCardNo).replace("-", "");	

		if("1".equals(create)){
			resultV3 = createPatient(idCardNo,patientName,gender,birthday,phone);
			return xStream.toXML(resultV3); 
		}
		
		resultV3 = verifyPatient(idCardNo,patientName,gender,birthday,"");
	
		//返回resultCode的情况有2种，需通过是否返回卡号进行下一步判断
		if(ErrorCode.Success.value().equals(resultV3.getResultCode())){
			if(StringUtil.isEmpty(resultV3.getHealthCardNo())){
				resultV3 = createPatient(idCardNo,patientName,gender,birthday,phone);
			}
		}
		
		return xStream.toXML(resultV3);
	}

	/**
	 * 校验当前病人信息，是否是新病人
	 * @param idCardNo
	 * @param patientName
	 * @param gender
	 * @return
	 * @throws Exception 
	 */
	private CreateNewPatientResultV3 verifyPatient(String idCardNo, String patientName, String gender, String birthday,String healthCardNo){
		CreateNewPatientResultV3 resultV3 = new CreateNewPatientResultV3();		
		try {
			String body = HL7ModelUtil.builderMSH(empi_patient_get)+"\n";
			body += HL7ModelUtil.builderQPD();
			body += String.format("0^%s^%s||%s|%s|%s", idCardNo,healthCardNo,patientName,birthday+"000000",gender)+"\n";
			body += HL7ModelUtil.builderRCP();
			
			String requestXml = HL7ModelUtil.builderRequestMode(body);
			logger.info("调用EMPI系统查询病人信息接口入参："+requestXml);
			String responseXml = service.request(requestXml);
			logger.info("调用EMPI系统查询病人信息接口出参："+responseXml);
			String responseBody = HL7ModelUtil.getResponseBody(responseXml);
			responseBody = HL7ModelUtil.fixHL7Split(responseBody);
			Document document = HL7ToXmlConverter.ConvertToXmlObject(responseBody);
			
			if (isSuccess(document)) {
				if(responseBody.indexOf("PID|||") == 1){
					resultV3 = renderPatient(document);
				}else if(responseBody.indexOf("PID|||") > 1){
					resultV3.setResultCode("4043");
					resultV3.setResultDesc("您已有该医院的就诊卡，不需要再注册，请到窗口激活。");					
				}else{
					resultV3.setData(ErrorCode.Success);
				}
			}else{
				resultV3.setData(ErrorCode.UnknowErr);
			}
		
		} catch (Exception e) {
			logger.error("调用EMPI系统查询病人信息接口异常",e);
			resultV3.setData(ErrorCode.UnknowErr);
		}		
		return resultV3;
	}
	
	/**
	 * 创建病人信息
	 * @param reqXml
	 * @return
	 * @throws Exception 
	 */
	private CreateNewPatientResultV3 createPatient(String idCardNo, String patientName, String gender, String birthday,String phone) throws Exception{
		String localId = getLocalID(idCardNo,patientName);
		
		String body = HL7ModelUtil.builderMSH(empi_patient_create)+"\n";
		body += HL7ModelUtil.builderEVN()+"\n";
		body += HL7ModelUtil.builderPID();
		body += String.format("1||%s^^^^LocalID~%s^^^^IdentifyNO~%s^^^^IDCard~^^^^PatientNO~^^^^OtherID~^^^^UPID||%s^^^^^1||%s|%s|||^^^^^^RH~^^^^^^H~^^^^^^C~^^^^^^O~^^^^^^"
				+ "BDL||%s^^PH^^^^%s~%s^^CP|||^0||||||^0|||||||||N"
				+ "\n"
				+ "NK1|1||^0"
				+ "\n"
				+ "IN1|1|000000|SZSI", 
				localId,idCardNo,localId,patientName,birthday+"000000",gender,phone,phone,phone);
		
		String requestXml = HL7ModelUtil.builderRequestMode(body);
		
		logger.info("调用EMPI系统注册病人信息接口入参："+requestXml);
		String responseXml = service.request(requestXml);
		logger.info("调用EMPI系统注册病人信息接口出参："+responseXml);
		
		String responseBody = HL7ModelUtil.getResponseBody(responseXml);
		responseBody = HL7ModelUtil.fixHL7Split(responseBody);
		Document document = HL7ToXmlConverter.ConvertToXmlObject(responseBody);
		if (isSuccess(document)) {
			//读取病人信息		
			return renderPatient(document);
		}
		return null;
	}
	
	private boolean isSuccess(Document document){
		String resultValue = HL7ToXmlConverter.GetText(document, "MSA/MSA.1");
		if ("AA".equals(resultValue)) {
			return true;
		}
		return false;
	}
	
	private CreateNewPatientResultV3 renderPatient(Document document){
		//读取病人信息		
		CreateNewPatientResultV3 patient = new CreateNewPatientResultV3();
		patient.setData(ErrorCode.Success);
		
		String healthCardNo = "";
		List nodes = HL7ToXmlConverter.GetTexts(document, "PID/PID.3");
		for(Object obj :nodes){
			Node node = (Node) obj;
			//logger.info(node.asXML());
			String fileName = node.selectSingleNode("PID.3.5").getText();
			if(("UPID").equals(fileName)){
				healthCardNo = node.selectSingleNode("PID.3.1").getText();
			}
		}		
		patient.setHealthCardNo(healthCardNo);
		patient.setPatientId(healthCardNo);
		return patient;
	}
	
	private String getLocalID(String idCardNo, String patientName){
		return String.format("DA%s", "0000"+Integer.valueOf((int) ((Math.random()*9+1)*1000)));
	}
	
	public static void main(String[] args) {
		
		StringBuffer buf = new StringBuffer();
		buf.append("<soapenv:Body><ns2:InvokeMethodResponse><return>MSH|^~\\&amp;|PMI||01||20180323162437||ZCK^ZP1|20180323162437|P|2.4|MSA|AA|20180323162437|[MsgInfo] Method Type: ZP1 -Success Flag: AA -MSG: success createPID|||DA00002035^^^^LocalID~440803197712310741^^^^IdentifyNO~DA00002035^^^^IDCard~^^^^PatientNO~^^^^OtherID~AH3355099^^^^UPID||何莎</return></ns2:InvokeMethodResponse></soapenv:Body></soapenv:Envelope>");
		
		String responseBody = HL7ModelUtil.getResponseBody(buf.toString());
		System.out.println("responseBody1:"+responseBody);
		responseBody = HL7ModelUtil.fixHL7Split(responseBody);
		System.out.println("responseBody2:"+responseBody);
		Document document = HL7ToXmlConverter.ConvertToXmlObject(responseBody);
		System.out.println("xml:"+document.asXML());
		String resultValue = HL7ToXmlConverter.GetText(document, "MSA/MSA.1");
		System.out.println("resultValue:"+resultValue);
		
		List nodes = HL7ToXmlConverter.GetTexts(document, "PID/PID.3");
		for(Object obj :nodes){
			Node node = (Node) obj;
			System.out.println(node.selectSingleNode("PID.3.5").getText() + ":" +node.selectSingleNode("PID.3.1").getText());
		}
	
	}
}
