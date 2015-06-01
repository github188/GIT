package com.efuture.javaPos.PrintTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;

import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;

import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.CalcRulePopDef;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleAppendDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.ZqInfoRequestDef;

public class SaleBillMode extends PrintTemplate
{
	public Vector zq = null;
	public Vector gift = null;
	public Vector mystore = null;

	protected static SaleBillMode saleBillMode = null;

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
	protected final static int SBM_syjhdesc = 72;// 收银机描述（商铺名）
	protected final static int SBM_ylyhje = 73; // 银联接口优惠金额
	
	protected final static int SBM_salefphname = 74;//开发票抬头名称

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
	protected String salefpname="";

	public static Vector vbillmode = new Vector();
	public static HashMap printConfig = null; // 打印配置

	public Vector hidePayCode = null; // 需隐藏账号的代码
	public Vector printDifTitle = null; // 多联小票打印不同抬头

	public static SaleBillMode getDefault()
	{
		return getDefault("SalePrintMode.ini");
	}

	public static SaleBillMode getDefault(String fileName)
	{
		if (printConfig == null)
		{
			readPrintConfig();
		}

		if (SaleBillMode.saleBillMode == null)
		{
			SaleBillMode.saleBillMode = CustomLocalize.getDefault().createSaleBillMode();

			// 加载辅助模板
			String rows[] = PathFile.getAllDirName(GlobalVar.ConfigPath);
			for (int i = 0; i < rows.length; i++)
			{
				if (rows[i].indexOf("SalePrintMode") >= 0)
				{
					SaleBillMode billmode = CustomLocalize.getDefault().createSaleBillMode();
					if (rows[i].indexOf("SalePrintMode_") >= 0)
					{
						billmode.ReadTemplateFile(GlobalVar.ConfigPath + "//" + rows[i]);
						vbillmode.add(new Object[] { rows[i], billmode });
					}
				}
			}
		}

		for (int i = 0; vbillmode != null && i < vbillmode.size(); i++)
		{
			Object[] element = (Object[]) vbillmode.elementAt(i);
			if (element[0].toString().indexOf(fileName) >= 0)
				return (SaleBillMode) element[1];
		}

		return SaleBillMode.saleBillMode;
	}

