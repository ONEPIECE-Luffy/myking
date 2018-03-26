package com.kingdeehit.mobile.his.xianggang.service.support;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingdeehit.mobile.his.entities.V3.result.support.GetGuideListResultV3;
import com.kingdeehit.mobile.his.entities.V3.result.support.GuideInfo;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;

/**
 * 缴费成功指引单列表
 * @author tangfulin
 */
public class getGuideList extends AbstractService {
	
	private static String hisInterface="getGuideList";
	

	@Override
	public String execute(String reqXml) throws Exception {
		String inputString=getInputParamString(reqXml);	
		String convertInputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface, inputString);
		logger.error("缴费成功指引单列表接口【support.getGuideList】-->【"+hisInterface+"】入参："+inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();					
		String resultXml= xmlRequest.request(convertInputString);	
		resultXml=CommonUtils.convertHisOutputParam(resultXml);		
		logger.error("缴费成功指引单列表接口【support.getGuideList】-->【"+hisInterface+"】出参："+resultXml);
		String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");
		if("-1".equals(resCode)){
			String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
			logger.error("接口返回失败！resCode="+resCode+";errorMsg="+errorMsg);
			return CommonUtils.getSuccessMsg();
		}else if("1".equals(resCode)){			
			logger.error("接口执行成功但没有对应数据！");
			return CommonUtils.getSuccessMsg();
		}	
		GetGuideListResultV3 resultV3=convertHisStringToV3String(resultXml);						
		XStream xstream = UtilXml.getXStream(GetGuideListResultV3.class);			
		return xstream.toXML(resultV3);		
	}
	
	/**
	 * V3入参转his入参
	 * @param req
	 * @return
	 */
	private String getInputParamString(String req){		
		String receiptId=UtilXml.getValueByAllXml(req, "receiptId");
		StringBuilder str=new StringBuilder(100);
		str.append("<request><params>");
		str.append("<branchCode></branchCode>");
		str.append("<patCardType>1</patCardType>");
		str.append("<patCardNo>"+receiptId+"</patCardNo>");
		str.append("<receiptNum>"+receiptId+"</receiptNum>");
		str.append("</params></request>");
		return str.toString();
	}
	
	/**
	 * his出参转V3出参
	 * @param orderString
	 * @return
	 * @throws DocumentException 
	 */
	private GetGuideListResultV3 convertHisStringToV3String(String resultXml) throws DocumentException{	
		GetGuideListResultV3 resultV3=new GetGuideListResultV3();
		Document document=DocumentHelper.parseText(resultXml);
		Element element=document.getRootElement();		
		Element res=element.element("result");		
		List<Element> list=res.elements("item");		
		List<GuideInfo> guideList=new ArrayList<GuideInfo>();
		for(Element ele:list){
			GuideInfo guidInfo=new GuideInfo();
			guidInfo.setExecDeptId(ele.elementText("execDeptId"));
			guidInfo.setExecDeptName(ele.elementText("execDeptName"));
			guidInfo.setExecDeptLocation(ele.elementText("execDeptLocation"));
			guidInfo.setExecDesc(ele.elementText("execDesc"));
			guidInfo.setItemName(ele.elementText("itemName"));
			guidInfo.setRemark(ele.elementText("remark"));
			guideList.add(guidInfo);
		}
		resultV3.setResultCode("0");
		resultV3.setResultDesc("成功");
		resultV3.setInfoList(guideList);
		return resultV3;
	}
	
}
