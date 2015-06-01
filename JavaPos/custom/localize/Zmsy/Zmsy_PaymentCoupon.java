package custom.localize.Zmsy;

import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.CalcRulePopDef;
import com.efuture.javaPos.Struct.CustFilterDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Zmsy_PaymentCoupon extends PaymentMzk
{
	protected String fjkrulecode = "";

	protected int fjktypeChoice = -1;

	public Vector couponList = new Vector(); // 券号，券名称，金额，汇率，已付付款号

	public int CouponType = 1; // 1-电子券 2 - 纸券

	protected Vector vi = null; // 可分摊的分组

	protected Vector rulelist;

	protected boolean ch;

	public int msrInputType = -2;// -2代表没有配置过TXT的输入类型

	public double yyje = -1;
	public double sjje = -1;

	public boolean isCloseShell = false;
	
	private String cardNo;//购物卡号（证件类型+证件号）
	private String cardName;//证件类型名称

	public Zmsy_PaymentCoupon()
	{
	}

	public Zmsy_PaymentCoupon(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public Zmsy_PaymentCoupon(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public void initPayment(PayModeDef mode, SaleBS sale)
	{
		super.initPayment(mode, sale);
	}

	public void setRequestDataBySalePay()
	{
		super.setRequestDataBySalePay();

		// 标记券类型,券活动规则代码
		String[] s = salepay.idno.split(",");

		if (s.length >= 1)
		{
			mzkreq.memo = s[0];
		}

		if (s.length >= 2)
		{
			fjkrulecode = s[1];
		}
	}

	public String getCouponPayType(SalePayDef spd)
	{
		return String.valueOf(spd.idno.charAt(0));
	}

	protected String getDisplayAccountInfo()
	{
		if (cardName == null || cardName.trim().length()<=0) cardName="证 件 号";
		return cardName;//"卡    号";
	}

	public boolean isApportionInBS()
	{
		return false;
	}

	/**
	 * public boolean createSalePay(double moneyA,double moneyB,double moneyF) {
	 * try { boolean issucc = false;
	 * 
	 * if (moneyA > 0) { mzkreq.memo = FJK_A; // 标记券类型
	 * 
	 * issucc = super.createSalePay(String.valueOf(moneyA));
	 * 
	 * if (issucc) { salepay.payname = FJKNAME_A; // 修改付款名称
	 * 
	 * if (moneyB > 0) { if (!CreateNewjPayment(FJK_B, FJKNAME_B, moneyB))
	 * return false; }
	 * 
	 * if (moneyF > 0) { if (!CreateNewjPayment(FJK_F, FJKNAME_F, moneyF))
	 * return false; } } } else { if (moneyB > 0) { mzkreq.memo = FJK_B; //
	 * 标记券类型
	 * 
	 * issucc = super.createSalePay(String.valueOf(moneyB));
	 * 
	 * if (issucc) { salepay.payname = FJKNAME_B; // 修改付款名称
	 * 
	 * if (moneyF > 0) { if (!CreateNewjPayment(FJK_F, FJKNAME_F, moneyF))
	 * return false; } } } else { mzkreq.memo = FJK_F; // 标记券类型
	 * 
	 * issucc = super.createSalePay(String.valueOf(moneyF));
	 * 
	 * if (issucc) { salepay.payname = FJKNAME_F; // 修改付款名称 } } }
	 * 
	 * return issucc; } catch (Exception ex) { ex.printStackTrace(); return
	 * false; } }
	 */
	public boolean deletePayment(int index, Zmsy_PaymentCoupon pcp1)
	{
		String[] rows = (String[]) couponList.elementAt(index);

		int oldpayindex = -1;

		// 查询并删除原付款
		if (Convert.toInt(rows[4]) == -1)
		{
			oldpayindex = getpaymentIndex(paymode.code, mzkret.cardno, rows[0]);
		}
		else
		{
			oldpayindex = Convert.toInt(rows[4]);
		}

		// 没有相同的付款方式
		if (oldpayindex == -1) { return true; }

		int index1 = -1;
		Vector vi = saleBS.salePayment;

		if (saleBS.isRefundStatus())
		{
			vi = saleBS.refundPayment;
		}

		for (int i = 0; i < vi.size(); i++)
		{
			SalePayDef spd = (SalePayDef) vi.elementAt(i);

			if (spd.num5 == oldpayindex)
			{
				index1 = i;

				break;
			}
		}

		if (index1 != -1)
		{
			if (saleBS.isRefundStatus())
			{
				saleBS.delSaleRefundObject(index1);
			}
			else
			{
				saleBS.delSalePayObject(index1);
			}
		}

		alreadyAddSalePay = true;

		return true;
	}

	protected boolean saveFindMzkResultToSalePay()
	{
		if (!super.saveFindMzkResultToSalePay()) { return false; }

		salepay.batch = "";
		salepay.payno = mzkret.cardno;
		salepay.idno = mzkreq.memo; // 标记是什么类型的券(1-A/2-B/3-F)

		if (!fjkrulecode.trim().equals(""))
		{
			salepay.idno += ("," + fjkrulecode); // 券的活动规则代码
		}

		if (salepay.idno.indexOf(",") < 0)
			salepay.idno = salepay.idno + ",," + salepay.hl;
		else
			salepay.idno = salepay.idno + "," + salepay.hl;

		// 设置memo,交易记账时要做为券类型和活动规则代码
		mzkreq.memo = salepay.idno;

		return true;
	}

	public boolean checkMzkMoneyValid()
	{
		if (!super.checkMzkMoneyValid()) { return false; }

//		if ((CouponType == 2) && !checkMzkIsBackMoney())
		if (GlobalInfo.sysPara.gwkQuan_iszl.equals("N") && !checkMzkIsBackMoney())
		{
			// 券必须一次付完,输入金额和可收金额
			if (ManipulatePrecision.doubleCompare(salepay.ybje, this.getAccountYe(), 2) != 0)
			{
				String syje = ManipulatePrecision.doubleToString(this.getAccountYe() - salepay.ybje);
				if (new MessageBox(salepay.payname + "的每张券必须一次性付完!\n是否将剩余金额"+ syje +"计入损溢？", null, true).verify() == GlobalVar.Key1)
				{
					// num1记录券付款溢余部分
					// salepay.num1 =
					// ManipulatePrecision.sub(ManipulatePrecision.mul(Double.parseDouble(this.getAccountYe()),
					// salepay.hl), ManipulatePrecision.mul(salepay.ybje,
					// salepay.hl));
					salepay.num1 = ManipulatePrecision.sub(ManipulatePrecision.mul(this.getAccountYe(), salepay.hl), Math.min(salepay.je, this.saleBS.calcPayBalance()));
					salepay.ybje = this.getAccountYe();
					salepay.je = ManipulatePrecision.doubleConvert(salepay.ybje * salepay.hl, 2, 1);

					return true;
				}
				else
				{
					return false;
				}
			}
		}

		return true;
	}

	public boolean iscloseShell()
	{
		return isCloseShell;
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

			Zmsy_PaymentCoupon cpf = new Zmsy_PaymentCoupon(paymode, saleBS);
 
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

	// 分摊付款方式
	public boolean paymentApportion(SalePayDef spay, Payment payobj, boolean test)
	{
		// 退货时不进行分摊
		if (SellType.ISBACK(salehead.djlb)) { return true; }

		// 开放分组
		Vector v = new Vector();

		for (int z = 0; z < vi.size(); z++)
		{
			CalcRulePopDef calPop = (CalcRulePopDef) vi.elementAt(z);

			// str3 模拟下此规则的已经分摊的金额
			if (test)
			{
				calPop.str3 = calPop.str2;
			}

			// 得到本规则所有商品总共满收
			int num1 = ManipulatePrecision.integerDiv(calPop.popje, Convert.toDouble(calPop.catid));
			double cxje = ManipulatePrecision.doubleConvert(num1 * Convert.toDouble(calPop.str1));
			double yfje = 0;

			for (int x = calPop.row_set.size() - 1; x >= 0; x--)
			{
				int i = Convert.toInt(((String[]) calPop.row_set.elementAt(x))[0]);
				GoodsDef goods = (GoodsDef) saleBS.goodsAssistant.elementAt(i);

				// 计算每个商品的最大可收金额,第一个商品用减计算出最大可收
				SaleGoodsDef sgd = (SaleGoodsDef) saleBS.saleGoods.elementAt(i);
				double ksje = 0;
				ksje = ManipulatePrecision.doubleConvert((sgd.hjje - sgd.hjzk) / Convert.toDouble(calPop.catid) * Convert.toDouble(calPop.str1));

				if (x == 0)
				{
					ksje = Math.max(ksje, ManipulatePrecision.sub(cxje, yfje));
				}
				else if (ksje > ManipulatePrecision.sub(cxje, yfje))
				{
					ksje = ManipulatePrecision.sub(cxje, yfje);
				}

				yfje = ManipulatePrecision.add(yfje, ksje);

				// 计算商品
				Object[] rows = { String.valueOf(i), String.valueOf(0), String.valueOf(goods.str4.split("\\|").length), calPop, String.valueOf(ksje) };
				int j = 0;

				for (j = 0; j < v.size(); j++)
				{
					Object[] rows1 = (Object[]) v.elementAt(j);

					if (Convert.toInt(rows1[2]) > Convert.toInt(rows[2]))
					{
						break;
					}

					if (Convert.toInt(rows1[2]) == Convert.toInt(rows[2]))
					{
						if (Convert.toInt(rows1[0]) < Convert.toInt(rows[0]))
						{
							break;
						}
					}
				}

				if (j < v.size())
				{
					v.add(j, rows);
				}
				else
				{
					v.add(rows);
				}
			}
		}

		double syje = ManipulatePrecision.doubleConvert(spay.je - spay.num1);

		for (int i = 0; i < v.size(); i++)
		{
			Object[] rows1 = (Object[]) v.elementAt(i);
			SpareInfoDef spinfo = (SpareInfoDef) saleBS.goodsSpare.elementAt(Convert.toInt(rows1[0]));
			CalcRulePopDef calPop = (CalcRulePopDef) rows1[3];

			int oldpayindex = -1;
			if (test)
				oldpayindex = getpaymentIndex(paymode.code, mzkret.cardno, spay.idno);

			double je1 = getValidValue(Convert.toInt(rows1[0]), oldpayindex); // 商品剩余未分摊金额

			// 商品按金额比例计算分摊金额
			// SaleGoodsDef sgd = (SaleGoodsDef)
			// saleBS.saleGoods.elementAt(Convert.toInt(rows1[0]));
			double je2 = Convert.toDouble(rows1[4]);// ManipulatePrecision.doubleConvert((sgd.hjje
													// - sgd.hjzk) /
													// Convert.toDouble(calPop.catid)
													// *
													// Convert.toDouble(calPop.str1));

			// 此规则最大能分摊金额
			int num1 = ManipulatePrecision.integerDiv(calPop.popje, Convert.toDouble(calPop.catid));
			double je3 = ManipulatePrecision.doubleConvert(num1 * Convert.toDouble(calPop.str1));

			// 此规则最大能分摊金额 - 此规则已分摊金额
			double je4 = 0;

			if (!test)
			{
				je4 = ManipulatePrecision.doubleConvert(je3 - Convert.toDouble(calPop.str2));
			}
			else
			{
				je4 = ManipulatePrecision.doubleConvert(je3 - Convert.toDouble(calPop.str3));
			}

			// 计算是否存在相同的券付款 , 减去已付款的此券金额（不包含同卡的已付金额）
			double tqfk = getftje(spinfo, spay.paycode, spay.payno, spay.idno.charAt(0));
			double je5 = ManipulatePrecision.doubleConvert(je2 - tqfk);

			// 比较商品最大能收金额和规则能收最大金额
			double je6 = Math.min(je4, je5);

			// 比较余额和商品最大能收金额
			je6 = Math.min(je6, je1);

			if (spinfo.payft == null)
			{
				spinfo.payft = new Vector();
			}

			double spje = Math.min(syje, je6);

			if (!test)
			{
				String[] ft = new String[] { String.valueOf(spay.num5), spay.paycode, spay.payname, String.valueOf(spje) };
				spinfo.payft.add(ft);
				calPop.str2 = ManipulatePrecision.doubleToString(Convert.toDouble(calPop.str2) + spje);
			}
			else
			{
				calPop.str3 = ManipulatePrecision.doubleToString(Convert.toDouble(calPop.str3) + spje);
			}

			syje = ManipulatePrecision.doubleConvert(syje - spje);

			if (syje <= 0)
			{
				break;
			}
		}

		if (syje > 0)
		{
			spay.num1 = ManipulatePrecision.doubleConvert(syje + spay.num1);
		}

		return true;
	}

	public void addMessage(Zmsy_PaymentCoupon cpf, StringBuffer bufferStr)
	{
	}

	public void showAccountYeMsg()
	{
	}

	protected boolean checkMoneyValid(String money, double ye)
	{
		return super.checkMoneyValid(money, ye);
	}

	public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
	{
		if (sjje >= 0)
			req.track2 = "0000";
		return DataService.getDefault().sendFjkSale(req, ret);
	}

	// 判断是否是返券卡
	public boolean isCzFile(String filename)
	{
		if (filename.startsWith("Fjk_") && filename.endsWith(".cz"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public String GetMzkCzFile()
	{
		return ConfigClass.LocalDBPath + "/Fjk_" + mzkreq.seqno + ".cz";
	}

	public boolean isAcceptFjkRule()
	{
		return true;
	}

	public int getAccountInputMode()
	{
		if (msrInputType != -2)
			return msrInputType;
		else
			return super.getAccountInputMode();
	}

	public int choicFjkType()
	{
		fjktypeChoice = -1;
		HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();
		// 获取自定义的解析规则
		rulelist = bs.showRule();
		if (rulelist != null && rulelist.size() <= 0)
			rulelist = null;

		// 先选择规则后刷会员卡
		if (GlobalInfo.sysPara.unionVIPMode == 'A')
		{
			if (rulelist != null && rulelist.size() > 1)
			{
				Vector con = new Vector();
				for (int i = 0; i < rulelist.size(); i++)
				{
					CustFilterDef filterDef = (CustFilterDef) rulelist.elementAt(i);

					if (filterDef.ispay == 1)
					{
						rulelist.remove(i);
						i--;
						continue;
					}

					con.add(new String[] { filterDef.desc });
				}

				if (con.size() > 1)
				{
					String[] title = { "会员卡类型" };
					int[] width = { 500 };

					int choice = new MutiSelectForm().open("请选择卡类型", title, width, con);
					fjktypeChoice = choice;

					if (choice != -1)
					{
						CustFilterDef rule = ((CustFilterDef) rulelist.elementAt(choice));
						rulelist.removeAllElements();
						rulelist.add(rule);
						if (rule.InputType != -2)
							msrInputType = rule.InputType;
					}
				}
				else
				{
					if (rulelist.size() == 1)
					{
						CustFilterDef rule = ((CustFilterDef) rulelist.elementAt(0));
						if (rule.InputType != -2)
							msrInputType = rule.InputType;
					}
				}

				if (rulelist != null)
					ch = true;
			}
		}

		return fjktypeChoice;
	}

	public String[] parseFjkTrack(String track1, String track2, String track3)
	{
		String[] s = new String[3];
		HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();
		// 检查磁道是否和规则相匹配
		if (rulelist != null && rulelist.size() > 0)
		{
			rulelist = bs.chkTrack(track1, track2, track3, rulelist, true);
		}

		// 如果匹配的规则有多个,再次让客户选择(B模式先刷卡后选择)
		if (rulelist != null && rulelist.size() > 1)
		{
			rulelist = bs.chooseRule(rulelist, true);
		}

		// 解析有效规则下的磁道号
		if (rulelist != null && rulelist.size() > 0)
		{
			track2 = bs.getTrackByDefine(track1, track2, track3, rulelist);
		}
		else
		{
			if (ch)
			{
				new MessageBox("刷卡与联名卡规则不匹配，该卡无效");
				return null;
			}
		}

		s[0] = track1;
		s[1] = track2;
		s[2] = track3;

		return s;
	}

	public boolean paynoMSR()
	{
		if (SellType.ISBACK(salehead.djlb))
		{
			if ((GlobalInfo.sysPara.backRefundMSR.charAt(0) == 'Y') && ((salepay.payno == null) || (salepay.payno.length() <= 0)))
			{
				while (true)
				{
					StringBuffer cardno = new StringBuffer();

					TextBox txt = new TextBox();

					if (!txt.open("请刷会员卡或储值卡", "卡号", "请将会员卡或储值卡从刷卡槽刷入", cardno, 0, 0, false, TextBox.MsrKeyInput)) { return false; }

					String[] track = parseFjkTrack(txt.Track1, txt.Track2, txt.Track3);

					MzkRequestDef mzkreq1 = new MzkRequestDef();
					mzkreq1.type = "05"; // 查询类型
					mzkreq1.seqno = 0;
					mzkreq1.termno = ConfigClass.CashRegisterCode;
					mzkreq1.mktcode = GlobalInfo.sysPara.mktcode;
					mzkreq1.syyh = GlobalInfo.posLogin.gh;
					mzkreq1.syjh = ConfigClass.CashRegisterCode;
					mzkreq1.fphm = GlobalInfo.syjStatus.fphm;
					mzkreq1.invdjlb = ((salehead != null) ? salehead.djlb : "");
					mzkreq1.paycode = ((paymode != null) ? paymode.code : "");
					mzkreq1.je = 0;
					mzkreq1.track1 = track[0];
					mzkreq1.track2 = track[1];
					mzkreq1.track3 = track[2];
					mzkreq1.passwd = "";
					mzkreq1.memo = "";

					MzkResultDef mzkret1 = new MzkResultDef();

					if (!sendMzkSale(mzkreq1, mzkret1))
					{
						new MessageBox("此卡号未找到 或 此卡为不可用状态");

						continue;
					}

					salepay.payno = mzkret1.cardno;

					break;
				}
			}
		}

		return true;
	}

	public boolean findFjk(String track1, String track2, String track3)
	{
		track2 = this.cardNo;//wangyong add by 2013.8.26//"210102193212220011";//
		PosLog.getLog(this.getClass().getSimpleName()).info("findFjk() track2=[" + cardNo + "].");
		if ((track1.trim().length() <= 0) && (track2.trim().length() <= 0) && (track3.trim().length() <= 0)) { return false; }

		// 解析磁道
		String[] s = parseFjkTrack(track1, track2, track3);

		if (s == null) { return false; }

		track1 = s[0];
		track2 = s[1];
		track3 = s[2];

		// 设置查询条件
		setRequestDataByFind(track1, track2, track3);

		// 查询时memo存放活动规则
		mzkreq.memo = fjkrulecode;

		if (mzkreq.invdjlb != null && SellType.ISBACK(mzkreq.invdjlb) && !saleBS.isRefundStatus())
		{
			if (GlobalInfo.sysPara.oldqpaydet != 'N' && track2.equals("0000"))
			{
				StringBuffer cardno = new StringBuffer();
				TextBox txt = new TextBox();
				if (!txt.open("请刷原小票里的会员卡或顾客打折卡", "会员号", "请将会员卡或顾客打折卡从刷卡槽刷入", cardno, 0, 0, false, getAccountInputMode())) { return false; }
				String tr = txt.Track2;

				String[] retinfo = NetService.getDefault().findoldqpaydet(salehead.ysyjh, salehead.yfphm, paymode.code, tr, "", "", "", "", "", GlobalInfo.localHttp);
				if (retinfo == null) { return false; }
				yyje = Convert.toDouble(retinfo[1]);
				sjje = Convert.toDouble(retinfo[0]);
			}

			// 传入原收银机号和原小票号
			mzkreq.track3 = saleBS.saleHead.ysyjh + "," + saleBS.saleHead.yfphm;
		}

		// 发送查询交易
		boolean done = sendMzkSale(mzkreq, mzkret);

		return done;
	}

	// 查找返券卡基本信息
	public boolean findFjkInfo(String track1, String track2, String track3, ArrayList fjklist)
	{
		if ((track1.trim().length() <= 0) && (track2.trim().length() <= 0) && (track3.trim().length() <= 0)) { return false; }

		// 解析磁道
		String[] s = parseFjkTrack(track1, track2, track3);

		if (s == null) { return false; }

		track1 = s[0];
		track2 = s[1];
		track3 = s[2];

		//
		setRequestDataByFind(track1, track2, track3);

		return DataService.getDefault().getFjkInfo(mzkreq, fjklist);
	}

	public String getCardName()
	{
		return mzkret.cardname;
	}

	public boolean initList()
	{
		try
		{
			couponList.clear();
			couponList.removeAllElements();

			if ((mzkret.memo != null) && (mzkret.memo.trim().length() > 0))
			{
				String[] row = mzkret.memo.split("\\|");

				if (row.length <= 0) { return false; }

				for (int i = 0; i < row.length; i++)
				{
					String[] line = row[i].split(",");

					if (line.length != 5)
					{
						continue;
					}

					// if (sjje >0)
					// {
					// String[] lines = { line[0], line[1],
					// ManipulatePrecision.doubleToString(sjje/Convert.toDouble(line[3])),
					// line[3], "-1", line[4] };
					// couponList.add(lines);
					// }
					// else
					// {
					String[] lines = { line[0], line[1], line[2], line[3], "-1", line[4] };
					couponList.add(lines);
					// }

				}

				return true;
			}
			else
			{
				new MessageBox("当前没有有效券余额\n或\n此券已经消费或者过期");
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}

		return false;
	}

	// 设置金额
	public boolean setYeShow(Table table)
	{
		// 设置余额列表
		table.removeAll();

		// 显示券类型
		for (int i = 0; i < couponList.size(); i++)
		{
			String[] str = new String[3];
			TableItem item = null;
			String[] row = (String[]) couponList.elementAt(i);

			str[0] = row[1];
			str[1] = ManipulatePrecision.doubleToString(Convert.toDouble(row[2]));
			str[2] = "0.00";
			item = new TableItem(table, SWT.NONE);
			item.setText(str);
		}
		return true;
	}

	// 查询可付金额
	public String getValidJe(int index)
	{
		String[] rows = (String[]) couponList.elementAt(index);
		String line = "";
		// 退货时不记算最大能退金额
		if (SellType.ISSALE(salehead.djlb))
		{
			allowpayje = getCouponJe(paymode.code, mzkret.cardno, rows[0], rows[3], Convert.toInt(rows[4]));
			double hl = Convert.toDouble(rows[3]);
			// 总是进位到分,确保hl*原币一定是大于应收
			allowpayje = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(allowpayje, hl) + 0.009, 2, 0);
			double showprice = saleBS.getDetailOverFlow(allowpayje);
			line = "可收" + rows[1] + "金额为： " + ManipulatePrecision.doubleToString(allowpayje);
			line += "\n汇率为 " + ManipulatePrecision.doubleToString(hl);
			allowpayje = Math.max(allowpayje, showprice);
		}
		else
		{
			double hl = 1;
			line = "汇率为 " + ManipulatePrecision.doubleToString(hl);

			if (GlobalInfo.sysPara.fjkkhhl != null && GlobalInfo.sysPara.fjkkhhl.length() > 0 && saleBS.isRefundStatus() && !SellType.ISCOUPON(saleBS.saletype))
			{
				String[] lines = null;
				if (GlobalInfo.sysPara.fjkkhhl.indexOf(";") >= 0)
					lines = GlobalInfo.sysPara.fjkkhhl.split(";");
				else if (GlobalInfo.sysPara.fjkkhhl.indexOf("|") >= 0)
					lines = GlobalInfo.sysPara.fjkkhhl.split("\\|");

				if (lines == null)
					lines = new String[] { GlobalInfo.sysPara.fjkkhhl };
				int i = 0;
				for (i = 0; i < lines.length; i++)
				{
					String l = lines[i];
					if (l.indexOf(",") > 0)
					{
						String cid = l.substring(0, l.indexOf(","));
						if (cid.equals(rows[0]))
						{
							hl = Convert.toDouble(l.substring(l.indexOf(",") + 1));
							if (hl != 1)
								line = "注意:此项请输入要扣回的积分数\n";
							line += "汇率为 " + ManipulatePrecision.doubleToString(hl);
							break;
						}
					}
				}
			}
			else
			{
				hl = Convert.toDouble(rows[3]);

				line = "汇率为 " + rows[3];
			}

			allowpayje = getBackCouponJe(paymode.code, mzkret.cardno, rows[0], String.valueOf(hl), Convert.toInt(rows[4]));

			if (sjje > 0)
			{
				allowpayje = Math.min(ManipulatePrecision.doubleConvert(sjje / Convert.toDouble(rows[3])), allowpayje);
			}

			line += "\n剩余未付金额为:" + allowpayje;
		}

		if (SellType.ISCOUPON(saleBS.saletype) && SellType.ISBACK(saleBS.saletype) && saleBS.isRefundStatus())
		{

			for (int i = 0; i < saleBS.refundlist.size(); i++)
			{
				String[] row = (String[]) saleBS.refundlist.elementAt(i);
				if (row[0].equals(rows[0]))
				{
					line += "\n此券需扣回金额为 " + row[1] + " :" + row[2] + "\n";
					break;
				}
			}
		}
		return line;
	}

	public double getBackCouponJe(String paycode, String payno, String couponID, String hl, int oldpayindex)
	{
		try
		{
			// 检查有没有重复的金额
			Vector vi = saleBS.salePayment;

			if (saleBS.isRefundStatus())
			{
				vi = saleBS.refundPayment;
			}

			double yf = 0;
			for (int n = 0; n < vi.size(); n++)
			{
				SalePayDef sp = (SalePayDef) vi.elementAt(n);

				if (sp.paycode.equals(paycode) && sp.payno.equals(payno) && sp.idno.equals(couponID))
				{
					yf = sp.je;
				}
			}

			return ManipulatePrecision.doubleConvert((saleBS.calcPayBalance() + yf) / Convert.toDouble(hl));
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}

		return saleBS.calcPayBalance();
	}

	// 查询可付金额
	public double getCouponJe(String paycode, String payno, String couponID, String hl, int oldpayindex)
	{

		CalcRulePopDef calPop = null;
		// 如果原付款行号为-1，查询对应的商品行号
		if (oldpayindex == -1)
		{
			oldpayindex = getpaymentIndex(paycode, payno, couponID);
		}

		if (vi == null)
		{
			vi = new Vector();
		}
		else
		{
			vi.removeAllElements();
		}
		// 行号，满金额，收金额，分组规则，剩余未付金额
		for (int i = 0; i < saleBS.goodsAssistant.size(); i++)
		{
			GoodsDef goods = (GoodsDef) saleBS.goodsAssistant.elementAt(i);
			SaleGoodsDef sgd = (SaleGoodsDef) saleBS.saleGoods.elementAt(i);
			SpareInfoDef spinfo = (SpareInfoDef) saleBS.goodsSpare.elementAt(i);

			if (goods.str4 != null && goods.str4.length() > 0)
			{
				// 券ID，收券条件，收券金额， 活动单号，是否跨柜
				if (goods.str4.indexOf(couponID + ",") >= 0)
				{
					String line = goods.str4.substring(goods.str4.indexOf(couponID + ","));
					if (line.indexOf("|") >= 0)
						line = line.substring(0, line.indexOf("|"));

					String[] values = line.split(",");

					String isOverGz = "Y";
					if (values.length > 4)
						isOverGz = values[4];

					// 剩余可分摊金额
					double je = getValidValue(i, oldpayindex);

					// 相同券已分摊金额
					double je1 = getftje(spinfo, paycode, payno, couponID.charAt(0));

					// 此券可收金额
					je = ManipulatePrecision.doubleConvert(je + je1);
					if (je >= 0)
					{
						int j = 0;
						for (j = 0; j < vi.size(); j++)
						{
							boolean isMerge = false;
							calPop = (CalcRulePopDef) vi.elementAt(j);
							if (isOverGz.equals("Y"))
							{
								if (calPop.code.equals(values[3]) && calPop.catid.equals(values[1]) && calPop.str1.equals(values[2]))
								{
									isMerge = true;
								}
							}
							else
							{
								if (calPop.str4.equals(sgd.gz) && calPop.code.equals(values[3]) && calPop.catid.equals(values[1]) && calPop.str1.equals(values[2]))
								{
									isMerge = true;
								}
							}
							if (isMerge)
							{
								if (GlobalInfo.sysPara.couponRuleType == 'Y')
								{
									calPop.popje = ManipulatePrecision.doubleConvert(calPop.popje + sgd.hjje - saleBS.getZZK(sgd));
								}
								else
								{
									calPop.popje = ManipulatePrecision.doubleConvert(calPop.popje + je);
								}
								calPop.row_set.add(new String[] { String.valueOf(i), String.valueOf(je) });
								calPop.str2 = ManipulatePrecision.doubleToString((Convert.toDouble(calPop.str2) + je1));
								break;
							}
							// if (calPop.code.equals(values[3]) &&
							// calPop.catid.equals(values[1]) &&
							// calPop.str1.equals(values[2]))
							// {
							// if (GlobalInfo.sysPara.couponRuleType == 'Y')
							// {
							// calPop.popje =
							// ManipulatePrecision.doubleConvert(calPop.popje +
							// sgd.hjje - saleBS.getZZK(sgd));
							// }
							// else
							// {
							// calPop.popje =
							// ManipulatePrecision.doubleConvert(calPop.popje +
							// je);
							// }
							// calPop.row_set.add(new String[] {
							// String.valueOf(i), String.valueOf(je) });
							// calPop.str2 =
							// ManipulatePrecision.doubleToString((Convert.toDouble(calPop.str2)
							// + je1));
							// break;
							// }
						}

						if (j >= vi.size())
						{
							if (Convert.toDouble(values[1]) <= 0)
								continue;

							calPop = new CalcRulePopDef();
							// calPop.code = values[3]; // 活动单号
							// calPop.rulecode = values[0]; // 规则码
							// calPop.catid = values[1]; // 条件金额
							// calPop.str1 = values[2]; // 收券金额
							// calPop.str2 = String.valueOf(je1); //
							calPop.rulecode = values[0]; // 规则码
							calPop.catid = values[1]; // 条件金额
							calPop.str1 = values[2]; // 收券金额
							calPop.code = values[3]; // 活动单号
							if (values.length > 4)
								calPop.str3 = values[4]; // 是否跨柜统计标志
							else
								calPop.str3 = "Y"; // 默认跨柜
							calPop.str4 = sgd.gz; // 柜组

							if (GlobalInfo.sysPara.couponRuleType == 'Y')
							{
								calPop.popje = ManipulatePrecision.doubleConvert(calPop.popje + sgd.hjje - saleBS.getZZK(sgd));
							}
							else
							{
								calPop.popje = ManipulatePrecision.doubleConvert(calPop.popje + je);
							}
							calPop.row_set = new Vector();
							calPop.row_set.add(new String[] { String.valueOf(i), String.valueOf(je) });// 行数和金额
							vi.add(calPop);
						}
					}
				}
			}
		}

		// 计算满收金额
		double ksje = 0; // 可收金额
		// double syze = 0; // 剩余总额
		for (int i = 0; i < vi.size(); i++)
		{
			calPop = (CalcRulePopDef) vi.elementAt(i);

			// 计算商品合计
			double syje = 0;

			if (calPop.popje >= Convert.toDouble(calPop.catid))
			{
				for (int j = 0; j < calPop.row_set.size(); j++)
				{
					String[] row = (String[]) calPop.row_set.elementAt(j);
					syje += Convert.toDouble(row[1]);
				}

				int num = ManipulatePrecision.integerDiv(calPop.popje, Convert.toDouble(calPop.catid));
				double je1 = ManipulatePrecision.doubleConvert(num * Convert.toDouble(calPop.str1));
				ksje += Math.min(je1, syje);
			}
			else
			{
				vi.remove(i);
				i--;
			}
		}

		// 减去已付款的此券金额（不包含同卡的已付金额）
		double yfje = 0;

		for (int i = 0; i < saleBS.salePayment.size(); i++)
		{
			SalePayDef sp = (SalePayDef) saleBS.salePayment.elementAt(i);

			if (DataService.getDefault().searchPayMode(sp.paycode).type == '5')
			{
				// 不同类型付款方式
				if (!isSameTypePayment(sp))
				{
					continue;
				}

				// 同卡
				if (sp.paycode.equals(paycode) && sp.payno.equals(payno))
				{
					continue;
				}

				if (sp.idno.charAt(0) == couponID.charAt(0)) // 券种相同
				{
					yfje += ManipulatePrecision.doubleConvert(sp.je - sp.num1);
				}
			}
		}

		double maxkfje = ManipulatePrecision.doubleConvert(ksje - yfje);

		// 模拟分摊，查看是否存在损益
		SalePayDef sp = new SalePayDef();
		sp.je = maxkfje;
		sp.paycode = paycode;
		sp.payno = payno;
		sp.idno = couponID;

		if (paymentApportion(sp, null, true))
		{
			maxkfje = ManipulatePrecision.doubleConvert(maxkfje - sp.num1);
		}

		return maxkfje;
	}

	public boolean isSameTypePayment(SalePayDef sp)
	{
		try
		{
			// 券类
			String[] pay = CreatePayment.getDefault().getCustomPaymentDefine("PaymentCoupon");

			if (pay != null)
			{
				for (int k = 0; k < pay.length; k++)
				{
					if (sp.paycode.equals(pay[k])) { return true; }
				}
			}

			return false;
		}
		catch (Exception er)
		{
			er.printStackTrace();

			return false;
		}
	}

	// 计算此商品已分摊的券金额
	public double getftje(SpareInfoDef spinfo, String paycode, String payno, char couponid)
	{
		if (spinfo.payft == null) { return 0; }

		double tqfk = 0;

		for (int aa = 0; aa < spinfo.payft.size(); aa++)
		{
			String[] ft = (String[]) spinfo.payft.elementAt(aa); // new
			// String[]
			// {String.valueOf(spay.num5),spay.paycode,spay.payname,rows1[1]};

			for (int ab = 0; ab < saleBS.salePayment.size(); ab++)
			{
				SalePayDef sp = (SalePayDef) saleBS.salePayment.elementAt(ab);

				if ((sp.num5 == Convert.toInt(ft[0])) && (DataService.getDefault().searchPayMode(sp.paycode).type == '5'))
				{
					// 不同类型付款方式
					if (!isSameTypePayment(sp))
					{
						continue;
					}

					// 同卡
					if (sp.paycode.equals(paycode) && sp.payno.equals(payno))
					{
						break;
					}

					if (sp.idno.charAt(0) == couponid) // 券种相同
					{
						tqfk += Convert.toDouble(ft[3]);

						break;
					}
				}
			}
		}

		return tqfk;
	}

	// 查询是否存在重复的付款方式
	public int getpaymentIndex(String paycode, String payno, String payidno)
	{
		Vector vi = saleBS.salePayment;

		if (saleBS.isRefundStatus())
		{
			vi = saleBS.refundPayment;
		}

		for (int n = 0; n < vi.size(); n++)
		{
			SalePayDef sp = (SalePayDef) vi.elementAt(n);

			if (sp.paycode.equals(paycode) && sp.payno.equals(payno))
			{
				if (sp.idno.length() > 0 && payidno.length() > 0)
				{
					if (sp.idno.charAt(0) == payidno.charAt(0)) { return (int) sp.num5; }
				}
			}
		}

		return -1;
	}

	public SalePayDef inputPay(String money)
		{
			try
			{
				// 退货小票不能使用,退货扣回按销售算
				if (checkMzkIsBackMoney() && (GlobalInfo.sysPara.thmzk != 'Y'))
				{
					new MessageBox("退货时不能使用" + paymode.name, null, false);
	
					return null;
				}
	
				// 先检查是否有冲正未发送
				if (!sendAccountCz()) { return null; }
	
				//读取购物卡号作为券卡号
				ZJNOReadForm zjfrm = new ZJNOReadForm(saleBS);
				zjfrm.open();
				if (zjfrm.getIsRead())
				{
					cardNo = zjfrm.getCardNo();
					cardName = zjfrm.getCardName();
					PosLog.getLog(this.getClass().getSimpleName()).info("inputPay() cardNo=[" + cardNo + "],cardName=[" + cardName + "].");
				}
				else
				{
					PosLog.getLog(this.getClass().getSimpleName()).info("inputPay() 退出证件号的读取，不进行付款.");
					//未读取到卡号时，则退出付款
					zjfrm=null;
					return null;
				}
				zjfrm=null;
				
				// 打开明细输入窗口
				new Zmsy_PaymentCouponForm().open(this, saleBS);
	
				// 如果付款成功,则salepay已在窗口中生成
				return salepay;
			}
			catch (Exception ex)
			{
				PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			}
	
			return null;
		}

	// 输入商品行数，重复的付款方式行数
	public double getValidValue(int index, int oldIndex)
	{
		SaleGoodsDef sg = (SaleGoodsDef) saleBS.saleGoods.elementAt(index);
		SpareInfoDef spinfo = (SpareInfoDef) saleBS.goodsSpare.elementAt(index);

		if (spinfo == null) { return 0; }

		// 计算商品可收付款的金额 = 成交价 - 已分摊的付款
		double ftje = 0;

		if (spinfo.payft != null)
		{
			for (int j = 0; j < spinfo.payft.size(); j++)
			{
				String[] s = (String[]) spinfo.payft.elementAt(j);

				if ((int) oldIndex == Convert.toInt(s[0]))
				{
					continue;
				}

				ftje += Convert.toDouble(s[3]);
			}
		}

		double maxfdje = ManipulatePrecision.doubleConvert(sg.hjje - sg.hjzk - ftje);

		return maxfdje;
	}

	public boolean needFindFjk(String track1, String track2, String track3)
	{
		return true;
	}

	public boolean unNeedFindFjkDone(String je)
	{
		return true;
	}

	// 如果为扣回，查询时用零售作为交易类型
	public void setRequestDataByFind(String track1, String track2, String track3)
	{
		super.setRequestDataByFind(track1, track2, track3);

		if (saleBS != null && saleBS.isRefundStatus())
		{
			// 扣回时通过termno为4来判断扣回
			mzkreq.termno = "4";
			mzkreq.invdjlb = "1";
		}
	}
	
	public void specialDeal (Zmsy_PaymentCouponEvent event)
	{
	}
}
