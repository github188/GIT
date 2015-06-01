package posserver.Configure.Tomcat;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import posserver.Configure.CmdDef.DataSourceCommonStruct;
import posserver.Configure.Common.GlobalVar;
import posserver.Configure.JBoss.ServerJBoss422GALogic;

public class ServerTomCat5523Logic extends ServerJBoss422GALogic
{	
	public String Getcfgmodelpath()
	{
		return GlobalVar.Path + "\\Tomcat5523CfgModel.ini";
	}
	
	// 获得xml文档根元素的名称
	public String GetRootElementName()
	{
		return "Context";
	}
	
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
				
				if (strelementname.equalsIgnoreCase("Resource"))
				{
					Iterator iter1 = element1.attributeIterator();
					ServerTomCat5523Struct sts = new ServerTomCat5523Struct();
					config.add(sts);
					while(iter1.hasNext())
					{
						Attribute attribute = (Attribute)iter1.next();
						String strelementname2 = attribute.getName();
						String text = attribute.getText();
						
						if (strelementname2.equalsIgnoreCase("name"))
						{
							sts.name = text;
						}
						else if (strelementname2.equalsIgnoreCase("type"))
						{
							sts.type = text;
						}
						else if (strelementname2.equalsIgnoreCase("driverClassName"))
						{
							sts.driverClassName = text;
						}
						else if (strelementname2.equalsIgnoreCase("url"))
						{
							sts.url = text;
						}
						else if (strelementname2.equalsIgnoreCase("username"))
						{
							sts.username = text;
						}
						else if (strelementname2.equalsIgnoreCase("password"))
						{
							sts.password = text;
						}
						else if (strelementname2.equalsIgnoreCase("maxIdle"))
						{
							sts.maxIdle = text;
						}
						else if (strelementname2.equalsIgnoreCase("maxActive"))
						{
							sts.maxActive = text;
						}
						else if (strelementname2.equalsIgnoreCase("maxWait"))
						{
							sts.maxWait = text;
						}
					}
				}
			}
		}
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
		
		
		Iterator iter = rootelement.attributeIterator();
		
		boolean ishave = false;
		while(iter.hasNext())
		{
			Attribute attribute = (Attribute)iter.next();
			if (attribute.getName().equalsIgnoreCase("path"))
			{
				ishave = true;
				break;
			}
		}
		
		if (!ishave)
		{	
			//String filename = ConfigWizard.GetFileName(GetCfgPath());
			String filename = GlobalVar.initCfgDef.GetPosServerName();
			filename = "/" + filename;
			rootelement.addAttribute("path",filename);
			rootelement.addAttribute("docBase",filename);
			rootelement.addAttribute("reloadable","true");
		}
		
		if (!xmlOper.WriteXmlDocument())
		{
			strmsg = xmlOper.GetMsg();
			return false;
		}
			
		UpdateDataSourceName();
		
		return true;
	}
	
	public Element ToXmlElementDom4j(Object obj)
	{
		  try
			{
			  ServerTomCat5523Struct sts = (ServerTomCat5523Struct)obj;
			    Element Resource = DocumentHelper.createElement("Resource");
			    Resource.addAttribute("name",sts.name);
			    Resource.addAttribute("type",sts.type);
			    Resource.addAttribute("driverClassName",sts.driverClassName);
			    Resource.addAttribute("url",sts.url);
			    Resource.addAttribute("username",sts.username);
			    Resource.addAttribute("password",sts.password);
			    Resource.addAttribute("maxIdle",sts.maxIdle);
			    Resource.addAttribute("maxActive",sts.maxActive);
			    Resource.addAttribute("maxWait",sts.maxWait);
					    
				return Resource;
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return null;
			}
	}
	
	public void GetDictionaryFromVector(Vector v,Vector v1)
	{
		Vector vr = v1;
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
			ServerTomCat5523Struct sts = (ServerTomCat5523Struct)Configs.get(i);
			if (sts.name.equalsIgnoreCase(key))
			{
				strmsg = "名称为" + key + "的jndi名称已经存在!";
				return false;
			}
		}
		
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
				ServerTomCat5523Struct sts = (ServerTomCat5523Struct)configs.get(i);
				
				DataSourceCommonStruct dscs = new DataSourceCommonStruct();
				dscs.DataSourceName = GlobalVar.initCfgDef.GetServerType().DatasoureName.replace("[JndiName]", sts.name);
				dscs.ConnectionUrl = sts.url;
				dscs.DriverClass = sts.driverClassName;
				dscs.UserName = sts.username;
				dscs.Password = sts.password;
				
				GlobalVar.Datasourcecommon.add(dscs);
			}
		}
	}
}
