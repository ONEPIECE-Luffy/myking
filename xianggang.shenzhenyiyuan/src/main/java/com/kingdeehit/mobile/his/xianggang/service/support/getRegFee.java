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
 * ��ȡ�Żݷ���
 *
 */
public class getRegFee extends AbstractService {
	
	private static String hisInterface="getRegLevel";
	
	@Override
	public String execute(String reqXml) throws Exception {		
		String svObjectId=UtilXml.getValueByAllXml(reqXml, "svObjectId");
		if("01".equals(svObjectId)){
			return  "<res><yhFee>0</yhFee><svObject>��ͨ</svObject></res>";
		}else if("02".equals(svObjectId)){
			String inputString=getInputParamString(reqXml);	
			String convertInputString=CommonUtils.convertHisInputParam(hisInterface, inputString);
			logger.error("��ȡ�Żݷ��á�support.getRegFee��-->��"+hisInterface+"����Σ�user="+ParamConstants.USER+";password="+ParamConstants.PASSWORD+";parameter="+inputString);
			HttpRequestService xmlRequest = HttpRequestService.getInstance();					
			String resultXml= xmlRequest.request(convertInputString);	
			resultXml=CommonUtils.convertABSOutputParam(resultXml);	
			logger.error("��ȡ�Żݷ��á�support.getRegFee��-->��"+hisInterface+"�����Σ�"+resultXml);
			String resCode=UtilXml.getValueByAllXml(resultXml, "STATE");
			if(StringUtils.isBlank(resCode)||Integer.parseInt(resCode)<=0){
				String err=UtilXml.getValueByAllXml(resultXml, "MSG");
				logger.error("�ӿڵ��÷��ؽ��ʧ�ܣ�resCode="+resCode+";msg="+err);
				return CommonUtils.getSuccessMsg();	
			}	
			return convertHisStringToV3String(resultXml);			
		}
		return "";
	}
	
	/**
	 * V3���תhis���
	 * @param req
	 * @return
	 */
	private String getInputParamString(String req){		
		/*String scheduleId=UtilXml.getValueByAllXml(req, "scheduleId");
		String[] tmp=null;
		logger.info("�Ű�ţ�"+scheduleId);
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
	 * his����תV3����
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
		str.append("<resultDesc>�ɹ�</resultDesc>");
		str.append("<yhFee>"+yhFee+"</yhFee>");
		str.append("<svObject>��ͨҽ��</svObject>");
		str.append("<medicareSettleLogId>"+StringUtil.generateUuid()+"</medicareSettleLogId>");
		str.append("<cashFee>0</cashFee>");
		str.append("<insuranFee>"+CommonUtils.convertUnitToMinute(insuFee)+"</insuranFee>");		
		str.append("</res>");
		return str.toString();
	}
	
}
