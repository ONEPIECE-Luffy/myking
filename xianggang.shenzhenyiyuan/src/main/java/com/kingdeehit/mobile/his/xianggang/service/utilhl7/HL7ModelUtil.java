package com.kingdeehit.mobile.his.xianggang.service.utilhl7;

import com.kingdeehit.mobile.utils.UtilDateTime;
import com.kingdeehit.mobile.utils.UtilXml;

/**
 * HL7��������Model
 * @author wudigang
 * @date 2018��3��21��
 */
public class HL7ModelUtil {

	/**
	 * ����EMPIϵͳ���루XML��ʽ��
	 * @param requestBody��HL7��ʽ��
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
	 * ������ϢMSH
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
	 * ������ϢEVN
	 * @return
	 */
	public static String builderEVN(){
		String timeStamp = UtilDateTime.getNowTimeStampFileString();
		return String.format(
				"EVN|ZP1|%s|%s|||%s|HIS", timeStamp,timeStamp,timeStamp);
	}
	
	/**
	 * ������ϢQPD
	 * @return
	 */
	public static String builderQPD(){
		return "QPD|ZP7^Find Candidates^HL7v2.4||";
	}
	
	/**
	 * ������ϢPID
	 * @return
	 */
	public static String builderPID(){
		return "PID|";
	}
	
	/**
	 * ������ϢRCP
	 * @return
	 */
	public static String builderRCP(){
		/**
		 * ����1- D����ʾ�ӳ٣�I��ʾ����ִ��
		 * ����2- ����20^RD��ʾ20����¼
		 * ����3- R����ʾʵʱ��B��ʾ������T��ʾ����δ����
		 */
		return "RCP|I|20^RD|R";
	}
	
	/**
	 * ��ȡ��������ҵ�����
	 * @param response
	 * @return
	 */
	public static String getResponseBody(String response){
		return UtilXml.getValueByAllXml(response, "return");
	}
	
	/**
	 * �޸��ָ���
	 * @param response
	 * @return
	 */
	public static String fixHL7Split(String response){
		String value = response.replace("MSA|", "\rMSA|").replace("PID|||", "\rPID|||");
		return value;
	}
}
