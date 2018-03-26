package com.kingdeehit.mobile.his.xianggang.service.pacs;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingdeehit.mobile.his.entities.V3.result.pacs.GetPacsReportResultV3;
import com.kingdeehit.mobile.his.entities.V3.result.pacs.PacsReportV3;
import com.kingdeehit.mobile.his.utils.Consts;
import com.kingdeehit.mobile.his.xianggang.constant.ParamConstants;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;

/**
 * 查询检查报告列表
 * @author tangfulin
 * 
 */
public class getReport extends AbstractService {
	
	//private static String hisInterface="getExamineList";
	private static String hisInterface="examresultlist";

	@Override
	public String execute(String reqXml) throws Exception {
		String healthCardNo=UtilXml.getValueByAllXml(reqXml, "healthCardNo");		
		String beginDate=UtilXml.getValueByAllXml(reqXml, "beginDate");
		String endDate=UtilXml.getValueByAllXml(reqXml, "endDate");		
		logger.error("检查报告列表查询接口【pacs.getReport】-->【"+hisInterface+"】入参：healthCardNo="+healthCardNo+",beginDate="+beginDate+",endDate="+endDate+",userId="+ParamConstants.USER_ID+",password="+ParamConstants.USER_PASSWORD);		
		String inputString=CommonUtils.convertPaceReportHisInputString(healthCardNo, beginDate,endDate);
		HttpRequestService xmlRequest = HttpRequestService.getInstance(Consts.HIS_SERVICE_URL1);					
		String resultXml= xmlRequest.request(inputString);	
		resultXml=CommonUtils.convertHisOutputParam(resultXml);			
		logger.error("检查报告列表查询接口【pacs.getReport】-->【"+hisInterface+"】出参："+resultXml);		
		String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");
		if("-1".equals(resCode)){
			String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
			logger.error("接口返回失败！resCode="+resCode+";errorMsg="+errorMsg);
			return CommonUtils.getSuccessMsg();
		}else if("1".equals(resCode)){			
			logger.error("接口执行成功但没有对应数据！");
			return CommonUtils.getSuccessMsg();
		}
		GetPacsReportResultV3 resultV3=convertHisStringToV3Object(resultXml);
		XStream xstream = UtilXml.getXStream(GetPacsReportResultV3.class);					
		return xstream.toXML(resultV3);			
	}
	
	/**
	 * his入参字符串构造
	 * @param reqXml
	 * @return
	 */
	private String getInputParamString(String reqXml){		
		String healthCardNo=UtilXml.getValueByAllXml(reqXml, "healthCardNo");
		String inpatientId=UtilXml.getValueByAllXml(reqXml, "inpatientId");
		String beginDate=UtilXml.getValueByAllXml(reqXml, "beginDate");
		String endDate=UtilXml.getValueByAllXml(reqXml, "endDate");
		String cardType="";
		if(StringUtils.isNotBlank(healthCardNo)){			
			if(healthCardNo.length()==18){
				cardType="5";		//身份证
			}else{
				cardType="1";		//诊疗卡
			}			
		}
		StringBuilder str=new StringBuilder(200);
		str.append("<request>");
		str.append("<params>");
		str.append("<branchCode></branchCode>");
		str.append("<patCardType>"+cardType+"</patCardType>");
		str.append("<patCardNo>"+healthCardNo+"</patCardNo>");
		str.append("<admissionNo>"+inpatientId+"</admissionNo>");
		str.append("<beginDate>"+beginDate+"</beginDate>");
		str.append("<endDate>"+endDate+"</endDate>");
		str.append("</params>");
		str.append("</request>");
		return str.toString();
	}
	
	/**
	 * his出参转V3出参
	 * @param orderString
	 * @return
	 * @throws DocumentException 
	 */
	@SuppressWarnings("unchecked")
	private GetPacsReportResultV3 convertHisStringToV3Object(String resultXml) throws DocumentException{		
		GetPacsReportResultV3 resultV3=new GetPacsReportResultV3();
		Document document=DocumentHelper.parseText(resultXml);
		Element root=document.getRootElement();		
		Element rows=root.element("result");
		List<Element> list=rows.elements("item");
		List<PacsReportV3> payInfoList=new ArrayList<PacsReportV3>();
		for(Element ele:list){
			PacsReportV3 reportV3=new PacsReportV3();
			reportV3.setReportId(ele.elementText("checkid"));
			reportV3.setReportTitle(ele.elementText("checkname"));
			reportV3.setReportDate(ele.elementText("reporttime"));
			reportV3.setStatus("1");			
			reportV3.setPatientName("");
			reportV3.setPatientAge("");
			reportV3.setGender("");			
			reportV3.setClinicalDiagnosis("");							
			payInfoList.add(reportV3);
		}		
		resultV3.setResultCode("0");
		resultV3.setResultDesc("成功");
		resultV3.setReport(payInfoList);		
		return resultV3;
	}
}
