package com.efuture.javaPos.Logic;

import java.io.BufferedReader;
import java.lang.reflect.Field;
import java.util.Vector;

import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.PrintTemplate.SaleAppendBillMode;
import com.efuture.javaPos.Struct.CommonResultDef;
import com.efuture.javaPos.Struct.KeyValueDef;
import com.efuture.javaPos.Struct.SaleAppendDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SaleMemoInfo;

public class SaleMemoBS 
{
	public SaleMemoBS(Shell shell ,SaleHeadDef saleHead, Vector salegoods, Vector salepay, Vector vcdefault, Table table, int flag)
	{
		this.saleHead = saleHead;
		this.salegoods = salegoods;
		this.salepay = salepay;
		this.vcdefault = vcdefault;
		this.table = table;
		this.shell = shell;
	}
	public SaleHeadDef saleHead = null;
	public Vector salegoods = null;
	public Vector salepay = null;
	public Vector vcdefault = null;
	public Table table = null;
	public Shell shell = null;
	
	public String isDataSaved = "N";
	
	public Vector vcCfg = new Vector();
	
	// 获得某对象的某个字段的值 
	public Object getObjectValue(Object obj,String fieldname)
	{
		try
		{
			Class cl = obj.getClass();
			Field fd = cl.getField(fieldname);
			return fd.get(obj);
		}
		catch(Exception ex)
		{
			return null;
		}
	}
	
	//	 获得ComBox里面的内容
	public void RefreshComboData(SaleMemoInfo memoInfo)
	{
		RefreshComboData(memoInfo, "");
	}
	
	//	 获得ComBox里面的内容
	public void RefreshComboData(SaleMemoInfo memoInfo,String strwhere)
	{
		if (!memoInfo.type.equals(SaleMemoInfo.COMBOTYPE)) return;
		
		try
		{
			memoInfo.curselindex = -1;
			memoInfo.content.vccontent.clear();
			
			if (memoInfo.content.type.equals(SaleMemoInfo.CMDGET))
			{
				memoInfo.vcresult = null;
			}
			
			// 获得查找参数
			String[] strspara = {memoInfo.value,"","","","","","","","",""};
			
			int index = 1;
			
			for (int i = 0; i<memoInfo.associate.size();i++)
			{
				String strvalue = (String)memoInfo.associate.get(i);
				
				for(int j = 0;j < vcCfg.size();j++)
				{
					SaleMemoInfo smi = (SaleMemoInfo)vcCfg.get(j);
					
					if (smi == memoInfo) continue;
					
					String[] strsvalue = strvalue.split("\\."); 
					
					if (smi.value.equals(strsvalue[0]))
					{
						if (smi.type.equals(SaleMemoInfo.TEXTTYPE))
						{
							strspara[index++] = smi.curcontent;
						}
						else if(smi.type.equals(SaleMemoInfo.COMBOTYPE))
						{
							if (smi.curselindex < 0) return;
							
							if (strsvalue.length > 1)
							{
								CommonResultDef crd = (CommonResultDef)smi.vcresult.get(smi.curselindex);
								
								Object obj = getObjectValue(crd, strsvalue[1]);
								
								if (obj == null) return;
								
								strspara[index++] = obj.toString();
							}
							else
							{
								strspara[index++] = ((KeyValueDef)smi.content.vccontent.get(smi.curselindex)).key.toString();
							}
						}
					}
				}
			}
      
			if (memoInfo.content.type.equals(SaleMemoInfo.CFGGET))
			{			
				if (memoInfo.vcresult == null || memoInfo.vcresult.size() <= 0)
				{
					return;
				}
				
				String key1 = "";	
				for (int i = 1;i < index; i++)
				{
					key1 = key1 + strspara[i] + ",";
				}
				
				if (key1.length() > 0) key1 = key1.substring(0,key1.length()-1);
				
				for (int h = 0;h < memoInfo.vcresult.size();h++)
				{
					KeyValueDef kvd = (KeyValueDef)memoInfo.vcresult.get(h);
					
					if (kvd.key.toString().equals(key1))
					{
						memoInfo.content.vccontent = (Vector)((Vector)kvd.value).clone();
					}
				}
			}
			
			if (memoInfo.content.type.equals(SaleMemoInfo.CMDGET))
			{
				strspara[index++] = strwhere;
				
				memoInfo.vcresult = null;
				
				Vector vc = new Vector();
				
				if (!DataService.getDefault().doCommonMethod("MEMODZINFO", strspara[0], strspara[1], strspara[2], strspara[3], strspara[4], strspara[5], strspara[6], strspara[7], strspara[8], strspara[9] ,vc))
				{
					return;
				}
					
				memoInfo.vcresult = vc;
				
				vcresultTovccontent(memoInfo);
			}
		}
		catch(Exception ex)
		{
			MessageBox msg = new MessageBox(shell);
			msg.setMessage(ex.getMessage());
			msg.open();
		}
	}
	
