package custom.localize.Cctd;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.CustomerVipZklDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;

import custom.localize.Bstd.Bstd_SaleBS;

public class Cctd_SaleBS extends Bstd_SaleBS
{
	public String syjh = "";
	public long fphm = 0L;

	public void findJfExchangeGoods(int index)
	{
		if ((this.curCustomer != null) && (this.curCustomer.status.indexOf("未实名") >= 0))
		{
			new MessageBox("请先到会员中心登记相关信息！");
			return;
		}
		super.findJfExchangeGoods(index);
	}

	public boolean checkCust(MzkResultDef mrd)
	{
		if ((mrd != null) && (mrd.cardname != null) && (mrd.cardname.indexOf("未实名") >= 0))
		{
			new MessageBox("请先到会员中心登记相关信息!");
			return false;
		}
		return true;
	}

	public void exchangeSale()
	{
		if ((this.saletype.equals("1")) || (this.saletype.equals("4")))
		{
			char oldHhflag = this.hhflag;
			if (this.hhflag == 'N')
			{
				this.hhflag = 'Y';
			}

			String type = this.saletype;
			if ((this.saletype.equals("1")) && (this.hhflag == 'Y'))
			{
				type = "4";
			}

			if (this.saleEvent.saleform.setSaleType(type))
				return;
			this.hhflag = oldHhflag;
		}
		else
		{
			new MessageBox("当前状态不能进行换货交易!");
		}
	}

	public boolean findGoods(String code, String yyyh, String gz)
	{
		if ((SellType.ISBACK(this.saletype)) && (this.hhflag == 'Y'))
		{
			new MessageBox("换退状态下必须输入原收银机号和原小票号");
			return false;
		}

		if (this.saleGoods.size() <= 0)
		{
			if ((SellType.ISSALE(this.saletype)) && (this.hhflag == 'Y') && (!(((Cctd_DataService) DataService.getDefault()).getHHback(ConfigClass.CashRegisterCode, new StringBuffer()))))
			{
				new MessageBox("上笔不是【换退】，本笔不能为【换销】");
				this.hhflag = 'N';
				this.saleEvent.initOneSale("1");
				return false;
			}

			if (((Cctd_AccessDayDB) AccessDayDB.getDefault()).getHcHHbackinfo(ConfigClass.CashRegisterCode, String.valueOf(GlobalInfo.syjStatus.fphm - 1L)) > 0L)
			{
				new MessageBox("上笔为【红冲换销】，必须先进行【红冲换退】才能进行其他操作");
				return false;
			}
		}

		return super.findGoods(code, yyyh, gz,null);
	}

	public void initOneSale(String type)
	{
		syjh = "";
		fphm = 0;

		// 换消需要输入原收银机号和原小票号
		StringBuffer buff = new StringBuffer();
		if (((Cctd_AccessDayDB) AccessDayDB.getDefault()).getlasthhbackinfo(ConfigClass.CashRegisterCode, buff))
		{
			syjh = ConfigClass.CashRegisterCode;
			fphm = Convert.toLong(buff.toString());
			if (!SellType.ISSALE(type) || hhflag != 'Y')
			{
				type = SellType.RETAIL_SALE;
				hhflag = 'Y';
				new MessageBox("上笔交易为换货退货，本笔必须为换货销售");
			}
		}

		super.initOneSale(type);

		if (((Cctd_AccessDayDB) AccessDayDB.getDefault()).getlasthhbackinfo(ConfigClass.CashRegisterCode, buff))
		{
			SaleHeadDef temp = new SaleHeadDef();
			if (((Cctd_AccessDayDB) AccessDayDB.getDefault()).getlasthhbackHead(temp, ConfigClass.CashRegisterCode))
			{
				if (temp.hykh != null && temp.hykh.length() > 0)
				{
					curCustomer = new CustomerDef();
					HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();
					CustomerDef cust = bs.findMemberCard("!" + temp.hykh);
					if (cust != null)
					{
						curCustomer = cust;
						saleHead.hykh = cust.code;
						saleHead.hytype = cust.type;
						saleHead.str4 = cust.valstr2;
						saleEvent.setVIPInfo(getVipInfoLabel());
					}
				}
			}
		}

		// super.initOneSale(type);

		if (SellType.ISSALE(saleHead.djlb))
		{
			NewKeyListener.sendKey(GlobalVar.MemberGrant);
		}
	}

