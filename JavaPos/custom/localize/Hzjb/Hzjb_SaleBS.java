package custom.localize.Hzjb;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.PaymentBankCMCC;
import com.efuture.javaPos.Payment.PaymentCustJfSale;

import com.efuture.javaPos.Payment.PaymentFjk;
import com.efuture.javaPos.Payment.Bank.Njys_PaymentBankFunc;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.CalcRulePopDef;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.JfSaleRuleDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;
import com.efuture.javaPos.UI.Design.SalePayForm;

import custom.localize.Bcrm.Bcrm_DataService;
import custom.localize.Bcrm.Bcrm_NetService;
import custom.localize.Bcrm.Bcrm_SaleBS;

public class Hzjb_SaleBS extends Bcrm_SaleBS
{
	public String[] rowInfo(SaleGoodsDef goodsDef)
	{
		String[] detail = new String[8];
		detail[1] = goodsDef.inputbarcode;
		detail[2] = Convert.appendStringSize("", goodsDef.name, 0, 18, 19) + goodsDef.gz;
		// detail[2] = Convert.newSubString(goodsDef.name, 0, 18)+goodsDef.gz;
		detail[3] = goodsDef.unit;
		detail[4] = ManipulatePrecision.doubleToString(goodsDef.sl, 4, 1, true);
		detail[5] = ManipulatePrecision.doubleToString(goodsDef.jg);
		detail[6] = ManipulatePrecision.doubleToString(goodsDef.hjzk) + (goodsDef.hjzk > 0 ? "(" + ManipulatePrecision.doubleToString((goodsDef.hjje - goodsDef.hjzk) / goodsDef.hjje * 100, 0, 1) + "%)" : "");
		detail[7] = ManipulatePrecision.doubleToString(goodsDef.hjje - goodsDef.hjzk, 2, 1);

		return detail;
	}

	public boolean preGetMSinfo()
	{
		if (GlobalInfo.sysPara.ispregetmsinfo == 'N') { return true; }

		// 汇总数据
		if (!saleSummary())
		{
			new MessageBox("预上传小票时交易数据汇总失败!");
			return false;
		}

		// 预上传小票
		boolean done = true;
		Bcrm_NetService netservice = (Bcrm_NetService) NetService.getDefault();
		char bc = saleHead.bc;
		try
		{
			saleHead.bc = '$';
			int result = netservice.sendSaleData(saleHead, saleGoods, salePayment, null, Bcrm_NetService.getDefault().getMemCardHttp(CmdDef.PRESENDCRMSELL), CmdDef.PRESENDCRMSELL);
			if (result != 0 && result != 2)
			{
				new MessageBox("预上传小票失败，无法获得满赠信息");
				done = false;
			}
		}
		finally
		{
			saleHead.bc = bc;
		}
		// 查询小票实时赠品信息
		Vector v = new Vector();
		if (done && netservice.getSaleTicketMSInfo(v, saleHead.mkt, saleHead.syjh, String.valueOf(saleHead.fphm), "N", Bcrm_NetService.getDefault().getMemCardHttp(CmdDef.PREGETMSINFO), CmdDef.PREGETMSINFO))
		{
			if (v.size() < 1) { return true; }

			GiftGoodsDef def = null;

			StringBuffer line = new StringBuffer();

			line.append("本次小票存在赠券\n");

			for (int i = 0; i < v.size(); i++)
			{
				def = (GiftGoodsDef) v.get(i);

				// 数量
				String sl = String.valueOf(def.sl);

				// 金额
				String je = ManipulatePrecision.doubleToString(def.je);
				String[] infos = def.info.split("&");

				if (infos != null && infos.length > 1)
				{
					line.append(infos[1] + "\n");
				}
				line.append("赠券金额  :" + Convert.appendStringSize("", je, 1, 16, 16, 0) + "\n");
				line.append("赠券数量  :" + Convert.appendStringSize("", sl, 1, 16, 16, 0) + "\n");

				// 描述
				if (infos != null && infos.length > 0)
				{
					line.append("赠券信息  :" + Convert.appendStringSize("", infos[0], 1, 16, 16, 0) + "\n");
				}
				line.append("----------\n");
			}

			new MessageBox(line.toString());
		}

		return true;
	}

	public void findGoodsCRMPop(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		String cardno = null;
		String cardtype = null;

		if ((curCustomer != null))
		{
			cardno = curCustomer.code;
			cardtype = curCustomer.type;
		}

		GoodsPopDef popDef = new GoodsPopDef();

		// 非促销商品 或者在退货时，不查找促销信息
		((Bcrm_DataService) DataService.getDefault()).findPopRuleCRM(popDef, sg.code, sg.gz, sg.uid, goods.specinfo, sg.catid, sg.ppcode, saleHead.rqsj, cardno, cardtype, saletype);
		if (popDef.yhspace == 0)
		{
			popDef.yhspace = 0;
			popDef.memo = "";
			popDef.poppfjzkfd = 1;
		}

		// 将收券规则放入GOODSDEF 列表
		goods.memo = popDef.str2;
		goods.num1 = popDef.num1;
		goods.num2 = popDef.num2;
		goods.str4 = popDef.mode;
		info.char3 = popDef.type;

		// 促销联比例
		sg.xxtax = Convert.toDouble(popDef.ksrq); // 促销联比例
		goods.xxtax = Convert.toDouble(popDef.ksrq);
		if (goods.memo == null)
			goods.memo = "";

		// 增加CRM促销信息
		crmPop.add(popDef);

		// 无促销,此会员不允许促销
		if (popDef.yhspace == 0)
		{
			info.str1 = "0000";
		}
		else
		{
			if (GlobalInfo.sysPara.iscrmtjprice == 'Y')
				info.str1 = Convert.increaseInt(popDef.yhspace, 5).substring(0, 4);
			else
				info.str1 = Convert.increaseInt(popDef.yhspace, 4);

			// 询问参加活动类型 满减或者满增
			String yh = info.str1;
			StringBuffer buff = new StringBuffer(yh);
			Vector contents = new Vector();

			for (int i = 0; i < buff.length(); i++)
			{
				// 2-任选促销/1-存在促销/0-无促销
				if (buff.charAt(i) == '2')
				{
					if (i == 0)
					{
						contents.add(new String[] { "D", "参与打折促销活动", "0" });
					}
					else if (i == 1)
					{
						contents.add(new String[] { "J", "参与减现促销活动", "1" });
					}
					else if (i == 2)
					{
						contents.add(new String[] { "Q", "参与返券促销活动", "2" });
					}
					else if (i == 3)
					{
						contents.add(new String[] { "Z", "参与赠品促销活动", "3" });
					}
				}
			}

			if (contents.size() <= 1)
			{
				if (contents.size() > 0)
				{
					String[] row = (String[]) contents.elementAt(0);
					int i = Integer.parseInt(row[2]);
					buff.setCharAt(i, '1');
				}
			}
			else
			{
				String[] title = { "代码", "描述" };
				int[] width = { 60, 400 };
				int choice = new MutiSelectForm().open("请选择参与满减满赠活动的规则", title, width, contents);

				for (int i = 0; i < contents.size(); i++)
				{
					if (i != choice)
					{
						String[] row = (String[]) contents.elementAt(i);
						int j = Integer.parseInt(row[2]);
						buff.setCharAt(j, '0');
					}
					else
					{
						String[] row = (String[]) contents.elementAt(i);
						int j = Integer.parseInt(row[2]);
						buff.setCharAt(j, '1');
					}
				}
			}

			info.str1 = buff.toString();
		}

		String line = "";

		if (info.str1.charAt(0) != '0')
		{
			line += "D";
		}

		if (info.str1.charAt(1) != '0')
		{
			line += "J";
		}

		if (info.str1.charAt(2) != '0')
		{
			line += "Q";
		}

		if (info.str1.charAt(3) != '0')
		{
			line += "Z";
		}

		if (line.length() > 0)
		{
			sg.name = "(" + line + ")" + sg.name;
		}

		// str3记录促销组合码
		if (GlobalInfo.sysPara.iscrmtjprice == 'Y')
			sg.str3 = info.str1 + String.valueOf(Convert.increaseInt(popDef.yhspace, 5).substring(4));
		else
			sg.str3 = info.str1;
		// 将商品属性码,促销规则加入SaleGoodsDef里
		sg.str3 += (";" + goods.specinfo);
		sg.str3 += (";" + popDef.memo);
		sg.str3 += (";" + popDef.poppfjzkl);
		sg.str3 += (";" + popDef.poppfjzkfd);
		sg.str3 += (";" + popDef.poppfj);

		// 只有找到了规则促销单，就记录到小票
		if (!info.str1.equals("0000"))
		{
			sg.zsdjbh = popDef.djbh;
			sg.zszkfd = popDef.poplsjzkfd;
		}

		if (goods.str2 != null && (goods.str2.trim().length() > 0) && (goods.str2.charAt(0) == 'Y'))
		{
			sg.name = "(红)" + sg.name;
			goods.isvipzk = 'N';
		}
	}

