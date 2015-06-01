package com.efuture.javaPos.Global;

import java.io.BufferedReader;
import java.util.HashMap;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Struct.WebServiceFunDef;

public class WebServiceConfigClass
{
	protected static WebServiceConfigClass wscc = null;
	private boolean isLoad =  false;
	private String endpoint 			= null;							// WebService访问服务器路径
	private String namingspace			= null;							// 访问命名空间名
	private String webServicetimeout	= "5000";						// 等待时间
	private HashMap funlist			= null;							// 方法列表
	
	public static WebServiceConfigClass getDefault()
    {
        if (WebServiceConfigClass.wscc == null)
        {
        	WebServiceConfigClass.wscc = new WebServiceConfigClass();
                                                       
        }

        return WebServiceConfigClass.wscc;
    }
	
	public boolean ReadWebServiceConfigFile()
    {
		BufferedReader br = null;
		
		if (!CommonMethod.isFileExist(GlobalVar.ConfigPath + "//WebServiceConfig.ini")) return true;
		
		br = CommonMethod.readFile(GlobalVar.ConfigPath + "//WebServiceConfig.ini");
		
		if ( br == null)
        {
            return false;
        }
		
		String line = null;
		String[] sp = null;
		WebServiceFunDef wsfd = null;
		
		try
		{
			funlist = new HashMap();
			
			while ((line = br.readLine()) != null)
			{
				if ((line == null) || (line.length() <= 0))
				{
					continue;
				}
				
				String[] lines = line.split("&&");
				
				if (lines[0].equalsIgnoreCase("<Begin>"))
				{
					wsfd = new  WebServiceFunDef();
					
					continue;
				}
				
				if (lines[0].equalsIgnoreCase("<End>"))
				{
					
					funlist.put(wsfd.getCmdCode(),wsfd);
					
					continue;
				}
				
				sp = lines[0].split("=");
				
				if (sp.length < 2) continue;
				
				if (sp[0].trim().compareToIgnoreCase("EndPoint") == 0)
				{
					setEndPoint(sp[1].trim());
				}
				else if (sp[0].trim().compareToIgnoreCase("NamingSpace") == 0)
				{
					setNamingSpace(sp[1].trim());
				}
				else if (sp[0].trim().compareToIgnoreCase("WebServiceTimeOut") == 0)
				{
					setWebServicetimeout(sp[1].trim());
				}
				else if (sp[0].trim().compareToIgnoreCase("CmdCode") == 0)
				{
					wsfd.setCmdCode(sp[1].trim());
				}
				else if (sp[0].trim().compareToIgnoreCase("MethodName") == 0)
				{
					wsfd.setMethodName(sp[1].trim());
				}
				else if (sp[0].trim().compareToIgnoreCase("JavaPosMappingName") == 0)
				{
					wsfd.setJavaPosMappingName(sp[1].trim());
				}
				else if (sp[0].trim().compareToIgnoreCase("ParameterName") == 0)
				{
					wsfd.setParameterName(sp[1].trim());
				}
				else if (sp[0].trim().compareToIgnoreCase("ParameterType") == 0)
				{
					wsfd.setParameterType(sp[1].trim());
				}
				else if (sp[0].trim().compareToIgnoreCase("ReturnType") == 0)
				{
					wsfd.setReturnType(sp[1].trim());
				}
				else if (sp[0].trim().compareToIgnoreCase("RetSuccessError") == 0)
				{
					wsfd.setRetSuccessError(sp[1].trim());
				}
			}
			
			isLoad = true;
			
			return isLoad;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
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
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
    }
	
	public void setEndPoint(String endpoint)
	{
		this.endpoint = endpoint;
	}
	
	public String getEndPoint()
	{
		return endpoint;
	}
	
	public void setNamingSpace(String nameingspace)
	{
		this.namingspace = nameingspace;
	}
	
	public String getNamingSpace()
	{
		return namingspace;
	}
	
	public void setWebServicetimeout(String webServicetimeout)
	{
		this.webServicetimeout	= webServicetimeout;
	}
	
	public String getWebServicetimeout()
	{
		return webServicetimeout;
	}
	
	public HashMap getFunList()
	{
		return funlist;
	}
	
	public boolean isLoad()
	{
		return isLoad;
	}
}


