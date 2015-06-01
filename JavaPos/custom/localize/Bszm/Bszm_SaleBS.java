package custom.localize.Bszm;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import org.eclipse.swt.widgets.Label;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.CashBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.AccessRemoteDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Bszm_SaleBS extends Bszm_SaleBS2Goods
{
	public void writeSellObjectToStream(ObjectOutputStream s) throws Exception
	{
		super.writeSellObjectToStream(s);

		s.writeObject(popinfo);
	}

	public void readStreamToSellObject(ObjectInputStream s) throws Exception
	{
		super.readStreamToSellObject(s);
		Vector popinfo1 = (Vector) s.readObject();
		popinfo = popinfo1;
	}

	public boolean clearSell(int index)
	{
		if (super.clearSell(index))
		{
			if (popinfo != null && popinfo.size() > 0)
				popinfo.removeAllElements();
			return true;
		}
		return false;
	}

	public boolean payCompleteDoneEvent()
	{
		
		// 扣回处理
		if (SellType.ISBACK(saletype))
		{
			if (GlobalInfo.sysPara.backpayctrl == 'Y' && super.salePayment.size() > 0)
			{
				double sfje = 0.0;
				
				for (int i = 0; i < salePayment.size(); i++)
				{
					SalePayDef paydef = (SalePayDef) salePayment.elementAt(i);
					sfje += (paydef.ybje * paydef.hl);
				}

				if (sfje != super.saleHead.ysje)
				{
					new MessageBox("当前退货不允许实收金额大于应付金额");
					return false;
				}
			}

			if (GlobalInfo.sysPara.refundByPos == 'Y') // 扣回在付款前进行的模式
			{
				// 添加扣回明细到付款明细
				addRefundToSalePay();
			}
			else if (GlobalInfo.sysPara.refundByPos == 'B') // 扣回在付款后进行的模式
			{
				// 执行扣回处理
				if (!doRefundEvent())
					return false;
			}
		}
		return true;
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

			// 输入小票附加信息
			if (!inputSaleAppendInfo())
			{
				new MessageBox("小票附加信息输入失败,不能完成交易!");
				return false;
			}

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

				//
				setSaleFinishHint(status, "正在清除付款冲正数据,请等待......");
				if (!saleCollectAccountClear())
				{
					AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票清除冲正数据失败,但小票已成交保存", StatusType.WORK_SENDERROR);

					new MessageBox("小票已成交保存,但清除冲正数据失败\n\n请完成本笔交易后重启款机尝试删除记账冲正数据!");
				}
				
				// 上传小票前打印小票
				if (GlobalInfo.sysPara.whenprintbill == 'N')
				{
					// 需要联网实时计算积分
					if (GlobalInfo.sysPara.calcjfbyconnect == 'Y' || GlobalInfo.sysPara.calcjfbyconnect == 'A')
						((Bszm_DataService)DataService.getDefault()).getCustomerSellJf(saleHead, saleGoods, salePayment);
					
					// 打印小票
					setSaleFinishHint(status, "正在打印交易小票,请等待......");
					printSaleBill();
				}

				//上传小票到JSTORE
				sendInvice(saleHead, saleGoods, salePayment);
				
				if (GlobalInfo.sysPara.localfind == 'N')
				{
					// 当未启用本地优先时,则上传当前小票到POS库
					setSaleFinishHint(status, "正在上传交易小票数据,请等待......");
					boolean bsend = GlobalInfo.isOnline;
					if (!DataService.getDefault().sendSaleData(saleHead, saleGoods, salePayment))
					{
						// 联网时发送小票却失败才记录日志
						if (bsend)
						{
							AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票,金额:" + saleHead.ysje + ",联网销售时小票送网失败", StatusType.WORK_SENDERROR);
						}
					}
				}
				else
				{
					//记录未传任务
					AccessLocalDB.getDefault()
		            .writeTask(StatusType.TASK_SENDINVOICE,
		                       TaskExecute.getKeyTextByBalanceDate());
				}
				
				
				

				// 发送当前收银状态
				setSaleFinishHint(status, "正在上传收银机交易汇总,请等待......");
				DataService.getDefault().sendSyjStatus();

				if (GlobalInfo.sysPara.whenprintbill == 'Y')
				{
					// 打印小票
					setSaleFinishHint(status, "正在打印交易小票,请等待......");
					printSaleBill();
				}
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
	
	private void sendInvice(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
	{
		try
		{			

			//判断是否向JSTORE传小票
			if (!ConfigClass.DataBaseEnable.equals("Y"))
			{
				return;
			}

			boolean isConnection=true;
			//检查是否能连接JSTORE库
			if (!AccessRemoteDB.getDefault().isConnection(false)) isConnection = false;
			
			int result = -1;
			if (isConnection)
			{
				//发送小票到JSTORE
		        result = AccessRemoteDB.getDefault().writeSale(saleHead, saleGoods, salePayment);
			}
			if (isConnection==false || result!=0)
			{
				//当无连接或上传失败时,则记任务
				AccessLocalDB.getDefault().writeTask(StatusType.TASK_SENDSALETOJSTORE, GlobalInfo.balanceDate + "," + saleHead.fphm);
			}
	        
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public double calcSaleBCJF()
	{
		if (curCustomer == null || curCustomer.isjf != 'Y') return 0.00;
		
		double bcjf = 0;
		double nJfJe = 0;//不参与积分的付款金额
		try
		{
			if (GlobalInfo.sysPara.jfPayCodeList == null || GlobalInfo.sysPara.jfPayCodeList.trim().length()<=0) return 0;
			//GlobalInfo.sysPara.jfPayCodeList="01,02,03,05,90";//01,02,03,04,05,90 //test
			SalePayDef paydef;
			for (int i = 0; i < salePayment.size(); i++)
			{
				paydef = (SalePayDef) salePayment.elementAt(i);
				if (paydef==null) continue;
				
				if (GlobalInfo.sysPara.jfPayCodeList.indexOf(paydef.paycode)<0) nJfJe += (paydef.ybje * paydef.hl);					
				
			}
			bcjf = saleHead.sjfk - saleHead.zl - saleHead.fk_sysy - nJfJe;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return ManipulatePrecision.doubleConvert(bcjf, 0, 0);//calcSaleLJJF();
	}
}
