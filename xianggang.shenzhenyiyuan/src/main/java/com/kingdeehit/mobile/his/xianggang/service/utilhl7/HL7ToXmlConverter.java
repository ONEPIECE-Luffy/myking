package com.kingdeehit.mobile.his.xianggang.service.utilhl7;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import ca.uhn.hl7v2.model.v26.message.EHC_E10;

public class HL7ToXmlConverter {

	private static Logger logger = Logger.getLogger("HL7ToXmlConverter");

	public static String ConvertToXml(String sHL7) {
		Document document = ConvertToXmlObject(sHL7);
		String hl7str = document.asXML();
		return hl7str;
	}

	public static String ConvertToXml(Document document) {
		String hl7str = document.asXML();
		return hl7str;
	}

	public static Document ConvertToXmlObject(String sHL7) {
		Document document = CreateXmlDoc();

		// ��HL7�ֳɶ�
		String[] sHL7Lines = sHL7.split("\r");

		// ȥ��XML�Ĺؼ���
		for (int i = 0; i < sHL7Lines.length; i++) {
			sHL7Lines[i] = sHL7Lines[i].replace("^~\\&", "").replace("MSH",
					"MSH|");
		}

		for (int i = 0; i < sHL7Lines.length; i++) {
			// �ж��Ƿ����
			if (sHL7Lines[i] != null) {
				String sHL7Line = sHL7Lines[i];

				// ͨ��/r ��/n �س����ָ�
				String[] sFields = GetMessgeFields(sHL7Line);

				// Ϊ�Σ�һ�У�������һ���ڵ�
				Element el = document.getRootElement().addElement(sFields[0]);

				// ѭ��ÿһ��
				Boolean isMsh = true;
				for (int a = 1; a < sFields.length; a++) {

					// �Ƿ����HL7�����ӷ�^~\\&
					if (sFields[a].indexOf('^') > 0
							|| sFields[a].indexOf('~') > 0
							|| sFields[a].indexOf('\\') > 0
							|| sFields[a].indexOf('&') > 0
							|| sFields[a].indexOf('^') != -1) {// 0:�����һ������һ�ָ���

						// ��ʼ����~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

						// ͨ��~�ָ�
						String[] sComponents = GetRepetitions(sFields[a]);
						if (sComponents.length > 1) {// 1:������Էָ�
													// 0001^����^����^EQ^AND~0002^��һ��^������^EQ^AND
							Element createComponents = CreateComponents(el, sComponents[0],
									sFields[0], a, sComponents.length , 0 );
								//el.setText(sFields[a]);
						} else {// 1��������ֻ��һ��ֵ�� 0001^����^����^EQ^AND
								// Ϊ�ֶδ����ڶ����ڵ�
							//Element fieldEl = el.addElement(sFields[0] + "." + a);
							CreateComponents(el, sFields[a], sFields[0], a, 0);

							// fieldEl.setText(sFields[a]+"11111111111111");
						}

						// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
					}else {// 0:�����һ��û���κηָ���
							// Ϊ�ֶδ����ڶ����ڵ�
						Element fieldEl = el.addElement(sFields[0] + "." + a);
						fieldEl.setText(sFields[a]);

					}

				}

			}// end if

		}// end for

		// �޸�MSH.1 �� MSH.2��ֵ
		document.selectSingleNode("HL7Message/MSH/MSH.1").setText("|");
		document.selectSingleNode("HL7Message/MSH/MSH.2").setText("~^\\&");
		// document.selectNodes("MSH/MSH.1");

		return document;
	}

	
	@SuppressWarnings("unused")
	private static Element CreateComponents(final Element el,
			final String hl7Components, String sField, int a, int b) {
		Element componentEl =el.addElement(sField + "." + a );
		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
		// ͨ��&�ָ�
		String[] subComponents = GetSubComponents(hl7Components);
		if (subComponents.length > 1) {// 2.��������飬һ����û�еġ����� �ӷ��� ��&������
			String[] sRepetitions = GetComponents(hl7Components);
			Element repetitionEl = null;
			for (int c = 0; c < sRepetitions.length; c++) {
				repetitionEl = componentEl.addElement(sField + "." + a
						+ "." + (b + 1)) ;
				repetitionEl.setText(sRepetitions[b]);
			}

		} else {// 2.���û���ˣ�����^����
			String[] sRepetitions = GetComponents(hl7Components);
			Element repetitionEl = null;
			if (sRepetitions.length > 1) {
				for (int c = 0; c < sRepetitions.length; c++) {
					repetitionEl = componentEl.addElement(sField + "." + a
							+ "." + (b + 1));
					repetitionEl.setText(sRepetitions[c]);
				}
			} else {
				componentEl.setText(hl7Components);
			}

		}
		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
		return el;

	}
	
