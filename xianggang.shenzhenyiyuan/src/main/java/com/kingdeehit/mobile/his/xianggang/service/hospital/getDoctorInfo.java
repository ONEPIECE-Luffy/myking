package com.kingdeehit.mobile.his.xianggang.service.hospital;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingdeehit.mobile.his.entities.V3.result.hospital.GetDoctorInfoResultItemV3;
import com.kingdeehit.mobile.his.entities.V3.result.hospital.GetDoctorInfoResultV3;
import com.kingdeehit.mobile.his.xianggang.constant.ParamConstants;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;
/**
 * 医生微首页,获取医生信息界面
 * @author tangfulin
 *
 */
public class getDoctorInfo extends AbstractService {
	
	private static String hisInterface="getDoctorInfo";

	@Override
	public String execute(String reqXml) throws Exception {		
		String inputString=getInputParamString(reqXml);				
		logger.error("医生信息查询接口【hospital.getDoctorInfo】-->【"+hisInterface+"】入参：user="+ParamConstants.USER+";password="+ParamConstants.PASSWORD+";parameter="+inputString);		
		inputString=CommonUtils.convertHisInputParam(hisInterface, inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();					
		String resultXml= xmlRequest.request(inputString);	
		resultXml=CommonUtils.convertABSOutputParam(resultXml);			
		logger.error("医生信息查询接口【hospital.getDoctorInfo】-->【"+hisInterface+"】出参："+resultXml);
		String resCode=UtilXml.getValueByAllXml(resultXml, "STATE");
		if(StringUtils.isBlank(resCode)||Integer.parseInt(resCode)<=0){
			String err=UtilXml.getValueByAllXml(resultXml, "MSG");
			logger.error("接口调用返回结果失败：resCode="+resCode+";msg="+err);
			return CommonUtils.getSuccessMsg();	
		}	
		GetDoctorInfoResultV3 resultParam=convertHisStringToV3Object(resultXml);			
		XStream xstream = UtilXml.getXStream(GetDoctorInfoResultV3.class);					
		return xstream.toXML(resultParam);
	}
	
	
	/**
	 * V3入参转his入参
	 * @param req
	 * @return
	 */
	private String getInputParamString(String req){
		String deptId=UtilXml.getValueByAllXml(req, "deptId");
		String doctorId=UtilXml.getValueByAllXml(req, "doctorId");		
		StringBuilder str=new StringBuilder(100);
		str.append("<PARAMETER>");
		str.append("<DEPTID>"+deptId+"</DEPTID>");
		str.append("<DOCID>"+doctorId+"</DOCID>");
		str.append("<DOCNAME></DOCNAME>");
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
	private GetDoctorInfoResultV3 convertHisStringToV3Object(String resultXml) throws DocumentException{		
		GetDoctorInfoResultV3 resultV3=new GetDoctorInfoResultV3();
		Document document=DocumentHelper.parseText(resultXml);
		Element root=document.getRootElement();		
		Element res=root.element("DATA");		
		List<Element> xmlList=res.elements("RECORD");
		List<GetDoctorInfoResultItemV3> list=new ArrayList<GetDoctorInfoResultItemV3>();
		for(Element ele:xmlList){
			GetDoctorInfoResultItemV3 result=new GetDoctorInfoResultItemV3();
			result.setDoctorId(ele.elementText("DOCID"));
			result.setDoctorName(ele.elementText("DOCNAME"));
			result.setDoctorLevelCode(ele.elementText("TITLEID"));
			result.setDoctorLevel(ele.elementText("TITLEID"));
			result.setDeptId(ele.elementText("DEPTID"));
			result.setDeptName("");
			result.setDescription(ele.elementText("DOCINTRO"));
			result.setPicture(ele.elementText("IMAGE"));
			list.add(result);
		}		
		resultV3.setResultCode("0");
		resultV3.setResultDesc("成功");
		resultV3.setList(list);		
		return resultV3;
	}
}
