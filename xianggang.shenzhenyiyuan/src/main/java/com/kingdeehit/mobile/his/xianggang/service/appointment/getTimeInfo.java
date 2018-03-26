package com.kingdeehit.mobile.his.xianggang.service.appointment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingdeehit.mobile.his.entities.V3.result.appointment.GetTimeRegInfoResultV3;
import com.kingdeehit.mobile.his.entities.V3.result.appointment.TimeRegInfoV3;
import com.kingdeehit.mobile.his.xianggang.constant.ParamConstants;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.DateUtils;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;

/**
 * 通过本接口获取某个医生在某天、某时段出诊的号源信息。
 * 当传入排班号时以排班号为准， 如果排班号为空，则其他全部参数都必须传入。
 * @author Administrator
 * @date 2015-12-09
 */
public class getTimeInfo extends AbstractService {
	
	private static String hisInterface="querySchedulesByShifId";

	@Override
	public String execute(String reqXml) throws Exception {
		String regDate=UtilXml.getValueByAllXml(reqXml, "reqXml");		
		String shiftCode=UtilXml.getValueByAllXml(reqXml, "shiftCode");
		String scheduleId=UtilXml.getValueByAllXml(reqXml, "scheduleId");		
		//排班号格式（当天挂号）：SHIFTID@reglevelid@1);（预约挂号）:SHIFTID@reglevelid
		logger.info("排班号："+scheduleId);
		String[] tmp=null;		
		if(StringUtils.isNotBlank(scheduleId)){
			tmp=scheduleId.split("@");
			if(tmp==null){
				logger.error("排班号信息处理异常！");
				return CommonUtils.getSuccessMsg();	
			}
		}		
		String inputString=getInputParamString(tmp[0],reqXml);		
		String convertInputString=CommonUtils.convertHisInputParam(hisInterface, inputString);
		logger.error("分时查询接口【appointment.getTimeInfo】-->【"+hisInterface+"】入参：user="+ParamConstants.USER+";password="+ParamConstants.PASSWORD+";parameter="+inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();					
		String resultXml= xmlRequest.request(convertInputString);	
		resultXml=CommonUtils.convertABSOutputParam(resultXml);		
		logger.error("分时查询接口【appointment.getTimeInfo】-->【"+hisInterface+"】出参："+resultXml);
		String resCode=UtilXml.getValueByAllXml(resultXml, "STATE");
		if(StringUtils.isBlank(resCode)||Integer.parseInt(resCode)<=0){
			String err=UtilXml.getValueByAllXml(resultXml, "MSG");
			logger.error("接口调用返回结果失败：resCode="+resCode+";msg="+err);
			return CommonUtils.getSuccessMsg();	
		}			
		GetTimeRegInfoResultV3 resultParam=convertHisStringToV3Object(tmp.length,regDate,shiftCode,resultXml);			
		XStream xstream = UtilXml.getXStream(GetTimeRegInfoResultV3.class);					
		return xstream.toXML(resultParam);
	}
	
	/**
	 * V3入参转his入参
	 * @param req
	 * @return
	 */
	private String getInputParamString(String scheduleId,String req){
		String deptId=UtilXml.getValueByAllXml(req, "deptId");		
		StringBuilder str=new StringBuilder(100);
		str.append("<PARAMETER>");
		str.append("<DEPTID>"+deptId+"</DEPTID>");
		str.append("<SHIFTID>"+scheduleId+"</SHIFTID>");
		str.append("</PARAMETER>");
		return str.toString();
	}
	
	/**
	 * his出参转V3出参
	 * @param orderString
	 * @return
	 * @throws DocumentException 
	 */
	@SuppressWarnings("unchecked")
	private GetTimeRegInfoResultV3 convertHisStringToV3Object(int len,String regDate,String shiftCode,String resultXml) throws DocumentException{		
		GetTimeRegInfoResultV3 resultV3=new GetTimeRegInfoResultV3();
		Document document=DocumentHelper.parseText(resultXml);
		Element root=document.getRootElement();		
		Element res=root.element("DATA");		
		List<Element> list=res.elements("RECORD");
		List<TimeRegInfoV3> timeInfoList=new ArrayList<TimeRegInfoV3>();
		String currentDate=DateUtils.getChineseTime();
		String yearMonthDay=DateUtils.getYMDTime(new Date());
		for(Element ele:list){			
			String noon=CommonUtils.covertNoonByCode(ele.elementText("AMFLAG"));			
			String startTime=ele.elementText("STARTTIME");
			String scheduleDate=yearMonthDay+" "+startTime+":00";
			logger.info("患者选择班别："+shiftCode+";当前分时时段班别："+noon+";当前时间："+currentDate+";排班时间："+scheduleDate);	
			if(noon.equals(shiftCode)){				
				if(len==3){
					//当天挂号只能挂当前时间后时间段的号
					if(CommonUtils.compareDate(scheduleDate, currentDate)<0){
						continue;
					}
				}
				TimeRegInfoV3 timeRegInfo=new TimeRegInfoV3();
				timeRegInfo.setPeriodId(ele.elementText("SHIFTID"));										
				timeRegInfo.setStartTime(startTime);				
				timeRegInfo.setEndTime(ele.elementText("ENDTIME"));
				timeRegInfo.setRegTotalCount(ele.elementText("TOTALQUOTA"));
				timeRegInfo.setRegLeaveCount(ele.elementText("OCCQUOTA"));
				String total=ele.elementText("TOTALQUOTA");
				String yiYuYue=ele.elementText("OCCQUOTA");				
				if(StringUtils.isNotBlank(total)){
					if(StringUtils.isBlank(yiYuYue)){
						timeRegInfo.setRegLeaveCount(Integer.parseInt(total)+"");
					}else{
						timeRegInfo.setRegLeaveCount((Integer.parseInt(total)-Integer.parseInt(yiYuYue))+"");
					}
				}else{
					timeRegInfo.setRegTotalCount("0");
				}				
				timeInfoList.add(timeRegInfo);
			}
		}	
		logger.error("分时号源数："+timeInfoList.size());
		resultV3.setResultCode("0");
		resultV3.setResultDesc("成功");
		resultV3.setTimeRegInfoList(timeInfoList);		
		return resultV3;
	}
}

