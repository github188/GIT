package posserver.Configure;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import org.eclipse.jface.wizard.Wizard;

import posserver.Configure.CmdDef.CmdDefCfgPage;
import posserver.Configure.Common.CommonMethod;
import posserver.Configure.Common.Convert;
import posserver.Configure.Common.GlobalVar;
import posserver.Configure.Common.KeyValueStruct;
import posserver.Configure.Init.CompleteCfgPage;
import posserver.Configure.Init.InitCfgPage;
import posserver.Configure.Init.ServerTypeStruct;
import posserver.Configure.JBoss.ServerJBoss422GACfgPage;
import posserver.Configure.SysConfig.PosServerSysCfgPage;
import posserver.Configure.Tomcat.ServerTomCat5523CfgPage;

public class ConfigWizard extends Wizard
{
	public static final String Initcfg = "InitCfgPage";
	public static final String JBossgacfg = "ServerJBoss422GACfgPage";
	public static final String Tomcatcfg = "ServerTomCat5523CfgPage";
	public static final String Cmddefcfg = "CmdDefCfgPage";
	public static final String Posserversyscfg = "PosServerSysCfgPage";
	public static final String Completecfg = "CompleteCfgPage";
	
    //声明向导页面
    private InitCfgPage initCfg;
    private ServerJBoss422GACfgPage jBossGACfg; 
    private ServerTomCat5523CfgPage tomcatCfg;
    private CmdDefCfgPage cmdDefCfg;
    private PosServerSysCfgPage posServerSysCfg;
    private CompleteCfgPage completeCfg;
    
    public ConfigWizard()
    {
    	// 初始化全局数据
    	if (!initGlobalData())
    	{
    		return; 
    	}
    	
    	initCfg = new InitCfgPage();
    	jBossGACfg = new ServerJBoss422GACfgPage();
    	tomcatCfg = new ServerTomCat5523CfgPage();
    	cmdDefCfg = new CmdDefCfgPage();
    	posServerSysCfg = new PosServerSysCfgPage();
    	completeCfg = new CompleteCfgPage();
    	
    	this.addPage(initCfg);
    	this.addPage(jBossGACfg);
    	this.addPage(tomcatCfg);
    	this.addPage(cmdDefCfg);
    	this.addPage(posServerSysCfg);
    	this.addPage(completeCfg);
    	
        this.setWindowTitle("JAVAPOS POSSERVER 配置向导"); //向导标题
                                             //this.setHelpAvailable( true );
    }
    
