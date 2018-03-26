package com.kingdeehit.mobile.his.xianggang.service.util;

import java.util.List;

import org.apache.log4j.Logger;
import org.bson.conversions.Bson;

import com.kingdeehit.mobile.his.utils.MongoDBHelper;
import com.kingdeehit.mobile.his.xianggang.constant.Const;
import com.kingdeehit.mobile.his.xianggang.entity.table.QueueInfo;
import com.mongodb.client.model.Filters;

import net.sf.json.JSONObject;
/**
* @ClassName: QueueDBHelper 
* @Description: �����Ŷ���Ϣ�洢����
* @author Luffy-CXM 
* @date 2018��3��20�� ����5:22:44
 */
public class QueueDBHelper {
	private static Logger logger = Logger.getLogger(QueueDBHelper.class);
	
	/**
	* @author Luffy-CXM 
	* @Description: ����Ƕ��η�����¶�����Ϣ
	 */
	public static void insertQueue(QueueInfo queueInfo) throws Exception{		
		Bson filter = Filters.and(Filters.eq("deptId",queueInfo.getDeptId()),Filters.eq("healthCardNo", queueInfo.getHealthCardNo()));
		List<org.bson.Document> recordList=new MongoDBHelper(Const.QUEUEINFO).query(filter);
		if(recordList==null||recordList.size()==0){	
			//queueInfo.set_id(StringUtil.generateUuid());
			org.bson.Document doc = org.bson.Document.parse(JSONObject.fromObject(queueInfo).toString());
			new MongoDBHelper(Const.QUEUEINFO).insert(doc);	
			logger.error("�����桿������Ϣ�ɹ���");
		}else{
			org.bson.Document tmp=recordList.get(0);
			queueInfo.set_id(tmp.getString("_id"));
			org.bson.Document doc = org.bson.Document.parse(JSONObject.fromObject(queueInfo).toString());
			new MongoDBHelper(Const.QUEUEINFO).updateById(tmp.getString("_id"),doc);
			logger.error("�����¡�������Ϣ�ɹ���");
		}
	}

}
