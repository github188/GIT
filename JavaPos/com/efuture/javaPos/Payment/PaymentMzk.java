package com.efuture.javaPos.Payment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import org.eclipse.swt.events.KeyEvent;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;

public class PaymentMzk extends Payment
{
	public static boolean recycleStatus = true;

	public MzkRequestDef mzkreq = new MzkRequestDef();
	public MzkResultDef mzkret = new MzkResultDef();

	public int mzkTrackType = -1;
	private int intErrPsw = 0;
	public boolean isControlGz = false;
	public boolean paynoMsrflag = false;
	public boolean info = false;

	public boolean messDisplay = true; // 在连续付款方式时，不弹出提示
	
	public PaymentMzk()
	{
	}

	public PaymentMzk(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	// 该构造函数用于红冲小票时,通过小票付款明细创建对象
	public PaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public void initPayment(SalePayDef pay, SaleHeadDef head)
	{
		super.initPayment(pay, head);

		setRequestDataBySalePay();
	}

	public SalePayDef inputPay(String money)
	{
		try
		{
			// 退货小票不能使用,退货扣回按销售算
			if (checkMzkIsBackMoney() && GlobalInfo.sysPara.thmzk != 'Y')
			{
//				new MessageBox("退货时不能使用" + paymode.name);
				new MessageBox(Language.apply("退货时不能使用{0}" ,new Object[]{paymode.name}));
				return null;
			}

			// 先检查是否有冲正未发送
			if (!sendAccountCz())
				return null;
			
			//是否通过外部设备读取卡号
			if(!autoFindCard()) return null;

			// 打开明细输入窗口
			new PaymentMzkForm().open(this, saleBS);

			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return null;
	}
		
	public boolean autoFindCard()
	{		
		return true;
	}
	
	public boolean isAutoFindCard()
	{		
		return false;
	}

	public boolean cancelPay()
	{
		// 如果不是即时记账,则可直接取消付款
		if (GlobalInfo.sysPara.cardrealpay != 'Y' || salepay.batch == null || salepay.batch.trim().length() <= 0)
		{
			// 未记账,直接返回,取消付款
			return true;
		}
		else
		{
			// 即时记账模式,取消已记账的付款
			if (GlobalInfo.sysPara.cardrealpay == 'Y')
			{
				if (mzkAccount(false))
				{
					deleteMzkCz();

					return true;
				}
				else
				{
					return false;
				}
			}

			return true;
		}
	}

	public boolean collectAccountPay()
	{
		// 如果不是即时记账,则集中记账
		if (GlobalInfo.sysPara.cardrealpay != 'Y' || salepay.batch == null || salepay.batch.trim().length() <= 0)
		{
			// 付款记账
			if (mzkAccount(true))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			// 已记账,直接返回
			return true;
		}
	}

	public boolean collectAccountClear()
	{
		// 删除相应的冲正记录
		return deleteMzkCz();
	}

	public boolean checkMzkIsBackMoney()
	{
		// 退货交易且不是扣回状态,按退钱处理
		if (SellType.ISBACK(salehead.djlb) && saleBS != null && !saleBS.isRefundStatus())
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean checkMzkMoneyValid()
	{
		try
		{
			// 退货扣回付款时付款算消费
			if (checkMzkIsBackMoney())
			{
				// 检查退款后余额是否大于卡面值
				if (mzkret.money > 0 && salepay.ybje + mzkret.ye > mzkret.money)
				{
					new MessageBox(Language.apply("退款余额不能超过面值!"));

					return false;
				}
			}
			else
			{
				// 检查金额是否超过卡余额
				if (ManipulatePrecision.doubleCompare(salepay.ybje, mzkret.ye, 2) > 0)
				{
					new MessageBox(Language.apply("卡内余额不足!"));

					return false;
				}

				// 检查金额是否超过限制金额
				if (this.allowpayje >= 0 && ManipulatePrecision.doubleCompare(salepay.ybje, this.allowpayje, 2) > 0)
				{
					new MessageBox(Language.apply("输入金额超过允许支付限额!"));

					return false;
				}

				// 判断是否是可回收的卡类型
				if (!recycle()) { return false; }
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean isRealAccountPay()
	{
		if (GlobalInfo.sysPara.cardrealpay == 'Y') { return true; }

		return false;
	}

	public boolean realAccountPay()
	{
		if (GlobalInfo.sysPara.cardrealpay == 'Y')
		{
			// 付款即时记账
			if (mzkAccount(true))
			{
				deleteMzkCz();

				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			// 不即时记账
			return true;
		}
	}

	public boolean createSalePay(String money)
	{
		try
		{
			// 创建付款信息
			if (super.createSalePay(money))
			{
				// salepay对象有效
				if (checkMzkMoneyValid())
				{
					// 记录帐号信息
					if (saveFindMzkResultToSalePay())
					{
						// 显示余额提示
						showAccountYeMsg();

						// 需要即时记账
						if (realAccountPay())
							return true;
					}
				}
			}
		}
		catch (Exception ex)
		{
			new MessageBox(Language.apply("生成交易付款对象失败\n\n") + ex.getMessage());
			ex.printStackTrace();
		}

		//
		salepay = null;
		return false;
	}

	public void showAccountYeMsg()
	{
		if (!messDisplay)
			return;

		StringBuffer info = new StringBuffer();

		String text = Language.apply("付");
		double ye = getAccountYe() - salepay.je;
		if (checkMzkIsBackMoney())
		{
			text = Language.apply("退");
			ye = getAccountYe() + salepay.je;
		}
//		info.append("卡内余额为: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(getAccountYe()), 0, 12, 12, 1) + "\n");
		info.append(Language.apply("卡内余额为: {0}\n" ,new Object[]{Convert.appendStringSize("", ManipulatePrecision.doubleToString(getAccountYe()), 0, 12, 12, 1)}));
		info.append(Language.apply("本次") + text + Language.apply("款额: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(salepay.je), 0, 12, 12, 1) + "\n");
		if (ye > 0)
			info.append(text + Language.apply("款后余额: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(ye), 0, 12, 12, 1) + "\n");

		new MessageBox(info.toString());
	}

	public void setRequestDataBySalePay()
	{
		// 根据salepay生成交易请求包
		mzkreq.type = "XX"; // 未确定交易类型
		mzkreq.seqno = 0;
		mzkreq.termno = ConfigClass.CashRegisterCode;
		mzkreq.mktcode = GlobalInfo.sysPara.mktcode;
		mzkreq.syyh = GlobalInfo.posLogin.gh;
		mzkreq.syjh = salehead.syjh;
		mzkreq.fphm = salehead.fphm;
		mzkreq.invdjlb = salehead.djlb;
		mzkreq.paycode = salepay.paycode;
		mzkreq.je = salepay.ybje;
		mzkreq.track1 = "CARDNO"; // 告诉后台过程磁道信息是存放的是卡号
		mzkreq.track2 = salepay.payno;
		mzkreq.track3 = "";
		mzkreq.passwd = "";
		mzkreq.memo = "";
	}

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
		
		if(GlobalInfo.sysPara.isUseNewMzkRange=='Y')
		{//百货新使用范围（精确到单品） WANGYONG ADD BY 2015.1.16
			if (saleBS==null || saleBS.saleGoods==null) return;
			
			String memo="";
			for (int i = 0; i < saleBS.saleGoods.size(); i++)
			{
				SaleGoodsDef sg = (SaleGoodsDef) saleBS.saleGoods.elementAt(i);
				if(sg==null) continue;
				//Code|Gz|Catid|Pp,Code|Gz|Catid|Pp
				if(i>0) memo = memo + ",";
				memo = memo + sg.code + "|" + sg.gz + "|" + sg.catid + "|" + sg.ppcode;			
			}
			mzkreq.memo = memo;
		}
	}

	public int getAccountInputMode()
	{
		if (paymode != null && paymode.type == '5')
			return TextBox.MsrKeyInput;
		return TextBox.MsrInput;
	}

	public boolean isPasswdInput()
	{
		if (mzkret.ispw == 'Y' && !GlobalInfo.sysPara.cardpasswd.equals("Y"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean compareInputPasswd(String pwd)
	{
		if (!this.pswInputValid())
		{
			return false;
		}
		else if (!pwd.equals(mzkret.cardpwd))
		{
			new MessageBox(Language.apply("输入的验证码不正确!"));

			return false;
		}

		//
		mzkreq.passwd = pwd;

		return true;
	}

	// 密码输入次数限制验证
	public boolean pswInputValid()
	{
		if (null != mzkret.func && mzkret.func.length() >= 5 && Integer.parseInt(mzkret.func.substring(4, 5)) > 0)
		{
			if (Integer.parseInt(mzkret.func.substring(4, 5)) == intErrPsw)
			{
				new MessageBox(Language.apply("输入验证码错误次数过多，该卡即将冻结!"));
				mzkreq.type = "06"; // 设置交易类型为“冻结”
				sendMzkSale(mzkreq, mzkret);
				return false;
			}
			else if (Integer.parseInt(mzkret.func.substring(4, 5)) <= intErrPsw)
			{
				new MessageBox(Language.apply("输入验证码错误次数过多，该卡已冻结!"));
				return false;
			}

			intErrPsw++; // 密码输入的次数
		}
		return true;
	}

	protected boolean getPasswdBeforeFindMzk(StringBuffer passwd)
	{
		if (GlobalInfo.sysPara.cardpasswd.equals("Y"))
		{
			TextBox txt = new TextBox();

			if (!txt.open(Language.apply("请输入密码"), "PASSWORD", Language.apply("需要先输入卡密码以后才能查询卡资料"), passwd, 0, 0, false, TextBox.AllInput)) { return false; }
		}
		else if (GlobalInfo.sysPara.cardpasswd.trim().length() > 1 && GlobalInfo.sysPara.cardpasswd.trim().charAt(0) == 'Y' && GlobalInfo.sysPara.cardpasswd.indexOf(",") > 0)
		{
			if ((GlobalInfo.sysPara.cardpasswd + ",").indexOf("," + paymode.code + ",") > 0)
			{
				TextBox txt = new TextBox();

				if (!txt.open(Language.apply("请输入密码"), "PASSWORD", Language.apply("需要先输入卡密码以后才能查询卡资料"), passwd, 0, 0, false, TextBox.AllInput)) { return false; }
			}
		}
		return true;
	}

	public int choicTrackType()
	{
		mzkTrackType = -1;

		return mzkTrackType;
	}

	public String[] parseTrack(String track1, String track2, String track3)
	{
		String[] s = new String[3];

		s[0] = track1;
		s[1] = track2;
		s[2] = track3;

		return s;
	}

	public boolean findMzkInfo(String track1, String track2, String track3)
	{
		if ((track1 == null || track1.trim().length() <= 0) && (track2 == null || track2.trim().length() <= 0) && (track3 == null || track3.trim().length() <= 0))
		{
			new MessageBox(Language.apply("磁道数据为空!"));
			return false;
		}

		// 解析磁道
		String[] s = parseTrack(track1, track2, track3);
		if (s == null)
			return false;
		track1 = s[0];
		track2 = s[1];
		track3 = s[2];

		// 设置请求数据
		setRequestDataByFind(track1, track2, track3);

		// 设置用户输入密码
		StringBuffer passwd = new StringBuffer();
		if (!getPasswdBeforeFindMzk(passwd))
		{
			return false;
		}
		else
		{
			mzkreq.passwd = passwd.toString();
		}

		//
		return DataService.getDefault().getMzkInfo(mzkreq, mzkret);
	}

	public boolean findMzk(String track1, String track2, String track3)
	{
		if ((track1 == null || track1.trim().length() <= 0) && (track2 == null || track2.trim().length() <= 0) && (track3 == null || track3.trim().length() <= 0))
		{
			new MessageBox(Language.apply("磁道数据为空!"));
			return false;
		}

		// 解析磁道
		String[] s = parseTrack(track1, track2, track3);
		if (s == null)
			return false;
		track1 = s[0];
		track2 = s[1];
		track3 = s[2];

		// 设置请求数据
		setRequestDataByFind(track1, track2, track3);

		// 设置用户输入密码
		StringBuffer passwd = new StringBuffer();
		if (!getPasswdBeforeFindMzk(passwd))
		{
			return false;
		}
		else
		{
			mzkreq.passwd = passwd.toString();
		}

		//
		return sendMzkSale(mzkreq, mzkret);
	}

	public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
	{
		/*
		 * // yinliang test if (ConfigClass.isDeveloperMode()) { ret.cardno =
		 * "0123456789"; ret.cardname = "模拟测试卡"; ret.money = 100; ret.ye = 100;
		 * new MessageBox("当前为模拟测试数据的虚假数据,仅供测试部分流程!!!"); return true; } else
		 */
		return DataService.getDefault().sendMzkSale(req, ret);

	}

	protected boolean needFindAccount()
	{
		return true;
	}

	protected String getDisplayStatusInfo()
	{
		// yinliang test
		// mzkret.func = "Y01Y";
		// mzkret.value3 = 100;

		try
		{
			String line = "";
			if (!checkMzkIsBackMoney())
			{
				// 如果卡有回收功能,显示回收提示
				double ye = -1;
				if (isRecycleType())
				{
					// 定义了回收功能键模式
					if (NewKeyListener.searchKeyCode(GlobalVar.MzkRecycle) > 0)
					{
						if (recycleStatus)
						{
							ye = mzkret.ye;
//							line = "回收状态(开)\n\n工本费为:" + ManipulatePrecision.doubleToString(mzkret.value3) + " 元\n\n有效金额:" + ManipulatePrecision.doubleToString(ye) + " 元";
							line = Language.apply("回收状态(开)\n\n工本费为:{0} 元\n\n有效金额:{1} 元" ,new Object[]{ManipulatePrecision.doubleToString(mzkret.value3) ,ManipulatePrecision.doubleToString(ye)});
						}
						else
						{
							ye = mzkret.ye - mzkret.value3;
//							line = "回收状态(关)\n\n工本费为:" + ManipulatePrecision.doubleToString(mzkret.value3) + " 元\n\n有效金额:" + ManipulatePrecision.doubleToString(ye) + " 元";
							line = Language.apply("回收状态(关)\n\n工本费为:{0} 元\n\n有效金额:{1} 元" ,new Object[]{ManipulatePrecision.doubleToString(mzkret.value3) ,ManipulatePrecision.doubleToString(ye)});
						}
					}
					else
					{
						ye = mzkret.ye;
//						line = "工本费为:" + ManipulatePrecision.doubleToString(mzkret.value3) + " 元\n\n有效金额:" + ManipulatePrecision.doubleToString(ye) + " 元";
						line = Language.apply("工本费为:{0} 元\n\n有效金额:{1} 元" ,new Object[]{ManipulatePrecision.doubleToString(mzkret.value3) ,ManipulatePrecision.doubleToString(ye)});
					}
				}

				// 计算并显示付款限制
				if (!this.allowpayjealready)
					this.allowpayje = ManipulatePrecision.doubleConvert(calcPayRuleMaxMoney() / paymode.hl);
				if (this.allowpayje >= 0 && ye >= 0)
					this.allowpayje = Math.min(allowpayje, ye);
				else if (ye >= 0)
					this.allowpayje = ye;
				if (this.allowpayje >= 0)
				{
					this.allowpayje = Math.max(this.allowpayje, saleBS.getDetailOverFlow(this.allowpayje));
//					String allowstr = "付款限制:" + ManipulatePrecision.doubleToString(allowpayje) + " 元";
					String allowstr = Language.apply("付款限制:{0} 元" ,new Object[]{ManipulatePrecision.doubleToString(allowpayje)});
					if (line.length() > 0)
						line += "\n\n" + allowstr;
					else
						line += allowstr;
				}
			}
			else
			{
				if (mzkret.money > 0)
				{
//					line = "面值为:" + ManipulatePrecision.doubleToString(mzkret.money) + " 元\n\n退款后卡余额不能大于面值";
					line = Language.apply("面值为:{0} 元\n\n退款后卡余额不能大于面值" ,new Object[]{ManipulatePrecision.doubleToString(mzkret.money)});
				}
				else
				{
					line = "";
				}
			}

			// 显示面值卡返回的提示信息
			if (mzkret.str3 != null && mzkret.str3.length() > 0)
			{
				if (line.length() > 0)
					line += "\n" + mzkret.str3;
				else
					line += mzkret.str3;
			}

			return line;
		}
		catch (Exception er)
		{
			er.printStackTrace();
			return "";
		}
	}

	public double calcPayRuleMaxMoney()
	{
		if (GlobalInfo.sysPara.havePayRule == 'Y' || GlobalInfo.sysPara.havePayRule == 'A')
		{
			return super.calcPayRuleMaxMoney();
		}
		else
		{
			if(GlobalInfo.sysPara.isUseNewMzkRange=='Y')
			{//百货新使用范围（精确到单品） WANGYONG ADD BY 2015.1.16
				//mzkret.str2="0,1";//"2010202003";
				String[] goodsList = null;;//mzkret.str2;
				if(mzkret.str2!=null && mzkret.str2.length()>0) goodsList = mzkret.str2.split(",");
				if(goodsList==null || goodsList.length!=saleBS.saleGoods.size())
				{
					return 0;//未找到匹配的规则或规则不正确
				}
				
				double hjje = 0;
				try
				{
					for (int i = 0; i < saleBS.saleGoods.size(); i++)
					{
						SaleGoodsDef sg = (SaleGoodsDef) saleBS.saleGoods.elementAt(i);
						SpareInfoDef spinfo = (SpareInfoDef) saleBS.goodsSpare.elementAt(i);
						boolean isallow = false;
						double ftje = 0;
						if (goodsList[i].trim().equalsIgnoreCase("0"))
						{
							if (spinfo == null)
								continue;

							if (spinfo.payft != null)
							{
								for (int j = 0; j < spinfo.payft.size(); j++)
								{
									String[] s = (String[]) spinfo.payft.elementAt(j);
									ftje += Convert.toDouble(s[3]);
								}
							}
							isallow = true;

						}
						if (isallow || goodsAllowApportionPay(i))
						{
							double maxfdje = sg.hjje - saleBS.getZZK(sg) - ftje;

							hjje += maxfdje;
						}
					}


					if (hjje > 0)
					{
						// 标记控制柜组
						isControlGz = true;
					}
				}
				catch (Exception er)
				{
					er.printStackTrace();
				}

				return hjje;
			}
			else
			{
				String gzlist = mzkret.str2;
				if (gzlist == null || gzlist.trim().length() <= 1)
					return -1;
				
				if (gzlist.trim().equals("ONLINE"))
				{
					String infolist = "";
				
					for (int i = 0; i < saleBS.saleGoods.size(); i++)
					{
						SaleGoodsDef sg = (SaleGoodsDef) saleBS.saleGoods.elementAt(i);
						//SpareInfoDef spinfo = (SpareInfoDef) saleBS.goodsSpare.elementAt(i);
						infolist += "|"+sg.code+","+sg.gz+","+sg.ppcode+","+sg.catid;
					}
					String[] ret = new String[3];
					if (!NetService.getDefault().findmzkscope(mzkret.cardno, infolist.substring(1), ret))
					{
						return 0;
					}
					else
					{
						String rule = ret[0];
						double hjje = 0;
						String gzlist1 = "";
						for (int i = 0; i < saleBS.saleGoods.size(); i++)
						{
							if (rule.charAt(i) != 'Y') continue;
							
							SaleGoodsDef sg = (SaleGoodsDef) saleBS.saleGoods.elementAt(i);
							SpareInfoDef spinfo = (SpareInfoDef) saleBS.goodsSpare.elementAt(i);
							//boolean isallow = false;
							double ftje = 0;

							if (spinfo == null)
								continue;
							
							gzlist1 += ","+sg.gz;

							if (spinfo.payft != null)
							{
								for (int j = 0; j < spinfo.payft.size(); j++)
								{
									String[] s = (String[]) spinfo.payft.elementAt(j);
									ftje += Convert.toDouble(s[3]);
								}
							}

							double maxfdje = sg.hjje - saleBS.getZZK(sg) - ftje;
							hjje += maxfdje;
						
						}
						if (hjje > 0)
						{
							isControlGz = true;
							//这种模式下，不允许用实时记账
							GlobalInfo.sysPara.cardrealpay = 'N';
							this.mzkret.str2 = gzlist1.substring(1);
						}
						
						return ManipulatePrecision.doubleConvert(hjje);
					}
				}
				

				gzlist = "," + mzkret.str2 + ",";
				double hjje = 0;
				try
				{
					for (int i = 0; i < saleBS.saleGoods.size(); i++)
					{
						SaleGoodsDef sg = (SaleGoodsDef) saleBS.saleGoods.elementAt(i);
						SpareInfoDef spinfo = (SpareInfoDef) saleBS.goodsSpare.elementAt(i);
						boolean isallow = false;
						double ftje = 0;
						if (gzlist.indexOf("," + sg.gz + ",") >= 0)
						{
							if (spinfo == null)
								continue;

							if (spinfo.payft != null)
							{
								for (int j = 0; j < spinfo.payft.size(); j++)
								{
									String[] s = (String[]) spinfo.payft.elementAt(j);
									ftje += Convert.toDouble(s[3]);
								}
							}
							isallow = true;

						}
						if (isallow || goodsAllowApportionPay(i))
						{
							double maxfdje = sg.hjje - saleBS.getZZK(sg) - ftje;

							hjje += maxfdje;
						}
					}


					if (hjje > 0)
					{
						// 标记控制柜组
						isControlGz = true;
					}
				}
				catch (Exception er)
				{
					er.printStackTrace();
				}

				return hjje;
			} 
		}
	}

	public boolean goodsAllowApportionPay(int sgindex)
	{
		// 无柜组范围
		if (mzkret.str2 == null || mzkret.str2.trim().length() <= 0)
			return true;

		// 标记控制柜组
		isControlGz = true;

		// 检查允许的柜组范围
		boolean allow = false;

		SaleGoodsDef sg = (SaleGoodsDef) saleBS.saleGoods.elementAt(sgindex);
		String[] allowlist = mzkret.str2.split(",");
		for (int j = 0; j < allowlist.length; j++)
		{
			String[] s = allowlist[j].split(":");
			if (s.length >= 2)
			{
				if (sg.code.equals(s[0]) && s[1].equals("1"))
					allow = true;
				else if (sg.gz.equals(s[0]) && s[1].equals("2"))
					allow = true;
				else if (sg.ppcode.equals(s[0]) && s[1].equals("3"))
					allow = true;
				else if (sg.catid.equals(s[0]) && s[1].equals("4"))
					allow = true;
			}
			else
			{
				// -20001321代表此柜组不收
				if (s[0].charAt(0) == '-')
				{
					allow = true;
					if (s[0] != null && s[0].length() > 1 && s[0].substring(1).equals(sg.gz))
						return false;
				}
				else if (sg.gz.equals(s[0]))
					allow = true;
			}
		}

		return allow;
	}

	public double getAccountYe()
	{
		return mzkret.ye;
		/*
		 * double yfje = 0; SalePayDef sp = null; for (int i = 0; i <
		 * saleBS.salePayment.size(); i++) { sp =
		 * (SalePayDef)saleBS.salePayment.get(i); if
		 * (mzkreq.paycode.equals(sp.paycode) && mzkret.cardno.equals(sp.payno))
		 * { yfje += sp.je; } }
		 * 
		 * double ye = mzkret.ye - yfje; if (ye < 0) { ye = 0; } return ye;
		 */
	}

	public boolean changeMzkPass(String track1, String track2, String track3)
	{
		return false;
	}

	public double getAccountAllowPay()
	{
		if (this.allowpayje >= 0)
			return Math.min(this.allowpayje, mzkret.ye);
		else
			return mzkret.ye;
	}

	public double getPayJe(double moneyText)
	{
		double min = 0;
		min = Math.min(moneyText, getAccountAllowPay());
		min = Math.min(min, getAccountYe());
		return min;
	}

	public void setMoneyVisible(PaymentMzkEvent paymentMzkEvent)
	{
	}

	// 判断是否是回收成功
	public boolean recycle()
	{
		// 手工回收标志，需要弹出提示
		if (null != (mzkret.func) && (mzkret.func).length() >= 4 && ((mzkret.ye - salepay.ybje < mzkret.value3)))
		{
			if ("M".equals((mzkret.func).substring(3, 4)))
			{
				new MessageBox(Language.apply("余额低于工本费，此卡可以回收"));
				return true;
			}
		}

		if (isRecycleType())
		{
			// 定义了回收功能键模式
			if (NewKeyListener.searchKeyCode(GlobalVar.MzkRecycle) > 0)
			{
				if (recycleStatus)
				{
					// 设置交易回收标记
					if (mzkret.ye - salepay.ybje > 0 && mzkret.ye - salepay.ybje < mzkret.value3)
					{
//						int ret = new MessageBox("卡内余额还有： " + ManipulatePrecision.doubleToString(mzkret.ye - salepay.ybje) + " 元\n是否回收?", null, true).verify();
						int ret = new MessageBox(Language.apply("卡内余额还有：{0} 元\n是否回收?" ,new Object[]{ManipulatePrecision.doubleToString(mzkret.ye - salepay.ybje)}), null, true).verify();
						if (ret == GlobalVar.Key1 || ret == GlobalVar.Enter)
						{
							return true;
						}
						else
						{
							return false;
						}

					}
					return true;
				}
				else
				{
					if (mzkret.ye - salepay.ybje < mzkret.value3)
					{
						new MessageBox(Language.apply("卡不回收,只能使用有效金额"));
						return false;
					}
				}
			}
			else
			{
				if (mzkret.ye - salepay.ybje < mzkret.value3)
				{
					double balance = mzkret.ye - salepay.ybje;
//					if (new MessageBox("消费后余额为：" + ManipulatePrecision.doubleToString(balance) + " 元\n\n小于工本费：" + ManipulatePrecision.doubleToString(mzkret.value3) + " 元\n\n卡必须回收才能消费", null, true).verify() != GlobalVar.Key1)
					if (new MessageBox(Language.apply("消费后余额为：{0} 元\n\n小于工本费：{1} 元\n\n卡必须回收才能消费" ,new Object[]{ManipulatePrecision.doubleToString(balance),ManipulatePrecision.doubleToString(mzkret.value3)}), null, true).verify() != GlobalVar.Key1)
					{
						return false;
					}
					else
					{
						return true;
					}
				}
			}
		}

		return true;
	}

	// 判断是否具有回收功能
	public boolean isRecycleType()
	{
		return isRecycleType(mzkret.func);
	}

	public boolean isRecycleType(String func)
	{
		if (null != func && func.length() >= 4)
		{
			if ("Y".equals(func.substring(3, 4))) { return true; }
		}
		return false;
	}

	protected String getDisplayAccountInfo()
	{
		return Language.apply("请 刷 卡");
	}

	public String getDisplayCardno()
	{
		return mzkret.cardno;
	}

	// PaymentMzkForm 内passwd的显示
	// false: 明码显示
	// true: 密码显示
	public boolean passwdMode()
	{
		return false;
	}

	public String getPasswdLabel()
	{
		return Language.apply("输入密码");
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
					boolean kh = false;
					if (salepay.je < 0)
						kh = true;

//					if (!txt.open((info == true) ? "上张卡无效，重新刷卡" : "请刷卡" + ((kh == true) ? "(扣回)" : ""), "卡号", "【" + ((salepay != null) ? salepay.payname : "") + "】" + "请从刷卡槽刷入", cardno, 0, 0, false, TextBox.MsrKeyInput)) { return false; }
					if (!txt.open((info == true) ? Language.apply("上张卡无效，重新刷卡") : Language.apply("请刷卡") + ((kh == true) ? Language.apply("(扣回)") : ""), Language.apply("卡号"), "【" + ((salepay != null) ? salepay.payname : "") + "】" + Language.apply("请从刷卡槽刷入"), cardno, 0, 0, false, TextBox.MsrKeyInput)) { return false; }

					String[] track = parseTrack(txt.Track1, txt.Track2, txt.Track3);

					MzkRequestDef mzkreq1 = new MzkRequestDef();
					mzkreq1.type = "05"; // 查询类型
					mzkreq1.seqno = 0;
					mzkreq1.termno = ConfigClass.CashRegisterCode;
					mzkreq1.mktcode = GlobalInfo.sysPara.mktcode;
					mzkreq1.syyh = GlobalInfo.posLogin.gh;
					mzkreq1.syjh = ConfigClass.CashRegisterCode;
					mzkreq1.fphm = GlobalInfo.syjStatus.fphm;
					if (kh)
						mzkreq1.invdjlb = SellType.RETAIL_SALE;
					else
						mzkreq1.invdjlb = ((salehead != null) ? salehead.djlb : "");
					mzkreq1.paycode = ((paymode != null) ? paymode.code : "");
					mzkreq1.je = 0;
					mzkreq1.track1 = track[0];
					mzkreq1.track2 = track[1];
					mzkreq1.track3 = track[2];
					mzkreq1.passwd = "";
					mzkreq1.memo = "";

					MzkResultDef mzkret1 = new MzkResultDef();
					info = true;

					if (!sendMzkSale(mzkreq1, mzkret1))
					{
						new MessageBox(Language.apply("此卡号未找到 或 此卡为不可用状态"));
						continue;
					}

					salepay.payno = mzkret1.cardno;
					salepay.kye = 0;

					paynoMsrflag = true;
					break;
				}
			}
		}

		return true;
	}

	public boolean mzkAccount(boolean isAccount)
	{
		do
		{
			// 退货交易卡号为空时提示刷卡
			paynoMsrflag = false;
			if (!paynoMSR())
				return false;

			// 设置交易类型,isAccount=true是记账,false是撤销
			if (isAccount)
			{
				if (SellType.SELLSIGN(salehead.djlb) > 0)
					mzkreq.type = "01"; // 消费,减
				else
					mzkreq.type = "03"; // 退货,加
			}
			else
			{
				if (SellType.SELLSIGN(salehead.djlb) > 0)
					mzkreq.type = "03"; // 退货,加
				else
					mzkreq.type = "01"; // 消费,减
			}

			// 保存交易数据进行交易
			if (!setRequestDataByAccount())
			{
				if (paynoMsrflag)
				{
					salepay.payno = "";
					continue;
				}
				return false;
			}

			// 先写冲正文件
			if (!writeMzkCz())
			{
				if (paynoMsrflag)
				{
					salepay.payno = "";
					continue;
				}
				return false;
			}

			// 记录面值卡交易日志
			BankLogDef bld = mzkAccountLog(false, null, mzkreq, mzkret);

			// 发送交易请求
			if (!sendMzkSale(mzkreq, mzkret))
			{
				if (paynoMsrflag)
				{
					salepay.payno = "";
					continue;
				}
				
				return false;
			}

			// 记录应答信息, batch标记本付款方式已记账,这很重要
			saveAccountMzkResultToSalePay();

			// 记账完成操作,可用于记录记账日志或其他操作
			return mzkAccountFinish(isAccount, bld);
		} while (true);
	}

	public boolean mzkAccountFinish(boolean isAccount)
	{
		return mzkAccountFinish(isAccount, null);
	}

	public boolean mzkAccountFinish(boolean isAccount, BankLogDef bld)
	{
		if (bld != null)
			mzkAccountLog(true, bld, mzkreq, mzkret);

		return true;
	}

	public BankLogDef mzkAccountLog(boolean success, BankLogDef bld, MzkRequestDef req, MzkResultDef ret)
	{
		try
		{
			if (GlobalInfo.sysPara.usemzklog != 'Y')
				return null;

			if (!success)
			{
				// 记录开始交易日志
				BankLogDef newbld = new BankLogDef();
				Object obj = GlobalInfo.dayDB.selectOneData("select max(rowcode) from BANKLOG");
				if (obj == null)
					newbld.rowcode = 1;
				else
					newbld.rowcode = Integer.parseInt(String.valueOf(obj)) + 1;
				newbld.rqsj = ManipulateDateTime.getCurrentDateTime();
				newbld.syjh = req.syjh;
				newbld.fphm = req.fphm;
				newbld.syyh = req.syyh;
				newbld.type = req.type;
				newbld.je = req.je;
				if (req.type.equals("01"))
					newbld.typename = Language.apply("消费");
				else if (req.type.equals("02"))
					newbld.typename = Language.apply("消费冲正");
				else if (req.type.equals("03"))
					newbld.typename = Language.apply("退货");
				else if (req.type.equals("04"))
					newbld.typename = Language.apply("退货冲正");
				else if (req.type.equals("05"))
					newbld.typename = Language.apply("查询");
				else
					newbld.typename = Language.apply("未知");
				newbld.classname = this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1);
				newbld.trace = req.seqno;
				newbld.oldrq = req.mktcode + "|" + req.invdjlb;
				newbld.bankinfo = req.paycode;
				newbld.cardno = req.track2;
				newbld.memo = req.memo;
				newbld.oldtrace = 0;

				newbld.crc = "";
				newbld.retcode = "";
				newbld.retmsg = "";
				newbld.retbz = 'N';
				newbld.net_bz = 'N';
				newbld.allotje = 0;

				if (!AccessDayDB.getDefault().writeBankLog(newbld))
				{
					new MessageBox(Language.apply("记录储值卡交易日志失败!"));
					return null;
				}

				return newbld;
			}
			else
			{
				if (bld == null)
					return null;

				// 更新交易应答数据
				if (ret != null && !CommonMethod.isNull(ret.cardno))
					bld.cardno = ret.cardno;
				bld.retcode = "00";

				if (bld.retmsg != null && !bld.retmsg.trim().equals(""))
				{
					bld.retmsg = Language.apply("交易成功|") + bld.retmsg;
				}
				else
				{
					bld.retmsg = Language.apply("交易成功");
				}

				bld.retbz = 'Y';
				bld.net_bz = 'N';
				if (NetService.getDefault().sendBankLog(bld))
					bld.net_bz = 'Y';
				AccessDayDB.getDefault().updateBankLog(bld);
				return bld;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	// 保存交易数据进行交易
	protected boolean setRequestDataByAccount()
	{
		// 得到消费序号
		long seqno = getMzkSeqno();
		if (seqno <= 0)
			return false;

		// 打消费交易包
		mzkreq.seqno = seqno;
		mzkreq.je = salepay.ybje;
		mzkreq.syjh = ConfigClass.CashRegisterCode;
		mzkreq.mktcode = GlobalInfo.sysPara.mktcode;
		mzkreq.fphm = GlobalInfo.syjStatus.fphm;
		mzkreq.syyh = GlobalInfo.posLogin.gh;
		mzkreq.paycode = salepay.paycode;
		mzkreq.invdjlb = ((salehead != null) ? salehead.djlb : "");

		// 告诉后台过程磁道信息是存放的是卡号,只采用卡号记账方式,不使用磁道记账方式
		mzkreq.track1 = "CARDNO";
		mzkreq.track2 = salepay.payno;

		return true;
	}

	protected boolean saveFindMzkResultToSalePay()
	{
		salepay.batch = "";
		salepay.payno = mzkret.cardno;
		salepay.kye = mzkret.ye;

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
		if (salepay.kye <= 0 || mzkret.ye > 0 || (mzkret.status != null && mzkret.status.equals("RETURNYE")))
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

	public long getMzkSeqno()
	{
		PrintWriter pw = null;
		BufferedReader br = null;

		try
		{
			// 读取消费序号
			String name = ConfigClass.LocalDBPath + "/SaleSeqno.ini";
			File indexFile = new File(name);

			// 无消费序号文件，产生一个
			if (!indexFile.exists())
			{
				pw = CommonMethod.writeFile(name);
				pw.println("1");
				pw.flush();
				pw.close();
				pw = null;
			}

			// 读取消费序号
			br = CommonMethod.readFile(name);
			String line = null;
			long seq = 0;

			while ((line = br.readLine()) != null)
			{
				if (line.length() <= 0)
				{
					continue;
				}
				else
				{
					seq = Convert.toLong(line.trim());
				}
			}
			br.close();
			br = null;

			// 消费序号+1
			pw = CommonMethod.writeFile(name);

			//9999够用一天了
			if (seq < 9999)
				pw.println(seq + 1);
			else
				pw.println(1);
			pw.flush();
			pw.close();
			pw = null;

			// 防止日期重复，前面加上时间字段如果是2013年10月10日，记录为31010+seq
			//如果按seq为9位，那再加上日期6位，就15位了，但是R5那边记录seqno为Int型，最大只能保存10位数
			//所以，动不动就会出现[Microsoft][SQLServer 2000 Driver for JDBC][SQLServer]从数据类型 bigint 转换为 int 时出错
			//综合考虑，改成5位日期加4位seq，共计9位
			String empty = GlobalInfo.balanceDate.replace("/", "").replace("-", "").substring(3);
			empty = empty + String.valueOf(seq);
			seq = Convert.toLong(empty);
			return seq;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			new MessageBox(Language.apply("读取消费序号失败!\n\n") + e.getMessage().trim());

			return -1;
		}
		finally
		{
			try
			{
				if (pw != null)
					pw.close();
				if (br != null)
					br.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public boolean writeMzkCz()
	{
		FileOutputStream f = null;

		try
		{
			String name = GetMzkCzFile();

			f = new FileOutputStream(name);
			ObjectOutputStream s = new ObjectOutputStream(f);
			s.writeObject(mzkreq);
			s.flush();
			s.close();
			f.close();
			s = null;
			f = null;

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return false;
		}
		finally
		{
			try
			{
				if (f != null)
					f.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public String GetMzkCzFile()
	{
		return ConfigClass.LocalDBPath + "/Mzk_" + mzkreq.seqno + ".cz";
	}

	public boolean autoCreateAccount()
	{
		return true;
	}

	// 判断是否是面值卡
	public boolean isCzFile(String filename)
	{
		if (filename.startsWith("Mzk_") && filename.endsWith(".cz"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean deleteMzkCz()
	{
		return deleteMzkCz(null);
	}

	public boolean deleteMzkCz(String fname)
	{
		try
		{

			String name = null;
			if (fname == null || fname.trim().equals(""))
			{
				name = GetMzkCzFile();
			}
			else
			{
				name = fname;
			}
			File file = new File(name);

			if (file.exists())
			{
				file.delete();
				if (file.exists())
				{
					new MessageBox(Language.apply("冲正文件没有被删除,请检查磁盘!"));

					// 加入日志
//					AccessDayDB.getDefault().writeWorkLog("冲正文件 " + name + " 没有删除成功");
					AccessDayDB.getDefault().writeWorkLog(Language.apply("冲正文件 {0} 没有删除成功" ,new Object[]{name}));

					// 在本地标记此冲正文件需要被删除，待重启删除被占用的文件
					File a = new File(ConfigClass.LocalDBPath + "/DEL_" + name);
					a.createNewFile();

					return false;
				}
				else
				{
					return true;
				}
			}
			else
			{
				return true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();

			new MessageBox(Language.apply("删除冲正文件失败!\n\n") + e.getMessage());

			return false;
		}
	}

	// 发送冲正
	public boolean sendAccountCz()
	{
		FileInputStream f = null;
		boolean ok = false;
		ProgressBox pb = null;

		try
		{
			File file = new File(ConfigClass.LocalDBPath);
			File[] filename = file.listFiles();

			for (int i = 0; i < filename.length; i++)
			{
				if (isCzFile(filename[i].getName()))
				{
					// 读取文件
					String name = filename[i].getAbsolutePath();

					// 检查文件是否为未删除文件
					File a = new File(ConfigClass.LocalDBPath + "/DEL_" + filename[i].getName());
					if (a.exists())
					{
						// 删除冲正文件
						deleteMzkCz(name);
						a.delete();
						continue;
					}

					// 显示冲正进度提示
					if (pb == null)
					{
						pb = new ProgressBox();
						pb.setText(Language.apply("正在发送付款冲正数据,请等待......"));
					}

					f = new FileInputStream(name);
					ObjectInputStream s = new ObjectInputStream(f);

					// 读取冲正数据
					MzkRequestDef req = (MzkRequestDef) s.readObject();

					// 关闭文件
					s.close();
					s = null;
					f.close();
					f = null;

					// 发送冲正交易
					if (!sendAccountCzData(req, name, filename[i].getName()))
						return false;
				}
			}

			ok = true;
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return false;
		}
		finally
		{
			try
			{
				if (f != null)
					f.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			if (pb != null)
				pb.close();
			if (!ok)
			{
				new MessageBox(Language.apply("有冲正数据未发送,不能进行卡交易!"));
			}
		}
	}

	public boolean sendAccountCzData(MzkRequestDef req, String czfile, String czname)
	{
		// 根据冲正文件的原交易类型转换冲正数据包
		if (req.type.equals("01"))
		{
			req.type = "02"; // 消费冲正,加
		}
		else if (req.type.equals("03"))
		{
			req.type = "04"; // 退货冲正,减
		}
		else
		{
			new MessageBox(Language.apply("冲正文件的交易类型无效，请检查冲正文件"));
			return false;
		}

		// 冲正记录
//		String czmsg = "发起[" + czname + "]冲正:" + req.type + "," + req.fphm + "," + req.track2 + "," + ManipulatePrecision.doubleToString(req.je) + ",返回:";
		String czmsg = Language.apply("发起")+"[" + czname + "]"+Language.apply("冲正:") + req.type + "," + req.fphm + "," + req.track2 + "," + ManipulatePrecision.doubleToString(req.je) + ","+Language.apply("返回:");

		// 记录面值卡交易日志
		BankLogDef bld = mzkAccountLog(false, null, req, null);

		// 发送冲正交易
		MzkResultDef ret = new MzkResultDef();

		if (!sendMzkSale(req, ret))
		{
			// 记录日志表明发送过冲正数据
			AccessDayDB.getDefault().writeWorkLog(czmsg + Language.apply("失败"), StatusType.WORK_SENDERROR);

			return false;
		}
		else
		{
			// 记录应答日志
			mzkAccountLog(true, bld, req, ret);

			// 记录日志表明发送过冲正数据
			AccessDayDB.getDefault().writeWorkLog(czmsg + Language.apply("成功"), StatusType.WORK_SENDERROR);

			// 冲正发送成功,删除冲正文件
			deleteMzkCz(czfile);
			return true;
		}
	}

	// 分摊是否在基类里分摊---防止多重重载的情况下不能调用基类
	// 判断是否为受限付款方式，如果是，折在基类里，如果不是折不分摊或者走手工输入原则
	public boolean isBaseApportion()
	{
		if (isControlGz)
			return true;

		return false;
	}

	public boolean allowMzkOffline()
	{
		return false;
	}

	// 自动计算付款金额,并生成付款方式
	public boolean AutoCalcMoney()
	{
		return false;
	}

	public String getInitMessage()
	{
		return "";
	}

	public boolean getExtRun()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void specialDeal(PaymentMzkEvent event)
	{
	}

	public void setPwdAndYe(PaymentMzkEvent event, KeyEvent e)
	{
		if (isPasswdInput())
		{
			// 显示密码
			event.yeTips.setText(getPasswdLabel());
			event.yeTxt.setVisible(false);
			event.pwdTxt.setVisible(true);
			event.yeTxt.setText(ManipulatePrecision.doubleToString(getAccountYe()));

			if (e != null)
				e.data = "focus";
			event.pwdTxt.setFocus();
			event.pwdTxt.selectAll();
		}
		else
		{
			// 显示余额
			event.yeTips.setText(Language.apply("账户余额"));
			event.yeTxt.setVisible(true);
			event.pwdTxt.setVisible(false);
			event.yeTxt.setText(ManipulatePrecision.doubleToString(getAccountYe()));

			// 输入金额
			if (e != null)
				e.data = "focus";
			event.moneyTxt.setFocus();
			event.moneyTxt.selectAll();
		}
	}

	public void doAfterFail(PaymentMzkEvent mzkEvent)
	{
		mzkEvent.shell.close();
		mzkEvent.shell.dispose();
	}
}