	// 读取打印配置
	public static boolean readPrintConfig()
	{
		BufferedReader br = null;
		String line = null;

		try
		{
			if (!CommonMethod.isFileExist(GlobalVar.ConfigPath + "//PrintConfig.ini"))
				return false;
			br = CommonMethod.readFile(GlobalVar.ConfigPath + "//PrintConfig.ini");
			printConfig = new HashMap();
			while ((line = br.readLine()) != null)
			{
				if (line.trim().length() <= 0)
				{
					continue;
				}

				if (line.trim().charAt(0) == ';') // 判断是否为备注
				{
					continue;
				}

				if (line.indexOf("=") < 0)
				{
					continue;
				}

				if (line.split("=").length < 2)
				{
					continue;
				}

				// 先去掉空格
				line = line.replaceAll(" ", "");
				// 按照=和,分隔 格式 1=1,x.ini|2,y.ini
				String[] in = line.split("\\|");
				Vector vInfo = new Vector();
				for (int i = 0; i < in.length; i++)
				{
					String[] info = null;
					if (i == 0)
					{
						info = in[i].split("=")[1].split(",");
					}
					else
					{
						info = in[i].split(",");
					}
					vInfo.add(info);
				}
				printConfig.put(line.split("=")[0], vInfo);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean ReadTemplateFile()
	{
		String line = GlobalVar.ConfigPath + "//SalePrintMode.ini";
		return ReadTemplateFile(line);
	}

	public boolean ReadTemplateFile(String name)
	{
		super.InitTemplate();

		return super.ReadTemplateFile(Title, name);
	}

	public void setTemplateObject(SaleHeadDef h, Vector s, Vector p, boolean flag)
	{
		//占坑函数，方便子类重写
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

	public void setMystoreCoupon(Vector coupon)
	{
		this.mystore = coupon;
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

			case SBM_syjhdesc: // 收银机描述（商铺名）
				line = GlobalInfo.syjDef.syjdesc;

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

				if (new File(GlobalVar.ConfigPath + "//HidePaycode.ini").exists())
				{
					if (hidePayCode == null)
						readHidePayCode();
					line = hidePayNo(code, payno);
				}
				else
				{
					line = payno;
				}

				if ((line == null) || (line.length() <= 0))
				{
					line = null;
				}

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

			case SBM_sjfkfpje:
				line = ManipulatePrecision.doubleToString(this.calcPayFPMoney() * SellType.SELLSIGN(salehead.djlb));
				break;
			case SBM_sjfkfpjedx:
				line = ManipulatePrecision.getFloatConverChinese(this.calcPayFPMoney() * SellType.SELLSIGN(salehead.djlb));
				break;

			case SBM_salefphname:// 打印发票名称
				line =this.salefpname;
				break;
			case SBM_salefphm:// 打印收银员的发票编号
				line = Convert.increaseLong(this.salefph, item.length);
				// 添加发票编号记录日志
				AccessDayDB.getDefault().writeWorkLog(Language.apply("打印收银员的发票编号:(" + this.salefph + ")"), String.valueOf(StatusType.TASK_SENDWORKLOG));
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
			case SBM_ylyhje:
				if (((SalePayDef) salepay.elementAt(index)).num6 == 0)
				{
					line = null;
				}
				else
				{
					line = String.valueOf(((SalePayDef) salepay.elementAt(index)).num6 * SellType.SELLSIGN(salehead.djlb));
				}
				break;
			}
		}

		if ((line != null) && line.equals("&!"))
		{
			line = null;
		}

		// if (line != null && Integer.parseInt(item.code) != 0 && item.text !=
		// null && !item.text.trim().equals(""))
		if ((line != null) && (Integer.parseInt(item.code) != 0) && (text != null) && !text.trim().equals(""))
		{
			// line = item.text + line;
			int maxline = item.length - Convert.countLength(text);

			line = text + Convert.appendStringSize("", line, 0, maxline, maxline, item.alignment);
		}

		return line;
	}

	protected void printLine(String s)
	{
		if (printstrack != -1)
		{
			super.printLine(s);
		}
		else
		{
			if ((SellType.ISBACK(salehead.djlb) || SellType.ISHC(salehead.djlb)) && (GlobalInfo.sysPara.printInBill != 'Y'))
			{
				super.printLine(s);
			}
			else
			{
				Printer.getDefault().printLine_Normal(s);
			}
		}
	}

	protected void printArea(int startRow, int endRow)
	{
		if (printstrack != -1)
		{
			super.printArea(startRow, endRow);
		}
		else
		{
			if ((SellType.ISBACK(salehead.djlb) || SellType.ISHC(salehead.djlb)) && (GlobalInfo.sysPara.printInBill != 'Y'))
			{
				super.printArea(startRow, endRow);
			}
			else
			{
				Printer.getDefault().setPrintArea_Normal(startRow, endRow);
			}
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

	public void printSetPage()
	{
		if (printstrack != -1)
		{
			super.printSetPage();
		}
		else
		{
			if ((SellType.ISBACK(salehead.djlb) || SellType.ISHC(salehead.djlb)) && (GlobalInfo.sysPara.printInBill != 'Y'))
			{
				super.printSetPage();
			}
			else
			{
				// 设置是否分页打印
				if (PagePrint != 1)
				{
					Printer.getDefault().setPagePrint_Normal(false, 1);
				}
				else
				{
					Printer.getDefault().setPagePrint_Normal(true, Area_PageFeet);
				}
			}
		}
	}

	protected double calcPayFPMoney()
	{
		double je = salehead.sjfk - salehead.zl;

		String payex = "," + GlobalInfo.sysPara.fpjepayex + ",";
		for (int i = 0; i < salepay.size(); i++)
		{
			SalePayDef sp = (SalePayDef) salepay.elementAt(i);
			if (sp.flag == '1' && payex.indexOf("," + sp.paycode + ",") >= 0)
			{
				je -= sp.je;

				for (int j = 0; j < salepay.size(); j++)
				{
					SalePayDef sp1 = (SalePayDef) salepay.elementAt(j);
					if (sp1.flag == '2' && sp1.paycode.equals(sp.paycode))
					{
						je += sp1.je;
					}
				}
			}
		}
		return je;
	}

	public void saveSaleFphm(long startfph)
	{
		if (!Printer.getDefault().haveSaleFphmCfg())
			return;

		// 小票打印完时号码
		long curfph = Printer.getDefault().getCurrentSaleFphm();

		// 记录发票打印汇总,重打印不计算
		if (salehead.printnum <= 0)
		{
			double jyje = salehead.sjfk - salehead.zl;
			double kpje = calcPayFPMoney();
			Printer.getDefault().saveSaleFphmSummary(salehead.djlb, (int) (curfph - startfph), jyje * SellType.SELLSIGN(salehead.djlb), kpje * SellType.SELLSIGN(salehead.djlb));
		}
		else
		{
			Printer.getDefault().saveSaleFphmSummary(salehead.djlb, 0, 0, 0);
		}

		// 小票上记录发票号
		if (salehead.salefphm == null || salehead.salefphm.trim().length() <= 0)
		{
			salehead.salefphm = String.valueOf(startfph);
			if (curfph - startfph >= 2)
				salehead.salefphm += "," + String.valueOf(curfph - startfph) + "," + Printer.getDefault().getSaleFphmAttr(Printer.getDefault().InvoiceStart) + "," + Printer.getDefault().getSaleFphmAttr(Printer.getDefault().InvoiceCount);

			// 保存到本地小票
			AccessDayDB.getDefault().updateSaleBz(salehead.fphm, 2, salehead.salefphm);

			if (GlobalInfo.sysPara.isModifySaleFP == 'Y')
				sendSaleFpCode();

		}
	}

	public void sendSaleFpCode()
	{
		// 更改后台表中发票字段
		Vector saleappend = new Vector();

		SaleAppendDef sad = new SaleAppendDef();

		sad.syjh = salehead.syjh;
		sad.fphm = salehead.fphm;
		sad.rowno = 0;
		sad.str1 = "A"; // 过程里面区分附加信息
		sad.str2 = salehead.salefphm;

		saleappend.add(sad);

		// 如果发送成功,不将信息记录到附加表中
		if (saleappend.size() > 0 && !NetService.getDefault().sendSaleAppend(saleappend))
		{
			AccessDayDB.getDefault().writeSaleAppend(GlobalInfo.dayDB, saleappend);
		}

		saleappend.clear();
		saleappend = null;
	}

	public void printBill()
	{
		int choice = GlobalVar.Key1;

		// 开始打印前的发票号
		salefph = Printer.getDefault().getCurrentSaleFphm();
		
		//开始打印前的抬头
		salefpname = Printer.getDefault().getCurrentSaleFpName();

		// 重打印小票时，如果是非超市且参数定义既打印机制单又打营业员联，才提示选择打印部分
		if (('N' != (GlobalInfo.syjDef.issryyy)) && (salehead.printnum > 0) && GlobalInfo.sysPara.fdprintyyy == 'Y')
		{
			StringBuffer info = new StringBuffer();
			info.append(Convert.appendStringSize("", Language.apply("请按键选择重打印内容"), 1, 30, 30, 2) + "\n");
			info.append(Convert.appendStringSize("", Language.apply("1、打印全部小票单据"), 1, 30, 30, 2) + "\n");
			info.append(Convert.appendStringSize("", Language.apply("2、只打印机制小票单"), 1, 30, 30, 2) + "\n");
			info.append(Convert.appendStringSize("", Language.apply("3、只打印营业员列印"), 1, 30, 30, 2) + "\n");
			info.append(Convert.appendStringSize("", Language.apply("按其他键则放弃重打印"), 1, 30, 30, 2) + "\n");

			choice = new MessageBox(info.toString(), null, false).verify();
		}

		int num = 1;
		boolean sequenceflag = true;
		if (GlobalInfo.sysPara.printyyhsequence == 'B')
			sequenceflag = false;

		while (num <= 2)
		{
			if (sequenceflag)
			{
				if ((choice == GlobalVar.Key1) || (choice == GlobalVar.Key3))
				{
					if (((YyySaleBillMode) YyySaleBillMode.getDefault()).isLoad())
					{
						printYyyBillPrintMode();
					}
					else
					{
						// 打印营业员分单联
						printYYYBill();
					}
				}

				sequenceflag = false;
			}
			else
			{

				if ((choice == GlobalVar.Key1) || (choice == GlobalVar.Key2))
				{
					// 根据参数控制打印销售小票的份数
					printnum = 0;
					// 先查看是否存在配置
					if (printConfig != null && printConfig.size() > 0)
					{
						// 查看本笔交易类型是否存在打印配置
						if (printConfig.containsKey(salehead.djlb))
						{
							Vector v = (Vector) printConfig.get(salehead.djlb);
							int num1 = v.size();

							for (int i = 0; i < num1; i++)
							{
								// 输出栈
								String strack = ((String[]) v.get(i))[0];
								// 对应的打印模板
								String template = ((String[]) v.get(i))[1];
								// 检查是否打印
								if (!checkIsPrint(template))
									continue;

								// 按照对应的打印模板生成打印模板对象
								SaleBillMode sbm = getDefault(template);
								// 设置交易数据
								sbm.setTemplateObject(salehead, salegoods, salepay);
								// 设置对应的输出栈
								sbm.printstrack = Integer.parseInt(strack);
								sbm.salefph = this.salefph;
								// 打印
								//
								if (!checkPrintNum(template))
								{
									sbm.printSellBill();
								}
								else
								{
									for (int salebillnum = 0; salebillnum < GlobalInfo.sysPara.salebillnum; salebillnum++)
									{
										// 打印交易小票联
										sbm.printSellBill();
										printnum++;
									}
								}
							}
						}
						else
						{
							for (int salebillnum = 0; salebillnum < GlobalInfo.sysPara.salebillnum; salebillnum++)
							{
								// 打印交易小票联
								printSellBill();
								printnum++;
							}
						}
					}
					// 没有打印配置的情况按默认程序打印
					else
					{
						for (int salebillnum = 0; salebillnum < GlobalInfo.sysPara.salebillnum; salebillnum++)
						{

							// 打印交易小票联
							printSellBill();
							printnum++;
						}
					}

					// 打印附加的各个小票联
					printAppendBill();
				}

				sequenceflag = true;
			}

			num = num + 1;
		}

		// 记录本笔小票用的发票张数
		saveSaleFphm(salefph);
	}

	public boolean checkPrintNum(String template)
	{
		return false;
	}

	public boolean checkIsPrint(String template)
	{
		return true;
	}

	public void printYYYBill()
	{
		int i = 0;
		int j = 0;
		Vector set = null;
		CalcRulePopDef calPop = null;
		SaleGoodsDef sgd = null;
		String line;

		// 如果发现在JAVAPOS根目录下存在noyyybill.ini文件,就不进行列印
		if (new File("noyyybill.ini").exists())
			return;

		// GlobalInfo.sysPara.fdprintyyy =
		// (N-不打营业员联但打印小票联/Y-打印营业员联也打印小票/A-打印营业员联但不打印小票)
		// 超市小票 或者 系统参数定义不打印分单,则不打印营业员小票
		if ((GlobalInfo.syjDef.issryyy == 'N') || (GlobalInfo.syjDef.issryyy == 'A' && ((SaleGoodsDef) salegoods.elementAt(0)).yyyh.equals(Language.apply("超市"))) || (GlobalInfo.sysPara.fdprintyyy == 'N')) { return; }

		// 先把商品进行分组
		set = new Vector();

		for (i = 0; i < salegoods.size(); i++)
		{
			sgd = (SaleGoodsDef) salegoods.elementAt(i);

			// 查找是否相同商品,按营业员柜组分组
			for (j = 0; j < set.size(); j++)
			{
				calPop = (CalcRulePopDef) set.elementAt(j);

				if (calPop.code.equals(sgd.yyyh) && calPop.gz.equals(sgd.gz))
				{
					calPop.row_set.add(String.valueOf(i));

					break;
				}
			}

			if (j >= set.size())
			{
				calPop = new CalcRulePopDef();
				calPop.code = sgd.yyyh;
				calPop.gz = sgd.gz;
				calPop.row_set = new Vector();
				calPop.row_set.add(String.valueOf(i));
				set.add(calPop);
			}
		}

		// 按分组进行分单打印
		for (i = 0; i < set.size(); i++)
		{
			calPop = (CalcRulePopDef) set.elementAt(i);

			// if (new MessageBox("请将营业员(" + calPop.code +
			// ")的销售单放入打印机\n\n按‘回车’键后开始打印\n按‘退出’键则跳过打印").verify() ==
			// GlobalVar.Exit)
			if (new MessageBox(Language.apply("请将营业员({0})的销售单放入打印机\n\n按‘回车’键后开始打印\n按‘退出’键则跳过打印", new Object[] { calPop.code })).verify() == GlobalVar.Exit)
			{
				continue;
			}

			Printer.getDefault().startPrint_Slip();

			Printer.getDefault().printLine_Slip(Language.apply("时间:") + salehead.rqsj + " NO." + salehead.syjh + "-" + salehead.fphm);
			Printer.getDefault().printLine_Slip(Language.apply("收银员:") + salehead.syyh + "          " + SellType.getDefault().typeExchange(salehead.djlb, salehead.hhflag, salehead));

			if (salehead.printnum > 0)
			{
				Printer.getDefault().printLine_Slip(Language.apply("---------------重打印-----------------"));
			}
			else
			{
				Printer.getDefault().printLine_Slip("--------------------------------------");
			}

			double hjje = 0;
			double hjzk = 0;

			for (j = 0; j < calPop.row_set.size(); j++)
			{
				sgd = (SaleGoodsDef) salegoods.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));

				line = Convert.appendStringSize("", sgd.barcode, 0, 10, Width);
				line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(sgd.sl, 4, 1, true), 12, 4, Width, 1);
				line = Convert.appendStringSize(line, " x ", 16, 3, Width);
				line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(sgd.jg), 19, 8, Width);
				line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(sgd.hjje), 28, 9, Width, 1);

				Printer.getDefault().printLine_Slip(line);
				Printer.getDefault().printLine_Slip(sgd.name);

				hjje += sgd.hjje;
				hjzk += sgd.hjzk;
			}

			Printer.getDefault().printLine_Slip("--------------------------------------");
			Printer.getDefault().printLine_Slip(Language.apply("营业员:") + Convert.appendStringSize("", calPop.code, 0, 10, 10) + "        " + Language.apply("柜组:") + Convert.appendStringSize("", calPop.gz, 0, 10, 10));
			Printer.getDefault().printLine_Slip(Language.apply("总小计:") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(hjje), 0, 10, 10) + "        " + Language.apply("折扣:") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(hjzk), 0, 10, 10));

			Printer.getDefault().cutPaper_Slip();
		}
	}

	public void setSaleTicketMSInfo(SaleHeadDef sh, Vector gifts)
	{
		// 记录小票赠送清单
		this.salemsinvo = sh.fphm;
		this.salemsgift = gifts;

		// 分解赠品清单
		Vector fj = new Vector();
		for (int i = 0; gifts != null && i < gifts.size(); i++)
		{
			GiftGoodsDef g = (GiftGoodsDef) gifts.elementAt(i);

			if (g.type.trim().equals("0"))
			{
				// 无促销
				break;
			}
			else if (g.type.trim().equals("1"))
			{
				fj.add(g);
			}
			else if (g.type.trim().equals("2"))
			{
				fj.add(g);
			}
			else if (g.type.trim().equals("3"))
			{
				fj.add(g);
			}
		}

		// 提示
		StringBuffer buff = new StringBuffer();
		double je = 0;
		for (int i = 0; i < fj.size(); i++)
		{
			GiftGoodsDef g = (GiftGoodsDef) fj.elementAt(i);
			buff.append(g.code + "   " + g.info + "      " + Convert.increaseChar(ManipulatePrecision.doubleToString(g.je), 14) + "\n");
			je += g.je;
		}
		buff.append(Language.apply("返券总金额为: ") + Convert.increaseChar(ManipulatePrecision.doubleToString(je), 14));
		if (je > 0)
		{
			new MessageBox(buff.toString());
		}

		if (salehead.printnum <= 0 && fj.size() > 0 && je > 0)
		{
			String zqinfo = "";
			for (int i = 0; i < fj.size(); i++)
			{
				GiftGoodsDef g = (GiftGoodsDef) fj.elementAt(i);
				if (g.type.equals("3"))
				{
					if (zqinfo.trim().length() > 0)
					{
						zqinfo = zqinfo + "|";
					}

					zqinfo = zqinfo + g.code.trim() + "," + g.info + "," + g.je;
				}
			}

			if (zqinfo.trim().length() > 0)
			{
				while (true)
				{
					StringBuffer cardno = new StringBuffer();
					TextBox txt = new TextBox();
					if (txt.open(Language.apply("请刷需要充值的返券卡"), Language.apply("卡号"), Language.apply("请将返券卡从刷卡槽刷入\n") + zqinfo, cardno, 0, 0, false, TextBox.MsrKeyInput))
					{
						String tr = txt.Track2;

						if (tr.trim().length() > 0)
						{
							DataService dataservice = (DataService) DataService.getDefault();
							ZqInfoRequestDef request = new ZqInfoRequestDef();
							request.cardno = tr.trim();
							request.mktcode = GlobalInfo.sysPara.mktcode;
							request.fphm = salehead.fphm;
							request.syjh = salehead.syjh;
							request.zqinfo = zqinfo;
							request.memo = "";
							request.str1 = "";
							request.str2 = "";
							request.str3 = "";

							if (dataservice.saveZqInfo(request))
							{
								for (int i = 0; i < salemsgift.size(); i++)
								{
									GiftGoodsDef g = (GiftGoodsDef) salemsgift.elementAt(i);
									if (g.type.equals("3"))
									{
										// 将磁道号附值，用于打印
										g.memo = tr.trim();
									}
								}
								break;
							}

							int ret = new MessageBox(Language.apply("返券操作失败!\n 任意键-重试 / 2-放弃 "), null, false).verify();
							if (ret == GlobalVar.Key2)
							{
								// 放弃将返券信息删除
								for (int i = 0; i < salemsgift.size(); i++)
								{
									GiftGoodsDef g = (GiftGoodsDef) salemsgift.elementAt(i);
									if (g.type.equals("3"))
									{
										salemsgift.remove(i);
										i--;
									}
								}
								break;
							}
						}
					}
					else
					{
						int ret = new MessageBox(Language.apply("是否取消送券?\n 1-否 / 2-是 "), null, false).verify();
						if (ret == GlobalVar.Key2)
						{
							// 放弃将返券信息删除
							for (int i = 0; i < salemsgift.size(); i++)
							{
								GiftGoodsDef g = (GiftGoodsDef) salemsgift.elementAt(i);
								if (g.type.equals("3"))
								{
									salemsgift.remove(i);
									i--;
								}
							}
							break;
						}
					}
				}
			}
		}
	}

	public boolean needMSInfoPrintGrant()
	{
		return false;
	}

	// 打印小票赠送清单
	public void printSaleTicketMSInfo()
	{
		printMystore();
	}

	public void printMystore()
	{
		if (mystore == null)
			return;

		try
		{
			for (int i = 0; i < mystore.size(); i++)
			{
				String[] coupon = (String[]) mystore.get(i);

				printLine("     " + Language.apply("微店电子券") + "\n");
				printLine("===  ===  ===  ===  ===" + "\n");
				printLine(Language.apply("活动名称:") + coupon[0] + "\n");
				printLine(Language.apply("会员卡号:") + coupon[1] + "\n");
				printLine(Language.apply("券    号:") + coupon[2] + "\n");
				printLine(Language.apply("起始日期:") + coupon[3] + "\n");
				printLine(Language.apply("截止日期:") + coupon[4] + "\n");
				printLine(Language.apply("活动内容:") + coupon[5] + "\n\n");
				printLine(Language.apply("打印日期:") + ManipulateDateTime.getCurrentDate() + "  " + ManipulateDateTime.getCurrentTime() + "\n");
				printLine("\n\n");

				printCutPaper();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			mystore = null;
		}
	}

	public void printBankBill()
	{
		// 在原始付款清单中,查找是否有银联卡付款方式
		for (int i = 0; i < originalsalepay.size(); i++)
		{
			SalePayDef pay = (SalePayDef) originalsalepay.elementAt(i);
			PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);

			if ((mode.isbank == 'Y') && (pay.batch != null) && (pay.batch.length() > 0))
			{
				PaymentBankFunc bank = CreatePayment.getDefault().getPaymentBankFunc(mode.code);
				bank.printXYKDoc(pay.batch);
			}
		}
	}

	protected void printAppendBill()
	{

		// 检查是否有未打印的银联签购单
		if (PaymentBank.haveXYKDoc)
		{
			printBankBill();
		}

		// 检查是否有
		if (CardSaleBillMode.getDefault().isLoad())
		{
			printMZKBillPrintMode();
		}
		else
		{
			// 打印面值卡联
			printMZKBill(1);

			// 打印返券卡联
			printMZKBill(2);
		}
		// 打印赠券联
		printSaleTicketMSInfo();
	}

	// 打印面值卡联
	public void printMZKBill(int type)
	{
		int i = 0;

		if (GlobalInfo.sysPara.mzkbillnum <= 0) { return; }

		try
		{
			// 先检查是否有需要打印的付款方式
			for (i = 0; i < originalsalepay.size(); i++)
			{
				SalePayDef pay = (SalePayDef) originalsalepay.elementAt(i);
				PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);

				if (mode == null)
				{
					continue;
				}

				if ((type == 1) && (mode.type == '4'))
				{
					break;
				}

				if ((type == 2) && CreatePayment.getDefault().isPaymentFjk(mode.code))
				{
					break;
				}
			}

			if (i >= originalsalepay.size()) { return; }

			for (int n = 0; n < GlobalInfo.sysPara.mzkbillnum; n++)
			{
				// 开始新打印
				printStart();

				if (type == 1)
				{
					printLine("\n             " + Language.apply("电子卡联"));
				}

				if (type == 2)
				{
					printLine("\n             " + Language.apply("返券卡联"));
				}

				printLine(Language.apply("交易类型:") + String.valueOf(SellType.getDefault().typeExchange(salehead.djlb, salehead.hhflag, salehead)));

				printLine(Language.apply("交易时间:") + salehead.rqsj);
				printLine(Language.apply("收银机号:") + salehead.syjh + "     " + Language.apply("交易号:") + Convert.increaseLong(salehead.fphm, 8));
				printLine(Language.apply("收银员号:") + salehead.syyh + "     " + Language.apply("门店号:") + GlobalInfo.sysPara.mktcode);
				printLine("\n");
				printLine(Language.apply("卡号") + "                        " + Language.apply("消费金额"));

				int num = 0;
				double hj = 0;
				String line = null;

				for (i = 0; i < originalsalepay.size(); i++)
				{
					SalePayDef pay = (SalePayDef) originalsalepay.elementAt(i);
					PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);

					if (((type == 1) && (mode.type == '4')) || ((type == 2) && (CreatePayment.getDefault().isPaymentFjk(mode.code))))
					{
						num++;
						line = Convert.appendStringSize("", pay.payno, 1, 20, 40, 0);
						line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(pay.ybje * SellType.SELLSIGN(salehead.djlb)), 25, 7, 40, 0);

						printLine(line);

						if (pay.hl == 0)
						{
							pay.hl = 1;
						}

						hj += (pay.ybje * pay.hl);
					}
				}

				if (type == 1)
				{
					// printLine("本次共 " + num + " 张电子卡消费");
					printLine(Language.apply("本次共 {0} 张电子卡消费", new Object[] { num + "" }));
				}

				if (type == 2)
				{
					// printLine("本次共 " + num + " 张返券卡消费");
					printLine(Language.apply("本次共 {0} 张返券卡消费", new Object[] { num + "" }));
				}

				printLine(Language.apply("合计消费金额") + "     " + ManipulatePrecision.doubleToString(hj * SellType.SELLSIGN(salehead.djlb)));
				printCutPaper();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	// 通过模版打印营业员联
	public void printYyyBillPrintMode()
	{
		// 如果发现在JAVAPOS根目录下存在noyyybill.ini文件,就不进行列印
		if (new File("noyyybill.ini").exists())
			return;
		// 根据模板打印营业员联
		YyySaleBillMode.getDefault().setTemplateObject(salehead, originalsalegoods, originalsalepay);
		YyySaleBillMode.getDefault().printBill();
	}

	// 通过模版来打印卡联
	public void printMZKBillPrintMode()
	{
		boolean bool = false;

		if (GlobalInfo.sysPara.mzkbillnum <= 0) { return; }

		if ((GlobalInfo.sysPara.printpaymode == null) || GlobalInfo.sysPara.printpaymode.equals("")) { return; }

		CardSaleBillMode.getDefault().setTemplateObject(salehead, salegoods, originalsalepay);

		String[] printpaymode = GlobalInfo.sysPara.printpaymode.split("\\|");

		for (int i = 0; i < printpaymode.length; i++)
		{
			CardSaleBillMode.getDefault().setPayCodes(printpaymode[i]);

			for (int j = 0; j < originalsalepay.size(); j++)
			{
				SalePayDef pay = (SalePayDef) originalsalepay.elementAt(j);
				PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);

				if (mode == null)
					continue;

				if (!CardSaleBillMode.getDefault().isExistPaycode(mode.code))
					continue;

				bool = true;

				CardSaleBillMode.getDefault().setPayName(mode.name);

				break;
			}

			for (int n = 0; (n < GlobalInfo.sysPara.mzkbillnum) && bool; n++)
			{
				CardSaleBillMode.getDefault().printBill();
			}

			bool = false;
		}
	}

	protected void printSellBill()
	{
		// GlobalInfo.sysPara.fdprintyyy =
		// (N-不打营业员联但打印小票联/Y-打印营业员联也打印小票/A-打印营业员联但不打印小票)
		// 非超市小票且系统参数定义只打印营业员分单，则不打印机制小票
		if (!((GlobalInfo.syjDef.issryyy == 'N') || (GlobalInfo.syjDef.issryyy == 'A' && ((SaleGoodsDef) salegoods.elementAt(0)).yyyh.equals(Language.apply("超市")))) && (GlobalInfo.sysPara.fdprintyyy == 'A')) { return; }

		if (!SellType.ISEXERCISE(salehead.djlb) && printnum < 1 && salehead.printnum < 1 && !getFaxInfo())
			new MessageBox(Language.apply("获取税控信息失败！"));

		// 设置打印方式
		printSetPage();

		// 多联小票打印不同抬头
		printDifTitle();

		// 打印头部区域
		printHeader();

		// 打印明细区域
		printDetail();

		// 打印汇总区域
		printTotal();

		// 打印付款区域
		printPay();

		// 打印尾部区域
		printBottom();

		// 切纸
		printCutPaper();
	}

	public void printDifTitle()
	{
		// 不存在PrintDifTitle.ini模板则不打印
		if (!new File(GlobalVar.ConfigPath + "//PrintDifTitle.ini").exists())
			return;
		// 每次启动javapos 只读取一次PrintDifTitle.ini
		if (printDifTitle == null)
			readprintDifTitle();

		for (int i = 0; i < printDifTitle.size(); i++)
		{
			String temp[] = (String[]) printDifTitle.get(i);
			if (Integer.parseInt(temp[0]) - 1 == printnum)
			{
				printLine(Convert.rightTrim(temp[1]) + "\n");
			}
		}
	}

	//
	private void readprintDifTitle()
	{
		BufferedReader br = null;
		String line = null;
		br = CommonMethod.readFile(GlobalVar.ConfigPath + "//PrintDifTitle.ini");
		String[] value = null;
		if (printDifTitle == null)
			printDifTitle = new Vector();
		try
		{
			while ((line = br.readLine()) != null)
			{
				String temp = new String(line);
				if (temp.indexOf(";") == 0)
					continue;

				value = temp.split("\\|");

				if (value[0].trim().equals(""))
					continue;
				printDifTitle.add(value);
			}

			br.close();
		}
		catch (IOException e)
		{

			e.printStackTrace();
		}
	}

	// 获取税控信息
	public boolean getFaxInfo()
	{
		return true;
	}

	public void printPageHead()
	{
		// 是否启用打印页头
		if (PageHeadPrint != 1)
			return;

		// 分页且套打时，不打印
		if (PagePrint == 1 && AreaPrint == 1)
			return;

		// 设置打印区域
		setPrintArea("Memo");

		// 打印
		printVector(getCollectDataString(Memo, -1, Width));
	}

	public void printDetail()
	{
		// 设置打印区域
		setPrintArea("Detail");

		// 循环打印商品明细
		for (int i = 0; i < salegoods.size(); i++)
		{
			SaleGoodsDef sgd = (SaleGoodsDef) salegoods.elementAt(i);

			// 赠品商品不打印
			if (sgd.flag == '1')
			{
				continue;
			}

			printVector(getCollectDataString(Detail, i, Width));
		}
	}

	public void printPay()
	{
		// 设置打印区域
		setPrintArea("Pay");

		// 循环打印付款明细
		for (int i = 0; i < salepay.size(); i++)
		{
			SalePayDef spd = (SalePayDef) salepay.elementAt(i);

			// 找零付款不打印
			if (spd.flag == '2')
			{
				continue;
			}

			printVector(getCollectDataString(Pay, i, Width));
		}
	}

	// 以下方法只在即扫即打方式使用
	public void printRealTimeHead()
	{
		// 开始打印前的发票号
		salefph = Printer.getDefault().getCurrentSaleFphm();
		
//		 开始打印前的发票名称
		salefpname = Printer.getDefault().getCurrentSaleFpName();

		// 设置打印方式
		printSetPage();

		// 打印头部区域
		printHeader();
	}

	public void printRealTimeDetail(int index)
	{
		// 设置打印区域
		setPrintArea("Detail");

		// 打印商品明细
		printVector(getCollectDataString(Detail, index, Width));
	}

	public void printRealTimeBottom()
	{
		// 打印汇总区域
		printTotal();

		// 打印付款区域
		printPay();

		// 打印尾部区域
		printBottom();

		// 切纸
		printCutPaper();

		// 打印附加的各个小票联
		printAppendBill();

		// 记录本笔小票用的发票张数
		saveSaleFphm(salefph);
	}

	public void printRealTimeDetail(SaleGoodsDef temp)
	{
		salegoods.add(0, temp);

		printRealTimeDetail(0);

		salegoods.remove(0);
	}

	public void printRealTimeCancel()
	{
		if ((SellType.ISBACK(salehead.djlb) || SellType.ISHC(salehead.djlb)) && (GlobalInfo.sysPara.printInBill != 'Y'))
		{
			super.printLine("--------------------------");
			super.printLine(Language.apply(" 以上小票明细作废，重新打印 "));
			super.printLine("--------------------------");
			super.printCutPaper();
		}
		else
		{
			Printer.getDefault().printLine_Normal("--------------------------");
			Printer.getDefault().printLine_Normal(Language.apply(" 以上小票明细作废，重新打印 "));
			Printer.getDefault().printLine_Normal("--------------------------");
			Printer.getDefault().cutPaper_Normal();
		}
	}

	// 隐藏付款账号
	public String hidePayNo(String code, String hp)
	{
		int len = hp.trim().length();
		String[] value = null;
		int begin = 0; // 显示前几位号
		int end = 0; // 显示后几位号
		String s1 = null;
		String s2 = null;

		for (int i = 0; i < hidePayCode.size(); i++)
		{
			String temp = (String) hidePayCode.get(i);

			value = temp.trim().split(",");

			if (value == null || value[0] == null || value.length < 3)
				continue;

			if (code.trim().equals(value[0].trim()))
			{
				begin = Integer.parseInt(value[1].trim());

				end = Integer.parseInt(value[2].trim());

				if (begin == 0 && end == 0)
					return hp;
				if (begin + end >= len)
					return hp;

				s1 = hp.trim().substring(0, begin);
				s2 = hp.trim().substring(len - end, len);

				for (int j = len - (begin + end); j > 0; j--)
				{
					s1 = s1 + "*";
				}
				hp = s1 + s2;
			}
		}
		return hp;
	}

	public void readHidePayCode()
	{
		BufferedReader br = null;
		String line = null;
		br = CommonMethod.readFile(GlobalVar.ConfigPath + "//HidePaycode.ini");
		String[] value = null;
		if (hidePayCode == null)
			hidePayCode = new Vector();
		try
		{

			while ((line = br.readLine()) != null)
			{
				String temp = new String(line);
				value = temp.trim().split(";");
				if (value[0].trim().equals(""))
					continue;
				hidePayCode.add(value[0].trim());
			}

			br.close();
		}
		catch (IOException e)
		{

			e.printStackTrace();
		}

	}
}