	// 由memoInfo.vcresult转向memoInfo.content.vccontent
	public void vcresultTovccontent(SaleMemoInfo memoInfo)
	{
		memoInfo.content.vccontent.clear();
		
		if (memoInfo.vcresult == null) return;
		
		for (int i = 0;i < memoInfo.vcresult.size();i++)
		{
			try
			{
				CommonResultDef crd = (CommonResultDef)memoInfo.vcresult.get(i);
				
				String codefiled = (memoInfo.content.code == null || memoInfo.content.code.trim().length() <=0)?"memo1":memoInfo.content.code; 
				String namefiled = (memoInfo.content.name == null || memoInfo.content.name.trim().length() <=0)?"memo2":memoInfo.content.name;
				
				Field fidcode = CommonResultDef.class.getField(codefiled);
				Field fidname = CommonResultDef.class.getField(namefiled);
				
				KeyValueDef kvd = new KeyValueDef();
				kvd.key = fidcode.get(crd);
				kvd.value = fidname.get(crd);
				
				memoInfo.content.vccontent.add(kvd);
			}
			catch(Exception ex)
			{
				
			}
		}
	}
	
	// ComBox修改数据之后,修改其它与之关联的填写项
	public void RefreshComboAssoData(SaleMemoInfo memoInfo)
	{
		if (!memoInfo.type.equals(SaleMemoInfo.COMBOTYPE)) return;
		
		Vector vc = new Vector();
		
		for(int i = 0;i < vcCfg.size();i++)
		{
			SaleMemoInfo smi = (SaleMemoInfo)vcCfg.get(i);
			
			if (smi == memoInfo) continue;
			
			for (int j = 0; j<smi.associate.size();j++)
			{
				String strvalue = (String)smi.associate.get(j);
				String[] strsvalue = strvalue.split("\\.");
				
				if (memoInfo.value.equals(strsvalue[0]))
				{
					if (smi.type.equals(SaleMemoInfo.TEXTTYPE))
					{
						if (memoInfo.curselindex < 0) smi.curcontent = "";
						else
						{
							// str1.str2这种形式配置出来,表示str1控件的查询结果集中的str2字段,否则直接使用name对应的字段
							// 只有通过SaleMemoInfo.CMDGET获得的结果集才存放在memoInfo.vcresult当中
							if (strsvalue.length > 1 && memoInfo.content.type.equals(SaleMemoInfo.CMDGET))
							{
								if (memoInfo.vcresult == null || memoInfo.vcresult.size() <= 0)
								{
									smi.curcontent = "";
								}
								else
								{
									CommonResultDef crd = (CommonResultDef)memoInfo.vcresult.get(memoInfo.curselindex);
									Object resultobj = getObjectValue(crd, strsvalue[1]);
									if (resultobj != null) smi.curcontent = resultobj.toString();
								}
							}
							else
							{
								smi.curcontent = ((KeyValueDef)memoInfo.content.vccontent.get(memoInfo.curselindex)).value.toString();
							}
						}
					}
					else if(smi.type.equals(SaleMemoInfo.COMBOTYPE))
					{
						// 对要刷新的内容进行排序,以关联字段少的优先(因为有可能,其它关联字段未变,这个关联先变,则改变就不准了)
						
						int k = 0;
						for (;k < vc.size();k++)
						{
							SaleMemoInfo smi1 = (SaleMemoInfo)vc.get(k);
							
							if (smi.associate.size() <= smi1.associate.size())
							{
								break;
							}
						}
						
						vc.insertElementAt(smi, k);
					}
				}
			}
		}
		
		for (int i = 0;i < vc.size();i++)
		{
			// 刷新combo里面的内容
			RefreshComboData((SaleMemoInfo)vc.get(i));
		}
	}
	
