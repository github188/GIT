package com.efuture.javaPos.Global;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.internal.win32.RECT;
import org.eclipse.swt.internal.win32.TCHAR;

/**
 * 全局变量类
 * 用于保存所用固定的全局变量
 * 
 * @author root
 * 
 */
public class GlobalVar
{
	// 键值定义
	public final static int Key0 = 1; // 数字0
	public final static int Key1 = 2; // 数字1
	public final static int Key2 = 3; // 数字2
	public final static int Key3 = 4; // 数字3
	public final static int Key4 = 5; // 数字4
	public final static int Key5 = 6; // 数字5
	public final static int Key6 = 7; // 数字6
	public final static int Key7 = 8; // 数字7
	public final static int Key8 = 9; // 数字8
	public final static int Key9 = 10; // 数字9
	public final static int Decimal = 11; // 小数点
	public final static int Plus = 12; // 加号
	public final static int Minu = 13; // 减号
	public final static int Mul = 14; // 乘号
	public final static int Div = 15; // 除号
	public final static int Exit = 16; // 退出
	public final static int Validation = 17; // 确认
	public final static int Enter = 18; // 回车
	public final static int BkSp = 19; // 退格
	public final static int ArrowUp = 20; // 光标上
	public final static int ArrowDown = 21; // 光标下
	public final static int ArrowLeft = 22; // 光标左
	public final static int ArrowRight = 23; // 光标右
	public final static int PageUp = 24; // 上翻页
	public final static int PageDown = 25; // 下翻页
	public final static int Quantity = 26; // 数量键 乘号
	public final static int Back = 27; // 退货键
	public final static int Del = 28; // 删除键
	public final static int Clear = 29; // 清除键
	public final static int Rebate = 30; // 单品折扣
	public final static int RebatePrice = 31; // 单品折让
	public final static int WholeRate = 32; // 总折扣
	public final static int WholeRebate = 33; // 总折让
	public final static int SetPrice = 34; // 改价键
	public final static int writeHang = 35; // 挂单键
	public final static int readHang = 36; // 解挂键
	public final static int StaffText = 37; // 营业员
	public final static int OperGrant = 38; // 工号授权
	public final static int MemberGrant = 39; // 会员授权
	public final static int MoreUnit = 40; // 多单位键
	public final static int Pay = 41; // 付款键
	public final static int MainList = 42; // 功能主单
	public final static int Caculator = 43; // 计算器键
	public final static int OpenDraw = 44; // 开钱箱键
	public final static int Leave = 45; // 离开键
	public final static int Call = 46; // 呼叫键
	public final static int Print = 47; // 打印键
	public final static int MzkInfo = 48; // 面值卡查询键
	public final static int HykInfo = 49; // 顾客卡查询键
	public final static int PayBank = 50; // 银联卡付款键
	public final static int PayCash = 51; // 现金付款键
	public final static int PayCheque = 52; // 支票付款键
	public final static int PayCredit = 53; // 信用卡付款键
	public final static int PayMzk = 54; // 面值卡付款键
	public final static int PayGift = 55; // 礼券付款键
	public final static int PayTally = 56;// 赊账付款键
	public final static int PayLcZc = 57;// 零钞转存
	public final static int InputFphm = 58;// 发票号输入
	public final static int HYRebate = 59;// 会员折扣选择功能键
	public final static int ICInput = 60;// IC卡功能键
	public final static int JfExchange = 61;// 积分换购键
	public final static int MzkRecycle = 62; // 储值卡回收
	public final static int ExchangeSell = 63; // 换货键
	public final static int ChangeGBillName = 64; // 更换商品名称键
	public final static int QueryGoodsInfo = 65; // 商品模糊查询键 wangyong add by
												 // 2010.5.28
	public final static int BuyInfo = 66; // 客户信息功能键 wulei add by 2010.5.31
	public final static int SaleAppendInfo = 67; // 查看小票附加信息键 wulei add by
												 // 2010.5.31
	public final static int InputJdfhdd = 68; // 输入家电发货地点 wulei add by 2010.7.30
	public final static int InputAppendInfo = 69; // 输入小票附加信息 柯仲平 add by
												  // 2010.8.13
	public final static int locategoods = 70; // 商品定位键 柯仲平 add by 2011.5.12
	public final static int InputMemoInfo = 71; // 专卖输入备注信息
	public final static int BankTracker = 72;// 银行设备读取会员卡及储值卡专用
	public final static int EBill = 73;// 调取专柜电子开票功能
	public final static int InputStam = 74; // 印花键
	public final static int InputPreSale = 75; // 超市预销售键
	public final static int DoubleZero = 99; // 00


