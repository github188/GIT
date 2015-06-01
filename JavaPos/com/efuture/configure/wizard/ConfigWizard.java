package com.efuture.configure.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.efuture.javaPos.Global.Language;


public class ConfigWizard extends Wizard
{
    public static final String Ipconfig = "IpConfig";
    public static final String UpdateConfig = "UpdateConfig";
    public static final String SyjConfig = "SyjConfig";
    public static final String ServerIp = "ServerIp";
    public static final String DeviceConfig = "DeviceConfig";
    public static final String OtherConfig = "OtherConfig";
    
    //声明向导页面
    private UpdateConfig updateConf;
    private SyjConfig syjConf;
    private ServerIp serverip;
    private DeviceConfig deviceConf;
    private OtherConfig otherConf;

    public ConfigWizard()
    {
        //创建三个向导页面对象
        updateConf = new UpdateConfig();
        syjConf    = new SyjConfig();
        serverip   = new ServerIp();
        deviceConf = new DeviceConfig(); 
        otherConf  = new OtherConfig();
          
        //分别添加这三个页面		
        this.addPage(updateConf);
        this.addPage(syjConf);
        this.addPage(serverip);
        this.addPage(deviceConf);
        this.addPage(otherConf);

       //this.setWindowTitle("JAVAPOS 配置向导"); //向导标题
        this.setWindowTitle(Language.apply("JAVAPOS 配置向导")); //向导标题
                                             //this.setHelpAvailable( true );
    }

    /* （非 Javadoc）
     * @see org.eclipse.jface.wizard.Wizard#canFinish()
     * 确定“完成”按钮是否可用,true为可用，false为不可用
     */
    public boolean canFinish()
    {
        //仅当当前页面为感谢页面时才将“完成”按钮置为可用状态
        if (this.getContainer().getCurrentPage() == otherConf)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    //必须实现该方法，当单击完成按钮后调用此方法
    public boolean performFinish()
    {
        return true;
    }

    public static boolean isValidIP(String ipAddr)
    {
        if (ipAddr == null)
        {
            return false;
        }

        String regx = "^(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|[1-9])\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|[1-9])$";

        return ipAddr.matches(regx);
    }

    public static boolean isValidPort(String str)
    {
        if ((str == null) || "".equals(str.trim()))
        {
            return false;
        }

        try
        {
            int i = Integer.parseInt(str);

            if ((i < 1) || (i > 65535))
            {
                return false;
            }

            return true;
        }
        catch (NumberFormatException e)
        {
            //e.printStackTrace();
        }

        return false;
    }

    public static String ipCheck(String ipAddr, String portNum)
    {
    	// 由于有可能以域名方式配置,所以不进行IP地址合法性的校验
    	/*
        if (!isValidIP(ipAddr))
        {
            return "IP地址格式不正确";
        }
    	 */
    	
        if (!isValidPort(portNum))
        {
            //return "端口号填写不正确";
        	return Language.apply("端口号填写不正确");
        }

        return null;
    }
}
