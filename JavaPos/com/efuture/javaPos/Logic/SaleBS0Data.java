package com.efuture.javaPos.Logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentChange;
import com.efuture.javaPos.PrintTemplate.DisplayAdvertMode;
import com.efuture.javaPos.PrintTemplate.DisplayMode;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsJFRule;
import com.efuture.javaPos.Struct.OperRoleDef;
import com.efuture.javaPos.Struct.SaleCustDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.ShopPreSaleDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.Struct.SuperMarketPopRuleDef;
import com.efuture.javaPos.UI.SaleEvent;
import com.efuture.javaPos.UI.SalePayEvent;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

// 交易数据
public class SaleBS0Data
{
	public final static int clearPermission = 1;

	public volatile SaleHeadDef saleHead = null;
	public Vector saleGoods = null;
	public Vector salePayment = null;
	public Vector payAssistant = null;
	public Vector goodsAssistant = null;
	public Vector goodsSpare = null;
	public Vector brokenAssistant = null;
	public Vector memoPayment = null; // 备用付款方式 在预售提货中用到
	public Vector backPayment = null;
	public SaleCustDef saleCust = null; // 小票顾客信息

	public Vector refundPayment = null;

	public volatile int salePayUnique = 0; // 付款唯一序号,每增加一个付款顺加,删除付款行也不减少
	public volatile double saleyfje = 0; // 交易应付金额
	public volatile double salezlexception = 0; // 找零除外金额
	public volatile String saletype; // 当前小票类型
	public volatile boolean saleFinish = false; // 当前是否销售完成
	public SaleEvent saleEvent = null;
	public SalePayEvent salePayEvent = null;
	public volatile boolean quickpaystart = false;
	public volatile int quickpaykey = 0;
	protected volatile double dzcmjgzk = 0;

	public OperRoleDef curGrant = new OperRoleDef();
	public volatile CustomerDef curCustomer = null;

	public volatile String curyyygz; // 当前营业员所属柜组
	public volatile String curyyyfph; // 当前发票号
	public volatile String cursqkh; // 当前授权卡号
	public volatile char cursqktype; // 当前授权卡类别,'1'-员工卡，'2'-顾客卡
	public volatile double cursqkzkfd; // 当前授权卡折扣分担

	public volatile String curBatch = null; // 当前批次号
	public volatile double curPfzkfd = 0;

	private volatile boolean clearBroken = (checkBrokenData() != null) ? false : true;

	public SaleGoodsDef thSaleGoods = null; // 原小票信息
	public String thSyjh = null; // 原收银机号
	public long thFphm = 0; // 原小票号

	public Vector lastGoodsDetail = null; // 上次显示的商品列表明细
	public volatile SaleHeadDef lastsaleHead = null; // 上笔成功交易小票的小票头

	public volatile StringBuffer realTimePrintFlag = null; // 即扫即打方式对应的打印标记
	public volatile boolean haveRealTimePrint = false; // 标记是否已有即扫即打内容
	public volatile boolean stopRealTimePrint = false; // 暂停即扫即打方式

	public volatile boolean waitlab = false; // 在找零界面上控制按键，防止并发
	public volatile boolean isbackticket = false; // 是否已经查询过退货小票
	public volatile char hhflag = 'N'; // 换货标记

	public Vector checkgoods = null; // 盘点单商品
	public String checkdjbh = null; // 盘点单号
	public String checkgz = ""; // 盘点柜组
	public String checkrq = ""; // 盘点日期
	public String checkcw = ""; // 盘点仓位
	public String checkeditflag = ""; // 原盘点单是否可编辑
	public int totalcheckrow = -1; // 原盘点单的总行数
	public int curcheckrow = -1; // 当前盘点单的总行数

	public volatile boolean isonlinegdjging = false; // 正在联网挂单解挂状态

	public String jdfhddname = ""; // 家电发货地点code
	public String jdfhddcode = ""; // 家电发货地点name

	public boolean isVIPZK = true;

	public Vector stampList = null; // 印花券列表
	public SuperMarketPopRuleDef stampRule = null; // 印花促销规则
	public ShopPreSaleDef preSale = null; // 现场宝

	public SaleBS0Data()
	{
	}

	public int getMaxSaleGoodsCount()
	{
		return GlobalInfo.sysPara.maxSaleGoodsCount;
	}

	public int getMaxSalePayCount()
	{
		return GlobalInfo.sysPara.maxSalePayCount;
	}

	public double getMaxSaleGoodsQuantity()
	{
		return GlobalInfo.sysPara.maxSaleGoodsQuantity;
	}

	public double getMaxSaleGoodsMoney()
	{
		return GlobalInfo.sysPara.maxSaleGoodsMoney;
	}

	public double getMaxSaleMoney()
	{
		return GlobalInfo.sysPara.maxSaleMoney;
	}

	public void setSaleEvent(SaleEvent e)
	{
		saleEvent = e;
	}

	public void setSalePayEvent(SalePayEvent e)
	{
		salePayEvent = e;
	}

	public Vector getFjkPopVector()
	{
		return null;
	}

	public boolean operPermission(int type, OperRoleDef oper)
	{
		switch (type)
		{
		case clearPermission: // 清空权限
			if ((curGrant.privqx != 'Y'))
				return true;
			break;
		}
		return false;
	}

	public void showBackPayment()
	{
		if (SellType.ISBACK(saletype) && (backPayment != null && backPayment.size() > 0))
		{
			Vector v = new Vector();
			for (int k = 0; k < backPayment.size(); k++)
			{
				SalePayDef mdf = (SalePayDef) backPayment.elementAt(k);

				v.add(new String[] { mdf.paycode, mdf.payname, mdf.payno });

			}

			String[] title = { Language.apply("付款代码"), Language.apply("付款名称"), Language.apply("付款账号") };
			int[] width = { 100, 150, 250 };
			new MutiSelectForm().open(Language.apply("原小票中的付款方式列表"), title, width, v);

		}
	}

