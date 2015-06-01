package custom.localize.Hzjb;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Hzjb_DisplaySaleTicketBS extends DisplaySaleTicketBS
{
	public void printSaleTicket(SaleHeadDef vsalehead, Vector vsalegoods, Vector vsalepay, boolean isRed)
	{
		boolean enableLHBillMode = false;

		for (int i = 0; i < vsalepay.size(); i++)
		{
			SalePayDef pay = (SalePayDef) vsalepay.get(i);

			if (pay.paycode.equals("0402"))
			{
				enableLHBillMode = true;
				break;
			}
		}

		if (enableLHBillMode)
		{
			StringBuffer sb = new StringBuffer();
			sb.append("请选择打印内容\n");
			sb.append(Convert.appendStringSize("", "1 - 打印联华凭证 ", 0, 20, 18, 0) + "\n");
			sb.append(Convert.appendStringSize("", "2 - 打印交易小票", 0, 20, 18, 0)+ "\n");
			sb.append(Convert.appendStringSize("", "任意键- 打印全部", 0, 20, 18, 0)+ "\n");
			int key = new MessageBox(sb.toString()).verify();

			if (key == GlobalVar.Key1)
			{
				printLHMzkBill(vsalehead, vsalepay);
				return;
			}
			else if (key == GlobalVar.Key2)
			{
				printSaleBill(vsalehead, vsalegoods, vsalepay, isRed, enableLHBillMode);
				return;
			}
			else
			{
				printLHMzkBill(vsalehead, vsalepay);
				printSaleBill(vsalehead, vsalegoods, vsalepay, isRed, enableLHBillMode);
				return;
			}
		}
		printSaleBill(vsalehead, vsalegoods, vsalepay, isRed, false);
	}

	public void printSaleBill(SaleHeadDef vsalehead, Vector vsalegoods, Vector vsalepay, boolean isRed, boolean enableLHBillMode)
	{
		// 打印小票前先查询满赠信息
		try
		{
			// 联网获取赠送打印清单
			DataService dataservice = (DataService) DataService.getDefault();
			Vector gifts = dataservice.getSaleTicketMSInfo(vsalehead, vsalegoods, vsalepay);

			if (enableLHBillMode)
				Hzjb_LHSaleBillMode.getInstance(vsalehead.djlb).setSaleTicketMSInfo(vsalehead, gifts);
			else
				SaleBillMode.getDefault().setSaleTicketMSInfo(vsalehead, gifts);

			// 检查是否需要重打印赠品联授权
			boolean bok = true;
			if (vsalehead.printnum > 0 && (enableLHBillMode ? Hzjb_LHSaleBillMode.getInstance().needMSInfoPrintGrant() : SaleBillMode.getDefault().needMSInfoPrintGrant()))
			{
				if (GlobalInfo.posLogin.priv.charAt(1) != 'Y')
				{
					OperUserDef staff = DataService.getDefault().personGrant("重打印赠券授权");

					if (staff == null || staff.priv.charAt(1) != 'Y')
					{
						new MessageBox("此交易存在赠券或者赠品\n该审批员无重打印赠品或者赠券权限");
						bok = false;
					}
				}
			}
			if (!bok)
			{
				if (enableLHBillMode)
					Hzjb_LHSaleBillMode.getInstance().setSaleTicketMSInfo(vsalehead, null);
				else
					SaleBillMode.getDefault().setSaleTicketMSInfo(vsalehead, null);
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}

		// 打印小票
		try
		{
			if (vsalehead != null && vsalegoods != null && vsalepay != null)
			{
				if (enableLHBillMode)
				{
					Hzjb_LHSaleBillMode.getInstance().setTemplateObject(vsalehead, vsalegoods, vsalepay);
					Hzjb_LHSaleBillMode.getInstance().printBill();
				}
				else
				{
					SaleBillMode.getDefault().setTemplateObject(vsalehead, vsalegoods, vsalepay);
					SaleBillMode.getDefault().printBill();
				}
			}
			else
			{
				new MessageBox("未发现小票对象，不能打印\n或\n打印模版读取失败");
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void printLHMzkBill(SaleHeadDef h, Vector p)
	{
		try
		{
			for (int i = 0; i < p.size(); i++)
			{
				SalePayDef pay = (SalePayDef) p.get(i);

				if (pay.paycode.equals("0402"))
					Hzjb_ICCardCaller.getDefault().rePrintBill(h, pay);

			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
