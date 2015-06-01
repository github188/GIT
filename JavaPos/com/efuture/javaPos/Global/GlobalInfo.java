package com.efuture.javaPos.Global;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.Vector;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Struct.CustInfoDef;
import com.efuture.javaPos.Struct.GlobalParaDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SyjMainDef;
import com.efuture.javaPos.Struct.SyjStatusDef;
import com.efuture.javaPos.Struct.TempDef;
import com.efuture.javaPos.UI.Design.BackgroundForm;
import com.efuture.javaPos.UI.Design.SaleForm;
import com.efuture.javaPos.UI.Design.StatusBarForm;


//这个类主要用于全局对象信息
public class GlobalInfo
{
	public static Shell mainshell = null;
    public static SaleForm saleform = null;
    public static BackgroundForm background = null; //背景
    public static StatusBarForm statusBar = null; //状态栏
    public static Label labelTitle = null; //标题栏
    public static int initHandle = 0; //初始shell的句柄
    
    public static String[][] keypad = null;
    public static ArrayList keyList = null;
    public static boolean enablequickkey = true;
    public static int validNum = 0;
    
    public static Timer backgroundTimer = null; //后台工作定时器
    
    public static Sqldb baseDB = null;
    public static Sqldb dayDB = null;
    public static Sqldb localDB = null;
    
    public static Sqldb RemoteDB = null;

    public static PreparedStatement psGoodsCode = null;
    public static PreparedStatement psGoodsBarCode = null;
    public static PreparedStatement psGoodsPop = null;
    public static PreparedStatement psManaframe = null;
    
    public static String balanceDate = null; //记账日期  1992/12/02
    public static boolean isQuitAsSoon = false; //马上退出
    public static String quitMessage = Language.apply("系统产生严重错误，强行退出");
    
    public static boolean isOnline = true; //是否联网
    public static String ipAddr = null;
    
    public static Http localHttp = null;  // 业务HTTP
    public static Http timeHttp = null;	  // 定时HTTP
    public static Http cardHttp = null;	  // 面值卡HTTP
    public static Http memcardHttp = null;// 会员HTTP
    public static Vector otherHttp = null;// 扩展HTTP	
    public static Vector httpStatus = null;	//HTTP连接状态
    
    public static Object axis = null;	//业务axis通讯对象
    
    public static Vector posTime = null; //班次定义
    public static Vector payMode = null; //付款方式
    public static Vector dzcMode = null; //电子秤模版
    public static Vector callInfo = null; //呼叫信息
    public static ArrayList menuFunArray = null; //菜单信息
    public static SyjMainDef syjDef = null; //收银机定义
    public static OperUserDef posLogin = null; //登陆收银员
    public static SyjStatusDef syjStatus = null; //收银状态
    public static GlobalParaDef sysPara = null; //系统参数
    public static boolean useMobileCharge = false;	//是否启用移动充值
    public static boolean useWhtCharge = false;	//是否启用武汉通零钞转存充值
    
    public static PrintStream exceptionps = null; //错误日志
    public static PrintWriter printwriter = null; //跟踪日志
    public static PrintWriter cmdlogwriter = null; //跟踪命令日志
    public static String ModuleType = ""; // 程序客户化模版号    
    
    public static boolean isStartICCard =  true;   //是否开启IC卡功能
    public static boolean isStartBankTracker = true; //是否开启银联设备刷卡功能
    
    public static Vector payLimit = null;		// 付款上限信息
    public static HashMap funcMap = null;		// 功能列表
    
    public static HashMap mutiICMap = null;
    
    public static TempDef tempDef = new TempDef();
    
    public static CustInfoDef custInfoDef = new CustInfoDef();
    public static boolean flag = true;
    
    public static String getPhysicsMarket()
    {
    	if (syjDef.syjplacemkt != null && syjDef.syjplacemkt.length() > 0) return syjDef.syjplacemkt;
    	else return sysPara.mktcode;
    }
}
