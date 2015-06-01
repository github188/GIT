package device;

public class DeviceInfo
{
    public String getAssemblyVersion()
    {
    	//主版本号 . 子版本号. 编译版本号
    	//发布时，建议修改编译版号及日期
    	return "16528 build 2014.01.16";
    }
    
    public static char[] convertCmdStringToCmdChar(String cmd)
    {
    	String[] s = cmd.toLowerCase().replaceAll("0x","").split("&");
    	char[] cmdchar = new char[s.length];
    	
    	for (int i=0;i<s.length;i++)
    	{
    		cmdchar[i] = (char)Integer.parseInt(String.valueOf(s[i]), 16);
    	}
    	
    	return cmdchar;
    }
}
