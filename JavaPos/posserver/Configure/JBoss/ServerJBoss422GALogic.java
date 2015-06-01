package posserver.Configure.JBoss;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import posserver.Configure.ConfigWizard;
import posserver.Configure.CmdDef.DataSourceCommonStruct;
import posserver.Configure.Common.DOM4JXML;
import posserver.Configure.Common.GlobalVar;
import posserver.Configure.Init.ServerTypeStruct;

public class ServerJBoss422GALogic
{
	protected String cfgpath = "";

	public void RefreshPath()
	{
		cfgpath = GlobalVar.initCfgDef.GetServerPath();
		
		ServerTypeStruct servertype = GlobalVar.initCfgDef.GetServerType();
		
		if (servertype != null)
		{
			String servername = GlobalVar.initCfgDef.GetPosServerName();
			if (servername.length() > 0)
			{
				cfgpath = cfgpath + servertype.ServerJndiCfgPath.replace("[SerName]",GlobalVar.initCfgDef.GetPosServerName());
			}
			else
			{
				cfgpath = cfgpath + servertype.ServerJndiCfgPath;
			}
		}
	}
	
	public void SetCfgPath(String path)
	{
		cfgpath = path;
	}
	
	public String GetCfgPath()
	{
		return cfgpath;
	}
	
	public String Getcfgmodelpath()
	{
		return GlobalVar.Path + "\\JBoss422GACfgiModel.ini";
	}
	
	// 获得xml文档根元素的名称
	public String GetRootElementName()
	{
		return "datasources";
	}
		
	protected Vector Configs = new Vector();
	
	public Vector GetConfig()
	{
		return Configs;
	}
	
	protected Vector CfgModel = new Vector();
	
	public Vector GetCfgModel()
	{
		return CfgModel;
	}
	
	protected String strmsg = "";	
	protected DOM4JXML xmlOper = new DOM4JXML();
	
	public ServerJBoss422GALogic()
	{
		InitCfgModel();
	}
	
	public void InitCfgModel()
	{
		Vector v = ConfigWizard.read_File(Getcfgmodelpath());
		
		if (v != null)
		{
			GetDictionaryFromVector(v,CfgModel);
		}
	}
	
	public String getMsg()
	{
		return strmsg;
	}
	
	public boolean CheckInvalidCfg(Document doc)
	{
		Element rootelement = doc.getRootElement();
		if (rootelement == null) return false;
		
		if (!rootelement.getName().equalsIgnoreCase(GetRootElementName())) return false;
		
		return true;
	}
	
	/**
	 * 装载数据
	 * @param 装载数据装载到的Vector
	 * @return 是否装载成功
	 */
	public boolean LoadData(Vector v,String path)
	{
		strmsg = "";
		
		if (!xmlOper.LoadXml(path))
		{
			v.clear();
			strmsg = xmlOper.GetMsg();
			return false;
		}
		
		if (!CheckInvalidCfg(xmlOper.GetDocument()))
		{	
			v.clear();
			strmsg = "配置文件格式错误!";
			return false;
		}
		
		FromDocumentDom4j(xmlOper.GetDocument(),v);
		
		return true;
	}
	
	public void ClearConfigs()
	{
		this.GetConfig().clear();
	}
	
	/**
	 * 初始化数据
	 * @return 是否初始化成功
	 */
	public boolean InitData()
	{
		ClearConfigs();
		
		if (LoadData(GetConfig(),GetCfgPath()))
		{
			UpdateDataSourceName();
			return true;
		}
		
		return false;
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
		xmlOper.RemoveChildElement(xmlOper.GetRootElement());
		
		Element rootelement = xmlOper.GetRootElement();
		for (int i = 0;i < elements.length ; i++)
		{
			rootelement.add(elements[i]);
		}
		
		if (!xmlOper.WriteXmlDocument())
		{
			strmsg = xmlOper.GetMsg();
			return false;
		}
			
		UpdateDataSourceName();
		
		return true;
	}
	
