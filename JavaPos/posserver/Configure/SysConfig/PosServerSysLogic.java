package posserver.Configure.SysConfig;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import posserver.Configure.Common.GlobalVar;
import posserver.Configure.JBoss.ServerJBoss422GALogic;

public class PosServerSysLogic extends ServerJBoss422GALogic
{	
	public PosServerSysLogic()
	{
		super();
		this.Configs.add(new PosServerSysStruct());
	}
	
	public String Getcfgmodelpath()
	{
		return "";
	}
	
	public Vector GetCfgModel()
	{
		return null;
	}
	
	public void InitCfgModel()
	{
		CfgModel = null;
	}
	
	public void RefreshPath()
	{
		cfgpath = GlobalVar.ServerPosServerCfgPath;
	}
	
	public Vector GetConfig()
	{		
		return ((PosServerSysStruct)Configs.get(0)).datasource;
	}
	
	public Vector GetAllConfig()
	{
		return Configs;
	}
	
	// 获得xml文档根元素的名称
	public String GetRootElementName()
	{
		return "Sysconfig";
	}
	
	/**
	 * 初始化数据
	 * @return 是否初始化成功
	 */
	public boolean InitData()
	{
		ClearConfigs();
		
		if (LoadData(GetAllConfig(),GetCfgPath()))
		{
			UpdateDataSourceName();
			return true;
		}
		
		return false;
	}
	
	public void FromDocumentDom4j(Document doc,Vector config)
	{		
		//config.clear();
		
		Element element = doc.getRootElement();
		
		if (element.getName().equalsIgnoreCase(GetRootElementName()))
		{
			PosServerSysStruct sss = (PosServerSysStruct)config.get(0);
			
			Iterator iter1 = element.elementIterator();
			while(iter1.hasNext())
			{
				Element element1 = (Element)iter1.next();
				String key1 = element1.getName();
				String value1 = element1.getTextTrim();
				
				if (key1.equalsIgnoreCase("username"))
				{
					sss.username = value1;
				} 
				else if (key1.equalsIgnoreCase("password"))
				{
					sss.password = value1;
				}
				else if (key1.equalsIgnoreCase("cashareadef"))
				{
					sss.cashareadef = value1;
				}
				else if (key1.equalsIgnoreCase("cashconnserver"))
				{
					sss.cashconnserver = value1;
				}
				else if (key1.equalsIgnoreCase("tasktime"))
				{
					sss.tasktime = value1;
				}
				else if (key1.equalsIgnoreCase("inspecttime"))
				{
					sss.inspecttime = value1;
				}
				else if (key1.equalsIgnoreCase("dayenddef"))
				{
					sss.dayenddef = value1;
				}
				else if (key1.equalsIgnoreCase("autodayendtime"))
				{
					sss.autodayendtime = value1;
				}
				else if (key1.equalsIgnoreCase("fileupdatedef"))
				{
					sss.fileupdatedef = value1;
				}
				else if (key1.equalsIgnoreCase("fileupdatestarttime"))
				{
					sss.fileupdatestarttime = value1;
				}
				else if (key1.equalsIgnoreCase("fileupdateendtime"))
				{
					sss.fileupdateendtime = value1;
				}
				else if (key1.equalsIgnoreCase("localdbtype"))
				{
					sss.localdbtype = value1;
				}
				else if (key1.equalsIgnoreCase("datasoureinfo"))
				{
					Iterator iter2 = element1.elementIterator();
					
					PosServerSysDataSourceStruct psdss = new PosServerSysDataSourceStruct();
					psdss.id = element1.attributeValue("id");
					sss.datasource.add(psdss);
					
					while(iter2.hasNext())
					{
						Element element2 = (Element)iter2.next();
						String key2 = element2.getName();
						String value2 = element2.getTextTrim();
						
						if (key2.equalsIgnoreCase("datasouremode"))
						{
							psdss.datasouremode = value2;
						}
						else if (key2.equalsIgnoreCase("datasourename"))
						{
							psdss.datasourename = value2;
						}
						else if (key2.equalsIgnoreCase("datasoureurl"))
						{
							psdss.datasoureurl = value2;
						}
						else if (key2.equalsIgnoreCase("cmddefcmdcode"))
						{
							String[] str = value2.split(",");
							
							for (int i = 0;i < str.length ; i++)
							{
								psdss.cmddefcmdcode.add(str[i]);
							}
						}
						else if (key2.equalsIgnoreCase("fileupdatecmdcode"))
						{
							String[] str = value2.split(",");
							
							for (int i = 0;i < str.length ; i++)
							{
								psdss.fileupdatecmdcode.add(str[i]);
							}
						}
						else if (key2.equalsIgnoreCase("createtablecmdcode"))
						{
							String[] str = value2.split(",");
							
							for (int i = 0;i < str.length ; i++)
							{
								psdss.createtablecmdcode.add(str[i]);
							}
						}
						else if (key2.equalsIgnoreCase("inputencoder"))
						{
							psdss.inputencoder = value2;
						}
						else if (key2.equalsIgnoreCase("outencoder"))
						{
							psdss.outencoder = value2;
						}
					}
				}
				else if (key1.equalsIgnoreCase("globalftppath"))
				{
					Iterator iter2 = element1.elementIterator();
					
					while(iter2.hasNext())
					{
						Element element2 = (Element)iter2.next();
						String key2 = element2.getName();
						//String value2 = element2.getTextTrim();
						
						if (key2.equalsIgnoreCase("ftpparameter"))
						{
							Iterator iter3 = element2.elementIterator();
							
							PosServerSysFtpParameterStruct psdss = new PosServerSysFtpParameterStruct();
							sss.globalftppath.add(psdss);
							
							while(iter3.hasNext())
							{
								Element element3 = (Element)iter3.next();
								String key3 = element3.getName();
								String value3 = element3.getTextTrim();
								
								if (key3.equalsIgnoreCase("parameteraddress"))
								{
									psdss.parameteraddress = value3;
								}
								else if (key3.equalsIgnoreCase("parametervalue"))
								{
									psdss.parametervalue = value3;
								}
							}
						}
					}
				}
				else if (key1.equalsIgnoreCase("globalparameter"))
				{
					Iterator iter2 = element1.elementIterator();
					
					while(iter2.hasNext())
					{
						Element element2 = (Element)iter2.next();
						String key2 = element2.getName();
						//String value2 = element2.getTextTrim();
						
						if (key2.equalsIgnoreCase("parameter"))
						{
							Iterator iter3 = element2.elementIterator();
							
							PosServerSysParameterStruct psdss = new PosServerSysParameterStruct();
							sss.globalparameter.add(psdss);
							
							while(iter3.hasNext())
							{
								Element element3 = (Element)iter3.next();
								String key3 = element3.getName();
								String value3 = element3.getTextTrim();
								
								if (key3.equalsIgnoreCase("paraname"))
								{
									psdss.paraname = value3;
								}
								else if (key3.equalsIgnoreCase("paravalue"))
								{
									psdss.paravalue = value3;
								}
							}
						}
					}
				}
			}
		}
	}
	