	public void addMemoPayment()
	{
		
		super.addMemoPayment();
		try
		{
			Vector payDetail = new Vector();
			StringBuffer buff = new StringBuffer();
			if (!(((Cctd_AccessDayDB) AccessDayDB.getDefault()).gethhbackpay(ConfigClass.CashRegisterCode, buff, payDetail)))
				return;
			boolean append = true;

			
			for (int j = 0; j < this.salePayment.size(); ++j)
			{
				SalePayDef def1 = (SalePayDef) this.salePayment.elementAt(j);
				if (!(def1.paycode.equals("0710")))
					continue;
				append = false;
				break;
			}

			if (!(append))
				return;
			for (int i = 0; i < payDetail.size(); ++i)
			{
				SalePayDef def = (SalePayDef) payDetail.elementAt(i);
				PayModeDef pmd = DataService.getDefault().searchPayMode(def.paycode);
				Payment pay = CreatePayment.getDefault().createPaymentByPayMode(pmd, this);
				pay.salepay = def;
				addSalePayObject(pay.salepay, pay);
			}
			
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}
	}

	public CustomerVipZklDef getGoodsVIPZKL(int index)
	{
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);

		// 未刷卡
		if (!checkMemberSale() || (curCustomer == null)) { return null; }

		// 查询商品VIP折上折折扣率定义
		CustomerVipZklDef zklDef = new CustomerVipZklDef();