	public Vector GetAssociate(SaleMemoInfo memoInfo)
	{
		Vector vc = new Vector();
		
		for(int i = 0;i < vcCfg.size();i++)
		{
			SaleMemoInfo smi = (SaleMemoInfo)vcCfg.get(i);
			
			if (smi == memoInfo) continue;
			
			for (int j = 0; j<smi.associate.size();j++)
			{
				String strvalue = (String)smi.associate.get(j);
				String[] strsvalue = strvalue.split("\\.");
				
				if (memoInfo.value.equals(strsvalue[0]))
				{
					vc.add(smi);
				}
			}
		}
		
		return vc;
	}

	// 初始ComboData
	public void InitComboData()
	{
		for (int i = 0;i < vcCfg.size();i++)
		{
			SaleMemoInfo memoInfo = (SaleMemoInfo)vcCfg.get(i);
			
			if (!memoInfo.type.equals(SaleMemoInfo.COMBOTYPE)) continue;
			if (memoInfo.content.type.equals(SaleMemoInfo.CMDGET))
			{
				// 有关联字段,则不进行初始化数据
				if (memoInfo.associate.size() > 0) continue;
		         
				// memoInfo.vcresult != null && memoInfo.vcresult.size() > 0 有默认值,则不进行初始化数据
				if (memoInfo.vcresult != null && memoInfo.vcresult.size() > 0) continue;
				
		        RefreshComboData(memoInfo);	
			}		
		}
	}
	
	// 读取配置信息
	public boolean readFormIni(Vector vector)
	{
		boolean issuc = false;
		issuc = readFormIni1(vector);
		
		if (!issuc)
		{
			vector.clear();
			
			issuc = readFormIni2(vector);
		}
		
		if (issuc)
		{
			initDefultValue();
		}
		
		return true;
	}
	
