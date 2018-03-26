package com.kingdeehit.mobile.his.xianggang.service.baseinfo;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingdeehit.mobile.his.entities.V3.result.baseinfo.GetOutpatientInfoResultV3;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;


/**
 * @author tangfulin
 * ��ѯ������Ϣ
 */
public class getOutpatientInfo extends AbstractService {
	
	private static String hisInterface="getMZPatient";

	@Override
	public String execute(String reqXml) throws Exception {
		//String patientName=UtilXml.getValueByAllXml(reqXml, "patientName");
		String healthCardNo=UtilXml.getValueByAllXml(reqXml, "healthCardNo");
		String inputString="";	
		if(StringUtils.isNotBlank(healthCardNo)){
			if(healthCardNo.length()==18){
				inputString=getInputParamString("5",reqXml);
			}else{
				inputString=getInputParamString("1",reqXml);
			}
		}else{
			return CommonUtils.getErrorMsg("-1", "����Ϊ��");
		}		
		logger.error("������Ϣ��ѯ�ӿڡ�baseinfo.getOutpatientInfo��-->��"+hisInterface+"����Σ�"+inputString);		
		inputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface, inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();					
		String resultXml= xmlRequest.request(inputString);	
		resultXml=CommonUtils.convertHisOutputParam(resultXml);
		logger.error("������Ϣ��ѯ�ӿڡ�baseinfo.getOutpatientInfo��-->��"+hisInterface+"�����Σ�"+resultXml);
		String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");		
		if("0".equals(resCode)){
			GetOutpatientInfoResultV3 resultV3=convertHisStringToV3Object(reqXml,resultXml);						
			XStream xstream = UtilXml.getXStream(GetOutpatientInfoResultV3.class);					
			return xstream.toXML(resultV3);		
		}else if("-1".equals(resCode)){
			String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
			logger.error("�ӿڷ���ʧ�ܣ�resCode="+resCode+";errorMsg="+errorMsg);
			return CommonUtils.getSuccessMsg();
		}else if("1".equals(resCode)){			
			logger.error("�ӿ�ִ�гɹ���û�ж�Ӧ���ݣ�");			
			if(healthCardNo.length()==18){				
				return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><res><resultCode>4025</resultCode>"
				+ "<resultDesc>���֤������ҽԺ�Ǽǲ��������������޸���Ϣ��󶨣�</resultDesc>"
				+ "<patientId>"+UtilXml.getValueByXml(reqXml, "healthCardNo")+"</patientId>"
				+ "<healthCardNo>"+UtilXml.getValueByXml(reqXml, "healthCardNo")+"</healthCardNo>"
				+ "<patientName>"+UtilXml.getValueByXml(reqXml, "patientName")+"</patientName>"
				+ "<phone>"+UtilXml.getValueByXml(reqXml, "phone")+"</phone></res>";
			}else{
				//�����֤���Ͳ�ѯʱ������鲻��������Ϣ�����ٴ��뿨������5��ѯ��
				//ԭ��:Ŀǰ֧�ְ�����Ϊ�����֤�����ա����ƿ����һ��յ������֤���ʹ������ƿ��źͻ����޷�����
				inputString=getInputParamString("5",reqXml);
				logger.error("�����֤������Ϣ��ѯ�ӿڶ��ε��á�baseinfo.getOutpatientInfo��-->��"+hisInterface+"����Σ�"+inputString);
				inputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface, inputString);								
				resultXml= xmlRequest.request(inputString);	
				resultXml=CommonUtils.convertHisOutputParam(resultXml);
				resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");	
				logger.error("�����֤������Ϣ��ѯ�ӿڶ��ε��á�baseinfo.getOutpatientInfo��-->��"+hisInterface+"�����Σ�"+resultXml);
				if("0".equals(resCode)){
					GetOutpatientInfoResultV3 resultV3=convertHisStringToV3Object(reqXml,resultXml);						
					XStream xstream = UtilXml.getXStream(GetOutpatientInfoResultV3.class);					
					return xstream.toXML(resultV3);		
				}else if("-1".equals(resCode)){
					String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
					logger.error("�ӿڷ���ʧ�ܣ�resCode="+resCode+";errorMsg="+errorMsg);
					return CommonUtils.getSuccessMsg();
				}else if("1".equals(resCode)){			
					logger.error("�ӿ�ִ�гɹ���û�ж�Ӧ���ݣ�");												
					return CommonUtils.getErrorMsg("4004", "�þ�������Ϣ��δ�����ﴦ�Ǽ�");					
				}
			}
		}
		return CommonUtils.getErrorMsg();			
	}
	
	/**
	 * his����ַ�������
	 * @param reqXml
	 * @return
	 */
	private String getInputParamString(String cardType,String reqXml){		
		String healthCardNo=UtilXml.getValueByAllXml(reqXml, "healthCardNo");
		String patientName=UtilXml.getValueByAllXml(reqXml, "patientName");
		StringBuilder str=new StringBuilder(200);
		str.append("<request>");
		str.append("<params>");
		str.append("<branchCode></branchCode>");
		str.append("<patName>"+patientName+"</patName>");
		str.append("<patCardType>"+cardType+"</patCardType>");
		str.append("<patCardNo>"+healthCardNo+"</patCardNo>");
		str.append("</params>");
		str.append("</request>");
		return str.toString();
	}
	
	/**
	 * his����תV3����
	 * @param resultXml
	 * @return
	 * @throws DocumentException 
	 */
	@SuppressWarnings("unchecked")
	private GetOutpatientInfoResultV3 convertHisStringToV3Object(String reqXml,String resultXml) throws DocumentException{		
		String patientName=UtilXml.getValueByAllXml(reqXml, "patientName");
		GetOutpatientInfoResultV3 resultV3=new GetOutpatientInfoResultV3();
		Document document=DocumentHelper.parseText(resultXml);
		Element root=document.getRootElement();		
		List<Element> list=root.elements("result");
		if(list!=null&&list.size()>0){
			Element ele=list.get(0);
			resultV3.setPatientId(ele.elementText("patId"));;
			resultV3.setHealthCardNo(ele.elementText("patCardNo"));
			if (patientName.equals(ele.elementText("patName")) ) {
				resultV3.setResultCode("0");
				resultV3.setResultDesc("�ɹ�");				
				resultV3.setPatientName(patientName);
				resultV3.setGender(ele.elementText("patSex"));
				resultV3.setIdCardNo(ele.elementText("patIdNo"));
				resultV3.setBirthday(ele.elementText("patBirth"));
				resultV3.setPhone(ele.elementText("patMobile"));
				
				
			} else {
				resultV3.setResultCode("4002");
				resultV3.setResultDesc("����������ƥ��");
				//resultV3.setHealthCardNo(ele.elementText("patCardNo"));
				//resultV3.setPatientId(ele.elementText("patId"));;
			}
		}
		return resultV3;
	}
	
}
