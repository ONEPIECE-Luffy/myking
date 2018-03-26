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
 * �˷ѽӿ�
 * @author tangfulin
 *
 */
public class returnPay extends AbstractService{

	private static String hisInterface="ackRefundReg";
	
	@Override
	public String execute(String reqXml) throws Exception {
		//�����ǵ���Һ��˷ѻ���ԤԼ�Һ��˷�
		String orderId=UtilXml.getValueByAllXml(reqXml, "orderId");
			   
	   //����������6��֮ǰ�������˷ѣ����أ�4139�������
		if(CommonUtils.isBeforeFixedDate("06:00:00")){
			return CommonUtils.getErrorMsg("4139","ϵͳ���ڽ���״̬���޷��˷ѣ�����7:00֮����ܽ����˷�");
		}
		ChannelAppointmentInfo appointInfo=BusinessDBHelper.getAppointmentInfo(orderId);
		String regDate = null;
		String currentDate=DateUtils.getYMDTime(new Date());
		if(appointInfo != null && appointInfo.getRegDate() != null){
			regDate=appointInfo.getRegDate();		
		}else{
			logger.info("����"+" ��MongoDB���Ҳ�����¼��δ���ǽ������������"); //�ǽ���Ķ����˺ţ�ʹ���뵱���˷�����һ��
			regDate=currentDate;
		}
		
		logger.error("��ǰ���ڣ�"+currentDate+";����ԤԼ���ڣ�"+regDate);
		if(currentDate.equals(regDate)){
			//����ִ�� ԤԼ�˷�ȷ��ackRefundReg
			String operationName="ackRefundCurReg";
			String inputString=getConfirmCancelInputParamString(reqXml);

			logger.error("ԤԼ�Һ�֧���ӿڡ�appointment.returnPay��-->1��"+operationName+"����Σ�"+inputString);
			inputString=CommonUtils.convertHisInputParamWithOutUserInfo(operationName, inputString);
			HttpRequestService xmlRequest = HttpRequestService.getInstance();
			String resultXml= xmlRequest.request(inputString);	//ִ������
			resultXml=CommonUtils.convertHisOutputParam(resultXml);
			logger.error("ԤԼ�Һ�֧���ӿڡ�appointment.returnPay��-->1��"+operationName+"�����Σ�"+resultXml);
			String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");
			if(!"0".equals(resCode)){

				String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
				logger.error("�ӿڷ���ʧ�ܣ�resCode="+resCode+";errorMsg="+errorMsg);
				return CommonUtils.getErrorMsg("4140","�޷�ȡ������������ϵ�ͷ���Ա");
			}

			//����ִ�� ԤԼ�˷�refundReg����
			operationName="refundCurReg";
			String cancelSerialNo=UtilXml.getValueByAllXml(resultXml, "cancelSerialNo");
			String cancelBillNo=UtilXml.getValueByAllXml(resultXml, "cancelBillNo");
			//���ƴ��
			inputString=getRenfundInputParamString(reqXml,cancelSerialNo,cancelBillNo);
			logger.error("ԤԼ�Һ�֧���ӿڡ�appointment.returnPay��-->2��"+operationName+"����Σ�"+inputString);
			inputString=CommonUtils.convertHisInputParamWithOutUserInfo(operationName, inputString);
			//ִ������
			resultXml= xmlRequest.request(inputString);
			//����ת��
			resultXml=CommonUtils.convertHisOutputParam(resultXml);
			logger.error("ԤԼ�Һ�֧���ӿڡ�appointment.returnPay��-->2��"+operationName+"�����Σ�"+resultXml);
			resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");
			if(!"0".equals(resCode)){
				String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
				logger.error("�ӿڷ���ʧ�ܣ�resCode="+resCode+";errorMsg="+errorMsg);
				return CommonUtils.getErrorMsg(errorMsg);
			}else{//�˷ѳɹ�
				try{
					cancelAppointmentCancel(orderId);
				}catch(Exception e){
					logger.error("�ͷź�Դ�����쳣����.......",e);
				}
				return convertHisStringToV3Object();
			}
		}else{
			String inputString=getConfirmCancelInputParamString(reqXml);
			logger.error("ԤԼ�Һ�֧���ӿڡ�appointment.returnPay��-->1��"+hisInterface+"����Σ�"+inputString);
			inputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface, inputString);
			HttpRequestService xmlRequest = HttpRequestService.getInstance();
			String resultXml= xmlRequest.request(inputString);
			resultXml=CommonUtils.convertHisOutputParam(resultXml);
			logger.error("ԤԼ�Һ�֧���ӿڡ�appointment.returnPay��-->1��"+hisInterface+"�����Σ�"+resultXml);
			String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");
			if(!"0".equals(resCode)){
				String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
				logger.error("�ӿڷ���ʧ�ܣ�resCode="+resCode+";errorMsg="+errorMsg);
				return CommonUtils.getErrorMsg("4140","�޷�ȡ������������ϵ�ͷ���Ա");
			}
			//����ִ�� ԤԼ�˷�refundReg����
			String cancelSerialNo=UtilXml.getValueByAllXml(resultXml, "cancelSerialNo");
			String cancelBillNo=UtilXml.getValueByAllXml(resultXml, "cancelBillNo");
			inputString=getRenfundInputParamString(reqXml,cancelSerialNo,cancelBillNo);
			String operationName="refundReg";
			logger.error("ԤԼ�Һ�֧���ӿڡ�appointment.returnPay��-->2��"+operationName+"����Σ�"+inputString);
			inputString=CommonUtils.convertHisInputParamWithOutUserInfo(operationName, inputString);
			resultXml= xmlRequest.request(inputString);
			resultXml=CommonUtils.convertHisOutputParam(resultXml);
			logger.error("ԤԼ�Һ�֧���ӿڡ�appointment.returnPay��-->2��"+operationName+"�����Σ�"+resultXml);
			resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");
			if(!"0".equals(resCode)){
				String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
				logger.error("�ӿڷ���ʧ�ܣ�resCode="+resCode+";errorMsg="+errorMsg);
				return CommonUtils.getErrorMsg(errorMsg);
			}else{  //�˷ѳɹ�
				try{
					cancelAppointmentCancel(orderId);
				}catch(Exception e){
					logger.error("�ͷź�Դ�����쳣����.......",e);
				}
				return convertHisStringToV3Object();
			}
		}
	}

	/**
	 * ȡ��ԤԼ��ϢappointmentCancel���ͷź�Դ
	 * @author YJB
	 * @date 2017��7��19�� ����5:16:07
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
			logger.error("���ݶ����ţ�"+orderId+"��mongodb��ȡ������Ϣʧ�ܣ�");
			return;
		}else{
			clinicSeq=inpatientInfo.getBookingNo();
		}
		String hisInterface="appointmentCancel";
		String inputString= getInputParamString(clinicSeq);
		String convertInputString=CommonUtils.convertHisInputParam(hisInterface, inputString);//����Σ�ת����hisϵͳ�ӿ�Ҫ���
		logger.error("�˺Žӿڡ�appointment.returnPay��-->��"+hisInterface+"����Σ�user="+ParamConstants.USER+";password="+ParamConstants.PASSWORD+";parameter="+inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();
		String resultXml= xmlRequest.request(convertInputString);	//ִ������
		resultXml=CommonUtils.convertABSOutputParam(resultXml);	//���Σ�ת���ɷ��Ͻ���ӿڹ淶
		logger.error("�˺Žӿڡ�appointment.returnPay��-->��"+hisInterface+"�����Σ�"+resultXml);
	}

	/**
	 * his����ַ������죨����Hisϵͳȡ��ԤԼ�ӿ���Ҫ�Ĳ�����
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
	 * his����ַ�������
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
	 * his����ַ�������
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
//		logger.error("����ܷ��ã�"+totalFee);
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
		str.append("<refundTime>"+refundTime+"</refundTime>");//�˷�ʱ��
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
	 * his����תV3����
	 * @return
	 */
	private String convertHisStringToV3Object(){
		StringBuilder str=new StringBuilder(200);
		str.append("<res>");
		str.append("<resultCode>0</resultCode>");
		str.append("<resultDesc>�ɹ�</resultDesc>");
		str.append("</res>");
		return str.toString();
	}



}
