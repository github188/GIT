package bankpay.Bank;

import java.io.BufferedReader;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.CustFilterDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Nbbh.Nbbh_DataService;
import custom.localize.Nbbh.Nbbh_PaymentBank;

/**
 * 宁波二百银行追送
 * 
 * 
 */
public class PaymentBankFunc_YlswNbeb extends PaymentBankFunc
{

	private Vector saleGoods;
	private Vector salePayment;
	private SaleBS saleBS;

	private void recordCustId(double money, int type)
	{
		double checkMoney = 30000;// 消费金额比较值
		// 判断是否需要进行身份记录
		if (money < checkMoney || type != Nbbh_PaymentBank.XYKXF) return;

		if (new MessageBox(Language.apply("交易金额较大,是否记录身份信息？"), null, true).verify() == GlobalVar.Key1)
		{
			String resultPath = "c:\\cardAPI\\result.txt";
			String exePath = "c:\\cardAPI\\cardApi.exe";
			String result = "";			
			do
			{
				// 读身份信息
				if (PathFile.fileExist(exePath))
				{
					if (PathFile.fileExist(resultPath))
		            {
		                PathFile.deletePath(resultPath);
		                
		                if (PathFile.fileExist(resultPath))
		                {
		            		new MessageBox("身份信息读取失败：\n交易请求文件request.txt无法删除,请重试");
		            		 	
		                }
		            }
					try
					{
						CommonMethod.waitForExec(exePath);
					}
					catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						new MessageBox("身份信息读取异常：\n" + e.getMessage());
					}
				}
				else
				{
					new MessageBox("找不到读取身份证读取模块" + exePath);
					//XYKSetError("XX", "找不到读取身份证模块 cardApi.exe");
					return;
				}
				// 记录信息
				String checkResult = "";
				BufferedReader br = null;
				try
				{
					br = CommonMethod.readFileGBK(resultPath);// 构造一个BufferedReader类来读取文件
					result = br.readLine();
				}
				catch (Exception e)
				{
					e.printStackTrace();
					new MessageBox("身份信息读取异常2：\n" + e.getMessage());
				}
				finally
				{
					if(br!=null)
					{
						try
						{
							br.close();
						}
						catch(Exception ex)
						{
							ex.printStackTrace();
						}
					}
				}
				// 判断是否读取成功
				if (result == null) result = "";
				checkResult = result.substring(0, 3);
				if (!"ERR".equals(checkResult) && !"".equals(checkResult))
				{
					bld.memo1 = result;
					new MessageBox("当前身份证信息：\n" + result);
					return;
				}
			} while (new MessageBox("身份证信息读取失败：\n" + result + "\n\n是否重新读取身份信息?", null, true).verify() == GlobalVar.Key1);
		}
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		// 身份证信息记录
		recordCustId(money, type);
		String callexec = getBankClassConfig("CALLEXEC");
		if (callexec == null || callexec.trim().length() <= 0)
		{
			// 为了模拟银行卡付款时计算追送金额
			money = getNewMoney(money, type, memo);
			bld.je = money;
			// yinliang test
			bld.retcode = "00";
			bld.retmsg = "模拟第三方支付交易成功!";
			bld.cardno = track2;
			bld.trace = Math.round(Math.random() * 1000000);
			bld.bankinfo = "0000测试银行";
			bld.memo = "100"; // 卡内余额
			this.errmsg = bld.retmsg;
			XYKCheckRetCode();
			// new MessageBox(bld.retmsg);
			return true;
		}
		else
		{
			return super.XYKExecute(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);
		}

	}

	protected boolean XYKWriteRequest(StringBuffer arg, int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		money = getNewMoney(money, type, memo);
		return super.XYKWriteRequest(arg, type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);
	}

	private double getNewMoney(double money, int type, Vector memo)
	{
		saleGoods = null;
		salePayment = null;
		saleBS = null;

		if (memo != null && memo.size() >= 4 && type == Nbbh_PaymentBank.XYKXF)
		{
			String paycode = String.valueOf(memo.elementAt(0));
			saleBS = (SaleBS) memo.elementAt(2);
			SalePayDef salepay = (SalePayDef) memo.elementAt(3);
			if (saleBS == null || salepay == null) return money;

			salePayment = saleBS.salePayment;
			saleGoods = saleBS.saleGoods;

			StringBuffer sbZsInfo = new StringBuffer();// 银行追送信息
			money = ManipulatePrecision.doubleConvert(Convert.toDouble(getNewMoney(String.valueOf(money), paycode, sbZsInfo)));
			PayModeDef mode = DataService.getDefault().searchPayMode(paycode);
			salepay.ybje = Double.parseDouble(saleBS.getPayMoneyByPrecision(money, mode));

			salepay.je = ManipulatePrecision.doubleConvert(salepay.ybje * salepay.hl, 2, 1);
			salepay.str6 = sbZsInfo.toString();
		}
		return money;
	}

	private String getNewMoney(String money, String paycode, StringBuffer sbZsInfo)
	{
		String retMoney = money;
		try
		{
			// 检查是否启用新追送BankPaycode || 只有销售时才追送
			if (GlobalInfo.sysPara.isUseNewBankZS != 'Y' || !SellType.ISSALE(saleBS.saletype)) return money;

			// 检查是否配置了银行卡付款方式列表及追送付款方式
			if (GlobalInfo.sysPara.BankPaycode == null || GlobalInfo.sysPara.BankPaycode.trim().length() <= 0
					|| GlobalInfo.sysPara.BankZSPaycode == null || GlobalInfo.sysPara.BankZSPaycode.trim().length() <= 0) { return money; }

			// 检查当前付款方式是否为银行付款方式
			// if (!checkIsBankPaycode(paycode)) return money;

			// 获取当前商品是否参与新追送及追送总金额
			double dblGoodsJE = getBankZSGoodsJE();
			if (dblGoodsJE <= 0) return money;

			// 获取当前已经参与的追送付款金额（银行卡+追送）
			double dblPayZJE = getBankPayZJE();

			// 计算剩余参与追送的商品总金额
			double dblGoodsYE = dblGoodsJE - dblPayZJE;
			if (dblGoodsYE <= 0)
			{
				showMsg("银行追送失败：参加活动的金额已经用完\n参与商品金额：" + dblGoodsJE + "\n已经使用金额：" + dblPayZJE);
				return money;
			}

			/*
			 * //获取当前所有的追送金额 double dblPayJE = getBankZSPayJE(money);
			 * if(dblPayJE==-1) return money;
			 * 
			 * //检查当前付款是否剩余总的追送余额 if
			 * (ManipulatePrecision.doubleConvert(dblGoodsJE-dblPayJE)<=0) {
			 * showMsg("银行追送失败：\n当前小票的银行追送折扣已经用完"); return money; }
			 */

			// 弹出追送银行列表供款员选择
			String[] bank = getBankNo();
			if (bank == null || bank.length < 2) return money;
			String bankNo = bank[0];
			String bankName = bank[1];

			// 弹出银行刷卡框，并取出卡号
			String bankCardNo = this.getBankCardNo();
			if (bankCardNo == null) return money;

			// 检查此银行卡号是否参与过追送（同一小票，同一卡号只能参与一次）
			if (checkBankcardNOZs(bankCardNo))
			{
				showMsg("银行追送失败：\n同一张银行卡，在同一张小票内只能参与一次");
				return money;
			}
			// 获取该卡的追送折扣范围及该银行的追送余额
			String[] zkInfo = new String[3];
			if (((Nbbh_DataService) DataService.getDefault()).getNewBankZsInfo(bankCardNo, bankNo, zkInfo) == false)
			{
				showMsg("银行追送失败：\n获取银行追送信息失败");
				return money;
			}
			double dblBandCardZkl = ManipulatePrecision.doubleConvert(Convert.toDouble(zkInfo[0]), 4, 1);// 卡折扣率
			double dblBankXE = ManipulatePrecision.doubleConvert(Convert.toDouble(zkInfo[1]));// 该银行卡限额
			if (dblBandCardZkl <= 0 || dblBandCardZkl >= 1)
			{
				showMsg("银行追送失败：\n当前银行卡没有追送折扣(" + String.valueOf(dblBandCardZkl) + ")");
				return money;
			}
			if (dblBankXE <= 0)
			{
				showMsg("银行追送失败：\n当前银行的追送额度已经用完");
				return money;
			}
			/*
			 * //获取当前银行卡所属银行已经参加的追送金额 double
			 * dblUseZsPayJE=getBankZsPayJE(bankNo); //判断该行是否还剩余送额度
			 * if(dblUseZsPayJE>dblBankXE) { showMsg("银行追送失败：\n该银行的追送限额为 " +
			 * ManipulatePrecision.doubleToString(dblBankXE) + "\n该小票已经使用 " +
			 * ManipulatePrecision.doubleToString(dblUseZsPayJE)); return money;
			 * }
			 */

			// double currBankYe =
			// ManipulatePrecision.doubleConvert(dblBankXE-dblUseZsPayJE);//当前银行的可用追送额度（=银行限额-本银行在本小票已追送的金额）;
			double currPayMoney = ManipulatePrecision.doubleConvert(Convert.toDouble(money));// 当前付款金额
			if (currPayMoney > dblGoodsYE) currPayMoney = dblGoodsYE;// 不能超过参与商品的剩余总金额

			/*
			 * //计算该银行可用的追送余额 double dblZZsYe =
			 * ManipulatePrecision.doubleConvert(dblGoodsJE-dblPayJE);//总的可用追送余额
			 * double dblBankZsYe =
			 * ManipulatePrecision.doubleConvert(dblBankXE-dblUseZsPayJE
			 * );//当前银行的可用追送余额 if(dblBankZsYe>dblZZsYe)
			 * dblBankZsYe=dblZZsYe;//不能超过总的可用追送余额 //付款金额不能超过限额
			 * if(ManipulatePrecision
			 * .doubleConvert(Convert.toDouble(money))>dblBankZsYe)
			 * dblBankZsYe=ManipulatePrecision
			 * .doubleConvert(Convert.toDouble(money)); if(dblBankZsYe<=0) {
			 * showMsg("银行追送失败：\n当前银行追送额度已经用完"); return money; }
			 */

			// 比较此银行卡付款金额的追送折扣额与此银行卡限额，取低
			double dblZsJe = ManipulatePrecision.doubleConvert(currPayMoney * (1 - dblBandCardZkl));// 当前银行卡的可用追送额度
			if (dblZsJe > dblBankXE) dblZsJe = dblBankXE;// 不能超过追送限额
			// if(dblZsJe>dblBankZsYe)dblZsJe=dblBankZsYe;
			if (dblZsJe <= 0)
			{
				showMsg("银行追送失败：\n当前银行追送额度已经用完");
				return money;
			}

			// 判断追送金额与付款金额
			double dblRetMoney = ManipulatePrecision.doubleConvert(Convert.toDouble(money) - dblZsJe);// 追送后的付款方式
			if (dblRetMoney <= 0)
			{
				showMsg("银行追送失败：\n银行追送后付款金额不足");
				return money;
			}

			// 提示是否参与追送
			StringBuffer tmp = new StringBuffer();
			tmp.append(("银行追送折扣活动") + "\n\n");
			tmp.append(appendStringSize("剩余支付金额:") + appendStringSize(getPayBalanceLabel(), 2) + "\n");
			tmp.append(appendStringSize("可参与活动总金额:") + appendStringSize(ManipulatePrecision.doubleToString(dblGoodsJE), 2) + "\n");
			tmp.append(appendStringSize("已参与活动总金额:") + appendStringSize(ManipulatePrecision.doubleToString(dblPayZJE), 2) + "\n");
			tmp.append(appendStringSize("卡号:") + appendStringSize(bankCardNo, 2) + "\n");
			tmp.append(appendStringSize("银行信息:")
					+ appendStringSize(bankName + "/" + ManipulatePrecision.doubleToString(dblBandCardZkl * 100) + "%", 2) + "\n");
			tmp.append(appendStringSize("本次优惠限额:") + appendStringSize(ManipulatePrecision.doubleToString(dblBankXE), 2) + "\n");
			tmp.append(appendStringSize("本次支付金额") + appendStringSize(money, 2) + "\n");
			tmp.append(appendStringSize("本次优惠金额:") + appendStringSize(ManipulatePrecision.doubleToString(dblZsJe), 2) + "\n");
			tmp.append(appendStringSize("实际支付金额:") + appendStringSize(ManipulatePrecision.doubleToString(dblRetMoney), 2) + "\n\n");
			tmp.append(Convert.appendStringSize("", "是否参与？", 0, 40, 40, 0) + "");
			writeLog(tmp.toString());
			if (new MessageBox(tmp.toString(), null, true).verify() != GlobalVar.Key1)
			{
				writeLog("款员未选择参与银行追送活动");
				return money;
			}

			// 记录追送信息：银行卡号，银行代号，追送折扣单号
			String retStr = ManipulatePrecision.doubleToString(dblZsJe) + "," + bankCardNo + "," + bankNo + "," + zkInfo[2].trim();
			writeLog(retStr);
			if (sbZsInfo != null)
			{
				sbZsInfo.append(retStr);
			}
			retMoney = ManipulatePrecision.doubleToString(dblRetMoney);
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			return money;
		}
		return retMoney;
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

	// alignment=0:左对齐 1； 右对齐 2；中间
	private String appendStringSize(String str, int alignment)
	{
		return Convert.appendStringSize("", str, 0, 20, 20, alignment);
	}

	private String appendStringSize(String str)
	{
		return appendStringSize(str, 0);
	}

	// 获取某银行已经追送的金额
	private double getBankZsPayJE(String bankNo)
	{
		double dblBankZsPayJE = 0;
		for (int j = 0; j < salePayment.size(); j++)
		{
			SalePayDef spd = (SalePayDef) salePayment.elementAt(j);
			if (spd == null) continue;
			if (spd.paycode.equalsIgnoreCase(GlobalInfo.sysPara.BankZSPaycode.trim()) && spd.memo != null)
			{
				String bankNoTmp = spd.memo.split(",")[0].trim();// 银行代号,规则单号
				if (bankNoTmp.equalsIgnoreCase(bankNo))
				{
					dblBankZsPayJE += spd.ybje;
				}

			}

		}
		return ManipulatePrecision.doubleConvert(dblBankZsPayJE);
	}

	// 获取某银行已经追送+付款的总金额
	private double getBankPayZJE()
	{
		double dblBankPayZJE = 0;
		for (int j = 0; j < salePayment.size(); j++)
		{
			SalePayDef spd = (SalePayDef) salePayment.elementAt(j);
			if (spd == null) continue;
			if (checkIsBankPaycode(spd.paycode) && spd.memo != null)
			{
				String bankCardNo = spd.memo.split(",")[0];// 追送金额，银行卡号，银行行号，规则单号
				String[] arr = spd.memo.split(",");
				if (arr.length >= 4)
				{
					bankCardNo = arr[1].trim();
				}

				for (int k = 0; k < salePayment.size(); k++)
				{
					// 查找对应追送付款方式
					SalePayDef spdZS = (SalePayDef) salePayment.elementAt(k);
					if (spdZS == null) continue;
					if (GlobalInfo.sysPara.BankZSPaycode != null && spdZS.paycode.equalsIgnoreCase(GlobalInfo.sysPara.BankZSPaycode.trim())
							&& spdZS.payno != null && spdZS.payno.equalsIgnoreCase(bankCardNo))
					{
						dblBankPayZJE += spd.ybje;// 银行卡付款
						dblBankPayZJE += spdZS.ybje;// 追送付款
						break;
					}
				}

			}

		}
		return ManipulatePrecision.doubleConvert(dblBankPayZJE);
	}

	// 检查当前付款方式是否为银行付款方式
	private boolean checkIsBankPaycode(String paycode)
	{
		if (GlobalInfo.sysPara.BankPaycode != null && GlobalInfo.sysPara.BankPaycode.trim().length() > 0)
		{
			String[] paycodeArr = GlobalInfo.sysPara.BankPaycode.split("\\|");
			if (paycodeArr.length >= 1)
			{
				for (int i = 0; i <= paycodeArr.length - 1; i++)
				{
					// if(paycodeArr[i].trim().equalsIgnoreCase(GlobalInfo.sysPara.BankZSPaycode.trim()))
					// continue;//付款方式不能同是为【银行付款方式】和【银行追送付款方式】

					if (paycodeArr[i].trim().equalsIgnoreCase(paycode)) return true;
				}
			}
		}
		return false;
	}

	// 获取当前商品是否参与新追送及追送总金额
	private double getBankZSGoodsJE()
	{
		double GoodsJE = 0;
		for (int i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
			// saleGoodsDef.str6="Y";
			if (saleGoodsDef.str6 != null && saleGoodsDef.str6.equalsIgnoreCase("Y"))
			{
				GoodsJE += ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - saleBS.getZZK(saleGoodsDef));
			}
		}
		return ManipulatePrecision.doubleConvert(GoodsJE);
	}

	// 获取当前付款的追送余额
	private double getBankZSPayJE(String money)
	{
		if (GlobalInfo.sysPara.BankZSPaycode == null || GlobalInfo.sysPara.BankZSPaycode.trim().length() <= 0)
		{
			showMsg(Language.apply("银行追送失败：参数未定义追送付款方式编码"));
			return -1;
		}
		double PayJE = 0;
		if (salePayment.size() <= 0) return PayJE;
		for (int i = 0; i < salePayment.size(); i++)
		{
			SalePayDef spd = (SalePayDef) salePayment.elementAt(i);
			if (spd != null && spd.paycode.equalsIgnoreCase(GlobalInfo.sysPara.BankZSPaycode.trim()))
			{
				PayJE += spd.ybje;
			}
		}
		return PayJE;
	}

	// 获取银行行号
	private String[] getBankNo()
	{
		String bankNo[] = null;

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
			String[] title = { Language.apply("银行卡列表") };
			int[] width = { 500 };

			int choice = new MutiSelectForm().open(Language.apply("请选择银行追送对应的银行"), title, width, con);
			// 没有选择规则不进行计算
			if (choice == -1) return null;

			CustFilterDef rule = ((CustFilterDef) v.elementAt(choice));
			bankNo = new String[2];
			bankNo[0] = rule.TrackFlag;
			bankNo[1] = rule.desc;
		}
		else
		{
			this.writeLog("获取追送的银行列表失败");
		}
		return bankNo;
	}

	// 获取银行卡号
	private String getBankCardNo()
	{
		// 输入银行卡号
		TextBox txt = new TextBox();
		StringBuffer cardno = new StringBuffer();
		if (!txt.open(Language.apply("请刷银行卡"), Language.apply("卡号"), Language.apply("请将银行卡从刷卡槽刷入"), cardno, 0, 0, false, TextBox.MsrKeyInput)) { return null; }

		String strcardno = txt.Track2;
		if (strcardno.indexOf("=") > 1) strcardno = strcardno.substring(0, strcardno.indexOf("="));
		;
		return strcardno;
	}

	// 获取某银行已经追送的金额
	private boolean checkBankcardNOZs(String bankCardNo)
	{
		for (int j = 0; j < salePayment.size(); j++)
		{
			SalePayDef spd = (SalePayDef) salePayment.elementAt(j);
			if (spd.paycode.equalsIgnoreCase(GlobalInfo.sysPara.BankZSPaycode.trim()) && spd.payno != null
					&& spd.payno.equalsIgnoreCase(bankCardNo.trim())) { return true; }
		}
		return false;
	}

	public String getPayBalanceLabel()
	{
		return ManipulatePrecision.doubleToString(saleBS.calcPayBalance());
	}

}
