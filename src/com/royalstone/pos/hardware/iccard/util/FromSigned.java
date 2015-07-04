package com.royalstone.pos.hardware.iccard.util;

/**
 * 一些实用方法,包括将int类型转为byte类型,将byte类型合成int,将字符串转为byte[]等.
 * @author marco
 */
public class FromSigned {
	public static byte toUnsignedByte(int unsignedByte) {
		return (byte) (unsignedByte & 0xff);
	}
	public static byte[] toUnsignedByteArray(int[] unsignedByteArray) {
		final byte[] result = new byte[unsignedByteArray.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = toUnsignedByte(unsignedByteArray[i]);
		}
		return result;
	}
	public static int toInt(byte lowByte, byte highByte) {
		return ((highByte & 0xff) << 8) + (lowByte & 0xff);
	}
	public static byte lowByte(int i) {
		return (byte) (i & 0x000000ff);
	}
	public static byte highByte(int i) {
		return (byte) ((i & 0x0000ff00) >> 8);
	}

	public static byte[] fromStrToByteArray(String value) {
		if (value == null || value.length() != 12)
			return new byte[0];
		int[] result = new int[value.length() / 2];
		for (int i = 0; i < result.length; i++) {
			String pair = value.substring(i*2, i*2 + 2);
			result[i] = Integer.parseInt(pair,16);
		}
		return toUnsignedByteArray(result);
	}
	
	public static void main(String args[]){
		
		byte[] byte1=fromStrToByteArray("A0A1A2A3A4A5");
		for(int i=0;i<byte1.length;i++){
			System.out.println(byte1[i]==com.royalstone.pos.hardware.iccard.RC500.KeyOfSector.A0A1A2A3A4A5[i]);	
		}
		
	}
	
	
}