	public void ClearConfigs()
	{
		if (GetAllConfig() != null && GetAllConfig().size() > 0)
		{
			((PosServerSysStruct)GetAllConfig().get(0)).datasource.clear();
			((PosServerSysStruct)GetAllConfig().get(0)).tasktime = "";
			((PosServerSysStruct)GetAllConfig().get(0)).inspecttime = "";
			((PosServerSysStruct)GetAllConfig().get(0)).dayenddef = "";
			((PosServerSysStruct)GetAllConfig().get(0)).autodayendtime = "";
			((PosServerSysStruct)GetAllConfig().get(0)).fileupdatedef = "";
			((PosServerSysStruct)GetAllConfig().get(0)).fileupdateendtime = "";
			((PosServerSysStruct)GetAllConfig().get(0)).fileupdatestarttime = "";
			((PosServerSysStruct)GetAllConfig().get(0)).fileupdateendtime = "";
			((PosServerSysStruct)GetAllConfig().get(0)).localdbtype = "";
			
			((PosServerSysStruct)GetAllConfig().get(0)).cashareadef = "";
			((PosServerSysStruct)GetAllConfig().get(0)).cashconnserver = "";
			((PosServerSysStruct)GetAllConfig().get(0)).username = "";
			((PosServerSysStruct)GetAllConfig().get(0)).password = "";
			((PosServerSysStruct)GetAllConfig().get(0)).globalftppath.clear();
			((PosServerSysStruct)GetAllConfig().get(0)).globalparameter.clear();
		}
	}
	