	@SuppressWarnings("unused")
	private static Element CreateComponents(final Element el,
			final String hl7Components, String sField, int a, int b,int c) {
		Element componentEl =el.addElement(sField + "." + a);
		for (int e = 0; e < b; e++) {
			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
			// ͨ��&�ָ�
			Element componentE2 =componentEl.addElement(sField + "." + a + "." + (e + 1));
			String[] subComponents = GetSubComponents(hl7Components);
			if (subComponents.length > 1) {// 2.��������飬һ����û�еġ����� �ӷ��� ��&������
				String[] sRepetitions = GetComponents(hl7Components);
				Element repetitionEl = null;
				for (int d = 0; d < sRepetitions.length; d++) {
					repetitionEl = componentE2.addElement(sField + "." + a
							+ "." + (e + 1) + "." + (d + 1));
					repetitionEl.setText(sRepetitions[d]);
				}
			} else {// 2.���û���ˣ�����^����
				String[] sRepetitions = GetComponents(hl7Components);
				Element repetitionEl = null;
				if (sRepetitions.length > 1) {
					for (int d = 0; d < sRepetitions.length; d++) {
						repetitionEl = componentE2.addElement(sField + "." + a
								+ "." + (e + 1) + "." + (d + 1));
						repetitionEl.setText(sRepetitions[d]);
					}
				} else {
					componentEl.setText(hl7Components);
				}
			}
		}
		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
		return el;

	}

	// / <summary>
	// / ͨ��|�ָ� �ֶ�
	// / </summary>
	// / <param name="s"></param>
	// / <returns></returns>
	private static String[] GetMessgeFields(String s) {
		return s.split("\\|");
	}

	// / <summary>
	// / ͨ��^�ָ� ���ֶ�
	// / </summary>
	// / <param name="s"></param>
	// / <returns></returns>
	private static String[] GetComponents(String s) {
		return s.split("\\^");
	}

	// / <summary>
	// / ͨ��&�ָ� �ӷ������ֶ�
	// / </summary>
	// / <param name="s"></param>
	// / <returns></returns>
	private static String[] GetSubComponents(String s) {
		return s.split("&");
	}

	// / <summary>
	// / ͨ��~�ָ� �ظ�
	// / </summary>
	// / <param name="s"></param>
	// / <returns></returns>
	private static String[] GetRepetitions(String s) {
		return s.split("~");
	}

	// / <summary>
	// / ����XML����
	// / </summary>
	// / <returns></returns>
	private static Document CreateXmlDoc() {
		Document output = DocumentHelper.createDocument();
		// ����һ���ӵ�
		Element rootNode = output.addElement("HL7Message");
		return output;
	}

	public static String GetText(Document document, String path) {
		try {
			Node node = document.selectSingleNode("HL7Message/" + path);
			if (node != null) {
				return node.getText();
			} else {
				return "";
			}
		} catch (Exception e) {
			logger.error("GetText error", e);
			return "";
		}
	}

	public static String GetText(Document document, String path, int index) {
		try {
			List nodes = document.selectNodes("HL7Message/" + path);
			if (nodes != null) {
				return ((Node) nodes.get(index)).getText();
			} else {
				return "";
			}
		} catch (Exception e) {
			logger.error("GetText error", e);
			return "";
		}
	}

	public static List GetTexts(Document document, String path) {
		List nodes = document.selectNodes("HL7Message/" + path);
		return nodes;

	}

	public static void writeDocument(Document document, String filepath) {
		try {
			// ��ȡ�ļ�
			// FileWriter fileWriter = new FileWriter(filepath);
			Writer writer = new OutputStreamWriter(new FileOutputStream(
					filepath), "utf-8");

			// �����ļ�����
			OutputFormat xmlFormat = new OutputFormat();
			xmlFormat.setEncoding("utf-8");
			// ����д�ļ�����
			XMLWriter xmlWriter = new XMLWriter(writer, xmlFormat);
			// д���ļ�
			xmlWriter.write(document);
			// �ر�
			xmlWriter.close();
		} catch (IOException e) {
			System.out.println("�ļ�û���ҵ�");
			e.printStackTrace();
		}
	}

