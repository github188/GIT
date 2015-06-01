package custom.localize.Szxw;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Payment.Bank.NjysXw_PaymentBankFunc;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bhls.Bhls_SaleBS;

public class Szxw_SaleBS extends Bhls_SaleBS
{
	public boolean paymentApportion(SalePayDef spay, Payment payobj)
	{
		return false;
	}

	public boolean paySellStart()
	{
		// 付款时如果没刷会员卡先提示刷卡
		if (saletype .equals( SellType.RETAIL_SALE) && !checkMemberSale())
		{
			saleEvent.memberGrant();
		}

		// 定金退订输入原定金单号
		if (SellType.ISEARNEST(this.saletype) && SellType.ISBACK(this.saletype))
		{
			StringBuffer sb = new StringBuffer();
			do
			{
				if (new TextBox().open("请输入要退订的定金单号", "定金单号", "定金单金额必须与退订金额 " + ManipulatePrecision.doubleToString(this.saleyfje) + " 元一致", sb, 0, 0, false))
				{
					PayModeDef paymode = DataService.getDefault().searchMainPayMode("0411");
					PaymentMzk mzk = CreatePayment.getDefault().getPaymentMzk();
					mzk.initPayment(paymode, this);
					if (!mzk.findMzk("", sb.toString(), "")) continue;
					if (ManipulatePrecision.doubleCompare(mzk.mzkret.ye,this.saleyfje,2) != 0)
					{
						new MessageBox("[" + sb.toString() + "]定金单金额为 " +mzk.mzkret.ye + "\n\n与当前退订金额不一致!");
						continue;
					}
					this.saleHead.jdfhdd = sb.toString();
					break;
				}
				else
				{
					return false;
				}
			} while (true);
		}

		return super.paySellStart();
	}

	public void paySell()
	{
		// 通过付款键进入付款窗口则默认要输入付款代码
		if (quickpaykey == 0) quickpaykey = GlobalVar.Pay;

		super.paySell();
	}

	public String[] rowInfo(SaleGoodsDef goodsDef)
	{
		String[] detail = new String[8];
		detail[1] = goodsDef.barcode;
		detail[2] = goodsDef.name;
		detail[3] = goodsDef.unit;
		detail[4] = ManipulatePrecision.doubleToString(goodsDef.sl, 4, 1, true);
		detail[5] = ManipulatePrecision.doubleToString(goodsDef.jg);
		double zk = goodsDef.hjzk - goodsDef.lszzk - goodsDef.lszzr;
		detail[6] = ManipulatePrecision.doubleToString(zk)
				+ ((zk > 0) && (goodsDef.hjje - zk > 0) ? "(" + ManipulatePrecision.doubleToString((goodsDef.hjje - zk) / goodsDef.hjje * 100, 0, 1)
						+ "%)" : "");
		detail[7] = ManipulatePrecision.doubleToString(goodsDef.hjje - goodsDef.hjzk, 2, 1);

		return detail;
	}

