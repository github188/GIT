package custom.localize.Nbbh;

import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PosTable;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.PrintTemplate.CheckGoodsMode;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.CustFilterDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Cmls.Cmls_SaleBS;

public class Nbbh_SaleBS extends Cmls_SaleBS
{
	public boolean baseApportion(SalePayDef spay, Payment payobj)
	{
		if (SellType.ISSALE(saleHead.djlb))
		{
			// 受限的MZK
			if (CreatePayment.getDefault().isPaymentMzk(spay.paycode))
			{
				String mzkretstr2 = ((Nbbh_PaymentMzk) payobj).mzkret.str2;
				
				if (mzkretstr2 != null && mzkretstr2.length() > 0 && mzkretstr2.split(",").length==saleGoods.size())
				{
					String[] goodsList = mzkretstr2.split(",");
					Vector v = new Vector();
					double hjje = 0;

					// 查询出所有能分摊的商品
					
					for (int i = 0; i < saleGoods.size(); i++)
					{
						SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);
						SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(i);

						if (goodsList[i].trim().equalsIgnoreCase("0"))//(((PaymentMzk) payobj).mzkret.str2.indexOf(sg.gz) >= 0)
						{
							if (spinfo == null) continue;

							// 计算商品可收付款的金额 = 成交价 - 已分摊的付款
							double ftje = getftje(spinfo);

							double maxfdje = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) - ftje);

							hjje += maxfdje;

							if (maxfdje > 0)
							{
								String[] row = {
												sg.barcode,
												sg.name,
												ManipulatePrecision.doubleToString(ftje),
												ManipulatePrecision.doubleToString(maxfdje),
												"",
												String.valueOf(i) };
								v.add(row);
							}
						}
					}

					// 计算损益
					if (ManipulatePrecision.doubleConvert(spay.je - spay.num1) > hjje)
					{
						spay.num1 = ManipulatePrecision.doubleConvert(spay.num1 + (ManipulatePrecision.doubleConvert(spay.je - spay.num1) - hjje));
					}
					// 开始分摊
					double syje = ManipulatePrecision.doubleConvert(spay.je - spay.num1); // 剩余金额

					if (GlobalInfo.sysPara.apportMode == 'C')
					{
						for (int i = v.size() - 1; i >= 0; i--)
						{
							String row[] = (String[]) v.elementAt(i);

							double je = 0;

							if (syje > Convert.toDouble(row[row.length - 3]))
							{
								je = Convert.toDouble(row[row.length - 3]);
							}
							else
							{
								je = syje;
							}
							row[row.length - 2] = String.valueOf(je);

							syje = ManipulatePrecision.doubleConvert(syje - je);

							if (syje <= 0) break;
						}
					}
					else
					{
						for (int i = 0; i < v.size(); i++)
						{
							String row[] = (String[]) v.elementAt(i);

							if (i == (v.size() - 1))
							{
								row[row.length - 2] = String.valueOf(syje);
								continue;
							}

							double je = ManipulatePrecision
															.doubleConvert((Convert.toDouble(row[row.length - 3]) / hjje * ManipulatePrecision
																																				.doubleConvert(spay.je
																																						- spay.num1)));
							row[row.length - 2] = String.valueOf(je);

							syje = ManipulatePrecision.doubleConvert(syje - je);
						}
					}

					// 记录商品分摊金额
					for (int i = 0; i < v.size(); i++)
					{
						String[] row = (String[]) v.elementAt(i);
						if (row[row.length - 2].length() <= 0) continue;

						// 按商品记录对应的付款分摊
						// 付款方式唯一序号,付款代码,付款名称(主要判断A/B券),分摊金额
						SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(Integer.parseInt(row[row.length - 1]));
						if (info.payft == null) info.payft = new Vector();
						String[] ft = new String[] { String.valueOf(spay.num5), spay.paycode, spay.payname, row[row.length - 2] };
						info.payft.add(ft);
					}
				}
			}

			// 积分消费,按金额平摊
			if (spay.paycode.equals("0508"))
			{
				// 计算可收券的商品总金额
				double kfje = 0;
				for (int i = 0; i < saleGoods.size(); i++)
				{
					SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(i);
					SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.get(i);
					if (info.char3 == 'N') continue;
					double je = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - saleGoodsDef.hjzk - getftje(info));
					kfje += je;
				}

				// 计算可收券的商品总金额
				double sy = spay.je;
				int index = -1;
				double maxje = 0;

				double syje = ManipulatePrecision.doubleConvert(spay.je - spay.num1); // 剩余金额

				if (GlobalInfo.sysPara.apportMode == 'C')
				{
					for (int i = saleGoods.size() - 1; i >= 0; i--)
					{
						System.out.println("i = " + i);
						SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(i);
						SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.get(i);
						if (info.char3 == 'N') continue;

						if (info.payft == null) info.payft = new Vector();
						double je = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - saleGoodsDef.hjzk - getftje(info));

						if (je > syje)
						{
							je = syje;
						}

						String[] ft = new String[] { String.valueOf(spay.num5), spay.paycode, spay.payname, String.valueOf(je) };

						info.payft.add(ft);

						syje = ManipulatePrecision.doubleConvert(syje - je);

						if (syje <= 0) break;
					}
				}
				else
				{
					for (int i = 0; i < saleGoods.size(); i++)
					{
						SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(i);
						SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.get(i);
						if (info.char3 == 'N') continue;

						if (info.payft == null) info.payft = new Vector();
						double je = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - saleGoodsDef.hjzk - getftje(info));

						if (je > maxje) index = i;

						double jf = ManipulatePrecision.doubleConvert(je / kfje * syje);
						sy = ManipulatePrecision.doubleConvert(sy - jf);

						if (sy < 0)
						{
							jf = ManipulatePrecision.doubleConvert(sy + jf);
							sy = 0;
						}
						String[] ft = new String[] { String.valueOf(spay.num5), spay.paycode, spay.payname, String.valueOf(jf) };
						info.payft.add(ft);
						if (sy == 0)
						{
							break;
						}
					}

					// 
					if (sy > 0)
					{
						SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(index);
						String[] ft = (String[]) info.payft.lastElement();
						ft[3] = ManipulatePrecision.doubleToString(Convert.toDouble(ft[3]) + sy);
					}
				}
			}
		}

		return true;
	}
	
	public SaleGoodsDef goodsDef2SaleGoods(GoodsDef goodsDef, String yyyh, double quantity, double price, double allprice, boolean dzcm)
	{
		SaleGoodsDef sgd = super.goodsDef2SaleGoods(goodsDef, yyyh, quantity, price, allprice, dzcm);

		if (GlobalInfo.sysPara.isUseNewBankZS=='Y' && sgd != null)
		{
			sgd.str6 = goodsDef.str1;//Y表示该商品参与新银行追送
			this.writeLog("code=[" + sgd.code + "],barcode=[" + sgd.barcode + "],str6=[" + sgd.str6 + "]");
		}

		return sgd;
	}
	
	public boolean payAccount(PayModeDef mode, String money)
	{
		 return super.payAccount(mode, money);		
	}
	
	public boolean checkPaymodeValid(PayModeDef mode, String money)
	{
		if(!super.checkPaymodeValid(mode, money)) return false;
		if (mode.code.equals(GlobalInfo.sysPara.BankZSPaycode.trim()))
		{
			if (!SellType.ISBACK(saletype))
			{
				showMsg(Language.apply("销售小票不允许使用此付款方式[" + mode.code + "]"));
				return false;
			}
				
		}
		return true;
	}
	
	public void addSalePayObject(SalePayDef spay,Payment payobj)
    {		
		super.addSalePayObject(spay, payobj);
		
		//增加新追送付款方式
		addSalePayObject_BankZs(spay, payobj);
    }
	
	public void delSalePayObject(int index)
    {
		double payje_zs = 0;
		String payno_zs = null;//银行卡号
		int tableindex_zs = -1;
		SalePayDef spd = (SalePayDef) salePayment.elementAt(index);
		if(spd!=null && checkIsBankPaycode(spd.paycode))
		{
			spd.memo = spd.str6;//memo字段在撤消时，会被清除，所以暂存为str6
			if(spd.memo!=null && spd.memo.trim().length()>0)
			{
				String[] bankZsInfo = spd.memo.split(",");//追送金额，银行卡号，银行行号，规则单号
		    	if (bankZsInfo != null && bankZsInfo.length >= 4) 
		    	{
		    		payje_zs = ManipulatePrecision.doubleConvert(Convert.toDouble(bankZsInfo[0]));
		    		payno_zs = bankZsInfo[1];
		    		tableindex_zs = getSalePayObjectIndex_BankZs(payje_zs, payno_zs);
		    	}
			}
	    	
		}
		
		super.delSalePayObject(index);
		
		//删除新追送付款方式
		delSalePayObject_BankZs(payje_zs, payno_zs, tableindex_zs);
    }
	
	private void addSalePayObject_BankZs(SalePayDef spay,Payment payobj)
	{
		if (GlobalInfo.sysPara.isUseNewBankZS != 'Y' || !SellType.ISSALE(saletype) || !GlobalInfo.isOnline) return;
		
    	if (!checkIsBankPaycode(spay.paycode)) return;
    	
    	if(spay.memo==null) return;
    	String[] bankZsInfo = spay.memo.split(",");//追送金额，银行卡号，银行行号，规则单号
    	if (bankZsInfo == null || bankZsInfo.length < 4) return;
    	
    	SalePayDef salePay;
    	salePay = new SalePayDef();
		//salePay.rowno = 0;
		salePay.paycode = GlobalInfo.sysPara.BankZSPaycode.trim();
		salePay.ybje = ManipulatePrecision.doubleConvert(Convert.toDouble(bankZsInfo[0]));
		salePay.payno = bankZsInfo[1].trim();
		salePay.memo = bankZsInfo[2].trim() + "," + bankZsInfo[3].trim();
		salePay.flag = '1';
		
		addPayment_BankZs(salePay);
	}
	
	public boolean addPayment_BankZs(SalePayDef salePay)
	{		
		if(salePay==null) return false;
		double payje = salePay.ybje;//yfqk;
		Payment pay = null;
		pay = CreatePayment.getDefault().createPaymentByPayMode(DataService.getDefault().searchPayMode(salePay.paycode), this);

		if (pay != null && payje > 0)
		{
			if(!pay.createSalePayObject(String.valueOf(payje)))
			{
				showMsg("自动添加银行追送付款方式失败！");
				return false;
			}
			
			pay.salepay.paycode = pay.salepay.paycode;
			pay.salepay.payname = pay.salepay.payname;
			pay.salepay.rowno=salePay.rowno;
			pay.salepay.payno=salePay.payno;
			pay.salepay.memo=salePay.memo;
			pay.salepay.str6=salePay.memo;
			pay.salepay.flag=salePay.flag;
			pay.salepay.ybje=salePay.ybje;
			pay.salepay.je=ManipulatePrecision.doubleConvert(salePay.ybje * pay.salepay.hl, 2, 1);
			
			//memoPayment.add(pay1);
			if (pay.salepay != null)
			{
				addSalePayObject(pay.salepay, pay);		 
				this.writeLog("自动添加银行追送付款方式成功 paycode=[" + pay.salepay.paycode + "],payno=[" + pay.salepay.payno + "],memo=[" + pay.salepay.memo + "],ybje=[" + pay.salepay.je + "]");
				return true;
			}
			
		}
		if(pay==null)
		{
			showMsg("失败：付款方式自动添加失败，未找到 [" + salePay.paycode + "] 付款方式");
		}
		else if(payje<=0)
		{
			showMsg("失败：付款方式自动添加失败，[" + salePay.paycode + "] 付款方式金额为" + payje);
		}
		
		return false;
	}
	
	//删除追送的付款方式
	public void delSalePayObject_BankZs(double payje, String payno, int tableindex_zs)
    {
		if (GlobalInfo.sysPara.isUseNewBankZS != 'Y' || !SellType.ISSALE(saletype)) return;
		if (payno==null || payno.trim().length()<=0) return;
		if(salePayment.size()<=0) return;
		
		int index_zs = getSalePayObjectIndex_BankZs(payje, payno);
		if(index_zs>=0) delSalePayObject(index_zs);
		if(tableindex_zs>=0) 
		{
			PosTable table1= this.salePayEvent.getTable1();
			if(table1!=null)
			{
				table1.deleteRow(tableindex_zs);
				table1.assignLast();
			}
			this.writeLog("追送的付款方式删除成功[" + payje + "][" + payno + "]");
		}
    }
	
	private int getSalePayObjectIndex_BankZs(double payje, String payno)
	{
		if (GlobalInfo.sysPara.isUseNewBankZS != 'Y' || !SellType.ISSALE(saletype)) return -1;
		if (payno==null || payno.trim().length()<=0) return -1;
		if(salePayment.size()<=0) return -1;
		
		for (int i = 0; i < salePayment.size(); i++)
		{
			SalePayDef spd = (SalePayDef) salePayment.elementAt(i);
			if(spd!=null && spd.paycode.equalsIgnoreCase(GlobalInfo.sysPara.BankZSPaycode.trim())
					&& spd.payno!=null && spd.payno.equalsIgnoreCase(payno))
			{
				return i;
			}			
		}
		return -1;
	}
		
	private void showMsg(String msg)
	{
		this.writeLog(msg);
		new MessageBox(msg);
	}
	private void writeLog(String strLog)
	{
		PosLog.getLog(this.getClass().getSimpleName()).info(strLog);
	}	 
	
	//检查当前付款方式是否为银行付款方式
	private boolean checkIsBankPaycode(String paycode)
	{
		if (GlobalInfo.sysPara.BankPaycode!=null && GlobalInfo.sysPara.BankPaycode.trim().length()>0)
		{
			String[] paycodeArr = GlobalInfo.sysPara.BankPaycode.split("\\|");
			if (paycodeArr.length>=1)
			{
				for(int i=0; i<=paycodeArr.length-1; i++)
				{
					//if(paycodeArr[i].trim().equalsIgnoreCase(GlobalInfo.sysPara.BankZSPaycode.trim())) continue;//付款方式不能同是为【银行付款方式】和【银行追送付款方式】
					
					if(paycodeArr[i].trim().equalsIgnoreCase(paycode)) return true;
				}
			}
		}
		return false;
	}	
	
	public boolean getCreditCardZK()
	{
		if (!SellType.ISSALE(saletype))
		{
			new MessageBox(Language.apply("只有在消费时才有[追送]功能"));
			return false;
		}

		// 如果发现积分换购以外的付款方式，不允许追送折扣，防止先付的金额大于折扣后的金额
		String[] pay = CreatePayment.getDefault().getCustomPaymentDefine("PaymentCustJfSale");
		if (pay != null)
		{
			for (int j = 0; j < salePayment.size(); j++)
			{
				SalePayDef spd = (SalePayDef) salePayment.elementAt(j);
				boolean done = false;
				for (int i = 1; i < pay.length; i++)
				{
					if (spd.paycode.equals(pay[i]) || spd.paycode.equals("0509"))
					{
						done = true;
						continue;
					}
				}

				if (!done)
				{
					new MessageBox(Language.apply("追送折扣前不能进行付款"));
					return true;
				}
			}
		}

		// 获取联名卡类表
		Vector v = new Vector();
		if (DataService.getDefault().getCreditCardList(v, GlobalInfo.sysPara.mktcode))
		{
			Vector con = new Vector();

			for (int i = 0; i < v.size(); i++)
			{
				CustFilterDef filterDef = (CustFilterDef) v.elementAt(i);
				con.add(new String[] { filterDef.desc });
			}
			String[] title = { Language.apply("银联卡类型") };
			int[] width = { 500 };

			int choice = new MutiSelectForm().open(Language.apply("请选择卡类型"), title, width, con);
			// 没有选择规则不进行计算
			if (choice == -1)
				return true;

			CustFilterDef rule = ((CustFilterDef) v.elementAt(choice));

			// 输入顾客卡号
			TextBox txt = new TextBox();
			StringBuffer cardno = new StringBuffer();
			if (!txt.open(Language.apply("请刷联名卡或顾客卡"), Language.apply("卡号"), Language.apply("请将联名卡或顾客卡从刷卡槽刷入"), cardno, 0, 0, false, TextBox.MsrKeyInput)) { return false; }

			String line1 = txt.Track2;
			if (rule.Trackno == 1)
			{
				line1 = txt.Track1;
			}
			else if (rule.Trackno == 2)
			{
				line1 = txt.Track2;
			}
			else if (rule.Trackno == 3)
			{
				line1 = txt.Track3;
			}
			else
			{
				new MessageBox(Language.apply("解析磁道号设定错误，磁道必须是1-3之间"));
				return true;
			}

			String line2 = "";
			if (rule.Tracklen != null && rule.Tracklen.charAt(0) == '[')
			{
				String flag1 = rule.Tracklen.trim();
				flag1 = flag1.substring(1, flag1.length() - 1);

				if (rule.Trackpos >= 0)
				{
					line2 = line1.substring(rule.Trackpos);
				}
				else if (line1.length() - rule.Trackpos >= 0)
				{
					line2 = line1.substring(line1.length() - rule.Trackpos);
				}
				else
				{
					line2 = line1;
				}

				if (line2.indexOf(flag1) <= 0)
				{
					new MessageBox(Language.apply("无效 【{0}】\n或者配置文件出错，磁道中未找到{1}", new Object[] { rule.desc.trim(), rule.Tracklen }));
					return false;
				}

				line2 = line2.substring(0, line2.indexOf(flag1));
			}
			else
			{
				if (rule.Trackpos >= 0)
				{
					line2 = line1.substring(rule.Trackpos);
				}
				else if (line1.length() - rule.Trackpos >= 0)
				{
					line2 = line1.substring(line1.length() - rule.Trackpos);
				}
				else
				{
					line2 = line1;
				}

				if (Convert.toInt(rule.Tracklen) < line2.length())
					line2 = line2.substring(0, Convert.toInt(rule.Tracklen));
			}

			boolean updateDisplay = false;
			for (int i = 0; i < saleGoods.size(); i++)
			{
				SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
				GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(i);
				SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(i);
				CustFilterDef filter = new CustFilterDef();

				if ((DataService.getDefault()).getCreditCardZK(filter, saleGoodsDef.code, line2, rule.TrackFlag, saleGoodsDef.gz, saleGoodsDef.catid, saleGoodsDef.ppcode, goodsDef.specinfo, saletype))
				{
					// 检查商品的已分摊金额
					double yftje = 0;
					if (spinfo.payft != null)
					{
						for (int j = 0; j < spinfo.payft.size(); j++)
						{
							String[] row = (String[]) spinfo.payft.elementAt(j);
							yftje += Convert.toDouble(row[3]);

						}
					}

					yftje = ManipulatePrecision.doubleConvert(yftje);

					double curje = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - saleGoodsDef.hjzk - yftje + saleGoodsDef.qtzke);
					if (curje > 0 && filter.zkl > 0)
					{
						saleGoodsDef.qtzke = ManipulatePrecision.doubleConvert(curje * (1 - filter.zkl));
						saleGoodsDef.qtzke = getConvertRebate(i, saleGoodsDef.qtzke);
						saleGoodsDef.str15 = filter.str2;//渠道号
						saleGoodsDef.num1 =filter.num1;//折扣限额
						saleGoodsDef.str5 = filter.desc;//流水号
						
						getZZK(saleGoodsDef);
						updateDisplay = true;
						
					}
				}
			}
			
			Vector  vg = new Vector();
			filter1 obj = new filter1();
			int j = 0;
			//商品分组
			for (int i = 0; i < saleGoods.size(); i++)
			{
				SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
				if(saleGoodsDef.qtzke==0)
				{
					continue;
				}
				for(j = 0 ;j<vg.size();j++)
				{
					obj =(filter1) vg.elementAt(j);
					//如果渠道号相同 流水号不同 分为同一组
					if(saleGoodsDef.str15.equals(obj.qdh))
					{
						obj.row.add(String.valueOf(i));
						break;
					}
				}
				
				if (j >= vg.size())
				{
					filter1 obj2 = new filter1();
					obj2.qdh =saleGoodsDef.str15;
					obj2.zkxe =	saleGoodsDef.num1;
					obj2.lsh = saleGoodsDef.str5;
					obj2.row.add(String.valueOf(i));
					vg.add(obj2);
				}
				
			}
			
			for (int i= 0;i < vg.size();i++)
			{
				filter1 obj2 = (filter1) vg.elementAt(i);
				int j1=0;
				double hjje = 0;
				double hjqtzk = 0;
				for (;j1< obj2.row.size();j1++)
				{
					SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt((String)obj2.row.elementAt(j1)));
					hjqtzk += ManipulatePrecision.doubleConvert(saleGoodsDef.qtzke);
					hjje += ManipulatePrecision.doubleConvert(saleGoodsDef.hjje-getZZK(saleGoodsDef) + saleGoodsDef.qtzke);
				}
				
				if (hjqtzk < obj2.zkxe)
				{
					continue;
				}
				else
				{
					double ysje = 0;
					for(int k = 0;k< obj2.row.size();k++)
					{
						
						SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt((String)obj2.row.elementAt(k)));
						if (k == (obj2.row.size() -1))
						{
							saleGoodsDef.qtzke = ManipulatePrecision.doubleConvert(obj2.zkxe - ysje);
							getZZK(saleGoodsDef);
							continue;
						}
						saleGoodsDef.qtzke = 0;
						saleGoodsDef.qtzke = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje-getZZK(saleGoodsDef))/hjje*obj2.zkxe);
						ysje = ManipulatePrecision.doubleConvert(ysje + saleGoodsDef.qtzke);
						getZZK(saleGoodsDef);
					}
				}
			}
			

			if (updateDisplay)
			{
				calcHeadYsje();
				// 计算剩余付款
				calcPayBalance();

				// 刷新商品列表
				saleEvent.updateTable(getSaleGoodsDisplay());
				saleEvent.setTotalInfo();
				// 刷新付款列表
				salePayEvent.refreshSalePayment();

				new MessageBox(Language.apply("此银行卡已经进行银行折扣\n请用此卡进行银联付款"));

				NewKeyListener.sendKey(GlobalVar.ArrowUp);
			}

		}
		return true;
	}
	/*public double getConvertRebate(int i, double zkje)
	{
		return getConvertRebate(i, zkje, -1);
	}
	
	public double getConvertRebate(int i, double zkje, double jd)
	{
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(i);
		double je;
		double zk;
		double jgjd;

		if (jd <= 0)
		{
			if (goodsDef.jgjd == 0)
			{
				jgjd = 0.01;
			}
			else
			{
				jgjd = goodsDef.jgjd;
			}
		}
		else
		{
			jgjd = jd;
		}

		je = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef), 2, 1);

		je = ManipulatePrecision.doubleConvert(ManipulatePrecision.doubleConvert(ManipulatePrecision.doubleConvert(je / jgjd, 2, 1), 0, 1) * jgjd, 2, 1);

		zk = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef) - je, 2, 1);

		zk = ManipulatePrecision.doubleConvert(zkje + zk, 2, 1);

		if (zk < 0)
		{
			zk = 0;
		}

		return zk;
	}
	
	public double getConvertPrice(double jg, GoodsDef gd)
	{
		int jd;
		int flag = 1;

		if (gd.jgjd<0)
		{
			flag=0;
		}
		if (gd.jgjd == 0)
		{
			jd = 2;
		}
		else
		{
			jd = ManipulatePrecision.getDoubleScale(gd.jgjd);
		}

		
		// 四舍五入到商品价格精度
		return ManipulatePrecision.doubleConvert(jg, jd, flag);//1
	}*/
	public double getConvertRebate(int i, double zkje, double jd)
	{	//old bak by 2014.10.08
		/*SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(i);
		double je;
		double zk;
		double jgjd;

		if (jd <= 0)
		{
			if (goodsDef.jgjd == 0)
			{
				jgjd = 0.01;
			}
			else
			{
				jgjd = goodsDef.jgjd;
			}
		}
		else
		{
			jgjd = jd;
		}

		je = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef), 2, 1);

		je = ManipulatePrecision.doubleConvert(ManipulatePrecision.doubleConvert(ManipulatePrecision.doubleConvert(je / jgjd, 2, 1), 0, 1) * jgjd, 2, 1);

		zk = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef) - je, 2, 1);

		zk = ManipulatePrecision.doubleConvert(zkje + zk, 2, 1);
		
				
		if (zk < 0)
		{
			zk = 0;
		}

		return zk;*/
		
		DecimalFormat df = new DecimalFormat("#.0");
		this.writeLog("zkje=[" + zkje + "],zkje2=[" +  Convert.toDouble(df.format(zkje + 0.0499999999999)) + "]");
		return Convert.toDouble(df.format(zkje + 0.0499999999999));//强行将 折扣额 进位到角
	}
	
	public double formatDecimal(double je, String f)
	{
		try
		{
			DecimalFormat df = new DecimalFormat(f);
			return ManipulatePrecision.doubleConvert(Double.parseDouble(df.format(je)));//Double.valueOf(df.format(je));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return je;
	}

	// 输入数量
	public boolean inputQuantity(int index, double quantity)
	{
		if (SellType.isJS(saletype)) { return false; }

		SaleGoodsDef oldGoodsDef = null;
		SpareInfoDef oldSpare = null;
		double newsl = -1;
		boolean flag = false;
		// 如果输入了
		if (quantity >= 0)
		{
			flag = true;
			newsl = quantity;
		}

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(index);

		if (SellType.ISCHECKINPUT(saletype) && isSpecifyCheckInput() && "D".equals(saleGoodsDef.str8))
			return false;
		// 判断是否允许修改数量
		if (!allowInputQuantity(index))
			return false;

		// 输入数量
		StringBuffer buffer = new StringBuffer();
		do
		{
			if (!flag)
			{
				buffer.delete(0, buffer.length());
				buffer.append(ManipulatePrecision.doubleToString(saleGoodsDef.sl, 4, 1, true));

				// 检查是否从电子称里获取重量金额
				boolean input = true;

				/*// 电子秤不让修改数量
				
				 * if (GlobalInfo.sysPara.elcScaleMode == '1') { if
				 * (!ElectronicScale.getDefault().isSendData()) { //电子秤商品 if
				 * (saleGoodsDef.flag == '2') { if
				 * (ElectronicScale.getDefault().setPrice(saleGoodsDef.jg)) {
				 * new MessageBox("请将[" + saleGoodsDef.name +
				 * "]商品放在电子称上\n按【确认键】获取重量后请将商品拿离", null, false,
				 * GlobalVar.Validation); if
				 * (ElectronicScale.getDefault().run()) { newsl =
				 * ElectronicScale.getDefault().getWeight();
				 * 
				 * if (newsl != 0) input = false; } else { new
				 * MessageBox("获取重量失败"); return false; } } else { new
				 * MessageBox("发送价格失败"); return false; } } } }*/
				 

				if (input)
				{
					if (SellType.ISCOUPON(saletype) || SellType.ISJFSALE(saletype) || (saleEvent.yyyh.getText().trim().equals(Language.apply("超市")) && GlobalInfo.sysPara.goodsAmountInteger == 'Y') && goodsDef.isdzc != 'Y')
					{
						if (!new TextBox().open(Language.apply("请输入该商品数量"), Language.apply("数量"), "", buffer, 1, getMaxSaleGoodsQuantity(), true, TextBox.IntegerInput, -1)) { return false; }
					}
					else
					{
						if (!new TextBox().open(Language.apply("请输入该商品数量"), Language.apply("数量"), "", buffer, 0.0001, getMaxSaleGoodsQuantity(), true)) { return false; }
					}
					newsl = Double.parseDouble(buffer.toString());
				}
				newsl = ManipulatePrecision.doubleConvert(newsl, 4, 1);
				flag = true;
			}
			// 检查销红
			if (SellType.ISSALE(saletype) && (GlobalInfo.sysPara.isxh != 'Y') && (goodsDef.kcsl > 0))
			{
				// 统计商品数量
				double hjsl = calcSameGoodsQuantity(goodsDef.code, goodsDef.gz);
				hjsl = (hjsl - ManipulatePrecision.mul(saleGoodsDef.sl, goodsDef.bzhl)) + ManipulatePrecision.mul(newsl, goodsDef.bzhl);

				if (goodsDef.kcsl < hjsl)
				{
					if (GlobalInfo.sysPara.xhisshowsl == 'Y')
						new MessageBox(Language.apply("销售数量已大于该商品库存【{0}】\n\n不能销售", new Object[]{goodsDef.kcsl + ""}));
					else
						new MessageBox(Language.apply("销售数量已大于该商品库存,不能销售"));

					if (flag)
						return false;
					continue;
				}
			}

			// 指定小票退货
			if (isSpecifyBack(saleGoodsDef))
			{
				// 统计商品数量
				double hjsl = calcSameGoodsQuantity(goodsDef.code, goodsDef.gz);
				hjsl = (hjsl - ManipulatePrecision.mul(saleGoodsDef.sl, goodsDef.bzhl)) + ManipulatePrecision.mul(newsl, goodsDef.bzhl);

				if (goodsDef.kcsl < hjsl)
				{
					new MessageBox(Language.apply("退货数量已大于该商品原销售数量\n\n不能退货"));
					if (flag)
						return false;
					continue;
				}
			}

			// 检查印花限量优惠
			if (stampList != null && stampList.size() > 0 && SellType.ISSALE(saletype) && goodsDef.poptype != '0' && goodsDef.infonum1 > -9999.00)
			{
				double hjsl = calcSameGoodsQuantity(goodsDef.code, goodsDef.gz) + newsl - saleGoodsDef.sl;
				if (goodsDef.infonum1 < hjsl)
				{
					new MessageBox(Language.apply("该商品只有【{0}】个促销数量\n\n商品数量修改无效", new Object[]{goodsDef.infonum1 +""}));
					if (flag)
						return false;
					continue;
				}
			}

			// 跳出循环
			break;
		} while (true);

		if (newsl < 0)
			return false;

		// 无权限
		if ((newsl < saleGoodsDef.sl) && (curGrant.privqx != 'Y') && (curGrant.privqx != 'Q'))
		{
			//
			OperUserDef staff = inputQuantityGrant(index);
			if (staff == null)
				return false;

			// 记录日志
			String log = "授权修改数量,小票号:" + saleHead.fphm + ",商品:" + saleGoodsDef.barcode + ",数量:" + newsl + ",授权:" + staff.gh;
			AccessDayDB.getDefault().writeWorkLog(log);
		}

		// 备份数据
		oldGoodsDef = (SaleGoodsDef) saleGoodsDef.clone();

		if (info != null)
			oldSpare = (SpareInfoDef) info.clone();

		// 重算商品应收
		double oldsl = saleGoodsDef.sl;
		saleGoodsDef.sl = newsl;
		saleGoodsDef.hjje = ManipulatePrecision.doubleConvert(saleGoodsDef.sl * saleGoodsDef.jg, 2, 1);
		double lszre = ManipulatePrecision.doubleConvert(saleGoodsDef.lszre / oldsl * newsl);
		double lszzk = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke / oldsl * newsl);
		clearGoodsGrantRebate(index);
		saleGoodsDef.lszre = lszre;
		saleGoodsDef.lszke = lszzk;

		getZZK(saleGoodsDef);
		calcGoodsYsje_TMP(index);

		// 重算小票应收
		calcHeadYsje();

		// 数量过大
		if (saleHead.ysje > getMaxSaleMoney())
		{
			new MessageBox(Language.apply("商品数量过大,导致销售金额达到上限\n\n商品数量修改无效"));

			// 恢复数量
			goodsSpare.setElementAt(oldSpare, index);
			saleGoods.setElementAt(oldGoodsDef, index);
			calcHeadYsje();

			return false;
		}

		// 退货金额过大
		if (SellType.ISBACK(saletype) && saleHead.ysje > curGrant.thxe)
		{
			new MessageBox(Language.apply("商品数量过大,导致退货金额超过限额\n\n商品数量修改无效"));

			// 恢复数量
			goodsSpare.setElementAt(oldSpare, index);
			saleGoods.setElementAt(oldGoodsDef, index);
			calcHeadYsje();

			return false;
		}

		// 盘点处理
		if (SellType.ISCHECKINPUT(saletype) && isSpecifyCheckInput() && !"U".equals(saleGoodsDef.str8))
		{
			if ("A".equals(saleGoodsDef.str8))
			{
				saleGoodsDef.name += "[修改]";
				saleGoodsDef.str8 = "A";
			}
			else if (saleGoodsDef.str8 == null || saleGoodsDef.str8.length() == 0)
			{
				saleGoodsDef.name += "[修改]";
				saleGoodsDef.str8 = "U";
			}
		}

		return true;
	}
	
	public void calcGoodsYsje_TMP(int index)
	{
		if ((index < 0) || (index >= saleGoods.size())) { return; }

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);

		// 根据价格精度进行截断或四舍五入
		//if (GlobalInfo.sysPara.isForceRound == 'Y')
		//{
			// 按价格精度计算折扣
			if (saleGoodsDef.hjje > 0)
			{
				double hjje = getConvertPrice_TMP(saleGoodsDef.hjje, (GoodsDef) goodsAssistant.elementAt(index));
				saleGoodsDef.qtzre = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - hjje);
			}
		//}

		// 计算会员折扣和优惠折扣
		calcAllRebate(index);

		// 计算批量销售折扣,根据情况会重算优惠折扣和会员折扣
		calcBatchRebate(index);

		// 计算商品的合计
		getZZK(saleGoodsDef);
	}
	
	public double getConvertPrice_TMP(double jg, GoodsDef gd)
	{
		int jd;
		int flag = 1;

		if (gd.jgjd<0)
		{
			flag=0;
		}
		if (gd.jgjd == 0)
		{
			jd = 2;
		}
		else
		{
			jd = ManipulatePrecision.getDoubleScale(gd.jgjd);
		}

		
		// 四舍五入到商品价格精度
		return ManipulatePrecision.doubleConvert(jg, jd, flag);//1
	}
	
	OperUserDef user = null;
