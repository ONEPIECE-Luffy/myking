package com.kingdeehit.mobile.his.xianggang.service.appointment;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.bson.conversions.Bson;

import com.kingdeehit.mobile.his.entities.table.ChannelAppointmentInfo;
import com.kingdeehit.mobile.his.entities.table.ChannelProductOrder;
import com.kingdeehit.mobile.his.utils.BusinessDBHelper;
import com.kingdeehit.mobile.his.utils.MongoDBHelper;
import com.kingdeehit.mobile.his.xianggang.constant.ParamConstants;
import com.kingdeehit.mobile.his.xianggang.entity.table.ChannelAppointmentLockInfo;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.DateUtils;
import com.kingdeehit.mobile.utils.UtilXml;
import com.mongodb.client.model.Filters;

/**
 * 退费接口
 * @author tangfulin
 *
 */
public class returnPay extends AbstractService{

	private static String hisInterface="ackRefundReg";
	
	@Override
	public String execute(String reqXml) throws Exception {
		//区分是当天挂号退费还是预约挂号退费
		String orderId=UtilXml.getValueByAllXml(reqXml, "orderId");
			   
	   //控制在早上6点之前不允许退费，返回：4139错误编码
		if(CommonUtils.isBeforeFixedDate("06:00:00")){
			return CommonUtils.getErrorMsg("4139","系统处于结算状态，无法退费，早上7:00之后才能进行退费");
		}
		ChannelAppointmentInfo appointInfo=BusinessDBHelper.getAppointmentInfo(orderId);
		String regDate = null;
		String currentDate=DateUtils.getYMDTime(new Date());
		if(appointInfo != null && appointInfo.getRegDate() != null){
			regDate=appointInfo.getRegDate();		
		}else{
			logger.info("单号"+" 在MongoDB中找不到记录，未【非金蝶渠道】订单"); //非金蝶的订单退号，使用与当日退费流程一致
			regDate=currentDate;
		}
		
		logger.error("当前日期："+currentDate+";订单预约日期："+regDate);
		if(currentDate.equals(regDate)){
			//下面执行 预约退费确认ackRefundReg
			String operationName="ackRefundCurReg";
			String inputString=getConfirmCancelInputParamString(reqXml);

			logger.error("预约挂号支付接口【appointment.returnPay】-->1【"+operationName+"】入参："+inputString);
			inputString=CommonUtils.convertHisInputParamWithOutUserInfo(operationName, inputString);
			HttpRequestService xmlRequest = HttpRequestService.getInstance();
			String resultXml= xmlRequest.request(inputString);	//执行请求
			resultXml=CommonUtils.convertHisOutputParam(resultXml);
			logger.error("预约挂号支付接口【appointment.returnPay】-->1【"+operationName+"】出参："+resultXml);
			String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");
			if(!"0".equals(resCode)){

				String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
				logger.error("接口返回失败！resCode="+resCode+";errorMsg="+errorMsg);
				return CommonUtils.getErrorMsg("4140","无法取消订单，请联系客服人员");
			}

			//下面执行 预约退费refundReg操作
			operationName="refundCurReg";
			String cancelSerialNo=UtilXml.getValueByAllXml(resultXml, "cancelSerialNo");
			String cancelBillNo=UtilXml.getValueByAllXml(resultXml, "cancelBillNo");
			//入参拼接
			inputString=getRenfundInputParamString(reqXml,cancelSerialNo,cancelBillNo);
			logger.error("预约挂号支付接口【appointment.returnPay】-->2【"+operationName+"】入参："+inputString);
			inputString=CommonUtils.convertHisInputParamWithOutUserInfo(operationName, inputString);
			//执行请求
			resultXml= xmlRequest.request(inputString);
			//出参转换
			resultXml=CommonUtils.convertHisOutputParam(resultXml);
			logger.error("预约挂号支付接口【appointment.returnPay】-->2【"+operationName+"】出参："+resultXml);
			resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");
			if(!"0".equals(resCode)){
				String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
				logger.error("接口返回失败！resCode="+resCode+";errorMsg="+errorMsg);
				return CommonUtils.getErrorMsg(errorMsg);
			}else{//退费成功
				try{
					cancelAppointmentCancel(orderId);
				}catch(Exception e){
					logger.error("释放号源出现异常！！.......",e);
				}
				return convertHisStringToV3Object();
			}
		}else{
			String inputString=getConfirmCancelInputParamString(reqXml);
			logger.error("预约挂号支付接口【appointment.returnPay】-->1【"+hisInterface+"】入参："+inputString);
			inputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface, inputString);
			HttpRequestService xmlRequest = HttpRequestService.getInstance();
			String resultXml= xmlRequest.request(inputString);
			resultXml=CommonUtils.convertHisOutputParam(resultXml);
			logger.error("预约挂号支付接口【appointment.returnPay】-->1【"+hisInterface+"】出参："+resultXml);
			String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");
			if(!"0".equals(resCode)){
				String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
				logger.error("接口返回失败！resCode="+resCode+";errorMsg="+errorMsg);
				return CommonUtils.getErrorMsg("4140","无法取消订单，请联系客服人员");
			}
			//下面执行 预约退费refundReg操作
			String cancelSerialNo=UtilXml.getValueByAllXml(resultXml, "cancelSerialNo");
			String cancelBillNo=UtilXml.getValueByAllXml(resultXml, "cancelBillNo");
			inputString=getRenfundInputParamString(reqXml,cancelSerialNo,cancelBillNo);
			String operationName="refundReg";
			logger.error("预约挂号支付接口【appointment.returnPay】-->2【"+operationName+"】入参："+inputString);
			inputString=CommonUtils.convertHisInputParamWithOutUserInfo(operationName, inputString);
			resultXml= xmlRequest.request(inputString);
			resultXml=CommonUtils.convertHisOutputParam(resultXml);
			logger.error("预约挂号支付接口【appointment.returnPay】-->2【"+operationName+"】出参："+resultXml);
			resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");
			if(!"0".equals(resCode)){
				String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
				logger.error("接口返回失败！resCode="+resCode+";errorMsg="+errorMsg);
				return CommonUtils.getErrorMsg(errorMsg);
			}else{  //退费成功
				try{
					cancelAppointmentCancel(orderId);
				}catch(Exception e){
					logger.error("释放号源出现异常！！.......",e);
				}
				return convertHisStringToV3Object();
			}
		}
	}

	/**
	 * 取消预约信息appointmentCancel，释放号源
	 * @author YJB
	 * @date 2017年7月19日 下午5:16:07
	 * @param reqXml
	 * @throws Exception
	 */
	private void cancelAppointmentCancel(String orderId) throws Exception {
		Bson filter = Filters.and(Filters.eq("orderId", orderId));
		List<org.bson.Document> list=new MongoDBHelper("channel_appointment_lock_info").query(filter);
		ChannelAppointmentLockInfo inpatientInfo = null;
		if(list!=null){
			org.bson.Document document=list.get(0);
			String json = document.toJson();
			JSONObject jsonObject = JSONObject.fromObject(json);
			inpatientInfo = (ChannelAppointmentLockInfo)JSONObject.toBean(jsonObject, ChannelAppointmentLockInfo.class);
		}
		String clinicSeq="";
		if(inpatientInfo==null){
			logger.error("根据订单号："+orderId+"到mongodb获取锁号信息失败！");
			return;
		}else{
			clinicSeq=inpatientInfo.getBookingNo();
		}
		String hisInterface="appointmentCancel";
		String inputString= getInputParamString(clinicSeq);
		String convertInputString=CommonUtils.convertHisInputParam(hisInterface, inputString);//将入参，转换成his系统接口要求的
		logger.error("退号接口【appointment.returnPay】-->【"+hisInterface+"】入参：user="+ParamConstants.USER+";password="+ParamConstants.PASSWORD+";parameter="+inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();
		String resultXml= xmlRequest.request(convertInputString);	//执行请求
		resultXml=CommonUtils.convertABSOutputParam(resultXml);	//出参，转换成符合金蝶接口规范
		logger.error("退号接口【appointment.returnPay】-->【"+hisInterface+"】出参："+resultXml);
	}

	/**
	 * his入参字符串构造（构造His系统取消预约接口需要的参数）
	 * @param reqXml
	 * @return
	 */
	private String getInputParamString(String bookingNo){
		StringBuilder str=new StringBuilder(200);
		str.append("<PARAMETER>");
		str.append("<APPTNO>"+bookingNo+"</APPTNO>");
		str.append("<CANCELOPERATOR>Kingdee</CANCELOPERATOR>");
		str.append("</PARAMETER>");
		return str.toString();
	}

	/**
	 * his入参字符串构造
	 * @param reqXml
	 * @return
	 */
	private String getConfirmCancelInputParamString(String reqXml){
		String clinicSeq=UtilXml.getValueByAllXml(reqXml, "clinicSeq");
		String orderId=UtilXml.getValueByAllXml(reqXml, "orderId");
		String refundFee=UtilXml.getValueByAllXml(reqXml, "refundFee");
		ChannelProductOrder productOrder=BusinessDBHelper.getPaymentInfo(orderId);
//		String totalFee=productOrder.getTotalFee();
//		String recPayAmount=productOrder.getRecPayAmout();
//		totalFee=StringUtils.isNotBlank(totalFee)?totalFee:"0";
//		recPayAmount=StringUtils.isNotBlank(recPayAmount)?recPayAmount:"0";
		String medicareSettleLogId=UtilXml.getValueByAllXml(reqXml, "medicareSettleLogId");
		String totalAmount="8600";
		if(StringUtils.isNotBlank(refundFee)){
			double fee=Double.parseDouble(refundFee);
			if(fee==10000){
				totalAmount=refundFee;
			}else{
				totalAmount="8600";
			}
		}
		StringBuilder str=new StringBuilder(200);
		str.append("<request><params>");
		str.append("<branchCode></branchCode>");
		str.append("<hisOrdNum>"+clinicSeq+"</hisOrdNum>");
		str.append("<psOrdNum>"+orderId+"</psOrdNum>");
		str.append("<refundMode>1</refundMode>");
		str.append("<refundAmout>"+totalAmount+"</refundAmout>");
		str.append("</params></request>");
		return str.toString();
	}


	/**
	 * his入参字符串构造
	 * @param reqXml
	 * @return
	 */
	private String getRenfundInputParamString(String reqXml,String cancelSerialNo,String cancelBillNo){
		String clinicSeq=UtilXml.getValueByAllXml(reqXml, "clinicSeq");
		String orderId=UtilXml.getValueByAllXml(reqXml, "orderId");
		String refundFee=UtilXml.getValueByAllXml(reqXml, "refundFee");
		String refundTime=UtilXml.getValueByAllXml(reqXml, "refundTime");
		String refundReason=UtilXml.getValueByAllXml(reqXml, "refundReason");
		String tradeNo=UtilXml.getValueByAllXml(reqXml, "tradeNo");
		String medicareSettleLogId=UtilXml.getValueByAllXml(reqXml, "medicareSettleLogId");
		String totalAmount="8600";
		if(StringUtils.isNotBlank(refundFee)){
			double fee=Double.parseDouble(refundFee);
			if(fee==10000){
				totalAmount=refundFee;
			}else{
				totalAmount="8600";
			}
		}

//		ChannelProductOrder productOrder=BusinessDBHelper.getPaymentInfo(orderId);
//		String totalFee=productOrder.getTotalFee();
//		String recPayAmount=productOrder.getRecPayAmout();
//		logger.error("诊间总费用："+totalFee);
//		totalFee=StringUtils.isNotBlank(totalFee)?totalFee:"0";
//		recPayAmount=StringUtils.isNotBlank(recPayAmount)?recPayAmount:"0";

		StringBuilder str=new StringBuilder(200);
		str.append("<request><params>");
		str.append("<branchCode></branchCode>");
		str.append("<hisOrdNum>"+clinicSeq+"</hisOrdNum>");
		str.append("<psOrdNum>"+orderId+"</psOrdNum>");
		str.append("<psRefOrdNum>"+orderId+"</psRefOrdNum>");
		str.append("<agtRefOrdNum>"+tradeNo+"</agtRefOrdNum>");
		str.append("<refundMode>1</refundMode>");
		str.append("<refundAmout>"+totalAmount+"</refundAmout>");
		str.append("<refundTime>"+refundTime+"</refundTime>");//退费时间
		str.append("<refundReason>"+refundReason+"</refundReason>");
		//ChannelAppointmentInfo appointInfo=BusinessDBHelper.getAppointmentInfo(orderId);
		//String  healthCard = UtilXml.getValueByAllXml(reqXml, "healthCard");
		String  healthCardNo = UtilXml.getValueByAllXml(reqXml, "healthCardNo");
		str.append("<patCardType>1</patCardType>");
		str.append("<patCardNo>"+healthCardNo+"</patCardNo>");
		str.append("</params></request>");
		return str.toString();
	}

	/**
	 * his出参转V3出参
	 * @return
	 */
	private String convertHisStringToV3Object(){
		StringBuilder str=new StringBuilder(200);
		str.append("<res>");
		str.append("<resultCode>0</resultCode>");
		str.append("<resultDesc>成功</resultDesc>");
		str.append("</res>");
		return str.toString();
	}



}
