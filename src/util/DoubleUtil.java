package util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class DoubleUtil {
	/**
	 * 保留两位小数
	 * @param d
	 * @return
	 */
	public static String formetDouble(double d){
		return new DecimalFormat("#####0.00").format(d);
	}
	
	public static String formetDouble(String money){
		DecimalFormat df = new DecimalFormat("#####0.00");
		BigDecimal bigDecimal = new BigDecimal(money);
		return df.format(bigDecimal);
	}
	/**
	 * 校验Double后最多保�?2位小�?
	 * @param str
	 * @return
	 */
	public static boolean check2Double(String str){
		if (str.contains(".")&&str.indexOf(".") + 3 < str.length()) {
			return false;
		}else
			return true;
	}
}
