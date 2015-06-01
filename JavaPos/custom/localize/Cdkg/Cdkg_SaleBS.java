package custom.localize.Cdkg;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.UI.Design.RetSYJForm;

import custom.localize.Bstd.Bstd_SaleBS;

public class Cdkg_SaleBS extends Bstd_SaleBS
{
	
	protected void initBackSell()
	{
		// 如果是要输入家电发货地点,并且是新指定小票退货则输入家电发货地点
		if (GlobalInfo.sysPara.isinputjdfhdd != 'N' && !isNewUseSpecifyTicketBack(false))
		{
			if (!SellType.ISCHECKINPUT(this.saletype))
			{
				if (GlobalInfo.sysPara.isinputjdfhdd == 'S' && jdfhddcode.trim().length() > 0)
				{
					if (GlobalInfo.syjDef.issryyy == 'N' || GlobalInfo.syjDef.issryyy == 'B')
					{
						saleHead.jdfhdd = jdfhddcode;

						saleEvent.yyyh.setText("家电");
						saleEvent.gz.setText("[" + jdfhddcode + "]" + jdfhddname);
					}
				}
				else
				{
					inputJdfhdd();
				}
			}
		}

		// 退货交易初始化以后，恢复授权信息
		if (SellType.ISBACK(this.saletype) && thgrantuser != null)
		{
			saleHead.thsq = thgrantuser.gh;
			curGrant.privth = thgrantuser.privth;
			curGrant.thxe = thgrantuser.thxe;

			thgrantuser = null;
		}

		// 判断退货是否输入收银机号
		if (SellType.isJS(this.saletype))
		{
		}
		else if (isSpecifyTicketBack())
		{
			RetSYJForm frm = new RetSYJForm();

			int done = frm.open(thSyjh, thFphm);
			if (done == frm.Done)
			{
				thSyjh = RetSYJForm.syj;
				thFphm = Long.parseLong(RetSYJForm.fph);
				
				thFphm = Cdkg_UitlTool.decryptFphm(thFphm);
				
				if (SellType.PREPARE_BACK.equals(this.saletype))
				{
					isbackticket = findPreSaleInfo();
				}
				else
				{
					isbackticket = findBackTicketInfo();

					if (!isbackticket)
					{
						if (GlobalInfo.sysPara.inputydoc == 'B' || GlobalInfo.sysPara.inputydoc == 'C')
						{
							// 退回对应的销售类型
							djlbBackToSale();

							// 初始化交易
							initOneSale(this.saletype);
						}
						else if (GlobalInfo.sysPara.inputydoc == 'A' || GlobalInfo.sysPara.inputydoc == 'Y')
						{
							twoBackPersonGrant();
						}
					}
				}
			}
			else if ((done == frm.Cancel || done == frm.Clear) && (GlobalInfo.sysPara.inputydoc == 'A' || GlobalInfo.sysPara.inputydoc == 'Y'))
			{
				// 二次授权
				twoBackPersonGrant();

			}
			else if (SellType.isJF(saletype))
			{
				// 退回对应的销售类型
				djlbBackToSale();

				// 初始化交易
				initOneSale(this.saletype);
			}
		}
	}
	
	public void backSell()
	{
		if (GlobalInfo.syjDef.isth != 'Y')
		{
			new MessageBox("该收银机不允许退货!");

			return;
		}

		// 检查发票是否打印完,打印完未设置新发票号则不能交易
		if (Printer.getDefault().getSaleFphmComplate()) { return; }

		// 已经是指定小票退货状态,再次按退货键则重新输入原小票信息
		if (SellType.isJS(this.saletype))
		{
		}
		else if (isSpecifyTicketBack())
		{
			RetSYJForm frm = new RetSYJForm();

			int done = frm.open(thSyjh, thFphm);

			if (done == frm.Done)
			{
				thSyjh = RetSYJForm.syj;
				thFphm = Long.parseLong(RetSYJForm.fph);
				
				thFphm = Cdkg_UitlTool.decryptFphm(thFphm);

				if (this.saletype.equals(SellType.PREPARE_BACK))
				{
					isbackticket = findPreSaleInfo();
				}
				else
				{
					isbackticket = findBackTicketInfo();
				}
			}
			else if (done == frm.Clear)
			{
				thSyjh = null;
				thFphm = 0;
			}
			else
			{
				// 放弃,不修改上次输入的原收银机号和小票号
			}

			return;
		}

		// 从销售状态切换到相应的退货状态
		if ((saleGoods.size() <= 0) && SellType.ISSALE(saletype))
		{
			// 检查权限
			thgrantuser = null;
			if (((curGrant.privth != 'Y') && (curGrant.privth != 'T')) || (curGrant.thxe <= 0))
			{
				OperUserDef staff = backSellGrant();

				if (staff == null) { return; }

				// 本次授权
				thgrantuser = staff;

				// 记录日志
				String log = "授权退货,小票号:" + GlobalInfo.syjStatus.fphm + ",最大退货限额:" + thgrantuser.thxe + ",授权:" + thgrantuser.gh;
				AccessDayDB.getDefault().writeWorkLog(log);
			}
			else
			{
				thgrantuser = (OperUserDef) (GlobalInfo.posLogin.clone());

				if (cursqktype == '1')
				{
					thgrantuser.gh = cursqkh;
				}
				else
				{
					thgrantuser.gh = GlobalInfo.posLogin.gh;
				}
			}

			// 提示退货权限
			if (curGrant.privth != 'T')
			{
				new MessageBox("授权退货,限额为 " + ManipulatePrecision.doubleToString(thgrantuser.thxe) + " 元");
			}

			// 切换到退货交易类型
			djlbSaleToBack();

			// 初始化交易
			initOneSale(this.saletype);
		}
		else
		{
			new MessageBox("请先完成当前交易!", null, false);
		}
	}

	public boolean checkFinalStatus()
	{
		this.saleHead.str2 = Convert.increaseLong(Cdkg_UitlTool.encryptFphm(this.saleHead.fphm), 8);
		return true;
	}
}
