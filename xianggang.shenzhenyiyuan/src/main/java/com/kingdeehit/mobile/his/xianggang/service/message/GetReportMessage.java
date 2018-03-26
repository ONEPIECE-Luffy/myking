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
 * ����Ϣ����ƽ̨���ýӿڣ����ͼ��鱨����
 * @author yuan
 * @date 2017��7��19�� ����9:57:35
 */
public class GetReportMessage  extends AbstractService {

	
	
	@Override
	public String execute(String reqXml) throws Exception {
	
		String hisInterface = null;
	    String his_Server_url = null;
		String inputString = null;
		String time = UtilXml.getValueByAllXml(reqXml, "time");
		String eventType = UtilXml.getValueByAllXml(reqXml, "eventType");
		
		logger.error("���ͼ��鱨�����ӿڡ�message.GetReportMessage�� ��Σ�"+reqXml);
		
		//�������������ͬ�����ò�ͬ�Ľӿ�
		if("pacsReportCompleted".equals(eventType)){
			hisInterface="jindieexamreport";
			his_Server_url = Const.HIS_SERVICE_URL1;
			inputString=CommonUtils.convertPacsReportCompletedHisInputString(time);	//��ƽ̨����Σ�ת����Hisϵͳ�������
		}else if("lisReportCompleted".equals(eventType)){
			hisInterface="jindielabreport";
			his_Server_url = Const.HIS_SERVICE_URL1;
			inputString=CommonUtils.convertLisReportCompletedHisInputString(time);//��ƽ̨����Σ�ת����Hisϵͳ�������
		}else{
			//���أ��޷�����û�ж�Ӧ������
			logger.error("����eventType��  "+eventType+" �޷�������");
			return CommonUtils.getErrorMsg(" �޷�������");
		}
	
		logger.error("���ͼ��鱨�����ӿڡ�message.GetReportMessage��-->��"+hisInterface+"����Σ�"+inputString);		
		
		HttpRequestService xmlRequest = HttpRequestService.getInstance(his_Server_url);					
	
		String resultXml= xmlRequest.request(inputString);		//����Http����
	
		resultXml= CommonUtils.convertHisOutputParam(resultXml);	
		
		logger.error("���ͼ��鱨�����ӿڡ�message.GetReportMessage��-->��"+hisInterface+"�����Σ�"+resultXml);		
	
		String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");
		String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
	
		if("-1".equals(resCode)){
			logger.error("�ӿڷ���ʧ�ܣ�resCode="+resCode+";errorMsg="+errorMsg);
			return CommonUtils.getSuccessMsg();
		}else if("1".equals(resCode)){			
			logger.error("�ӿ�ִ�гɹ���û�ж�Ӧ���ݣ�");
			return CommonUtils.getSuccessMsg();
		}else{	//�ж�����ӿڣ�����ɹ������أ����ʧ�ܷ�װ���Լ���״̬��������
			 String resultV3=  convertHisNotifyParam(resCode,errorMsg,resultXml);//�����ز�������ת���ɽ����׼�ӿڵ���ʽ	
			 logger.error("�ӿ�ִ�гɹ�,�����Ѿ��ɹ����أ����ص�������   "+resultV3);
			 return resultV3;		 
		}
	}
	/**
	 * ��Hisϵͳ�����ֶΣ�ת���ɽ�����ͽӿڿ���ʶ����ֶΡ���ʽ
	 * @author YJB
	 * @date 2017��7��19�� ����11:47:32
	 * @param outputString
	 * @param resultXml 
	 * @param errorMsg 
	 * @return
	 * @throws DocumentException 
	 */
	public String convertHisNotifyParam(String resultCode,String resultMessage,String outputString) throws DocumentException {
 
		//�����ӿڵĳ���
		Document document = DocumentHelper.parseText(outputString);
		//��ȡ�����нڵ�
		Element root=document.getRootElement();		
		List<Element> list= root.elements("item");	
		
		
		//�½�һ��xml��Ϊ����
		Document resultXML = DocumentHelper.createDocument(); 
		
		//������뿪ʼ�����XML��������
		Element resultRoot = resultXML.addElement("res");
		Element resultDesc = resultRoot.addElement("resultMessage");
		Element resultResultCode = resultRoot.addElement("resultCode");
		//�������XML���ı��ڵ�
		resultDesc.setText(resultCode); 
		resultResultCode.setText(resultMessage);
		
		for (Element element : list) {
			Element event = resultRoot.addElement("event");
		    //�����ǽڵ�event������
			Element eventNo = event.addElement("eventNo");
			Element eventType = event.addElement("eventType");
			Element eventData = event.addElement("eventData");
		    //�����ǽڵ�eventData �е��ӽڵ�
			Element examedate  = eventData.addElement("exameDate");
			Element reportid  = eventData.addElement("reportId");
			Element reporttitle  = eventData.addElement("reportTitle");
			Element patientid  = eventData.addElement("patientId");
			Element reportdate  = eventData.addElement("reportDate");
			Element clinicseq  = eventData.addElement("clinicSeq");
			Element healthcardno  = eventData.addElement("healthCardNo");
			Element inpatientid  = eventData.addElement("inpatientId");
			
			//�����Ǹ��ڵ�event������ӽڵ��� ����ı��ڵ�
			eventNo.addText(element.elementText("eventno"));
			eventType.addText(element.elementText("eventtype"));
			
			//�����ǽڵ�eventData������ӽڵ��� ����ı��ڵ�
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
