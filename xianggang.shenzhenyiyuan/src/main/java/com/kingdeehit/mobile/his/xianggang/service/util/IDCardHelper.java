package com.kingdeehit.mobile.his.xianggang.service.util;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IDCardHelper {
	/**
	 * ���ܣ����֤����Ч��֤
	 * 
	 * @param IDStr
	 *            ���֤��
	 * @return ��Ч������"" ��Ч������String��Ϣ
	 * @throws ParseException
	 */
	public static String IDCardValidate(String IDStr) throws Exception {
		String errorInfo = "";// ��¼������Ϣ
		String[] ValCodeArr = { "1", "0", "x", "9", "8", "7", "6", "5", "4",
				"3", "2" };
		String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",
				"9", "10", "5", "8", "4", "2" };
		String Ai = "";
		// ================ ����ĳ��� 15λ��18λ ================
		if (IDStr.length() != 15 && IDStr.length() != 18) {
			errorInfo = "���֤���볤��Ӧ��Ϊ15λ��18λ��";
			return errorInfo;
		}
		// =======================(end)========================

		// ================ ���� �������Ϊ��Ϊ���� ================
		if (IDStr.length() == 18) {
			Ai = IDStr.substring(0, 17);
		} else if (IDStr.length() == 15) {
			Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
		}
		if (isNumeric(Ai) == false) {
			errorInfo = "���֤15λ���붼ӦΪ���� ; 18λ��������һλ�⣬��ӦΪ���֡�";
			return errorInfo;
		}
		// =======================(end)========================

		// ================ ���������Ƿ���Ч ================
		String strYear = Ai.substring(6, 10);// ���
		String strMonth = Ai.substring(10, 12);// �·�
		String strDay = Ai.substring(12, 14);// �·�
		if (isDataFormat(strYear + "-" + strMonth + "-" + strDay) == false) {
			errorInfo = "���֤������Ч��";
			return errorInfo;
		}
		GregorianCalendar gc = new GregorianCalendar();
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
		if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
				|| (gc.getTime().getTime() - s.parse(
						strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
			errorInfo = "���֤���ղ�����Ч��Χ��";
			return errorInfo;
		}
		if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
			errorInfo = "���֤�·���Ч";
			return errorInfo;
		}
		if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
			errorInfo = "���֤������Ч";
			return errorInfo;
		}
		// =====================(end)=====================

		// ================ ������ʱ����Ч ================
		Hashtable h = GetAreaCode();
		if (h.get(Ai.substring(0, 2)) == null) {
			errorInfo = "���֤�����������";
			return errorInfo;
		}
		// ==============================================

		// ================ �ж����һλ��ֵ ================
		int TotalmulAiWi = 0;
		for (int i = 0; i < 17; i++) {
			TotalmulAiWi = TotalmulAiWi
					+ Integer.parseInt(String.valueOf(Ai.charAt(i)))
					* Integer.parseInt(Wi[i]);
		}
		int modValue = TotalmulAiWi % 11;
		String strVerifyCode = ValCodeArr[modValue];
		Ai = Ai + strVerifyCode;

		if (IDStr.length() == 18) {
			if (Ai.toUpperCase().equals(IDStr.toUpperCase()) == false) {
				errorInfo = "���֤��Ч�����ǺϷ������֤����";
				return errorInfo;
			}
		} else {
			return "";
		}
		// =====================(end)=====================
		return "";
	}

	public static String getBirthday(String IDStr) throws Exception {
		String strYear = IDStr.substring(6, 10);// ���
		String strMonth = IDStr.substring(10, 12);// �·�
		String strDay = IDStr.substring(12, 14);// �·�
		return strYear + "-" + strMonth + "-" + strDay;
	}
	
	public static String getSex(String IDStr) throws Exception {
		String sCardNum = "";
		if (IDStr.length() == 18){
			sCardNum = IDStr.substring(16, 17);  
	        if (Integer.parseInt(sCardNum) % 2 != 0) {  
	            return "M";  
	        } else {  
	            return "F";  
	        }
		} else if (IDStr.length() == 15) {
			sCardNum = IDStr.substring(14, 15);  
	        if (Integer.parseInt(sCardNum) % 2 != 0) {  
	            return "M";  
	        } else {  
	            return "F";  
	        }
		}
		
		return "";
	}
	
	/**
	 * ���ܣ��ж��ַ����Ƿ�Ϊ����
	 * 
	 * @param str
	 * @return
	 */
	private static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (isNum.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ���ܣ����õ�������
	 * 
	 * @return Hashtable ����
	 */
	private static Hashtable GetAreaCode() {
		Hashtable hashtable = new Hashtable();
		hashtable.put("11", "����");
		hashtable.put("12", "���");
		hashtable.put("13", "�ӱ�");
		hashtable.put("14", "ɽ��");
		hashtable.put("15", "���ɹ�");
		hashtable.put("21", "����");
		hashtable.put("22", "����");
		hashtable.put("23", "������");
		hashtable.put("31", "�Ϻ�");
		hashtable.put("32", "����");
		hashtable.put("33", "�㽭");
		hashtable.put("34", "����");
		hashtable.put("35", "����");
		hashtable.put("36", "����");
		hashtable.put("37", "ɽ��");
		hashtable.put("41", "����");
		hashtable.put("42", "����");
		hashtable.put("43", "����");
		hashtable.put("44", "�㶫");
		hashtable.put("45", "����");
		hashtable.put("46", "����");
		hashtable.put("50", "����");
		hashtable.put("51", "�Ĵ�");
		hashtable.put("52", "����");
		hashtable.put("53", "����");
		hashtable.put("54", "����");
		hashtable.put("61", "����");
		hashtable.put("62", "����");
		hashtable.put("63", "�ຣ");
		hashtable.put("64", "����");
		hashtable.put("65", "�½�");
		hashtable.put("71", "̨��");
		hashtable.put("81", "���");
		hashtable.put("82", "����");
		hashtable.put("91", "����");
		return hashtable;
	}

	/**
	 * ��֤�����ַ����Ƿ���YYYY-MM-DD��ʽ
	 * 
	 * @param str
	 * @return
	 */
	private static boolean isDataFormat(String str) {
		boolean flag = false;
		// String
		// regxStr="[1-9][0-9]{3}-[0-1][0-2]-((0[1-9])|([12][0-9])|(3[01]))";
		String regxStr = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
		Pattern pattern1 = Pattern.compile(regxStr);
		Matcher isNo = pattern1.matcher(str);
		if (isNo.matches()) {
			flag = true;
		}
		return flag;
	}

	//�ɳ������ڻ������  
    public static int getAge(Date birthDay) throws Exception {  
        Calendar cal = Calendar.getInstance();  
  
        if (cal.before(birthDay)) {  
            throw new IllegalArgumentException(  
                    "The birthDay is before Now.It's unbelievable!");  
        }  
        int yearNow = cal.get(Calendar.YEAR);  
        int monthNow = cal.get(Calendar.MONTH);  
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);  
        
        cal.setTime(birthDay);  
        int yearBirth = cal.get(Calendar.YEAR);  
        int monthBirth = cal.get(Calendar.MONTH);  
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);  
  
        int age = yearNow - yearBirth;  
  
        if (monthNow <= monthBirth) {  
            if (monthNow == monthBirth) {  
                if (dayOfMonthNow < dayOfMonthBirth) age--;  
            }else{  
                age--;  
            }  
        }  
        return age;  
    } 
    
    /**
     * ��ȡ���������ڵ����·�
     * @param birthDay
     * @return
     * @throws Exception
     */
    public static int getAgeMonth(Date birthDay) throws Exception {  
        Calendar cal = Calendar.getInstance();  
  
        if (cal.before(birthDay)) {  
            throw new IllegalArgumentException(  
                    "The birthDay is before Now.It's unbelievable!");  
        }  
        int yearNow = cal.get(Calendar.YEAR);  
        int monthNow = cal.get(Calendar.MONTH);  
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);  
        
        cal.setTime(birthDay);  
        int yearBirth = cal.get(Calendar.YEAR);  
        int monthBirth = cal.get(Calendar.MONTH);  
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);  
  
        int month = yearNow - yearBirth;  
  
        if (yearNow == yearBirth) { 
        	month = monthNow - monthBirth;
        }else if(yearNow > yearBirth){
        	month = (yearNow - yearBirth-1) *12 + (12- monthBirth + monthNow);
        }  
        return month;  
    } 
    
    public static String idCard15To18(int century, String idCardNo15) {

        String centuryStr = "" + century;
        if(century <0 || centuryStr.length() != 2)
            throw new IllegalArgumentException("��������Ч��Ӧ������λ����������");
        if(idCardNo15.length() != 15)
            throw new IllegalArgumentException("�ɵ����֤�Ÿ�ʽ����ȷ��");

        int[] weight = new int[] {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1};

        // ͨ������������, ��� 17 Ϊ���º��뱾��.
        String newNoBody = idCardNo15.substring(0, 6) + centuryStr + idCardNo15.substring(6);

        //���������һλУ����
        int checkSum = 0;
        for(int i=0; i< 17; i++) {
            int ai = Integer.parseInt("" + newNoBody.charAt(i)); // λ�� i λ�õ���ֵ
            checkSum = checkSum + ai * weight[i];
        }

        int checkNum = checkSum % 11;
        String checkChar = null;

        switch(checkNum) {
            case 0: checkChar = "1"; break;
            case 1: checkChar = "0"; break;
            case 2: checkChar = "X"; break;
            default: checkChar = "" + (12 - checkNum);
        }

        return newNoBody + checkChar;

    }
	public static void main(String[] arg1) throws Exception{
	/*	IDCardHelper helper = new IDCardHelper();
		try {
			System.out.println(helper.idCard15To18(19,"410103197200619"));
			System.out.println(helper.idCard15To18(19,"210423600703002"));
			
			//System.out.println(helper.IDCardValidate("511721197812212336"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		String ss = getSex("123456789012346133");
		System.out.println(ss);

	}
	
	
}