	/**
	 * 获得各种数据库类型配置的初始值
	 * @return 返回ServerJBoss422GAStruct，表示各种数据库类型包含的初始值
	 */
	public void GetDictionaryFromVector(Vector v,Vector v1)
	{
		Vector vr = v1;
		vr.clear();
		for (int i = 0;i < v.size(); i++)
		{
			String[] row = (String[])v.get(i);
			
			if (row[0] == null) continue;
			
			if (row[0].length() > 2 && row[0].charAt(0) == '[' && row[0].charAt(row[0].length()-1) == ']')
			{   
				ServerJBoss422GAStruct sjs = new ServerJBoss422GAStruct();
				ServerJBoss422GAJndiModelStruct sjjms = new ServerJBoss422GAJndiModelStruct();
				sjjms.JBoss422CfgModelData = sjs;
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
					
					if (key.equalsIgnoreCase("jndi-name"))
					{
						sjs.jndi_name = value;
					}
					else if (key.equalsIgnoreCase("connection-url"))
					{
						sjs.connection_url = value;
					}
					else if (key.equalsIgnoreCase("driver-class"))
					{
						sjs.driver_class = value;
					}
					else if (key.equalsIgnoreCase("user-name"))
					{
						sjs.user_name = value;
					}
					else if (key.equalsIgnoreCase("password"))
					{
						sjs.password = value;
					}
					else if (key.equalsIgnoreCase("min-pool-size"))
					{
						sjs.min_pool_size = value;
					}
					else if (key.equalsIgnoreCase("max-pool-size"))
					{
						sjs.max_pool_size = value;
					}
					else if (key.equalsIgnoreCase("blocking-timeout-millis"))
					{
						sjs.blocking_timeout_millis = value;
					}
					else if (key.equalsIgnoreCase("idle-timeout-minutes"))
					{
						sjs.idle_timeout_minutes = value;
					}
					else if (key.equalsIgnoreCase("SetBigStringTryClob"))
					{
						sjs.SetBigStringTryClob = value;
					}
					else if (key.equalsIgnoreCase("exception-sorter-class-name"))
					{
						sjs.exception_sorter_class_name = value;
					}
					else if (key.equalsIgnoreCase("type-mapping"))
					{
						sjs.type_mapping = value;
					}
				}
			}
		}
	}
	
	/**
	 * 从Dom4j的Document返回ServerJBoss422GAStruct的实例集合
	 * @param doc document
	 * @return 返回ServerJBoss422GAStruct实例的集合
	 */
	public void FromDocumentDom4j(Document doc,Vector config)
	{		
		config.clear();
		Element element = doc.getRootElement();
		
		if (element.getName().equalsIgnoreCase(GetRootElementName()))
		{
			Iterator iter = element.elementIterator();
			
			while(iter.hasNext())
			{
				Element element1 = (Element)iter.next();
				String strelementname = element1.getName();
				
				if (strelementname.equalsIgnoreCase("local-tx-datasource"))
				{
					Iterator iter1 = element1.elementIterator();
					ServerJBoss422GAStruct sjs = new ServerJBoss422GAStruct();
					config.add(sjs);
					while(iter1.hasNext())
					{
						Element element2 = (Element)iter1.next();
						String strelementname2 = element2.getName();
						
						if (strelementname2.equalsIgnoreCase("jndi-name"))
						{
							sjs.jndi_name = element2.getTextTrim();
						}
						else if (strelementname2.equalsIgnoreCase("connection-url"))
						{
							sjs.connection_url = element2.getTextTrim();
						}
						else if (strelementname2.equalsIgnoreCase("driver-class"))
						{
							sjs.driver_class = element2.getTextTrim();
						}
						else if (strelementname2.equalsIgnoreCase("user-name"))
						{
							sjs.user_name = element2.getTextTrim();
						}
						else if (strelementname2.equalsIgnoreCase("password"))
						{
							sjs.password = element2.getTextTrim();
						}
						else if (strelementname2.equalsIgnoreCase("min-pool-size"))
						{
							sjs.min_pool_size = element2.getTextTrim();
						}
						else if (strelementname2.equalsIgnoreCase("max-pool-size"))
						{
							sjs.max_pool_size = element2.getTextTrim();
						}
						else if (strelementname2.equalsIgnoreCase("blocking-timeout-millis"))
						{
							sjs.blocking_timeout_millis = element2.getTextTrim();
						}
						else if (strelementname2.equalsIgnoreCase("idle-timeout-minutes"))
						{
							sjs.idle_timeout_minutes = element2.getTextTrim();
						}
						else if (strelementname2.equalsIgnoreCase("SetBigStringTryClob"))
						{
							sjs.SetBigStringTryClob = element2.getTextTrim();
						}
						else if (strelementname2.equalsIgnoreCase("exception-sorter-class-name"))
						{
							sjs.exception_sorter_class_name = element2.getTextTrim();
						}
						else if (strelementname2.equalsIgnoreCase("metadata"))
						{
							Iterator iter2 = element2.elementIterator();
							
							while(iter2.hasNext())
							{
								Element element3 = (Element)iter2.next();
								String strelementname3 = element3.getName();
								if (strelementname3.equalsIgnoreCase("type-mapping"))
								{
									sjs.type_mapping = element3.getTextTrim();
								}
							}
						}

					}
				}
			}
		}
	}
	
	public Element[] ToElementsDom4j(Vector v)
	{
		Element[] elements = new Element[v.size()];
		
		for (int i = 0;i < v.size(); i++)
		{
			elements[i] = ToXmlElementDom4j(v.get(i)); 
		}
		
		return elements;
	}
	
	public Element ToXmlElementDom4j(Object obj)
	{
		  try
			{
			  	ServerJBoss422GAStruct sjs = (ServerJBoss422GAStruct)obj;
			    Element local_tx_datasource = DocumentHelper.createElement("local-tx-datasource");
			    Element jndi_name = local_tx_datasource.addElement("jndi-name");
			    Element connection_url = local_tx_datasource.addElement("connection-url");
			    Element driver_class = local_tx_datasource.addElement("driver-class");
			    Element user_name = local_tx_datasource.addElement("user-name");
			    Element password = local_tx_datasource.addElement("password");
			    Element min_pool_size = local_tx_datasource.addElement("min-pool-size");
			    Element max_pool_size = local_tx_datasource.addElement("max-pool-size");
			    Element blocking_timeout_millis = local_tx_datasource.addElement("blocking-timeout-millis");
			    Element idle_timeout_minutes = local_tx_datasource.addElement("idle-timeout-minutes");
			    Element SetBigStringTryClob = local_tx_datasource.addElement("SetBigStringTryClob");
			    Element exception_sorter_class_name = local_tx_datasource.addElement("exception-sorter-class-name");
			    Element metadata = local_tx_datasource.addElement("metadata");
			    Element type_mapping = metadata.addElement("type-mapping");

			    jndi_name.setText(sjs.jndi_name);
			    connection_url.setText(sjs.connection_url);
			    driver_class.setText(sjs.driver_class);
			    user_name.setText(sjs.user_name);
			    password.setText(sjs.password);
			    min_pool_size.setText(sjs.min_pool_size);
			    max_pool_size.setText(sjs.max_pool_size);
			    blocking_timeout_millis.setText(sjs.blocking_timeout_millis);
			    idle_timeout_minutes.setText(sjs.idle_timeout_minutes);
			    SetBigStringTryClob.setText(sjs.SetBigStringTryClob);
			    exception_sorter_class_name.setText(sjs.exception_sorter_class_name);
			    type_mapping.setText(sjs.type_mapping);
			
			    //String str = local_tx_datasource.asXML();
			    
				return local_tx_datasource;
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return null;
			}
	}
	
	/**
	 * 判断是否可以保存
	 * @param 主健
	 * @param index>=0为修改的索引,index <0新增
	 * @return
	 */
	public boolean CheckSave(String key,int index)
	{
		for (int i = 0;i < this.Configs.size(); i++)
		{
			if (i == index) continue;
			ServerJBoss422GAStruct sjs = (ServerJBoss422GAStruct)Configs.get(i);
			if (sjs.jndi_name.equalsIgnoreCase(key))
			{
				strmsg = "名称为" + key + "的jndi名称已经存在!";
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * 是否可继续下一页
	 * @return
	 */
	public boolean CanContact()
	{
		UpdatePosServerCfgPath(GetCfgPath());
		
		return true;
	}
	
	public void UpdateDataSourceName()
	{
		Vector configs = GetConfig();
		if (configs != null)
		{
			GlobalVar.Datasourcecommon.clear();
			
			for(int i = 0;i < configs.size();i++)
			{
				ServerJBoss422GAStruct sjs = (ServerJBoss422GAStruct)configs.get(i);
				
				DataSourceCommonStruct dscs = new DataSourceCommonStruct();
				dscs.DataSourceName = GlobalVar.initCfgDef.GetServerType().DatasoureName.replace("[JndiName]", sjs.jndi_name);
				dscs.ConnectionUrl = sjs.connection_url;
				dscs.DriverClass = sjs.driver_class;
				dscs.UserName = sjs.user_name;
				dscs.Password = sjs.password;
				
				GlobalVar.Datasourcecommon.add(dscs);
			}
		}
	}
	
	/**
	 * 更新PosServer的配置文件路径
	 * @param path
	 */
	protected void UpdatePosServerCfgPath(String path)
	{
		if (this.GetConfig() == null) return;
		
		//String filename = ConfigWizard.GetFileName(path);
		
		String filename = GlobalVar.initCfgDef.GetPosServerName();
		
		if (filename.length() <= 0)
		{
			filename = ConfigWizard.GetFileName(path);
		}
		
		ServerTypeStruct servertype = GlobalVar.initCfgDef.GetServerType();
		
		if (servertype != null)
		{
			GlobalVar.ServerPosServerCfgPath = GlobalVar.initCfgDef.GetServerPath() + servertype.ServerPosServerCfgPath.replace("[SerName]", filename);
			GlobalVar.ServerPosServerCmdPath = GlobalVar.initCfgDef.GetServerPath() + servertype.ServerPosServerCmdPath.replace("[SerName]", filename);
		}
	}
}