    public final static int Debug = 777;   //调式功能键
    public final static int AutoTest = 888;   //自动测试功能键
	public final static int MsrError = 999; 	 //MSR错误键
	public final static int AliBacBill= 666;   //阿里签购单重打印键
	public final static int SuNingBacBill= 665;   //阿里签购单重打印键
	
    // 目录定义
    public final static String HomeBase = System.getProperty("user.dir");
    //public final static String Home = HomeBase + "/com/efuture/javaPos";

	public final static int CustomKey0 = 100; // 客户备用功能键
	public final static int CustomKey1 = 101; // 客户备用功能键
	public final static int CustomKey2 = 102; // 客户备用功能键
	public final static int CustomKey3 = 103; // 客户备用功能键
	public final static int CustomKey4 = 104; // 客户备用功能键
	public final static int CustomKey5 = 105; // 客户备用功能键
	public final static int CustomKey6 = 106; // 客户备用功能键
	public final static int CustomKey7 = 107; // 客户备用功能键
	public final static int CustomKey8 = 108; // 客户备用功能键
	public final static int CustomKey9 = 109; // 客户备用功能键

	// 配置文件定义
	public static String ConfigPath = "./javaPos.ConfigFile";
	public static String ConfigFile = ConfigPath + "/Config.ini";
	public static String KeyFile = ConfigPath + "/KeyDef.ini";
	public static String ShortcutKeyFile = ConfigPath + "/ShortcutKeyDef.ini";

	// 通讯分割符
	public final static String divisionFlag1 = "#@#";
	public final static String divisionFlag2 = "&@&";

	// 标准SHELL 的样式
	public final static int style_linux = SWT.NONE | SWT.APPLICATION_MODAL;
	public final static int style_windows = SWT.NONE | SWT.APPLICATION_MODAL | SWT.TITLE;
	public static int style = style_windows;

	// 设计时 都是有框的窗口， 到linux变成无框时 需要减去的值
	public static int heightPL = 0;

	// 设计时 根据客显的分辨率判断字体大小,默认为1024*768，如果小于1024*768 设定值为负数
	public static int secFont = 0;

	// 其他
	public static int MaxLengthOfMoney = 10;
	public static int MaxlengthOfPasswd = 10;
	public static int MaxLengthOfStaffID = 8;
	public static int MaxLengthOfArk = 20;
	public static int MaxLengthOfCom = 20;

	public static Point rec = new Point(1024, 768);

	//
	public static void RefushConfPath(String path)
	{
		ConfigPath = path;

		ConfigFile = path + "/Config.ini";
		KeyFile = path + "/KeyDef.ini";
		ShortcutKeyFile = path + "/ShortcutKeyDef.ini";
	}

	public static void EnableWindowsTrayWnd(boolean flag)
	{

		if (System.getProperties().getProperty("os.name").substring(0, 5).equals("Linux"))
			return;

		if (ConfigClass.DebugMode)
			return;
		if (ConfigClass.DebugModeString.equals("S") || ConfigClass.DebugModeString.equals("Z"))
			return;

		TCHAR windowClass = new TCHAR(0, "Shell_TrayWnd", true);
		TCHAR lpWindowName = new TCHAR(0, "", true);
		int tray = OS.FindWindow(windowClass, lpWindowName);

		if (!flag)
		{
			RECT a = new RECT();

			if (OS.GetWindowRect(tray, a))
			{
				OS.ShowWindow(tray, OS.SW_HIDE);

				GlobalVar.rec = new Point(a.right, a.bottom);
				OS.SetCursorPos(GlobalVar.rec.x, 0);
			}
		}
		else
		{
			OS.ShowWindow(tray, OS.SW_SHOW);
		}
	}

	public static int EnableScreenKeyboard(boolean flag)
	{
		TCHAR windowClass = new TCHAR(0, "OnScreenKeyboard", true);
		TCHAR lpWindowName = new TCHAR(0, "", true);

		int kbwnd = OS.FindWindow(windowClass, lpWindowName);
		if (kbwnd != 0)
		{
			if (flag)
			{
				OS.SetWindowPos(kbwnd, OS.HWND_TOPMOST, 0, 0, 0, 0, OS.SWP_NOMOVE | OS.SWP_NOSIZE);
				OS.ShowWindow(kbwnd, OS.SW_SHOW);
			}
			else
			{
				OS.SetWindowPos(kbwnd, OS.HWND_NOTOPMOST, 0, 0, 0, 0, OS.SWP_NOMOVE | OS.SWP_NOSIZE);
				OS.ShowWindow(kbwnd, OS.SW_HIDE);
			}
		}

		return kbwnd;
	}
}
