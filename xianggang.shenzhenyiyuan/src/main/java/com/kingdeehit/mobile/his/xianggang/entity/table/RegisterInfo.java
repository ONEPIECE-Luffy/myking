package com.kingdeehit.mobile.his.xianggang.entity.table;

import com.kingdeehit.mobile.his.entities.table.ChannelBaseInfo;

/**
 * 存储 his挂号信息  
 * @author YJB
 * @date 2017年10月11日 下午8:17:10
 */
public class RegisterInfo  extends ChannelBaseInfo {

	
	String bookingNo; //his挂号流水号
	String mzlsh;//锁号后得到的门诊流水号
	String orderType; //号源类型 10-金蝶  99其他渠道
	public String getBookingNo() {
		return bookingNo;
	}
	public void setBookingNo(String bookingNo) {
		this.bookingNo = bookingNo;
	}
	public String getMzlsh() {
		return mzlsh;
	}
	public void setMzlsh(String mzlsh) {
		this.mzlsh = mzlsh;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	
	
	
}