	public String getFuncMenuByPaying()
	{
		// 付款窗口打开功能菜单只允许使用以下功能
		// 0806 信用卡余额查询
		// 0807 信用卡签购单重打
		// 0808 信用卡交易查询
		return "0008,0806,0807,0808";
	}

	public void djlbSaleToBack()
	{
		saletype = SellType.getDjlbSaleToBack(saletype);
	}

	public void djlbBackToSale()
	{
		saletype = SellType.getDjlbBackToSale(saletype);
	}

	public boolean isSpecifyBack()
	{
		return isSpecifyBack(null);
	}

	public boolean isSpecifyBack(SaleGoodsDef g)
	{
		if (g == null)
		{
			if (SellType.ISBACK(saletype) && (thSyjh != null) && GlobalInfo.sysPara.inputydoc != 'D')
				return true;
			else
				return false;
		}
		else
		{
			if (SellType.ISBACK(saletype) && (g.ysyjh != null && g.ysyjh.trim().length() > 0) && GlobalInfo.sysPara.inputydoc != 'D')
				return true;
			else
				return false;
		}
	}

	public boolean isNewUseSpecifyTicketBack()
	{
		return isNewUseSpecifyTicketBack(true);
	}

	// 是否已经使用了新的指定小票退货
	public boolean isNewUseSpecifyTicketBack(boolean display)
	{
		if ((GlobalInfo.sysPara.inputydoc == 'A' || GlobalInfo.sysPara.inputydoc == 'C') && SellType.ISBACK(saletype) && isbackticket)
		{
			if (display)
				new MessageBox(Language.apply("前台指定小票退货不能输入交易信息!"));
			return true;
		}

		// 调出原盘点单的模式且改盘点单被定义为不可编辑时
		if (GlobalInfo.sysPara.isblankcheckgoods == 'B' && "N".equals(checkeditflag))
		{
			if (display)
				new MessageBox(Language.apply("该盘点单不允许修改!"));
			return true;
		}

		// 团购时不允许修改商品
		if (saletype.equals(SellType.GROUPBUY_SALE) && !Groupbuy_Change())
		{
			if (display)
				new MessageBox(Language.apply("团购不允许修改交易信息"));
			return true;
		}

		/*
		 * if(SellType.ISHH(saletype))
		 * {
		 * new MessageBox("换货小票，不允许进行此操作");
		 * return false;
		 * }
		 */
		return false;
	}

	public boolean Groupbuy_Change()
	{
		return false;
	}

	public void initSellData()
	{
		// 初始化单头
		saleHead = new SaleHeadDef();
		saleHead.syjh = ConfigClass.CashRegisterCode;
		saleHead.fphm = GlobalInfo.syjStatus.fphm;
		saleHead.djlb = saletype;
		saleHead.mkt = GlobalInfo.sysPara.mktcode;
		saleHead.bc = GlobalInfo.syjStatus.bc;
		saleHead.syyh = GlobalInfo.posLogin.gh;
		saleHead.rqsj = ManipulateDateTime.getCurrentDateTime();
		saleHead.hykh = "";
		saleHead.jfkh = "";
		saleHead.thsq = "";
		saleHead.ghsq = "";
		saleHead.hysq = "";
		saleHead.sqkh = "";
		saleHead.sqktype = '1';
		saleHead.sqkzkfd = 0;
		saleHead.ysje = 0;
		saleHead.sjfk = 0;
		saleHead.zl = 0;
		saleHead.sswr_sysy = 0;
		saleHead.fk_sysy = 0;
		saleHead.hjzje = 0;
		saleHead.hjzsl = 0;
		saleHead.hjzke = 0;
		saleHead.hyzke = 0;
		saleHead.yhzke = 0;
		saleHead.lszke = 0;
		saleHead.netbz = 'N';
		saleHead.printbz = 'N';
		saleHead.hcbz = 'N';
		saleHead.buyerinfo = "";
		saleHead.jdfhdd = "";
		saleHead.salefphm = "";
		saleHead.printnum = 0;
		saleHead.bcjf = 0;
		saleHead.memo = "";
		saleHead.str1 = "";
		saleHead.str2 = "";
		saleHead.str3 = "";
		saleHead.str4 = "";
		saleHead.str5 = "";
		saleHead.str6 = "";
		saleHead.str7 = "";
		saleHead.str8 = "";
		saleHead.str9 = "";
		saleHead.str10 = "";
		saleHead.num1 = 0;
		saleHead.num2 = 0;
		saleHead.num3 = 0;
		saleHead.num4 = 0;
		saleHead.num5 = 0;
		saleHead.num6 = 0;
		saleHead.num7 = 0;
		saleHead.num8 = 0;
		saleHead.num9 = 0;
		saleHead.num10 = 0;
		saleHead.hhflag = hhflag;

		saleHead.hykname = "";
		saleHead.yfphm = "";
		saleHead.ysyjh = "";

		// 初始化单据明细
		if ((saleGoods == null) || (salePayment == null) || (goodsAssistant == null) || (payAssistant == null) || brokenAssistant == null || lastGoodsDetail == null || goodsSpare == null || realTimePrintFlag == null || stampList == null)
		{
			saleGoods = new Vector();
			salePayment = new Vector();
			goodsAssistant = new Vector();
			goodsSpare = new Vector();
			payAssistant = new Vector();
			memoPayment = new Vector();
			backPayment = new Vector();
			brokenAssistant = new Vector();
			lastGoodsDetail = new Vector();
			realTimePrintFlag = new StringBuffer();
			stampList = new Vector();
		}
		else
		{
			saleGoods.removeAllElements();
			salePayment.removeAllElements();
			goodsAssistant.removeAllElements();
			goodsSpare.removeAllElements();
			payAssistant.removeAllElements();
			brokenAssistant.removeAllElements();
			lastGoodsDetail.removeAllElements();
			memoPayment.removeAllElements();
			backPayment.removeAllElements();
			realTimePrintFlag.delete(0, realTimePrintFlag.length());
			stampList.removeAllElements();
		}

		if (refundPayment != null)
			refundPayment.removeAllElements();

		//
		saleyfje = 0;
		salezlexception = 0;
		salePayUnique = 0;
		stampRule = new SuperMarketPopRuleDef();
	}

