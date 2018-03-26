package com.kingdeehit.mobile.his.xianggang.service.outpatient;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingdeehit.mobile.his.entities.V3.result.outpatient.CompletedPayInfo;
import com.kingdeehit.mobile.his.entities.V3.result.outpatient.GetCompletedPayInfoResultV3;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;

/**
 * 已缴费记录查询
 * @author tangfulin
 *
 */
public class getCompletedPayInfo extends AbstractService {
	
	private static String hisInterface="getPayList";

	@Override
	public String execute(String reqXml) throws Exception {		
		String inputString=getInputParamString(reqXml);
		logger.error("诊间付费待缴费详细查询接口【outpatient.getCompletedPayInfo】-->【"+hisInterface+"】入参："+inputString);		
		inputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface, inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();					
		String resultXml= xmlRequest.request(inputString);	
		resultXml=CommonUtils.convertHisOutputParam(resultXml);		
		logger.error("诊间付费待缴费详细查询接口【outpatient.getCompletedPayInfo】-->【"+hisInterface+"】出参："+resultXml);		
		String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");
		if("-1".equals(resCode)){
			String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
			logger.error("接口返回失败！resCode="+resCode+";errorMsg="+errorMsg);
			return CommonUtils.getSuccessMsg();
		}else if("1".equals(resCode)){			
			logger.error("接口执行成功但没有对应数据！");
			return CommonUtils.getSuccessMsg();
		}		
		GetCompletedPayInfoResultV3 resultResult= convertHisStringToV3Object(resultXml);							
		XStream xstream = UtilXml.getXStream(GetCompletedPayInfoResultV3.class);					
		return xstream.toXML(resultResult);		
	}
	
	
	/**
	 * his入参字符串构造
	 * @param reqXml
	 * @return
	 */
	private String getInputParamString(String reqXml){
		String hospitalId=UtilXml.getValueByAllXml(reqXml, "hospitalId");
		hospitalId=CommonUtils.getHospitalByBranchCode(hospitalId);
		String healthCardNo=UtilXml.getValueByAllXml(reqXml, "healthCardNo");
		String startDate=UtilXml.getValueByAllXml(reqXml, "startDate");
		String endDate=UtilXml.getValueByAllXml(reqXml, "endDate");	
		StringBuilder str=new StringBuilder(200);
		str.append("<request><params>");
		str.append("<branchCode>"+hospitalId+"</branchCode>");
		str.append("<patCardType>1</patCardType>");
		str.append("<patCardNo>"+healthCardNo+"</patCardNo>");
		str.append("<payMode>0</payMode>");
		str.append("<beginDate>"+startDate+"</beginDate>");
		str.append("<endDate>"+endDate+"</endDate>");
		str.append("<psOrdNum></psOrdNum>");		
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
	private GetCompletedPayInfoResultV3 convertHisStringToV3Object(String resultXml) throws DocumentException{		
		GetCompletedPayInfoResultV3 resultV3=new GetCompletedPayInfoResultV3();
		Document document=DocumentHelper.parseText(resultXml);
		Element element=document.getRootElement();		
		Element res=element.element("result");		
		List<Element> list=res.elements("item");
		List<CompletedPayInfo> payInfoList=new ArrayList<CompletedPayInfo>();
		for(Element ele:list){
			CompletedPayInfo payInfo=new CompletedPayInfo();
			payInfo.setClinicSeq(ele.elementText("hisOrdNum"));
			payInfo.setClinicTime(ele.elementText("payTime"));			
			payInfo.setDeptId("");
			payInfo.setDeptName(ele.elementText("deptName"));
			payInfo.setDoctorId("");
			payInfo.setDoctorName(ele.elementText("doctorName"));
			payInfo.setPayAmout(ele.elementText("payAmout"));
			payInfo.setPayMode("");
			payInfo.setRecPayAmout("0");
			payInfo.setRecPayAmout(ele.attributeValue("MZJE"));
			payInfo.setChargeDate(ele.elementText("payTime"));
			payInfo.setReceiptId(ele.elementText("receiptNum"));	
			payInfo.setRemark(ele.elementText("hisMessage"));
			payInfoList.add(payInfo);
		}		
		resultV3.setResultCode("0");
		resultV3.setResultDesc("成功");
		resultV3.setInfoList(payInfoList);		
		return resultV3;
	}
	
}
