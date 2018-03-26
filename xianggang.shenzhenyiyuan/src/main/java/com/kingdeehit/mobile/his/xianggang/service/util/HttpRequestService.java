package com.kingdeehit.mobile.his.xianggang.service.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

import com.kingdeehit.mobile.his.utils.Consts;

public class HttpRequestService {
	
	private static Logger logger=Logger.getLogger(HttpRequestService.class);
	
	private int connectTimeout;
	private int readTimeout;
	
	private String wsdlAddress;

	public HttpRequestService(String wsdl){
		wsdlAddress=wsdl;
		connectTimeout = 30000;
		readTimeout = 60000;		
	}
	
	public static HttpRequestService getInstance(){		
		return new HttpRequestService(Consts.HIS_SERVICE_URL);
	}
	

	public static HttpRequestService getInstance(String wsdl){
		return new HttpRequestService(wsdl);
	}

	
	/**
	 * �ӿڵ���
	 * @param requestContent
	 * @return
	 * @throws Exception
	 */
	public String request(String requestContent) throws Exception {		
		String result = null;		
		URL url = new URL(wsdlAddress);
		logger.info("�ӿڵ�ַ��"+wsdlAddress);
		HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
		//���ӳ�ʱ��Ϊ30�룬��ȡ���ݳ�ʱ��Ϊ60��
		httpUrlConn.setConnectTimeout(connectTimeout);
		httpUrlConn.setReadTimeout(readTimeout);
		httpUrlConn.setDoOutput(true);
		httpUrlConn.setDoInput(true);
		httpUrlConn.setUseCaches(false);
		httpUrlConn.setRequestProperty("Content-type","application/x-java-serialized-object");
		// �趨����ķ���Ϊ"POST"��Ĭ����GET
		httpUrlConn.setRequestMethod("POST");
		InputStream inputStream = null;
		OutputStream outputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		try {
			if (null != requestContent) {
				outputStream = httpUrlConn.getOutputStream();
				// ע������ʽ����ֹ��������
				outputStream.write(requestContent.getBytes("UTF-8"));				
			}
			// �����ص�������ת�����ַ���
			inputStream = httpUrlConn.getInputStream();
			StringBuffer buffer = new StringBuffer();
			inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				int firstLt=str.indexOf("&lt;");
				int firstGt=str.indexOf("&gt;");
				int ltNum=str.lastIndexOf("&lt;");
				int gtNum=str.lastIndexOf("&gt;");
				//�ж��ַ������Ƿ�ֻ����һ��xml�Ŀ�ʼ��ǩ
				//&lt;itemname&gt;��(K)&lt;/itemname&gt;
				if(firstLt==ltNum&&firstGt==gtNum){
					str=str.replace("&lt;", "<").replace("&gt;", ">");
				}else{				
					int endLt=str.lastIndexOf("&lt;/");
					int endGt=str.lastIndexOf("&gt;");
					if(firstLt>=0&&firstGt>=0&&endLt>=0&&endGt>=0){	
						StringBuffer strBuf=new StringBuffer();
						strBuf.append("<").append(str.substring(firstLt+4,firstGt)).append(">").append(str.substring(firstGt+4, endLt)).append("</").append(str.substring(endLt+5,endGt)).append(">");
						str=strBuf.toString();					
					}	
				}
				buffer.append(str);
			}			
			result = buffer.toString();
		} catch (Exception e) {			
			throw e;
		} finally {
			try {
				if(bufferedReader != null){
					bufferedReader.close();
					bufferedReader = null;
				}
				if(inputStreamReader!=null){
					inputStreamReader.close();
					inputStreamReader = null;
				}
				if (inputStream != null) {
					inputStream.close();
					inputStream = null;
				}
				if (outputStream !=null){
					outputStream.close();
					outputStream = null;
				}
				httpUrlConn.disconnect();
			} catch (Exception e) {
				logger.error("���ر��쳣", e);
			}
		}
		return result;
	}
}
