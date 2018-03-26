package com.kingdeehit.mobile.his.xianggang.service.lis;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingdeehit.mobile.his.entities.V3.result.lis.GetLisReportItemResultV3;
import com.kingdeehit.mobile.his.entities.V3.result.lis.LisReportItemV3;
import com.kingdeehit.mobile.his.utils.Consts;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;

/**
 * 检验报告详细信息查询
 * @author tangfulin
 *
 */
public class getReportItem extends AbstractService {
	
	private static String hisInterface="jindielabresultitem";

	@Override
	public String execute(String reqXml) throws Exception {
		String inspectionId=UtilXml.getValueByAllXml(reqXml, "inspectionId");
		//String inputString=getInputParamString(reqXml);
		logger.error("检验报告详细信息查询接口【getReportItem】-->【"+hisInterface+"】入参：inspectionId="+inspectionId);
		
//		LabresultdetailStub stub=new LabresultdetailStub();
//		Labresultdetail labresultdetail=new Labresultdetail();
//		labresultdetail.setInspectId(inspectionId);		
//		labresultdetail.setPassword(ParamConstants.USER_PASSWORD);
//		labresultdetail.setUserId(ParamConstants.USER_ID);
//		LabresultdetailResponse response=stub.labresultdetail(labresultdetail);
//		String  resultXml=response.get_return();
//		logger.error("接口地址："+Consts.HIS_SERVICE_URL4);
//		AxisDynamicClient client=AxisDynamicClient.getInstance(Consts.HIS_SERVICE_URL4);
//		String resultXml=(String)client.invokeWs(hisInterface, new Object[]{inspectionId,ParamConstants.USER_ID,ParamConstants.USER_PASSWORD}, new Class[]{String.class});
		
		
		String inputString=CommonUtils.convertLisReportDetailHisInputString(inspectionId);
		HttpRequestService xmlRequest = HttpRequestService.getInstance(Consts.HIS_SERVICE_URL4);					
		String resultXml= xmlRequest.request(inputString);	
		resultXml=CommonUtils.convertHisOutputParam(resultXml);		
		logger.error("检验报告详细信息查询接口【getReportItem】-->【"+hisInterface+"】出参："+resultXml);
		String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");
		if("-1".equals(resCode)){
			String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
			logger.error("接口返回失败！resCode="+resCode+";errorMsg="+errorMsg);
			return CommonUtils.getSuccessMsg();
		}else if("1".equals(resCode)){			
			logger.error("接口执行成功但没有对应数据！");
			return CommonUtils.getSuccessMsg();
		}		
		GetLisReportItemResultV3 resultV3=convertHisStringToV3Object(resultXml);
		XStream xstream = UtilXml.getXStream(GetLisReportItemResultV3.class);					
		return xstream.toXML(resultV3);
	}
	
	/**
	 * his入参字符串构造
	 * @param reqXml
	 * @return
	 */
	private String getInputParamString(String reqXml){		
		String inspectionId=UtilXml.getValueByAllXml(reqXml, "inspectionId");
		StringBuilder str=new StringBuilder(200);
		str.append("<request>");
		str.append("<params>");
		str.append("<branchCode></branchCode>");
		str.append("<inspectId>"+inspectionId+"</inspectId>");
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
	private GetLisReportItemResultV3 convertHisStringToV3Object(String resultXml) throws DocumentException{		
		GetLisReportItemResultV3 resultV3=new GetLisReportItemResultV3();
		Document document=DocumentHelper.parseText(resultXml);
		Element root=document.getRootElement();		
		Element row=root.element("result");
		List<Element> list=row.elements("item");
		List<LisReportItemV3> payInfoList=new ArrayList<LisReportItemV3>();
		for(Element ele:list){
			LisReportItemV3 reportV3=new LisReportItemV3();
			reportV3.setItemId("");
			reportV3.setItemName(ele.elementText("itemname"));
			reportV3.setOrderNo("0");
			reportV3.setTestResult(ele.elementText("result"));
			reportV3.setUnit(ele.elementText("unit"));
			reportV3.setItemRef(ele.elementText("refrange"));
			String abnormal=ele.elementText("abnormal");
			if("0".equals(abnormal)){
				reportV3.setQuaResult("");	
			}else if("1".equals(abnormal)){
				reportV3.setQuaResult("↑");	
			}else if("2".equals(abnormal)){
				reportV3.setQuaResult("↓");	
			}else if("3".equals(abnormal)){
				reportV3.setQuaResult("N");	
			}					
			reportV3.setTestDate("");							
			payInfoList.add(reportV3);
		}		
		resultV3.setResultCode("0");
		resultV3.setResultDesc("成功");
		resultV3.setReport(payInfoList);		
		return resultV3;
	}
}