    private boolean initGlobalData()
    {   
        // 初始化服务器类型
    	Vector vservertypecfg = ConfigWizard.read_File(GlobalVar.ServerTypeCfgPath);
        if (vservertypecfg == null)
        {
            return false;
        }
        
        for (int i = 0;i < vservertypecfg.size(); i++)
		{
			String[] row = (String[])vservertypecfg.get(i);
			
			if (row[0] == null) continue;
			
			if (row[0].length() > 2 && row[0].charAt(0) == '[' && row[0].charAt(row[0].length()-1) == ']')
			{   
				ServerTypeStruct sts = new ServerTypeStruct();
				sts.ServerType = row[0].substring(1, row[0].length() - 1);
				GlobalVar.ServerTypes.add(sts);
				
				for (int j = i+1;j < vservertypecfg.size(); j++)
				{
					String[] row1 = (String[])vservertypecfg.get(j);

					i++;
					
					if (row1[0] == null) continue;
					
					if (row1[0].length() > 2 && row1[0].charAt(0) == '[' && row1[0].charAt(row1[0].length()-1) == ']')
					{
						i = j-1;
						break;
					}
						
					String key = row1[0];
					String value = row1[1] == null?"":row1[1];
					
					if (key.equalsIgnoreCase("ServerPage"))
					{
						sts.ServerPage = value;
					}
					else if (key.equalsIgnoreCase("ServerJndiCfgPath"))
					{
						sts.ServerJndiCfgPath = value;
					}
					else if (key.equalsIgnoreCase("ServerPosServerCfgPath"))
					{
						sts.ServerPosServerCfgPath = value;
					}
					else if (key.equalsIgnoreCase("ServerPosServerCmdPath"))
					{
						sts.ServerPosServerCmdPath = value;
					}
					else if (key.equalsIgnoreCase("ServerJdbcJarInstallFile"))
					{
						sts.ServerJdbcJarInstallFile = value;
					}
					else if (key.equalsIgnoreCase("ServerJdbcJarInstallPath"))
					{
						sts.ServerJdbcJarInstallPath = value;
					}
					else if (key.equalsIgnoreCase("DatasoureName"))
					{
						sts.DatasoureName = value;
					}
					else if (key.equalsIgnoreCase("ServerPosServerInstallPath"))
					{
						sts.ServerPosServerInstallPath = value;
					} 	
				}
			}
		}
        
    	// 初始化配置文件
    	Vector vinitcfg = ConfigWizard.read_File(GlobalVar.InitCfgPath);
        if (vinitcfg == null)
        {
            return false;
        }
        
        for (int i = 0; i < vinitcfg.size(); i++)
        {
            String[] row = (String[]) vinitcfg.elementAt(i);
            
            if (row[0] == null) continue;
            if (row[0].equalsIgnoreCase("ServerType"))
            {
            	int index = Convert.toInt(row[1]);
            	if (index >= 0 && index < GlobalVar.ServerTypes.size())
            	{
            		ServerTypeStruct servertype = (ServerTypeStruct)GlobalVar.ServerTypes.get(index);
            		GlobalVar.initCfgDef.SetServerType(servertype);
            	}
            }
            else if (row[0].equalsIgnoreCase("ServerPath"))
            {
            	GlobalVar.initCfgDef.SetServerPath(row[1]);
            }
            else if (row[0].equalsIgnoreCase("PosServerPath"))
            {
            	File file = new File(CommonMethod.GetCurrentPath());
        		GlobalVar.initCfgDef.SetPosServerPath(file.getParent());
            	/*
            	if (row[1] == null || row[1].trim().length() == 0)
            	{
            		File file = new File(CommonMethod.GetCurrentPath());
            		GlobalVar.initCfgDef.SetPosServerPath(file.getParent());
            	}
            	else
            	{
            		GlobalVar.initCfgDef.SetPosServerPath(row[1]);
            	}
            	*/
            }
            else if (row[0].equalsIgnoreCase("PosServerName"))
            {
            	GlobalVar.initCfgDef.SetPosServerName(ConfigWizard.GetFileName(GlobalVar.initCfgDef.GetPosServerPath()));
            	/*
            	if (row[1] == null || row[1].trim().length() == 0)
            	{
            		GlobalVar.initCfgDef.SetPosServerName(ConfigWizard.GetFileName(GlobalVar.initCfgDef.GetPosServerPath()));
            	}
            	else
            	{
            		GlobalVar.initCfgDef.SetPosServerName(row[1]);
            	}
            	*/
            }
        }
        
        // 初始化字典
    	Vector vdictionarycfg = ConfigWizard.read_File(GlobalVar.DictionaryCfgPath);
        if (vdictionarycfg == null)
        {
            return false;
        }
        
        for (int i = 0;i < vdictionarycfg.size(); i++)
		{
			String[] row = (String[])vdictionarycfg.get(i);
			
			if (row[0] == null) continue;
			
			if (row[0].length() > 2 && row[0].charAt(0) == '[' && row[0].charAt(row[0].length()-1) == ']')
			{   
				String key = row[0].substring(1, row[0].length() - 1);
				
				for (int j = i+1;j < vdictionarycfg.size(); j++)
				{
					String[] row1 = (String[])vdictionarycfg.get(j);

					i++;
					
					if (row1[0] == null) continue;
					
					if (row1[0].length() > 2 && row1[0].charAt(0) == '[' && row1[0].charAt(row1[0].length()-1) == ']')
					{
						i = j-1;
						break;
					}
						
					String key1 = row1[0];
					String value1 = row1[1] == null?"":row1[1];
					
					if (key.equalsIgnoreCase("datasouremode"))
					{
						GlobalVar.Datasouremode.add(key1);
					}
					else if (key.equalsIgnoreCase("fileupdatedef"))
					{
						GlobalVar.Fileupdatedef.add(key1);
					}
					else if (key.equalsIgnoreCase("dayenddef"))
					{
						GlobalVar.Dayenddef.add(key1);
					}
					else if (key.equalsIgnoreCase("localdbtype"))
					{
						GlobalVar.Localdbtype.add(key1);
					}
					else if (key.equalsIgnoreCase("inputencoder"))
					{
						GlobalVar.Inputencoder.add(key1);
					}
					else if (key.equalsIgnoreCase("outencoder"))
					{
						GlobalVar.Outencoder.add(key1);
					}
					else if (key.equalsIgnoreCase("paradatatype"))
					{
						KeyValueStruct kvs = new KeyValueStruct();
						kvs.key = key1;
						kvs.value = value1;
						GlobalVar.Paradatatype.add(kvs);
					}
					else if (key.equalsIgnoreCase("StartTrans"))
					{
						GlobalVar.Starttrans.add(key1);
					}
					else if (key.equalsIgnoreCase("CmdType"))
					{
						GlobalVar.Cmdtype.add(key1);
					}
					else if (key.equalsIgnoreCase("Cmd_XX_Mode"))
					{
						GlobalVar.Cmd_XX_Mode.add(key1);
					}
					else if (key.equalsIgnoreCase("Sql_XX_Type"))
					{
						GlobalVar.Sql_XX_Type.add(key1);
					}
				}
			}
		}
        
        return true;
    }

