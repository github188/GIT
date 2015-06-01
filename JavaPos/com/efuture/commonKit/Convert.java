package com.efuture.commonKit;

public class Convert
{
	public static String rightTrim(String line)
	{
		int len = line.length();

		if (len == 0)
			return "";
		int st = 0;

		for (int i = len - 1; i >= 0; i--)
		{
			if (line.charAt(i) != ' ')
			{
				st = i;
				break;
			}
		}

		return line.substring(0, st + 1);
	}

	public static String increaseChar(String str, int num)
	{
		return increaseChar(str, ' ', num);
	}

	public static String increaseChar(String str, char c, int num)
	{
		int limit;

		if (num <= 0)
		{
			limit = 16;
		}
		else
		{
			limit = num;
		}

		int len = str.length();

		for (int i = len; i < limit; i++)
		{
			str += c;
		}

		if (str.length() > limit)
			return str.substring(str.length() - limit);
		else
			return str;
	}

	public static String increaseCharForward(String str, int num)
	{
		return increaseCharForward(str, ' ', num);
	}

	public static String increaseCharForward(String str, char c, int num)
	{
		int limit;

		if (num <= 0)
		{
			limit = 16;
		}
		else
		{
			limit = num;
		}

		int len = str.length();

		for (int i = len; i < limit; i++)
		{
			str = c + str;
		}

		if (str.length() > limit)
			return str.substring(str.length() - limit);
		else
			return str;
	}

	public static String increaseInt(int num, int lim)
	{
		int limit;
		String str = "";

		if (lim <= 0)
		{
			limit = 4;
		}
		else
		{
			limit = lim;
		}

		String a = String.valueOf(num);
		int len = a.length();

		for (int i = 0; i < (limit - len); i++)
		{
			str += "0";
		}

		str += num;

		if (str.length() > limit)
			return str.substring(str.length() - limit);
		else
			return str;
	}

	//
	public static String increaseDou(double num, int lim)
	{
		int limit;
		String str = "";

		if (lim <= 0)
		{
			limit = 16;
		}
		else
		{
			limit = lim;
		}

		String a = String.valueOf(num);
		int len = a.length();

		for (int i = 0; i < (limit - len); i++)
		{
			str += "0";
		}

		str += num;

		if (str.length() > limit)
			return str.substring(str.length() - limit);
		else
			return str;
	}

	//
	public static String increaseLong(long num, int lim)
	{
		int limit;
		String str = "";

		if (lim <= 0)
		{
			limit = 8;
		}
		else
		{
			limit = lim;
		}

		String a = String.valueOf(num);
		int len = a.length();

		for (int i = 0; i < (limit - len); i++)
		{
			str += "0";
		}

		str += num;

		if (str.length() > limit)
			return str.substring(str.length() - limit);
		else
			return str;
	}

	public static String convertPasswd(String passwd)
	{
		String line = "";
		int len = passwd.length();

		for (int i = 2; i < len; i += 2)
		{
			line += ("," + Integer.valueOf(passwd.substring(i, i + 2), 16).toString());
		}

		return line.substring(1);
	}

	// 0:左对齐 1； 右对齐 2；中间
	public static String appendStringSize(String orgLine, String addLine, int startIndex, int length, int orgSize)
	{
		return appendStringSize(orgLine, addLine, startIndex, length, orgSize, 0);
	}

