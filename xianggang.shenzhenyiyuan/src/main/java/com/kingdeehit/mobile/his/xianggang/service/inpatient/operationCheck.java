package com.kingdeehit.mobile.his.xianggang
.service.inpatient;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.UtilXml;

/**
 * @author xcz
 * @date 2015-12-14
 */
public class operationCheck extends AbstractService {
	
	private static String hisInterface="getPerBedFee";

	@Override
	public String execute(String reqXml) throws Exception {
		String inputString=getInputParamString(reqXml);		
		logger.error("患者住院业务功能检测接口【inpatient.operationCheck】-->【"+hisInterface+"】入参："+inputString);
		inputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface, inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();					
		String resultXml= xmlRequest.request(inputString);	
		resultXml=CommonUtils.convertHisOutputParam(resultXml);		
		logger.error("患者住院业务功能检测接口【inpatient.operationCheck】-->【"+hisInterface+"】出参："+resultXml);
		String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");
		if("-1".equals(resCode)){
			String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
			logger.error("接口返回失败！resCode="+resCode+";errorMsg="+errorMsg);
			return CommonUtils.getErrorMsg(errorMsg);
		}else if("1".equals(resCode)){			
			logger.error("接口执行成功但没有对应数据！");
			return CommonUtils.getErrorMsg();
		}else if("0".equals(resCode)){					
			return convertHisStringToV3String(resultXml);
		}
		return CommonUtils.getErrorMsg();
	}
	
	/**
	 * 预约挂号his入参字符串构造
	 * @param reqXml
	 * @return
	 */
	private String getInputParamString(String reqXml){
		String inpatienId=UtilXml.getValueByAllXml(reqXml, "inpatienId");						
		StringBuilder str=new StringBuilder(200);
		str.append("<request><params>");
		str.append("<branchCode></branchCode>");
		str.append("<patCardType></patCardType>");
		str.append("<patCardNo>"+inpatienId+"</patCardNo>");
		str.append("<admissionNo>"+inpatienId+"</admissionNo>");			
		str.append("</params></request>");		
		return str.toString();
	}
	
	/**
	 * his出参转V3出参
	 * @param orderString
	 * @return
	 */
	private String convertHisStringToV3String(String resultXml){	
		StringBuilder str=new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");		
		str.append("<res>");
		str.append("<resultCode>0</resultCode>");
		str.append("<resultDesc>成功</resultDesc>");
		str.append("<remark></remark>");		
		str.append("</res>");
		return str.toString();
	}
	
}
