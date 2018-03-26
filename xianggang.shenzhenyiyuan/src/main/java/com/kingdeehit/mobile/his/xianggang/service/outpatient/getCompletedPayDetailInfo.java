package com.kingdeehit.mobile.his.xianggang.service.outpatient;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingdeehit.mobile.his.entities.V3.result.outpatient.CompletedPayDetailInfo;
import com.kingdeehit.mobile.his.entities.V3.result.outpatient.GetCompletedPayDetailInfoResultV3;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;

/**
 * �����ѽɷѼ�¼��ϸ��ѯ
 * @author tangfulin
 */
public class getCompletedPayDetailInfo extends AbstractService {
	
	private static String hisInterface="getPayFeeDetail";

	@Override
	public String execute(String reqXml) throws Exception {
		String inputString=getInputParamString(reqXml);
		logger.error("�����ѽɷѼ�¼��ϸ��ѯ��outpatient.getCompletedPayDetailInfo��-->��"+hisInterface+"����Σ�"+inputString);		
		inputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface, inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();					
		String resultXml= xmlRequest.request(inputString);	
		resultXml=CommonUtils.convertHisOutputParam(resultXml);		
		logger.error("�����ѽɷѼ�¼��ϸ��ѯ��outpatient.getCompletedPayDetailInfo��-->��"+hisInterface+"�����Σ�"+resultXml);		
		String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");
		if("-1".equals(resCode)){
			String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
			logger.error("�ӿڷ���ʧ�ܣ�resCode="+resCode+";errorMsg="+errorMsg);
			return CommonUtils.getSuccessMsg();
		}else if("1".equals(resCode)){			
			logger.error("�ӿ�ִ�гɹ���û�ж�Ӧ���ݣ�");
			return CommonUtils.getSuccessMsg();
		}	
		GetCompletedPayDetailInfoResultV3 resultResult= convertHisStringToV3Object(resultXml);							
		XStream xstream = UtilXml.getXStream(GetCompletedPayDetailInfoResultV3.class);					
		return xstream.toXML(resultResult);					
	}
	
	/**
	 * ԤԼ�Һ�his����ַ�������
	 * @param reqXml
	 * @return
	 */
	private String getInputParamString(String reqXml){
		String clinicSeq=UtilXml.getValueByAllXml(reqXml, "clinicSeq");		
		StringBuilder str=new StringBuilder(200);
		str.append("<request><params>");
		str.append("<branchCode></branchCode>");
		str.append("<hisOrdNum>"+clinicSeq+"</hisOrdNum>");		
		str.append("</params></request>");		
		return str.toString();
	}
	
	
	/**
	 * his����תV3����
	 * @param resultXml
	 * @return
	 * @throws DocumentException 
	 */
	@SuppressWarnings("unchecked")
	private GetCompletedPayDetailInfoResultV3 convertHisStringToV3Object(String resultXml) throws DocumentException{		
		GetCompletedPayDetailInfoResultV3 resultV3=new GetCompletedPayDetailInfoResultV3();
		Document document=DocumentHelper.parseText(resultXml);
		Element element=document.getRootElement();		
		Element res=element.element("result");
		Element collection=res.element("collection");		
		List<Element> list=collection.elements("item");
		List<CompletedPayDetailInfo> payInfoList=new ArrayList<CompletedPayDetailInfo>();
		for(Element ele:list){
			CompletedPayDetailInfo payInfo=new CompletedPayDetailInfo();
			payInfo.setDetailFee(ele.elementText("itemType"));
			payInfo.setDetailId(ele.elementText("mzFeeId"));
			payInfo.setDetailName(ele.elementText("itemName"));
			payInfo.setDetailCount(ele.elementText("itemNumber"));
			payInfo.setDetailUnit("");
			payInfo.setDetailAmout(ele.elementText("itemTotalFee"));
			payInfo.setDetailSpec(ele.elementText("itemSpec"));
			payInfo.setDetailPrice(ele.elementText("itemPrice"));			
			payInfoList.add(payInfo);
		}		
		resultV3.setResultCode("0");
		resultV3.setResultDesc("�ɹ�");
		resultV3.setInfoList(payInfoList);		
		return resultV3;
	}
	
}