	public void initNewSale()
	{
		// 即扫即打在开始新交易前放弃原已打印内容
		realTimePrintCancelSale();

		// 初始化小票数据
		initSellData();

		// 初始化授权为当前收银员的授权
		curGrant.privth = GlobalInfo.posLogin.privth;
		curGrant.privqx = GlobalInfo.posLogin.privqx;
		curGrant.privdy = GlobalInfo.posLogin.privdy;
		curGrant.privgj = GlobalInfo.posLogin.privgj;
		curGrant.priv = GlobalInfo.posLogin.priv;
		curGrant.dpzkl = GlobalInfo.posLogin.dpzkl;
		curGrant.zpzkl = GlobalInfo.posLogin.zpzkl;
		curGrant.thxe = GlobalInfo.posLogin.thxe;
		curGrant.privje1 = GlobalInfo.posLogin.privje1;
		curGrant.privje2 = GlobalInfo.posLogin.privje2;
		curGrant.privje3 = GlobalInfo.posLogin.privje3;
		curGrant.privje4 = GlobalInfo.posLogin.privje4;
		curGrant.privje5 = GlobalInfo.posLogin.privje5;
		curGrant.grantgz = GlobalInfo.posLogin.grantgz;

		cursqkh = GlobalInfo.posLogin.gh;
		cursqktype = '1';
		cursqkzkfd = GlobalInfo.posLogin.privje1;
		curyyygz = "";
		curyyyfph = "";

		// 初始化其他数据
		thSaleGoods = null;
		thSyjh = null;
		thFphm = 0;

		curCustomer = null;
		curBatch = null;
		curPfzkfd = 1;

		// 初始化其他数据
		saleFinish = false;
		quickpaystart = false;
		quickpaykey = 0;
		isbackticket = false;
		waitlab = false;
		preSale = null;

		// 即扫即打暑假
		haveRealTimePrint = false;
		stopRealTimePrint = false;

		// 初始化打印信息
		SaleBillMode.getDefault().setTemplateObject(saleHead, saleGoods, salePayment);
		DisplayMode.getDefault().setTemplateObject(saleHead, saleGoods);
		DisplayAdvertMode.getDefault().setTemplateObject(saleHead, saleGoods);
		Printer.getDefault().enableRealPrintMode(false);

		// 对象第一次创建时,不清除断点数据,保证能读取一次断点数据,以后每次初始化都清除断点
		if (clearBroken)
		{
			clearBrokenData();
		}

		isonlinegdjging = false;

		if (GlobalInfo.sysPara.isinputjdfhdd != 'S')
		{
			jdfhddcode = "";
			jdfhddname = "";
		}

		// 默认关闭控制商品是否享用会员折扣的开关
		isVIPZK = true;

		// 强制垃圾回收,清理一次内存数据
		System.gc();
	}

	public void addSaleGoodsObject(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		saleGoods.add(sg);
		goodsAssistant.add(goods);
		goodsSpare.add(info);

		// goods不为空才是销售的商品,查找商品对应收款规则
		if ((GlobalInfo.sysPara.havePayRule == 'Y' || GlobalInfo.sysPara.havePayRule == 'A') && goods != null && info != null)
		{
			info.payrule = DataService.getDefault().getGoodsPayRule(goods);
		}

		if (GlobalInfo.sysPara.custompayobj.indexOf("PaymentJfNew") >= 0)
		{
			GoodsJFRule jfrule = new GoodsJFRule();
			if (NetService.getDefault().getGoodsjfrule(sg.code, sg.gz, sg.catid, sg.ppcode, saleHead.rqsj, saleHead.hykh, jfrule))
			{
				info.memo1 = jfrule.jfrule;
				sg.jfrule = jfrule.jfrule;
				sg.str8 = jfrule.jfrule;
			}
		}

		setPreShopSaleQuantity();
	}

	public void setPreShopSaleQuantity()
	{
		if (preSale != null && preSale.index == saleGoods.size() - 1)
		{
			SaleGoodsDef preGoods = (SaleGoodsDef) saleGoods.get(preSale.index);

			// 电子秤不让改数量
			if (preGoods.flag != '2')
			{
				preGoods.sl = preSale.sl;
				preGoods.hjje = ManipulatePrecision.doubleConvert(preGoods.jg * preGoods.sl, 2, 1);
			}

			preSale = null;
		}
	}

	public boolean delSaleGoodsObject(int index)
	{
		saleGoods.removeElementAt(index);
		goodsAssistant.removeElementAt(index);
		goodsSpare.removeElementAt(index);

		return true;
	}

	public void addSalePayObject(SalePayDef spay, Payment payobj)
	{
		// 标记本行付款唯一序号,用于删除对应商品的分摊
		if (spay != null)
			spay.num5 = salePayUnique++;

		// 加入付款明细
		salePayment.add(spay);
		payAssistant.add(payobj);

		// 找零付款方式不计算损益
		// 付款金额已足够,计算付款损溢
		if (spay.flag != '2' && saleEvent.saleBS.calcPayBalance() <= 0)
		{
			// 先计算可找零金额
			PaymentChange pc = CreatePayment.getDefault().getPaymentChange(saleEvent.saleBS);
			StringBuffer buff = new StringBuffer();
			pc.calcPreChange(buff);
			double zl = Convert.toDouble(buff.toString());

			// 实际付款 - 找零 超过应付时，本笔付款产生了损溢，记入该付款方式溢余
			if (ManipulatePrecision.doubleConvert(saleHead.sjfk - saleyfje - salezlexception - zl) > 0)
			{
				spay.num1 = ManipulatePrecision.doubleConvert(spay.num1 + (saleHead.sjfk - saleyfje - salezlexception - zl));
			}
		}

		// 再分摊付款到商品明细
		paymentApportion(spay, payobj);
	}

