package com.kingdeehit.mobile.his.xianggang.entity.table;

import com.kingdeehit.mobile.his.entities.table.ChannelBaseInfo;

/**
 * �洢 his�Һ���Ϣ  
 * @author YJB
 * @date 2017��10��11�� ����8:17:10
 */
public class RegisterInfo  extends ChannelBaseInfo {

	
	String bookingNo; //his�Һ���ˮ��
	String mzlsh;//���ź�õ���������ˮ��
	String orderType; //��Դ���� 10-���  99��������
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
