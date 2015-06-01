package custom.localize.Wqbh;


import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentCoupon;
import com.efuture.javaPos.Payment.PaymentCouponEvent;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import device.ICCard.KTL512VWQ;


public class Wqbh_PaymentCoupon extends PaymentCoupon
{
	public Wqbh_PaymentCoupon()
	{
	}

	public Wqbh_PaymentCoupon(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public Wqbh_PaymentCoupon(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}
	public boolean CreateNewjPayment(int index, double money, StringBuffer bufferStr)
	{
		try
		{
			if (money <= 0)
			{
				new MessageBox("付款金额必须大于0");

				return false;
			}

			Wqbh_PaymentCoupon cpf = new Wqbh_PaymentCoupon(paymode, saleBS);

			cpf.paymode = (PayModeDef) this.paymode.clone();
			cpf.salehead = this.salehead;
			cpf.saleBS = this.saleBS;
			cpf.couponList = this.couponList;

			cpf.mzkreq = (MzkRequestDef) mzkreq.clone();
			cpf.mzkret = (MzkResultDef) mzkret.clone();

			// ///////////////////// 创建新的付款明细对象
			// 设置券类型
			String[] rows = (String[]) couponList.elementAt(index);

			if (Convert.toInt(rows[5]) > 0)
			{
				cpf.CouponType = Convert.toInt(rows[5]);
			}

			cpf.mzkreq.memo = rows[0];
			cpf.mzkret.ye = Convert.toDouble(rows[2]);

			if ((GlobalInfo.sysPara.fjkkhhl != null) && (GlobalInfo.sysPara.fjkkhhl.length() > 0) && saleBS.isRefundStatus() && !SellType.ISCOUPON(saleBS.saletype))
			{
				String[] lines = null;
				if (GlobalInfo.sysPara.fjkkhhl.indexOf(";") >= 0)
					lines = GlobalInfo.sysPara.fjkkhhl.split(";");
				else if (GlobalInfo.sysPara.fjkkhhl.indexOf("|") >= 0)
					lines = GlobalInfo.sysPara.fjkkhhl.split("\\|");

				if (lines == null)
					lines = new String[] { GlobalInfo.sysPara.fjkkhhl };

				if (lines != null)
				{
					int i = 0;

					for (i = 0; i < lines.length; i++)
					{
						String l = lines[i];

						if (l.indexOf(",") > 0)
						{
							String cid = l.substring(0, l.indexOf(","));

							if (cid.equals(rows[0]))
							{
								cpf.paymode.hl = Convert.toDouble(l.substring(l.indexOf(",") + 1));

								break;
							}
						}
					}

					if (i >= lines.length)
					{
						cpf.paymode.hl = Convert.toDouble(rows[3]);
					}
				}
			}
			else
			{
				cpf.paymode.hl = Convert.toDouble(rows[3]);
			}
			cpf.allowpayje = this.allowpayje;

			// 查询并删除原付款
			// 如果是退货且非扣回时，不删除原付款方式
			if (!(SellType.ISBACK(salehead.djlb) && !saleBS.isRefundStatus()) || GlobalInfo.sysPara.isBackPaymentCover == 'Y')
			{
				if (!deletePayment(index, cpf))
				{
					new MessageBox("删除原付款方式失败！");

					return false;
				}
			}
			/*
			if (!(SellType.ISBACK(salehead.djlb) && !saleBS.isRefundStatus()) && !deletePayment(index, cpf))
			{
				(GlobalInfo.sysPara.isBackPaymentCover == 'N')
				new MessageBox("删除原付款方式失败！");

				return false;
			}
			*/

			if (this.allowpayje >= 0 && money > this.allowpayje && paymode.isyy != 'Y')
			{
				new MessageBox("该付款方式最多允许付款 " + ManipulatePrecision.doubleToString(allowpayje) + " 元");

				return false;
			}

			double yy = 0;
			if (yyje > 0 && sjje > 0)
			{
				double min = Math.min(ManipulatePrecision.doubleConvert(sjje / cpf.paymode.hl), cpf.allowpayje);
				if (sjje > 0 && money > min)
				{
					new MessageBox("最大可退金额为: " + min);
					return false;
				}

				if (GlobalInfo.sysPara.oldqpaydet == 'A')
				{
					StringBuffer buf = new StringBuffer();
					buf.append(ManipulatePrecision.doubleToString(money + (yyje / cpf.paymode.hl)));
					TextBox txt = new TextBox();
					txt.open("请输入券面值", "券面值", "实际付款为:" + ManipulatePrecision.doubleToString(money) + "\n最大券面值为:" + ManipulatePrecision.doubleToString(money + (yyje / cpf.paymode.hl)), buf, 0, ManipulatePrecision.doubleConvert(money + (yyje / cpf.paymode.hl)), true, TextBox.DoubleInput, -1);
					double yfk = money;
					money = Convert.toDouble(buf.toString());
					if (money > yfk)
						yy = ManipulatePrecision.doubleConvert(money - yfk);
				}
				else
				{
					StringBuffer buf = new StringBuffer();
					// buf.append(ManipulatePrecision.doubleToString((yyje/cpf.paymode.hl)));
					TextBox txt = new TextBox();
					txt.open("请输入此券益余金额", "益余金额", "实际付款为:" + ManipulatePrecision.doubleToString(money) + "\n最大益余金额为:" + ManipulatePrecision.doubleToString((yyje / cpf.paymode.hl)), buf, 0, ManipulatePrecision.doubleConvert((yyje / cpf.paymode.hl)), true, TextBox.DoubleInput, -1);

					if (Convert.toDouble(buf.toString()) > 0)
						yy = Convert.toDouble(buf.toString());
				}
			}
			// 创建付款对象
			if (cpf.createSalePay(String.valueOf(money + yy)))
			{
				// 设置付款方式名称
				cpf.salepay.payname = rows[1];
				if (yy > 0)
					cpf.salepay.num1 = ManipulatePrecision.doubleConvert(yy * cpf.salepay.hl);

				// 增加已付款
				if (SellType.ISBACK(saleBS.saletype) && saleBS.isRefundStatus())
				{
					cpf.salepay.payname += "扣回";
					saleBS.addSaleRefundObject(cpf.salepay, cpf);

				}
				else
				{
					saleBS.addSalePayObject(cpf.salepay, cpf);
				}

				alreadyAddSalePay = true;

				// 记录当前付款方式
				rows[4] = String.valueOf(cpf.salepay.num5);

				addMessage(cpf, bufferStr);

				// 开始分摊到各个商品
				paymentApportion(cpf.salepay, cpf, false);

				if (GlobalInfo.sysPara.oldqpaydet != 'N' && sjje > 0 && yyje > 0)
				{
					isCloseShell = true;
				}

				return true;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return false;
	}
	protected boolean setRequestDataByAccount()
	{
		//得到消费序号
		long seqno = getMzkSeqno();
		if (seqno <= 0) return false;
		
		// 打消费交易包
		mzkreq.seqno = seqno;
		mzkreq.je = salepay.ybje;
		mzkreq.syjh = ConfigClass.CashRegisterCode;
		mzkreq.mktcode = GlobalInfo.sysPara.mktcode;
		String oldinfo = "";
		if (salehead.ysyjh != null && salehead.yfphm != null && salehead.ysyjh.length() > 0 && salehead.yfphm.length() > 0)	oldinfo = "," + salehead.ysyjh + "," + salehead.yfphm;
		mzkreq.syyh = GlobalInfo.posLogin.gh + oldinfo;	// 退券时需要找到原批次 所以需要上传原小票号和原收银机号
		mzkreq.paycode = salepay.paycode;
		mzkreq.invdjlb = ((salehead != null ) ? salehead.djlb : "");
		
		// 告诉后台过程磁道信息是存放的是卡号,只采用卡号记账方式,不使用磁道记账方式
		mzkreq.track1 = "CARDNO";
		mzkreq.track2 = salepay.payno;
		
		return true;
	}
	
	public void specialDeal(PaymentCouponEvent event)
	{
		try
		{
			if (msrInputType != 3) return;
			
			String track2 = "";
			
			ProgressBox pb = null;
			pb = new ProgressBox();
			pb.setText("正在输入卡号和密码,请等待...");
			track2 = new KTL512VWQ().findCard();
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
			
			event.msrRead(null, "", track2, "");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			event.shell.close();
			event.shell.dispose();
			event.shell = null;
		}
	}
//	 如果为扣回，查询时用零售作为交易类型
	public void setRequestDataByFind(String track1, String track2, String track3)
	{

		// 根据磁道生成查询请求包
		mzkreq.type = "05"; // 查询类型
		mzkreq.seqno = 0;
		mzkreq.termno = ConfigClass.CashRegisterCode;
		mzkreq.mktcode = GlobalInfo.sysPara.mktcode;
		mzkreq.syyh = GlobalInfo.posLogin.gh;
		mzkreq.syjh = ConfigClass.CashRegisterCode;
		mzkreq.fphm = GlobalInfo.syjStatus.fphm;
		mzkreq.invdjlb = ((salehead != null) ? salehead.djlb : "");
		mzkreq.paycode = ((paymode != null) ? paymode.code : "");
		mzkreq.je = 0;
		mzkreq.track1 = track1;
		mzkreq.track2 = track2;
		mzkreq.track3 = track3;
		mzkreq.passwd = "";
		mzkreq.memo = "";
	

		if (saleBS != null && saleBS.isRefundStatus())
		{
			// 扣回时通过termno为4来判断扣回
			mzkreq.termno = "4";
			//mzkreq.invdjlb = "1";
		}
	}
}
