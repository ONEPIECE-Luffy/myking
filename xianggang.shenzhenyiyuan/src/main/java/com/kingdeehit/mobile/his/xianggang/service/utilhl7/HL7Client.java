package com.kingdeehit.mobile.his.xianggang.service.utilhl7;
/*package com.kingdeehit.mobile.his.xianggang.service.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.kingdeehit.mobile.his.utils.Consts;
import com.kingdeehit.mobile.his.utils.HL7ToXmlConverter;
import com.kingdeehit.mobile.his.xianggang.constant.HL7MsgXmlKey;
import com.kingdeehit.mobile.his.xianggang.service.utilhl7.NewHL7Util;
import com.kingdeehit.mobile.utils.StringUtil;

*//**
 * 发送HL7消息Client
 *
 * @author dongx
 *
 *//*
public class HL7Client {

	private Logger logger = Logger.getLogger(getClass());
	*//**
	 * 重发睡眠时间(单位：毫秒)
	 *//*
	private final int SLEEP_TIME = 2000;

	*//**
	 * 重发次数
	 *//*
	private final int REPEAT_TIMES = 3;

	private static HL7SendThread sendThread = null;

	public HL7Client() {
		sendThread = HL7SendThread.getInstance();
	}

	*//**
	 * 异步模式发送hl7消息
	 * @param hl7Message
	 * @return 发送结果
	 *//*
	public boolean sendHL7MessageAsyn(String hl7Message) {

		String result = getRecMessage(hl7Message);
		if (result == null) return false;
		boolean res = false;
		String ackRes;
		try {
			Document document = HL7ToXmlConverter.ConvertToXmlObject(result.toString());
			ackRes = HL7ToXmlConverter.GetText(document, HL7MsgXmlKey.ACK_RES_ID);
			if ("AA".equals(ackRes)) {
				res = true;
			}
		} catch (Exception e) {
			logger.error("解析ack数据失败。", e);
		}
		return res;

	}

	*//**
	 * 同步方式发送HL7消息
	 * @param hl7Message
	 * @return 消息结果
	 * @throws IOException
	 *//*
	public String sendHL7MessageSyn(String hl7Message) throws IOException {
		return getRecMessage(hl7Message);
//		return sendThread.sendHLMessage(hl7Message);
	}

	private String getRecMessage(String hl7Message) {
		String res = null;
		DataOutputStream out = null;
		DataInputStream input = null;
		Socket socket = null;
		int sendTimes = 0;
		String newUid = "";
		try {
			newUid = NewHL7Util.random(20);
		} catch (NoSuchAlgorithmException e) {
			logger.error("消息ID生成失败", e);
		}

		try {
			// 创建一个流套接字并将其连接到指定主机上的指定端口号
			socket = new Socket(Consts.HOSPITAL_HL7_SERVER_IP, Consts.HOSPITAL_HL7_SERVER_PORT);
			// 向服务器端发送数据
			out = new DataOutputStream(socket.getOutputStream());
			input = new DataInputStream(socket.getInputStream());

			String uid = NewHL7Util.getMsgControlId(hl7Message);
			hl7Message = hl7Message.replace(uid, newUid);
			uid = newUid;
			logger.info("调用HL7Client的发送消息:"+hl7Message);

			// 消息重发
//			while (sendTimes < REPEAT_TIMES && res == null) {
//				if (sendTimes > 0) {
//					// 等待睡眠，重发
//					Thread.sleep(SLEEP_TIME);
//				}
				NewHL7Util.sendHL7MsgByStream(hl7Message, out);

				res = NewHL7Util.getHL7MsgByStream(input);
//			}

		} catch (Exception e) {
			logger.error("客户端sendHL7Message异常。", e);
			res = null;
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {
				logger.error("客户端sendHL7Message关闭输入输出流异常。", e);
			}

			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					socket = null;
					logger.error("客户端sendHL7Message关闭socket异常。", e);
					res = null;
				}
			}
		}

		if (StringUtil.isNotEmpty(res)) {
			if (!"M".equals(res.substring(0, 1))) {
				logger.info("消息开头非M，补M");
				res="M"+res;
			}
		}

		try {
			org.dom4j.Document document = HL7ToXmlConverter.ConvertToXmlObject(res);
			String msgReId = HL7ToXmlConverter.GetText(document, "MSA/MSA.2");
			if (!newUid.equals(msgReId)) {
				logger.info("发送与接收消息ID不符："+newUid+"---"+msgReId);
				res="";
			}
		} catch (Exception e) {
			logger.error("获取回传消息ID异常", e);
			res="";
		}

		return res;
	}

	public static void main(String args[]){
		String res="MSH|^~\\&|HIS||MIH|";
		System.out.println(res.substring(0, 1));
		if (StringUtil.isNotEmpty(res)) {
			if (!"M".equals(res.substring(0, 1))) {
				res="M"+res;
			}
		}
		System.out.println(res);
	}
}
*/