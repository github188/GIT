package com.efuture.commonKit;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;

//这个类用于控制精度
public class ManipulatePrecision
{
	private static final int DEF_DIV_SCALE = 10;

	public ManipulatePrecision()
	{
	}

	public static double add(double v1, double v2)
	{
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));

		return b1.add(b2).doubleValue();
	}

	public static double sub(double v1, double v2)
	{
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));

		return b1.subtract(b2).doubleValue();
	}

	public static double mul(double v1, double v2)
	{
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));

		return b1.multiply(b2).doubleValue();
	}

	public static double div(double v1, double v2)
	{
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));

		return b1.divide(b2, DEF_DIV_SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	// 取余数
	public static double mod(double v1, double v2)
	{
		if (v2 == 0)
			return v1;

		double a = div(v1, v2);
		int b = (int) a;
		double c = mul(b, v2);
		return sub(Math.abs(v1), Math.abs(c));
	}

	// 比较小数
	public static int doubleCompare(double f1, double f2, int dec)
	{
		int i;
		float f = 1.00F;

		for (i = 0; i <= dec; i++)
		{
			f /= 10.00;
		}

		f1 = doubleConvert(f1, dec, 1);
		f2 = doubleConvert(f2, dec, 1);

		if (Math.abs(f1 - f2) <= f) { return 0; }

		if (f1 > f2) { return 1; }

		return -1;
	}

	public static int getDoubleSign(double f)
	{
		return (doubleCompare(f, 0, 2) >= 0) ? 1 : (-1);
	}

	public static int getDoubleScale(double f)
	{
		int i = 0;
		double d;

		do
		{
			d = f * Math.pow(10.0, i);
			if (d == (long) d)
				return i;
			i++;
		} while (true);
	}

	public static String doubleToString(double value)
	{
		return doubleToString(value, 2, 1, false, 0);
	}

	public static String doubleToString(double value, int dec, int flag)
	{
		return doubleToString(value, dec, flag, false, 0);
	}

	public static String doubleToString(double value, int dec, int flag, boolean subdec)
	{
		return doubleToString(value, dec, flag, subdec, 0);
	}

	public static String getXMLItem(String input, String point)
	{
		int len = point.length();
		int start = input.indexOf("<" + point + ">");
		int end = input.indexOf("</" + point + ">");
		if (start >= 0 && end >= 0) { return input.substring(start + len + 2, end); }
		return null;
	}

	public static String doubleToString(double value, int dec, int flag, boolean subdec, int rightwidth)
	{
		StringBuffer sb = new StringBuffer("0");

		if (dec > 0)
			sb.append(".");
		for (int i = 0; i < dec; i++)
			sb.append("0");

		DecimalFormat df = new DecimalFormat(sb.toString());

		String s = df.format(doubleConvert(value, dec, flag));

		// 去掉位部0
		if (subdec)
		{
			while (s.charAt(s.length() - 1) == '0' || s.charAt(s.length() - 1) == '.')
			{
				if (s.charAt(s.length() - 1) == '.')
				{
					s = s.substring(0, s.length() - 1);
					break;
				}
				else
				{
					s = s.substring(0, s.length() - 1);
				}
			}
		}

		if (rightwidth > 0)
		{
			s = Convert.increaseCharForward(s, rightwidth);
		}

		return s;
	}

	// 浮点运算1 = 0.999999,需要进位到两位小数再取整
	// 浮点运算299/300 = 0.996666,进位取整=1,还需再乘分母用金额比较，如果大倍数要减1
	public static int integerDiv(double f1, double f2)
	{
		double fz = Math.abs(f1);
		double fm = Math.abs(f2);
		if (fm == 0)
			fm = 1;

		int num = (int) doubleConvert(fz / fm);
		if (doubleCompare(fm * num, fz, 2) > 0)
			num--;
		if (num < 0)
			num = 0;

		return num;
	}

	public static double doubleConvert(double f)
	{
		return doubleConvert(f, 2, 1);
	}

	// 转换数,f:表示传入值,dec:表示精确位数,flag:表示0就是截断,1表示四舍五入
	public static double doubleConvert(double f, int dec, int flag, boolean r)
	{
		try
		{
			if (flag == 1)
			{
				double d = f;
				// 先计算数字f 精度后2位的精度
				if (!r)
					d = doubleConvert(d, dec + 2, 1, true);
				d = mul(d, Math.pow(10.0, dec));
				return (Math.round(d)) / (Math.pow(10.0, dec));
			}
			else
			{
				// BigDecimal b = new BigDecimal(Math.abs(f)).setScale(dec,
				// BigDecimal.ROUND_FLOOR);
				// return b.doubleValue() * (f < 0?-1:1);
				double d = Math.abs(f);
				if (!r)
					d = doubleConvert(d, dec + 2, 1, true);
				d = mul(d, Math.pow(10.0, dec));
				return (Math.floor(d)) / (Math.pow(10.0, dec) * (f < 0 ? -1 : 1));
			}
		}
		catch (Exception ex)
		{
			return 0;
		}
	}

	// 转换数,f:表示传入值,dec:表示精确位数,flag:表示0就是截断,1表示四舍五入
	public static double doubleConvert(double f, int dec, int flag)
	{
		return doubleConvert(f, dec, flag, false);
	}

	public static String[][] registerFunction = { { "1", "金卡工程" }, { "2", "双屏广告" }, { "4", "" }, { "8", "" }, { "16", "" } };
	public static final int REGBANK = 1;
	public static final int REGMOVIE = 2;
	public static final int REGFUNC1 = 4;
	public static final int REGFUNC2 = 8;
	public static final int REGFUNC3 = 16;

	public static boolean checkRegisterFunction(String regcode, int func)
	{
		if (regcode == null || regcode.length() < 29)
			return false;

		// 序号前2位小于32,是为老项目注册码，为兼容不控制功能授权
		int seqno = Convert.toInt(getRegisterCodeSeqno(regcode).substring(0, 2));
		if (seqno < 32)
			return true;

		// 去掉最高位，按位进行&运算，检查是否匹配功能
		seqno &= ~32;
		seqno &= func;
		if (seqno > 0)
			return true;
		else
			return false;
	}

	public static String getRegisterFunctionModel()
	{
		if (PathFile.fileExist(GlobalVar.ConfigPath + "/ModelFunc.ini"))
		{
			BufferedReader br = null;
			try
			{
				String modelfunc = null;
				br = CommonMethod.readFile(GlobalVar.ConfigPath + "/ModelFunc.ini");
				if (br != null)
				{
					String line = null;
					while ((line = br.readLine()) != null)
					{
						if (line != null && line.trim().length() >= 29)
						{
							modelfunc = line.trim();
							break;
						}
					}
					br.close();
					br = null;
				}

				// 定义了扩展注册授权码,检查授权
				if (modelfunc != null)
				{
					String key = getRegisterCodeKey(ConfigClass.CDKey);
					String strseq = getRegisterCodeSeqno(modelfunc);
					String code1 = ManipulatePrecision.getRegisterCode("", "", strseq, key);
					String code2 = ManipulatePrecision.getRegisterCode(GlobalInfo.sysPara != null ? GlobalInfo.sysPara.mktname : "", GlobalInfo.sysPara != null ? GlobalInfo.sysPara.mktcode : "", strseq, key);
					if (modelfunc.startsWith(code1) || modelfunc.startsWith(code2)) { return modelfunc; }
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if (br != null)
				{
					try
					{
						br.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}

		return null;
	}

	public static String getRegisterCode(String cust, String mkt, String seq, String keys)
	{
		long Num1, Num2, Num3, Num4;
		int i, len, j, k, keylen, keysum;
		StringBuffer reg;
		char[] sn;
		String key, user, strseq;

		reg = new StringBuffer();
		Num1 = 0;
		Num2 = 0;
		Num3 = 0;
		Num4 = 0;

		if (keys.length() < 4)
			key = "0000".substring(0, 4 - keys.length()) + keys;
		else
			key = keys.substring(0, 4);
		key = key.toUpperCase();
		keysum = key.charAt(0) + key.charAt(1) + key.charAt(2) + key.charAt(3);

		if (seq.length() < 5)
			strseq = "00000".substring(0, 5 - seq.length()) + seq;
		else
			strseq = seq.substring(0, 5);
		strseq = strseq.toUpperCase();

		user = cust + mkt + strseq;

		len = user.length();
		keylen = key.length();
		if (len > 0)
		{
			j = 1;
			for (i = 1; i <= len; i++)
			{
				// KEY
				if (keylen <= 0)
					k = 0;
				else
				{
					k = (int) key.charAt(j - 1) * keysum;
					j++;
					if (j > keylen)
						j = 1;
				}

				// 第一步算法
				Num1 = ((long) (Num1 + ((int) user.charAt(i - 1) * i * i) * (i * (int) user.charAt(i - 1) + 1) + k * keylen * j)) % 100000;

				// 第二步算法
				Num2 = (Num2 * i + ((int) user.charAt(i - 1) * i) + k * keylen * j) % 100000;

				// 第三步算法
				Num3 = (Num2 + Num1 + ((int) user.charAt(i - 1) * i + 1) + k * keylen * j) % 100000;

				// 第四步算法
				Num4 = (Num3 + Num2 + Num1 + ((int) user.charAt(i - 1) * i) + k * keylen * j) % 100000;
			}

			// 以下把四个算法结果分别生成5个字符,共有20个
			sn = new char[20];
			for (i = 0; i < 5; i++)
				sn[i] = (char) ((Num1 + 31 + i * i * i + len) % 128);
			for (i = 5; i < 10; i++)
				sn[i] = (char) ((Num2 + 31 + i * i * i + len) % 128);
			for (i = 10; i < 15; i++)
				sn[i] = (char) ((Num3 + 31 + i * i * i + len) % 128);
			for (i = 15; i < 20; i++)
				sn[i] = (char) ((Num4 + 31 + i * i * i + len) % 128);

			// 以下循环把所有生成的字符转换为0---9，A---Z
			for (i = 0; i < 20; i++)
			{
				while ((sn[i] < '0' || sn[i] > '9') && (sn[i] < 'A' || sn[i] > 'Z'))
				{
					sn[i] = (char) ((sn[i] + 31 + 7 * i) % 128);
				}
			}

			// 赋值给一个string变量，用做函数返回值
			reg = reg.append(sn);
			reg = reg.insert(5, "-");
			reg = reg.insert(11, "-");
			reg = reg.insert(17, "-");

			//
			reg.setCharAt(2, key.charAt(0));
			reg.setCharAt(8, key.charAt(1));
			reg.setCharAt(14, key.charAt(2));
			reg.setCharAt(20, key.charAt(3));

			//
			reg.append("-" + strseq);
		}

		return reg.toString();
	}

	public static String getRegisterCodeKey(String regcode)
	{
		char[] key = new char[4];

		if (regcode == null || regcode.length() < 29)
			return "";

		key[0] = regcode.charAt(2);
		key[1] = regcode.charAt(8);
		key[2] = regcode.charAt(14);
		key[3] = regcode.charAt(20);

		return String.valueOf(key);
	}

	public static String getRegisterCodeSeqno(String regcode)
	{
		if (regcode == null || regcode.length() < 29)
			return "";

		return regcode.substring(24, 29);
	}

	public static String readApplicationModuleType()
	{
		if (PathFile.fileExist("moduletype.ini"))
		{
			BufferedReader br = null;
			try
			{
				br = CommonMethod.readFile("moduletype.ini");
				if (br != null)
				{
					String line = null;
					while ((line = br.readLine()) != null)
					{
						if (line != null && line.trim().length() >= 4) { return line.trim().substring(0, 4); }
					}
					br.close();
					br = null;
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if (br != null)
				{
					try
					{
						br.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}

		return null;
	}

	static final String gs_key_para = "Bvp01CeFM234H5ljZhiG6rstDu789abnxIfPgQcEdOqyTwNJmWXUYKLkozARSV";

	public static String EncodeString(String strCode, String strKey)
	{
		StringBuffer strEncode = null, strTemp = null;
		char c;
		int i, pre, key, j, k, keylen;

		if (strCode == null)
			return null;
		strEncode = new StringBuffer();
		strTemp = new StringBuffer();

		j = 0;
		for (i = 0; i < strCode.length(); i++)
			j += (i + 1) * strCode.charAt(i);
		String codeKey = getRegisterCode(strCode, String.valueOf(j), "99999", strKey).substring(0, 23).replaceAll("-", "");
		strTemp.append(strCode);
		for (i = 0, j = 0; j < 20 && j < strCode.length(); j++, i += 2)
			strTemp.insert(i, codeKey.charAt(j));
		strCode = strTemp.toString();

		j = 1;
		keylen = strKey.length();

		strEncode.delete(0, strEncode.length());
		strTemp.delete(0, strTemp.length());
		for (i = 0; i < strCode.length(); i++)
		{
			// KEY
			if (keylen <= 0)
				k = 0;
			else
			{
				k = (int) strKey.charAt(j - 1);
				j++;
				if (j > keylen)
					j = 1;
			}

			//
			c = strCode.charAt(i);

			if ((int) c < 256)
			{
				if (i % 2 == 0)
					pre = ((int) c) % 16;
				else
					pre = ((int) c) / 16;
				if (pre == 0)
				{
					pre = 1;
					strTemp.append('1');
				}
				else
					strTemp.append(Integer.toString((int) c, 16).charAt((i + 1) % 2));

				key = pre * ((int) c) + (k + keylen * j) % 4;
				strTemp.append(gs_key_para.charAt(key / 62));
				strTemp.append(gs_key_para.charAt(key % 62));
			}
			else
			{
				strTemp.append('G');
				key = ((int) c + (k + keylen * j) % 256) / 256;
				strTemp.append(gs_key_para.charAt(key / 62));
				strTemp.append(gs_key_para.charAt(key % 62));
				key = ((int) c + (k + keylen * j) % 256) % 256;
				strTemp.append(gs_key_para.charAt(key / 62));
				strTemp.append(gs_key_para.charAt(key % 62));
			}
		}
		for (i = 0; i < strTemp.length(); i++)
			if ((i + 1) % 2 == 0)
				strEncode.append(strTemp.charAt(i));
		for (i = 0; i < strTemp.length(); i++)
			if ((i + 1) % 2 != 0)
				strEncode.append(strTemp.charAt(i));

		return strEncode.toString();
	}

	public static String DecodeString(String strCode, String strKey)
	{
		StringBuffer strDecode, strTemp;
		char c;
		int i, n, pre, key, j, k, keylen;

		if (strCode == null)
			return null;

		j = 1;
		keylen = strKey.length();

		strDecode = new StringBuffer();
		strTemp = new StringBuffer();
		for (i = 0; i < strCode.length() / 2; i++)
		{
			strTemp.append(strCode.charAt(strCode.length() / 2 + i));
			strTemp.append(strCode.charAt(i));
		}
		if (strTemp.length() < strCode.length())
			strTemp.append(strCode.charAt(strCode.length() / 2 + i));
		for (i = 0; i < strTemp.length(); i += 3)
		{
			// KEY
			if (keylen <= 0)
				k = 0;
			else
			{
				k = (int) strKey.charAt(j - 1);
				j++;
				if (j > keylen)
					j = 1;
			}

			c = strTemp.substring(i, i + 1).toUpperCase().charAt(0);
			if (c >= '0' && c <= 'F')
			{
				pre = Integer.parseInt(String.valueOf(c), 16);

				key = 0;

				if (i + 1 < strTemp.length())
					c = strTemp.substring(i + 1, i + 2).charAt(0);
				else
					return strDecode.toString();
				for (n = 0; n < 62; n++)
					if (gs_key_para.charAt(n) == c)
					{
						key += n * 62;
						break;
					}

				if (i + 2 < strTemp.length())
					c = strTemp.substring(i + 2, i + 3).charAt(0);
				else
					return strDecode.toString();
				for (n = 0; n < 62; n++)
					if (gs_key_para.charAt(n) == c)
					{
						key += n;
						break;
					}

				if (pre <= 0)
					return strDecode.toString();
				else
					c = (char) ((key - (k + keylen * j) % 4) / pre);

				strDecode.append(c);
			}
			else
			{
				key = 0;

				if (i + 1 < strTemp.length())
					c = strTemp.substring(i + 1, i + 2).charAt(0);
				else
					return strDecode.toString();
				for (n = 0; n < 62; n++)
					if (gs_key_para.charAt(n) == c)
					{
						key += n * 62;
						break;
					}

				if (i + 2 < strTemp.length())
					c = strTemp.substring(i + 2, i + 3).charAt(0);
				else
					return strDecode.toString();
				for (n = 0; n < 62; n++)
					if (gs_key_para.charAt(n) == c)
					{
						key += n;
						break;
					}

				key *= 256;
				if (i + 3 < strTemp.length())
					c = strTemp.substring(i + 3, i + 4).charAt(0);
				else
					return strDecode.toString();
				for (n = 0; n < 62; n++)
					if (gs_key_para.charAt(n) == c)
					{
						key += n * 62;
						break;
					}

				if (i + 4 < strTemp.length())
					c = strTemp.substring(i + 4, i + 5).charAt(0);
				else
					return strDecode.toString();
				for (n = 0; n < 62; n++)
					if (gs_key_para.charAt(n) == c)
					{
						key += n;
						break;
					}

				i += 2;

				c = (char) (key - (k + keylen * j) % 256);
				strDecode.append(c);
			}
		}

		i = strDecode.length() / 2;
		for (j = 0; j < 20 && j < i; j++)
			strDecode.deleteCharAt(j);

		return strDecode.toString();
	}

	// 加密算法,返回s2
	public static String getEncrypt(String s1)
	{
		int len = s1.length();
		int k = 0;
		boolean signal = false; // 表示后面的字节是否需要根据前面的字节组成一个汉字
		byte[] s2 = new byte[s1.length()];
		String passwd = "";

		if ((s1 == null) || s1.equals("")) { return ""; }

		for (int i = 0; i < len; i++)
		{
			k = 255 - s1.charAt(i);

			for (int j = 0; j < (len - i); j++)
			{
				k = ((2 * k) % 256) + (k / 128);
			}

			passwd = passwd + "," + k;
			s2[i] = (byte) k;

			if (signal)
			{
				signal = false;
			}
			else
			{
				if ((int) k > 127)
				{
					signal = true;
				}
			}
		}

		try
		{
			passwd = passwd.substring(1);

			return passwd;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/*
	 * //浮点转化为字符串 public static String getFloatConverStr(double f, int len, int
	 * dec, int flag) { String str = " "; String str1 = ""; int flen = 0;
	 * 
	 * if (flag == 1) { BigDecimal bd = new BigDecimal(f).setScale(dec, 4); flen
	 * = bd.toString().length(); str1 = bd.toString(); System.out.println(flen);
	 * System.out.println(len);
	 * 
	 * for (int i = flen; i < len; i++) { str1 = str1 + str; }
	 * 
	 * return str1.substring(0); } else { flen = String.valueOf(f).length();
	 * str1 = String.valueOf(f);
	 * 
	 * for (int i = flen; i < len; i++) { str1 += str; }
	 * 
	 * return str1.substring(0); } }
	 * 
	 * //获得文件大小 public static long getFileLength(String filename) { File file =
	 * new File(filename);
	 * 
	 * if (file.exists()) { return file.length(); } else { return 0L; } }
	 */
	// 浮点转化为中文
	public static String getFloatConverChinese(double value)
	{
		String strnum = doubleToString(value);
		String strCheck = null;
		String strFen = null;
		String strDW = null;
		String strNum = null;
		String strBig = null;
		String strNow = null;
		String strBear = "";

		if ((strnum == null) || strnum.trim().equals("")||value == 0) { return "零元整"; }
		
		double d = 0;

		try
		{
			d = Double.parseDouble(strnum);
		}
		catch (Exception ex)
		{
			return "数据" + value + "非法！";
		}

		strCheck = value + ".";

		int dot = strCheck.indexOf(".");

		if (dot > 12) { return "数据" + value + "过大，无法处理！"; }

		try
		{
			int i = 0;
			strBig = "";
			strDW = "";
			strNum = "";

			long intFen = (long) mul(d, 100);

			if (strnum.charAt(0) == '-')
			{
				strBear = "负";
				strFen = String.valueOf(intFen).substring(1);
			}
			else
			{
				strFen = String.valueOf(intFen);
			}

			int lenIntFen = strFen.length();

			while (lenIntFen != 0)
			{
				i++;

				switch (i)
				{
					case 1:
						strDW = "分";

						break;

					case 2:
						strDW = "角";

						break;

					case 3:
						strDW = "元";

						break;

					case 4:
						strDW = "拾";

						break;

					case 5:
						strDW = "佰";

						break;

					case 6:
						strDW = "仟";

						break;

					case 7:
						strDW = "万";

						break;

					case 8:
						strDW = "拾";

						break;

					case 9:
						strDW = "佰";

						break;

					case 10:
						strDW = "仟";

						break;

					case 11:
						strDW = "亿";

						break;

					case 12:
						strDW = "拾";

						break;

					case 13:
						strDW = "佰";

						break;

					case 14:
						strDW = "仟";

						break;
				}

				switch (strFen.charAt(lenIntFen - 1))
				{
					case '1':
						strNum = "壹";

						break;

					case '2':
						strNum = "贰";

						break;

					case '3':
						strNum = "叁";

						break;

					case '4':
						strNum = "肆";

						break;

					case '5':
						strNum = "伍";

						break;

					case '6':
						strNum = "陆";

						break;

					case '7':
						strNum = "柒";

						break;

					case '8':
						strNum = "捌";

						break;

					case '9':
						strNum = "玖";

						break;

					case '0':
						strNum = "零";

						break;
				}

				strNow = strBig;

				if ((i == 1) && (strFen.charAt(lenIntFen - 1) == '0'))
				{
					strBig = "整";
				}

				else if ((i == 2) && (strFen.charAt(lenIntFen - 1) == '0'))
				{
					if (!strBig.equals("整"))
					{
						strBig = "零" + strBig;
					}
				}
				else if ((i == 3) && (strFen.charAt(lenIntFen - 1) == '0'))
				{
					strBig = "元" + strBig;
				}
				else if ((i < 7) && (i > 3) && (strFen.charAt(lenIntFen - 1) == '0') && (strNow.charAt(0) != '零') && (strNow.charAt(0) != '元'))
				{
					strBig = "零" + strBig;
				}
				else if ((i < 7) && (i > 3) && (strFen.charAt(lenIntFen - 1) == '0') && (strNow.charAt(0) == '零'))
				{
				}

				else if ((i < 7) && (i > 3) && (strFen.charAt(lenIntFen - 1) == '0') && (strNow.charAt(0) == '元'))
				{
				}

				else if ((i == 7) && (strFen.charAt(lenIntFen - 1) == '0'))
				{
					strBig = "万" + strBig;
				}

				else if ((i < 11) && (i > 7) && (strFen.charAt(lenIntFen - 1) == '0') && (strNow.charAt(0) != '零') && (strNow.charAt(0) != '万'))
				{
					strBig = "零" + strBig;
				}
				else if ((i < 11) && (i > 7) && (strFen.charAt(lenIntFen - 1) == '0') && (strNow.charAt(0) == '万'))
				{
				}
				else if ((i < 11) && (i > 7) && (strFen.charAt(lenIntFen - 1) == '0') && (strNow.charAt(0) == '零'))
				{
				}
				else if ((i < 11) && (i > 8) && (strFen.charAt(lenIntFen - 1) == '0') && (strNow.charAt(0) == '万') && (strNow.charAt(2) == '仟'))
				{
					strBig = strNum + strDW + "万零" + strBig.substring(1, strBig.length());
				}

				else if (i == 11)
				{
					// 亿位为零且万全为零存在仟位时，去掉万补为零
					if ((strFen.charAt(lenIntFen - 1) == '0') && (strNow.charAt(0) == '万') && (strNow.charAt(2) == '仟'))
					{
						strBig = "亿" + "零" + strBig.substring(1, strBig.length());
					}

					// 亿位为零且万全为零不存在仟位时，去掉万
					else if ((strFen.charAt(lenIntFen - 1) == '0') && (strNow.charAt(0) == '万') && (strNow.charAt(2) != '仟'))
					{
						strBig = "亿" + strBig.substring(1, strBig.length());
					}

					// 亿位不为零且万全为零存在仟位时，去掉万补为零
					else if ((strNow.charAt(0) == '万') && (strNow.charAt(2) == '仟'))
					{
						strBig = strNum + strDW + "零" + strBig.substring(1, strBig.length());
					}

					// 亿位不为零且万全为零不存在仟位时，去掉万
					else if ((strNow.charAt(0) == '万') && (strNow.charAt(2) != '仟'))
					{
						strBig = strNum + strDW + strBig.substring(1, strBig.length());
					}

					// 其他正常情况
					else
					{
						strBig = strNum + strDW + strBig;
					}
				}

				else if ((i < 15) && (i > 11) && (strFen.charAt(lenIntFen - 1) == '0') && (strNow.charAt(0) != '零') && (strNow.charAt(0) != '亿'))
				{
					strBig = "零" + strBig;
				}

				else if ((i < 15) && (i > 11) && (strFen.charAt(lenIntFen - 1) == '0') && (strNow.charAt(0) == '亿'))
				{
				}

				else if ((i < 15) && (i > 11) && (strFen.charAt(lenIntFen - 1) == '0') && (strNow.charAt(0) == '零'))
				{
				}

				else if ((i < 15) && (i > 11) && (strFen.charAt(lenIntFen - 1) != '0') && (strNow.charAt(0) == '零') && (strNow.charAt(1) == '亿') && (strNow.charAt(3) != '仟'))
				{
					strBig = strNum + strDW + strBig.substring(1, strBig.length());
				}
				else if ((i < 15) && (i > 11) && (strFen.charAt(lenIntFen - 1) != '0') && (strNow.charAt(0) == '零') && (strNow.charAt(1) == '亿') && (strNow.charAt(3) == '仟'))
				{
					strBig = strNum + strDW + "亿零" + strBig.substring(2, strBig.length());
				}
				else
				{
					strBig = strNum + strDW + strBig;
				}

				strFen = strFen.substring(0, lenIntFen - 1);
				lenIntFen--;
			}

			if (strBear.equals("负"))
			{
				strBig = strBear + strBig;
			}

			return strBig;
		}
		catch (Exception exx)
		{
			exx.printStackTrace();

			return "";
		}
	}

	public static String getRandom()
	{
		String crcstr = String.valueOf(Math.round(Math.random() * 1000));

		if (crcstr.length() > 3)
		{
			return crcstr.substring(0, 3);
		}
		else
		{
			return Convert.increaseCharForward(crcstr, '0', 3);
		}

	}

	// 删除数字已外的字符
	public static String getFilterNumberNoStr(String str)
	{
		String str1 = "";

		for (int i = 0; i < str.length(); i++)
		{
			if (isNumber(String.valueOf(str.charAt(i))))
			{
				str1 = str1 + str.charAt(i);
			}
		}

		return str1;
	}

	public static boolean isNumber(String str)
	{
		try
		{
			if (str.matches("^(-?\\d+)(\\d+)?$"))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public static boolean isDoubleOrNumber(String str)
	{
		try
		{
			if (str.matches("^(-?\\d+)(\\.\\d+)?$"))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

}
