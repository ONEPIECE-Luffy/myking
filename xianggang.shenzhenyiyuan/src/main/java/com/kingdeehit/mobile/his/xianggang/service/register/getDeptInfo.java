package com.kingdeehit.mobile.his.xianggang.service.register;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingdeehit.mobile.his.entities.V3.result.appointment.DeptInfoV3;
import com.kingdeehit.mobile.his.entities.V3.result.register.GetDeptInfoTodayResultV3;
import com.kingdeehit.mobile.his.xianggang.constant.ParamConstants;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.ConfigUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;


/**
 * 当天门诊出诊科室信息查询
 * @author tangfulin
 *
 */
public class getDeptInfo extends AbstractService {
	
	private static String hisInterface="getDeptment";
	
	
	@Override
	public String execute(String reqXml) throws Exception {
		String inputString=getInputParamString(reqXml);		
		logger.error("当天挂号科室查询接口【register.getDeptInfo】-->【getDeptment】入参：user="+ParamConstants.USER+";password="+ParamConstants.PASSWORD+";parameter="+inputString);		
		inputString=CommonUtils.convertHisInputParam(hisInterface, inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();					
		String resultXml= xmlRequest.request(inputString);	
		resultXml=CommonUtils.convertABSOutputParam(resultXml);		
		logger.error("当天挂号科室查询接口【register.getDeptInfo】-->【getDeptment】出参："+resultXml);
		String resCode=UtilXml.getValueByAllXml(resultXml, "STATE");
		if(StringUtils.isBlank(resCode)||Integer.parseInt(resCode)<=0){
			String err=UtilXml.getValueByAllXml(resultXml, "MSG");
			logger.error("接口调用返回结果失败：resCode="+resCode+";msg="+err);
			return CommonUtils.getSuccessMsg();	
		}	
		GetDeptInfoTodayResultV3 resultParam=convertHisStringToV3Object(resultXml);			
		XStream xstream = UtilXml.getXStream(GetDeptInfoTodayResultV3.class);					
		return xstream.toXML(resultParam);
	}
	
	
	/**
	 * V3入参转his入参
	 * @param req
	 * @return
	 */
	private String getInputParamString(String req){
		//String deptId=UtilXml.getValueByAllXml(req, "deptId");
		StringBuilder str=new StringBuilder(100);
		str.append("<PARAMETER>");
		//str.append("<DEPTID>"+deptId+"</DEPTID>");
		//str.append("<DEPTNAME></DEPTNAME>");
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
	private GetDeptInfoTodayResultV3 convertHisStringToV3Object(String resultXml) throws DocumentException{		
		GetDeptInfoTodayResultV3 resultV3=new GetDeptInfoTodayResultV3();
		Document document=DocumentHelper.parseText(resultXml);
		Element root=document.getRootElement();		
		Element res=root.element("DATA");		
		List<Element> list=res.elements("RECORD");
		List<DeptInfoV3> deptInfoList=new ArrayList<DeptInfoV3>();
		Map<String,String> deptMap=new HashMap<String,String>();
		Map<String,String> deptDesMap=new HashMap<String,String>();
		Map<String,String> subDeptMap=new HashMap<String,String>();		//存在子科室的科室集合
		for(Element ele:list){
			String flag=ele.elementText("FLAG");			
			String parentId=ele.elementText("PARENTDEPTID");
			String insuranceflag=ele.elementText("INSURANCEFLAG");//是否医保
			//缓存map--key:deptId value:INSURANCEFLAG是否医保标识
			String deptIdToMap=ele.elementText("DEPTID");
			CommonUtils.cacheDeptInfo(deptIdToMap, insuranceflag);
			if(StringUtils.isNotBlank(flag)&&(flag.indexOf("6")>=0)){
				String deptId=ele.elementText("DEPTID");
				String deptName=ele.elementText("DEPTNAME");
				String deptDesc=ele.elementText("DEPTINTRO");					
				
				//如果是IMC科室，则屏蔽
				if (ConfigUtils.getInstance().getImcId().equals(deptId)) {
					continue;
				}
				DeptInfoV3 deptInfoV3=new DeptInfoV3();
				deptInfoV3.setDeptId(deptId);
				deptInfoV3.setDeptName(deptName);				
				if("0000".equals(parentId)){
					deptInfoV3.setParentId("-1");
					deptMap.put(deptId, deptName);
					deptDesMap.put(deptId, deptDesc);
				}else{
					deptInfoV3.setParentId(parentId);	
					subDeptMap.put(parentId, deptName);
				}
				deptInfoV3.setDescription(deptDesc);						
				deptInfoList.add(deptInfoV3);
			}
		}
		//对于不存在子科室的科室，虚拟出子科室，以便能进行预约挂号操作
		if(deptMap!=null&&deptMap.size()>0){
			Set<String> set=deptMap.keySet();
			Iterator<String> its=set.iterator();
			while(its.hasNext()){
				String key=its.next();
				if(!subDeptMap.containsKey(key)){
					DeptInfoV3 deptInfoV3=new DeptInfoV3();
					deptInfoV3.setDeptId(key);
					deptInfoV3.setDeptName(deptMap.get(key));
					deptInfoV3.setParentId(key);
					deptInfoV3.setDescription(deptDesMap.get(key));						
					deptInfoList.add(deptInfoV3);
				}
			}			
		}
		//新增几个子科室节点
		//CommonUtils.addDeptNode(deptInfoList, deptMap, deptDesMap);		
		//科室排序		
		Collections.sort(deptInfoList, new Comparator<DeptInfoV3>(){				
			public int compare(DeptInfoV3 o1, DeptInfoV3 o2) {	
				String o1DeptId=o1.getDeptId();
				String o2DeptId=o2.getDeptId();
				Integer o1Inter=Integer.parseInt(o1DeptId);
				Integer o2Inter=Integer.parseInt(o2DeptId);				
				if(o1Inter>o2Inter){
					return 1;
				}else if(o1Inter<o2Inter){
					return -1;
				}else{
					return 0;
				}
			}				
		});
		resultV3.setResultCode("0");
		resultV3.setResultDesc("成功");
		resultV3.setDeptInfoList(deptInfoList);		
		return resultV3;
	}
}
