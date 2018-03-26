package com.kingdeehit.mobile.his.xianggang.service.utilhl7;

import com.kingdeehit.mobile.utils.UtilDateTime;
import com.kingdeehit.mobile.utils.UtilXml;

/**
 * HL7请求数据Model
 * @author wudigang
 * @date 2018年3月21日
 */
public class HL7ModelUtil {

	/**
	 * 构造EMPI系统的入（XML格式）
	 * @param requestBody（HL7格式）
	 * @return
	 */
	public static String builderRequestMode(String requestBody) {
		StringBuffer param = new StringBuffer();
		param.append(
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:alip=\"http://www.sinodata.net.cn/AlipayService\">");
		param.append("<soapenv:Header/>");
		param.append("<soapenv:Body>");
		param.append("<alip:InvokeMethod>");
		param.append("<alip:payload>");
		param.append("<![CDATA["+requestBody+"]]>");
		param.append("</alip:payload>");
		param.append("</alip:InvokeMethod>");
		param.append("</soapenv:Body>");
		param.append("</soapenv:Envelope>");
		return param.toString();
	}
	
	/**
	 * 构造消息MSH
	 * @param method
	 * @return
	 */
	public static String builderMSH(String method){
		String timeStamp = UtilDateTime.getNowTimeStampFileString();
		
		return String.format(
				"MSH|^~\\&|01||PMI||%s||%s|%s|P|2.4|||AL|AL|CHN|UNICODE", 
				timeStamp,method,timeStamp);
	}

	/**
	 * 构造消息EVN
	 * @return
	 */
	public static String builderEVN(){
		String timeStamp = UtilDateTime.getNowTimeStampFileString();
		return String.format(
				"EVN|ZP1|%s|%s|||%s|HIS", timeStamp,timeStamp,timeStamp);
	}
	
	/**
	 * 构造消息QPD
	 * @return
	 */
	public static String builderQPD(){
		return "QPD|ZP7^Find Candidates^HL7v2.4||";
	}
	
	/**
	 * 构造消息PID
	 * @return
	 */
	public static String builderPID(){
		return "PID|";
	}
	
	/**
	 * 构造消息RCP
	 * @return
	 */
	public static String builderRCP(){
		/**
		 * 参数1- D：表示延迟，I表示立即执行
		 * 参数2- 例如20^RD表示20条记录
		 * 参数3- R：表示实时，B表示分批，T表示大量未分批
		 */
		return "RCP|I|20^RD|R";
	}
	
	/**
	 * 获取请求结果的业务参数
	 * @param response
	 * @return
	 */
	public static String getResponseBody(String response){
		return UtilXml.getValueByAllXml(response, "return");
	}
	
	/**
	 * 修复分隔符
	 * @param response
	 * @return
	 */
	public static String fixHL7Split(String response){
		String value = response.replace("MSA|", "\rMSA|").replace("PID|||", "\rPID|||");
		return value;
	}
}
