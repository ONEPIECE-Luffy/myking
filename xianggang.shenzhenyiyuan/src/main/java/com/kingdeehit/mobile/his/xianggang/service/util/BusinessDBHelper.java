package com.kingdeehit.mobile.his.xianggang.service.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.kingdeehit.mobile.his.entities.table.ChannelAppointmentInfo;
import com.kingdeehit.mobile.his.entities.table.ChannelBindingCard;
import com.kingdeehit.mobile.his.entities.table.ChannelProductOrder;
import com.kingdeehit.mobile.his.utils.ChannelManager;
import com.kingdeehit.mobile.his.utils.MongoDBHelper;
import com.kingdeehit.mobile.his.xianggang.entity.table.ChannelAppointmentLockInfo;
import com.kingdeehit.mobile.his.xianggang.entity.table.ChannelLockInfo;
import com.kingdeehit.mobile.his.xianggang.entity.table.RegisterInfo;
import com.kingdeehit.mobile.utils.StringUtil;
import com.kingdeehit.mobile.utils.UtilJson;
import com.mongodb.client.model.Filters;

import net.sf.json.JSONObject;

/**
 * ҵ������DB������
 * @author wudigang
 * 2016-07-28
 */
public class BusinessDBHelper {

	protected static Logger logger = Logger.getLogger(BusinessDBHelper.class);	

	/**
	 * Ժ�ڰ󿨱�
	 */
	public static final String CHANNEL_BINDING_CARD = "channel_binding_card";
	
	/**
	 * Ժ�ڹҺ�������Ϣ��
	 */
	public static final String CHANNEL_REGISTER_INFO = "channel_register_info";
	
	/**
	 * Ժ��ԤԼ�Һż�¼��
	 */
	public static final String CHANNEL_APPOINT_REGISTER = "channel_appoint_register";
	
	/**
	 * Ժ��֧��������
	 */
	public static final String CHANNEL_PRODUCT_ORDER = "channel_product_order";
	
	 
	
	
	
	private static MongoDBHelper getDB(String tableName){
		MongoDBHelper mongoDBHelper = new MongoDBHelper(tableName);
		return mongoDBHelper;
	}

	/**
     * ��document�����б�ת��Ϊҵ��ʵ������б�
     * @param documentList
     * @return
     */
	private static Object formatDocument(List<Document> documentList,Class clazz){
		List<Object> objList = new ArrayList<Object>();
    	if(documentList != null){
			for(Document document : documentList){
				String json = document.toJson();
				JSONObject jsonObject = JSONObject.fromObject(json);			
				objList.add(JSONObject.toBean(jsonObject, clazz));
			}
		}
		return objList;
    }
	

	public static ChannelBindingCard getPatientInfo(String healthcardNo) {
		Bson filters = Filters.and(Filters.eq("healthCardNo", healthcardNo));
		List<Document> documentList = getDB(CHANNEL_BINDING_CARD).query(filters);
		@SuppressWarnings("unchecked")
		List<ChannelBindingCard> resultList = (List<ChannelBindingCard>) formatDocument(documentList,ChannelBindingCard.class);
		if(resultList != null && resultList.size()>0)
			return resultList.get(0);
		
		return null;
	}
	
	/**
	 * ���ݲ���id��ѯ������Ϣ
	 * @param patientId
	 * @return
	 */
	public static ChannelBindingCard getPatientInfoByPatientId(String patientId) {
		Bson filters = Filters.and(Filters.eq("patientId", patientId));
		List<Document> documentList = getDB(CHANNEL_BINDING_CARD).query(filters);
		@SuppressWarnings("unchecked")
		List<ChannelBindingCard> resultList = (List<ChannelBindingCard>) formatDocument(documentList,ChannelBindingCard.class);
		if(resultList != null && resultList.size()>0)
			return resultList.get(0);
		
		return null;
	}
	
