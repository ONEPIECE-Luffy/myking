/**
 *
 */
package com.kingdeehit.mobile.his.xianggang.service.support;

import com.kingdeehit.mobile.his.utils.BusinessDBHelper;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.utils.UtilXml;


/**
 * @author v
 * ��ȡ�Żݶ���
 *
 */
public class getSvObject extends AbstractService {
	@Override
	public String execute(String reqXml) throws Exception {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><res>"
				+"<resultCode>0</resultCode>"
				+"<resultDesc>�ɹ�</resultDesc>"
				+"<svObjectInfo>"
				+"<svObjectId>01</svObjectId>"
				+"<svObject>�Է�</svObject>"
				+"<isInsuran>0</isInsuran>"
				+"</svObjectInfo>"
				+"<svObjectInfo>"
				+"<svObjectId>02</svObjectId>"
				+"<svObject>��ͨҽ��</svObject>"
				+"<isInsuran>1</isInsuran>"
				+"</svObjectInfo>"
				+"</res>";
		return xml;
	}

}
