package com.kingdeehit.mobile.his.xianggang.service.utilhl7;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v24.message.ACK;
import ca.uhn.hl7v2.model.v24.segment.MSA;
import ca.uhn.hl7v2.model.v24.segment.MSH;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;

import com.kingdeehit.mobile.his.utils.HL7ToXmlConverter;
import com.kingdeehit.mobile.his.xianggang.constant.HL7MsgXmlKey;
import com.kingdeehit.mobile.utils.DateUtils;
import com.kingdeehit.mobile.utils.StringUtil;

/**
 * HL7工具类
 *
 * @author dongx
 *
 */
public class NewHL7Util {

	private static Logger logger = Logger.getLogger("NewHL7Util");
	public static StringBuffer messageInfo = new StringBuffer();
	/**
	 * 验证hl7消息是否合法
	 *
	 * @param hl7Message
	 * @return
	 */
	public static boolean checkHL7Message(byte[] messageByte) {
		return checkHL7Message(messageByte, messageByte.length);
	}

	/**
	 * 验证hl7消息是否合法
	 *
	 * @param hl7Message
	 * @return
	 */
	public static boolean checkHL7Message(byte[] messageByte, int length) {
		logger.info("hl7消息接收结束标识：数组长度："+messageByte.length+";传递长度："+length+";"+messageByte[0] +";"+messageByte[length - 1]+";"+messageByte[length - 2]);
		if (messageByte[0] == 0x0B && messageByte[length - 1] == 0x0D && messageByte[length - 2] == 0x1C) {
			return true;
		}
		return false;
	}

	/**
	 * 获取ack响应消息
	 *
	 * @param msgContorlId
	 * @return
	 * @throws HL7Exception
	 * @throws Exception
	 */
	public static String getResponseAck(String msgContorlId) throws HL7Exception {
		// 组装返回的ACK消息
		String res = "";
		ACK ack = new ACK();
		// MSH消息段(Segment)
		MSH mshSegment = ack.getMSH();
		mshSegment.getMsh1_FieldSeparator().setValue("|");
		mshSegment.getMsh2_EncodingCharacters().setValue("^~\\&");
		mshSegment.getMsh3_SendingApplication().getHd1_NamespaceID().setValue("MIH");
		mshSegment.getMsh5_ReceivingApplication().getHd1_NamespaceID().setValue("EAI");
		mshSegment.getMsh7_DateTimeOfMessage().getTs1_TimeOfAnEvent().setValue(DateUtils.parseChineseYMDHmsDateStr(new Date()));
		mshSegment.getMsh9_MessageType().getMsg1_MessageType().setValue("ACK");
		mshSegment.getMessageControlID().setValue(msgContorlId);
		mshSegment.getProcessingID().getProcessingID().setValue("P");
		mshSegment.getVersionID().getVersionID().setValue("2.4");

		MSA msa = ack.getMSA();
		msa.getMsa1_AcknowledgementCode().setValue("AA");
		msa.getMsa2_MessageControlID().setValue(msgContorlId);
		msa.getMsa3_TextMessage().setValue("Accepted!");

		Parser parser = new PipeParser();
		res = parser.encode(ack);

		return res;
	}

	/**
	 * @功能 读取流
	 * @param inStream
	 * @return 字节数组
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static byte[] readStream(InputStream inStream) throws IOException, InterruptedException {
		int count = 0;
		long time = System.currentTimeMillis();
		while (count == 0 && System.currentTimeMillis() - time < 10 * 1000) {
			count = inStream.available();
			if (count == 0) {
				Thread.sleep(100);
			}
		}
		byte[] b = new byte[count];
		inStream.read(b);
		return b;
	}

	/**
	 * 获取hl7消息的id
	 *
	 * @param document
	 * @return
	 */
	public static String getMsgControlId(String hl7Message) {
		// String recXml = null;
		// try {
		// recXml = HL7ToXmlConverter.ConvertToXml(hl7Message);
		// } catch (Exception e) {
		// logger.error("HL7消息格式错误", e);
		// }
		// logger.info("HL7ToXml串：\n"+recXml);

		Document document = HL7ToXmlConverter.ConvertToXmlObject(hl7Message);

		return getMsgControlId(document);
	}

