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
import com.efuture.javaPos.Communication.NetService;
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
import com.efuture.javaPos.Struct.JfSaleRuleDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class PaymentJfNew extends PaymentMzk
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

	public PaymentJfNew()
	{
	}

	public PaymentJfNew(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public PaymentJfNew(SalePayDef pay, SaleHeadDef head)
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
		return Language.apply("请 刷 卡");
	}

	public SalePayDef inputPay(String money)
	{
    	if (!checkUsed())
    	{
    		return null;
    	}
		
		try
		{
			// 退货小票不能使用,退货扣回按销售算
			if (checkMzkIsBackMoney() && (GlobalInfo.sysPara.thmzk != 'Y'))
			{
//				new MessageBox("退货时不能使用" + paymode.name, null, false);
				new MessageBox(Language.apply("退货时不能使用{0}" ,new Object[]{paymode.name}), null, false);

				return null;
			}

			// 先检查是否有冲正未发送
			if (!sendAccountCz()) { return null; }

			// 打开明细输入窗口
			new PaymentJfNewForm().open(this, saleBS);

			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return null;
	}

	public boolean isApportionInBS()
	{
		return false;
	}

	public boolean deletePayment(int index, PaymentJfNew pcp1)
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
		Vector vi1 = saleBS.salePayment;

		if (saleBS.isRefundStatus())
		{
			vi1 = saleBS.refundPayment;
		}

		for (int i = 0; i < vi1.size(); i++)
		{
			SalePayDef spd = (SalePayDef) vi1.elementAt(i);

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

	public boolean checkMzkMoneyValid()
	{
		// 检查金额是否超过限制金额
		if (this.allowpayje >= 0 && ManipulatePrecision.doubleCompare(salepay.ybje,this.allowpayje, 2) > 0)
		{
			new MessageBox(Language.apply("输入金额超过允许支付限额!"));
			
			return false;
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
				new MessageBox(Language.apply("付款金额必须大于0"));

				return false;
			}
			
			if (money > this.allowpayje)
			{
				new MessageBox(Language.apply("输入数字超过限制积分"));
			}

			PaymentJfNew cpf = new PaymentJfNew(paymode, saleBS);

			cpf.paymode = (PayModeDef) this.paymode.clone();
			cpf.salehead = this.salehead;
			cpf.saleBS = this.saleBS;
			cpf.couponList = this.couponList;
			cpf.vi = this.vi;
			cpf.mzkreq = (MzkRequestDef) mzkreq.clone();
			cpf.mzkret = (MzkResultDef) mzkret.clone();

			// ///////////////////// 创建新的付款明细对象
			// 设置券类型
//			种类|名称|金额|汇率|原行数|纸券
			String[] rows = (String[]) couponList.elementAt(index);

/*			if (Convert.toInt(rows[5]) > 0)
			{
				cpf.CouponType = Convert.toInt(rows[5]);
			}*/

			//最大可收积分
			cpf.allowpayje = this.allowpayje;

			// 查询并删除原付款
			// 如果是退货且非扣回时，不删除原付款方式
			if (!(SellType.ISBACK(salehead.djlb) && !saleBS.isRefundStatus()) || GlobalInfo.sysPara.isBackPaymentCover == 'Y')
			{
				if (!deletePayment(index, cpf))
				{
					new MessageBox(Language.apply("删除原付款方式失败！"));

					return false;
				}
			}
			
			//开始进行分摊
			// 创建付款对象
			double kyjf = calkyjf(money);
			if (kyjf != money)
			{
//				new MessageBox("积分无法使用完全\n最接近积分为："+kyjf);
				new MessageBox(Language.apply("积分无法使用完全\n最接近积分为：{0}" ,new Object[]{kyjf+""}));
				return false;
			}
			
			if (cpf.createSalePay(String.valueOf(getSjfk())))
			{
				cpf.salepay.payname = rows[1];
				cpf.salepay.str4 = rows[0];
				
				cpf.salepay.idno = cpf.salepay.str4 + "," + String.valueOf(money) + "," + String.valueOf(getSjfk());
				
				alreadyAddSalePay = true;
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
				// 开始分摊到各个商品
				paymentApportion(cpf.salepay,cpf);
				
				return true;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return false;
	}
	
	private void paymentApportion(SalePayDef salepay, PaymentJfNew cpf)
	{
		// TODO Auto-generated method stub
		CalcRulePopDef calPop;
		for (int i = 0; i < vi.size(); i++)
		{
			calPop = (CalcRulePopDef) vi.elementAt(i);
		
			for (int y = 0; y < calPop.row_set.size();y++)
			{
				String[] row = (String[]) calPop.row_set.elementAt(y);
				//商品行号，可收积分类型数量，合计金额，可收金额（默认为-1，后面计算）
				
				if(Convert.toDouble(row[3]) <= 0) continue;
				
				SpareInfoDef spinfo = (SpareInfoDef) saleBS.goodsSpare.elementAt(Convert.toInt(row[0]));

				
				if (spinfo.payft == null)
				{
					spinfo.payft = new Vector();
				}
				String[] ft = new String[] { String.valueOf(salepay.num5), salepay.paycode, salepay.payname, row[3] };
				spinfo.payft.add(ft);
			}				
		}
	}

	//返回实际扣款总金额
	public double getSjfk()
	{
		CalcRulePopDef calPop;
		double hjje = 0;
		for (int i = 0; i < vi.size(); i++)
		{
			calPop = (CalcRulePopDef) vi.elementAt(i);
			
				for (int y = 0; y < calPop.row_set.size();y++)
				{
					String[] row = (String[]) calPop.row_set.elementAt(y);
					//商品行号，可手机分类型数量，合计金额，可收金额（默认为-1，后面计算）
					hjje = ManipulatePrecision.doubleConvert(hjje+Convert.toDouble(row[3]));
				}				
		}
		return hjje;
	}

	
	public void addMessage(PaymentJfNew cpf, StringBuffer bufferStr)
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
		if (NetService.getDefault().sendHykSaleNew(req, ret))
		{
    		if (saleBS != null)
        	{
        		if (!saleBS.checkCust(ret))
        		{
        			return false;
        		}
        	}
    		return true;
		}
		else return false;
	}

	// 判断是否是返券卡
	public boolean isCzFile(String filename)
	{
		if (filename.startsWith("NewCus_") && filename.endsWith(".cz"))
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
		return ConfigClass.LocalDBPath + "/NewCus_" + mzkreq.seqno + ".cz";
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
					String[] title = { Language.apply("会员卡类型") };
					int[] width = { 500 };

					int choice = new MutiSelectForm().open(Language.apply("请选择卡类型"), title, width, con);
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
			if ((GlobalInfo.sysPara.backRefundMSR.charAt(0) == 'Y') && ((salepay.payno == null) || (salepay.payno.length() <= 0)))
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
				if (!txt.open(Language.apply("请刷原小票里的会员卡或顾客打折卡"), Language.apply("会员号"), Language.apply("请将会员卡或顾客打折卡从刷卡槽刷入"), cardno, 0, 0, false, getAccountInputMode())) { return false; }
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
	public boolean findJfInfo(String track1, String track2, String track3, ArrayList fjklist)
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

		return DataService.getDefault().getJfInfo(mzkreq, fjklist);
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

					//种类|名称|金额|汇率|原行数|纸券
					String[] lines = { line[0], line[1], line[2], line[3], "-1", line[4] };
					couponList.add(lines);
					// }

				}

				return true;
			}
			else
			{
				new MessageBox(Language.apply("当前没有积分余额\n或\n此积分已经消费或者过期"));
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
			new MessageBox("Exception initList :"+er.getMessage());
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
			//传入付款代码,卡号,积分类型,卡内可用积分,原付款位置(这个位置已经输入过一次)
			allowpayje = getCouponJe(paymode.code, mzkret.cardno, rows[0], Convert.toDouble(rows[2]), Convert.toInt(rows[4]));
			
//			line +="可收积分为: " + ManipulatePrecision.doubleToString(allowpayje);
			line += Language.apply("可收积分为:{0}" ,new Object[]{ManipulatePrecision.doubleToString(allowpayje)});
		}
		else
		{
			/*double hl = 1;
			line = "";//"汇率为 " + ManipulatePrecision.doubleToString(hl);
*/
			/*if (GlobalInfo.sysPara.fjkkhhl != null && GlobalInfo.sysPara.fjkkhhl.length() > 0 && saleBS.isRefundStatus() && !SellType.ISCOUPON(saleBS.saletype))
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
			}*/

			/*allowpayje = getBackCouponJe(paymode.code, mzkret.cardno, rows[0], String.valueOf(hl), Convert.toInt(rows[4]));

			if (sjje > 0)
			{
				allowpayje = Math.min(ManipulatePrecision.doubleConvert(sjje / Convert.toDouble(rows[3])), allowpayje);
			}

			line += "\n剩余未付金额为:" + allowpayje;*/
		}

/*		if (SellType.ISCOUPON(saleBS.saletype) && SellType.ISBACK(saleBS.saletype) && saleBS.isRefundStatus())
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
		}*/
		return line;
	}
	


	// 查询可付金额
	public double getCouponJe(String paycode, String payno, String couponID, double kjf, int oldpayindex)
	{
		try{
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
		
			//先分组，合并相同积分折现的商品
			for (int i = 0; i < saleBS.goodsAssistant.size(); i++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) saleBS.saleGoods.elementAt(i);
				SpareInfoDef spinfo = (SpareInfoDef) saleBS.goodsSpare.elementAt(i);
				
				if (spinfo.char2 == 'Y') continue;
				
				//积分规则 种类1|门槛1|上线1|积分1|抵现金1;
				if(spinfo.memo1 != null && spinfo.memo1.length() > 0)
				{
					int indexmemo = 0;
					//检查商品能否收取COUPON类型积分
					if ((indexmemo = spinfo.memo1.indexOf(couponID+"|")) >=0)
					{
						String jfrule = "";
						//截取对应积分规则 jfrule代表当前COUPON的收取规则
						if (spinfo.memo1.indexOf(";", indexmemo) >=0) jfrule = spinfo.memo1.substring(indexmemo,spinfo.memo1.indexOf(";", indexmemo));
						else jfrule = spinfo.memo1.substring(indexmemo);
						
						int y=0;
						//此商品能收积分类型的数量
						int num1 = spinfo.memo1.split(";").length;
						//查询有没有同类型的积分规则
						for (y=0; y< vi.size(); y++)
						{
							CalcRulePopDef crpd1 = (CalcRulePopDef) vi.elementAt(y);
							//如果存在相同的积分折现规则
							if (jfrule.equals(crpd1.jfrule))
							{
								//计算同类型积分商品的总金额
								crpd1.hjje = ManipulatePrecision.doubleConvert(crpd1.hjje + sgd.hjje - sgd.hjzk);
								
								// 商品行号，可收积分类型数量，这一行商品合计金额，积分折现可收金额（默认为-1，后面计算）
								String[] a= {String.valueOf(i),String.valueOf(num1),String.valueOf(getValidValue(i,oldpayindex)),"-1"};
								//可收积分类型数量比较
								int z = 0;
								for(z = 0; z < crpd1.row_set.size(); z++)
								{
									String[] z1 = (String[])crpd1.row_set.elementAt(z);
									if (num1 <= Convert.toInt(z1[1]))
									{
										crpd1.row_set.add(z,a);	
										break;
									}
								}
								
								//如果可收积分类型最大，放到最后面
								if (z >= crpd1.row_set.size())
								{
									crpd1.row_set.add(a);	
								}
								else
								{
									break;
								}
	
							}
						}
						
						if (y >= vi.size())
						{
							CalcRulePopDef crpd = new CalcRulePopDef();
							crpd.jfrule = jfrule;	//规则
							
							crpd.hjje = ManipulatePrecision.doubleConvert(sgd.hjje - sgd.hjzk);  		//合计金额
							crpd.mk = Convert.toDouble(jfrule.split("\\|")[1]);				//门槛
							crpd.limit = Convert.toDouble(jfrule.split("\\|")[2]);					//上限
							if (crpd.limit == 0) crpd.limit = kjf;
							crpd.zxjf = Convert.toDouble(jfrule.split("\\|")[3]);
							
							if (crpd.zxjf == 0)
								continue;
							
							crpd.zxje = Convert.toDouble(jfrule.split("\\|")[4]);
							crpd.zxbl = (crpd.zxjf/crpd.zxje);
							if (crpd.row_set == null) crpd.row_set = new Vector();
							
							//商品行号，可手机分类型数量，合计金额，可收金额（默认为-1，后面计算）
							String[] a= {String.valueOf(i),String.valueOf(num1), String.valueOf(getValidValue(i,oldpayindex)),"-1"};
							crpd.row_set.add(a);
							
							//根据折现比例计算塞入位置
							int z = 0;
							for (z = 0; z < vi.size(); z++)
							{
								CalcRulePopDef crpd2 = (CalcRulePopDef) vi.elementAt(z);
								if (crpd.zxbl < crpd2.zxbl)
								{
									vi.add(z,crpd);
									break;
								}
							}
							
							if (z >= vi.size()) vi.add(crpd);
						}
					}
				}
			}
		}catch(Exception er)
		{
			new MessageBox("getCouponJe: "+er.getMessage());
		}
		
		return calkyjf(kjf);
		
	}
	
	//传入输入积分，计算出可收最大积分
	public double calkyjf(double kjf)
	{
		try
		{
			CalcRulePopDef calPop;
	//		 计算满收金额
			double hjftjf = 0; // 可收金额
			// double syze = 0; // 剩余总额
			for (int i = 0; i < vi.size(); i++)
			{
				calPop = (CalcRulePopDef) vi.elementAt(i);
				calPop.yftjf = 0;
				// 如果合计金额大于门槛,计算每个商品分摊
				if (calPop.hjje >= calPop.mk)
				{
					double ksjf = kjf - hjftjf; 
					if (ksjf > (calPop.limit - calPop.yftjf)) ksjf = ManipulatePrecision.doubleConvert((calPop.limit - calPop.yftjf));
					
					int num1 = ManipulatePrecision.integerDiv(ksjf, calPop.zxjf);
					double cxje = ManipulatePrecision.doubleConvert(num1 * calPop.zxje);
					ksjf =  ManipulatePrecision.doubleConvert (num1 * calPop.zxjf);
					double hjje = cxje;
					for (int y = 0; y < calPop.row_set.size();y++)
					{
						String[] row = (String[]) calPop.row_set.elementAt(y);
						//商品行号，可手机分类型数量，合计金额，可收金额（默认为-1，后面计算）
						if (Convert.toDouble(row[2]) < cxje)
						{
							row[3] = row[2];
							cxje = ManipulatePrecision.doubleConvert(cxje - Convert.toDouble(row[2]));
						}
						else
						{
							row[3] = String.valueOf(cxje);
							cxje = 0;
						}
					}
	
					//如果大于0，代表有剩余，计算出实际计算出的积分
					if (cxje > 0)
					{
						int num2 = ManipulatePrecision.integerDiv(ManipulatePrecision.doubleConvert(hjje-cxje), calPop.zxje);
						ksjf= ManipulatePrecision.doubleConvert(num2 * calPop.zxjf);
					}
	
					hjftjf = ManipulatePrecision.doubleConvert(hjftjf + ksjf);
					calPop.yftjf = ManipulatePrecision.doubleConvert(calPop.yftjf + ksjf);
					
				}
				else
				{
					vi.remove(i);
					i--;
				}
			}
			
			return hjftjf;
		}catch(Exception er)
		{
			new MessageBox("calkyjf "+er.getMessage());
		}
		return kjf;
	}

	public boolean isSameTypePayment(SalePayDef sp)
	{
		try
		{
			// 券类
			String[] pay = CreatePayment.getDefault().getCustomPaymentDefine("PaymentJfNew");

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
					if (sp.str4 .equals(payidno)) { return (int) sp.num5; }
				}
			}
		}

		return -1;
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
	
	public void specialDeal (PaymentJfNewEvent event)
	{
	}
	
    public boolean createJfExchangeSalePay(double je,double jf,JfSaleRuleDef jfrd)
    {
    	// 创建SalePay对象
    	if (!createSalePayObject(String.valueOf(je)))
    	{
    		return false;
    	}
    	
    	// 记录账号信息到SalePay
    	if (!saveFindMzkResultToSalePay())
    	{
    		return false;
    	}
    	
    	//要扣的积分,XX积分,兑单个商品XX金额		
//    	salepay.idno = jf + "," + String.valueOf(jfrd.jf) + "," + String.valueOf(je / (jf/jfrd.jf)) + "," + salehead.ysyjh + "," + salehead.yfphm;

    	// 标记为积分换购	
		salepay.payname = Language.apply("积分换购");
    	salepay.memo = "2";
    	
    	return true;
    }
    
    protected boolean setRequestDataByAccount()
    {
		//得到消费序号
		long seqno = getMzkSeqno();
		if (seqno <= 0) return false;
		
		// 打消费交易包
		mzkreq.fphm = salehead.fphm;
		mzkreq.seqno = seqno;
		mzkreq.syjh = ConfigClass.CashRegisterCode;
		mzkreq.mktcode = GlobalInfo.sysPara.mktcode;
		mzkreq.syyh = GlobalInfo.posLogin.gh;	
		mzkreq.paycode = salepay.paycode;
		mzkreq.invdjlb = ((salehead != null ) ? salehead.djlb : "");
		
		// 告诉后台过程磁道信息是存放的是卡号,只采用卡号记账方式,不使用磁道记账方式
		mzkreq.track1 = "CARDNO";
		mzkreq.track2 = salepay.payno;
		mzkreq.track3 = salepay.str4;
		
		String salepaylist[] = salepay.idno.split(",");
		mzkreq.je = Double.parseDouble(salepaylist[1]);
		mzkreq.memo = salepay.idno + "," + salehead.ysyjh + "," + salehead.yfphm;
		
		return true;
    }
    
    private boolean checkUsed()
	{
    	if (saleBS != null)
    	{
    		if (!saleBS.checkCust())
    		{
    			return false;
    		}
    	}
		return true;
	}
    
    /*
    public boolean findMzk(String track1, String track2, String track3)
    {
    	if (super.findMzk(track1, track2, track3))
    	{
    		if (saleBS != null)
        	{
        		if (!saleBS.checkCust(mzkret))
        		{
        			return false;
        		}
        	}
    		return true;
    	}
    	
    	return false;
    }
    */
}
