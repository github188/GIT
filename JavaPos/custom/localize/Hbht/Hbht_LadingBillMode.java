package custom.localize.Hbht;

import java.util.HashMap;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.PrintTemplate;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Hbht_LadingBillMode extends PrintTemplate
{

	protected static Hbht_LadingBillMode ladingBillMode = null;

	protected final static int SBM_text = 0;
	protected final static int SBM_mktname = 1;
	protected final static int SBM_syjh = 2;
	protected final static int SBM_gh = 3;
	protected final static int SBM_name = 4;
	protected final static int SBM_fphm = 5;
	protected final static int SBM_rq = 6;
	protected final static int SBM_sj = 7;
	protected final static int SBM_index = 8;
	protected final static int SBM_code = 9;
	protected final static int SBM_goodname = 10;
	protected final static int SBM_sl = 11;
	protected final static int SBM_jg = 12;
	protected final static int SBM_sjje = 13;
	protected final static int SBM_dphjzk = 14;
	protected final static int SBM_hjzsl = 15;
	protected final static int SBM_hjzke = 16;
	protected final static int SBM_ysje = 17;
	protected final static int SBM_sjfk = 18;
	protected final static int SBM_zl = 19;
	protected final static int SBM_hykh = 20;
	protected final static int SBM_sqkh = 21;
	protected final static int SBM_payname = 22;
	protected final static int SBM_ybje = 23;
	protected final static int SBM_payno = 24;
	protected final static int SBM_djlb = 25;
	protected final static int SBM_sysy = 26;
	protected final static int SBM_printnum = 27;
	protected final static int SBM_inputbarcode = 28;
	protected final static int SBM_unit = 29;
	protected final static int SBM_cjje = 30;
	protected final static int SBM_cjdj = 31;
	protected final static int SBM_jfkh = 32;
	protected final static int SBM_bcjf = 33;
	protected final static int SBM_yyyh = 34;
	protected final static int SBM_ysjedx = 35;
	protected final static int SBM_mktcode = 36;
	protected final static int SBM_hyzke = 37;
	protected final static int SBM_sqzkhj = 38;
	protected final static int SBM_printrq = 39;
	protected final static int SBM_ljjf = 40;
	protected final static int SBM_gz = 41;
	protected final static int SBM_ye = 42;
	protected final static int SBM_printinfo1 = 43;
	protected final static int SBM_printinfo2 = 44;
	protected final static int SBM_spzkbfb = 45;
	protected final static int SBM_Aqje = 46;
	protected final static int SBM_Bqje = 47;
	protected final static int SBM_Jfmemo = 48;
	protected final static int SBM_hjzje = 49;
	protected final static int SBM_printsj = 50;
	protected final static int SBM_goodnamebreak = 51;
	protected final static int SBM_fpje = 52;
	protected final static int SBM_payfkje = 53;
	protected final static int SBM_paycode = 54;
	protected final static int SBM_changebillname = 55;
	protected final static int SBM_sjfkfpje = 56;
	protected final static int SBM_sjfkfpjedx = 57;
	protected final static int SBM_salefphm = 58;
	protected final static int SBM_yfje = 59;
	protected final static int SBM_Memo = 60; // java_getsalems里type = 119
	// 的memo字段，用于打印后台返回的信息，比如赠券
	protected final static int SBM_barcode = 61;
	protected final static int SBM_hysjinfo = 62;
	protected final static int SBM_SyjGroup = 63;
	protected final static int SBM_gzname = 64;
	protected final static int SBM_phone = 65;// 移动(在线)充值手机号码
	protected final static int SBM_zkspzje = 66;// 促销商品总金额
	protected final static int SBM_zjspzje = 67;// 正价商品总金额
	protected final static int SBM_fkyy = 68;// 付款溢余
	protected final static int SBM_hymaxdate = 69;// 会员有效期

	protected final static int SBM_thsq = 70;// 退货授权
	protected final static int SBM_ghsq = 71;// 工号授权

	protected final static int SBM_custItem = 201; // 客户化的打印项从201开始编号,自己控制

	public SaleHeadDef salehead;
	protected Vector salegoods;
	protected Vector salepay;
	protected Vector originalsalepay;
	protected Vector originalsalegoods;
	protected int printnum = 0;
	protected int goodnamemaxlength = 0;
	public Vector salemsgift = null;
	public long salemsinvo = 0;
	protected long salefph = 0;

	public static Vector vbillmode = new Vector();
	public static HashMap printConfig = null; // 打印配置

	public Vector hidePayCode = null; // 需隐藏账号的代码
	public Vector printDifTitle = null; // 多联小票打印不同抬头

	public static Hbht_LadingBillMode getDefault()
	{
		return getDefault("LadingPrintMode.ini");
	}

	public static Hbht_LadingBillMode getDefault(String fileName)
	{
		if (Hbht_LadingBillMode.ladingBillMode == null)
			Hbht_LadingBillMode.ladingBillMode = new Hbht_LadingBillMode();

		return Hbht_LadingBillMode.ladingBillMode;
	}

	public boolean ReadTemplateFile()
	{
		String line = GlobalVar.ConfigPath + "//LadingPrintMode.ini";
		return ReadTemplateFile(line);
	}

	public boolean ReadTemplateFile(String name)
	{
		super.InitTemplate();

		return super.ReadTemplateFile(Title, name);
	}

	public void setTemplateObject(SaleHeadDef h, Vector s, Vector p)
	{
		salehead = h;
		salegoods = convertGoodsDetail(s);
		salepay = convertPayDetail(p);

		originalsalegoods = s;
		originalsalepay = p;

		super.salehead_temp = h;
		super.salegoods_temp = salegoods;
		super.salepay_temp = salepay;
		super.originalsalegoods_temp = s;
		super.originalsalepay_temp = p;
	}

	public SaleHeadDef getSalehead()
	{
		return salehead;
	}

	public Vector getSalegoods()
	{
		return salegoods;
	}

	public Vector getSalepay()
	{
		return salepay;
	}

	protected Vector convertGoodsDetail(Vector s)
	{
		if (s != null && s.size() > 1)
		{
			// 盘点时不在这里进行汇总
			if (GlobalInfo.syjDef.printfs == '2' && GlobalInfo.sysPara.isGoodsSryPrn == 'Y' && !SellType.ISCHECKINPUT(salehead.djlb))
			{
				Vector tmpSryGoods = new Vector();
				boolean[] flag = new boolean[s.size()]; // 标记已被查找过的商品

				// 将相同商品进行汇总
				for (int i = 0; i < s.size(); i++)
				{
					SaleGoodsDef firstGoods = (SaleGoodsDef) ((SaleGoodsDef) s.elementAt(i)).clone();

					if (flag[i])
						continue;

					for (int j = i + 1; j < s.size(); j++)
					{
						if (flag[j])
							continue;

						// 电子称商品不进行汇总
						if (firstGoods.flag == '2')
							continue;

						SaleGoodsDef secondGoods = (SaleGoodsDef) ((SaleGoodsDef) s.elementAt(j)).clone();
						// 依据条码，编码，柜组对商品则进行汇总
						if (firstGoods.barcode.equals(secondGoods.barcode) && firstGoods.code.equals(secondGoods.code) && firstGoods.gz.equals(secondGoods.gz) && ManipulatePrecision.doubleCompare(firstGoods.jg, secondGoods.jg, 2) == 0 && ManipulatePrecision.doubleCompare(firstGoods.hjzk / firstGoods.sl, secondGoods.hjzk / secondGoods.sl, 2) == 0)

						{
							firstGoods.sl += secondGoods.sl; // 数量
							firstGoods.hjzk += secondGoods.hjzk; // 合计折扣
							firstGoods.hjje += secondGoods.hjje; // 合计金额
							flag[j] = true;
						}
					}
					tmpSryGoods.add(firstGoods);
				}
				return tmpSryGoods;
			}
		}
		return s;
	}

	protected Vector convertPayDetail(Vector p)
	{
		// 减找零付款
		boolean calcSjfk = false;
		for (int i = 0; i < Pay.size(); i++)
		{
			PrintTemplateItem item = (PrintTemplateItem) Pay.elementAt(i);
			if (item.code.trim().equals(String.valueOf(SBM_payfkje)))
			{
				calcSjfk = true;
			}
		}

		if (calcSjfk)
		{
			SalePayDef pay1 = null;
			SalePayDef pay2 = null;

			for (int k = 0; k < p.size(); k++)
			{
				pay1 = (SalePayDef) p.elementAt(k);

				if (pay1.flag == '1')
				{
					for (int i = 0; i < p.size(); i++)
					{
						pay2 = (SalePayDef) p.elementAt(i);

						if ((pay2.flag == '2') && pay2.paycode.equals(pay1.paycode) && (pay2.ybje != 0))
						{
							if (pay1.ybje > pay2.ybje)
							{
								pay1.ybje = pay1.ybje - pay2.ybje;
								pay2.ybje = 0;
							}
							else if (pay1.ybje <= pay2.ybje)
							{
								pay2.ybje = pay2.ybje - pay1.ybje;
								pay1.ybje = 0;
								p.removeElementAt(k);
								k--;
							}

							break;
						}
					}
				}
			}
		}

		// 不汇总付款方式
		if (GlobalInfo.sysPara.printpaysummary.equals("N")) { return p; }

		// 分组汇总相同的付款方式
		Vector newp = new Vector();
		SalePayDef spd = null;
		SalePayDef spd1 = null;

		for (int i = 0; i < p.size(); i++)
		{
			spd = (SalePayDef) p.elementAt(i);

			int j = 0;

			for (j = 0; j < newp.size(); j++)
			{
				spd1 = (SalePayDef) newp.elementAt(j);

				if (spd.paycode.equals(spd1.paycode) && (spd.flag == spd1.flag))
				{
					if (convertPayDetail(spd, spd1))
					{
						break;
					}
				}
			}

			if (j >= newp.size())
			{
				newp.add(spd.clone());
			}
			else
			{
				// 金额汇总
				spd1.ybje += spd.ybje;
				spd1.je += spd.je;
				spd1.num1 += spd.num1;

				// 如果汇总的付款方式帐号不一致,则清除记录的付款方式帐号
				if (!spd.payno.equals(spd1.payno))
				{
					spd1.payno = "";
				}
			}
		}

		return newp;
	}

	protected boolean convertPayDetail(SalePayDef spd, SalePayDef spd1)
	{
		// 检查是否系统参数设置的需要汇总付款方式,如果是则汇总打印
		String[] s = GlobalInfo.sysPara.printpaysummary.split(",");

		for (int i = 0; i < s.length; i++)
		{
			if (spd.paycode.equals(s[i].trim())) { return true; }
		}

		// 金卡工程付款方式不汇总
		PayModeDef mode = DataService.getDefault().searchPayMode(spd.paycode);

		if ((mode != null) && (mode.isbank == 'Y')) { return false; }

		// 返券卡付款由于券种不同也不汇总
		if (CreatePayment.getDefault().isPaymentFjk(spd.paycode)) { return false; }

		// 付款帐号一致可汇总
		return spd.payno.equals(spd1.payno);
	}

	protected String getItemDataString(PrintTemplateItem item, int index)
	{

		String line = null;

		line = extendCase(item, index);

		String text = item.text;

		if (text != null && text != "")
		{
			// 开头找&&
			if (text.indexOf("&") == 0 && text.indexOf("&", 1) > 0)
			{
				String text1 = text.substring(text.indexOf("&") + 1, text.indexOf("&", 1));
				if (text1 != null && text1.length() > 0)
				{
					String type[] = text1.split("\\|");
					if (type.length > 0)
					{
						int i = 0;
						for (; i < type.length; i++)
						{
							// 如果当前交易类型与&&里面设的类型能匹配上，则把&&后面部分赋值给text
							String type1 = type[i];
							if (salehead.djlb.equals(type1))
							{
								text = text.substring(text.indexOf("&", 1) + 1);
								break;
							}
						}
						if (i >= type.length) { return ""; }
					}
				}
				else
				{
					// &&里面没有设值，把&&后面部分赋值给text
					text = text.substring(text.indexOf("&", 1) + 1);
				}
			}
		}

		if (line == null)
		{
			switch (Integer.parseInt(item.code))
			{
				case SBM_text: // 文本

					if (text == null)
					{
						line = "";
					}
					else
					{
						if (text.trim().indexOf("calc|") == 0)
						{
							line = super.calString(text, index);
						}
						else
						{
							line = text;
						}
					}

					break;

				case SBM_mktname: // 商场名称

					if (GlobalInfo.sysPara.mktname != null)
					{
						line = GlobalInfo.sysPara.mktname;
					}
					else
					{
						line = "";
					}

					break;

				case SBM_syjh: // 收银机号
					line = GlobalInfo.syjStatus.syjh;

					break;

				case SBM_gh: // 收银员号
					line = salehead.syyh;

					break;

				case SBM_name: // 收银员名称

					if (salehead.syyh.trim().equals(GlobalInfo.posLogin.gh.trim()))
					{
						line = GlobalInfo.posLogin.name;
					}
					else
					{
						OperUserDef staff = new OperUserDef();

						if (!DataService.getDefault().getOperUser(staff, salehead.syyh.trim()))
						{
							line = "";
						}
						else
						{
							line = staff.name;
						}
					}

					break;

				case SBM_fphm: // 小票号码
					line = Convert.increaseLong(salehead.fphm, 8);

					break;

				case SBM_rq: // 交易日期
					line = salehead.rqsj.split(" ")[0];

					break;

				case SBM_sj: // 交易时间
					line = salehead.rqsj.split(" ")[1];

					break;

				case SBM_printrq: // 打印日期
					line = ManipulateDateTime.getCurrentDate();

					break;

				case SBM_printsj: // 打印时间
					line = ManipulateDateTime.getCurrentTime();

					break;

				case SBM_index: // 商品序号
					line = String.valueOf(index + 1);

					break;

				case SBM_code: // 商品代码
					line = ((SaleGoodsDef) salegoods.elementAt(index)).code;

					break;

				case SBM_goodname: // 商品名称
					line = ((SaleGoodsDef) salegoods.elementAt(index)).name;
					// 记录商品所能打印的最大长度
					goodnamemaxlength = item.length;

					break;

				case SBM_goodnamebreak: // 需要换行打印的商品名称

					String goodname = ((SaleGoodsDef) salegoods.elementAt(index)).name;

					// 商品行不够打印商品名称的时候
					if (goodnamemaxlength < goodname.length())
					{
						// 将打不出来的部分赋值给商品名称换行打印项
						line = Convert.newSubString(goodname, goodnamemaxlength, goodname.getBytes().length);
					}
					else
					{
						line = "";
					}

					break;

				case SBM_sl: // 数量
					line = ManipulatePrecision.doubleToString(((SaleGoodsDef) salegoods.elementAt(index)).sl * SellType.SELLSIGN(salehead.djlb), 4, 1, true);

					break;

				case SBM_jg: // 售价
					line = ManipulatePrecision.doubleToString(((SaleGoodsDef) salegoods.elementAt(index)).jg);

					break;

				case SBM_sjje: // 售价金额（数量*售价）
					line = ManipulatePrecision.doubleToString(((SaleGoodsDef) salegoods.elementAt(index)).hjje * SellType.SELLSIGN(salehead.djlb));

					break;

				case SBM_dphjzk: // 单品合计折扣

					if (((SaleGoodsDef) salegoods.elementAt(index)).hjzk == 0)
					{
						line = null;
					}
					else
					{
						line = ManipulatePrecision.doubleToString(((SaleGoodsDef) salegoods.elementAt(index)).hjzk * SellType.SELLSIGN(salehead.djlb));
					}

					break;

				case SBM_hjzsl: // 总件数
					line = ManipulatePrecision.doubleToString(salehead.hjzsl * SellType.SELLSIGN(salehead.djlb), 4, 1, true);

					break;

				case SBM_hjzke: // 总折扣

					if (salehead.hjzke == 0)
					{
						line = null;
					}
					else
					{
						line = ManipulatePrecision.doubleToString(salehead.hjzke * SellType.SELLSIGN(salehead.djlb));
					}

					break;

				case SBM_ysje: // 应收金额
					line = ManipulatePrecision.doubleToString(salehead.ysje * SellType.SELLSIGN(salehead.djlb));

					break;

				case SBM_yfje: // 应付金额
					line = ManipulatePrecision.doubleToString((salehead.ysje + salehead.sswr_sysy) * SellType.SELLSIGN(salehead.djlb));

					break;

				case SBM_sjfk: // 实收金额
					line = ManipulatePrecision.doubleToString(salehead.sjfk * SellType.SELLSIGN(salehead.djlb));

					break;

				case SBM_zl: // 找零金额
					line = ManipulatePrecision.doubleToString(salehead.zl);

					break;

				case SBM_hymaxdate: // 会员卡号

					if ((salehead.hymaxdate == null) || (salehead.hymaxdate.length() <= 0))
					{
						line = null;
					}
					else
					{
						ManipulateDateTime mdt = new ManipulateDateTime();

						if (mdt.compareDate(salehead.hymaxdate, mdt.getDateBySign()) <= Integer.parseInt(GlobalInfo.sysPara.hyMaxdateMsg))
						{
							line = salehead.hymaxdate;
						}
						else
						{
							line = null;
						}

					}

					break;

				case SBM_sqkh: // 授权卡号

					if ((salehead.sqkh == null) || (salehead.sqkh.length() <= 0))
					{
						line = null;
					}
					else
					{
						line = salehead.sqkh;
					}

					break;

				case SBM_thsq: // 退货授权

					if ((salehead.thsq == null) || (salehead.thsq.length() <= 0))
					{
						line = null;
					}
					else
					{
						line = salehead.thsq;
					}

					break;

				case SBM_ghsq: // 工号授权

					if ((salehead.ghsq == null) || (salehead.ghsq.length() <= 0))
					{
						line = null;
					}
					else
					{
						line = salehead.ghsq;
					}

					break;
				case SBM_payname: // 付款方式名称
					line = ((SalePayDef) salepay.elementAt(index)).payname;

					break;

				case SBM_ybje: // 付款方式金额
					line = ManipulatePrecision.doubleToString(((SalePayDef) salepay.elementAt(index)).ybje * SellType.SELLSIGN(salehead.djlb));

					break;

				case SBM_payno: // 付款方式帐号
					String payno = ((SalePayDef) salepay.elementAt(index)).payno;
					String code = ((SalePayDef) salepay.elementAt(index)).paycode;

					line = payno;

					if ((line == null) || (line.length() <= 0))
						line = null;

					break;

				case SBM_djlb: // 交易类型
					line = String.valueOf(SellType.getDefault().typeExchange(salehead.djlb, salehead.hhflag, salehead));

					break;

				case SBM_sysy: // 收银损溢金额

					if ((salehead.sswr_sysy + salehead.fk_sysy) == 0)
					{
						line = null;
					}
					else
					{
						line = ManipulatePrecision.doubleToString(salehead.sswr_sysy + salehead.fk_sysy);
					}

					break;

				case SBM_printnum: // 重打小票标志及重打次数

					if (salehead.printnum == 0)
					{
						line = null;
					}
					else
					{
						line = "**" + Language.apply("重印") + salehead.printnum + "**";
					}

					break;

				case SBM_inputbarcode: // 打印输入商品编码

					if (((SaleGoodsDef) salegoods.elementAt(index)).inputbarcode != null && ((SaleGoodsDef) salegoods.elementAt(index)).inputbarcode.trim().length() > 0)
					{
						line = ((SaleGoodsDef) salegoods.elementAt(index)).inputbarcode;
					}
					else if (GlobalInfo.syjDef.issryyy == 'N')
					{
						line = ((SaleGoodsDef) salegoods.elementAt(index)).barcode;
					}
					else
					{
						line = ((SaleGoodsDef) salegoods.elementAt(index)).code;
					}

					break;

				case SBM_barcode: // 打印输入商品编码
					line = ((SaleGoodsDef) salegoods.elementAt(index)).barcode;

					break;

				case SBM_unit: // 商品单位
					line = String.valueOf(((SaleGoodsDef) salegoods.elementAt(index)).unit);

					break;

				case SBM_cjje: // 成交金额
					line = ManipulatePrecision.doubleToString((((SaleGoodsDef) salegoods.elementAt(index)).hjje - ((SaleGoodsDef) salegoods.elementAt(index)).hjzk) * SellType.SELLSIGN(salehead.djlb));

					break;

				case SBM_cjdj: // 成交单价
					line = ManipulatePrecision.doubleToString(ManipulatePrecision.div((((SaleGoodsDef) salegoods.elementAt(index)).hjje - ((SaleGoodsDef) salegoods.elementAt(index)).hjzk), ((SaleGoodsDef) salegoods.elementAt(index)).sl));

					break;

				case SBM_jfkh: // 积分卡号

					if (salehead.jfkh.length() <= 0)
					{
						line = null;
					}
					else
					{
						line = String.valueOf(salehead.jfkh);
					}

					break;

				case SBM_bcjf: // 本次积分

					if (salehead.bcjf == 0)
					{
						line = null;
					}
					else
					{
						line = ManipulatePrecision.doubleToString(salehead.bcjf);
					}

					break;

				case SBM_yyyh: // 营业员号
					line = String.valueOf(((SaleGoodsDef) salegoods.elementAt(index)).yyyh);

					break;

				case SBM_ysjedx: // 人民币大写应收金额
					line = ManipulatePrecision.getFloatConverChinese(salehead.ysje);

					break;

				case SBM_mktcode: // 卖场代码
					line = GlobalInfo.sysPara.mktcode;

					break;

				case SBM_hyzke: // 会员折扣合计

					if (salehead.hyzke == 0)
					{
						line = null;
					}
					else
					{
						line = ManipulatePrecision.doubleToString(salehead.hyzke * SellType.SELLSIGN(salehead.djlb));
					}

					break;

				case SBM_sqzkhj: // 授权折扣合计

					if (salehead.lszke == 0)
					{
						line = null;
					}
					else
					{
						line = ManipulatePrecision.doubleToString(salehead.lszke * SellType.SELLSIGN(salehead.djlb));
					}

					break;
				case SBM_zkspzje:
					double cxspzje = 0;
					if (ManipulatePrecision.doubleCompare(salehead.hjzke, 0, 2) > 0)
					{
						for (int i = 0; i < salegoods.size(); i++)
						{
							SaleGoodsDef msgd = (SaleGoodsDef) salegoods.get(i);
							if (ManipulatePrecision.doubleCompare(msgd.hjzk, 0, 2) > 0)
							{
								cxspzje = ManipulatePrecision.add(cxspzje, ManipulatePrecision.sub(msgd.hjje, msgd.hjzk));
							}
						}
					}

					line = ManipulatePrecision.doubleToString(cxspzje * SellType.SELLSIGN(salehead.djlb));
					break;
				case SBM_zjspzje:
					double zjspzje = 0;
					for (int i = 0; i < salegoods.size(); i++)
					{
						SaleGoodsDef msgd = (SaleGoodsDef) salegoods.get(i);
						if (ManipulatePrecision.doubleCompare(msgd.hjzk, 0, 2) <= 0)
						{
							zjspzje = ManipulatePrecision.add(zjspzje, ManipulatePrecision.sub(msgd.hjje, msgd.hjzk));
						}
					}

					line = ManipulatePrecision.doubleToString(zjspzje * SellType.SELLSIGN(salehead.djlb));
					break;
				case SBM_ljjf: // 累计积分

					if (salehead.ljjf == 0)
					{
						line = null;
					}
					else
					{
						line = ManipulatePrecision.doubleToString(salehead.ljjf);
					}

					break;

				case SBM_gz: // 商品柜组
					if (index < 0)
						index = 0;
					line = ((SaleGoodsDef) salegoods.elementAt(index)).gz;

					break;
				case SBM_gzname: // 商品柜组名称
					if (index < 0)
						index = 0;
					line = ((SaleGoodsDef) salegoods.elementAt(index)).gz;

					Object obj = GlobalInfo.localDB.selectOneData("select NAME from MANAFRAME where GZ='" + line + "'");
					if (obj != null && !String.valueOf(obj).equals(""))
					{
						line = String.valueOf(obj).trim();
					}
					else
					{
						line = "";
					}
					break;
				case SBM_ye: // 付款余额

					if (((SalePayDef) salepay.elementAt(index)).kye <= 0)
					{
						line = null;
					}
					else
					{
						line = ManipulatePrecision.doubleToString(((SalePayDef) salepay.elementAt(index)).kye);
					}

					break;

				case SBM_spzkbfb: // 总折扣百分比

					SaleGoodsDef saleGoodsDef = (SaleGoodsDef) salegoods.elementAt(index);

					// double je = saleGoodsDef.hjje - saleGoodsDef.hjzk;
					String zkbfb = ManipulatePrecision.doubleToString((saleGoodsDef.hjzk * 100) / saleGoodsDef.hjje, 1, 1, true);
					line = zkbfb + "%";

					break;

				case SBM_Aqje: // A券金额

					if ((salehead.memo != null) && (salehead.memo.split(",").length > 1))
					{
						String[] row = salehead.memo.split(",");
						double aje = Convert.toDouble(row[0]);

						if (aje > 0)
						{
							line = ManipulatePrecision.doubleToString(aje);
						}
					}

					break;

				case SBM_Bqje: // B券金额

					if ((salehead.memo != null) && (salehead.memo.split(",").length > 1))
					{
						String[] row = salehead.memo.split(",");
						double bje = Convert.toDouble(row[1]);

						if (bje > 0)
						{
							line = ManipulatePrecision.doubleToString(bje);
						}
					}

					break;

				case SBM_printinfo1: // 自定义打印信息
				case SBM_printinfo2: // 自定义打印信息
				{
					String printInfo = null;

					if (Integer.parseInt(item.code) == SBM_printinfo1)
					{
						printInfo = GlobalInfo.sysPara.printInfo1;
					}
					else
					{
						printInfo = GlobalInfo.sysPara.printInfo2;
					}

					if ((printInfo == null) || printInfo.trim().equals(""))
					{
						line = null;
					}
					else
					{
						line = null;

						String dt = new ManipulateDateTime().getDateByEmpty();
						String[] l = printInfo.split(";");

						for (int i = 0; i < l.length; i++)
						{
							String[] s = l[i].split(",");

							if (s.length < 3)
							{
								continue;
							}

							if ((dt.compareTo(s[0]) >= 0) && (dt.compareTo(s[1]) <= 0) && !s[2].trim().equals(""))
							{
								if (line == null)
								{
									line = "";
								}

								line += (s[2].trim() + "\n");
							}
						}

						if (line != null)
						{
							line = line.substring(0, line.length() - 1);
						}
						else if (line == null && printInfo.length() > 0)
						{
							line = printInfo;
						}
					}

					break;
				}

				case SBM_Jfmemo:

					if ((salehead.str5 != null) && (salehead.str5.length() > 0))
					{
						line = salehead.str5;
					}

					break;

				case SBM_hjzje:

					if (salehead.hjzje == 0)
					{
						line = null;
					}
					else
					{
						line = ManipulatePrecision.doubleToString(salehead.hjzje * SellType.SELLSIGN(salehead.djlb));
					}

					break;

				case SBM_fpje:

					String[] paycodes = text.split("\\|");
					SalePayDef payDef = null;
					StringBuffer payInfo = new StringBuffer(Language.apply("发票金额:\n "));

					for (int i = 0; i < paycodes.length; i++)
					{
						for (int j = 0; j < salepay.size(); j++)
						{
							payDef = (SalePayDef) salepay.elementAt(j);

							if ((payDef.flag == '1') && payDef.paycode.equals(paycodes[i]))
							{
								payInfo.append(payDef.payname.trim() + ":" + ManipulatePrecision.doubleToString(payDef.ybje * SellType.SELLSIGN(salehead.djlb)) + "\n ");
							}
						}
					}

					text = "";
					line = payInfo.toString().trim();

					break;

				case SBM_payfkje:
					line = ManipulatePrecision.doubleToString(((SalePayDef) salepay.elementAt(index)).ybje * SellType.SELLSIGN(salehead.djlb));

					break;

				case SBM_paycode: // 付款方式代码
					line = ((SalePayDef) salepay.elementAt(index)).paycode;

					break;

				case SBM_changebillname: // 商品发票名称
					line = ((SaleGoodsDef) salegoods.elementAt(index)).str9;

					if (line == null || line.trim().length() < 1)
					{
						line = ((SaleGoodsDef) salegoods.elementAt(index)).name;
					}

					break;

				case SBM_salefphm:// 打印收银员的发票编号
					line = Convert.increaseLong(this.salefph, item.length);
					break;
				case SBM_Memo:
					if (salemsgift != null)
					{
						for (int i = 0; i < salemsgift.size(); i++)
						{
							GiftGoodsDef def = (GiftGoodsDef) this.salemsgift.elementAt(i);
							if (def.type.equals("119"))
							{
								line = def.memo;
								break;
							}
						}
					}
					break;
				// 会员升级信息
				case SBM_hysjinfo:
					line = salehead.str4;
					break;
				// 收银机组
				case SBM_SyjGroup:
					line = GlobalInfo.syjDef.priv;
					break;
				// 移动(在线)充值手机号码
				case SBM_phone:
					if (AccessLocalDB.getDefault().checkMobileCharge(((SaleGoodsDef) salegoods.elementAt(0)).barcode) != null)
					{
						line = ((SaleGoodsDef) salegoods.elementAt(index)).batch;
					}
					else
					{
						line = null;
					}

					break;
				// 付款溢余
				case SBM_fkyy:
					double yy = ((SalePayDef) salepay.elementAt(index)).num1;
					if (yy != 0)
						line = String.valueOf(yy);
					else
						line = null;
					break;
				case SBM_hykh: // 会员卡号

					if ((salehead.hykh == null) || (salehead.hykh.length() <= 0))
					{
						line = null;
					}
					else
					{
						line = salehead.hykh;
					}

					break;
			}
		}

		if ((line != null) && line.equals("&!"))
		{
			line = null;
		}

		if ((line != null) && (Integer.parseInt(item.code) != 0) && (text != null) && !text.trim().equals(""))
		{
			// line = item.text + line;
			int maxline = item.length - Convert.countLength(text);

			line = text + Convert.appendStringSize("", line, 0, maxline, maxline, item.alignment);
		}

		return line;
	}

	public void printBill()
	{
		for (int i = 0; i < salegoods.size(); i++)
		{
			printHeader();
			printGoods(i);
			printTotal();
			printPay();
		}

		printBottom();
		printCutPaper();
	}

	public void printPay()
	{
		// 设置打印区域
		setPrintArea("Pay");

		// 循环打印付款明细
		for (int i = 0; i < salepay.size(); i++)
		{
			SalePayDef spd = (SalePayDef) salepay.elementAt(i);

			printVector(getCollectDataString(Pay, i, Width));
		}

	}

	public void printCutPaper()
	{
		if (printstrack != -1)
		{
			super.printCutPaper();
		}
		else
		{
			if ((SellType.ISBACK(salehead.djlb) || SellType.ISHC(salehead.djlb)) && (GlobalInfo.sysPara.printInBill != 'Y'))
			{
				super.printCutPaper();
			}
			else
			{
				Printer.getDefault().cutPaper_Normal();
			}
		}
	}

	public void printGoods(int i)
	{
		// 设置打印区域
		setPrintArea("Detail");

		SaleGoodsDef sgd = (SaleGoodsDef) salegoods.elementAt(i);

		// 赠品商品不打印
		if (sgd.flag == '1')
			return;

		printVector(getCollectDataString(Detail, i, Width));

	}

}