//	 重新打印上一张小票
	public void rePrint()
	{
		ResultSet rs = null;
		SaleHeadDef saleheadprint = null;
		Vector salegoodsprint = null;
		Vector salepayprint = null;

		// 盘点
		if (SellType.ISCHECKINPUT(saletype))
		{
			if (saleGoods == null || saleGoods.size() <= 0)
				return;

			if (!CheckGoodsMode.getDefault().isLoad())
				return;

			MessageBox me = new MessageBox(Language.apply("你确实要打印盘点小票吗?"), null, true);

			if (me.verify() != GlobalVar.Key1)
				return;

			CheckGoodsMode.getDefault().setTemplateObject(saleHead, saleGoods, salePayment);

			CheckGoodsMode.getDefault().printBill();

			return;
		}

		if (GlobalInfo.syjDef.printfs == '1' && saleGoods != null && saleGoods.size() > 0)
		{
			new MessageBox(Language.apply("当前打印为即扫即打并且已有商品交易,不能重打!"), null, false);

			return;
		}

		// 检查发票是否打印完,打印完未设置新发票号则不能交易
		if (Printer.getDefault().getSaleFphmComplate()) { return; }

		MessageBox me = new MessageBox(Language.apply("你确实要重印上一张小票吗?"), null, true);
		try
		{
			if (me.verify() == GlobalVar.Key1 && getReprintAuth())
			{
				Object obj = null;
				String fphm = null;

				if (curGrant.privdy != 'Y' && curGrant.privdy != 'L')
				{
					if ((user = DataService.getDefault().personGrant(Language.apply("授权重打印小票"))) != null)
					{
						if (user.privdy != 'Y' && user.privdy != 'L')
						{
							new MessageBox(Language.apply("当前工号没有重打上笔小票权限!"));

							return;
						}

						String log = Language.apply("授权重打印上一笔小票,授权工号:") + user.gh;
						AccessDayDB.getDefault().writeWorkLog(log);
					}
					else
					{
						return;
					}
				}
				else
				{
					user = new OperUserDef();
				}

				if ((obj = GlobalInfo.dayDB.selectOneData("select max(fphm) from salehead where syjh = '" + ConfigClass.CashRegisterCode + "'")) != null)
				{
					try
					{
						fphm = String.valueOf(obj);

						if ((rs = GlobalInfo.dayDB.selectData("select * from salehead where syjh = '" + ConfigClass.CashRegisterCode + "' and  fphm = " + fphm)) != null)
						{

							if (!rs.next())
							{
								new MessageBox(Language.apply("没有查询到小票头,不能打印!"));
								return;
							}

							saleheadprint = new SaleHeadDef();

							if (!GlobalInfo.dayDB.getResultSetToObject(saleheadprint)) { return; }
						}
						else
						{
							new MessageBox(Language.apply("查询小票头失败!"), null, false);
							return;
						}
					}
					catch (Exception ex)
					{
						new MessageBox(Language.apply("查询小票头出现异常!"), null, false);
						ex.printStackTrace();
						return;
					}
					finally
					{
						GlobalInfo.dayDB.resultSetClose();
					}

					try
					{
						if ((rs = GlobalInfo.dayDB.selectData("select * from SALEGOODS where syjh = '" + ConfigClass.CashRegisterCode + "' and fphm = " + fphm + " order by rowno")) != null)
						{
							boolean ret = false;
							salegoodsprint = new Vector();
							while (rs.next())
							{
								SaleGoodsDef sg = new SaleGoodsDef();

								if (!GlobalInfo.dayDB.getResultSetToObject(sg)) { return; }

								salegoodsprint.add(sg);

								ret = true;
							}

							if (!ret)
							{
								new MessageBox(Language.apply("没有查询到小票明细,不能打印!"));
								return;
							}
						}
						else
						{
							new MessageBox(Language.apply("查询小票明细失败!"), null, false);
							return;
						}
					}
					catch (Exception ex)
					{
						new MessageBox(Language.apply("查询小票明细出现异常!"), null, false);
						ex.printStackTrace();
						return;
					}
					finally
					{
						GlobalInfo.dayDB.resultSetClose();
					}

					try
					{
						if ((rs = GlobalInfo.dayDB.selectData("select * from SALEPAY where syjh = '" + ConfigClass.CashRegisterCode + "' and fphm = " + fphm + " order by rowno")) != null)
						{
							boolean ret = false;
							salepayprint = new Vector();
							while (rs.next())
							{
								SalePayDef sp = new SalePayDef();

								if (!GlobalInfo.dayDB.getResultSetToObject(sp)) { return; }

								salepayprint.add(sp);

								ret = true;
							}
							if (!ret)
							{
								new MessageBox(Language.apply("没有查询到付款明细,不能打印!"));
								return;
							}
						}
						else
						{
							new MessageBox(Language.apply("查询付款明细失败!"), null, false);
							return;
						}
					}
					catch (Exception ex)
					{
						new MessageBox(Language.apply("查询付款明细出现异常!"), null, false);
						ex.printStackTrace();
						return;
					}
					finally
					{
						GlobalInfo.dayDB.resultSetClose();
					}

					saleheadprint.printnum++;
					AccessDayDB.getDefault().updatePrintNum(saleheadprint.syjh, String.valueOf(saleheadprint.fphm), String.valueOf(saleheadprint.printnum));
					ProgressBox pb = new ProgressBox();
					pb.setText(Language.apply("现在正在重打印小票,请等待....."));
					try
					{
						printSaleTicket(saleheadprint, salegoodsprint, salepayprint, false);
					}
					finally
					{
						pb.close();
					}
				}
				else
				{
					new MessageBox(Language.apply("当前没有销售数据,不能打印!"));
				}
			}
		}
		finally
		{
			saleheadprint = null;

			if (salegoodsprint != null)
			{
				salegoodsprint.clear();
				salegoodsprint = null;
			}

			if (salepayprint != null)
			{
				salepayprint.clear();
				salepayprint = null;
			}
		}
	}
	
	public void getVIPZK(int index, int type)
	{
		if (1 == 1)
		{
			//super.getVIPZK(index, type);
			//return;
		}
		boolean zszflag = true;
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		GoodsPopDef popDef = (GoodsPopDef) crmPop.elementAt(index);
		SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(index);

		// 指定小票退货时不重算优惠价和会员价
		if (isSpecifyBack(saleGoodsDef)) { return; }

		// 积分换购商品不计算会员打折
		if (info.char2 == 'Y') { return; }

		if (curCustomer == null || (curCustomer != null && curCustomer.iszk != 'Y')) return;

		// 批发销售不计算
		if (SellType.ISBATCH(saletype)) { return; }

		if (SellType.ISEARNEST(saletype)) { return; }

		if (SellType.ISCOUPON(saletype)) { return; }
		
		if (SellType.ISJFSALE(saletype)) { return; }

		// 削价商品和赠品不计算
		if ((saleGoodsDef.flag == '3') || (saleGoodsDef.flag == '1')) { return; }

		// 不为VIP折扣的商品不重新计算会员折扣额
		if (goodsDef.isvipzk == 'N') return;

		// 折扣门槛
		if (saleGoodsDef.hjje == 0
				|| ManipulatePrecision
										.doubleConvert((saleGoodsDef.hjje - saleGoodsDef.lszke - saleGoodsDef.lszre - saleGoodsDef.lszzk - saleGoodsDef.lszzr)
												/ saleGoodsDef.hjje) < GlobalInfo.sysPara.vipzklimit) return;

		// 商品会员促销价
		if (popDef.jsrq != null && popDef.jsrq.length() > 0 && popDef.jsrq.split(",").length >= 5 && type == vipzk1)
		{
			// 商品会员价促销单号,商品促销价，限量数量 ，已享受数量，积分方式（0:正常积分 ,1:不积分 2:特价积分） 
			String[] arg = popDef.jsrq.split(",");

			double price = Convert.toDouble(arg[1]);
			double max = Convert.toDouble(arg[2]);
			double used = Convert.toDouble(arg[3]);

			// 限量
			boolean isprice = false;
			if (max > 0)
			{
				double q = 0;
				for (int i = 0; i < saleGoods.size(); i++)
				{
					SaleGoodsDef saleGoodsDef1 = (SaleGoodsDef) saleGoods.elementAt(i);
					SpareInfoDef info1 = (SpareInfoDef) goodsSpare.elementAt(i);

					if (i == index) continue;

					if (saleGoodsDef1.code.equals(saleGoodsDef.code) && info1.char1 == 'Y')
					{
						q += saleGoodsDef1.sl;
					}
				}

				if (ManipulatePrecision.doubleConvert(max - used - q) > 0)
				{
					if (ManipulatePrecision.doubleConvert(saleGoodsDef.sl) > ManipulatePrecision.doubleConvert(max - used - q))
					{
//						new MessageBox("此商品存在促销价，但是商品数量[" + saleGoodsDef.sl + "]超出数量限额【" + ManipulatePrecision.doubleConvert(max - used - q)
//								+ "】\n 强制将商品数量修改为【" + ManipulatePrecision.doubleConvert(max - used - q) + "】参与促销价");
						new MessageBox(Language.apply("此商品存在促销价，但是商品数量[{0}]超出数量限额【{1}】\n 强制将商品数量修改为【{2}】参与促销价" ,new Object[]{saleGoodsDef.sl+"" ,ManipulatePrecision.doubleConvert(max - used - q)+"" ,ManipulatePrecision.doubleConvert(max - used - q)+""}));
						saleGoodsDef.sl = ManipulatePrecision.doubleConvert(max - used - q);
						saleGoodsDef.hjje = ManipulatePrecision.doubleConvert(saleGoodsDef.sl * saleGoodsDef.jg, 2, 1);
						calcGoodsYsje(index);
					}
					isprice = true;
				}
			}
			else
			{
				isprice = true;
			}

			if (isprice == true)
			{
				saleGoodsDef.hyzke = 0;
				saleGoodsDef.yhzke = 0;
				saleGoodsDef.lszke = 0;
				saleGoodsDef.lszre = 0;
				saleGoodsDef.lszzk = 0;
				saleGoodsDef.lszzr = 0;

				if (info.str1.length() > 1 && info.str1.charAt(0) == '9')
				{
					StringBuffer buff = new StringBuffer(info.str1);
					for (int z = 1; z < buff.length(); z++)
					{
						buff.setCharAt(z, '0');
					}
					info.str1 = buff.toString();
				}
				else
				{
					info.str1 = "0000";
				}
				//saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert((saleGoodsDef.jg - price) * saleGoodsDef.sl);
				//saleGoodsDef.hyzke = getConvertRebate(index, saleGoodsDef.hyzke);
				saleGoodsDef.hyzke = (saleGoodsDef.jg - price) * saleGoodsDef.sl;//new
				saleGoodsDef.str1 = popDef.jsrq;
				info.char1 = 'Y';
			}
		}

		// 已计算了商品会员促销价，不再继续VIP折扣
		if (info.char1 == 'Y') return;

		if (goodsDef.isvipzk == 'Y')
		{
			// 开始计算VIP折扣
			saleGoodsDef.hyzke = 0;
			saleGoodsDef.hyzkfd = goodsDef.hyjzkfd;
		}

		// 判断促销单是否允许折上折
		if (goodsDef.pophyjzkl % 10 >= 1) zszflag = zszflag && true;
		else zszflag = zszflag && false;

		//是否进行VIP打折,通过CRM促销控制
		boolean vipzk = false;

		//无CRM促销，以分期促销折上折标志为准
		if (popDef.yhspace == 0)
		{
			vipzk = true;
		}
		else
		//存在CRM促销
		{
			//不享用VIP折扣,不进行VIP打折
			if (popDef.pophyjzkl == 0)
			{
				vipzk = false;
			}
			else
			//享用VIP折扣，进行VIP折上折
			{
				vipzk = true;
				zszflag = zszflag && true;
			}
		}

		if (checkMemberSale() && curCustomer != null && goodsDef.isvipzk == 'H' && type == vipzk1)
		{
			double je = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - getZZK(saleGoodsDef)));
			double hyj = 0;
			if (goodsDef.pophyj != 0)
			{
				hyj = goodsDef.pophyj;
			}

			if (goodsDef.hyj != 0)
			{
				if (hyj == 0) hyj = goodsDef.hyj;
				else hyj = Math.min(hyj, goodsDef.hyj);
			}

			if (hyj != 0 && je > ManipulatePrecision.doubleConvert(hyj * saleGoodsDef.sl))
			{
				//saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert(je - ManipulatePrecision.doubleConvert(hyj * saleGoodsDef.sl));
				//saleGoodsDef.hyzke = getConvertRebate(index, saleGoodsDef.hyzke);
				saleGoodsDef.hyzke = je - (hyj * saleGoodsDef.sl);//new
			}

		}
		//存在会员卡， 商品允许VIP折扣， CRM促销单允许享用VIP折扣
		else if (checkMemberSale() && curCustomer != null && goodsDef.isvipzk == 'Y' && vipzk && curCustomer.iszk == 'Y')
		{
			// 获取VIP折扣率定义
			calcVIPZK(index);

			// 折上折标志
			zszflag = zszflag && (goodsDef.num4 == 1);

			// 不计算会员卡折扣
			if (goodsDef.hyj == 1) return;

			// vipzk1 = 输入商品时计算商品VIP折扣,原VIP折上折模式
			if (type == vipzk1 && (GlobalInfo.sysPara.vipPromotionCrm == null || GlobalInfo.sysPara.vipPromotionCrm.equals("1")))
			{
				//有折扣,进行折上折
				if (getZZK(saleGoodsDef) >= 0.01 && goodsDef.hyj < 1.00)
				{
					// 需要折上折
					if (zszflag)
					{
						//saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert((1 - goodsDef.hyj) * (saleGoodsDef.hjje - getZZK(saleGoodsDef)), 2, 1);
						saleGoodsDef.hyzke = (1 - goodsDef.hyj) * (saleGoodsDef.hjje - getZZK(saleGoodsDef));
					}
					else
					{
						// 商品不折上折时，取商品的hyj和综合折扣较低者
						if (ManipulatePrecision.doubleCompare(saleGoodsDef.hjje - getZZK(saleGoodsDef), goodsDef.hyj * saleGoodsDef.hjje, 2) > 0)
						{
							double zke = ManipulatePrecision.doubleConvert((1 - goodsDef.hyj) * saleGoodsDef.hjje, 2, 1);
							zke = (1 - goodsDef.hyj) * saleGoodsDef.hjje;
							if (zke > getZZK(saleGoodsDef))
							{
								//saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert(zke - getZZK(saleGoodsDef), 2, 1);
								saleGoodsDef.hyzke = zke - getZZK(saleGoodsDef);
							}
						}
					}
				}
				else
				{
					//无折扣,按商品缺省会员折扣打折
					//saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert((1 - goodsDef.hyj) * saleGoodsDef.hjje, 2, 1);
					saleGoodsDef.hyzke = (1 - goodsDef.hyj) * saleGoodsDef.hjje;
				}
			}
			else // vipzk2 = 按下付款键时计算商品VIP折扣,起点折扣计算模式 
			if (type == vipzk2 && GlobalInfo.sysPara.vipPromotionCrm != null && GlobalInfo.sysPara.vipPromotionCrm.equals("2"))
			{
				// VIP折扣要除券付款
				double fte = 0;
				if (GlobalInfo.sysPara.vipPayExcp == 'Y') fte = getGoodsftje(index);

				double vipzsz = 0;

				// 直接在以以后折扣的基础上打商品定义的VIP会员折扣率
				if (GlobalInfo.sysPara.vipCalcType.equals("2"))
				{
					//vipzsz = ManipulatePrecision.doubleConvert((1 - goodsDef.hyj) * (saleGoodsDef.hjje - getZZK(saleGoodsDef) - fte), 2, 1);
					vipzsz = (1 - goodsDef.hyj) * (saleGoodsDef.hjje - getZZK(saleGoodsDef) - fte);
				}
				else if (GlobalInfo.sysPara.vipCalcType.equals("1"))
				{
					double mk = popDef.num6;
					double zkl = popDef.num7;
					if(popDef.num6 == 0 && popDef.num7 == 0 && popDef.num8 == 0 && ( popDef.str6 == null || popDef.str6.trim().equals(""))){
						mk = curCustomer.value3;
						zkl = curCustomer.zkl;
					}
					// 当前折扣如果高于门槛则还可以进行VIP折上折,否则VIP不能折上折
					if (getZZK(saleGoodsDef) > 0
							&& zszflag
							&& ManipulatePrecision.doubleCompare(saleGoodsDef.hjje - getZZK(saleGoodsDef), saleGoodsDef.hjje *  mk, 2) >= 0)
					{
						//vipzsz = ManipulatePrecision.doubleConvert((1 - curCustomer.zkl) * (saleGoodsDef.hjje - getZZK(saleGoodsDef) - fte), 2, 1);
						vipzsz = (1 - zkl) * (saleGoodsDef.hjje - getZZK(saleGoodsDef) - fte);
					}

					// 如果VIP折上折以后的成交价 高于 该商品定义的VIP会员折扣率，则商品以商品定义的折扣执行VIP折
					double spvipcjj = ManipulatePrecision.doubleConvert(goodsDef.hyj * (saleGoodsDef.hjje - fte), 2, 1);
					spvipcjj = goodsDef.hyj * (saleGoodsDef.hjje - fte);
					if (ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef) - fte - vipzsz, 2, 1) > ManipulatePrecision.doubleConvert(spvipcjj, 2, 1))
					{
						//vipzsz = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef) - fte - spvipcjj);
						vipzsz = saleGoodsDef.hjje - getZZK(saleGoodsDef) - fte - spvipcjj;
					}
				}

				saleGoodsDef.hyzke = vipzsz;
			}

			// 按价格精度计算折扣
			//saleGoodsDef.hyzke = getConvertRebate(index, saleGoodsDef.hyzke);
		}
		saleGoodsDef.hyzke = getConvertRebate(index, saleGoodsDef.hyzke);
		getZZK(saleGoodsDef);
	}
	
	SaleHeadDef tempsalehead = null;
	static String[] b = null;
	
	public void printSaleTicket(SaleHeadDef vsalehead, Vector vsalegoods, Vector vsalepay, boolean isRed)
	{
		String type = "SalePrintMode.ini";
		if (vsalehead != null && vsalehead.djlb != null)
			type = vsalehead.djlb;

		Vector tempsalegoods = null;
		Vector tempsalepay = null;

		try
		{
			tempsalehead = SaleBillMode.getDefault(type).getSalehead();
			tempsalegoods = SaleBillMode.getDefault(type).getSalegoods();
			tempsalepay = SaleBillMode.getDefault(type).getSalepay();

			// 联网获取赠送打印清单
			DataService dataservice = (DataService) DataService.getDefault();
			Vector gifts = dataservice.getSaleTicketMSInfo(vsalehead, vsalegoods, vsalepay);
			SaleBillMode.getDefault(type).setSaleTicketMSInfo(vsalehead, gifts);

			// 检查是否需要重打印赠品联授权
			boolean bok = true;
			
			
			if (vsalehead.printnum > 0 && SaleBillMode.getDefault(type).needMSInfoPrintGrant())
			{
				if (GlobalInfo.posLogin.priv.charAt(1) != 'Y')
				{
					OperUserDef staff = DataService.getDefault().personGrant(Language.apply("重打印赠券授权"));

					if (staff == null || staff.priv.charAt(1) != 'Y')
					{
						new MessageBox(Language.apply("此交易存在赠券或者赠品\n该审批员无重打印赠品或者赠券权限"));
						bok = false;
					}
				}
			}
			
	 		if(!getReprint(vsalehead)) return ;
			
			if (!bok)
			{
				SaleBillMode.getDefault(type).setSaleTicketMSInfo(vsalehead, null);
			}

			if (vsalehead != null && vsalegoods != null && vsalepay != null)
			{
				SaleBillMode.getDefault(type).setTemplateObject(vsalehead, vsalegoods, vsalepay);
				SaleBillMode.getDefault(type).printBill();
				
				
				if(vsalehead.printnum > 0)
				{
					String[] s = Nbbh_SaleBS.getResult();   //上传字段

					s[7] = String.valueOf(Printer.getDefault().getCurrentSaleFphm()-1);
					new Nbbh_NetService().postReprint(s);
				}
				
			
			}
			else
			{
				new MessageBox(Language.apply("未发现小票对象，不能打印\n或\n打印模版读取失败"));
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			SaleBillMode.getDefault(type).setTemplateObject(tempsalehead, tempsalegoods, tempsalepay);
		}
	}
		
	protected boolean getReprint(SaleHeadDef vsalehead)
	{
//		从本地查找重打原因
		ResultSet rs = null;
		ReprintDef print = null;
		try{			
			if ((rs = GlobalInfo.localDB.selectData("select IWID,IWMEMO,IWSTATUS from REPRINT")) != null)
			{
				if (!rs.next())
				{
					new MessageBox(Language.apply("没有查询到小票打印原因!"));
					return false;
				}

				String code = "";
				String text = "";
				String log = "";
				String printType = "2";              //发票打印类型  1正常销售 2重打
				
				String startfph = String.valueOf(Printer.getDefault().getCurrentSaleFphm());
				String usedfphnum = "";
				
				Vector reprint = new Vector();
				String[] title =  { "打印原因ID" ,"原因说明" ,"是否启用(Y/N)"};
				int[] width = { 150, 250 ,150};

				do{
					print = new ReprintDef();
					
					if (!GlobalInfo.localDB.getResultSetToObject(print)) { return false; }
					
					reprint.add(new String[] {String.valueOf(print.IWID), print.IWMEMO, print.IWSTATUS});
				}while(rs.next());

				if(user.gh == null) user.gh = GlobalInfo.posLogin.gh;						
				int choice = new MutiSelectForm().open("请选择打印原因", title, width, reprint, true);
				if (choice == -1)
				{
					new MessageBox(Language.apply("没有选择打印原因"));
					log = "收银机号:" + ConfigClass.CashRegisterCode + ",小票号:" + vsalehead.fphm + ",发票打印类型:" + printType + ",打印原因:" + "0" + "没有选打印原因" + ",授权工号:"+ user.gh ;
					return false;
				}else {
					String[] row = (String[]) (reprint.elementAt(choice));
					code = row[0].toString();
					text = row[2].toString();
					log = "收银机号:" + ConfigClass.CashRegisterCode + ",小票号:" + vsalehead.fphm + ",发票打印类型:" + printType + ",打印原因:" + code + ",授权工号:" + user.gh;
				}
				AccessDayDB.getDefault().writeWorkLog(log,"1234");
				
				
				String[] s = {GlobalInfo.sysPara.mktcode, ConfigClass.CashRegisterCode ,String.valueOf(vsalehead.fphm) ,printType ,code ,user.gh ,startfph, usedfphnum, ManipulateDateTime.getDateTimeByClock()};
				b = s;
				
				return true;
			}
			else
			{
				new MessageBox(Language.apply("没有查询到小票打印原因!"), null, false);
				return false;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			new MessageBox(Language.apply("获取打印原因列表异常"));
			return false;
		}		
	}
	
	public static String[] getResult()
	{
		return b;
	}

	class filter1
	{
		public double zkxe = 0;
		public String qdh;
		public String lsh;
		public Vector row = new Vector();
	}
}