	public Element ToXmlElementDom4j(Object obj)
	{
		  try
			{
			    PosServerSysStruct sss = (PosServerSysStruct)obj;
			    Element Sysconfig = DocumentHelper.createElement("Sysconfig");
			    Sysconfig.addElement("username").setText(sss.username);
			    Sysconfig.addElement("password").setText(sss.password);
			    Sysconfig.addElement("tasktime").setText(sss.tasktime);
			    Sysconfig.addElement("inspecttime").setText(sss.inspecttime);
			    Sysconfig.addElement("dayenddef").setText(sss.dayenddef);
			    Sysconfig.addElement("autodayendtime").setText(sss.autodayendtime);
			    Sysconfig.addElement("fileupdatedef").setText(sss.fileupdatedef);
			    Sysconfig.addElement("fileupdatestarttime").setText(sss.fileupdatestarttime);
			    Sysconfig.addElement("fileupdateendtime").setText(sss.fileupdateendtime);
			    Sysconfig.addElement("localdbtype").setText(sss.localdbtype);
			    Sysconfig.addElement("cashareadef").setText(sss.cashareadef);
			    Sysconfig.addElement("cashconnserver").setText(sss.cashconnserver);
			    
			    Element globalftppath = Sysconfig.addElement("globalftppath");
			    Element globalparameter = Sysconfig.addElement("globalparameter");
			    
			    Iterator iter = sss.datasource.iterator();
			    while(iter.hasNext())
			    {
			    	PosServerSysDataSourceStruct psss = (PosServerSysDataSourceStruct)iter.next();
			    	Element element1 = Sysconfig.addElement("datasoureinfo");
			    	element1.addAttribute("id", psss.id);
			    	element1.addElement("datasouremode").setText(psss.datasouremode);
			    	element1.addElement("datasourename").setText(psss.datasourename);
			    	element1.addElement("datasoureurl").setText(psss.datasoureurl);
			    	
			    	String cmddefcmdcode = "";
			    	String fileupdatecmdcode = "";
			    	String createtablecmdcode = "";
			    	
			    	for (int i = 0;i < psss.cmddefcmdcode.size();i++)
			    	{
			    		String str = (String)psss.cmddefcmdcode.get(i);
			    		cmddefcmdcode = cmddefcmdcode + str +",";
			    	}
			    	
			    	if (cmddefcmdcode.length() > 0)
			    	{
			    		cmddefcmdcode = cmddefcmdcode.substring(0,cmddefcmdcode.length() -1);
			    	}
			    	
			    	for (int i = 0;i < psss.fileupdatecmdcode.size();i++)
			    	{
			    		String str = (String)psss.fileupdatecmdcode.get(i);
			    		fileupdatecmdcode = fileupdatecmdcode + str +",";
			    	}
			    	
			    	if (fileupdatecmdcode.length() > 0)
			    	{
			    		fileupdatecmdcode = fileupdatecmdcode.substring(0,fileupdatecmdcode.length() -1);
			    	}
			    	
			    	for (int i = 0;i < psss.createtablecmdcode.size();i++)
			    	{
			    		String str = (String)psss.createtablecmdcode.get(i);
			    		createtablecmdcode = createtablecmdcode + str +",";
			    	}
			    	
			    	if (createtablecmdcode.length() > 0)
			    	{
			    		createtablecmdcode = createtablecmdcode.substring(0,createtablecmdcode.length() -1);
			    	}
			    	
			    	element1.addElement("cmddefcmdcode").setText(cmddefcmdcode);
			    	element1.addElement("fileupdatecmdcode").setText(fileupdatecmdcode);
			    	element1.addElement("createtablecmdcode").setText(createtablecmdcode);
			    	element1.addElement("inputencoder").setText(psss.inputencoder);
			    	element1.addElement("outencoder").setText(psss.outencoder);
			    }
			    
			    iter = sss.globalftppath.iterator();
			    while(iter.hasNext())
			    {
			    	PosServerSysFtpParameterStruct psss = (PosServerSysFtpParameterStruct)iter.next();
			    	Element element1 = globalftppath.addElement("ftpparameter");
			    	element1.addElement("parameteraddress").setText(psss.parameteraddress);
			    	element1.addElement("parametervalue").setText(psss.parametervalue);
			    }
			    
			    iter = sss.globalparameter.iterator();
			    while(iter.hasNext())
			    {
			    	PosServerSysParameterStruct psss = (PosServerSysParameterStruct)iter.next();
			    	Element element1 = globalparameter.addElement("parameter");
			    	element1.addElement("paraname").setText(psss.paraname);
			    	element1.addElement("paravalue").setText(psss.paravalue);
			    }	  
			    
				return Sysconfig;
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return null;
			}
	}
	
