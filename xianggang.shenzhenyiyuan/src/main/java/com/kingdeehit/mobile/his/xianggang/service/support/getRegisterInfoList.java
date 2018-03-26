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

import com.kingdeehit.mobile.his.consts.ProductOrderStatus;
import com.kingdeehit.mobile.his.entities.V3.result.support.GetRegisterInfoResultItemV3;
import com.kingdeehit.mobile.his.entities.V3.result.support.GetRegisterInfoResultV3;
import com.kingdeehit.mobile.his.entities.table.ChannelAppointmentInfo;
import com.kingdeehit.mobile.his.entities.table.ChannelBindingCard;
import com.kingdeehit.mobile.his.entities.table.ChannelProductOrder;
import com.kingdeehit.mobile.his.xianggang.constant.Const;
import com.kingdeehit.mobile.his.xianggang.constant.ParamConstants;
import com.kingdeehit.mobile.his.xianggang.entity.table.ChannelAppointmentLockInfo;
import com.kingdeehit.mobile.his.xianggang.entity.table.ChannelLockInfo;
import com.kingdeehit.mobile.his.xianggang.entity.table.RegisterInfo;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.appointment.returnPay;
import com.kingdeehit.mobile.his.xianggang.service.register.OrderCurReg;
import com.kingdeehit.mobile.his.xianggang.service.util.BusinessDBHelper;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.DateUtils;
import com.kingdeehit.mobile.utils.StringUtil;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;


/**
 * 预约挂号信息查询，这个与getRegisterInfo  区别是 返回类型不同而已，这个类用于第三方取号支付前，获取锁号请求需要的参数
 * @author YJB
 *
 */
public class getRegisterInfoList extends AbstractService {

	private static String hisInterface="getOrderData";
	private static String hisInterface2="getRegRecords";
	
