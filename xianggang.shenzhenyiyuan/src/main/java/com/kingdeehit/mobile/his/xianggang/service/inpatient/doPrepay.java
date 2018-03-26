package com.kingdeehit.mobile.his.xianggang.service.inpatient;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingdeehit.mobile.his.entities.V3.result.inpatient.DoPrepayResultV3;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;


/**
 * 住院预缴金接口
 * @author tangfulin
 *
 */
public class doPrepay extends AbstractService{
		
	private static String hisInterface="payDeposit";
	
	@Override
	public String execute(String reqXml) throws Exception {		
		try {
			String inputString=getInputParamString(reqXml);
			logger.error("住院预交金缴纳接口【inpatient.doPrepay】-->【"+hisInterface+"】入参："+inputString);
			inputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface, inputString);
			HttpRequestService xmlRequest = HttpRequestService.getInstance();					
			String resultXml= xmlRequest.request(inputString);	
			resultXml=CommonUtils.convertHisOutputParam(resultXml);	
			logger.error("住院预交金缴纳接口【inpatient.doPrepay】-->【"+hisInterface+"】出参："+resultXml);
			String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");
			if("-1".equals(resCode)){
				String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
				logger.error("接口返回失败！resCode="+resCode+";errorMsg="+errorMsg);
				return CommonUtils.getErrorMsg();
			}else if("1".equals(resCode)){			
				logger.error("接口执行成功但没有对应数据！");
				return CommonUtils.getErrorMsg();
			}else if("0".equals(resCode)){
				DoPrepayResultV3 resultV3=convertHisStringToV3Object(resultXml);						
				XStream xstream = UtilXml.getXStream(DoPrepayResultV3.class);			
				return xstream.toXML(resultV3);	
			}		
			String err=UtilXml.getValueByAllXml(resultXml, "ERR");
			return CommonUtils.getErrorMsg(err);
		} catch (Exception e) {
			logger.error(e);
			return CommonUtils.getErrorMsg("4201", "");
		}
	}
	
	/**
	 * his入参字符串构造
	 * @param reqXml
	 * @return
	 */
	private String getInputParamString(String reqXml){
		String inpatientId=UtilXml.getValueByAllXml(reqXml, "inpatientId");						
		StringBuilder str=new StringBuilder(200);
		str.append("<request><params>");
		str.append("<branchCode></branchCode>");		
		str.append("<patientId>"+inpatientId+"</patientId>");
		str.append("<admissionNo>"+inpatientId+"</admissionNo>");
		str.append("<inTime>1</inTime>");			
		str.append("<psOrdNum></psOrdNum>");
		str.append("<agtOrdNum></agtOrdNum>");
		str.append("<agtCode></agtCode>");
		str.append("<payMode></payMode>");
		str.append("<payAmout></payAmout>");
		str.append("<payTime></payTime>");
		str.append("</params></request>");
		return str.toString();
	}
	
	/**
	 * his出参转V3出参
	 * @param orderString
	 * @return
	 * @throws DocumentException 
	 */
	@SuppressWarnings("unchecked")
	private DoPrepayResultV3 convertHisStringToV3Object(String resultXml) throws DocumentException{		
		DoPrepayResultV3 resultV3=new DoPrepayResultV3();
		Document document=DocumentHelper.parseText(resultXml);
		Element element=document.getRootElement();		
		Element res=element.element("RES");		
		Element rows=res.element("Rows");
		List<Element> list=rows.elements("Row");
		if(list!=null&&list.size()>0){
			Element ele=list.get(0);
			resultV3.setReceiptId(ele.attributeValue("CSJH"));
			resultV3.setBalance(ele.attributeValue("MJE"));
			resultV3.setRemark(ele.attributeValue("CDESC"));	
		}
		resultV3.setResultCode("0");
		resultV3.setResultDesc("成功");			
		return resultV3;
	}
}
