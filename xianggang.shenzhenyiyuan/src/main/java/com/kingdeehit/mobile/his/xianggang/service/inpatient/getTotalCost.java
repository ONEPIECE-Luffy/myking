package com.kingdeehit.mobile.his.xianggang.service.inpatient;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingdeehit.mobile.his.entities.V3.result.inpatient.GetTotalCostItemV3;
import com.kingdeehit.mobile.his.entities.V3.result.inpatient.GetTotalCostResultV3;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;

/**
 * סԺ���û��ܲ�ѯ�ӿ�
 * @author tangfulin
 */
public class getTotalCost extends AbstractService {
	
	private static String hisInterface="getPerBedFee";

	@Override
	public String execute(String reqXml) throws Exception {
		String inputString=getInputParamString(reqXml);		
		logger.error("סԺ���û��ܲ�ѯ�ӿڡ�inpatient.getTotalCost��-->��"+hisInterface+"����Σ�"+inputString);
		inputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface, inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();					
		String resultXml= xmlRequest.request(inputString);	
		resultXml=CommonUtils.convertHisOutputParam(resultXml);		
		logger.error("סԺ���û��ܲ�ѯ�ӿڡ�inpatient.getTotalCost��-->��"+hisInterface+"�����Σ�"+resultXml);
		String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");
		if("-1".equals(resCode)){
			String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
			logger.error("�ӿڷ���ʧ�ܣ�resCode="+resCode+";errorMsg="+errorMsg);
			return CommonUtils.getSuccessMsg();
		}else if("1".equals(resCode)){			
			logger.error("�ӿ�ִ�гɹ���û�ж�Ӧ���ݣ�");
			return CommonUtils.getSuccessMsg();
		}
		String inpatientId=UtilXml.getValueByAllXml(reqXml, "inpatientId");	
		GetTotalCostResultV3 resultV3=convertHisStringToV3Object(inpatientId,resultXml);						
		XStream xstream = UtilXml.getXStream(GetTotalCostResultV3.class);			
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
		str.append("<costDate></costDate>");
		str.append("<patientId>"+inpatientId+"</patientId>");
		str.append("<admissionNo>"+inpatientId+"</admissionNo>");
		str.append("<inTime>1</inTime>");		
		str.append("</params></request>");		
		return str.toString();
	}
	
	
	/**
	 * his����תV3����
	 * @param orderString
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private GetTotalCostResultV3 convertHisStringToV3Object(String patientId,String resultXml) throws Exception{		
		GetTotalCostResultV3 resultV3=new GetTotalCostResultV3();
		Document document=DocumentHelper.parseText(resultXml);
		Element element=document.getRootElement();		
		Element res=element.element("result");		
		List<Element> list=res.elements("item");		
		List<GetTotalCostItemV3> billList=new ArrayList<GetTotalCostItemV3>();
		List<GetTotalCostItemV3> dabaoList=new ArrayList<GetTotalCostItemV3>();
		/*if(list!=null&&list.size()>0){			
			for(Element tmp:list){
				GetTotalCostItemV3 billInfo=new GetTotalCostItemV3();
				billInfo.setTypeAmout(tmp.elementText("costAmout"));
				billInfo.setTypeCode(tmp.elementText("costType"));
				billInfo.setTypeName(tmp.elementText("costName"));
				billList.add(billInfo);
			}
		}*/
		//������ϸ������������...�ѡ���ֻ��ʾ�����...�ѡ���һ�������������Ŀ����ʾ��ϸ��Ŀ
		String money = "";
		if(list!=null&&list.size()>0){			
			for(Element tmp:list){
				String name = tmp.elementText("costName");
				GetTotalCostItemV3 billInfo=new GetTotalCostItemV3();
				billInfo.setTypeAmout(tmp.elementText("costAmout"));
				billInfo.setTypeCode(tmp.elementText("costType"));
				billInfo.setTypeName(tmp.elementText("costName"));
				if (name.contains("���")) {
					money = tmp.elementText("costAmout");
					dabaoList.add(billInfo);
					break;
				}
				billList.add(billInfo);
			}
		}
		
	
		
		String inputString=getFeeInputParamString(patientId);
		String hisInterface="getBedFee";
		logger.error("��inpatient.getTotalCost��-->��"+hisInterface+"����Σ�"+inputString);
		inputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface, inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();					
		String result= xmlRequest.request(inputString);	
		result=CommonUtils.convertHisOutputParam(result);			
		logger.error("��inpatient.getTotalCost��-->��"+hisInterface+"�����Σ�"+result);
		String resCode=UtilXml.getValueByAllXml(result, "resultCode");		
		if(!"0".equals(resCode)){
			String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
			logger.error("�ӿڷ���ʧ�ܣ�resCode="+resCode+";errorMsg="+errorMsg);				
		}else{	
			Document doc=DocumentHelper.parseText(result);
			Element ele2=doc.getRootElement();		
			Element res2=ele2.element("result");				
			resultV3.setTotalAmout(res2.elementText("totalFee"));
			resultV3.setPrepayAmout(res2.elementText("payedFee"));			
			resultV3.setValidPrepayAmout(res2.elementText("payedFee"));
			resultV3.setUnsettled(res2.elementText("totalFee"));
		}
		//�������סԺ�����
		if (dabaoList.size() > 0) {
			resultV3.setList(dabaoList);
			resultV3.setTotalAmout(money);
			resultV3.setUnsettled(money);
		} else {
			resultV3.setList(billList);
		}
		resultV3.setResultCode("0");
		resultV3.setResultDesc("�ɹ�");		
		return resultV3;
	}
	
	/**
	 * his����ַ�������
	 * @param patientId
	 * @param admissionNo
	 * @return
	 */
	private String getFeeInputParamString(String patientId){		
		StringBuilder str=new StringBuilder(200);
		str.append("<request>");
		str.append("<params>");
		str.append("<branchCode></branchCode>");
		str.append("<patientId>"+patientId+"</patientId>");
		str.append("<admissionNo>"+patientId+"</admissionNo>");
		str.append("<inTime>1</inTime>");
		str.append("</params>");
		str.append("</request>");
		return str.toString();
	}
	
	/*public static void main(String[] args) {
		String xml = "<response><resultCode>0</resultCode><resultMessage>���ýӿ�������</resultMessage><result><deptName>B5(��)�ǿ�</deptName><bedno>110961</bedno><balance>-525700</balance><destination></destination><todayAmout>1025700</todayAmout><item><costType>097</costType><costName>סԺ���ִ����</costName><costAmout>320000</costAmout></item><item><costType>009</costType><costName>��λ��</costName><costAmout>162000</costAmout></item><item><costType>082</costType><costName>ȡů��</costName><costAmout>0</costAmout></item><item><costType>030</costType><costName>�����</costName><costAmout>2500</costAmout></item><item><costType>006</costType><costName>���Ʒ�</costName><costAmout>541200</costAmout></item></result></response>";
		try {
			List<GetTotalCostItemV3> list = new getTotalCost().convertHisStringToV3Object("123",xml).getList();
			System.out.println(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
}
