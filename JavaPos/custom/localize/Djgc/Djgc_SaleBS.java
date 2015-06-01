package custom.localize.Djgc;

import java.sql.ResultSet;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.CheckGoodsMode;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Cmls.Cmls_SaleBS;

public class Djgc_SaleBS extends Cmls_SaleBS
{
	OperUserDef user = null;
//	 重新打印上一张小票
	public void rePrint()
	{
		ResultSet rs = null;
		SaleHeadDef saleheadprint = null;
		Vector salegoodsprint = null;
		Vector salepayprint = null;

		// 盘点
		if (SellType.ISCHECKINPUT(saletype))
		{
			if (saleGoods == null || saleGoods.size() <= 0)
				return;

			if (!CheckGoodsMode.getDefault().isLoad())
				return;

			MessageBox me = new MessageBox(Language.apply("你确实要打印盘点小票吗?"), null, true);

			if (me.verify() != GlobalVar.Key1)
				return;

			CheckGoodsMode.getDefault().setTemplateObject(saleHead, saleGoods, salePayment);

			CheckGoodsMode.getDefault().printBill();

			return;
		}

		if (GlobalInfo.syjDef.printfs == '1' && saleGoods != null && saleGoods.size() > 0)
		{
			new MessageBox(Language.apply("当前打印为即扫即打并且已有商品交易,不能重打!"), null, false);

			return;
		}

		// 检查发票是否打印完,打印完未设置新发票号则不能交易
		if (Printer.getDefault().getSaleFphmComplate()) { return; }

		MessageBox me = new MessageBox(Language.apply("你确实要重印上一张小票吗?"), null, true);
		try
		{
			if (me.verify() == GlobalVar.Key1 && getReprintAuth())
			{
				Object obj = null;
				String fphm = null;

				if (curGrant.privdy != 'Y' && curGrant.privdy != 'L')
				{
					if ((user = DataService.getDefault().personGrant(Language.apply("授权重打印小票"))) != null)
					{
						if (user.privdy != 'Y' && user.privdy != 'L')
						{
							new MessageBox(Language.apply("当前工号没有重打上笔小票权限!"));

							return;
						}

						String log = Language.apply("授权重打印上一笔小票,授权工号:") + user.gh;
						AccessDayDB.getDefault().writeWorkLog(log);
					}
					else
					{
						return;
					}
				}
				else
				{
					user = new OperUserDef();
				}

				if ((obj = GlobalInfo.dayDB.selectOneData("select max(fphm) from salehead where syjh = '" + ConfigClass.CashRegisterCode + "'")) != null)
				{
					try
					{
						fphm = String.valueOf(obj);

						if ((rs = GlobalInfo.dayDB.selectData("select * from salehead where syjh = '" + ConfigClass.CashRegisterCode + "' and  fphm = " + fphm)) != null)
						{

							if (!rs.next())
							{
								new MessageBox(Language.apply("没有查询到小票头,不能打印!"));
								return;
							}

							saleheadprint = new SaleHeadDef();

							if (!GlobalInfo.dayDB.getResultSetToObject(saleheadprint)) { return; }
						}
						else
						{
							new MessageBox(Language.apply("查询小票头失败!"), null, false);
							return;
						}
					}
					catch (Exception ex)
					{
						new MessageBox(Language.apply("查询小票头出现异常!"), null, false);
						ex.printStackTrace();
						return;
					}
					finally
					{
						GlobalInfo.dayDB.resultSetClose();
					}

					try
					{
						if ((rs = GlobalInfo.dayDB.selectData("select * from SALEGOODS where syjh = '" + ConfigClass.CashRegisterCode + "' and fphm = " + fphm + " order by rowno")) != null)
						{
							boolean ret = false;
							salegoodsprint = new Vector();
							while (rs.next())
							{
								SaleGoodsDef sg = new SaleGoodsDef();

								if (!GlobalInfo.dayDB.getResultSetToObject(sg)) { return; }

								salegoodsprint.add(sg);

								ret = true;
							}

							if (!ret)
							{
								new MessageBox(Language.apply("没有查询到小票明细,不能打印!"));
								return;
							}
						}
						else
						{
							new MessageBox(Language.apply("查询小票明细失败!"), null, false);
							return;
						}
					}
					catch (Exception ex)
					{
						new MessageBox(Language.apply("查询小票明细出现异常!"), null, false);
						ex.printStackTrace();
						return;
					}
					finally
					{
						GlobalInfo.dayDB.resultSetClose();
					}

					try
					{
						if ((rs = GlobalInfo.dayDB.selectData("select * from SALEPAY where syjh = '" + ConfigClass.CashRegisterCode + "' and fphm = " + fphm + " order by rowno")) != null)
						{
							boolean ret = false;
							salepayprint = new Vector();
							while (rs.next())
							{
								SalePayDef sp = new SalePayDef();

								if (!GlobalInfo.dayDB.getResultSetToObject(sp)) { return; }

								salepayprint.add(sp);

								ret = true;
							}
							if (!ret)
							{
								new MessageBox(Language.apply("没有查询到付款明细,不能打印!"));
								return;
							}
						}
						else
						{
							new MessageBox(Language.apply("查询付款明细失败!"), null, false);
							return;
						}
					}
					catch (Exception ex)
					{
						new MessageBox(Language.apply("查询付款明细出现异常!"), null, false);
						ex.printStackTrace();
						return;
					}
					finally
					{
						GlobalInfo.dayDB.resultSetClose();
					}

					saleheadprint.printnum++;
					AccessDayDB.getDefault().updatePrintNum(saleheadprint.syjh, String.valueOf(saleheadprint.fphm), String.valueOf(saleheadprint.printnum));
					ProgressBox pb = new ProgressBox();
					pb.setText(Language.apply("现在正在重打印小票,请等待....."));
					try
					{
						printSaleTicket(saleheadprint, salegoodsprint, salepayprint, false);
					}
					finally
					{
						pb.close();
					}
				}
				else
				{
					new MessageBox(Language.apply("当前没有销售数据,不能打印!"));
				}
			}
		}
		finally
		{
			saleheadprint = null;

			if (salegoodsprint != null)
			{
				salegoodsprint.clear();
				salegoodsprint = null;
			}

			if (salepayprint != null)
			{
				salepayprint.clear();
				salepayprint = null;
			}
		}
	}
	
	
	SaleHeadDef tempsalehead = null;
	static String[] b = null;
	
