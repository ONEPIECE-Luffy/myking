package com.kingdeehit.mobile.his.xianggang.service.support;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingdeehit.mobile.his.entities.V3.result.support.GetRegisterInfoResultItemV3;
import com.kingdeehit.mobile.his.entities.V3.result.support.GetRegisterInfoResultV3;
import com.kingdeehit.mobile.his.entities.table.ChannelAppointmentInfo;
import com.kingdeehit.mobile.his.xianggang.constant.Const;
import com.kingdeehit.mobile.his.xianggang.constant.ParamConstants;
import com.kingdeehit.mobile.his.xianggang.entity.table.ChannelAppointmentLockInfo;
import com.kingdeehit.mobile.his.xianggang.entity.table.RegisterInfo;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.register.OrderCurReg;
import com.kingdeehit.mobile.his.xianggang.service.util.BusinessDBHelper;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.StringUtil;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;


/**
 * ԤԼ�Һ���Ϣ��ѯ
 * @author YJB
 *
 */
public class getRegisterInfo extends AbstractService {

	private static String hisInterface="getOrderData";
	private static String hisInterface2="getRegRecords";
	
	@Override
	public String execute(String reqXml) throws Exception {
	 
		String orderId=UtilXml.getValueByAllXml(reqXml, "orderId");
		String resultXml="";
		String inputString=getPayInputParamString(reqXml);
		String convertInputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface2, inputString);
		logger.error("ԤԼ�Һ���Ϣ��ѯ�ӿڡ�support.getRegisterInfo��-->��"+hisInterface2+"����Σ�user="+ParamConstants.USER+";password="+ParamConstants.PASSWORD+";parameter="+inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();
		resultXml= xmlRequest.request(convertInputString);
		//resultXml = "<response><resultCode>0</resultCode><resultMessage>���ýӿ�������</resultMessage><result><item><hisOrdNum>2529913187</hisOrdNum><hisRefOrdNum></hisRefOrdNum><deptCode>9932</deptCode><deptName>���Բ�����</deptName><doctorCode></doctorCode><doctorName></doctorName><bookDate>2017-10-09</bookDate><timeFlag>2</timeFlag><beginTime>16:30</beginTime><endTime>17:00</endTime><regType>1</regType><orderMode>2</orderMode><payMode>1</payMode><status>2</status><realRegFee>0</realRegFee><realTreatFee>8600</realTreatFee><desc></desc><receiptNum>R00000002563</receiptNum><serialNum></serialNum><visitLocation>����¥-���Բ�����</visitLocation><takeTime>2017-10-09 16:00:00</takeTime><barCode>AJ9615893</barCode><visitDesc>����ԤԼʱ��ο�ʼʱ����ǰ���Сʱ��ҽԺ����¥-���Բ��������̨�������</visitDesc></item></result></response>";
		resultXml=CommonUtils.convertHisOutputParam(resultXml);
		logger.error("ԤԼ�Һ���Ϣ��ѯ�ӿڡ�support.getRegisterInfo��-->��"+hisInterface2+"�����Σ�"+resultXml);
		//String resultXml = "<response><resultCode>0</resultCode><resultMessage>���ýӿ�������</resultMessage><result><item><hisOrdNum>5047829558</hisOrdNum><hisRefOrdNum></hisRefOrdNum><deptCode>8124</deptCode><deptName>���ڿ�����</deptName><doctorCode></doctorCode><doctorName></doctorName><bookDate>2018-02-06</bookDate><timeFlag>2</timeFlag><beginTime>16:30</beginTime><endTime>17:00</endTime><regType>1</regType><orderMode>2</orderMode><payMode>1</payMode><status>8</status><realRegFee>0</realRegFee><realTreatFee>10000</realTreatFee><desc></desc><receiptNum>R00000082635</receiptNum><serialNum></serialNum><visitLocation>��¥</visitLocation><takeTime>2018-02-06 16:00:00</takeTime><barCode>AK2331553</barCode><visitDesc>����ԤԼʱ��ο�ʼʱ����ǰ���Сʱ��ҽԺ��¥����̨�������</visitDesc></item><item><hisOrdNum>49653296";
		String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");
			if("0".equals(resCode)){
				GetRegisterInfoResultV3 resultV3= convertResult(reqXml,resultXml); 
				XStream xstream = UtilXml.getXStream(GetRegisterInfoResultV3.class);
				return xstream.toXML(resultV3);
			}else if("1".equals(resCode)){

				List<GetRegisterInfoResultItemV3> otherRegList = getOtherRegRecords(reqXml);
				
				if(otherRegList==null){
					otherRegList = new  ArrayList<GetRegisterInfoResultItemV3>();
				}
				//getAppointmetOrder(reqXml,otherRegList);//��ȡ���߾��������ǽ����Ժ�ļ�¼��Ȼ���Ƿ�֧��ֱ������Ϊ 0,  ƽ̨����Ĭ��δ���ĺ�Դ����֧�����������������ʵ����ɾ����ɾ������ز��ԣ�����ᵼ���������⣬Ӱ��ĳЩ����ҽ���Һź����֧���޷�ҽ������
				 
				GetRegisterInfoResultV3 resultV3 = new GetRegisterInfoResultV3();
				if(otherRegList!=null&&otherRegList.size()>0){
					resultV3.setList(otherRegList);
				}
				resultV3.setResultCode("0");
				resultV3.setResultDesc("�ɹ�");
				XStream xstream = UtilXml.getXStream(GetRegisterInfoResultV3.class);
				return xstream.toXML(resultV3);
			}else{
				String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
				logger.error("�ӿڷ���ʧ�ܣ�resCode="+resCode+";errorMsg="+errorMsg);
				return CommonUtils.getErrorMsg(errorMsg);
			}
	 
	}
	/**
	 * �����ӿڳ��Σ���������һ���ӿڻ�ȡ����ĹҺż�¼�Ĳ�������������ӿڵ����ݺϲ�(�����ظ����󷵻�
	 * @author YJB
	 * @date 2017��9��8�� ����11:11:44
	 * @param reqXml
	 * @param resultXml
	 * @return
	 * @throws Exception
	 */
	private GetRegisterInfoResultV3 convertResult(String reqXml, String resultXml) throws Exception {
		
		
		GetRegisterInfoResultV3 resultV3 = convertGetRegRecordsToV3Object(reqXml,resultXml);
		
		 List<GetRegisterInfoResultItemV3> list = resultV3.getList();
		 
		 String[] orders = getOrders(list);
		 
		 List<GetRegisterInfoResultItemV3> otherRegList = getOtherRegRecords(reqXml);
		
		 boolean isRepeat = false;
		 //�ж��Ƿ����ظ����������
		 if(otherRegList!=null&&otherRegList.size()>0){
			 
			 for(GetRegisterInfoResultItemV3 item: otherRegList){
				 
					String bookingNo = item.getBookingNo().trim();
					 for(int i=0;i<orders.length;i++){ 
						 if(orders[i].equals(bookingNo)){
							 isRepeat = true;
							 break;
						 } 
					 }
					 if(!isRepeat){	  //������û���ظ����ͼ�������
						 list.add(item);
					 } 
				    isRepeat = false; //��λ
				 }
		 }
		 
		 if(list==null){
			 list = new  ArrayList<GetRegisterInfoResultItemV3>();
		  }
		 //getAppointmetOrder(reqXml,list); //��ȡ���߾��������ǽ����Ժ��ԤԼ��¼��Ȼ���Ƿ�֧��ֱ������Ϊ 0,  ƽ̨����Ĭ��δ���ĺ�Դ����֧�����������������ʵ����ɾ����ɾ������ز��ԣ�����ᵼ���������⣬Ӱ��ĳЩ����ҽ���Һź����֧���޷�ҽ������
		 
		return resultV3;
	}
	/**
	 * ��ȡ�������켰�Ժ�ƽ̨�ĹҺż�¼������His�ҵĹҺż�¼�ӿڲ�����δ����ԤԼ�Һż�¼������Ժ�His�����ˣ��Ͳ���Ҫ��������ˣ�
	 * @author YJB
	 * @date 2017��9��29�� ����6:42:33
	 * @param reqXml
	 * @param list
	 * @throws ParseException 
	 */
	
	
	private void getAppointmetOrder(String reqXml,List<GetRegisterInfoResultItemV3> list) throws ParseException {
			
		String healthCardNo = UtilXml.getValueByAllXml(reqXml, "healthCardNo");

		String tomorrow = CommonUtils.getFetureDate(1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long tomorrowTime = sdf.parse(tomorrow).getTime();
		long regDataTime = 0;
		
		List<ChannelAppointmentInfo> appointmentInfo = BusinessDBHelper.getAllAppointmentInfo(healthCardNo);
		String regDate = "";
		String orderId = "";
		if(appointmentInfo!=null&&appointmentInfo.size()>0){
			
			GetRegisterInfoResultItemV3 itemV3 = null;
			
			for (ChannelAppointmentInfo channelAppointmentInfo : appointmentInfo) {
				regDate = channelAppointmentInfo.getRegDate();
				regDataTime = sdf.parse(regDate).getTime();
				orderId = channelAppointmentInfo.getOrderId();
				if (tomorrowTime<=regDataTime) { //���ڻ��������ļ�¼���г�
					logger.info("��ȡ������ԤԼ������Ϊ "+orderId+" ��ԤԼ��¼");
					itemV3 =  new GetRegisterInfoResultItemV3();
					itemV3.setOrderId(orderId);
					itemV3.setIsPayment("0");
					list.add(itemV3);
				}else{
					logger.info("����ԤԼ������Ϊ "+orderId+" ��ԤԼ��¼,ԤԼ������ "+regDate+" ��������ʾ����");
				}
			}
		}else{
			logger.info("û�в�ѯ������ԤԼ��¼��");
		}
	}
	
	
	private void getAppointmetOrder1(String reqXml,List<GetRegisterInfoResultItemV3> list) throws ParseException {
		
		String healthCardNo = UtilXml.getValueByAllXml(reqXml, "healthCardNo");

		String tomorrow = CommonUtils.getFetureDate(1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long tomorrowTime = sdf.parse(tomorrow).getTime();
		long regDataTime = 0;
		
		String regDate = "";
		String orderId = "111";
		logger.info("��ȡ������ԤԼ������Ϊ "+orderId+" ��ԤԼ��¼");
		GetRegisterInfoResultItemV3 itemV3 =  new GetRegisterInfoResultItemV3();
		itemV3.setOrderId(orderId);
		itemV3.setIsPayment("0");
		if(list==null){
			list = new  ArrayList<GetRegisterInfoResultItemV3>();
		}
		list.add(itemV3);
	 
		
	}
	/**
	 * ��ȡ��һ���ӿ��еĶ����ţ����ڵ�����һ���ӿڵ�ʱ���ж�His���صĶ����Ƿ��ظ�
	 * @author YJB
	 * @date 2017��9��8�� ����10:57:07
	 * @param list
	 * @return
	 */
	private String[] getOrders(List<GetRegisterInfoResultItemV3> list) {
		
		String[] orders = new String[list.size()];
		int i=0;
		for(GetRegisterInfoResultItemV3 item: list){
			orders[i] = item.getBookingNo().trim();
			i++;
		}
		return orders;
	}
	/**
	 * ��ȡ��һ���ӿڵ��չҺŲ�ѯ�еĹҺż�¼
	 * @author YJB
	 * @date 2017��9��8�� ����11:15:15
	 * @param reqXml
	 * @param resultXml
	 * @return
	 * @throws Exception
	 */
	public List<GetRegisterInfoResultItemV3> getOtherRegRecords(String reqXml) throws Exception {
		
		String inputString= getOrderDataInputParamString(reqXml);
		String convertInputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface, inputString);
		logger.error("ԤԼ�Һ���Ϣ��ѯ�ӿڡ�support.getRegisterInfo��-->��"+hisInterface+"����Σ�user="+ParamConstants.USER+";password="+ParamConstants.PASSWORD+";parameter="+inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();
		String resultXml= xmlRequest.request(convertInputString);
		//SString resultXml = "<response><resultCode>0</resultCode><resultMessage>���ýӿ�������</resultMessage><result><item><patCardType>1</patCardType><patCardNo></patCardNo><patName>����ƽ</patName><deptCode>9932</deptCode><deptName>���Բ�����</deptName><doctorCode>9932</doctorCode><doctorName>���Բ�����</doctorName><scheduleDate>2017-10-09</scheduleDate><timeFlag>2</timeFlag><beginTime>16:30</beginTime><endTime>17:00</endTime><workId>6362292</workId><regFee>0</regFee><treatFee>10000</treatFee><SStreatFee>8600</SStreatFee><payFlag>2</payFlag><receiptNum>R00000002563</receiptNum><serialNum></serialNum><visitLocation>����¥-���Բ�����</visitLocation><payMode></payMode><regType></regType><orderTime>2017-10-09 08:30:17</orderTime><hisOrderNum>2529913187</hisOrderNum></item></result></response>";
		resultXml=CommonUtils.convertHisOutputParam(resultXml);
		logger.error("ԤԼ�Һ���Ϣ��ѯ�ӿڡ�support.getRegisterInfo��-->��"+hisInterface+"�����Σ�"+resultXml);
		String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");

		if("0".equals(resCode)){
			GetRegisterInfoResultV3 resultV3=convertGetOrderDataToV3Object(reqXml,resultXml);
			return resultV3.getList();
		}else if("1".equals(resCode)){
			return  null;
		}else{
			String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
			logger.error("�ӿڷ���ʧ�ܣ�resCode="+resCode+";errorMsg="+errorMsg);
			return null;
		}
		
	}
	 
	/**
	 * ���� getOrderData�ӿڵ��������
	 * @author YJB
	 * @date 2017��9��21�� ����6:16:04
	 * @param reqXml
	 * @return
	 */
	private String getOrderDataInputParamString(String reqXml) {
		
		String idCardNo = UtilXml.getValueByAllXml(reqXml, "idCardNo");
		String patName = UtilXml.getValueByAllXml(reqXml, "patientName");
		String orderNo = UtilXml.getValueByAllXml(reqXml, "orderId");
		
		StringBuilder str=new StringBuilder(100);
		str.append("<request><params>");
		str.append("<branchCode>xgdxszyy</branchCode>");
		str.append("<idCardNo>"+idCardNo+"</idCardNo>");
		str.append("<patName>"+patName+"</patName>");
		str.append("<orderNo>"+orderNo+"</orderNo>");
		str.append("</params></request>");
		return str.toString();
	}
	/**
	 * V3���תhis���
	 * @param req
	 * @return
	 */
	private String getPayInputParamString(String req){
		String orderId=UtilXml.getValueByAllXml(req, "orderId");
		String healthCardNo=UtilXml.getValueByAllXml(req, "healthCardNo");
		String orderDate=UtilXml.getValueByAllXml(req, "orderDate");
		//String orderDate="";
		String startDate = null;
		String endDate = null;
		
		StringBuilder str=new StringBuilder(100);
		str.append("<request><params>");
		str.append("<branchCode></branchCode>");
		str.append("<patCardType>1</patCardType>");
		str.append("<patCardNo>"+healthCardNo+"</patCardNo>");
		str.append("<orderMode>1</orderMode>");
		if(StringUtils.isEmpty(orderDate)){
			startDate = CommonUtils.getPastDate(7);
			endDate = CommonUtils.getFetureDate(7);
			str.append("<beginDate>" + startDate + "</beginDate>");
			str.append("<endDate>" + endDate + "</endDate>");
		}else{
			str.append("<beginDate>"+orderDate+"</beginDate>");
			str.append("<endDate>"+orderDate+"</endDate>");
		}
		str.append("<psOrdNum>"+orderId+"</psOrdNum>");
		str.append("</params></request>");
		return str.toString();
	}
 
	/**
	 * ���� ����getOrderData ���ص�����
	 * @author YJB
	 * @date 2017��9��19�� ����4:21:25
	 * @param req
	 * @param resultXml
	 * @return
	 * @throws Exception 
	 */
	private GetRegisterInfoResultV3 convertGetOrderDataToV3Object(String req,String resultXml) throws Exception{
		
		String svObjectId = "";
		String healthCardNo=UtilXml.getValueByAllXml(req, "healthCardNo");
		GetRegisterInfoResultV3 resultV3=new GetRegisterInfoResultV3();
		Document document=DocumentHelper.parseText(resultXml);
		Element element=document.getRootElement();
		Element res=element.element("result");
		List<Element> list=res.elements("item");
		List<GetRegisterInfoResultItemV3> payInfoList=new ArrayList<GetRegisterInfoResultItemV3>();
		for(Element ele:list){
			GetRegisterInfoResultItemV3 resultItem=new GetRegisterInfoResultItemV3();
			String status=ele.elementText("payFlag");
			if("0".equals(status)){
				resultItem.setStatus("0");
			}else if("1".equals(status)){
				resultItem.setStatus("1");
			}else if("2".equals(status)){
				resultItem.setStatus("1");
			}
			resultItem.setHealthCardNo(healthCardNo);
			resultItem.setPatientName(UtilXml.getValueByAllXml(req, "patientName"));
			resultItem.setPatientId(UtilXml.getValueByAllXml(req, "patientId"));
			String orderId= getOrderIdByBookingNo(ele.elementText("hisOrderNum"));
			resultItem.setOrderId(orderId);
			
			resultItem.setBookingNo(ele.elementText("hisOrderNum"));  
			
			resultItem.setClinicSeq(ele.elementText("receiptNum"));
			resultItem.setHospitalId(Const.HOSPITAL_ID);
			resultItem.setDeptId(ele.elementText("deptCode"));
			resultItem.setDeptName(ele.elementText("deptName"));
			if(StringUtil.isEmpty(ele.elementText("doctorCode"))){
				resultItem.setDoctorId(ele.elementText("deptCode"));
			}else{
				resultItem.setDoctorId(ele.elementText("doctorCode"));
			}
			if(StringUtil.isEmpty(ele.elementText("doctorName"))){
				resultItem.setDoctorName(ele.elementText("deptName"));
			}else{
				resultItem.setDoctorName(ele.elementText("doctorName"));
			}
			String regData = ele.elementText("scheduleDate").trim();
			resultItem.setRegDate(regData);
			String timeFlag=ele.elementText("timeFlag");
			if("1".equals(timeFlag)){
				resultItem.setShiftCode("1");
				resultItem.setShiftName("����");
			}else if("2".equals(timeFlag)){
				resultItem.setShiftCode("2");
				resultItem.setShiftName("����");
			}else if("3".equals(timeFlag)){
				resultItem.setShiftCode("3");
				resultItem.setShiftName("����");
			}
			resultItem.setStartTime(ele.elementText("beginTime"));
			resultItem.setEndTime(ele.elementText("endTime"));
			resultItem.setRegisterType("1");
			
			resultItem.setQueueNo(ele.elementText("serialNum"));
			resultItem.setWaitingCount("");
			resultItem.setWaitingTime("");
			//resultItem.setInfoTime(ele.elementText("takeTime"));
			//resultItem.setVisitTime(ele.elementText("takeTime"));//����ʱ�䣨ʵ�ʽ���ʱ�䣩YYYY-MM-DD HI24:MI:SS
//			boolean isOther = CommonUtils.isOtherPlatform(resultItem.getBookingNo());
//			String  mzlsh = getMzlsh(ele,isOther);
			//resultItem.setBookingNo(ele.elementText("hisOrderNum")); //��ԭ����bookingNo��������Ȼ���滻�����ŵ�id������ҽ���Һź��ҽ����ҽ����
		
		 
			resultItem.setIsCancelabe("0"); //Ŀǰ��ʱȫ����Դȫ�������˺�
			
			
			String bookingNo = ele.elementText("hisOrderNum");
			svObjectId = getSvObjectByBookingNo(bookingNo);
			String today = CommonUtils.getToday();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			long now = sdf.parse(sdf.format(new Date())).getTime();
			long startTime = sdf.parse(ele.elementText("beginTime")).getTime();
			logger.info(bookingNo+" ��Դ��ʼʱ���� "+ele.elementText("beginTime")+ "  "+startTime);
			logger.info("��ǰʱ���� "+sdf.format(new Date())+ "  "+now);
			
			if("0".equals(status)){//   	0 δ����        �ж��û��Ƿ���ҽ��ԤԼȡ�ţ�����ǣ�Ҫ�������Ʊ�����ﵱ�ղſ��Խ���֧��
				if(today.equals(regData)){ 
					resultItem.setIsPayment("1"); // isPayment  �Ƿ�����֧����0-������1-����
					resultItem.setPayStatus("0"); //0-δ֧�� 1-��֧��  2-���˷�
					resultItem.setIsSelectSvObject("1");//����ѡ���Żݶ���
					
					//resultItem.setIsSelectSvObject("0"); //��ʱ������ѡ��ҽ������
					/*if((startTime-now)>1000*60*30){  //���վ��ﵱǰ������ǰС�ڰ�Сʱ����������֧�� 
						logger.info(bookingNo+" ��Դ��ʼʱ�� ���� ��ǰʱ�� 30���ӣ�����������֧��");
						//----���ﵱ�죬����ʱ����ھ���ǰ��Сʱ����ͨ���ߺ;��ﻼ�߶�����֧��--------//
						resultItem.setIsPayment("1"); // isPayment  �Ƿ�����֧����0-������1-����
						resultItem.setPayStatus("0"); //0-δ֧�� 1-��֧��  2-���˷�
						//resultItem.setIsSelectSvObject("1");//����ѡ���Żݶ���
						resultItem.setIsSelectSvObject("0"); //��ʱ������ѡ��ҽ������
					}else{
						logger.info(bookingNo+" ��Դ��ʼʱ��  С�� ��ǰʱ�� 30���ӣ���������֧��");
						resultItem.setIsPayment("0"); //ҽ���û�  �Ǿ��ﵱ�գ�������֧��
						resultItem.setPayStatus("0"); //0-δ֧�� 1-��֧��  2-���˷�
						//resultItem.setIsSelectSvObject("1");//����ѡ���Żݶ���
						resultItem.setIsSelectSvObject("0"); //��ʱ������ѡ��ҽ������
					}*/
				}else{
					if("02".equals(svObjectId)){ 
						String regDate = ele.elementText("bookDate").trim();
						logger.info("ҽ�����ߣ�ԤԼ������ "+regDate);
						resultItem.setIsPayment("0"); //ҽ���û�  �Ǿ��ﵱ�գ�������֧��
						resultItem.setPayStatus("0"); //0-δ֧�� 1-��֧��  2-���˷�
						resultItem.setIsSelectSvObject("1");//����ѡ���Żݶ���
						resultItem.setIsCancelabe("1");
						//resultItem.setIsSelectSvObject("0"); //��ʱ������ѡ��ҽ������
					}else{
						logger.info("�˻��߷�ҽ������");
						resultItem.setIsPayment("1"); // isPayment  �Ƿ�����֧����0-������1-����
						resultItem.setPayStatus("0"); //0-δ֧�� 1-��֧��  2-���˷�
						//resultItem.setIsSelectSvObject("1");//����ѡ���Żݶ���
						resultItem.setIsSelectSvObject("0"); //��ʱ������ѡ��ҽ������
						resultItem.setIsCancelabe("1");
					}
				}
			} else if ("3".equals(status)) {
				resultItem.setPayStatus("0");
				resultItem.setIsCancelabe("0");
				resultItem.setIsPayment("0");
				resultItem.setIsSelectSvObject("0");
			} else if ("2".equals(status) || "4".equals(status)) {
				if ("2".equals(status)) {
					resultItem.setIsCancelabe("1");
				}
				resultItem.setIsSelectSvObject("0");
				resultItem.setIsPayment("0"); // isPayment  �Ƿ�����֧����0-������1-����
				resultItem.setPayStatus("1"); // 0-δ֧�� 1-��֧��  2-���˷�
			}else{//1 �Ѹ���δȡ��  2��ȡ��  
				resultItem.setIsSelectSvObject("0");
				resultItem.setIsPayment("0"); // isPayment  �Ƿ�����֧����0-������1-����
				resultItem.setPayStatus("2"); // 0-δ֧�� 1-��֧��  2-���˷�
			}
			 

			if(StringUtil.isEmpty(orderId)){ //����ǹҺŶ���������Ҫ���������ֶ�
			  //���붩���������ֶ�
				//����Ƿ�΢���������ȵ������Žӿ�
				resultItem.setIsCancelabe("0");
				//resultItem.setAutoLockBeforePay("1"); //modify by CXM
				resultItem.setOrderType("99");
				resultItem.setOrderTypeName("��΢������");
			}else{
				resultItem.setOrderType("10");
				resultItem.setOrderTypeName("΢������");
			}
			resultItem.setOrderTime("");
			resultItem.setRemark("����ǰ���Сʱ�ڣ�����������֧�������Ʋ����ڡ�");
			String regFee =	ele.elementText("regFee");
			resultItem.setRegFee(regFee);
			
			String treatFee = ele.elementText("treatFee");
			resultItem.setTreatFee(treatFee);
	
			resultItem.setYhFee("0");
			
			Double payFee = Double.parseDouble(treatFee)+ Double.parseDouble(regFee);
			resultItem.setPayFee(payFee+"");
			payInfoList.add(resultItem);
		}
		resultV3.setList(payInfoList);
		resultV3.setResultCode("0");
		resultV3.setResultDesc("�ɹ�");
		return resultV3;
	}
	/**
	 * ���� ����getRegRecords(ԤԼ���ϲ�ѯ) ���ص�����
	 * @author YJB
	 * @date 2017��9��19�� ����4:21:25
	 * @param req
	 * @param resultXml
	 * @return
	 * @throws DocumentException
	 * @throws ParseException 
	 */
	private GetRegisterInfoResultV3 convertGetRegRecordsToV3Object(String req,String resultXml) throws DocumentException, ParseException{
	
		//String orderId=UtilXml.getValueByAllXml(req, "orderId");
		//logger.error("����Ķ�������  "+orderId);
	
		String svObjectId="";
		String healthCardNo=UtilXml.getValueByAllXml(req, "healthCardNo");
		GetRegisterInfoResultV3 resultV3=new GetRegisterInfoResultV3();
		Document document=DocumentHelper.parseText(resultXml);
		Element element=document.getRootElement();
		Element res=element.element("result");
		List<Element> list=res.elements("item");
		List<GetRegisterInfoResultItemV3> payInfoList=new ArrayList<GetRegisterInfoResultItemV3>();
		for(Element ele:list){
			GetRegisterInfoResultItemV3 resultItem=new GetRegisterInfoResultItemV3();
			String status=ele.elementText("status");
		    //ֻ��ʾ��Щ ��Ҫ֧����  ����֧���ĺ�Դ�����߿����ɣ������Ի�����˵���岻��
			if("1".equals(status)){ //1��δ֧��  2����֧�� 3����ȡ��4���ɷѳ�ʱ5����ȡ��6���Ѿ���7���ѹ���8�����˷�
				resultItem.setStatus("0");
			}else if("2".equals(status)){
				resultItem.setStatus("1");
			}else if("3".equals(status)){//resultItem.setStatus("4");
				break;
			}else if("4".equals(status)){//resultItem.setStatus("4");
				break;
			}else if("5".equals(status)){
				resultItem.setStatus("1");
			}else if("6".equals(status)){
				resultItem.setStatus("3");
			}else if("7".equals(status)){//resultItem.setStatus("7");
				break;
			}else if("8".equals(status)){ //���˷� ����ʾ
				break;
			} 
			
			
			resultItem.setHealthCardNo(healthCardNo);
			resultItem.setPatientName(UtilXml.getValueByAllXml(req, "patientName"));
			resultItem.setPatientId(UtilXml.getValueByAllXml(req, "patientId"));
			String orderId= getOrderIdByBookingNo(ele.elementText("hisOrdNum"));
			resultItem.setOrderId(orderId);
			resultItem.setBookingNo(ele.elementText("hisOrdNum"));
			resultItem.setClinicSeq(ele.elementText("receiptNum"));
			resultItem.setHospitalId(Const.HOSPITAL_ID);
			resultItem.setDeptId(ele.elementText("deptCode"));
			resultItem.setDeptName(ele.elementText("deptName"));
			if(StringUtil.isEmpty(ele.elementText("doctorCode"))){
				resultItem.setDoctorId(ele.elementText("deptCode"));
			}else{
				resultItem.setDoctorId(ele.elementText("doctorCode"));
			}
			if(StringUtil.isEmpty(ele.elementText("doctorName"))){
				resultItem.setDoctorName(ele.elementText("deptName"));
			}else{
				resultItem.setDoctorName(ele.elementText("doctorName"));
			}
			String regData = ele.elementText("bookDate");
			resultItem.setRegDate(regData);
			String timeFlag=ele.elementText("timeFlag");
			if("1".equals(timeFlag)){
				resultItem.setShiftCode("1");
				resultItem.setShiftName("����");
			}else if("2".equals(timeFlag)){
				resultItem.setShiftCode("2");
				resultItem.setShiftName("����");
			}else if("3".equals(timeFlag)){
				resultItem.setShiftCode("3");
				resultItem.setShiftName("����");
			}
			resultItem.setStartTime(ele.elementText("beginTime"));
			resultItem.setEndTime(ele.elementText("endTime"));
			resultItem.setRegisterType("1");
			
			resultItem.setQueueNo(ele.elementText("serialNum"));
			resultItem.setWaitingCount("");
			resultItem.setWaitingTime("");
			resultItem.setInfoTime(ele.elementText("takeTime"));
			resultItem.setVisitTime(ele.elementText("takeTime"));//����ʱ�䣨ʵ�ʽ���ʱ�䣩YYYY-MM-DD HI24:MI:SS
			
			resultItem.setOrderTime("");
			resultItem.setRemark("����ǰ���Сʱ�ڣ�����������֧�������Ʋ����ڡ�");
			
			
			if("2".equals(status)){//��������Ѿ�֧������ô�ǿ����˷ѵ�
				resultItem.setIsCancelabe("1");
			}else{
				resultItem.setIsCancelabe("0"); //�������δ�����������˺ŵ�  
			}
			
			String bookingNo = ele.elementText("hisOrdNum");
			
			svObjectId = getSvObjectByBookingNo(bookingNo);
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			long now = sdf.parse(sdf.format(new Date())).getTime();
			long startTime = sdf.parse(ele.elementText("beginTime")).getTime();
			String today = CommonUtils.getToday();
			logger.info(bookingNo+" ��Դ��ʼʱ���� "+ele.elementText("beginTime")+ "  "+startTime);
			logger.info("��ǰʱ���� "+sdf.format(new Date())+ "  "+now);
			
			
			if("1".equals(status)){ //1��δ֧��  2����֧�� 3����ȡ��4���ɷѳ�ʱ5����ȡ��6���Ѿ���7���ѹ���8�����˷�
				if(today.equals(regData)){ 
					resultItem.setIsPayment("1"); // isPayment  �Ƿ�����֧����0-������1-����
					resultItem.setPayStatus("0"); //0-δ֧�� 1-��֧��  2-���˷�
					resultItem.setIsSelectSvObject("1");//����ѡ���Żݶ���
					resultItem.setIsCancelabe("1");
					//resultItem.setIsSelectSvObject("0"); //��ʱ������ѡ��ҽ������
				/*	if((now-startTime)>1000*60*30){  //���վ��ﵱǰ������ǰС�ڰ�Сʱ����������֧��
						//----���ﵱ�죬����ʱ����ھ���ǰ��Сʱ����ͨ���ߺ;��ﻼ�߶�����֧��--------//
						logger.info(bookingNo+" ��Դ��ʼʱ��  ���� ��ǰʱ�� 30���ӣ�������֧��");
						resultItem.setIsPayment("1"); // isPayment  �Ƿ�����֧����0-������1-����
						resultItem.setPayStatus("0"); //0-δ֧�� 1-��֧��  2-���˷�
						//resultItem.setIsSelectSvObject("1");//����ѡ���Żݶ���
						resultItem.setIsSelectSvObject("0"); //��ʱ������ѡ��ҽ������
					}else{
						logger.info(bookingNo+" ��Դ��ʼʱ��  С�� ��ǰʱ�� 30���ӣ���������֧��");
						resultItem.setIsPayment("0"); //ҽ���û�  �Ǿ��ﵱ�գ�������֧��
						resultItem.setPayStatus("0"); //0-δ֧�� 1-��֧��  2-���˷�
						//resultItem.setIsSelectSvObject("1");//����ѡ���Żݶ���
						resultItem.setIsSelectSvObject("0"); //��ʱ������ѡ��ҽ������
					}*/
				}else{
					if("02".equals(svObjectId)){ 
						String regDate = ele.elementText("bookDate").trim();
						logger.info("ҽ�����ߣ�ԤԼ������ "+regDate);
						resultItem.setIsPayment("0"); //ҽ���û�  �Ǿ��ﵱ�գ�������֧��
						resultItem.setPayStatus("0"); //0-δ֧�� 1-��֧��  2-���˷�
						resultItem.setIsSelectSvObject("1");//����ѡ���Żݶ���
						resultItem.setIsCancelabe("1");
						//resultItem.setIsSelectSvObject("0"); //��ʱ������ѡ��ҽ������
					}else{
						resultItem.setIsPayment("1"); // isPayment  �Ƿ�����֧����0-������1-����
						resultItem.setPayStatus("0"); //0-δ֧�� 1-��֧��  2-���˷�
						//resultItem.setIsSelectSvObject("1");//����ѡ���Żݶ���
						resultItem.setIsSelectSvObject("0"); //��ʱ������ѡ��ҽ������
						resultItem.setIsCancelabe("1");
					}
				}
			}else if("2".equals(status)){//2 ��֧����ԤԼ�ɹ������Խ���ԤԼ�˷ѣ�
				resultItem.setIsPayment("0"); // isPayment  �Ƿ�����֧����0-������1-����
				resultItem.setPayStatus("1"); //0-δ֧�� 1-��֧��  2-���˷�
				resultItem.setIsSelectSvObject("0");
			}else if("5".equals(status)){//5����ȡ��
				resultItem.setIsPayment("0"); // isPayment  �Ƿ�����֧����0-������1-����
				resultItem.setPayStatus("1"); //0-δ֧�� 1-��֧��  2-���˷�
				resultItem.setIsSelectSvObject("0");
			}else if("6".equals(status)){//6���Ѿ���
				resultItem.setIsPayment("0"); // isPayment  �Ƿ�����֧����0-������1-����
				resultItem.setPayStatus("1"); //0-δ֧�� 1-��֧��  2-���˷�
				resultItem.setIsSelectSvObject("0");
			}
			
			if(StringUtil.isEmpty(orderId)){ //����ǹҺŶ���������Ҫ���������ֶ�,�ǽ������MongoDB���Ҳ���orderId
				//resultItem.setAutoLockBeforePay("1"); //modify by CXM
				resultItem.setOrderType("99");
				resultItem.setOrderTypeName("��΢������");
				resultItem.setIsCancelabe("0"); //�������ҺŲ�����ȡ��
			}else{
				resultItem.setOrderType("10");
				resultItem.setOrderTypeName("΢������");
			}
			String regFee =	ele.elementText("realRegFee");
			resultItem.setRegFee(regFee);
			
			String treatFee = ele.elementText("realTreatFee");
			resultItem.setTreatFee(treatFee);
	
			resultItem.setYhFee("0");
			
			Double payFee = Double.parseDouble(treatFee)+ Double.parseDouble(regFee);
			resultItem.setPayFee(payFee+"");
			
			payInfoList.add(resultItem);
		}
		resultV3.setList(payInfoList);
		resultV3.setResultCode("0");
		resultV3.setResultDesc("�ɹ�");
		return resultV3;
	}
	/**
	 * ͨ��His��ԤԼ�Ż�ȡ������,his�ӿڲ�����orderId
	 * @author YJB
	 * @date 2017��10��23�� ����11:42:10
	 * @param elementText
	 * @return
	 */
	private String getOrderIdByBookingNo(String bookingNo) {
		
		ChannelAppointmentLockInfo  info=BusinessDBHelper.getLockInfoByBookingNo(bookingNo);
		if(info!=null){
			logger.info("his��ˮ��Ϊ "+bookingNo+" �ĹҺŶ�������MongoDB��orderIdΪ "+info.getOrderId());
			return info.getOrderId();
		}else{
			logger.warn("his��ˮ��Ϊ "+bookingNo+" �ĹҺŶ�������MongoDBû�д洢��Ϣ���˺�ӦΪ������������Դ��");
			return "";
		}
		
	}
	/**
	 * ��ȡ������ˮ��
	 * @author YJB
	 * @date 2017��10��11�� ����8:14:17
	 * @param ele
	 * @param isOther 
	 * @return
	 * @throws Exception 
	 */
	private String getMzlsh(Element ele, boolean isOther) throws Exception {
		
		String  bookingNo = ele.elementText("hisOrderNum");
		
		RegisterInfo info = BusinessDBHelper.getMZLSHByBookingNO(bookingNo);
		
		if(info==null){
			//���û��������ˮ�źţ��������Žӿڣ���������
			String mzlsh = getByOrderCurReg(ele);
			 info = new RegisterInfo();
			 info.setBookingNo(bookingNo);
			 info.setMzlsh(mzlsh);
			 info.setOrderType(isOther?"99":"10"); //���������ƽ̨��orderType��99
			 BusinessDBHelper.saveRegisterInfo(info);
			return mzlsh;
		}else{
			return info.getMzlsh();
		}
		
		
	}
	/**
	 * �������Žӿڻ�ȡ������ˮ��
	 * @author YJB
	 * @date 2017��10��11�� ����8:38:17
	 * @param ele
	 * @return
	 * @throws Exception 
	 */
	private String getByOrderCurReg(Element ele) throws Exception {
		
		String lockReqXml = getLockReqXml(ele);
		//Ҫô����Ҫ���ӿڲ�ѯ����������Դ������
		OrderCurReg orderCurReg = new OrderCurReg();
		logger.info("����Һ�֧���ӿڣ������ǽ�����������Ƚ������ţ����ŵ������ "+lockReqXml);
		String resultStr = orderCurReg.execute(lockReqXml);
		
		
		return null;
	}
	private String getLockReqXml(Element ele) {
		
		
		return null;
	}
	private String getSvObjectByBookingNo(String bookingNo) {
		String orderId = "";
		ChannelAppointmentLockInfo  info=BusinessDBHelper.getLockInfoByBookingNo(bookingNo);
		if(info!=null){
			orderId = info.getOrderId();
			ChannelAppointmentInfo  appointmentInfo = BusinessDBHelper.getAppointmentInfo(orderId);
			if(appointmentInfo!=null){
				String svObjectId =appointmentInfo.getSvObjectId();
				logger.error("����HIS��ˮ��"+bookingNo+"���ҵ����ߵ�svObjectId�� "+svObjectId);
				return svObjectId;
			}else{
				logger.warn("���ݶ�����"+orderId+" ���Ҳ�����ƽ̨��Ӧ�Ķ�����Ϣ���õ�Ϊ���ǽ��������");
				return null;
			}
		}else{
			logger.warn("����HIS��ˮ��"+bookingNo+" ���Ҳ�����ƽ̨��Ӧ�Ķ����ţ��õ�Ϊ���ǽ��������");
			return null;
		}
	}
	
public List<GetRegisterInfoResultItemV3> getOtherRegRecordsTest(String reqXml) throws Exception {
		
		String inputString= getOrderDataInputParamString(reqXml);
		String convertInputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface, inputString);
		logger.error("ԤԼ�Һ���Ϣ��ѯ�ӿڡ�support.getRegisterInfo��-->��"+hisInterface+"����Σ�user="+ParamConstants.USER+";password="+ParamConstants.PASSWORD+";parameter="+inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();
		String resultXml= xmlRequest.request(convertInputString);
		//String resultXml = "<response><resultCode>0</resultCode><resultMessage>���ýӿ�������</resultMessage><result><item><patCardType>1</patCardType><patCardNo></patCardNo><patName>����ƽ</patName><deptCode>8124</deptCode><deptName>���ڿ�����</deptName><doctorCode>8124</doctorCode><doctorName>���ڿ�����</doctorName><scheduleDate>2017-09-30</scheduleDate><timeFlag>2</timeFlag><beginTime>21:30</beginTime><endTime>22:00</endTime><workId>6350102</workId><regFee>0</regFee><treatFee>10000</treatFee><SStreatFee>8600</SStreatFee><payFlag>0</payFlag><receiptNum></receiptNum><serialNum></serialNum><visitLocation>�����������</visitLocation><payMode></payMode><regType></regType><orderTime>2017-09-30 08:56:17</orderTime><hisOrderNum>6405188644</hisOrderNum></item></result></response>";
		resultXml=CommonUtils.convertHisOutputParam(resultXml);
		logger.error("ԤԼ�Һ���Ϣ��ѯ�ӿڡ�support.getRegisterInfo��-->��"+hisInterface+"�����Σ�"+resultXml);
		String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");

		if("0".equals(resCode)){
			GetRegisterInfoResultV3 resultV3=convertGetOrderDataToV3Object(reqXml,resultXml);
			return resultV3.getList();
		}else if("1".equals(resCode)){
			return  null;
		}else{
			String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
			logger.error("�ӿڷ���ʧ�ܣ�resCode="+resCode+";errorMsg="+errorMsg);
			return null;
		}
		
	}


public static void main(String[] args) throws Exception {
	/*String reqXml =  "<req><healthCardNo>4401000000000000</healthCardNo><patientId>424355466</patientId><orderId>111111</orderId><orderDate></orderDate></req>";
	getRegisterInfo  info = new getRegisterInfo();
	logger.info(info.execute(reqXml));*/
	
	/*SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	long now = sdf.parse(sdf.format(new Date())).getTime();
	long startTime = sdf.parse("22:20").getTime();
	String today = CommonUtils.getToday();
	
	if((now-startTime)>1000*60*30){ 
		System.out.println("���뵱��19:20 �뵱ǰʱ����೬����Сʱ");
	}else{
		System.out.println("���뵱��19:20 �뵱ǰʱ�����  С�� ��Сʱ");
	}*/
	/*List<GetRegisterInfoResultItemV3> list = null;
	getRegisterInfo  info = new getRegisterInfo();
	info.getAppointmetOrder1("", list);
	if(list.size()>0){
		logger.info("����0");
	}*/
	//logger.info(new getRegisterInfo().execute(""));
	String req="<req><healthCardNo>AK2331553</healthCardNo><patientId>0001614642</patientId><patientName>���Ľ�</patientName><idCardNo>62050319900215201X</idCardNo></req>";
	String reString = "<response><resultCode>0</resultCode><resultMessage>���ýӿ�������</resultMessage><result><item><patCardType>1</patCardType><patCardNo></patCardNo><patName>���Ľ�</patName><deptCode>9940</deptCode><deptName>��ҽ������</deptName><doctorCode>9940</doctorCode><doctorName>��ҽ������</doctorName><scheduleDate>2018-02-08</scheduleDate><timeFlag>2</timeFlag><beginTime>16:00</beginTime><endTime>16:30</endTime><workId>6510602</workId><regFee>0</regFee><treatFee>10000</treatFee><SStreatFee>8600</SStreatFee><payFlag>2</payFlag><receiptNum>R00000083544</receiptNum><serialNum></serialNum><visitLocation>һ¥</visitLocation><payMode></payMode><regType></regType><orderTime>2018-02-08 15:32:46</orderTime><hisOrderNum>4965329652</hisOrderNum><mzFeeId>6584622</mzFeeId></item><item><patCardType>1</patCardType><patCardNo></patCardNo><patName>���Ľ�</patName><deptCode>8124</deptCode><deptName>���ڿ�����</deptName><doctorCode>8124</doctorCode><doctorName>���ڿ�����</doctorName><scheduleDate>2018-02-08</scheduleDate><timeFlag>2</timeFlag><beginTime>16:00</beginTime><endTime>16:30</endTime><workId>6517192</workId><regFee>0</regFee><treatFee>10000</treatFee><SStreatFee>8600</SStreatFee><payFlag>2</payFlag><receiptNum>R00000083546</receiptNum><serialNum></serialNum><visitLocation>��¥</visitLocation><payMode></payMode><regType></regType><orderTime>2018-02-08 15:34:51</orderTime><hisOrderNum>5047836765</hisOrderNum><mzFeeId>6584648</mzFeeId></item></result></response>";
	logger.info(new getRegisterInfo().convertGetRegRecordsToV3Object(req, reString));
}
}
