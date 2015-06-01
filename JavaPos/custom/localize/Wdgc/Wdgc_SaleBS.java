package custom.localize.Wdgc;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.RdPlugins;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bhcm.Bhcm_SaleBS;

public class Wdgc_SaleBS extends Bhcm_SaleBS
{

	String mktcode = GlobalInfo.sysPara.mktcode;
	String syjh = GlobalInfo.syjStatus.syjh;
	String syyh = GlobalInfo.posLogin.gh;
	String trace = String.valueOf(GlobalInfo.syjStatus.fphm);

	public void exitSell()
	{
		// 检查是否允许退出
		if (!checkAllowExit()) { return; }

		// 如果是退货则回到销售状态
		if (SellType.ISBACK(this.saletype) && (GlobalInfo.posLogin.privth != 'T'))
		{
			if (new MessageBox("你确定从退货切换到销售状态吗?", null, true).verify() == GlobalVar.Key1)
			{
				// 退回对应的销售类型
				djlbBackToSale();

				// 初始化交易
				initOneSale(this.saletype);
			}
		}
		else if (SellType.ISBACK(this.saletype) && GlobalInfo.posLogin.privth == 'T')
		{
			if (new MessageBox("你确定要退出收银系统吗?\n\n等待20秒，严禁关机过程中断电.", null, true).verify() == GlobalVar.Key1)
			{
				// 关闭销售界面
				saleEvent.saleform.dispose();

				// 退出系统
				AccessDayDB.getDefault().writeWorkLog("收银员注销登录", StatusType.WORK_RELOGIN);
				GlobalInfo.background.quitSysInfo();
			}
		}
		else if (!SellType.getDefault().COMMONBUSINESS(this.saletype, this.hhflag, this.saleHead) && ManipulateStr.textInString("0101", GlobalInfo.posLogin.funcmenu, ",", false))
		{
			if (new MessageBox("你确定返回到正常销售状态吗?", null, true).verify() == GlobalVar.Key1)
			{
				// 退回对应的销售类型
				this.saletype = SellType.RETAIL_SALE;
				this.hhflag = 'N';

				// 初始化交易
				initOneSale(this.saletype);
			}
		}
		else
		{
			// 调试模式且定义了菜单功能键可直接退出系统,否则弹出系统功能菜单
			if (allowQuickExitSell())
			{
				if (new MessageBox("你确定要退出收银系统吗?\n\n等待20秒，严禁关机过程中断电.", null, true).verify() == GlobalVar.Key1)
				{
					// 关闭销售界面
					saleEvent.saleform.dispose();

					// 退出系统
					AccessDayDB.getDefault().writeWorkLog("收银员登出", StatusType.WORK_RELOGIN);
					GlobalInfo.background.quitSysInfo();
				}
			}
			else
			{
				saleEvent.showFuncMenu();
			}
		}
	}

	// 会员授权
	public boolean memberGrant()
	{
		if (isPreTakeStatus())
		{
			new MessageBox("预售提货状态下不允许重新刷卡");
			return false;
		}

		// 会员卡必须在商品输入前,则输入了商品以后不能刷卡,指定小票除外
		if (GlobalInfo.sysPara.customvsgoods == 'A' && saleGoods.size() > 0 && !isNewUseSpecifyTicketBack(false))
		{
			new MessageBox("必须在输入商品前进行刷会员卡\n\n请把商品清除后再重刷卡");
			return false;
		}

//		if (curCustomer != null)
//		{
//			if ((new MessageBox("已刷大会员卡,是否重新刷卡?", null, true)).verify() != GlobalVar.Key1) { return false; }
//		}
		// 读取会员卡
		// bigCust = new CustomerDef();
		StringBuffer cardno = new StringBuffer();
		TextBox txt = new TextBox();
		if (!txt.open("请刷大会员卡", "大会员号", "请将大会员卡从刷卡槽刷入", cardno, 0, 0, false, TextBox.MsrKeyInput)) { return false; }

		PosLog.getLog(getClass()).fatal("Track1:" + txt.Track1 + " ;Track2:" + txt.Track2 + " ;Track3:" + txt.Track3 );
		if (null == txt.Track2 || txt.Track2.equals("") || txt.Track2.length() >= 40)
		{
			new MessageBox("读取磁道信息有问题！！！");
			return false;
		}
		
		String track =  txt.Track2;

		// 商户代码、收银员号、磁道号、收银流水号
		String checkMemberReturn = null;

		if (RdPlugins.getDefault().getPlugins1().exec(2, syjh + "," + syyh + "," + track + "," + trace))
		{
			checkMemberReturn = (String) RdPlugins.getDefault().getPlugins1().getObject();
		}

		CustomerDef cust = null;
		if (checkMemberReturn == null || checkMemberReturn.trim().length() < 1)
			return false;
		if (checkMemberReturn.substring(0, 2).equals("00"))
		{
			cust = new CustomerDef();
			cust.code = checkMemberReturn.substring(2, 21).trim();
			cust.value6 = Double.parseDouble(checkMemberReturn.substring(21, 33));
			cust.value7 = Double.parseDouble(checkMemberReturn.substring(33, 45));
			cust.memo = track; // 记录刷卡信息，在交易完成后增加积分需要使用磁道信息
			cust.status = "Y";// 大会员卡消费后标记；Y-初始化标记;0-消费;1-消费撤销;2-隔日退货;3-退货不成功

		}

		if (cust == null)
			return false;

		curCustomer = cust;
		saleHead.hykh = cust.code;

		if (SellType.ISBACK(this.saletype))
		{
			InputDetailForm idf = new InputDetailForm();
			if (idf.open())
			{
				// curCustomer = new CustomerDef();

				curCustomer.str1 = idf.getOldTrace();// 主机流水号
				curCustomer.str2 = idf.getOldBatch();// 批次
				curCustomer.str3 = idf.getOldDate();// 日期

				// curCustomer.status = "2";
				curCustomer.num1 = 1; // 标记:1 - 未扣减积分 ,2 - 扣减积分成功
			}
		}
		/*
		 * // 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况) if
		 * (isNewUseSpecifyTicketBack(false)) { // 指定小票退仅记录卡号,不执行商品重算等处理
		 * curCustomer = cust; saleHead.hykh = cust.code; //saleHead.hytype =
		 * cust.type; //saleHead.str4 = cust.valstr2; //saleHead.hymaxdate =
		 * cust.maxdate; return true; }
		 */

		return true;
	}

