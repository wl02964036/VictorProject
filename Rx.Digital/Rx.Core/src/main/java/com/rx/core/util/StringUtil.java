package com.rx.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 共用 字串模組
 * 
 * @ClassName: StringUtil
 * @author Hank_Chuang
 * @date 2018-02-08 15:33:41
 */
public class StringUtil extends org.apache.commons.lang3.StringUtils {

	private static final Logger LOGGER = LogManager.getLogger(StringUtil.class);

	/** 空字串 */
	public static final String EMPTY = "";

	/**
	 * 字串補滿
	 * 
	 * @Method: fillString
	 * @author Hank_Chuang
	 * @param sValue  String 輸入值
	 * @param iLength int 長度
	 * @param sFillBy char 用於補滿的字元
	 * @param sLR     true:左補 , false:右補
	 * @return
	 * @date 2018-02-08 15:33:58
	 */
	public static String fillString(String sValue, int iLength, char sFillBy, boolean sLR) {
		if (sValue == null || sValue.length() > iLength) {
			return sValue;
		}
		StringBuffer temp = new StringBuffer();
		while (iLength > sValue.length()) {
			temp.append(sFillBy);
			iLength--;
		}

		return (sLR) ? temp + sValue : sValue + temp;
	}

	/**
	 * 判斷若為 Null 則返回空字串
	 * 
	 * @Method: nvl
	 * @author Hank_Chuang
	 * @param obj 任何型態值
	 * @return
	 * @date 2018-02-08 15:35:23
	 */
	public static String nvl(Object obj) {
		if (obj == null) {
			return EMPTY;
		} else {
			return obj.toString().trim();
		}
	}

	/**
	 * 是否 Null 或 空白
	 * 
	 * @Method: isBlank
	 * @author Hank_Chuang
	 * @param obj 任何型態值
	 * @return
	 * @date 2018-02-08 15:35:49
	 */
	public static boolean isBlank(Object obj) {
		boolean is = true;
		if (obj != null) {
			is = isBlank(obj.toString());
		}
		return is;
	}

	/**
	 * 是否 Null 或 空白
	 * 
	 * @Method: isNotBlank
	 * @author VictorChi
	 * @param obj 任何型態值
	 * @return
	 * @date 2023-11-24 14:04:38
	 */
	public static boolean isNotBlank(Object obj) {
		if (obj == null) {
			return false;
		}
		return isNotBlank(obj.toString());
	}

	/**
	 * 檢核-交集結果
	 * 
	 * @Method: intersect
	 * @author Hank_Chuang
	 * @param arr1 字串 Array1
	 * @param arr2 字串 Array2
	 * @return
	 * @date 2018-02-08 15:36:13
	 */
	public static String[] intersect(String[] arr1, String[] arr2) {
		List<String> l = new LinkedList<String>();
		Set<String> common = new HashSet<String>();
		for (String str : arr1) {
			if (!l.contains(nvl(str))) {
				l.add(nvl(str));
			}
		}

		for (String str : arr2) {
			if (l.contains(nvl(str))) {
				common.add(nvl(str));
			}
		}

		String[] result = {};
		return common.toArray(result);
	}

	/**
	 * SHA-256 編碼
	 * 
	 * @Method: sha256
	 * @author Hank_Chuang
	 * @param value 編碼字串
	 * @return
	 * @throws Exception
	 * @date 2018-02-08 15:36:52
	 */
	public static String sha256(String value) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(value.getBytes("UTF-8"));

		StringBuffer sb = new StringBuffer();
		for (byte byt : md.digest()) {
			sb.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}

	/**
	 * 字串轉換回 byte array
	 * 
	 * @Method: hex2bytes
	 * @author Hank_Chuang
	 * @param s 字串值
	 * @return
	 * @date 2018-02-08 15:37:46
	 */
	public static byte[] hex2bytes(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}

