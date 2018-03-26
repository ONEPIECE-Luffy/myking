package com.kingdeehit.mobile.his.xianggang.service.register;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.bson.conversions.Bson;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONException;

import com.kingdeehit.mobile.his.entities.V3.result.support.GetRegisterInfoResultItemV3;
import com.kingdeehit.mobile.his.entities.table.ChannelBindingCard;
import com.kingdeehit.mobile.his.utils.MongoDBHelper;
import com.kingdeehit.mobile.his.xianggang.entity.table.ChannelAppointmentLockInfo;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.appointment.returnPay;
import com.kingdeehit.mobile.his.xianggang.service.support.getRegisterInfoList;
import com.kingdeehit.mobile.his.xianggang.service.util.BusinessDBHelper;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.UtilXml;
import com.mongodb.client.model.Filters;

/**
 * 当天挂号支付接口
 * @author tangfulin
 *
 */
public class pay extends AbstractService{
	
	private static String hisInterface="payCurReg";
	String orderId;
	@Override
	public String execute(String reqXml) throws Exception {		
		
		String bookingNo = UtilXml.getValueByAllXml(reqXml, "infoSeq");
		orderId = UtilXml.getValueByAllXml(reqXml, "orderId");
		 if (!CommonUtils.isOtherPlatform(bookingNo)) { //金蝶渠道的挂号流程
			return payMethod(reqXml);
		}else{ //非金蝶渠道的挂号流程  先锁号，再挂号
				String lockReqXml = getLockReqXml(reqXml,bookingNo);
				//要么这里要调接口查询，第三方号源的数据
				OrderCurReg orderCurReg = new OrderCurReg();
				logger.info("当天挂号支付接口，处理【非金蝶】渠道，先进行锁号，锁号的入参是 "+lockReqXml);
				String resultStr = orderCurReg.execute(lockReqXml);
				String resultCode = UtilXml.getValueByAllXml(resultStr, "resultCode");
				if("0".equals(resultCode)){ //锁号成功进行挂号
					return payMethod(reqXml);
				}else{ // 锁号失败
					logger.info("预约取号  其他渠道的号源，锁号失败！！");
					return CommonUtils.getErrorMsg();
				}
		}
	}
	/**
	 * 第三方号源需要再支付接口中锁号，缺少一些数据，需要请求我的挂号列表接口进行拼凑
	 * @author YJB
	 * @date 2017年9月8日 下午3:50:36
	 * @param reqXml
	 * @param bookingNo 
	 * @return
	 * @throws Exception 
	 */
	private String getLockReqXml(String reqXml, String bookingNo) throws Exception {
		
		String healthCardNo =UtilXml.getValueByAllXml(reqXml, "healthCardNo");
		ChannelBindingCard  cardInfo=null;
		cardInfo = BusinessDBHelper.getPatientInfo(healthCardNo);
		String patientName="";
		String idCardNo="";
		String phone ="";
		if(cardInfo!=null){
			patientName=cardInfo.getPatientName();
			idCardNo= cardInfo.getIdCardNo();
			phone = cardInfo.getPhone();
			logger.info("查找到患者卡号为 "+healthCardNo +" 的个人信息身份证号是 "+idCardNo);
		}else{
			logger.info("查找不到患者卡号为 "+healthCardNo +" 的个人信息");
		}
		
		//调用我的挂号记录列表，获取锁号需要的请求参数
		getRegisterInfoList registerInfoList = new getRegisterInfoList(); 
		
		String req = "<req>"
					+ "<healthCardNo>"+healthCardNo+"</healthCardNo>"
					+ "<orderId>"+bookingNo+"</orderId>"
					+ "<idCardNo>"+idCardNo+"</idCardNo>"
					+ "<patientName>"+patientName+"</patientName>"
					+ "</req>";
		 List<GetRegisterInfoResultItemV3> otherRegList = registerInfoList.executeList(req);
		 
		 GetRegisterInfoResultItemV3 infoResultItemV3 = null;
		 for (GetRegisterInfoResultItemV3 getRegisterInfoResultItemV3 : otherRegList) {
			 if(bookingNo.equals(getRegisterInfoResultItemV3.getBookingNo())){
				 infoResultItemV3 = getRegisterInfoResultItemV3; //找到His订单号一致的挂号信息
				 break;
			 }
		}
		 
		 if(infoResultItemV3!=null){
			 
			GetRegisterInfoResultItemV3 itemV3 = infoResultItemV3;
			String deptId=itemV3.getDeptId();
			String doctorId = itemV3.getDoctorId();
			String regDate =itemV3.getRegDate();
			String startTime =itemV3.getStartTime();
			String endTime =itemV3.getEndTime();
			String patientId =UtilXml.getValueByAllXml(reqXml, "patientId");
			String regFee=itemV3.getRegFee();
			String treatFee=itemV3.getTreatFee();
			String svObjectId = UtilXml.getValueByAllXml(reqXml, "svObjectId");
			String medicareSettleLogId= UtilXml.getValueByAllXml(reqXml, "medicareSettleLogId");
			
			String reqLockXml = "<req>"
					+ "<orderId>"+orderId+"</orderId>"
					+ "<infoSeq>"+bookingNo+"</infoSeq>"
					//+ "<hospitalId></hospitalId>"
					+ "<deptId>"+deptId+"</deptId>"
					//+ "<clinicUnitId>"++"</clinicUnitId>"
					+ "<doctorId>"+doctorId+"</doctorId>"
					//+ "<doctorLevelCode>"++"</doctorLevelCode>"
					//+ "<scheduleId>"++"</scheduleId>"
					//+ "<periodId>"++"</periodId>"
					+ "<regDate>"+regDate+"</regDate>"
					//+ "<shiftCode>"++"</shiftCode>"
					+ "<startTime>"+startTime+"</startTime>"
					+ "<endTime>"+endTime+"</endTime>"
					+ "<healthCardNo>"+healthCardNo+"</healthCardNo>"
					+ "<patientId>"+patientId+"</patientId>"
					+ "<patientName>"+patientName+"</patientName>"
					+ "<idCardNo>"+idCardNo+"</idCardNo>"
					+ "<phone>"+phone+"</phone>"
					+"<svObjectId>"+svObjectId+"</svObjectId>"
					+ "<regFee>"+regFee+"</regFee>"
					+ "<treatFee>"+treatFee+"</treatFee>"
					+"<medicareSettleLogId>"+medicareSettleLogId+"</medicareSettleLogId>"
					//+ "<scheduleId>"++"</scheduleId>"
					//+ "<periodId>"++"</periodId>"
					+ "</req>";
			return reqLockXml;
			
		 }else{
			 logger.info("患者预约取号，号源信息查询失败！");
			 return null;
		 }
	}
	 