	public String getTotalPayMoneyLabel()
	{
		if (saleGoods.size() > 0)
		{
			String s = "";
			double zzk = 0;
			for (int i = 0; i < saleGoods.size(); i++)
			{
				SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);
				zzk += sg.lszzk + sg.lszzr;
			}
			if (zzk > 0)
			{
				s = ManipulatePrecision.doubleToString(zzk) + "("
						+ ManipulatePrecision.doubleToString(saleHead.ysje / (saleHead.ysje + zzk) * 100, 0, 1) + "%) ";
			}
			return s + ManipulatePrecision.doubleToString(saleyfje);
		}
		else
		{
			return "";
		}
	}

	public boolean isSpecifyTicketBack()
	{
		// 定金退订(预销售)总是不指定小票退
		if (SellType.ISEARNEST(this.saletype)) return false;

		return super.isSpecifyTicketBack();
	}

	public boolean saleCollectAccountPay()
	{
		// 先删除付款集合中的DJQF行
		for (int i = 0; i < salePayment.size(); i++)
		{
			if (((SalePayDef) salePayment.elementAt(i)).paycode.equals("DJQF"))
			{
				salePayment.remove(i);
				i--;
			}
		}

		// 发送定金单签发交易
		PaymentMzk pay = null;
		SalePayDef spay = null;
		if (SellType.ISEARNEST(saleHead.djlb))
		{
			spay = new SalePayDef();
			spay.syjh = saleHead.syjh;
			spay.fphm = saleHead.fphm;
			spay.paycode = "DJQF";
			spay.payname = "定金签发";
			spay.flag = '1';
			spay.ybje = saleHead.ysje;
			spay.hl = 1;
			spay.je = ManipulatePrecision.doubleConvert(spay.ybje * spay.hl, 2, 1);
			spay.payno = "";
			spay.batch = "";
			spay.kye = 0;

			pay = CreatePayment.getDefault().getPaymentMzk();
			pay.initPayment(spay, saleHead);

			//记录签发定金单的柜组
			String gz = ((SaleGoodsDef) saleGoods.elementAt(0)).gz;

			//上传定金单签发付款代码如果是卡付款类型传入卡号
			SalePayDef salePayDef = (SalePayDef) salePayment.elementAt(0);
			if (salePayment.size() == 1)
			{
				if (salePayDef.payno != null)
				{
					pay.mzkreq.memo = salePayDef.paycode + "," + salePayDef.payno + "," + gz;
				}
				else
				{
					pay.mzkreq.memo = salePayDef.paycode + ",," + gz;
				}
			}
			else 
			{
				pay.mzkreq.memo = salePayDef.paycode + ", ," + gz;
			}
			// 加入付款列表
			addSalePayObject(spay, pay);

			// 定金单退订
			if (SellType.ISBACK(saleHead.djlb))
			{
				spay.payname = "定金退订";
				spay.payno = saleHead.jdfhdd; // 原定金签发的定金单号
				if (spay.payno == null || spay.payno.trim().equals(""))
				{
					new MessageBox("定金退订找不到原定金单号\n\n定金单退订必须指定原签发交易退订");
					return false;
				}
			}
		}

		// 付款记账
		if (!super.saleCollectAccountPay()) return false;

		// 返回的定金单号记录到jdfhdd
		if (SellType.ISEARNEST(saleHead.djlb))
		{
			// mzkret.cardno返回签发的定金单号
			saleHead.jdfhdd = pay.mzkret.cardno;
			if (spay.payno == null || spay.payno.equals("")) spay.payno = pay.mzkret.cardno;

			// 没有签发定金单号
			if (saleHead.jdfhdd == null || saleHead.jdfhdd.trim().equals(""))
			{
				new MessageBox("定金签发未生成有效定金单号\n\n定金签发失败");
				return false;
			}
		}

		return true;
	}

	public int payButtonToPayModePosition(int key)
	{
		int k = -1;
		PayModeDef paymode = null;

		for (k = 0; k < GlobalInfo.payMode.size(); k++)
		{
			paymode = (PayModeDef) (GlobalInfo.payMode.elementAt(k));
			if (key == GlobalVar.CustomKey1 && paymode.code.equals("0411")) break;
		}

		//
		if (k >= GlobalInfo.payMode.size())
		{
			return super.payButtonToPayModePosition(key);
		}
		else
		{
			return k;
		}
	}

	public void execCustomKey1(boolean keydownonsale)
	{
		if (keydownonsale)
		{
			sendQuickPayButton(GlobalVar.CustomKey1);
		}
		else
		{
			// 定位付款方式
			int last = this.payButtonToPayModePosition(GlobalVar.CustomKey1);
			if (last >= 0)
			{
				salePayEvent.gotoPayModeLocation(last);
			}
		}
	}

	public void execCustomKey4(boolean keydownonsale)
	{
		try
		{
			NjysXw_PaymentBankFunc func = new NjysXw_PaymentBankFunc();
			func.callBankFunc(PaymentBank.XYKJZ, 0, "", "", "", "", "", "", null);
		}
		catch (Exception er)
		{
			er.printStackTrace();
			return;
		}
	}

	//定义客户键3位定金签发
	public void execCustomKey3(boolean keydownonsale)
	{
		try
		{
			GlobalInfo.saleform.setSaleType(SellType.EARNEST_SALE);
		}
		catch (Exception er)
		{
			er.printStackTrace();
			return;
		}
	}

	public void exchangeSale()
	{
		if (saletype .equals( SellType.RETAIL_SALE) || saletype .equals( SellType.RETAIL_BACK))
		{
			//记录按下换货键之前的换货标记状态
			char oldHhflag = hhflag;
			if (hhflag == 'N')
			{
				hhflag = 'Y';
			}
			else
			{
				hhflag = 'N';
			}

			// 销售状态按换货键，直接先进行换货退货  
			String type = saletype;
			if (saletype .equals( SellType.RETAIL_SALE )&& hhflag == 'Y')
			{
				type = SellType.RETAIL_SALE;
			}

			// 如果初始化失败，还原换货标记
			if (!saleEvent.saleform.setSaleType(type))
			{
				hhflag = oldHhflag;
			}
		}
		else
		{
			new MessageBox("当前状态不能进行换货交易!");
		}
	}

	public boolean payAccount(PayModeDef mode, String money)
	{
		//定金单发定，只能使用一种付款方式
		if (SellType.ISEARNEST(this.saletype))
		{
			//不能用定金单或券付定金单
			if (mode.code.equals("0411") || mode.type == '5')
			{
				new MessageBox("发售定金单不能用" + mode.name + "的付款方式");
				return false;
			}
			
			if (salePayment.size() > 0)
			{
				SalePayDef salePayDef = (SalePayDef)salePayment.elementAt(salePayment.size() - 1);
				
				if (!mode.code.equalsIgnoreCase(salePayDef.paycode))
				{
					new MessageBox("发售定金单必须使用同一种付款方式");
					return false;
				}
			}
//			if (salePayment.size() >= 1)
//			{
//				new MessageBox("发售定金单必须使用同一种付款方式");
//				return false;
//			}
		}

		//不能退货退到定金单
		if (SellType.ISBACK(this.saletype))
		{
			if (mode.code.equals("0411"))
			{
				new MessageBox("不能用定金单方式退货");
				return false;
			}
		}

		return super.payAccount(mode, money);
	}

	public void backToSaleStatus()
	{
		super.backToSaleStatus();
		
		// 定金签发状态恢复到正常销售状态
		if (SellType.ISEARNEST(saletype))
		{
			saletype = SellType.RETAIL_SALE;
		}
	}

	public double getMaxSaleMoney()
	{
		return 99999999.99;
	}

	public double getMaxSaleGoodsMoney()
	{
		return 99999999.99;
	}

	public String getFuncMenuByPaying()
	{
		// 付款窗口打开功能菜单只允许使用以下功能
		// 0806 信用卡余额查询
		// 0807 信用卡签购单重打
		// 0808 信用卡交易查询
		return "0008,0805";
	}

	public String getSyyInfoLabel()
	{
		if (GlobalInfo.posLogin != null) return GlobalInfo.posLogin.name;
		else return super.getSyyInfoLabel();
	}
	
	public double calcPayBalance()
	{
		// 如果是扣回付款,付款余额为扣回余额
		if (isRefundStatus()) return calcRefundBalance();

		// 计算实际付款
		SalePayDef paydef = null;
		double payje = 0;
		double sy = 0;
		for (int i = 0; i < salePayment.size(); i++)
		{
			paydef = (SalePayDef) salePayment.elementAt(i);
			if (!(paydef.paycode.equals("DJQF"))){
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
		if (salezlexception >= Math.abs(saleHead.sswr_sysy)) sy = saleHead.sswr_sysy;
		else sy = salezlexception;

		// 当实际付款方式的价额进度符合应付价额精度时，剩余付款不进行补偿
		if (ManipulatePrecision.getDoubleScale(saleyfje) == ManipulatePrecision.getDoubleScale(saleHead.sjfk - salezlexception)) sy = 0;
		double ye = (saleyfje - sy) - (saleHead.sjfk - salezlexception);
		if (ye < 0) ye = 0;

		if (ManipulatePrecision.doubleCompare(ye, GlobalInfo.sysPara.lackpayfee, 2) < 0) ye = 0;
		//if (ye < GlobalInfo.sysPara.lackpayfee) ye = 0;
		
		return ManipulatePrecision.doubleConvert(ye, 2, 1);
	}

}