	public void printSaleTicket(SaleHeadDef vsalehead, Vector vsalegoods, Vector vsalepay, boolean isRed)
	{
		String type = "SalePrintMode.ini";
		if (vsalehead != null && vsalehead.djlb != null)
			type = vsalehead.djlb;

		Vector tempsalegoods = null;
		Vector tempsalepay = null;

		try
		{
			tempsalehead = SaleBillMode.getDefault(type).getSalehead();
			tempsalegoods = SaleBillMode.getDefault(type).getSalegoods();
			tempsalepay = SaleBillMode.getDefault(type).getSalepay();

			// 联网获取赠送打印清单
			DataService dataservice = (DataService) DataService.getDefault();
			Vector gifts = dataservice.getSaleTicketMSInfo(vsalehead, vsalegoods, vsalepay);
			SaleBillMode.getDefault(type).setSaleTicketMSInfo(vsalehead, gifts);

			// 检查是否需要重打印赠品联授权
			boolean bok = true;
			
			
			if (vsalehead.printnum > 0 && SaleBillMode.getDefault(type).needMSInfoPrintGrant())
			{
				if (GlobalInfo.posLogin.priv.charAt(1) != 'Y')
				{
					OperUserDef staff = DataService.getDefault().personGrant(Language.apply("重打印赠券授权"));

					if (staff == null || staff.priv.charAt(1) != 'Y')
					{
						new MessageBox(Language.apply("此交易存在赠券或者赠品\n该审批员无重打印赠品或者赠券权限"));
						bok = false;
					}
				}
			}
			
	 		if(!getReprint(vsalehead)) return ;
			
			if (!bok)
			{
				SaleBillMode.getDefault(type).setSaleTicketMSInfo(vsalehead, null);
			}

			if (vsalehead != null && vsalegoods != null && vsalepay != null)
			{
				SaleBillMode.getDefault(type).setTemplateObject(vsalehead, vsalegoods, vsalepay);
				SaleBillMode.getDefault(type).printBill();
				
				
				if(vsalehead.printnum > 0)
				{
					String[] s = Djgc_SaleBS.getResult();   //上传字段

					s[7] = String.valueOf(Printer.getDefault().getCurrentSaleFphm()-1);
					new Djgc_NetService().postReprint(s);
				}
				
			
			}
			else
			{
				new MessageBox(Language.apply("未发现小票对象，不能打印\n或\n打印模版读取失败"));
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			SaleBillMode.getDefault(type).setTemplateObject(tempsalehead, tempsalegoods, tempsalepay);
		}
	}
	
	
	
	protected boolean getReprint(SaleHeadDef vsalehead)
	{
//		从本地查找重打原因
		ResultSet rs = null;
		ReprintDef print = null;
		try{			
			if ((rs = GlobalInfo.localDB.selectData("select IWID,IWMEMO,IWSTATUS from REPRINT")) != null)
			{
				if (!rs.next())
				{
					new MessageBox(Language.apply("没有查询到小票打印原因!"));
					return false;
				}

				String code = "";
				String text = "";
				String log = "";
				String printType = "2";              //发票打印类型  1正常销售 2重打
				
				String startfph = String.valueOf(Printer.getDefault().getCurrentSaleFphm());
				String usedfphnum = "";
				
				Vector reprint = new Vector();
				String[] title =  { "打印原因ID" ,"原因说明" ,"是否启用(Y/N)"};
				int[] width = { 150, 250 ,150};

				do{
					print = new ReprintDef();
					
					if (!GlobalInfo.localDB.getResultSetToObject(print)) { return false; }
					
					reprint.add(new String[] {String.valueOf(print.IWID), print.IWMEMO, print.IWSTATUS});
				}while(rs.next());

				if(user.gh == null) user.gh = GlobalInfo.posLogin.gh;						
				int choice = new MutiSelectForm().open("请选择打印原因", title, width, reprint, true);
				if (choice == -1)
				{
					new MessageBox(Language.apply("没有选择打印原因"));
					log = "收银机号:" + ConfigClass.CashRegisterCode + ",小票号:" + vsalehead.fphm + ",发票打印类型:" + printType + ",打印原因:" + "0" + "没有选打印原因" + ",授权工号:"+ user.gh ;
					return false;
				}else {
					String[] row = (String[]) (reprint.elementAt(choice));
					code = row[0].toString();
					text = row[2].toString();
					log = "收银机号:" + ConfigClass.CashRegisterCode + ",小票号:" + vsalehead.fphm + ",发票打印类型:" + printType + ",打印原因:" + code + ",授权工号:" + user.gh;
				}
				AccessDayDB.getDefault().writeWorkLog(log,"1234");
				
				
				String[] s = {GlobalInfo.sysPara.mktcode, ConfigClass.CashRegisterCode ,String.valueOf(vsalehead.fphm) ,printType ,code ,user.gh ,startfph, usedfphnum, ManipulateDateTime.getDateTimeByClock()};
				b = s;
				
				return true;
			}
			else
			{
				new MessageBox(Language.apply("没有查询到小票打印原因!"), null, false);
				return false;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			new MessageBox(Language.apply("获取打印原因列表异常"));
			return false;
		}		
	}
	
	public static String[] getResult()
	{
		return b;
	}
}
