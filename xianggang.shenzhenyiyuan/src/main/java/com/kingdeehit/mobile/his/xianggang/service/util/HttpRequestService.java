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
	 * 接口调用
	 * @param requestContent
	 * @return
	 * @throws Exception
	 */
	public String request(String requestContent) throws Exception {		
		String result = null;		
		URL url = new URL(wsdlAddress);
		logger.info("接口地址："+wsdlAddress);
		HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
		//连接超时设为30秒，读取数据超时设为60秒
		httpUrlConn.setConnectTimeout(connectTimeout);
		httpUrlConn.setReadTimeout(readTimeout);
		httpUrlConn.setDoOutput(true);
		httpUrlConn.setDoInput(true);
		httpUrlConn.setUseCaches(false);
		httpUrlConn.setRequestProperty("Content-type","application/x-java-serialized-object");
		// 设定请求的方法为"POST"，默认是GET
		httpUrlConn.setRequestMethod("POST");
		InputStream inputStream = null;
		OutputStream outputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		try {
			if (null != requestContent) {
				outputStream = httpUrlConn.getOutputStream();
				// 注意编码格式，防止中文乱码
				outputStream.write(requestContent.getBytes("UTF-8"));				
			}
			// 将返回的输入流转换成字符串
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
				//判断字符串中是否只出现一次xml的开始标签
				//&lt;itemname&gt;钾(K)&lt;/itemname&gt;
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
				logger.error("流关闭异常", e);
			}
		}
		return result;
	}
}
