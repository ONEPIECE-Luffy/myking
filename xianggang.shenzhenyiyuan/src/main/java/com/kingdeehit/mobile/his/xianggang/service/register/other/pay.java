package com.kingdeehit.mobile.his.xianggang.service.register.other;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.bson.conversions.Bson;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingdeehit.mobile.his.utils.MongoDBHelper;
import com.kingdeehit.mobile.his.xianggang.entity.table.ChannelAppointmentLockInfo;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
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
	
	@Override
	public String execute(String reqXml) throws Exception {		
		try {
			String orderId=UtilXml.getValueByAllXml(reqXml, "orderId");
			boolean flag=CommonUtils.isNotPaymentOrder(orderId);
			if(flag){
				return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><res><resultCode>4154</resultCode><resultDesc>您的医保预约暂时不能支付，请到挂号窗口支付。</resultDesc></res>";
			}
			String inputString=getInputParamString(reqXml);		
			logger.error("当天挂号支付接口【register.pay】-->【"+hisInterface+"】入参："+inputString);
			inputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface, inputString);
			HttpRequestService xmlRequest = HttpRequestService.getInstance();					
			String resultXml= xmlRequest.request(inputString);	
			resultXml=CommonUtils.convertHisOutputParam(resultXml);		
			logger.error("当天挂号支付接口【register.pay】-->【"+hisInterface+"】出参："+resultXml);
			String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");		
			if(!"0".equals(resCode)){
				String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
				logger.error("接口返回失败！resCode="+resCode+";errorMsg="+errorMsg);
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
		if(list!=null){
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
					
		StringBuilder str=new StringBuilder(200);
		str.append("<request><params>");
		str.append("<branchCode>"+CommonUtils.getHospitalByBranchCode(hospitalId)+"</branchCode>");
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
			str.append("<insuranceAmout>"+(Double.parseDouble(payAmout)+Double.parseDouble(recPayAmout))+"</insuranceAmout>");
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
	
}