    /* （非 Javadoc）
     * @see org.eclipse.jface.wizard.Wizard#canFinish()
     * 确定“完成”按钮是否可用,true为可用，false为不可用
     */
    public boolean canFinish()
    {
        //仅当当前页面为感谢页面时才将“完成”按钮置为可用状态
        if (this.getContainer().getCurrentPage() == completeCfg)
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

    public static void write_File(String fileName, Vector v)
    {
        PrintWriter pw = null;

        try
        {
            pw = CommonMethod.writeFileUTF(fileName);

            for (int i = 0; i < v.size(); i++)
            {
                String[] row = (String[]) v.elementAt(i);

                //当此行为空行时
                if ((row[0] == null) || (row[0].trim().length() <= 0))
                {
                    if (i > 0)
                    {
                        String[] s = (String[]) v.elementAt(i - 1);

                        if ((s[0] != null) && (s[0].trim().length() > 0))
                        {
                            pw.println("");
                        }
                    }

                    continue;
                }

                String line = Convert.appendStringSize("", row[0], 0, 20, 20);

                //当此行是[]标注时
                if (line.trim().charAt(0) == '[')
                {
                    if (i > 0)
                    {
                        String[] s = (String[]) v.elementAt(i - 1);

                        if ((s[0] != null) && (s[0].trim().length() > 0))
                        {
                            pw.println("");
                        }
                    }

                    pw.println(line.trim());

                    continue;
                }

                if (((row[1] == null) || row[1].trim().equals("")) && ((row[2] == null) || row[2].trim().equals("")))
                {
                    line += " =";
                }
                else if ((row[1] == null) || row[1].trim().equals(""))
                {
                    line += (" = " + Convert.appendStringSize("", "", 0, 60, 60));
                }
                else
                {
                    line += (" = " + Convert.appendStringSize("", row[1].trim(), 0, 60, 60));
                }

                if ((row[2] != null) && (row[2].trim().length() > 0))
                {
                    line += (" && " + row[2].trim());
                }

                pw.println(line);
            }
        }
        catch (Exception er)
        {
            er.printStackTrace();
        }
        finally
        {
            if (pw != null)
            {
                pw.close();
            }
        }
    }

    public static Vector read_File(String fileName)
    {
        BufferedReader br = null;

        br = CommonMethod.readFile(fileName);

        if (br == null)
        {
            return null;
        }

        Vector v = new Vector();
        String line;
        String[] content = null;

        try
        {
            while ((line = br.readLine()) != null)
            {
                content = new String[3];

                line = line.trim();
/*
                if (line.indexOf('[') >= 0)
                {
                    line = ManipulateStr.delSpecialChar(line);
                }
*/
                if ((line == null) || (line.trim().length() <= 0))
                {
                    v.add(new String[3]);

                    continue;
                }

                String[] lines = new String[2];

                if (line.indexOf("&&") < 0)
                {
                    lines[0] = line;
                    lines[1] = null;
                }
                else
                {
                    lines[0] = line.substring(0, line.indexOf("&&"));
                    lines[1] = line.substring(line.indexOf("&&") + 2);
                }

                if (lines[1] == null)
                {
                    content[2] = null;
                }
                else
                {
                    content[2] = lines[1].trim();
                }

                if (lines[0].indexOf("=") < 0)
                {
                    content[0] = lines[0].trim();
                    content[1] = null;
                }
                else
                {
                    content[0] = lines[0].substring(0, lines[0].indexOf("=")).trim();
                    content[1] = lines[0].substring(lines[0].indexOf("=") + 1).trim();
                }

                v.add(content);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (br != null)
                {
                    br.close();
                }
            }
            catch (IOException e)
            {
            }
        }

        return v;
    }
    
    public static String GetFileName(String path)
    {
		String str = "";
		int index = path.lastIndexOf('\\');
		if (index >= 0)
		{
			str = path.substring(index+1,path.length());
			int index1 = str.lastIndexOf('.');
			
			if (index1 > 0) str = str.substring(0,index1);
		}
		
		return str;
    }
}
