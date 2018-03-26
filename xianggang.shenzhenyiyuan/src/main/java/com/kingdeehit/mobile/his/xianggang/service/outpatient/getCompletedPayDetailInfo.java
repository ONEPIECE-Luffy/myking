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
 * 门诊已缴费记录明细查询
 * @author tangfulin
 */
public class getCompletedPayDetailInfo extends AbstractService {
	
	private static String hisInterface="getPayFeeDetail";

	@Override
	public String execute(String reqXml) throws Exception {
		String inputString=getInputParamString(reqXml);
		logger.error("门诊已缴费记录明细查询【outpatient.getCompletedPayDetailInfo】-->【"+hisInterface+"】入参："+inputString);		
		inputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface, inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();					
		String resultXml= xmlRequest.request(inputString);	
		resultXml=CommonUtils.convertHisOutputParam(resultXml);		
		logger.error("门诊已缴费记录明细查询【outpatient.getCompletedPayDetailInfo】-->【"+hisInterface+"】出参："+resultXml);		
		String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");
		if("-1".equals(resCode)){
			String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
			logger.error("接口返回失败！resCode="+resCode+";errorMsg="+errorMsg);
			return CommonUtils.getSuccessMsg();
		}else if("1".equals(resCode)){			
			logger.error("接口执行成功但没有对应数据！");
			return CommonUtils.getSuccessMsg();
		}	
		GetCompletedPayDetailInfoResultV3 resultResult= convertHisStringToV3Object(resultXml);							
		XStream xstream = UtilXml.getXStream(GetCompletedPayDetailInfoResultV3.class);					
		return xstream.toXML(resultResult);					
	}
	
	/**
	 * 预约挂号his入参字符串构造
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
	 * his出参转V3出参
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
		resultV3.setResultDesc("成功");
		resultV3.setInfoList(payInfoList);		
		return resultV3;
	}
	
}
