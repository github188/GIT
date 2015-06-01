package custom.localize.Jdhx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.commonKit.TimeDate;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Jdhx_SaleBS1Goods extends Jdhx_SaleBS0Cmpop
{
	public boolean writeHang()
	{
		if (saleEvent.table.getItemCount() <= 0)
		{
			NewKeyListener.sendKey(GlobalVar.readHang);
			return true;
		}

		// 练习交易不允许挂单
		if (SellType.ISEXERCISE(this.saletype))
			return false;

		if (getHangFileCount() + 1 > GlobalInfo.sysPara.gdTimes)
		{
			new MessageBox(Language.apply("当前挂单数已超过系统限定笔数"));
			return false;
		}

		// 检查挂单权限
		if (!writeHangGrant())
			return false;

		if (isRealTimePrint())
		{
			for (int i = 0; i < realTimePrintFlag.length(); i++)
			{
				if (realTimePrintFlag.charAt(i) == 'N')
				{
					SaleBillMode.getDefault().printRealTimeDetail(i);
				}
			}

		}

		FileOutputStream f = null;
		try
		{
			TimeDate date = new TimeDate(GlobalInfo.balanceDate, "00:00:00");
			String path = ConfigClass.LocalDBPath + "Invoice/" + date.cc + date.yy + date.mm + date.dd;

			// 读取挂单最大值
			int maxGD = getHangFileIndex(true);
			if (maxGD < 0)
				return false;

			// 写入挂单文件
			f = new FileOutputStream(path + "//" + gd_Prefix + maxGD);

			ObjectOutputStream s = new ObjectOutputStream(f);

			// 写入交易类型
			s.writeObject(new String(saletype));

			// 写入交易对象
			brokenAssistant.removeAllElements();
			writeSellObjectToStream(s);

			s.flush();
			s.close();
			f.close();
			s = null;
			f = null;

			StringBuffer strnetcode = new StringBuffer();

			if (GlobalInfo.sysPara.onlineGd.equals("Y") || GlobalInfo.sysPara.onlineGd.equals("A"))
			{
				if (!sendHang(maxGD, strnetcode))
				{
					new MessageBox(Language.apply("当前交易挂单失败!"));

					File gdFile = new File(path + "//" + gd_Prefix + maxGD);
					if (gdFile.exists())
					{
						gdFile.delete();
					}
					return false;
				}
			}

			// 记录日志
			AccessDayDB.getDefault().writeWorkLog("收银员进行 " + maxGD + " 号挂单成功,挂单金额: " + ManipulatePrecision.doubleToString(saleHead.ysje));

			// 提示
			StringBuffer info = new StringBuffer();
			info.append(Language.apply("挂 单 号: ") + Convert.appendStringSize("", String.valueOf(maxGD), 1, 20, 20, 0) + "\n");
			info.append(Language.apply("挂单金额: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(saleHead.ysje) + Language.apply(" 元"), 1, 20, 20, 0) + "\n");
			info.append(Language.apply("挂单时间: ") + Convert.appendStringSize("", ManipulateDateTime.getCurrentDateTime(), 1, 20, 20, 0) + "\n");
			info.append(Language.apply("收银机号: ") + Convert.appendStringSize("", ConfigClass.CashRegisterCode, 1, 20, 20, 0) + "\n");
			info.append(Language.apply("收银员号: ") + Convert.appendStringSize("", GlobalInfo.posLogin.gh, 1, 20, 20, 0) + "\n");
			if (strnetcode.length() > 0)
				info.append(Language.apply("网络挂单: ") + Convert.appendStringSize("", strnetcode.substring(0), 1, 20, 20, 0) + "\n");

			new MessageBox(info.toString());

			if (GlobalInfo.sysPara.isPrintGd.trim().equals("Y") || GlobalInfo.sysPara.isPrintGd.trim().equals("A"))
			{
				// 由于要打印挂单，所以在即扫即打时先把已打印部分做打印放弃
				realTimePrintCancelSale();
				// 打印
				printHang(maxGD);

			}

			// 开始新交易
			saleEvent.initOneSale(this.saletype);

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			new MessageBox(Language.apply("当前交易挂单失败!\n\n") + e.getMessage().trim());

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

	public boolean allowEditGoods()
	{
		if (SellType.isJF(saletype))
		{
			new MessageBox("缴费交易不允许修改!");
			return false;
		}

		return super.allowEditGoods();
	}

	public boolean findGroupBuyInfo(String billNo)
	{
		//1、单据类别使用a和b，a为团购销售单；b为团购退货单；
		//2、团购单号需要填写到salehead.hykh中。

		Vector thsaleGoods = null;
		try
		{
			thsaleGoods = new Vector();
			// 联网查询原小票信息
			ProgressBox pb = new ProgressBox();
			pb.setText(Language.apply("开始查找团购小票操作....."));
			if (!((Jdhx_NetService) NetService.getDefault()).getGroupBuyInfo(billNo, saletype, thsaleGoods))
			{
				pb.close();
				pb = null;
				return false;
			}
			pb.close();
			pb = null;

			// 生成退货商品明细
			saleGoods.clear();
			lastGoodsDetail.clear();
			saleHead.str3 = billNo;
			for (int i = 0; i < thsaleGoods.size(); i++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) thsaleGoods.get(i);

				sgd.yfphm = sgd.fphm;
				sgd.ysyjh = sgd.syjh;
				sgd.syjh = ConfigClass.CashRegisterCode;
				sgd.fphm = GlobalInfo.syjStatus.fphm;
				sgd.rowno = saleGoods.size() + 1;
				sgd.type = 1;
				sgd.flag = '4';
				
				saleHead.hykh = sgd.memo; //保存团购单号
				
				sgd.lszke =ManipulatePrecision.doubleConvert( (sgd.lsj-sgd.jg)*sgd.sl,2,1);
				sgd.hjje+=sgd.lszke;
				saleGoods.add(sgd);
				
				getZZK(sgd);
				
			}

			// 查找原交易会员卡资料
			if (saleHead.hykh != null && !saleHead.hykh.trim().equals(""))
			{
				curCustomer = new CustomerDef();
				curCustomer.code = saleHead.hykh;
				curCustomer.name = saleHead.hykh;
				curCustomer.ishy = 'Y';
			}

			
			// 计算小票应收
			calcHeadYsje();
			
			double je  = 0;
//			double je  = 1440;
			Payment pay = null;
			if (je > 0)// 赊账付款，编码固定为0601
			{
				PayModeDef mode = DataService.getDefault().searchPayMode("0601");
				pay = CreatePayment.getDefault().createPaymentByPayMode(mode, this);
			}

			if (pay != null )
			{
				pay.inputPay(String.valueOf(je));
				memoPayment.add(pay);
			}
			
		
			// 刷新界面显示
			saleEvent.clearTableItem();
			saleEvent.updateSaleGUI();

			return true;

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (thsaleGoods != null)
			{
				thsaleGoods.clear();
				thsaleGoods = null;
			}
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
				new MessageBox("电子秤码校验位错误", null, false);

				return false;
			}

			if (dzcmjg <= 0 && dzcmsl <= 0)
			{
				new MessageBox("该电子秤格式条码无效", null, false);

				return false;
			}
		}
		else
		{
			comcode = barcode;
		}

		// 查找详细商品资料,可支持数量转换
		StringBuffer slbuf = new StringBuffer("1");

		GoodsDef goodsDef = findGoodsInfo(comcode, yyyh, gz, dzcmscsj, isdzcm, slbuf);
		if (goodsDef == null)
			return false;
		quantity *= Convert.toDouble(slbuf.toString());

		// 获得最小批量数量
		quantity = getMinPlsl(quantity, goodsDef);

		// 电子秤商品记录原始电子秤码
		goodsDef.inputbarcode = barcode;
		if (isdzcm)
			goodsDef.barcode = convertDzcmBarcode(goodsDef, barcode, isdzcm);

		// 设置商品缺省售价
		price = setGoodsDefaultPrice(goodsDef);

		// 当电子称价格因子为1时，表示商品数量按个卖，忽略掉条码上的重量
		if (GlobalInfo.sysPara.enabledzcCoefficient == 'Y')
		{
			if (Convert.toDouble(goodsDef.str1) == 1)
			{
				String dscsl = String.valueOf(dzcmsl);
				dscsl = dscsl.substring(dscsl.indexOf(".") + 1);

				if (Convert.toInt(dscsl) > 0)
				{
					new MessageBox("电子称数量不合法\n价格因子为1的商品,数量不允许出现小数");
					return false;
				}
			}
		}

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

					boolean done = new TextBox().open("请输入商品[" + goodsDef.inputbarcode + "]" + (goodsDef.name.trim().length() > 20 ? goodsDef.name.trim().substring(0, 19) : goodsDef.name.trim()) + "价格", "价格", "", pricestr, 0.01, getMaxSaleGoodsMoney(), true);

					if (!done)
					{
						return false;
					}
					else
					{
						dzcmjg = ManipulatePrecision.doubleConvert(getConvertPrice(Double.parseDouble(pricestr.toString()), goodsDef), 2, 1);

						if (dzcmjg <= 0)
						{
							new MessageBox("该商品价格必须大于0");
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

					boolean done = new TextBox().open("请输入商品[" + goodsDef.inputbarcode + "]" + (goodsDef.name.trim().length() > 20 ? goodsDef.name.trim().substring(0, 19) : goodsDef.name.trim()) + "数量", "数量", "", slstr, 0.01, getMaxSaleGoodsQuantity(), true);

					if (!done)
					{
						return false;
					}
					else
					{
						dzcmsl = Double.parseDouble(slstr.toString());

						if (dzcmsl <= 0)
						{
							new MessageBox("该商品数量必须大于0");
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

						if (SellType.ISBACK(saletype))
						{
							dzcmjgzk = allprice - ManipulatePrecision.doubleConvert(quantity * goodsDef.pfj);
						}
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
				
				//当两个数 按某个精确度比较时，取出两个数中精确度较大的那个精确度，然后按较大的精确度进行比较
				//否则，当   1.00 和 0.78， 按 1.00 的精确度 0 比较时，比较结果是两个数 是相等的。
				double je = goodsDef.lsj * quantity;
				int scale1 = ManipulatePrecision.getDoubleScale(je);
				int scale2 = ManipulatePrecision.getDoubleScale(allprice);
				int maxScale = scale1 >= scale2 ? scale1: scale2;
				
				if (goodsDef.lsj > 0 && ManipulatePrecision.doubleCompare(je, allprice, maxScale) == 0)
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
					String[] title = { "商品编码", "数量", "单价", "合计折扣", "应付金额" };
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

					cho = new MutiSelectForm().open("请选择退货商品信息", title, width, choice);
				}
				thSaleGoods = (SaleGoodsDef) back.elementAt(cho);

				if (thSaleGoods.sl < quantity)
				{
					new MessageBox("该商品退货数量大于原销售数量\n\n不能退货");
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
								new MessageBox("该商品价格必须大于0");
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

}
