package com.efuture.javaPos.Payment;

import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.CalcRulePopDef;
import com.efuture.javaPos.Struct.CustFilterDef;
import com.efuture.javaPos.Struct.FjkInfoDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class PaymentFjk extends PaymentMzk
{
	protected double fjkAMaxJe;
	protected double fjkBMaxJe;
	protected double fjkFMaxJe;
	public final String FJK_A = "1";
	public final String FJK_B = "2";
	public final String FJK_F = "3";
	protected String FJKNAME_A = "A"+Language.apply("券");
	protected String FJKNAME_B = "B"+Language.apply("券");
	protected String FJKNAME_F = "F"+Language.apply("券");
	protected String FJKYETYPE = null;
	protected String fjkrulecode = "";
	protected int fjktypeChoice = -1;
	protected Vector rulelist;
	private boolean ch;

	public PaymentFjk()
	{
	}

	public PaymentFjk(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public PaymentFjk(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public void initPayment(PayModeDef mode, SaleBS sale)
	{
		super.initPayment(mode, sale);

		//
		FJKNAME_A = mode.name + "A";
		FJKNAME_B = mode.name + "B";
		FJKNAME_F = mode.name + "F";
	}

	public void setRequestDataBySalePay()
	{
		super.setRequestDataBySalePay();

		// 标记券类型,券活动规则代码
		String[] s = salepay.idno.split(",");
		if (s.length >= 1) mzkreq.memo = s[0];
		if (s.length >= 2) fjkrulecode = s[1];
	}

	public String getFjkPayType(SalePayDef spd)
	{
		String[] s = spd.idno.split(",");

		if (s[0].trim().equals(FJK_A)) return "A";
		else if (s[0].trim().equals(FJK_B)) return "B";
		else return "F";
	}

	protected String getDisplayAccountInfo()
	{
		return Language.apply("请 刷 卡");
	}

	public SalePayDef inputPay(String money)
	{
		try
		{
			// 退货小票不能使用,退货扣回按销售算
			if (checkMzkIsBackMoney() && GlobalInfo.sysPara.thmzk != 'Y')
			{
				new MessageBox(Language.apply("退货时不能使用") + paymode.name, null, false);
				return null;
			}

			// 先检查是否有冲正未发送
			if (!sendAccountCz()) return null;

			// 打开明细输入窗口
			new PaymentFjkForm().open(this, saleBS);

			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return null;
	}

	public boolean createSalePay(double moneyA, double moneyB, double moneyF)
	{
		try
		{
			boolean issucc = false;

			if (moneyA > 0)
			{
				mzkreq.memo = FJK_A; // 标记券类型

				issucc = super.createSalePay(String.valueOf(moneyA));

				if (issucc)
				{
					salepay.payname = FJKNAME_A; // 修改付款名称

					if (moneyB > 0)
					{
						if (!CreateNewjPayment(FJK_B, FJKNAME_B, moneyB)) return false;
					}

					if (moneyF > 0)
					{
						if (!CreateNewjPayment(FJK_F, FJKNAME_F, moneyF)) return false;
					}
				}
			}
			else
			{
				if (moneyB > 0)
				{
					mzkreq.memo = FJK_B; // 标记券类型

					issucc = super.createSalePay(String.valueOf(moneyB));

					if (issucc)
					{
						salepay.payname = FJKNAME_B; // 修改付款名称

						if (moneyF > 0)
						{
							if (!CreateNewjPayment(FJK_F, FJKNAME_F, moneyF)) return false;
						}
					}
				}
				else
				{
					mzkreq.memo = FJK_F; // 标记券类型

					issucc = super.createSalePay(String.valueOf(moneyF));

					if (issucc)
					{
						salepay.payname = FJKNAME_F; // 修改付款名称
					}
				}
			}

			return issucc;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean CreateNewjPayment(String type, String name, double money)
	{
		return CreateNewjPayment(type, name, money, new StringBuffer());
	}

	public boolean CreateNewjPayment(String type, String name, double money, StringBuffer bufferStr)
	{
		try
		{
			PaymentFjk cpf = new PaymentFjk(paymode, saleBS);

			cpf.paymode = this.paymode;
			cpf.salehead = this.salehead;
			cpf.saleBS = this.saleBS;

			cpf.mzkreq = (MzkRequestDef) mzkreq.clone();
			cpf.mzkret = (MzkResultDef) mzkret.clone();
			cpf.fjkrulecode = this.fjkrulecode;

			cpf.fjkAMaxJe = this.fjkAMaxJe;
			cpf.fjkBMaxJe = this.fjkBMaxJe;
			cpf.fjkFMaxJe = this.fjkFMaxJe;
			cpf.FJKNAME_A = this.FJKNAME_A;
			cpf.FJKNAME_B = this.FJKNAME_B;
			cpf.FJKNAME_F = this.FJKNAME_F;
			cpf.FJKYETYPE = this.FJKYETYPE;

			// ///////////////////// 创建新的付款明细对象
			// 设置券类型
			cpf.mzkreq.memo = type;

			// 创建付款对象
			if (cpf.createSalePay(String.valueOf(money)))
			{
				// 设置付款方式名称
				cpf.salepay.payname = name;

				// 增加已付款
				if (SellType.ISBACK(saleBS.saletype) && saleBS.isRefundStatus())
				{
					cpf.salepay.payname += Language.apply("扣回");
					saleBS.addSaleRefundObject(cpf.salepay, cpf);
				}
				else
				{
					saleBS.addSalePayObject(cpf.salepay, cpf);
				}

				addMessage(cpf, bufferStr);
				return true;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return false;
	}

	public void addMessage(PaymentFjk cpf, StringBuffer bufferStr)
	{

	}

	public void showAccountYeMsg()
	{

	}

	protected boolean saveFindMzkResultToSalePay()
	{
		if (!super.saveFindMzkResultToSalePay()) return false;

		salepay.batch = "";
		salepay.payno = mzkret.cardno;
		if (mzkreq.memo.equals(FJK_A)) salepay.kye = mzkret.ye;
		else if (mzkreq.memo.equals(FJK_B)) salepay.kye = mzkret.value1;
		else if (mzkreq.memo.equals(FJK_F)) salepay.kye = mzkret.value2;
		salepay.idno = mzkreq.memo; // 标记是什么类型的券(1-A/2-B/3-F)
		if (!fjkrulecode.trim().equals(""))
		{
			salepay.idno += "," + fjkrulecode; // 券的活动规则代码
		}

		// 设置memo,交易记账时要做为券类型和活动规则代码
		mzkreq.memo = salepay.idno;

		return true;
	}

	public boolean checkMzkMoneyValid()
	{
		// 计算扣回汇率
		double hl = getRefundHl(mzkreq.memo);
		if (ManipulatePrecision.doubleCompare(hl, salepay.hl, 4) != 0)
		{
			salepay.hl = hl;
			salepay.je = ManipulatePrecision.doubleConvert(salepay.ybje * salepay.hl, 2, 1);
		}

		// 券付款超过允许付款部分记溢余
		if (mzkreq.memo.equals(FJK_A) && ManipulatePrecision.doubleCompare(salepay.ybje, fjkAMaxJe, 2) > 0)
		{
			// num1记录券付款溢余部分
			salepay.num1 = ManipulatePrecision.sub(ManipulatePrecision.mul(salepay.ybje, salepay.hl), Math.min(salepay.je, fjkAMaxJe));
		}

		// 券付款超过允许付款部分记溢余
		if (mzkreq.memo.equals(FJK_B) && ManipulatePrecision.doubleCompare(salepay.ybje, fjkBMaxJe, 2) > 0)
		{
			// num1记录券付款溢余部分
			salepay.num1 = ManipulatePrecision.sub(ManipulatePrecision.mul(salepay.ybje, salepay.hl), Math.min(salepay.je, fjkBMaxJe));
		}

		// 券付款超过允许付款部分记溢余
		if (mzkreq.memo.equals(FJK_F) && ManipulatePrecision.doubleCompare(salepay.ybje, fjkFMaxJe, 2) > 0)
		{
			// num1记录券付款溢余部分
			salepay.num1 = ManipulatePrecision.sub(ManipulatePrecision.mul(salepay.ybje, salepay.hl), Math.min(salepay.je, fjkFMaxJe));
		}

		return true;
	}

	public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
	{
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

	// 查找返券卡规则
	public boolean findFjkRule(String track1, String track2, String track3)
	{
		ArrayList fjklist = null;
		String content[] = null;

		// 初始化活动规则
		fjkrulecode = "";

		//
		if (GlobalInfo.sysPara.acceptfjkrule == 'Y' && isAcceptFjkRule())
		{
			fjklist = new ArrayList();

			setRequestDataByFind(track1, track2, track3);

			if (!DataService.getDefault().getFjkRuleInfo(mzkreq, fjklist)) return false;

			if (fjklist.size() <= 0) return false;

			String[] title = { Language.apply("规则代码"), Language.apply("规则描述"), Language.apply("A余额"), Language.apply("B余额"), Language.apply("F余额") };
			int[] width = { 180, 240, 100, 100, 100 };
			Vector contents = new Vector();

			for (int i = 0; i < fjklist.size(); i++)
			{
				FjkInfoDef fid = (FjkInfoDef) fjklist.get(i);

				contents.add(new String[] {
											fid.cardno,
											fid.cardname,
											ManipulatePrecision.doubleToString(fid.yeA),
											ManipulatePrecision.doubleToString(fid.yeB),
											ManipulatePrecision.doubleToString(fid.yeF) });
			}

			int choice = new MutiSelectForm().open(Language.apply("请选择返券卡规则"), title, width, contents, false, 775, 319, 745, 192, false);

			if (choice == -1) return false;

			if (choice >= 0)
			{
				content = (String[]) contents.get(choice);

				fjkrulecode = content[0];
			}

			return true;
		}
		else
		{
			return true;
		}
	}

	public int choicFjkType()
	{
		fjktypeChoice = -1;
		HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();
		// 获取自定义的解析规则
		rulelist = bs.showRule();
		if (rulelist != null && rulelist.size() <= 0) rulelist = null;

		// 先选择规则后刷会员卡
		if (GlobalInfo.sysPara.unionVIPMode == 'A')
		{
			if (rulelist != null && rulelist.size() > 1)
			{
				Vector con = new Vector();
				for (int i = 0; i < rulelist.size(); i++)
				{
					CustFilterDef filterDef = (CustFilterDef) rulelist.elementAt(i);
					con.add(new String[] { filterDef.desc });
				}
				String[] title = { Language.apply("会员卡类型") };
				int[] width = { 500 };

				int choice = new MutiSelectForm().open(Language.apply("请选择卡类型"), title, width, con);
				fjktypeChoice = choice;
				
				if (choice != -1)
				{
					CustFilterDef rule = ((CustFilterDef) rulelist.elementAt(choice));
					rulelist.removeAllElements();
					rulelist.add(rule);
				}
				if (rulelist != null) ch = true;
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
			rulelist = bs.chkTrack(track1, track2, track3, rulelist,true);
		}

		// 如果匹配的规则有多个,再次让客户选择(B模式先刷卡后选择)
		if (rulelist != null && rulelist.size() > 1)
		{
			rulelist = bs.chooseRule(rulelist,true);
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
				new MessageBox(Language.apply("刷卡与联名卡规则不匹配，该卡无效"));
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
			if (GlobalInfo.sysPara.backRefundMSR.charAt(0) == 'Y' && (salepay.payno == null || salepay.payno.length() <= 0))
			{
				while (true)
				{
					StringBuffer cardno = new StringBuffer();

					TextBox txt = new TextBox();
					if (!txt.open(Language.apply("请刷会员卡或储值卡"), Language.apply("卡号"), Language.apply("请将会员卡或储值卡从刷卡槽刷入"), cardno, 0, 0, false, TextBox.MsrKeyInput)) { return false; }

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
						new MessageBox(Language.apply("此卡号未找到 或 此卡为不可用状态"));
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
		if (track1.trim().length() <= 0 && track2.trim().length() <= 0 && track3.trim().length() <= 0) { return false; }

		// 解析磁道
		String[] s = parseFjkTrack(track1, track2, track3);
		if (s == null) return false;
		track1 = s[0];
		track2 = s[1];
		track3 = s[2];

		// 先查找返券卡的活动规则
		if (!findFjkRule(track1, track2, track3)) return false;

		// 设置查询条件
		setRequestDataByFind(track1, track2, track3);

		// 查询时memo存放活动规则
		mzkreq.memo = fjkrulecode;

		// 发送查询交易
		return sendMzkSale(mzkreq, mzkret);
	}

	// 查找返券卡基本信息
	public boolean findFjkInfo(String track1, String track2, String track3, ArrayList fjklist)
	{
		if (track1.trim().length() <= 0 && track2.trim().length() <= 0 && track3.trim().length() <= 0) { return false; }

		// 解析磁道
		String[] s = parseFjkTrack(track1, track2, track3);
		if (s == null) return false;
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

	// A券
	public String getAccountYeA()
	{
		return ManipulatePrecision.doubleToString(mzkret.ye);
	}

	// B券
	public String getAccountYeB()
	{
		return ManipulatePrecision.doubleToString(mzkret.value1);
	}

	// F券
	public String getAccountYeF()
	{
		return ManipulatePrecision.doubleToString(mzkret.value2);
	}

	// A券名称
	public String getAccountNameA()
	{
		return FJKNAME_A;
	}

	// B券名称
	public String getAccountNameB()
	{
		return FJKNAME_B;
	}

	// F券名称
	public String getAccountNameF()
	{
		return FJKNAME_F;
	}

	public void setAccountYeType(String type)
	{
		if (type != null && !type.equals(""))
		{
			FJKYETYPE = type;
		}
		else
		{
			FJKYETYPE = "A,B,F";
		}
	}

	// 设置金额
	public boolean setYeShow(Table table)
	{
		// 退货扣回时按销售算，最大允许收券金额就=余额
		if (saleBS.isRefundStatus())
		{
			fjkAMaxJe = Convert.toDouble(getAccountYeA());
			fjkBMaxJe = Convert.toDouble(getAccountYeB());
			fjkFMaxJe = Convert.toDouble(getAccountYeF());
		}
		else
		{
			if (SellType.ISBACK(saleBS.saletype))
			{
				fjkAMaxJe = saleBS.saleyfje;
				fjkBMaxJe = saleBS.saleyfje;
				fjkFMaxJe = saleBS.saleyfje;
			}
			else
			{
				// 计算最大允许收券金额
				if (!calcFjkMaxJe()) return false;
			}
		}

		// 设置返券卡余额类型
		if (FJKYETYPE == null || FJKYETYPE.equals(""))
		{
			setAccountYeType("");
		}

		// 设置余额列表
		table.removeAll();
		String str[] = new String[3];
		TableItem item = null;
		for (int i = 1; i <= 3; i++)
		{
			switch (i)
			{
				case 1:
					if (FJKYETYPE.indexOf('A') >= 0)
					{
						str[0] = getAccountNameA();
						str[1] = this.getAccountYeA();
						str[2] = "0.00";
						item = new TableItem(table, SWT.NONE);
						item.setText(str);
					}
					break;
				case 2:
					if (FJKYETYPE.indexOf('B') >= 0)
					{
						str[0] = getAccountNameB();
						str[1] = this.getAccountYeB();
						str[2] = "0.00";
						item = new TableItem(table, SWT.NONE);
						item.setText(str);
					}
					break;
				case 3:
					if (FJKYETYPE.indexOf('F') >= 0)
					{
						str[0] = getAccountNameF();
						str[1] = this.getAccountYeF();
						str[2] = "0.00";
						item = new TableItem(table, SWT.NONE);
						item.setText(str);
					}
					break;
			}
			// if (item != null) item.setForeground(1,
			// SWTResourceManager.getColor(SWT.COLOR_RED));
		}

		return true;
	}

	protected String getDisplayStatusInfo()
	{
		StringBuffer sb = new StringBuffer();

		for (int i = 1; i <= 3; i++)
		{
			switch (i)
			{
				case 1:
					if (FJKYETYPE.indexOf('A') >= 0)
					{
						if (saleBS.isRefundStatus())
						{
							double hl = getRefundHl(FJK_A);
//							sb.append(getAccountNameA() + "的折现扣回比例为:" + ManipulatePrecision.doubleToString(hl, 4, 1) + "\n");
							sb.append(Language.apply("{0}的折现扣回比例为:{1}\n" ,new Object[]{getAccountNameA() ,ManipulatePrecision.doubleToString(hl, 4, 1)}));
						}
						else
						{
//							sb.append(getAccountNameA() + "允许的最大付款金额为" + (paymode.isyy == 'Y' ? "(可溢余)" : "") + ":");
							sb.append(Language.apply("{0}允许的最大付款金额为" ,new Object[]{getAccountNameA()}) + (paymode.isyy == 'Y' ? Language.apply("(可溢余)") : "") + ":");
							sb.append(ManipulatePrecision.doubleToString(getFjkAMaxJe()) + "\n");
						}
					}
					break;
				case 2:
					if (FJKYETYPE.indexOf('B') >= 0)
					{
						if (saleBS.isRefundStatus())
						{
							double hl = getRefundHl(FJK_B);
//							sb.append(getAccountNameB() + "的折现扣回比例为:" + ManipulatePrecision.doubleToString(hl, 4, 1) + "\n");
							sb.append(Language.apply("{0}的折现扣回比例为:{1}\n" ,new Object[]{getAccountNameB() ,ManipulatePrecision.doubleToString(hl, 4, 1)}));
						}
						else
						{
//							sb.append(getAccountNameB() + "允许的最大付款金额为" + (paymode.isyy == 'Y' ? "(可溢余)" : "") + ":");
							sb.append(Language.apply("{0}允许的最大付款金额为" ,new Object[]{getAccountNameB()}) + (paymode.isyy == 'Y' ? Language.apply("(可溢余)") : "") + ":");
							sb.append(ManipulatePrecision.doubleToString(getFjkBMaxJe()) + "\n");
						}
					}
					break;
				case 3:
					if (FJKYETYPE.indexOf('F') >= 0)
					{
						if (saleBS.isRefundStatus())
						{
							double hl = getRefundHl(FJK_F);
//							sb.append(getAccountNameF() + "的折现扣回比例为:" + ManipulatePrecision.doubleToString(hl, 4, 1) + "\n");
							sb.append(Language.apply("{0}的折现扣回比例为:{1}\n" ,new Object[]{getAccountNameF() ,ManipulatePrecision.doubleToString(hl, 4, 1)}));
						}
						else
						{
//							sb.append(getAccountNameF() + "允许的最大付款金额为" + (paymode.isyy == 'Y' ? "(可溢余)" : "") + ":");
							sb.append(Language.apply("{0}允许的最大付款金额为" ,new Object[]{getAccountNameF()}) + (paymode.isyy == 'Y' ? Language.apply("(可溢余)") : "") + ":");
							sb.append(ManipulatePrecision.doubleToString(getFjkFMaxJe()) + "\n");
						}
					}
					break;
			}
		}

		sb.append(Language.apply("按回车键输入下个券种的付款金额\n"));
		sb.append(Language.apply("按确认键完成所有券种的付款输入\n"));
		sb.append(Language.apply("按上下键可选择券种输入付款金额"));

		return sb.toString();
	}

	public boolean calcFjkMaxJe()
	{
		SaleGoodsDef sgd = null;
		GoodsDef gd = null;
		int i = 0, j = 0;
		boolean newmode = false;
		Vector set = new Vector();
		CalcRulePopDef calPop = null;

		try
		{
			fjkAMaxJe = 0;
			fjkBMaxJe = 0;
			fjkFMaxJe = 0;

			// 先分组商品允许收券的规则，确定是新模式的收券,还是老模式
			for (i = 0; i < saleBS.saleGoods.size(); i++)
			{
				sgd = (SaleGoodsDef) saleBS.saleGoods.get(i);
				gd = (GoodsDef) saleBS.goodsAssistant.get(i);

				// sgd.memo先初始化为空,计算后记录商品可收券金额
				sgd.memo = "";
				if (sgd.flag == '1') continue;

				// memo为空,默认按比例方式收券
				if (gd.memo == null) gd.memo = "";
				if (gd.memo.trim().length() > 0)
				{
					String[] rule = gd.memo.split(",");
					if (rule.length == 5)
					{
						// 收券活动单号,收券规则码,收券规则单据号,收券模式(6-满收/3-按比例收),满收条件金额 0为不收券模式
						// 查找是否相同规则的商品
						for (j = 0; j < set.size(); j++)
						{
							calPop = (CalcRulePopDef) set.elementAt(j);
							if (calPop.code.equals(rule[0]) && calPop.rulecode.equals(rule[1]) && calPop.uid.equals(rule[3]))
							{
								calPop.row_set.add(String.valueOf(i));
								break;
							}
						}
						if (j >= set.size())
						{
							calPop = new CalcRulePopDef();
							calPop.code = rule[0]; // 活动单号
							calPop.rulecode = rule[1]; // 规则码
							calPop.gz = rule[2]; // 规则单号
							calPop.uid = rule[3]; // B收券模式
							calPop.catid = rule[4]; // B条件金额
							calPop.str1 = "3"; // A按比例收
							calPop.str2 = "1";
							calPop.str3 = "3"; // F按比例收
							calPop.str4 = "1";
							calPop.ppcode = String.valueOf(gd.num1) + "," + String.valueOf(gd.num2) + ",1";
							calPop.row_set = new Vector();
							calPop.row_set.add(String.valueOf(i));
							set.add(calPop);
						}
						newmode = true;
					}
					else if (rule.length == 7)
					{
						// 收券活动单号,收券规则码,收券规则单据号,B收券模式(6-满收/3-按比例收),B满收条件金额,A收券模式(6-满收/3-按比例收),A满收条件金额
						// 0为不收券模式
						// 查找是否相同规则的商品
						for (j = 0; j < set.size(); j++)
						{
							calPop = (CalcRulePopDef) set.elementAt(j);
							if (calPop.code.equals(rule[0]) && calPop.rulecode.equals(rule[1]) && calPop.uid.equals(rule[3])
									&& calPop.str1.equals(rule[5]))
							{
								calPop.row_set.add(String.valueOf(i));
								break;
							}
						}
						if (j >= set.size())
						{
							calPop = new CalcRulePopDef();
							calPop.code = rule[0]; // 活动单号
							calPop.rulecode = rule[1]; // 规则码
							calPop.gz = rule[2]; // 规则单号
							calPop.uid = rule[3]; // B收券模式
							calPop.catid = rule[4]; // B条件金额
							calPop.str1 = rule[5]; // A收券模式
							calPop.str2 = rule[6]; // A条件金额
							calPop.str3 = "3"; // F按比例收
							calPop.str4 = "1";
							calPop.ppcode = String.valueOf(gd.num1) + "," + String.valueOf(gd.num2) + ",1";
							calPop.row_set = new Vector();
							calPop.row_set.add(String.valueOf(i));
							set.add(calPop);
						}
						newmode = true;
					}
					else if (rule.length == 9)
					{
						// 收券活动单号,收券规则码,收券规则单据号,
						// B收券模式(6-满收/3-按比例收),B满收条件金额,A收券模式(6-满收/3-按比例收),A满收条件金额,F收券模式(6-满收/3-按比例收),F满收条件金额
						// 0为不收券模式
						// 查找是否相同规则的商品
						for (j = 0; j < set.size(); j++)
						{
							calPop = (CalcRulePopDef) set.elementAt(j);
							if (calPop.code.equals(rule[0]) && calPop.rulecode.equals(rule[1]) && calPop.uid.equals(rule[3])
									&& calPop.str1.equals(rule[5]) && calPop.str3.equals(rule[7]))
							{
								calPop.row_set.add(String.valueOf(i));
								break;
							}
						}
						if (j >= set.size())
						{
							calPop = new CalcRulePopDef();
							calPop.code = rule[0]; // 活动单号
							calPop.rulecode = rule[1]; // 规则码
							calPop.gz = rule[2]; // 规则单号
							calPop.uid = rule[3]; // B收券模式
							calPop.catid = rule[4]; // B条件金额
							calPop.str1 = rule[5]; // A收券模式
							calPop.str2 = rule[6]; // A条件金额
							calPop.str3 = rule[7]; // F收券模式
							calPop.str4 = rule[8]; // F条件金额
							calPop.ppcode = String.valueOf(gd.num1) + "," + String.valueOf(gd.num2) + "," + String.valueOf(gd.num3);
							calPop.row_set = new Vector();
							calPop.row_set.add(String.valueOf(i));
							set.add(calPop);
						}
						newmode = true;
					}
				}
			}

			// 计算商品允许收券的最大金额
			if (!newmode)
			{
				for (i = 0; i < saleBS.saleGoods.size(); i++)
				{
					// 判断商品和券的活动规则是否一致
					if (!checkFjkRuleCode(i)) continue;

					sgd = (SaleGoodsDef) saleBS.saleGoods.get(i);
					gd = (GoodsDef) saleBS.goodsAssistant.get(i);

					double dsjje = sgd.hjje - sgd.hjzk;
					double abl = ((GoodsDef) saleBS.goodsAssistant.get(i)).num1;
					double bbl = ((GoodsDef) saleBS.goodsAssistant.get(i)).num2;
					double fbl = 1;
					double aqje = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(dsjje, abl), 2, 1);
					double bqje = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(dsjje, bbl), 2, 1);
					double fqje = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(dsjje, fbl), 2, 1);

					fjkAMaxJe = fjkAMaxJe + aqje;
					fjkBMaxJe = fjkBMaxJe + bqje;
					fjkFMaxJe = fjkFMaxJe + fqje;

					// 在每行商品上标记
					sgd.memo = ManipulatePrecision.doubleToString(aqje) + "," + ManipulatePrecision.doubleToString(bqje) + ","
							+ ManipulatePrecision.doubleToString(fqje);
				}
			}
			else
			{
				/*
				 * // 不允许同一单有不同收券规则,也不允许部分商品参与收券，部分商品不参与收券 if (set.size() >= 2) {
				 * new MessageBox("本笔交易存在不同的商品收券规则\n\n请分单进行收银"); return false; }
				 * if (((CalcRulePopDef)set.elementAt(0)).row_set.size() !=
				 * saleBS.saleGoods.size()) { new
				 * MessageBox("本笔交易部分商品允许收券,部分不收券\n\n请分单进行收银"); return false; }
				 */
				// 分组计算
				for (i = 0; i < set.size(); i++)
				{
					calPop = (CalcRulePopDef) set.elementAt(i);

					// 计算商品合计
					double sphj = 0;
					double saqje = 0;
					double sbqje = 0;
					double sfqje = 0;
					for (j = 0; j < calPop.row_set.size(); j++)
					{
						// 判断商品和券的活动规则是否一致
						if (!checkFjkRuleCode(Integer.parseInt((String) calPop.row_set.elementAt(j)))) continue;

						sgd = (SaleGoodsDef) saleBS.saleGoods.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
						sphj += sgd.hjje - sgd.hjzk;
					}

					// 计算B收券金额
					if (calPop.uid.equals("6")) // B满多少收
					{
						double tjje = Double.parseDouble(calPop.catid);
						if (tjje <= 0) tjje = 1;
						int num = ManipulatePrecision.integerDiv(sphj, tjje);
						String[] s = calPop.ppcode.split(",");
						sbqje = Double.parseDouble(s[1]);
						sbqje = ManipulatePrecision.doubleConvert(sbqje * num, 2, 1);
						System.out.println("收券规则： "+calPop.ppcode+"  "+sbqje);
						fjkBMaxJe = fjkBMaxJe + sbqje;
					}
					else if (calPop.uid.equals("3")) // B按比例收
					{
						// String[] s = calPop.ppcode.split(",");
						// sbqje = Double.parseDouble(s[1]);
						// sbqje = ManipulatePrecision.doubleConvert(sphj *
						// sbqje,2,1);
						// fjkBMaxJe = fjkBMaxJe + sbqje;
						for (int k = 0; k < calPop.row_set.size(); k++)
						{
							sgd = (SaleGoodsDef) saleBS.saleGoods.get(Integer.parseInt((String) calPop.row_set.elementAt(k)));
							gd = (GoodsDef) saleBS.goodsAssistant.get(Integer.parseInt((String) calPop.row_set.elementAt(k)));

							double dsjje = sgd.hjje - sgd.hjzk;
							double bbl = gd.num2;
							fjkBMaxJe = fjkBMaxJe + ManipulatePrecision.mul(dsjje, bbl);
							System.out.println("收券规则1： "+dsjje+"  "+bbl);
						}
					}

					// 计算A收券金额
					if (calPop.str1.equals("6")) // A满多少收
					{
						double tjje = Double.parseDouble(calPop.str2);
						if (tjje <= 0) tjje = 1;
						int num = ManipulatePrecision.integerDiv(sphj, tjje);
						String[] s = calPop.ppcode.split(",");
						saqje = Double.parseDouble(s[0]);
						saqje = ManipulatePrecision.doubleConvert(saqje * num, 2, 1);

						fjkAMaxJe = fjkAMaxJe + saqje;
					}
					else if (calPop.str1.equals("3")) // A按比例收
					{
						// String[] s = calPop.ppcode.split(",");
						// saqje = Double.parseDouble(s[0]);
						// saqje = ManipulatePrecision.doubleConvert(sphj *
						// saqje,2,1);
						// fjkAMaxJe = fjkAMaxJe + saqje;

						for (int k = 0; k < calPop.row_set.size(); k++)
						{
							sgd = (SaleGoodsDef) saleBS.saleGoods.get(Integer.parseInt((String) calPop.row_set.elementAt(k)));
							gd = (GoodsDef) saleBS.goodsAssistant.get(Integer.parseInt((String) calPop.row_set.elementAt(k)));

							double dsjje = sgd.hjje - sgd.hjzk;
							double bbl = gd.num1;
							fjkAMaxJe = fjkAMaxJe + ManipulatePrecision.doubleConvert(dsjje * bbl, 2, 1);
						}
					}

					// 计算F收券金额
					if (calPop.str3.equals("6")) // F满多少收
					{
						double tjje = Double.parseDouble(calPop.str4);
						if (tjje <= 0) tjje = 1;
						int num = ManipulatePrecision.integerDiv(sphj, tjje);
						String[] s = calPop.ppcode.split(",");
						sfqje = Double.parseDouble(s[2]);
						sfqje = ManipulatePrecision.doubleConvert(sfqje * num, 2, 1);

						fjkFMaxJe = fjkFMaxJe + sfqje;
					}
					else if (calPop.str3.equals("3")) // F按比例收
					{
						// String[] s = calPop.ppcode.split(",");
						// sfqje = Double.parseDouble(s[2]);
						// sfqje = ManipulatePrecision.doubleConvert(sphj *
						// sfqje,2,1);
						// fjkFMaxJe = fjkFMaxJe + sfqje;
						for (int k = 0; k < calPop.row_set.size(); k++)
						{
							sgd = (SaleGoodsDef) saleBS.saleGoods.get(Integer.parseInt((String) calPop.row_set.elementAt(k)));
							gd = (GoodsDef) saleBS.goodsAssistant.get(Integer.parseInt((String) calPop.row_set.elementAt(k)));

							if (calPop.str4.equals("1")) gd.num3 = 1;

							double dsjje = sgd.hjje - sgd.hjzk;
							double bbl = gd.num3;

							fjkFMaxJe = fjkFMaxJe + ManipulatePrecision.doubleConvert(dsjje * bbl, 2, 1);
						}
					}

					// System.out.println("F按比例收 "+ calPop.str3+"||
					// "+calPop.ppcode + "|| "+fjkFMaxJe);

					// 无A、B、F券付款
					if (!calPop.uid.equals("6") && !calPop.uid.equals("3") && !calPop.str1.equals("6") && !calPop.str1.equals("3")
							&& !calPop.str3.equals("6") && !calPop.str3.equals("3"))
					{
						set.remove(i);
						i--;
						continue;
					}

					// 分摊收券额到每个商品
					double yfdaq = 0, yfdbq = 0, yfdfq = 0;
					for (j = 0; j < calPop.row_set.size(); j++)
					{
						// 判断商品和券的活动规则是否一致
						if (!checkFjkRuleCode(Integer.parseInt((String) calPop.row_set.elementAt(j)))) continue;

						//
						sgd = (SaleGoodsDef) saleBS.saleGoods.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
						gd = (GoodsDef) saleBS.goodsAssistant.get(Integer.parseInt((String) calPop.row_set.elementAt(j)));
						double aqje = 0, bqje = 0, fqje = 0;
						if (calPop.uid.equals("6")) // B满多少收
						{
							// 把剩余未分摊金额，直接分摊到最后一个商品
							if (j == (calPop.row_set.size() - 1))
							{
								bqje = ManipulatePrecision.doubleConvert(sbqje - yfdbq, 2, 1);
							}
							else
							{
								bqje = ManipulatePrecision.doubleConvert((sgd.hjje - sgd.hjzk) / sphj * sbqje, 2, 1);
							}
						}
						else if (calPop.uid.equals("3"))
						{
							double dsjje = sgd.hjje - sgd.hjzk;
							double bbl = gd.num2;
							bqje = ManipulatePrecision.doubleConvert(dsjje * bbl, 2, 1);
						}

						if (calPop.str1.equals("6")) // A满多少收
						{
							// 把剩余未分摊金额，直接分摊到最后一个商品
							if (j == (calPop.row_set.size() - 1))
							{
								aqje = ManipulatePrecision.doubleConvert(saqje - yfdaq, 2, 1);
							}
							else
							{
								aqje = ManipulatePrecision.doubleConvert((sgd.hjje - sgd.hjzk) / sphj * saqje, 2, 1);
							}
						}
						else if (calPop.str1.equals("3"))
						{
							double dsjje = sgd.hjje - sgd.hjzk;
							double bbl = gd.num1;
							aqje = ManipulatePrecision.doubleConvert(dsjje * bbl, 2, 1);
						}

						if (calPop.str3.equals("6")) // F满多少收
						{
							// 把剩余未分摊金额，直接分摊到最后一个商品
							if (j == (calPop.row_set.size() - 1))
							{
								fqje = ManipulatePrecision.doubleConvert(sfqje - yfdfq, 2, 1);
							}
							else
							{
								fqje = ManipulatePrecision.doubleConvert((sgd.hjje - sgd.hjzk) / sphj * sfqje, 2, 1);
							}
						}
						else if (calPop.str3.equals("3"))
						{
							double dsjje = sgd.hjje - sgd.hjzk;
							double bbl = gd.num3;
							fqje = ManipulatePrecision.doubleConvert(dsjje * bbl, 2, 1);
						}

						yfdaq += aqje;
						yfdbq += bqje;
						yfdfq += fqje;

						// 在每行商品上标记
						sgd.memo = ManipulatePrecision.doubleToString(aqje) + "," + ManipulatePrecision.doubleToString(bqje) + ","
								+ ManipulatePrecision.doubleToString(fqje);
					}
				}
			}

			// 改变了saleGoods的memo,刷新商品列表，保存断点数据
			saleBS.getSaleGoodsDisplay();

			// 计算已付款的电子券付款金额
			String[] s = getFjkPayTotal(saleBS.salePayment).split(",");
			double dyfjea = Double.parseDouble(s[0]);
			double dyfjeb = Double.parseDouble(s[1]);
			double dyfjef = Double.parseDouble(s[2]);

			// 计算出剩余电子券最大付款金额
			fjkAMaxJe = fjkAMaxJe - dyfjea;
			fjkBMaxJe = fjkBMaxJe - dyfjeb;
			fjkFMaxJe = fjkFMaxJe - dyfjef;
			if (fjkAMaxJe < 0) fjkAMaxJe = 0;
			if (fjkBMaxJe < 0) fjkBMaxJe = 0;
			if (fjkFMaxJe < 0) fjkFMaxJe = 0;

			// 换算为付款方式相应汇率、截断方式的金额值
			if (paymode.hl <= 0) paymode.hl = 1;
			fjkAMaxJe = ManipulatePrecision.div(fjkAMaxJe, paymode.hl);
			fjkBMaxJe = ManipulatePrecision.div(fjkBMaxJe, paymode.hl);
			fjkFMaxJe = ManipulatePrecision.div(fjkFMaxJe, paymode.hl);

			fjkAMaxJe = saleBS.getDetailOverFlow(ManipulatePrecision.doubleConvert(fjkAMaxJe, 2, 1));
			fjkBMaxJe = saleBS.getDetailOverFlow(ManipulatePrecision.doubleConvert(fjkBMaxJe, 2, 1));
			fjkFMaxJe = saleBS.getDetailOverFlow(ManipulatePrecision.doubleConvert(fjkFMaxJe, 2, 1));

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox(Language.apply("电子券允许收券金额计算异常:") + ex.getMessage());
			ex.printStackTrace();
			return false;
		}
	}

	public double getFjkAMaxJe()
	{
		return fjkAMaxJe;
	}

	public double getFjkBMaxJe()
	{
		return fjkBMaxJe;
	}

	public double getFjkFMaxJe()
	{
		return fjkFMaxJe;
	}

	public String getFjkPayTotal(Vector salepay)
	{
		SalePayDef spd = null;
		double dyfjea = 0;
		double dyfjeb = 0;
		double dyfjef = 0;

		for (int i = 0; i < salepay.size(); i++)
		{
			spd = (SalePayDef) salepay.get(i);
			if (CreatePayment.getDefault().isPaymentFjk(spd.paycode))
			{
				if (getFjkPayType(spd).equals("A"))
				{
					dyfjea = ManipulatePrecision.doubleConvert(dyfjea + spd.je - spd.num1, 2, 1);
				}
				else if (getFjkPayType(spd).equals("B"))
				{
					dyfjeb = ManipulatePrecision.doubleConvert(dyfjeb + spd.je - spd.num1, 2, 1);
				}
				else
				{
					dyfjef = ManipulatePrecision.doubleConvert(dyfjef + spd.je - spd.num1, 2, 1);
				}
			}
		}

		return ManipulatePrecision.doubleToString(dyfjea) + "," + ManipulatePrecision.doubleToString(dyfjeb) + ","
				+ ManipulatePrecision.doubleToString(dyfjef);
	}

	// 比较返券卡规则码和促销码是否相等
	public boolean checkFjkRuleCode(int index)
	{
		// 通收
		if (fjkrulecode.equals("0") || fjkrulecode.trim().length() <= 0)
		{
			return true;
		}
		else
		{
			Vector v = saleBS.getFjkPopVector();
			if (v != null)
			{
				GoodsPopDef gpd = (GoodsPopDef) v.get(index);
				String rule = ((SpareInfoDef) saleBS.goodsSpare.elementAt(index)).str1;

				if (rule.charAt(0) == '9') rule = rule.substring(1);
				// 此商品不进行返券
				if (rule.charAt(2) == '0') return false;

				if (gpd.memo == null || gpd.memo.trim().length() <= 0) return false;

				String[] rules = gpd.memo.split(",");
				if (rules.length > 1)
				{
					String fjrule = rules[1];

					if (fjrule == null || fjrule.trim().length() <= 0) return false;

					if (fjkrulecode.equals(fjrule)) return true;
					else return false;
				}
				else
				{
					return false;
				}
			}
			else
			{
				return true;
			}
		}
	}

	public boolean needFindFjk(String track1, String track2, String track3)
	{
		return true;
	}

	public boolean unNeedFindFjkDone(String je)
	{
		return true;
	}

	public double getRefundHl(String fjktype)
	{
		if (GlobalInfo.sysPara.fjkkhhl != null && GlobalInfo.sysPara.fjkkhhl.length() > 0 && saleBS.isRefundStatus())
		{
			String[] lines = null;
			if (GlobalInfo.sysPara.fjkkhhl.indexOf(";") >= 0) lines = GlobalInfo.sysPara.fjkkhhl.split(";");
			else if (GlobalInfo.sysPara.fjkkhhl.indexOf("\\|") >= 0) lines = GlobalInfo.sysPara.fjkkhhl.split("\\|");

			if (lines != null)
			{
				for (int i = 0; i < lines.length; i++)
				{
					String l = lines[i];
					if (l.indexOf(",") > 0)
					{
						String cid = l.substring(0, l.indexOf(","));
						if (cid.equals(fjktype)) { return Convert.toDouble(l.substring(l.indexOf(",") + 1)); }
					}
				}
			}
		}

		return paymode.hl;
	}
}