	public String getVipInfoLabel()
	{
		if (curCustomer == null)
			return "";
		else
		{
			return "[" + curCustomer.code + "]";
		}
	}

	// 检查是否是第一次使用大会员支付
	public boolean checkIsSalePay(String code)
	{

		if (code.equals("0303"))
		{
			if (curCustomer == null)
			{
				new MessageBox("请刷大会员卡后再使用 万达会员支付 方式!\n\n");
				return true;// false;
			}
//			else
//			{
//				if (curCustomer.status.equals("0") || curCustomer.status.equals("2"))
//				{
//					new MessageBox("每笔销售只能使用 万达会员支付 方式一次!\n\n");
//					return true;
//				}
//				/*
//				 * else if (curCustomer.status.equals("2")) { new
//				 * MessageBox("退货的时候不允许大会员支付!\n\n" ); return true; }
//				 */
//			}
		}
		return false;
	}

	public boolean deleteSalePay(int index, boolean isautodel)
	{
		// 是否允许删除当前付款方式
		if (!isautodel && !isDeletePay(index))
			return false;

		// 扣回处理
		if (isRefundStatus())
			return deleteRefundPay(index);

		try
		{
			if (index >= 0)
			{
				// 付款取消交易才能删除已付款
				Payment p = (Payment) payAssistant.elementAt(index);
				
				if (p.salepay.payname.equals("万达会员支付"))
				{
					new MessageBox("万达大会员支付不容许撤消。\n如想撤消，请付完款后退货处理!!");
					
					return false;
				}

				if (p.cancelPay())
				{
					// 删除已付款
					delSalePayObject(index);

					// 重算剩余付款
					calcPayBalance();

					// 刷新已付款，更新断点文件
					getSalePaymentDisplay();

					return true;
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return false;
	}
	
	
	// 打印pos销售小票之前，若有大会员付款，先发送Of_TransComplete()完成交易
	public void doSaleFinshed(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
	{
		try
		{
			if (curCustomer != null)
			{
				Dhy_PaymentBankFunc pbfunc = new Dhy_PaymentBankFunc();
				Vector memo = new Vector();
				memo.add("0303");
				memo.add(trace);
				memo.add(this);
				//当使用了万达会员支付，则已经进行了积分，不需发送积分
					for (int j = 0; j < salePayment.size(); j++)
					{
						SalePayDef salepay = (SalePayDef) salePayment.get(j);
						if (salepay.payname.equals("万达会员支付"))
							return;
					}
					if (SellType.ISBACK(this.saletype))
					{
						String Return = null; String code =""; 
						for (int i = 0; i < 3 && !code.equals("00"); i++ ) 
						{
							  boolean ret = pbfunc.callBankFunc(PaymentBank.XYKTH, saleHead.ysje," ", " ", " ", " ", "", "", memo);
							  Return = pbfunc.result;
							  if (Return != null) 
							  { 
								  code = Return.substring(0,2); 
						      }							 
						}
					}
					else
					{
						// 刷了会员卡后，没用大会员的支付方式付款，发送积分
						String SaleReturn = null;
						String code = "";
						for (int i = 0; i < 3 && !code.equals("00"); i++)
						{
							boolean ret = pbfunc.callBankFunc(PaymentBank.XKQT1, saleHead.ysje, " ", " ", " ", " ", " ", " ", memo);
							SaleReturn = pbfunc.result;
							if (SaleReturn != null)
							{
								code = SaleReturn.substring(0, 2);
							}
						}

					}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			curCustomer = null; // 每次交易完成将大会员信息置空
		}

	}
}
