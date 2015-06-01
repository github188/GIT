package com.efuture.commonKit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import org.eclipse.swt.graphics.Point;

//这个类主要是用来操作字符串的
public class ManipulateStr
{
	final static int STR_HEAD = 0;
	final static int STR_BOTTOM = 1;
	final static int STR_ALL = 2;
	static ArrayList list = null;

	public static int getFileLength(String path)
	{
		File file = new File(path);

		try
		{
			// System.out.println(file.list().length);
			return file.list().length;
		}
		catch (Exception e)
		{
			return 0;
		}
	}

	public static Point covnertPoint(Point Plocation, Point Psize, Point childSize)
	{
		int x = (Plocation.x + (Psize.x / 2)) - (childSize.x / 2);
		int y = (Plocation.y + (Psize.y / 2)) - (childSize.y / 2);

		return new Point(x, y);
	}

	// 删除一个字符串的字符,第一个参数:传入一个字符串,第二个参数:传入一个字符,传入一个标记
	public static String getDelCharInStr(String str, char c, int flag)
	{
		if (str == null) { return null; }

		int strlength = str.length(); // 得到字符串的长度
		int index = -1;
		int charindex = -1;
		String newstr = "";

		// 从字符串头文件进行删除
		if (flag == ManipulateStr.STR_HEAD)
		{
			index = str.indexOf(c);

			if (index == 0)
			{
				for (int i = index; i < strlength; i++)
				{
					if (str.charAt(i) == c)
					{
						charindex = i;
					}
					else
					{
						break;
					}
				}

				newstr = str.substring(charindex + 1);
			}
			else
			{
				newstr = str;
			}
		}

		// 从字符串尾部进行删除
		if (flag == ManipulateStr.STR_BOTTOM)
		{
			index = str.lastIndexOf(c);

			if (index == (strlength - 1))
			{
				for (int i = index; i >= 0; i--)
				{
					if (str.charAt(i) == c)
					{
						charindex = i;
					}
					else
					{
						break;
					}
				}

				newstr = str.substring(0, charindex);
			}
			else
			{
				newstr = str;
			}
		}

		// 删除字符串中所包含的所有字符
		if (flag == ManipulateStr.STR_ALL)
		{
			index = str.indexOf(c);

			if (index >= 0)
			{
				String[] strsp = str.split(String.valueOf(c));

				for (int i = 0; i < strsp.length; i++)
				{
					if (strsp[i] != null)
					{
						newstr = newstr + strsp[i];
					}
				}

				System.out.println(newstr);
			}
			else
			{
				newstr = str;
			}
		}

		return newstr;
	}

	public static boolean textInString(String text, String str, String c, boolean ignore)
	{
		String[] s = str.split(c);
		for (int i = 0; s != null && i < s.length; i++)
		{
			if (ignore && s[i].trim().equalsIgnoreCase(text.trim()))
				return true;
			if (!ignore && s[i].trim().equals(text.trim()))
				return true;
		}
		return false;
	}

	// 参数:传入一个十六进制的字符串,返回十进制
	public static int getHexStrToNum(String str)
	{
		int val = 0;

		try
		{
			if (!(str.substring(0, 2).equalsIgnoreCase("0x"))) { return 0; }

			for (int i = 2; i < str.length(); i++)
			{
				if ((str.charAt(i) >= '0') && (str.charAt(i) <= '9'))
				{
					val = val * 16;
					val += (str.charAt(i) - '0');
				}

				if ((str.charAt(i) >= 'A') && (str.charAt(i) <= 'F'))
				{
					val = val * 16;
					val += (str.charAt(i) - '0' - 7);
				}

				if ((str.charAt(i) >= 'a') && (str.charAt(i) <= 'f'))
				{
					val = val * 16;
					val += (str.charAt(i) - '0' - 39);
				}
			}

			return val;
		}
		catch (Exception nsae)
		{
			nsae.printStackTrace();

			return 0;
		}
	}

	// 参数:传入一个十六进制的字符串,返回字符
	public static String getHexStrToChar(String bytes)
	{
		String hexString = "0123456789ABCDEF";

		ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);