		if (DataService.getDefault().findVIPZKL(zklDef, curCustomer.code, curCustomer.type, goodsDef))
		{
			// 促销允许折上折且会员类允许折上折，VIP折扣才允许折上折
			if (!(popvipzsz == 'Y' && (zklDef.iszsz == 'Y' || zklDef.iszsz == 'A')))
				zklDef.iszsz = 'N';

			// 有柜组和商品的VIP折扣定义
			return zklDef;
		}
		else
		{
			return null;
		}
	}

	public void addSaleGoodsObject(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		if ((this.saletype.equals("1")) && (this.hhflag == 'Y'))
		{
			sg.yfphm = this.fphm;
			sg.ysyjh = this.syjh;
		}

		super.addSaleGoodsObject(sg, goods, info);

		if (SellType.ISCHECKINPUT(saletype)) { return; }

		if (goods != null)
		{
			// 查找该商品是否能够积分消费
			getGoodsIsJFXF(goods, info);
		}
	}

	public boolean HHinit()
	{
		return ((this.hhflag == 'Y') && (SellType.ISSALE(this.saletype)));
	}

	public boolean exchangeSale(boolean ishhPay)
	{
		if ((this.hhflag == 'Y') && (!(SellType.ISSALE(this.saletype))))
			return true;
		return (!(ishhPay));
	}

	public boolean memberGrant()
	{
		if ((this.saletype.equals("1")) && (this.hhflag == 'Y')) { return false; }
		return super.memberGrant();
	}

	public boolean memberGrantFinish(CustomerDef cust)
	{
		cust.iszk = 'N';
		return super.memberGrantFinish(cust);
	}

	private boolean getGoodsIsJFXF(GoodsDef goods, SpareInfoDef info)
	{
		if (GlobalInfo.isOnline)
		{
			Cctd_NetService netservice = (Cctd_NetService) NetService.getDefault();
			return netservice.getGoodsIsJFXF(saleHead, goods, info, NetService.getDefault().getMemCardHttp(131));
		}
		else
		{
			return false;
		}
	}

	public void paySell()
	{
		if ((((Cctd_DataService) DataService.getDefault()).getHHback(ConfigClass.CashRegisterCode, new StringBuffer())) && (((!(SellType.ISSALE(this.saletype))) || (this.hhflag != 'Y'))))
		{
			this.saleEvent.initOneSale("1");
			return;
		}	
		super.paySell();
	}

	public boolean paySellStart()
	{
		if (!(super.paySellStart()))
			return false;

		if ((SellType.ISSALE(this.saleHead.djlb)) && (this.hhflag == 'Y'))
		{
			double ysje = ((Cctd_AccessDayDB) AccessDayDB.getDefault()).gethhbackYsje(ConfigClass.CashRegisterCode, String.valueOf(this.saleHead.fphm - 1L));
			if (this.saleHead.ysje < ysje)
			{
				new MessageBox("换销的商品总额必须大于换退的商品总额\n请继续输入其他商品");
				return false;
			}
		}
		return true;
	}

	public boolean backToInit(PayModeDef pay)
	{

		if (GlobalInfo.sysPara.loopInputPay != null && !GlobalInfo.sysPara.loopInputPay.equals(""))
		{
			String[] s = GlobalInfo.sysPara.loopInputPay.split(",");
			for (int i = 0; i < s.length; i++)
			{
				if (pay.code.equals(s[i].trim())) { return false; }
			}
		}

		if (GlobalInfo.sysPara.payex != null && GlobalInfo.sysPara.payex.split(",").length >= 1)
		{
			String[] paycode = GlobalInfo.sysPara.payex.split(",");
			for (int i = 0; i < paycode.length; i++)
			{
				if (pay.code.equals(paycode[i])) { return false; }
			}
		}

		return true;
	}

	/*
	 * public void findJfExchangeGoods(int index)
	 * {
	 * if (hhflag == 'Y')
	 * {
	 * new MessageBox("换货状态不允许使用积分换购");
	 * return ;
	 * }
	 * // 无会员卡不进行积分换购
	 * if (curCustomer == null)
	 * {
	 * new MessageBox("没有刷会员卡不允许积分换购");
	 * return;
	 * }
	 * 
	 * // 无0509付款方式,不能进行积分换购
	 * PayModeDef paymode = DataService.getDefault().searchPayMode("0509");
	 * if (paymode == null)
	 * {
	 * new MessageBox("没有找到0509付款方式");
	 * return;
	 * }
	 * 
	 * // 查找积分换购商品规则
	 * JfSaleRuleDef jfrd = new JfSaleRuleDef();
	 * SaleGoodsDef saleGoodsDef = (SaleGoodsDef)saleGoods.get(index);
	 * 
	 * if
	 * (!((Bcrm_DataService)DataService.getDefault()).getJfExchangeGoods(jfrd,
	 * saleGoodsDef.code,saleGoodsDef.gz,curCustomer.code,curCustomer.type))
	 * {
	 * return;
	 * }
	 * if ((saleGoodsDef.hjje - saleGoodsDef.hjzk) <= jfrd.money *
	 * saleGoodsDef.sl)
	 * {
	 * new MessageBox("当前商品销售金额小于等于兑换金额\n不能进行换购");
	 * return;
	 * }
	 * 
	 * if (saleGoodsDef.name.indexOf("【换】") < 0) saleGoodsDef.name += "【换】";
	 * 
	 * double maxsl = -1;
	 * // 判断换购数量
	 * if (String.valueOf(jfrd.char1).length() > 0 && jfrd.char1 == 'Y')
	 * {
	 * double sum = 0;
	 * // 按商品行号查找对应的积分换购付款
	 * for (int i = 0; i < saleGoods.size(); i++)
	 * {
	 * SpareInfoDef info = (SpareInfoDef)goodsSpare.elementAt(i);
	 * SaleGoodsDef salegoods = (SaleGoodsDef) saleGoods.elementAt(i);
	 * if (i == index) continue;
	 * 
	 * if (info.char2 == 'Y' && salegoods.code.equals(saleGoodsDef.code))
	 * {
	 * sum += salegoods.sl;
	 * }
	 * }
	 * 
	 * maxsl = ManipulatePrecision.doubleConvert(jfrd.num1 - sum);
	 * 
	 * if (ManipulatePrecision.doubleConvert(sum + saleGoodsDef.sl) > jfrd.num1)
	 * {
	 * // "包含此商品后，"+jfrd.str1+" 此规则已换购数量"+ManipulatePrecision.doubleConvert(sum
	 * + saleGoodsDef.sl)+"\n"
	 * new MessageBox( saleGoodsDef.code+" 超出最大可换购数量【"+jfrd.num1+"】");
	 * return;
	 * }
	 * }
	 * 
	 * // 提示是否进行换购
	 * MessageBox me = new MessageBox("您目前可用" + jfrd.jf + "积分加上" +
	 * ManipulatePrecision.doubleToString(jfrd.money) + "元\n换购该商品\n是否要进行换购?",
	 * null, true);
	 * if (me.verify() != GlobalVar.Key1)
	 * {
	 * return;
	 * }
	 * 
	 * // 弹出提示框
	 * StringBuffer buffer = new StringBuffer();
	 * double max = ManipulatePrecision.doubleConvert((int)(jfrd.num2 /
	 * jfrd.jf));
	 * 
	 * // 如果存在限量
	 * if (maxsl > 0) max = Math.min(max, maxsl);
	 * 
	 * if (max < 1)
	 * {
	 * new MessageBox("换购类型积分余额不足");
	 * return;
	 * }
	 * 
	 * buffer.append(max);
	 * do{
	 * if (new TextBox().open("请输入要兑换的数量","数量",
	 * "目前最大可兑换的数量为"+ManipulatePrecision.doubleToString(max), buffer, 1,max,
	 * true, TextBox.IntegerInput, -1))
	 * {
	 * double inputsl = Convert.toDouble(buffer.toString());
	 * if (!inputQuantity(index,inputsl))
	 * {
	 * continue;
	 * }
	 * 
	 * }
	 * else
	 * {
	 * return ;
	 * }
	 * break;
	 * }while(true);
	 * 
	 * //先删除换购付款
	 * delJfExchangeByGoods(index);
	 * 
	 * SaleGoodsDef sgd = (SaleGoodsDef)saleGoodsDef.clone();
	 * 
	 * // 生成积分换购付款方式
	 * PaymentJfNew pay = new PaymentJfNew(paymode,this);
	 * 
	 * // 记录单号，折扣及分担
	 * double jfyhje;
	 * sgd.yhdjbh = jfrd.str1;
	 * jfyhje = ManipulatePrecision.doubleConvert(sgd.hjje - jfrd.num4 * sgd.sl
	 * - jfrd.money * sgd.sl);
	 * sgd.yhzke = jfyhje;
	 * if (sgd.yhzke < 0)
	 * {
	 * new MessageBox("负折扣 不允许换购");
	 * return;
	 * }
	 * sgd.yhzkfd = jfrd.num3;
	 * getZZK(sgd);
	 * 
	 * double jf = getDetailOverFlow(ManipulatePrecision.doubleConvert((sgd.hjje
	 * - sgd.hjzk - jfrd.money * sgd.sl)));
	 * if (pay != null &&
	 * pay.createJfExchangeSalePay(jf,ManipulatePrecision.mul(
	 * jfrd.jf,sgd.sl),jfrd))
	 * {
	 * // 转换名称用于显示
	 * sgd.name = "(积分" + jfrd.jf + "+" +
	 * ManipulatePrecision.doubleToString(jfrd.money) + "元换购);" + sgd.name;
	 * 
	 * // 在付款对象记录商品信息(要扣的积分,XX积分,兑单个商品XX金额 ，换购规则单号,商品编码，商品数量)
	 * // pay.salepay.str2 = String.valueOf(saleGoods.size()) + "," + sgd.code;
	 * // 扣减的积分,积分价值,规则单号,加价金额,原收银机,原小票,数量,商品编码
	 * pay.salepay.idno = jfrd.str2 + "," +
	 * ManipulatePrecision.mul(jfrd.jf,sgd.sl) + "," + jf +","+ jfrd.str1 +","+
	 * ManipulatePrecision.mul(jfrd.money,sgd.sl) + "," + sgd.sl + "," +
	 * sgd.code;
	 * // 积分种类
	 * pay.salepay.str4 = jfrd.str2;
	 * 
	 * pay.salepay.payno = curCustomer.code;
	 * 
	 * // 增加已付款
	 * addSalePayObject(pay.salepay,pay);
	 * 
	 * SpareInfoDef info = (SpareInfoDef)goodsSpare.elementAt(index);
	 * 
	 * // 积分换购商品标志
	 * info.char2 = 'Y';
	 * info.str3 = String.valueOf(pay.salepay.num5)+","+jfrd.str1;
	 * //记录积分扣回的分摊
	 * if (info.payft == null) info.payft = new Vector();
	 * String[] ft = new String[]
	 * {String.valueOf(pay.salepay.num5),pay.salepay.paycode
	 * ,pay.salepay.payname,String.valueOf(jf)};
	 * info.payft.add(ft);
	 * 
	 * // 记录单号，折扣及分担
	 * saleGoodsDef.yhdjbh = jfrd.str1;
	 * saleGoodsDef.yhzke = jfyhje;
	 * saleGoodsDef.yhzkfd = jfrd.num3;
	 * getZZK(saleGoodsDef);
	 * 
	 * calcHeadYsje();
	 * // 计算剩余付款
	 * calcPayBalance();
	 * // 刷新商品列表
	 * saleEvent.updateTable(getSaleGoodsDisplay());
	 * saleEvent.setTotalInfo();
	 * saleEvent.table.modifyRow(rowInfo(sgd), index);
	 * }
	 * else
	 * {
	 * new MessageBox("积分换购付款对象创建失败\n请删除商品后重新试一次!");
	 * }
	 * 
	 * sgd = null;
	 * }
	 * 
	 * public void takeBackTicketInfo(SaleHeadDef thsaleHead,Vector
	 * thsaleGoods,Vector thsalePayment)
	 * {
	 * //在断点保护的情况下，可能出现退货付款方式存入2次的情况，加以判断
	 * memoPayment.removeAllElements();
	 * 
	 * //goodsSpare = new Vector();
	 * for (int i=0;i<saleGoods.size();i++)
	 * {
	 * SaleGoodsDef sgd = (SaleGoodsDef)saleGoods.elementAt(i);
	 * 
	 * //liwenjin Add 在退货时添加对应的分摊类
	 * //goodsSpare.add(new SpareInfoDef());
	 * 
	 * // 小票头记录原单号
	 * saleHead.yfphm = String.valueOf(sgd.yfphm);
	 * saleHead.ysyjh = sgd.ysyjh;
	 * 
	 * // 积分换购付款方式处理
	 * if (sgd.str2.indexOf("0509") >= 0)
	 * {
	 * int st = sgd.str2.lastIndexOf(",",sgd.str2.indexOf("0509"));
	 * int end = sgd.str2.indexOf(",", sgd.str2.indexOf("0509"));
	 * if (st <= 0) st = 0;
	 * if (end <= 0) end = sgd.str2.length();
	 * String line = sgd.str2.substring(st, end);
	 * 
	 * if (line.charAt(0) == ',') line .substring(1);
	 * 
	 * String rowno = line.substring(0, line.indexOf(":"));
	 * int rowno1 = -1;
	 * try
	 * {
	 * rowno1 = Integer.parseInt(rowno);
	 * rowno1 --;
	 * }
	 * catch(Exception er)
	 * {
	 * er.printStackTrace();
	 * }
	 * 
	 * if (rowno1 >= 0)
	 * {
	 * SalePayDef spd1 = (SalePayDef) thsalePayment.elementAt(rowno1);
	 * spd1.syjh = ConfigClass.CashRegisterCode;
	 * if (spd1.paycode.equals("0509"))
	 * {
	 * PayModeDef pmd = DataService.getDefault().searchPayMode("0509");
	 * PaymentCustJfSale pay = new PaymentCustJfSale(pmd,this);
	 * 
	 * if (sgd.sl < sgd.memonum1)
	 * {
	 * String salepaylist[] = spd1.idno.split(",");
	 * double jf = Double.parseDouble(salepaylist[0]);
	 * jf = ManipulatePrecision.doubleConvert(jf * (sgd.sl/sgd.memonum1));
	 * spd1.je = ManipulatePrecision.doubleConvert(spd1.je *
	 * (sgd.sl/sgd.memonum1));
	 * spd1.idno = jf+","+spd1.idno.substring(spd1.idno.indexOf(",")+1);
	 * }
	 * 
	 * pay.salepay = spd1;
	 * memoPayment.add(pay);
	 * }
	 * }
	 * }
	 * 
	 * // 将原付款分摊删除
	 * sgd.str2 = "";
	 * }
	 * }
	 */

}
