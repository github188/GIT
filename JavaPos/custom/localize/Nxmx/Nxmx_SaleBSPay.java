package custom.localize.Nxmx;

import org.eclipse.swt.widgets.Label;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.CashBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentChange;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.UI.Design.BuyInfoForm;


import custom.localize.Bszm.Bszm_SaleBS;

public class Nxmx_SaleBSPay extends Bszm_SaleBS
{

	public void paySell()
	{
		try
		{
			if (GlobalInfo.sysPara.issaleby0 == 'Y' && calcHeadYfje() <= 0)
			{
				for (int i = 0; i < GlobalInfo.payMode.size(); i++)
				{
					PayModeDef pmd = (PayModeDef) GlobalInfo.payMode.get(i);

					if (pmd.ismj == 'Y' && pmd.type == '1' && pmd.iszl == 'Y')
					{
						// 创建一个付款方式对象
						Payment pay = CreatePayment.getDefault().createPaymentByPayMode(pmd, saleEvent.saleBS);

						if (pay == null)
							continue;

						// inputPay这个方法根据不同的付款方式进行重写
						SalePayDef sp = pay.inputPay("1");

						payAccount(pay, sp);

						sp.je = 0;
						sp.ybje = 0;
						calcPayBalance();
						break;
					}
				}
			}

			if (SellType.ISBACK(saletype))
			{
				BuyInfoForm backSaleForm = new BuyInfoForm();
				backSaleForm.ismustsel = true;
				backSaleForm.isshownodata = false;
				// 选择退货理由，上传到工作日志
				backSaleForm.open(new String[] { "TH" });
				String code = "";
				if (backSaleForm.selCode.size() > 0)
				{
					code = ((String[]) backSaleForm.selCode.get(0))[1];
				}
				saleHead.buyerinfo = code;
			}

			// 需要在付款时释放打印机时
			if (GlobalInfo.sysPara.issetprinter == 'Y' && GlobalInfo.syjDef.isprint == 'Y' && Printer.getDefault() != null && Printer.getDefault().getStatus())
			{
				Printer.getDefault().close();
			}

			// 暂停实时打印,避免计算促销产生的折扣分摊到商品后,商品重新正负打印
			if (GlobalInfo.sysPara.isRealPrintPOP == 'N')
				stopRealTimePrint(true);

			// 检查付款开始
			if (!paySellStart())
				return;

			// 指定小票退货进行扣回处理,扣回在付款前进行的模式
			if (GlobalInfo.sysPara.refundByPos == 'Y' && SellType.ISBACK(saletype) && !doRefundEvent())
				return;

			// 检查在付款前是否存在特殊的功能
			custMethod();

			// 辅助付款信息
			addMemoPayment();

			// 开始自动付款
			autoPay();

			// 允许下一次快捷键付款
			quickpaystart = false;

			// 通过快捷付款键进入付款窗口(在SalePayEvent中处理，避免刷新双屏广告付款信息窗口获取焦点键值)
			// if (quickpaykey != 0) NewKeyListener.sendKey(quickpaykey);

			// 打开付款窗口			
			new Nxmx_SalePayForm(saleEvent.saleform.composite).open(this, false);
			
			// 取消快捷付款键
			quickpaykey = 0;

			// 付款完成，开始新交易
			if (this.saleFinish)
			{
				sellFinishComplete();
			}
			else
			{
				// 放弃付款
				paySellCancel();
			}
		}
		finally
		{
			// 恢复实时打印
			stopRealTimePrint(false);

			// 付款结束后，重新连接打印机
			if (GlobalInfo.sysPara.issetprinter == 'Y' && GlobalInfo.syjDef.isprint == 'Y' && Printer.getDefault() != null && !Printer.getDefault().getStatus())
			{
				Printer.getDefault().open();
				Printer.getDefault().setEnable(true);
			}
		}
	}