	// 初始化默认值
	public void initDefaultValue(SaleMemoInfo memoInfo)
	{
		if (vcdefault != null)
		{
			// vcdefault key="str1" value="01-abc" 
			for (int j = 0;j < vcdefault.size();j++)
			{
				KeyValueDef kvd = (KeyValueDef)vcdefault.get(j);
				
				if (!kvd.key.toString().equals(memoInfo.value)) continue;
				
				if (memoInfo.type.equals(SaleMemoInfo.TEXTTYPE))
				{
					memoInfo.curcontent = kvd.value.toString();
				}
				else if (memoInfo.type.equals(SaleMemoInfo.COMBOTYPE))
				{
					String[] values = kvd.value.toString().split("-");
					if (memoInfo.content.type.equals(SaleMemoInfo.CFGGET))
					{
						for (int l = 0; l<memoInfo.associate.size();l++)
						{
							String strvalue = (String)memoInfo.associate.get(l);
							
							for(int k = 0;k < vcCfg.size();k++)
							{
								SaleMemoInfo smi = (SaleMemoInfo)vcCfg.get(k);
								
								if (smi == memoInfo) continue;
								
								String[] strsvalue = strvalue.split("\\."); 
								
								if (smi.value.equals(strsvalue[0]))
								{
									initDefaultValue(smi);
								}
							}
						}
						
						RefreshComboData(memoInfo);
						
						for (int k = 0;k < memoInfo.content.vccontent.size();k++)
						{
							KeyValueDef kvd1 = (KeyValueDef)memoInfo.content.vccontent.get(k);
							
							if (kvd1.key.equals(values[0]))
							{
								memoInfo.curselindex = k;
							}
						}
					}
					else if (memoInfo.content.type.equals(SaleMemoInfo.CMDGET))
					{
						try
						{
							// 生成一个结果集对象以供使用
							CommonResultDef crd = new CommonResultDef();
							Vector vcresult = new Vector();
							vcresult.add(crd);
							Field codefield = CommonResultDef.class.getField(memoInfo.content.code);
							Field namefield = CommonResultDef.class.getField(memoInfo.content.name);
							
							String code = values[0];
							String value = "";
							
							for (int n = 1;n < values.length;n++)
							{
								value += values[n] + "-";
							}
							
							if (value.length() > 0)
							{
								value = value.substring(0,value.length()-1);
							}
							
							codefield.set(crd, code);
							namefield.set(crd, value);
							
							memoInfo.vcresult = vcresult;
							
							vcresultTovccontent(memoInfo);
							
							memoInfo.curselindex = 0;
						}
						catch(Exception ex)
						{
							
						}
					}
				}
			}
		}
	}
	
	// 初始化默认值
	public void initDefultValue()
	{
		if (vcdefault == null || vcdefault.size() <= 0) return;
		
		SaleMemoInfo memoInfo;
		for (int i = 0; i < vcCfg.size(); i++)
		{			
			memoInfo = (SaleMemoInfo)vcCfg.elementAt(i);
			
			initDefaultValue(memoInfo);
		}
	}
	
	// 从SaleMemoInfo配置文件中读取
	public boolean readFormIni2(Vector vector)
	{
		vcCfg.clear();
		
		Vector vc = CommonMethod.readFileByVector(GlobalVar.ConfigPath + "\\SaleMemoInfo.ini","GBK");
		
		if (vc == null) return false;
		
		SaleMemoInfo smi = null;
		
		for (int i = 0;i < vc.size(); i++)
		{
			if (vc.get(i) == null) continue;
				
			String[] content = (String[])vc.get(i);
			
			if (content[0] == null) continue;
			
			if (content[0].startsWith("[") && content[0].endsWith("]"))
			{
				smi = new SaleMemoInfo();
				smi.value = content[0].substring(1,content[0].length() - 1);
				vcCfg.add(smi);
			}
			
			if (smi == null) continue;
				
			if (content[0].equalsIgnoreCase("desc"))
			{
				smi.desc = content[1];
			}
			else if (content[0].equalsIgnoreCase("type"))
			{
				smi.type = content[1];
			}
			else if (content[0].equalsIgnoreCase("allowNull"))
			{
				smi.allowNull = content[1].length() >0?content[1].charAt(0):'N';
			}
			else if (content[0].equalsIgnoreCase("maxLength"))
			{
				smi.maxLength = Convert.toInt(content[1]);
			}
			else if (content[0].equalsIgnoreCase("contenttype"))
			{
				smi.content.type = content[1];
			}
			else if (content[0].equalsIgnoreCase("contentcode"))
			{
				smi.content.code = content[1];
			}
			else if (content[0].equalsIgnoreCase("contentname"))
			{
				smi.content.name = content[1];
			}
			else if (content[0].equalsIgnoreCase("contentvalue"))
			{
				if (content[1] != null)
				{
					smi.vcresult = new Vector();
					
					String[] strs = content[1].split("&");
					
					for (int j = 0;j < strs.length;j++) 
					{
						String str = strs[j];
						
						if (str.trim().length() <= 0) continue;
						
						KeyValueDef kvd = new KeyValueDef();
						smi.vcresult.add(kvd);
						
						String[] strs1 = str.split("@");
						if (strs1.length > 0)
						{
							Vector vc1 = new Vector();
							kvd.value = vc1;
							
							String[] strs2 = strs1[0].split("\\|");
							for (int k = 0;k < strs2.length;k++)
							{
								String str1 = strs2[k].trim();
								
								if (str1.length() <= 0) continue;
								
								String[] strs3 = str1.split("-");
								
								if (strs3.length != 2) continue;

								KeyValueDef kvd1 = new KeyValueDef();
								kvd1.key = strs3[0];
								kvd1.value = strs3[1];
								
								vc1.add(kvd1);
							}
						}
							
						if (strs1.length > 1)
						{
							kvd.key = strs1[1];
						}	
					}
					
					if (smi.vcresult != null && smi.vcresult.size() == 1)
					{
						KeyValueDef kvd = (KeyValueDef)smi.vcresult.get(0);
						
						if (kvd.key == null || kvd.key.toString().trim().length() <= 0)
						{
							if (kvd.value != null)
							{
								smi.content.vccontent = (Vector)((Vector)kvd.value).clone();
								smi.vcresult = null;
							}
						}
					}
				}
			}
			else if (content[0].equalsIgnoreCase("associate"))
			{
				if (content[1] != null)
				{
					String[] strs = content[1].split("\\|");
					
					for (int j = 0;j < strs.length;j++)
					{
						String str = strs[j].trim();
						
						if (str.length() <= 0) continue;
						
						smi.associate.add(str);
					}
				}
			}
		}
		
		return true;
	}
	
