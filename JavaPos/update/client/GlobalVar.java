package update.client;

import org.eclipse.swt.SWT;

public class GlobalVar 
{
	//设计时 都是有框的窗口， 到linux变成无框时 需要减去的值
    public static int heightPL = 0;
    
    //标准SHELL 的样式   
    public final static int style_linux = SWT.NONE | SWT.APPLICATION_MODAL;
    public final static int style_windows = SWT.NONE|SWT.APPLICATION_MODAL| SWT.TITLE;
    public static int style = style_windows;
     
    public static String ConfigPath = "./javaPos.ConfigFile";
    public static String Installpath = System.getProperty("user.dir");
	public static String temp = "/Temp";
	public static String invoice = "/Invoice";
	public static String ftpUpdateFile = "Update.xml";
	
	public static String localUpdateFile = Installpath + temp+ "/"+ ftpUpdateFile;
	public static String oldUpdateFile = ConfigPath + "/"+ ftpUpdateFile;
	
	public static String downloadFile = Installpath + temp;
	public static String UpdateConfig = ConfigPath + "/Update.ini";
	public static String PosIdConfig = ConfigPath + "/PosID.ini"; 
	
	// N-不自动更新 A-自动静默 S-起动更新前提示 F-发现新版本提示 
	public static String UpdateMode = "A";
}