	/**
	 * 支付方法
	 * @author YJB
	 * @date 2017年9月8日 下午3:17:02
	 * @param reqXml
	 * @return
	 */
	private String payMethod(String reqXml) {
		try {
			String orderId = UtilXml.getValueByAllXml(reqXml, "orderId");
			boolean flag = CommonUtils.isNotPaymentOrder(orderId);
			if (flag) {
				return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><res><resultCode>4154</resultCode><resultDesc>您的医保预约暂时不能支付，请到挂号窗口支付。</resultDesc></res>";
			}
			String inputString = getInputParamString(reqXml);
			logger.error("当天挂号支付接口【register.pay】-->【" + hisInterface
					+ "】入参：" + inputString);
			inputString = CommonUtils.convertHisInputParamWithOutUserInfo(
					hisInterface, inputString);
			String resultXml;
			try {
				HttpRequestService xmlRequest = HttpRequestService
						.getInstance();
				resultXml = xmlRequest.request(inputString);
			} catch (Exception e) {
				logger.error(e);
				return CommonUtils.getErrorMsg("请求过程中异常！");
			}
			resultXml = CommonUtils.convertHisOutputParam(resultXml);
			logger.error("当天挂号支付接口【register.pay】-->【" + hisInterface
					+ "】出参：" + resultXml);
			String resCode = UtilXml.getValueByAllXml(resultXml,
					"resultCode");
			if (!"0".equals(resCode)) {
				String errorMsg = UtilXml.getValueByAllXml(resultXml,
						"resultMessage");
				logger.error("接口返回失败！resCode=" + resCode + ";errorMsg="
						+ errorMsg);
				return CommonUtils.getErrorMsg(errorMsg);
			}
			return convertHisStringToV3Object(resultXml);
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
		String tradeNo=UtilXml.getValueByAllXml(reqXml, "tradeNo");
		String orderId=UtilXml.getValueByAllXml(reqXml, "orderId");
		String infoSeq=UtilXml.getValueByAllXml(reqXml, "infoSeq");
					
		Bson filter = Filters.and(Filters.eq("orderId", orderId));
		List<org.bson.Document> list=new MongoDBHelper("channel_appointment_lock_info").query(filter);
		ChannelAppointmentLockInfo inpatientInfo = null;
		if(list!=null&&list.size()>0){
			org.bson.Document document=list.get(0);
			String json = document.toJson();
			JSONObject jsonObject = JSONObject.fromObject(json);
			inpatientInfo = (ChannelAppointmentLockInfo)JSONObject.toBean(jsonObject, ChannelAppointmentLockInfo.class);
		}
		if(inpatientInfo==null){
			logger.error("根据订单号："+orderId+"到mongodb获取锁号信息失败！");
		}else{
			infoSeq=inpatientInfo.getBookingNo();
		}
		
		String payAmout=UtilXml.getValueByAllXml(reqXml, "payAmout");
		String orderTime=UtilXml.getValueByAllXml(reqXml, "orderTime");
		String hospitalId=UtilXml.getValueByAllXml(reqXml, "hospitalId");
		String healthCardNo=UtilXml.getValueByAllXml(reqXml, "healthCardNo");
		String medicareSettleLogId=UtilXml.getValueByAllXml(reqXml, "medicareSettleLogId");
		medicareSettleLogId=medicareSettleLogId.replaceAll("\"\\[", "[").replaceAll("\\]\"", "]").replaceAll("\"\\{", "{").replaceAll("\\}\"", "}");
		String clinicSeq = "";//门诊流水号
		StringBuilder str=new StringBuilder(200);
		str.append("<request><params>");
		str.append("<branchCode>"+CommonUtils.getHospitalByBranchCode(hospitalId)+"</branchCode>");
		//str.append("<hisOrdNum>"+infoSeq+"</hisOrdNum>");
		str.append("<hisOrdNum>"+infoSeq+"</hisOrdNum>");
		str.append("<psOrdNum>"+orderId+"</psOrdNum>");
		str.append("<agtOrdNum>"+tradeNo+"</agtOrdNum>");
		str.append("<agtCode></agtCode>");
		str.append("<payMode>1</payMode>");			
		str.append("<payTime>"+orderTime+"</payTime>");
		str.append("<orderNo></orderNo>");
		str.append("<patCardType>1</patCardType>");
		str.append("<patCardNo>"+healthCardNo+"</patCardNo>");
		if(StringUtils.isNotBlank(medicareSettleLogId)){
			String recPayAmout=UtilXml.getValueByAllXml(reqXml, "recPayAmout");					
			str.append("<payAmout>"+payAmout+"</payAmout>");		
			str.append("<accountAmout>"+recPayAmout+"</accountAmout>");
			str.append("<medicareAmount>0</medicareAmount>");			
			//str.append("<insuranceAmout>"+(Double.parseDouble(payAmout)+Double.parseDouble(recPayAmout))+"</insuranceAmout>");
			str.append("<insuranceAmout>"+Double.parseDouble(recPayAmout)+"</insuranceAmout>");
			str.append("<isInsurance>1</isInsurance>");
			str.append("<insuredType>1</insuredType>");
			str.append("<patientType>1</patientType>");
			str.append("<payments>");
			JSONObject object=JSONObject.fromObject(medicareSettleLogId);
			if(object!=null){
				JSONObject jsonObject=object.getJSONObject("registration_order_pay");
				JSONArray array=jsonObject.getJSONArray("mzghdj");
				if(array!=null){
					for(int i=0,len=array.size();i<len;i++){
						JSONObject obj=array.getJSONObject(i);
						str.append("<payment><paymentId>"+obj.getString("zfxm")+"</paymentId><paymentFee>"+CommonUtils.convertUnitToMinute(obj.getString("je"))+"</paymentFee></payment>");
					}
				}
				
			}			
			str.append("</payments>");
		}else{
			str.append("<payAmout>"+payAmout+"</payAmout>");		
			str.append("<accountAmout>0</accountAmout>");
			str.append("<medicareAmount>0</medicareAmount>");	
			str.append("<insuranceAmout>0</insuranceAmout>");	
			str.append("<isInsurance>0</isInsurance>");
			str.append("<insuredType></insuredType>");
			str.append("<patientType></patientType>");
			str.append("<payments></payments>");
		}
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
	private String convertHisStringToV3Object(String resultXml) throws DocumentException{		
		Document document=DocumentHelper.parseText(resultXml);
		Element root=document.getRootElement();		
		Element res=root.element("result");		
		StringBuilder str=new StringBuilder(200);
		String receiptNum=res.elementText("receiptNum");
		str.append("<res>");
		str.append("<resultCode>0</resultCode>");
		str.append("<resultDesc>成功</resultDesc>");
		str.append("<roomAddress>"+res.elementText("visitLocation")+"</roomAddress>");	
		str.append("<oppatNo>"+receiptNum+"</oppatNo>");
		str.append("<queueNo></queueNo>");
		str.append("<clinicTime></clinicTime>");
		str.append("<clinicSeq>"+receiptNum+"</clinicSeq>");
		str.append("</res>");
		return str.toString();
	}
	
	
	/**
	 * 获取社保支付结果串
	 * @param medicareSettleLogId
	 * @return
	 * @throws JSONException
	 */
	private String getSSitemString(String medicareSettleLogId) throws JSONException {
		StringBuffer res = new StringBuffer();
		JSONObject jsonObject =  JSONObject.fromObject(medicareSettleLogId);
		String json2 = jsonObject.getString("registration_order_pay");
		JSONObject jsonObject2 =  JSONObject.fromObject(json2);
		JSONArray jsonArray = JSONArray.fromObject(jsonObject2.getString("mzghdj"));
		if (jsonArray.size()>0) {
			for (int i = 0; i < jsonArray.size(); i++) {
				net.sf.json.JSONObject object = jsonArray.getJSONObject(i);
				String Zfxm = object.getString("zfxm");
				String Je = object.getString("je");
				res.append("<Item>");
				res.append("<Zfxm>"+Zfxm+"</Zfxm>");
				res.append("<Je>"+CommonUtils.convertUnitToMinute(Je)+"</Je>");
				res.append("</Item>");
			}
		}
		return res.toString();
	}
	public static void main(String[] args) throws Exception {
		String test = "<req><lockId>w001222017092000004</lockId><infoSeq>2525778828</infoSeq><hospitalId>100122001</hospitalId><orderType>10</orderType><orderTime>2017-09-20 16:11:03</orderTime><orderId>w001222017092000004</orderId><healthCardNo>AK9545341</healthCardNo><payAmout>10000</payAmout><payMode>98</payMode><tradeNo>4200000014201709203208616998</tradeNo><patientId>0001521601</patientId><operatorId>7354</operatorId></req>";
		String test1 = "<req><lockId>w001222017092800002</lockId><infoSeq>6405036851</infoSeq><hospitalId>100122001</hospitalId><orderType>10</orderType><orderTime>2017-09-28 09:51:41</orderTime><orderId>w001222017092800002</orderId><healthCardNo>AK9545341</healthCardNo><payAmout>10000</payAmout><payMode>98</payMode><tradeNo>4200000012201709284692228052</tradeNo><patientId>0001521601</patientId><operatorId>7354</operatorId></req>";
		 pay y = new pay();
		y.payMethod(test); 
		/*int temp =  test.lastIndexOf("</req>");
		String req =test.substring(0,temp)+"<idCardNo>"+1111111+"</idCardNo></req>";
		logger.info(req);*/
	/*	int temp =  test.lastIndexOf("<orderId>");
		int temp1 = test.lastIndexOf("</orderId>");
		String  req =  test.substring(0,temp) +"<orderId>1122211"+ test.substring(temp1,test.length());
		logger.info(req);*/
	
	}
	
}