	// 从SaleMemoInfo配置文件中读取
	public boolean readFormIni1(Vector vector)
	{
		if (!PathFile.fileExist(GlobalVar.ConfigPath + "\\SaleMemoInfo.ini"))
		{
			MessageBox msg = new MessageBox(shell);
			msg.setMessage(Language.apply("没有找到 SaleMemoInfo.ini 文件"));
			msg.open();
			return false;
		}

		try
		{
			BufferedReader br = CommonMethod.readFileGBK(GlobalVar.ConfigPath + "\\SaleMemoInfo.ini");
			if (br == null)
			{
				MessageBox msg = new MessageBox(shell);
				msg.setMessage(Language.apply("打开 SaleMemoInfo.ini 文件失败!"));
				msg.open();
				return false;
			}

			String line = null;
			String[] a;
			String[] b;
			SaleMemoInfo memoInfo;
			while ((line = br.readLine()) != null)
			{
				// 第二种配置文件的模式
				if (line.trim().startsWith("[") && line.trim().endsWith("]")) return false;
				
				if (line.trim().length() < 1) continue;
				
				if (line.trim().charAt(0) == ';') continue;
				
				if (line.trim().indexOf('=') < 0) continue;
				
				a = new String[2];
				a[0] = line.substring(0, line.indexOf("=")).trim();
				a[1] = line.substring(line.indexOf("=") + 1, line.length()).trim();

				if (a.length != 2) continue;

				memoInfo = new SaleMemoInfo();
				// 记录描述
				memoInfo.desc = a[0];

				b = a[1].split(",");

				// 记录对应的字段
				if (b.length > 0)
				{
					memoInfo.value = b[0];
				}
				
				//记录控件类型
				if (b.length > 1)
				{
					memoInfo.type = b[1];
				}
				
				//是否允许为空
				if (b.length > 2)
				{	
					memoInfo.allowNull = b[2].trim().length()>0?b[2].trim().charAt(0):'Y';
				}
				
				if (b.length > 3)
				{
					// 下拉框记录选项
					if (b[1].equalsIgnoreCase("COMBOX"))
					{
						// cmd&code-str2|name-str3
						// txt&01-value1|02-value2
						
						String[] strstemp = b[2].split("&");
						//判断是否有命令 strstemp[0] = cmd;strstemp[1] = code-str2|name-str3
						if (strstemp.length > 1 && strstemp[0].trim().length() > 0)
						{
							memoInfo.content.type = strstemp[0];
							// strstemp1[0]=code-str2; strstemp1[1]=name-str3
							String[] strstemp1 = strstemp[1].split("\\|");
							
							// 解析code,name应该存放的值  
							for (int i = 0;i < strstemp1.length;i++)
							{
								String strtemp = strstemp1[i];
								
								String[] strstemp2 = strtemp.split("-");
								
								if (strstemp2.length == 2)
								{
									if (memoInfo.content.type.equals("cmd") && strstemp2[0].equals("code"))
									{
										memoInfo.content.code = strstemp2[1];
									}
									else if (memoInfo.content.type.equals("cmd") && strstemp2[0].equals("name"))
									{
										memoInfo.content.name = strstemp2[1];
									}
									else
									{
										KeyValueDef kvd = new KeyValueDef();
										kvd.key = strstemp2[0];
										kvd.value = strstemp2[1];
										
										memoInfo.content.vccontent.add(kvd);
									}
								}
							}
						}
					}
					
					// 文本框记录最大长度
					if (b[1].equalsIgnoreCase("TEXTBOX"))
					{
						memoInfo.maxLength = Integer.parseInt(b[3]);
					}
				}
				
				if (b.length > 4)
				{
					String[] strs = b[4].split("\\|");
					
					for(int i = 0;i < strs.length;i++)
					{
						memoInfo.associate.add(strs[i]);
					}
				}
				
				vector.add(memoInfo);
			}
		}
		catch (Exception e)
		{
			return false;
		}
		
		return true;
	}

