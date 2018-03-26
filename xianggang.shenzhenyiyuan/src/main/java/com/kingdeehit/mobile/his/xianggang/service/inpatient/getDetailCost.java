package com.kingdeehit.mobile.his.xianggang.service.inpatient;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingdeehit.mobile.his.entities.V3.result.inpatient.GetDetailCostItemV3;
import com.kingdeehit.mobile.his.entities.V3.result.inpatient.GetDetailCostResultV3;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;

/**
 * סԺ������ϸ��ѯ�ӿ�
 * @author tangfulin
 */
public class getDetailCost extends AbstractService {
	
	private static String hisInterface = "getBedFeeItem";

	@Override
	public String execute(String reqXml) throws Exception {
		String inputString=getInputParamString(reqXml);
		logger.error("סԺ������ϸ��ѯ�ӿڡ�inpatient.getDetailCost��-->��"+hisInterface+"����Σ�"+inputString);
		inputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface, inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();					
		String resultXml= xmlRequest.request(inputString);	
		resultXml=CommonUtils.convertHisOutputParam(resultXml);
		logger.error("סԺ������ϸ��ѯ�ӿڡ�inpatient.getDetailCost-->��"+hisInterface+"�����Σ�"+resultXml);		
		String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");
		if("-1".equals(resCode)){
			String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
			logger.error("�ӿڷ���ʧ�ܣ�resCode="+resCode+";errorMsg="+errorMsg);
			return CommonUtils.getSuccessMsg();
		}else if("1".equals(resCode)){			
			logger.error("�ӿ�ִ�гɹ���û�ж�Ӧ���ݣ�");
			return CommonUtils.getSuccessMsg();
		}
		GetDetailCostResultV3 resultV3=convertHisStringToV3Object(resultXml);						
		XStream xstream = UtilXml.getXStream(GetDetailCostResultV3.class);			
		return xstream.toXML(resultV3);		
	}
	
	/**
	 * ԤԼ�Һ�his����ַ�������
	 * @param reqXml
	 * @return
	 */
	private String getInputParamString(String reqXml){
		String inpatientId=UtilXml.getValueByAllXml(reqXml, "inpatientId");
		String billDate=UtilXml.getValueByAllXml(reqXml, "billDate");
		String typeCode=UtilXml.getValueByAllXml(reqXml, "typeCode");
		StringBuilder str=new StringBuilder(200);
		str.append("<request><params>");
		str.append("<branchCode></branchCode>");
		str.append("<costDate>"+billDate+"</costDate>");
		str.append("<patientId>"+inpatientId+"</patientId>");
		str.append("<admissionNo>"+inpatientId+"</admissionNo>");
		str.append("<inTime>1</inTime>");
		str.append("<costType>"+typeCode+"</costType>");
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
	private GetDetailCostResultV3 convertHisStringToV3Object(String resultXml) throws DocumentException{		
		GetDetailCostResultV3 resultV3=new GetDetailCostResultV3();
		Document document=DocumentHelper.parseText(resultXml);
		Element element=document.getRootElement();		
		Element res=element.element("result");
		Element collect=res.element("collection");
		List<Element> list=collect.elements("item");		
		List<GetDetailCostItemV3> itemList=new ArrayList<GetDetailCostItemV3>();
		if(list!=null&&list.size()>0){			
			for(Element tmp:list){
				GetDetailCostItemV3 itemV3=new GetDetailCostItemV3();				
				itemV3.setDetailId(tmp.elementText("projectName"));
				itemV3.setDetailName(tmp.elementText("projectName"));
				itemV3.setDetailSpec(tmp.elementText("spec"));
				itemV3.setDetailPrice(tmp.elementText("price"));
				itemV3.setDetailCount(tmp.elementText("quantity"));
				itemV3.setDetailUnit(tmp.elementText("unit"));
				itemV3.setDetailAmout(tmp.elementText("amout"));
				itemList.add(itemV3);			
			}
		}	
		
		resultV3.setResultCode("0");
		resultV3.setResultDesc("�ɹ�");	
		resultV3.setList(itemList);
		return resultV3;
	}
}
