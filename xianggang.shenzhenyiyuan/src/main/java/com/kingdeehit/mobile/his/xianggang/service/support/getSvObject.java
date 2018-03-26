/**
 *
 */
package com.kingdeehit.mobile.his.xianggang.service.support;

import com.kingdeehit.mobile.his.utils.BusinessDBHelper;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.utils.UtilXml;


/**
 * @author v
 * 获取优惠对象
 *
 */
public class getSvObject extends AbstractService {
	@Override
	public String execute(String reqXml) throws Exception {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><res>"
				+"<resultCode>0</resultCode>"
				+"<resultDesc>成功</resultDesc>"
				+"<svObjectInfo>"
				+"<svObjectId>01</svObjectId>"
				+"<svObject>自费</svObject>"
				+"<isInsuran>0</isInsuran>"
				+"</svObjectInfo>"
				+"<svObjectInfo>"
				+"<svObjectId>02</svObjectId>"
				+"<svObject>普通医保</svObject>"
				+"<isInsuran>1</isInsuran>"
				+"</svObjectInfo>"
				+"</res>";
		return xml;
	}

}
