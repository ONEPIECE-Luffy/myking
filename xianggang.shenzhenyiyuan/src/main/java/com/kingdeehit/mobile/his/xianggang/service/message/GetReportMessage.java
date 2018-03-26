package com.kingdeehit.mobile.his.xianggang.service.message;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingdeehit.mobile.his.xianggang.constant.Const;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.UtilXml;

/** 
 * 供消息推送平台调用接口，推送检验报告结果
 * @author yuan
 * @date 2017年7月19日 上午9:57:35
 */
public class GetReportMessage  extends AbstractService {

	
	
	@Override
	public String execute(String reqXml) throws Exception {
	
		String hisInterface = null;
	    String his_Server_url = null;
		String inputString = null;
		String time = UtilXml.getValueByAllXml(reqXml, "time");
		String eventType = UtilXml.getValueByAllXml(reqXml, "eventType");
		
		logger.error("推送检验报告结果接口【message.GetReportMessage】 入参："+reqXml);
		
		//根据请求参数不同，调用不同的接口
		if("pacsReportCompleted".equals(eventType)){
			hisInterface="jindieexamreport";
			his_Server_url = Const.HIS_SERVICE_URL1;
			inputString=CommonUtils.convertPacsReportCompletedHisInputString(time);	//将平台的入参，转换成His系统的入参数
		}else if("lisReportCompleted".equals(eventType)){
			hisInterface="jindielabreport";
			his_Server_url = Const.HIS_SERVICE_URL1;
			inputString=CommonUtils.convertLisReportCompletedHisInputString(time);//将平台的入参，转换成His系统的入参数
		}else{
			//返回，无法处理，没有对应处理方法
			logger.error("请求eventType是  "+eventType+" 无法处理！！");
			return CommonUtils.getErrorMsg(" 无法处理！！");
		}
	
		logger.error("推送检验报告结果接口【message.GetReportMessage】-->【"+hisInterface+"】入参："+inputString);		
		
		HttpRequestService xmlRequest = HttpRequestService.getInstance(his_Server_url);					
	
		String resultXml= xmlRequest.request(inputString);		//进行Http请求
	
		resultXml= CommonUtils.convertHisOutputParam(resultXml);	
		
		logger.error("推送检验报告结果接口【message.GetReportMessage】-->【"+hisInterface+"】出参："+resultXml);		
	
		String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");
		String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
	
		if("-1".equals(resCode)){
			logger.error("接口返回失败！resCode="+resCode+";errorMsg="+errorMsg);
			return CommonUtils.getSuccessMsg();
		}else if("1".equals(resCode)){			
			logger.error("接口执行成功但没有对应数据！");
			return CommonUtils.getSuccessMsg();
		}else{	//判断请求接口，如果成功，返回，如果失败封装成自己的状态参数返回
			 String resultV3=  convertHisNotifyParam(resCode,errorMsg,resultXml);//将返回参数进行转换成金蝶标准接口的形式	
			 logger.error("接口执行成功,数据已经成功返回！返回的数据是   "+resultV3);
			 return resultV3;		 
		}
	}
	/**
	 * 将His系统返回字段，转换成金蝶推送接口可以识别的字段、格式
	 * @author YJB
	 * @date 2017年7月19日 上午11:47:32
	 * @param outputString
	 * @param resultXml 
	 * @param errorMsg 
	 * @return
	 * @throws DocumentException 
	 */
	public String convertHisNotifyParam(String resultCode,String resultMessage,String outputString) throws DocumentException {
 
		//解析接口的出参
		Document document = DocumentHelper.parseText(outputString);
		//获取出参中节点
		Element root=document.getRootElement();		
		List<Element> list= root.elements("item");	
		
		
		//新建一个xml作为出参
		Document resultXML = DocumentHelper.createDocument(); 
		
		//下面代码开始对输出XML进行配置
		Element resultRoot = resultXML.addElement("res");
		Element resultDesc = resultRoot.addElement("resultMessage");
		Element resultResultCode = resultRoot.addElement("resultCode");
		//增加输出XML的文本节点
		resultDesc.setText(resultCode); 
		resultResultCode.setText(resultMessage);
		
		for (Element element : list) {
			Element event = resultRoot.addElement("event");
		    //下面是节点event的数据
			Element eventNo = event.addElement("eventNo");
			Element eventType = event.addElement("eventType");
			Element eventData = event.addElement("eventData");
		    //下面是节点eventData 中的子节点
			Element examedate  = eventData.addElement("exameDate");
			Element reportid  = eventData.addElement("reportId");
			Element reporttitle  = eventData.addElement("reportTitle");
			Element patientid  = eventData.addElement("patientId");
			Element reportdate  = eventData.addElement("reportDate");
			Element clinicseq  = eventData.addElement("clinicSeq");
			Element healthcardno  = eventData.addElement("healthCardNo");
			Element inpatientid  = eventData.addElement("inpatientId");
			
			//下面是给节点event下面的子节点中 添加文本节点
			eventNo.addText(element.elementText("eventno"));
			eventType.addText(element.elementText("eventtype"));
			
			//下面是节点eventData下面的子节点中 添加文本节点
			examedate.addText(element.elementText("examedate"));
			reportid.addText(element.elementText("reportid"));
			reporttitle.addText(element.elementText("reporttitle"));
			patientid.addText(element.elementText("patientid"));
			reportdate.addText(element.elementText("reportdate"));
			clinicseq.addText(element.elementText("clinicseq"));
			healthcardno.addText(element.elementText("healthcardno"));
			inpatientid.addText(element.elementText("inpatientid"));
			
		}
		
		return resultRoot.asXML().toString();
	
	
	}

	 
}
