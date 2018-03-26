package com.kingdeehit.mobile.his.xianggang.service.appointment;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingdeehit.mobile.his.entities.V3.result.appointment.GetScheduleInfoResultV3;
import com.kingdeehit.mobile.his.entities.V3.result.appointment.RegInfoV3;
import com.kingdeehit.mobile.his.entities.V3.result.appointment.ScheduleInfoV3;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.DateUtils;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;

/**
 * @author tangfulin
 * 查询医生信息
 */
public class getScheduleInfo extends AbstractService{
	
	private static String hisInterface="querySchedules";
	
	@Override
	public String execute(String reqXml) throws Exception {
		String inputString=getInputParamString(reqXml);		
		logger.error("医生排班信息查询接口【appointment.getScheduleInfo】-->【"+hisInterface+"】入参："+inputString);
		inputString=CommonUtils.convertHisInputParam(hisInterface, inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();					
		String resultXml= xmlRequest.request(inputString);	
		resultXml=CommonUtils.convertABSOutputParam(resultXml);		
		logger.error("医生排班信息查询接口【appointment.getScheduleInfo】-->【"+hisInterface+"】出参："+resultXml);
		String resCode=UtilXml.getValueByAllXml(resultXml, "STATE");
		if(StringUtils.isBlank(resCode)||Integer.parseInt(resCode)<=0){
			String err=UtilXml.getValueByAllXml(resultXml, "MSG");
			logger.error("接口调用返回结果失败：resCode="+resCode+";msg="+err);
			return CommonUtils.getSuccessMsg();	
		}				
		GetScheduleInfoResultV3 resultParam=convertHisStringToV3Object(resultXml);			
		XStream stream = UtilXml.getXStream(GetScheduleInfoResultV3.class);					
		return stream.toXML(resultParam);		
	}
	
	/**
	 * V3入参转his入参
	 * @param req
	 * @return
	 */
	private String getInputParamString(String req){
		String deptId=UtilXml.getValueByAllXml(req, "deptId");		
		String startDate=UtilXml.getValueByAllXml(req, "startDate");
		//DateUtils.getSevenDaysDate(startDate,-1)
		String endDate=UtilXml.getValueByAllXml(req, "endDate");
		long days=DateUtils.getDaysBetweenDate(startDate,endDate);		
		StringBuilder str=new StringBuilder(100);
		str.append("<PARAMETER>");
		str.append("<DEPTID>"+deptId+"</DEPTID>");
		str.append("<STARTDATE>"+startDate+"</STARTDATE>");
		str.append("<DAYOFF>"+days+"</DAYOFF>");
		str.append("<AMFLAG></AMFLAG>");
		str.append("</PARAMETER>");
		return str.toString();
	}
	
	/**
	 * his挂号级别接口调用V3入参转his入参
	 * @param regLevelid
	 * @return
	 */
	private String getAppInputParamString(String regLevelid){	
		StringBuilder str=new StringBuilder(100);
		str.append("<PARAMETER>");
		str.append("<REGLEVELID>"+regLevelid+"</REGLEVELID>");
		str.append("<REGLEVELNAME ></REGLEVELNAME >");
		str.append("</PARAMETER>");
		return str.toString();
	}
	
	
	/**
	 * his出参转V3出参
	 * @param orderString
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private GetScheduleInfoResultV3 convertHisStringToV3Object(String resultXml) throws Exception{		
		GetScheduleInfoResultV3 resultV3=new GetScheduleInfoResultV3();
		Document document=DocumentHelper.parseText(resultXml);
		Element root=document.getRootElement();		
		Element res=root.element("DATA");		
		List<Element> list=res.elements("RECORD");
		List<RegInfoV3> deptInfoList=new ArrayList<RegInfoV3>();
		for(Element ele:list){			
			RegInfoV3 regInfoV3=null;
			String deptId=ele.elementText("DEPTID");	
			String date=ele.elementText("DATE");
			String startTime=ele.elementText("STARTTIME");
			for(RegInfoV3 infoV3:deptInfoList){
				if(infoV3.getDoctorId().equals(deptId)){
					regInfoV3=infoV3;					
					break;
				}
			}	
			if(regInfoV3!=null){
				List<ScheduleInfoV3> scheduleList=regInfoV3.getScheduleInfoV3List();
				boolean flag=false;
				if(scheduleList!=null&&scheduleList.size()>0){
					for(ScheduleInfoV3 v3:scheduleList){
						//String noon=CommonUtils.covertNoonByCode(ele.elementText("AMFLAG"));
						if(v3.getRegDate().equals(date)&&v3.getStartTime().equals(startTime)){
							flag=true;					
							break;
						}					
					}
				}
				if(!flag){							
					ScheduleInfoV3 v3=new ScheduleInfoV3();
					v3.setDeptId(deptId);
					v3.setDeptName(regInfoV3.getDoctorName());	
					v3.setClinicUnitId(deptId);
					v3.setClinicUnitName(regInfoV3.getDoctorName());
					v3.setRegDate(ele.elementText("DATE"));
					v3.setStartTime(ele.elementText("STARTTIME"));
					v3.setEndTime(ele.elementText("ENDTIME"));				
					String noon=CommonUtils.covertNoonByCode(ele.elementText("AMFLAG"));				
					v3.setShiftCode(noon);
					v3.setShiftName(noon);
					v3.setIsTimeReg("1");
					v3.setRegStatus("1");
					String reglevelid=ele.elementText("REGLEVELID");
					v3.setScheduleId(ele.elementText("SHIFTID")+"@"+reglevelid);
					String count=ele.elementText("TOTALQUOTA");
					String used=ele.elementText("OCCQUOTA");					
					v3.setRegTotalCount(count);				
					if(StringUtils.isNotBlank(count)){
						if(StringUtils.isBlank(used)){
							v3.setRegLeaveCount(Integer.parseInt(count)+"");
						}else{
							v3.setRegLeaveCount((Integer.parseInt(count)-Integer.parseInt(used))+"");
						}
					}else{
						v3.setRegLeaveCount("0");
					}					
					logger.error("挂号级别："+reglevelid);
					//调用接口获取挂号费用信息
					if(StringUtils.isNotBlank(reglevelid)){					
						String inputString=getAppInputParamString(reglevelid);
						//String inputString=getAppInputParamString(reglevelid);
						String operationName="getRegLevel";
						logger.error("医生排班信息查询接口【getScheduleInfo】-->【"+operationName+"】入参："+inputString);							
						inputString=CommonUtils.convertHisInputParam(operationName, inputString);
						HttpRequestService xmlRequest = HttpRequestService.getInstance();
						String ret= xmlRequest.request(inputString);	
						ret=CommonUtils.convertABSOutputParam(ret);					
						logger.error("医生排班信息查询接口【getScheduleInfo】-->【"+operationName+"】出参："+ret);
						String retCode=UtilXml.getValueByAllXml(ret, "STATE");
						if(StringUtils.isBlank(retCode)){
							continue;
						}
						if(Integer.parseInt(retCode)>0){
							document=DocumentHelper.parseText(ret);
							Element root2=document.getRootElement();		
							Element res2=root2.element("DATA");		
							List<Element> list2=res2.elements("RECORD");
							if(list2!=null){
								for(Element tmp:list2){
									String level=tmp.elementText("REGLEVELID");
									if(level.equals(reglevelid)){	
										String levelName=tmp.elementText("REGLEVELNAME");
										v3.setRegFee(CommonUtils.convertUnitToMinute(tmp.elementText("REGFEE"))+"");
										v3.setTreatFee(CommonUtils.convertUnitToMinute(tmp.elementText("TREATFEE"))+"");
										regInfoV3.setDoctorLevel(levelName);
										regInfoV3.setDoctorLevelCode(level);										
									}
								}
							}
							
						}
					}
					scheduleList.add(v3);
				}				
			}else if(regInfoV3==null&&StringUtils.isNotBlank(deptId)){
				regInfoV3=new RegInfoV3();
				regInfoV3.setDoctorId(deptId);
				String str=getDeptInfoInputParamString(deptId);
				String operationName="getDeptment";
				logger.error("医生排班信息查询接口【getScheduleInfo】-->【getDeptment】入参："+str);	
				str=CommonUtils.convertHisInputParam(operationName, str);
				HttpRequestService xmlRequest = HttpRequestService.getInstance();					
				String resValue= xmlRequest.request(str);	
				resValue=CommonUtils.convertABSOutputParam(resValue);				
				logger.error("医生排班信息查询接口【getScheduleInfo】-->【getDeptment】出参："+resValue);
				String resCode=UtilXml.getValueByAllXml(resValue, "STATE");
				if(StringUtils.isBlank(resValue)){
					continue;
				}
				String deptName="";
				if(Integer.parseInt(resCode)>0){					
					document=DocumentHelper.parseText(resValue);
					Element root2=document.getRootElement();		
					Element res2=root2.element("DATA");		
					List<Element> list2=res2.elements("RECORD");
					if(list2!=null){
						Element tmp=list2.get(0);
						deptName=tmp.elementText("DEPTNAME");
						regInfoV3.setDoctorName(deptName);
						regInfoV3.setDescription(tmp.elementText("DEPTINTRO"));
						regInfoV3.setDoctorLevel("");
						regInfoV3.setDoctorLevelCode("");
					}
				}
				List<ScheduleInfoV3> schedule=new ArrayList<ScheduleInfoV3>();
				regInfoV3.setScheduleInfoV3List(schedule);
				ScheduleInfoV3 v3=new ScheduleInfoV3();
				v3.setDeptId(deptId);
				v3.setDeptName(deptName);
				v3.setClinicUnitId(deptId);
				v3.setClinicUnitName(regInfoV3.getDoctorName());
				v3.setRegDate(ele.elementText("DATE"));
				v3.setStartTime(ele.elementText("STARTTIME"));
				v3.setEndTime(ele.elementText("ENDTIME"));				
				String noon=CommonUtils.covertNoonByCode(ele.elementText("AMFLAG"));				
				v3.setShiftCode(noon);
				v3.setShiftName(noon);
				v3.setRegStatus("1");
				v3.setIsTimeReg("1");
				String reglevelid=ele.elementText("REGLEVELID");
				v3.setScheduleId(ele.elementText("SHIFTID")+"@"+reglevelid);
				String count=ele.elementText("TOTALQUOTA");
				String used=ele.elementText("OCCQUOTA");
				logger.error("科室："+deptName+";挂号总数："+count+";已预约数量："+used);
				v3.setRegTotalCount(count);				
				if(StringUtils.isNotBlank(count)){
					if(StringUtils.isBlank(used)){
						v3.setRegLeaveCount(Integer.parseInt(count)+"");
					}else{
						v3.setRegLeaveCount((Integer.parseInt(count)-Integer.parseInt(used))+"");
					}
				}else{
					v3.setRegLeaveCount("0");
				}				
				logger.error("挂号级别："+reglevelid);
				//调用接口获取挂号费用信息
				if(StringUtils.isNotBlank(reglevelid)){					
					String inputString=getAppInputParamString("");
					//String inputString=getAppInputParamString(reglevelid);
					operationName="getRegLevel";
					logger.error("医生排班信息查询接口【getScheduleInfo】-->【"+operationName+"】入参："+inputString);							
					inputString=CommonUtils.convertHisInputParam(operationName, inputString);
					String ret= xmlRequest.request(inputString);	
					ret=CommonUtils.convertABSOutputParam(ret);					
					logger.error("医生排班信息查询接口【getScheduleInfo】-->【"+operationName+"】出参："+ret);
					String retCode=UtilXml.getValueByAllXml(ret, "STATE");
					if(StringUtils.isBlank(retCode)){
						continue;
					}
					if(Integer.parseInt(retCode)>0){
						document=DocumentHelper.parseText(ret);
						Element root2=document.getRootElement();		
						Element res2=root2.element("DATA");		
						List<Element> list2=res2.elements("RECORD");
						if(list2!=null){
							for(Element tmp:list2){
								String level=tmp.elementText("REGLEVELID");
								if(level.equals(reglevelid)){						
									String levelName=tmp.elementText("REGLEVELNAME");
									v3.setRegFee(CommonUtils.convertUnitToMinute(tmp.elementText("REGFEE"))+"");
									v3.setTreatFee(CommonUtils.convertUnitToMinute(tmp.elementText("TREATFEE"))+"");
									regInfoV3.setDoctorLevel(levelName);
									regInfoV3.setDoctorLevelCode(level);									
								}
							}
						}
					}					
				}		
				schedule.add(v3);
				deptInfoList.add(regInfoV3);
			}						
		}		
		resultV3.setResultCode("0");
		resultV3.setResultDesc("成功");
		resultV3.setRegInfoV3List(deptInfoList);		
		return resultV3;
	}
	
	/**
	 * V3入参转his入参
	 * @param req
	 * @return
	 */
	private String getDeptInfoInputParamString(String deptId){		
		StringBuilder str=new StringBuilder(100);
		str.append("<PARAMETER>");
		str.append("<DEPTID>"+deptId+"</DEPTID>");
		str.append("<DEPTNAME></DEPTNAME>");
		str.append("</PARAMETER>");
		return str.toString();
	}
}