	public static ChannelAppointmentInfo getAppointmentInfo(String orderId) {
		Bson filters = Filters.and(Filters.eq("orderId", orderId));
		List<Document> documentList = getDB(CHANNEL_APPOINT_REGISTER).query(filters);
		@SuppressWarnings("unchecked")
		List<ChannelAppointmentInfo> resultList = (List<ChannelAppointmentInfo>) formatDocument(documentList,ChannelAppointmentInfo.class);
		if(resultList != null && resultList.size()>0)
			return resultList.get(0);
		
		return null;
	}
	public static ChannelAppointmentInfo getAppointmentInfoByBookingNo(String bookingNo) { //AddOrder��bookingNo�����his��APPTNO���ҽ�roomAddress����hisOrderNO
		Bson filters = Filters.and(Filters.eq("remark", bookingNo),Filters.eq("channelCode", "0001"));
		List<Document> documentList = getDB(CHANNEL_APPOINT_REGISTER).query(filters);
		@SuppressWarnings("unchecked")
		List<ChannelAppointmentInfo> resultList = (List<ChannelAppointmentInfo>) formatDocument(documentList,ChannelAppointmentInfo.class);
		if(resultList != null && resultList.size()>0)
			return resultList.get(0);
		
		return null;
	}
	public static ChannelProductOrder getPaymentInfo(String orderId) {
		Bson filters = Filters.and(Filters.eq("orderId", orderId));
		List<Document> documentList = getDB(CHANNEL_PRODUCT_ORDER).query(filters);
		@SuppressWarnings("unchecked")
		List<ChannelProductOrder> resultList = (List<ChannelProductOrder>) formatDocument(documentList,ChannelProductOrder.class);
		if(resultList != null && resultList.size()>0)
			return resultList.get(0);
		
		return null;
	}
	/**
	 * �洢�ҺŹ��������ŵ�Id���ҵĹҺ��У����ֺ�Դ������
	 * @author YJB
	 * @date 2017��9��18�� ����3:06:47
	 * @param info
	 */
	public static void saveAndUpdateRegisterInfo(ChannelLockInfo info) {
		
		info.setChannelName(ChannelManager.getChannelName(info.getChannelCode()));
		
		if(StringUtil.isEmpty(info.get_id())){
			info.set_id(StringUtil.generateUuid());
			Document doc = Document.parse(JSONObject.fromObject(info).toString());
			try {
				new MongoDBHelper(CHANNEL_REGISTER_INFO).insert(doc);
			} catch (Exception e) {
				logger.error("����������Ϣʧ�ܣ�������ΪorderId" + info.getOrderId()+" ���ŵ�id�� "+info.getBookingNo());
			}
		} else {
			Document doc = Document.parse(UtilJson.jsonForNoNullValue(info));
			try {
				new MongoDBHelper(CHANNEL_REGISTER_INFO).updateById(info.get_id(), doc);
			} catch (Exception e) {
				logger.error("����������Ϣʧ�ܣ�������ΪorderId" + info.getOrderId()+" ���ŵ�id�� "+info.getBookingNo());
			}
		}
		
	}
	/**
	 * ��ѯ����Id�Ƿ����
	 * @author YJB
	 * @date 2017��9��18�� ����3:21:13
	 * @param bookingNo
	 * @return
	 */
	public static ChannelAppointmentLockInfo getLockInfoByBookingNo(String bookingNo) {
		
		Bson filters = Filters.or(Filters.eq("bookingNo", bookingNo),Filters.eq("hisOrderNo", bookingNo));
		List<Document> documentList = getDB("channel_appointment_lock_info").query(filters);
		@SuppressWarnings("unchecked")
		List<ChannelAppointmentLockInfo> resultList = (List<ChannelAppointmentLockInfo>) formatDocument(documentList,ChannelAppointmentLockInfo.class);
		if(resultList != null && resultList.size()>0)
			return resultList.get(0);
		
		return null;
	}
	/**
	 * ��ѯĳ������ĳ��ʱ���֮��ĵ�ԤԼ�Һż�¼
	 * @author YJB
	 * @date 2017��9��29�� ����8:38:18
	 * @param healthCardNo
	 * @param date
	 * @return
	 */
	public static List<ChannelAppointmentInfo> getAllAppointmentInfo(
			String healthCardNo) {
		
		/* BasicDBObject searchQuery = new BasicDBObject();//������ѯ�Ķ���  
		 searchQuery.put("regDate", BasicDBObjectBuilder.start("$gte", startDate+" 00:00:00").add("$lte", endDate+" 23:59:59").get());  
	       */
		Bson filters = Filters.and(Filters.eq("healthCardNo", healthCardNo),Filters.eq("svObjectId", "02"));
		List<Document> documentList = getDB(CHANNEL_APPOINT_REGISTER).query(filters);
		@SuppressWarnings("unchecked")
		List<ChannelAppointmentInfo> resultList = (List<ChannelAppointmentInfo>) formatDocument(documentList,ChannelAppointmentInfo.class);
		if(resultList != null && resultList.size()>0)
			return resultList;
		
		return null;
	}
	/**
	 * ����his�Һ���ˮ�Ż�ȡ����id��������ˮ�ţ�
	 * @author YJB
	 * @date 2017��10��11�� ����8:18:17
	 * @param bookingNo
	 * @return
	 */
	public static RegisterInfo getMZLSHByBookingNO(String bookingNo) {
		
		Bson filters = Filters.and(Filters.eq("bookingNo", bookingNo));
		List<Document> documentList = getDB(CHANNEL_REGISTER_INFO).query(filters);
		List<RegisterInfo> resultList = (List<RegisterInfo>) formatDocument(documentList,RegisterInfo.class);
		if(resultList != null && resultList.size()>0){
			return resultList.get(0);
		}else{
			return null;
		}
		
	}
	/**
	 * ����Һ���Ϣ  ��bookingNo��������ˮ�ŵĹ�ϵ��
	 * @author YJB
	 * @date 2017��10��11�� ����8:43:19
	 * @param info
	 */
	public static void saveRegisterInfo(RegisterInfo info) {
		
		Document doc = Document.parse(JSONObject.fromObject(info).toString());
		try {
			getDB(CHANNEL_REGISTER_INFO).insert(doc);
		} catch (Exception e) {
			logger.error("����Һ���Ϣʧ�ܣ�His����id "+info.getBookingNo()+" ������ˮ���� "+info.getMzlsh() );
		}
	}
	/**
	 * ���ݶ����ţ���ȡ������Ϣ
	 * @author YJB
	 * @date 2017��10��13�� ����9:25:57
	 * @param lockId
	 * @return
	 */
	public static ChannelAppointmentLockInfo getLockInfoByOrderId(String lockId) {
		Bson filters = Filters.and(Filters.eq("orderId", lockId));
		List<Document> documentList = getDB(CHANNEL_REGISTER_INFO).query(filters);
		List<ChannelAppointmentLockInfo> list = (List<ChannelAppointmentLockInfo>)formatDocument(documentList, ChannelAppointmentLockInfo.class);
		if(list!=null&&list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
	
}