	public void delSalePayObject(int index)
	{
		// 先删除商品的付款分摊
		paymentApportionDelete(index);

		// 删除付款明细
		salePayment.removeElementAt(index);
		payAssistant.removeElementAt(index);
	}

	// 删除商品分摊到的付款方式
	public void paymentApportionDelete(int index)
	{
		if (GlobalInfo.sysPara.havePayRule != 'Y' && GlobalInfo.sysPara.havePayRule != 'A')
			return;

		SalePayDef spay = (SalePayDef) salePayment.elementAt(index);
		Payment payobj = (Payment) payAssistant.elementAt(index);

		// 查找所有商品对应该付款的分摊,并删除
		if (spay != null && payobj != null)
		{
			int seqno = (int) spay.num5;
			for (int i = 0; i < goodsSpare.size(); i++)
			{
				SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(i);
				if (info == null || info.payft == null)
					continue;

				for (int j = 0; j < info.payft.size(); j++)
				{
					String[] s = (String[]) info.payft.elementAt(j);

					if (Convert.toInt(s[0]) == seqno)
					{
						info.payft.removeElementAt(j);
						j--;
					}
				}
			}
		}
	}

	// 分摊付款方式到商品
	public boolean paymentApportion(SalePayDef spay, Payment payobj)
	{
		if (GlobalInfo.sysPara.havePayRule != 'Y' && GlobalInfo.sysPara.havePayRule != 'A')
			return false;

		if (spay != null && payobj != null)
		{
			// 记录各商品分摊付款的金额,每行如下:商品编码,商品名称,已付金额,限制金额,分摊金额,对应商品行号
			Vector v = paymentApportionBySale(spay, payobj);
			if (v == null || v.size() <= 0)
				v = payobj.paymentApportionByRule();

			// 记录商品分摊金额
			payobj.paymentApportionToGoods(v);
		}

		return true;
	}

	public Vector paymentApportionBySale(SalePayDef spay, Payment payobj)
	{
		return null;
	}

	public boolean paymentApportionSummary()
	{
		if (GlobalInfo.sysPara.havePayRule != 'Y' && GlobalInfo.sysPara.havePayRule != 'A')
			return true;

		// 汇总付款分摊到商品明细的str2字段
		for (int i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);
			if (goodsSpare == null || goodsSpare.size() <= i)
				continue;
			SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(i);
			if (spinfo == null)
				continue;

			// sg.str2 = 付款行号:付款代码:分摊金额
			// payft = 付款方式唯一序号,付款代码,付款名称(主要判断同付款代码不同付款FLAG),分摊金额
			sg.str2 = "";
			for (int j = 0; spinfo.payft != null && j < spinfo.payft.size(); j++)
			{
				String[] s = (String[]) spinfo.payft.elementAt(j);
				String rowno = "";
				for (int n = 0; n < salePayment.size(); n++)
				{
					SalePayDef sp = (SalePayDef) salePayment.elementAt(n);
					if ((int) sp.num5 == Convert.toInt(s[0]))
					{
						rowno = String.valueOf(sp.rowno);
						break;
					}
				}
				if (rowno.length() > 0)
					sg.str2 += "," + rowno + ":" + s[1] + ":" + s[3];
			}
			if (sg.str2.length() > 0)
				sg.str2 = sg.str2.substring(1);
		}

