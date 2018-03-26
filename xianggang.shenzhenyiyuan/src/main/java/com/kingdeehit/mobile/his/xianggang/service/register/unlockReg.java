package com.kingdeehit.mobile.his.xianggang.service.register;
import org.apache.commons.lang.StringUtils;

import com.kingdeehit.mobile.his.xianggang.constant.ParamConstants;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.StringUtil;
import com.kingdeehit.mobile.utils.UtilXml;


/**
 * ����ҺŽ�������ӿ�
 * @author tangfulin
 *
 */
public class unlockReg extends AbstractService{
	
	private static String hisInterface="appointmentCancel";
	
	@Override
	public String execute(String reqXml) throws Exception {
		
		//�Ƿ���Ҫ�����жϣ��Ƿ��ǵ�������Դ��������򲻽����ͷź�Դ��ֱ�ӷ��سɹ�
		//��������ô�жϣ��ͷŵĺ�Դ�Ƿ���ͨ�����ǵ�����Ԥ���ģ�
	 
		if (isOtherPlatform(reqXml)) { //
			return CommonUtils.getSuccessMsg();
		}else{
			String inputString = getInputParamString(reqXml);
			logger.error("����ҺŽ��������register.unlockReg��-->��appointmentCancel����Σ�user="
					+ ParamConstants.USER
					+ ";password="
					+ ParamConstants.PASSWORD + ";parameter=" + inputString);
			inputString = CommonUtils.convertHisInputParam(hisInterface,
					inputString);
			HttpRequestService xmlRequest = HttpRequestService.getInstance();
			String resultXml = xmlRequest.request(inputString);
			resultXml = CommonUtils.convertABSOutputParam(resultXml);
			logger.error("����ҺŽ��������register.unlockReg��-->��appointmentCancel�����Σ�"
					+ resultXml);
			String resCode = UtilXml.getValueByAllXml(resultXml, "STATE");
			if (StringUtils.isBlank(resCode) || Integer.parseInt(resCode) <= 0) {
				return CommonUtils.getErrorMsg();
			}
			return CommonUtils.getSuccessMsg();
			
		}
	}
	/**
	 * ����ԤԼ���жϸ�ԤԼ�Ƿ�����������
	 * @author YJB
	 * @date 2017��9��8�� ����3:10:41
	 * @param bookingNo
	 * @return
	 */
	private boolean isOtherPlatform(String reqXml) {
		String lockId = UtilXml.getValueByAllXml(reqXml, "lockId");
		return CommonUtils.isOtherPlatformByOrderId(lockId);
	}

	/**
	 * his����ַ�������
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