	public static void main(String[] arg) {
		/*String myHL7string = "MSH|^~\\&|455755610_0100||0200||20110624160404|000|QRY^A19^QRY_A19|0123456001|P|2.6\nQRD|||||||||0001^����^����^EQ^AND~0002^��һ��^������^EQ^AND\nQRF||20110627|20110803";
		Document document = HL7ToXmlConverter.ConvertToXmlObject(myHL7string);

		// ��ȡ�¼�
		String eventName = HL7ToXmlConverter.GetText(document,"MSH/MSH.9/MSH.9.3");
		System.out.println("eventName:" + eventName);

		// List nodeValue = document.selectNodes("MSH.1");
		String nodeValue = document.selectSingleNode("HL7Message/MSH/MSH.1").getText();
		String nodeValue2 = document.selectSingleNode("HL7Message/MSH/MSH.3").getText();
		// DocumentElement.SelectNodes(path);
		System.out.println(nodeValue + ":" + nodeValue2);

		String value = HL7ToXmlConverter.GetText(document, "QRD/QRD.9/QRD.9.1",0);
		String value1 = HL7ToXmlConverter.GetText(document,"QRD/QRD.9/QRD.9.1", 1);
		String value2 = HL7ToXmlConverter.GetText(document, "QRD/QRD.9/QRD.9.1");
		System.out.println(value + ":" + value1 + ":" + value2);

		List<Node> list = HL7ToXmlConverter.GetTexts(document,"QRD/QRD.9/QRD.9.1");
		for (Node node : list) {
			System.out.println(":" + node.getText());
		}

		System.out.println(HL7ToXmlConverter.ConvertToXml(myHL7string));
*/
		StringBuffer buf = new StringBuffer();
		/*buf.append("MSH|^~\\&|HIS||EAI||20150707170124||ADT^A01|40258254|P|2.4|||AL|AL|CHN");
		buf.append("\r\n");
		buf.append("EVN|A01|20150707170124|20150707170124|01|003516^����|20150707170124|HIS");
		buf.append("\r\n");
		buf.append("PID|1|0000312799|0000312799^^^^IDCard~0000291199^^^^PatientNO~510822198810023940^^^^IdentifyNO||��÷||19881002000000|F|||�㶫ʡ������������������^^^^518000^^H~��^^^^518000^^O~�Ĵ�ʡ��Ԫ���ന��^^^^518000^^N||^^^^^^^^18925224542|^^^^^^^^��||M^�ѻ�||||||1^����|�Ĵ�ʡ��Ԫ���ന��|||||156^�й�|||||||�Ĵ�ʡ��Ԫ���ന��|||1^�Է�");
		buf.append("\r\n");
		buf.append("NK1|1|���Ļ�|1^����|����|^^^^^^^^18925220902|^^^^^^^^��||||��|12");
		buf.append("\r\n");
		buf.append("PV1|1|I|9505^^^4506^������|1|||||||||2|1||0|003109|01|112998108|||||||||||||||||||||||||20150707170124");
		buf.append("\r\n");
		buf.append("DG1||||||11||||||||||003109");*/

		//*********************************************************//
		buf.append("MSH|^~\\&|HRP||MIH||20151116045609||MFN^M01|510217598y|P|2.4|||AL|AL|CHN");
		buf.append("\r\n");
		buf.append("MFI|Department^����^HRP||UPD||100801|AL");
		buf.append("\r\n");
		buf.append("MFE|MUP||||CE");
		buf.append("\r\n");
		buf.append("Z01|0306|�ڷ������л����|111|11|C|1|1|0|1||NFMYDXMZ|MWIGWYUY||20151116124329");
		buf.append("\r\n");

		/*buf.append("MSH|^~\\&|HIS||EAI||20150707154258||ADT^A04|40251509|P|2.4|||AL|AL|CHN");
		buf.append("\r\n");
		buf.append("EVN|A04|20150707154258|20150707154258|01|005024^������|20150707154258|HIS");
		buf.append("\r\n");
		buf.append("PID|1|0000750786|0000750786^^^^IDCard~0000750786^^^^Card_No~350625196512291057^^^^IdentifyNO||����ϣ||19651229000000|M|||^^^^^^H~^^^^^^O||^^^^^^^^18926570703||||||6043499976|||||||||||||||||||2^��ͨҽ��");
		buf.append("\r\n");
		buf.append("NK1|1");
		buf.append("\r\n");
		buf.append("PV1|1|O|^^^0304|3|||||||||11|||0||02|112997718|||||||||||||||||||||||||20150707154258|||||||0");*/

		System.out.println("HL7ԭʼ����\n"+buf.toString());

		String recXml=HL7ToXmlConverter.ConvertToXml(buf.toString().replace("", ""));
		System.out.println("HL7ToXml����\n"+recXml);

		Document document = HL7ToXmlConverter.ConvertToXmlObject(buf.toString());
		String eventName = HL7ToXmlConverter.GetText(document,"Z01/Z01.1");
		System.out.println("��ȡ�̶��ڵ㣺"+eventName);
		String eventName1 = HL7ToXmlConverter.GetText(document,"Z02/Z02.1");
		System.out.println("��ȡ�̶��ڵ㣺"+eventName1);

		String value1 = HL7ToXmlConverter.GetText(document, "PID/PID.3/PID.3.1",0);
		String value2 = HL7ToXmlConverter.GetText(document,"PID/PID.3/PID.3.1", 1);
		String value3 = HL7ToXmlConverter.GetText(document, "PID/PID.3/PID.3.1", 2);
		System.out.println("��ȡ�б���ͬ�ڵ�����ֵ��"+value1 + ":" + value2 + ":" + value3);

		List<Node> list = HL7ToXmlConverter.GetTexts(document,"PID/PID.3/PID.3.1");
		for (Node node : list) {
			System.out.println("ѭ����ȡ�б���ͬ�ڵ�����ֵ:" + node.getText());
		}


	}
}