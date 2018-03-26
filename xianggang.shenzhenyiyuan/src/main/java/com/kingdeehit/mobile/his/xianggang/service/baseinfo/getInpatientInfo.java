package com.kingdeehit.mobile.his.xianggang.service.baseinfo;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingdeehit.mobile.his.entities.V3.result.baseinfo.GetInpatientInfoResultV3;
import com.kingdeehit.mobile.his.entities.V3.result.baseinfo.InpatientInfoItemV3;
import com.kingdeehit.mobile.his.xianggang.constant.ParamConstants;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;


/**
 * סԺ������Ϣ��ѯ�ӿ�
 * @author tangfulin
 *
 */
public class getInpatientInfo extends AbstractService {

	private static String hisInterface="getBedRecords";

	@Override
	public String execute(String reqXml) throws Exception {		
		String inputString=getInputParamString(reqXml);
		logger.error("����סԺ��Ϣ��ѯ�ӿڡ�baseinfo.getInpatientInfo��-->��"+hisInterface+"����Σ�"+inputString);
		inputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface, inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();					
		String resultXml= xmlRequest.request(inputString);	
		resultXml=CommonUtils.convertHisOutputParam(resultXml);		
		logger.error("����סԺ��Ϣ��ѯ�ӿڡ�baseinfo.getInpatientInfo��-->��"+hisInterface+"�����Σ�"+resultXml);
		String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");		
		if("-1".equals(resCode)){
			String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
			logger.error("�ӿڷ���ʧ�ܣ�resCode="+resCode+";errorMsg="+errorMsg);
			return CommonUtils.getSuccessMsg();
		}else if("1".equals(resCode)){			
			logger.error("�ӿ�ִ�гɹ���û�ж�Ӧ���ݣ�");
			return CommonUtils.getSuccessMsg();
		}		
		String healthCardNo=UtilXml.getValueByAllXml(reqXml, "healthCardNo");
		GetInpatientInfoResultV3 resultResult= convertHisStringToV3Object(resultXml);							
		XStream xstream = UtilXml.getXStream(GetInpatientInfoResultV3.class);					
		return xstream.toXML(resultResult);	
	}
	
	/**
	 * his����ַ�������
	 * @param reqXml
	 * @return
	 */
	private String getInputParamString(String reqXml){		
		String healthCardNo=UtilXml.getValueByAllXml(reqXml, "healthCardNo");
		StringBuilder str=new StringBuilder(200);
		str.append("<request>");
		str.append("<params>");
		str.append("<branchCode></branchCode>");
		str.append("<patCardType>1</patCardType>");
		str.append("<patCardNo>"+healthCardNo+"</patCardNo>");
		str.append("<admissionNo></admissionNo>");
		str.append("</params>");
		str.append("</request>");
		return str.toString();
	}
	
