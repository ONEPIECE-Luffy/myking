package com.kingdeehit.mobile.his.xianggang.service.outpatient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingdeehit.mobile.his.entities.V3.result.outpatient.GetPayInfoResultV3;
import com.kingdeehit.mobile.his.entities.V3.result.outpatient.PayInfo;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.DateUtils;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;

/**
 * ���ɷѼ�¼�б��ѯ
 * @author tangfulin
 *
 */
public class getPayInfo extends AbstractService {
	
	private static String hisInterface="getMZFeeList";
	
	@Override
	public String execute(String reqXml) throws Exception {
		String hospitalId=UtilXml.getValueByAllXml(reqXml, "hospitalId");
		hospitalId=CommonUtils.getHospitalByBranchCode(hospitalId);
		String inputString=getInputParamString(reqXml);
		logger.error("���ɷѼ�¼�б��ѯ�ӿڡ�outpatient.getPayInfo��-->��"+hisInterface+"����Σ�"+inputString);		
		inputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface, inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();					
		String resultXml= xmlRequest.request(inputString);	
		resultXml=CommonUtils.convertHisOutputParam(resultXml);		
		logger.error("���ɷѼ�¼�б��ѯ�ӿڡ�outpatient.getPayInfo��-->��"+hisInterface+"�����Σ�"+resultXml);
		String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");
		if("-1".equals(resCode)){
			String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
			logger.error("�ӿڷ���ʧ�ܣ�resCode="+resCode+";errorMsg="+errorMsg);
			return CommonUtils.getSuccessMsg();
		}else if("1".equals(resCode)){			
			logger.error("�ӿ�ִ�гɹ���û�ж�Ӧ���ݣ�");
			return CommonUtils.getSuccessMsg();
		}		
		GetPayInfoResultV3 resultResult= convertHisStringToV3Object(hospitalId,resultXml);							
		XStream xstream = UtilXml.getXStream(GetPayInfoResultV3.class);					
		return xstream.toXML(resultResult);			
	}
	
	/**
	 * his����ַ�������
	 * @param reqXml
	 * @return
	 */
	private String getInputParamString(String reqXml){		
		String healthCardNo=UtilXml.getValueByAllXml(reqXml, "healthCardNo");
		String hospitalId=UtilXml.getValueByAllXml(reqXml, "hospitalId");
		hospitalId=CommonUtils.getHospitalByBranchCode(hospitalId);
		StringBuilder str=new StringBuilder(200);
		str.append("<request>");
		str.append("<params>");
		str.append("<branchCode>"+hospitalId+"</branchCode>");
		str.append("<patCardType>1</patCardType>");
		str.append("<patCardNo>"+healthCardNo+"</patCardNo>");
		str.append("</params>");
		str.append("</request>");
		return str.toString();
	}
	
	/**
	 * his����תV3����
	 * @param hospitalId
	 * @param resultXml
	 * @return
	 * @throws DocumentException
	 */
	@SuppressWarnings("unchecked")
	private GetPayInfoResultV3 convertHisStringToV3Object(String hospitalId,String resultXml) throws DocumentException{		
		GetPayInfoResultV3 resultV3=new GetPayInfoResultV3();
		Document document=DocumentHelper.parseText(resultXml);
		Element root=document.getRootElement();		
		Element res=root.element("result");	
		List<Element> list=res.elements("item");
		List<PayInfo> payInfoList=new ArrayList<PayInfo>();		
		for(Element target:list){			
			PayInfo payInfo=new PayInfo();
			payInfo.setClinicSeq(target.elementText("mzFeeId")+"@"+target.elementText("doctorCode")+"@"+target.elementText("deptCode")+"@"+target.elementText("mzBillId"));
			//payInfo.setClinicTime(ele.attributeValue("DGH"));
			String time=target.elementText("time");
			payInfo.setClinicTime(time);
			payInfo.setHospitalId(hospitalId);
			payInfo.setDeptId(target.elementText("deptCode"));
			payInfo.setDeptName(target.elementText("deptName"));
			payInfo.setDoctorId(target.elementText("doctorCode"));
			payInfo.setDoctorName(target.elementText("doctorName"));			
			String payType=target.elementText("payType");
			if(StringUtils.isBlank(payType)||"�Է�".equals(payType)){
				payInfo.setSettleCode("00");
				payInfo.setSettleType("�Է�");
				payInfo.setPayAmout(target.elementText("payAmout"));
				payInfoList.add(payInfo);
			}else{				
				payInfo.setSettleCode(payType);
				payInfo.setSettleType(payType);							
				payInfo.setPayAmout(target.elementText("totalAmout"));
				//ҽ��ֻ��ʾ���������
				String currentDate=DateUtils.getYMDTime(new Date());
				if(StringUtils.isNotBlank(time)){
					time=CommonUtils.getDateStrByPattern(time);
					logger.info("ҽ����������ǰʱ�䣺"+currentDate+"��his����ʱ�䣺"+time);
					if(time.equals(currentDate)){
						payInfoList.add(payInfo);
					}
				}				
			}			
			
		}
		resultV3.setResultCode("0");
		resultV3.setResultDesc("�ɹ�");
		resultV3.setInfoList(payInfoList);		
		return resultV3;
	}
}
