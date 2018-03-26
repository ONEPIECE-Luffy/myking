package com.kingdeehit.mobile.his.xianggang.service.support.other;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingdeehit.mobile.his.consts.ProductOrderStatus;
import com.kingdeehit.mobile.his.entities.V3.result.support.GetRegisterInfoResultItemV3;
import com.kingdeehit.mobile.his.entities.V3.result.support.GetRegisterInfoResultV3;
import com.kingdeehit.mobile.his.entities.table.ChannelAppointmentInfo;
import com.kingdeehit.mobile.his.entities.table.ChannelProductOrder;
import com.kingdeehit.mobile.his.utils.BusinessDBHelper;
import com.kingdeehit.mobile.his.xianggang.constant.ParamConstants;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.DateUtils;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;


/**
 * 预约挂号信息查询
 * @author tangfulin
 *
 */
public class getRegisterInfo1 extends AbstractService {

	private static String hisInterface="getOrderData";
	private static String hisInterface2="getRegRecords";
	boolean otherPlatform = false;
	String orderId = null;
	
	@Override
	public String execute(String reqXml) throws Exception {
		//判断是不是查询列表
		//判断是不是查询某个订单
		//入参如果只有healthCardNo和patientId，则返回该病人有效的挂号记录，如果传入bookingNo或者orderId或者clinicSeq，则只返回对应的挂号记录。
		 orderId=UtilXml.getValueByAllXml(reqXml, "orderId");
		//String bookingNo=UtilXml.getValueByAllXml(reqXml, "bookingNo");
		//String clinicSeq=UtilXml.getValueByAllXml(reqXml, "clinicSeq");
		String resultXml="";
	
		if(StringUtils.isNotEmpty(orderId)){//如果不为空，查询某个订单内容
			ChannelProductOrder productOrder=BusinessDBHelper.getPaymentInfo(orderId);
			if (productOrder!=null) {//如果查询不到订单号，那么这个单就是通过其他渠道挂号的
				otherPlatform = false;
			}else{
				otherPlatform = true; 
			}
			String inputString=getPayInputParamString(reqXml);
			String convertInputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface2, inputString);
			logger.error("预约挂号信息查询接口【support.getRegisterInfo】-->【"+hisInterface2+"】入参：user="+ParamConstants.USER+";password="+ParamConstants.PASSWORD+";parameter="+inputString);
			HttpRequestService xmlRequest = HttpRequestService.getInstance();
			resultXml= xmlRequest.request(convertInputString);
			//resultXml = "<response><resultCode>0</resultCode><resultMessage>调用接口正常。</resultMessage><result><item><patCardType>1</patCardType><patCardNo></patCardNo><orderNo>8034143781</orderNo><patName>雷军</patName><deptCode>8125</deptCode><deptName>消化内科门诊</deptName><doctorCode>8125</doctorCode><doctorName>消化内科门诊</doctorName><scheduleDate>2017-07-06</scheduleDate><timeFlag>2</timeFlag><beginTime>16:00</beginTime><endTime>16:30</endTime><workId>6274342</workId><regFee>0</regFee><treatFee>10000</treatFee><SStreatFee>8600</SStreatFee><payFlag>0</payFlag><receiptNum></receiptNum><serialNum></serialNum><visitLocation>二楼</visitLocation><payMode></payMode><regType></regType><orderTime>2017-07-05 18:24:29</orderTime></item></result></response>";			
			resultXml=CommonUtils.convertHisOutputParam(resultXml);
			logger.error("预约挂号信息查询接口【support.getRegisterInfo】-->【"+hisInterface2+"】出参："+resultXml);
			String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");

			if("0".equals(resCode)){
				GetRegisterInfoResultV3 resultV3=convertPayHisStringToV3Object(reqXml,resultXml);
				XStream xstream = UtilXml.getXStream(GetRegisterInfoResultV3.class);
				return xstream.toXML(resultV3);
			}else{
				String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
				logger.error("接口返回失败！resCode="+resCode+";errorMsg="+errorMsg);
				return CommonUtils.getErrorMsg(errorMsg);
			}
			
		}else{//如果为空查询订单列表
			String inputString=getPayInputParamString(reqXml);
			String convertInputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface2, inputString);
			logger.error("预约挂号信息查询接口【support.getRegisterInfo】-->【"+hisInterface2+"】入参：user="+ParamConstants.USER+";password="+ParamConstants.PASSWORD+";parameter="+inputString);
			HttpRequestService xmlRequest = HttpRequestService.getInstance();
			resultXml= xmlRequest.request(convertInputString);
			//resultXml = "<response><resultCode>0</resultCode><resultMessage>调用接口正常。</resultMessage><result><item><hisOrdNum>0112567812</hisOrdNum><hisRefOrdNum></hisRefOrdNum><deptCode>1253</deptCode><deptName>男性与前列腺病</deptName><doctorCode></doctorCode><doctorName></doctorName><bookDate>2017-08-23</bookDate><timeFlag>2</timeFlag><beginTime>14:30</beginTime><endTime>15:00</endTime><regType>1</regType><orderMode>2</orderMode><payMode></payMode><status>1</status><realRegFee>0</realRegFee><realTreatFee>8600</realTreatFee><desc></desc><receiptNum></receiptNum><serialNum></serialNum><visitLocation>三楼</visitLocation><takeTime>2017-08-23 14:00:00</takeTime><barCode></barCode><visitDesc>请在预约时间段开始时间提前半个小时来医院三楼分诊台报到候诊。</visitDesc></item></result></response>";
			resultXml=CommonUtils.convertHisOutputParam(resultXml);
			logger.error("预约挂号信息查询接口【support.getRegisterInfo】-->【"+hisInterface2+"】出参："+resultXml);
			String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");

