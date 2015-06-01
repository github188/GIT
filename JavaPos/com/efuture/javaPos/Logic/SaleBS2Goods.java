package com.efuture.javaPos.Logic;

import java.util.Vector;

import bankpay.Payment.Dzcm_CheckVerify;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentBankCMCC;
import com.efuture.javaPos.PrintTemplate.CheckGoodsMode;
import com.efuture.javaPos.Struct.CheckGoodsDef;
import com.efuture.javaPos.Struct.DzcModeDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsUnitsDef;
import com.efuture.javaPos.Struct.MemoInfoDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

// 销售功能业务类
public class SaleBS2Goods extends SaleBS1Calc
{
	public SaleBS2Goods()
	{
		super();
	}

	public OperUserDef findYYYH(String id)
	{
		OperUserDef staff = new OperUserDef();

		if (!DataService.getDefault().getOperUser(staff, id))
		{
			return null;
		}
		else
		{
			return staff;
		}
	}

	public boolean allowStartFindGoods()
	{
		// 检查ispresale参数，是否允许预销售，只允许预销售退货
		if (GlobalInfo.sysPara.isPreSale == 'N' && SellType.PREPARE_SALE.equals(saletype))
		{
			new MessageBox(Language.apply("当前只允许预销售退货，不允许预销售\n开放此功能请检查参数PZ"));
			return false;
		}
		// 会员卡必须在商品输完后刷,那么刷卡以后不能输入商品
		if (GlobalInfo.sysPara.custommust == 'Y' && !checkMemberSale())
		{
			new MessageBox(Language.apply("未刷VIP卡,不能增加商品\n\n请刷VIP卡后再输入"));
			return false;
		}

		// 会员卡必须在商品输完后刷,那么刷卡以后不能输入商品
		if (GlobalInfo.sysPara.customvsgoods == 'B' && checkMemberSale())
		{
			new MessageBox(Language.apply("已刷VIP卡,不能再修改商品\n\n请付款或取消VIP卡后再输入"));
			return false;
		}

		// 指定小票退货必须联网进行
		if (isSpecifyBack() && !GlobalInfo.isOnline)
		{
			new MessageBox(Language.apply("指定小票退货必须在联网状态下进行!"));

			return false;
		}

		if (this.saletype.equals(SellType.PREPARE_BACK) && isbackticket)
		{
			new MessageBox(Language.apply("预售退货状态下不允许修改商品状态"));
			return false;
		}

		// 检查现金存量
		if ((GlobalInfo.sysPara.maxxj > 0) && (GlobalInfo.sysPara.cashsale != 'Y') && (GlobalInfo.syjStatus.xjje > GlobalInfo.sysPara.maxxj))
		{
			new MessageBox(Language.apply("目前现金存量已达到最高限额\n请立即进行缴款"), null, false);

			return false;
		}

		// 检查商品个数
		if (getMaxSaleGoodsCount() > 0 && saleGoods.size() >= getMaxSaleGoodsCount())
		{
			new MessageBox(Language.apply("目前输入的商品个数已达到上限\n请按付款键付款"), null, false);

			return false;
		}

		// 检查发票是否打印完,打印完未设置新发票号则不能交易
		if (Printer.getDefault().getSaleFphmComplate()) { return false; }

		// 设置交易时间以第一个输入的商品时间为准
		if (saleGoods.size() <= 0)
		{
			saleHead.rqsj = ManipulateDateTime.getCurrentDateTime();
		}

		return true;
	}

	public String convertDzcmScsj(String dzcmscsj, boolean isdzcm)
	{
		ManipulateDateTime mdt = null;
		String scsj;

		// 处理生鲜商品生产日期
		if (dzcmscsj != null && dzcmscsj.trim().length() > 0)
		{
			mdt = new ManipulateDateTime();
			String date = mdt.getDateBySlash();
			date = date.substring(0, 8) + dzcmscsj.substring(0, 2);
			String time = dzcmscsj.substring(2, 4) + ":" + dzcmscsj.substring(4, 6) + ":00";

			scsj = date + " " + time;
		}
		else
		{
			scsj = "";
		}

		return scsj;
	}

	public String convertDzcmBarcode(GoodsDef goods, String barcode, boolean isdzcm)
	{
		if (GlobalInfo.sysPara.dzcbarcodestyle != 'Y')
			return goods.barcode;

		return barcode;
	}

	public GoodsDef findGoodsInfo(String code, String yyyh, String gz, String dzcmscsj, boolean isdzcm, StringBuffer slbuf)
	{
		return findGoodsInfo(code, yyyh, gz, dzcmscsj, isdzcm, slbuf, false);
	}

	// iszdxp：是否为指定柜组查询,需要确认GlobalInfo.sysPara.yyygz != 'Y'
	public GoodsDef findGoodsInfo(String code, String yyyh, String gz, String dzcmscsj, boolean isdzcm, StringBuffer slbuf, boolean iszdxp)
	{
		GoodsDef goodsDef = new GoodsDef();
		int searchFlag = 0;

		String yhsj = null;
		String scsj;

		// 设置查找商品的查找标志,1-超市销售/2-柜台销售检查营业员串柜/3-柜台销售不检查营业员串柜/4赠品
		if (GlobalInfo.syjDef.issryyy == 'N' || yyyh.equals(Language.apply("超市")))
		{
			searchFlag = 1; // 超市
		}
		else if ((GlobalInfo.sysPara.yyygz != 'N' && GlobalInfo.syjDef.issryyy != 'B' && gz != null && gz.length() > 0 && !gz.equals("多个柜")) || iszdxp)
		{
			searchFlag = 2; // 控制串柜
		}
		else
		{
			searchFlag = 3; // 不控制串柜
		}

		// 退货时不查找优惠,优惠时间以交易时间为准
		if (SellType.ISBACK(saletype))
		{
			yhsj = "";
		}
		else
		{
			yhsj = saleHead.rqsj;
		}

		// 生鲜商品生产时间
		scsj = convertDzcmScsj(dzcmscsj, isdzcm);

		// 盘点输入不控制串柜输入
		if (SellType.ISCHECKINPUT(saletype))
		{
			searchFlag = 3;
		}

		// 看板销售传入标记9,如何选择了家电发货地点则
		if (jdfhddcode != null && jdfhddcode.length() > 0)
		{
			searchFlag = 9;
			scsj = saleHead.jdfhdd; // scsj标记发货地点
		}

		// 开始查找商品
		int result = DataService.getDefault().getGoodsDef(goodsDef, searchFlag, code, gz, scsj, yhsj, saletype);
		switch (result)
		{
			case 0:
				break;
			case 4:// 商品存在多柜组

				if (inputGoodsGZ(goodsDef, searchFlag, code, scsj, yhsj, saletype) != 0)
					return null;

				/*
				 * //wangyong update by 2013.9.18
				 * 单独写到一个函数,是为了分支能界面自选柜组信息(替代现在的手输柜组号) //old bak start
				 * StringBuffer gzstr = new StringBuffer(); boolean done = true;
				 * done = new TextBox().open("请输入[" + code.trim() + "]商品的柜组",
				 * "柜组号", "该商品有多个柜组，请输入柜组号以便销售", gzstr, 0, 0, false); if (!done)
				 * { return null; } else { searchFlag = 2; int ret =
				 * DataService.getDefault().getGoodsDef(goodsDef, searchFlag,
				 * code, gzstr.toString(), scsj, yhsj, saletype); if (ret == 4)
				 * { new MessageBox("在指定柜组内未找到该商品\n请重新确定柜组是否正确"); return null; }
				 * else if (ret != 0) { return null; } } //old bak end
				 */
				break;

			default:
				return null;
		}

		// 检查营业员串柜情况
		if (GlobalInfo.sysPara.yyygz != 'N' && GlobalInfo.syjDef.issryyy != 'B' && curyyygz.length() > 0)
		{
			String[] s = curyyygz.split(",");
			if (s.length > 1)
			{
				int i;
				for (i = 0; i < s.length; i++)
				{
					if (goodsDef.gz.equalsIgnoreCase(s[i]))
						break;
				}
				if (i >= s.length)
				{
					new MessageBox(Language.apply("该商品不是营业柜组范围内的商品\n\n营业员的营业柜组范围是\n") + curyyygz);
					return null;
				}
			}
		}

		// 使用代码销售时检查多单位商品
		if (code.equals(goodsDef.code) && goodsDef.isuid == 'Y') { return getMutiUnitChoice(goodsDef); }

		// 母商品选择子商品进行销售
		if (goodsDef.type == '6') { return getSubGoodsDef(goodsDef); }

		// 判断是否VIP折扣标志设置该单品是否享受VIP折扣
		if (GlobalInfo.sysPara.isHandVIPDiscount == 'A' && !isVIPZK)
		{
			goodsDef.name = "[" + goodsDef.name + "]";
			goodsDef.isvipzk = 'N';
		}

		return goodsDef;
	}