	public List<GetRegisterInfoResultItemV3> executeList(String reqXml) throws Exception {
	 
		String orderId=UtilXml.getValueByAllXml(reqXml, "orderId");
		String resultXml="";
		String inputString=getPayInputParamString(reqXml);
		String convertInputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface2, inputString);
		logger.error("第三方取号 ，锁号前需先调用我的挂号记录列表  预约挂号信息查询接口【support.getRegisterInfo】-->【"+hisInterface2+"】入参：user="+ParamConstants.USER+";password="+ParamConstants.PASSWORD+";parameter="+inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();
		resultXml= xmlRequest.request(convertInputString);
		//resultXml = "<response><resultCode>0</resultCode><resultMessage>调用接口正常。</resultMessage><result><item><hisOrdNum>2529913187</hisOrdNum><hisRefOrdNum></hisRefOrdNum><deptCode>9932</deptCode><deptName>慢性病门诊</deptName><doctorCode></doctorCode><doctorName></doctorName><bookDate>2017-10-09</bookDate><timeFlag>2</timeFlag><beginTime>16:30</beginTime><endTime>17:00</endTime><regType>1</regType><orderMode>2</orderMode><payMode>1</payMode><status>2</status><realRegFee>0</realRegFee><realTreatFee>8600</realTreatFee><desc></desc><receiptNum>R00000002563</receiptNum><serialNum></serialNum><visitLocation>门诊楼-慢性病门诊</visitLocation><takeTime>2017-10-09 16:00:00</takeTime><barCode>AJ9615893</barCode><visitDesc>请在预约时间段开始时间提前半个小时来医院门诊楼-慢性病门诊分诊台报到候诊。</visitDesc></item></result></response>";
		resultXml=CommonUtils.convertHisOutputParam(resultXml);
		logger.error("第三方取号 ，锁号前需先调用我的挂号记录列表   预约挂号信息查询接口【support.getRegisterInfo】-->【"+hisInterface2+"】出参："+resultXml);
		String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");

		if("0".equals(resCode)){
			GetRegisterInfoResultV3 resultV3= convertResult(reqXml,resultXml); 
			return resultV3.getList();
		}else if("1".equals(resCode)){

			List<GetRegisterInfoResultItemV3> otherRegList = getOtherRegRecords(reqXml);
			getAppointmetOrder(reqXml,otherRegList);
			 
			GetRegisterInfoResultV3 resultV3 = new GetRegisterInfoResultV3();
			if(otherRegList!=null&&otherRegList.size()>0){
				resultV3.setList(otherRegList);
			}
			return resultV3.getList();
		}else{
			String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
			logger.error("第三方取号 ，锁号前需先调用我的挂号记录列表  接口返回失败！resCode="+resCode+";errorMsg="+errorMsg);
			return null;
		}
	 
	}
	/**
	 * 解析接口出参，并调用另一个接口获取更多的挂号记录的参数，最后将两个接口的数据合并(过滤重复）后返回
	 * @author YJB
	 * @date 2017年9月8日 上午11:11:44
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
		 //判断是否有重复，有则过滤
		 if(otherRegList!=null&&otherRegList.size()>0){
			 
			 for(GetRegisterInfoResultItemV3 item: otherRegList){
				 
					String bookingNo = item.getBookingNo().trim();
					 for(int i=0;i<orders.length;i++){ 
						 if(orders[i].equals(bookingNo)){
							 isRepeat = true;
							 break;
						 } 
					 }
					 if(!isRepeat){	  //遍历后没有重复，就加入结果集
						 list.add(item);
					 } 
				    isRepeat = false; //复位
				 }
		 }
		 getAppointmetOrder(reqXml,list); //获取患者未来七天的预约记录，然后是否支付直接设置为 0
		 
		return resultV3;
	}
	/**
	 * 获取患者明天及以后平台的挂号记录，兼容His我的挂号记录接口不返回未来的预约挂号记录（如果以后His返回了，就不需要这个方法了）
	 * @author YJB
	 * @date 2017年9月29日 下午6:42:33
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
				if (tomorrowTime<=regDataTime) { //大于或等于明天的记录才列出
					logger.info("获取到患者预约订单号为 "+orderId+" 的预约记录");
					itemV3 =  new GetRegisterInfoResultItemV3();
					itemV3.setOrderId(orderId);
					itemV3.setIsPayment("0");
					if(list==null){
						list = new  ArrayList<GetRegisterInfoResultItemV3>();
					}
					list.add(itemV3);
				}else{
					logger.info("患者预约订单号为 "+orderId+" 的预约记录,预约日期是 "+regDate+" 不加入显示队列");
				}
			}
		}else{
			logger.info("没有查询到患者预约记录！");
		}
		
	
		
	}
	/**
	 * 获取第一个接口中的订单号，便于调用另一个接口的时候，判断His返回的订单是否重复
	 * @author YJB
	 * @date 2017年9月8日 上午10:57:07
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
	 * 获取另一个接口当日挂号查询中的挂号记录
	 * @author YJB
	 * @date 2017年9月8日 上午11:15:15
	 * @param reqXml
	 * @param resultXml
	 * @return
	 * @throws Exception
	 */
	public List<GetRegisterInfoResultItemV3> getOtherRegRecords(String reqXml) throws Exception {
		
		String inputString= getOrderDataInputParamString(reqXml);
		String convertInputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface, inputString);
		logger.error("预约挂号信息查询接口【support.getRegisterInfo】-->【"+hisInterface+"】入参：user="+ParamConstants.USER+";password="+ParamConstants.PASSWORD+";parameter="+inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();
		String resultXml= xmlRequest.request(convertInputString);
		//SString resultXml = "<response><resultCode>0</resultCode><resultMessage>调用接口正常。</resultMessage><result><item><patCardType>1</patCardType><patCardNo></patCardNo><patName>彭林平</patName><deptCode>9932</deptCode><deptName>慢性病门诊</deptName><doctorCode>9932</doctorCode><doctorName>慢性病门诊</doctorName><scheduleDate>2017-10-09</scheduleDate><timeFlag>2</timeFlag><beginTime>16:30</beginTime><endTime>17:00</endTime><workId>6362292</workId><regFee>0</regFee><treatFee>10000</treatFee><SStreatFee>8600</SStreatFee><payFlag>2</payFlag><receiptNum>R00000002563</receiptNum><serialNum></serialNum><visitLocation>门诊楼-慢性病门诊</visitLocation><payMode></payMode><regType></regType><orderTime>2017-10-09 08:30:17</orderTime><hisOrderNum>2529913187</hisOrderNum></item></result></response>";
		resultXml=CommonUtils.convertHisOutputParam(resultXml);
		logger.error("预约挂号信息查询接口【support.getRegisterInfo】-->【"+hisInterface+"】出参："+resultXml);
		String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");

		if("0".equals(resCode)){
			GetRegisterInfoResultV3 resultV3=convertGetOrderDataToV3Object(reqXml,resultXml);
			return resultV3.getList();
		}else if("1".equals(resCode)){
			return  null;
		}else{
			String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
			logger.error("接口返回失败！resCode="+resCode+";errorMsg="+errorMsg);
			return null;
		}
		
	}
	 