		// 将每2位16进制整数组装成一个字节
		for (int i = 0; i < bytes.length(); i += 3)
		{
			baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString.indexOf(bytes.charAt(i + 1))));

		}

		return new String(baos.toByteArray());
	}

	// 传入十进制字符串,返回十六进制字符串
	public static String getNumToHexStr(String numstr)
	{
		if (numstr == null || numstr.equals(""))
			return null;

		String numstring[] = numstr.split(" ");
		String str = "";

		for (int i = 0; i < numstring.length; i++)
		{
			str = str + Convert.increaseCharForward(Integer.toHexString(Integer.parseInt(numstring[i])), '0', 2) + " ";
		}

		return str.substring(0, str.length() - 1);
	}

	public static String PadLeft(String str, int length, char padchar)
	{
		int count = length - str.length();

		for (int i = 0; i < count; i++)
		{
			str = padchar + str;
		}

		return str;
	}

	public static String PadRight(String str, int length, char padchar)
	{
		int count = length - str.length();

		for (int i = 0; i < count; i++)
		{
			str = str + padchar;
		}

		return str;
	}
	
	public static String PadChineseLeft(String str, int length, char padchar)
	{
		//汉字按两字节计算
		int count = length - countLength(str);

		for (int i = 0; i < count; i++)
		{
			str = padchar + str;
		}

		return str;
	}

	public static String PadChineseRight(String str, int length, char padchar)
	{
		int count = length - countLength(str);

		for (int i = 0; i < count; i++)
		{
			str = str + padchar;
		}

		return str;
	}

	// 计算字符串长度 中文 占2个字节
	public static int countLength(String line)
	{
		int length = 0;

		for (int i = 0; i < line.length(); i++)
		{
			char c = line.charAt(i);

			if ((int) c > 127)
			{
				length += 2;
			}
			else
			{
				length += 1;
			}
		}

		return length;
	}
	
	// 截取多于的字符串
	public static String interceptExceedStr(String value, int length)
	{
		try
		{
			if (value == null) { return ""; }

			if (value.trim().length() <= (length - 1)) { return value; }

			return value.trim().substring(0, length - 1);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return "";
		}
	}

	public static String trimRight(String value)
	{
		String result = value;
		if (result == null)
			return result;

		char ch[] = result.toCharArray();
		int endIndex = -1;

		for (int i = ch.length - 1; i > -1; i--)
		{
			if (Character.isWhitespace(ch[i]))
			{
				endIndex = i;
			}
			else
			{
				break;
			}
		}

		if (endIndex != -1)
		{
			result = result.substring(0, endIndex);
		}

		return result;
	}

	public static String delSpecialChar(String str)
	{
		StringBuffer buff = new StringBuffer(str.trim());

		for (int j = 0; j < buff.length(); j++)
		{
			char ch = buff.charAt(j);

			if (ch < 33 || ch > 126 || ch == 60 || ch == 62)
			{
				buff.deleteCharAt(j);
				j--;
			}
		}

		return buff.toString();
	}

	public static String getConvertStringNewLine(String str, int num)
	{
		String s = "";
		String s1 = "";

		for (int i = 0; i < str.length(); i++)
		{
			s = s + String.valueOf(str.charAt(i));

			byte b[] = s.getBytes();

			if (b.length >= num)
			{
				s = s + "\n";
				s1 = s1 + s;
				s = "";
			}
		}

		s1 = s1 + s;

		return s1;
	}

	// 指定标志和数量,返回指定的字符串
	public static String getIndexStr(String str, char flag, int amount)
	{
		int j = 0;

		for (int i = 0; i < str.length(); i++)
		{
			if (str.charAt(i) == flag)
			{
				j = j + 1;

				if (j == amount)
					return str.substring(i + 1);
			}

		}

		return str;
	}

	public static char[] getHexFromNumberStr(String code)
	{
		char[] hexcode = new char[code.length()];
		try
		{
			char[] tmpcode = code.toCharArray();

			for (int i = 0; i < tmpcode.length; i++)
			{
				if (tmpcode[i] == '0')
					hexcode[i] = 0x30;
				else if (tmpcode[i] == '1')
					hexcode[i] = 0x31;
				else if (tmpcode[i] == '2')
					hexcode[i] = 0x32;
				else if (tmpcode[i] == '3')
					hexcode[i] = 0x33;
				else if (tmpcode[i] == '4')
					hexcode[i] = 0x34;
				else if (tmpcode[i] == '5')
					hexcode[i] = 0x35;
				else if (tmpcode[i] == '6')
					hexcode[i] = 0x36;
				else if (tmpcode[i] == '7')
					hexcode[i] = 0x37;
				else if (tmpcode[i] == '8')
					hexcode[i] = 0x38;
				else if (tmpcode[i] == '9')
					hexcode[i] = 0x39;
			}

			return hexcode;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}

	}
	
	
	private static String hexString = "0123456789ABCDEF"; 	
	/*
	* 将字符串编码成16进制数字 */
	public static String getStrToHex(String str) {
		   // 根据默认编码获取字节数组
		   byte[] bytes = str.getBytes();
		   StringBuilder sb = new StringBuilder(bytes.length * 2);
		   // 将字节数组中每个字节拆解成2位16进制整数
		   for (int i = 0; i < bytes.length; i++) {
		    sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
		    sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
		   }
		   return sb.toString();
		}
	
	/*
	* 将16进制数字解码成字符串 */
	public static String getHexToStr(String bytes) {
	   ByteArrayOutputStream baos = new ByteArrayOutputStream(
	     bytes.length() / 2);
	   // 将每2位16进制整数组装成一个字节
	   for (int i = 0; i < bytes.length(); i += 2)
	    baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
	      .indexOf(bytes.charAt(i + 1))));
	   return new String(baos.toByteArray());
	}
	
	 // 将字母转换成数字   
    public static String letterToNum(String input) {  
    	String retstr = "";
        for (byte b : input.getBytes()) {  
//            System.out.print(b - 96);  
            retstr += String.valueOf(b - 96);
        } 
        return retstr;
    }  
  
    // 将数字转换成字母   
    public static String numToLetter(String input) {  
    	String retstr="";
        for (byte b : input.getBytes()) {  
//            System.out.print((char) (b + 48));  
            retstr += String.valueOf((char) (b + 48));
        }  
        return retstr;
    }  
}
