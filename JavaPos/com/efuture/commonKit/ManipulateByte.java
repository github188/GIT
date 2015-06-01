package com.efuture.commonKit;

import com.efuture.DeBugTools.PosLog;

public class ManipulateByte
{
	public static byte[] getCharBytes(char data)
	{
		byte[] bytes = new byte[1];
		bytes[0] = (byte) (data);
		return bytes;
	}
	
	public static byte[] getChar2Bytes(char data)
	{
		byte[] bytes = new byte[2];
		bytes[0] = (byte) (data);
		bytes[1] = (byte) (data >> 8);
		return bytes;
	}

	public static byte[] getStringBytes(String data)
	{
		return getStringBytes(data, 0, '\0');
	}
	
	public static byte[] getStringBytes(String data, int endIndex)
	{
		return getStringBytes(data, endIndex, '\0');
	}
	
	public static byte[] getStringBytes(String data, int endIndex, char fillChr)
	{
		return getStringBytes(data, 0, endIndex, fillChr);
		/* old
		int maxLen = endIndex;
		if (data == null) data = "";
		byte[] byt = data.getBytes();
		if(maxLen<=0) return byt;
		
		if(byt.length>maxLen)
		{
			//当超过最大长度时，则截取
			byte[] bytRet = new byte[maxLen];
			for(int i=0; i<maxLen; i++)
			{
				bytRet[i] = byt[i];
			}
			return bytRet;
		}
		else if(byt.length<maxLen)
		{
			//当未超过最大长度时，则填充
			byte bytChr = getCharBytes(fillChr)[0];
			byte[] bytRet = new byte[maxLen];
			for(int i=0; i<maxLen; i++)
			{
				if(i<byt.length)
				{
					bytRet[i]=byt[i];
				}
				else
				{
					bytRet[i]=bytChr;
				}
			}
			return bytRet;
		}
		return byt;*/
	}
	
	/**
	 * 获取固定长度的字节数组
	 * @param data
	 * @param startIndex
	 * @param endIndex
	 * @return
	 * @see 比如 data="12345", startIndex=1, endIndex=5,则返回2345的字节数组
	 */
	public static byte[] getStringBytes(String data, int startIndex, int endIndex)
	{
		return getStringBytes(data, startIndex, endIndex, '\0');
	}
	/**
	 * 获取固定长度的字节数组
	 * @param data 传入数据
	 * @param startIndex 起始位置
	 * @param endIndex 结束位置
	 * @param fillChr 填充字符
	 * @return
	 * @see 比如 data="12345", startIndex=1, endIndex=5,则返回2345的字节数组
	 */
	public static byte[] getStringBytes(String data, int startIndex, int endIndex, char fillChr)
	{
		if (data == null) data = "";
		byte[] byt = data.getBytes();
		if(endIndex<=0) return byt;
		if(startIndex<0) startIndex=0;
		if(endIndex<=startIndex) return new byte[]{};

		int i=startIndex;
		int j=0;
		if(byt.length > endIndex)
		{
			//当超过最大长度时，则截取
			byte[] bytRet = new byte[endIndex-startIndex];
			for(; i<endIndex; i++)
			{
				bytRet[j] = byt[i];
				j++;
			}
			return bytRet;
		}
		else if(byt.length < endIndex)
		{
			//当未超过最大长度时，则填充
			byte bytChr = getCharBytes(fillChr)[0];
			byte[] bytRet = new byte[endIndex-startIndex];
			for(; i<endIndex; i++)
			{
				if(i<byt.length)
				{
					bytRet[j]=byt[i];
				}
				else
				{
					bytRet[j]=bytChr;
				}
				j++;
			}
			return bytRet;
		}
		return byt;
	}
	

	public static byte[] getShortBytes(short data)
	{
		byte[] bytes = new byte[2];
		bytes[0] = (byte) (data & 0xff);
		bytes[1] = (byte) ((data & 0xff00) >> 8);
		return bytes;
	}

