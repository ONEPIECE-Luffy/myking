package com.kingdeehit.mobile.his.xianggang.service.register;
import org.apache.commons.lang.StringUtils;

import com.kingdeehit.mobile.his.xianggang.constant.ParamConstants;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.StringUtil;
import com.kingdeehit.mobile.utils.UtilXml;


/**
 * 当天挂号解除锁定接口
 * @author tangfulin
 *
 */
public class unlockReg extends AbstractService{
	
	private static String hisInterface="appointmentCancel";
	
	@Override
	public String execute(String reqXml) throws Exception {
		
		//是否需要进行判断，是否是第三方号源，如果是则不进行释放号源，直接返回成功
		//在这里怎么判断，释放的号源是否是通过我们的渠道预定的？
	 
		if (isOtherPlatform(reqXml)) { //
			return CommonUtils.getSuccessMsg();
		}else{
			String inputString = getInputParamString(reqXml);
			logger.error("当天挂号解除锁定【register.unlockReg】-->【appointmentCancel】入参：user="
					+ ParamConstants.USER
					+ ";password="
					+ ParamConstants.PASSWORD + ";parameter=" + inputString);
			inputString = CommonUtils.convertHisInputParam(hisInterface,
					inputString);
			HttpRequestService xmlRequest = HttpRequestService.getInstance();
			String resultXml = xmlRequest.request(inputString);
			resultXml = CommonUtils.convertABSOutputParam(resultXml);
			logger.error("当天挂号解除锁定【register.unlockReg】-->【appointmentCancel】出参："
					+ resultXml);
			String resCode = UtilXml.getValueByAllXml(resultXml, "STATE");
			if (StringUtils.isBlank(resCode) || Integer.parseInt(resCode) <= 0) {
				return CommonUtils.getErrorMsg();
			}
			return CommonUtils.getSuccessMsg();
			
		}
	}
	/**
	 * 根据预约号判断该预约是否是其他渠道
	 * @author YJB
	 * @date 2017年9月8日 下午3:10:41
	 * @param bookingNo
	 * @return
	 */
	private boolean isOtherPlatform(String reqXml) {
		String lockId = UtilXml.getValueByAllXml(reqXml, "lockId");
		return CommonUtils.isOtherPlatformByOrderId(lockId);
	}

	/**
	 * his入参字符串构造
	 * @param reqXml
	 * @return
	 */
	private String getInputParamString(String reqXml){		
		String infoSeq=UtilXml.getValueByAllXml(reqXml, "infoSeq");	
		StringBuilder str=new StringBuilder(200);		
		str.append("<PARAMETER>");
		str.append("<APPTNO>"+infoSeq+"</APPTNO>");
		str.append("<CANCELOPERATOR>Kingdee</CANCELOPERATOR>");
		str.append("</PARAMETER>");		
		return str.toString();
	}
	
}