	// 检查是否存在需要校验的数据为空的文本框
	public boolean checkAllowNull()
	{
		if (this.vcCfg == null || this.vcCfg.size() < 1) return false;
		SaleMemoInfo memoInfo;
		for (int i = 0; i < this.vcCfg.size(); i++)
		{
			memoInfo = (SaleMemoInfo) this.vcCfg.get(i);
			if (memoInfo.allowNull == 'N' || (memoInfo.allowNull == 'S' && GlobalInfo.isOnline))
			{
				if (SaleMemoInfo.TEXTTYPE.equalsIgnoreCase(memoInfo.type))
				{
					if (memoInfo.curcontent.trim().length() < 1)
					{
						int count = i + 1;
						MessageBox msg = new MessageBox(shell);
						msg.setMessage(Language.apply("第{0}行{1}数据为必填，请填写完整", new Object[]{count + "",memoInfo.desc + ""}));
						msg.open();
						return false;
					}
				}
				else if (SaleMemoInfo.COMBOTYPE.equalsIgnoreCase(memoInfo.type))
				{
					if (memoInfo.curselindex < 0)
					{
						int count = i + 1;
						MessageBox msg = new MessageBox(shell);
						msg.setMessage(Language.apply("第{0}行{1}数据为必填，请填写完整", new Object[]{count + "",memoInfo.desc + ""}));
						msg.open();
						return false;
					}
				}
			}
		}
		
		return true;
	}

	public boolean setValueToObject(SaleAppendDef saleAppendDef)
	{
		saleAppendDef.syjh = this.saleHead.syjh;
		saleAppendDef.fphm = this.saleHead.fphm;
		saleAppendDef.rowno = -1;

		SaleMemoInfo saleMemoInfo;
		try
		{
			for (int i = 0; i < this.vcCfg.size(); i++)
			{
				saleMemoInfo = (SaleMemoInfo) this.vcCfg.get(i);
				String col = saleMemoInfo.value;

				for (int j = 0; j < SaleAppendDef.ref.length; j++)
				{
					if (col.equalsIgnoreCase(SaleAppendDef.ref[j]))
					{
						String value = "";
						
						if (saleMemoInfo.type.equals(SaleMemoInfo.TEXTTYPE))
						{
							value = saleMemoInfo.curcontent;
						}
						else if (saleMemoInfo.type.equals(SaleMemoInfo.COMBOTYPE))
						{
							if (saleMemoInfo.curselindex >= 0)
							{
								KeyValueDef kvd = (KeyValueDef)saleMemoInfo.content.vccontent.get(saleMemoInfo.curselindex);
								value = kvd.key + "-" + kvd.value;
							}
						}
							
						saleAppendDef.getClass().getDeclaredField(SaleAppendDef.ref[j]).set(saleAppendDef, value);
					}
				}
			}
			return true;
		}
		catch (Exception e)
		{
			// TODO 自动生成 catch 块
			e.printStackTrace();
			return false;
		}
	}
	
