package custom.localize.Bxmx;

import java.util.Vector;

import org.eclipse.swt.widgets.Label;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.CashBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Bxmx_SaleBS2Pay extends Bxmx_SaleBS1Goods
{
	public void paySell()
	{
		if (SellType.ISPREPARETAKE(saletype))
		{
			if (saleHead.str3.equals("A"))
			{
				if (new MessageBox("此单款项已付清,客户是否取货?", null, true).verify() == GlobalVar.Key1)
				{
					if (((Bxmx_NetService) NetService.getDefault()).updatePreSaleheadFlag(saleHead))
					{
						this.initOneSale(SellType.RETAIL_SALE);
					}
					else
					{
						new MessageBox("更新单据状态失败");
					}
				}
				return;
			}

		}
		super.paySell();
	}

	public void calcSellPayMoney(boolean calc)
	{
		super.calcSellPayMoney(calc);

		if (SellType.ISEARNEST(saletype) && SellType.ISBACK(saletype))
		{
			if (saleHead.num1 > 0)
			{
				PayModeDef pmd = (PayModeDef) DataService.getDefault().searchPayMode("01").clone();
				pmd.code = "00";
				pmd.name = "未付款";

				Payment pay = CreatePayment.getDefault().createPaymentByPayMode(pmd, saleEvent.saleBS);
				SalePayDef sp = pay.inputPay(String.valueOf(saleHead.num1));

				addSalePayObject(sp, pay);
			}
		}
	}

	public double calcPayBalance()
	{
		// 如果是扣回付款,付款余额为扣回余额
		if (isRefundStatus())
			return calcRefundBalance();

		// 计算实际付款
		SalePayDef paydef = null;
		double payje = 0;
		double sy = 0;
		if (SellType.ISEARNEST(saletype))
		{
			for (int i = 0; i < salePayment.size(); i++)
			{
				paydef = (SalePayDef) salePayment.elementAt(i);
				if (paydef.flag == '1' || paydef.flag == '4')
				{
					payje += paydef.je;
					sy += paydef.num1; // 付款方式中不记入付款的溢余部分
				}
			}
		}
		else
		{
			for (int i = 0; i < salePayment.size(); i++)
			{
				paydef = (SalePayDef) salePayment.elementAt(i);
				if (paydef.flag == '1')
				{
					payje += paydef.je;
					sy += paydef.num1; // 付款方式中不记入付款的溢余部分
				}
			}

		}
		saleHead.sjfk = ManipulatePrecision.doubleConvert(payje, 2, 1);
		salezlexception = sy; // 所有不记入付款的溢余合计,计算找零时要减出该部分

		// 计算付款余额
		// 如果付款产生损溢超过四舍五入产生的损溢则补偿了这部分，应付金额中不应再包含这部分
		if (salezlexception >= Math.abs(saleHead.sswr_sysy))
			sy = saleHead.sswr_sysy;
		else
			sy = salezlexception;

		// 当实际付款方式的价额进度符合应付价额精度时，剩余付款不进行补偿
		if (ManipulatePrecision.getDoubleScale(saleyfje) == ManipulatePrecision.getDoubleScale(saleHead.sjfk - salezlexception))
			sy = 0;
		double ye = (saleyfje - sy) - (saleHead.sjfk - salezlexception);
		if (ye < 0)
			ye = 0;

		if (ManipulatePrecision.doubleCompare(ye, GlobalInfo.sysPara.lackpayfee, 2) < 0)
			ye = 0;

		return ManipulatePrecision.doubleConvert(ye, 2, 1);
	}

	public String getSaleSfje()
	{
		if (SellType.ISEARNEST(saletype))
		{
			double virtualmoney = 0.0;
			for (int i = 0; i < salePayment.size(); i++)
			{
				SalePayDef paydef = (SalePayDef) salePayment.elementAt(i);
				if (paydef.flag == '4')
					virtualmoney += paydef.je;
			}
			return ManipulatePrecision.doubleToString(saleHead.sjfk - virtualmoney);
		}
		return super.getSaleSfje();
	}

	public boolean saleFinishDone(Label status, StringBuffer waitKeyCloseForm)
	{

		try
		{
			// 如果没有连接打印机则连接
			if (GlobalInfo.sysPara.issetprinter == 'Y' && GlobalInfo.syjDef.isprint == 'Y' && Printer.getDefault() != null && !Printer.getDefault().getStatus())
			{
				Printer.getDefault().open();
				Printer.getDefault().setEnable(true);
			}

			// 标记最后交易完成方法已开始，避免重复触发
			if (!waitlab)
				waitlab = true;
			else
				return false;

			setSaleFinishHint(status, "正在汇总交易数据,请等待.....");
			if (!saleSummary())
			{
				new MessageBox("交易数据汇总失败!");

				return false;
			}

			//
			setSaleFinishHint(status, "正在校验数据平衡,请等待.....");
			if (!AccessDayDB.getDefault().checkSaleData(saleHead, saleGoods, salePayment))
			{
				new MessageBox("交易数据校验错误!");

				return false;
			}

			// 最终效验
			if (!checkFinalStatus())
				return false;

			// 不是练习交易数据写盘
			if (!SellType.ISEXERCISE(saletype))
			{
				// 输入顾客信息
				setSaleFinishHint(status, "正在输入客户信息,请等待......");
				selectAllCustomerInfo();

				//
				setSaleFinishHint(status, "正在打开钱箱,请等待.....");
				CashBox.getDefault().openCashBox();

				//
				setSaleFinishHint(status, "正在记账付款数据,请等待.....");
				if (!saleCollectAccountPay())
				{
					new MessageBox("付款数据记账失败\n\n稍后将自动发起已记账付款的冲正!");

					// 记账失败,及时把冲正发送出去
					setSaleFinishHint(status, "正在发送冲正数据,请等待.....");
					CreatePayment.getDefault().sendAllPaymentCz();

					return false;
				}

				if (SellType.ISCOUPON(saletype) || SellType.ISCARD(saletype))
				{
					setSaleFinishHint(status, "正在对卡券进行激活,请等待......");

					StringBuffer cardinfo = new StringBuffer();
					StringBuffer zkinfo = new StringBuffer();
					String start = "", zk = "";

					int count = saleGoods.size();

					for (int i = 0; i < saleGoods.size(); i++)
					{
						SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(i);
						cardinfo.append(sgd.barcode);
						cardinfo.append("#");
						zkinfo.append(String.valueOf(sgd.hjzk));
						zkinfo.append("#");
					}

					start = cardinfo.toString().substring(0, cardinfo.toString().length() - 1);
					zk = zkinfo.toString().substring(0, zkinfo.toString().length() - 1);

					if (!((Bxmx_NetService) NetService.getDefault()).sellCardOrCoupon(SellType.ISBACK(saletype) ? "3" : "1", SellType.ISCARD(saletype) ? "0" : "1", start, "", String.valueOf(count), zk, null))
					{
						AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票,金额:" + saleHead.ysje + ",激活卡券失败", StatusType.WORK_SENDERROR);
						return false;
					}
				}
				else if (SellType.ISEARNEST(saletype))
				{
					if (!SellType.ISBACK(saletype))
					{
						this.saleHead.str2 = fetchinfo.getString();

						double wfje = 0;
						for (int i = 0; i < salePayment.size(); i++)
						{
							SalePayDef sp = (SalePayDef) salePayment.get(i);
							if (sp.flag != '4')
							{
								sp.str2 = "Y";
							}
							else
							{
								wfje = ManipulatePrecision.doubleConvert(wfje + sp.ybje);
							}
							
						}
						saleHead.num1 = wfje;

						
						// 有欠款时
						if (saleHead.num1 > 0)
						{
							saleHead.str3 = "N";

						}
						else
						{
							saleHead.str3 = "A";
						}
					}
				}
				else if (SellType.ISPREPARETAKE(saletype))
				{
					// 取货必须付全款，并且已取货
					saleHead.str3 = saleHead.billno;
				}
				
				
				setSaleFinishHint(status, "正在写入交易数据,请等待......");
				if (!AccessDayDB.getDefault().writeSale(saleHead, saleGoods, salePayment))
				{
					new MessageBox("交易数据写盘失败!");
					AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票,金额:" + saleHead.ysje + ",发生数据写盘失败", StatusType.WORK_SENDERROR);

					// 记账失败,及时把冲正发送出去
					setSaleFinishHint(status, "正在发送冲正数据,请等待.....");
					CreatePayment.getDefault().sendAllPaymentCz();

					return false;
				}

				// 小票已写盘,本次交易就要认为完成,即使后续处理异常也要返回成功
				saleFinish = true;

				// 小票保存成功以后，及时清除断点
				setSaleFinishHint(status, "正在清除断点保护数据,请等待......");
				clearBrokenData();

				setSaleFinishHint(status, "正在清除付款冲正数据,请等待......");
				if (!saleCollectAccountClear())
				{
					AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票清除冲正数据失败,但小票已成交保存", StatusType.WORK_SENDERROR);

					new MessageBox("小票已成交保存,但清除冲正数据失败\n\n请完成本笔交易后重启款机尝试删除记账冲正数据!");
				}

				// 处理交易完成后一些后续动作
				doSaleFinshed(saleHead, saleGoods, salePayment);

				// 上传当前小票
				setSaleFinishHint(status, "正在上传交易小票数据,请等待......");
				boolean bsend = GlobalInfo.isOnline;

				// 非买卡券交易小票送网
				// if (!SellType.ISCOUPON(saletype) &&
				// !SellType.ISCARD(saletype))
				// {
				// //预收提货
				/*if (SellType.ISPREPARETAKE(saletype) && saleHead.mkt.equals(srcmkt) && saleHead.str3.equals("Y"))
				{
					saleHead.mkt = this.srcmkt;
					saleHead.fphm = this.srcfphm;
					saleHead.syjh = this.srcsyjh;

					for (int i = 0; i < saleGoods.size(); i++)
					{
						SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(i);
						sgd.fphm = this.srcfphm;
						sgd.syjh = this.srcsyjh;
					}

					for (int j = 0; j < salePayment.size(); j++)
					{
						SalePayDef spd = (SalePayDef) salePayment.get(j);
						spd.fphm = this.srcfphm;
					}
				}*/

				if (!DataService.getDefault().sendSaleData(saleHead, saleGoods, salePayment))
				{
					// 联网时发送小票却失败才记录日志
					if (bsend)
					{
						AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票,金额:" + saleHead.ysje + ",联网销售时小票送网失败", StatusType.WORK_SENDERROR);
					}
				}

				// 发送当前收银状态
				setSaleFinishHint(status, "正在上传收银机交易汇总,请等待......");
				DataService.getDefault().sendSyjStatus();
				// }

				// 打印小票
				setSaleFinishHint(status, "正在打印交易小票,请等待......");
				printSaleBill();
			}
			else
			{
				if (GlobalInfo.sysPara.lxprint == 'Y')
				{
					// 打印小票
					setSaleFinishHint(status, "正在打印交易小票,请等待......");
					printSaleBill();
				}

				// 标记本次交易已完成
				saleFinish = true;
			}

			// 返回到正常销售界面
			backToSaleStatus();

			// 保存本次的小票头
			if (saleFinish && saleHead != null)
			{
				lastsaleHead = saleHead;
			}

			// 清除本次交易数据

			this.saletype = SellType.RETAIL_SALE;
			this.initNewSale();

			// 关闭钱箱
			setSaleFinishHint(status, "正在等待关闭钱箱,请等待......");
			if (GlobalInfo.sysPara.closedrawer == 'Y')
			{
				// 如果钱箱能返回状态，采用等待钱箱关闭的方式来关闭找零窗口
				if (CashBox.getDefault().canCheckStatus())
				{
					// 等待钱箱关闭,最多等待一分钟
					int cnt = 0;
					while (CashBox.getDefault().getOpenStatus() && cnt < 30)
					{
						Thread.sleep(2000);

						cnt++;
					}

					// 等待一分钟后,钱箱还未关闭，标记为要等待按键才关闭找零窗口
					if (CashBox.getDefault().getOpenStatus() && cnt >= 30)
					{
						waitKeyCloseForm.delete(0, waitKeyCloseForm.length());
						waitKeyCloseForm.append("Y");
					}
				}
				else
				{
					// 标记为要等待按键才关闭找零窗口
					waitKeyCloseForm.delete(0, waitKeyCloseForm.length());
					waitKeyCloseForm.append("Y");
				}
			}

			// 交易完成
			setSaleFinishHint(status, "本笔交易结束,开始新交易");

			// 标记本次交易已完成
			saleFinish = true;

			return saleFinish;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			new MessageBox("完成交易时发生异常:\n\n" + ex.getMessage());

			return saleFinish;
		}

	}

	public void printSaleBill()
	{
		Vector news = new Vector();
		if (((Bxmx_NetService) NetService.getDefault()).getPopNew(GlobalInfo.sysPara.mktcode, news))
		{
			String[] popnew = (String[]) news.get(0);
			StringBuffer sb = new StringBuffer();
			int len = 0;
			if (popnew != null && popnew.length > 0)
			{
				do
				{
					len = popnew[0].length();
					if (len < 35)
					{
						sb.append(popnew[0].substring(0, len) + "\n");
						break;
					}

					sb.append(popnew[0].substring(0, 35) + "\n");
					popnew[0] = popnew[0].substring(35);

				} while (true);

				saleHead.str4 = sb.toString();
			}
		}
		super.printSaleBill();
	}

	public void paySellCancel()
	{
		if (SellType.ISPREPARETAKE(saletype))
			this.initOneSale(saletype);
		else
			super.paySellCancel();
	}

	public boolean saleCollectAccountPay()
	{
		Payment p = null;
		boolean czsend = true;

		// 付款对象记账
		for (int i = 0; i < payAssistant.size(); i++)
		{
			p = (Payment) payAssistant.elementAt(i);
			if (p == null)
				continue;

			//str2表示是已付款，
			if (p.salepay.str2.equals("Y"))
				continue;

			// 第一次记账前先检查是否有冲正需要发送
			if (czsend)
			{
				czsend = false;
				if (!p.sendAccountCz())
					return false;
			}

			// 如果是预售提货则不发送付款

			if (!p.collectAccountPay())
				return false;
		}

		// 移动充值对象记账
		if (GlobalInfo.useMobileCharge && !mobileChargeCollectAccount(true))
			return false;

		return true;
	}

	public boolean checkDeleteSalePay(String string)
	{
		if (SellType.ISEARNEST(saletype) && SellType.ISBACK(saletype) && string.indexOf("00") > 0)
			return true;

		return super.checkDeleteSalePay(string);
	}
	
	// isDelete代表是否删除付款的时候
	public boolean checkDeleteSalePay(String string, boolean isDelete)
	{
		if (SellType.EARNEST_BACK.equals(saletype) && isDelete)
		{
			if (string.indexOf("未付款")>=0) return true;
		}
		
		if (SellType.ISPREPARETAKE(saletype) && isDelete)
		{
			if (string.indexOf("(预付款)")>=0)
				return true;
		}
		// TODO 自动生成方法存根
		return false;
	}

}
