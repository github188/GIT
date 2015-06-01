package posserver.Configure.Init;


public class InitCfgStruct
{
	//应用服务器类型
	private ServerTypeStruct ServerType;
	
	// 应用服务器安装路径
	private String ServerPath = "";
	
	// PosServer安装文件路径
	private String PosServerPath = "";
	
	// PosServer服务名
	private String PosServerName = "";
	
	// 是否安装PosServer文件
	private boolean IsCopyPosInstallFile = false;
	
	public boolean GetIsCopyPosInstallFile()
	{
		return IsCopyPosInstallFile;
	}
	
	public void SetIsCopyPosInstallFile(boolean iscopyposinstallfile)
	{
		IsCopyPosInstallFile = iscopyposinstallfile;
	}
	
	public void SetPosServerPath(String posserverpath)
	{
		String str = posserverpath.trim();
		if (str.length() >0 && (str.charAt(str.length()-1) == '\\' || str.charAt(str.length()-1) == '/'))
		{
			PosServerPath = posserverpath.substring(0,str.length() - 1);
		}
		else
		{
			PosServerPath = posserverpath;
		}
	}
	
	public String GetPosServerPath()
	{
		return PosServerPath;
	}
	
	public void SetPosServerName(String posservername)
	{
		PosServerName = posservername;
	}
	
	public String GetPosServerName()
	{
		return PosServerName;
	}
	
	public ServerTypeStruct GetServerType()
	{
		return ServerType;
	}
	
	public String GetServerPath()
	{
		return ServerPath;
	}
	
	public void SetServerType(ServerTypeStruct servertype)
	{
		ServerType = servertype;
	}
	
	public void SetServerPath(String serverpath)
	{
		String str = serverpath.trim();
		if (str.length() >0 && (str.charAt(str.length()-1) == '\\' || str.charAt(str.length()-1) == '/'))
		{
			ServerPath = serverpath.substring(0,str.length() - 1);
		}
		else
		{
			ServerPath = serverpath;
		}
	}
}