	public char send()
	{
		char issuc = 'N';
		
		// 保存并打印
		if (checkAllowNull())
		{
			SaleAppendDef saleAppendDef = new SaleAppendDef();
			if (setValueToObject(saleAppendDef))
			{
				Vector saleappends = new Vector();
				saleappends.add(saleAppendDef);
				String sellDate = saleHead.rqsj.substring(0,10);
				
				// 既上传又打印
				if (GlobalInfo.sysPara.saleAppendStatus == 'Y')
				{
					if (AccessDayDB.getDefault().writeSaleAppend(getDaySql(sellDate), saleappends))
					{
						MessageBox msg = new MessageBox(shell);
						msg.setMessage(Language.apply("小票附加数据保存成功"));
						msg.open();
						
						isDataSaved = "Y";
						
						if (DataService.getDefault().sendSaleAppend(saleappends))issuc = 'Y';
						
						// 打印
						SaleAppendBillMode appendBillMode = (SaleAppendBillMode)SaleAppendBillMode.getDefault();
						appendBillMode.setTemplateObject(saleHead, salegoods, salepay, saleappends);
						appendBillMode.printBill();
					}
				}
				
				// 既不上传又不打印
				if (GlobalInfo.sysPara.saleAppendStatus == 'N')
				{
					if (AccessDayDB.getDefault().writeSaleAppend(getDaySql(sellDate), saleappends))
					{
						MessageBox msg = new MessageBox(shell);
						msg.setMessage(Language.apply("小票附加数据保存成功"));
						msg.open();
						
						isDataSaved = "Y";
						
						issuc = 'S';
					}
				}
				
				// 只上传不打印
				if (GlobalInfo.sysPara.saleAppendStatus == 'S')
				{
					if (AccessDayDB.getDefault().writeSaleAppend(getDaySql(sellDate), saleappends))
					{
						MessageBox msg = new MessageBox(shell);
						msg.setMessage(Language.apply("小票附加数据保存成功"));
						msg.open();
						
						isDataSaved = "Y";
						
						if (DataService.getDefault().sendSaleAppend(saleappends)) issuc = 'Y';
					}
				}
				
				// 只打印不上传
				if (GlobalInfo.sysPara.saleAppendStatus == 'P')
				{
					if (AccessDayDB.getDefault().writeSaleAppend(getDaySql(sellDate), saleappends))
					{
						MessageBox msg = new MessageBox(shell);
						msg.setMessage(Language.apply("小票附加数据保存成功"));
						msg.open();
						
						isDataSaved = "Y";
						
						issuc = 'S';
						
						// 打印
						SaleAppendBillMode appendBillMode = (SaleAppendBillMode)SaleAppendBillMode.getDefault();
						appendBillMode.setTemplateObject(saleHead, salegoods, salepay, saleappends);
						appendBillMode.printBill();
					}
				}
			}
		}
		
		return issuc;
	}
	
