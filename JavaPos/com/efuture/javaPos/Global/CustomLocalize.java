package com.efuture.javaPos.Global;

import org.eclipse.swt.graphics.Color;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Logic.AliSaleTicketListBS;
import com.efuture.javaPos.Logic.ArkGroupSaleStatBS;
import com.efuture.javaPos.Logic.BankLogQueryBS;
import com.efuture.javaPos.Logic.BusinessPersonnelStatBS;
import com.efuture.javaPos.Logic.BuyInfoBS;
import com.efuture.javaPos.Logic.CleanUpLocalDataBaseBS;
import com.efuture.javaPos.Logic.ConnNetWorkBS;
import com.efuture.javaPos.Logic.CouponQueryInfoBS;
import com.efuture.javaPos.Logic.DeleteCzDataBS;
import com.efuture.javaPos.Logic.DisConnNetWorkBS;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.Logic.FjkInfoQueryBS;
import com.efuture.javaPos.Logic.GoodsInfoQueryBS;
import com.efuture.javaPos.Logic.GoodsStockQueryBS;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.JfQueryInfoBS;
import com.efuture.javaPos.Logic.LoginBS;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.MessageQueryBS;
import com.efuture.javaPos.Logic.MutiSelectBS;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Logic.MzkRechargeBS;
import com.efuture.javaPos.Logic.MzkSeqNoResetBS;
import com.efuture.javaPos.Logic.MzkStatisticsBS;
import com.efuture.javaPos.Logic.PassModifyBS;
import com.efuture.javaPos.Logic.PersonGrantBS;
import com.efuture.javaPos.Logic.PersonnelGoBS;
import com.efuture.javaPos.Logic.PreMoneyBS;
import com.efuture.javaPos.Logic.QueryWorkLogBS;
import com.efuture.javaPos.Logic.RemoveDayBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Logic.SaleTicketListBS;
import com.efuture.javaPos.Logic.SetSystemTimeBS;
import com.efuture.javaPos.Logic.ShortcutKeyBS;
import com.efuture.javaPos.Logic.SyySaleStatBS;
import com.efuture.javaPos.Logic.WithdrawBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.ArkGroupBillMode;
import com.efuture.javaPos.PrintTemplate.BusinessPerBillMode;
import com.efuture.javaPos.PrintTemplate.CardSaleBillMode;
import com.efuture.javaPos.PrintTemplate.CheckGoodsMode;
import com.efuture.javaPos.PrintTemplate.DisplayAdvertMode;
import com.efuture.javaPos.PrintTemplate.DisplayMode;
import com.efuture.javaPos.PrintTemplate.GiftBillMode;
import com.efuture.javaPos.PrintTemplate.HangBillMode;
import com.efuture.javaPos.PrintTemplate.InvoiceSummaryMode;
import com.efuture.javaPos.PrintTemplate.MzkRechargeBillMode;
import com.efuture.javaPos.PrintTemplate.PayinBillMode;
import com.efuture.javaPos.PrintTemplate.SaleAppendBillMode;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.PrintTemplate.StoredCardStatisticsMode;
import com.efuture.javaPos.PrintTemplate.SyySaleBillMode;
import com.efuture.javaPos.PrintTemplate.YyySaleBillMode;
import com.swtdesigner.SWTResourceManager;

import custom.localize.Jlsd.Jlsd_ShoppingBagBS;

public class CustomLocalize
{
	public static CustomLocalize currentCustomLocalize = null;

	public static CustomLocalize getDefault()
	{
		if (CustomLocalize.currentCustomLocalize == null)
		{
			// 无客户版本则创建标准版本
			if (!createCustomLocalize())
			{
				CustomLocalize.currentCustomLocalize = new CustomLocalize();
			}
		}

		return CustomLocalize.currentCustomLocalize;
	}

	public static boolean crmMode()
	{
		return false;
	}
	
	private static boolean createCustomLocalize()
	{
		// 生成客户模块实例
		try
		{
			// 标准版本
			if (GlobalInfo.ModuleType == null || GlobalInfo.ModuleType.trim().length() <= 0 || GlobalInfo.ModuleType.equals("0000"))
				return false;

			// 生成类名
			String module = Character.toUpperCase(GlobalInfo.ModuleType.charAt(0)) + GlobalInfo.ModuleType.substring(1).toLowerCase();
			StringBuffer className = new StringBuffer();
			className.append("custom.localize.");
			className.append(module);
			className.append(".");
			className.append(module);
			className.append("_CustomLocalize");

			// 生成实例
			Class cl = Class.forName(className.toString());
			CustomLocalize.currentCustomLocalize = (CustomLocalize) cl.newInstance();

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

//			new MessageBox("程序版本和当前客户模块[" + GlobalInfo.ModuleType + "]不匹配!");
			new MessageBox(Language.apply("程序版本和当前客户模块[{0}]不匹配!", new Object[]{GlobalInfo.ModuleType}));

			// 开启WINDOWS任务栏
			GlobalVar.EnableWindowsTrayWnd(true);

			// 退出系统
			SWTResourceManager.dispose();
			System.exit(0);
		}

		return false;
	}

