package com.kingdeehit.mobile.his.xianggang.entity;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.kingdeehit.mobile.his.entities.Response;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * @author chenly
 *
 */
@XStreamAlias("res")
public class GetRegisterInfoResultV3 extends Response {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XStreamImplicit
	@XmlElement(name="orderInfo", required=false, nillable=true)	
	private List<GetRegisterInfoResultItemV3> list;

	public List<GetRegisterInfoResultItemV3> getList() {
		return list;
	}

	public void setList(List<GetRegisterInfoResultItemV3> list) {
		this.list = list;
	}

	
}
