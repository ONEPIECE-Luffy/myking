package com.kingdeehit.mobile.his.xianggang.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.kingdeehit.mobile.his.utils.ChannelManager;
import com.kingdeehit.mobile.his.utils.MongoDBHelper;
import com.kingdeehit.mobile.his.xianggang.constant.Const;
import com.kingdeehit.mobile.his.xianggang.entity.table.QueueInfo;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.QueueDBHelper;
import com.kingdeehit.mobile.utils.StringUtil;
import com.kingdeehit.mobile.utils.UtilXml;
import com.mongodb.client.model.Filters;

public class patientWaiting extends AbstractService {

	@Override
	public String execute(String reqXml) throws Exception {
		String eventNo = UtilXml.getValueByAllXml(reqXml, "eventNo");// 事件流水号
		String deptId = UtilXml.getValueByAllXml(reqXml, "deptId");
		String deptName = UtilXml.getValueByAllXml(reqXml, "deptName");
		String patientId = UtilXml.getValueByAllXml(reqXml, "patientId");
		String patientName = UtilXml.getValueByAllXml(reqXml, "patientName");
		String healthCardNo = UtilXml.getValueByAllXml(reqXml, "healthCardNo");
		String visitIndicator = UtilXml.getValueByAllXml(reqXml, "visitIndicator");// 分诊标识
		String queueName = UtilXml.getValueByAllXml(reqXml, "queueName");// 队列名称
		String queueNo = UtilXml.getValueByAllXml(reqXml, "queueNo");// 排队号
		String time = UtilXml.getValueByAllXml(reqXml, "time");// 操作时间

		String message = "";

		switch (visitIndicator) {
		case "A"://A有两种情况，1是分诊，2是从一个队列拉到另一个队列
			
			// 通过科室id和健康卡号检索数据库，如果找到记录，证明A是从一个队列拉到另一个队列
			Bson filterA = Filters.and(Filters.eq("deptId", deptId), Filters.eq("healthCardNo", healthCardNo));
			List<Document> list = new MongoDBHelper(Const.QUEUEINFO).query(filterA);

			if (list != null && list.size() > 0) {
				Document document = list.get(0);
				String queueNameA = document.getString("queueName");
				logger.info("该患者原来所在的队列是：" + queueNameA);
				// 删掉该患者在原来的队列
				new MongoDBHelper(Const.QUEUEINFO).delete(filterA);
				pushMessageToFifthAndTenthPatient(eventNo, deptId, deptName, queueNameA);
			}

			// 无论是分诊还是从一个队列拉到另一个队列，都需要推送分诊消息
			getFirstPush(eventNo, deptId, deptName, patientId, patientName, healthCardNo, visitIndicator, queueName,
					queueNo, time);
			break;

		case "B":// 从暂不看诊拉到队列
			
			getFirstPush(eventNo, deptId, deptName, patientId, patientName, healthCardNo, visitIndicator, queueName,
					queueNo, time);
			break;

		case "C"://暂不看诊或诊出
			
			Bson filterC = Filters.and(Filters.eq("deptId", deptId), Filters.eq("healthCardNo", healthCardNo));
			List<Document> listCC = new MongoDBHelper(Const.QUEUEINFO).query(filterC);
			message = "您已完成就诊！";
			pushMessage(eventNo, deptName, patientId, patientName, healthCardNo, message, queueName);// 推送消息

			if (listCC != null && listCC.size() > 0) {
				Document document = listCC.get(0);
				String queueNameC = document.getString("queueName");
				logger.info("该患者原来所在的队列是：" + queueNameC);
				// 删掉该患者在原来的队列
				new MongoDBHelper(Const.QUEUEINFO).delete(filterC);
				//向该患者所在队列的第五第十位推送消息
				pushMessageToFifthAndTenthPatient(eventNo, deptId, deptName, queueNameC);
			}
			break;
			
		case "D"://过号
			
			Bson filterD = Filters.and(Filters.eq("deptId", deptId), Filters.eq("healthCardNo", healthCardNo));

			// 如果是C暂不看诊或诊出；D：过号；E：叫号 删掉数据库队列里患者的信息
			new MongoDBHelper(Const.QUEUEINFO).delete(filterD);
			message = "您已经过号，请重新分诊！";
			pushMessage(eventNo, deptName, patientId, patientName, healthCardNo, message, queueName);
			break;
			
		case "E"://叫号
			
			Bson filterE = Filters.and(Filters.eq("deptId", deptId), Filters.eq("healthCardNo", healthCardNo));

			// 如果是C暂不看诊或诊出；D：过号；E：叫号 删掉数据库队列里患者的信息
			new MongoDBHelper(Const.QUEUEINFO).delete(filterE);
			message = "您已叫号，请及时就诊！";
			pushMessage(eventNo, deptName, patientId, patientName, healthCardNo, message, queueName);
			//向该患者所在队列的第五第十位推送消息
			pushMessageToFifthAndTenthPatient(eventNo, deptId, deptName, queueName);
			break;

		default:
			break;
		}

		return CommonUtils.getSuccessMsg();
	}

	/**
	 * @author CXM
	 * @Description: 向第五第十位患者推送消息
	 */
	private void pushMessageToFifthAndTenthPatient(String eventNo, String deptId, String deptName, String queueName) {
		Bson calculateFilter = Filters.and(Filters.eq("deptId", deptId), Filters.eq("queueName", queueName));
		List<Document> list = new MongoDBHelper(Const.QUEUEINFO).querySort(calculateFilter, "time", 1);
		// 当队列大于等于10人时
		if (list.size() >= 10) {

			pushMessaggeByCalculate(eventNo, deptName, list, 4);// 给队列第5位患者推送排队提醒消息

			pushMessaggeByCalculate(eventNo, deptName, list, 9);// 给队列第10位患者推送排队提醒消息

		} else if (list.size() >= 5 && list.size() < 10) {// 当队列大于等于5人并小于等于10人时，只给给队列第5位患者推送排队提醒消息
			pushMessaggeByCalculate(eventNo, deptName, list, 4);
		}
	}

