package com.kingdeehit.mobile.his.xianggang.service.inpatient;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingdeehit.mobile.his.entities.V3.result.inpatient.GetPrepayRecordResultItemV3;
import com.kingdeehit.mobile.his.entities.V3.result.inpatient.GetPrepayRecordResultV3;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;


/**
 * Ԥ�ɽ��ѯ�ӿ�
 * @author tangfulin
 *
 */
public class getPrepayRecord extends AbstractService {
	
	private static String hisInterface = "getDepositList";
	

	@Override
	public String execute(String reqXml) throws Exception {
		String inputString=getInputParamString(reqXml);		
		logger.error("Ԥ�ɽ��ѯ�ӿڡ�inpatient.getPrepayRecord��-->��"+hisInterface+"����Σ�"+inputString);
		inputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface, inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();					
		String resultXml= xmlRequest.request(inputString);	
		resultXml=CommonUtils.convertHisOutputParam(resultXml);
		logger.error("Ԥ�ɽ��ѯ�ӿڡ�inpatient.getPrepayRecord��-->��"+hisInterface+"�����Σ�"+resultXml);		
		String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");
		if("-1".equals(resCode)){
			String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
			logger.error("�ӿڷ���ʧ�ܣ�resCode="+resCode+";errorMsg="+errorMsg);
			return CommonUtils.getSuccessMsg();
		}else if("1".equals(resCode)){			
			logger.error("�ӿ�ִ�гɹ���û�ж�Ӧ���ݣ�");
			return CommonUtils.getSuccessMsg();
		}
		GetPrepayRecordResultV3 resultV3=convertHisStringToV3Object(resultXml);						
		XStream xstream = UtilXml.getXStream(GetPrepayRecordResultV3.class);			
		return xstream.toXML(resultV3);			
	}
	
	/**
	 * ԤԼ�Һ�his����ַ�������
	 * @param reqXml
	 * @return
	 */
	private String getInputParamString(String reqXml){
		String inpatientId=UtilXml.getValueByAllXml(reqXml, "inpatientId");		
		StringBuilder str=new StringBuilder(200);
		str.append("<request><params>");
		str.append("<branchCode></branchCode>");
		str.append("<patientId>"+inpatientId+"</patientId>");
		str.append("<admissionNo>"+inpatientId+"</admissionNo>");
		str.append("<inTime>1</inTime>");
		str.append("<patCardNo></patCardNo>");
		str.append("<beginDate></beginDate>");
		str.append("<endDate></endDate>");
		str.append("<payMode>1</payMode>");
		str.append("<psOrdNum></psOrdNum>");
		str.append("</params></request>");		
		return str.toString();
	}
	
	
	/**
	 * his����תV3����
	 * @param orderString
	 * @return
	 * @throws DocumentException 
	 */
	@SuppressWarnings("unchecked")
	private GetPrepayRecordResultV3 convertHisStringToV3Object(String resultXml) throws DocumentException{		
		GetPrepayRecordResultV3 resultV3=new GetPrepayRecordResultV3();
		Document document=DocumentHelper.parseText(resultXml);
		Element element=document.getRootElement();		
		Element res=element.element("result");		
		List<Element> list=res.elements("item");
		List<GetPrepayRecordResultItemV3> payInfoList=new ArrayList<GetPrepayRecordResultItemV3>();
		for(Element ele:list){
			GetPrepayRecordResultItemV3 resultItem=new GetPrepayRecordResultItemV3();			
			resultItem.setPayTime(ele.elementText("payTime"));
			resultItem.setPayAmout(ele.elementText("amout"));
			resultItem.setPayMode(ele.elementText("payMode"));			
			resultItem.setPayFlag("");
			payInfoList.add(resultItem);
		}	
		resultV3.setList(payInfoList);
		resultV3.setResultCode("0");
		resultV3.setResultDesc("�ɹ�");		
		return resultV3;
	}
}
