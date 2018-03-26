package com.kingdeehit.mobile.his.xianggang.service.register;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingdeehit.mobile.his.entities.V3.result.appointment.RegInfoV3;
import com.kingdeehit.mobile.his.entities.V3.result.appointment.ScheduleInfoV3;
import com.kingdeehit.mobile.his.entities.V3.result.register.GetScheduleInfoTodayResultV3;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.DateUtils;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;

/**
 * ����ҽ��������Ϣ��ѯ
 * @author tangfulin
 *
 */
public class getScheduleInfo extends AbstractService{
	
	private static String hisInterface="querySchedules";
	

	@Override
	public String execute(String reqXml) throws Exception {		
		String inputString=getInputParamString(reqXml);
		logger.error("����Һ�ҽ���Ű���Ϣ��ѯ�ӿڡ�register.getScheduleInfo��-->��querySchedules����Σ�"+inputString);		
		inputString=CommonUtils.convertHisInputParam(hisInterface, inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();					
		String resultXml= xmlRequest.request(inputString);	
		resultXml=CommonUtils.convertABSOutputParam(resultXml);		
		logger.error("����Һ�ҽ���Ű���Ϣ��ѯ�ӿڡ�register.getScheduleInfo��-->��querySchedules�����Σ�"+resultXml);
		String resCode=UtilXml.getValueByAllXml(resultXml, "STATE");
		if(StringUtils.isBlank(resCode)||Integer.parseInt(resCode)<=0){
			String err=UtilXml.getValueByAllXml(resultXml, "ERR");
			logger.error("�������ʧ�ܣ�resCode="+resCode+";errorMsg="+err);
			return CommonUtils.getSuccessMsg();
		}				
		GetScheduleInfoTodayResultV3 resultParam=convertHisStringToV3Object(resultXml);			
		XStream stream = UtilXml.getXStream(GetScheduleInfoTodayResultV3.class);					
		return stream.toXML(resultParam);		
	}
	
	/**
	 * V3���תhis���
	 * @param req
	 * @return
	 */
	private String getInputParamString(String req){
		String deptId=UtilXml.getValueByAllXml(req, "deptId");		
		String startDate=DateUtils.getYMDTime(new Date());		
		StringBuilder str=new StringBuilder(100);
		str.append("<PARAMETER>");
		str.append("<DEPTID>"+deptId+"</DEPTID>");
		str.append("<STARTDATE>"+startDate+"</STARTDATE>");
		str.append("<DAYOFF>0</DAYOFF>");
		str.append("<AMFLAG></AMFLAG>");
		str.append("</PARAMETER>");
		return str.toString();
	}
	
	/**
	 * his�Һż���ӿڵ���V3���תhis���
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
	 * his����תV3����
	 * @param orderString
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private GetScheduleInfoTodayResultV3 convertHisStringToV3Object(String resultXml) throws Exception{		
		GetScheduleInfoTodayResultV3 resultV3=new GetScheduleInfoTodayResultV3();
		Document document=DocumentHelper.parseText(resultXml);
		Element root=document.getRootElement();		
		Element res=root.element("DATA");		
		List<Element> list=res.elements("RECORD");
		List<RegInfoV3> deptInfoList=new ArrayList<RegInfoV3>();
		HttpRequestService xmlRequest = HttpRequestService.getInstance();
		for(Element ele:list){			
			RegInfoV3 regInfoV3=null;
			String deptId=ele.elementText("DEPTID");	
			String date=ele.elementText("DATE");
			String startTime=ele.elementText("STARTTIME");
			String noon=CommonUtils.covertNoonByCode(ele.elementText("AMFLAG"));			
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
						if(v3.getShiftCode().equals(noon)){
							flag=true;					
							break;
						}					
					}
				}
				if(!flag){	
					String currentNoon=CommonUtils.getNoon(new Date());					
					//����Һ����粻�ܳ�������ĹҺż�¼
					if("����".equals(currentNoon)&&!noon.equals(currentNoon)){
						continue;
					}
					ScheduleInfoV3 v3=new ScheduleInfoV3();
					v3.setDeptId(deptId);
					v3.setDeptName(regInfoV3.getDoctorName());	
					v3.setClinicUnitId(deptId);
					v3.setClinicUnitName(regInfoV3.getDoctorName());
					v3.setRegDate(ele.elementText("DATE"));
					v3.setStartTime(ele.elementText("STARTTIME"));
					v3.setEndTime(ele.elementText("ENDTIME"));									
					v3.setShiftCode(noon);
					v3.setShiftName(noon);
					v3.setIsTimeReg("1");
					v3.setRegStatus("1");
					String reglevelid=ele.elementText("REGLEVELID");
					String scheduleId=ele.elementText("SHIFTID");
					v3.setScheduleId(ele.elementText("SHIFTID")+"@"+reglevelid+"@"+"1");
					String count=ele.elementText("TOTALQUOTA");
					String used=ele.elementText("OCCQUOTA");					
					String operationName="querySchedulesByShifId";
					String timeReqXml=getTimeInputParamString(scheduleId,deptId);				
					logger.error("ҽ���Ű���Ϣ��Դ���㡾register.getScheduleInfo��-->��"+operationName+"����Σ�"+timeReqXml);
					timeReqXml=CommonUtils.convertHisInputParam(operationName, timeReqXml);					
					String timeResultXml= xmlRequest.request(timeReqXml);	
					timeResultXml=CommonUtils.convertABSOutputParam(timeResultXml);	
					logger.error("ҽ���Ű���Ϣ��Դ���㡾register.getScheduleInfo��-->��"+operationName+"�����Σ�"+timeResultXml);
					int realCount=getScheduleNum(noon,timeResultXml);
					logger.error("��Դ������"+count+";������Դ������"+realCount);					
					v3.setRegTotalCount(count);				
					v3.setRegLeaveCount(realCount+"");										
					logger.error("�Һż���"+reglevelid);
					//���ýӿڻ�ȡ�Һŷ�����Ϣ
					if(StringUtils.isNotBlank(reglevelid)){					
						String inputString=getAppInputParamString(reglevelid);
						//String inputString=getAppInputParamString(reglevelid);
						operationName="getRegLevel";
						logger.error("ҽ���Ű���Ϣ�Һŷ��ýӿڵ��á�register.getScheduleInfo��-->��"+operationName+"����Σ�"+inputString);							
						inputString=CommonUtils.convertHisInputParam(operationName, inputString);						
						String ret= xmlRequest.request(inputString);	
						ret=CommonUtils.convertABSOutputParam(ret);					
						logger.error("ҽ���Ű���Ϣ�Һŷ��ýӿڵ��á�register.getScheduleInfo��-->��"+operationName+"�����Σ�"+ret);
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
				String currentNoon=CommonUtils.getNoon(new Date());
				logger.info("��ǰ���"+currentNoon+";his�Ű����"+noon);
				//����Һ����粻�ܳ�������ĹҺż�¼
				if("����".equals(currentNoon)&&!noon.equals(currentNoon)){
					continue;
				}				
				regInfoV3=new RegInfoV3();
				regInfoV3.setDoctorId(deptId);
				String str=getDeptInfoInputParamString(deptId);
				logger.error("ҽ���Ű���Ϣ������Ϣ��ѯ�ӿڵ��á�register.getScheduleInfo��-->��getDeptment����Σ�"+str);
				String operationName="getDeptment";
				str=CommonUtils.convertHisInputParam(operationName, str);								
				String resValue= xmlRequest.request(str);	
				resValue=CommonUtils.convertABSOutputParam(resValue);				
				logger.error("ҽ���Ű���Ϣ������Ϣ��ѯ�ӿڵ��á�register.getScheduleInfo��-->��getDeptment�����Σ�"+resValue);
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
				v3.setRegDate(ele.elementText("DATE"));
				v3.setStartTime(ele.elementText("STARTTIME"));
				v3.setEndTime(ele.elementText("ENDTIME"));							
				v3.setShiftCode(noon);
				v3.setShiftName(noon);
				v3.setIsTimeReg("1");
				v3.setRegStatus("1");
				String reglevelid=ele.elementText("REGLEVELID");
				String scheduleId=ele.elementText("SHIFTID");
				v3.setScheduleId(scheduleId+"@"+reglevelid+"@"+"1");
				String count=ele.elementText("TOTALQUOTA");
				String used=ele.elementText("OCCQUOTA");			
				//��ȡʵ�ʺ�Դ��
				operationName="querySchedulesByShifId";
				String timeReqXml=getTimeInputParamString(scheduleId,deptId);
				logger.error("ҽ���Ű���Ϣ��Դ���㡾register.getScheduleInfo��-->��"+operationName+"����Σ�"+timeReqXml);
				timeReqXml=CommonUtils.convertHisInputParam(operationName, timeReqXml);				
				String timeResultXml= xmlRequest.request(timeReqXml);	
				timeResultXml=CommonUtils.convertABSOutputParam(timeResultXml);	
				logger.error("ҽ���Ű���Ϣ��Դ���㡾register.getScheduleInfo��-->��"+operationName+"�����Σ�"+timeResultXml);
				int realCount=getScheduleNum(noon,timeResultXml);
				logger.error("����:"+deptName+";"+"�Һ�������"+count+";��ԤԼ������"+used+";������Դ������"+realCount);
				v3.setRegTotalCount(count);
				v3.setRegLeaveCount(realCount+"");						
				logger.error("�Һż���"+reglevelid);
				//���ýӿڻ�ȡ�Һŷ�����Ϣ
				if(StringUtils.isNotBlank(reglevelid)){					
					String inputString=getAppInputParamString(reglevelid);
					operationName="getRegLevel";
					logger.error("ҽ���Ű���Ϣ�Һŷ��ýӿڵ��á�register.getScheduleInfo��-->��"+operationName+"����Σ�"+inputString);										
					inputString=CommonUtils.convertHisInputParam(operationName, inputString);
					HttpRequestService xmlRequest3 = HttpRequestService.getInstance();
					String ret= xmlRequest3.request(inputString);	
					ret=CommonUtils.convertABSOutputParam(ret);					
					logger.error("ҽ���Ű���Ϣ�Һŷ��ýӿڵ��á�register.getScheduleInfo��-->��"+operationName+"�����Σ�"+ret);
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
					schedule.add(v3);
				}	
				deptInfoList.add(regInfoV3);
			}				
		}		
		resultV3.setResultCode("0");
		resultV3.setResultDesc("�ɹ�");
		resultV3.setRegInfoV3List(deptInfoList);		
		return resultV3;
	}
	
	/**
	 * V3�Ű��ʱ��Ϣhis���
	 * @param req
	 * @return
	 */
	private String getTimeInputParamString(String scheduleId,String deptId){	
		StringBuilder str=new StringBuilder(100);
		str.append("<PARAMETER>");
		str.append("<DEPTID>"+deptId+"</DEPTID>");
		str.append("<SHIFTID>"+scheduleId+"</SHIFTID>");
		str.append("</PARAMETER>");
		return str.toString();
	}
	
	/**
	 * ��ȡʵ��ԤԼ�Һŵ�����
	 * @param len
	 * @param regDate
	 * @param shiftCode
	 * @param resultXml
	 * @return
	 * @throws DocumentException
	 */
	@SuppressWarnings("unchecked")
	private int getScheduleNum(String shiftCode,String resultXml) throws DocumentException{		
		Document document=DocumentHelper.parseText(resultXml);
		Element root=document.getRootElement();		
		Element res=root.element("DATA");		
		List<Element> list=res.elements("RECORD");		
		String currentDate=DateUtils.getChineseTime();
		String yearMonthDay=DateUtils.getYMDTime(new Date());
		int count=0;
		for(Element ele:list){			
			String noon=CommonUtils.covertNoonByCode(ele.elementText("AMFLAG"));				
			String startTime=ele.elementText("STARTTIME");
			String scheduleDate=yearMonthDay+" "+startTime+":00";
			logger.info("��ǰʱ�䣺"+currentDate+";�Ű�ʱ�䣺"+scheduleDate);	
			if(noon.equals(shiftCode)){				
				//����Һ�ֻ�ܹҵ�ǰʱ���ʱ��εĺ�
				if(CommonUtils.compareDate(scheduleDate, currentDate)<0){
					continue;
				}	
				String total=ele.elementText("TOTALQUOTA");
				String leval=ele.elementText("OCCQUOTA");
				count+=(Integer.parseInt(total)-Integer.parseInt(leval));
			}
		}		
		return count;
	}
	
	/**
	 * V3���תhis���
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