	public static byte[] getIntBytes(int data)
	{
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (data & 0xff);
		bytes[1] = (byte) ((data & 0xff00) >> 8);
		bytes[2] = (byte) ((data & 0xff0000) >> 16);
		bytes[3] = (byte) ((data & 0xff000000) >> 24);
		return bytes;
	}
	 
	public static byte[] getFloatBytes(float data)
	{
		int intBits = Float.floatToIntBits(data);
		return getIntBytes(intBits);
	}

	public static byte[] getLong4Bytes(long data)
	{
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (data & 0xff);
		bytes[1] = (byte) ((data & 0xff00) >> 8);
		bytes[2] = (byte) ((data & 0xff0000) >> 16);
		bytes[3] = (byte) ((data & 0xff000000) >> 24);
		return bytes;
	}
	
	public static byte[] getLong8Bytes(long data)
	{
		byte[] bytes = new byte[8];
		bytes[0] = (byte) (data & 0xff);
		bytes[1] = (byte) ((data >> 8) & 0xff);
		bytes[2] = (byte) ((data >> 16) & 0xff);
		bytes[3] = (byte) ((data >> 24) & 0xff);
		bytes[4] = (byte) ((data >> 32) & 0xff);
		bytes[5] = (byte) ((data >> 40) & 0xff);
		bytes[6] = (byte) ((data >> 48) & 0xff);
		bytes[7] = (byte) ((data >> 56) & 0xff);
		return bytes;
	}

	public static byte[] getDoubleBytes(double data)
	{
		long intBits = Double.doubleToLongBits(data);
		return getLong8Bytes(intBits);
	}

	public static short getShort(byte[] bytes)
    {
        return (short) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
    }

	public static int getInt(byte[] bytes)
	{
		return (0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)) | (0xff0000 & (bytes[2] << 16)) | (0xff000000 & (bytes[3] << 24));
	}

	public static long getLong4(byte[] bytes)
	{
		return (0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)) | (0xff0000 & (bytes[2] << 16)) | (0xff000000 & (bytes[3] << 24));
	}

	public static long getLong8(byte[] bytes)
	{
		return (0xffL & (long) bytes[0]) | (0xff00L & ((long) bytes[1] << 8)) | (0xff0000L & ((long) bytes[2] << 16))
				| (0xff000000L & ((long) bytes[3] << 24)) | (0xff00000000L & ((long) bytes[4] << 32)) | (0xff0000000000L & ((long) bytes[5] << 40))
				| (0xff000000000000L & ((long) bytes[6] << 48)) | (0xff00000000000000L & ((long) bytes[7] << 56));
	}

	public static double getDouble(byte[] bytes)
	{
		long l = getLong8(bytes);		
		return Double.longBitsToDouble(l);
	}

	public static float getFloat(byte[] bytes)
	{
		return Float.intBitsToFloat(getInt(bytes));
	}
	
	public static char getChar(byte[] bytes)
	{
		return (char) ((0xff & bytes[0]) );//| (0xff00 & (bytes[1] << 8))
	}
	
	public static char getChar2(byte[] bytes)
	{
		return (char) ((0xff & bytes[0])| (0xff00 & (bytes[1] << 8)) );
	}
	
	public static String getString(byte[] byt)
	{
		try
		{
			return getString(byt, "GBK");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return "";
		}
	}
	
	public static String getString(byte[] byt, String charsetName)
	{
		try
		{
			return getString(byt, charsetName, '\0', ' ');
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return "";
		}

	}
	
	public static String getString(byte[] byt, String charsetName, char replaceChr, char replaceNewChr)
	{
		try
		{			 
			//return new String(byt, Charset.forName(charsetName)).replace(replaceChr, replaceNewChr);
			return new String(byt, charsetName).replace(replaceChr, replaceNewChr);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			PosLog.getLog(ManipulateByte.class).error(ex);
			return "";
		}

	}
	
}