	public static String appendStringSize(String orgLine, String addLine, int startIndex, int length, int orgSize, int alignment)
	{
		String newLine = orgLine;

		// 将原始字符串 添加或删除到指定长度
		int orgLength = countLength(orgLine);

		if (orgLength > orgSize)
		{
			newLine = newSubString(orgLine, 0, orgSize);
		}
		else
		{
			for (int i = orgLength; i < orgSize; i++)
			{
				newLine += " ";
			}
		}

		// 如果需要修改某段字符串
		if (addLine != null)
		{
			// 取需要添加字符串的最小值为指定添加长度
			if (countLength(addLine) >= length)
			{
				addLine = newSubString(addLine, 0, length);
			}
			else
			{
				int sub = 0;
				switch (alignment)
				{
					case 0:
						sub = 0;
						break;
					case 1:
						sub = (length - countLength(addLine));
						break;
					case 2:
						sub = (length - countLength(addLine)) / 2;
						break;
				}

				for (int i = countLength(addLine); i < length; i++)
				{
					if (sub > 0)
					{
						addLine = " " + addLine;
						sub--;
					}
					else
					{
						addLine += " ";
					}
				}
			}
			// 截取
			String head = newSubString(newLine, 0, startIndex);
			String end = "";

			try
			{
				end = newSubString(newLine, startIndex + length, orgSize);
			}
			catch (Exception er)
			{
				er.printStackTrace();
			}

			// 修改后的字符串
			newLine = head + addLine + end;

			if (Convert.countLength(newLine) > orgSize)
			{
				newLine = newSubString(newLine, 0, orgSize);
			}
		}

		return newLine;
	}

	public static String newSubString(String line, int start)
	{
		return newSubString(line, start, countLength(line));
	}

	// 截取新字符串 中文 占2个字节
	public static String newSubString(String line, int start, int end)
	{
		if (start >= end) { return ""; }

		if (countLength(line) <= (end - start)) { return line; }

		boolean flag = false;
		int startindex = 0;
		int endindex = 0;
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

			if (length >= (end + 1))
			{
				endindex = i;

				break;
			}

			if ((length >= (start + 1)) && !flag)
			{
				flag = true;
				startindex = i;
			}
		}

		if (startindex > endindex)
		{
			return line.substring(startindex);
		}
		else
		{
			return line.substring(startindex, endindex);
		}
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

	public static boolean isNull(Object obj)
	{
		if (obj == null || obj.toString().trim() == "") { return true; }

		return false;
	}

	public static boolean isNumber(Object obj)
	{
		try
		{
			Double.parseDouble(obj.toString());

			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	public static boolean isShort(Object obj)
	{
		try
		{
			Short.parseShort(obj.toString());

			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	public static boolean isInt(Object obj)
	{
		try
		{
			Integer.parseInt(obj.toString());

			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	public static boolean isLong(Object obj)
	{
		try
		{
			Long.parseLong(obj.toString());

			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	/**
	 * 判断字符是否为字母
	 * @param c
	 * @return
	 */
	public static boolean isLetter(char c)
	{
		if (((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public static boolean isDouble(Object obj)
	{
		try
		{
			Double.parseDouble(obj.toString());

			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	public static double toDouble(Object obj)
	{
		try
		{
			if (obj == null)
				return 0;
			return Double.parseDouble(obj.toString());
		}
		catch (Exception e)
		{
			return 0;
		}
	}

	public static int toInt(char c)
	{
		try
		{
			return Character.getNumericValue(c);
		}
		catch (Exception e)
		{
			return 0;
		}

	}

	public static short toShort(Object obj)
	{
		try
		{
			return (short) toDouble(obj);
		}
		catch (Exception e)
		{
			return 0;
		}
	}
	
	public static int toInt(Object obj)
	{
		try
		{
			return (int) toDouble(obj);
		}
		catch (Exception e)
		{
			return 0;
		}
	}

	public static long toLong(Object obj)
	{
		try
		{
			return (long) toDouble(obj);
		}
		catch (Exception e)
		{
			return 0;
		}
	}

	public static String codeInString(String text, char c)
	{
		int pos = 0;

		if (c == '-')
		{
			pos = text.indexOf(c);
			if (pos < 0)
				return "";
			else
				return text.substring(0, pos);
		}
		else if (c == '[' || c == ']')
		{
			pos = text.indexOf('[');
			if (pos < 0)
				return "";
			int len = text.indexOf(']');
			if (len < 0)
				return "";
			return text.substring(pos + 1, len - pos);
		}
		else
			return text;
	}
}
