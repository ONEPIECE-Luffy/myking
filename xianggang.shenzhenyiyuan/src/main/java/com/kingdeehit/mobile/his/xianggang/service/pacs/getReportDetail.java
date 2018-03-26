package com.kingdeehit.mobile.his.xianggang.service.pacs;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingdeehit.mobile.his.entities.V3.result.pacs.GetPacsReportItemResultV3;
import com.kingdeehit.mobile.his.utils.Consts;
import com.kingdeehit.mobile.his.xianggang.constant.ParamConstants;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;

/**
 * 查询检查报告明细查询
 * @author tangfulin
 *
 */
public class getReportDetail extends AbstractService {
	
	private static String hisInterface="examresultdetail";

	@Override
	public String execute(String reqXml) throws Exception {
		String reportId=UtilXml.getValueByAllXml(reqXml, "reportId");
		//String inputString=getInputParamString(reqXml);
		logger.error("检查报告明细查询接口【getReportDetail】-->【"+hisInterface+"】入参reportId=："+reportId);		
		String inputString=CommonUtils.convertPaceReportDetailHisInputString(reportId);
		HttpRequestService xmlRequest = HttpRequestService.getInstance(Consts.HIS_SERVICE_URL2);					
		String resultXml= xmlRequest.request(inputString);	
		resultXml=CommonUtils.convertHisOutputParam(resultXml);		
		logger.error("检查报告明细查询接口【getReportDetail】-->【"+hisInterface+"】出参："+resultXml);		
		String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");
		if("-1".equals(resCode)){
			String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
			logger.error("接口返回失败！resCode="+resCode+";errorMsg="+errorMsg);
			return CommonUtils.getSuccessMsg();
		}else if("1".equals(resCode)){			
			logger.error("接口执行成功但没有对应数据！");
			return CommonUtils.getSuccessMsg();
		}
		GetPacsReportItemResultV3 resultV3=convertHisStringToV3Object(resultXml);
		XStream xstream = UtilXml.getXStream(GetPacsReportItemResultV3.class);					
		return xstream.toXML(resultV3);				
	}
	
	/**
	 * his入参字符串构造
	 * @param reqXml
	 * @return
	 */
	private String getNewInputParamString(String reqXml){		
		String reportId=UtilXml.getValueByAllXml(reqXml, "reportId");
		StringBuilder str=new StringBuilder(200);
		str.append("<request>");
		str.append("<params>");		
		str.append("<checkId>"+reportId+"</checkId>");
		str.append("<userId>"+ParamConstants.USER_ID+"</userId>");
		str.append("<password>"+ParamConstants.USER_PASSWORD+"</password>");
		str.append("</params>");
		str.append("</request>");
		return str.toString();
	}
	
	/**
	 * his入参字符串构造
	 * @param reqXml
	 * @return
	 */
	private String getInputParamString(String reqXml){		
		String reportId=UtilXml.getValueByAllXml(reqXml, "reportId");
		StringBuilder str=new StringBuilder(200);
		str.append("<request>");
		str.append("<params>");
		str.append("<branchCode></branchCode>");
		str.append("<checkId>"+reportId+"</checkId>");
		str.append("<checkType></checkType>");
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
	private GetPacsReportItemResultV3 convertHisStringToV3Object(String resultXml) throws DocumentException{		
		GetPacsReportItemResultV3 resultV3=new GetPacsReportItemResultV3();
		Document document=DocumentHelper.parseText(resultXml);
		Element element=document.getRootElement();		
		Element res=element.element("result");		
		Element row=res.element("item");
		resultV3.setDeptName("");
		resultV3.setReportDoctorName("");
		resultV3.setCheckParts(row.elementText("checkpart"));
		resultV3.setExamination(row.elementText("checksituation"));
		resultV3.setDiagnosis(row.elementText("option"));
		resultV3.setCheckDoctorName("");
		resultV3.setExaminationDate("");								
		resultV3.setResultCode("0");
		resultV3.setResultDesc("成功");	
		return resultV3;
	}
}
