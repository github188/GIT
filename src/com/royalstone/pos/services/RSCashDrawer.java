package com.royalstone.pos.services;

/**
 * 钱箱的JNI的驱动程序,供JavaPos的Services层调用
 * @author liangxinbiao
 */
public class RSCashDrawer {

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
	 * 钱箱的初始化
	 * @return 是否成功 0 成功 其他 失败
	 */
	public native static int drawerInit();
	
	/**
	 * 取得钱箱的状态
	 * @return 状态 0 关闭 1 打开 
	 */
	public native static int drawerChk();
	
	/**
	 * 开钱箱
	 * @return 是否成功 0 成功 其他 失败 
	 */
	public native static int drawerOpen();
	

	public native static int drawerClose();

	/**
	 * 主方法,测试用
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("length="+args.length);
//		driverInit("RSCashDrawr41_Wincor");
//		if(args.length>0){
//			paramInit("64", null, null, null, null, null, null, null, null, null);
//		}else{
//			paramInit("128", null, null, null, null, null, null, null, null, null);
//		}
		driverInit("drv/NCRCashDrawer");
		//paramInit("COM1","9600","8","0","0",null,null,null,null,null);

		drawerInit();
		System.out.println("Before Open....");
		showCashDrawerStatus();
		drawerOpen();
		Thread.sleep(1000);
		System.out.println("After Open....");
		showCashDrawerStatus();
		drawerClose();
	}

	/**
	 * 显示钱箱的状态,测试用 
	 */
	public static void showCashDrawerStatus() {
		if (drawerChk() == 1) {
			System.out.println("CashDrawer Open");
		} else {
			System.out.println("CashDrawer Close");
		}

	}
}