	/**
	 * @author CXM
	 * @Description: 分诊时推送消息
	 */
	private void getFirstPush(String eventNo, String deptId, String deptName, String patientId, String patientName,
			String healthCardNo, String visitIndicator, String queueName, String queueNo, String time)
			throws Exception {
		String message = "";
		QueueInfo queueInfo = new QueueInfo();
		queueInfo.set_id(StringUtil.generateUuid());
		queueInfo.setDeptId(deptId);
		queueInfo.setDeptName(deptName);
		queueInfo.setPatientId(patientId);
		queueInfo.setPatientName(patientName);
		queueInfo.setHealthCardNo(healthCardNo);
		queueInfo.setVisitIndicator(visitIndicator);
		queueInfo.setQueueName(queueName);
		queueInfo.setQueueNo(queueNo);
		queueInfo.setTime(time);
		// 插入数据库
		QueueDBHelper.insertQueue(queueInfo);

		// his目前没有有提供前面排队还有多少人，我们通过查询数据库的记录数来计算分诊时前面还有多少名患者
		Bson calculateFilter = Filters.and(Filters.eq("deptId", deptId), Filters.eq("queueName", queueName));
		List<Document> calculateFilterList = new MongoDBHelper(Const.QUEUEINFO).query(calculateFilter);
		if (calculateFilterList != null && calculateFilterList.size() > 0) {
			message = "您已分诊！排队号为" + queueNo + "，队列前面还有" + (calculateFilterList.size() - 1) + "个人。";
			pushMessage(eventNo, deptName, patientId, patientName, healthCardNo, message, queueName);
		}
	}

	/**
	 * @author CXM
	 * @Description: 根据不同的消息去推送
	 */
	private void pushMessage(String eventNo, String deptName, String patientId, String patientName, String healthCardNo,
			String message, String queueName) {
		message = getMessage(deptName, patientName, healthCardNo, message, queueName);
		String missNo = getCustomMessageParam(eventNo, patientId, healthCardNo, message);
		logger.info("推送customMessage消息：" + missNo);
		ChannelManager.dispatch(missNo);
	}

	/**
	 * 
	 * @author CXM
	 * @Description: 给队列中的第i个人推送消息
	 */
	private void pushMessaggeByCalculate(String eventNo, String deptName, List<Document> list, int i) {
		String message;
		Document document = list.get(i);
		message = "您的排队号为" + document.getString("queueNo") + "，队列前面还有" + i + "个人";
		message = getMessage(deptName, document.getString("patientName"), document.getString("healthCardNo"), message,
				document.getString("queueName"));
		String fifthMessage = getCustomMessageParam(eventNo, document.getString("patientId"),
				document.getString("healthCardNo"), message);
		logger.info("推送customMessage消息：" + fifthMessage);
		ChannelManager.dispatch(fifthMessage);
	}

	/**
	 * @author Luffy-CXM
	 * @Description: 自定义消息参数拼接，用于一次或者二次分诊时，推送消息给患者
	 */
	private String getCustomMessageParam(String eventNo, String patientId, String healthCardNo, String message) {
		StringBuffer sb = new StringBuffer();
		sb.append("<req>");
		sb.append("<eventNo>" + eventNo + "</eventNo>");
		sb.append("<eventType>customMessage</eventType>");
		sb.append("<eventData>");
		sb.append("<patientId>" + patientId + "</patientId>");
		sb.append("<healthCardNo>" + healthCardNo + "</healthCardNo>");
		sb.append("<title>急诊科排队消息提醒</title>");
		sb.append("<message>" + message + "</message>");
		sb.append("</eventData>");
		sb.append("</req>");
		return sb.toString();
	}

	/**
	 * @author CXM
	 * @Description: 组装提示单消息
	 */
	private String getMessage(String deptName, String patientName, String healthCardNo, String message,
			String queueName) {
		StringBuffer sb = new StringBuffer();
		sb.append(patientName + "(" + healthCardNo + ")\n");
		sb.append(message + "\n\n");
		sb.append("医院：香港大学深圳医院\n");
		sb.append("科室：" + deptName + "\n");
		sb.append("队列：" + queueName + "\n");
		sb.append("姓名：" + patientName + "\n");
		sb.append("病人号：" + healthCardNo + "\n");

		return sb.toString();
	}

	
	
	public static void main(String[] args) throws Exception {
		
		 Date date = new Date(); SimpleDateFormat sd = new
		 SimpleDateFormat("yyyyMMddHHmmssSSS"); 
		 String nowTime = sd.format(date); 
		 String reqXml = "<req><eventNo>41332004413</eventNo><eventType>patientWaiting</eventType><eventData><deptId>0412</deptId><deptName>急诊科</deptName><patientId>1007</patientId><patientName>王7</patientName><healthCardNo>6667</healthCardNo><visitIndicator>A</visitIndicator><queueName>1队列</queueName><queueNo>8</queueNo><waitingCount>13</waitingCount><time>"+
		 nowTime +"</time></eventData></req>"; 
		 
		 
		 logger.info( new patientWaiting().execute(reqXml));
	}
}
