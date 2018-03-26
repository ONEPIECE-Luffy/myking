package com.kingdeehit.mobile.his.xianggang.service.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.kingdeehit.mobile.his.consts.ServiceCode.statistics;

/**
 * 默认读取resource下面的config.properties文件
 * @author yuan
 * @date 2017年8月6日 下午11:17:36
 */
public class ConfigUtils {
	
	Logger logger = Logger.getLogger(ConfigUtils.class);
	
	private static ConfigUtils instance;
    Properties  properties;
	//单例设计模式
	public static ConfigUtils getInstance() {
		if (instance == null) { // 使用的是懒汉式
			instance = new ConfigUtils();
		}
		return instance;
	}
	private  String secretKey;
	
	private  String serverUrl;
	
	private  String channelId;
	
	private String passportId;
	
	private String hospitalId;
	
	private String pictureServerUrl;
	
	private String tempPicture;
	
	private String deptSmaller18;
	
	private String deptBigger18;
	
	private String deptBigger10;
	
	private String imcId; //IMC科室ID 
	
	
	private String femaleDept;//女性科室
	
	private String selfPayDept;//全自费科室
	
	//缓存
	private static Map<String, String> femaleDeptMap = new HashMap<String, String>();
	private static Map<String, String> selfPayDeptMap = new HashMap<String, String>();
	
	public ConfigUtils(){
		
		properties = new Properties();
		try {
			 properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
			 this.setDeptSmaller18(getValue("DeptSmaller18"));
			 this.setDeptBigger18(getValue("DeptBigger18"));
			 this.setDeptBigger10(getValue("DeptBigger10"));
			 this.setImcId(getValue("imcId"));
			 this.setFemaleDept(getValue("femaleDept"));
			 this.setSelfPayDept(getValue("selfPayDept"));
			 //initDeptParam();
		}   catch (Exception e) {
			logger.error("配置文件初始化出错，系统停止！",e);
			System.exit(0);
		}
	}
	/**
	 * 根据键获取值，取出字符串前后的空格
	 * @author YJB
	 * @date 2017年8月7日 上午8:26:24
	 * @param key
	 * @return
	 */
	public String getValue(String key){
		try {
			return properties.getProperty(key).trim();
		} catch (Exception e) {
			logger.warn("加载配置文件中，未找到 key为 "+key+" 的值");
			return "";
		}
	}
	
	/**
	* @Title: getFemaleDeptMap 
	* @author Luffy-CXM 
	* @Description: 获取女性科室   
	* @return Map<String,String>
	 */
	public static Map<String, String> getFemaleDeptMap(){
		if (femaleDeptMap.size() == 0) {
			String[] female = ConfigUtils.getInstance().getFemaleDept().split(",");
			for (String deptId : female) {
				femaleDeptMap.put(deptId, deptId);
			}
		}
		return femaleDeptMap;
	}
	
	/**
	* @Title: getFemaleDeptMap 
	* @author Luffy-CXM 
	* @Description: 获取自费科室
	* @return Map<String,String>
	 */
	public static Map<String, String> getSelfPayDeptMap(){
		if (selfPayDeptMap.size() == 0) {
			String[] selfPay= ConfigUtils.getInstance().getSelfPayDept().split(",");
			for (String deptId : selfPay) {
				selfPayDeptMap.put(deptId, deptId);
			}
		}
		return selfPayDeptMap;
	}

	public String getDeptSmaller18() {
		return deptSmaller18;
	}
	public void setDeptSmaller18(String deptSmaller18) {
		this.deptSmaller18 = deptSmaller18;
	}
	public String getDeptBigger18() {
		return deptBigger18;
	}
	public void setDeptBigger18(String deptBigger18) {
		this.deptBigger18 = deptBigger18;
	}
	public String getDeptBigger10() {
		return deptBigger10;
	}
	public void setDeptBigger10(String deptBigger10) {
		this.deptBigger10 = deptBigger10;
	}
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	public String getTempPicture() {
		return tempPicture;
	}
	public void setTempPicture(String tempPicture) {
		this.tempPicture = tempPicture;
	}
	public String getPictureServerUrl() {
		return pictureServerUrl;
	}
	public void setPictureServerUrl(String pictureServerUrl) {
		this.pictureServerUrl = pictureServerUrl;
	}
	public String getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}
	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
	 
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getPassportId() {
		return passportId;
	}
	public void setPassportId(String passportId) {
		this.passportId = passportId;
	}
	 
	public String getImcId() {
		return imcId;
	}
	public void setImcId(String imcId) {
		this.imcId = imcId;
	}
	public String getFemaleDept() {
		return femaleDept;
	}
	public void setFemaleDept(String femaleDept) {
		this.femaleDept = femaleDept;
	}
	public String getSelfPayDept() {
		return selfPayDept;
	}
	public void setSelfPayDept(String selfPayDept) {
		this.selfPayDept = selfPayDept;
	}
}