	public String getAssemblyVersion()
	{
		return "0.0.0 bulid 0000.00.00";
	}

	public Color getStatusBarColor()
	{
		return null;
	}

	public boolean checkAllowUseMovie()
	{
		if (ManipulatePrecision.checkRegisterFunction(ConfigClass.CDKey, ManipulatePrecision.REGMOVIE))
		{
			return true;
		}
		else
		{
			String modelfunc = ManipulatePrecision.getRegisterFunctionModel();
			if (modelfunc != null && ManipulatePrecision.checkRegisterFunction(modelfunc, ManipulatePrecision.REGMOVIE))
			{
				return true;
			}
			else
			{
				new MessageBox(Language.apply("该收银机没有双屏广告模块的使用授权!"));
				return false;
			}
		}
	}

	public boolean checkAllowUseBank()
	{
		boolean ret = false;

		if (ManipulatePrecision.checkRegisterFunction(ConfigClass.CDKey, ManipulatePrecision.REGBANK))
		{
			String strseq = ManipulatePrecision.getRegisterCodeSeqno(ConfigClass.CDKey);
			if (checkAllowUseBank(Convert.toInt(strseq)))
			{
				ret = true;
			}
		}

		if (ret)
		{
			return true;
		}
		else
		{
			String modelfunc = ManipulatePrecision.getRegisterFunctionModel();
			if (modelfunc != null && ManipulatePrecision.checkRegisterFunction(modelfunc, ManipulatePrecision.REGBANK))
			{
				return true;
			}
			else
			{
				new MessageBox(Language.apply("该收银机没有银联金卡工程模块的使用授权!"));
				return false;
			}
		}
	}

	protected boolean checkAllowUseBank(int seqno)
	{
		if (seqno >= 1000)
			return true;
		else
			return false;
	}

	public SellType createSellType()
	{
		return new SellType();
	}

	public LoadSysInfo createLoadSysInfo()
	{
		return new LoadSysInfo();
	}

	public DataService createDataService()
	{
		return new DataService();
	}

	public NetService createNetService()
	{
		return new NetService();
	}

	public AccessLocalDB createAccessLocalDB()
	{
		return new AccessLocalDB();
	}

	public AccessBaseDB createAccessBaseDB()
	{
		return new AccessBaseDB();
	}

	public AccessDayDB createAccessDayDB()
	{
		return new AccessDayDB();
	}

	public AccessRemoteDB createRemoteDB()
	{
		return new AccessRemoteDB();
	}

	public TaskExecute createTaskExecute()
	{
		return new TaskExecute();
	}

	public CreatePayment createCreatePayment()
	{
		return new CreatePayment();
	}

	public ArkGroupSaleStatBS createArkGroupSaleStatBS()
	{
		return new ArkGroupSaleStatBS();
	}

	public BusinessPersonnelStatBS createBusinessPersonnelStatBS()
	{
		return new BusinessPersonnelStatBS();
	}

	public ConnNetWorkBS createConnNetWorkBS()
	{
		return new ConnNetWorkBS();
	}

	public DisConnNetWorkBS createDisConnNetWorkBS()
	{
		return new DisConnNetWorkBS();
	}

	public DisplaySaleTicketBS createDisplaySaleTicketBS()
	{
		return new DisplaySaleTicketBS();
	}

	public GoodsInfoQueryBS createGoodsInfoQueryBS()
	{
		return new GoodsInfoQueryBS();
	}

	public LoginBS createLoginBS()
	{
		return new LoginBS();
	}

	public MenuFuncBS createMenuFuncBS()
	{
		return new MenuFuncBS();
	}

	public MessageQueryBS createMessageQueryBS()
	{
		return new MessageQueryBS();
	}

	public MzkInfoQueryBS createMzkInfoQueryBS()
	{
		return new MzkInfoQueryBS();
	}

	public FjkInfoQueryBS createFjkInfoQueryBS()
	{
		return new FjkInfoQueryBS();
	}

	public HykInfoQueryBS createHykInfoQueryBS()
	{
		return new HykInfoQueryBS();
	}