		return data;
	}

	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}

	/**
	 * byte array 轉回字串
	 * 
	 * @Method: hexDump
	 * @author Hank_Chuang
	 * @param data byte array 資料
	 * @return
	 * @date 2018-02-08 15:38:17
	 */
	public static String hexDump(byte[] data) {
		final String hexCodes = "0123456789ABCDEF";

		int len = data.length;

		char[] ret = new char[len * 2];

		byte digit;
		int j = 0;

		for (int i = 0; i < len; ++i) {
			// mask & get the first 4 bits of the byte
			digit = (byte) ((data[i] & 0xF0l) >>> 4);
			// convert to hex
			ret[j++] = hexCodes.charAt(digit);
			// mask & get the last 4 bits of the byte
			digit = (byte) (data[i] & 0x0Fl);
			// convert to hex
			ret[j++] = hexCodes.charAt(digit);
		}

		return (new String(ret));
	}

	/**
	 * 轉換為Hex String
	 * 
	 * @Method: toHexString
	 * @author Hank_Chuang
	 * @param in byte array 轉換前資料
	 * @param c  轉換資料插入字串(符號)
	 * @return
	 * @throws Exception
	 * @date 2018-02-08 15:38:50
	 */
	public static String toHexString(byte[] in, String c) throws Exception {
		StringBuilder hexString = new StringBuilder();

		for (int i = 0; i < in.length; i++) {
			String hex = Integer.toHexString(0xFF & in[i]);

			if (hexString.length() > 0) {
				hexString.append(c);
			}

			if (hex.length() == 1) {
				hexString.append('0');
			}

			hexString.append(hex);

		}

		return hexString.toString();
	}

	/**
	 * Print Stack Track
	 * 
	 * @Method: getStackTrace
	 * @author Hank_Chuang
	 * @param t Throwable
	 * @return
	 * @date 2018-02-08 15:39:42
	 */
	public static String getStackTrace(Throwable t) {

		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));

		return sw.toString();

	}

	/**
	 * 字串遮蔽處理 (ex. shieldOtherStr("A123456789", 3, 3) 結果: A12****789,
	 * shieldOtherStr("A123456789", 0, 3) 結果: *******789 )
	 * 
	 * @Method: shieldOtherStr
	 * @author Hank_Chuang
	 * @param str     字串內容
	 * @param ibefore 前幾碼 (顯示)
	 * @param iafter  後幾碼 (顯示)
	 * @return
	 * @date 2018-02-08 16:23:15
	 */
	public static String shieldOtherStr(String str, int ibefore, int iafter) {
		return shieldOtherStr(str, ibefore, iafter, "*");
	}

	/**
	 * 字串遮蔽處理 (ex. shieldOtherStr("A123456789", 3, 3, "*") 結果: A12****789,
	 * shieldOtherStr("A123456789", 0, 3, "*") 結果: *******789 )
	 * 
	 * @Method: shieldOtherStr
	 * @author Hank_Chuang
	 * @param str     字串內容
	 * @param ibefore 前幾碼 (顯示)
	 * @param iafter  後幾碼 (顯示)
	 * @param symbol  遮蔽符號
	 * @return
	 * @date 2018-02-08 16:26:40
	 */
	public static String shieldOtherStr(String str, int ibefore, int iafter, String symbol) {
		StringBuffer sb = new StringBuffer();
		if (!isBlank(str)) {
			char[] c = str.toCharArray();
			int iA = (c.length - iafter) < 0 ? 0 : (c.length - iafter);
			for (int i = 0; i < c.length; i++) {
				if (ibefore > i) {
					sb.append(c[i]);
				} else if ((i + 1) > iA) {
					sb.append(c[i]);
				} else {
					sb.append(symbol);
				}
			}
		}

		return sb.toString();
	}

	/**
	 * 取得 i18n訊息
	 * 
	 * @Method: getLocaleMessage
	 * @author Hank_Chuang
	 * @param key              關鍵值
	 * @param resourceBaseName 多語系檔名稱
	 * @param locale           語系 locale
	 * @return
	 * @date 2018-02-08 16:28:23
	 */
	public static String getLocaleMessage(String key, String resourceBaseName, Locale locale) {
		return ResourceBundle.getBundle(resourceBaseName, locale).getString(key);
	}

	/**
	 * 取得 亂數產生密碼
	 * 
	 * @param hasNumeric   是否有數字
	 * @param hasUpperCase 是否有大寫英文字母
	 * @param hasLowerCase 是否有小寫字母
	 * @param pwdLength    密碼長度
	 * @return
	 */
	public static String getRandomPWD(Boolean hasNumeric, Boolean hasUpperCase, Boolean hasLowerCase, int pwdLength) {

		int z;
		StringBuilder sb = new StringBuilder();
		while (sb.length() < pwdLength) {
			z = (int) ((Math.random() * 7) % 3);

			if (z == 1) { // 放數字
				if (hasNumeric) {
					sb.append((int) ((Math.random() * 10) + 48));
				}
			} else if (z == 2) { // 放大寫英文
				if (hasUpperCase) {
					sb.append((char) (((Math.random() * 26) + 65)));
				}
			} else {// 放小寫英文
				if (hasLowerCase) {
					sb.append(((char) ((Math.random() * 26) + 97)));
				}
			}
		}

		return sb.toString();
	}

	/**
	 * 判斷是否為空陣列
	 * 
	 * @Method: isEmptyByteArray
	 * @author Tim_Kao
	 * @param byteArray
	 * @return true:空; false:非空
	 * @date 2018-08-24 15:53:28
	 */
	public static boolean isEmptyByteArray(byte[] byteArray) {
		return StringUtil.isBlank(byteArray) || byteArray.length == 0;
	}

	/**
	 * 取代字串空白
	 * 
	 * @Method: replaceBlank
	 * @author Shirley_Wang
	 * @param str
	 * @return
	 * @date 2018-10-15 14:31:30
	 */
	public static String replaceBlank(String str) {
		if (!StringUtil.isBlank(str)) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			str = m.replaceAll("");
			str = str.replaceAll("　", "");
		}
		return str;
	}

	/**
	 * 取代轉換符號
	 * 
	 * @Method: replaceSymbol
	 * @author Shirley_Wang
	 * @param str
	 * @return
	 * @date 2018-10-15 14:31:30
	 */
	public static String replaceSymbol(String str) {

		if (!StringUtil.isBlank(str)) {
			str = str.replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"")
					.replace("&#39;", "'");
		}

		return str;
	}

	/**
	 * 將傳入的字串值，把特殊字改為＊(鯨躍發票開立用)
	 * 
	 * @param brfString
	 * @return 將特殊字 改為＊
	 */
	public static String checkHardWord(String brfString) {
		if (brfString == null)
			return "";

		String afterString = brfString;

		while (checkExtendedChar(afterString) != -1) {
			int j = checkExtendedChar(afterString);
			if (j != -1) {
				afterString = afterString.substring(0, j - 1) + "＊"
						+ afterString.substring(j + 1, afterString.length());
			}
		}

		return afterString;
	}

	/**
	 * 判斷傳入值是否有特殊字，若無回傳-1，若有回傳所在位置
	 * 
	 * @param sequence
	 * @return 特殊字所在位置
	 */
	private static int checkExtendedChar(CharSequence sequence) {
		int count = 0;
		for (int i = 0, len = sequence.length(); i < len; i++) {
			char ch = sequence.charAt(i);
			if (ch <= 0x7F) {
				count++;
			} else if (ch <= 0x7FF) {
				count++;
			} else if (Character.isHighSurrogate(ch)) {
				return count + 1;
			} else {
				count++;
			}
		}

		return -1;

	}

	/**
	 * 判斷傳入參數值是否為純Ascii碼
	 * 
	 * @param cs 傳入值
	 * @return 是否為純Ascii碼
	 */
	public static boolean isPureAscii(CharSequence cs) {
		return StandardCharsets.US_ASCII.newEncoder().canEncode(cs);
	}

	/**
	 * 去除html標簽
	 * 
	 * @Method delHTMLTag
	 * @author LouiseLiu
	 * @param htmlStr
	 * @return
	 * @date 2020-08-06 10:39:37
	 */
	public static String delHTMLTag(String htmlStr) {
		String regEx_script = "<script[^>]*?>[//s//S]*?<///script>"; // 定義script的正則表示式
		String regEx_style = "<style[^>]*?>[//s//S]*?<///style>"; // 定義style的正則表示式
		String regEx_html = "<[^>]+>"; // 定義HTML標籤的正則表示式

		Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
		Matcher m_script = p_script.matcher(htmlStr);
		htmlStr = m_script.replaceAll(""); // 過濾script標籤

		Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
		Matcher m_style = p_style.matcher(htmlStr);
		htmlStr = m_style.replaceAll(""); // 過濾style標籤

		Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
		Matcher m_html = p_html.matcher(htmlStr);
		htmlStr = m_html.replaceAll(""); // 過濾html標籤

		// 另外新增要取代的
		htmlStr = htmlStr.replace("&nbsp;", "");
		htmlStr = htmlStr.replace("&lt;", "<");
		htmlStr = htmlStr.replace("&gt;", ">");
		htmlStr = htmlStr.replace("&quot;", "\"");
		htmlStr = htmlStr.replace("&apos;", "'");

		return htmlStr.trim(); // 返回文字字串
	}

	public static String encodeHexString(byte[] byteArray) {
		StringBuffer hexStringBuffer = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			hexStringBuffer.append(byteToHex(byteArray[i]));
		}
		return hexStringBuffer.toString();
	}

	public static String byteToHex(byte num) {
		char[] hexDigits = new char[2];
		hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
		hexDigits[1] = Character.forDigit((num & 0xF), 16);
		return new String(hexDigits);
	}

	/**
	 * 將指定符號轉換成全形
	 * 
	 * @Method: replaceSymbolFull
	 * @author
	 * @param str
	 * @return
	 * @date 2021-05-28 11:26:07
	 */
	public static String replaceSymbolFull(String str) {

		if (!StringUtil.isBlank(str)) {
			str = str.replace("&quot;", "＂");
		}

		return str;
	}

	/**
	 * 全形轉半形
	 * 
	 * @Method: tranText
	 * @param str
	 * @return
	 * @date 2022-07-22 16:42:54
	 */
	public static String replaceSymbolHalf(String str) {
		for (char c : str.toCharArray()) {
			if ((int) c >= 65281 && (int) c <= 65374) {
				str = str.replace(c, (char) (((int) c) - 65248));
			}
		}
		return str;
	}

	/**
	 * 檢核email格式
	 * 
	 * @Method checkEmail
	 * @Authod Joseph_shih
	 * @param email
	 * @return
	 * @date 2021-09-27 15:01:02
	 */
	public static boolean checkEmail(String email) {
		if (isBlank(email)) {
			return false;
		}
		Pattern pattern = Pattern.compile(
				"^((([a-z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`\\{\\|\\}~]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])+(\\.([a-z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`\\{\\|\\}~]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])+)*)|((\\x22)((((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(([\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x7f]|\\x21|[\\x23-\\x5b]|[\\x5d-\\x7e]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(\\\\([\\x01-\\x09\\x0b\\x0c\\x0d-\\x7f]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF]))))*(((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(\\x22)))@((([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.)+(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.?$");
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	/**
	 * 檢核手機格式
	 * 
	 * @Method checkMobilePhone
	 * @Authod Joseph_shih
	 * @param mobile_phone
	 * @return
	 * @date 2021-09-27 15:01:25
	 */
	public static boolean checkMobilePhone(String mobile_phone) {
		Pattern pattern = Pattern.compile("^09\\d{8}$");
		Matcher matcher = pattern.matcher(mobile_phone);
		return matcher.matches();
	}

	/**
	 * 替換Header資料中,可能被攻擊的字元
	 * 
	 * @author JosephShih
	 * @since 2022年6月14日 上午10:12:17
	 * @param headerValue
	 * @return
	 */
	public static String headerSecurityReplace(String headerValue) {
		List<String> list = new ArrayList<String>();
		list.add("%0a");
		list.add("%0A");
		list.add("%0d");
		list.add("%0D");
		list.add("\r");
		list.add("\n");
		String normalize = Normalizer.normalize(headerValue, Normalizer.Form.NFKC);
		for (String str : list) {
			normalize = normalize.replace(str, "");
		}
		return normalize;
	}

	/**
	 * 替換路徑,可能被攻擊的字元
	 * 
	 * @author JosephShih
	 * @since 2022年6月14日 上午10:31:09
	 * @param filepath
	 * @return
	 */
	public static String validFilePath(String filepath) {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("1", "1");
		map.put("2", "2");
		map.put("3", "3");
		map.put("4", "4");
		map.put("5", "5");
		map.put("6", "6");
		map.put("7", "7");
		map.put("8", "8");
		map.put("9", "9");
		map.put("0", "0");

		map.put("a", "a");
		map.put("b", "b");
		map.put("c", "c");
		map.put("d", "d");
		map.put("e", "e");
		map.put("f", "f");
		map.put("g", "g");
		map.put("h", "h");
		map.put("i", "i");
		map.put("j", "j");
		map.put("k", "k");
		map.put("l", "l");
		map.put("m", "m");
		map.put("n", "n");
		map.put("o", "o");
		map.put("p", "p");
		map.put("q", "q");
		map.put("r", "r");
		map.put("s", "s");
		map.put("t", "t");
		map.put("u", "u");
		map.put("v", "v");
		map.put("w", "w");
		map.put("x", "x");
		map.put("y", "y");
		map.put("z", "z");

		map.put("A", "A");
		map.put("B", "B");
		map.put("C", "C");
		map.put("D", "D");
		map.put("E", "E");
		map.put("F", "F");
		map.put("G", "G");
		map.put("H", "H");
		map.put("I", "I");
		map.put("J", "J");
		map.put("K", "K");
		map.put("L", "L");
		map.put("M", "M");
		map.put("N", "N");
		map.put("O", "O");
		map.put("P", "P");
		map.put("Q", "Q");
		map.put("R", "R");
		map.put("S", "S");
		map.put("T", "T");
		map.put("U", "U");
		map.put("V", "V");
		map.put("W", "W");
		map.put("X", "X");
		map.put("Y", "Y");
		map.put("Z", "Z");

		map.put(".", ".");
		map.put(":", ":");
		map.put("/", "/");
		map.put("\\", "\\");
		map.put("_", "_");

		String temp = "";
		for (int i = 0; i < filepath.length(); i++) {
			if (map.get(filepath.charAt(i) + "") != null) {
				temp += map.get(filepath.charAt(i) + "");
			}
		}
		filepath = temp;

		return filepath;

	}

	/**
	 * 去除字串中所包含的空格（包括:空格(全形，半形)、製表符、換頁符等）
	 * 
	 * @param s
	 * @return
	 */
	public static String trimAll(String s) {
		String result = "";
		if (s == null) {
			return "";
		} else if (null != s && StringUtil.isNotBlank(s)) {
			result = s.replaceAll("[　*| *| *|//s*|\"*]*", "");
		}
		return result;
	}

	/**
	 * HmacSHA256加密並轉Base64
	 * 
	 * @Method: encodeHmacSHA256AndEncodeBase64
	 * @param key
	 * @param message
	 * @return
	 */
	public static String hmacSHA256ToBase64(String key, String message) {
		String returnMessage = "";
		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
			sha256_HMAC.init(secret_key);
			byte[] hmacData = sha256_HMAC.doFinal(message.getBytes("UTF-8"));
			returnMessage = java.util.Base64.getEncoder().encodeToString(hmacData);
		} catch (Exception e) {
			LOGGER.error("", e);
		}

		return returnMessage;
	}
}
