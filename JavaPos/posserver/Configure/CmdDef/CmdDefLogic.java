package posserver.Configure.CmdDef;

import java.util.Iterator;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import posserver.Configure.Common.Convert;
import posserver.Configure.Common.GlobalVar;
import posserver.Configure.Common.KeyValueStruct;
import posserver.Configure.JBoss.ServerJBoss422GALogic;

public class CmdDefLogic extends ServerJBoss422GALogic
{	
	//0-通讯命令 1-更新命令 2-日终命令
	public int cmdfiletype = 0;
	
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
	
	public void GetDictionaryFromVector(Vector v,Vector v1)
	{
		
	}
	
	public void UpdateDataSourceName()
	{

	}
	
	public void RefreshPath()
	{
		cfgpath = GlobalVar.ServerPosServerCmdPath;
	}
	
	// 获得xml文档根元素的名称
	public String GetRootElementName()
	{
		return "General";
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
				String strelementname1 = element1.getName();
				if (strelementname1.equalsIgnoreCase("configure"))
				{
					Iterator iter1 = element1.elementIterator();
					CmdDefStruct sts = new CmdDefStruct();
					config.add(sts);
					
					sts.CmdCode = element1.attributeValue("CmdCode");
					
					while(iter1.hasNext())
					{
						Element element2 = (Element)iter1.next();
						String strelementname2 = element2.getName();
						String text = element2.getText();
						
						if (strelementname2.equalsIgnoreCase("CmdType"))
						{
							sts.CmdType = text;
						}
						else if (strelementname2.equalsIgnoreCase("CmdMemo"))
						{
							sts.CmdMemo = text;
						}
						else if (strelementname2.equalsIgnoreCase("CmdTran"))
						{
							sts.CmdTran = text;
						}
						else if (strelementname2.equalsIgnoreCase("StartTrans"))
						{
							sts.StartTrans = text;
						}
						else if (strelementname2.equalsIgnoreCase("CmdFileName"))
						{
							sts.CmdFileName = text;
						}
						else if (strelementname2.equalsIgnoreCase("LocalTableName"))
						{
							sts.LocalTableName = text;
						}
						else
						{
							int count = Convert.toInt(sts.CmdTran);
							if (count == 0) count = 1;
								
							for (int i = 1;i <= count; i++)
							{
								String strnum = Convert.increaseCharForward(String.valueOf(i),'0',2);
								
								if (strelementname2.equalsIgnoreCase("Cmd_" + strnum + "_Mode"))
								{
									CmdTextStruct cmdtext = new CmdTextStruct();
									cmdtext.Cmd_XX_Mode = text;
									sts.CmdText.add(cmdtext);
									
									break;
								}
								else if (strelementname2.equalsIgnoreCase("Sql_" + strnum + "_Type"))
								{
									if (sts.CmdText.size() >= i)
									{
										CmdTextStruct cmdtext = (CmdTextStruct)(sts.CmdText.get(i-1));
										cmdtext.Sql_XX_Type = text;
									}
									
									break;
								}
								else if (strelementname2.equalsIgnoreCase("Tran_" + strnum + "_Sql"))
								{
									if (sts.CmdText.size() >= i)
									{
										CmdTextStruct cmdtext = ((CmdTextStruct)sts.CmdText.get(i-1));
										cmdtext.Tran_XX_Sql = text;
									}
									
									break;
								}
								else if (strelementname2.equalsIgnoreCase("Tran_" + strnum + "_ParaName"))
								{
									if (!text.equals("*") && text.trim().length() > 0)
									{
										if (sts.CmdText.size() >= i)
										{
											CmdTextStruct cmdtext = ((CmdTextStruct)sts.CmdText.get(i-1));
											
											if (!text.equals("*") && text.trim().length() > 0)
											{
												String[] str = text.split(",");
												for(int j = 0;j < str.length ; j++)
												{
													CmdParaStruct cmdpara = new CmdParaStruct();
													cmdtext.Tran_XX_Para.add(cmdpara);
													cmdpara.Name = str[j];
												}
											}
										}
									}
									break;
								}
								else if (strelementname2.equalsIgnoreCase("Tran_" + strnum + "_ParaType"))
								{
									if (!text.equals("*") && text.trim().length() > 0)
									{
										if (sts.CmdText.size() >= i)
										{
											CmdTextStruct cmdtext = ((CmdTextStruct)sts.CmdText.get(i-1));
											String[] str = text.split(",");
											for(int j = 0;j < str.length ; j++)
											{
												if (cmdtext.Tran_XX_Para.size() < (j+1)) break;
												
												CmdParaStruct cmdpara = (CmdParaStruct)(cmdtext.Tran_XX_Para.get(j));
												
												cmdpara.Type = str[j];
												cmdpara.TypeDesc = str[j];
												
												for(int k = 0;k < GlobalVar.Paradatatype.size();k++)
												{
													KeyValueStruct kvs = (KeyValueStruct)(GlobalVar.Paradatatype.get(k));
													
													if (kvs.key.equalsIgnoreCase(cmdpara.Type))
													{
														cmdpara.TypeDesc = kvs.value;
													}
												}
											}
										}
									}
									break;
								}
								else if (strelementname2.equalsIgnoreCase("Tran_" + strnum + "_ColName"))
								{
									if (!text.equals("*") && text.trim().length() > 0)
									{
										if (sts.CmdText.size() >= i)
										{
											CmdTextStruct cmdtext = (CmdTextStruct)(sts.CmdText.get(i-1));
											String[] str = text.split(",");
											for(int j = 0;j < str.length ; j++)
											{
												CmdParaStruct cmdpara = new CmdParaStruct();
												cmdtext.Tran_XX_Col.add(cmdpara);
												cmdpara.Name = str[j];
											}
										}
									}
									break;
								}
								else if (strelementname2.equalsIgnoreCase("Tran_" + strnum + "_ColType"))
								{
									if (!text.equals("*") && text.trim().length() > 0)
									{
										if (sts.CmdText.size() >= i)
										{
											CmdTextStruct cmdtext = (CmdTextStruct)(sts.CmdText.get(i-1));
											String[] str = text.split(",");
											for(int j = 0;j < str.length ; j++)
											{
												if (cmdtext.Tran_XX_Col.size() < (j+1)) break;
												
												CmdParaStruct cmdpara = (CmdParaStruct)(cmdtext.Tran_XX_Col.get(j));
												
												cmdpara.Type = str[j];
												cmdpara.TypeDesc = str[j];
												
												for(int k = 0;k < GlobalVar.Paradatatype.size();k++)
												{
													KeyValueStruct kvs = (KeyValueStruct)(GlobalVar.Paradatatype.get(k));
													
													if (kvs.key.equalsIgnoreCase(cmdpara.Type))
													{
														cmdpara.TypeDesc = kvs.value;
													}
												}
											}
										}
									}
									break;
								}
							}
						}
					}
				}
			}
		}
	}
	/*
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
			if (attribute.getName().equals("path"))
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
	*/
	public Element ToXmlElementDom4j(Object obj)
	{
		  try
			{
			    CmdDefStruct sts = (CmdDefStruct)obj;
			    Element Resource = DocumentHelper.createElement("configure");
			    Resource.addAttribute("CmdCode",sts.CmdCode);
			    Resource.addElement("CmdMemo").setText(sts.CmdMemo);
			    Resource.addElement("CmdType").setText(sts.CmdType);
			    //Resource.addElement("CmdTran").setText(sts.CmdTran);

			    if (cmdfiletype == 0)
			    {
				    Resource.addElement("CmdTran").setText(String.valueOf(sts.CmdText.size()));
				    Resource.addElement("StartTrans").setText(sts.StartTrans);
			    }
			    else if (cmdfiletype == 1) 
			    {
			    	if (sts.CmdText.size() > 0)
			    	{
			    		Resource.addElement("CmdTran").setText(String.valueOf(sts.CmdText.size()));
			    	}
			    	
			    	Resource.addElement("CmdFileName").setText(sts.CmdFileName);
			    }
			    else if (cmdfiletype == 2)
			    {
			    	if (sts.CmdText.size() > 0)
			    	{
			    		Resource.addElement("CmdTran").setText(String.valueOf(sts.CmdText.size()));
			    	}
			    	
			    	Resource.addElement("LocalTableName").setText(sts.LocalTableName);
			    }
			    
			    for(int i = 1;i <= sts.CmdText.size() ;i++)
			    {
			    	String strnum = Convert.increaseCharForward(String.valueOf(i), '0', 2);
			    	CmdTextStruct cmdtext = (CmdTextStruct)(sts.CmdText.get(i-1));
				    Resource.addElement("Cmd_" + strnum + "_Mode").setText(cmdtext.Cmd_XX_Mode);
				    Resource.addElement("Sql_" + strnum + "_Type").setText(cmdtext.Sql_XX_Type);
				    Resource.addElement("Tran_" + strnum + "_Sql").setText(cmdtext.Tran_XX_Sql);
				    
				    String strtran_xx_paraname = "";
				    String Strtran_xx_paratype = "";
				    for (int j = 0; j < cmdtext.Tran_XX_Para.size() ;j ++)
				    {
				    	CmdParaStruct tran_xx_para = (CmdParaStruct)(cmdtext.Tran_XX_Para.get(j));
				    	strtran_xx_paraname = strtran_xx_paraname + tran_xx_para.Name + ",";
				    	Strtran_xx_paratype = Strtran_xx_paratype + tran_xx_para.Type + ",";
				    }
				    
				    if (strtran_xx_paraname.length() > 0 && strtran_xx_paraname.substring(strtran_xx_paraname.length() - 1,strtran_xx_paraname.length()).equals(","))
				    {
				    	strtran_xx_paraname = strtran_xx_paraname.substring(0,strtran_xx_paraname.length() - 1);
				    }
				    
				    if (Strtran_xx_paratype.length() > 0 && Strtran_xx_paratype.substring(Strtran_xx_paratype.length() - 1,Strtran_xx_paratype.length()).equals(","))
				    {
				    	Strtran_xx_paratype = Strtran_xx_paratype.substring(0,Strtran_xx_paratype.length() - 1);
				    }
				    
				    if (strtran_xx_paraname.trim().length() <= 0)
				    {
				    	strtran_xx_paraname = "*";
				    }
				    
				    if (Strtran_xx_paratype.trim().length() <= 0)
				    {
				    	Strtran_xx_paratype = "*";
				    }
				    
				    Resource.addElement("Tran_" + strnum + "_ParaName").setText(strtran_xx_paraname);
				    Resource.addElement("Tran_" + strnum + "_ParaType").setText(Strtran_xx_paratype);
				    
				    String strtran_xx_colname = "";
				    String Strtran_xx_coltype = "";
				    for (int j = 0; j < cmdtext.Tran_XX_Col.size() ;j ++)
				    {
				    	CmdParaStruct tran_xx_para = (CmdParaStruct)(cmdtext.Tran_XX_Col.get(j));
				    	strtran_xx_colname = strtran_xx_colname + tran_xx_para.Name + ",";
				    	Strtran_xx_coltype = Strtran_xx_coltype + tran_xx_para.Type + ",";
				    }
				    
				    if (strtran_xx_colname.length() > 0 && strtran_xx_colname.substring(strtran_xx_colname.length() - 1,strtran_xx_colname.length()).equals(","))
				    {
				    	strtran_xx_colname = strtran_xx_colname.substring(0,strtran_xx_colname.length() - 1);
				    }
				    
				    if (Strtran_xx_coltype.length() > 0 && Strtran_xx_coltype.substring(Strtran_xx_coltype.length() - 1,Strtran_xx_coltype.length()).equals(","))
				    {
				    	Strtran_xx_coltype = Strtran_xx_coltype.substring(0,Strtran_xx_coltype.length() - 1);
				    }
				    
				    if (strtran_xx_colname.trim().length() <= 0)
				    {
				    	strtran_xx_colname = "*";
				    }
				    
				    if (Strtran_xx_coltype.trim().length() <= 0)
				    {
				    	Strtran_xx_coltype = "*";
				    }
				    
				    Resource.addElement("Tran_" + strnum + "_ColName").setText(strtran_xx_colname);
				    Resource.addElement("Tran_" + strnum + "_ColType").setText(Strtran_xx_coltype);
			    }

					    
				return Resource;
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return null;
			}
	}

	
	public boolean CheckKey(String key,int index)
	{
		for (int i = 0;i < this.Configs.size(); i++)
		{
			if (i == index) continue;
			CmdDefStruct sts = (CmdDefStruct)Configs.get(i);
			if (sts.CmdCode.equalsIgnoreCase(key))
			{
				strmsg = "代码为" + key + "的命令已经存在!";
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * 判断是否可以保存
	 * @param 主健
	 * @param index>=0为修改的索引,index <0新增
	 * @return
	 */
	public boolean CheckSave(String key,int index)
	{
		if (!CheckKey(key,index)) return false;
		
		// 如果是新增则不判断
		if (index >= 0)
		{
			CmdDefStruct cds = (CmdDefStruct)(GetConfig().get(index));
			for (int i = 0;i < cds.CmdText.size();i++)
			{
				CmdTextStruct cts = (CmdTextStruct)(cds.CmdText.get(i));
				int sqlparacount = 0;
				int paracount = cts.Tran_XX_Para.size();
				int colcount = cts.Tran_XX_Col.size();
				
				StringBuffer sb = new StringBuffer(cts.Tran_XX_Sql);
				for(int j = 0;j < sb.length();j++)
				{
					if (sb.charAt(j) == '?')
					{
						sqlparacount ++;
					}
				}
				
				if (sqlparacount != (paracount + colcount))
				{
					String strnum = Convert.increaseCharForward(String.valueOf(i+1), '0', 2);
					 MessageBox msgbox=new MessageBox(new Shell(),SWT.YES|SWT.NO);
				     msgbox.setMessage("[" + strnum + "]号命令的Sql语句\n\n"+cts.Tran_XX_Sql+"\n\n的参数与配置的输入输出参数和不一致,是否保存?");
				     if(msgbox.open()!=SWT.YES)
				     {
				    	 this.strmsg = "保存失败!参数数量不一致!";
					     return false;
				     }
				}
			}
		}
		
		 MessageBox msgbox=new MessageBox(new Shell(),SWT.YES|SWT.NO|SWT.ICON_QUESTION);
	     msgbox.setMessage("该文件是JavaPOS的命令配置文件,请不要随意进行修改保存\n\n应该在JavaPOS技术人员的指导下进行修改\n\n你确定修改无误要进行保存吗？");
	     if(msgbox.open() != SWT.YES)
	     {
	    	 this.strmsg = "";
	    	 return false;
	     }
	     
		return true;
	}
	
	public boolean CheckSql(CmdTextStruct cts)
	{
		String strsql = cts.Tran_XX_Sql;
		
		if (strsql.trim().length() <= 0) 
		{
			strmsg = "请输入Sql语句!";
			return false;
		}
		
		if (strsql.length() < 10) 
		{
			strmsg = "请输入正确的Sql语句!";
			return false;
		}
			
		if (!strsql.substring(0,6).equals("{call "))
		{
			strmsg = "请输入正确的Sql语句!";
			return false;
		}
		
		int pos1 = strsql.indexOf("(");
		int pos2 = strsql.indexOf(")");
		
		if (pos1 < 0 || pos2 <0 || pos2 < pos1)
		{
			strmsg = "请输入正确的Sql语句!";
			return false;
		}
		/*
		String procedurename = strsql.substring(6,pos1).trim();
		if ()
		sql = 
		
		{call j()} 
		sql = >{call java_findsyj(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)};
		{call java_findsyj(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}
		*/
		return true;
	}
	
	/*
	 * *
	 * 		if ()
		int sqlparacount = 0;
		int paracount = 0;
		int colcount = 0;
		
		if (lbTran_XX_SqlNum.getText().trim().length() > 0)
		{
			sqlparacount = Convert.toInt(lbTran_XX_SqlNum.getText().trim());
		}
		
		if (lbParaNum.getText().trim().length() > 0)
		{
			paracount = Convert.toInt(lbParaNum.getText().trim());
		}
		
		if (lbColNum.getText().trim().length() > 0)
		{
			colcount = Convert.toInt(lbParaNum.getText().trim());
		}
		
		if (sqlparacount != (paracount + colcount))
		{
			 MessageBox msgbox=new MessageBox(new Shell(),SWT.YES|SWT.NO);
		     msgbox.setMessage("Sql语句中的参数与输入参数和输出参数的和不相等，是否继续保存?");
		     msgbox.open();
		     if(msgbox.open()!=SWT.YES)
		     {
		    	 return;
		     }
		}
	 * /
	 */
}