		return true;
	}

	public String getTotalQuantityLabel()
	{
		if (saleGoods.size() > 0)
			return String.valueOf(saleHead.hjzsl);
		else
			return "";
	}

	public String getTotalMoneyLabel()
	{
		if (saleGoods.size() > 0)
			return ManipulatePrecision.doubleToString(saleHead.hjzje);
		else
			return "";
	}

	public String getTotalRebateLabel()
	{
		if (saleGoods.size() > 0)
			return ManipulatePrecision.doubleToString(saleHead.hjzke);
		else
			return "";
	}

	public String getTotalPayMoneyLabel()
	{
		if (saleGoods.size() > 0)
			return ManipulatePrecision.doubleToString(saleHead.ysje);
		else
			return "";
	}

	public String getSellPayMoneyLabel()
	{
		if (saleGoods.size() > 0)
			return ManipulatePrecision.doubleToString(saleyfje);
		else
			return "";
	}

	public String getVipInfoLabel()
	{
		if (curCustomer == null)
			return "";
		else
		{
			return "[" + curCustomer.code + "]" + curCustomer.name;
		}
	}

	public String getSyjFphmInfoLabel()
	{
		return saleHead.syjh + "(" + String.valueOf(saleHead.fphm) + ")";
	}

	public String getSyyInfoLabel()
	{
		return saleHead.syyh;
	}

	public String getDjlbLabel()
	{
		return SellType.getDefault().typeExchange(saleHead.djlb, saleHead.hhflag, saleHead);
	}

	public String getYfjeLabel()
	{
		if (SellType.ISBACK(saletype))
		{
			return Language.apply("应退额");
		}
		else
		{
			return Language.apply("应付额");
		}
	}

	public boolean refreshSaleData()
	{
		// 刷新单头的小票号
		if (saleHead != null)
		{
			saleHead.syjh = ConfigClass.CashRegisterCode;
			saleHead.fphm = GlobalInfo.syjStatus.fphm;
			saleHead.djlb = saletype;
			saleHead.mkt = GlobalInfo.sysPara.mktcode;
			saleHead.bc = GlobalInfo.syjStatus.bc;
			saleHead.syyh = GlobalInfo.posLogin.gh;
			saleHead.rqsj = ManipulateDateTime.getCurrentDateTime();
		}

		// 刷新商品明细小票号
		SaleGoodsDef saleGoodsDef = null;

		for (int i = 0; i < saleGoods.size(); i++)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);

			saleGoodsDef.syjh = saleHead.syjh;
			saleGoodsDef.fphm = GlobalInfo.syjStatus.fphm;

			// 销售交易才清除付款时计算的数据
			if (SellType.ISSALE(saleHead.djlb))
			{
				if (saleGoodsDef.flag == '1' || saleGoodsDef.flag == '5' || (goodsAssistant.size() > i && goodsAssistant.elementAt(i) == null))
				{
					delSaleGoodsObject(i);

					i--;
					continue;
				}
				saleGoodsDef.zsdjbh = null;
				saleGoodsDef.zszke = 0;
				saleGoodsDef.zszkfd = 0;
			}
		}

		// 刷新付款明细小票号
		SalePayDef salePayDef = null;

		for (int i = 0; i < salePayment.size(); i++)
		{
			salePayDef = (SalePayDef) salePayment.elementAt(i);
			salePayDef.syjh = saleHead.syjh;
			salePayDef.fphm = GlobalInfo.syjStatus.fphm;
		}

		//
		SaleBillMode.getDefault().setTemplateObject(saleHead, saleGoods, salePayment);
		DisplayMode.getDefault().setTemplateObject(saleHead, saleGoods);
		DisplayAdvertMode.getDefault().setTemplateObject(saleHead, saleGoods);

		return true;
	}

	public void doSaleGoodsDisplayFinishedEvent()
	{

	}

	public void doSaleGoodsDisplayEvent(SaleGoodsDef oldGoods, int index)
	{
		/*
		 * // index行的商品被修改
		 * if (oldGoods != null && index >= 0)
		 * {
		 * 
		 * }
		 * else
		 * // index行的商品被删除
		 * if (oldGoods != null && index < 0)
		 * {
		 * 
		 * }
		 * else
		 * // index行的商品被新增
		 * if (oldGoods == null && index >= 0)
		 * {
		 * 
		 * }
		 */
	}

	public void querySyjStatus()
	{
		if (SellType.ISCHECKINPUT(this.saletype))
		{
			GlobalInfo.syjStatus.status = StatusType.STATUS_CHECK;
		}
		else if (saleGoods.size() > 0)
		{
			GlobalInfo.syjStatus.status = StatusType.STATUS_SALEING;
		}
		else
		{
			GlobalInfo.syjStatus.status = StatusType.STATUS_LOGIN;
		}
	}

	public boolean getSaleGoodsDisplay()
	{
		SaleGoodsDef goodsDef1, goodsDef2;

		querySyjStatus();
		
		// 检查已显示的信息是否需要更新
		for (int i = 0; i < Math.min(lastGoodsDetail.size(), saleGoods.size()); i++)
		{
			goodsDef1 = (SaleGoodsDef) saleGoods.elementAt(i);
			goodsDef2 = (SaleGoodsDef) lastGoodsDetail.elementAt(i);

			if (!goodsDef1.code.equals(goodsDef2.code) || (goodsDef1.hjje - goodsDef1.hjzk) != (goodsDef2.hjje - goodsDef2.hjzk) || ManipulatePrecision.doubleCompare(goodsDef1.sl, goodsDef2.sl, 4) != 0 || !goodsDef1.unit.equals(goodsDef2.unit) || (SellType.ISCHECKINPUT(saletype) && GlobalInfo.sysPara.isblankcheckgoods == 'B' && !goodsDef1.name.equals(goodsDef2.name)))
			{
				// 修改已经改变的商品明细
				saleEvent.table.modifyRow(rowInfo(goodsDef1), i);

				// 商品附加处理
				doSaleGoodsDisplayEvent(goodsDef2, i);

				// 即扫即打被改变的商品
				realTimePrintGoods(goodsDef2, i);

			}
		}

		// 检查是否有新的商品需要添加
		if (saleGoods.size() > lastGoodsDetail.size())
		{
			for (int i = lastGoodsDetail.size(); i < saleGoods.size(); i++)
			{
				goodsDef1 = (SaleGoodsDef) saleGoods.elementAt(i);

				// 刷新明细显示
				saleEvent.updateTable(rowInfo(goodsDef1));

				// 商品附加处理
				doSaleGoodsDisplayEvent(null, i);
			}

		}
		else
		{
			// 当前商品列表行数减少，从列表删除多余行
			if (saleGoods.size() < lastGoodsDetail.size())
			{
				for (int i = lastGoodsDetail.size() - 1; i >= saleGoods.size(); i--)
				{
					goodsDef1 = (SaleGoodsDef) lastGoodsDetail.elementAt(i);

					// 删除多余的商品
					getDeleteGoodsDisplay(i, goodsDef1);
				}
			}
		}

		doSaleGoodsDisplayFinishedEvent();

		// 盘点和买卷交易不实时打印
		if (!SellType.ISCHECKINPUT(saletype))
		{
			// 即扫即打状态，第一个商品前先打印交易头
			if (lastGoodsDetail.size() <= 0 && saleGoods.size() > lastGoodsDetail.size())
			{
				realTimePrintStartSale();
			}

			// 当商品为第一次输入时,不需要打印但需要扩展打印标记
			if (saleGoods.size() == 1)
			{
				realTimePrintGoods(null, -1);
			}
			else
			{
				// 即扫即打新增的商品,最后一个新增的商品不在本次打印,输入下一个商品时打印前一个商品
				for (int i = 0; i < saleGoods.size() - 1; i++)
				{
					realTimePrintGoods(null, i);
				}
			}
		}

		// 做判断，检查是否有跳号出现，如果跳号，刷新表
		if (saleEvent.table.getTableCount() != saleGoods.size())
		{
			lastGoodsDetail = new Vector();
			saleEvent.table.clear();
			getSaleGoodsDisplay();
		}

		// 备份本次商品列表明细
		lastGoodsDetail = null;
		lastGoodsDetail = cloneSaleGoodsVector(saleGoods);

		// 在要刷新商品列表时,写入断点数据
		writeBrokenData();

		return true;

	}

	public boolean getDeleteGoodsDisplay(int index, SaleGoodsDef oldgoods)
	{
		// 删除列表
		lastGoodsDetail.removeElementAt(index);
		saleEvent.table.deleteRow(index);

		// 刷新选中
		if (saleEvent.table.getItemCount() > index)
			saleEvent.table.setSelection(index);
		else
			saleEvent.table.setSelection(saleEvent.table.getItemCount() - 1);

		// || SellType.ISCOUPON(saletype)
		if (SellType.ISCHECKINPUT(saletype))
			return true;

		// 商品附加处理
		doSaleGoodsDisplayEvent(oldgoods, -1);

		// 即扫即打被删除的商品
		if (isRealTimePrint())
		{
			if (realTimePrintFlag.charAt(index) == 'Y')
				realTimePrintGoods(oldgoods, -1);
			realTimePrintFlag.deleteCharAt(index);

			// 如果所有商品都删除掉了，作废之前打印
			if (saleGoods.size() <= 0)
				realTimePrintCancelSale();
		}

		return true;
	}

	/*
	 * public String[] getNewSaleGoodsItem()
	 * {
	 * String[] detail = new String[8];
	 * SaleGoodsDef goodsDef = (SaleGoodsDef) saleGoods.lastElement();
	 * detail[1] = goodsDef.barcode;
	 * detail[2] = goodsDef.name;
	 * detail[3] = goodsDef.unit;
	 * detail[4] = ManipulatePrecision.doubleToString(goodsDef.sl,4,1,true);
	 * detail[5] = ManipulatePrecision.doubleToString(goodsDef.jg);
	 * detail[6] = ManipulatePrecision.doubleToString(goodsDef.hjzk);
	 * detail[7] = ManipulatePrecision.doubleToString(goodsDef.hjje -
	 * goodsDef.hjzk,2,1);
	 * 
	 * // 在要刷新商品列表时,写入断点数据
	 * writeBrokenData();
	 * 
	 * return detail;
	 * }
	 */
	public String[] rowInfo(SaleGoodsDef goodsDef)
	{
		if (SellType.ISCHECKINPUT(saletype))
		{
			String[] detail = new String[8];

			if (goodsDef.inputbarcode != null && goodsDef.inputbarcode.trim().length() > 0 && GlobalInfo.sysPara.barcodeshowcode == 'N')
			{
				detail[1] = goodsDef.inputbarcode;
			}
			else if (GlobalInfo.sysPara.barcodeshowcode == 'Y')
			{
				detail[1] = goodsDef.code;
			}
			else
			{
				detail[1] = goodsDef.barcode;
			}

			detail[2] = goodsDef.name;
			detail[3] = goodsDef.unit;

			if (goodsDef.sqkh != null && goodsDef.sqkh.trim().length() > 0)
			{
				detail[4] = goodsDef.sqkh;
			}

			if (goodsDef.jg > 0 || goodsDef.type == 'Z')
			{
				detail[5] = ManipulatePrecision.doubleToString(goodsDef.jg);
				detail[6] = ManipulatePrecision.doubleToString(goodsDef.sl, 4, 1, true);
			}
			else
			{
				detail[5] = "";
				detail[6] = "";

				// 不定价商品需要盘点数量时，界面也要显示数量
				if (GlobalInfo.sysPara.ischeckquantity == 'Y')
				{
					detail[6] = ManipulatePrecision.doubleToString(goodsDef.sl, 4, 1, true);
				}
			}

			detail[7] = ManipulatePrecision.doubleToString(goodsDef.hjje, 2, 1);

			return detail;
		}
		else
		{
			String[] detail = new String[8];

			if (goodsDef.inputbarcode != null && goodsDef.inputbarcode.trim().length() > 0 && GlobalInfo.sysPara.barcodeshowcode == 'N')
			{
				detail[1] = goodsDef.inputbarcode;
			}
			else if (GlobalInfo.sysPara.barcodeshowcode == 'Y')
			{
				detail[1] = goodsDef.code;
			}
			else
			{
				detail[1] = goodsDef.barcode;
			}

			if (GlobalInfo.syjDef.issryyy == 'Y' && goodsDef.gz != null && !goodsDef.gz.equals("") && GlobalInfo.sysPara.showgoodscode == 'Y')
			{
				detail[2] = "[" + goodsDef.gz + "]" + goodsDef.name;
			}
			else if (GlobalInfo.sysPara.showgoodscode == 'C')
			{
				detail[2] = "[" + goodsDef.code + "]" + goodsDef.name;
			}
			else if (GlobalInfo.sysPara.showgoodscode == 'B')
			{
				detail[2] = "[" + goodsDef.barcode + "]" + goodsDef.name;
			}
			else
			{
				detail[2] = goodsDef.name;
			}

			detail[3] = goodsDef.unit;
			detail[4] = ManipulatePrecision.doubleToString(goodsDef.sl, 4, 1, true);
			if (saletype == SellType.GROUPBUY_SALE)
			{
				detail[5] = ManipulatePrecision.doubleToString(goodsDef.lsj);
			}
			else
			{
				detail[5] = ManipulatePrecision.doubleToString(goodsDef.jg);
			}
			detail[6] = ManipulatePrecision.doubleToString(goodsDef.hjzk) + ((goodsDef.hjzk > 0) && (goodsDef.hjje - goodsDef.hjzk > 0) ? "(" + ManipulatePrecision.doubleToString((goodsDef.hjje - goodsDef.hjzk) / goodsDef.hjje * 100, 0, 1) + "%)" : "");
			detail[7] = ManipulatePrecision.doubleToString(goodsDef.hjje - goodsDef.hjzk, 2, 1);

			return detail;
		}
	}

	public void writeSellObjectToStream(ObjectOutputStream s) throws Exception
	{
		try
		{
			brokenAssistant.insertElementAt(String.valueOf(salePayUnique), 0);

			s.writeObject(saleHead);
			s.writeObject(saleGoods);
			s.writeObject(goodsAssistant);
			s.writeObject(goodsSpare);
			s.writeObject(brokenAssistant);
			s.writeObject(curGrant);
			s.writeObject(curCustomer);
			s.writeObject(curyyygz);
			s.writeObject(cursqkh);
			// s.writeObject(memoPayment);
			s.writeObject(new Character(cursqktype));
			s.writeObject(new Double(cursqkzkfd));
			s.writeObject(thSyjh);
			s.writeObject(new Long(thFphm));
			s.writeObject(new Boolean(isbackticket));
			s.writeObject(checkdjbh);
			s.writeObject(salePayment);
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}

	}

	public void readStreamToSellObject(ObjectInputStream s) throws Exception
	{
		try
		{
			// 读取
			SaleHeadDef saleHead1 = (SaleHeadDef) s.readObject();
			Vector saleGoods1 = (Vector) s.readObject();
			Vector goods1 = (Vector) s.readObject();
			Vector spare1 = (Vector) s.readObject();
			Vector brokenAssistant1 = (Vector) s.readObject();
			OperRoleDef curGrant1 = (OperRoleDef) s.readObject();
			CustomerDef curCustomer1 = (CustomerDef) s.readObject();
			String curyyygz1 = (String) s.readObject();
			String cursqkh1 = (String) s.readObject();
			// Vector memoPayment1 = (Vector)s.readObject();
			Character cursqktype1 = (Character) s.readObject();
			Double cursqkzkfd1 = (Double) s.readObject();
			String thSyjh1 = (String) s.readObject();
			Long thFphm1 = (Long) s.readObject();
			Boolean isbackticket1 = (Boolean) s.readObject();
			String checkdjbh1 = (String) s.readObject();
			Vector salePayment1 = (Vector) s.readObject();

			// 赋对象
			saleHead = saleHead1;
			saleGoods = saleGoods1;
			goodsAssistant = goods1;
			goodsSpare = spare1;
			brokenAssistant = brokenAssistant1;
			curGrant = curGrant1;
			curCustomer = curCustomer1;
			curyyygz = curyyygz1;
			cursqkh = cursqkh1;
			// memoPayment = memoPayment1;
			cursqktype = cursqktype1.charValue();
			cursqkzkfd = cursqkzkfd1.doubleValue();
			thSyjh = thSyjh1;
			thFphm = thFphm1.longValue();
			isbackticket = isbackticket1.booleanValue();
			checkdjbh = checkdjbh1;
			salePayment = salePayment1;

			salePayUnique = Convert.toInt(brokenAssistant.remove(0));
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
	}

	public boolean writeBrokenData()
	{
		FileOutputStream f = null;

		if (saleGoods.size() <= 0 || GlobalInfo.sysPara.havebroken == 'N')
		{
			clearBrokenData();
			return false;
		}

		try
		{
			String name = ConfigClass.LocalDBPath + "/Broken.dat";

			f = new FileOutputStream(name);
			ObjectOutputStream s = new ObjectOutputStream(f);

			// 多写一个当前工号
			s.writeObject(GlobalInfo.posLogin.gh);

			// 将交易对象写入对象文件
			brokenAssistant.removeAllElements();
			writeSellObjectToStream(s);

			// 多写付款对象
			// s.writeObject(salePayment);

			s.flush();
			s.close();
			f.close();
			s = null;
			f = null;

			AccessDayDB.getDefault().writeWorkLog("写入断电保护文件", StatusType.WORK_WRITEBRODATA);

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

	public boolean readBrokenData()
	{
		FileInputStream f = null;

		// 本次断点读取以后,后续每次初始化交易时,都清除断点数据
		clearBroken = true;

		try
		{
			String name = ConfigClass.LocalDBPath + "/Broken.dat";

			f = new FileInputStream(name);
			ObjectInputStream s = new ObjectInputStream(f);

			// 读取工号
			// String gh = (String)
			s.readObject();

			// 读交易对象
			readStreamToSellObject(s);

			AccessDayDB.getDefault().writeWorkLog("读取断电保护文件", StatusType.WORK_READBRODATA);

			// 读付款对象
			// Vector salePayment1 = (Vector) s.readObject();
			// salePayment = salePayment1;
			hhflag = saleHead.hhflag;

			// 关闭断点文件
			s.close();
			s = null;
			f.close();
			f = null;

			// 提货交易放弃断点
			if (SellType.ISPREPARETAKE(saleHead.djlb)) { return false; }

			// 刷新数据
			saletype = saleHead.djlb;
			refreshSaleData();

			// 处理从断点读入数据
			doBrokenData();

			// 计算应付金额
			saleEvent.saleBS.calcHeadYfje();

			// 刷新界面显示
			saleEvent.updateSaleGUI();

			// 焦点到编码输入框
			if (saleGoods.size() > 0)
			{
				SaleGoodsDef g = (SaleGoodsDef) saleGoods.elementAt(saleGoods.size() - 1);
				saleEvent.yyyh.setText(g.yyyh);
				if (GlobalInfo.syjDef.issryyy == 'Y')
					saleEvent.gz.setText(g.gz);
				saleEvent.saleform.setFocus(saleEvent.code);

				// 检查是否存在付款断点
				if (salePayment.size() > 0)
				{
					// 先清除全部付款对象列表
					payAssistant.removeAllElements();

					// 根据付款信息创建付款对象
					SalePayDef sp = null;
					for (int i = 0; i < salePayment.size(); i++)
					{
						sp = (SalePayDef) salePayment.elementAt(i);

						// 创建付款对象
						Payment pay = CreatePayment.getDefault().createPaymentBySalePay(sp, saleHead);
						if (pay == null)
						{
							// 放弃所有已付款
							salePayment.removeAllElements();
							payAssistant.removeAllElements();
							return true;
						}

						// 增加已付款
						payAssistant.add(pay);
					}

					// 发送付款键,让系统直接进入付款
					NewKeyListener.sendKey(GlobalVar.Pay);
				}
			}

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

	public static void clearBrokenData()
	{
		try
		{
			String name = ConfigClass.LocalDBPath + "/Broken.dat";

			new File(name).delete();

			if (PathFile.fileExist(name))
			{
				AccessDayDB.getDefault().writeWorkLog("清除断电保护文件失败", StatusType.WORK_CLEARBRODATA);
				new MessageBox(Language.apply("断点保护文件没有被删除,请检查磁盘!"));
			}
			AccessDayDB.getDefault().writeWorkLog(Language.apply("清除断电保护文件"), StatusType.WORK_CLEARBRODATA);
		}
		catch (Exception e)
		{
			e.printStackTrace();

			AccessDayDB.getDefault().writeWorkLog(Language.apply("清除断电保护文件失败"), StatusType.WORK_CLEARBRODATA);
			new MessageBox(Language.apply("删除断点保护文件失败!\n\n") + e.getMessage());
		}
	}

	public static String checkBrokenData()
	{
		FileInputStream f = null;

		try
		{
			String name = ConfigClass.LocalDBPath + "/Broken.dat";

			if (!new File(name).exists())
				return null;

			f = new FileInputStream(name);
			ObjectInputStream s = new ObjectInputStream(f);

			// 读取断点数据中的工号信息
			String gh = (String) s.readObject();

			s.close();
			s = null;
			f.close();
			f = null;

			// 返回断点的工号,开机自动登录
			return gh;
		}
		catch (Exception e)
		{
			e.printStackTrace();
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

		return null;
	}

	public void doBrokenData()
	{

	}

	// 请确定Vector内的元素一定能够被clone?
	public Vector cloneSaleGoodsVector(Vector org)
	{
		Vector v = new Vector();

		for (int i = 0; i < org.size(); i++)
		{
			v.add(((SaleGoodsDef) org.elementAt(i)).clone());
		}

		return v;
	}

	
	// /////////////// 即扫即打相关
	public boolean isRealTimePrint()
	{
		if (GlobalInfo.syjDef.printfs == '1' && ((saleHead != null && !SellType.ISEXERCISE(saleHead.djlb) && !SellType.ISCHECKINPUT(saletype)) || GlobalInfo.sysPara.lxprint == 'Y') && !stopRealTimePrint)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public void getMinusSaleCom(SaleGoodsDef dcom)
	{
		dcom.sl = 0 - dcom.sl;
		dcom.hjje = 0 - dcom.hjje;
		dcom.hjzk = 0 - dcom.hjzk;
		dcom.hyzke = 0 - dcom.hyzke;
		dcom.yhzke = 0 - dcom.yhzke;
		dcom.lszke = 0 - dcom.lszke;
		dcom.lszre = 0 - dcom.lszre;
		dcom.lszzk = 0 - dcom.lszzk;
		dcom.lszzr = 0 - dcom.lszzr;
		dcom.plzke = 0 - dcom.plzke;
	}

	public void realTimePrintGoods(SaleGoodsDef oldGoods, int index)
	{
		if (!isRealTimePrint())
			return;

		// 填充即扫即打模式的打印标记
		while (realTimePrintFlag.length() < saleGoods.size())
		{
			realTimePrintFlag.append('N');
		}

		// 打印以被即扫即打的商品，对应的相反商品数据
		if (oldGoods != null && (index < 0 || (index >= 0 && realTimePrintFlag.charAt(index) != 'N')))
		{
			getMinusSaleCom(oldGoods);
			SaleBillMode.getDefault().printRealTimeDetail(oldGoods);

			// 设置为未打印标志
			if (index >= 0)
			{
				realTimePrintFlag.setCharAt(index, 'N');
			}
		}

		// 打印真实商品明细
		if (index >= 0 && realTimePrintFlag.charAt(index) == 'N' && oldGoods == null)
		{
			SaleBillMode.getDefault().printRealTimeDetail(index);

			// 设置为已打印标志
			realTimePrintFlag.setCharAt(index, 'Y');
		}
		/*
		 * 删除暂不使用
		 * // 标记即扫即打模式的打印标记
		 * while (realTimePrintFlag.length() > saleGoods.size())
		 * {
		 * realTimePrintFlag.deleteCharAt(realTimePrintFlag.length() - 1);
		 * }
		 */
		// 标记
		setHaveRealTimePrint(true);
	}

	public void realTimePrintStartSale()
	{
		if (!isRealTimePrint())
			return;

		// 打印即扫即打小票头
		SaleBillMode.getDefault().setTemplateObject(saleHead, saleGoods, salePayment);
		SaleBillMode.getDefault().printRealTimeHead();

		// 标记即扫即打开始
		Printer.getDefault().enableRealPrintMode(true);

		// 标记
		setHaveRealTimePrint(true);
	}

	public void realTimePrintCancelSale()
	{
		// 没有已打印的即扫即打票据
		if (!haveRealTimePrint)
			return;

		if (!isRealTimePrint())
			return;

		// 标记即扫即打结束
		Printer.getDefault().enableRealPrintMode(false);

		// 放弃打印
		SaleBillMode.getDefault().printRealTimeCancel();

		// 标记
		setHaveRealTimePrint(false);
	}

	public boolean stopRealTimePrint(boolean flag)
	{
		stopRealTimePrint = flag;

		return stopRealTimePrint;
	}

	public boolean setHaveRealTimePrint(boolean flag)
	{
		haveRealTimePrint = flag;

		return haveRealTimePrint;
	}

	public String getUnUploadSaleHead()
	{
		String num = "";
		String command = "select count(*) from SALEHEAD where NETBZ='N'";

		Object obj = GlobalInfo.dayDB.selectOneData(command);

		if (obj == null)
		{
			num = "0";
		}
		else
		{
			num = obj.toString();
		}

		return num;
	}

	public String getUnUploadPayInHead()
	{
		String num = "";
		String command = "select count(*) from PAYINHEAD where NETBZ='N'";
		Object obj = GlobalInfo.dayDB.selectOneData(command);
		if (obj == null)
		{
			num = "0";
		}
		else
		{
			num = obj.toString();
		}

		return num;
	}

	/**
	 * 是否为换货商品
	 * 
	 * @param saleGoodsDef
	 * @return
	 */
	public boolean isHHGoods(SaleGoodsDef saleGoodsDef)
	{
		try
		{
			if (saleGoodsDef == null || saleGoodsDef.str13 == null || !saleGoodsDef.str13.equalsIgnoreCase("T"))
				return false;
			if (SellType.ISHH(saletype) && saleGoodsDef.str13.equalsIgnoreCase("T"))
				return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return false;

	}
}