	public char reSend()
	{
		char issuc = 'N';
		
		// 保存并打印
		if (checkAllowNull())
		{
			SaleAppendDef saleAppendDef = new SaleAppendDef();
			if (setValueToObject(saleAppendDef))
			{
				Vector saleappends = new Vector();
				saleappends.add(saleAppendDef);
				String sellDate = saleHead.rqsj.substring(0,10);
				
				// 既上传又打印
				if (GlobalInfo.sysPara.saleAppendStatus == 'Y')
				{
					if (AccessDayDB.getDefault().writeSaleAppend(getDaySql(sellDate), saleappends))
					{
						MessageBox msg = new MessageBox(shell);
						msg.setMessage(Language.apply("小票附加数据保存成功"));
						msg.open();
						
						isDataSaved = "Y";
						
						if (DataService.getDefault().sendSaleAppend(saleappends)) issuc = 'N';
						// 打印
					}
				}
				
				// 既不上传又不打印
				if (GlobalInfo.sysPara.saleAppendStatus == 'N')
				{
					if (AccessDayDB.getDefault().writeSaleAppend(getDaySql(sellDate), saleappends))
					{
						MessageBox msg = new MessageBox(shell);
						msg.setMessage(Language.apply("小票附加数据保存成功"));
						msg.open();
						
						isDataSaved = "Y";
						
						issuc = 'S';
					}
				}
				
				// 只上传不打印
				if (GlobalInfo.sysPara.saleAppendStatus == 'S')
				{
					if (AccessDayDB.getDefault().writeSaleAppend(getDaySql(sellDate), saleappends))
					{
						MessageBox msg = new MessageBox(shell);
						msg.setMessage(Language.apply("小票附加数据保存成功"));
						msg.open();
						
						isDataSaved = "Y";
						
						
						if (DataService.getDefault().sendSaleAppend(saleappends)) issuc = 'Y';
					}
				}
				
				// 只打印不上传
				if (GlobalInfo.sysPara.saleAppendStatus == 'P')
				{
					if (AccessDayDB.getDefault().writeSaleAppend(getDaySql(sellDate), saleappends))
					{
						MessageBox msg = new MessageBox(shell);
						msg.setMessage(Language.apply("小票附加数据保存成功"));
						msg.open();
						
						isDataSaved = "Y";
						
						issuc = 'S';
					}
				}
			}
		}
		
		return issuc;
	}
	
	public Sqldb getDaySql (String date)
	{
        ManipulateDateTime dt = new ManipulateDateTime();
		if (date.equals(dt.getDateByEmpty()))
		{
			return 	GlobalInfo.dayDB;
		}
		else
		{
			return LoadSysInfo.getDefault().loadDayDB(date);
		}
	}
	
	// 从本地day数据库中读取小票附加信息
	public char readFromDataBase ()
	{
		char issuc = 'N';
		
		Vector datas = new Vector();
		SaleAppendDef saleAppend = null;
		
		try
		{
			String sellDate = saleHead.rqsj.substring(0,10);
			
			if (AccessDayDB.getDefault().getSaleAppendInfo(getDaySql(sellDate), datas, saleHead.syjh, saleHead.fphm))
			{
				if (datas.size() < 1) 
				{
					MessageBox msg = new MessageBox(shell);
					msg.setMessage(Language.apply("没有找到[{0}]小票附加信息", new Object[]{saleHead.fphm + ""}));
					msg.open();
					return issuc;
				}

				saleAppend = (SaleAppendDef)datas.get(0);
				
				issuc = saleAppend.netbz == 'Y'?'Y':'S';
				
				// 根据saleAppend生成vcdefault(默认值,以便初始化的时候,好填入表单进去);
				for (int j = 0; j < SaleAppendDef.ref.length; j ++)
				{
					KeyValueDef kvd = new KeyValueDef();
					kvd.key = SaleAppendDef.ref[j];
					kvd.value = saleAppend.getClass().getDeclaredField(SaleAppendDef.ref[j]).get(saleAppend).toString();
				
					if (vcdefault == null)
					{
						vcdefault = new Vector();
					}
					
					vcdefault.add(kvd);
				}

				return issuc;
			}
			
			return issuc;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return issuc;
		}
	}
}
