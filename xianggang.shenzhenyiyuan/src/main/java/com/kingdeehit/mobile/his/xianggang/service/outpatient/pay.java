package com.kingdeehit.mobile.his.xianggang.service.outpatient;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingdeehit.mobile.his.entities.V3.result.outpatient.PayResultV3;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.DateUtils;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;

/**
 * 诊间支付
 * @author tangfulin
 *
 */
public class pay extends AbstractService{
	
	private static String hisInterface="ackPayOrder";
	
	@Override
	public String execute(String reqXml) throws Exception {		
		try {
			String getMzinsurance=getInputString(reqXml);	
			if("error".equals(getMzinsurance)){						
				return CommonUtils.getErrorMsg();
			}
			logger.error("诊间支付接口调用【outpatient.pay】-->【"+hisInterface+"】入参："+getMzinsurance);
			getMzinsurance=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface, getMzinsurance);
			HttpRequestService xmlRequest = HttpRequestService.getInstance();					
			String resultXml= xmlRequest.request(getMzinsurance);	
			resultXml=CommonUtils.convertHisOutputParam(resultXml);	
			logger.error("诊间支付接口调用【outpatient.pay】-->【"+hisInterface+"】出参："+resultXml);
			String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");
			if("0".equals(resCode)){			
				PayResultV3 resultV3=convertHisStringToV3Object(resultXml);						
				XStream xstream = UtilXml.getXStream(PayResultV3.class);			
				return xstream.toXML(resultV3);
			}else{
				String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
				logger.error("接口返回失败！resCode="+resCode+";errorMsg="+errorMsg);
				return CommonUtils.getErrorMsg(errorMsg);
			}
		} catch (Exception e) {
			logger.error(e);
			return CommonUtils.getErrorMsg("4201", "");
		}					
	}
	
	/**
	 * 预约挂号his入参字符串构造
	 * @param reqXml
	 * @return
	 */
	private String getInsuranceInputParamString(String reqXml){
		String clinicSeq=UtilXml.getValueByAllXml(reqXml, "clinicSeq");
		String hospitalId=UtilXml.getValueByAllXml(reqXml, "hospitalId");		
		String healthCardNo=UtilXml.getValueByAllXml(reqXml, "healthCardNo");
		String[] res=clinicSeq.split("@");
		//形如：就诊流水号@医生ID@科室ID@门诊单据号
		logger.error("拼接就诊流水号为："+clinicSeq);
				
		StringBuilder str=new StringBuilder(200);
		str.append("<request><params>");
		//str.append("<branchCode>"+hospitalId+"</branchCode>");
		str.append("<branchCode></branchCode>");
		str.append("<patCardType>1</patCardType>");
		str.append("<patCardNo>"+healthCardNo+"</patCardNo>");
		str.append("<mzFeeId>"+res[0]+"</mzFeeId>");
		str.append("<mzBillId>"+res[3]+"</mzBillId>");
		str.append("<deptCode>"+res[2]+"</deptCode>");
		str.append("<doctorCode>"+res[1]+"</doctorCode>");
		str.append("<insuranceSource></insuranceSource>");
		str.append("</params></request>");		
		return str.toString();
	}
	
	/**
	 * 预约挂号his入参字符串构造
	 * @param reqXml
	 * @return
	 */
	private String getMZFeeDetailString(String reqXml){
		String clinicSeq=UtilXml.getValueByAllXml(reqXml, "clinicSeq");
		String hospitalId=UtilXml.getValueByAllXml(reqXml, "hospitalId");
		String doctorId=UtilXml.getValueByAllXml(reqXml, "doctorId");		
		String[] res=clinicSeq.split("@");
		//形如：就诊流水号@医生ID@科室ID@门诊单据号
		logger.error("拼接就诊流水号为："+clinicSeq);
				
		StringBuilder str=new StringBuilder(200);
		str.append("<request><params>");
		str.append("<branchCode>"+hospitalId+"</branchCode>");
		str.append("<mzFeeId>"+res[0]+"</mzFeeId>");
		str.append("<deptCode>"+res[2]+"</deptCode>");
		//str.append("<doctorCode>"+doctorId+"</doctorCode>");	
		str.append("<doctorCode>"+res[1]+"</doctorCode>");	
		str.append("</params></request>");		
		return str.toString();
	}
	
	/**
	 * his入参字符串构造
	 * @param reqXml
	 * @return
	 * @throws Exception 
	 */
	private String getInputString(String reqXml) throws Exception{
		String hospitalId=UtilXml.getValueByAllXml(reqXml, "hospitalId");
		hospitalId=CommonUtils.getHospitalByBranchCode(hospitalId);
		String clinicSeq=UtilXml.getValueByAllXml(reqXml, "clinicSeq");		
		String orderId=UtilXml.getValueByAllXml(reqXml, "orderId");
		String payAmout=UtilXml.getValueByAllXml(reqXml, "payAmout");
		String totalPayAmout=UtilXml.getValueByAllXml(reqXml, "totalPayAmout");
		String tradeNo=UtilXml.getValueByAllXml(reqXml, "tradeNo");	
		String recPayAmout=UtilXml.getValueByAllXml(reqXml, "recPayAmout");
		String medicareSettleLogId=UtilXml.getValueByAllXml(reqXml, "medicareSettleLogId");
		medicareSettleLogId=medicareSettleLogId.replaceAll("\"\\[", "[").replaceAll("\\]\"", "]").replaceAll("\"\\{", "{").replaceAll("\\}\"", "}");
		//形如：就诊流水号@医生ID@科室ID@门诊单据号
		logger.error("拼接就诊流水号为："+clinicSeq);
		String[] res=clinicSeq.split("@");		
		Element insurResult=null;
		Element detailResult=null;
		if(StringUtils.isNotBlank(medicareSettleLogId)){
			String operationName="getMZInsurance";
			String insuranceInputString=getInsuranceInputParamString(reqXml);
			logger.error("诊间支付接口调用【outpatient.pay】-->【"+operationName+"】入参："+insuranceInputString);
			insuranceInputString=CommonUtils.convertHisInputParamWithOutUserInfo(operationName, insuranceInputString);
			HttpRequestService xmlRequest2 = HttpRequestService.getInstance();					
			String insuranceXml= xmlRequest2.request(insuranceInputString);	
			insuranceXml=CommonUtils.convertHisOutputParam(insuranceXml);	
			logger.error("诊间支付接口调用【outpatient.pay】-->【"+operationName+"】出参："+insuranceXml);		
			String resCode2=UtilXml.getValueByAllXml(insuranceXml, "resultCode");
			if("-1".equals(resCode2)||"1".equals(resCode2)){			
				String errorMsg=UtilXml.getValueByAllXml(insuranceXml, "resultMessage");
				logger.error("接口返回失败！resCode="+resCode2+";errorMsg="+errorMsg);
				return "error";
			}
			Document insurDocument=DocumentHelper.parseText(insuranceXml);
			Element insurRoot=insurDocument.getRootElement();		
			insurResult=insurRoot.element("result");		
			if(insurResult==null){
				logger.error("getMZInsurance接口调用出参服务解析！insuranceXml="+insuranceXml);
				return "error";
			}
			operationName="getMZFeeDetail";
			insuranceInputString=getMZFeeDetailString(reqXml);
			logger.error("诊间支付接口调用【outpatient.pay】-->【"+operationName+"】入参："+insuranceInputString);
			insuranceInputString=CommonUtils.convertHisInputParamWithOutUserInfo(operationName, insuranceInputString);
			HttpRequestService xmlRequest3 = HttpRequestService.getInstance();					
			insuranceXml= xmlRequest3.request(insuranceInputString);	
			insuranceXml=CommonUtils.convertHisOutputParam(insuranceXml);	
			logger.error("诊间支付接口调用【outpatient.pay】-->【"+operationName+"】出参："+insuranceXml);		
			resCode2=UtilXml.getValueByAllXml(insuranceXml, "resultCode");			
			if("-1".equals(resCode2)||"1".equals(resCode2)){			
				String errorMsg=UtilXml.getValueByAllXml(insuranceXml, "resultMessage");
				logger.error("接口返回失败！resCode="+resCode2+";errorMsg="+errorMsg);
				return "error";
			}
			Document insurDocument2=DocumentHelper.parseText(insuranceXml);
			Element insurRoot2=insurDocument2.getRootElement();		
			detailResult=insurRoot2.element("result");		
			if(detailResult==null){
				logger.error("getMZInsurance接口调用出参服务解析！insuranceXml="+insuranceXml);
				return "error";
			}
		}
		StringBuilder str=new StringBuilder(200);
		str.append("<request>");
		str.append("<params>");
		str.append("<branchCode>"+hospitalId+"</branchCode>");
		str.append("<mzFeeId>"+res[0]+"</mzFeeId>");
		str.append("<mzBillId>"+res[3]+"</mzBillId>");
		if(StringUtils.isNotBlank(medicareSettleLogId)){
			str.append("<SSFeeNo>"+insurResult.elementText("SSFeeNo")+"</SSFeeNo>");
			str.append("<SSBillNo>"+insurResult.elementText("SSBillNo")+"</SSBillNo>");
		}else{
			str.append("<SSFeeNo></SSFeeNo>");
			str.append("<SSBillNo></SSBillNo>");
		}
		str.append("<payAmout>"+payAmout+"</payAmout>");
		str.append("<deptCode>"+res[2]+"</deptCode>");
		str.append("<doctorCode>"+res[1]+"</doctorCode>");		
		str.append("<recipeType></recipeType>");
		str.append("<recipeId></recipeId>");
		if(StringUtils.isNotBlank(medicareSettleLogId)){
			String account=insurResult.elementText("accountAmout");
			String medicareAmount=insurResult.elementText("medicareAmount");
			//double dAccount=Double.parseDouble(account);
			//double dMedicareAccount=Double.parseDouble(medicareAmount);			
			str.append("<accountAmout>"+recPayAmout+"</accountAmout>");
			str.append("<medicareAmount>0</medicareAmount>");
			str.append("<insuranceAmout>"+recPayAmout+"</insuranceAmout>");
			//str.append("<totalAmout>"+insurResult.elementText("totalAmout")+"</totalAmout>");
			//totalAmout=insuranceAmout+PayAmount
			str.append("<totalAmout>"+(Double.parseDouble(payAmout)+Double.parseDouble(recPayAmout))+"</totalAmout>");
			str.append("<isInsurance>1</isInsurance>");
			str.append("<settlements>");			
			//List<Element> list=detailResult.elements("item");
			JSONObject object=JSONObject.fromObject(medicareSettleLogId);			
			if(object!=null){	
				JSONObject jsonObject=object.getJSONObject("treat_order_pay");
				JSONArray xmArray=jsonObject.getJSONArray("mzjs");
				if(xmArray!=null){
					for(int i=0,len=xmArray.size();i<len;i++){
						JSONObject obj=xmArray.getJSONObject(i);
						str.append("<settlement><settlementId>"+obj.getString("jsxm")+"</settlementId><settlementFee>"+CommonUtils.convertUnitToMinute(obj.getString("je"))+"</settlementFee></settlement>");
					}
				}	
			}
			str.append("</settlements>");
			str.append("<payments>");
			if(object!=null){
				JSONObject jsonObject=object.getJSONObject("treat_order_pay");
				JSONArray array=jsonObject.getJSONArray("mzzf");
				if(array!=null){
					for(int i=0,len=array.size();i<len;i++){
						JSONObject obj=array.getJSONObject(i);
						str.append("<payment><paymentId>"+obj.getString("zfxm")+"</paymentId><paymentFee>"+CommonUtils.convertUnitToMinute(obj.getString("je"))+"</paymentFee></payment>");
					}
				}				
			}			
			str.append("</payments>");
		}else{
			str.append("<accountAmout>0</accountAmout>");
			str.append("<medicareAmount>0</medicareAmount>");
			str.append("<insuranceAmout>0</insuranceAmout>");	
			str.append("<totalAmout>"+payAmout+"</totalAmout>");
			str.append("<isInsurance>0</isInsurance>");
			str.append("<settlements></settlements>");
			str.append("<payments></payments>");
		}				
		str.append("<psOrdNum>"+orderId+"</psOrdNum>");
		str.append("<agtOrdNum>"+tradeNo+"</agtOrdNum>");
		str.append("<agtCode></agtCode>");
		str.append("<payMode>1</payMode>");
		str.append("<payTime>"+DateUtils.getChineseTime()+"</payTime>");
		str.append("</params>");
		str.append("</request>");		
		return str.toString();
	}

	/**
	 * his出参转V3出参
	 * @param resultXml
	 * @return
	 * @throws DocumentException 
	 */
	private PayResultV3 convertHisStringToV3Object(String resultXml) throws DocumentException{		
		PayResultV3 resultV3=new PayResultV3();
		Document document=DocumentHelper.parseText(resultXml);
		Element root=document.getRootElement();		
		Element res=root.element("result");			
		resultV3.setResultCode("0");
		resultV3.setResultDesc("成功");
		if(res!=null){
			String receiptNum=res.elementText("receiptNum");
			resultV3.setGuideInfo("发票号("+receiptNum+")   "+res.elementText("hisMessage"));
			resultV3.setReceiptId(res.elementText("receiptNum"));
			resultV3.setRemark("");		
		}
		return resultV3;
	}
}
