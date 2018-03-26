package com.kingdeehit.mobile.his.xianggang.service.outpatient;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingdeehit.mobile.his.entities.V3.result.outpatient.FeeInfo;
import com.kingdeehit.mobile.his.entities.V3.result.outpatient.GetPaybillfeeResultV3;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.StringUtil;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;

/**
 * ��ѯ��丶�Ѵ��ɷ���ϸ��Ϣ
 * @author tangfulin
 */
public class getPaybillfee extends AbstractService {

	private static String hisInterface="getMZFeeDetail";

	@Override
	public String execute(String reqXml) throws Exception {
		String res;
		try {
			String inputString=getInputParamString(reqXml);
			logger.error("��丶�Ѵ��ɷ���ϸ��Ϣ��outpatient.getPaybillfee��-->��"+hisInterface+"����Σ�"+inputString);
			inputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface, inputString);
			HttpRequestService xmlRequest = HttpRequestService.getInstance();
			String resultXml= xmlRequest.request(inputString);
			resultXml=CommonUtils.convertHisOutputParam(resultXml);
			logger.error("��丶�Ѵ��ɷ���ϸ��Ϣ��outpatient.getPaybillfee��-->��"+hisInterface+"�����Σ�"+resultXml);
			String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");//resultCode ���������룺0-�ɹ�
			if("-1".equals(resCode)){
				String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
				logger.error("�ӿڷ���ʧ�ܣ�resCode="+resCode+";errorMsg="+errorMsg);
				return CommonUtils.getErrorMsg("4202", "��ǰ�����ڽ��н���");
			}else if("1".equals(resCode)){
				logger.error("�ӿ�ִ�гɹ���û�ж�Ӧ���ݣ�resCode="+resCode);
				return CommonUtils.getErrorMsg("4202", "��ǰ�����ڽ��н���");
			}
			String settleCode=UtilXml.getValueByAllXml(reqXml, "settleCode");
			GetPaybillfeeResultV3 resultResult=null;
			if("00".equals(settleCode)){
				resultResult= convertHisStringToV3Object(resultXml);
			}else{
				String operationName="getMZInsurance";
				String insuranceInputString=getInsuranceInputParamString(reqXml);
				logger.error("��丶�Ѵ��ɷ���ϸ��Ϣ��outpatient.getPaybillfee��-->��"+operationName+"����Σ�"+insuranceInputString);
				//���û���His��ι���
				insuranceInputString=CommonUtils.convertHisInputParamWithOutUserInfo(operationName, insuranceInputString);
				HttpRequestService xmlRequest2 = HttpRequestService.getInstance();
				String insuranceXml= xmlRequest2.request(insuranceInputString);
				insuranceXml=CommonUtils.convertHisOutputParam(insuranceXml);
				logger.error("��丶�Ѵ��ɷ���ϸ��Ϣ��outpatient.getPaybillfee��-->��"+operationName+"�����Σ�"+insuranceXml);
				String resCode2=UtilXml.getValueByAllXml(insuranceXml, "resultCode");
				if("-1".equals(resCode2)){
					String errorMsg=UtilXml.getValueByAllXml(insuranceXml, "resultMessage");
					logger.error("�ӿڷ���ʧ�ܣ�resCode="+resCode2+";errorMsg="+errorMsg);
					return CommonUtils.getErrorMsg("4202", "��ǰ�����ڽ��н���");
				}else if("1".equals(resCode2)){
					logger.error("�ӿ�ִ�гɹ���û�ж�Ӧ���ݣ�resCode="+resCode2);
					return CommonUtils.getErrorMsg("4202", "��ǰ�����ڽ��н���");
				}
				resultResult = convertInsuranceHisStringToV3Object(insuranceXml,resultXml);
			}
			XStream xstream = UtilXml.getXStream(GetPaybillfeeResultV3.class);
			res = xstream.toXML(resultResult);
			res=res.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
			return res;
		} catch (Exception e) {
			logger.error(e);
			return CommonUtils.getErrorMsg("4202", "��ǰ�����ڽ��н���");
		}		
	}

	/**
	 * ԤԼ�Һ�his����ַ�������
	 * @param reqXml
	 * @return
	 */
	private String getInputParamString(String reqXml){
		String clinicSeq=UtilXml.getValueByAllXml(reqXml, "clinicSeq");
		String hospitalId=UtilXml.getValueByAllXml(reqXml, "hospitalId");
		String doctorId=UtilXml.getValueByAllXml(reqXml, "doctorId");
		String[] res=clinicSeq.split("@");
		//���磺������ˮ��@ҽ��ID@����ID@���ﵥ�ݺ�
		logger.error("ƴ�Ӿ�����ˮ��Ϊ��"+clinicSeq);

		StringBuilder str=new StringBuilder(200);
		str.append("<request><params>");
		str.append("<branchCode>"+hospitalId+"</branchCode>");
		str.append("<mzFeeId>"+res[0]+"</mzFeeId>");
		str.append("<deptCode>"+res[2]+"</deptCode>");
		str.append("<doctorCode>"+doctorId+"</doctorCode>");
		str.append("</params></request>");
		return str.toString();
	}

	/**
	 * ԤԼ�Һ�his����ַ�������
	 * @param reqXml
	 * @return
	 */
	private String getInsuranceInputParamString(String reqXml){
		String clinicSeq=UtilXml.getValueByAllXml(reqXml, "clinicSeq");
		String hospitalId=UtilXml.getValueByAllXml(reqXml, "hospitalId");
		String doctorId=UtilXml.getValueByAllXml(reqXml, "doctorId");
		String healthCardNo=UtilXml.getValueByAllXml(reqXml, "healthCardNo");
		String[] res=clinicSeq.split("@");
		//���磺������ˮ��@ҽ��ID@����ID@���ﵥ�ݺ�
		logger.error("ƴ�Ӿ�����ˮ��Ϊ��"+clinicSeq);

		StringBuilder str=new StringBuilder(200);
		str.append("<request><params>");
		str.append("<branchCode></branchCode>");
		str.append("<patCardType>1</patCardType>");
		str.append("<patCardNo>"+healthCardNo+"</patCardNo>");
		str.append("<mzFeeId>"+res[0]+"</mzFeeId>");
		str.append("<mzBillId>"+res[3]+"</mzBillId>");
		str.append("<deptCode>"+res[2]+"</deptCode>");
		str.append("<doctorCode>"+doctorId+"</doctorCode>");
		str.append("<insuranceSource></insuranceSource>");
		str.append("</params></request>");
		return str.toString();
	}

	/**
	 * his����תV3����
	 * @param resultXml
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private GetPaybillfeeResultV3 convertInsuranceHisStringToV3Object(String insuranceXml,String resultXml) throws Exception{
		Document insurDocument=DocumentHelper.parseText(insuranceXml);
		Element insurRoot=insurDocument.getRootElement();
		Element insurResult=insurRoot.element("result");
		GetPaybillfeeResultV3 resultV3=new GetPaybillfeeResultV3();
		resultV3.setResultCode("0");
		resultV3.setResultDesc("�ɹ�");
		if(insurResult==null){
			logger.error("getMZInsurance�ӿڵ��ó��η��������insuranceXml="+insuranceXml);
			return resultV3;
		}
		List<FeeInfo> infoList=new ArrayList<FeeInfo>();
		Document document=DocumentHelper.parseText(resultXml);
		Element root=document.getRootElement();
		Element res=root.element("result");
		List<Element> list=res.elements("item");
		StringBuilder str=new StringBuilder(200);
		for(Element ele:list){
			FeeInfo feeInfo=new FeeInfo();
			String itemId=ele.elementText("itemId");
			feeInfo.setTypeCode("<![CDATA["+itemId+"]]>");
			feeInfo.setTypeAmout("<![CDATA["+ele.elementText("itemTotalFee")+"]]>");
			feeInfo.setTypeName("<![CDATA["+ele.elementText("itemName")+"]]>");
			infoList.add(feeInfo);
			if(StringUtils.isNotBlank(itemId)){
				str.append(itemId+",");
			}
		}
		String cfd="";
		if(StringUtils.isNotBlank(str.toString())){
			cfd=str.substring(0, str.length()-1);
			logger.info("���ɷѴ�������Ϣ���ϣ�"+cfd);
		}
		resultV3.setPrescriptionIds(StringUtil.generateUuid());
		resultV3.setTotalPayAmout(insurResult.elementText("totalAmout"));
		resultV3.setPayAmout(insurResult.elementText("payAmout"));
		resultV3.setRecPayAmout(insurResult.elementText("insuranceAmout"));
		String medicareSettleLogId="{\"serial_no\":\""+insurResult.elementText("SSFeeNo")+"\",\"bill_no\":\""+insurResult.elementText("SSBillNo")+"\"}";
		resultV3.setMedicareSettleLogId(medicareSettleLogId);
		resultV3.setInfoList(infoList);
		return resultV3;
	}

	/**
	 * his����תV3����
	 * @param resultXml
	 * @return
	 * @throws DocumentException
	 */
	@SuppressWarnings("unchecked")
	private GetPaybillfeeResultV3 convertHisStringToV3Object(String resultXml) throws DocumentException{
		GetPaybillfeeResultV3 resultV3=new GetPaybillfeeResultV3();
		List<FeeInfo> infoList=new ArrayList<FeeInfo>();
		Document document=DocumentHelper.parseText(resultXml);
		Element root=document.getRootElement();
		Element res=root.element("result");
		List<Element> list=res.elements("item");
		StringBuilder str=new StringBuilder(200);
		int money=0;
		for(Element ele:list){
			FeeInfo feeInfo=new FeeInfo();
			feeInfo.setTypeCode("<![CDATA["+ele.elementText("itemId")+"]]>");
			feeInfo.setTypeAmout("<![CDATA["+ele.elementText("itemTotalFee")+"]]>");
			feeInfo.setTypeName("<![CDATA["+ele.elementText("itemName")+"]]>");
			infoList.add(feeInfo);
			money+=Integer.parseInt(ele.elementText("itemTotalFee"));
			String itemId=ele.elementText("itemId");
			if(StringUtils.isNotBlank(itemId)){
				str.append(itemId+",");
			}
		}
		String cfd="";
		if(StringUtils.isNotBlank(str.toString())){
			cfd=str.substring(0, str.length()-1);
			logger.info("���ɷѴ�������Ϣ���ϣ�"+cfd);
		}
		resultV3.setInfoList(infoList);
		resultV3.setTotalPayAmout(money+"");
		resultV3.setPayAmout(money+"");
		resultV3.setPrescriptionIds(StringUtil.generateUuid());
		resultV3.setRecPayAmout("0");
		resultV3.setResultCode("0");
		resultV3.setResultDesc("�ɹ�");
		return resultV3;
	}
}
