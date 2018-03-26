package com.kingdeehit.mobile.his.xianggang.service.appointment;
import org.apache.commons.lang.StringUtils;

import com.kingdeehit.mobile.his.xianggang.constant.ParamConstants;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.UtilXml;

/**
 * 取消预约
 * @author tangfulin
 *
 */
public class cancelOrder extends AbstractService{
	
	private static String hisInterface="appointmentCancel";

	@Override
	public String execute(String reqXml) throws Exception {		
		String inputString=getInputParamString(reqXml);	
		String convertInputString=CommonUtils.convertHisInputParam(hisInterface, inputString);
		logger.error("预约挂号接口【appointment.cancelOrder】-->【"+hisInterface+"】入参：user="+ParamConstants.USER+";password="+ParamConstants.PASSWORD+";parameter="+inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();					
		String resultXml= xmlRequest.request(convertInputString);	
		resultXml=CommonUtils.convertABSOutputParam(resultXml);	
		logger.error("预约挂号接口【appointment.cancelOrder】-->【"+hisInterface+"】出参："+resultXml);
		String resCode=UtilXml.getValueByAllXml(resultXml, "STATE");
		if(StringUtils.isBlank(resCode)||Integer.parseInt(resCode)<=0){
			return CommonUtils.getErrorMsg();
		}
		return CommonUtils.getSuccessMsg();
	}
	
	/**
	 * his入参字符串构造
	 * @param reqXml
	 * @return
	 */
	private String getInputParamString(String reqXml){		
		String bookingNo=UtilXml.getValueByAllXml(reqXml, "bookingNo");	
		StringBuilder str=new StringBuilder(200);
		str.append("<PARAMETER>");
		str.append("<APPTNO>"+bookingNo+"</APPTNO>");
		str.append("<CANCELOPERATOR>Kingdee</CANCELOPERATOR>");
		str.append("</PARAMETER>");
		return str.toString();
	}
	
	
}
