package com.efuture.javaPos.Payment;

import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Bank.Interface_PaymentBankForm;
import com.efuture.javaPos.Payment.Bank.PaymentBankForm;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.MemoInfoDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class CreatePayment
{
	public static CreatePayment currentCreatePayment = null;

	public static CreatePayment getDefault()
	{
		if (CreatePayment.currentCreatePayment == null)
		{
			CreatePayment.currentCreatePayment = CustomLocalize.getDefault().createCreatePayment();
		}

		return CreatePayment.currentCreatePayment;
	}

	public PaymentChange getPaymentChange(SaleBS sale)
	{
		return new PaymentChange(sale);
	}

	// 储值卡付款对象相关
	public PaymentMzk getPaymentMzk()
	{
		// 检索自定义付款对象列表中是否有面值卡付款对象,如果有则以该付款对象为面值卡查询的对象
		String[] pay = getCustomPaymentDefine("PaymentMzk");
		if (pay != null)
		{
			try
			{
				Class cl = payClassName(pay[0]);
				if (cl != null)
					return (PaymentMzk) cl.newInstance();
				else
//					new MessageBox("付款对象 " + pay[0] + " 不存在\n\n" + "将生成默认付款对象进行交易处理");
					new MessageBox(Language.apply("付款对象 {0} 不存在\n\n将生成默认付款对象进行交易处理" ,new Object[]{pay[0]}));
			}
			catch (Exception e)
			{
				e.printStackTrace();
//				new MessageBox("付款对象 " + pay[0] + " 创建失败\n\n" + e.getMessage() + "\n\n" + "将生成默认付款对象进行交易处理");
				new MessageBox(Language.apply("付款对象 {0} 创建失败\n\n{1}\n\n将生成默认付款对象进行交易处理" ,new Object[]{pay[0] ,e.getMessage()}));
			}
		}

		// 生成默认对象
		return new PaymentMzk();
	}

	public boolean isPaymentMzk(String paycode)
	{
		PayModeDef pmd = DataService.getDefault().searchPayMode(paycode);
		if (pmd.type == '4')
			return true;
		else
			return false;
	}

	// 返券卡付款对象相关
	public PaymentFjk getPaymentFjk()
	{
		// 检索自定义付款对象列表中是否有返券卡付款对象,如果有则以该付款对象为返券卡查询的对象
		String[] pay = getCustomPaymentDefine("PaymentFjk");
		if (pay != null)
		{
			try
			{
				Class cl = payClassName(pay[0]);
				if (cl != null)
					return (PaymentFjk) cl.newInstance();
				else
//				new MessageBox("付款对象 " + pay[0] + " 不存在\n\n" + "将生成默认付款对象进行交易处理");
				new MessageBox(Language.apply("付款对象 {0} 不存在\n\n将生成默认付款对象进行交易处理" ,new Object[]{pay[0]}));
			}
			catch (Exception e)
			{
				e.printStackTrace();
//				new MessageBox("付款对象 " + pay[0] + " 创建失败\n\n" + e.getMessage() + "\n\n" + "将生成默认付款对象进行交易处理");
				new MessageBox(Language.apply("付款对象 {0} 创建失败\n\n{1}\n\n将生成默认付款对象进行交易处理" ,new Object[]{pay[0] ,e.getMessage()}));
			}
		}

		// 生成默认对象
		return new PaymentFjk();
	}

	public PaymentCoupon getPaymentCoupon()
	{
		// 检索自定义付款对象列表中是否有返券卡付款对象,如果有则以该付款对象为返券卡查询的对象
		String[] pay = getCustomPaymentDefine("PaymentCoupon");
		if (pay != null)
		{
			try
			{
				Class cl = payClassName(pay[0]);
				if (cl != null)
					return (PaymentCoupon) cl.newInstance();
				else
//					new MessageBox("付款对象 " + pay[0] + " 不存在\n\n" + "将生成默认付款对象进行交易处理");
					new MessageBox(Language.apply("付款对象 {0} 不存在\n\n将生成默认付款对象进行交易处理" ,new Object[]{pay[0]}));
			}
			catch (Exception e)
			{
				e.printStackTrace();
//				new MessageBox("付款对象 " + pay[0] + " 创建失败\n\n" + e.getMessage() + "\n\n" + "将生成默认付款对象进行交易处理");
				new MessageBox(Language.apply("付款对象 {0} 创建失败\n\n{1}\n\n将生成默认付款对象进行交易处理" ,new Object[]{pay[0] ,e.getMessage()}));
			}
		}

		// 生成默认对象
		return new PaymentCoupon();
	}

	public boolean isPaymentFjk(String code)
	{
		try
		{
			String[] pay = getCustomPaymentDefine("PaymentCoupon");
			if (pay != null)
			{
				for (int i = 0; i < pay.length; i++)
				{
					if (code.trim().equals(pay[i].trim())) { return true; }
				}
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}

		if (GlobalInfo.sysPara.fjkyetype == null || GlobalInfo.sysPara.fjkyetype.equals(""))
		{
			if (code.trim().equals("0500")) { return true; }
		}
		else
		{
			String s[] = GlobalInfo.sysPara.fjkyetype.split(";");
			for (int i = 0; i < s.length; i++)
			{
				// T为退打印券的付款方式，不生成用返券付款对象
				// 后台根据这个付款方式，生成退券
				String p[] = s[i].split("=");
				if (code.trim().equals(p[0].trim()) && p.length >= 2 && p[1].trim().charAt(0) != 'T') { return true; }
			}
		}

		return false;
	}

	public PaymentMyStore getPaymentMyStore()
	{
		// 检索自定义付款对象列表中是否有会员卡付款对象,如果有则以该付款对象为会员卡查询的对象
		String[] pay = getCustomPaymentDefine("PaymentMyStore");
		if (pay != null)
		{
			try
			{
				Class cl = payClassName(pay[0]);
				if (cl != null)
					return (PaymentMyStore) cl.newInstance();
				else
//					new MessageBox("付款对象 " + pay[0] + " 不存在\n\n" + "将生成默认付款对象进行交易处理");
					new MessageBox(Language.apply("付款对象 {0} 不存在\n\n将生成默认付款对象进行交易处理" ,new Object[]{pay[0]}));
			}
			catch (Exception e)
			{
				e.printStackTrace();
//				new MessageBox("付款对象 " + pay[0] + " 创建失败\n\n" + e.getMessage() + "\n\n" + "将生成默认付款对象进行交易处理");
				new MessageBox(Language.apply("付款对象 {0} 创建失败\n\n{1}\n\n将生成默认付款对象进行交易处理" ,new Object[]{pay[0] ,e.getMessage()}));
			}
		}

		// 生成默认对象
		return new PaymentMyStore();

	}

	public PaymentCust getPaymentCust()
	{
		// 检索自定义付款对象列表中是否有会员卡付款对象,如果有则以该付款对象为会员卡查询的对象
		String[] pay = getCustomPaymentDefine("PaymentCust");
		if (pay != null)
		{
			try
			{
				Class cl = payClassName(pay[0]);
				if (cl != null)
					return (PaymentCust) cl.newInstance();
				else
//					new MessageBox("付款对象 " + pay[0] + " 不存在\n\n" + "将生成默认付款对象进行交易处理");
					new MessageBox(Language.apply("付款对象 {0} 不存在\n\n将生成默认付款对象进行交易处理" ,new Object[]{pay[0]}));
			}
			catch (Exception e)
			{
				e.printStackTrace();
//				new MessageBox("付款对象 " + pay[0] + " 创建失败\n\n" + e.getMessage() + "\n\n" + "将生成默认付款对象进行交易处理");
				new MessageBox(Language.apply("付款对象 {0} 创建失败\n\n{1}\n\n将生成默认付款对象进行交易处理" ,new Object[]{pay[0] ,e.getMessage()}));
			}
		}

		// 生成默认对象
		return new PaymentCust();
	}

	// 赊账和订金付款对象相关
	public boolean isPaymentPreDebt(String code)
	{
		if (code.trim().equals("0601")) { return true; }

		return false;
	}

	public boolean isPaymentPreDeposit(String code)
	{
		if (code.trim().equals("0602")) { return true; }

		return false;
	}

	public PayModeDef getPaymentPreDebtMode()
	{
		return DataService.getDefault().searchPayMode("0601");
	}

	public PayModeDef getPaymentPreDepositMode()
	{
		return DataService.getDefault().searchPayMode("0602");
	}

	// 零钞转存付款对象相关
	public PaymentCustLczc getPaymentLczc(SaleBS sbs)
	{
		String[] pay = getCustomPaymentDefine("PaymentCustLczc");
		if (pay != null)
		{
			try
			{
				Class cl = payClassName(pay[0]);
				if (cl != null)
				{
					PayModeDef paymode = DataService.getDefault().searchPayMode(pay[1]);
					if (paymode == null)
						return null;
					PaymentCustLczc p = (PaymentCustLczc) cl.newInstance();
					p.initPayment(paymode, sbs);
					return p;
				}
				else
//					new MessageBox("付款对象 " + pay[0] + " 不存在\n\n" + "将生成默认付款对象进行交易处理");
					new MessageBox(Language.apply("付款对象 {0} 不存在\n\n将生成默认付款对象进行交易处理" ,new Object[]{pay[0]}));
			}
			catch (Exception e)
			{
				e.printStackTrace();
//				new MessageBox("付款对象 " + pay[0] + " 创建失败\n\n" + e.getMessage() + "\n\n" + "将生成默认付款对象进行交易处理");
				new MessageBox(Language.apply("付款对象 {0} 创建失败\n\n{1}\n\n将生成默认付款对象进行交易处理" ,new Object[]{pay[0] ,e.getMessage()}));
			}
		}

		// 生成默认对象
		PayModeDef paymode = DataService.getDefault().searchPayMode("0111");
		if (paymode == null)
			return null;
		else
			return new PaymentCustLczc(paymode, sbs);
	}

	// 零钞转存付款对象相关
	public PaymentCustJfSale getPaymentJfChange(PayModeDef mode, SaleBS sale)
	{
		return new PaymentCustJfSale(mode, sale);
	}

	public boolean isPaymentLczc(SalePayDef sp)
	{
		String[] pay = getCustomPaymentDefine("PaymentCustLczc");
		if (pay != null)
		{
			for (int i = 1; i < pay.length; i++)
			{
				if (sp.paycode.equals(pay[i]) && sp.memo.trim().equals("3"))
					return true;
			}
		}

		if (sp.paycode.equals("0111") && sp.memo.trim().equals("3"))
			return true;
		else
			return false;
	}

	public boolean isPaymentMystore(SalePayDef sp)
	{
		String[] pay = getCustomPaymentDefine("PaymentMyStore");
		if (pay != null)
		{
			for (int i = 1; i < pay.length; i++)
			{
				if (sp.paycode.equals(pay[i]))
					return true;
			}
		}

		return false;
	}

	public boolean isPaymentJfxf(SalePayDef sp)
	{
		String[] pay = getCustomPaymentDefine("PaymentCustJfSale");
		if (pay != null)
		{
			for (int i = 1; i < pay.length; i++)
			{
				if (sp.paycode.equals(pay[i]) && sp.memo.trim().equals("1"))
					return true;
			}
		}

		return false;
	}

	// 移动找零充值付款对象相关
	public PaymentBankCMCC getPaymentMobileCharge(SaleBS sbs)
	{
		// 先找到找零充值付款方式
		String paycode = "0722";
		MemoInfoDef info = AccessLocalDB.getDefault().checkMobileCharge(null);
		if (info != null)
			paycode = info.text;

		// 生成默认对象
		PayModeDef paymode = DataService.getDefault().searchPayMode(paycode);
		if (paymode == null)
			return null;
		else
			return new PaymentBankCMCC(paymode, sbs);
	}

	public boolean isPaymentMobileCharge(SalePayDef sp)
	{
		// 先找到找零充值付款方式
		String paycode = "0722";
		MemoInfoDef info = AccessLocalDB.getDefault().checkMobileCharge(null);
		if (info != null)
			paycode = info.text;

		if (sp.paycode.equals(paycode) && sp.memo.trim().equals("3"))
			return true;
		else
			return false;
	}

	// 移动找零充值付款对象相关
	public PaymentWuHanTong getPaymentWhtCharge(SaleBS sbs)
	{
		// 先找到找零充值付款方式
		String paycode = "0723";;

		// 生成默认对象
		PayModeDef paymode = DataService.getDefault().searchPayMode(paycode);
		if (paymode == null)
			return null;
		else
			return new PaymentWuHanTong(paymode, sbs);
	}

	public boolean isPaymentWhtCharge(SalePayDef sp)
	{
		// 先找到找零充值付款方式
		String paycode = "0723";

		if (sp.paycode.equals(paycode) && sp.memo.trim().equals("3"))
			return true;
		else
			return false;
	}

	public Class bankClassName(String name)
	{
		Class cl = null;
		int n = 0;
		for (n = 0; n < 4; n++)
		{
			try
			{
				if (n == 0)
					cl = Class.forName(name);
				else if (n == 1)
					cl = Class.forName("com.efuture.javaPos.Payment.Bank." + name);
				else if (n == 2)
				{
					int p = name.indexOf('_');
					if (p >= 0)
						cl = Class.forName("custom.localize." + name.substring(0, p) + "." + name);
					else
						cl = Class.forName("custom.localize." + name);
				}
				else
					cl = Class.forName("bankpay.Bank." + name);
				if (cl != null)
					break;
			}
			catch (Exception e)
			{
				continue;
			}
		}
		if (n >= 4)
			return null;
		else
			return cl;
	}

	public PaymentBankFunc getConfigBankFunc(String paycode)
	{
		if (ConfigClass.Bankfunc != null && ConfigClass.Bankfunc.length() > 0)
		{
			try
			{
				String conf[] = ConfigClass.Bankfunc.split("\\|");
				if (conf.length <= 1)
				{
					Class cl = bankClassName(ConfigClass.Bankfunc);
					if (cl != null)
					{
						PaymentBankFunc bank = (PaymentBankFunc) cl.newInstance();
						bank.paycode = paycode; // 当多种付款方式调用一种接口是，需要通过paycode来确认路径
						bank.readBankClassConfig(null);
						return bank;
					}
					else
					{
						if (PathFile.fileExist(GlobalVar.ConfigPath + "\\" + ConfigClass.Bankfunc + ".ini"))
						{
							PaymentBankFunc bank = new PaymentBankFunc();
							bank.paycode = paycode;
							bank.readBankClassConfig(ConfigClass.Bankfunc);
							return bank;
						}
						else
//							new MessageBox("支付对象 " + ConfigClass.Bankfunc + " 不存在\n\n" + "将生成默认付款对象进行交易处理");
							new MessageBox(Language.apply("支付对象 {0} 不存在\n\n将生成默认付款对象进行交易处理" ,new Object[]{ConfigClass.Bankfunc}));
					}
				}
				else
				{
					// 未指定付款代码则进行选择
					if (paycode == null || "".equals(paycode) || (ConfigClass.Bankfunc).indexOf("," + paycode) < 0)
					{
						Vector v = new Vector();
						for (int i = 0; i < conf.length; i++)
						{
							String[] s = conf[i].split(",");
							for (int j = 1; j < s.length; j++)
							{
								PayModeDef pm = DataService.getDefault().searchPayMode(s[j]);
								if (pm != null)
									v.add(new String[] { pm.code, pm.name });
								else
									v.add(new String[] { s[j], s[0] });
							}
						}
						if (v.size() > 1)
						{
							String[] title = { Language.apply("付款代码"), Language.apply("付款名称") };
							int[] width = { 100, 400 };
							int choice = -1;
							do
							{
								choice = new MutiSelectForm().open(Language.apply("请选择第三方支付接口"), title, width, v);
							} while (choice < 0);
							paycode = ((String[]) v.elementAt(choice))[0];
						}
					}

					// 根据付款代码确定银联对象
					String bankclass = null;
					for (int i = 0; paycode != null && i < conf.length; i++)
					{
						String bankinfo = conf[i] + ",";
						if ((bankinfo.indexOf("," + paycode + ",")) >= 0)
						{
							int j = bankinfo.indexOf(",");
							bankclass = bankinfo.substring(0, j);
							break;
						}
					}

					// 没有找到对应代码定义的银联对象则总是用第一个
					if (bankclass == null)
					{
						String bankinfo = conf[0] + ",";
						int j = bankinfo.indexOf(",");
						bankclass = bankinfo.substring(0, j);
					}

					// 创建银联对象
					Class cl = bankClassName(bankclass);
					if (cl != null)
					{
						PaymentBankFunc bank = (PaymentBankFunc) cl.newInstance();
						bank.paycode = paycode;
						bank.readBankClassConfig(null);
						return bank;
					}
					else
					{
						if (PathFile.fileExist(GlobalVar.ConfigPath + "\\" + bankclass + ".ini"))
						{
							PaymentBankFunc bank = new PaymentBankFunc();
							bank.paycode = paycode;
							bank.readBankClassConfig(bankclass);
							return bank;
						}
						else
//							new MessageBox("支付对象 " + bankclass + " 不存在\n\n" + "将生成默认付款对象进行交易处理");
							new MessageBox(Language.apply("支付对象 {0} 不存在\n\n将生成默认付款对象进行交易处理" ,new Object[]{bankclass}));
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();

				return null;
			}
		}

		return null;
	}

	public PaymentBankFunc getPaymentBankFunc(String paycode)
	{
		PaymentBankFunc bank = getConfigBankFunc(paycode);
		if (bank == null)
		{
			bank = new PaymentBankFunc();
			bank.readBankClassConfig(null);
		}

		// 如果参数允许单独进行银联消费则进行设置,无论是Y还是A都允许单独消费
		if (GlobalInfo.sysPara.allowbankselfsale == 'Y' || GlobalInfo.sysPara.allowbankselfsale == 'A')
		{
			bank.allowSaleBySelf(true);
		}

		// 即扫即打模式签购单放在最后打印,设置为非即时打印签购单模式
		if (GlobalInfo.syjDef.printfs == '1')
		{
			bank.setOnceXYKPrintDoc(false);
		}

		return bank;
	}

	public PaymentBankFunc getPaymentBankFuncByMenu(String paycode)
	{
		PaymentBankFunc bank = getPaymentBankFunc(paycode);

		// 通过菜单调用总是即时打印签购单模式
		bank.setOnceXYKPrintDoc(true);

		return bank;
	}

	// 保持原有重载的兼容性
	public PaymentBankFunc getConfigBankFunc()
	{
		return getConfigBankFunc(null);
	}

	// 保持原有重载的兼容性
	public PaymentBankFunc getPaymentBankFunc()
	{
		return getPaymentBankFunc(null);
	}

	// 保持原有重载的兼容性
	public PaymentBankFunc getPaymentBankFuncByMenu()
	{
		return getPaymentBankFuncByMenu(null);
	}

	public Interface_PaymentBankForm getPaymentBankForm()
	{
		return (Interface_PaymentBankForm) new PaymentBankForm();
	}

	// 是否允许直接在金额款输入付款金额
	public boolean allowQuickInputMoney(PayModeDef pay)
	{
		// 不是末级付款，要显示下级付款，不能直接输入
		if (pay.ismj != 'Y')
			return false;

		// isbank = 'Z',允许直接输入付款金额
		if (pay.isbank == 'Z')
			return true;

		// 是2级付款且不是直接输入付款金额模式，则不能直接输入付款金额
		if (pay.level > 1 && pay.isbank != 'N')
			return false;

		// 储值卡类不能直接输入付款金额
		if (pay.type == '4')
			return false;

		// isbank = 'M',储值付款方式，不能直接输入付款金额
		if (pay.isbank == 'M')
			return false;

		// isbank = 'Y',金卡工程付款，不能直接输入付款金额
		if (pay.isbank == 'Y')
			return false;

		// isbank = 'A',明细付款模式，不能直接输入付款金额
		if (pay.isbank == 'A')
			return false;

		// 其他允许直接付款
		return true;
	}

	public String[] getCustomPaymentDefine(String namelike)
	{
		if (ConfigClass.CustomPayment != null && ConfigClass.CustomPayment.size() > 0)
		{
			for (int i = 0; i < ConfigClass.CustomPayment.size(); i++)
			{
				String s = (String) ConfigClass.CustomPayment.elementAt(i);
				String[] sp = s.split(",");
				if (sp.length >= 2 && sp[0].endsWith(namelike)) { return sp; }
			}
		}

		return null;
	}

	public Class payClassName(String name)
	{
		Class cl = null;
		int n = 0;
		for (n = 0; n < 4; n++)
		{
			try
			{
				if (n == 0)
				{
					cl = Class.forName(name);
				}
				else if (n == 1)
				{
					cl = Class.forName("com.efuture.javaPos.Payment." + name);
				}
				else if (n == 2)
				{
					int p = name.indexOf('_');

					if (p >= 0)
					{
						cl = Class.forName("custom.localize." + name.substring(0, p) + "." + name);
					}
					else
					{
						cl = Class.forName("custom.localize." + name);
					}
				}
				else
				{
					cl = Class.forName("bankpay.Payment." + name);
				}

				if (cl != null)
					break;
			}
			catch (Exception e)
			{
				continue;
			}
		}
		if (n >= 4)
			return null;
		else
			return cl;
	}

	public Payment createCustomPayment(boolean flag, PayModeDef mode, SaleBS sale, SalePayDef pay, SaleHeadDef head)
	{
		if (ConfigClass.CustomPayment != null && ConfigClass.CustomPayment.size() > 0)
		{
			for (int i = 0; i < ConfigClass.CustomPayment.size(); i++)
			{
				String s = (String) ConfigClass.CustomPayment.elementAt(i);
				String[] sp = s.split(",");
				if (sp.length <= 0)
					continue;
				int j;
				for (j = 1; j < sp.length; j++)
				{
					if (mode.code.equalsIgnoreCase(sp[j].trim()))
						break;
				}
				if (j >= sp.length)
					continue;

				// 创建付款对象
				try
				{
					Class cl = payClassName(sp[0]);
					if (cl == null)
					{
//						new MessageBox("付款对象 " + sp[0] + " 不存在\n\n" + "将生成默认付款对象进行交易处理");
						new MessageBox(Language.apply("付款对象 {0} 不存在\n\n将生成默认付款对象进行交易处理" ,new Object[]{sp[0]}));
						return null;
					}
					else
					{
						Payment p = (Payment) cl.newInstance();
						if (flag)
							p.initPayment(mode, sale);
						else
							p.initPayment(pay, head);
						return p;
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
//					new MessageBox("付款对象 " + sp[0] + " 创建失败\n\n" + e.getMessage() + "\n\n" + "将生成默认付款对象进行交易处理");
					new MessageBox(Language.apply("付款对象 {0} 创建失败\n\n{1}\n\n将生成默认付款对象进行交易处理" ,new Object[]{sp[0] ,e.getMessage()}));
					return null;
				}
			}
		}

		return null;
	}

	public Payment createLocalizePayment(boolean flag, PayModeDef mode, SaleBS sale, SalePayDef pay, SaleHeadDef head)
	{
		return createCustomPayment(flag, mode, sale, pay, head);
	}

	// flag:true代表以销售窗口创建对象;false红冲
	public Payment createPaymentAll(boolean flag, PayModeDef mode, SaleBS sale, SalePayDef pay, SaleHeadDef head)
	{
		// 客户化付款对象
		Payment p = createLocalizePayment(flag, mode, sale, pay, head);
		if (p != null)
			return p;

		// 允许直接输入付款金额
		if (allowQuickInputMoney(mode))
		{
			if (flag)
			{
				return new Payment(mode, sale);
			}
			else
			{
				return new Payment(pay, head);
			}
		}
		else
		{
			// 金卡工程,生成相应的金卡工程付款对象
			if (mode.isbank == 'Y')
			{
				if (flag)
				{
					return new PaymentBank(mode, sale);
				}
				else
				{
					return new PaymentBank(pay, head);
				}
			}
			else if (mode.isbank == 'M')
			{
				if (flag)
				{
					return new PaymentMzk(mode, sale);
				}
				else
				{
					return new PaymentMzk(pay, head);
				}
			}
			else
			{
				// 根据不同的付款方式,创建相应的付款对象
				switch (mode.type)
				{
					case '4': // 面值卡付款
					{
						if (flag)
						{
							return new PaymentMzk(mode, sale);
						}
						else
						{
							return new PaymentMzk(pay, head);
						}
					}

					default: // 其他付款方式
					{
						if (flag)
						{
							return new PaymentDetail(mode, sale);
						}
						else
						{
							return new PaymentDetail(pay, head);
						}
					}
				}
			}
		}
	}

	public Payment createPaymentBySalePay(SalePayDef pay, SaleHeadDef head)
	{
		PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);

		if (mode == null) { return null; }

		return createPaymentAll(false, mode, null, pay, head);
	}

	public Payment createPaymentByPayMode(PayModeDef mode, SaleBS sale)
	{
		try
		{
			return createPaymentAll(true, mode, sale, null, null);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return null;
		}
	}

	public boolean sendAllPaymentCz()
	{
		// 按付款方式发送各自的冲正数据
		boolean ok = true;
		for (int i = 0; GlobalInfo.payMode != null && i < GlobalInfo.payMode.size(); i++)
		{
			PayModeDef pmd = (PayModeDef) GlobalInfo.payMode.elementAt(i);
			ok = createPaymentAll(true, pmd, null, null, null).sendAccountCz();
		}
		return ok;
		/*
		 * // 发送面值卡冲正 if
		 * (!CreatePayment.getDefault().getPaymentMzk().sendAccountCz()) {
		 * return false; }
		 * 
		 * // 发送返券卡冲正 if
		 * (!CreatePayment.getDefault().getPaymentFjk().sendAccountCz()) {
		 * return false; }
		 * 
		 * // 发送会员卡记账冲正 if
		 * (!CreatePayment.getDefault().getPaymentCust().sendAccountCz()) {
		 * return false; }
		 */
	}
}