	/**
	 * 输入商品柜组
	 * 
	 * @param goodsDef
	 * @param searchFlag
	 * @param code
	 * @param scsj
	 * @param yhsj
	 * @param djlb
	 * @return 0成功,其它失败
	 */
	public int inputGoodsGZ(GoodsDef goodsDef, int searchFlag, String code, String scsj, String yhsj, String djlb)
	{
		try
		{
			StringBuffer gzstr = new StringBuffer();
			boolean done = true;
			done = new TextBox().open(Language.apply("请输入[{0}]商品的柜组", new Object[] { code.trim() }), Language.apply("柜组号"), Language.apply("该商品有多个柜组，请输入柜组号以便销售"), gzstr, 0, 0, false);
			if (!done)
			{
				return -2;// null;
			}
			else
			{
				searchFlag = 2;
				int ret = DataService.getDefault().getGoodsDef(goodsDef, searchFlag, code, gzstr.toString(), scsj, yhsj, saletype);
				if (ret == 4)
				{
					new MessageBox(Language.apply("在指定柜组内未找到该商品\n请重新确定柜组是否正确"));
					return -4;// null;
				}
				// else if (ret != 0) { return null; }
				return ret;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return 999;
		}
	}

	public String[] convertQuantityBarcode(String code)
	{
		String barcode;
		double quantity = 1;

		if (GlobalInfo.sysPara.isnumandcode == 'Y')
		{
			if ((code.indexOf("*") <= 0) || (code.indexOf("*") >= (code.length() - 1)))
			{
				barcode = code;
				quantity = 1;
			}
			else
			{
				// 解析出数量
				String[] codes = new String[2];
				codes[0] = code.substring(0, code.indexOf("*"));
				codes[1] = code.substring(code.indexOf("*") + 1);

				// 检查数量是否合法
				try
				{
					if (codes[0].indexOf(".") > -1 && GlobalInfo.sysPara.goodsAmountInteger == 'Y')
					{
						quantity = 0;
						new MessageBox(Language.apply("数量不能输入小数,请重新输入"));
						return null;
					}

					quantity = Double.parseDouble(codes[0]);
					if (quantity <= 0)
						quantity = 1;
				}
				catch (Exception ex)
				{
					quantity = 0;
					new MessageBox(Language.apply("数量输入不是有效数字,请重新输入"));
					return null;
				}

				// 检查数量是否合法
				if (quantity > getMaxSaleGoodsQuantity())
				{
					new MessageBox(Language.apply("商品数量过大，请分行输入"));
					return null;
				}

				//
				barcode = codes[1];
				codes = null;
			}
		}
		else
		{
			barcode = code;
			quantity = 1;
		}

		return new String[] { String.valueOf(quantity), barcode };
	}

	public boolean isGoodsAllowCheck(GoodsDef goodsDef)
	{
		return true;
	}

	public boolean findCheckGoods(String code, String yyyh, String gz)
	{
		double quantity = 1, price = 0, allprice = 0;
		String barcode = "";
		boolean isdzcm = false;

		if (checkdjbh == null || checkdjbh.trim().length() <= 0)
		{
			new MessageBox(Language.apply("没有输入盘点单号,不能进行盘点输入!"));
			return false;
		}

		// 分解输入码 数量*编码
		String[] s = convertQuantityBarcode(code);
		if (s == null)
			return false;
		quantity = Convert.toDouble(s[0]);
		barcode = s[1];

		// 查找详细商品资料
		StringBuffer slbuf = new StringBuffer("1");
		GoodsDef goodsDef = findGoodsInfo(barcode, yyyh, gz, null, false, slbuf);
		if (goodsDef == null)
			return false;
		if (!isGoodsAllowCheck(goodsDef))
			return false;
		quantity *= Convert.toDouble(slbuf.toString());

		goodsDef.inputbarcode = barcode;

		// 检查是否控制了盘点柜组
		if (GlobalInfo.sysPara.ischeckgz == 'Y' && this.checkgz != null && !goodsDef.gz.equals(this.checkgz))
		{
			new MessageBox(Language.apply("该商品不属于盘点柜组范围，不允许盘点"));
			return false;
		}

		// 检查是否已经在本单输入
		int i = 0;
		SaleGoodsDef sg = null;
		for (i = 0; i < saleGoods.size(); i++)
		{
			sg = (SaleGoodsDef) saleGoods.elementAt(i);
			if (sg.code.equals(goodsDef.code) && sg.gz.equals(goodsDef.gz) && sg.uid.equals(goodsDef.uid))
			{
				break;
			}
		}
		if (i >= saleGoods.size())
			sg = null;

		// 检查商品是否为空白盘点单商品
		CheckGoodsDef chkgd = null;

		if (GlobalInfo.sysPara.isblankcheckgoods == 'N' && GlobalInfo.sysPara.ischeckcode == 'N')
		{
			if (sg == null && checkgoods != null && checkgoods.size() > 0)
			{
				Vector m_checkgoods = new Vector();
				if (!NetService.getDefault().getCheckGoodsList(m_checkgoods, checkdjbh, goodsDef.code, goodsDef.gz))
				{
					new MessageBox(Language.apply("该商品不属于当前盘点单!"));

					return false;
				}
				else
				{
					if (m_checkgoods.size() <= 0)
					{
						new MessageBox(Language.apply("该商品不属于当前盘点单!"));

						return false;
					}
				}
			}
		}

		// 输入实盘数量
		price = goodsDef.lsj;
		allprice = ManipulatePrecision.doubleConvert(price * quantity, 2, 1);
		isdzcm = false;
		if (ManipulatePrecision.doubleCompare(quantity, 1, 4) == 0 || goodsDef.lsj <= 0)
		{
			String text = null;
			StringBuffer pdstr = new StringBuffer();
			StringBuffer pdstr1 = new StringBuffer();
			pdstr.delete(0, pdstr.length());
			// 售价 大于 0 或者 是 赠品 的时候按数量盘点
			if (goodsDef.lsj > 0 || goodsDef.type == 'Z')
			{
				text = Language.apply("数量");
				if (chkgd != null)
					pdstr.append(chkgd.pdsl);
				if (sg != null)
					pdstr.append(sg.sl);
			}
			else
			{
				text = Language.apply("金额");
				if (chkgd != null)
					pdstr.append(chkgd.pdje);
				if (sg != null)
					pdstr.append(sg.hjje);
			}

			boolean done = true;
			boolean done1 = true;

			do
			{
				// 盘点累加模式且售价大于0
				if (GlobalInfo.sysPara.ischeckadditive == 'Y' && (goodsDef.lsj > 0 || goodsDef.type == 'Z'))
				{
					double amount = 0;

					for (int j = 0; j < saleGoods.size(); j++)
					{
						SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.elementAt(j);
						if (sgd.code.equals(goodsDef.code) && sgd.gz.equals(goodsDef.gz) && sgd.uid.equals(goodsDef.uid))
						{
							amount = sgd.sl;
							text = text + Language.apply(",已盘合计数量\n为 ") + ManipulatePrecision.doubleToString(amount, 4, 1, true) + " " + sgd.unit + "    " + Language.apply("[本次输入数量将累加到已盘合计中]");
							break;
						}
					}

					pdstr.delete(0, pdstr.length());

					if (goodsDef.isdzc == 'N' && GlobalInfo.sysPara.goodsAmountInteger == 'Y')
					{
						done = new TextBox().open(Language.apply("请输入[{0}]商品的实盘数量", new Object[] { goodsDef.name }), Language.apply("数量"), Language.apply("请输入该商品的实际盘点出的") + text, pdstr, -99999999, 99999999, true, TextBox.IntegerInput, -1);
					}
					else
					{
						done = new TextBox().open(Language.apply("请输入[{0}]商品的实盘数量", new Object[] { goodsDef.name }), Language.apply("数量"), Language.apply("请输入该商品的实际盘点出的") + text, pdstr, -99999999.99, 99999999.99, true);
					}

					amount = doAmount(amount, pdstr);

					/*
					 * // 如果数量大于0累加,小于0覆盖 if (Convert.toDouble(pdstr) > 0) {
					 * amount = amount + Convert.toDouble(pdstr); } else {
					 * amount = Convert.toDouble(pdstr); }
					 */

					pdstr.delete(0, pdstr.length());
					pdstr.append(amount);
				}
				else if (GlobalInfo.sysPara.ischeckadditive == 'A' || GlobalInfo.sysPara.ischeckadditive == 'B')
				{
					String memo = "";

					if (goodsDef.lsj > 0 || goodsDef.type == 'Z')
					{
						memo = text + Language.apply("\n说明:盘点数以相同商品总数量为准");
					}
					else
					{
						memo = text;
					}

					pdstr.delete(0, pdstr.length());

					if (goodsDef.isdzc == 'N' && GlobalInfo.sysPara.goodsAmountInteger == 'Y')
					{
						done = new TextBox().open(Language.apply("请输入[{0}]商品的实盘", new Object[] { goodsDef.name }) + text, text, Language.apply("请输入该商品的实际盘点出的") + memo, pdstr, -99999999, 99999999, true, TextBox.IntegerInput, -1);
					}
					else
					{
						done = new TextBox().open(Language.apply("请输入[{0}]商品的实盘", new Object[] { goodsDef.name }) + text, text, Language.apply("请输入该商品的实际盘点出的") + memo, pdstr, -99999999, 99999999.99, true);
					}
				}
				else
				{
					String memo = "";

					if (goodsDef.lsj > 0 || goodsDef.type == 'Z')
					{
						memo = text + Language.apply("\n说明:盘点数以本次输入数量为准");
					}
					else
					{
						memo = text;
					}

					if (goodsDef.isdzc == 'N' && GlobalInfo.sysPara.goodsAmountInteger == 'Y')
					{
						done = new TextBox().open(Language.apply("请输入[{0}]商品的实盘", new Object[] { goodsDef.name }) + text, text, Language.apply("请输入该商品的实际盘点出的") + memo, pdstr, -99999999, 99999999, true, TextBox.IntegerInput, -1);
					}
					else
					{
						done = new TextBox().open(Language.apply("请输入[{0}]商品的实盘", new Object[] { goodsDef.name }) + text, text, Language.apply("请输入该商品的实际盘点出的") + memo, pdstr, -99999999, 99999999.99, true);
					}
				}

				// 不定价商品盘点数量
				if (GlobalInfo.sysPara.ischeckquantity == 'Y' && (goodsDef.lsj <= 0 && goodsDef.type != 'Z'))
				{
					String memo = "";
					String text1 = Language.apply("数量");
					memo = text1;
					if (chkgd != null)
						pdstr1.append(chkgd.pdsl);
					if (sg != null)
						pdstr1.append(sg.sl);

					if (goodsDef.isdzc == 'N' && GlobalInfo.sysPara.goodsAmountInteger == 'Y')
					{
						done1 = new TextBox().open(Language.apply("请输入[{0}]商品的实盘", new Object[] { goodsDef.name }) + text, text, Language.apply("请输入该商品的实际盘点出的") + memo, pdstr1, -99999999, 99999999, true, TextBox.IntegerInput, -1);
					}
					else
					{
						done1 = new TextBox().open(Language.apply("请输入[{0}]商品的实盘", new Object[] { goodsDef.name }) + text, text, Language.apply("请输入该商品的实际盘点出的") + memo, pdstr1, -99999999, 99999999.99, true);
					}
				}

				if (!done)
					return false;
				if (GlobalInfo.sysPara.ischeckquantity == 'Y' && !done1)
					return false;

				if (goodsDef.lsj > 0 || goodsDef.type == 'Z')
				{
					price = goodsDef.lsj;
					quantity = ManipulatePrecision.doubleConvert(Convert.toDouble(pdstr), 4, 1);
					allprice = ManipulatePrecision.doubleConvert(price * quantity, 2, 1);
					isdzcm = false;
				}
				else
				{
					price = 0;
					quantity = 0;
					allprice = ManipulatePrecision.doubleConvert(Convert.toDouble(pdstr), 2, 1);
					isdzcm = true;

					if (GlobalInfo.sysPara.ischeckquantity == 'Y')
					{
						quantity = ManipulatePrecision.doubleConvert(Convert.toDouble(pdstr1), 4, 1);
					}
				}

				break;

			} while (true && GlobalInfo.sysPara.ischeckadditive != 'A' && GlobalInfo.sysPara.ischeckadditive != 'B');
		}

		// 生成盘点商品明细
		if (sg != null && GlobalInfo.sysPara.ischeckadditive != 'A' && GlobalInfo.sysPara.ischeckadditive != 'B')
		{
			if (goodsDef.lsj > 0 || goodsDef.type == 'Z')
			{
				sg.sl = ManipulatePrecision.doubleConvert(quantity, 4, 1);
				sg.jg = ManipulatePrecision.doubleConvert(price, 2, 1);
				sg.hjje = ManipulatePrecision.doubleConvert(sg.jg * sg.sl, 2, 1);
			}
			else
			{
				sg.sl = ManipulatePrecision.doubleConvert(quantity, 4, 1);
				sg.jg = ManipulatePrecision.doubleConvert(price, 2, 1);
				sg.hjje = ManipulatePrecision.doubleConvert(allprice, 2, 1);
			}

			if (isSpecifyCheckInput())
			{
				String[] type = getCheckEditType(sg);
				sg.str8 = type[0];
				sg.name += type[1];
				sg.rowno = curcheckrow + 1;
				curcheckrow += 1;
			}
		}
		else
		{
			SaleGoodsDef saleGoodsDef = goodsDef2SaleGoods(goodsDef, yyyh, quantity, price, allprice, isdzcm);
			saleGoodsDef.batch = checkdjbh;

			if (isSpecifyCheckInput())
			{
				String[] type = getCheckEditType(saleGoodsDef);
				saleGoodsDef.str8 = type[0];
				saleGoodsDef.name += type[1];
				saleGoodsDef.rowno = curcheckrow + 1;
				curcheckrow += 1;
			}

			if (chkgd != null)
			{
				if (goodsDef.lsj > 0 || goodsDef.type == 'Z')
				{
					saleGoodsDef.sqkh = chkgd.kcsl;
					chkgd.pdsl = ManipulatePrecision.doubleToString(saleGoodsDef.sl, 4, 1);
				}
				else
				{
					saleGoodsDef.sqkh = chkgd.kcje;
					chkgd.pdje = ManipulatePrecision.doubleToString(saleGoodsDef.hjje, 2, 1);
				}
			}
			else
			{
				saleGoodsDef.sqkh = null;
			}

			addSaleGoodsObject(saleGoodsDef, goodsDef, null);
		}

		// 计算小票应收
		calcHeadYsje();

		return true;

	}

	/*
	 * public void checkSell() { if (saleGoods.size() <= 0) { new
	 * MessageBox("请先输入要盘点的商品"); return; }
	 * 
	 * MessageBox me = new MessageBox("你确实要提交盘点单吗?", null, true);
	 * 
	 * if (me.verify() != GlobalVar.Key1) return; // 检查空白盘点单商品是否全部输入
	 * CheckGoodsDef chkgd = null; StringBuffer checkgroupid = null; // 盘点数据送网
	 * boolean ok = true; ProgressBox pb = null;
	 * 
	 * Vector saleGoods1 = null;
	 * 
	 * try { pb = new ProgressBox(); pb.setText("正在发送盘点信息,请等待...");
	 * 
	 * saleGoods1 = (Vector)saleGoods.clone();
	 * 
	 * SaleGoodsDef sg = null; chkgd = new CheckGoodsDef();
	 * 
	 * for (int i = 0;i < saleGoods1.size();i++) { sg =
	 * (SaleGoodsDef)saleGoods1.elementAt(i);
	 * 
	 * if (GlobalInfo.sysPara.ischeckadditive == 'A') { chkgd.code = sg.code;
	 * chkgd.gz = sg.gz; chkgd.row = i + 1; chkgd.pdsl = String.valueOf(sg.sl);
	 * chkgd.pdje = String.valueOf(sg.hjje);
	 * 
	 * for (int j = i + 1;j < saleGoods1.size();j++) { SaleGoodsDef sg1 =
	 * (SaleGoodsDef)saleGoods1.elementAt(j);
	 * 
	 * if (sg.code.trim().equals(sg1.code.trim()) &&
	 * sg.gz.trim().equals(sg1.gz.trim())) { chkgd.pdsl = String.valueOf(sg1.sl
	 * + Double.parseDouble(chkgd.pdsl)); chkgd.pdje = String.valueOf(sg1.hjje +
	 * Double.parseDouble(chkgd.pdje)); chkgd.uid = sg.uid; chkgd.bzhl =
	 * sg.bzhl;
	 * 
	 * saleGoods1.remove(j);
	 * 
	 * j = j - 1; } } } else { chkgd.code = sg.code; chkgd.gz = sg.gz; chkgd.row
	 * = i + 1; chkgd.pdsl = String.valueOf(sg.sl); chkgd.pdje =
	 * String.valueOf(sg.hjje); chkgd.uid = sg.uid; chkgd.bzhl = sg.bzhl; }
	 * 
	 * pb.setText("正在发送["+chkgd.code+"]商品的盘点数据...");
	 * 
	 * 
	 * if (checkgroupid == null) { checkgroupid = new StringBuffer(); } else {
	 * checkgroupid.delete(0,checkgroupid.length()); }
	 * 
	 * if
	 * (!NetService.getDefault().sendCheckGoods(checkdjbh,chkgd,checkgroupid,this
	 * .checkcw,this.checkrq)) { ok = false; // 选中报错的行
	 * saleEvent.table.setSelection(i); saleEvent.table.showSelection();
	 * saleEvent.setCurGoodsBigInfo();
	 * 
	 * if (new
	 * MessageBox("["+chkgd.code+"]商品的盘点数据保存失败\n\n继续上传其他商品盘点数据吗？",null,true
	 * ).verify() != GlobalVar.Key1) { return; } } }
	 * 
	 * pb.close(); pb = null;
	 * 
	 * if (GlobalInfo.sysPara.checkgrouplen <= 0) { // 单据号+组号 if (checkgroupid
	 * != null && !checkgroupid.toString().trim().equals("")) { saleHead.yfphm =
	 * saleHead.yfphm.substring(0,GlobalInfo.sysPara.checklength) + "-" +
	 * checkgroupid.toString().trim(); } }
	 * 
	 * if (ok) { if (GlobalInfo.sysPara.ischecksaveprint == 'Y') { if
	 * (CheckGoodsMode.getDefault().isLoad()) {
	 * CheckGoodsMode.getDefault().setTemplateObject(saleHead, saleGoods,
	 * salePayment);
	 * 
	 * CheckGoodsMode.getDefault().printBill(); } } else { new
	 * MessageBox("本次盘点输入完成!"); } } else { new
	 * MessageBox("本次盘点输入完成,但有部分盘点数据上传失败!"); } } catch (Exception ex) {
	 * ex.printStackTrace(); } finally { if (saleGoods1 != null) {
	 * saleGoods1.clear(); saleGoods1 = null; }
	 * 
	 * if (pb != null) { pb.close(); pb = null; } } // 开始新盘点单 if (ok)
	 * saleEvent.initOneSale(this.saletype); }
	 */

	public void checkSell()
	{
		// 不可编辑的盘点单不需要上传
		if (GlobalInfo.sysPara.isblankcheckgoods == 'B' && "N".equals(checkeditflag)) { return; }
		if (saleGoods.size() <= 0)
		{
			new MessageBox(Language.apply("请先输入要盘点的商品"));
			return;
		}

		MessageBox me = new MessageBox(Language.apply("你确实要提交盘点单吗?"), null, true);

		if (me.verify() != GlobalVar.Key1)
			return;

		// 检查空白盘点单商品是否全部输入
		CheckGoodsDef chkgd = null;
		StringBuffer checkgroupid = null;

		// 盘点数据送网
		boolean ok = true;
		ProgressBox pb = null;
		Vector saleGoods1 = new Vector();

		try
		{
			pb = new ProgressBox();

			// saleGoods1 = (Vector) saleGoods.clone();

			// vector的clone是浅拷贝，只是拷贝的引用
			// 要循环调用salegoods自身的clone
			for (int i = 0; i < saleGoods.size(); i++)
				saleGoods1.add(((SaleGoodsDef) saleGoods.elementAt(i)).clone());

			SaleGoodsDef sg = null;

			// 先汇总，便于记录总行数
			if (isSpecifyCheckInput())
			{
				pb.setText(Language.apply("正在汇总盘点信息,请等待..."));
				for (int i = 0; i < saleGoods1.size(); i++)
				{
					sg = (SaleGoodsDef) saleGoods1.elementAt(i);
					if (!"A".equals(sg.str8) && !"U".equals(sg.str8) && !"D".equals(sg.str8))
					{
						saleGoods1.remove(i);
						i = i - 1;
					}
				}
			}

			if (GlobalInfo.sysPara.ischeckadditive == 'A')
			{
				pb.setText(Language.apply("正在汇总盘点信息,请等待..."));

				for (int i = 0; i < saleGoods1.size(); i++)
				{
					sg = (SaleGoodsDef) saleGoods1.elementAt(i);
					for (int j = i + 1; j < saleGoods1.size(); j++)
					{
						SaleGoodsDef sg1 = (SaleGoodsDef) saleGoods1.elementAt(j);

						if (sg.code.trim().equals(sg1.code.trim()) && sg.gz.trim().equals(sg1.gz.trim()))
						{
							sg.sl = sg1.sl + sg.sl;
							sg.hjje = sg1.hjje + sg.hjje;
							saleGoods1.remove(j);

							j = j - 1;
						}
					}
				}
			}

			chkgd = new CheckGoodsDef();

			pb.setText(Language.apply("正在发送盘点信息,请等待..."));

			// int maxRow = totalcheckrow;
			for (int i = 0; i < saleGoods1.size(); i++)
			{
				String isLastLine = "";
				if (saleGoods1.size() == 1)
				{
					isLastLine = "A"; // 标记只有一行
				}
				else
				{
					if (i == 0)
						isLastLine = "F"; // 标记第一行
					if (i == saleGoods1.size() - 1)
						isLastLine = "L"; // 标记最后一行
				}

				sg = (SaleGoodsDef) saleGoods1.elementAt(i);
				chkgd.code = sg.code;
				chkgd.gz = sg.gz;
				if (isSpecifyCheckInput())
					chkgd.row = sg.rowno;
				else
					chkgd.row = i + 1;
				chkgd.pdsl = String.valueOf(sg.sl);
				chkgd.pdje = String.valueOf(sg.hjje);
				chkgd.uid = sg.uid;
				chkgd.bzhl = sg.bzhl;
				chkgd.handInputcode = sg.inputbarcode;

				// // 此盘点模式只发送发生了改变的增量
				// if (isSpecifyCheckInput())
				// {
				// if ("A".equals(sg.str8))
				// {
				// maxRow = maxRow + 1;
				// chkgd.row = maxRow;
				// }
				// else
				// {
				// chkgd.row = sg.rowno;
				// }
				// }

				pb.setText(Language.apply("正在发送[{0}]商品的盘点数据...", new Object[] { chkgd.code }));

				if (checkgroupid == null)
				{
					checkgroupid = new StringBuffer();
				}
				else
				{
					checkgroupid.delete(0, checkgroupid.length());
				}

				if (!NetService.getDefault().sendCheckGoods(checkdjbh, chkgd, checkgroupid, this.checkcw, this.checkrq, isLastLine, sg.str8))
				{
					ok = false;

					// 选中报错的行
					saleEvent.table.setSelection(i);
					saleEvent.table.showSelection();
					saleEvent.setCurGoodsBigInfo();

					if (new MessageBox("[" + chkgd.code + Language.apply("]商品的盘点数据保存失败\n\n继续上传其他商品盘点数据吗？"), null, true).verify() != GlobalVar.Key1) { return; }
				}
			}

			pb.close();
			pb = null;

			if (GlobalInfo.sysPara.checkgrouplen <= 0)
			{
				// 单据号+组号
				if (checkgroupid != null && !checkgroupid.toString().trim().equals(""))
				{
					saleHead.yfphm = saleHead.yfphm.substring(0, GlobalInfo.sysPara.checklength) + "-" + checkgroupid.toString().trim();
				}
			}

			if (ok)
			{
				if (GlobalInfo.sysPara.ischecksaveprint == 'Y')
				{
					if (CheckGoodsMode.getDefault().isLoad())
					{
						CheckGoodsMode.getDefault().setTemplateObject(saleHead, saleGoods, salePayment);

						CheckGoodsMode.getDefault().printBill();
					}
				}
				else
				{
					new MessageBox(Language.apply("本次盘点输入完成!"));
				}
			}
			else
			{
				new MessageBox(Language.apply("本次盘点输入完成,但有部分盘点数据上传失败!"));
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (saleGoods1 != null)
			{
				saleGoods1.clear();
				saleGoods1 = null;
			}

			if (pb != null)
			{
				pb.close();
				pb = null;
			}
		}

		// 开始新盘点单
		if (ok)
			saleEvent.initOneSale(this.saletype);
	}

	public boolean findCoupon(String code, String yyyh, String gz)
	{
		double quantity = 1;
		String barcode = "";
		double price = 0;

		// 检查是否允许找商品
		if (!allowStartFindGoods())
			return false;

		// 分解输入码 数量*编码
		String[] s = convertQuantityBarcode(code);
		if (s == null)
			return false;
		quantity = Convert.toDouble(s[0]);
		barcode = s[1];

		// 找券
		Vector v = NetService.getDefault().findCoupon(barcode, saletype);
		if (v == null || v.size() <= 0)
		{
			new MessageBox(Language.apply("此券规则号无效"));
			return false;
		}

		String[] row = null;

		if (GlobalInfo.sysPara.couponSaleType.equals("A"))
		{
			if (v.size() > 1)
			{
				int choice = 0;
				if ((choice = new MutiSelectForm().open(Language.apply("请输入券规则号"), new String[] { Language.apply("券规则号"), Language.apply("券规则描述"), Language.apply("金额") }, new int[] { 250, 210, 100 }, v, true)) < 0) { return false; }

				row = (String[]) v.elementAt(choice);
			}
			else
			{
				row = (String[]) v.elementAt(0);
			}

			if (Convert.toDouble(row[2]) <= 0)
			{
				StringBuffer pricestr = new StringBuffer();
				boolean done = new TextBox().open(Language.apply("请输入商品价格"), Language.apply("价格"), "", pricestr, 0.01, getMaxSaleGoodsMoney(), true);
				if (!done) { return false; }
				price = Convert.toDouble(pricestr);
			}
			else
			{
				price = Convert.toDouble(row[2]);
			}
		}
		else if (GlobalInfo.sysPara.couponSaleType.equals("B"))
		{
			if (v.size() > 1)
			{
				int choice = 0;
				if ((choice = new MutiSelectForm().open(Language.apply("请输入券规则号"), new String[] { Language.apply("券规则号"), Language.apply("券规则描述"), Language.apply("兑换比例") }, new int[] { 250, 210, 100 }, v, true)) < 0) { return false; }

				row = (String[]) v.elementAt(choice);
			}
			else
			{
				row = (String[]) v.elementAt(0);
			}

			StringBuffer pricestr = new StringBuffer();
			boolean done = new TextBox().open(Language.apply("请输入券金额"), Language.apply("金额"), "", pricestr, 0.01, getMaxSaleGoodsMoney(), true);
			if (!done) { return false; }
			price = Convert.toDouble(pricestr);
			row[1] = pricestr + "元" + row[1];
			price = ManipulatePrecision.doubleConvert(price / Convert.toDouble(row[2]));

		}
		String[] temp1 = row[0].split("@");

		GoodsDef goods = new GoodsDef();
		goods.barcode = temp1[0];
		goods.code = temp1[0];
		goods.name = row[1]; // 名称
		goods.lsj = price;
		// 记录兑换比例
		if (GlobalInfo.sysPara.couponSaleType.equals("B"))
			goods.attr08 = row[2];

		SaleGoodsDef saleGoodsDef = new SaleGoodsDef();
		saleGoodsDef.syjh = saleHead.syjh; // 收银机号,主键
		saleGoodsDef.fphm = saleHead.fphm; // 小票号,主键
		saleGoodsDef.rowno = saleGoods.size() + 1; // 行号,主键
		saleGoodsDef.yyyh = yyyh; // 营业员
		saleGoodsDef.fph = curyyyfph; // 营业员手工单发票号
		saleGoodsDef.barcode = temp1[0]; // 商品条码
		saleGoodsDef.code = temp1[0]; // 商品编码
		saleGoodsDef.type = '1'; // 编码类别
		saleGoodsDef.gz = temp1[0]; // 商品柜组
		saleGoodsDef.catid = temp1[1]; // 商品品类
		// saleGoodsDef.ppcode = goodsDef.ppcode; // 商品品牌
		// saleGoodsDef.uid = goodsDef.uid; // 多单位码
		// saleGoodsDef.batch = curBatch; // 批号
		// saleGoodsDef.yhdjbh = goodsDef.popdjbh; // 优惠单据编号
		saleGoodsDef.name = row[1]; // 名称
		saleGoodsDef.unit = "张"; // 单位
		// saleGoodsDef.bzhl = goodsDef.bzhl; // 包装含量
		saleGoodsDef.sl = ManipulatePrecision.doubleConvert(quantity, 4, 1); // 销售数量

		if (GlobalInfo.sysPara.couponSaleType.equals("A"))
			saleGoodsDef.lsj = Convert.toDouble(row[2]); // 零售价
		else if (GlobalInfo.sysPara.couponSaleType.equals("B"))
			saleGoodsDef.lsj = price; // 零售价

		saleGoodsDef.jg = ManipulatePrecision.doubleConvert(price, 2, 1); // 销售价格

		saleGoodsDef.hjje = ManipulatePrecision.doubleConvert(saleGoodsDef.jg * saleGoodsDef.sl, 2, 1); // 合计金额

		saleGoodsDef.hjzk = 0; // 合计折扣,等于各种折扣之和
		saleGoodsDef.hyzke = 0; // 会员折扣额(来自会员优惠)
		saleGoodsDef.hyzkfd = 0; // 会员折扣分担
		saleGoodsDef.yhzke = 0; // 优惠折扣额(来自营销优惠)
		saleGoodsDef.yhzkfd = 0; // 优惠折扣分担
		saleGoodsDef.lszke = 0; // 零时折扣额(来自手工打折)
		saleGoodsDef.lszre = 0; // 零时折让额(来自手工打折)
		saleGoodsDef.lszzk = 0; // 零时总品折扣
		saleGoodsDef.lszzr = 0; // 零时总品折让
		saleGoodsDef.plzke = 0; // 批量折扣
		saleGoodsDef.zszke = 0; // 赠送折扣
		// saleGoodsDef.lszkfd = curPfzkfd; // 临时折扣分担
		saleGoodsDef.sqkh = ""; // 单品授权卡号
		saleGoodsDef.sqktype = '\0'; // 单品授权卡类别
		saleGoodsDef.sqkzkfd = 0; // 单品授权卡授权折扣分担
		saleGoodsDef.isvipzk = 'Y'; // 是否允许VIP折扣（Y/N）
		// saleGoodsDef.xxtax = goodsDef.xxtax; // 税率
		saleGoodsDef.flag = '4'; // 商品标志，1-赠品,2-电子秤条码，3-削价，4-一般

		saleGoodsDef.yjhxcode = ""; // 以旧换新条码
		saleGoodsDef.ysyjh = ""; // 原收银机号
		saleGoodsDef.yfphm = 0; // 原小票号
		saleGoodsDef.fhdd = ""; // 发货地点
		saleGoodsDef.memo = ""; // 备注
		saleGoodsDef.str1 = ""; // 备用字段
		saleGoodsDef.str2 = ""; // 备用字段
		saleGoodsDef.num1 = 0; // 备用字段
		saleGoodsDef.num2 = 0; // 备用字段

		addSaleGoodsObject(saleGoodsDef, goods, getGoodsSpareInfo(goods, saleGoodsDef));

		// 计算小票应收
		calcHeadYsje();

		return true;
	}

	public boolean findJSDetail(String code, String yyyh, String gz)
	{
		double quantity = 1;
		// String barcode = "";
		double price = 0;

		// 检查是否允许找商品
		if (!allowStartFindGoods())
			return false;

		// 分解输入码 数量*编码
		String[] s = convertQuantityBarcode(code);
		if (s == null)
			return false;
		quantity = Convert.toDouble(s[0]);

		// 找结算列表
		Vector v = NetService.getDefault().findChargeFee(code, yyyh, GlobalInfo.posLogin.gh, saletype, "", "", "", "", "");
		if (v == null || v.size() <= 0)
		{
			new MessageBox(Language.apply("此券规则号无效"));
			return false;
		}

		if (v.size() > 0 && saleGoods.size() > 0)
		{
			for (int i = 0; i < saleGoods.size(); i++)
			{
				SaleGoodsDef si = (SaleGoodsDef) saleGoods.elementAt(i);

				if (code.equals(si.inputbarcode))
				{
					new MessageBox(Language.apply("此结算单已经存在"));
					return false;
				}
			}
		}

		for (int i = 0; i < v.size(); i++)
		{
			String[] row = (String[]) v.elementAt(i);
			price = Convert.toDouble(row[2]);
			String[] temp1 = row[0].split(",");
			String[] temp2 = row[1].split(",");

			GoodsDef goods = new GoodsDef();
			goods.barcode = temp1[0];
			goods.code = temp1[0];
			if (temp2.length > 0)
				goods.name = temp2[0]; // 名称

			goods.lsj = price;

			SaleGoodsDef saleGoodsDef = new SaleGoodsDef();
			saleGoodsDef.syjh = saleHead.syjh; // 收银机号,主键
			saleGoodsDef.fphm = saleHead.fphm; // 小票号,主键
			saleGoodsDef.rowno = saleGoods.size() + 1; // 行号,主键
			saleGoodsDef.yyyh = yyyh; // 营业员
			saleGoodsDef.fph = curyyyfph; // 营业员手工单发票号
			saleGoodsDef.barcode = temp1[0]; // 商品条码
			saleGoodsDef.code = temp1[0]; // 商品编码
			saleGoodsDef.type = '1'; // 编码类别
			saleGoodsDef.gz = row[3]; // 商品柜组
			if (temp1.length > 1)
				saleGoodsDef.catid = temp1[1];
			if (temp1.length > 2)
				saleGoodsDef.fhdd = temp1[2];

			if (temp2.length > 1)
				saleGoodsDef.batch = temp2[1]; // 结算门店号
			// saleGoodsDef.catid = row[4]; // 商品品类
			saleGoodsDef.ppcode = "0"; // 商品品牌
			// saleGoodsDef.uid = goodsDef.uid; // 多单位码
			// saleGoodsDef.batch = curBatch; // 批号
			// saleGoodsDef.yhdjbh = goodsDef.popdjbh; // 优惠单据编号
			saleGoodsDef.name = row[1]; // 名称
			saleGoodsDef.unit = "张"; // 单位
			// saleGoodsDef.bzhl = goodsDef.bzhl; // 包装含量
			saleGoodsDef.sl = ManipulatePrecision.doubleConvert(quantity, 4, 1); // 销售数量
			saleGoodsDef.lsj = Convert.toDouble(row[2]); // 零售价
			saleGoodsDef.jg = ManipulatePrecision.doubleConvert(price, 2, 1); // 销售价格

			saleGoodsDef.hjje = ManipulatePrecision.doubleConvert(saleGoodsDef.jg * saleGoodsDef.sl, 2, 1); // 合计金额

			saleGoodsDef.hjzk = 0; // 合计折扣,等于各种折扣之和
			saleGoodsDef.hyzke = 0; // 会员折扣额(来自会员优惠)
			saleGoodsDef.hyzkfd = 0; // 会员折扣分担
			saleGoodsDef.yhzke = 0; // 优惠折扣额(来自营销优惠)
			saleGoodsDef.yhzkfd = 0; // 优惠折扣分担
			saleGoodsDef.lszke = 0; // 零时折扣额(来自手工打折)
			saleGoodsDef.lszre = 0; // 零时折让额(来自手工打折)
			saleGoodsDef.lszzk = 0; // 零时总品折扣
			saleGoodsDef.lszzr = 0; // 零时总品折让
			saleGoodsDef.plzke = 0; // 批量折扣
			saleGoodsDef.zszke = 0; // 赠送折扣
			// saleGoodsDef.lszkfd = curPfzkfd; // 临时折扣分担
			saleGoodsDef.sqkh = ""; // 单品授权卡号
			saleGoodsDef.sqktype = '\0'; // 单品授权卡类别
			saleGoodsDef.sqkzkfd = 0; // 单品授权卡授权折扣分担
			saleGoodsDef.isvipzk = 'Y'; // 是否允许VIP折扣（Y/N）
			// saleGoodsDef.xxtax = goodsDef.xxtax; // 税率
			saleGoodsDef.flag = '4'; // 商品标志，1-赠品,2-电子秤条码，3-削价，4-一般
			saleGoodsDef.inputbarcode = code;
			saleGoodsDef.yjhxcode = ""; // 以旧换新条码
			saleGoodsDef.ysyjh = ""; // 原收银机号
			saleGoodsDef.yfphm = 0; // 原小票号

			saleGoodsDef.memo = ""; // 备注
			saleGoodsDef.str1 = ""; // 备用字段
			saleGoodsDef.str2 = ""; // 备用字段
			if (row.length > 4 && row[4].trim().length() > 0)
				saleGoodsDef.num1 = Convert.toDouble(row[4]); // 备用字段
			saleGoodsDef.num2 = 0; // 备用字段

			addSaleGoodsObject(saleGoodsDef, goods, getGoodsSpareInfo(goods, saleGoodsDef));
		}
		// 计算小票应收
		calcHeadYsje();

		return true;
	}

	public boolean findJFDetail(String code, String yyyh, String gz)
	{
		double quantity = 1;
		double price = 0;

		// 检查是否允许找商品
		if (!allowStartFindGoods())
			return false;

		// 分解输入码 数量*编码
		String[] s = convertQuantityBarcode(code);
		if (s == null)
			return false;
		quantity = Convert.toDouble(s[0]);

		// 找结算列表
		Vector v = NetService.getDefault().findChargeFee(code, yyyh, GlobalInfo.posLogin.gh, saletype, "", "", "", "", "");
		if (v == null || v.size() <= 0)
		{
			new MessageBox(Language.apply("此券规则号无效"));
			return false;
		}

		String[] row = null;
		if (v.size() > 1)
		{
			int choice = 0;
			if ((choice = new MutiSelectForm().open(Language.apply("请输入券规则号"), new String[] { Language.apply("缴费单号"), Language.apply(" 描 述 "), Language.apply("金额") }, new int[] { 250, 210, 100 }, v, true)) < 0) { return false; }

			row = (String[]) v.elementAt(choice);
		}
		else
		{
			row = (String[]) v.elementAt(0);
		}

		if (Convert.toDouble(row[2]) <= 0)
		{
			StringBuffer pricestr = new StringBuffer();
			boolean done = new TextBox().open(Language.apply("请输入商品价格"), Language.apply("价格"), "", pricestr, 0.01, getMaxSaleGoodsMoney(), true);
			if (!done) { return false; }
			price = Convert.toDouble(pricestr);
		}
		else
		{
			price = Convert.toDouble(row[2]);
		}

		String[] temp1 = row[0].split("@");
		String[] temp2 = row[1].split(",");

		GoodsDef goods = new GoodsDef();
		goods.barcode = temp1[0];
		goods.code = temp1[0];
		goods.name = row[1]; // 名称
		goods.lsj = price;

		SaleGoodsDef saleGoodsDef = new SaleGoodsDef();
		saleGoodsDef.syjh = saleHead.syjh; // 收银机号,主键
		saleGoodsDef.fphm = saleHead.fphm; // 小票号,主键
		saleGoodsDef.rowno = saleGoods.size() + 1; // 行号,主键
		saleGoodsDef.yyyh = yyyh; // 营业员
		saleGoodsDef.fph = curyyyfph; // 营业员手工单发票号
		saleGoodsDef.barcode = temp1[0]; // 商品条码
		saleGoodsDef.code = temp1[0]; // 商品编码
		saleGoodsDef.type = '1'; // 编码类别
		saleGoodsDef.gz = temp1[0]; // 商品柜组
		saleGoodsDef.catid = temp1[0]; // 商品品类
		// saleGoodsDef.ppcode = goodsDef.ppcode; // 商品品牌
		// saleGoodsDef.uid = goodsDef.uid; // 多单位码
		if (temp2.length > 1)
			saleGoodsDef.batch = temp2[1]; // 批号
		// saleGoodsDef.yhdjbh = goodsDef.popdjbh; // 优惠单据编号
		if (temp2.length > 0)
			saleGoodsDef.name = temp2[0]; // 名称

		// saleGoodsDef.bzhl = goodsDef.bzhl; // 包装含量
		saleGoodsDef.sl = ManipulatePrecision.doubleConvert(quantity, 4, 1); // 销售数量
		saleGoodsDef.lsj = Convert.toDouble(row[2]); // 零售价
		saleGoodsDef.jg = ManipulatePrecision.doubleConvert(price, 2, 1); // 销售价格

		saleGoodsDef.hjje = ManipulatePrecision.doubleConvert(saleGoodsDef.jg * saleGoodsDef.sl, 2, 1); // 合计金额
		saleGoodsDef.unit = "";
		saleGoodsDef.hjzk = 0; // 合计折扣,等于各种折扣之和
		saleGoodsDef.hyzke = 0; // 会员折扣额(来自会员优惠)
		saleGoodsDef.hyzkfd = 0; // 会员折扣分担
		saleGoodsDef.yhzke = 0; // 优惠折扣额(来自营销优惠)
		saleGoodsDef.yhzkfd = 0; // 优惠折扣分担
		saleGoodsDef.lszke = 0; // 零时折扣额(来自手工打折)
		saleGoodsDef.lszre = 0; // 零时折让额(来自手工打折)
		saleGoodsDef.lszzk = 0; // 零时总品折扣
		saleGoodsDef.lszzr = 0; // 零时总品折让
		saleGoodsDef.plzke = 0; // 批量折扣
		saleGoodsDef.zszke = 0; // 赠送折扣
		// saleGoodsDef.lszkfd = curPfzkfd; // 临时折扣分担
		saleGoodsDef.sqkh = ""; // 单品授权卡号
		saleGoodsDef.sqktype = '\0'; // 单品授权卡类别
		saleGoodsDef.sqkzkfd = 0; // 单品授权卡授权折扣分担
		saleGoodsDef.isvipzk = 'N'; // 是否允许VIP折扣（Y/N）
		// saleGoodsDef.xxtax = goodsDef.xxtax; // 税率
		saleGoodsDef.flag = '4'; // 商品标志，1-赠品,2-电子秤条码，3-削价，4-一般
		saleGoodsDef.unit = "";
		saleGoodsDef.yjhxcode = ""; // 以旧换新条码
		saleGoodsDef.ysyjh = ""; // 原收银机号
		saleGoodsDef.yfphm = 0; // 原小票号
		saleGoodsDef.fhdd = ""; // 发货地点
		saleGoodsDef.memo = ""; // 备注
		saleGoodsDef.str1 = ""; // 备用字段
		saleGoodsDef.str2 = ""; // 备用字段
		if (row.length > 4 && row[4].trim().length() > 0 && row[4].equals("Y"))
			saleGoodsDef.num1 = 1; // 备用字段
		if (saleGoodsDef.num1 > 0)
			saleGoodsDef.unit = "N"; // 单位
		saleGoodsDef.num2 = 0; // 备用字段

		addSaleGoodsObject(saleGoodsDef, goods, getGoodsSpareInfo(goods, saleGoodsDef));

		// 计算小票应收
		calcHeadYsje();

		return true;
	}

	protected double doAmount(double ysl, StringBuffer sb)
	{
		double amount;
		try
		{
			// 如果数量大于0累加,小于0覆盖
			if (Convert.toDouble(sb) > 0)
			{
				amount = ysl + Convert.toDouble(sb);
			}
			else
			{
				if(GlobalInfo.sysPara.isaddwithzeroquantity == 'Y')
					amount = ysl + Convert.toDouble(sb);
				else
					amount = Convert.toDouble(sb);
			}

			return amount;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return 0;
		}
	}

	public boolean findGoods(String code, String yyyh, String gz, String memo)
	{
		String comcode = "";
		String barcode = "";
		boolean isdzcm;
		double dzcmjg = 0;
		double dzcmsl = 0;
		String dzcmscsj = "";
		double quantity = 1;
		double price = 0;

		// 检查是否允许找商品
		if (!allowStartFindGoods())
			return false;

		// 分解输入码 数量*编码
		String[] s = convertQuantityBarcode(code);
		if (s == null)
			return false;
		quantity = Convert.toDouble(s[0]);
		barcode = s[1];

		// 解析电子秤码
		String[] codeInfo = new String[4];
		isdzcm = analyzeBarcode(barcode, codeInfo);

		if (isdzcm)
		{
			comcode = codeInfo[0];
			dzcmjg = ManipulatePrecision.doubleConvert(Double.parseDouble(codeInfo[1]), 2, 1);
			dzcmsl = ManipulatePrecision.doubleConvert(Double.parseDouble(codeInfo[2]), 4, 1);
			dzcmscsj = codeInfo[3];

			// 验证电子秤校验位
			if (!verifyDzcmCheckbit(barcode))
			{
				new MessageBox(Language.apply("电子秤码校验位错误"), null, false);

				return false;
			}

			if (dzcmjg <= 0 && dzcmsl <= 0)
			{
				new MessageBox(Language.apply("该电子秤格式条码无效"), null, false);

				return false;
			}
		}
		else
		{
			comcode = barcode;
		}

		// 查找详细商品资料,可支持数量转换
		StringBuffer slbuf = new StringBuffer(ManipulatePrecision.doubleToString(quantity));

		GoodsDef goodsDef = findGoodsInfo(comcode, yyyh, gz, dzcmscsj, isdzcm, slbuf);
		if (goodsDef == null)
			return false;
		quantity = Convert.toDouble(slbuf.toString());

		// 获得最小批量数量
		quantity = getMinPlsl(quantity, goodsDef);

		// 电子秤商品记录原始电子秤码
		goodsDef.inputbarcode = barcode;
		if (isdzcm)
			goodsDef.barcode = convertDzcmBarcode(goodsDef, barcode, isdzcm);

		// 设置商品缺省售价
		price = setGoodsDefaultPrice(goodsDef);

		// 电子秤码没有通过条码解析销售，补入商品价格或数量
		if (goodsDef.isdzc == 'Y' && !isdzcm)
		{
			// 输入价格模式
			if (GlobalInfo.sysPara.dzccodesale == 'Y')
			{
				isdzcm = true;

				StringBuffer pricestr = new StringBuffer();
				do
				{
					pricestr.delete(0, pricestr.length());
					pricestr.append(price);

					boolean done = new TextBox().open(Language.apply("请输入商品[") + goodsDef.inputbarcode + "]" + (goodsDef.name.trim().length() > 20 ? goodsDef.name.trim().substring(0, 19) : goodsDef.name.trim()) + Language.apply("价格"), Language.apply("价格"), "", pricestr, 0.01, getMaxSaleGoodsMoney(), true);

					if (!done)
					{
						return false;
					}
					else
					{
						dzcmjg = ManipulatePrecision.doubleConvert(getConvertPrice(Double.parseDouble(pricestr.toString()), goodsDef), 2, 1);

						if (dzcmjg <= 0)
						{
							new MessageBox(Language.apply("该商品价格必须大于0"));
						}
						else
						{
							break;
						}
					}
				} while (true);
			}

			// 输入数量模式
			if (GlobalInfo.sysPara.dzccodesale == 'A')
			{
				isdzcm = true;

				StringBuffer slstr = new StringBuffer();
				do
				{
					slstr.delete(0, slstr.length());
					slstr.append(0.000);

					boolean done = new TextBox().open(Language.apply("请输入商品[") + goodsDef.inputbarcode + "]" + (goodsDef.name.trim().length() > 20 ? goodsDef.name.trim().substring(0, 19) : goodsDef.name.trim()) + Language.apply("数量"), Language.apply("数量"), "", slstr, 0.01, getMaxSaleGoodsQuantity(), true);

					if (!done)
					{
						return false;
					}
					else
					{
						dzcmsl = Double.parseDouble(slstr.toString());

						if (dzcmsl <= 0)
						{
							new MessageBox(Language.apply("该商品数量必须大于0"));
						}
						else
						{
							break;
						}
					}
				} while (true);
			}
		}

		// 电子秤条码的数量价格处理
		int dzcprice = 0;
		double allprice = 0;

		if (isdzcm)
		{
			dzcmjgzk = 0;

			if ((dzcmsl > 0) && (dzcmjg <= 0)) // 只有数量
			{
				// bzhl记录电子秤称重单位和商品主档单位的转换比例
				if (goodsDef.bzhl <= 0)
					goodsDef.bzhl = 1;
				quantity = ManipulatePrecision.doubleConvert(dzcmsl / goodsDef.bzhl, 4, 1);
				price = ManipulatePrecision.doubleConvert(goodsDef.lsj, 2, 1);
				allprice = quantity * price;
				dzcprice = 1;

				// 电子秤打印的合计一般都是从第三位截断再四舍五入
				allprice = ManipulatePrecision.doubleConvert(allprice, 3, 0);
				allprice = ManipulatePrecision.doubleConvert(allprice, 2, 1);

				// 按价格精度进行计算,差额记折扣
				double jg = getConvertPrice(allprice, goodsDef);
				if (ManipulatePrecision.doubleCompare(allprice, jg, 2) != 0)
				{
					dzcmjgzk = ManipulatePrecision.sub(allprice, jg);
				}
			}
			else if ((dzcmsl <= 0) && (dzcmjg > 0)) // 只有金额
			{

				if (goodsDef.lsj <= 0) // 不定价商品
				{
					quantity = 1;
					price = dzcmjg;
					allprice = price;
					dzcprice = 1;
				}
				else
				// 定价商品,反算数量
				{
					// pfj存放电子秤实际秤上的价格(可能是促销价),如果和商品主档价格不一致,说明有促销,
					// 用秤的价格反算出数量然后再正常计算促销
					if (GlobalInfo.sysPara.isCalcAsPfj == 'Y' && (goodsDef.pfj > 0 && ManipulatePrecision.doubleCompare(goodsDef.lsj, goodsDef.pfj, 2) != 0))
					{
						quantity = ManipulatePrecision.doubleConvert((dzcmjg / goodsDef.pfj), 4, 1);
						price = goodsDef.lsj;
						allprice = ManipulatePrecision.doubleConvert(quantity * price);
						dzcprice = 2;
						/*
						 * if (SellType.ISBACK(saletype)) { dzcmjgzk = allprice
						 * - ManipulatePrecision.doubleConvert(quantity *
						 * goodsDef.pfj); }
						 */
						dzcmjgzk = allprice - ManipulatePrecision.doubleConvert(quantity * goodsDef.pfj);
					}
					else
					{
						quantity = ManipulatePrecision.doubleConvert((dzcmjg / goodsDef.lsj), 4, 1);
						price = goodsDef.lsj;
						allprice = dzcmjg;
						dzcprice = 2;
					}
				}
			}
			else if ((dzcmsl > 0) && (dzcmjg > 0)) // 即有数量又有价格
			{

				// bzhl记录电子秤称重单位和商品主档单位的转换比例
				// 如果定价商品单价*数量的成交金额已经与秤的成交价四舍五入精度后一致,则无需重算商品单价
				if (goodsDef.bzhl <= 0)
					goodsDef.bzhl = 1;
				quantity = ManipulatePrecision.doubleConvert(dzcmsl / goodsDef.bzhl, 4, 1);
				allprice = dzcmjg;
				if (goodsDef.lsj > 0 && ManipulatePrecision.doubleCompare(goodsDef.lsj * quantity, allprice, ManipulatePrecision.getDoubleScale(allprice)) == 0)
				{
					// 电子秤的成交价可能到角,秤的成交价和数量*单价到分的成交价之间的四舍五入差额记折扣
					if (ManipulatePrecision.doubleCompare(goodsDef.lsj * quantity, allprice, 2) != 0)
					{
						allprice = ManipulatePrecision.doubleConvert(goodsDef.lsj * quantity, 2, 1);
						dzcmjgzk = ManipulatePrecision.sub(allprice, dzcmjg);
						dzcmjgzk = ManipulatePrecision.doubleConvert(dzcmjgzk, 2, 1);
					}
				}
				else
				{
					goodsDef.lsj = goodsDef.hyj = goodsDef.pfj = ManipulatePrecision.doubleConvert(dzcmjg / (dzcmsl / goodsDef.bzhl), 2, 1);
				}
				price = goodsDef.lsj;
				//
			}
		}

		// 检查找到的商品是否允许销售
		if (!checkFindGoodsAllowSale(goodsDef, quantity, isdzcm, dzcmsl, dzcmjg))
			return false;

		// 未定价商品或退货或批发要求输入售价
		if (isPriceConfirm(goodsDef))
		{
			// 指定小票退货,查询退货原始交易信息
			if (isSpecifyBack())
			{
				Vector back = new Vector();

				if (!DataService.getDefault().getBackGoodsDetail(back, thSyjh, String.valueOf(thFphm), goodsDef.code, goodsDef.gz, goodsDef.uid)) { return false; }

				int cho = 0;
				if (back.size() > 1)
				{
					Vector choice = new Vector();
					String[] title = { Language.apply("商品编码"), Language.apply("数量"), Language.apply("单价"), Language.apply("合计折扣"), Language.apply("应付金额") };
					int[] width = { 100, 100, 100, 100, 100 };
					String[] row = null;
					for (int j = 0; j < back.size(); j++)
					{
						thSaleGoods = (SaleGoodsDef) back.elementAt(j);
						row = new String[5];
						row[0] = thSaleGoods.code;
						row[1] = ManipulatePrecision.doubleToString(thSaleGoods.sl, 4, 1, true);
						row[2] = ManipulatePrecision.doubleToString(thSaleGoods.lsj, 2, 1);
						row[3] = ManipulatePrecision.doubleToString(thSaleGoods.hjzk, 2, 1);
						row[4] = ManipulatePrecision.doubleToString(thSaleGoods.hjje - thSaleGoods.hjzk, 2, 1);
						choice.add(row);
					}

					cho = new MutiSelectForm().open(Language.apply("请选择退货商品信息"), title, width, choice);
				}
				thSaleGoods = (SaleGoodsDef) back.elementAt(cho);

				if (thSaleGoods.sl < quantity)
				{
					new MessageBox(Language.apply("该商品退货数量大于原销售数量\n\n不能退货"));
					thSaleGoods = null;
					return false;
				}
			}

			// 如果是指定小票退货，不进行价格确认
			// 如果是电子秤商品且价格确定，不进行价格确认
			if (!isConfirmPrice(isdzcm, dzcprice, goodsDef))
			{
			}
			else
			{
				if (!isonlinegdjging && memo == null)
				{
					StringBuffer pricestr = new StringBuffer();
					do
					{
						pricestr.delete(0, pricestr.length());
						// pricestr.append(price);
						pricestr.append(goodsDef.lsj);

						String strprice = setGoodsLSJ(goodsDef, pricestr);
						if (strprice == null) { return false; }
						price = ManipulatePrecision.doubleConvert(getConvertPrice(Double.parseDouble(strprice), goodsDef), 2, 1);

						if (GlobalInfo.sysPara.isGoodsMoney0 != 'Y' && price <= 0)
						{
							// 检查价格
							if (price <= 0 && goodsDef.type != 'Z')
							{
								new MessageBox(Language.apply("该商品价格必须大于0"));
							}
						}
						else
						{
							// 电子秤商品重新计算
							if (isdzcm && (dzcprice > 0))
							{
								if (dzcprice == 1)
								{
									allprice = quantity * price;
								}
								else
								{
									quantity = ManipulatePrecision.doubleConvert(dzcmjg / price, 4, 1);
								}
							}

							// 是否允许在商品退货时,商品是否在下限和上限的价格之内
							if (!isAllowedBackPriceLimit(goodsDef, price))
								continue;

							break;
						}

					} while (true);
				}
			}
		}

		// 如果是联网挂单状态，则不输入商品附加信息
		if (!isonlinegdjging && !inputGoodsAddInfo(goodsDef))
			return false;

		// 检查找到的商品最后是否OK
		if (!allowFinishFindGoods(goodsDef, quantity, price))
			return false;

		// 增加商品到商品明细中
		if (!addSaleGoods(goodsDef, yyyh, quantity, price, allprice, isdzcm))
			return false;

		return true;

	}

	public boolean findGoods(String code, String yyyh, String gz)
	{
		return findGoods(code, yyyh, gz, null);
	}

	public String setGoodsLSJ(GoodsDef goodsDef, StringBuffer pricestr)
	{
		double min = 0.01;
		if (goodsDef.type == 'Z')
		{
			min = 0;
		}

		if (SellType.ISBATCH(saletype))
		{
			pricestr.delete(0, pricestr.length());
			pricestr.append(goodsDef.pfj);
		}

		boolean done = new TextBox().open(Language.apply("请输入商品[") + goodsDef.inputbarcode + "]" + (goodsDef.name.trim().length() > 20 ? goodsDef.name.trim().substring(0, 19) : goodsDef.name.trim()) + Language.apply("价格"), Language.apply("价格"), "", pricestr, min, getMaxSaleGoodsMoney(), true);
		if (!done)
			return null;
		return pricestr.toString();

	}

	// 是否确认价格
	public boolean isConfirmPrice(boolean isdzcm, double dzcprice, GoodsDef goodsDef)
	{
		if (isSpecifyBack() || (isdzcm && dzcprice > 0) || (!isSpecifyBack() && SellType.ISBACK(saletype) && GlobalInfo.sysPara.setPriceBackStatus == 'N' && goodsDef.lsj > 0)) { return false; }
		return true;
	}

	public boolean verifyDzcmCheckbit(String dzcm)
	{
		if (GlobalInfo.sysPara.verifyDzcmname != null && !GlobalInfo.sysPara.verifyDzcmname.equals("")) { return Dzcm_CheckVerify.verifyDzcmCheckbit(dzcm); }

		return true;
	}

	public double getMinPlsl(double quantity, GoodsDef goodsDef)
	{
		return quantity;
	}

	// 判断是否需要确认价格
	public boolean isPriceConfirm(GoodsDef goodsDef)
	{
		if (((goodsDef.lsj <= 0) && (goodsDef.type != 'P') && (goodsDef.type != 'Z')) || SellType.ISBACK(saletype) || SellType.ISBATCH(saletype) || SellType.ISJDXXFK(saletype))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public double setGoodsDefaultPrice(GoodsDef goodsDef)
	{
		double price = 0;

		// 批发交易默认价格为商品pfj
		if (SellType.ISBATCH(saletype))
		{
			price = goodsDef.pfj;
		}
		else
		{
			price = goodsDef.lsj;
		}

		// 退货交易默认价格
		if (SellType.ISBACK(saletype))
		{
			if (GlobalInfo.sysPara.isbackpricestatus == 'F')
			{
				if (goodsDef.pfj > 0 && ManipulatePrecision.doubleCompare(goodsDef.lsj, goodsDef.pfj, 2) != 0)
				{
					price = goodsDef.pfj;
				}
			}
			else if (GlobalInfo.sysPara.isbackpricestatus == 'N')
			{
				price = 0;
			}
		}

		return price;
	}

	public boolean checkFindGoodsAllowSale(GoodsDef goodsDef, double quantity, boolean isdzcm, double dzcmsl, double dzcmjg)
	{
		// 检查电子秤码,'Y'秤重的电子秤商品,'O'非秤重的电子秤商品;非秤重的电子秤商品允许输入数量
		if (isdzcm)
		{
			if ((goodsDef.isdzc != 'Y') && (goodsDef.isdzc != 'O'))
			{
				new MessageBox(Language.apply("该商品不是电子秤商品\n不能用电子秤码销售"));

				return false;
			}

			if ((dzcmsl > 0) && (dzcmjg < 0) && (goodsDef.lsj <= 0))
			{
				new MessageBox(Language.apply("该电子秤商品未定价,不能销售"));

				return false;
			}

			if ((goodsDef.isdzc == 'Y') && (dzcmsl > 0 && ManipulatePrecision.doubleCompare(ManipulatePrecision.doubleConvert(dzcmsl / goodsDef.bzhl, 4, 1), quantity, 4) != 0))
			{
				new MessageBox(Language.apply("电子秤条码不允许输入数量"));

				return false;
			}
		}
		else
		{
			if ((goodsDef.isdzc == 'Y'))
			{
				new MessageBox(Language.apply("该商品是电子秤商品\n不能直接销售"));

				return false;
			}
		}

		// 以旧换新码处理
		if (goodsDef.type == '8')
		{
			if (!checkOldExChangeNew(goodsDef))
			{
				new MessageBox(Language.apply("请先输入以旧换新码对应的新品编码"));

				return false;
			}
		}

		// 子母商品销售
		if (goodsDef.type == '6')
		{
			new MessageBox(Language.apply("母商品不能直接销售，请选择相应的子商品销售!"));
			return false;
		}

		// 特卖码商品是否允许销售
		if (goodsDef.type == 'T' && goodsDef.iszs != 'Y' && SellType.ISSALE(saletype))
		{
			new MessageBox(Language.apply("特卖码未生效或已过期,不能销售！"));
			return false;
		}

		if (GlobalInfo.sysPara.isEARNESTZT == 'N' && SellType.ISEARNEST(saletype) && goodsDef.iszt != 'Y')
		{
			new MessageBox(Language.apply("该商品不能进行") + SellType.getDefault().typeExchange(saletype, saleHead.hhflag, saleHead));
			return false;
		}

		// 不允许销红,检查库存
		if ((SellType.ISSALE(saletype) && GlobalInfo.sysPara.isxh != 'Y' && goodsDef.isxh != 'Y'))
		{
			// 统计商品销售数量
			double hjsl = ManipulatePrecision.mul(quantity, goodsDef.bzhl) + calcSameGoodsQuantity(goodsDef);
			if (goodsDef.kcsl < hjsl)
			{
				if (GlobalInfo.sysPara.xhisshowsl == 'Y')
					new MessageBox(Language.apply("该商品库存为{0}\n库存不足,不能销售", new Object[] { ManipulatePrecision.doubleToString(goodsDef.kcsl) }));
				else
					new MessageBox(Language.apply("该商品库存不足,不能销售"));

				return false;
			}
		}

		// T代表此商品已经被停用
		if (SellType.ISSALE(saletype) && goodsDef.iszs == 'T')
		{
			new MessageBox(Language.apply("当前商品已停售,不能销只能退！"));
			return false;
		}

		return true;
	}

	public boolean inputGoodsAddInfo(GoodsDef goodsDef)
	{
		// 输入商品批号,加入商品明细时检查是否加入批号了
		if (goodsDef.isbatch == 'Y' || goodsDef.isbatch == 'A')
		{
			// A模式商品继承上一个商品的批号,不清除批号
			if (goodsDef.isbatch == 'Y')
				curBatch = "";
			if (goodsDef.isbatch == 'A' && saleGoods.size() <= 0)
				curBatch = "";

			// 输入批号
			if (curBatch == null || curBatch.equals(""))
			{
				StringBuffer bstr = new StringBuffer();
				if (new TextBox().open(Language.apply("请输入商品批号"), Language.apply("批号"), Language.apply("该商品要求输入商品批号"), bstr, -1, -1, false, TextBox.AllInput))
				{
					curBatch = bstr.toString();
				}
				bstr = null;
			}
		}
		else
		{
			curBatch = "";
		}

		// 批发销售输入折扣分担
		curPfzkfd = 1;
		if (SellType.ISBATCH(saletype))
		{
			// 隐藏状态，默认为参数设置值
			if (GlobalInfo.sysPara.bacthfdisvisible.equals("Y"))
			{
				curPfzkfd = ManipulatePrecision.doubleConvert(GlobalInfo.sysPara.batchfdrate / 100, 4, 1);
				return true;
			}

			StringBuffer bstr = new StringBuffer();
			if (GlobalInfo.sysPara.batchfdrate < 1)
				bstr.append(GlobalInfo.sysPara.batchfdrate * 100);
			else
				bstr.append(GlobalInfo.sysPara.batchfdrate);
			
			if (!new TextBox().open(Language.apply("请输入商品的商家折扣分担(%):"), Language.apply("折扣分担"), Language.apply("请输入百分比,不能大于100,也不能小于0"), bstr, 0, 100, true))
			{
				new MessageBox(Language.apply("未输入商品商家折扣分担\n\n商品查询失败！"));
				return false;
			}
			curPfzkfd = ManipulatePrecision.doubleConvert(Double.parseDouble(bstr.toString()) / 100, 4, 1);
			bstr = null;
		}

		return true;
	}

	public boolean allowFinishFindGoods(GoodsDef goodsDef, double quantity, double price)
	{
		// 检查销售金额是否过大
		if (ManipulatePrecision.doubleCompare(saleHead.ysje + (quantity * price), getMaxSaleMoney(), 2) > 0)
		{
			new MessageBox(Language.apply("输入商品后销售金额已达到上限\n请重新输入或先付款"));

			return false;
		}

		// 退货数量过大
		if (SellType.ISBACK(saletype) && curGrant.thxe > 0 && ManipulatePrecision.doubleCompare(saleHead.ysje + (quantity * price), curGrant.thxe, 2) > 0)
		{
			OperUserDef staff = backSellGrant();
			if (staff == null)
				return false;

			//
			if (curGrant.thxe > 0 && ManipulatePrecision.doubleCompare(saleHead.ysje + (quantity * price), staff.thxe, 2) > 0)
			{
				new MessageBox(Language.apply("超出退货的最大限额，不能退货"));
				return false;
			}

			// 记录日志
			curGrant.thxe = staff.thxe;
			String log = "授权退货,小票号:" + saleHead.fphm + ",最大退货限额:" + curGrant.thxe + ",授权:" + staff.gh;
			AccessDayDB.getDefault().writeWorkLog(log);

			//
			new MessageBox(Language.apply("授权退货,限额为{0}元", new Object[] { ManipulatePrecision.doubleToString(curGrant.thxe) }));
		}

		// 检查是否移动充值商品
		if (GlobalInfo.useMobileCharge && SellType.ISSALE(saletype))
		{
			MemoInfoDef mobcharge = AccessLocalDB.getDefault().checkMobileCharge(goodsDef.barcode);
			if (mobcharge != null)
			{
				if (saleGoods.size() > 0 && AccessLocalDB.getDefault().checkMobileCharge(((SaleGoodsDef) saleGoods.elementAt(0)).barcode) == null)
				{
					new MessageBox(Language.apply("本交易已销售正常商品\n\n不能再销售移动充值商品"));
					return false;
				}

				// 调用充值付款输入充值手机号,商品的batch记录充值手机号
				PaymentBankCMCC pay = CreatePayment.getDefault().getPaymentMobileCharge(this.saleEvent.saleBS);
				if (pay != null)
				{
					// memo = 2离线充值,否在线充值要求输入手机号,再根据batch区别是在线还是离线来
					if (mobcharge.memo.equals("2"))
						curBatch = "";
					else
					{
						curBatch = pay.inputChargePhone(ManipulatePrecision.doubleConvert(quantity * price));
						if (curBatch == null || curBatch.trim().length() <= 0)
							return false;
					}
				}
				else
				{
					new MessageBox(Language.apply("没有移动充值付款方式\n\n无法进行移动充值的功能!"));
					return false;
				}
			}
			else
			{
				if (saleGoods.size() > 0 && AccessLocalDB.getDefault().checkMobileCharge(((SaleGoodsDef) saleGoods.elementAt(0)).barcode) != null)
				{
					new MessageBox(Language.apply("本交易已经销售移动充值商品\n\n不能再销售正常商品"));
					return false;
				}
			}
		}

		return true;
	}

	public OperUserDef backSellGrant()
	{
		OperUserDef staff = DataService.getDefault().personGrant();
		if (staff == null)
			return null;
		if (staff.privth != 'Y' && staff.privth != 'T')
		{
			new MessageBox(Language.apply("该员工授权卡无法授权退货"));
			return null;
		}

		return staff;
	}

	public boolean analyzeBarcode(String barcode, String[] key)
	{
		String code = null;
		String price = null;
		String quantity = null;
		String scsj = null;

		int i;
		int j;

		try
		{
			DzcModeDef dzc = null;

			for (i = 0; i < GlobalInfo.dzcMode.size(); i++)
			{
				dzc = (DzcModeDef) GlobalInfo.dzcMode.elementAt(i);

				int location = dzc.symbolpos - 1;

				if ((barcode.trim().length() == dzc.length) && (barcode.substring(location, location + dzc.symbollen).trim().equals(dzc.symbol.trim())))
				{
					break;
				}
			}

			if (i >= GlobalInfo.dzcMode.size())
				return false;

			// 截取编码
			code = barcode.substring(dzc.codepos - 1, (dzc.codepos + dzc.codelen) - 1);

			// 截取价格
			if (dzc.pricelen > 0)
			{
				j = dzc.pricelen - dzc.pricedec;

				StringBuffer sb = new StringBuffer();
				sb.append(barcode.substring(dzc.pricepos - 1, (dzc.pricepos + j) - 1));
				sb.append(".");
				sb.append(barcode.substring((dzc.pricepos + j) - 1, (dzc.pricepos + dzc.pricelen) - 1));
				price = sb.toString();
			}
			else
			{
				price = "0";
			}

			// 截取数量
			if (dzc.quantitylen > 0)
			{
				j = dzc.quantitylen - dzc.quantitydec;

				StringBuffer sb = new StringBuffer();
				sb.append(barcode.substring(dzc.quantitypos - 1, (dzc.quantitypos + j) - 1));
				sb.append(".");
				sb.append(barcode.substring((dzc.quantitypos + j) - 1, (dzc.quantitypos + dzc.quantitylen) - 1));
				quantity = sb.toString();
			}
			else
			{
				quantity = "0";
			}

			// 截取生产时间
			if (dzc.timelen > 0)
			{
				scsj = barcode.substring(dzc.timepos - 1, (dzc.timepos + dzc.timelen) - 1);
			}
			else
			{
				scsj = "";
			}

			// 返回
			key[0] = code;
			key[1] = price;
			key[2] = quantity;
			key[3] = scsj;

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public boolean checkOldExChangeNew(GoodsDef goodsDef)
	{
		if (saleGoods.size() <= 0)
			return false;

		// 以旧换新码有定价则必须对应新品编码，不定价则必须柜组匹配
		SaleGoodsDef lastGoodsDef = (SaleGoodsDef) saleGoods.lastElement();
		if ((goodsDef.lsj > 0 && !(goodsDef.gz.equals(lastGoodsDef.gz) && goodsDef.fxm.equals(lastGoodsDef.code))) || (goodsDef.lsj <= 0 && !(goodsDef.gz.equals(lastGoodsDef.gz)))) { return false; }

		// 以旧换新商品的fxm存放对应的新商品编码
		goodsDef.fxm = lastGoodsDef.code;

		return true;
	}

	// 多单位商品查询
	public GoodsDef getMutiUnitChoice(GoodsDef goodsDef)
	{
		Vector mutiUnit = new Vector();

		if (DataService.getDefault().getGoodsMutiUnit(mutiUnit, goodsDef.code) && mutiUnit.size() > 0)
		{
			String[] title = { Language.apply("商品条码"), Language.apply("单位"), Language.apply("包装含量") };
			int[] width = { 200, 100, 100 };
			String[] content = null;
			Vector contents = new Vector();
			for (int i = 0; i < mutiUnit.size(); i++)
			{
				GoodsUnitsDef unitDef = (GoodsUnitsDef) mutiUnit.elementAt(i);
				content = new String[3];
				content[0] = unitDef.barcode;
				content[1] = unitDef.unit;
				content[2] = ManipulatePrecision.doubleToString(unitDef.bzhl, 4, 1);
				contents.add(content);
			}

			int choice = -1;
			if (contents.size() <= 1 || (contents.size() > 1 && (choice = new MutiSelectForm().open(Language.apply("此商品存在多单位，请确定单位"), title, width, contents)) >= 0))
			{
				if (choice < 0)
					choice = 0;
				GoodsUnitsDef unitDef = (GoodsUnitsDef) mutiUnit.elementAt(choice);

				int searchFlag = 2;
				String yhsj = saleHead.rqsj;
				String scsj = "";
				if (saleHead.jdfhdd != null && saleHead.jdfhdd.compareTo("") != 0)
				{
					searchFlag = 9;
					scsj = saleHead.jdfhdd;
				}

				GoodsDef newGoods = new GoodsDef();

				int result = DataService.getDefault().getGoodsDef(newGoods, searchFlag, unitDef.barcode, goodsDef.gz, scsj, yhsj, saletype);
				if (result == 0)
					return newGoods;
				else
					return null;
			}
			else
			{
				new MessageBox(Language.apply("此商品存在多单位，但未选定多单位信息，不能销售"));
				return null;
			}
		}
		else
		{
			new MessageBox(Language.apply("此商品存在多单位，但多单位信息未找到，不能销售"));
			return null;
		}
	}

	public GoodsDef getSubGoodsDef(GoodsDef goodsDef)
	{
		Vector subGoods = new Vector();
		if (DataService.getDefault().getSubGoodsDef(subGoods, goodsDef.code, goodsDef.gz, 'C') && subGoods.size() > 0)
		{
			String[] title = { Language.apply("子商品条码"), Language.apply("子商品名称") };
			int[] width = { 200, 300 };
			int choice = -1;
			if ((choice = new MutiSelectForm().open(Language.apply("请选择母商品下属子商品进行销售"), title, width, subGoods)) >= 0)
			{
				// 对应子商品的条码
				String barcode = ((String[]) subGoods.elementAt(choice))[0];

				// 重新查找子商品，退货、批发时不查找优惠
				int searchFlag = 2;
				String yhsj = saleHead.rqsj;
				if (!SellType.ISSALE(saletype) || SellType.ISBATCH(saletype))
					yhsj = "";
				String scsj = "";
				if (saleHead.jdfhdd != null && saleHead.jdfhdd.compareTo("") != 0)
				{
					searchFlag = 9;
					scsj = saleHead.jdfhdd;
				}

				// 按子商品条码重新查找商品信息
				GoodsDef newGoods = new GoodsDef();
				int result = DataService.getDefault().getGoodsDef(newGoods, searchFlag, barcode, goodsDef.gz, scsj, yhsj, saletype);
				if (result == 0)
					return newGoods;
				else
					return null;
			}
		}

		//
		new MessageBox(Language.apply("此商品为母商品，必须选择相应子商品进行销售"));
		return null;
	}

	// 是否允许在商品退货时,商品是否在下限和上限的价格之内
	public boolean isAllowedBackPriceLimit(GoodsDef goodsDef, double newjg)
	{
		if (goodsDef != null && SellType.ISBACK(saletype) && goodsDef.lsj > 0)
		{
			if (goodsDef != null && GlobalInfo.sysPara.backgoodsminmoney > 0 && ManipulatePrecision.mul(goodsDef.lsj, GlobalInfo.sysPara.backgoodsminmoney) > newjg)
			{
				new MessageBox(Language.apply("该商品价格不能小于退货下限价: ") + ManipulatePrecision.doubleToString(ManipulatePrecision.mul(goodsDef.lsj, GlobalInfo.sysPara.backgoodsminmoney)));
				return false;
			}

			if (goodsDef != null && GlobalInfo.sysPara.backgoodsmaxmoney > 0 && ManipulatePrecision.mul(goodsDef.lsj, GlobalInfo.sysPara.backgoodsmaxmoney) < newjg)
			{
				new MessageBox(Language.apply("该商品价格不能大于退货上限价: ") + ManipulatePrecision.doubleToString(ManipulatePrecision.mul(goodsDef.lsj, GlobalInfo.sysPara.backgoodsmaxmoney)));
				return false;
			}
		}

		return true;
	}

	// 在原盘点单上新增商品时 根据汇总参数返回正确的操作标志
	public String[] getCheckEditType(SaleGoodsDef sg)
	{
		String editflag = "A";
		String editname = Language.apply("[新增]");
		// 合并的情况 查找是否存在想通编码的商品 如果存在 操作类型为修改
		if (GlobalInfo.sysPara.ischeckadditive == 'N' || GlobalInfo.sysPara.ischeckadditive == 'Y' || GlobalInfo.sysPara.ischeckadditive == 'A')
		{
			String str8 = "";
			String barcode = sg.barcode;
			SaleGoodsDef sg1 = null;
			boolean haveSameGoods = false;
			for (int i = 0; i < saleGoods.size(); i++)
			{
				sg1 = (SaleGoodsDef) saleGoods.elementAt(i);
				if (barcode.equals(sg1.barcode))
					;
				{
					if (sg1.str8 != null)
						str8 = sg1.str8;
					haveSameGoods = true;
					break;
				}
			}

			// 已存在该商品，进行合并
			if (haveSameGoods && !"A".equals(str8))
			{
				editflag = "U";
				editname = Language.apply("[修改]");
			}
			// 不存在该商品
			else
			{
				editname = "";
			}
		}
		return new String[] { editflag, editname };

	}

	// 判断是否取回原单的盘点方式
	public boolean isSpecifyCheckInput()
	{
		if (GlobalInfo.sysPara.isblankcheckgoods == 'B' && "Y".equals(checkeditflag))
			return true;
		return false;
	}

	public boolean getGoodsOrCatePages(Vector listgoods, boolean searchflag, long startpos, long endpos, long cateid, int level)
	{
		return DataService.getDefault().getGoodsOrCatePages(listgoods, searchflag, startpos, endpos, cateid, level);
	}

	public long getGoodsOrCateMaxCount(boolean searchflag, long cateid, int level)
	{
		return DataService.getDefault().getGoodsOrCateMaxCount(searchflag, cateid, level);
	}

	// 获取买积分信息
	public boolean findJf(String code, String yyyh, String gz)
	{
		if (!checkMemberSale() || (curCustomer == null))
		{
			new MessageBox(Language.apply("未刷会员卡，无法购买积分"));
			return false;
		}

		double quantity = 1;
		String barcode = "";
		double price = 0;

		// 检查是否允许找商品
		if (!allowStartFindGoods())
			return false;

		// 分解输入码 数量*编码
		String[] s = convertQuantityBarcode(code);
		if (s == null)
			return false;
		quantity = Convert.toDouble(s[0]);
		barcode = s[1];

		// 找券
		Vector v = NetService.getDefault().findJf(barcode, saletype);
		if (v == null || v.size() <= 0)
		{
			new MessageBox(Language.apply("此积分规则号无效"));
			return false;
		}

		String[] row = null;

		if (GlobalInfo.sysPara.couponSaleType.equals("A"))
		{
			if (v.size() > 1)
			{
				int choice = 0;
				if ((choice = new MutiSelectForm().open(Language.apply("请输入积分规则号"), new String[] { Language.apply("积分规则号"), Language.apply("积分规则描述"), Language.apply("金额") }, new int[] { 250, 210, 100 }, v, true)) < 0) { return false; }

				row = (String[]) v.elementAt(choice);
			}
			else
			{
				row = (String[]) v.elementAt(0);
			}

			if (Convert.toDouble(row[2]) <= 0)
			{
				StringBuffer pricestr = new StringBuffer();
				boolean done = new TextBox().open(Language.apply("请输入商品价格"), Language.apply("价格"), "", pricestr, 0.01, getMaxSaleGoodsMoney(), true);
				if (!done) { return false; }
				price = Convert.toDouble(pricestr);
			}
			else
			{
				price = Convert.toDouble(row[2]);
			}
		}
		else if (GlobalInfo.sysPara.couponSaleType.equals("B"))
		{
			if (v.size() > 1)
			{
				int choice = 0;
				if ((choice = new MutiSelectForm().open(Language.apply("请输入积分规则号"), new String[] { Language.apply("积分规则号"), Language.apply("积分规则描述"), Language.apply("兑换比例") }, new int[] { 250, 210, 100 }, v, true)) < 0) { return false; }

				row = (String[]) v.elementAt(choice);
			}
			else
			{
				row = (String[]) v.elementAt(0);
			}

			StringBuffer pricestr = new StringBuffer();
			boolean done = new TextBox().open(Language.apply("请输入积分金额"), Language.apply("金额"), "", pricestr, 0.01, getMaxSaleGoodsMoney(), true);
			if (!done) { return false; }
			price = Convert.toDouble(pricestr);
			row[1] = pricestr + "元" + row[1];
			price = ManipulatePrecision.doubleConvert(price / Convert.toDouble(row[2]));

		}
		String[] temp1 = row[0].split("@");

		GoodsDef goods = new GoodsDef();
		goods.barcode = temp1[0];
		goods.code = temp1[0];
		goods.name = row[1]; // 名称
		goods.lsj = price;
		// 记录兑换比例
		if (GlobalInfo.sysPara.couponSaleType.equals("B"))
			goods.attr08 = row[2];

		SaleGoodsDef saleGoodsDef = new SaleGoodsDef();
		saleGoodsDef.syjh = saleHead.syjh; // 收银机号,主键
		saleGoodsDef.fphm = saleHead.fphm; // 小票号,主键
		saleGoodsDef.rowno = saleGoods.size() + 1; // 行号,主键
		saleGoodsDef.yyyh = yyyh; // 营业员
		saleGoodsDef.fph = curyyyfph; // 营业员手工单发票号
		saleGoodsDef.barcode = temp1[0]; // 商品条码
		saleGoodsDef.code = temp1[0]; // 商品编码
		saleGoodsDef.type = '1'; // 编码类别
		saleGoodsDef.gz = temp1[0]; // 商品柜组
		saleGoodsDef.catid = temp1[1]; // 商品品类
		// saleGoodsDef.ppcode = goodsDef.ppcode; // 商品品牌
		// saleGoodsDef.uid = goodsDef.uid; // 多单位码
		// saleGoodsDef.batch = curBatch; // 批号
		// saleGoodsDef.yhdjbh = goodsDef.popdjbh; // 优惠单据编号
		saleGoodsDef.name = row[1]; // 名称
		saleGoodsDef.unit = "张"; // 单位
		// saleGoodsDef.bzhl = goodsDef.bzhl; // 包装含量
		saleGoodsDef.sl = ManipulatePrecision.doubleConvert(quantity, 4, 1); // 销售数量

		if (GlobalInfo.sysPara.couponSaleType.equals("A"))
			saleGoodsDef.lsj = Convert.toDouble(row[2]); // 零售价
		else if (GlobalInfo.sysPara.couponSaleType.equals("B"))
			saleGoodsDef.lsj = price; // 零售价

		saleGoodsDef.jg = ManipulatePrecision.doubleConvert(price, 2, 1); // 销售价格

		saleGoodsDef.hjje = ManipulatePrecision.doubleConvert(saleGoodsDef.jg * saleGoodsDef.sl, 2, 1); // 合计金额

		saleGoodsDef.hjzk = 0; // 合计折扣,等于各种折扣之和
		saleGoodsDef.hyzke = 0; // 会员折扣额(来自会员优惠)
		saleGoodsDef.hyzkfd = 0; // 会员折扣分担
		saleGoodsDef.yhzke = 0; // 优惠折扣额(来自营销优惠)
		saleGoodsDef.yhzkfd = 0; // 优惠折扣分担
		saleGoodsDef.lszke = 0; // 零时折扣额(来自手工打折)
		saleGoodsDef.lszre = 0; // 零时折让额(来自手工打折)
		saleGoodsDef.lszzk = 0; // 零时总品折扣
		saleGoodsDef.lszzr = 0; // 零时总品折让
		saleGoodsDef.plzke = 0; // 批量折扣
		saleGoodsDef.zszke = 0; // 赠送折扣
		// saleGoodsDef.lszkfd = curPfzkfd; // 临时折扣分担
		saleGoodsDef.sqkh = ""; // 单品授权卡号
		saleGoodsDef.sqktype = '\0'; // 单品授权卡类别
		saleGoodsDef.sqkzkfd = 0; // 单品授权卡授权折扣分担
		saleGoodsDef.isvipzk = 'Y'; // 是否允许VIP折扣（Y/N）
		// saleGoodsDef.xxtax = goodsDef.xxtax; // 税率
		saleGoodsDef.flag = '4'; // 商品标志，1-赠品,2-电子秤条码，3-削价，4-一般

		saleGoodsDef.yjhxcode = ""; // 以旧换新条码
		saleGoodsDef.ysyjh = ""; // 原收银机号
		saleGoodsDef.yfphm = 0; // 原小票号
		saleGoodsDef.fhdd = ""; // 发货地点
		saleGoodsDef.memo = ""; // 备注
		saleGoodsDef.str1 = ""; // 备用字段
		saleGoodsDef.str2 = ""; // 备用字段
		saleGoodsDef.num1 = 0; // 备用字段
		saleGoodsDef.num2 = 0; // 备用字段

		addSaleGoodsObject(saleGoodsDef, goods, getGoodsSpareInfo(goods, saleGoodsDef));

		// 计算小票应收
		calcHeadYsje();

		return true;
	}
}
