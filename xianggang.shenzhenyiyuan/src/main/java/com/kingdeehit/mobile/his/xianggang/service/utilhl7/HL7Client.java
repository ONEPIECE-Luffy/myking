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
 * ����HL7��ϢClient
 *
 * @author dongx
 *
 *//*
public class HL7Client {

	private Logger logger = Logger.getLogger(getClass());
	*//**
	 * �ط�˯��ʱ��(��λ������)
	 *//*
	private final int SLEEP_TIME = 2000;

	*//**
	 * �ط�����
	 *//*
	private final int REPEAT_TIMES = 3;

	private static HL7SendThread sendThread = null;

	public HL7Client() {
		sendThread = HL7SendThread.getInstance();
	}

	*//**
	 * �첽ģʽ����hl7��Ϣ
	 * @param hl7Message
	 * @return ���ͽ��
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
			logger.error("����ack����ʧ�ܡ�", e);
		}
		return res;

	}

	*//**
	 * ͬ����ʽ����HL7��Ϣ
	 * @param hl7Message
	 * @return ��Ϣ���
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
			logger.error("��ϢID����ʧ��", e);
		}

		try {
			// ����һ�����׽��ֲ��������ӵ�ָ�������ϵ�ָ���˿ں�
			socket = new Socket(Consts.HOSPITAL_HL7_SERVER_IP, Consts.HOSPITAL_HL7_SERVER_PORT);
			// ��������˷�������
			out = new DataOutputStream(socket.getOutputStream());
			input = new DataInputStream(socket.getInputStream());

			String uid = NewHL7Util.getMsgControlId(hl7Message);
			hl7Message = hl7Message.replace(uid, newUid);
			uid = newUid;
			logger.info("����HL7Client�ķ�����Ϣ:"+hl7Message);

			// ��Ϣ�ط�
//			while (sendTimes < REPEAT_TIMES && res == null) {
//				if (sendTimes > 0) {
//					// �ȴ�˯�ߣ��ط�
//					Thread.sleep(SLEEP_TIME);
//				}
				NewHL7Util.sendHL7MsgByStream(hl7Message, out);

				res = NewHL7Util.getHL7MsgByStream(input);
//			}

		} catch (Exception e) {
			logger.error("�ͻ���sendHL7Message�쳣��", e);
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
				logger.error("�ͻ���sendHL7Message�ر�����������쳣��", e);
			}

			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					socket = null;
					logger.error("�ͻ���sendHL7Message�ر�socket�쳣��", e);
					res = null;
				}
			}
		}

		if (StringUtil.isNotEmpty(res)) {
			if (!"M".equals(res.substring(0, 1))) {
				logger.info("��Ϣ��ͷ��M����M");
				res="M"+res;
			}
		}

		try {
			org.dom4j.Document document = HL7ToXmlConverter.ConvertToXmlObject(res);
			String msgReId = HL7ToXmlConverter.GetText(document, "MSA/MSA.2");
			if (!newUid.equals(msgReId)) {
				logger.info("�����������ϢID������"+newUid+"---"+msgReId);
				res="";
			}
		} catch (Exception e) {
			logger.error("��ȡ�ش���ϢID�쳣", e);
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