	/**
	 * 获取hl7消息的来源id
	 * @param document
	 * @return
	 */
	public static String getMsgSourceId(String hl7Message) {
		Document document = HL7ToXmlConverter.ConvertToXmlObject(hl7Message);
		return HL7ToXmlConverter.GetText(document, "MSA/MSA.2");
	}

	/**
	 * 获取hl7消息的id
	 *
	 * @param document
	 * @return
	 */
	public static String getMsgControlId(Document document) {
		return HL7ToXmlConverter.GetText(document, HL7MsgXmlKey.MSG_CONTROL_ID);
	}

	/**
	 * 通过输出流发送HL7消息
	 *
	 * @param msg
	 * @param out
	 */
	public static boolean sendHL7MsgByStream(String msg, OutputStream out) {
		boolean res = true;
		try {
			byte[] bb = msg.getBytes("GBK");
			byte[] bs = new byte[bb.length + 3];
			bs[0] = (byte) 0x0B;
			System.arraycopy(bb, 0, bs, 1, bb.length);
			bs[bb.length + 1] = (byte) 0x1C;
			bs[bb.length + 2] = (byte) 0x0D;
			// 发送数据
			out.write(bs);
		} catch (UnsupportedEncodingException e) {
			logger.error("通过输出流发送数据失败1。", e);
			res = false;
		} catch (IOException e) {
//			logger.error("通过输出流发送数据失败2。", e);
			logger.error("HL7平台存在连接拒接发送行为，请与HL7平台工程师查看具体拒绝接收我发信息原因（如与HIS交互正常，该错误消息忽略）");
			res = false;
		} catch (Exception e) {
			logger.error("通过输出流发送数据失败3。", e);
			res = false;
		}
		return res;
	}

	/**
	 * 通过输入流读取消息
	 *
	 * @param input
	 * @return
	 * @throws InterruptedException
	 * @throws Exception
	 */
	public static String getHL7MsgByStream(InputStream input) throws IOException, InterruptedException {
		String ret = null;
		long time = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder();
		// 1m的缓存
		byte[] data = new byte[1024 * 1024];
		int position = 0;
		//分片处理最大处理时间30秒，超时返回Null
		while (System.currentTimeMillis() - time < 30 * 1000) {
			byte[] buffer = readStream(input);
			int length = buffer.length;

			if (length <= 0) {
				continue;
			}

			//logger.debug("接收数据：" + new String(buffer));
			sb.append(new String(buffer));
			System.arraycopy(buffer, 0, data, position, length);
			position += length;

			// 消息不合法，不进行处理
			if (buffer.length == 0 || !checkHL7Message(data, position)) {
				logger.debug("消息接收不全不合法：" + new String(buffer));
				messageInfo.append(new String(buffer));
				continue;
			} else {
				//logger.info("接收数据正确:" + sb.toString());
				byte[] result = new byte[position - 3];
				System.arraycopy(data, 1, result, 0, position - 3);
				ret = new String(result, "GBK");
				logger.info("接收ret数据:" + ret);
				break;
			}
		}
		return ret;
	}

	public static String random(int length) throws NoSuchAlgorithmException {
		StringBuilder builder = new StringBuilder(length);
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

		for (int i = 0; i < length; i++) {

			int r = (int) (random.nextDouble() * 3);
			int rn1 = (int) (48 + random.nextDouble() * 10);
			int rn2 = (int) (65 + random.nextDouble() * 26);
			int rn3 = (int) (97 + random.nextDouble() * 26);

			switch (r) {
			case 0:
				builder.append((char) rn1);
				break;
			case 1:
				builder.append((char) rn2);
				break;
			case 2:
				builder.append((char) rn3);
				break;
			}
		}
		return builder.toString();
	}
}