	public void GetDictionaryFromVector(Vector v,Vector v1)
	{
		/*
		Vector vr = v1;
		v1.clear();
		
		for (int i = 0;i < v.size(); i++)
		{
			String[] row = (String[])v.get(i);
			
			if (row[0] == null) continue;
			
			if (row[0].length() > 2 && row[0].charAt(0) == '[' && row[0].charAt(row[0].length()-1) == ']')
			{   
				ServerTomCat5523Struct sts = new ServerTomCat5523Struct();
				ServerTomCat5523JndiModelStruct sjjms = new ServerTomCat5523JndiModelStruct();
				sjjms.TomCat5523CfgModelData = sts;
				sjjms.DataBaseType = row[0].substring(1, row[0].length() - 1);
				vr.add(sjjms);
				
				for (int j = i+1;j < v.size(); j++)
				{
					String[] row1 = (String[])v.get(j);

					i++;
					
					if (row1[0] == null) continue;
					
					if (row1[0].length() > 2 && row1[0].charAt(0) == '[' && row1[0].charAt(row1[0].length()-1) == ']')
					{
						i = j-1;
						break;
					}
						
					String key = row1[0];
					String value = row1[1] == null?"":row1[1];
					
					if (key.equalsIgnoreCase("name"))
					{
						sts.name = value;
					}
					else if (key.equalsIgnoreCase("type"))
					{
						sts.type = value;
					}
					else if (key.equalsIgnoreCase("driverClassName"))
					{
						sts.driverClassName = value;
					}
					else if (key.equalsIgnoreCase("url"))
					{
						sts.url = value;
					}
					else if (key.equalsIgnoreCase("password"))
					{
						sts.password = value;
					}
					else if (key.equalsIgnoreCase("username"))
					{
						sts.username = value;
					}
					else if (key.equalsIgnoreCase("password"))
					{
						sts.password = value;
					}
					else if (key.equalsIgnoreCase("maxIdle"))
					{
						sts.maxIdle = value;
					}
					else if (key.equalsIgnoreCase("maxActive"))
					{
						sts.maxActive = value;
					}
					else if (key.equalsIgnoreCase("maxWait"))
					{
						sts.maxWait = value;
					}
				}
			}
		}
		*/
	}
	
	/**
	 * 判断是否可以保存
	 * @param 主健
	 * @param index>=0为修改的索引,index <0新增
	 * @return
	 */
	public boolean CheckSave(String key,int index)
	{
		for (int i = 0;i < this.GetConfig().size(); i++)
		{
			if (i == index) continue;
			PosServerSysDataSourceStruct sjs = (PosServerSysDataSourceStruct)GetConfig().get(i);
			if (sjs.id.equalsIgnoreCase(key))
			{
				strmsg = "名称为" + key + "的jndi名称已经存在!";
				return false;
			}
		}
		
		return true;
	}
	
	public boolean CanContact()
	{
		return true;
	}
	
	public void UpdateDataSourceName()
	{
		
	}
	
	public boolean Save()
	{
		File file = new File(GetCfgPath());
		
		// 如果修改的文件不是现在装载的文件则提示
		if (file.exists()) 
		{
			if (!xmlOper.GetPath().equalsIgnoreCase(GetCfgPath()))
			{
			     MessageBox msgbox=new MessageBox(new Shell(),SWT.YES|SWT.NO);
			     msgbox.setMessage("您要保存的文件已存在，是否覆盖该文件?");
			     if (msgbox.open() == SWT.NO)
			     {
			    	 this.strmsg = "您好要保存的文件已存在!";
			    	 return false;
			     }
			     else
			     {
						if (!xmlOper.CreateXml(GetCfgPath(),this.GetRootElementName(),null))
						{
							this.strmsg = "创建配置文件失败!" + xmlOper.GetMsg(); 
							return false;
						}
			     }
			}
		}
		else
		{
			if (!xmlOper.CreateXml(GetCfgPath(),this.GetRootElementName(),null))
			{
				this.strmsg = "创建配置文件失败!" + xmlOper.GetMsg(); 
				return false;
			}
		}
		
		Element[] elements = ToElementsDom4j(Configs);
		xmlOper.RemoveRootElement();
		xmlOper.AddElement(null,elements[0]);
		
		if (!xmlOper.WriteXmlDocument())
		{
			strmsg = xmlOper.GetMsg();
			return false;
		}
			
		UpdateDataSourceName();
		
		return true;
	}
	
	/**
	 * 装载数据
	 * @param 装载数据装载到的Vector
	 * @return 是否装载成功
	 */
	public boolean LoadData(Vector v,String path)
	{
		if (!xmlOper.LoadXml(path))
		{
			strmsg = xmlOper.GetMsg();
			return false;
		}
		
		if (!CheckInvalidCfg(xmlOper.GetDocument()))
		{	
			strmsg = "配置文件格式错误!";
			return false;
		}
		
		FromDocumentDom4j(xmlOper.GetDocument(),v);
		
		return true;
	}
}
