package com.royalstone.pos.services;

/**
 * 打印机的JNI的驱动程序,供JavaPos的Services层调用
 * @author liangxinbiao
 */
public class RSPrinter {
	
	/**
	 * @param driverName 驱动程序的DLL的名字
	 */
	public static void driverInit(String driverName) {
		System.loadLibrary(driverName);
	}

	/**
	 * 初始化参数
	 * @param portName 端口名字
	 * @param baudRate 波特率
	 * @param byteSize 位长
	 * @param parity   校验位
	 * @param stopBits 停止位
	 * @param value1   预留值1
	 * @param value2   预留值2
	 * @param value3   预留值3
	 * @param value4   预留值4
	 * @param value5   预留值5
	 */
	public native static void paramInit(
		String portName,
		String baudRate,
		String byteSize,
		String parity,
		String stopBits,
		String value1,
		String value2,
		String value3,
		String value4,
		String value5);

	
	/**
	 * 打印机的初始化
	 * @return 是否成功 0 成功 其他 失败
	 */
	public native static int printInit();
	
	/**
	 * 打印字符串
	 * @param str 字符串的Byte数组
	 * @return 是否成功 0 成功 其他 失败
	 */
	public native static int printStr(byte[] str);
	
	/**
	 * 打印机状态检测
	 * @deprecated
	 */
	public native static int printChk();
	
	/**
	 * 切纸
	 * @return 是否成功 0 成功 其他 失败
	 */
	public native static int printCut();
	
	/**
	 * 走纸
	 * @param feedCount 行数
	 * @return 是否成功 0 成功 其他 失败
	 */
	public native static int printFeed144(int feedCount);
	
	/**
	 * 主方法,测试用
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		driverInit("RSPrinter_Wincor_ND210");
		paramInit("COM1","9600","8","0","0",null,null,null,null,null);
		printStr("This is a Test!!\n".getBytes());
		printStr("这是一个测试!!\n".getBytes("GB2312"));
		//printFeed144(10);
		//printCut();
	}
	
}
