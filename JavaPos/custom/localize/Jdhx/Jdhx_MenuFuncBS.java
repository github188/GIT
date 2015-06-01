package custom.localize.Jdhx;

import java.io.File;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.PrintTemplate.InvoiceSummaryMode;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.MenuFuncDef;
import com.efuture.javaPos.UI.MenuFuncEvent;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Bstd.Bstd_MenuBS;
import custom.localize.Jdhx.Jdhx_WaterFee.FeeHeadDef;

public class Jdhx_MenuFuncBS extends Bstd_MenuBS
{
    public final static int MN_JFSALECZ = 126;							//缴水费红冲
    public final static int MN_CHANGEPRINT = 908;							//切换打印模板

	public boolean execExtendFuncMenu(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		try
		{
			switch (Integer.parseInt(mfd.code))
			{
				case MN_JFSALECZ:
					openJFSALECZ(mfd, mffe);
					break;
				case MN_CHANGEPRINT:
					changePrint(mfd, mffe);
					break;
				default:
					return false;
			}
			
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	public static void openJFSALECZ(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		try
		{
			String id = "";
			StringBuffer sb = new StringBuffer();
			TextBox txt = new TextBox();
			do
			{
				
				if (!txt.open("请输入用户编号或刷会员卡", "用户编号", "请输入用户编号或会员卡查询缴费情况", sb, 0, 0, false, TextBox.MsrKeyInput))
					return ;
				
				if (txt.Track2.trim().equals(""))
					continue;
				
				break;
			}
			while (true);
			
			// 根据会员号或手机号来查询水费用户号
			if (txt.Track2.length() > 9 || txt.Track2.length() == 11)
			{
				CustomerDef cust = new CustomerDef();
				if (!DataService.getDefault().getCustomer(cust, txt.Track2))
					return ;
				
				if (cust != null)
				{
					if (cust.str1 == null || cust.str1.trim().equals(""))
					{
						new MessageBox("该会员未绑定水费用户号");
						return ;
					}
					id  = cust.str1;					
				}
			}
			
			if (txt.Track2.length() >0 && txt.Track2.length() < 9)
			{
				txt.Track2 = Convert.increaseCharForward(txt.Track2, '0', 8);
				id = txt.Track2.trim();
			}	
			
			File file = new File(ConfigClass.LocalDBPath + "water" );
			File[] list = file.listFiles();
			
			int count = 0;
			for(int i = 0; list != null && i < list.length; i ++)
			{
				if (!(list[i].getName().startsWith("water_") && list[i].getName().endsWith(".dat")))
					continue;
				
				ManipulateDateTime mdt = new ManipulateDateTime();
				
				//获得红冲交易文件日期， 超过当天的就删掉,  文件名格式  water_日期时间_用户号.dat,例如: water_20141111143700_00032268.dat
				String date = list[i].getName().split("_")[1].substring(0,8);
				if (mdt.getDateByEmpty().compareTo(date) > 0)
				{
					list[i].delete();
					continue;
				}
				
				if (!id.equals(list[i].getName().split("_")[2].substring(0,8)))
					continue;
				
				count ++;
				Vector info = new Vector();
				info =CommonMethod.readFileByVector(list[i].getAbsolutePath());
				if (info.isEmpty() )
					continue;
				
				String czData = ((String[]) info.get(4))[1].trim();
				sendCzData(czData, list[i].getAbsolutePath());
			}
			
			if (count <= 0)
			{
				new MessageBox("未找到用户号 " + id + " 红冲记录");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public static boolean sendCzData(String info, String file)
	{
		try
		{
			StringBuffer sb = new StringBuffer();
			
			String msg = "";
			if (!Jdhx_WaterFee.getDefault().senddata(info, sb))
			{
				msg = "发送水费冲正信息问题失败!!!\n";
			}
			
			FeeHeadDef head = FeeHeadDef.parseFeeHeadDef(sb.toString());
			if (head == null)
			{
				msg += "解析返回信息失败!!!\n";
			}
			
			if (head != null && head.fhm != null && !head.fhm.equals("00"))
			{
				msg += Jdhx_WaterFee.getDefault().getError(head.fhm);
			}
			
			if (!msg.equals(""))
			{
				new MessageBox(msg);
				return false;
			}
			
			File deFile = new File(file);
			deFile.delete();
			
			new MessageBox("冲正用户" + head.yhh + "信息成功");
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static void changePrint(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		ProgressBox pb = null;
		try
		{
			File file = new File(GlobalVar.ConfigPath);
			File[] list = file.listFiles();
			
			Vector v = new Vector();
			for(int i = 0; i < list.length; i ++)
			{
				if (!list[i].getName().startsWith("SalePrintMode_") )
					continue;
				v.add(list[i].getAbsolutePath());
			}
			
			if (v.size() == 1)
			{
				String path = (String) v.get(0);
				String info = "";
				if (path.endsWith("_Invoice.ini"))
				{
					info = "确认将 正常小票模板 更改为 发票模板 ";
					MessageBox msg =  new MessageBox(info, null, true);
					if (msg.verify() != GlobalVar.Key1)
						return ;
					
					File printFile = new File(GlobalVar.ConfigPath + "//SalePrintMode.ini");
					if (!printFile.renameTo(new File(GlobalVar.ConfigPath + "//SalePrintMode_Ticket.ini")))
					{
						new MessageBox("更改 小票模板 文件名失败， 更改 发票模板失败!!!");
						return ;
					}
					
					File changeFile = new  File(path);
					if (!changeFile.renameTo(new File(GlobalVar.ConfigPath + "//SalePrintMode.ini")))
					{
						new MessageBox("更改 发票模板 文件名失败， 更改 发票模板失败!!!");
						return;
					}
				}
				else if (path.endsWith("_Ticket.ini"))
				{
					info = "确认将 发票模板  更改为  正常小票模板";
					MessageBox msg =  new MessageBox(info, null, true);
					if (msg.verify() != GlobalVar.Key1)
						return ;
					
					File printFile = new File(GlobalVar.ConfigPath + "//SalePrintMode.ini");
					if (!printFile.renameTo(new File(GlobalVar.ConfigPath + "//SalePrintMode_Invoice.ini")))
					{
						new MessageBox("更改 发票模板文件名 失败， 更改 正常小票模板 失败!!!");
						return ;
					}
					
					File changeFile = new  File(path);
					if (!changeFile.renameTo(new File(GlobalVar.ConfigPath + "//SalePrintMode.ini")))
					{
						new MessageBox("更改 小票模板文件名失败， 更改 正常小票模板 失败!!!");
						return;
					}					
				}
				else
				{
					return ;
				}  // end 	 if (path.endsWith("_Invoice.ini"))			
			}
			else if (v.size() == 2)
			{
				MessageBox msg = new MessageBox("1 - 切换至正常小票模板\n2 - 切换至发票打印模板\n其他键 - 退出         ");
				if (msg.verify() == GlobalVar.Key1)
				{
										
					File printFile = new File(GlobalVar.ConfigPath + "//SalePrintMode.ini");
					if (!printFile.delete())
					{
						new MessageBox("删除小票模板文件失败， 更改 发票模板失败!!!");
						return ;
					}

					for (int i = 0; i < v.size(); i++)
					{
						String path = (String) v.get(i);
						if (path.endsWith("_Ticket.ini"))
						{
							File changeFile = new  File(path);
							if (!changeFile.renameTo(new File(GlobalVar.ConfigPath + "//SalePrintMode.ini")))
							{
								new MessageBox("更改 小票模板文件名失败， 更改 正常小票模板失败!!!");
								return;
							}
						}
					}  /// end for
										
				}
				else if (msg.verify() == GlobalVar.Key2)
				{
										
					File printFile = new File(GlobalVar.ConfigPath + "//SalePrintMode.ini");
					if (!printFile.delete())
					{
						new MessageBox("删除小票模板文件失败， 更改 发票模板失败!!!");
						return ;
					}

					for (int i = 0; i < v.size(); i++)
					{
						String path = (String) v.get(i);
						if (path.endsWith("_Invoice.ini"))
						{
							File changeFile = new  File(path);
							if (!changeFile.renameTo(new File(GlobalVar.ConfigPath + "//SalePrintMode.ini")))
							{
								new MessageBox("更改 小票模板文件名失败， 更改 正常小票模板失败!!!");
								return;
							}
						}
					}  /// end for
										
				}
				else
				{
					return ;
				}  // end  if (msg.verify() == GlobalVar.Key1)
			}
			else
			{
				return ;
			} // end if (v.size() == 1)
			
			pb =  new ProgressBox();
			pb.setText("正在读取小票打印模版......");
			if (!SaleBillMode.getDefault().ReadTemplateFile())
			{
				new MessageBox(Language.apply("读取小票打印模版文件错误,建议重启收银软件"));
			}

			pb.setText("正在读取小票汇总打印模版......");
			if (!InvoiceSummaryMode.getDefault().ReadTemplateFile())
			{
				new MessageBox(Language.apply("读取小票汇总打印模版文件错误!,建议重启收银软件"));
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (pb != null)
			{
				pb.close();
				pb = null;
			}

		}
	}

}
