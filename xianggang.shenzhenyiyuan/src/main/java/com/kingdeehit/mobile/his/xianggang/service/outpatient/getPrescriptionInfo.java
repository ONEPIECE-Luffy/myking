package com.kingdeehit.mobile.his.xianggang.service.outpatient;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;

/**
 * 门诊处方待缴费列表查询（分方可勾选）
 * @author tangfulin
 *
 */
public class getPrescriptionInfo extends AbstractService{
	

	@Override
	public String execute(String reqXml) throws Exception {
		return CommonUtils.getSuccessMsg();					
	}
	
}