	public void calcVIPZK(int index)
	{
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);

		// 未刷卡
		if (!checkMemberSale() || curCustomer == null)
			return;

		// 非零售开票
		if (!saletype.equals(SellType.RETAIL_SALE))
		{
			goodsDef.hyj = 1;
			return;
		}

		// 查询商品VIP折上折定义
		double zk = (saleGoodsDef.hjje - getZZK(saleGoodsDef)) / saleGoodsDef.hjje;

		GoodsPopDef popDef = new GoodsPopDef();
		if (((Bcrm_DataService) DataService.getDefault()).findHYZK(popDef, saleGoodsDef.code, curCustomer.type, saleGoodsDef.gz, saleGoodsDef.catid, saleGoodsDef.ppcode, goodsDef.specinfo))
		{
			// 有柜组和商品的VIP折扣定义
			goodsDef.hyj = popDef.pophyj;
		}
		else
		{
			// 无柜组和商品的VIP折扣定义,以卡类别的折扣率为VIP打折标准
			goodsDef.hyj = curCustomer.zkl;
		}

		// 折扣门槛
		double zkmk = popDef.num1;

		// new MessageBox("折扣门槛：" + String.valueOf(zkmk));

		// new MessageBox("折扣：" + String.valueOf(zk));

		if (zk < zkmk)
		{
			goodsDef.hyj = 1;
			return;
		}

