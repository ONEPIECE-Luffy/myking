package com.kingdeehit.mobile.his.xianggang.service.support;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingdeehit.mobile.his.entities.V3.result.support.PageQueryOrderResultItemV3;
import com.kingdeehit.mobile.his.entities.V3.result.support.PageQueryOrderResultV3;
import com.kingdeehit.mobile.his.xianggang.service.AbstractService;
import com.kingdeehit.mobile.his.xianggang.service.util.CommonUtils;
import com.kingdeehit.mobile.his.xianggang.service.util.HttpRequestService;
import com.kingdeehit.mobile.utils.UtilXml;
import com.thoughtworks.xstream.XStream;

/**
 * ���˽ӿڲ�ѯ
 * @author tangfulin
 *
 */
public class pageQueryOrder extends AbstractService {

	private static String hisInterface="queryFinance";

	@Override
	public String execute(String reqXml) throws Exception {
		String inputString=getInputParamString(reqXml);
		logger.error("��ҳ��ѯ�ӿڡ�support.pageQueryOrder��-->��"+hisInterface+"����Σ�"+inputString);
		inputString=CommonUtils.convertHisInputParamWithOutUserInfo(hisInterface, inputString);
		HttpRequestService xmlRequest = HttpRequestService.getInstance();
		String resultXml= xmlRequest.request(inputString);
		resultXml=CommonUtils.convertHisOutputParam(resultXml);
		String resCode=UtilXml.getValueByAllXml(resultXml, "resultCode");//���������룺0-�ɹ�
		logger.error("��ҳ��ѯ�ӿڡ�support.pageQueryOrder��-->��"+hisInterface+"�����Σ�"+resultXml);
		if("-1".equals(resCode)){
			String errorMsg=UtilXml.getValueByAllXml(resultXml, "resultMessage");
			logger.error("�ӿڷ���ʧ�ܣ�resCode="+resCode+";errorMsg="+errorMsg);
			return CommonUtils.getSuccessMsg();
		}else if("1".equals(resCode)){
			logger.error("�ӿ�ִ�гɹ���û�ж�Ӧ���ݣ�");
			return CommonUtils.getSuccessMsg();
		}
		PageQueryOrderResultV3 resultV3=convertHisStringToV3Object(resultXml);
		XStream xstream = UtilXml.getXStream(PageQueryOrderResultV3.class);
		return xstream.toXML(resultV3);
	}

	/**
	 * his����ַ�������
	 * @param reqXml
	 * @return
	 */
	private String getInputParamString(String reqXml){
		String tradeDate=UtilXml.getValueByAllXml(reqXml, "tradeDate");//��������
		String hisOrdNum=UtilXml.getValueByAllXml(reqXml, "orderId");//�ƶ�֧��������
		StringBuilder str=new StringBuilder(200);
		str.append("<request>");
		str.append("<params>");
		str.append("<branchCode></branchCode>");
		str.append("<beginPayTime>"+tradeDate+" 00:00:00"+"</beginPayTime>");
		str.append("<endPayTime>"+tradeDate+" 23:59:59"+"</endPayTime>");
		str.append("<psOrdNum></psOrdNum>");
		str.append("<hisOrdNum>"+hisOrdNum+"</hisOrdNum>");
		str.append("<payMode>1</payMode>");//**
		str.append("</params>");
		str.append("</request>");
		return str.toString();
	}


	/**
	 * his����תV3����
	 * @param orderString
	 * @return
	 * @throws DocumentException
	 */
	@SuppressWarnings("unchecked")
	private PageQueryOrderResultV3 convertHisStringToV3Object(String resultXml) throws DocumentException{
		PageQueryOrderResultV3 resultV3=new PageQueryOrderResultV3();
		Document document=DocumentHelper.parseText(resultXml);
		Element element=document.getRootElement();
		Element res=element.element("result");
		List<Element> list=res.elements("item");
		List<PageQueryOrderResultItemV3> payInfoList=new ArrayList<PageQueryOrderResultItemV3>();
		for(Element ele:list){			
			String payMode=ele.elementText("payMode");
			String totalAmount=ele.elementText("totalAmount");
			String payTime=ele.elementText("payTime");
			if("1".equals(payMode)){
				PageQueryOrderResultItemV3 resultItem=new PageQueryOrderResultItemV3();
				resultItem.setOrderId(ele.elementText("psOrdNum"));
				resultItem.setHisOrderId(ele.elementText("hisOrdJournalNum"));
				resultItem.setTradeFee(totalAmount);
				resultItem.setTradeNo(ele.elementText("agtOrdNum"));
				resultItem.setTradeDate(payTime);
				resultItem.setCashFee(ele.elementText("payAmount"));
				resultItem.setInsuranFee(ele.elementText("insuranceAmount"));
				resultItem.setRemark("");
				resultItem.setPayMode("98");
				payInfoList.add(resultItem);
			}
		}
		resultV3.setResultCode("0");
		resultV3.setResultDesc("�ɹ�");
		resultV3.setList(payInfoList);
		return resultV3;
	}

}
