package com.kingdeehit.mobile.his.xianggang.service.register;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.kingdeehit.mobile.his.xianggang.constant.ParamConstants;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.StringUtil;
import com.kingdeehit.mobile.utils.UtilXml;
/**
 * His的锁号接口，
 * （港大有两个系统一个号源系统，一个His系统，第三方挂号的时候仅仅是在号源系统系统有记录，取号之前，需要先调用his的锁号接口，才可以支付）
 * @author YJB
 * @date 2017年10月20日 上午8:45:00
 */
public class OrderCurReg extends AbstractService{
	
	private static String hisInterface="orderCurReg";

	@Override
	public String execute(String reqXml) throws Exception {
		
		//reqXml = "<req><lockId>w001222017093000004</lockId><infoSeq>6405188644</infoSeq><hospitalId>100122001</hospitalId><orderType>10</orderType><orderTime>2017-09-30 15:27:14</orderTime><orderId>w001222017093000004</orderId><healthCardNo>AJ9615893</healthCardNo><payAmout>0</payAmout><payMode>98</payMode><tradeNo>M17093030152635704</tradeNo><patientId>0000980602</patientId><svObjectId>02</svObjectId><operatorId>7354</operatorId><medicareSettleLogId>{'registration_order_pay':{'ylzh':'6051529711','dnh':'','brlx':'1','mzghdj':'[{'je':86,'jssj':'2017-09-30 15:27:14','zfxm':'02'},{'je':86,'jssj':'2017-09-30 15:27:14','zfxm':'0201'},{'je':86,'jssj':'2017-09-30 15:27:15','zfxm':'03'},{'je':7508.15,'jssj':'2017-09-30 15:27:14','zfxm':'0303'},{'je':7422.15,'jssj':'2017-09-30 15:27:14','zfxm':'0306'}]','cblx':'1'}}</medicareSettleLogId><recPayAmout>8600</recPayAmout><totalPayAmout>8600</totalPayAmout></req>";
		String inputString=getInputParamString(reqXml);				
		logger.error("预约取号号源锁定接口【 预约取号 】-->【"+hisInterface+"】入参：user="+ParamConstants.USER+";password="+ParamConstants.PASSWORD+";parameter="+inputString);		
		inputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface, inputString);
		logger.info("处理完后的入参是  "+inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();					
		String resultXml= xmlRequest.request(inputString);	
		//String resultXml = "<response>    <resultCode>0</resultCode>    <resultMessage>调用接口正常。</resultMessage>    <result>        <yljgbm>HHK00</yljgbm>        <czybm>M00001</czybm>        <czyxm>移动支付01</czyxm>        <regCategory>普通</regCategory>        <hisOrdNum>6404946723</hisOrdNum>        <mzFeeId>186041</mzFeeId>        <cancelSerialNo>186042</cancelSerialNo>        <cancelBillNo>#</cancelBillNo>        <regFee>0</regFee>        <treatFee>10000</treatFee>        <desc></desc>    </result></response>";
		resultXml=CommonUtils.convertHisOutputParam(resultXml);		
		logger.error("预约取号号源锁定接口【 预约取号 】-->【"+hisInterface+"】出参："+resultXml);
		String resCode=UtilXml.getValueByAllXml(resultXml, "STATE");
		String resultCode = UtilXml.getValueByAllXml(resultXml, "resultCode");
		if("0".equals(resultCode)){ //锁号成功进行挂号
			return CommonUtils.getSuccessMsg();
		}else{
			return CommonUtils.getErrorMsg();
		}
		
	}

	private String getInputParamString(String reqXml) {
		
		String orderId = UtilXml.getValueByAllXml(reqXml, "orderId");
		String tradeNo=UtilXml.getValueByAllXml(reqXml, "tradeNo");
		String infoSeq=UtilXml.getValueByAllXml(reqXml, "infoSeq");
		String deptId=UtilXml.getValueByAllXml(reqXml, "deptId");
		String doctorId = UtilXml.getValueByAllXml(reqXml, "doctorId");
		String regDate =UtilXml.getValueByAllXml(reqXml, "regDate");
		String startTime =UtilXml.getValueByAllXml(reqXml, "startTime");
		String endTime =UtilXml.getValueByAllXml(reqXml, "endTime");
		String patientId =UtilXml.getValueByAllXml(reqXml, "patientId");
		String regFee=UtilXml.getValueByAllXml(reqXml, "regFee");
		String treatFee=UtilXml.getValueByAllXml(reqXml, "treatFee");	
		String svObjectId = UtilXml.getValueByAllXml(reqXml, "svObjectId");
		String medicareSettleLogId= UtilXml.getValueByAllXml(reqXml, "medicareSettleLogId");
		medicareSettleLogId=medicareSettleLogId.replaceAll("\"\\[", "[").replaceAll("\\]\"", "]").replaceAll("\"\\{", "{").replaceAll("\\}\"", "}");
		String ssCardNo = "";
		if(StringUtil.isNotEmpty(medicareSettleLogId)){
			ssCardNo = getSSCardNo(medicareSettleLogId);
		} 
				
		StringBuilder str=new StringBuilder(400);
		str.append("<request><params>");
		str.append("<psOrdNum>"+orderId+"</psOrdNum>");
		str.append("<branchCode>xgdxszyy</branchCode>");
		str.append("<timeFlag></timeFlag>");
		str.append("<deptCode>"+deptId+"</deptCode>");
		str.append("<doctorCode>"+doctorId+"</doctorCode>");			
		str.append("<beginTime>"+startTime+"</beginTime>");
		str.append("<endTime>"+endTime+"</endTime>");
		str.append("<workId></workId>");
		str.append("<regFee>"+regFee+"</regFee>");
		str.append("<treatFee>"+treatFee+"</treatFee>");
		str.append("<regType>1</regType>");
		str.append("<patCardType>1</patCardType>");
		str.append("<patCardNo>"+patientId+"</patCardNo>");
		str.append("<orderMode>"+"5"+"</orderMode>");//原来是2 ，项目实施要求改成5
		str.append("<orderTime></orderTime>");
		str.append("<orderNo>"+infoSeq+"</orderNo>");
	 
		if("02".equals(svObjectId)){
			str.append("<isInsurance>1</isInsurance>");
		}else{
			str.append("<isInsurance>0</isInsurance>");
		}
		str.append("<medicalCardNo>"+ssCardNo+"</medicalCardNo>");
		str.append("<medicareCardNo></medicareCardNo>");
		str.append("<computerNo></computerNo>");
		str.append("</params></request>");
		
		return str.toString();
	}
	/**
	 * 获取患者医疗卡号
	 */
	private String getSSCardNo(String medicareSettleLogId){
		
		JSONObject jsonObject =  JSONObject.fromObject(medicareSettleLogId);
		
		JSONObject payInfo  = jsonObject.getJSONObject("registration_order_pay");
		
		String ssCardNo = payInfo.getString("ylzh");
		
		logger.info("获取到患者医疗卡号是 "+ssCardNo);
		
		return ssCardNo;
		
	}
	public static void main(String[] args) throws Exception {
		OrderCurReg reg = new OrderCurReg();
		reg.execute("");
		
	}
		
}
