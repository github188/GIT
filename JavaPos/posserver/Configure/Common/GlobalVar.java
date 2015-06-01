package posserver.Configure.Common;

import java.util.Vector;

import org.eclipse.jface.wizard.WizardPage;

import posserver.Configure.Init.InitCfgStruct;

public class GlobalVar 
{
	//D:\\E Work\\EclipseWorkSpace\\JavaPos\\posserver\\configure\\wizard
	public static String Path = ".";
	public static String InitCfgPath = "\\InitCfg.ini";
	public static String ServerTypeCfgPath = "\\ServerTypeCfg.ini";
	public static String DictionaryCfgPath = "\\DictionaryCfg.ini";
	public static String ServerPosServerCfgPath = "";
	public static String ServerPosServerCmdPath = "";

	public static void RefushConfPath(String strpath)
	{
		if (strpath != null && strpath.trim().length() > 0)
		{
			GlobalVar.Path = strpath;
		}
		GlobalVar.InitCfgPath = GlobalVar.Path +GlobalVar.InitCfgPath;
		GlobalVar.ServerTypeCfgPath = GlobalVar.Path +GlobalVar.ServerTypeCfgPath;
		GlobalVar.DictionaryCfgPath = GlobalVar.Path +GlobalVar.DictionaryCfgPath;
	}
	
	public static InitCfgStruct initCfgDef = new InitCfgStruct();
	
	public static Vector ServerTypes = new Vector();
	
	public static Vector Localdbtype = new Vector();
	
	public static Vector Dayenddef = new Vector();
	
	public static Vector Fileupdatedef = new Vector();
	
	public static Vector Datasouremode = new Vector();
	
	public static Vector Datasourcecommon = new Vector();
	
	public static Vector Inputencoder = new Vector();
	
	public static Vector Outencoder = new Vector();
	
	public static Vector Starttrans = new Vector();
	
	public static Vector Cmdtype = new Vector();
	
	public static Vector Cmd_XX_Mode = new Vector();
	
	public static Vector Sql_XX_Type = new Vector();
	
	public static Vector Paradatatype = new Vector();
	
	public static WizardPage LastPage;
	
	
}
