package com.kingdeehit.mobile.his.xianggang.service;

import org.apache.log4j.Logger;

import com.kingdeehit.mobile.his.service.Service;

public abstract class AbstractService implements Service{

	protected static Logger logger = Logger.getLogger("STDOUT");
	protected String serviceName;
	protected String channelCode;
	protected String proName;

	public AbstractService() {
		super();
	}

	@Override
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
		//存储过程命名不能出现“.”，统一替换为“_”
		this.proName = serviceName.replace(".", "_");
	}

	@Override
	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}
		
}