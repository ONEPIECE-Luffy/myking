package com.kingdeehit.mobile.his.xianggang.service;

import java.net.SocketTimeoutException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transport.http.HTTPException;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.log4j.Logger;

import com.kingdeehit.mobile.his.utils.Consts;

public class CxfDynamicClient {
	
	private static Logger logger = Logger.getLogger(CxfDynamicClient.class);
	
	private Client client;
	
	private static String nameSpace="http://www.sinodata.net.cn/AlipayService";
	private static String serviceName="AlipayService";
	private static String wsdlAddress=Consts.HIS_SERVICE_URL;
	
	private static long connectionTimeOut=10*1000l;
	private static long receiveTimeOut=60*1000l;
	 

	private CxfDynamicClient(){
		init();
	}
	
	/**
	 * ��ʼ������
	 */
	private void init() {		
		try {
			JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
			client = dcf.createClient(wsdlAddress, new QName(nameSpace, serviceName));			
			//JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();			
			
			//client = dcf.createClient(wsdlAddress, new QName(nameSpace, "YXHISPlatformService"), new QName(nameSpace, "YXHISPlatformServiceHttpSoap11Endpoint"));
			//client = dcf.createClient(wsdlAddress);			
			//���ó�ʱ��λΪ����
			HTTPConduit http = (HTTPConduit) client.getConduit();      
			HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();      
			httpClientPolicy.setConnectionTimeout(connectionTimeOut);  //���ӳ�ʱ    
			httpClientPolicy.setAllowChunking(false);    //ȡ������� 
			httpClientPolicy.setReceiveTimeout(receiveTimeOut);     //��Ӧ��ʱ
			http.setClient(httpClientPolicy);			
			client.getRequestContext().put("thread.local.request.context","true");
			client.getOutInterceptors().add(new org.apache.cxf.interceptor.LoggingOutInterceptor());
			client.getInInterceptors().add(new org.apache.cxf.interceptor.LoggingInInterceptor());			
		} catch (Exception e) {
			Throwable ta = e.getCause();
			logger.error("����webService clientʧ�ܣ�", e);
			if (ta instanceof SocketTimeoutException) {
				logger.error("��Ӧ��ʱ...");
			} else if (ta instanceof HTTPException) {
				logger.error("����˵�ַ��Ч404...");
			} else if (ta instanceof XMLStreamException) {
				logger.error("���ӳ�ʱ...");
			}
			throw e;
		}		
	}
	
	/**
	 * �ӿڷ������� 
	 * @param params	����
	 * @return
	 * @throws Exception 
	 */
	public String invoke(String operationName,Object... params) throws Exception{
		try {			
			Object[] objects=client.invoke(new QName(nameSpace, operationName), params);
			if(objects!=null){
				String result=objects[0].toString();
				logger.error("�ӿڵ��÷��ؽ����"+result);
				return result;
			}
		} catch (Exception e) {
			logger.error(operationName+"�ӿڵ���ʧ�ܣ�",e);
			throw e;
		}
		return null;
	}

	/**
	 * ��ȡ����ʵ��
	 * @return
	 */
	public static CxfDynamicClient getInstance(){
		return ClientInnerClass.cxfDynamicClient;
	}
	
	/**
	 * �ڲ��࣬ͨ���ڲ���ʵ�������ص�ĸ��
	 * @author tangfulin
	 */
	public static class ClientInnerClass{
		private static CxfDynamicClient cxfDynamicClient=new CxfDynamicClient();
	}
}