	/**
	 * his����ַ�������
	 * @param patientId
	 * @param admissionNo
	 * @return
	 */
	private String getFeeInputParamString(String patientId,String admissionNo){		
		StringBuilder str=new StringBuilder(200);
		str.append("<request>");
		str.append("<params>");
		str.append("<branchCode></branchCode>");
		str.append("<patientId>"+patientId+"</patientId>");
		str.append("<admissionNo>"+admissionNo+"</admissionNo>");
		str.append("<inTime>1</inTime>");
		str.append("</params>");
		str.append("</request>");
		return str.toString();
	}
	
	
	/**
	 * his����תV3����
	 * @param orderString
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private GetInpatientInfoResultV3 convertHisStringToV3Object(String resultXml) throws Exception{		
		GetInpatientInfoResultV3 resultV3=new GetInpatientInfoResultV3();
		Document document=DocumentHelper.parseText(resultXml);
		Element element=document.getRootElement();		
		Element res=element.element("result");
		List<Element> list=res.elements("item");
		List<InpatientInfoItemV3> payInfoList=new ArrayList<InpatientInfoItemV3>();
		if(list!=null&&list.size()>0){
			Element ele=list.get(0);			
			InpatientInfoItemV3 resultItem=new InpatientInfoItemV3();	
			String patientId=ele.elementText("patientId");
			resultItem.setPatientName(ele.elementText("name"));				
			resultItem.setHospitalId(ParamConstants.BRANCHCODE);
			resultItem.setPatientId(patientId);			
			resultItem.setHospitalId("");			
			String rysj=ele.elementText("inDate");
			//String cysj=ele.attributeValue("DCYSJ");
			String cysj=ele.elementText("outDate");			
			resultItem.setInTime(rysj);
			resultItem.setOutTime(cysj);
			resultItem.setInDays(ele.elementText("InDays"));
			// 0:��Ժ 1:δ����2:��Ժ
			//��Ժ/Ԥ��Ժ/����/��Ժ/����
			String status=ele.elementText("status");
			if("0".equals(status)){
				resultItem.setPatientFlag("��Ժ");
			}else if("1".equals(status)){
				resultItem.setPatientFlag("��Ժ");
			}			
						
			String admissionNo=ele.elementText("admissionNo");
			resultItem.setHospitalId(ParamConstants.BRANCHCODE);
			resultItem.setInpatientId(patientId);
			resultItem.setDeptId(ele.elementText("deptCode"));
			resultItem.setDeptName(ele.elementText("deptName"));
			resultItem.setBedNo(ele.elementText("bedNo"));
			
			resultItem.setChargeDoctorId(ele.elementText("doctorCode"));
			resultItem.setChargeDoctorName(ele.elementText("doctorName"));
			resultItem.setChargeNurseId("");
			resultItem.setChargeNurseName("");
			resultItem.setGender("");
			
			String inputString=getFeeInputParamString(patientId,admissionNo);
			String hisInterface="getBedFee";
			logger.error("ԤԼ�Һ�֧���ӿڡ�baseinfo.getInpatientInfo��-->��"+hisInterface+"����Σ�"+inputString);
			inputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface, inputString);
			HttpRequestService xmlRequest = HttpRequestService.getInstance();					
			String result= xmlRequest.request(inputString);	
			result=CommonUtils.convertHisOutputParam(result);			
			logger.error("ԤԼ�Һ�֧���ӿڡ�baseinfo.getInpatientInfo��-->��"+hisInterface+"�����Σ�"+result);
			String resCode=UtilXml.getValueByAllXml(result, "resultCode");		
			if(!"0".equals(resCode)){
				String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
				logger.error("�ӿڷ���ʧ�ܣ�resCode="+resCode+";errorMsg="+errorMsg);				
			}else{	
				Document doc=DocumentHelper.parseText(result);
				Element ele2=doc.getRootElement();		
				Element res2=ele2.element("result");				
				resultItem.setTotalAmout(res2.elementText("totalFee"));
				resultItem.setPrepayAmout(res2.elementText("payedFee"));			
				//resultItem.setBalance("-"+res2.elementText("leftFee"));
				//his��Ԥ������㹫ʽ�����ǵ��෴  ������prepayAmout-totalAmout,his��totalAmout-prepayAmout,������Ҫ������ת��
				String balance = res2.elementText("leftFee");
				if (StringUtils.isNotBlank(balance) && balance.contains("-")) {
					balance = balance.replace("-", "");
				} else {
					balance = "-" + balance;
				}
				
				resultItem.setBalance(balance);
				resultItem.setSettled("");
			}			
			payInfoList.add(resultItem);		
		}
		resultV3.setResultCode("0");
		resultV3.setResultDesc("�ɹ�");
		resultV3.setRecordInfo(payInfoList);		
		return resultV3;
	}
	
	/**
	 * mongodb���ݱ������
	 * @param resultXml
	 * @throws DocumentException 
	 * @throws ParseException 
	 */
	private void insertMongodb(String resultXml) throws Exception{		
		Document document=DocumentHelper.parseText(resultXml);
		Element element=document.getRootElement();		
		Element res=element.element("RES");		
		Element rows=res.element("Rows");
		List<Element> list=rows.elements("Row");
		List<InpatientInfoItemV3> payInfoList=new ArrayList<InpatientInfoItemV3>();
		String ghsjTemp="";
		Element target=null;
		for(Element ele:list){
			if(StringUtils.isNotBlank(ghsjTemp)){
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date1=format.parse(ghsjTemp);
				Date date2=format.parse(ele.attributeValue("DRYSJ"));
				if(date2.after(date1)){
					ghsjTemp=ele.attributeValue("DRYSJ");
					target=ele;
				}				
			}else{
				ghsjTemp=ele.attributeValue("DRYSJ");
				target=ele;
			}			
		}		
		if(target!=null){
//			ChannelInpatientInfo inpatientInfo=new ChannelInpatientInfo();
//			//inpatientInfo.setClinic(target.attributeValue("CZYH"));
//			inpatientInfo.setStartTime(target.attributeValue("DRYSJ"));
//			inpatientInfo.setDeptId(target.attributeValue("IZYKS"));
//			inpatientInfo.setDeptName(target.attributeValue("CZYKS"));
//			inpatientInfo.setDoctorId(target.attributeValue("IZYYS"));
//			inpatientInfo.setDoctorName(target.attributeValue("CZYYS"));
//			inpatientInfo.setCardNo(target.attributeValue("CSFZH"));
//			inpatientInfo.setFee(target.attributeValue("MSJFY"));
//			//inpatientInfo.setName(target.attributeValue("CXM"));
//			inpatientInfo.setEndTime(target.attributeValue("DCYSJ"));
//			inpatientInfo.set_id(StringUtil.generateUuid());
//			
//			Bson filter = Filters.and(Filters.eq("clinic", inpatientInfo.getClinic()));
//			List<org.bson.Document> recordList=new MongoDBHelper("channel_inpatient_Info").query(filter);			
//			if(recordList==null||recordList.size()==0){				
//				org.bson.Document doc = org.bson.Document.parse(JSONObject.fromObject(inpatientInfo).toString());
//				new MongoDBHelper("channel_inpatient_Info").insert(doc);	
//				logger.error("mongodb����סԺ��¼�ɹ���");
//			}else{
//				org.bson.Document tmp=recordList.get(0);
//				inpatientInfo.set_id(tmp.getString("_id"));
//				org.bson.Document doc = org.bson.Document.parse(JSONObject.fromObject(inpatientInfo).toString());
//				new MongoDBHelper("channel_inpatient_Info").updateById(inpatientInfo.get_id(), doc);
//			}
		}
	}

}
