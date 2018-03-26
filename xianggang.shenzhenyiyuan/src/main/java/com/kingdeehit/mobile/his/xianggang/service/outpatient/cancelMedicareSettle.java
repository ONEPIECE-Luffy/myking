package com.kingdeehit.mobile.his.xianggang.service.outpatient;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;

/**
 * 取消医保结算接口封装
 * @author tangfulin
 *
 */
public class cancelMedicareSettle extends AbstractService{
	

	@Override
	public String execute(String reqXml) throws Exception {		
		return CommonUtils.getSuccessMsg();				
	}

}
