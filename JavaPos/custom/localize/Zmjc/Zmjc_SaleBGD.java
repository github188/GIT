package custom.localize.Zmjc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Vector;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TimeDate;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Zmjc_SaleBGD extends Zmjc_SaleBSClk
{

	// 写入挂单
	public boolean writeHang()
	{
		try
		{
			return super.writeHang();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			GlobalInfo.statusBar.setHangCount(getHangFileCount());
		}
	}
	
	public boolean readHang()
	{

		// 练习交易不允许挂单
		if (SellType.ISEXERCISE(this.saletype))
			return false;

		// 已有商品不能解挂
		if (saleGoods.size() > 0 && !GlobalInfo.sysPara.onlineGd.equals("A"))
			return false;

		// 检查解挂授权
		if (!readHangGrant())
			return false;

		FileInputStream in = null;
		ObjectInputStream si = null;
		int indexID = 0;

		// 是否需要联网解挂
		boolean isonlinegd = false;

		try
		{
			//
			StringBuffer slstr = new StringBuffer();
			String path = "";
			File gdFile = null;

			int maxGD = getHangFileIndex(false) - 1;
			if (maxGD > 0)
				slstr.append(maxGD);

			do
			{
				// 如果在连网挂单可以不在解挂列表中选择,直接按回车退出选择挂单界面
				boolean cannotchoice = false;
				String strmsg = Language.apply("请输入挂单号");
				if (GlobalInfo.sysPara.onlineGd.equals("Y") || GlobalInfo.sysPara.onlineGd.equals("A"))
				{
					cannotchoice = true;
					strmsg = Language.apply("请输入挂单号或网络挂单号");
				}

				// 输入挂单号
				int choice = -1;
				//获取挂单头vec
				Vector v = getHangFileInfo();//挂单信息(vec_list=vec[arrRowHead+vec[saleGoods] + ...])
				if(v==null || v.size()<=0)
				{
					new MessageBox(Language.apply("读取挂单信息失败!"));
					return false;
				}
				MutiSelectForm msf = new MutiSelectForm();
				//choice = msf.open(strmsg, new String[] { "挂单号", "挂单时间", "收银员号", "交易类型", "交易金额" }, new int[] { 80, 210, 120, 100, 135 }, v, true, 700, 400, 673, 285, false, cannotchoice);
				frmReadHang frmGD = new frmReadHang();
				frmGD.open(v);
				if(!frmGD.isReadOK()) return false;
				choice = frmGD.getGDNO();//挂单编号
				
				if (choice < 0 && choice != -2)
				{
					return false;
				}
				else if (choice == -2 && msf.InputText.length() > 0)
				{
					String strinvno = msf.InputText;
					if (GlobalInfo.sysPara.onlineGd.equals("Y") || GlobalInfo.sysPara.onlineGd.equals("A"))
					{
						if (getHang(strinvno))
						{
							isonlinegd = true;

							break;
						}
						else
						{
							// 联网挂单失败则初始化交易
							// initNewSale();
						}
					}
				}
				else if (choice >= 0)
				{
					// 本地挂单
					//String[] row = (String[]) v.elementAt(choice);
					//indexID = Integer.parseInt(row[0]);
					indexID = choice;

					// 查找挂单
					TimeDate date = new TimeDate(GlobalInfo.balanceDate, "00:00:00");
					path = ConfigClass.LocalDBPath + "Invoice/" + date.cc + date.yy + date.mm + date.dd;
					gdFile = new File(path + "//" + gd_Prefix + indexID);
					if (!gdFile.exists())
					{
						new MessageBox(Language.apply("找不到{0}号挂单!", new Object[]{indexID + ""}));
					}
					else
					{
						isonlinegd = false;

						break;
					}
				}
			} while (true);

			// 如果不是联网挂单需要联网解挂
			if (!isonlinegd)
			{
				// 读取本地挂单
				in = new FileInputStream(path + "//" + gd_Prefix + indexID);
				si = new ObjectInputStream(in);

				// 先检查交易类型
				String saletype1 = (si.readObject()).toString();
				if (!saletype1.equals(saletype))
				{
					si.close();
					si = null;

					new MessageBox(Language.apply("此挂单必须在{0}状态下才能解挂!", new Object[]{SellType.getDefault().typeExchange(saletype1, 'N', saleHead)}));

					return false;
				}

				// 先初始化交易
				initNewSale();

				// 读取交易对象
				readStreamToSellObject(si);

				// 关闭文件
				si.close();
				si = null;
				in.close();
				in = null;

				if (gdFile != null)
				{
					// 删除挂单文件
					gdFile.delete();
				}
			}

			// 刷新数据
			refreshSaleData();

			// 记录日志
			AccessDayDB.getDefault().writeWorkLog(Language.apply("收银员进行{0}号挂单解挂,解挂金额: ", new Object[]{indexID + ""}) + ManipulatePrecision.doubleToString(saleHead.ysje));

			// 计算应付金额
			calcHeadYfje();

			// 刷新界面显示
			saleEvent.updateSaleGUI();

			// 焦点到编码输入框
			if (saleGoods.size() > 0 && GlobalInfo.syjDef.issryyy == 'Y')
			{
				SaleGoodsDef g = (SaleGoodsDef) saleGoods.elementAt(saleGoods.size() - 1);
				saleEvent.yyyh.setText(g.yyyh);
				saleEvent.gz.setText(g.gz);
				saleEvent.saleform.setFocus(saleEvent.code);
			}

			// 检查是否存在付款
			if (salePayment.size() > 0)
			{
				// 先清除全部付款对象列表
				payAssistant.removeAllElements();

				// 根据付款信息创建付款对象
				SalePayDef sp = null;
				for (int i = 0; i < salePayment.size(); i++)
				{
					sp = (SalePayDef) salePayment.elementAt(i);

					// 创建付款对象
					Payment pay = CreatePayment.getDefault().createPaymentBySalePay(sp, saleHead);
					if (pay == null)
					{
						// 放弃所有已付款
						salePayment.removeAllElements();
						payAssistant.removeAllElements();
						return true;
					}

					// 增加已付款
					payAssistant.add(pay);
				}
			}

			GlobalInfo.statusBar.setHangCount(getHangFileCount());
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			new MessageBox(indexID + Language.apply(" 号挂单解挂失败!\n\n") + e.getMessage().trim());

			return false;
		}
		finally
		{
			try
			{
				if (in != null)
					in.close();
				if (si != null)
					si.close();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}	

	public Vector getHangFileInfo()
	{
		Vector vecGDList = new Vector();//挂单列表:vec(头vec + 明细vec)
		

		TimeDate date = new TimeDate(GlobalInfo.balanceDate, "00:00:00");
		String path = ConfigClass.LocalDBPath + "Invoice/" + date.cc + date.yy + date.mm + date.dd;

		File file = new File(path);
		if (file.isDirectory())
		{
			ManipulateDateTime mdt = new ManipulateDateTime();
			String[] filelist = file.list();
			for (int i = 0; i < filelist.length; i++)
			{
				Vector vecGD = new Vector();
				if (filelist[i].indexOf(gd_Prefix) == 0)
				{
					// 读取挂单数据
					FileInputStream in = null;
					ObjectInputStream si = null;
					String saletype1;
					SaleHeadDef saleHead1;
					Vector detail;
					try
					{
						in = new FileInputStream(path + "//" + filelist[i]);
						si = new ObjectInputStream(in);
						saletype1 = (si.readObject()).toString();
						saleHead1 = (SaleHeadDef) si.readObject();
						detail=(Vector) si.readObject();
						System.out.println();
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
						continue;
					}
					finally
					{
						try
						{
							si.close();
							si = null;
							in.close();
							in = null;
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}

					//
					String fileInfo[] = new String[5];
					fileInfo[0] = filelist[i].substring(gd_Prefix.length());
					mdt.setTimeInMill(file.listFiles()[i].lastModified());
					fileInfo[1] = mdt.getDateTimeString();
					fileInfo[2] = saleHead1.syyh;
					fileInfo[3] = SellType.getDefault().typeExchange(saletype1, 'N', saleHead);
					fileInfo[4] = ManipulatePrecision.doubleToString(saleHead1.ysje);
					vecGD.add(fileInfo);//头					
					vecGD.add(detail);//明细
					vecGDList.add(vecGD);//挂单列表
				}
			}
		}
		return vecGDList;
	}
}
