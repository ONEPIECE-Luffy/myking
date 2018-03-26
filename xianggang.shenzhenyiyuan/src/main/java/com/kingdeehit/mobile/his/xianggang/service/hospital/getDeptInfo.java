package com.kingdeehit.mobile.his.xianggang.service.hospital;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingdeehit.mobile.his.entities.V3.result.appointment.DeptInfoV3;
import com.kingdeehit.mobile.his.entities.V3.result.appointment.GetDeptInfoResultV3;
import com.kingdeehit.mobile.his.xianggang.constant.ParamConstants;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;
/**
 * ҽԺ--������Ϣ��ѯ
 * @author tangfulin
 *
 */
public class getDeptInfo extends AbstractService {
	
	private static String hisInterface="getDeptment";

	@Override
	public String execute(String reqXml) throws Exception {
		String inputString=getInputParamString(reqXml);				
		logger.error("ҽԺ-���Ҳ�ѯ�ӿڡ�hospital.getDeptInfo��-->��getDeptment����Σ�user="+ParamConstants.USER+";password="+ParamConstants.PASSWORD+";parameter="+inputString);		
		inputString=CommonUtils.convertHisInputParam(hisInterface, inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();					
		String resultXml= xmlRequest.request(inputString);	
		resultXml=CommonUtils.convertABSOutputParam(resultXml);			
		logger.error("ҽԺ-���Ҳ�ѯ�ӿڡ�hospital.getDeptInfo��-->��getDeptment�����Σ�"+resultXml);
		String resCode=UtilXml.getValueByAllXml(resultXml, "STATE");
		if(StringUtils.isBlank(resCode)||Integer.parseInt(resCode)<=0){
			String err=UtilXml.getValueByAllXml(resultXml, "MSG");
			logger.error("�ӿڵ��÷��ؽ��ʧ�ܣ�resCode="+resCode+";msg="+err);
			return CommonUtils.getSuccessMsg();	
		}			
		GetDeptInfoResultV3 resultParam=convertHisStringToV3Object(resultXml);			
		XStream xstream = UtilXml.getXStream(GetDeptInfoResultV3.class);					
		return xstream.toXML(resultParam);		
	}
	
	/**
	 * V3���תhis���
	 * @param req
	 * @return
	 */
	private String getInputParamString(String req){
		String deptId=UtilXml.getValueByAllXml(req, "deptId");
		StringBuilder str=new StringBuilder(100);
		str.append("<PARAMETER>");
		str.append("<DEPTID>"+deptId+"</DEPTID>");
		str.append("<DEPTNAME></DEPTNAME>");
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
	private GetDeptInfoResultV3 convertHisStringToV3Object(String resultXml) throws DocumentException{		
		GetDeptInfoResultV3 resultV3=new GetDeptInfoResultV3();
		Document document=DocumentHelper.parseText(resultXml);
		Element root=document.getRootElement();		
		Element res=root.element("DATA");		
		List<Element> list=res.elements("RECORD");
		List<DeptInfoV3> deptInfoList=new ArrayList<DeptInfoV3>();
		for(Element ele:list){
			String flag=ele.elementText("FLAG");
			//6ֻ��ʾ΢��ԤԼ
			if("1".equals(flag)||"6".equals(flag)){
				DeptInfoV3 deptInfoV3=new DeptInfoV3();
				deptInfoV3.setDeptId(ele.elementText("DEPTID"));
				deptInfoV3.setDeptName(ele.elementText("DEPTNAME"));
				String parentId=ele.elementText("PARENTDEPTID");
				if(StringUtils.isBlank(parentId)||"0000".equals(parentId)){
					deptInfoV3.setParentId("-1");
				}else{
					deptInfoV3.setParentId(ele.elementText("PARENTDEPTID"));				
				}
				deptInfoV3.setDescription(ele.elementText("DEPTINTRO"));						
				deptInfoList.add(deptInfoV3);
			}
		}		
		resultV3.setResultCode("0");
		resultV3.setResultDesc("�ɹ�");
		resultV3.setDeptInfoList(deptInfoList);		
		return resultV3;
	}
}