	/**
	 * 设置 getOrderData接口的请求入参
	 * @author YJB
	 * @date 2017年9月21日 下午6:16:04
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
	 * V3入参转his入参
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
 
	 
	public static void main(String[] args) throws Exception {
		/*String reqXml =  "<req><healthCardNo>4401000000000000</healthCardNo><patientId>424355466</patientId><orderId>111111</orderId><orderDate></orderDate></req>";
		getRegisterInfo  info = new getRegisterInfo();
		logger.info(info.execute(reqXml));*/
		
		/*SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		long now = sdf.parse(sdf.format(new Date())).getTime();
		long startTime = sdf.parse("22:20").getTime();
		String today = CommonUtils.getToday();
		
		if((now-startTime)>1000*60*30){ 
			System.out.println("输入当日19:20 与当前时间相距超过半小时");
		}else{
			System.out.println("输入当日19:20 与当前时间相距  小于 半小时");
		}*/
		getRegisterInfoList  info = new getRegisterInfoList();
		logger.info(info.execute(""));
	}
	/**
	 * 解析 请求getOrderData 返回的数据
	 * @author YJB
	 * @date 2017年9月19日 下午4:21:25
	 * @param req
	 * @param resultXml
	 * @return
	 * @throws Exception 
	 */
	private GetRegisterInfoResultV3 convertGetOrderDataToV3Object(String req,String resultXml) throws Exception{
	
	 
		String orderId= "";
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
			resultItem.setOrderId(orderId);
			
			resultItem.setBookingNo(ele.elementText("hisOrderNum")); //在下面会覆盖这个值
			
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
				resultItem.setShiftName("上午");
			}else if("2".equals(timeFlag)){
				resultItem.setShiftCode("2");
				resultItem.setShiftName("下午");
			}else if("3".equals(timeFlag)){
				resultItem.setShiftCode("3");
				resultItem.setShiftName("晚上");
			}
			resultItem.setStartTime(ele.elementText("beginTime"));
			resultItem.setEndTime(ele.elementText("endTime"));
			resultItem.setRegisterType("1");
			
			resultItem.setQueueNo(ele.elementText("serialNum"));
			resultItem.setWaitingCount("");
			resultItem.setWaitingTime("");
			//resultItem.setInfoTime(ele.elementText("takeTime"));
			//resultItem.setVisitTime(ele.elementText("takeTime"));//就诊时间（实际接诊时间）YYYY-MM-DD HI24:MI:SS
			boolean isOther = CommonUtils.isOtherPlatform(resultItem.getBookingNo());
			//String  mzlsh = getMzlsh(ele,isOther);
			//resultItem.setBookingNo(ele.elementText("hisOrderNum")); //将原来的bookingNo存起来，然后替换成锁号的id，兼容医保挂号后的医保就医结算
		
			if(isOther){ //如果是挂号订单详情需要返回下面字段
			  //加入订单类类型字段
				resultItem.setOrderType("99");
				resultItem.setOrderTypeName("非微信渠道");
			}else{
				resultItem.setOrderType("10");
				resultItem.setOrderTypeName("微信渠道");
			}
			resultItem.setOrderTime("");
			resultItem.setRemark("就诊前半个小时内，不允许线上支付，请移步窗口。");
		 
			resultItem.setIsCancelabe("0"); //目前暂时全部号源全部不给退号
			
			
			String bookingNo = ele.elementText("hisOrderNum");
			svObjectId = getSvObjectByBookingNo(bookingNo);
			String today = CommonUtils.getToday();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			long now = sdf.parse(sdf.format(new Date())).getTime();
			long startTime = sdf.parse(ele.elementText("beginTime")).getTime();
		
			if("0".equals(status)){//   	0 未付费        判断用户是否是医保预约取号，如果是，要进行限制必须就诊当日才可以进行支付
				if(today.equals(regData)){ 
					if((now-startTime)>1000*60*30||(startTime-now)>1000*60*30){  //当日就诊当前，就诊前小于半小时不允许线上支付
						//----就诊当天，就诊时间大于就诊前半小时，普通患者和就诊患者都可以支付--------//
						resultItem.setIsPayment("1"); // isPayment  是否允许支付【0-不允许；1-允许
						resultItem.setPayStatus("0"); //0-未支付 1-已支付  2-已退费
						//resultItem.setIsSelectSvObject("1");//允许选择优惠对象
						resultItem.setIsSelectSvObject("0"); //暂时不允许选择医保对象
					}else{
						resultItem.setIsPayment("0"); //医保用户  非就诊当日，不允许支付
						resultItem.setPayStatus("0"); //0-未支付 1-已支付  2-已退费
						//resultItem.setIsSelectSvObject("1");//允许选择优惠对象
						resultItem.setIsSelectSvObject("0"); //暂时不允许选择医保对象
					}
				}else{
					if("02".equals(svObjectId)){ 
						String regDate = ele.elementText("bookDate").trim();
						logger.info("医保患者，预约日期是 "+regDate);
						resultItem.setIsPayment("0"); //医保用户  非就诊当日，不允许支付
						resultItem.setPayStatus("0"); //0-未支付 1-已支付  2-已退费
						//resultItem.setIsSelectSvObject("1");//允许选择优惠对象
						resultItem.setIsSelectSvObject("0"); //暂时不允许选择医保对象
					}else{
						logger.info("此患者非医保患者");
						resultItem.setIsPayment("1"); // isPayment  是否允许支付【0-不允许；1-允许
						resultItem.setPayStatus("0"); //0-未支付 1-已支付  2-已退费
						//resultItem.setIsSelectSvObject("1");//允许选择优惠对象
						resultItem.setIsSelectSvObject("0"); //暂时不允许选择医保对象
					}
				}
			}else{//1 已付费未取号  2已取号  
				resultItem.setIsSelectSvObject("0");
				resultItem.setIsPayment("0"); // isPayment  是否允许支付【0-不允许；1-允许
				resultItem.setPayStatus("1"); // 0-未支付 1-已支付  2-已退费
			}
			 
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
		resultV3.setResultDesc("成功");
		return resultV3;
	}
	/**
	 * 解析 请求getRegRecords(预约资料查询) 返回的数据
	 * @author YJB
	 * @date 2017年9月19日 下午4:21:25
	 * @param req
	 * @param resultXml
	 * @return
	 * @throws DocumentException
	 * @throws ParseException 
	 */
	private GetRegisterInfoResultV3 convertGetRegRecordsToV3Object(String req,String resultXml) throws DocumentException, ParseException{
	
		String orderId=UtilXml.getValueByAllXml(req, "orderId");
		logger.error("处理的订单号是  "+orderId);
	
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
		    //只显示那些 需要支付的  和已支付的号源给患者看即可，其他对患者来说意义不大
			if("1".equals(status)){ //1：未支付  2：已支付 3：已取消4：缴费超时5：已取号6：已就诊7：已过期8：已退费
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
			}else if("8".equals(status)){ //已退费 不显示
				break;
			} 
			
			
			resultItem.setHealthCardNo(healthCardNo);
			resultItem.setPatientName(UtilXml.getValueByAllXml(req, "patientName"));
			resultItem.setPatientId(UtilXml.getValueByAllXml(req, "patientId"));
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
				resultItem.setShiftName("上午");
			}else if("2".equals(timeFlag)){
				resultItem.setShiftCode("2");
				resultItem.setShiftName("下午");
			}else if("3".equals(timeFlag)){
				resultItem.setShiftCode("3");
				resultItem.setShiftName("晚上");
			}
			resultItem.setStartTime(ele.elementText("beginTime"));
			resultItem.setEndTime(ele.elementText("endTime"));
			resultItem.setRegisterType("1");
			
			resultItem.setQueueNo(ele.elementText("serialNum"));
			resultItem.setWaitingCount("");
			resultItem.setWaitingTime("");
			resultItem.setInfoTime(ele.elementText("takeTime"));
			resultItem.setVisitTime(ele.elementText("takeTime"));//就诊时间（实际接诊时间）YYYY-MM-DD HI24:MI:SS
			if(CommonUtils.isOtherPlatform(resultItem.getBookingNo())){ //如果是挂号订单详情需要返回下面字段
				//if(StringUtil.isEmpty(svObjectId)){ 
			  //加入订单类类型字段
				//resultItem.setOrderId("A88888888");
				resultItem.setOrderType("99");
				resultItem.setOrderTypeName("非微信渠道");
			}else{
				resultItem.setOrderType("10");
				resultItem.setOrderTypeName("微信渠道");
			}
			resultItem.setOrderTime("");
			resultItem.setRemark("就诊前半个小时内，不允许线上支付，请移步窗口。");
			
			
			if("2".equals(status)){//如果患者已经支付，那么是可以退费的
				resultItem.setIsCancelabe("1");
			}else{
				resultItem.setIsCancelabe("0"); //如果患者未就诊是允许退号的  
			}
			
			String bookingNo = ele.elementText("hisOrdNum");
			
			svObjectId = getSvObjectByBookingNo(bookingNo);
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			long now = sdf.parse(sdf.format(new Date())).getTime();
			long startTime = sdf.parse(ele.elementText("beginTime")).getTime();
			String today = CommonUtils.getToday();
			if("1".equals(status)){ //1：未支付  2：已支付 3：已取消4：缴费超时5：已取号6：已就诊7：已过期8：已退费
				if(today.equals(regData)){ 
					if((now-startTime)>1000*60*30||(startTime-now)>1000*60*30){  //当日就诊当前，就诊前小于半小时不允许线上支付
						//----就诊当天，就诊时间大于就诊前半小时，普通患者和就诊患者都可以支付--------//
						resultItem.setIsPayment("1"); // isPayment  是否允许支付【0-不允许；1-允许
						resultItem.setPayStatus("0"); //0-未支付 1-已支付  2-已退费
						//resultItem.setIsSelectSvObject("1");//允许选择优惠对象
						resultItem.setIsSelectSvObject("0"); //暂时不允许选择医保对象
					}else{
						resultItem.setIsPayment("0"); //医保用户  非就诊当日，不允许支付
						resultItem.setPayStatus("0"); //0-未支付 1-已支付  2-已退费
						//resultItem.setIsSelectSvObject("1");//允许选择优惠对象
						resultItem.setIsSelectSvObject("0"); //暂时不允许选择医保对象
					}
				}else{
					if("02".equals(svObjectId)){ 
						String regDate = ele.elementText("bookDate").trim();
						logger.info("医保患者，预约日期是 "+regDate);
						resultItem.setIsPayment("0"); //医保用户  非就诊当日，不允许支付
						resultItem.setPayStatus("0"); //0-未支付 1-已支付  2-已退费
						//resultItem.setIsSelectSvObject("1");//允许选择优惠对象
						resultItem.setIsSelectSvObject("0"); //暂时不允许选择医保对象
					}else{
						resultItem.setIsPayment("1"); // isPayment  是否允许支付【0-不允许；1-允许
						resultItem.setPayStatus("0"); //0-未支付 1-已支付  2-已退费
						//resultItem.setIsSelectSvObject("1");//允许选择优惠对象
						resultItem.setIsSelectSvObject("0"); //暂时不允许选择医保对象
					}
				}
			}else if("2".equals(status)){//2 已支付（预约成功，可以进行预约退费）
				resultItem.setIsPayment("0"); // isPayment  是否允许支付【0-不允许；1-允许
				resultItem.setPayStatus("1"); //0-未支付 1-已支付  2-已退费
				resultItem.setIsSelectSvObject("0");
			}else if("5".equals(status)){//5：已取号
				resultItem.setIsPayment("0"); // isPayment  是否允许支付【0-不允许；1-允许
				resultItem.setPayStatus("1"); //0-未支付 1-已支付  2-已退费
				resultItem.setIsSelectSvObject("0");
			}else if("6".equals(status)){//6：已就诊
				resultItem.setIsPayment("0"); // isPayment  是否允许支付【0-不允许；1-允许
				resultItem.setPayStatus("1"); //0-未支付 1-已支付  2-已退费
				resultItem.setIsSelectSvObject("0");
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
		resultV3.setResultDesc("成功");
		return resultV3;
	}
	/**
	 * 获取门诊流水号
	 * @author YJB
	 * @date 2017年10月11日 下午8:14:17
	 * @param ele
	 * @param isOther 
	 * @return
	 * @throws Exception 
	 */
	private String getMzlsh(Element ele, boolean isOther) throws Exception {
		
		String  bookingNo = ele.elementText("hisOrderNum");
		
		RegisterInfo info = BusinessDBHelper.getMZLSHByBookingNO(bookingNo);
		
		if(info==null){
			//如果没有门诊流水号号，调用锁号接口，进行锁号
			String mzlsh = getByOrderCurReg(ele);
			 info = new RegisterInfo();
			 info.setBookingNo(bookingNo);
			 info.setMzlsh(mzlsh);
			 info.setOrderType(isOther?"99":"10"); //如果是外联平台，orderType是99
			 BusinessDBHelper.saveRegisterInfo(info);
			return mzlsh;
		}else{
			return info.getMzlsh();
		}
		
		
	}
	/**
	 * 调用锁号接口获取门诊流水号
	 * @author YJB
	 * @date 2017年10月11日 下午8:38:17
	 * @param ele
	 * @return
	 * @throws Exception 
	 */
	private String getByOrderCurReg(Element ele) throws Exception {
		
		String lockReqXml = getLockReqXml(ele);
		//要么这里要调接口查询，第三方号源的数据
		OrderCurReg orderCurReg = new OrderCurReg();
		logger.info("当天挂号支付接口，处理【非金蝶】渠道，先进行锁号，锁号的入参是 "+lockReqXml);
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
				logger.error("根据HIS流水号"+bookingNo+"查找到患者的svObjectId是 "+svObjectId);
				return svObjectId;
			}else{
				logger.warn("根据订单号"+orderId+" 查找不到在平台相应的订单信息，该单为【非金蝶渠道】");
				return null;
			}
		}else{
			logger.warn("根据HIS流水号"+bookingNo+" 查找不到在平台相应的订单号，该单为【非金蝶渠道】");
			return null;
		}
	}
	
