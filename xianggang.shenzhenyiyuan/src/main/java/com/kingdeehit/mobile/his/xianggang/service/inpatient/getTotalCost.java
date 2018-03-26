package com.kingdeehit.mobile.his.xianggang.service.inpatient;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingdeehit.mobile.his.entities.V3.result.inpatient.GetTotalCostItemV3;
import com.kingdeehit.mobile.his.entities.V3.result.inpatient.GetTotalCostResultV3;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;

/**
 * 住院费用汇总查询接口
 * @author tangfulin
 */
public class getTotalCost extends AbstractService {
	
	private static String hisInterface="getPerBedFee";

	@Override
	public String execute(String reqXml) throws Exception {
		String inputString=getInputParamString(reqXml);		
		logger.error("住院费用汇总查询接口【inpatient.getTotalCost】-->【"+hisInterface+"】入参："+inputString);
		inputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface, inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();					
		String resultXml= xmlRequest.request(inputString);	
		resultXml=CommonUtils.convertHisOutputParam(resultXml);		
		logger.error("住院费用汇总查询接口【inpatient.getTotalCost】-->【"+hisInterface+"】出参："+resultXml);
		String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");
		if("-1".equals(resCode)){
			String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
			logger.error("接口返回失败！resCode="+resCode+";errorMsg="+errorMsg);
			return CommonUtils.getSuccessMsg();
		}else if("1".equals(resCode)){			
			logger.error("接口执行成功但没有对应数据！");
			return CommonUtils.getSuccessMsg();
		}
		String inpatientId=UtilXml.getValueByAllXml(reqXml, "inpatientId");	
		GetTotalCostResultV3 resultV3=convertHisStringToV3Object(inpatientId,resultXml);						
		XStream xstream = UtilXml.getXStream(GetTotalCostResultV3.class);			
		return xstream.toXML(resultV3);		
	}
	

	/**
	 * 预约挂号his入参字符串构造
	 * @param reqXml
	 * @return
	 */
	private String getInputParamString(String reqXml){
		String inpatientId=UtilXml.getValueByAllXml(reqXml, "inpatientId");						
		StringBuilder str=new StringBuilder(200);
		str.append("<request><params>");
		str.append("<branchCode></branchCode>");
		str.append("<costDate></costDate>");
		str.append("<patientId>"+inpatientId+"</patientId>");
		str.append("<admissionNo>"+inpatientId+"</admissionNo>");
		str.append("<inTime>1</inTime>");		
		str.append("</params></request>");		
		return str.toString();
	}
	
	
	/**
	 * his出参转V3出参
	 * @param orderString
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private GetTotalCostResultV3 convertHisStringToV3Object(String patientId,String resultXml) throws Exception{		
		GetTotalCostResultV3 resultV3=new GetTotalCostResultV3();
		Document document=DocumentHelper.parseText(resultXml);
		Element element=document.getRootElement();		
		Element res=element.element("result");		
		List<Element> list=res.elements("item");		
		List<GetTotalCostItemV3> billList=new ArrayList<GetTotalCostItemV3>();
		List<GetTotalCostItemV3> dabaoList=new ArrayList<GetTotalCostItemV3>();
		/*if(list!=null&&list.size()>0){			
			for(Element tmp:list){
				GetTotalCostItemV3 billInfo=new GetTotalCostItemV3();
				billInfo.setTypeAmout(tmp.elementText("costAmout"));
				billInfo.setTypeCode(tmp.elementText("costType"));
				billInfo.setTypeName(tmp.elementText("costName"));
				billList.add(billInfo);
			}
		}*/
		//费用明细里面包含“打包...费”，只显示“打包...费”这一项，当不包含该项目，显示明细项目
		String money = "";
		if(list!=null&&list.size()>0){			
			for(Element tmp:list){
				String name = tmp.elementText("costName");
				GetTotalCostItemV3 billInfo=new GetTotalCostItemV3();
				billInfo.setTypeAmout(tmp.elementText("costAmout"));
				billInfo.setTypeCode(tmp.elementText("costType"));
				billInfo.setTypeName(tmp.elementText("costName"));
				if (name.contains("打包")) {
					money = tmp.elementText("costAmout");
					dabaoList.add(billInfo);
					break;
				}
				billList.add(billInfo);
			}
		}
		
	
		
		String inputString=getFeeInputParamString(patientId);
		String hisInterface="getBedFee";
		logger.error("【inpatient.getTotalCost】-->【"+hisInterface+"】入参："+inputString);
		inputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface, inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();					
		String result= xmlRequest.request(inputString);	
		result=CommonUtils.convertHisOutputParam(result);			
		logger.error("【inpatient.getTotalCost】-->【"+hisInterface+"】出参："+result);
		String resCode=UtilXml.getValueByAllXml(result, "resultCode");		
		if(!"0".equals(resCode)){
			String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
			logger.error("接口返回失败！resCode="+resCode+";errorMsg="+errorMsg);				
		}else{	
			Document doc=DocumentHelper.parseText(result);
			Element ele2=doc.getRootElement();		
			Element res2=ele2.element("result");				
			resultV3.setTotalAmout(res2.elementText("totalFee"));
			resultV3.setPrepayAmout(res2.elementText("payedFee"));			
			resultV3.setValidPrepayAmout(res2.elementText("payedFee"));
			resultV3.setUnsettled(res2.elementText("totalFee"));
		}
		//如果包含住院打包费
		if (dabaoList.size() > 0) {
			resultV3.setList(dabaoList);
			resultV3.setTotalAmout(money);
			resultV3.setUnsettled(money);
		} else {
			resultV3.setList(billList);
		}
		resultV3.setResultCode("0");
		resultV3.setResultDesc("成功");		
		return resultV3;
	}
	
	/**
	 * his入参字符串构造
	 * @param patientId
	 * @param admissionNo
	 * @return
	 */
	private String getFeeInputParamString(String patientId){		
		StringBuilder str=new StringBuilder(200);
		str.append("<request>");
		str.append("<params>");
		str.append("<branchCode></branchCode>");
		str.append("<patientId>"+patientId+"</patientId>");
		str.append("<admissionNo>"+patientId+"</admissionNo>");
		str.append("<inTime>1</inTime>");
		str.append("</params>");
		str.append("</request>");
		return str.toString();
	}
	
	/*public static void main(String[] args) {
		String xml = "<response><resultCode>0</resultCode><resultMessage>调用接口正常。</resultMessage><result><deptName>B5(东)骨科</deptName><bedno>110961</bedno><balance>-525700</balance><destination></destination><todayAmout>1025700</todayAmout><item><costType>097</costType><costName>住院病种打包费</costName><costAmout>320000</costAmout></item><item><costType>009</costType><costName>床位费</costName><costAmout>162000</costAmout></item><item><costType>082</costType><costName>取暖费</costName><costAmout>0</costAmout></item><item><costType>030</costType><costName>化验费</costName><costAmout>2500</costAmout></item><item><costType>006</costType><costName>诊疗费</costName><costAmout>541200</costAmout></item></result></response>";
		try {
			List<GetTotalCostItemV3> list = new getTotalCost().convertHisStringToV3Object("123",xml).getList();
			System.out.println(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
}