		// new MessageBox(String.valueOf(goodsDef.hyj));
	}

	public int existPayment(String code, String account, boolean overmode)
	{
		if (!isRefundStatus() && DataService.getDefault().searchPayMode(code).type == '5' && account.equals("0000"))
		{
			return -1;
		}
		else
		{
			return super.existPayment(code, account, overmode);
		}
	}

	public boolean paymentApportion(SalePayDef spay, Payment payobj)
	{
		// 加入解百特殊金卡工程断电保护
		String path = "c:\\gmc\\answer.txt";

		if (PathFile.fileExist(path))
		{
			PathFile.deletePath(path);

			if (PathFile.fileExist(path))
			{
				new MessageBox(path + "已经被其他程序锁住，请联系电脑部解决");
			}
		}

		return super.paymentApportion(spay, payobj);
	}

	public void paySellCancel_Extend()
	{

	}

	public void calcAllRebate(int index)
	{
		char zszflag = 'Y';
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);

		// hzjb
		saleGoodsDef.plzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(saleGoodsDef.num5, saleGoodsDef.sl));

		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		GoodsPopDef popDef = (GoodsPopDef) crmPop.elementAt(index);

		// 指定小票退货时不重算优惠价和会员价
		if (isSpecifyBack(saleGoodsDef)) { return; }

		// 批发销售不计算
		if (SellType.ISBATCH(saletype)) { return; }

		if (SellType.ISEARNEST(saletype)) { return; }

		// 削价商品和赠品不计算
		if ((saleGoodsDef.flag == '3') || (saleGoodsDef.flag == '1')) { return; }

		saleGoodsDef.hyzke = 0;
		saleGoodsDef.hyzkfd = goodsDef.hyjzkfd;
		saleGoodsDef.yhzke = 0;
		saleGoodsDef.yhzkfd = 0;
		saleGoodsDef.zszke = 0;

		// 促销优惠
		if (goodsDef.poptype != '0')
		{
			// 定价且是单品优惠
			if ((saleGoodsDef.lsj > 0) && ((goodsDef.poptype == '1') || (goodsDef.poptype == '7')))
			{
				// 促销折扣
				if ((saleGoodsDef.lsj > goodsDef.poplsj) && (goodsDef.poplsj > 0))
				{
					saleGoodsDef.yhzke = (saleGoodsDef.lsj - goodsDef.poplsj) * saleGoodsDef.sl;
					saleGoodsDef.yhzkfd = goodsDef.poplsjzkfd;
				}
			}
			else
			{
				// 促销折扣
				if ((1 > goodsDef.poplsjzkl) && (goodsDef.poplsjzkl > 0))
				{
					saleGoodsDef.yhzke = saleGoodsDef.hjje * (1 - goodsDef.poplsjzkl);
					saleGoodsDef.yhzkfd = goodsDef.poplsjzkfd;
				}
			}

			//
			saleGoodsDef.yhzke = ManipulatePrecision.doubleConvert(saleGoodsDef.yhzke, 2, 1);

			// 按价格精度计算折扣
			saleGoodsDef.yhzke = getConvertRebate(index, saleGoodsDef.yhzke);

			// 判断促销单是否允许折上折
			if (goodsDef.pophyjzkl % 10 >= 1)
				zszflag = 'Y';
			else
				zszflag = 'N';
		}

		// 是否进行VIP打折,通过CRM促销控制
		boolean vipzk = false;

		// 无CRM促销，以分期促销折上折标志为准
		if (popDef.yhspace == 0)
		{
			vipzk = true;
		}
		else
		// 存在CRM促销
		{
			// 不享用VIP折扣,不进行VIP打折
			if (popDef.pophyjzkl == 0)
			{
				vipzk = false;
			}
			else
			// 享用VIP折扣，进行VIP折上折
			{
				vipzk = true;
				zszflag = 'Y';
			}
		}

		// 存在会员卡， 商品允许VIP折扣， CRM促销单允许享用VIP折扣
		if (checkMemberSale() && curCustomer != null && goodsDef.isvipzk == 'Y' && vipzk && curCustomer.iszk == 'Y')
		{
			// 获取VIP折扣率定义
			calcVIPZK(index);

			// 有折扣,进行折上折
			if (getZZK(saleGoodsDef) >= 0.01 && goodsDef.hyj < 1.00)
			{
				// 需要折上折
				if (zszflag == 'Y')
				{
					saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert((1 - goodsDef.hyj) * (saleGoodsDef.hjje - getZZK(saleGoodsDef)), 2, 1);
				}
				else
				{
					double zkl = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - getZZK(saleGoodsDef)) / saleGoodsDef.hjje, 2, 1);

					// 商品不折上折时，取商品的hyj和综合折扣较低者
					if (goodsDef.hyj < zkl)
					{
						zkl = ManipulatePrecision.doubleConvert((1 - goodsDef.hyj) * saleGoodsDef.hjje, 2, 1);
						if (zkl > getZZK(saleGoodsDef))
						{
							saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert(zkl - getZZK(saleGoodsDef), 2, 1);
						}
					}
				}
			}
			else
			{
				// 无折扣,按商品缺省会员折扣打折
				saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert((1 - goodsDef.hyj) * saleGoodsDef.hjje, 2, 1);
			}

			// 按价格精度计算折扣
			saleGoodsDef.hyzke = getConvertRebate(index, saleGoodsDef.hyzke);
		}
	}

	public void addSaleGoodsObject(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		if (findKeyValue(sg, goods, info))
		{
			super.addSaleGoodsObject(sg, goods, info);
		}
	}

	// HZJB 要求传入是否为电子秤码
	public String convertDzcmScsj(String dzcmscsj, boolean isdzcm)
	{
		String scsj = super.convertDzcmScsj(dzcmscsj, isdzcm);

		if (isdzcm)
		{
			scsj = "Y;" + scsj;
		}

		return scsj;
	}

	public String convertDzcmBarcode(GoodsDef goods, String barcode, boolean isdzcm)
	{
		if (isdzcm)
			return goods.barcode;
		else
			return barcode;
	}

	public void calcBatchRebate(int index)
	{
	}

	public boolean paySellStart()
	{
		if (super.paySellStart())
		{
			// 未显示方式
			checkBankPayment();

			return true;
		}

		return false;
	}

	public void execCustomKey4(boolean keydownonsale)
	{
		try
		{
			String input = "";
			Njys_PaymentBankFunc func = new Njys_PaymentBankFunc();
			input = func.XYKgetRequest(PaymentBank.XYKJZ, 0, "", "", "", "", "", "", null);
			func.creditTransABC(input);
			func.XYKPrintDoc();
		}
		catch (Exception er)
		{
			er.printStackTrace();

			return;
		}
	}

	public boolean payAccount(PayModeDef mode, String money)
	{
		checkBankPayment();

		if (GlobalInfo.sysPara.customerUnpayment != null && GlobalInfo.sysPara.customerUnpayment.trim().length() > 0 && curCustomer != null && curCustomer.iszk == 'Y')
		{
			String[] row = GlobalInfo.sysPara.customerUnpayment.split(",");
			for (int i = 0; i < row.length; i++)
			{
				if (row[i].equals(mode.code))
				{
					new MessageBox("[" + mode.code + "]" + mode.name + " 不能在享用VIP折扣的情况下付款");
					return false;
				}
			}
		}

		return super.payAccount(mode, money);
	}

	public boolean goToNextPaymode(PayModeDef paymode)
	{
		return false;
	}

	public boolean checkBankPayment()
	{
		// 如果存在answer.txt 提示回收
		String path = "c:\\gmc\\answer.txt";
		boolean del = false;

		if (PathFile.fileExist(path))
		{
			BufferedReader br = null;

			try
			{
				br = CommonMethod.readFileGB2312(path);

				String line = null;

				while ((line = br.readLine()) != null)
				{
					String retcode = line.substring(0, 2);

					if (!retcode.equals("00"))
					{
						del = true;

						return false;
					}
					else
					{
						String cardno = Convert.newSubString(line, 2, 21);
						String crc = Convert.newSubString(line, 21, 22);

						if (crc.equals("O"))
						{
							del = true;

							return false;
						}

						String je = Convert.newSubString(line, 22, 34);
						double j = Double.parseDouble(je);
						j = ManipulatePrecision.mul(j, 0.01);

						double yje = j;
						long oldtrace = Long.parseLong(Convert.newSubString(line, 34, 40));
						long trace = Long.parseLong(Convert.newSubString(line, 40, 52));
						String bankinfo = line.substring(52);

						String bankno = bankinfo.substring(0, 3);
						bankinfo = bankinfo.substring(3);

						SalePayDef pay = new SalePayDef();
						pay.payname = bankinfo;
						pay.paycode = "0300";
						pay.flag = '1';
						pay.ybje = yje;
						pay.hl = 1;
						pay.je = yje;
						pay.str1 = String.valueOf(trace);
						pay.payno = String.valueOf(cardno);
						pay.idno = bankno;

						int verity = new MessageBox("是否导入参考号后6位为" + trace + "的银联交易 \n 1 - 是 2 - 放弃", null, false).verify();

						if (verity == GlobalVar.Key1)
						{
							PaymentBank bank = new PaymentBank(DataService.getDefault().searchPayMode("0300"), this);
							bank.salepay = pay;

							if (crc.equals("C"))
							{
								// 写入日志
								BankLogDef bld = null;
								PaymentBankFunc bankfunc = new PaymentBankFunc();
								bankfunc.WriteRequestLog(PaymentBank.XYKXF, pay.je, "", "", "");
								bld = bankfunc.getBankLog();
								bld.cardno = pay.payno;
								bld.trace = trace;
								bld.bankinfo = bankno + bankinfo;
								bld.retcode = "00";
								bld.oldtrace = oldtrace;
								bld.retbz = 'Y';
								bld.net_bz = 'N';
								bld.retmsg = "收银员导入银联交易";

								if (NetService.getDefault().sendBankLog(bld))
								{
									bld.net_bz = 'Y';
								}

								if (!AccessDayDB.getDefault().updateBankLog(bld))
								{
									new MessageBox("导入异常付款日志失败");
									AccessDayDB.getDefault().writeWorkLog("付款导入" + pay.paycode + " 名称:" + pay.payname + " 金额:" + pay.je + " 类型:消费" + " 卡号:" + pay.payno, "0300");
								}

								salePayment.add(pay);
								payAssistant.add(bank);
								del = true;

								return true;
							}
							else if (crc.equals("D"))
							{
								boolean exsit = false;

								for (int i = 0; i < salePayment.size(); i++)
								{
									SalePayDef pay1 = (SalePayDef) salePayment.elementAt(i);

									if (pay1.str1.equals(pay.str1))
									{
										exsit = true;
										salePayment.removeElementAt(i);
										payAssistant.removeElementAt(i);

										break;
									}
								}

								if (exsit)
								{
									// 写入日志
									BankLogDef bld = null;
									PaymentBankFunc bankfunc = new PaymentBankFunc();
									bankfunc.WriteRequestLog(PaymentBank.XYKCX, pay.je, "", "", "");
									bld = bankfunc.getBankLog();
									bld.cardno = pay.payno;
									bld.trace = trace;
									bld.bankinfo = bankno + bankinfo;
									bld.retcode = "00";
									bld.retbz = 'Y';
									bld.oldtrace = oldtrace;
									bld.net_bz = 'N';
									bld.retmsg = "收银员导入银联交易";

									if (NetService.getDefault().sendBankLog(bld))
									{
										bld.net_bz = 'Y';
									}

									if (!AccessDayDB.getDefault().updateBankLog(bld))
									{
										new MessageBox("导入异常付款日志失败");
										AccessDayDB.getDefault().writeWorkLog("付款导入" + pay.paycode + " 名称:" + pay.payname + " 金额:" + pay.je + " 类型:退货" + " 卡号:" + pay.payno, "0300");
									}
								}

								del = true;

								return true;
							}
						}
						else if (verity == GlobalVar.Key2)
						{
							// 写入日志
							BankLogDef bld = null;
							PaymentBankFunc bankfunc = new PaymentBankFunc();
							if (crc.equals("C"))
							{
								bankfunc.WriteRequestLog(98, pay.je, "", "", "");
							}
							else if (crc.equals("D"))
							{

								bankfunc.WriteRequestLog(99, pay.je, "", "", "");
							}

							bld = bankfunc.getBankLog();
							bld.cardno = pay.payno;
							bld.trace = trace;
							bld.bankinfo = bankno + bankinfo;
							bld.retcode = "00";
							bld.oldtrace = oldtrace;
							bld.retbz = 'Y';
							bld.net_bz = 'N';

							if (bld.type.equals("98"))
							{
								bld.retmsg = "放弃导入银联消费付款方式";
							}
							else
							{
								bld.retmsg = "放弃导入银联撤销付款方式";
							}

							if (NetService.getDefault().sendBankLog(bld))
							{
								bld.net_bz = 'Y';
							}

							if (!AccessDayDB.getDefault().updateBankLog(bld))
							{
								new MessageBox("导入异常付款日志失败");
								AccessDayDB.getDefault().writeWorkLog("付款导入" + pay.paycode + " 名称:" + pay.payname + " 金额:" + pay.je + " 类型:" + crc + " 卡号:" + pay.payno, "0300");
							}
							del = true;

							return false;
						}

						return false;
					}
				}
			}
			catch (Exception er)
			{
				return false;
			}
			finally
			{
				if (br != null)
				{
					try
					{
						br.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}

				if (PathFile.fileExist(path) && del)
				{
					PathFile.deletePath(path);
				}
			}
		}

		return false;
	}

	public String getVipInfoLabel()
	{
		if (curCustomer == null)
		{
			return "";
		}
		else
		{
			return "[" + curCustomer.code + "]";
		}
	}

	// 新CRM计算满减
	public boolean doCrmPop()
	{
		boolean haveCrmPop = false;

		// 默认总是不进行分摊付款的
		apportionPay = false;

		// 先总是无满减规则方式的付款
		isPreparePay = payNormal;

		if (GlobalInfo.sysPara.rulepop == 'N') { return false; }

		if (!SellType.ISSALE(saletype)) { return false; }

		if (SellType.ISEARNEST(saletype)) { return false; }

		// 先进行直接打折
		int i = 0;
		double hjzszk = 0;

		for (i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
			String mjrule = ((SpareInfoDef) goodsSpare.elementAt(i)).str1;
			GoodsPopDef goodsPop1 = (GoodsPopDef) crmPop.elementAt(i);
			double zkl = ((GoodsDef) goodsAssistant.elementAt(i)).maxzkl;

			if (mjrule.charAt(0) == '1')
			{
				double sj = saleGoodsDef.hjje - getZZK(saleGoodsDef);
				double dz = ManipulatePrecision.mul(sj, goodsPop1.poplsjzkl);

				double minje = saleGoodsDef.hjje * zkl;

				if (dz < minje)
				{
					saleGoodsDef.zszke = ManipulatePrecision.sub(sj, minje);
					saleGoodsDef.zszkfd = goodsPop1.poplsjzkfd;
					saleGoodsDef.zsdjbh = goodsPop1.djbh;
				}
				else
				{
					saleGoodsDef.zszke = ManipulatePrecision.sub(sj, dz);
					saleGoodsDef.zszkfd = goodsPop1.poplsjzkfd;
					saleGoodsDef.zsdjbh = goodsPop1.djbh;
				}

				getZZK(saleGoodsDef);
				hjzszk += saleGoodsDef.zszke;

				haveCrmPop = true;
			}
		}

		if (hjzszk > 0)
		{
			// 重算应收
			calcHeadYsje();

			// 刷新商品列表
			saleEvent.updateTable(getSaleGoodsDisplay());

			new MessageBox("有商品参加活动促销，总共可打折 " + ManipulatePrecision.doubleToString(hjzszk));
		}

		// 检查促销折扣控制 如果低于折扣率,不进行满减,返券,返礼促销
		for (int j = 0; j < saleGoods.size(); j++)
		{
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(j);
			String mjrule = ((SpareInfoDef) goodsSpare.elementAt(j)).str1;
			GoodsPopDef goodsPop1 = (GoodsPopDef) crmPop.elementAt(j);
			double zkl = (saleGoodsDef.hjje - getZZK(saleGoodsDef)) / saleGoodsDef.hjje;

			if (zkl < goodsPop1.pophyjzkfd)
			{
				mjrule = mjrule.charAt(0) + "000";
				((SpareInfoDef) goodsSpare.elementAt(j)).str1 = mjrule;
				saleGoodsDef.str3 = mjrule + saleGoodsDef.str3.substring(saleGoodsDef.str3.indexOf(";"));
			}
		}

		// 再查找是否存在满减或减现
		int j = 0;
		Vector set = new Vector();
		CalcRulePopDef calPop = null;

		// 先按商品分组促销规则
		for (i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
			GoodsDef goodsDef = ((GoodsDef) goodsAssistant.elementAt(i));
			GoodsPopDef goodsPop = (GoodsPopDef) crmPop.elementAt(i);
			String mjrule = ((SpareInfoDef) goodsSpare.elementAt(i)).str1;
			String ruleCode = goodsDef.specinfo;

			// 选择了不参与减现继续下一个商品
			if (mjrule.equals("N") || (mjrule.charAt(1) != '1'))
			{
				continue;
			}

			// 查找是否相同促销规则
			for (j = 0; j < set.size(); j++)
			{
				calPop = (CalcRulePopDef) set.elementAt(j);

				int oldIndex = Integer.parseInt((String) calPop.row_set.elementAt(0));
				SaleGoodsDef saleGoodsDef1 = (SaleGoodsDef) saleGoods.elementAt(oldIndex);
				GoodsDef goodsDef1 = ((GoodsDef) goodsAssistant.elementAt(oldIndex));
				GoodsPopDef goodsPop1 = (GoodsPopDef) crmPop.elementAt(oldIndex);
				String mjrule1 = ((SpareInfoDef) goodsSpare.elementAt(oldIndex)).str1;

				// 判断是否为同规则促销
				if (isSamePop(saleGoodsDef, goodsDef, goodsPop, mjrule, saleGoodsDef1, goodsDef1, goodsPop1, mjrule1))
				{
					calPop.row_set.add(String.valueOf(i));

					break;
				}
			}

			if (j >= set.size())
			{
				calPop = new CalcRulePopDef();
				calPop.code = saleGoodsDef.code;
				calPop.gz = saleGoodsDef.gz;
				calPop.uid = saleGoodsDef.uid;
				calPop.rulecode = ruleCode;
				calPop.catid = saleGoodsDef.catid;
				calPop.ppcode = saleGoodsDef.ppcode;
				calPop.popDef = goodsPop;
				calPop.row_set = new Vector();
				calPop.row_set.add(String.valueOf(i));
				set.add(calPop);
			}
		}

		// 无规则促销
		if (set.size() <= 0) { return haveCrmPop; }

		// 引用促销规则集合，用于付款分摊时进行判断，只有一个规则自动平摊到每个商品
		rulePopSet = set;

		// 检查是否要除券
		boolean havepaycw = false;

		for (i = 0; i < set.size(); i++)
		{
			calPop = (CalcRulePopDef) set.elementAt(i);

			if (calPop.popDef.catid.equals("Y"))
			{
				/*
				 * if (set.size() >= 2) { new
				 * MessageBox("本笔交易存在不同的活动促销\n\n请分单进行收银"); doRulePopExit = true;
				 * return false; } if (calPop.row_set.size() !=
				 * saleGoods.size()) { new
				 * MessageBox("本笔交易部分商品参与活动促销,部分不参与\n\n请分单进行收银"); doRulePopExit
				 * = true; return false; }
				 */
				havepaycw = true;

				break;
			}
		}

		// int cxgz = set.size();
		// 循环两次
		// 第一次先检查是否有满足条件的规则,如果没有则直接返回
		// 第二次检查除券外是否还有满足条件的规则,如果不需要除券,则只用循环一次
		int nwhile = 1;

		do
		{
			// 开始计算商品分组参与计算的合计金额
			for (i = 0; i < set.size(); i++)
			{
				// 如果是能进入第二次循环,说明有交易金额是满足促销条件的规则促销
				// 如果需要扣除券付款,先输入券付款方式
				if ((nwhile >= 2) && havepaycw)
				{
					// 提示先输入券付款
					int ret = new MessageBox("客户是否用券付款？\n 不用券 - 回车 / 用券 - 1").verify();
					if (ret == GlobalVar.Exit)
					{
						doRulePopExit = true;
						return false;
					}
					else if (ret == GlobalVar.Key1)
					{
						// 开始预付除外付款方式
						isPreparePay = payPopPrepare;

						// 打开付款窗口
						new SalePayForm().open(saleEvent.saleBS);

						if (salePayment.size() <= 0)
						{
							doRulePopExit = true;

							return false;
						}

						/*
						 * //显示所有指定分摊金额 String line = "";
						 * 
						 * for (int x = 0; x < saleGoods.size(); x++) {
						 * SaleGoodsDef saledef = (SaleGoodsDef)
						 * saleGoods.elementAt(x);
						 * 
						 * if (saledef.str2.trim().length() > 0) { //计算除券金额
						 * String[] str = saledef.str2.split(","); double num1 =
						 * 0;
						 * 
						 * for (int y = 0; y < str.length; y++) { if ((str[y] !=
						 * null) && (str[y].length() > 0)) { String[] str1 =
						 * str[y].split(":"); double num2 = 0;
						 * 
						 * try { num2 = Double.parseDouble(str1[1]); } catch
						 * (Exception er) { er.printStackTrace(); }
						 * 
						 * num1 += num2; } }
						 * 
						 * String line1 = Convert.appendStringSize("",
						 * saledef.name, 1, 20, 60); line1 =
						 * Convert.appendStringSize(line1, "可参与促销金额为", 21, 19,
						 * 60); line1 = Convert.appendStringSize(line1,
						 * ManipulatePrecision.doubleToString(saledef.hjje -
						 * getZZK(saledef) - num1), 41, 10, 60, 1); line +=
						 * (line1 + "\n"); } }
						 * 
						 * if (line.trim().length() > 0) { new MessageBox(line);
						 * }
						 */

						// 付款完成，开始新交易
						if (this.saleFinish)
						{
							sellFinishComplete();

							// 预先付款就已足够,不再继续后续付款
							doRulePopExit = true;
							return false;
						}
					}

					// 进入实付剩余付款方式,只允许非券付款方式进行付款
					isPreparePay = payPopOther;

					// 券除外付款只输入一次
					havepaycw = false;
				}

				// 计算同规则商品参与促销的合计
				calPop = (CalcRulePopDef) set.elementAt(i);

				double sphj = 0;

				for (j = 0; j < calPop.row_set.size(); j++)
				{
					SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
					sphj += (saleGoodsDef.hjje - getZZK(saleGoodsDef));
				}

				// 如果只有一组促销规则,计算前存在的付款方式都算需要除外的付款
				// 如果有多个组促销规则,除外金额为该商品已分摊的付款金额
				/**
				 * if (cxgz <= 1) { double cwpayje = 0; for
				 * (j=0;j<salePayment.size();j++) { SalePayDef pay =
				 * (SalePayDef)salePayment.elementAt(j); cwpayje +=
				 * (pay.je-pay.num1); } cwpayje -= salezlexception; sphj =
				 * ManipulatePrecision.doubleConvert(sphj - cwpayje,2,1); } else
				 * {
				 */
				for (j = 0; j < calPop.row_set.size(); j++)
				{
					SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));

					if (spinfo.payft == null)
					{
						continue;
					}

					for (int n = 0; n < spinfo.payft.size(); n++)
					{
						String[] s = (String[]) spinfo.payft.elementAt(n);
						sphj -= Convert.toDouble(s[3]);
					}
				}

				// }
				if (sphj <= 0)
				{
					set.remove(i);
					i--;

					continue;
				}

				// 满减限额
				double limitje = 0;

				if (calPop.popDef.sl <= 0)
				{
					limitje = 99999999;
				}
				else
				{
					limitje = calPop.popDef.sl;
				}

				// 检查是否满足条件
				if (calPop.popDef.gz.equals("1")) // 按金额满减
				{
					double mjje = 0;
					calPop.popje = sphj;

					double bcje = 0;
					int num = 0;

					// 检查是否存在促销条件,现在全部的条件都在此地设定 用分号分隔
					if ((calPop.popDef.str3 != null) && (calPop.popDef.str3.trim().length() > 0))
					{
						String[] row = calPop.popDef.str3.split(";");

						for (int c = row.length - 1; c >= 0; c--)
						{
							if ((row[c] == null) || (row[c].split(",").length != 4))
							{
								continue;
							}

							double a = Convert.toDouble(row[c].split(",")[0]); // 参加下限
							double b = Convert.toDouble(row[c].split(",")[1]); // 参加上限
							double t = Convert.toDouble(row[c].split(",")[2]); // 满减条件
							double je = Convert.toDouble(row[c].split(",")[3]); // 满减金额

							if ((je == 0) || (b == 0))
							{
								continue;
							}

							if ((sphj >= a) && (sphj <= b))
							{
								if (t == 0)
								{
									if (je < limitje)
									{
										mjje = je;
									}
									else
									{
										mjje = limitje;
									}

									break;
								}

								num = ManipulatePrecision.integerDiv(sphj, t);

								if (num > 0)
								{
									bcje = num * je;
								}

								if (bcje > limitje)
								{
									bcje = limitje;
								}

								mjje = bcje;

								break;
							}
							else
							{
								continue;
							}
						}
					}

					if (mjje > 0)
					{
						calPop.mult_Amount = mjje;
					}
					else
					{
						set.remove(i);
						i--;
					}
				}
				else if (calPop.popDef.gz.equals("2")) // 按百分比减现
				{
					// 无效的减现比例
					if ((calPop.popDef.poplsjzkl <= 0) || (calPop.popDef.poplsjzkl >= 1) || ((sphj * calPop.popDef.poplsjzkl) > limitje))
					{
						set.remove(i);
						i--;
					}
					else
					{
						calPop.popje = sphj;
					}
				}
				else
				{
					set.remove(i);
					i--;
				}
			}

			// 无有效的、满足条件的规则促销
			if (set.size() <= 0) { return haveCrmPop; }

			// 循环计数,如果不需要除券,则不用进行第二次循环
			nwhile++;

			if (!havepaycw)
			{
				nwhile++;
			}
		} while (nwhile <= 2);

		// 分摊满减折扣金额
		for (i = 0; i < set.size(); i++)
		{
			calPop = (CalcRulePopDef) set.elementAt(i);

			//
			double je = 0;
			double hj = 0;

			// 按金额满减
			if (calPop.popDef.gz.equals("1"))
			{
				je = calPop.mult_Amount;

				// 提示满减规则
				new MessageBox("参加活动的金额为 " + ManipulatePrecision.doubleToString(calPop.popje) + " 元\n\n减现 " + ManipulatePrecision.doubleToString(je) + " 元");
			}

			// 按百分比减现
			if (calPop.popDef.gz.equals("2"))
			{
				je = calPop.popje * calPop.popDef.poplsjzkl;

				// 提示满减规则
				new MessageBox("现有促销减现 " + ManipulatePrecision.doubleToString(calPop.popDef.poplsjzkl * 100) + "%\n\n你目前可参加活动的金额为 " + ManipulatePrecision.doubleToString(calPop.popje) + " 元\n\n你目前可以减现 " + ManipulatePrecision.doubleToString(je) + " 元");
			}

			// 记录规则促销单据信息
			for (j = 0; j < calPop.row_set.size(); j++)
			{
				SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));

				hj += (saleGoodsDef.hjje - getZZK(saleGoodsDef));
				saleGoodsDef.zsdjbh = calPop.popDef.djbh;
				saleGoodsDef.zszkfd = calPop.popDef.poplsjzkfd;
			}

			// 分摊满减折扣到各商品
			double yfd = 0;

			for (j = 0; j < calPop.row_set.size(); j++)
			{
				SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
				GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
				// GoodsDef goodsdef = (goods)
				// 把剩余未分摊金额，直接分摊到最后一个商品
				double lszszk = 0;

				if (j == (calPop.row_set.size() - 1))
				{
					lszszk = ManipulatePrecision.doubleConvert(je - yfd, 2, 1);
				}
				else
				{
					lszszk = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - getZZK(saleGoodsDef)) / hj * je, 2, 1);
				}

				lszszk = getConvertPrice(lszszk, goodsDef);
				saleGoodsDef.zszke += lszszk;
				getZZK(saleGoodsDef);

				// 计算已分摊的金额
				yfd += lszszk;
			}
		}

		// 重算应收
		calcHeadYsje();

		// 刷新商品列表
		saleEvent.updateTable(getSaleGoodsDisplay());

		// 提示收银员查看满减结果
		// new MessageBox("请核对促销活动的相关折扣金额!");

		//
		haveCrmPop = true;

		return haveCrmPop;
	}

	public Vector getSalePaymentDisplay()
	{
		Vector v = new Vector();
		String[] detail = null;
		SalePayDef saledef = null;

		for (int i = 0; i < salePayment.size(); i++)
		{
			saledef = (SalePayDef) salePayment.elementAt(i);

			detail = new String[3];
			detail[0] = "[" + saledef.paycode + "]" + saledef.payname;

			if (saledef.paycode.equals("0400") && (saledef.payno.length() > 8))
			{
				detail[1] = saledef.payno.substring(0, 8);
			}
			else
			{
				detail[1] = saledef.payno;
			}

			detail[2] = ManipulatePrecision.doubleToString(saledef.ybje);
			v.add(detail);
		}

		// 在要刷新付款列表时,写入断点数据
		writeBrokenData();

		return v;
	}

	public boolean isSamePop(SaleGoodsDef salegoods1, GoodsDef goods1, GoodsPopDef popDef1, String mjrule1, SaleGoodsDef salegoods2, GoodsDef goods2, GoodsPopDef popDef2, String mjrule2)
	{
		if ((popDef1.memo.indexOf(",") == 0) || (popDef2.memo.indexOf(",") == 0)) { return false; }

		// 如果商品的满减规则、满送规则不通，或者任选模式下，选择的不一样,都要进行券分摊动作
		if (!popDef1.memo.equalsIgnoreCase(popDef2.memo) || !mjrule1.equalsIgnoreCase(mjrule2))
		{
			apportionPay = true;
		}

		// 截取出满减规则
		String mjdh1 = popDef1.memo.substring(0, popDef1.memo.indexOf(","));
		String mjdh2 = popDef2.memo.substring(0, popDef2.memo.indexOf(","));

		// 选的是同一个满减规则，且允许跨柜则认为是同一个规则，要进行合计
		if (mjdh1.equalsIgnoreCase(mjdh2) && ((popDef1.ppcode.equalsIgnoreCase("Y") && popDef2.ppcode.equalsIgnoreCase("Y")) || ((!popDef1.ppcode.equalsIgnoreCase("Y") || !popDef2.ppcode.equalsIgnoreCase("Y")) && salegoods1.gz.equalsIgnoreCase(salegoods2.gz))))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean memberGrant()
	{
		if (super.memberGrant())
		{
			int ret = new MessageBox("本单是否享用VIP折扣？1 - 是 / 2 - 否", null, true).verify();
			if (ret != GlobalVar.Key1 && ret != GlobalVar.Enter)
			{
				curCustomer.iszk = 'N';
			}
			
//			curCustomer.valuememo 保存最大可用积分 curCustomer.value5 保存常规积分 这样做不用修改积分消费
			double cgjf = curCustomer.valuememo;
			curCustomer.valuememo = curCustomer.value5;
			curCustomer.valuememo = Math.min(Convert.toDouble(curCustomer.valstr2),curCustomer.valuememo);
			curCustomer.value5 = cgjf;
			
			return true;
		}
		return false;
	}

	// 由于打印赠券，需要验证，方便管理代码，将销售界面里重打印键关闭
	public void rePrint()
	{
		return;
	}

	public boolean findKeyValue(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		if (goods == null)
			return true;

		if (!goods.str1.equals("Y")) { return true; }

		while (true)
		{
			// 电子称商品K值为 1
			if (GlobalInfo.syjDef.issryyy == 'N')
			{
				sg.plzkfd = 1;

				return true;
			}

			TextBox txt = new TextBox();
			StringBuffer buff = new StringBuffer();
			buff.append("1");

			if (!txt.open("本商品必须输入KEY值才能销售", "Key", "请输入KEY值", buff, 0, 0, false, TextBox.IntegerInput)) { return false; }

			double[] area = new double[2];

			if (!((Hzjb_NetService) NetService.getDefault()).findKeyValue(area, goods.code, sg.gz, buff.toString()))
			{
				continue;
			}

			if ((area[0] == 1) && (area[1] == 1))
			{
				sg.plzkfd = Convert.toDouble(buff.toString());

				return true;
			}

			double bottom1 = ManipulatePrecision.mul(sg.jg, area[0]);


            String bottom = ManipulatePrecision.doubleToString(bottom1);
            double top1 = ManipulatePrecision.mul(sg.jg, area[1]);
            top1 = ManipulatePrecision.doubleConvert(top1 - 0.01);
            String top = ManipulatePrecision.doubleToString(top1);
            StringBuffer buffer = new StringBuffer();

			if (bottom1 <= 0)
			{
				bottom1 = 0.01;
			}


			if (!txt.open("请输入KEY对应的商品金额", "金额", "金额范围 (" + bottom + " , " + top + ")", buffer, bottom1, top1, true, TextBox.DoubleInput, -1, "金额范围 (" + bottom1 + " , " + top1 + ")", -1))
			{
				continue;
			}

			double je = Convert.toDouble(buffer);
			sg.num5 = ManipulatePrecision.sub(sg.jg, je);
			sg.plzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(sg.num5, sg.sl));
			sg.plzkfd = Convert.toDouble(buff.toString());

			return true;
		}
	}

	// 客户化分摊方式
	public Vector customApportion(SalePayDef spay, Payment payobj)
	{
		Vector v = new Vector();

		if (!CreatePayment.getDefault().isPaymentFjk(spay.paycode))
		{
			double leftje = spay.je - spay.num1;
			int i = 0;

			for (i = 0; i < (saleGoods.size() - 1); i++)
			{
				SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);

				double je = ManipulatePrecision.doubleConvert((sg.hjje - sg.hjzk) / (saleHead.hjzke - saleHead.hjzke) * spay.je);
				leftje = ManipulatePrecision.doubleConvert(leftje - je);

				if (je > 0)
				{
					String[] row = { sg.barcode, sg.name, "0", "0", String.valueOf(je), String.valueOf(i) };
					v.add(row);
				}
			}

			if (saleGoods.size() > 0)
			{
				SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);
				String[] row = { sg.barcode, sg.name, "0", "0", String.valueOf(leftje), String.valueOf(i) };
				v.add(row);
			}
		}
		else
		{

			PaymentFjk ob = null;

			try
			{
				ob = (PaymentFjk) payobj;
			}
			catch (Exception er)
			{
				er.printStackTrace();
			}

			double hjzje = 0;
			Vector row1 = new Vector();

			for (int i = 0; i < saleGoods.size(); i++)
			{
				// 付款方式的规则和商品的CRM促销规则一致,商品才可收
				if (!ob.checkFjkRuleCode(i))
				{
					continue;
				}

				SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);
				if (sg.memo.trim().length() <= 0)
					continue;
				double maxsqje = 0;
				if (spay.payname.equals(ob.getAccountNameA()))
				{
					maxsqje = Convert.toDouble(sg.memo.split(",")[0]);
				}
				else if (spay.payname.equals(ob.getAccountNameB()))
				{
					maxsqje = Convert.toDouble(sg.memo.split(",")[1]);
				}
				else
				{
					maxsqje = sg.hjje - getZZK(sg);
				}

				if (maxsqje <= 0)
					continue;

				hjzje += maxsqje;
				row1.add(String.valueOf(i));
			}

			int i = 0;
			double leftje = spay.je - spay.num1;

			for (i = 0; i < (row1.size() - 1); i++)
			{
				SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt(String.valueOf(row1.elementAt(i))));

				if (sg.memo.trim().length() <= 0)
					continue;
				double maxsqje = 0;
				if (spay.payname.equals(ob.getAccountNameA()))
				{
					maxsqje = Convert.toDouble(sg.memo.split(",")[0]);
				}
				else if (spay.payname.equals(ob.getAccountNameB()))
				{
					maxsqje = Convert.toDouble(sg.memo.split(",")[1]);
				}
				else
				{
					maxsqje = sg.hjje - getZZK(sg);
				}

				double je = ManipulatePrecision.doubleConvert((maxsqje) / (hjzje) * spay.je);
				leftje = ManipulatePrecision.doubleConvert(leftje - je);

				if (je > 0)
				{
					String[] row = { sg.barcode, sg.name, "0", "0", String.valueOf(je), String.valueOf(row1.elementAt(i)) };
					v.add(row);
				}
			}

			if (row1.size() > 0)
			{
				SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt(String.valueOf(row1.elementAt(i))));
				String[] row = { sg.barcode, sg.name, "0", "0", String.valueOf(leftje), String.valueOf(row1.elementAt(i)) };
				v.add(row);
			}
		}

		return v;
	}

	// 检查商品是否允许折扣
	public boolean checkGoodsRebate(GoodsDef goodsDef)
	{
		if (super.checkGoodsRebate(goodsDef))
		{
			// 判断是否为红标商品
			if ((goodsDef.str2.trim().length() > 0) && (goodsDef.str2.trim().charAt(0) == 'Y')) { return false; }

			return true;
		}

		return false;
	}

	public String getFuncMenuByPaying()
	{
		// 付款窗口打开功能菜单只允许使用以下功能
		// 0806 信用卡余额查询
		// 0807 信用卡签购单重打
		// 0808 信用卡交易查询
		return null;
	}

	public void printSaleBill()
	{
		boolean enableLHBillMode = false;

		for (int i = 0; i < salePayment.size(); i++)
		{
			SalePayDef pay = (SalePayDef) salePayment.get(i);

			if (pay.paycode.equals("0402"))
			{
				enableLHBillMode = true;
				break;
			}
		}

		// 打印小票前先查询满赠信息并设置到打印模板供打印
		if (!SellType.ISEXERCISE(saletype))
		{
			DataService dataservice = (DataService) DataService.getDefault();
			Vector gifts = dataservice.getSaleTicketMSInfo(saleHead, saleGoods, salePayment);
			if (enableLHBillMode)
				Hzjb_LHSaleBillMode.getInstance(saleHead.djlb).setSaleTicketMSInfo(saleHead, gifts);
			else
				SaleBillMode.getDefault(saleHead.djlb).setSaleTicketMSInfo(saleHead, gifts);
		}

		// 恢复暂停状态的实时打印
		stopRealTimePrint(false);

		// 实时打印只打印剩余部分
		if (isRealTimePrint())
		{
			if (enableLHBillMode)
				Hzjb_LHSaleBillMode.getInstance(saleHead.djlb).setTemplateObject(saleHead, saleGoods, salePayment);
			else
				SaleBillMode.getDefault(saleHead.djlb).setTemplateObject(saleHead, saleGoods, salePayment);

			// 标记即扫即打结束
			Printer.getDefault().enableRealPrintMode(false);

			// 打印那些即扫即打未打印的商品
			for (int i = 0; i < saleGoods.size(); i++)
				realTimePrintGoods(null, i);

			// 打印即扫即打剩余小票部分
			if (enableLHBillMode)
				Hzjb_LHSaleBillMode.getInstance(saleHead.djlb).printRealTimeBottom();
			else
				SaleBillMode.getDefault(saleHead.djlb).printRealTimeBottom();

			//
			setHaveRealTimePrint(false);
		}
		else
		{
			if (enableLHBillMode)
			{
				Hzjb_LHSaleBillMode.getInstance(saleHead.djlb).setTemplateObject(saleHead, saleGoods, salePayment);
				Hzjb_LHSaleBillMode.getInstance(saleHead.djlb).printBill();
			}
			else
			{
				SaleBillMode.getDefault(saleHead.djlb).setTemplateObject(saleHead, saleGoods, salePayment);
				SaleBillMode.getDefault(saleHead.djlb).printBill();
			}

			// 打印整张小票

		}

		// 只在交易完成时打印一次移动离线充值券,因此无需放到小票模板中
		if (GlobalInfo.useMobileCharge)
		{
			PaymentBankCMCC pay = CreatePayment.getDefault().getPaymentMobileCharge(this.saleEvent.saleBS);
			if (pay != null)
				pay.printOfflineChargeBill(saleHead.fphm);
		}
	}
    