public List<GetRegisterInfoResultItemV3> getOtherRegRecordsTest(String reqXml) throws Exception {
		
		String inputString= getOrderDataInputParamString(reqXml);
		String convertInputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface, inputString);
		logger.error("预约挂号信息查询接口【support.getRegisterInfo】-->【"+hisInterface+"】入参：user="+ParamConstants.USER+";password="+ParamConstants.PASSWORD+";parameter="+inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();
		String resultXml= xmlRequest.request(convertInputString);
		//String resultXml = "<response><resultCode>0</resultCode><resultMessage>调用接口正常。</resultMessage><result><item><patCardType>1</patCardType><patCardNo></patCardNo><patName>彭林平</patName><deptCode>8124</deptCode><deptName>心内科门诊</deptName><doctorCode>8124</doctorCode><doctorName>心内科门诊</doctorName><scheduleDate>2017-09-30</scheduleDate><timeFlag>2</timeFlag><beginTime>21:30</beginTime><endTime>22:00</endTime><workId>6350102</workId><regFee>0</regFee><treatFee>10000</treatFee><SStreatFee>8600</SStreatFee><payFlag>0</payFlag><receiptNum></receiptNum><serialNum></serialNum><visitLocation>门诊二都门诊</visitLocation><payMode></payMode><regType></regType><orderTime>2017-09-30 08:56:17</orderTime><hisOrderNum>6405188644</hisOrderNum></item></result></response>";
		resultXml=CommonUtils.convertHisOutputParam(resultXml);
		logger.error("预约挂号信息查询接口【support.getRegisterInfo】-->【"+hisInterface+"】出参："+resultXml);
		String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");

		if("0".equals(resCode)){
			GetRegisterInfoResultV3 resultV3=convertGetOrderDataToV3Object(reqXml,resultXml);
			return resultV3.getList();
		}else if("1".equals(resCode)){
			return  null;
		}else{
			String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
			logger.error("接口返回失败！resCode="+resCode+";errorMsg="+errorMsg);
			return null;
		}
		
	}
@Override
public String execute(String reqXml) throws Exception {
	return null;
}
}
