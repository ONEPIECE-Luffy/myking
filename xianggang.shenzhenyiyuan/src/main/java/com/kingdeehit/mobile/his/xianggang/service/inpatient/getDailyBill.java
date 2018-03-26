package com.kingdeehit.mobile.his.xianggang.service.inpatient;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingdeehit.mobile.his.entities.V3.result.inpatient.DailyBillFeeInfoV3;
import com.kingdeehit.mobile.his.entities.V3.result.inpatient.GetDailyBillResultV3;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;


/**
 * סԺÿ���嵥��ѯ�ӿ�
 * @author tangfulin
 *
 */
public class getDailyBill extends AbstractService {
	
	private static String hisInterface = "getPerBedFee";

	@Override
	public String execute(String reqXml) throws Exception {
		String inputString=getInputParamString(reqXml);
		logger.error("סԺ����ÿ���嵥��ѯ�ӿڡ�inpatient.getDailyBill��-->��"+hisInterface+"����Σ�"+inputString);
		inputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface, inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();					
		String resultXml= xmlRequest.request(inputString);	
		resultXml=CommonUtils.convertHisOutputParam(resultXml);
		logger.error("סԺ����ÿ���嵥��ѯ�ӿڡ�inpatient.getDailyBill��-->��"+hisInterface+"�����Σ�"+resultXml);
		String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");
		if("-1".equals(resCode)){
			String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
			logger.error("�ӿڷ���ʧ�ܣ�resCode="+resCode+";errorMsg="+errorMsg);
			return CommonUtils.getSuccessMsg();
		}else if("1".equals(resCode)){			
			logger.error("�ӿ�ִ�гɹ���û�ж�Ӧ���ݣ�");
			return CommonUtils.getSuccessMsg();
		}
		GetDailyBillResultV3 resultV3=convertHisStringToV3Object(resultXml);						
		XStream xstream = UtilXml.getXStream(GetDailyBillResultV3.class);			
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
		StringBuilder str=new StringBuilder(200);
		str.append("<request><params>");
		str.append("<branchCode></branchCode>");
		str.append("<costDate>"+billDate+"</costDate>");
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
	 * @throws DocumentException 
	 */
	@SuppressWarnings("unchecked")
	private GetDailyBillResultV3 convertHisStringToV3Object(String resultXml) throws DocumentException{		
		GetDailyBillResultV3 resultV3=new GetDailyBillResultV3();
		Document document=DocumentHelper.parseText(resultXml);
		Element element=document.getRootElement();		
		Element res=element.element("result");		
		List<Element> list=res.elements("item");		
		List<DailyBillFeeInfoV3> billList=new ArrayList<DailyBillFeeInfoV3>();		
		List<DailyBillFeeInfoV3> dabaoList=new ArrayList<DailyBillFeeInfoV3>();		
		/*if(list!=null&&list.size()>0){			
			for(Element tmp:list){
				DailyBillFeeInfoV3 billInfo=new DailyBillFeeInfoV3();				
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
				DailyBillFeeInfoV3 billInfo=new DailyBillFeeInfoV3();				
				billInfo.setTypeAmout(tmp.elementText("costAmout"));
				billInfo.setTypeCode(tmp.elementText("costType"));
				billInfo.setTypeName(tmp.elementText("costName"));
				
				String name = tmp.elementText("costName");
				if (name.contains("���")) {
					money = tmp.elementText("costAmout");
					dabaoList.add(billInfo);
					break;
				}
				billList.add(billInfo);
			}
		}
		
		if (dabaoList.size() > 0) {
			resultV3.setList(dabaoList);
			resultV3.setTotalAmout(money);
		} else {
			resultV3.setTotalAmout(res.elementText("todayAmout"));
			resultV3.setList(billList);
		}
		//resultV3.setList(billList);
		resultV3.setPrepayAmout(res.elementText("balance"));
		resultV3.setSettled("");
		resultV3.setUnsettled("");
		resultV3.setBalance(res.elementText("balance"));	
		resultV3.setRemark(res.elementText("destination"));
		resultV3.setResultCode("0");
		resultV3.setResultDesc("�ɹ�");		
		return resultV3;
	}
}
