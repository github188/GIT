package custom.localize.Bjkl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

/**
 * 
 * 0112零钞转存 0113折扣帐户 0413 储值/会员帐户
 */
public class Bjkl_PaymentCustMzk extends PaymentMzk
{
	protected static SaleReqDef saleReq = null;
	protected boolean isHand = false;

	public Bjkl_PaymentCustMzk()
	{
	}

	public Bjkl_PaymentCustMzk(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	// 该构造函数用于红冲小票时,通过小票付款明细创建对象
	public Bjkl_PaymentCustMzk(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public SalePayDef inputPay(String money)
	{
		if (!GlobalInfo.isOnline)
		{
			new MessageBox("脱网无法使用会员卡支付功能!");
			return null;
		}

		if (SellType.ISBACK(saleBS.saletype))
		{
			new MessageBox("不允许使用该付款方式退货!");
			return null;
		}

		return super.inputPay(money);
	}

	public boolean setCustomerInfo()
	{
		if (saleBS.curCustomer != null)
		{
			setRequestDataByFind("", saleBS.curCustomer.code, "");

			mzkret.cardno = saleBS.curCustomer.code;
			mzkret.cardpwd = saleBS.curCustomer.track;
			mzkret.cardname = saleBS.curCustomer.name;
			mzkret.ye = saleBS.curCustomer.value1; // 零钞余额
			mzkret.money = saleBS.curCustomer.value2; // 最大零钞余额
		}

		return true;
	}

	public double getRebateAmount()
	{
		double amount = 0.0;
		for (int i=0; i<saleBS.salePayment.size(); i++)
		{
			SalePayDef spd = (SalePayDef)saleBS.salePayment.get(i);
			if(spd.paycode.equals("0113"))
			{
				amount =  spd.je;
				break;
			}
		}
		return amount;
	}
	protected boolean needFindAccount()
	{
		if (saleBS.curCustomer != null && saleBS.curCustomer.code.startsWith("0618") && paymode.code.equals("0112"))
			return false;

		return true;
	}

	public void afterFormOpenDoEvent()
	{

	}

	public boolean autoCreateAccount()
	{
		setCustomerInfo();

		// 查询是否已刷此卡
		if (saleBS.existPayment(paymode.code, mzkret.cardno) >= 0)
		{
			boolean ret = false;
			if (new MessageBox(Language.apply("此卡已进行付款,你要取消原付款重新输入吗？"), null, true).verify() == GlobalVar.Key1)
			{
				ret = true;
				int n = -1;
				do
				{
					n = saleBS.existPayment(paymode.code, mzkret.cardno);
					if (n >= 0)
					{
						if (!saleBS.deleteSalePay(n))
						{
							ret = false;
							break;
						}
					}
				}
				while (n >= 0);

				// 重新刷新付款余额及已付款列表
				saleBS.salePayEvent.refreshSalePayment();
			}
			if (!ret)
			{
				new MessageBox(Language.apply("此卡已经付款，请先删除原付款"));
				return ret;
			}
		}
		return true;
	}

	protected boolean createRebatePay(String money)
	{
		try
		{
			// 检查金额是否有效
			double ye = saleBS.calcPayBalance();

			if (checkMoneyValid(money, ye))
			{
				if (!createSalePayObject(money))
					return false;

				return true;
			}
		}
		catch (Exception ex)
		{
			new MessageBox(Language.apply("生成付款对象出现异常\n\n") + ex.getMessage());
			ex.printStackTrace();
		}

		//
		salepay = null;
		return false;
	}

	// money为空代表自动创建
	public boolean createSalePay(String money)
	{

		// 用于积分和折扣帐户
		if (saleBS.curCustomer != null)// && paymode.code.equals("0112"))
		{
			
			//money.equals("0")用于控制刷了会员，但无会员消费的情况
			if (money.equals("0"))
			{
				if (createSalePayObject(money))
				{
					salepay.payno = saleBS.curCustomer.code;
					salepay.idno = saleBS.curCustomer.track;
					return true;
				}
			}
			// 折扣帐户为0时，不创建付款
			else if (saleBS.curCustomer.code.startsWith("0618") && money.equals(""))
			{
				if (saleBS.curCustomer.valnum1 <= 0.00)
					return false;

				money = String.valueOf(Math.min(saleBS.calcPayBalance(), saleBS.curCustomer.valnum1));
				if (createRebatePay(money))
				{
					salepay.payno = saleBS.curCustomer.code;
					salepay.idno = saleBS.curCustomer.track;
					mzkret.ye = saleBS.curCustomer.valnum1; //记录卡余额
					saleBS.curCustomer.valnum1 = ManipulatePrecision.doubleConvert(saleBS.curCustomer.valnum1 - Convert.toDouble(money), 2, 1);
//					salepay.kye = saleBS.curCustomer.valnum1; 
					salepay.isused = 'Y'; // 标记使用折扣账户余额消费
					return true;
				}
			}
		}
		/*
		 * else
		 * {
		 * if (super.createSalePay(money))
		 * {
		 * salepay.payno = saleBS.curCustomer.code;
		 * salepay.idno = saleBS.curCustomer.track;
		 * saleBS.curCustomer.value1 =
		 * ManipulatePrecision.doubleConvert(saleBS.curCustomer.value1 -
		 * Convert.toDouble(money), 2, 1);
		 * }
		 * 
		 * return true;
		 * }
		 * 
		 * }
		 */

		// vip储值卡记账
		return super.createSalePay(money);
	}

	public boolean writeMzkCz()
	{
		return true;
	}

	public boolean realAccountPay()
	{
		setRequestDataByAccount();
		
		if (saleReq != null)
			return sendData();

		salepay.payno = mzkret.cardno; // 记录卡号
		salepay.idno = mzkret.cardpwd; // 记录密码
//		salepay.kye = mzkret.ye - mzkreq.je;

		return true;
	}

	protected void saveAccountMzkResultToSalePay()
	{
		// batch标记本付款方式已记账,这很重要
		salepay.batch = String.valueOf(mzkreq.seqno);

		// 标记记账返回的卡号
		if (!CommonMethod.isNull(mzkret.cardno))
			salepay.payno = mzkret.cardno;

		// new MessageBox("salepay.kye="+salepay.kye+" mzkret.ye="+mzkret.ye);
		// 后台退货时没有刷卡所以记录后台返回的卡余额,或者记账过程返回了最终余额
		if (salepay.kye <= 0 || (mzkret.status != null && mzkret.status.equals("RETURNYE")))
		{
			salepay.kye = mzkret.ye;
		}
		else
		{
			// 记账过程没有返回最终余额，以查询的余额做基准加减计算新余额
			if (mzkreq.type == "01")
				salepay.kye -= mzkreq.je;
			else
				salepay.kye += mzkreq.je;
		}
		salepay.kye = ManipulatePrecision.doubleConvert(salepay.kye);
		// new MessageBox("salepay.kye="+salepay.kye);
		// 更新付款断点数据，标记为已付款状态,否则在记账以后如果掉电,断点读入的还是未记账状态
		if (this.saleBS != null)
			this.saleBS.writeBrokenData();
	}
	
	public void showAccountYeMsg()
	{

	}

	public boolean findMzk(String track1, String track2, String track3)
	{
		// 1.判断传入的磁道信息是否为空
		if (track2 == null || track2.trim().equals(""))
			return false;

		// 8位储值卡
		if (track2.length() == 8)
		{
			new MessageBox("该卡不能用于消费!");
			return false;
		}

//		if (track2.trim().length() > 16)
//		{
//			// 2.判磁道读取的卡号和卡上印刷的卡号是否一致（通过收银员输入卡号6位效验)
//			StringBuffer sb = new StringBuffer();
//			TextBox txt = new TextBox();
//			if (!txt.open(Language.apply("请输入卡号后 6 位"), Language.apply("卡号效验"), Language.apply("请输入卡号后 6 位"), sb, 0, 0, false, 3))
//				return false;
//
//			String check = txt.Track2.trim();
//			if (check.equals("") || !check.equals(track2.substring(10, 16)))
//			{
//				new MessageBox("效验卡号失败!");
//				return false;
//			}
//		}

		// 手工输入的会员号
		if (track2.length() == 13 && "995".equals(track2.substring(1, 4)))
			isHand = true; // 标识是手输的会员卡

		return super.findMzk(track1, track2, track3);
	}

	public boolean findMzkInfo(String track1, String track2, String track3)
	{
		// 查询面值卡
		if ((track2.length() == 24 && !track2.startsWith("0618")) || track2.length() == 8)
		{
			mzkreq.track2 = track2;
			return CardModule.getDefault().getMzkInfo(mzkreq, mzkret);
		}

		return false;
	}

	public boolean extendAction1()
	{
		if (saleReq == null)
			saleReq = new SaleReqDef();

	
		if (!saleReq.isinit)
		{
			//总金额中除去折扣帐户金额
			double realamount = ManipulatePrecision.doubleConvert(saleBS.saleHead.ysje-getRebateAmount(),2,1);
			saleReq.initData(saleBS.saleHead,SellType.SELLSIGN(saleBS.saletype) * realamount);
		}

		if (isHand || (saleBS.curCustomer != null))
		{
			// 手工输入只用于积分
			if (isHand)
			{
				saleReq.DISUSEMONEY = Convert.increaseCharForward("0.00", '0', 8);
				saleReq.MONEY += 0.0;
				saleReq.CARDSALEITEM = saleReq.CARDSALEITEM + Convert.increaseCharForward(saleBS.curCustomer.code, '0', 16) + Convert.increaseCharForward(saleBS.curCustomer.track, '0', 8) + Convert.increaseCharForward(0.00 + "", '0', 8);
			}
			else
			{
				// if (mzkreq.track2 == null || mzkreq.track2.trim().equals(""))
				// {
				// saleReq.DISUSEMONEY = Convert.increaseCharForward("0.00" +
				// "", '0', 8);
				// saleReq.MONEY += 0;
				// saleReq.CARDSALEITEM = saleReq.CARDSALEITEM +
				// Convert.increaseCharForward(saleBS.curCustomer.code, '0', 16)
				// + Convert.increaseCharForward(saleBS.curCustomer.track, '0',
				// 8) + Convert.increaseCharForward(0.00 + "", '0', 8);
				// }
				// else
				if (saleBS.curCustomer != null && salepay != null && salepay.isused == 'Y')
				{
					saleReq.DISUSEMONEY = Convert.increaseCharForward(salepay.je + "", '0', 8);
					// saleReq.CARDSALEITEM = saleReq.CARDSALEITEM +
					// Convert.increaseCharForward(salepay.payno, '0', 16) +
					// Convert.increaseCharForward(salepay.idno, '0', 8) +
					// Convert.increaseCharForward("", '0', 8);
				}
				// 找零支付方式
				else if (salepay.memo.equals("3"))
				{
					double charge = Math.abs(salepay.je);
					saleReq.CHARGE = Convert.increaseCharForward(charge + "", '0', 8);
//					saleReq.CHARGE = Convert.increaseCharForward( (salepay.je < 0? salepay.je*-1:salepay.je) + "", '0', 8);
					if (saleReq.CARDSALEITEM.equals(""))
					{
						saleReq.CARDSALEITEM = saleReq.CARDSALEITEM + Convert.increaseCharForward(salepay.payno, '0', 16) + Convert.increaseCharForward(salepay.idno, '0', 8) + Convert.increaseCharForward("", '0', 8);
					}
				}
				else
				{
					saleReq.MONEY += (salepay.je < 0 ? salepay.je * -1 : salepay.je);
					saleReq.CARDSALEITEM = saleReq.CARDSALEITEM + Convert.increaseCharForward(salepay.payno, '0', 16) + Convert.increaseCharForward(salepay.idno, '0', 8) + Convert.increaseCharForward((salepay.je < 0 ? salepay.je * -1 : salepay.je) + "", '0', 8);
				}
			}
		}
		else
		{
			saleReq.MONEY += salepay.je;
			saleReq.CARDSALEITEM = saleReq.CARDSALEITEM + Convert.increaseCharForward(salepay.payno, '0', 16) + Convert.increaseCharForward(salepay.idno, '0', 8) + Convert.increaseCharForward((salepay.je < 0 ? salepay.je * -1 : salepay.je) + "", '0', 8);
		}
				
		return true;
	}
	
	public boolean extendAction2(String param1, String param2)
	{
		return sendData();
	}

	public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
	{
		// 查询
		if (req.type.equals("05"))
		{
			// // 查询会员用于积分，但不能消费
			// if (isHand || req.track2.startsWith("0618"))
			// {
			// saleBS.curCustomer =
			// CardModule.getDefault().getCustomer(req.track2);
			//
			// if (saleBS.curCustomer == null)
			// return false;
			//
			// saleBS.curCustomer.isHandInput = isHand;
			// if (saleBS.autoPay())
			// {
			// saleBS.salePayEvent.refreshFeeLabel();
			// saleBS.salePayEvent.showSalePaymentDisplay();
			// saleBS.salePayEvent.calcPayResult();
			// }
			//
			// if (isHand)
			// {
			// ret.cardno = req.track2;
			// ret.ye = 0.0;
			// ret.money = 0.0;
			// }
			// else
			// {
			// ret.cardno = saleBS.curCustomer.code;
			// ret.cardpwd = saleBS.curCustomer.track;
			// ret.money = ret.ye = saleBS.curCustomer.value1;
			// }
			//
			// return true;
			// }

			return CardModule.getDefault().getMzkInfo(req, ret);
		}

		// 消费
		if (req.type.equals("01"))
		{
			extendAction1();
			return true;
		}

		return false;
	}

	protected boolean sendData()
	{
		// 提取京客隆卡（会员卡，存值卡）消费信息，并发送给卡系统
		try
		{
			if (saleReq == null)
				return true;

			String line = "";
			if (GlobalInfo.isOnline)
			{
				File file = new File(ConfigClass.LocalDBPath);
				File[] list = file.listFiles();

				for (int i = 0; i < list.length; i++)
				{
					// 读取文件
					String name = list[i].getName();

					if (!(name.startsWith("jkl_") && name.endsWith(".dat")))
						continue;

					FileInputStream f = null;
					try
					{
						f = new FileInputStream(list[i].getPath());
						ObjectInputStream s = new ObjectInputStream(f);
						SaleReqDef req = (SaleReqDef) s.readObject();
						line = req.toString();
						s.close();
						s = null;
						f.close();
						f = null;
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					StringBuffer sb = new StringBuffer();
					// 向卡系统提交数据失败
					if (!CardModule.getDefault().submitSale(line, sb))
						continue;

					// 京客隆卡要求消费提交成功后，向数据库插一条数据，表示消费成功
					if (!((Bjkl_NetService) NetService.getDefault()).sendBjklSubmitSale(sb))
						continue;

					// 发送脱网销售数据成功，删除数据
					list[i].delete();
				}

				line = saleReq.toString();
				StringBuffer sb = new StringBuffer();

				// 向卡系统提交数据失败
				if (!CardModule.getDefault().submitSale(line, sb))
					return false;

				// 京客隆卡要求消费提交成功后，向数据库插一条数据，表示消费成功
				if (!((Bjkl_NetService) NetService.getDefault()).sendBjklSubmitSale(sb))
					return false;

				// 记录积分
				mzkret.value1 = Convert.toDouble(sb.toString().split("\\|")[1].split(",")[0]);
				mzkret.value2 = Convert.toDouble(sb.toString().split("\\|")[1].split(",")[1]);
				mzkret.ye = Convert.toDouble(sb.toString().split("\\|")[2].split(",")[0]);

				return true;

			}
			// 脱网状态下，先保存，待下次联网销售时发送
			else
			{
				PrintWriter pw = null;
				// line = saleReq.toString();
				FileOutputStream f = null;
				try
				{
					String name = ConfigClass.LocalDBPath + "jkl_" + saleReq.SEQID + ".dat";
					f = new FileOutputStream(name);
					ObjectOutputStream s = new ObjectOutputStream(f);

					// 多写一个当前工号
					s.writeObject(saleReq);
					s.flush();
					s.close();
					f.close();
					s = null;
					f = null;

				}
				finally
				{
					if (f != null)
					{
						f.close();
						f = null;
					}
				}

				return true;
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			new MessageBox("发送 消费交易 出现异常：" + e.getMessage());

			return false;
		}
		finally
		{
			saleReq = null;
		}
	}

	public boolean cancelPay()
	{
		if (!super.cancelPay())
			return false;
		// 折扣账户付款删除时，标记改为0
		if (salepay.isused == 'Y')
		{
			salepay.isused = 0;
			// 折扣账户余额还原
			saleBS.curCustomer.valnum1 = ManipulatePrecision.doubleConvert(saleBS.curCustomer.valnum1 + salepay.je, 2, 1);
		}

		return true;
	}

	// 创建转存付款
	public boolean createLczcSalePay(double zl)
	{
		// 初始设置
		if (saleBS.curCustomer != null)
		{
			setRequestDataByFind("", saleBS.curCustomer.code, "");

			mzkret.cardno = saleBS.curCustomer.code;
			mzkret.cardpwd = saleBS.curCustomer.track;
			mzkret.cardname = saleBS.curCustomer.name;
			mzkret.ye = saleBS.curCustomer.value1; // 零钞余额
			mzkret.money = saleBS.curCustomer.value2; // 最大零钞余额
		}

		// 创建SalePay
		if (!createSalePayObject(String.valueOf((zl))))
			return false;
		
		salepay.batch = "";
		salepay.payno = mzkret.cardno;
		salepay.idno = mzkret.cardpwd;
		salepay.kye = mzkret.ye;
		
		// 零钞转存付款方式金额记负数
		salepay.ybje *= -1;
		salepay.je *= -1;

		// 代表零钞转存
		salepay.memo = "3";

		return true;
	}

	static class SaleReqDef implements Serializable
	{
		public String POSID = ""; // 款台号
		public String CASHID = "";// 银员号
		public String SEQID = "";// 交易流水号
		public double MONEY;// 消费金额
		public String TMONEY = "";// 消费总金额
		public String CHARGE = "";// 找零金额
		public String DISTYPE = "";// 折扣帐户类型
		public String DISDEPOSITMONEY = "";// 折扣帐户存入额
		public String DISUSEMONEY = "";// 折扣帐户使用金额
		public String DISINDETAIL = "";// 折扣存商品明细
		public String CARDSALEITEM = "";// 一条消费明细

		public boolean isinit = false;

		public void initData(SaleHeadDef salehead,double tmoney)
		{

			POSID = Convert.increaseCharForward(salehead.syjh, '0', 4);
			CASHID = Convert.increaseCharForward(salehead.syyh, '0', 4);
			SEQID = Convert.increaseCharForward(salehead.fphm + "", '0', 10);
			//退货时，金额为负
			if (tmoney < 0)
			{
				TMONEY = "-" + Convert.increaseCharForward(Math.abs(tmoney)  + "", '0', 7);
			}
			else
			{
				TMONEY = Convert.increaseCharForward(tmoney  + "", '0', 8);
			}
//			TMONEY = Convert.increaseCharForward(tmoney  + "", '0', 8);
			CHARGE = Convert.increaseCharForward("0.00", '0', 8);
			DISTYPE = "01";
			DISDEPOSITMONEY = Convert.increaseCharForward("0.00", '0', 8);
			DISINDETAIL = "";
			DISUSEMONEY = Convert.increaseCharForward("0.00", '0', 8);

			isinit = true;
		}

		public String toString()
		{
			MONEY = ManipulatePrecision.doubleConvert(MONEY, 2, 1);
			return "2," + POSID + CASHID + SEQID + Convert.increaseCharForward(MONEY + "", '0', 8) + TMONEY + CHARGE + DISTYPE + DISDEPOSITMONEY + DISUSEMONEY + DISINDETAIL + CARDSALEITEM;
		}
	}
}