			if("0".equals(resCode)){

				GetRegisterInfoResultV3 resultV3=convertPayHisStringToV3Object(reqXml,resultXml);
				XStream xstream = UtilXml.getXStream(GetRegisterInfoResultV3.class);
				return xstream.toXML(resultV3);
			}else{
				String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
				logger.error("接口返回失败！resCode="+resCode+";errorMsg="+errorMsg);
				return CommonUtils.getErrorMsg(errorMsg);
			}
		}
	}

	/**
	 * V3入参转his入参
	 * @param req
	 * @return
	 */
	private String getInputParamString(String req){
		String orderId=UtilXml.getValueByAllXml(req, "orderId");
		String clinicSeq=UtilXml.getValueByAllXml(req, "clinicSeq");
		ChannelAppointmentInfo appointInfo=null;
		if(StringUtils.isNotBlank(orderId)){
			appointInfo=BusinessDBHelper.getAppointmentInfo(orderId);
		}
		String idCardNo="";
		String patName="";
		if(appointInfo!=null){
			idCardNo=appointInfo.getIdCardNo();
			patName=appointInfo.getPatientName();
		}
		StringBuilder str=new StringBuilder(100);
		str.append("<request><params>");
		str.append("<branchCode></branchCode>");
		str.append("<idCardNo>"+idCardNo+"</idCardNo>");
		str.append("<patName>"+patName+"</patName>");
		str.append("<orderNo>"+clinicSeq+"</orderNo>");
		str.append("</params></request>");
		return str.toString();
	}

	/**
	 * V3入参转his入参
	 * @param req
	 * @return
	 */
	private String getPayInputParamString(String req){
		String orderId=UtilXml.getValueByAllXml(req, "orderId");
		String healthCardNo=UtilXml.getValueByAllXml(req, "healthCardNo");
		String orderDate=UtilXml.getValueByAllXml(req, "orderDate");

		StringBuilder str=new StringBuilder(100);
		str.append("<request><params>");
		str.append("<branchCode></branchCode>");
		str.append("<patCardType>1</patCardType>");
		str.append("<patCardNo>"+healthCardNo+"</patCardNo>");
		str.append("<orderMode>1</orderMode>");
		str.append("<beginDate>"+orderDate+"</beginDate>");
		str.append("<endDate>"+orderDate+"</endDate>");
		str.append("<psOrdNum>"+orderId+"</psOrdNum>");
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
	private GetRegisterInfoResultV3 convertHisStringToV3Object(String req,String resultXml) throws DocumentException{
		String healthCardNo=UtilXml.getValueByAllXml(req, "healthCardNo");
		String orderId=UtilXml.getValueByAllXml(req, "orderId");
		GetRegisterInfoResultV3 resultV3=new GetRegisterInfoResultV3();
		Document document=DocumentHelper.parseText(resultXml);
		Element element=document.getRootElement();
		Element res=element.element("result");
		List<Element> list=res.elements("item");
		List<GetRegisterInfoResultItemV3> payInfoList=new ArrayList<GetRegisterInfoResultItemV3>();
		for(Element ele:list){
			GetRegisterInfoResultItemV3 resultItem=new GetRegisterInfoResultItemV3();
			String status=ele.elementText("payFlag");
			//0:未付费  1:已付费未取号 2:已取号
			if("2".equals(status)){
				resultItem.setStatus("0");
			}else if("5".equals(status)){
				resultItem.setStatus("1");
			}else if("8".equals(status)){
				resultItem.setStatus("2");
			}else if("6".equals(status)){
				resultItem.setStatus("3");
			}else if("4".equals(status)){
				resultItem.setStatus("4");
			}else if("3".equals(status)){
				resultItem.setStatus("5");
			}else if("1".equals(status)){
				resultItem.setStatus("6");
			}else if("7".equals(status)){
				resultItem.setStatus("7");
			}

			resultItem.setOrderId(orderId);
			resultItem.setBookingNo(healthCardNo);
			resultItem.setClinicSeq("");
			resultItem.setDeptId(ele.elementText("deptCode"));
			resultItem.setDeptName(ele.elementText("deptName"));
			resultItem.setDoctorId(ele.elementText("doctorCode"));
			resultItem.setDoctorName(ele.elementText("doctorName"));
			resultItem.setRegDate(ele.elementText("scheduleDate"));
			resultItem.setShiftCode(CommonUtils.getNoon(DateUtils.getHMTime(ele.elementText("beginTime"))));
			resultItem.setShiftName(CommonUtils.getNoon(DateUtils.getHMTime(ele.elementText("beginTime"))));
			resultItem.setStartTime(ele.elementText("beginTime"));
			resultItem.setEndTime(ele.elementText("endTime"));
			resultItem.setRegisterType("");
			resultItem.setQueueNo("");
			resultItem.setOrderTypeName("");
			resultItem.setIsCancelabe("");
			resultItem.setPayStatus("");
			resultItem.setIsPayment("1");
			payInfoList.add(resultItem);
		}
		resultV3.setList(payInfoList);
		resultV3.setResultCode("0");
		resultV3.setResultDesc("成功");
		return resultV3;
	}

	/**
	 * 已支付his出参转V3出参
	 * @param orderString
	 * @return
	 * @throws DocumentException
	 */
	/**
	 * @author YJB
	 * @date 2017年9月8日 上午9:32:02
	 * @param req
	 * @param resultXml
	 * @return
	 * @throws DocumentException
	 */
	@SuppressWarnings("unchecked")
	private GetRegisterInfoResultV3 convertPayHisStringToV3Object(String req,String resultXml) throws DocumentException{

		String orderId=UtilXml.getValueByAllXml(req, "orderId");
		ChannelAppointmentInfo appointmentOrder=BusinessDBHelper.getAppointmentInfo(orderId);
		String svObjectId="";
		if(appointmentOrder!=null){
			svObjectId=appointmentOrder.getSvObjectId();
		}else{
			logger.error("根据订单号"+orderId+"未查找到预约挂号信息！");
		}
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
			//0:未付费  1:已付费未取号 2:已取号
			if("2".equals(status)){
				resultItem.setStatus("0");
			}else if("5".equals(status)){
				resultItem.setStatus("1");
			}else if("8".equals(status)){
				resultItem.setStatus("2");
			}else if("6".equals(status)){
				resultItem.setStatus("3");
			}else if("4".equals(status)){
				resultItem.setStatus("4");
			}else if("3".equals(status)){
				resultItem.setStatus("5");
			}else if("1".equals(status)){
				resultItem.setStatus("6");
			}else if("7".equals(status)){
				resultItem.setStatus("7");
			}
			resultItem.setOrderId(orderId);
			resultItem.setBookingNo(ele.elementText("hisOrdNum"));
			resultItem.setClinicSeq(ele.elementText("receiptNum"));
			resultItem.setDeptId(ele.elementText("deptCode"));
			resultItem.setDeptName(ele.elementText("deptName"));
			resultItem.setDoctorId(ele.elementText("doctorCode"));
			resultItem.setDoctorName(ele.elementText("doctorName"));
			resultItem.setRegDate(ele.elementText("bookDate"));
			String timeFlag=ele.elementText("timeFlag");
			if("1".equals(timeFlag)){
				resultItem.setShiftCode("上午");
				resultItem.setShiftName("上午");
			}else if("2".equals(timeFlag)){
				resultItem.setShiftCode("下午");
				resultItem.setShiftName("下午");
			}else if("3".equals(timeFlag)){
				resultItem.setShiftCode("晚上");
				resultItem.setShiftName("晚上");
			}
			resultItem.setStartTime(ele.elementText("beginTime"));
			resultItem.setEndTime(ele.elementText("endTime"));
			String regType=ele.elementText("regType");
			if("1".equals(regType)){
				resultItem.setRegisterType("0");
			}else if("2".equals(regType)){
				resultItem.setRegisterType("1");
			}
			resultItem.setQueueNo(ele.elementText("serialNum"));
			resultItem.setInfoTime(ele.elementText("takeTime"));

			resultItem.setVisitTime(ele.elementText("takeTime"));//就诊时间（实际接诊时间）YYYY-MM-DD HI24:MI:SS

			
			if("3".equals(status)||"6".equals(status)||"7".equals(status)||"8".equals(status)||"5".equals(status)){
				resultItem.setIsCancelabe("0");
			}else if("1".equals(status)||"2".equals(status)||"4".equals(status)){
				resultItem.setIsCancelabe("1");
			}
			//String remark=ele.elementText("visitDesc");
			resultItem.setRemark("就诊前半个小时内，不允许线上支付，请移步窗口。");
			if("2".equals(status)||"3".equals(status)||"4".equals(status)||"6".equals(status)||"7".equals(status)||"8".equals(status)||"5".equals(status)){
				resultItem.setIsPayment("0");
			}else if("1".equals(status)){
				if("02".equals(svObjectId)){
					String regDate=ele.elementText("takeTime");
					if(StringUtils.isNotBlank(regDate)){
						String hisYear=regDate.substring(0, 10);	//获取就诊年月日
						Date hisRegDate=DateUtils.parseChineseDate(regDate);
						Date currentDate=new Date();
						String currentDay=DateUtils.getYMDTime(currentDate);	//当前年月日
						logger.error("订单类型："+svObjectId+"；当前时间："+currentDay+";就诊时间："+regDate);
						if(hisYear.equals(currentDay)){
							if(currentDate.getTime()<=hisRegDate.getTime()){
								resultItem.setIsPayment("1");		//医保订单只能当天支付
							}else{
								resultItem.setRemark("医保预约，请就诊当天("+hisYear+")做挂号支付，并于就诊时间前完成支付！");
								resultItem.setIsPayment("0");
							}
						}else{
							resultItem.setRemark("医保预约，请就诊当天("+hisYear+")做挂号支付，并于就诊时间前完成支付！");
							resultItem.setIsPayment("0");
						}
					}
				}else if("01".equals(svObjectId)){
					resultItem.setIsPayment("1");
				}
			}
			if("2".equals(status)||"5".equals(status)||"6".equals(status)||"7".equals(status)){
				resultItem.setPayStatus("1");
			}else if("1".equals(status)){
				resultItem.setPayStatus("0");
			}else if("3".equals(status)||"4".equals(status)||"8".equals(status)){
				resultItem.setPayStatus("2");
			}
			if(StringUtils.isNotEmpty(orderId)){ //如果是挂号订单详情需要返回下面字段
				//加入订单类类型字段
				if(otherPlatform){
					resultItem.setOrderType("99");
					resultItem.setOrderTypeName("非金蝶渠道");
				}else{
					resultItem.setOrderType("10");
					resultItem.setOrderTypeName("微信");
				}
			}
			payInfoList.add(resultItem);
		}
		resultV3.setList(payInfoList);
		resultV3.setResultCode("0");
		resultV3.setResultDesc("成功");
		return resultV3;
	}
	
	public static void main(String[] args) throws Exception {
		String reqXml =  "<req><healthCardNo>4401000000000000</healthCardNo><patientId>424355466</patientId><orderId>111111</orderId><orderDate></orderDate></req>";
		getRegisterInfo1  info = new getRegisterInfo1();
		logger.info(info.execute(reqXml));
	}

}
