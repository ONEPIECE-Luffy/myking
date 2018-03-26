package com.kingdeehit.mobile.his.xianggang.service.lis;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingdeehit.mobile.his.entities.V3.result.lis.GetLisReportResultV3;
import com.kingdeehit.mobile.his.entities.V3.result.lis.LisReportV3;
import com.kingdeehit.mobile.his.utils.Consts;
import com.kingdeehit.mobile.his.xianggang.constant.ParamConstants;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;

/**
 * 检验报告列表查询
 * @author tangfulin
 *
 */
public class getReport extends AbstractService {
	
	private static String hisInterface="labresultlist";

	@Override
	public String execute(String reqXml) throws Exception {
		//String inputString=getInputParamString(reqXml);
		String healthCardNo=UtilXml.getValueByAllXml(reqXml, "healthCardNo");		
		String beginDate=UtilXml.getValueByAllXml(reqXml, "beginDate");
		String endDate=UtilXml.getValueByAllXml(reqXml, "endDate");
		logger.error("检验报告列表查询接口【lis.getReport】-->【"+hisInterface+"】healthCardNo="+healthCardNo+",beginDate="+beginDate+",endDate="+endDate+",userId="+ParamConstants.USER_ID+",password="+ParamConstants.USER_PASSWORD);		
		String inputString=CommonUtils.convertLisReportHisInputString(healthCardNo, beginDate,endDate);
		HttpRequestService xmlRequest = HttpRequestService.getInstance(Consts.HIS_SERVICE_URL3);					
		String resultXml= xmlRequest.request(inputString);	
		resultXml=CommonUtils.convertHisOutputParam(resultXml);		
		logger.error("检验报告列表查询接口【lis.getReport】-->【"+hisInterface+"】出参："+resultXml);		
		String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");
		if("-1".equals(resCode)){
			String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
			logger.error("接口返回失败！resCode="+resCode+";errorMsg="+errorMsg);
			return CommonUtils.getSuccessMsg();
		}else if("1".equals(resCode)){			
			logger.error("接口执行成功但没有对应数据！");
			return CommonUtils.getSuccessMsg();
		}	
		GetLisReportResultV3 resultV3=convertHisStringToV3Object(resultXml);
		XStream xstream = UtilXml.getXStream(GetLisReportResultV3.class);					
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
	private GetLisReportResultV3 convertHisStringToV3Object(String resultXml) throws DocumentException{		
		GetLisReportResultV3 resultV3=new GetLisReportResultV3();
		Document document=DocumentHelper.parseText(resultXml);
		Element root=document.getRootElement();		
		Element rows=root.element("result");		
		List<Element> list=rows.elements("item");
		List<LisReportV3> payInfoList=new ArrayList<LisReportV3>();
		for(Element ele:list){
			LisReportV3 reportV3=new LisReportV3();
			reportV3.setInspectionId(ele.elementText("inspectid"));
			reportV3.setInspectionName(ele.elementText("inspectname"));
			reportV3.setInspectionDate(ele.elementText("inspecttime"));
			//调整			
			reportV3.setStatus("1");			
			reportV3.setReportType("");
			reportV3.setPatientName("");
			reportV3.setPatientAge("");
			reportV3.setGender("");
			reportV3.setDeptName(ele.elementText("deptname"));
			reportV3.setClinicalDiagnosis("");
			reportV3.setReportDoctorName(ele.elementText("inspectdoctor"));					
			payInfoList.add(reportV3);
		}		
		resultV3.setResultCode("0");
		resultV3.setResultDesc("成功");
		resultV3.setReport(payInfoList);		
		return resultV3;
	}
}