	public boolean saleFinishDone(Label status, StringBuffer waitKeyCloseForm)
	{

		try
		{
			// 如果没有连接打印机则连接
			if (GlobalInfo.sysPara.issetprinter == 'Y' && GlobalInfo.syjDef.isprint == 'Y' && Printer.getDefault() != null
					&& !Printer.getDefault().getStatus())
			{
				Printer.getDefault().open();
				Printer.getDefault().setEnable(true);
			}

			// 标记最后交易完成方法已开始，避免重复触发
			if (!waitlab) waitlab = true;
			else return false;

/*			// 输入小票附加信息
			if (!inputSaleAppendInfo())
			{
				new MessageBox("小票附加信息输入失败,不能完成交易!");
				return false;
			}
*/
			//
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
			if (!checkFinalStatus()) { return false; }

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

				setSaleFinishHint(status, "正在写入交易数据,请等待......");
				if (!AccessDayDB.getDefault().writeSale(saleHead, saleGoods, salePayment))
				{
					new MessageBox("交易数据写盘失败!");
					AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票,金额:"+saleHead.ysje+",发生数据写盘失败", StatusType.WORK_SENDERROR);

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

				//
				setSaleFinishHint(status, "正在清除付款冲正数据,请等待......");
				if (!saleCollectAccountClear())
				{
					AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票清除冲正数据失败,但小票已成交保存", StatusType.WORK_SENDERROR);
					
					new MessageBox("小票已成交保存,但清除冲正数据失败\n\n请完成本笔交易后重启款机尝试删除记账冲正数据!");
				}

				// 上传当前小票
				setSaleFinishHint(status, "正在上传交易小票数据,请等待......");
				boolean bsend = GlobalInfo.isOnline;
				
				if (!DataService.getDefault().sendSaleData(saleHead, saleGoods, salePayment))
				{
					// 联网时发送小票却失败才记录日志
					if (bsend)
					{
						AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票,金额:"+saleHead.ysje+",联网销售时小票送网失败", StatusType.WORK_SENDERROR);
					}
				}

				
				// 发送当前收银状态
				setSaleFinishHint(status, "正在上传收银机交易汇总,请等待......");
				DataService.getDefault().sendSyjStatus();

				//小票发送成功计算返券
				setSaleFinishHint(status, "正在计算返券,请等待......");
				DataService.getDefault().getSellRealFQ(saleHead);
				
				//打开券激活窗口
				Nxmx_CouponActiveForm couponActiveForm = new Nxmx_CouponActiveForm();
				couponActiveForm.open(saleHead.memo);
				
				// 打印小票
				setSaleFinishHint(status, "正在打印交易小票,请等待......");
				printSaleBill();
			}
			else
			{
				if (GlobalInfo.sysPara.lxprint == 'Y')
				{
					//打印小票
					setSaleFinishHint(status, "正在打印交易小票,请等待......");
					printSaleBill();
				}

				//标记本次交易已完成
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
	
	public boolean payComplete()
	{
		// 检查付款是否足够
		if (!comfirmPay() || calcPayBalance() > 0 || (saleHead.sjfk <= 0 && GlobalInfo.sysPara.issaleby0 != 'Y'))
		{
			new MessageBox("付款金额不足!");
			return false;
		}

		// 付款完成处理
		if (!payCompleteDoneEvent()) return false;

		// 找零处理
		PaymentChange pc = calcSaleChange();
		if (pc == null)
		{
			// 付款完成放弃
			payCompleteCancelEvent();

			return false;
		}

		// 付款确认	
		 new Nxmx_SaleShowAccountForm(saleEvent.saleform.composite).open(this);
		 
		// 恢复状态，允许再次触发最后交易完成方法
		waitlab = false;

		// 交易未成功
		if (!saleFinish)
		{
			// 付款完成放弃
			payCompleteCancelEvent();

			// 清除找零
			pc.clearChange();
		}

		return saleFinish;
	}
	
}
