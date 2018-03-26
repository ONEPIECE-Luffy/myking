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
	 * 初始化操作
	 */
	private void init() {		
		try {
			JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
			client = dcf.createClient(wsdlAddress, new QName(nameSpace, serviceName));			
			//JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();			
			
			//client = dcf.createClient(wsdlAddress, new QName(nameSpace, "YXHISPlatformService"), new QName(nameSpace, "YXHISPlatformServiceHttpSoap11Endpoint"));
			//client = dcf.createClient(wsdlAddress);			
			//设置超时单位为毫秒
			HTTPConduit http = (HTTPConduit) client.getConduit();      
			HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();      
			httpClientPolicy.setConnectionTimeout(connectionTimeOut);  //连接超时    
			httpClientPolicy.setAllowChunking(false);    //取消块编码 
			httpClientPolicy.setReceiveTimeout(receiveTimeOut);     //响应超时
			http.setClient(httpClientPolicy);			
			client.getRequestContext().put("thread.local.request.context","true");
			client.getOutInterceptors().add(new org.apache.cxf.interceptor.LoggingOutInterceptor());
			client.getInInterceptors().add(new org.apache.cxf.interceptor.LoggingInInterceptor());			
		} catch (Exception e) {
			Throwable ta = e.getCause();
			logger.error("创建webService client失败！", e);
			if (ta instanceof SocketTimeoutException) {
				logger.error("响应超时...");
			} else if (ta instanceof HTTPException) {
				logger.error("服务端地址无效404...");
			} else if (ta instanceof XMLStreamException) {
				logger.error("连接超时...");
			}
			throw e;
		}		
	}
	
	/**
	 * 接口方法调用 
	 * @param params	参数
	 * @return
	 * @throws Exception 
	 */
	public String invoke(String operationName,Object... params) throws Exception{
		try {			
			Object[] objects=client.invoke(new QName(nameSpace, operationName), params);
			if(objects!=null){
				String result=objects[0].toString();
				logger.error("接口调用返回结果："+result);
				return result;
			}
		} catch (Exception e) {
			logger.error(operationName+"接口调用失败！",e);
			throw e;
		}
		return null;
	}

	/**
	 * 获取单例实例
	 * @return
	 */
	public static CxfDynamicClient getInstance(){
		return ClientInnerClass.cxfDynamicClient;
	}
	
	/**
	 * 内部类，通过内部类实现懒加载的母的
	 * @author tangfulin
	 */
	public static class ClientInnerClass{
		private static CxfDynamicClient cxfDynamicClient=new CxfDynamicClient();
	}
}
