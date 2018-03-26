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
 * ͨ�����ӿڻ�ȡĳ��ҽ����ĳ�졢ĳʱ�γ���ĺ�Դ��Ϣ��
 * �������Ű��ʱ���Ű��Ϊ׼�� ����Ű��Ϊ�գ�������ȫ�����������봫�롣
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
		//�Ű�Ÿ�ʽ������Һţ���SHIFTID@reglevelid@1);��ԤԼ�Һţ�:SHIFTID@reglevelid
		logger.info("�Ű�ţ�"+scheduleId);
		String[] tmp=null;		
		if(StringUtils.isNotBlank(scheduleId)){
			tmp=scheduleId.split("@");
			if(tmp==null){
				logger.error("�Ű����Ϣ�����쳣��");
				return CommonUtils.getSuccessMsg();	
			}
		}		
		String inputString=getInputParamString(tmp[0],reqXml);		
		String convertInputString=CommonUtils.convertHisInputParam(hisInterface, inputString);
		logger.error("��ʱ��ѯ�ӿڡ�appointment.getTimeInfo��-->��"+hisInterface+"����Σ�user="+ParamConstants.USER+";password="+ParamConstants.PASSWORD+";parameter="+inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();					
		String resultXml= xmlRequest.request(convertInputString);	
		resultXml=CommonUtils.convertABSOutputParam(resultXml);		
		logger.error("��ʱ��ѯ�ӿڡ�appointment.getTimeInfo��-->��"+hisInterface+"�����Σ�"+resultXml);
		String resCode=UtilXml.getValueByAllXml(resultXml, "STATE");
		if(StringUtils.isBlank(resCode)||Integer.parseInt(resCode)<=0){
			String err=UtilXml.getValueByAllXml(resultXml, "MSG");
			logger.error("�ӿڵ��÷��ؽ��ʧ�ܣ�resCode="+resCode+";msg="+err);
			return CommonUtils.getSuccessMsg();	
		}			
		GetTimeRegInfoResultV3 resultParam=convertHisStringToV3Object(tmp.length,regDate,shiftCode,resultXml);			
		XStream xstream = UtilXml.getXStream(GetTimeRegInfoResultV3.class);					
		return xstream.toXML(resultParam);
	}
	
	/**
	 * V3���תhis���
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
	 * his����תV3����
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
			logger.info("����ѡ����"+shiftCode+";��ǰ��ʱʱ�ΰ��"+noon+";��ǰʱ�䣺"+currentDate+";�Ű�ʱ�䣺"+scheduleDate);	
			if(noon.equals(shiftCode)){				
				if(len==3){
					//����Һ�ֻ�ܹҵ�ǰʱ���ʱ��εĺ�
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
		logger.error("��ʱ��Դ����"+timeInfoList.size());
		resultV3.setResultCode("0");
		resultV3.setResultDesc("�ɹ�");
		resultV3.setTimeRegInfoList(timeInfoList);		
		return resultV3;
	}
}

