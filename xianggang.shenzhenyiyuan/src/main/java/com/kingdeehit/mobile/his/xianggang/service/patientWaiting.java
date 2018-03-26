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
		String eventNo = UtilXml.getValueByAllXml(reqXml, "eventNo");// �¼���ˮ��
		String deptId = UtilXml.getValueByAllXml(reqXml, "deptId");
		String deptName = UtilXml.getValueByAllXml(reqXml, "deptName");
		String patientId = UtilXml.getValueByAllXml(reqXml, "patientId");
		String patientName = UtilXml.getValueByAllXml(reqXml, "patientName");
		String healthCardNo = UtilXml.getValueByAllXml(reqXml, "healthCardNo");
		String visitIndicator = UtilXml.getValueByAllXml(reqXml, "visitIndicator");// �����ʶ
		String queueName = UtilXml.getValueByAllXml(reqXml, "queueName");// ��������
		String queueNo = UtilXml.getValueByAllXml(reqXml, "queueNo");// �ŶӺ�
		String time = UtilXml.getValueByAllXml(reqXml, "time");// ����ʱ��

		String message = "";

		switch (visitIndicator) {
		case "A"://A�����������1�Ƿ��2�Ǵ�һ������������һ������
			
			// ͨ������id�ͽ������ż������ݿ⣬����ҵ���¼��֤��A�Ǵ�һ������������һ������
			Bson filterA = Filters.and(Filters.eq("deptId", deptId), Filters.eq("healthCardNo", healthCardNo));
			List<Document> list = new MongoDBHelper(Const.QUEUEINFO).query(filterA);

			if (list != null && list.size() > 0) {
				Document document = list.get(0);
				String queueNameA = document.getString("queueName");
				logger.info("�û���ԭ�����ڵĶ����ǣ�" + queueNameA);
				// ɾ���û�����ԭ���Ķ���
				new MongoDBHelper(Const.QUEUEINFO).delete(filterA);
				pushMessageToFifthAndTenthPatient(eventNo, deptId, deptName, queueNameA);
			}

			// �����Ƿ��ﻹ�Ǵ�һ������������һ�����У�����Ҫ���ͷ�����Ϣ
			getFirstPush(eventNo, deptId, deptName, patientId, patientName, healthCardNo, visitIndicator, queueName,
					queueNo, time);
			break;

		case "B":// ���ݲ�������������
			
			getFirstPush(eventNo, deptId, deptName, patientId, patientName, healthCardNo, visitIndicator, queueName,
					queueNo, time);
			break;

		case "C"://�ݲ���������
			
			Bson filterC = Filters.and(Filters.eq("deptId", deptId), Filters.eq("healthCardNo", healthCardNo));
			List<Document> listCC = new MongoDBHelper(Const.QUEUEINFO).query(filterC);
			message = "������ɾ��";
			pushMessage(eventNo, deptName, patientId, patientName, healthCardNo, message, queueName);// ������Ϣ

			if (listCC != null && listCC.size() > 0) {
				Document document = listCC.get(0);
				String queueNameC = document.getString("queueName");
				logger.info("�û���ԭ�����ڵĶ����ǣ�" + queueNameC);
				// ɾ���û�����ԭ���Ķ���
				new MongoDBHelper(Const.QUEUEINFO).delete(filterC);
				//��û������ڶ��еĵ����ʮλ������Ϣ
				pushMessageToFifthAndTenthPatient(eventNo, deptId, deptName, queueNameC);
			}
			break;
			
		case "D"://����
			
			Bson filterD = Filters.and(Filters.eq("deptId", deptId), Filters.eq("healthCardNo", healthCardNo));

			// �����C�ݲ�����������D�����ţ�E���к� ɾ�����ݿ�����ﻼ�ߵ���Ϣ
			new MongoDBHelper(Const.QUEUEINFO).delete(filterD);
			message = "���Ѿ����ţ������·��";
			pushMessage(eventNo, deptName, patientId, patientName, healthCardNo, message, queueName);
			break;
			
		case "E"://�к�
			
			Bson filterE = Filters.and(Filters.eq("deptId", deptId), Filters.eq("healthCardNo", healthCardNo));

			// �����C�ݲ�����������D�����ţ�E���к� ɾ�����ݿ�����ﻼ�ߵ���Ϣ
			new MongoDBHelper(Const.QUEUEINFO).delete(filterE);
			message = "���ѽкţ��뼰ʱ���";
			pushMessage(eventNo, deptName, patientId, patientName, healthCardNo, message, queueName);
			//��û������ڶ��еĵ����ʮλ������Ϣ
			pushMessageToFifthAndTenthPatient(eventNo, deptId, deptName, queueName);
			break;

		default:
			break;
		}

		return CommonUtils.getSuccessMsg();
	}

	/**
	 * @author CXM
	 * @Description: ������ʮλ����������Ϣ
	 */
	private void pushMessageToFifthAndTenthPatient(String eventNo, String deptId, String deptName, String queueName) {
		Bson calculateFilter = Filters.and(Filters.eq("deptId", deptId), Filters.eq("queueName", queueName));
		List<Document> list = new MongoDBHelper(Const.QUEUEINFO).querySort(calculateFilter, "time", 1);
		// �����д��ڵ���10��ʱ
		if (list.size() >= 10) {

			pushMessaggeByCalculate(eventNo, deptName, list, 4);// �����е�5λ���������Ŷ�������Ϣ

			pushMessaggeByCalculate(eventNo, deptName, list, 9);// �����е�10λ���������Ŷ�������Ϣ

		} else if (list.size() >= 5 && list.size() < 10) {// �����д��ڵ���5�˲�С�ڵ���10��ʱ��ֻ�������е�5λ���������Ŷ�������Ϣ
			pushMessaggeByCalculate(eventNo, deptName, list, 4);
		}
	}

	/**
	 * @author CXM
	 * @Description: ����ʱ������Ϣ
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
		// �������ݿ�
		QueueDBHelper.insertQueue(queueInfo);

		// hisĿǰû�����ṩǰ���Ŷӻ��ж����ˣ�����ͨ����ѯ���ݿ�ļ�¼�����������ʱǰ�滹�ж���������
		Bson calculateFilter = Filters.and(Filters.eq("deptId", deptId), Filters.eq("queueName", queueName));
		List<Document> calculateFilterList = new MongoDBHelper(Const.QUEUEINFO).query(calculateFilter);
		if (calculateFilterList != null && calculateFilterList.size() > 0) {
			message = "���ѷ���ŶӺ�Ϊ" + queueNo + "������ǰ�滹��" + (calculateFilterList.size() - 1) + "���ˡ�";
			pushMessage(eventNo, deptName, patientId, patientName, healthCardNo, message, queueName);
		}
	}

	/**
	 * @author CXM
	 * @Description: ���ݲ�ͬ����Ϣȥ����
	 */
	private void pushMessage(String eventNo, String deptName, String patientId, String patientName, String healthCardNo,
			String message, String queueName) {
		message = getMessage(deptName, patientName, healthCardNo, message, queueName);
		String missNo = getCustomMessageParam(eventNo, patientId, healthCardNo, message);
		logger.info("����customMessage��Ϣ��" + missNo);
		ChannelManager.dispatch(missNo);
	}

	/**
	 * 
	 * @author CXM
	 * @Description: �������еĵ�i����������Ϣ
	 */
	private void pushMessaggeByCalculate(String eventNo, String deptName, List<Document> list, int i) {
		String message;
		Document document = list.get(i);
		message = "�����ŶӺ�Ϊ" + document.getString("queueNo") + "������ǰ�滹��" + i + "����";
		message = getMessage(deptName, document.getString("patientName"), document.getString("healthCardNo"), message,
				document.getString("queueName"));
		String fifthMessage = getCustomMessageParam(eventNo, document.getString("patientId"),
				document.getString("healthCardNo"), message);
		logger.info("����customMessage��Ϣ��" + fifthMessage);
		ChannelManager.dispatch(fifthMessage);
	}

	/**
	 * @author Luffy-CXM
	 * @Description: �Զ�����Ϣ����ƴ�ӣ�����һ�λ��߶��η���ʱ��������Ϣ������
	 */
	private String getCustomMessageParam(String eventNo, String patientId, String healthCardNo, String message) {
		StringBuffer sb = new StringBuffer();
		sb.append("<req>");
		sb.append("<eventNo>" + eventNo + "</eventNo>");
		sb.append("<eventType>customMessage</eventType>");
		sb.append("<eventData>");
		sb.append("<patientId>" + patientId + "</patientId>");
		sb.append("<healthCardNo>" + healthCardNo + "</healthCardNo>");
		sb.append("<title>������Ŷ���Ϣ����</title>");
		sb.append("<message>" + message + "</message>");
		sb.append("</eventData>");
		sb.append("</req>");
		return sb.toString();
	}

	/**
	 * @author CXM
	 * @Description: ��װ��ʾ����Ϣ
	 */
	private String getMessage(String deptName, String patientName, String healthCardNo, String message,
			String queueName) {
		StringBuffer sb = new StringBuffer();
		sb.append(patientName + "(" + healthCardNo + ")\n");
		sb.append(message + "\n\n");
		sb.append("ҽԺ����۴�ѧ����ҽԺ\n");
		sb.append("���ң�" + deptName + "\n");
		sb.append("���У�" + queueName + "\n");
		sb.append("������" + patientName + "\n");
		sb.append("���˺ţ�" + healthCardNo + "\n");

		return sb.toString();
	}

	
	
	public static void main(String[] args) throws Exception {
		
		 Date date = new Date(); SimpleDateFormat sd = new
		 SimpleDateFormat("yyyyMMddHHmmssSSS"); 
		 String nowTime = sd.format(date); 
		 String reqXml = "<req><eventNo>41332004413</eventNo><eventType>patientWaiting</eventType><eventData><deptId>0412</deptId><deptName>�����</deptName><patientId>1007</patientId><patientName>��7</patientName><healthCardNo>6667</healthCardNo><visitIndicator>A</visitIndicator><queueName>1����</queueName><queueNo>8</queueNo><waitingCount>13</waitingCount><time>"+
		 nowTime +"</time></eventData></req>"; 
		 
		 
		 logger.info( new patientWaiting().execute(reqXml));
	}
}
