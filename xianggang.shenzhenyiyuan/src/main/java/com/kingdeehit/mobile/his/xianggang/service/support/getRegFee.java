/**
 *
 */
package com.kingdeehit.mobile.his.xianggang.service.support;

import org.apache.commons.lang.StringUtils;

import com.kingdeehit.mobile.his.xianggang.constant.ParamConstants;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.StringUtil;
import com.kingdeehit.mobile.utils.UtilXml;

/**
 * @author v
 * 获取优惠费用
 *
 */
public class getRegFee extends AbstractService {
	
	private static String hisInterface="getRegLevel";
	
	@Override
	public String execute(String reqXml) throws Exception {		
		String svObjectId=UtilXml.getValueByAllXml(reqXml, "svObjectId");
		if("01".equals(svObjectId)){
			return  "<res><yhFee>0</yhFee><svObject>普通</svObject></res>";
		}else if("02".equals(svObjectId)){
			String inputString=getInputParamString(reqXml);	
			String convertInputString=CommonUtils.convertHisInputParam(hisInterface, inputString);
			logger.error("获取优惠费用【support.getRegFee】-->【"+hisInterface+"】入参：user="+ParamConstants.USER+";password="+ParamConstants.PASSWORD+";parameter="+inputString);
			HttpRequestService xmlRequest = HttpRequestService.getInstance();					
			String resultXml= xmlRequest.request(convertInputString);	
			resultXml=CommonUtils.convertABSOutputParam(resultXml);	
			logger.error("获取优惠费用【support.getRegFee】-->【"+hisInterface+"】出参："+resultXml);
			String resCode=UtilXml.getValueByAllXml(resultXml, "STATE");
			if(StringUtils.isBlank(resCode)||Integer.parseInt(resCode)<=0){
				String err=UtilXml.getValueByAllXml(resultXml, "MSG");
				logger.error("接口调用返回结果失败：resCode="+resCode+";msg="+err);
				return CommonUtils.getSuccessMsg();	
			}	
			return convertHisStringToV3String(resultXml);			
		}
		return "";
	}
	
	/**
	 * V3入参转his入参
	 * @param req
	 * @return
	 */
	private String getInputParamString(String req){		
		/*String scheduleId=UtilXml.getValueByAllXml(req, "scheduleId");
		String[] tmp=null;
		logger.info("排班号："+scheduleId);
		if(StringUtils.isNotBlank(scheduleId)){
			tmp=scheduleId.split("@");
		}*/
		StringBuilder str=new StringBuilder(100);
		str.append("<PARAMETER>");
		//str.append("<REGLEVELID>"+tmp[1]+"</REGLEVELID>");
		str.append("<REGLEVELID>"+"7"+"</REGLEVELID>");
		str.append("<REGLEVELNAME></REGLEVELNAME>");
		str.append("</PARAMETER>");
		return str.toString();
	}

	/**
	 * his出参转V3出参
	 * @param orderString
	 * @return
	 */
	private String convertHisStringToV3String(String resultXml){		
		String insuFee=UtilXml.getValueByAllXml(resultXml, "SSTREATFEE");	
		String totalFee=UtilXml.getValueByAllXml(resultXml, "TREATFEE");
		double yhFee=CommonUtils.convertUnitToMinute(totalFee)-CommonUtils.convertUnitToMinute(insuFee);
		StringBuilder str=new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");		
		
		str.append("<res>");
		str.append("<resultCode>0</resultCode>");
		str.append("<resultDesc>成功</resultDesc>");
		str.append("<yhFee>"+yhFee+"</yhFee>");
		str.append("<svObject>普通医保</svObject>");
		str.append("<medicareSettleLogId>"+StringUtil.generateUuid()+"</medicareSettleLogId>");
		str.append("<cashFee>0</cashFee>");
		str.append("<insuranFee>"+CommonUtils.convertUnitToMinute(insuFee)+"</insuranFee>");		
		str.append("</res>");
		return str.toString();
	}
	
}
