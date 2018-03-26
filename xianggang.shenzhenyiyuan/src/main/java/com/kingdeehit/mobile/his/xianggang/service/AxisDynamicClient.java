package com.kingdeehit.mobile.his.xianggang.service;

import javax.xml.namespace.QName;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.rpc.client.RPCServiceClient;

public class AxisDynamicClient {

	
	private static String namespaceURI="http://www.PKU-HIT.com/hie/dsg/service";	
	private static long timeOut=30*1000l;
	private String wsURL;
	
	public AxisDynamicClient(String wsdl){
		this.wsURL=wsdl;
	}
	
	public static AxisDynamicClient getInstance(String wsdl){
		return new AxisDynamicClient(wsdl);
	}

	public Object invokeWs(String method, Object[] parm, Class[] returnTypes)
			throws AxisFault {
		RPCServiceClient serviceClient  = new RPCServiceClient();
		MessageContext msgContext = new MessageContext();
		msgContext.setServiceContext(serviceClient.getServiceContext());
		MessageContext.setCurrentMessageContext(msgContext);
		Options options = new Options();
		options.setTimeOutInMilliSeconds(this.timeOut);// 不设则默认为30秒		
		options.setTo(new EndpointReference(wsURL));
		options.setAction("http://172.18.62.1:8061/dsg/service/labresultdetail/labresultdetail");
		serviceClient.setOptions(options);
		// 这里可以返回数组刚取数组，返回对象则取返回的第一个元素
		return serviceClient.invokeBlocking(new QName(namespaceURI, method),
				parm, returnTypes)[0];
	}

	

	public static void main(String[] args) throws AxisFault {
		AxisDynamicClient TClient = new AxisDynamicClient("");
		TClient.setWsURL("http://localhost:8080/webApp/services/tesTaskExcuteService");
		TClient.setNamespaceURI("http://webservice.asyntask.common.stat2");
		String methodName = "excuteTask";
		TClient.setTimeOut(5000l);
		Boolean result = (Boolean) TClient.invokeWs(methodName,
				new Object[] { new Long(123456) },
				new Class[] { boolean.class });
		System.out.println("result :" + result.toString());
	}

	public long getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(long timeOut) {
		this.timeOut = timeOut;
	}

	public String getWsURL() {
		return wsURL;
	}

	public void setWsURL(String wsURL) {
		this.wsURL = wsURL;
	}

	public String getNamespaceURI() {
		return namespaceURI;
	}

	public void setNamespaceURI(String namespaceURI) {
		this.namespaceURI = namespaceURI;
	}

}