	public PassModifyBS createPassModifyBS()
	{
		return new PassModifyBS();
	}

	public PersonGrantBS createPersonGrantBS()
	{
		return new PersonGrantBS();
	}

	public PersonnelGoBS createPersonnelGoBS()
	{
		return new PersonnelGoBS();
	}

	public PreMoneyBS createPreMoneyBS()
	{
		return new PreMoneyBS();
	}

	public Jlsd_ShoppingBagBS createJlsd_ShoppingBagBS()
	{
		return new Jlsd_ShoppingBagBS();
	}

	public QueryWorkLogBS createQueryWorkLogBS()
	{
		return new QueryWorkLogBS();
	}

	public SaleBS createSaleBS()
	{
		return new SaleBS();
	}

	public SaleTicketListBS createSaleTicketListBS()
	{
		return new SaleTicketListBS();
	}

	public SetSystemTimeBS createSetSystemTimeBS()
	{
		return new SetSystemTimeBS();
	}

	public ShortcutKeyBS createShortcutKeyBS()
	{
		return new ShortcutKeyBS();
	}

	public SyySaleStatBS createSyySaleStatBS()
	{
		return new SyySaleStatBS();
	}

	public WithdrawBS createWithdrawBS()
	{
		return new WithdrawBS();
	}

	public CleanUpLocalDataBaseBS createCleanUpLocalDataBaseBS()
	{
		return new CleanUpLocalDataBaseBS();
	}

	public RemoveDayBS createRemoveDayBS()
	{
		return new RemoveDayBS();
	}

	public BankLogQueryBS createBankCardQueryBS()
	{
		return new BankLogQueryBS();
	}

	public SaleBillMode createSaleBillMode()
	{
		return new SaleBillMode();
	}

	public InvoiceSummaryMode createInvoiceSummaryMode()
	{
		return new InvoiceSummaryMode();
	}

	public StoredCardStatisticsMode createStoredCardStatisticsMode()
	{
		return new StoredCardStatisticsMode();
	}

	public MzkStatisticsBS createMzkStatisticsBS()
	{
		return new MzkStatisticsBS();
	}

	public YyySaleBillMode createYyySaleBillMode()
	{
		return new YyySaleBillMode();
	}

	public SaleAppendBillMode createSaleAppendBillMode()
	{
		return new SaleAppendBillMode();
	}

	public DisplayMode createDisplayMode()
	{
		return new DisplayMode();
	}

	public DisplayAdvertMode createDisplayAdvertMode()
	{
		return new DisplayAdvertMode();
	}

	public GiftBillMode createGiftMode()
	{
		return new GiftBillMode();
	}

	public PayinBillMode createPayinBillMode()
	{
		return new PayinBillMode();
	}

	public SyySaleBillMode createSyySaleBillMode()
	{
		return new SyySaleBillMode();
	}

	public ArkGroupBillMode createArkGroupBillMode()
	{
		return new ArkGroupBillMode();
	}

	public BusinessPerBillMode createBusinessPerBillMode()
	{
		return new BusinessPerBillMode();
	}

	public HangBillMode createHangBillMode()
	{
		return new HangBillMode();
	}

	public CardSaleBillMode createCardSaleBillMode()
	{
		return new CardSaleBillMode();
	}

	public MzkRechargeBillMode createMzkRechargeBillMode()
	{
		return new MzkRechargeBillMode();
	}

	public CheckGoodsMode createCheckGoodsMode()
	{
		return new CheckGoodsMode();
	}

	public Printer createPrinter(String name)
	{
		return new Printer(name);
	}

	public MzkSeqNoResetBS createMzkSeqNoResetBS()
	{
		return new MzkSeqNoResetBS();
	}

	public DeleteCzDataBS createDeleteCzDataBS()
	{
		return new DeleteCzDataBS();
	}

	public CouponQueryInfoBS createCouponQueryInfoBS()
	{
		return new CouponQueryInfoBS();
	}

	public BuyInfoBS createBuyInfoBS()
	{
		return new BuyInfoBS();
	}

	public MutiSelectBS createMutiSelectBS()
	{
		return new MutiSelectBS();
	}

	public GoodsStockQueryBS createGoodsStockQueryBS()
	{
		return new GoodsStockQueryBS();
	}

	public MzkRechargeBS createMzkRechargeBS()
	{
		return new MzkRechargeBS();
	}
	
	public JfQueryInfoBS createJfQueryInfoBS()
	{
		return new JfQueryInfoBS();
	}
	
	//阿里小票查询
	public AliSaleTicketListBS createAliSaleTicketListBS()
	{
		return new AliSaleTicketListBS();
	}

}
