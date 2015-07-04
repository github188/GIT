package com.royalstone.pos.services;

/**
 * 顾客显示屏的JNI的驱动程序,供JavaPos的Services层调用
 * @author liangxinbiao
 */
public class RSVFD {
	
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
	 * 顾显的初始化
	 * @param param
	 * @return 是否成功 0 成功 其他 失败
	 */
	public native static int vfdInit(char[] param);
	
	/**
	 * 显示字符串
	 * @param str 字符串的Byte数组
	 * @return 是否成功 0 成功 其他 失败
	 */
	public native static int vfdPrint(byte[] str);
	
	/**
	 * 清屏
	 * @return 是否成功 0 成功 其他 失败
	 */
	public native static int vfdClear();
	
	/**
	 * 显示欢迎字样
	 * @param str 欢迎字符串的Byte数组
	 * @return 是否成功 0 成功 其他 失败
	 */
	public native static int vfdWelcome(byte[] str);
	
	/**
	 * 显示小计
	 * @param str 小计字符串的Byte数组
	 * @return 是否成功 0 成功 其他 失败
	 */
	public native static int vfdPrintSubTotal(byte[] str);

	/**
	 * 显示合计
	 * @param str 合计字符串的Byte数组
	 * @return 是否成功 0 成功 其他 失败
	 */
	public native static int vfdPrintTotal(byte[] str);
	
	/**
	 * 显示找回
	 * @param str 找回字符串的Byte数组
	 * @return 是否成功 0 成功 其他 失败
	 */
	public native static int vfdPrintReturn(byte[] str);
	
	/**
	 * 显示商品名
	 * @param str 商品名字符串的Byte数组
	 * @return 是否成功 0 成功 其他 失败
	 */
	public native static int vfdPrintGoods(byte[] str);
	
	/**
	 * 显示金额
	 * @param str 金额字符串的Byte数组
	 * @return 是否成功 0 成功 其他 失败
	 */
	public native static int vfdPrintAmtPr(byte[] str);

	/**
	 * 主方法,测试用
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		driverInit("RSVFD41_1008G");
		paramInit("COM4","2400","8","0","0",null,null,null,null,null);
		vfdInit(null);
		vfdClear();
		Thread.sleep(5000);
		vfdPrint("15.67".getBytes());
		Thread.sleep(5000);
		vfdWelcome("欢迎光临!!".getBytes());
//		int i= System.in.read();
//		Thread.sleep(5000);
		vfdPrintReturn("12.34".getBytes());
		Thread.sleep(5000);
		vfdPrintTotal("31232".getBytes());
		Thread.sleep(5000);
		vfdPrintAmtPr("555.55".getBytes());
	
	}
}
