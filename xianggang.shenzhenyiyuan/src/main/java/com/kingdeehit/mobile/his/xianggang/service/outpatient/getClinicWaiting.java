package com.kingdeehit.mobile.his.xianggang.service.outpatient;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import com.kingdeehit.mobile.his.consts.ErrorCode;
import com.kingdeehit.mobile.his.entities.waiting.result.GetPatClinicWaitingItem;
import com.kingdeehit.mobile.his.entities.waiting.result.GetPatClinicWaitingResult;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
/**
* @ClassName: getClinicWaiting 
* @Description: 个人门诊排队查询 
* @author Luffy-CXM 
* @date 2018年2月28日 下午4:05:52
 */
public class getClinicWaiting  extends AbstractService{

	private static String hisInterface="getClinicWaiting";
	@Override
	public String execute(String reqXml) throws Exception {
		String inputString = getHisInputParam(reqXml);
		logger.error("个人门诊排队查询接口【outpatient.getClinicWaiting】-->【"+hisInterface+"】入参："+inputString);
		inputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface, inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();					
		String resultXml= xmlRequest.request(inputString);	
		//String resultXml = "<response><resultCode>0</resultCode><resultMessage>调用接口正常。</resultMessage><result><patientId>24802</patientId><patientName>张XX</patientName><healthCardNo>AL2117919</healthCardNo><deptName>急诊科</deptName><deptId>8149</deptId><waitingCount>0</waitingCount><queueNo>40003</queueNo><roomAddress></roomAddress><queueName>Ⅳ类</queueName><reglevlName></reglevlName><queryTime>2018-02-11 04:02:19</queryTime></result></response>";
		resultXml=CommonUtils.convertHisOutputParam(resultXml);
		logger.error("个人门诊排队查询接口【outpatient.getClinicWaiting】-->【"+hisInterface+"】出参："+resultXml);
		
		String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");
		if("-1".equals(resCode)){
			String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
			logger.error("接口返回失败！resCode="+resCode+";errorMsg="+errorMsg);
			return CommonUtils.getSuccessMsg();
		}else if("1".equals(resCode)){			
			logger.error("接口执行成功但没有对应数据！");
			return CommonUtils.getSuccessMsg();
		}
		GetPatClinicWaitingResult resultV3 = convertHisStringToV3Object(resultXml);	
		XStream xStream = new XStream(new DomDriver("utf-8"));
		xStream.processAnnotations(GetPatClinicWaitingResult.class);
		return xStream.toXML(resultV3);
	}
	
	

	/**
	* @Title: getHisInputParam 
	* @author Luffy-CXM 
	* @Description: 构造his入参  
	* @return String
	 */
	private String getHisInputParam(String reqXml) {
		StringBuffer str = new StringBuffer();		
		String idCardNo=UtilXml.getValueByAllXml(reqXml, "idCardNo");	
		String healthCardNo=UtilXml.getValueByAllXml(reqXml, "healthCardNo");	
		//String patientId=UtilXml.getValueByAllXml(reqXml, "patientId");	
		
		str.append("<request>");
		str.append("<params>");
		str.append("<idCardNo>" + idCardNo + "</idCardNo>");
		str.append("<healthCardNo>" + healthCardNo + "</healthCardNo>");
		str.append("<patientId></patientId>");
		str.append("</params>");
		str.append("</request>");		
	
		return str.toString();
	}
	
	/**
	* @Title: convertHisStringToV3Object 
	* @author Luffy-CXM 
	* @Description: 把his的出参转化成我们的出参
	* @return GetPatClinicWaitingResult
	 */
	private GetPatClinicWaitingResult convertHisStringToV3Object(String resultXml) throws DocumentException {
		GetPatClinicWaitingResult resultV3 = new GetPatClinicWaitingResult();
		List<GetPatClinicWaitingItem> itemList = new ArrayList<GetPatClinicWaitingItem>();
		
		List<Element> list = CommonUtils.getNodeList(resultXml, "result");
		if (list != null && list.size() > 0) {
			for (Element ele : list) {
				GetPatClinicWaitingItem item = new GetPatClinicWaitingItem();
				item.setPatientName(ele.elementText("patientName"));
				item.setDeptId(ele.elementText("deptId"));
				item.setDeptName(ele.elementText("deptName"));
				item.setWaitingCount(ele.elementText("waitingCount"));
				item.setQueueNo(ele.elementText("queueNo"));
				item.setRoomAddress(ele.elementText("roomAddress"));
				item.setDoctorName("叫号分配");
				item.setStatus("1");
				itemList.add(item);
			}
		}
		resultV3.setData(ErrorCode.Success);
		resultV3.setList(itemList);
		return resultV3;
	}
	
	
	public static void main(String[] args) {
		getClinicWaiting waiting = new getClinicWaiting();
		try {
			logger.info(waiting.execute(""));;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