//  查找商品是否存在换购规则
    public void findJfExchangeGoods(int index)
    {
    	if (hhflag == 'Y')
    	{
    		new MessageBox("换货状态不允许使用积分换购");
    		return ;
    	}
    	// 无会员卡不进行积分换购
    	if (curCustomer == null)
    	{
    		new MessageBox("没有刷会员卡不允许积分换购");
    		return;
    	}

    	// 无0509付款方式,不能进行积分换购
		PayModeDef paymode = DataService.getDefault().searchPayMode("0509");
		if (paymode == null) 
		{
			new MessageBox("没有找到0509付款方式");
			return;
		}
		
    	// 查找积分换购商品规则
    	JfSaleRuleDef jfrd = new JfSaleRuleDef();
    	SaleGoodsDef saleGoodsDef = (SaleGoodsDef)saleGoods.get(index);
    	
    	if (!((Bcrm_DataService)DataService.getDefault()).getJfExchangeGoods(jfrd,saleGoodsDef.code,saleGoodsDef.gz,curCustomer.code,curCustomer.type))
    	{
    		return;
    	}
		
    	double jf1 = 0;
    	if (jfrd.num2 == 2)
    	{
    		jf1 = jfrd.num3;
    	}
    	else
    	{
    		// curCustomer.value5 保存常规积分
    		jf1 = curCustomer.value5;
    	}
    	
    	if ((saleGoodsDef.hjje - saleGoodsDef.hjzk) <= jfrd.money * saleGoodsDef.sl)
		{
			new MessageBox("当前商品销售金额小于等于兑换金额\n不能进行换购");
			return;
		}
		
		if (jf1 < jfrd.jf)
 		{
			new MessageBox("当前会员卡的积分小于换购积分\n不能进行换购");
			return;
 		}
		
		saleGoodsDef.name = "【换】" + saleGoodsDef.name; 
		
		double maxsl = -1;
		// 判断换购数量
		if (String.valueOf(jfrd.char1).length() > 0 && jfrd.char1 == 'Y')
		{
			double sum = 0;
	    	// 按商品行号查找对应的积分换购付款
	        for (int i = 0; i < saleGoods.size(); i++)
	        {
	            SpareInfoDef info = (SpareInfoDef)goodsSpare.elementAt(i);
	            SaleGoodsDef salegoods = (SaleGoodsDef) saleGoods.elementAt(i);
	            if (i == index) continue;
	            
	            if (info.char2 == 'Y' && salegoods.code.equals(saleGoodsDef.code))
	            {
	            	sum += salegoods.sl;
	            }
	        } 
	        
	        maxsl = ManipulatePrecision.doubleConvert(jfrd.num1 - sum);
	        
	        if (ManipulatePrecision.doubleConvert(sum + saleGoodsDef.sl) > jfrd.num1)
	        {
	        	// "包含此商品后，"+jfrd.str1+" 此规则已换购数量"+ManipulatePrecision.doubleConvert(sum + saleGoodsDef.sl)+"\n"
	        	new MessageBox( saleGoodsDef.code+" 超出最大可换购数量【"+jfrd.num1+"】");
	        	return;
	        }
		}
		
		// 提示是否进行换购
		MessageBox me = new MessageBox("您目前可用" + jfrd.jf + "积分加上" + ManipulatePrecision.doubleToString(jfrd.money) + "元\n换购该商品\n是否要进行换购?", null, true);
 		if (me.verify() != GlobalVar.Key1)
		{
 			return;
		}
 		
		// 弹出提示框			
		StringBuffer buffer = new StringBuffer();
		double max = ManipulatePrecision.doubleConvert((int)(jf1/jfrd.jf));
		
		// 如果存在限量
		if (maxsl > 0) max = Math.min(max, maxsl);
		
		buffer.append(max);
		do{
			if (new TextBox().open("请输入要兑换的数量","数量", "目前最大可兑换的数量为"+ManipulatePrecision.doubleToString(max), buffer, 1,max, true, TextBox.IntegerInput, -1))
			{
				double inputsl = Convert.toDouble(buffer.toString());
				if (!inputQuantity(index,inputsl))
				{
					continue;
				}
					
			}
			else
			{
				return ;
			}
			break;
		}while(true);
 		
 		//先删除换购付款
 		delJfExchangeByGoods(index);
 		
 		SaleGoodsDef sgd = (SaleGoodsDef)saleGoodsDef.clone();
 		
		// 生成积分换购付款方式
		PaymentCustJfSale pay = new PaymentCustJfSale(paymode,this);
		
		double jf = getDetailOverFlow(ManipulatePrecision.doubleConvert((sgd.hjje - sgd.hjzk) - (jfrd.money * sgd.sl)));
		if (pay != null && pay.createJfExchangeSalePay(jf,ManipulatePrecision.mul(jfrd.jf,sgd.sl),jfrd,index))
		{
	 		// 转换名称用于显示
	 		sgd.name = "(积分" + jfrd.jf + "+" + ManipulatePrecision.doubleToString(jfrd.money) + "元换购);" + sgd.name;
	 		
			// 在付款对象记录商品信息(要扣的积分,XX积分,兑单个商品XX金额	，换购规则单号,商品编码，商品数量)
			// pay.salepay.str2 = String.valueOf(saleGoods.size()) + "," + sgd.code;
			pay.salepay.idno += ","+jfrd.str1+","+sgd.code+","+sgd.sl+","+jfrd.num2+","+jfrd.str2;
			
			// 增加已付款
			addSalePayObject(pay.salepay,pay);
			
            SpareInfoDef info = (SpareInfoDef)goodsSpare.elementAt(index);
            
            // 积分换购商品标志
            info.char2 = 'Y';
            info.str3  = String.valueOf(pay.salepay.num5)+","+jfrd.str1;
            //记录积分扣回的分摊
            if (info.payft == null) info.payft = new Vector();
            String[] ft = new String[] {String.valueOf(pay.salepay.num5),pay.salepay.paycode,pay.salepay.payname,String.valueOf(jf)};
            info.payft.add(ft);
            
			// 计算剩余付款
			calcPayBalance();
			
			saleEvent.table.modifyRow(rowInfo(sgd), index);
		}
		else
		{
			new MessageBox("积分换购付款对象创建失败\n请删除商品后重新试一次!");
		}
		
		sgd = null;
    }
    
}
