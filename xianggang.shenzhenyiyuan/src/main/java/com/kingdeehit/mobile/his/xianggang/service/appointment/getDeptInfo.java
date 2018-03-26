package com.kingdeehit.mobile.his.xianggang.service.appointment;
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
import com.kingdeehit.mobile.his.entities.V3.result.appointment.GetDeptInfoResultV3;
import com.kingdeehit.mobile.his.xianggang.constant.ParamConstants;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.ConfigUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;


/**
 * ���Ҳ�ѯ�ӿ�
 * @author tangfulin
 *
 */
public class getDeptInfo extends AbstractService {
	
	private static String hisInterface="getDeptment";
	
	@Override
	public String execute(String reqXml) throws Exception {		
		String inputString=getInputParamString(reqXml);		
		String convertInputString=CommonUtils.convertHisInputParam(hisInterface, inputString);
		logger.error("ԤԼ�Һſ��Ҳ�ѯ�ӿڡ�appointment.getDeptInfo��-->��"+hisInterface+"����Σ�user="+ParamConstants.USER+";password="+ParamConstants.PASSWORD+";parameter="+inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();					
		String resultXml= xmlRequest.request(convertInputString);	
		resultXml=CommonUtils.convertABSOutputParam(resultXml);		
		logger.error("ԤԼ�Һſ��Ҳ�ѯ�ӿڡ�appointment.getDeptInfo��-->��"+hisInterface+"�����Σ�"+resultXml);
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
		//String deptId=UtilXml.getValueByAllXml(req, "deptId");
		StringBuilder str=new StringBuilder(100);
		str.append("<PARAMETER>");
		//str.append("<DEPTID>10100203</DEPTID>");
		//str.append("<DEPTNAME></DEPTNAME>");
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
		Map<String,String> deptMap=new HashMap<String,String>();
		Map<String,String> deptDesMap=new HashMap<String,String>();
		Map<String,String> subDeptMap=new HashMap<String,String>();		//�����ӿ��ҵĿ��Ҽ���
		for(Element ele:list){
			String flag=ele.elementText("FLAG");
			String parentId=ele.elementText("PARENTDEPTID");
			
			String insuranceflag=ele.elementText("INSURANCEFLAG");
			String deptIdToMap=ele.elementText("DEPTID");
			//����map--key:deptId value:INSURANCEFLAG�Ƿ�ҽ����ʶ
			CommonUtils.cacheDeptInfo(deptIdToMap, insuranceflag);
			
			//6ֻ��ʾ΢��ԤԼ
			if(StringUtils.isNotBlank(flag)&&(flag.indexOf("6")>=0)){
				String deptId=ele.elementText("DEPTID");
				String deptName=ele.elementText("DEPTNAME");
				//�����IMC���ң�������
				if (ConfigUtils.getInstance().getImcId().equals(deptId)) {
					continue;
				}
				String deptDesc=ele.elementText("DEPTINTRO");				
				
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
		//���ڲ������ӿ��ҵĿ��ң�������ӿ��ң��Ա��ܽ���ԤԼ�ҺŲ���
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
		//���������ӿ��ҽڵ�
		//CommonUtils.addDeptNode(deptInfoList, deptMap, deptDesMap);	
		//��������		
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
		resultV3.setResultDesc("�ɹ�");
		resultV3.setDeptInfoList(deptInfoList);		
		return resultV3;
	}
	/*public static void main(String[] args) {
		System.out.println("ddddd   "+ConfigUtils.getInstance().getImcId());
	}*/
}
