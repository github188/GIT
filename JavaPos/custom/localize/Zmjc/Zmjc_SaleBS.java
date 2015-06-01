package custom.localize.Zmjc;

import java.io.File;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Vector;

import org.eclipse.swt.widgets.Label;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.CashBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentBankCMCC;
import com.efuture.javaPos.PrintTemplate.CheckGoodsMode;
import com.efuture.javaPos.PrintTemplate.PrintTemplate;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.ParaNodeDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleCustDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.Struct.SuperMarketPopRuleDef;
import com.efuture.javaPos.UI.Design.RetSYJForm;

import custom.localize.Bcrm.Bcrm_DataService;

public class Zmjc_SaleBS extends Zmjc_SaleBGD//Zmjc_SaleBSClk//Bcrm_SaleBS
{
	protected boolean cxRebate = false;//价随变量促销标识
	protected int currLine = -1;//当前行号(合并商品时用)
	protected Zmjc_AccessLocalDB localDB = (Zmjc_AccessLocalDB) AccessLocalDB.getDefault();
	private Vector vecFlights;
	public void initSellData()
	{
		super.initSellData();
		saleCust = new SaleCustDef();
	}

	/*@Override
	 public boolean memberGrant()
	 {
	 // TODO 自动生成方法存根
	 //return super.memberGrant();
	 new CustInfoForm().open(GlobalInfo.saleform.sale.saleBS);//
	 return false;//test
	 }
	 */

	
	public String[] rowInfo(SaleGoodsDef goodsDef)
	{
		String[] rowinfos = super.rowInfo(goodsDef);
		if (!newrowInfo) return rowinfos;

		int rowindex = saleGoods.indexOf(goodsDef);
		rowinfos[0] = String.valueOf(rowindex + 1);
		int index = 0;
		boolean default1 = true;

		// 检查有没有匹配的类型
		String[] info = new String[saleEvent.table.getColumnCount()];

		for (int i = 0; i < Math.min(rowinfos.length, info.length); i++)
		{
			info[i] = rowinfos[i];
		}

		for (int i = 0; i < tab.size(); i++)
		{
			boolean fit = false;
			String[] lines = (String[]) tab.elementAt(i);
			if (lines[2].split(",").length <= 1)
			{
				if ((SellType.ISCHECKINPUT(this.saletype) && lines[2].equals("CHECK_INPUT"))) fit = true;
				else if (lines[2].equals(this.saletype)) fit = true;
			}
			else
			{
				String[] types = lines[2].split(",");
				for (int j = 0; j < types.length; j++)
				{
					if ((SellType.ISCHECKINPUT(this.saletype) && types[j].equals("CHECK_INPUT")))
					{
						fit = true;
						break;
					}
					else if (types[j].equals(this.saletype))
					{
						fit = true;
						break;
					}
				}
			}

			if (fit)
			{
				if (lines[1].split(",").length > 1)
				{
					default1 = false;

					if (lines[1].split(",")[1].equals("cjdj"))
					{
						// 成交单价
						info[index] = ManipulatePrecision.doubleToString(ManipulatePrecision.div(
																									ManipulatePrecision.sub(goodsDef.hjje,
																															goodsDef.hjzk),
																									goodsDef.sl), 2, 1);
					}
					else if (lines[1].split(",")[1].equals("ysje"))
					{
						// 应收金额
						info[index] = ManipulatePrecision.doubleToString(ManipulatePrecision.sub(goodsDef.hjje, goodsDef.hjzk), 2, 1);
					}
					else
					{
						info[index] = String.valueOf(PrintTemplate.findObjectValue(this, lines[1].split(",")[1], rowindex));
					}

				}
				index++;
			}
		}

		if (default1)
		{
			// 首先填入默认值
			index = 0;
			for (int i = 0; i < tab.size(); i++)
			{
				String[] lines = (String[]) tab.elementAt(i);
				if (lines[2].equals("Default"))
				{
					if (lines[1].split(",").length > 1)
					{

						if (lines[1].split(",")[1].equals("barcode"))
						{
							info[index] = (goodsDef.barcode == null ? "" : goodsDef.barcode.trim());
						}
						else if (lines[1].split(",")[1].equals("code"))
						{
							info[index] = (goodsDef.code == null ? "" : goodsDef.code.trim());
						}
						else if (lines[1].split(",")[1].equals("hh"))
						{
							info[index] = (goodsDef.str12 == null ? "" : goodsDef.str12.trim());
							;
						}
						else if (lines[1].split(",")[1].equals("name"))
						{
							info[index] = (goodsDef.name == null ? "" : goodsDef.name.trim());
							;
							;
						}
						else if (lines[1].split(",")[1].equals("unit"))
						{
							info[index] = (goodsDef.unit == null ? "" : goodsDef.unit.trim());
							;
							;
							;
						}
						else if (lines[1].split(",")[1].equals("sl"))
						{
							info[index] = ManipulatePrecision.doubleToString(goodsDef.sl, 2, 1);
							;
						}
						else if (lines[1].split(",")[1].equals("cjdj"))
						{
							// 成交单价
							info[index] = ManipulatePrecision.doubleToString(ManipulatePrecision.div(ManipulatePrecision.sub(goodsDef.hjje,
																																goodsDef.hjzk),
																										goodsDef.sl), 2, 1);
						}
						else if (lines[1].split(",")[1].equals("sj"))
						{
							// 售价(折前单价)
							info[index] = ManipulatePrecision.doubleToString(ManipulatePrecision.div(goodsDef.hjje, goodsDef.sl), 2, 1);
						}
						else if (lines[1].split(",")[1].equals("zkje"))
						{
							// 折扣金额，百分比显示折扣
							//info[index] = ManipulatePrecision.doubleToString(goodsDef.hjzk) + ((goodsDef.hjzk > 0) && (goodsDef.hjje - goodsDef.hjzk > 0) ? "(" + ManipulatePrecision.doubleToString((goodsDef.hjje - goodsDef.hjzk) / goodsDef.hjje * 100, 0, 1) + "%)" : "");
							info[index] = ManipulatePrecision.doubleToString(goodsDef.hjzk);//宽度有限，所以暂不显示百分比
						}
						else if (lines[1].split(",")[1].equals("ysje"))
						{
							// 应收金额
							info[index] = ManipulatePrecision.doubleToString(ManipulatePrecision.sub(goodsDef.hjje, goodsDef.hjzk), 2, 1);
						}
						else
						{
							info[index] = String.valueOf(PrintTemplate.findObjectValue(this, lines[1].split(",")[1], rowindex));
						}

					}

					index++;
				}
			}
		}

		return info;
	}
	
	public boolean paySellStart()
	{
		boolean blnRet = true;
		try
		{
			if (!super.paySellStart()) return blnRet = false; // 不允许进行付款

			//最低价格提示
			checkGoodsMinJG();

			//获取原单退货小票的顾客信息
			if (!getBackSaleCustomerInfo()) return false;

			/*
			 //顾客信息录入
			 if (GlobalInfo.sysPara.isEnableCustInput == 'Y')//参数控制是否需录入顾客信息
			 {
			 StringBuffer buffer = new StringBuffer();//大类限额信息
			 new CustInfoForm().open(GlobalInfo.saleform.sale.saleBS, buffer);
			 
			 //判断大类限额,若超限,则不允许付款
			 if (buffer.toString().equalsIgnoreCase("exit")) return blnRet = false;//选择了退出界面,没有录入顾客信息,所以返回扫码界面
			 
			 //检查大类消费是否超额度
			 if (!checkDlInfo(buffer)) return blnRet = false;
			 }
			 
			 return blnRet = true;*/
			blnRet = checkCustPay();
			return blnRet;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return blnRet = false;
		}
		finally
		{
			if (!blnRet)
			{
				paySellCancel();
			}
		}

	}

	/**
	 * 按原单退货时，获取原单对应的顾客信息
	 * @return
	 */
	public boolean getBackSaleCustomerInfo()
	{
		try
		{
			if (SellType.ISBACK(saletype))
			{
				if (((Zmjc_DataService) DataService.getDefault()).getBackSaleCustomerInfo(thSyjh, thFphm, saleCust) != 0) return true;//机场店不控制，就算获取失败，也可以　 for fangl 2013.8.23
			}
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		return true;
	}

	/**
	 * ZMJC:检查顾客信息录入
	 * @return
	 */
	public boolean checkCustPay()
	{

		//顾客信息录入
		if ((SellType.ISSALE(saletype) && GlobalInfo.sysPara.isEnableCustInput == 'Y')
				|| (SellType.ISBACK(saletype) && GlobalInfo.sysPara.isEnableCustInput_TH == 'Y'))//参数控制是否需录入顾客信息
		{
			StringBuffer buffer = new StringBuffer();//大类限额信息
			new CustInfoForm().open(this, buffer);

			//判断大类限额,若超限,则不允许付款
			if (buffer.toString().equalsIgnoreCase("exit")) return false;//选择了退出界面,没有录入顾客信息,所以返回扫码界面

			//检查大类消费是否超额度
			if (!checkDlInfo(buffer)) return false;
		}

		return true;
	}

	//当商品价格低于规定的价格时,则给出(仅仅是)提示
	protected void checkGoodsMinJG()
	{
		//仅限于联网状态
		if (!GlobalInfo.isOnline) return;

		try
		{
			for (int i = 0; i < saleGoods.size(); i++)
			{
				SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);
				if (sg == null) continue;
				if (ManipulatePrecision.doubleConvert(sg.num5, 2, 1) > 0
						&& ManipulatePrecision.doubleConvert(sg.hjje - sg.hjzk, 2, 1) < ManipulatePrecision.doubleConvert(sg.num5, 2, 1))
				{
					new MessageBox(Language.apply("[{0}][{1}]的成交价已超过该商品的最低成交价", new Object[]{sg.code,sg.name}) + "[" + ManipulatePrecision.doubleConvert(sg.num5, 2, 1) + "]");
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	//检查大类消费是否超额度
	public boolean checkDlInfo(StringBuffer buffer)
	{
		try
		{
			//脱网或退货时不处理
			if (!GlobalInfo.isOnline || SellType.ISBACK(saletype)) return true;

			//分割
			String[] arrDLList = buffer.toString().split("\\|");
			double total = 0;//大类总额
			String goodsCategory = "";//大类
			double limitMoney = 0;//限额
			String[] arrDLDetail = null;
			for (int j = 0; j < arrDLList.length; j++)
			{
				//防止过程返回大类限购信息为空
				if (arrDLList[j] == null || arrDLList[j].trim().length() <= 0)
				{
					continue;
				}

				total = 0;
				limitMoney = 0;

				//分割大类与大类限额
				arrDLDetail = arrDLList[j].split(",");
				if (arrDLDetail == null || arrDLDetail.length < 2) continue;

				//根据顾客信息返回的大类限购，计算总金额是否超限购
				goodsCategory = arrDLDetail[0];
				limitMoney = Convert.toDouble(arrDLDetail[1]);

				if (goodsCategory == null || goodsCategory.trim().length() <= 0 || limitMoney <= 0)
				{
					continue;
				}

				for (int i = 0; i < saleGoods.size(); i++)
				{
					SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);

					//录入商品大类为空不计算
					if (sg == null || sg.str6 == null || sg.str6.trim().length() <= 0 || goodsCategory == null || goodsCategory.trim().length() <= 0)
					{
						continue;
					}

					//计算同一商品大类累计总金额
					if (sg.str6.trim().equals(goodsCategory.trim()))
					{
						total = total + (sg.hjje - sg.hjzk);
					}
					if (total > limitMoney)
					{
						new MessageBox(Language.apply("无法付款:商品大类{0}已超限额", new Object[]{goodsCategory}));
						return false;
					}
				}
			}
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("无法付款:检查商品大类是否超限额时异常"));
			return false;
		}
	}

	public boolean saleFinishDone(Label status, StringBuffer waitKeyCloseForm)
	{
		try
		{
			// 如果没有连接打印机则连接
			if (GlobalInfo.sysPara.issetprinter == 'Y' && GlobalInfo.syjDef.isprint == 'Y' && Printer.getDefault() != null
					&& !Printer.getDefault().getStatus())
			{
				Printer.getDefault().open();
				Printer.getDefault().setEnable(true);
			}

			// 标记最后交易完成方法已开始，避免重复触发
			if (!waitlab) waitlab = true;
			else return false;

			// 输入小票附加信息
			if (!inputSaleAppendInfo())
			{
				new MessageBox(Language.apply("小票附加信息输入失败,不能完成交易!"));
				return false;
			}

			//
			setSaleFinishHint(status, Language.apply("正在汇总交易数据,请等待....."));
			if (!saleSummary())
			{
				new MessageBox(Language.apply("交易数据汇总失败!"));

				return false;
			}

			//
			setSaleFinishHint(status, Language.apply("正在校验数据平衡,请等待....."));
			if (!AccessDayDB.getDefault().checkSaleData(saleHead, saleGoods, salePayment))
			{
				new MessageBox(Language.apply("交易数据校验错误!"));

				return false;
			}

			// 最终效验
			if (!checkFinalStatus()) { return false; }

			// 不是练习交易数据写盘
			if (!SellType.ISEXERCISE(saletype))
			{
				// 输入顾客信息
				setSaleFinishHint(status, Language.apply("正在输入客户信息,请等待......"));
				selectAllCustomerInfo();

				//
				setSaleFinishHint(status, Language.apply("正在打开钱箱,请等待....."));
				CashBox.getDefault().openCashBox();

				//
				setSaleFinishHint(status, Language.apply("正在记账付款数据,请等待....."));
				if (!saleCollectAccountPay())
				{
					new MessageBox(Language.apply("付款数据记账失败\n\n稍后将自动发起已记账付款的冲正!"));

					// 记账失败,及时把冲正发送出去
					setSaleFinishHint(status, Language.apply("正在发送冲正数据,请等待....."));
					CreatePayment.getDefault().sendAllPaymentCz();

					return false;
				}

				setSaleFinishHint(status, Language.apply("正在写入交易数据,请等待......"));
				if (!((Zmjc_AccessDayDB) AccessDayDB.getDefault()).writeSale(saleHead, saleGoods, salePayment, saleCust))
				{
					new MessageBox(Language.apply("交易数据写盘失败!"));
					AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票,金额:" + saleHead.ysje + ",发生数据写盘失败", StatusType.WORK_SENDERROR);

					// 记账失败,及时把冲正发送出去
					setSaleFinishHint(status, Language.apply("正在发送冲正数据,请等待....."));
					CreatePayment.getDefault().sendAllPaymentCz();

					return false;
				}

				// 小票已写盘,本次交易就要认为完成,即使后续处理异常也要返回成功
				saleFinish = true;

				// 小票保存成功以后，及时清除断点
				setSaleFinishHint(status, Language.apply("正在清除断点保护数据,请等待......"));
				clearBrokenData();

				//
				setSaleFinishHint(status, Language.apply("正在清除付款冲正数据,请等待......"));
				if (!saleCollectAccountClear())
				{
					AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票清除冲正数据失败,但小票已成交保存", StatusType.WORK_SENDERROR);

					new MessageBox(Language.apply("小票已成交保存,但清除冲正数据失败\n\n请完成本笔交易后重启款机尝试删除记账冲正数据!"));
				}

				// 处理交易完成后一些后续动作
				doSaleFinshed(saleHead, saleGoods, salePayment);

				// 上传当前小票
				setSaleFinishHint(status, Language.apply("正在上传交易小票数据,请等待......"));
				boolean bsend = GlobalInfo.isOnline;
				if (!DataService.getDefault().sendSaleDataCust(saleHead, saleGoods, salePayment, saleCust))
				{
					// 联网时发送小票却失败才记录日志
					if (bsend)
					{
						AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票,金额:" + saleHead.ysje + ",联网销售时小票送网失败", StatusType.WORK_SENDERROR);
					}
				}
				else
				{
					//处理暂存单
					inputZCD();
				}

				// 发送当前收银状态
				setSaleFinishHint(status, Language.apply("正在上传收银机交易汇总,请等待......"));
				DataService.getDefault().sendSyjStatus();

				// 打印小票
				setSaleFinishHint(status, Language.apply("正在打印交易小票,请等待......"));
				printSaleBill();
			}
			else
			{
				if (GlobalInfo.sysPara.lxprint == 'Y')
				{
					// 打印小票
					setSaleFinishHint(status, Language.apply("正在打印交易小票,请等待......"));
					printSaleBill();
				}

				// 标记本次交易已完成
				saleFinish = true;
			}

			// 返回到正常销售界面
			backToSaleStatus();

			// 保存本次的小票头
			if (saleFinish && saleHead != null)
			{
				lastsaleHead = saleHead;
			}

			// 清除本次交易数据
			this.initNewSale();

			// 关闭钱箱
			setSaleFinishHint(status, Language.apply("正在等待关闭钱箱,请等待......"));
			if (GlobalInfo.sysPara.closedrawer == 'Y')
			{
				// 如果钱箱能返回状态，采用等待钱箱关闭的方式来关闭找零窗口
				if (CashBox.getDefault().canCheckStatus())
				{
					// 等待钱箱关闭,最多等待一分钟
					int cnt = 0;
					while (CashBox.getDefault().getOpenStatus() && cnt < 30)
					{
						Thread.sleep(2000);

						cnt++;
					}

					// 等待一分钟后,钱箱还未关闭，标记为要等待按键才关闭找零窗口
					if (CashBox.getDefault().getOpenStatus() && cnt >= 30)
					{
						waitKeyCloseForm.delete(0, waitKeyCloseForm.length());
						waitKeyCloseForm.append("Y");
					}
				}
				else
				{
					// 标记为要等待按键才关闭找零窗口
					waitKeyCloseForm.delete(0, waitKeyCloseForm.length());
					waitKeyCloseForm.append("Y");
				}
			}

			// 交易完成
			setSaleFinishHint(status, Language.apply("本笔交易结束,开始新交易"));

			// 标记本次交易已完成
			saleFinish = true;

			return saleFinish;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			new MessageBox(Language.apply("完成交易时发生异常:\n\n") + ex.getMessage());

			return saleFinish;
		}
	}
	
	public boolean getRetFlights(Vector vecFlights) {

		ProgressBox pb = null;
		try {
			pb = new ProgressBox();
			pb.setText(Language.apply("正在读取回程航班信息,请等待..."));
			return localDB.getRetFlights(vecFlights, "", false);

		} catch (Exception ex) {
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			new MessageBox(Language.apply("回程航班信息获取失败：发生异常") + ex.getMessage());
		} finally {
			if (pb != null) {
				pb.close();
				pb = null;
			}
		}

		return false;
	}
	
	//暂存单
	public void inputZCD()
	{
		try
		{
			if (!SellType.ISSALE(saletype) || GlobalInfo.sysPara.isInputZCD != 'Y') return;
			//取回程航班信息
			if (vecFlights == null || vecFlights.size() <= 0) {
				vecFlights = new Vector();
				if (!getRetFlights(vecFlights)) {
					new MessageBox(Language.apply("【回程航班信息】读取失败"));
				}
			}
			//暂存单界面。。。
			//暂存单信息存到小票头的 str2 字段，格式为 回程航班编码|回程航班号|回程日期|回程时间|联系方式
		    CustZCDForm  CustZCD =new CustZCDForm();
		    CustZCD.open(saleHead, vecFlights);
		    
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public SaleGoodsDef goodsDef2SaleGoods(GoodsDef goodsDef, String yyyh, double quantity, double price, double allprice, boolean dzcm)
	{
		SaleGoodsDef saleGoodsDef = super.goodsDef2SaleGoods(goodsDef, yyyh, quantity, price, allprice, dzcm);

		// 对不同的行业进行属性转换
		// 这个地方以后得规范字段赋值(目前先采用str6到str9)
		saleGoodsDef.str6 = goodsDef.str1; // 商品大类信息
		saleGoodsDef.num5 = goodsDef.num5; //商品最低售价
		saleGoodsDef.str12 = goodsDef.str2;
		; // 货号

		saleGoodsDef.dblMaxYhSl = goodsDef.dblMaxYhSl;//限量数量
		return saleGoodsDef;
	}

	// 根据付款方式的付款精度,计算付款金额
	public String getPayMoneyByPrecision(double je, PayModeDef mode)
	{
		//付款界面以舍入方式来控制精度(只用于多币种找零),否则与舍入方式冲突 wangyong by 2013.6.25
		double value = je;
		if (mode.sswrfs == '0')
		{
			//精确到分
			value = formatDecimal(je, "#.00");
		}
		else if (mode.sswrfs == '1')
		{
			//四舍五入到角
			value = formatDecimal(je, "#.0");
		}
		else if (mode.sswrfs == '2')
		{
			//截断到角
			value = formatDecimal(je - 0.04, "#.0");
		}
		else if (mode.sswrfs == '3')
		{
			//四舍五入到元
			value = formatDecimal(je, "#");
		}
		else if (mode.sswrfs == '4')
		{
			//截断到元
			value = formatDecimal(je - 0.4, "#");
		}
		else if (mode.sswrfs == '5')
		{
			//进位到角
			value = formatDecimal(je + 0.0499999, "#.0");
		}
		else if (mode.sswrfs == '6')
		{
			//进位到元
			value = formatDecimal(je + 0.499999, "#");//Math.ceil(je);
		}
		else
		{
			int jd;
			if (mode.sswrjd == 0)
			{
				jd = 2;
			}
			else
			{
				jd = ManipulatePrecision.getDoubleScale(mode.sswrjd);
			}
			if (mode.sswrfs == 'Y')
			{
				//截断
				value = ManipulatePrecision.doubleConvert(je, jd, 0);
			}
			else if (mode.sswrfs == 'N')
			{
				//四舍五入
				value = ManipulatePrecision.doubleConvert(je, jd, 1);
			}
			else
			{
				//精确到分
				value = formatDecimal(je, "#.00");
			}
		}
		return ManipulatePrecision.doubleToString(value);
		/* int jd;

		 if (mode.sswrjd == 0)
		 {
		 jd = 2;
		 }
		 else
		 {
		 if (mode.sswrjd >= 1)
		 {
		 jd = 2;
		 }
		 else
		 {
		 jd = ManipulatePrecision.getDoubleScale(mode.sswrjd);
		 }
		 //WANGYONG UPDATE BY 2013.6.23 暂不处理mode.sswrjd >= 1 的情况
		 jd = ManipulatePrecision.getDoubleScale(mode.sswrjd);
		 }

		 double ye = 0 ;
		 
		 if (mode.sswrfs == 'Y')
		 {
		 ye = ManipulatePrecision.doubleConvert(je, jd, 0);
		 }
		 else
		 {
		 ye = ManipulatePrecision.doubleConvert(je, jd, 1);
		 }
		 
		 // 如果按付款方式精度计算以后的余额比原余额小,则补上精度，保证缺省余额是足够的
		 if (ManipulatePrecision.doubleCompare(je - ye,GlobalInfo.sysPara.lackpayfee,2) > 0)
		 {
		 if (mode.sswrjd>=1)
		 {
		 ye = ManipulatePrecision.doubleConvert(je + 0.01, jd, 1);
		 }
		 else
		 {
		 ye = ManipulatePrecision.doubleConvert(je + mode.sswrjd, jd, 1);
		 }
		 
		 }

		 return ManipulatePrecision.doubleToString(ye, jd, 1);*/
	}

	public double formatDecimal(double je, String f)
	{
		try
		{
			DecimalFormat df = new DecimalFormat(f);
			return Double.parseDouble(df.format(je));//Double.valueOf(df.format(je));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return je;
	}

	public boolean checkPaymodeValid(PayModeDef mode, String money)
	{
		if (mode.ismj == 'Y' && mode.iszl == 'Y' && mode.minval > 0)//最小付款金额必须大于0,否则不判断
		{
			if (Convert.toDouble(money) < mode.minval)//mode.sswrjd
			{
				new MessageBox(Language.apply("付款失败:[{0}]的最小付款金额为[{1}]", new Object[]{"\n\n" + mode.code + mode.name,mode.minval+ "\n"}));//mode.sswrjd
				return false;
			}

			double yu = (ManipulatePrecision.doubleConvert(Convert.toDouble(money) * 1000)) % ManipulatePrecision.doubleConvert(mode.minval * 1000);//取余
			if (yu != 0)
			{
				new MessageBox(Language.apply("付款失败:[{0}]的付款必须为[{1}]的整数倍", new Object[]{"\n\n" + mode.code + mode.name,mode.minval + "\n"}));
				return false;
			}

		}
		return super.checkPaymodeValid(mode, money);
	}

	public void addSaleGoodsObject(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		if (sg.type == '9')
		{
			//打包商品特殊处理:增加打包商品明细到界面
			Vector packGoodsCode = new Vector();//打包商品码

			String goodscode = "";
			//String barcode = "";
			String je = "";
			String sl = "";
			String sjje = "";
			//String zkfd = "";
			ProgressBox pb = null;
			try
			{
				pb = new ProgressBox();
				pb.setText(Language.apply("正在获取打包明细商品,请等待..."));

				//1.获取打包明细商品
				if (((Zmjc_DataService) DataService.getDefault()).getGoodsPackList(packGoodsCode, sg.code, sg.gz, sg.lsj))
				{
					Vector packSaleGoodsList = new Vector();
					Vector packGoodsList = new Vector();
					Vector packSpareInfoList = new Vector();

					SaleGoodsDef packSaleGoods;
					GoodsDef packGoods;
					SpareInfoDef packSpareInfo;

					Random rnd = new Random();
					String tmpRand = goods.code + "_" + String.valueOf(rnd.nextInt(1000));//编码_随机数,用于删除时标识
					String row[];

					//2.循环查找明细商品,并改价/改数量
					for (int i = 0; i < packGoodsCode.size(); i++)
					{
						row = (String[]) packGoodsCode.elementAt(i);
						if (row == null || row.length < 10)
						{
							new MessageBox(Language.apply("添加商品失败:\n\n此商品为打包商品,解析其对应的打包明细商品时失败 \n"));
							return;
						}
						goodscode = row[0];
						//barcode = row[1]; 						
						je = row[2];
						sl = row[3];
						sjje = row[4];
						//zkfd = row[5];

						packGoods = findPackGoodsInfo(goodscode, sg.gz);//查找商品信息(暂不考虑多单位等情况)
						if (packGoods == null)
						{
							new MessageBox(Language.apply("添加商品失败:\n\n") + Language.apply("此商品为打包商品,无法找到其对应的打包明细商品") + "[" + goodscode + "] \n");
							return;
						}

						// 不允许销红,检查库存
						if ((SellType.ISSALE(saletype) && GlobalInfo.sysPara.isxh != 'Y' && packGoods.isxh != 'Y'))
						{
							// 统计商品销售数量
							double hjsl = ManipulatePrecision.mul(Convert.toDouble(sl), packGoods.bzhl) + calcSameGoodsQuantity(packGoods.code, packGoods.gz);
							if (packGoods.kcsl < hjsl)
							{
								if (GlobalInfo.sysPara.xhisshowsl == 'Y')
									new MessageBox(Language.apply(packGoods.name + "\n" + "该商品库存为{0}\n库存不足,不能销售", new Object[]{ManipulatePrecision.doubleToString(packGoods.kcsl)}));
								else
									new MessageBox(Language.apply(packGoods.name +  "\n" + "该商品库存不足,不能销售"));

								return;
							}
						}

						//组包明细商品不计算分期促销，所以要清除
						packGoods.popdjbh = "";
						
						// 生成明细
						packSaleGoods = goodsDef2SaleGoods(packGoods, saleEvent.yyyh.getText(), Convert.toDouble(sl), Convert.toDouble(je),
															Convert.toDouble(sjje), false);
						packSpareInfo = getGoodsSpareInfo(packGoods, packSaleGoods);

						packSaleGoods.str7 = tmpRand;

						//增加明细商品到临时变量
						packSaleGoodsList.add(packSaleGoods);//小票明细
						packGoodsList.add(packGoods); //商品明细
						packSpareInfoList.add(packSpareInfo);//辅助信息

					}

					//3.增加明细商品到界面
					for (int i = 0; i < packGoodsList.size(); i++)
					{
						if (!combineGoods((SaleGoodsDef) packSaleGoodsList.elementAt(i)))
						{
							addPackSaleGoodsObject((SaleGoodsDef) packSaleGoodsList.elementAt(i), (GoodsDef) packGoodsList.elementAt(i),
													(SpareInfoDef) packSpareInfoList.elementAt(i));
						}
					}

					//清除变量
					packSaleGoodsList = null;
					packGoodsList = null;
					packSpareInfoList = null;

				}
				else
				{
					new MessageBox(Language.apply("添加商品失败:\n\n") + Language.apply("此商品为打包商品,无法找到其对应的打包明细商品 \n"));
				}

			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if (pb != null)
				{
					pb.close();
					pb = null;
				}
			}

		}
		else
		{
			//是否合并商品
			if (!combineGoods(sg))
			{
				super.addSaleGoodsObject(sg, goods, info);
			}
		}
	}

	protected boolean combineGoods(SaleGoodsDef saleGoodsDef)
	{
		try
		{
			//是否合并商品
			if (GlobalInfo.syjDef.printfs == '2' && GlobalInfo.sysPara.isHbGoods == 'Y' && saleGoods.size() >= 1)
			{
				currLine = -1;
				PosLog.getLog(this.getClass()).info("combineGoods() barcode=" + saleGoodsDef.barcode + ",code=" + saleGoodsDef.code + ",type=" + saleGoodsDef.type); 
				//电子称商品、削价商品、旧换新商品、不定价商品等不允许合并
				if (saleGoodsDef.flag == '2' || saleGoodsDef.flag == '3' || saleGoodsDef.type == '8' || saleGoodsDef.lsj <= 0
						|| isPackGoods(saleGoodsDef) || SellType.ISBACK(saletype)) return false;//add isPackGoods 打包商品不合并 BY 2013.10.8

				SaleGoodsDef saleGoodsDefTmp = null;
				for (int i = 0; i < saleGoods.size(); i++)
				{
					saleGoodsDefTmp = (SaleGoodsDef) saleGoods.elementAt(i);
					if (saleGoodsDefTmp == null || isPackGoods(saleGoodsDefTmp)) //add isPackGoods 打包商品不合并 BY 2013.10.8
					continue;

					/*if (saleGoodsDefTmp.lszke > 0 || saleGoodsDefTmp.lszre > 0 || saleGoodsDefTmp.lszzk > 0 || saleGoodsDefTmp.lszzr > 0)
					 continue;*/

					// 判断合并条件
					if (saleGoodsDefTmp.code.equals(saleGoodsDef.code) && saleGoodsDefTmp.gz.equals(saleGoodsDef.gz)
							&& (saleGoodsDefTmp.barcode.equals(saleGoodsDef.barcode) || saleGoodsDefTmp.barcode.equals(saleGoodsDef.inputbarcode))
							&& saleGoodsDefTmp.bzhl == saleGoodsDef.bzhl && saleGoodsDefTmp.unit.equals(saleGoodsDef.unit))
					{
						PosLog.getLog(this.getClass()).info("combineGoods()2 barcode=" + saleGoodsDef.barcode + ",code=" + saleGoodsDef.code);
						//注:若合并商品后,则只重算单品折扣/折让,但其它手工折扣会被清除
						double oldSL = saleGoodsDefTmp.sl;
						saleGoodsDefTmp.sl += saleGoodsDef.sl;// 新数量

						currLine = i;
						saleGoodsDefTmp.hjje = ManipulatePrecision.doubleConvert(saleGoodsDefTmp.sl * saleGoodsDefTmp.jg, 2, 1);
						double lszre = ManipulatePrecision.doubleConvert(saleGoodsDefTmp.lszre / oldSL * saleGoodsDefTmp.sl);
						double lszzk = ManipulatePrecision.doubleConvert(saleGoodsDefTmp.lszke / oldSL * saleGoodsDefTmp.sl);

						clearGoodsGrantRebate(i);//清除手工折扣

						saleGoodsDefTmp.lszre = lszre;//重算单品折让
						saleGoodsDefTmp.lszke = lszzk;//重算单品折扣

						getZZK(saleGoodsDefTmp);
						calcGoodsYsje(i);

						return true;// 合并成功
					}
				}
			}

		}
		catch (Exception ex)
		{
			currLine = -1;
			ex.printStackTrace();
		}
		return false;// 合并失败
	}

	public boolean doShowInfoFinish()
	{
		if (currLine != -1)
		{
			saleEvent.table.setSelection(currLine);
			saleEvent.table.showSelection();
		}

		return true;
	}

	//增加打包明细商品
	protected void addPackSaleGoodsObject(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		//增加商品
		saleGoods.add(sg);
		goodsAssistant.add(goods);
		goodsSpare.add(info);

		// goods不为空才是销售的商品,查找商品对应收款规则
		if ((GlobalInfo.sysPara.havePayRule == 'Y' || GlobalInfo.sysPara.havePayRule == 'A') && goods != null && info != null)
		{
			info.payrule = DataService.getDefault().getGoodsPayRule(goods);
		}

		if (isCmPopMode())
		{
			// goods不为空才是销售的商品,查找商品对应促销情况
			if (goods != null && info != null) findGoodsCMPOPInfo(sg, goods, info);
			else goodsCmPop.add(null);
		}
		else
		{
			sg.memo = "0,0";

			if (SellType.ISCHECKINPUT(saletype)) { return; }

			if (goods != null)
			{
				// 每查找一个商品，就查找商品CRM促销规则
				findGoodsCRMPop(sg, goods, info);
			}
		}

		// 计算商品应收
		calcGoodsYsje(saleGoods.size() - 1);
	}

	//获取打包商品信息
	protected GoodsDef findPackGoodsInfo(String code, String packGZ)
	{
		GoodsDef goodsDef = new GoodsDef();
		int searchFlag = 0;

		String yhsj = null;
		String scsj;
		boolean isdzcm = false;
		boolean iszdxp = false;
		String yyyh = saleEvent.yyyh.getText();
		String gz = getGzCode(saleEvent.gz.getText());
		String dzcmscsj = "";

		// 设置查找商品的查找标志,1-超市销售/2-柜台销售检查营业员串柜/3-柜台销售不检查营业员串柜/4赠品
		if (GlobalInfo.syjDef.issryyy == 'N' || yyyh.equals(Language.apply("超市")))
		{
			searchFlag = 1; // 超市
		}
		else if ((GlobalInfo.sysPara.yyygz != 'N' && GlobalInfo.syjDef.issryyy != 'B' && gz != null && gz.length() > 0 && !gz.equals(Language.apply("多个柜")))
				|| iszdxp)
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
		PosLog.getLog(this.getClass()).info("findPackGoodsInfo: searchFlag=[" + searchFlag + ",code=[" + code + ",gz=[" + gz + ",scsj=[" + scsj + ",yhsj=[" + yhsj + ",saletype=[" + saletype + "].");
		int result = DataService.getDefault().getGoodsDef(goodsDef, searchFlag, code, gz, scsj, yhsj, saletype);
		switch (result)
		{
			case 0:
				break;
			case 4:// 商品存在多柜组
				//当打包明细商品有多柜组时,则取原打包码的柜组 wangyong 2013.5.21 for fangl
				//StringBuffer gzstr = new StringBuffer();
				boolean done = true;
				//done = new TextBox().open("请输入[" + code.trim() + "]商品的柜组", "柜组号", "该商品有多个柜组，请输入柜组号以便销售", gzstr, 0, 0, false);
				if (!done)
				{
					return null;
				}
				else
				{
					searchFlag = 2;
					int ret = DataService.getDefault().getGoodsDef(goodsDef, searchFlag, code, packGZ, scsj, yhsj, saletype);//gzstr.toString()
					if (ret == 4)
					{
						new MessageBox(Language.apply("商品查找失败 \n  在柜组[{0}]内未找到打包明细商品[{1}]", new Object[]{packGZ,code}));
						return null;
					}
					else if (ret != 0) { return null; }
				}
				break;

			default:
				return null;
		}

		/*// 检查营业员串柜情况
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
		 new MessageBox("该商品不是营业柜组范围内的商品\n\n营业员的营业柜组范围是\n" + curyyygz);
		 return null;
		 }
		 }
		 }*/

		// 使用代码销售时检查多单位商品
		//if (code.equals(goodsDef.code) && goodsDef.isuid == 'Y') { return getMutiUnitChoice(goodsDef); }
		// 母商品选择子商品进行销售
		//if (goodsDef.type == '6') { return getSubGoodsDef(goodsDef); }
		// 判断是否VIP折扣标志设置该单品是否享受VIP折扣
		if (GlobalInfo.sysPara.isHandVIPDiscount == 'A' && !isVIPZK)
		{
			goodsDef.name = "[" + goodsDef.name + "]";
			goodsDef.isvipzk = 'N';
		}

		return goodsDef;
	}

	//删除商品
	public boolean deleteGoods(int index)
	{
		SaleGoodsDef old_goods = null;

		if (!allowDeleteGoods(index)) return false;

		// 根据参数决定删除商品是按光标选择还是编码删除
		double quantity = 0;//要删除商品的数量
		String barcode = "";//要删除商品的编码
		double tempSL = 0;//保存旧的销售数量

		GlobalInfo.sysPara.removeGoodsModel = 'N';//中免强行为N
		if (GlobalInfo.sysPara.removeGoodsModel == 'Y')//Y-按商编码删除商品的模式/N-现有用光标选择商品进行删除的模式
		{
			StringBuffer code = new StringBuffer();
			boolean done = new TextBox().open(Language.apply("请输入要删除的商品条码"), Language.apply("商品条码"), Language.apply("1、直接输入商品条码，删除列表中所有匹配的商品；") + "\r\n" + Language.apply("2、输入 数量*商品条码，倒序删除指定数量的商品。"), code, 0, 0,
												false, TextBox.AllInput);
			if (!done) { return false; }

			//分解输入码 数量*编码，得到输入的数量、商品编码
			String[] s = convertQuantityBarcode(code.toString());
			if (s == null) return false;
			quantity = Convert.toDouble(s[0]);
			barcode = s[1];

			//倒序搜索商品列表，找到匹配的商品编码（按inputbarcode字段匹配）
			index = -1;
			int deleteMode = code.toString().indexOf("*") >= 0 ? 2 : 1;//求删除模式
			SaleGoodsDef temp = null;
			boolean result = true;

			for (int i = saleGoods.size() - 1; i >= 0; i--)
			{
				temp = (SaleGoodsDef) saleGoods.elementAt(i);
				if (temp.inputbarcode != null && temp.inputbarcode.equals(barcode))//找到商品后将本行数量减去相应的值
				{
					index = i;//得到要删的商品行号
					old_goods = temp;
					if (deleteMode == 1)//删除所有匹配商品
					{
						if (!doneDeleteGoods(index, old_goods)) result = false;
					}
					else
					{
						// 删除指定数量商品
						if (quantity <= 0.0) break;
						if (quantity >= old_goods.sl)
						{
							tempSL = old_goods.sl;
							if (!doneDeleteGoods(index, old_goods)) result = false;

							quantity = quantity - tempSL;
						}
						else
						{
							tempSL = quantity;
							old_goods.sl = old_goods.sl - quantity;

							// 重算商品应收
							old_goods.hjje = ManipulatePrecision.doubleConvert(old_goods.sl * old_goods.jg, 2, 1);
							clearGoodsGrantRebate(index);
							calcGoodsYsje(index);

							// 重算小票应收  
							calcHeadYsje();

							quantity = quantity - tempSL;
						}
					}
				}
			}

			if (-1 == index)
			{
				new MessageBox(Language.apply("没有找到要删除的商品条码。"));
				return false;
			}
			else
			{
				//new MessageBox("商品删除完成。");
				return result;
			}
		}
		else
		{
			// 删除前提示确认
			if (GlobalInfo.sysPara.removeGoodsMsg == 'Y')
			{
				old_goods = (SaleGoodsDef) saleGoods.elementAt(index);
				if (new MessageBox(Language.apply("你确定要删除此以下商品吗?") + "\n\n[" + old_goods.barcode + "]" + old_goods.name, null, true).verify() != GlobalVar.Key1) { return false; }
			}

			// 现有用光标选择商品进行删除的模式，先将本行数量记0
			old_goods = (SaleGoodsDef) saleGoods.elementAt(index);
			String packGoodsFlag = old_goods.str7;

			if (old_goods.str7 != null && old_goods.str7.length() > 0 && old_goods.str7.indexOf("_") > 0)
			{
				SaleGoodsDef tmpGoods;
				//删除打包商品
				for (int i = saleGoods.size() - 1; i >= 0; i--)
				{
					if (i > saleGoods.size() - 1) { return false; }
					tmpGoods = (SaleGoodsDef) saleGoods.elementAt(i);
					if (tmpGoods.str7 != null && tmpGoods.str7.equalsIgnoreCase(packGoodsFlag))
					{
						if (!doneDeleteGoods(i, tmpGoods)) return false;
					}

				}
				return true;
			}
			else
			{
				return doneDeleteGoods(index, old_goods);
			}

		}
	}

	//是否允许修改数量
	public boolean allowInputQuantity(int index)
	{
		if (super.allowInputQuantity(index)) { return isModifyGoods(index, Language.apply("打包商品不允许修改数量！")); }
		return false;
	}

	//是否允许输入价格
	public boolean allowInputPrice(int index)
	{
		if (super.allowInputPrice(index)) { return isModifyGoods(index, Language.apply("打包商品不允许修改价格！")); }
		return false;
	}

	protected String getPriv2(String staffPriv)
	{
		//将"是否突破最低折扣"赋值过来
		StringBuffer sb = new StringBuffer();
		sb.append(curGrant.priv);
		sb.setCharAt(2, staffPriv.charAt(2));
		return sb.toString();
	}

	//输入折扣
	public boolean inputRebate(int index)
	{
		if (!isModifyGoods(index, Language.apply("打包商品不允许单品折扣！"))) return false;
		//return super.inputRebate(index);
		double grantzkl = 0;
		boolean grantflag = false;

		// A模式指定小票退货不允许修改商品
		if (isNewUseSpecifyTicketBack()) return false;

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(index);
		// 小计、削价不处理
		if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3')) { return false; }

		// 服务费、以旧换新不处理
		if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8')) { return false; }

		// 不能打折
		if (!checkGoodsRebate(goodsDef, info))
		{
			new MessageBox(Language.apply("该商品不允许打折!"));

			return false;
		}

		// 备份数据
		SaleGoodsDef oldGoodsDef = (SaleGoodsDef) saleGoodsDef.clone();

		// 授权
		if ((curGrant.dpzkl * 100) >= 100 || !checkGoodsGrantRange(goodsDef, curGrant.grantgz))
		{
			OperUserDef staff = inputRebateGrant(index);
			if (staff == null) return false;

			// 本次授权折扣
			grantzkl = staff.dpzkl;
			grantflag = breachRebateGrant(staff);

			// 记录授权工号
			saleGoodsDef.sqkh = staff.gh;
			saleGoodsDef.sqktype = '1';
			saleGoodsDef.sqkzkfd = staff.privje1;

			//中免要求,一次性授权,不需要每次授权 start
			cursqkh = staff.gh;
			cursqktype = '1';
			cursqkzkfd = staff.privje1;
			curGrant.dpzkl = staff.dpzkl;
			curGrant.grantgz = staff.grantgz;

			/*//将"是否突破最低折扣"赋值过来
			 StringBuffer sb = new StringBuffer();
			 sb.append(curGrant.priv);
			 sb.setCharAt(2, staff.priv.charAt(2));*/
			curGrant.priv = getPriv2(staff.priv);

			//end

			// 记录日志
			String log = "授权单品折扣,小票号:" + saleHead.fphm + ",商品:" + saleGoodsDef.barcode + ",折扣权限:" + grantzkl * 100 + "%,授权:" + staff.gh;
			AccessDayDB.getDefault().writeWorkLog(log);
		}
		else
		{
			// 本次授权折扣
			grantzkl = curGrant.dpzkl;
			grantflag = breachRebateGrant(curGrant);

			// 记录授权工号
			saleGoodsDef.sqkh = cursqkh;
			saleGoodsDef.sqktype = cursqktype;
			saleGoodsDef.sqkzkfd = cursqkzkfd;
		}

		// 计算权限允许的最大折扣率
		double maxzkl = 0;
		if (grantflag)
		{
			//new MessageBox("允许突破最低折扣");
			// 允许突破最低折扣
			maxzkl = getMaxRebateGrant(grantzkl, 0);
		}
		else
		{
			// 不允许最低折扣
			maxzkl = getMaxRebateGrant(grantzkl, goodsDef);
		}

		// 以最大折扣率模拟计算折扣,检查打折以后商品的折扣合计是否超出权限允许的折扣率
		saleGoodsDef.lszke = 0;
		saleGoodsDef.lszke = ManipulatePrecision.doubleConvert((1 - maxzkl) * (saleGoodsDef.hjje - getZZK(saleGoodsDef)), 2, 1);
		if (getZZK(saleGoodsDef) > ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzkl)), 2, 1))
		{
			saleGoodsDef.lszke -= getZZK(saleGoodsDef) - ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzkl)), 2, 1);
			saleGoodsDef.lszke = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke, 2, 1);
		}
		if (saleGoodsDef.lszke < 0) saleGoodsDef.lszke = 0;

		// 根据模拟计算得到当前最大打折比例
		double lszkl = saleGoodsDef.lszke / (saleGoodsDef.hjje - getZZK(saleGoodsDef) + saleGoodsDef.lszke);
		lszkl = ManipulatePrecision.doubleConvert((1 - lszkl) * 100, 2, 1);

		// 输入折扣
		String maxzklmsg = Language.apply("收银员正在对该商品进行打折");

		if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
		{
			maxzklmsg = Language.apply("收银员对该商品的单品折扣权限为 {0}%\n你目前最多在成交价基础上再打折  {1}%",
			                           new Object[]{ ManipulatePrecision.doubleToString(maxzkl * 100, 2, 1, true),ManipulatePrecision.doubleToString(lszkl, 2, 1, true)});
//			maxzklmsg = "收银员对该商品的单品折扣权限为 " + ManipulatePrecision.doubleToString(maxzkl * 100, 2, 1, true) + "%\n你目前最多在成交价基础上再打折 "
//			+ ManipulatePrecision.doubleToString(lszkl, 2, 1, true) + "%";
		}

		StringBuffer buffer = new StringBuffer();
		if (!new TextBox().open(Language.apply("请输入单品折扣百分比(%)") + (grantflag == true ? Language.apply("(允许突破商品最低折扣限制)") : ""), Language.apply("单品折扣"), maxzklmsg, buffer, lszkl, 100, true))
		{
			// 恢复数据
			saleGoods.setElementAt(oldGoodsDef, index);

			return false;
		}

		// 得到折扣率
		grantzkl = Double.parseDouble(buffer.toString());

		// 计算最终折扣
		saleGoodsDef.lszke = 0;
		saleGoodsDef.lszke = ManipulatePrecision.doubleConvert((100 - grantzkl) / 100 * (saleGoodsDef.hjje - getZZK(saleGoodsDef)), 2, 1);
		if (getZZK(saleGoodsDef) > ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzkl)), 2, 1))
		{
			saleGoodsDef.lszke -= getZZK(saleGoodsDef) - ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzkl)), 2, 1);
			saleGoodsDef.lszke = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke, 2, 1);
		}
		if (saleGoodsDef.lszke < 0) saleGoodsDef.lszke = 0;
		saleGoodsDef.lszke = getConvertRebate(index, saleGoodsDef.lszke);

		// 重算商品折扣合计
		getZZK(saleGoodsDef);

		// 重算小票应收
		calcHeadYsje();

		return true;
	}

	//输入折让金额
	public boolean inputRebatePrice(int index)
	{
		if (!isModifyGoods(index, Language.apply("打包商品不允许单品折让！"))) return false;
		//return super.inputRebatePrice(index);

		double grantzkl = 0;
		boolean grantflag = false;

		// A模式指定小票退货不允许修改商品
		if (isNewUseSpecifyTicketBack()) return false;

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(index);

		// 小计、削价不处理
		if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3')) { return false; }

		// 服务费、以旧换新不处理
		if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8')) { return false; }

		// 不能打折
		if (!checkGoodsRebate(goodsDef, info))
		{
			new MessageBox(Language.apply("该商品不允许打折!"));

			return false;
		}

		// 备份数据
		SaleGoodsDef oldGoodsDef = (SaleGoodsDef) saleGoodsDef.clone();

		// 授权
		if ((curGrant.dpzkl * 100) >= 100 || !checkGoodsGrantRange(goodsDef, curGrant.grantgz))
		{
			OperUserDef staff = inputRebateGrant(index);
			if (staff == null) return false;

			// 本次授权折扣
			grantzkl = staff.dpzkl;
			grantflag = breachRebateGrant(staff);

			// 记录授权工号
			saleGoodsDef.sqkh = staff.gh;
			saleGoodsDef.sqktype = '1';
			saleGoodsDef.sqkzkfd = staff.privje1;

			//中免要求,一次性授权,不需要每次授权 start
			cursqkh = staff.gh;
			cursqktype = '1';
			cursqkzkfd = staff.privje1;
			curGrant.dpzkl = staff.dpzkl;
			curGrant.grantgz = staff.grantgz;
			curGrant.priv = getPriv2(staff.priv);
			//end

			// 记录日志
			String log = "授权单品折让,小票号:" + saleHead.fphm + ",商品:" + saleGoodsDef.barcode + ",折扣权限:" + grantzkl * 100 + "%,授权:" + staff.gh;
			AccessDayDB.getDefault().writeWorkLog(log);
		}
		else
		{
			// 本次授权折扣
			grantzkl = curGrant.dpzkl;
			grantflag = breachRebateGrant(curGrant);

			// 记录授权工号
			saleGoodsDef.sqkh = cursqkh;
			saleGoodsDef.sqktype = cursqktype;
			saleGoodsDef.sqkzkfd = cursqkzkfd;
		}

		// 计算权限允许的最大折扣额
		double maxzkl = 0;
		if (grantflag)
		{
			//new MessageBox("允许突破最低折扣");
			// 允许突破最低折扣
			maxzkl = getMaxRebateGrant(grantzkl, 0);
		}
		else
		{
			// 不允许最低折扣
			maxzkl = getMaxRebateGrant(grantzkl, goodsDef);
		}
		double maxzre = ManipulatePrecision.doubleConvert((1 - maxzkl) * saleGoodsDef.hjje, 2, 1);
		// goodsDef.maxzke为最低限价
		if ((goodsDef.maxzke * saleGoodsDef.sl) <= saleGoodsDef.hjje && saleGoodsDef.hjje - (goodsDef.maxzke * saleGoodsDef.sl) < maxzre)
		{
			maxzre = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - (goodsDef.maxzke * saleGoodsDef.sl), 2, 1);
		}

		// 输入折让
		String maxzremsg = Language.apply("收银员对该商品进行折让");

		StringBuffer buffer = new StringBuffer();
		if (GlobalInfo.sysPara.rebatepriacemode == 'Y')
		{
			// 计算最大折让到金额
			double lszre = saleGoodsDef.hjje - maxzre;
			lszre = ManipulatePrecision.doubleConvert(lszre, 2, 1);

			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				maxzremsg = Language.apply("收银员对该商品的单品折扣权限为 {0}%\n你目前对该商品最多只能够折让到 {1} 元",
						new Object[]{ManipulatePrecision.doubleToString(maxzkl * 100, 2, 1, true),ManipulatePrecision.doubleToString(lszre, 2, 1, true)});
			}

			if (!new TextBox().open(Language.apply("请输入单品折让后的成交价") + (grantflag == true ? Language.apply("(允许突破最低折扣)") : ""), Language.apply("单品折让"), maxzremsg, buffer, lszre, saleGoodsDef.hjje,
									true))
			{
				// 恢复数据
				saleGoods.setElementAt(oldGoodsDef, index);

				return false;
			}

			//得到折让额	      
			lszre = Double.parseDouble(buffer.toString());

			// 清除所有手工折扣,按输入的成交价计算最终折让
			saleGoodsDef.lszke = 0;
			saleGoodsDef.lszre = 0;
			saleGoodsDef.lszzk = 0;
			saleGoodsDef.lszzr = 0;
			saleGoodsDef.lszre = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef) - lszre, 2, 1);
		}
		else
		{
			// 计算最大可折让金额
			double lszre = maxzre - getZZK(saleGoodsDef) + saleGoodsDef.lszre;
			lszre = ManipulatePrecision.doubleConvert(lszre, 2, 1);
			if (lszre < 0) lszre = 0;

			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				maxzremsg = Language.apply("收银员对该商品的单品折扣权限为 {0}%\n你目前对该商品最多还可以再折让 {1} 元",
				                           new Object[]{ManipulatePrecision.doubleToString(maxzkl * 100, 2, 1, true),ManipulatePrecision.doubleToString(lszre, 2, 1, true)});
			}

			if (!new TextBox().open(Language.apply("请输入单品要折让的金额") + (grantflag == true ? Language.apply("(允许突破最低折扣)") : ""), Language.apply("单品折让"), maxzremsg, buffer, 0, lszre, true))
			{
				// 恢复数据
				saleGoods.setElementAt(oldGoodsDef, index);

				return false;
			}

			// 得到折让额
			saleGoodsDef.lszre = ManipulatePrecision.doubleConvert(Double.parseDouble(buffer.toString()), 2, 1);
		}

		if (getZZK(saleGoodsDef) > maxzre)
		{
			saleGoodsDef.lszre -= getZZK(saleGoodsDef) - maxzre;
			saleGoodsDef.lszre = ManipulatePrecision.doubleConvert(saleGoodsDef.lszre, 2, 1);
		}

		if (saleGoodsDef.lszre < 0) saleGoodsDef.lszre = 0;
		saleGoodsDef.lszre = getConvertRebate(index, saleGoodsDef.lszre);

		// 重算商品折扣合计
		getZZK(saleGoodsDef);

		// 重算小票应收
		calcHeadYsje();

		return true;
	}

	//检查是否允许修改当前商品
	protected boolean isModifyGoods(int index, String msg)
	{
		//打包商品有多行，拆包商品只有一行，所以当只有一行时，则允许修改数量 for fangl 2013.8.15 by wangyong
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		if (isPackGoods(saleGoodsDef))//saleGoodsDef != null && saleGoodsDef.str7 != null && saleGoodsDef.str7.length()>0 && saleGoodsDef.str7.indexOf("_")>0)
		{
			SaleGoodsDef tmpGoods;
			//循环商品列表，看是否有多行打包明细
			for (int i = saleGoods.size() - 1; i >= 0; i--)
			{
				tmpGoods = (SaleGoodsDef) saleGoods.elementAt(i);
				if (i != index && tmpGoods.str7 != null && tmpGoods.str7.equalsIgnoreCase(saleGoodsDef.str7))
				{
					//若存在多行打包明细，则不允许修改数量，否则允许修改数量
					new MessageBox(msg);
					return false;
				}

			}

		}
		return true;
	}

	/**
	 * 是否为打包商品
	 * @return true为打包商品 false为非打包商品
	 * wangyong add by 2013.110.8
	 */
	protected boolean isPackGoods(SaleGoodsDef saleGoodsDef)
	{
		try
		{
			if (saleGoodsDef != null && saleGoodsDef.str7 != null && saleGoodsDef.str7.length() > 0 && saleGoodsDef.str7.indexOf("_") > 0) { return true; }
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		return false;
	}

	public boolean paySellPop()
	{
		//doCmPopWriteData();
		if (calcCXRebate())
		{
			cxRebate = true;
		}
		//return super.paySellPop();

		// 处理CRM促销
		doRulePopExit = false;
		
		//if (GlobalInfo.sysPara.isSuperMarketPop == 'Y') doSuperMarketCrmPop();

		haveRulePop = doCrmPop();

		if (GlobalInfo.sysPara.isSuperMarketPop == 'Y') doSuperMarketCrmPop();//中免的满赠放到最后计算 by gejx/zhangli 2014.6.4
		
		if (doRulePopExit) return false; // 不再继续进行付款

		if (havePaymode)
		{
			havePaymode = false;
			return false;
		}
		return true;
	}

	public void paySellCancel()
	{
		//delCmPopReadData();

		super.paySellCancel();

		if (cxRebate)
		{
			cxRebate = false;
			calcHeadYsje();

			// 刷新商品列表
			saleEvent.updateTable(getSaleGoodsDisplay());
			saleEvent.setTotalInfo();
		}
	}

	public void findGoodsCRMPop(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		super.findGoodsCRMPop(sg, goods, info);

		String cardno = null;
		String cardtype = null;
		String isfjk = "";
		String grouplist = "";

		if ((curCustomer != null && curCustomer.iszk == 'Y'))
		{
			cardno = curCustomer.code;
			cardtype = curCustomer.type;
			if (curCustomer.func.length() >= 2) isfjk = String.valueOf(curCustomer.func.charAt(1));
			grouplist = curCustomer.valstr3;
		}

		if (!GlobalInfo.isOnline) { return; }

		//GlobalInfo.sysPara.isGroupJSLB='Y';//TEST
		if (GlobalInfo.sysPara.isGroupJSLB == 'Y')//GlobalInfo.sysPara.isGroupJSLB != 'N' || !SellType.isGroupbuy(this.saletype)
		{
			//查询商品价随量变信息
			((Zmjc_DataService) DataService.getDefault()).findBatchRule(info, sg.code, sg.gz, sg.uid, goods.str1, sg.catid, sg.ppcode, saleHead.rqsj,
																		cardno, cardtype, isfjk, grouplist, saletype, GlobalInfo.localHttp);
			if (info.Zklist != null && info.Zklist.trim().length() > 1) sg.name = "(B)" + sg.name;
		}
	}

	public boolean calcCXRebate()
	{
		// 价随量变的促销
		//if (1==1)return true;
		if (!SellType.ISSALE(saletype)) { return false; }

		if (SellType.NOPOP(saletype)) return false;

		if (SellType.ISEARNEST(saletype)) { return false; }

		if (SellType.ISPREPARETAKE(saletype)) { return false; }
		
		if (SellType.ISHH(saletype)) { return false; }
		
		Vector group = new Vector();
		// 先进行分组
		for (int i = 0; i < goodsSpare.size(); i++)
		{
			SpareInfoDef sid = (SpareInfoDef) goodsSpare.elementAt(i);
			if (sid.Zklist == null || sid.Zklist.length() <= 0 || sid.Zklist.equals("0"))
			{
				continue;
			}
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
			if (saleGoodsDef.yhzke > 0) continue;//当有分期促销或购物卡折扣率促销时,则不再参与价随量变促销 for yans 2013.9.25 sanya
			GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(i);
			int n = 0;
			for (; n < group.size(); n++)
			{
				CxRebateDef cx = (CxRebateDef) group.elementAt(n);
				String condition = null;
				if (sid.Zklist.indexOf("1:") == 0) condition = sid.Zklist;/* 折扣列表 1:X|2:Y|3:Z*/
				else condition = sid.Zklist;/* 折扣列表 1:X|2:Y|3:Z*/
				//？？ cx.seq.equals(sid.seq) 不相同
				if (cx.addrule.equals(sid.addrule) && cx.Zklist.equals(condition) && cx.pmbillno.equals(sid.pmbillno) && cx.bz.equals(sid.bz))
				{
					boolean cond = true;
					if (cx.addrule.length() > 0 && cx.addrule.charAt(0) == 'Y')
					{
						cond = cond && cx.gys.equals(goodsDef.str1);
					}

					if (cx.addrule.length() > 1 && cx.addrule.charAt(1) == 'Y')
					{
						cond = cond && cx.gz.equals(goodsDef.gz);
					}

					if (cx.addrule.length() > 2 && cx.addrule.charAt(2) == 'Y')
					{
						cond = cond && cx.pp.equals(goodsDef.ppcode);
					}

					if (cx.addrule.length() > 3 && cx.addrule.charAt(3) == 'Y')
					{
						cond = cond && cx.catid.equals(goodsDef.catid);
					}

					if (cx.addrule.length() > 4 && cx.addrule.charAt(4) == 'Y')
					{
						cond = cond && cx.code.equals(goodsDef.code);
					}

					if (cond)
					{
						cx.zsl = ManipulatePrecision.doubleConvert(cx.zsl + saleGoodsDef.sl);
						cx.list.add(String.valueOf(i));
						break;
					}
				}
			}

			if (n >= group.size())
			{
				CxRebateDef cx = new CxRebateDef();
				cx.pmbillno = sid.pmbillno;/*促销单号*/
				cx.addrule = sid.addrule;/*累计规则 'YYYYY'*/
				if (sid.Zklist.indexOf("1:") == 0) cx.Zklist = sid.Zklist;/* 折扣列表 1:X|2:Y|3:Z*/
				else cx.Zklist = sid.Zklist;/* 折扣列表 1:X|2:Y|3:Z*/
				cx.pmrule = sid.pmrule;/*价随量变规则*/
				cx.etzkmode2 = sid.etzkmode2;/*1-阶梯折扣 2-统一打折*/
				cx.seq = sid.seq; /*规则序号*/
				cx.zkfd = Convert.toDouble(sid.zkfd);
				cx.bz = sid.bz;
				cx.maxnum = sid.maxnum;

				cx.zsl = ManipulatePrecision.doubleConvert(saleGoodsDef.sl);
				cx.list.add(String.valueOf(i));

				cx.gys = goodsDef.str1;
				cx.gz = goodsDef.gz;
				cx.pp = goodsDef.ppcode;
				cx.catid = goodsDef.catid;
				cx.code = goodsDef.code;

				group.add(cx);
			}
		}

		if (group.size() <= 0) return false;
		//检查促销生效
		for (int i = 0; i < group.size(); i++)
		{
			CxRebateDef cx = (CxRebateDef) group.elementAt(i);

			//new MessageBox("开始检查促销生效信息"+cx.Zklist+" "+i);

			String zklist = cx.Zklist;
			String[] zk = zklist.split("\\|");
			int n = zk.length - 1;
			for (; n >= 0; n--)
			{
				String rule = zk[n];
				double sl = Convert.toDouble(rule.substring(0, rule.indexOf(":")));
				double zkl = Convert.toDouble(rule.substring(rule.indexOf(":") + 1));
				if (cx.zsl < sl)
				{
					continue;
				}
				else
				{
					cx.sl_cond = sl;
					cx.zkl_result = zkl;
					break;
				}
			}

			if (n < 0)
			{
				group.removeElementAt(i);
				i--;
			}
		}

		boolean done = false;
		//开始计算促销
		for (int i = 0; i < group.size(); i++)
		{
			CxRebateDef cx = (CxRebateDef) group.elementAt(i);

			String zkcheck1 = cx.Zklist;
			String[] zkcheck2 = zkcheck1.split("\\|");
			if (zkcheck2[0] != null)
			{
				double sl1 = Convert.toDouble(zkcheck2[0].substring(0, zkcheck2[0].indexOf(":")));
				//new MessageBox(String.valueOf(sl1));
				if (sl1 > 1)
				{
					cx.Zklist = "1:1|" + cx.Zklist;
				}
				//new MessageBox(cx.Zklist);
			}

			for (int x = 0; x < cx.list.size(); x++)
			{
				int index = Convert.toInt(cx.list.elementAt(x));
				SpareInfoDef sid = (SpareInfoDef) goodsSpare.elementAt(index);
				SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
				double yhzke = 0;
				//new MessageBox("计算促销 "+Convert.toInt(cx.etzkmode2)+" "+cx.bz);
				//  不循环且为阶梯折扣时
				if (Convert.toInt(cx.etzkmode2) == 1 && cx.bz.trim().equals("N"))
				{
					yhzke = caculateSL(cx, saleGoodsDef, saleGoodsDef.sl, cx.Zklist.split("\\|"), 0);
				}
				else if (Convert.toInt(cx.etzkmode2) == 1 && cx.bz.trim().equals("Y"))
				{
					yhzke = caculateSL1(cx, saleGoodsDef, saleGoodsDef.sl, cx.Zklist.split("\\|"), 0);
					//new MessageBox("done");
				}
				else if (Convert.toInt(cx.etzkmode2) == 2 && cx.bz.trim().equals("Y"))
				{
					if (cx.cursl + saleGoodsDef.sl > cx.maxnum)
					{
						double num1 = ManipulatePrecision.doubleConvert(cx.maxnum - cx.cursl);
						yhzke = ManipulatePrecision.doubleConvert(saleGoodsDef.jg * (num1) * (1 - cx.zkl_result));
						cx.cursl = cx.maxnum;
					}
					else
					{
						yhzke = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje * (1 - cx.zkl_result));
						cx.cursl = ManipulatePrecision.doubleConvert(cx.cursl + saleGoodsDef.sl);
					}
				}
				else
				{
					yhzke = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje * (1 - cx.zkl_result));
				}

				if (yhzke >= 0)
				{

					saleGoodsDef.hyzke = 0;
					saleGoodsDef.zszke = 0;
					saleGoodsDef.lszke = 0;
					saleGoodsDef.lszre = 0;
					saleGoodsDef.lszzk = 0;
					saleGoodsDef.lszzr = 0;
					saleGoodsDef.yhzke = yhzke;
					saleGoodsDef.yhzke = getConvertRebate(index, saleGoodsDef.yhzke);
					getZZK(saleGoodsDef);
					saleGoodsDef.yhzkfd = cx.zkfd;
					saleGoodsDef.yhdjbh = cx.pmbillno;
					if (sid.str1 != null)
					{
						StringBuffer buf = new StringBuffer(sid.str1.trim());
						for (int z = 0; z < buf.length(); z++)
						{
							buf.setCharAt(z, '0');
						}
						sid.str1 = buf.toString();
						saleGoodsDef.isvipzk = 'N';
					}

					done = true;
				}
			}

		}

		if (done)
		{
			//重算应收
			calcHeadYsje();

			// 刷新商品列表
			saleEvent.updateTable(getSaleGoodsDisplay());
			saleEvent.setTotalInfo();
			return true;
		}
		return false;

	}

	//阶梯且循环
	public double caculateSL1(CxRebateDef cx, SaleGoodsDef sgd, double use_sl, String[] zk, int index)
	{
		double sumzk = 0;
		for (int i = 0; i < use_sl; i++)
		{
			cx.cursl++;

			int n = zk.length - 1;
			for (; n >= 0; n--)
			{
				String rule = zk[n];
				//new MessageBox(rule);
				double sl = Convert.toDouble(rule.substring(0, rule.indexOf(":")));
				double zkl = Convert.toDouble(rule.substring(rule.indexOf(":") + 1));

				//代表为最后一级，需要循环
				if (cx.cursl >= sl && n == (zk.length - 1))
				{
					cx.cursl = 0;
					sumzk = ManipulatePrecision.doubleConvert(sumzk + sgd.jg * (1 - zkl));
					break;
				}

				if (cx.cursl >= sl)
				{
					sumzk = ManipulatePrecision.doubleConvert(sumzk + sgd.jg * (1 - zkl));
					break;
				}

			}
		}

		return sumzk;
	}

	public double caculateSL(CxRebateDef cx, SaleGoodsDef sgd, double use_sl, String[] zk, int index)
	{
		if (index >= zk.length) return 0;

		if (index == 0)
		{
			int n = 0;
			for (; n < zk.length; n++)
			{
				String rule = zk[n];
				double sl = Convert.toDouble(rule.substring(0, rule.indexOf(":")));
				double sl1 = -1;
				if ((n + 1) < zk.length)
				{
					sl1 = Convert.toDouble(zk[n + 1].substring(0, zk[n + 1].indexOf(":")));
				}

				if (sl1 < 0)
				{

					double use_sl1 = ManipulatePrecision.doubleConvert(cx.cursl + use_sl - sl);
					if (use_sl1 <= 0)
					{
						continue;
					}
					use_sl = use_sl1;
					index = n;
					break;
				}
				else if (cx.cursl > sl && cx.cursl >= sl1)
				{

					continue;
				}
				else if (cx.cursl >= sl && cx.cursl < sl1)
				{
					if (sl1 - cx.cursl <= use_sl) index = n + 1;
					else index = n;
					break;
				}
				else if (ManipulatePrecision.doubleConvert(cx.cursl + use_sl) >= sl1 && ManipulatePrecision.doubleConvert(cx.cursl + use_sl) > sl)
				{
					index = n;
					break;

				}
				else if (ManipulatePrecision.doubleConvert(cx.cursl + use_sl) >= sl)
				{
					index = n;
					break;
				}
			}

			if (n >= zk.length)
			{
				cx.cursl = ManipulatePrecision.doubleConvert(cx.cursl + use_sl);
				return 0;
			}
		}

		String rule = zk[index];

		double sl = Convert.toDouble(rule.substring(0, rule.indexOf(":")));
		double zkl = Convert.toDouble(rule.substring(rule.indexOf(":") + 1));

		boolean done_b = false;
		if ((index + 1) < zk.length)
		{
			double sl1 = Convert.toDouble(zk[index + 1].substring(0, zk[index + 1].indexOf(":")));
			if (cx.cursl >= sl1) done_b = true;
		}

		//证明有下一及
		if ((index + 1) < zk.length)
		{
			String rule1 = zk[index + 1];
			double sl1 = Convert.toDouble(rule1.substring(0, rule1.indexOf(":")));

			//本级需要计算的数量
			double x_sl = (cx.cursl + use_sl - sl1);
			if (x_sl < 0)
			{
				cx.cursl = ManipulatePrecision.doubleConvert(cx.cursl + use_sl);
				double zkje = ManipulatePrecision.doubleConvert(sgd.jg * (use_sl) * (1 - zkl));
				return zkje;
			}
			else
			{
				x_sl = ManipulatePrecision.doubleConvert(sl1 - sl);
			}
			double zkje = ManipulatePrecision.doubleConvert(sgd.jg * x_sl * (1 - zkl));

			//当前已经计算的数量
			cx.cursl = ManipulatePrecision.doubleConvert(cx.cursl + x_sl);
			//当前未计算的数量
			double y_sl = ManipulatePrecision.doubleConvert(use_sl - x_sl);
			return zkje + caculateSL(cx, sgd, y_sl, zk, (index + 1));
		}
		else
		//没有下一集，用本级计算
		{
			double zkje = ManipulatePrecision.doubleConvert(sgd.jg * use_sl * (1 - zkl));
			return zkje;
		}
	}

	class CxRebateDef
	{
		public String pmbillno;/*促销单号*/
		public String addrule;/*累计规则 'YYYYY'*/
		public String Zklist;/* 折扣列表 1:X|2:Y|3:Z*/
		public String pmrule;/*价随量变规则*/
		public String etzkmode2;/*1-阶梯折扣 2-统一打折*/
		public String seq; /*规则序号*/
		public double zkfd;
		public String bz;/*阶梯折扣时  Y/N是否循环   统一折扣时  Y/N是否启用上限数量*/
		public double maxnum; /*上限数量*/

		public double zsl;

		public double sl_cond; // 满足数量条件
		public double zkl_result;//折扣率
		public double cursl; //计算阶梯折扣时，记录当前数量

		//供应商 柜组 品牌 类别 商品
		public String gys;
		public String gz;
		public String pp;
		public String catid;
		public String code;

		Vector list = new Vector();
	};

	public Vector getPayModeBySuper(String sjcode, StringBuffer index, String code)
	{
		Vector child = new Vector();
		String[] temp = null;
		PayModeDef mode = null;
		int k = -1;
		for (int i = 0; i < GlobalInfo.payMode.size(); i++)
		{
			mode = (PayModeDef) GlobalInfo.payMode.elementAt(i);

			if ((mode.sjcode.trim().equals(sjcode.trim()) || (sjcode.equals("0") && mode.sjcode.trim().equals(mode.code))) && getPayModeByNeed(mode))
			{
				k++;

				// 标记code付款方式在vector中的位置
				if (index != null && code != null && mode.code.compareTo(code) == 0)
				{
					index.append(String.valueOf(k));
				}

				//
				if (GlobalInfo.sysPara.salepayDisplayRate == 'Y')
				{
					temp = new String[3];
					temp[0] = mode.code.trim();
					temp[1] = mode.name;
					temp[2] = ManipulatePrecision.doubleToString(mode.hl, 6, 1, false);//4-->6
				}
				else
				{
					temp = new String[2];
					temp[0] = mode.code.trim();
					temp[1] = mode.name;
					if (mode.hl != 1) temp[1] = temp[1] + "<" + ManipulatePrecision.doubleToString(mode.hl, 6, 1, false) + ">";//4-->6
				}
				child.add(temp);
			}
		}

		return child;
	}

	public void initNewSale()
	{

		super.initNewSale();
		//SaleBillMode.getDefault().setTemplateObject(saleHead, saleGoods, salePayment);
		((Zmjc_SaleBillMode) SaleBillMode.getDefault()).setTemplateObject(saleHead, saleGoods, salePayment, saleCust);
	}

	public void printSaleBill()
	{
		//super.printSaleBill();

		// 打印小票前先查询满赠信息并设置到打印模板供打印
		if (!SellType.ISEXERCISE(saletype))
		{
			DataService dataservice = (DataService) DataService.getDefault();
			Vector gifts = dataservice.getSaleTicketMSInfo(saleHead, saleGoods, salePayment);
			SaleBillMode.getDefault(saleHead.djlb).setSaleTicketMSInfo(saleHead, gifts);
		}

		// 恢复暂停状态的实时打印
		stopRealTimePrint(false);

		// 实时打印只打印剩余部分
		if (isRealTimePrint())
		{
			//SaleBillMode.getDefault(saleHead.djlb).setTemplateObject(saleHead, saleGoods, salePayment);
			((Zmjc_SaleBillMode) SaleBillMode.getDefault(saleHead.djlb)).setTemplateObject(saleHead, saleGoods, salePayment, saleCust);

			// 标记即扫即打结束
			Printer.getDefault().enableRealPrintMode(false);

			// 打印那些即扫即打未打印的商品
			for (int i = 0; i < saleGoods.size(); i++)
				realTimePrintGoods(null, i);

			// 打印即扫即打剩余小票部分
			SaleBillMode.getDefault(saleHead.djlb).printRealTimeBottom();

			//
			setHaveRealTimePrint(false);
		}
		else
		{
			//SaleBillMode.getDefault(saleHead.djlb).setTemplateObject(saleHead, saleGoods, salePayment);
			((Zmjc_SaleBillMode) SaleBillMode.getDefault(saleHead.djlb)).setTemplateObject(saleHead, saleGoods, salePayment, saleCust);
			// 打印整张小票
			PosLog.getLog(this.getClass().getSimpleName()).info("printSaleBill() 打印开始 syjh=[" + saleHead.syjh + "],fphm=[" + saleHead.fphm + "].");
			SaleBillMode.getDefault(saleHead.djlb).printBill();
			PosLog.getLog(this.getClass().getSimpleName()).info("printSaleBill() 打印结束");
		}

		// 只在交易完成时打印一次移动离线充值券,因此无需放到小票模板中
		if (GlobalInfo.useMobileCharge)
		{
			PaymentBankCMCC pay = CreatePayment.getDefault().getPaymentMobileCharge(this.saleEvent.saleBS);
			if (pay != null) pay.printOfflineChargeBill(saleHead.fphm);
		}

	}

	public void setZL(Label lblZL, Label lblStatus, String memo)
	{
		try
		{
			if (lblZL == null) return;
			String zl = "";
			String bzl = "";
			for (int i = 0; i < salePayment.size(); i++)
			{
				SalePayDef spd = (SalePayDef) salePayment.elementAt(i);

				// 付款不打印
				if (spd.flag != '2')
				{
					continue;
				}
				if (spd.payname.indexOf(Language.apply("补")) == 0)
				{
					//补找零
					bzl = spd.payname + ":" + ManipulatePrecision.doubleToString(spd.ybje);
				}
				else
				{
					//找零
					zl = spd.payname + ":" + ManipulatePrecision.doubleToString(spd.ybje);
				}
			}

			//格式: 美元:0.00 补人民币:3
			lblZL.setText(zl + " " + bzl);
			lblStatus.setText(Language.apply("按\"回车\"键开始付款，按其他键退出付款"));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	// 重新打印上一张小票
	public void rePrint()
	{
		ResultSet rs = null;
		SaleHeadDef saleheadprint = null;
		Vector salegoodsprint = null;
		Vector salepayprint = null;
		SaleCustDef saleCustPrint = null; //小票顾客信息

		// 盘点
		if (SellType.ISCHECKINPUT(saletype))
		{
			if (saleGoods == null || saleGoods.size() <= 0) return;

			if (!CheckGoodsMode.getDefault().isLoad()) return;

			MessageBox me = new MessageBox(Language.apply("你确实要打印盘点小票吗?"), null, true);

			if (me.verify() != GlobalVar.Key1) return;

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

		//MessageBox me = new MessageBox("你确实要重印上一张小票吗?", null, true);
		try
		{
			if (getReprintAuth())//(me.verify() == GlobalVar.Key1 && getReprintAuth())
			{
				//Object obj = null;
				String fphm = null;
				String syjh = ConfigClass.CashRegisterCode;

				if (curGrant.privdy != 'Y' && curGrant.privdy != 'L')
				{
					OperUserDef user = null;
					if ((user = DataService.getDefault().personGrant(Language.apply("授权重打印小票"))) != null)
					{
						if (user.privdy != 'Y' && user.privdy != 'L')
						{
							new MessageBox(Language.apply("当前工号没有重打上笔小票权限!"));

							return;
						}

						String log = "授权重打印上一笔小票,授权工号:" + user.gh;
						AccessDayDB.getDefault().writeWorkLog(log);
					}
					else
					{
						return;
					}
				}

				//新增默认上一笔的小票号、收银机号 wangyong by 2013.9.3
				RetSYJForm frm = new RetSYJForm();
				int done = frm.open(null, -1, Language.apply("请输入【重印】收银机号和小票号"));
				if (done == frm.Done)
				{
					syjh = RetSYJForm.syj;
					fphm = String.valueOf(RetSYJForm.fph);
				}
				else
				{
					// 放弃重打印
					return;
				}

				//if ((obj = GlobalInfo.dayDB.selectOneData("select max(fphm) from salehead where syjh = '" + syjh + "'")) != null)
				//{
				Sqldb db = getPrintSqlDB(syjh, fphm);
				if (db==null)
				{
					new MessageBox(Language.apply("没有查询到小票,不能打印!"));
					return;
				}
				try
				{
					//fphm = String.valueOf(obj);
					
					if ((rs = db.selectData("select * from salehead where syjh = '" + syjh + "' and  fphm = " + fphm)) != null)
					{

						if (!rs.next())
						{
							new MessageBox(Language.apply("没有查询到小票头,不能打印!"));
							return;
						}

						saleheadprint = new SaleHeadDef();

						if (!db.getResultSetToObject(saleheadprint)) { return; }
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
					PosLog.getLog(this.getClass().getSimpleName()).error(ex);
					return;
				}
				finally
				{
					GlobalInfo.dayDB.resultSetClose();
				}

				try
				{
					if ((rs = db.selectData("select * from SALEGOODS where syjh = '" + syjh + "' and fphm = " + fphm
							+ " order by rowno")) != null)
					{
						boolean ret = false;
						salegoodsprint = new Vector();
						while (rs.next())
						{
							SaleGoodsDef sg = new SaleGoodsDef();

							if (!db.getResultSetToObject(sg)) { return; }

							salegoodsprint.add(sg);
							saleheadprint.yfphm = String.valueOf(sg.yfphm);//wangyong add by 2013.10.16 记录原小票信息,否则打印不了
							saleheadprint.ysyjh = sg.ysyjh;

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
					PosLog.getLog(this.getClass().getSimpleName()).error(ex);
					return;
				}
				finally
				{
					db.resultSetClose();
				}

				try
				{
					if ((rs = db.selectData("select * from SALEPAY where syjh = '" + syjh + "' and fphm = " + fphm + " order by rowno")) != null)
					{
						boolean ret = false;
						salepayprint = new Vector();
						while (rs.next())
						{
							SalePayDef sp = new SalePayDef();

							if (!db.getResultSetToObject(sp)) { return; }

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
					PosLog.getLog(this.getClass().getSimpleName()).error(ex);
					return;
				}
				finally
				{
					db.resultSetClose();
				}

				try
				{
					//读取会员信息
					if ((rs = db.selectData("select * from SALECUST where syjh = '" + syjh + "' and fphm = " + String.valueOf(fphm)
							+ " ")) != null)
					{
						boolean ret = false;
						saleCustPrint = new SaleCustDef();
						ParaNodeDef node;
						while (rs.next())
						{
							node = new ParaNodeDef();
							node.code = CustInfoDef.CUST_SCPASSPORTNO;
							node.name = "";
							node.value = CommonMethod.isNull(rs.getString(node.code), "");
							saleCustPrint.custAdd(node.code, node);

							node = new ParaNodeDef();
							node.code = CustInfoDef.CUST_SCNATIONALITY;
							node.name = "";
							node.value = CommonMethod.isNull(rs.getString(node.code), "");
							saleCustPrint.custAdd(node.code, node);

							node = new ParaNodeDef();
							node.code = CustInfoDef.CUST_SCID;
							node.name = "";
							node.value = CommonMethod.isNull(rs.getString(node.code), "");
							saleCustPrint.custAdd(node.code, node);

							node = new ParaNodeDef();
							node.code = CustInfoDef.CUST_SCOTHERNO;
							node.name = "";
							node.value = CommonMethod.isNull(rs.getString(node.code), "");
							saleCustPrint.custAdd(node.code, node);

							node = new ParaNodeDef();
							node.code = CustInfoDef.CUST_SCNUMBER;
							node.name = "";
							node.value = CommonMethod.isNull(rs.getString(node.code), "");
							saleCustPrint.custAdd(node.code, node);

							node = new ParaNodeDef();
							node.code = CustInfoDef.CUST_SCNAME;
							node.name = "";
							node.value = CommonMethod.isNull(rs.getString(node.code), "");
							saleCustPrint.custAdd(node.code, node);

							node = new ParaNodeDef();
							node.code = CustInfoDef.CUST_SCSEX;
							node.name = "";
							node.value = CommonMethod.isNull(rs.getString(node.code), "");
							saleCustPrint.custAdd(node.code, node);

							node = new ParaNodeDef();
							node.code = CustInfoDef.CUST_SCMEMO;
							node.name = "";
							node.value = CommonMethod.isNull(rs.getString(node.code), "");
							saleCustPrint.custAdd(node.code, node);

							ret = true;
						}
						if (!ret)
						{
							//new MessageBox("没有查询到付款明细,不能打印!");
							//return;
							saleCustPrint = null;
						}
					}
					else
					{
						//new MessageBox("查询付款明细失败!", null, false);
						//return;

						//没有找到顾客信息
						saleCustPrint = null;
					}

				}
				catch (Exception ex)
				{
					new MessageBox(Language.apply("查询付款明细出现异常!"), null, false);
					PosLog.getLog(this.getClass().getSimpleName()).error(ex);
					return;
				}
				finally
				{
					db.resultSetClose();
				}

				saleheadprint.printnum++;
				AccessDayDB.getDefault().updatePrintNum(saleheadprint.syjh, String.valueOf(saleheadprint.fphm),
														String.valueOf(saleheadprint.printnum));
				ProgressBox pb = new ProgressBox();
				pb.setText(Language.apply("现在正在重打印小票,请等待....."));
				try
				{
					printSaleTicket(saleheadprint, salegoodsprint, salepayprint, saleCustPrint, false);
				}
				finally
				{
					pb.close();
				}
				//}
				//else
				//{
				//new MessageBox("当前没有销售数据,不能打印!");
				//}
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

	protected Sqldb getPrintSqlDB(String syjh, String fphm)
	{
		Sqldb db = null;
		ProgressBox pb = null;
		try
		{
			pb = new ProgressBox();
			pb.setText(Language.apply("正在查找小票, 请稍等..."));
			
			String path = ConfigClass.LocalDBPath + "Invoice";//"C:\\JavaPOS\\javaPos.Database\\Invoice";

			File dir = new File(path);
			File[] fileList = dir.listFiles();
			String dbpath;
			File f;
			for (int j = fileList.length - 1; j >= 0; j--)
			{
				if (!fileList[j].isDirectory()) continue;

				//检查文件是否存在
				dbpath = ConfigClass.LocalDBPath + "Invoice/" + fileList[j].getName().trim() + "/" + LoadSysInfo.getDefault().getDayDBName();
				f = new File(dbpath);
				if (!f.exists()) continue;

				//获取数据源
				db = LoadSysInfo.getDefault().loadDayDB(ManipulateDateTime.getConversionDate(fileList[j].getName().trim()));

				//检查是否存在小票
				if (isExistsSale(db, syjh, fphm))
				{
					//存在,退出查找
					db.resultSetClose();
					PosLog.getLog(this.getClass().getSimpleName()).info("找到小票: syjh=[" + syjh + "],fphm=[" + fphm + "],date=[" +  fileList[j].getName().trim()+ "].");
					break;
				}
				else
				{
					//不存在,继续查找
					db = null;
					PosLog.getLog(this.getClass().getSimpleName()).info("未找到小票: syjh=[" + syjh + "],fphm=[" + fphm + "].");
					continue;
				}
			}
		}
		catch (Exception ex)
		{
			db = null;
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		finally
		{
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
		}
		return db;
	}

	//检查是否存在小票号
	protected boolean isExistsSale(Sqldb db, String syjh, String fphm)
	{
		boolean blnRet = false;
		try
		{
			if (db == null) return false;
			
			ResultSet rs = null;
			if ((rs = db.selectData("select * from salehead where syjh = '" + syjh + "' and  fphm = " + fphm)) != null)
			{

				if (!rs.next())
				{
					//new MessageBox("没有查询到小票头,不能打印!");
					return false;
				}
				return true;
			}
			else
			{
				//new MessageBox("查询小票头失败!", null, false);
				return false;
			}
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		return blnRet;
	}

	public void printSaleTicket(SaleHeadDef vsalehead, Vector vsalegoods, Vector vsalepay, SaleCustDef saleCust, boolean isRed)
	{
		String type = "SalePrintMode.ini";
		if (vsalehead != null && vsalehead.djlb != null) type = vsalehead.djlb;
		SaleHeadDef tempsalehead = null;
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
			if (!bok)
			{
				SaleBillMode.getDefault(type).setSaleTicketMSInfo(vsalehead, null);
			}

			if (vsalehead != null && vsalegoods != null && vsalepay != null)
			{
				((Zmjc_SaleBillMode) SaleBillMode.getDefault(type)).setTemplateObject(vsalehead, vsalegoods, vsalepay, saleCust);
				SaleBillMode.getDefault(type).printBill();
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
			((Zmjc_SaleBillMode) SaleBillMode.getDefault(type)).setTemplateObject(tempsalehead, tempsalegoods, tempsalepay, saleCust);
		}
	}

	/*	public String[] rowInfo(SaleGoodsDef goodsDef)
	 {
	 String[] detail = super.rowInfo(goodsDef);
	 if (SellType.ISCHECKINPUT(saletype))
	 {
	 
	 }
	 else
	 {		    	
	 if (GlobalInfo.sysPara.barcodeshowcode == 'Y')
	 {
	 //detail[1] = goodsDef.code;
	 }
	 else
	 {
	 //detail[1] = goodsDef.barcode;
	 detail[2] = "["+goodsDef.code + "]"+goodsDef.name;
	 }
	 
	 
	 }
	 return detail;
	 }*/

	public GoodsDef findGoodsInfo(String code, String yyyh, String gz, String dzcmscsj, boolean isdzcm, StringBuffer slbuf, boolean iszdxp)
	{
		GoodsDef goods = super.findGoodsInfo(code, yyyh, gz, dzcmscsj, isdzcm, slbuf, iszdxp);
		//GoodsDef goods = findGoodsInfo_ZMJC_CLK(code, yyyh, gz, dzcmscsj, isdzcm, slbuf, iszdxp);
		if (goods != null)
		{
			if (goods.isdzc != 'Y' && goods.kcsl < 1 && goods.kcsl > 0)
			{
				//当普通商品的库存数量为小数时,则将当前商品的默认扫码数量置为库存数,然后款员再去修改合适的数量 for fangl
				if (slbuf != null)
				{
					slbuf.delete(0, slbuf.length());
					slbuf.append(goods.kcsl);
				}
			}
		}
		return goods;
	}

	protected boolean cancelMemberOrGoodsRebate(int index)
	{
		if ((this.saletype.equals(SellType.PREPARE_BACK) || this.saletype.equals(SellType.PREPARE_TAKE))) return false;
		if (isNewUseSpecifyTicketBack(false)) return false;

		// true=先打折，后刷VIP卡，因此取消时先取消VIP，再取消折扣
		// false=先VIP，再打折，因此取消时先取消折扣，再取消VIP
		if (memberAfterGoodsMode())
		{
			// 取消VIP
			if (checkMemberSale())
			{
				if (new MessageBox(Language.apply("已经刷了VIP卡,你确定要取消VIP卡吗?"), null, true).verify() == GlobalVar.Key1)
				{
					// 记录当前顾客卡
					curCustomer = null;

					// 记录到小票        	
					saleHead.hykh = null;

					// 重算所有商品应收
					for (int i = 0; i < saleGoods.size(); i++)
					{
						calcGoodsYsje(i);
					}

					// 计算小票应收
					calcHeadYsje();
					saleEvent.updateSaleGUI();
				}
				return true;
			}

			// 取消临时折扣
			if (index >= 0 && 1 == 2)
			{
				SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
				double sum = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke + saleGoodsDef.lszre + saleGoodsDef.lszzk + saleGoodsDef.lszzr);
				if (sum > 0)
				{
					if (new MessageBox("【" + saleGoodsDef.name + "】" +Language.apply("存在临时折扣\n你确定要取消此商品的临时折扣吗?"), null, true).verify() == GlobalVar.Key1)
					{
						saleGoodsDef.lszke = 0;
						saleGoodsDef.lszre = 0;
						saleGoodsDef.lszzk = 0;
						saleGoodsDef.lszzr = 0;

						// 计算小票应收
						calcGoodsYsje(index);
						calcHeadYsje();
						saleEvent.updateSaleGUI();
					}
					return true;
				}
			}
			else
			{
				return clearAllGoodsZK();
			}
		}
		else
		{
			// 取消临时折扣
			if (index >= 0 && 1 == 2)
			{
				SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
				double sum = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke + saleGoodsDef.lszre + saleGoodsDef.lszzk + saleGoodsDef.lszzr);
				if (sum > 0)
				{
					if (new MessageBox("【" + saleGoodsDef.name + "】" + Language.apply("存在临时折扣\n你确定要取消此商品的临时折扣吗?"), null, true).verify() == GlobalVar.Key1)
					{
						saleGoodsDef.lszke = 0;
						saleGoodsDef.lszre = 0;
						saleGoodsDef.lszzk = 0;
						saleGoodsDef.lszzr = 0;

						// 计算小票应收
						calcGoodsYsje(index);
						calcHeadYsje();
						saleEvent.updateSaleGUI();
					}
					return true;
				}
			}
			else
			{
				return clearAllGoodsZK();
			}

			// 先刷卡状态下如果取消VIP必须取消整单
			/**
			 if (checkMemberSale())
			 {
			 if (new MessageBox("已经刷了VIP卡,你确定要取消VIP卡吗?",null,true).verify()==GlobalVar.Key1)
			 {
			 // 记录当前顾客卡
			 curCustomer = null;
			 
			 // 记录到小票        	
			 saleHead.hykh = null;
			 saleHead.hytype = null;
			 
			 // 重算所有商品应收
			 for (int i=0;i<saleGoods.size();i++)
			 {
			 calcGoodsYsje(i);
			 }
			 
			 // 计算小票应收
			 calcHeadYsje();
			 saleEvent.updateSaleGUI();
			 }
			 return true;
			 }
			 */
		}

		// 返回false,执行基类取消交易的处理
		return false;
	}

	//清除商品所有手工折扣
	public boolean clearAllGoodsZK()
	{
		boolean isExistsZK = false;
		try
		{
			//中免要求:清除整单折扣,而不是单个商品的
			SaleGoodsDef saleGoodsDef;
			// 重算所有商品应收
			for (int i = 0; i < saleGoods.size(); i++)
			{
				saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);

				if (!isExistsZK)
				{
					double sum = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke + saleGoodsDef.lszre + saleGoodsDef.lszzk + saleGoodsDef.lszzr);
					if (sum > 0)
					{
						if (new MessageBox(Language.apply("你确定要取消【所有商品】的临时折扣吗?"), null, true).verify() != GlobalVar.Key1) { return true; }
						isExistsZK = true;
					}
				}

				saleGoodsDef.lszke = 0;
				saleGoodsDef.lszre = 0;
				saleGoodsDef.lszzk = 0;
				saleGoodsDef.lszzr = 0;

				calcGoodsYsje(i);
			}

			// 计算小票应收
			calcHeadYsje();
			saleEvent.updateSaleGUI();

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return isExistsZK;
	}

	public int inputGoodsGZ(GoodsDef goodsDef, int searchFlag, String code, String scsj, String yhsj, String djlb)
	{
		//自选柜组信息,待重载
		return super.inputGoodsGZ(goodsDef, searchFlag, code, scsj, yhsj, djlb);
	}
	
	public boolean saleSummary()
	{
		boolean blnRet = super.saleSummary();
		try
		{
			if (blnRet && saleGoods != null && saleGoods.size() > 0)
			{
				SaleGoodsDef saleGoodsDef = null;
				for (int i = 0; i < saleGoods.size(); i++)
				{
					saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
					PosLog.getLog(this.getClass().getSimpleName()).info("saleSummary() sqkh=[" + saleGoodsDef.sqkh + "],gh=[" + GlobalInfo.posLogin.gh + "].");
					if (saleGoodsDef.sqkh != null && saleGoodsDef.sqkh.trim().length() > 0 && saleGoodsDef.sqkh.trim().equalsIgnoreCase(GlobalInfo.posLogin.gh))
					{
						//当授权卡号为当前收银员时,则不允许(在没有授权的情况下,不能记为本身) for yans by wangyong add 2013.11.08
						saleGoodsDef.sqkh = "";
						PosLog.getLog(this.getClass().getSimpleName()).info("sqkh clear");
						//saleGoodsDef.sqktype = '\0';
						//saleGoodsDef.sqkzkfd = 0;
					}
				}
			}
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}		
		return blnRet;
	}
	
	public boolean writeHangGrant()
	{
		if (super.writeHangGrant())
		{
			MessageBox me = new MessageBox(Language.apply("是否需要挂单?\n\n任意键-是 / 退出键-否"), null, false);
			if (me.verify() != GlobalVar.Exit) return true;			
		}
		return false;
	}
	

	public boolean doSuperMarketCrmPop()
	{
		saleHead.str10="";
		if (!SellType.ISSALE(saletype)) { return false; }

		if (SellType.NOPOP(saletype)) { return false; }

		if (SellType.ISEARNEST(saletype)) { return false; }

		if (SellType.ISPREPARETAKE(saletype)) { return false; }

		// 初始化超市促销标志
		for (int i = 0; i < saleGoods.size(); i++)
			((SaleGoodsDef) saleGoods.get(i)).isSMPopCalced = 'Y';

		// 排序
		if (saleGoods.size() > 1)
		{
			SaleGoodsDef sgd = null;
			GoodsDef gd = null;
			SpareInfoDef sid = null;
			GoodsPopDef gpd = null;
			for (int i = 1; i < saleGoods.size(); i++)
			{
				for (int j = 0; j < saleGoods.size() - i; j++)
				{
					String code = ((SaleGoodsDef) saleGoods.get(j)).code;
					String code1 = ((SaleGoodsDef) saleGoods.get(j + 1)).code;

					if (code.equals(code1)) continue;

					if (code1.length() < code.length() || (code1.length() == code.length() && code1.compareTo(code) < 0))
					{
						sgd = (SaleGoodsDef) saleGoods.get(j);
						saleGoods.setElementAt(saleGoods.get(j + 1), j);
						saleGoods.setElementAt(sgd, j + 1);

						gd = (GoodsDef) goodsAssistant.get(j);
						goodsAssistant.setElementAt(goodsAssistant.get(j + 1), j);
						goodsAssistant.setElementAt(gd, j + 1);

						sid = (SpareInfoDef) goodsSpare.get(j);
						goodsSpare.setElementAt(goodsSpare.get(j + 1), j);
						goodsSpare.setElementAt(sid, j + 1);

						gpd = (GoodsPopDef) crmPop.get(j);
						crmPop.setElementAt(crmPop.get(j + 1), j);
						crmPop.setElementAt(gpd, j + 1);
					}
				}
			}
		}

		// 查找规则
		SuperMarketPopRuleDef ruleDef = null;
		Vector notRuleDjbh = new Vector();
		int calcCount = saleGoods.size();
		int k, j, l, m, n;
		double zje, je, t_zje;
		double or_yhsl = 0;//结果为OR关系的时候,存放第一个结果的数量
		long bs, minbs, t_minbs;

		String cardNo = "";
		if (curCustomer != null)
		{
			cardNo = curCustomer.code;
		}

		SaleGoodsDef saleGoodsDef = null;
		GoodsDef goodsDef = null;
		//SpareInfoDef spareInfoDef = null;
		for (int i = 0; i < calcCount + 1; i++)
		{
			// 查找单品优惠单号
			if (i != calcCount)
			{
				saleGoodsDef = (SaleGoodsDef) saleGoods.get(i);
				goodsDef = (GoodsDef) goodsAssistant.get(i);
				//	spareInfoDef = (SpareInfoDef) goodsSpare.get(i);
				// 只有普通商品才能参与
				if (saleGoodsDef.flag != '2' && saleGoodsDef.flag != '4') continue;
				// 判断是否曾参与规则促销
				if (saleGoodsDef.isSMPopCalced == 'N') continue;
				// 标准百货crm促销，跳过
				//				if (goodsDef.str4.equals("Y")) continue;

				//已经查过的商品无需重复查规则促销
				for (k = 0; k < i; k++)
				{
					if (goodsDef.code.equals(((GoodsDef) goodsAssistant.get(k)).code) && goodsDef.gz.equals(((GoodsDef) goodsAssistant.get(k)).gz)
							&& goodsDef.uid.equals(((GoodsDef) goodsAssistant.get(k)).uid)) break;
				}
				if (k < i) continue;

			}
			// 超找整单超市促销单号
			else
			{
				goodsDef = new GoodsDef();
				goodsDef.code = "ALL";
				goodsDef.gz = ManipulatePrecision.doubleToString(saleHead.ysje);
				goodsDef.catid = "";
				goodsDef.ppcode = "";
				goodsDef.uid = "";
			}

			// 首先查找超市规则促销单号
			ruleDef = new SuperMarketPopRuleDef();
			if (!((Bcrm_DataService) DataService.getDefault()).findSuperMarketPopBillNo(ruleDef, goodsDef.code, goodsDef.gz, goodsDef.catid,
																						goodsDef.ppcode, goodsDef.uid, saleHead.rqsj, saleHead.rqsj,
																						cardNo))
			{
				continue;
			}

			System.out.println("商品：" + goodsDef.code + " 对应规则单号：" + ruleDef.djbh);

			//检查该单据是否已经运算过，如果已经运行过则无需重复运算
			for (k = 0; k < notRuleDjbh.size(); k++)
			{
				if (((String) notRuleDjbh.get(k)).equals(ruleDef.djbh)) break;
			}
			if (k < notRuleDjbh.size()) continue;

			// 查找超市促销规则明细
			ruleReqList = new Vector();
			rulePopList = new Vector();
			if (!((Bcrm_DataService) DataService.getDefault()).findSuperMarketPopRule(ruleReqList, rulePopList, ruleDef) || ruleReqList.size() == 0
					|| rulePopList.size() == 0)
			{
				continue;
			}

			// 初始化条件参数
			for (k = 0; k < saleGoods.size(); k++)
			{
				SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
				// 标志为A表示在上一轮规则中被条件排除的商品，因此可以参与本轮规则促销
				sg.isPopExc = ' ';
				if (sg.isSMPopCalced == 'A') ((SaleGoodsDef) saleGoods.get(k)).isSMPopCalced = 'Y';
			}

			for (j = 0; j < ruleReqList.size(); j++)
			{
				for (k = 0; k < saleGoods.size(); k++)
				{
					SaleGoodsDef sg=((SaleGoodsDef) saleGoods.get(k));					
					if(sg.ruledjbh!=null && sg.ruledjbh.length()>0 && sg.hjje==sg.hjzk) continue;//已经满赠赠品商品，则不再参与
					//商品是否条件匹配
					if (isMatchCommod((SuperMarketPopRuleDef) ruleReqList.get(j), k))
					{
						((SaleGoodsDef) saleGoods.get(k)).isPopExc = 'Y';//表示条件满足
					}
				}
			}

			//先将规则条件中要排除的商品排除掉
			for (j = 0; j < ruleReqList.size(); j++)
			{
				//presentsl为1表示该条件是排除的。
				if (((SuperMarketPopRuleDef) ruleReqList.get(j)).presentsl == 1)
				{
					for (k = 0; k < saleGoods.size(); k++)
					{
						SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
						//商品是否条件匹配
						if (sg.isPopExc == 'Y' && isMatchCommod((SuperMarketPopRuleDef) ruleReqList.get(j), k))
						{
							//将排除商品的标志置为N,表示不参与规则促销
							if (((SuperMarketPopRuleDef) ruleReqList.get(j)).istjn.charAt(0) == '0') //排除条件
							sg.isPopExc = ' ';
							else if (((SuperMarketPopRuleDef) ruleReqList.get(j)).istjn.charAt(0) == '1') //排除结果
							sg.isPopExc = 'N';
							else
							//条件和结果都排除
							{
								sg.isPopExc = ' ';
								sg.isSMPopCalced = 'A';
							}
						}
					}
					//如果是排除条件。那么无论结果是排除条件还是排除结果都不纳入条件计算 
					//例如,单据是一行类别的条件和一行排除结果的条件，如果此处不删除排除结果的那一行数据，
					//并且输入的商品中没有买这个排除结果的商品，会导致后面计算AND条件的时候算出倍数为0的情况。
					//如果是排除条件，则需将条件从条件列表中删除，这样是为了后面算多级
					ruleReqList.remove(j);
					j--;
				}
			}

			minbs = 0;
			zje = 0;

			for (n = 0; n < ((SuperMarketPopRuleDef) ruleReqList.get(0)).jc; n++)
			{
				t_minbs = 0;
				t_zje = 0;

				//得到当前级次
				getCurRuleJc(n + 1);

				//匹配规则条件中属于必须满足的条件
				for (l = 0, j = 0; j < ruleReqList.size(); j++)
				{
					SuperMarketPopRuleDef ruleReq = (SuperMarketPopRuleDef) ruleReqList.get(j);
					//条件为AND
					if (ruleReq.presentjs == 1)
					{
						l++;
						je = 0;
						for (k = 0; k < saleGoods.size(); k++)
						{
							SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
							//商品是否条件匹配
							if ((sg.isPopExc == 'Y' || sg.isPopExc == 'N') && isMatchCommod((SuperMarketPopRuleDef) ruleReqList.get(j), k))
							{
								//yhhyj = 0，表示yhlsj中记录的是数量
								//yhhyj = 1，表示yhlsj中记录的是金额
								if (ruleReq.yhhyj == 0) je += sg.sl;
								else je += sg.hjje - sg.hjzk;// sg.yhzke - sg.hyzke - sg.plzke; WANGYONG BY 2014.6.6 FOR 去除所有折扣
								//								此处如果该变cominfo[k].infostr1[5] = 'A',在计算第2级别的时候,程序不能进入上面的IF条件进行统计，导致无法计算一级以上的级别
								//								避免后面的or判断时又找到该条件
							}
						}

						bs = 0;
						if (ManipulatePrecision.doubleCompare(je, ruleReq.yhlsj, 2) >= 0
								&& ManipulatePrecision.doubleCompare(ruleReq.yhlsj, 0, 2) >= 0)
						{
							bs = new Double(je / ruleReq.yhlsj).longValue();
						}
						if (l == 1) t_minbs = bs;
						else t_minbs = t_minbs > bs ? bs : t_minbs;

						t_zje += je;
					}
				}
				//有必须全满足的条件，并且未全满足时，则认为条件不满足
				if (l > 0 && t_minbs <= 0)
				{
					//还原上一级
					if (n > 0) getCurRuleJc(n);
					break;
				}
				
				if(l>0 && t_minbs>0)
				{//当找到全部满足时，则不再找部分满足
					minbs = t_minbs;
					zje = t_zje;
					continue;
				}
				//匹配规则条件中属于非必须满足的条件				
				t_minbs=0;
				je=0;//wangyong add by 2014.6.20
				m=-1;//wangyong add by 2014.6.20
				for (je = 0, m = -1, j = 0; j < ruleReqList.size(); j++)
				{
					SuperMarketPopRuleDef ruleReq = (SuperMarketPopRuleDef) ruleReqList.get(j);
					//条件为OR
					if (ruleReq.presentjs == 0)
					{
						//m = j;//wangyong delete by 2014.6.20
						for (k = 0; k < saleGoods.size(); k++)
						{
							SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
							//商品是否条件匹配
							if ((sg.isPopExc == 'Y' || sg.isPopExc == 'N') && isMatchCommod(ruleReq, k))
							{
								m = j;//wangyong add by 2014.6.20
								//yhhyj = 0，表示yhlsj中记录的是数量
								//yhhyj = 1，表示yhlsj中记录的是金额
								if (ManipulatePrecision.doubleCompare(ruleReq.yhhyj, 0, 2) == 0) je += sg.sl;
								else je += sg.hjje - sg.hjzk;// - sg.yhzke - sg.hyzke - sg.plzke;WANGYONG BY 2014.6.6 FOR 去除所有折扣
							}
						}

						/*//计算or条件的倍数
						if (m >= 0 && ManipulatePrecision.doubleCompare(je, ruleReq.yhlsj, 2) >= 0
								&& ManipulatePrecision.doubleCompare(ruleReq.yhlsj, 0, 2) > 0)
						{
							bs = new Double(je / ruleReq.yhlsj).longValue();
							if (l > 0) t_minbs = t_minbs > bs ? bs : t_minbs; 
							else t_minbs = bs;
							t_minbs = t_minbs+bs;//wangyong update 2014.7.2 by zhangli 含有多个部分满足时，视为翻倍
						}
						
						t_zje += je;*/
					}
				}
				t_zje += je;

				//计算or条件的倍数

				if (m >= 0)
				{
					SuperMarketPopRuleDef ruleReqM = (SuperMarketPopRuleDef) ruleReqList.get(0);//m
					if (ManipulatePrecision.doubleCompare(je, ruleReqM.yhlsj, 2) >= 0 && ManipulatePrecision.doubleCompare(ruleReqM.yhlsj, 0, 2) > 0)
					;
					{
						bs = new Double(je / ruleReqM.yhlsj).longValue();
						if (l > 0) t_minbs = t_minbs > bs ? bs : t_minbs;
						else t_minbs = bs;
					}
				}
				if (t_minbs > 0)
				{
					minbs = t_minbs;
					zje = t_zje;
				}
				else
				{
					//还原上一级
					if (n > 0) getCurRuleJc(n);
					break;
				}
			}

			//有必须全满足的条件，并且未全满足时，则认为条件不满足
			if (minbs <= 0)
			{
				//记录下不匹配的单据号，以便后面的商品再找到该单据时不用再次进行匹配运算
				notRuleDjbh.add(ruleDef.djbh);
				continue;
			}
			else
			{
			}
			//ppistr6中的第1个字符为1时，表示1倍封顶
			if (((SuperMarketPopRuleDef) ruleReqList.get(0)).ppistr5.charAt(0) == '1') minbs = 1;

			//计算促销的结果
			for (j = 0; j < rulePopList.size(); j++)
			{
				SuperMarketPopRuleDef rulePop = (SuperMarketPopRuleDef) rulePopList.get(j);
				//商品优惠 商品优惠对应 一级商品 也对应多级的商品
				if (rulePop.yhdjlb == 'A' || rulePop.yhdjlb == 'E')
				{
					double yhsl = minbs * rulePop.yhhyj;//优惠数量

					//结果为OR关系的时候 按照第一个结果的优惠数量来优惠剩余商品
					if (rulePop.presentjs == 0)
					{
						if (j == 0) or_yhsl = yhsl;
						else yhsl = or_yhsl;
					}

					for (k = 0; k < saleGoods.size() && ManipulatePrecision.doubleCompare(yhsl, 0, 4) > 0; k++)
					{
						SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
						//商品是否结果匹配 排除结果
						if (isMatchCommod(rulePop, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
						{
							//商品拆分
							if (ManipulatePrecision.doubleCompare(sg.sl, yhsl, 4) <= 0)
							{
								yhsl -= sg.sl;
								or_yhsl -= sg.sl;
							}
							else
							{
								//拆分商品行
								splitSalecommod(k, yhsl);
								yhsl = 0;
								or_yhsl = 0;
							}
							double misszke = 0;
							//整单满减
							if (ruleDef.type == '8')
							{
								misszke = sg.yhzke + sg.hyzke + sg.plzke + sg.rulezke;//记录可能被清零的折扣额
							}
							else
							{
								misszke = sg.yhzke + sg.hyzke + sg.plzke;//记录可能被清零的折扣额
							}
							//取价方式判断
							if (rulePop.presentjg == 0)//取价方式 取价格
							{
								//如果是折上折，那么折后金额 = 一般优惠后的金额 * 现在的规则定价 /商品本身的价格
								if (rulePop.iszsz.charAt(0) == '1')
								{
									if (ruleDef.type == '8')
									{
										sg.mjzke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg), 2, 0)
												- ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);
										sg.mjzke = calcComZkxe(k, sg.mjzke);

										sg.mjdjbh = rulePop.djbh;
										sg.mjzkfd = rulePop.zkfd;
										//统计当前规则促销的折扣金额
										superMarketRuleyhje += sg.mjzke;
									}
									else
									{
										sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg), 2, 0)
												- ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);
										sg.rulezke = calcComZkxe(k, sg.rulezke);

										sg.ruledjbh = rulePop.djbh;
										sg.rulezkfd = rulePop.zkfd;
										//统计当前规则促销的折扣金额
										superMarketRuleyhje += sg.rulezke;
									}
								}
								else
								{
									if (ruleDef.type == '8')
									{
										sg.mjzke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + sg.yhzke + sg.hyzke + sg.plzke
												+ sg.rulezke, 2, 0)
												- ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);
										if (sg.mjzke > misszke)//如果规则优惠大于一般优惠，一般优惠清零
										{
											sg.yhzke = 0;
											sg.hyzke = 0;
											sg.plzke = 0;
											sg.spzkfd = 0;
											sg.yhzkfd = 0;
											sg.rulezke = 0;
											sg.rulezkfd = 0;
											sg.yhdjbh = "";
											sg.ruledjbh = "";
											sg.mjdjbh = rulePop.djbh;
											sg.mjzke = calcComZkxe(k, sg.mjzke);
											sg.mjzkfd = rulePop.zkfd;
											//统计当前规则促销的折扣金额
											superMarketRuleyhje += sg.mjzke - misszke;
										}
										else
										{
											sg.mjzke = 0;
										}
									}
									else
									{
										sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + sg.yhzke + sg.plzke + sg.hyzke, 2, 0)
												- ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);

										if (sg.rulezke > misszke)//如果规则优惠大于一般优惠，一般优惠清零
										{
											sg.yhzke = 0;
											sg.hyzke = 0;
											sg.plzke = 0;
											sg.spzkfd = 0;
											sg.yhzkfd = 0;
											sg.yhdjbh = "";
											sg.ruledjbh = rulePop.djbh;
											sg.rulezke = calcComZkxe(k, sg.rulezke);
											sg.rulezkfd = rulePop.zkfd;
											//统计当前规则促销的折扣金额
											superMarketRuleyhje += sg.rulezke - misszke;
										}
										else
										{
											sg.rulezke = 0;
										}
									}
								}
							}
							else if (rulePop.presentjg == 1)//取折扣率
							{
								if (rulePop.iszsz.charAt(0) == '1')
								{
									if (ruleDef.type == '8')
									{
										sg.mjzke = 0;
										sg.mjzke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg), 2, 0)
												- ManipulatePrecision.doubleConvert((sg.hjje - getZZK(sg)) * rulePop.yhlsj, 2, 0);
										sg.mjzke = calcComZkxe(k, sg.mjzke);

										sg.mjdjbh = rulePop.djbh;
										sg.mjzkfd = rulePop.zkfd;
										//统计当前规则促销的折扣金额
										superMarketRuleyhje += sg.mjzke;
									}
									else
									{
										sg.rulezke = 0;
										sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg), 2, 0)
												- ManipulatePrecision.doubleConvert((sg.hjje - getZZK(sg)) * rulePop.yhlsj, 2, 0);
										sg.rulezke = calcComZkxe(k, sg.rulezke);
										sg.ruledjbh = rulePop.djbh;
										sg.rulezkfd = rulePop.zkfd;
										//统计当前规则促销的折扣金额
										superMarketRuleyhje += sg.rulezke;
									}
								}
								else
								{
									if (ruleDef.type == '8')
									{
										sg.mjzke = 0;
										sg.mjzke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + misszke, 2, 0)
												- ManipulatePrecision.doubleConvert((sg.hjje - getZZK(sg) + misszke) * rulePop.yhlsj, 2, 0);

										if (sg.mjzke > misszke)//如果规则优惠大于一般优惠，规则优惠额记录规则优惠额跟一般优惠额的差额
										{
											sg.yhzke = 0;
											sg.hyzke = 0;
											sg.plzke = 0;
											sg.spzkfd = 0;
											sg.yhzkfd = 0;//清除普通优惠和会员优惠
											sg.rulezke = 0;
											sg.rulezkfd = 0;
											sg.yhdjbh = "";
											sg.ruledjbh = "";
											sg.mjzke = calcComZkxe(k, sg.mjzke);
											sg.mjdjbh = rulePop.djbh;
											sg.mjzkfd = rulePop.zkfd;
											//统计当前规则促销的折扣金额
											superMarketRuleyhje += sg.mjzke - misszke;
										}
										else
										{
											sg.mjzke = 0;
										}
									}
									else
									{
										sg.rulezke = 0;
										sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + misszke, 2, 0)
												- ManipulatePrecision.doubleConvert((sg.hjje - getZZK(sg) + misszke) * rulePop.yhlsj, 2, 0);

										if (sg.rulezke > misszke)//如果规则优惠大于一般优惠，规则优惠额记录规则优惠额跟一般优惠额的额
										{
											sg.yhzke = 0;
											sg.hyzke = 0;
											sg.plzke = 0;
											sg.spzkfd = 0;
											sg.yhzkfd = 0;//清除普通优惠和会员优惠
											sg.yhdjbh = "";
											sg.rulezke = calcComZkxe(k, sg.rulezke);
											sg.ruledjbh = rulePop.djbh;
											sg.rulezkfd = rulePop.zkfd;
											//统计当前规则促销的折扣金额
											superMarketRuleyhje += sg.rulezke - misszke;
										}
										else
										{
											sg.rulezke = 0;
										}
									}
								}
							}
							else if (rulePop.presentjg == 2)//取折扣额
							{
								if (rulePop.iszsz.charAt(0) == '1')
								{
									if (ruleDef.type == '8')
									{
										sg.mjzke = rulePop.yhlsj;
										sg.mjzke = calcComZkxe(k, sg.mjzke);

										sg.mjdjbh = rulePop.djbh;
										sg.mjzkfd = rulePop.zkfd;
										superMarketRuleyhje += sg.mjzke;//统计规则促销的折扣金额
									}
									else
									{
										sg.rulezke = rulePop.yhlsj;
										sg.rulezke = calcComZkxe(k, sg.rulezke);

										sg.ruledjbh = rulePop.djbh;
										sg.rulezkfd = rulePop.zkfd;
										superMarketRuleyhje += sg.rulezke;//统计规则促销的折扣金额
									}
								}
								else
								{
									if (ruleDef.type == '8')
									{
										sg.mjzke = rulePop.yhlsj;

										if (sg.mjzke > misszke)//如果规则优惠大于一般优惠，规则优惠额记录规则优惠额跟一般优惠额的差额
										{
											sg.yhzke = 0;
											sg.hyzke = 0;
											sg.plzke = 0;
											sg.spzkfd = 0;
											sg.yhzkfd = 0;//清除普通优惠和会员优惠
											sg.rulezke = 0;
											sg.rulezkfd = 0;

											sg.ruledjbh = "";
											sg.yhdjbh = "";
											sg.mjzke = calcComZkxe(k, sg.mjzke);
											sg.mjdjbh = rulePop.djbh;
											sg.mjzkfd = rulePop.zkfd;
											superMarketRuleyhje += sg.mjzke - misszke;//统计规则促销的折扣金额	
										}
										else
										{
											sg.mjzke = 0;
										}
									}
									else
									{
										sg.rulezke = rulePop.yhlsj;

										if (sg.rulezke > misszke)//如果规则优惠大于一般优惠，规则优惠额记录规则优惠额跟一般优惠额的差额
										{
											sg.yhzke = 0;
											sg.hyzke = 0;
											sg.plzke = 0;
											sg.spzkfd = 0;
											sg.yhzkfd = 0;//清除普通优惠和会员优惠
											sg.yhdjbh = "";
											sg.rulezke = calcComZkxe(k, sg.rulezke);
											sg.ruledjbh = rulePop.djbh;
											sg.rulezkfd = rulePop.zkfd;
											superMarketRuleyhje += sg.rulezke - misszke;//统计规则促销的折扣金额	
										}
										else
										{
											sg.rulezke = 0;
										}
									}
								}
							}
							else
							//用于其它用途
							{
							}
							sg.isPopExc = 'Y';
						}
					}
				}
				//赠品
				if (rulePop.yhdjlb == 'B' || rulePop.yhdjlb == 'F')
				{
					//中免赠品版本：
					//要先弹出提示框，由款员选择是否赠送
					//'4'表示买赠，该赠品需要自动添加到商品到界面//是小票列表中的正常商品，要将其改成正赠品
					//'5'表示礼品，只是提示并打印在小票上（ruleDef.code为赠品名称）
					if (rulePop.ppistr6.charAt(0) == '4' && rulePop.type=='1')//单品优惠
					{
						SaleGoodsDef zpSaleGoods;
						GoodsDef zpGoods;
						SpareInfoDef zpSpareInfo;
						PosLog.getLog(this.getClass()).info("满赠赠品：code=[" + rulePop.code + "],gz=[" + rulePop.gz + "].");
						zpGoods = findPackGoodsInfo(rulePop.code, rulePop.gz);
						quantity = minbs * rulePop.yhlsj;//赠品数量
						if(zpGoods==null)
						{
							new MessageBox(Language.apply("本单有赠品:") + "\n" + rulePop.code + "\n\n" + Language.apply("但系统查找失败，所以无法赠送"));
						}
						else if(quantity<=0)
						{
							//new MessageBox("本单有赠品:\n" + rulePop.code + "【" + zpGoods.name + "】\n\n但系统设置赠送数量为[" + quantity + "]，所以无法赠送");
						}
						else
						{
							String zpmsg = Language.apply("本单有赠品：") + "\n" + zpGoods.code + "【" + zpGoods.name + "】, " + Language.apply("数量:") + quantity;
							
							// 赠品不允许销红,检查库存 by 2015.1.19 for yanj+gejx
							if ((SellType.ISSALE(saletype) && GlobalInfo.sysPara.isxh != 'Y' && zpGoods.isxh != 'Y'))
							{
								// 统计商品销售数量
								double hjsl = /*ManipulatePrecision.mul(quantity, zpGoods.bzhl)*/ quantity+ calcSameGoodsQuantity(zpGoods);
								if (zpGoods.kcsl < hjsl)
								{
									if (GlobalInfo.sysPara.xhisshowsl == 'Y')
										new MessageBox(zpmsg + "\n但该赠品库存为" + ManipulatePrecision.doubleToString(zpGoods.kcsl) + "\n库存不足,无法赠送");
									else
										new MessageBox(zpmsg + "\n但该赠品库存不足,无法赠送");

									return false;
								}
							}
							
							if(new MessageBox(zpmsg + "\n\n" + Language.apply("是否赠送?"), null, true).verify() == GlobalVar.Key1) 
							{								
								zpSaleGoods = goodsDef2SaleGoods(zpGoods, saleEvent.yyyh.getText(), quantity, 0, 0, false);
								zpSpareInfo = getGoodsSpareInfo(zpGoods, zpSaleGoods);
								
								//将该商品改为赠品
								zpSaleGoods.flag = '1';
								zpSaleGoods.batch = rulePop.ppistr6;

								zpSaleGoods.xxtax = quantity;//ManipulatePrecision.doubleConvert(minbs * rulePop.yhhyj, 2, 0);
								zpSaleGoods.rulezke = zpSaleGoods.lsj * quantity;
								zpSaleGoods.jg = zpSaleGoods.lsj;
								zpSaleGoods.sl = quantity;
								zpSaleGoods.hjje = zpSaleGoods.lsj * zpSaleGoods.sl;

								zpSaleGoods.ruledjbh = rulePop.djbh; //记录优惠单据编号
								zpSaleGoods.rulezkfd = rulePop.zkfd;
								zpSaleGoods.name = Language.apply("(赠品)") + zpSaleGoods.name;
								//该商品的应收金额都记为优惠金额
								superMarketRuleyhje += zpSaleGoods.hjje;
								
								addPackSaleGoodsObject(zpSaleGoods, zpGoods, zpSpareInfo);
							}
							
						}
						
					}
					else if (rulePop.ppistr6.charAt(0) == '5' && rulePop.type=='1')
					{
						quantity = minbs * rulePop.yhlsj;//赠品数量
						if(quantity<=0)
						{
							//new MessageBox("本单可赠送礼品:\n" + "【" + rulePop.code + "】\n\n但系统设置赠送数量为[" + quantity + "]，所以无法赠送");
						}
						else
						{
							String zpmsg = Language.apply("本单可赠送礼品:") + "【" + rulePop.code + "】, " + Language.apply("数量:") + quantity;
							
							if(new MessageBox(zpmsg + "\n\n" + Language.apply("是否赠送?"), null, true).verify() == GlobalVar.Key1) 
							{
								//将此字段记录到小票头，用于打印
								if(saleHead.str10==null) saleHead.str10="";
								if(saleHead.str10.length()>1) saleHead.str10 = saleHead.str10 + "\n";
								saleHead.str10 += zpmsg;
							}
							
						}
						
					}
					
					
					/*if (rulePop.ppistr6.charAt(0) == '4')
					{
						//赠品数量
						quantity = minbs * rulePop.yhlsj;

						for (k = 0; k < saleGoods.size() && ManipulatePrecision.doubleCompare(quantity, 0, 4) > 0; k++)
						{
							SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
							//商品是否结果匹配  排除结果
							if (isMatchCommod(rulePop, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
							{
								//商品拆分
								if (ManipulatePrecision.doubleCompare(sg.sl, quantity, 4) <= 0)
								{
									quantity -= sg.sl;
								}
								else
								{
									//拆分商品行
									splitSalecommod(k, quantity);
									quantity = 0;
								}
								//将该商品改为赠品
								sg.flag = '1';
								sg.batch = rulePop.ppistr6;

								sg.xxtax = ManipulatePrecision.doubleConvert(minbs * rulePop.yhhyj, 2, 0);
								sg.rulezke += sg.hjje - getZZK(sg) - ManipulatePrecision.doubleConvert(minbs * rulePop.yhhyj, 2, 0);

								sg.ruledjbh = rulePop.djbh; //记录优惠单据编号
								sg.rulezkfd = rulePop.zkfd;

								//该商品的应收金额都记为优惠金额
								superMarketRuleyhje += sg.zszke;
							}
						}
					}*/
				}
				//任意几个定应收金额 只分多级
				if (rulePop.yhdjlb == 'X')
				{
					if (ManipulatePrecision.doubleCompare(zje, ((SuperMarketPopRuleDef) ruleReqList.get(0)).yhlsj, 2) >= 0
							&& ManipulatePrecision.doubleCompare(((SuperMarketPopRuleDef) ruleReqList.get(0)).yhlsj, 0, 2) > 0)
					{
						double yhje = ManipulatePrecision.doubleConvert(minbs * rulePop.yhlsj, 2, 0);
						long yhsl = new Double(minbs * ((SuperMarketPopRuleDef) ruleReqList.get(0)).yhlsj).longValue();
						double t_zyhje = 0;

						t_zje = 0;
						t_zyhje = 0;

						//yhje应小于小票的应收金额，不然话，小票金额有成负数的可能。
						if (ManipulatePrecision.doubleCompare(yhje, saleHead.ysje, 2) < 0)
						{
							int t_maxjerow = -1;
							long t_yhsl = yhsl;
							//计算本次参与优惠商品的总金额
							for (l = 0; l < ruleReqList.size(); l++)
							{
								SuperMarketPopRuleDef ruleReq = (SuperMarketPopRuleDef) ruleReqList.get(l);
								for (k = 0; k < saleGoods.size(); k++)
								{
									SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
									if (t_yhsl > 0 && isMatchCommod(ruleReq, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
									{
										//商品拆分
										if (ManipulatePrecision.doubleCompare(sg.sl, t_yhsl, 4) <= 0)
										{
											t_yhsl -= (long) sg.sl;
										}
										else
										{
											//拆分商品行
											splitSalecommod(k, t_yhsl);
											t_yhsl = 0;
										}
										//如果是折上折
										if (rulePop.iszsz.charAt(0) == '1')
										{
											t_zje += sg.hjje - getZZK(sg);
										}
										else
										{
											if (ruleDef.type == '8')
											{
												t_zje += sg.hjje - getZZK(sg) + sg.yhzke + sg.hyzke + sg.plzke + sg.rulezke;
												t_zyhje += sg.yhzke + sg.hyzke + sg.plzke + sg.rulezke;
											}
											else
											{
												t_zje += sg.hjje - getZZK(sg) + sg.yhzke + sg.plzke + sg.hyzke;
												t_zyhje += sg.yhzke + sg.hyzke + sg.plzke;
											}
										}
									}
								}
							}

							//计算出优惠金额
							yhje = t_zje - yhje;

							double t_je = 0;
							//将优惠金额按金额占比分摊到本次参与的商品上面
							for (l = 0; l < ruleReqList.size(); l++)
							{
								SuperMarketPopRuleDef ruleReq = (SuperMarketPopRuleDef) ruleReqList.get(l);
								for (k = 0; k < saleGoods.size(); k++)
								{
									SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
									if (yhsl > 0 && isMatchCommod(ruleReq, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
									{
										//商品拆分
										if (ManipulatePrecision.doubleCompare(sg.sl, yhsl, 4) <= 0)
										{
											yhsl -= sg.sl;
										}
										else
										{
											//拆分商品行
											splitSalecommod(k, yhsl);
											yhsl = 0;
										}
										double misszke = 0;
										if (ruleDef.type == '8')
										{
											misszke = sg.yhzke + sg.yhzke + sg.plzke + sg.rulezke;
										}
										else
										{
											misszke = sg.yhzke + sg.yhzke + sg.plzke;
										}
										//根据折上折来判断，如果非折上折，取低价优先
										if (rulePop.iszsz.charAt(0) == '1')
										{
											if (ruleDef.type == '8')
											{
												sg.mjzke = 0;
												sg.mjzke = (sg.hjje - getZZK(sg)) / t_zje * yhje;
												calcComZkxe(k, sg.mjzke);
												//记录折扣分担
												sg.mjzkfd = rulePop.zkfd;
												//记录优惠单据编号
												sg.mjdjbh = rulePop.djbh;
												//统计当前规则促销的折扣金额
												superMarketRuleyhje += sg.mjzke;

											}
											else
											{
												sg.rulezke = 0;
												sg.rulezke = (sg.hjje - getZZK(sg)) / t_zje * yhje;
												calcComZkxe(k, sg.rulezke);
												//记录折扣分担
												sg.rulezkfd = rulePop.zkfd;
												//记录优惠单据编号
												sg.ruledjbh = rulePop.djbh;
												//统计当前规则促销的折扣金额
												superMarketRuleyhje += sg.rulezke;
											}
										}
										else
										{
											if (yhje > t_zyhje)
											{
												if (ruleDef.type == '8')
												{
													sg.mjzke = 0;
													sg.mjzke = (sg.hjje - getZZK(sg) + misszke) / t_zje * yhje;

													sg.yhzke = 0;
													sg.hyzke = 0;
													sg.plzke = 0;
													sg.spzkfd = 0;
													sg.yhzkfd = 0;
													sg.rulezke = 0;
													sg.rulezkfd = 0;
													sg.yhdjbh = "";
													sg.ruledjbh = "";
													calcComZkxe(k, sg.mjzke);
													//记录折扣分担
													sg.mjzkfd = rulePop.zkfd;
													//记录优惠单据编号
													sg.ruledjbh = rulePop.djbh;
													//统计当前规则促销的折扣金额
													superMarketRuleyhje += sg.mjzke - misszke;

												}
												else
												{
													sg.rulezke = 0;
													sg.rulezke = (sg.hjje - getZZK(sg) + misszke) / t_zje * yhje;
													sg.yhzke = 0;
													sg.hyzke = 0;
													sg.plzke = 0;
													sg.spzkfd = 0;
													sg.yhzkfd = 0;
													sg.yhdjbh = "";
													calcComZkxe(k, sg.rulezke);
													//记录折扣分担
													sg.rulezkfd = rulePop.zkfd;
													//记录优惠单据编号
													sg.ruledjbh = rulePop.djbh;
													//统计当前规则促销的折扣金额
													superMarketRuleyhje += sg.rulezke - misszke;
												}
											}
											else
											{
												t_je = yhje;
											}
										}
										// 将参与优惠的商品打上标记
										sg.isPopExc = 'Y';
										if (ruleDef.type == '8')
										{
											t_je += sg.mjzke;
										}
										else
										{
											t_je += sg.rulezke;
										}

										//记下金额最大的行号
										if (t_maxjerow >= 0
												&& ManipulatePrecision.doubleCompare(sg.hjje, ((SaleGoodsDef) saleGoods.get(t_maxjerow)).hjje, 2) > 0
												|| t_maxjerow < 0) t_maxjerow = k;
									}
								}
								if (yhsl <= 0) break;
							}
							if (ManipulatePrecision.doubleCompare(Math.abs(yhje - t_je), 0, 2) > 0)
							{
								if (ruleDef.type == '8')
								{
									((SaleGoodsDef) saleGoods.get(t_maxjerow)).mjzke += yhje - t_je;
									calcComZkxe(t_maxjerow, ((SaleGoodsDef) saleGoods.get(t_maxjerow)).mjzke);
								}
								else
								{
									((SaleGoodsDef) saleGoods.get(t_maxjerow)).rulezke += yhje - t_je;
									calcComZkxe(t_maxjerow, ((SaleGoodsDef) saleGoods.get(t_maxjerow)).rulezke);
								}
								superMarketRuleyhje += yhje - t_je;
							}
						}
					}
					//任意多级的，只判断一个结果
					break;
				}
				//数量促销 数量促销没有折上折(取的单价)，没有分级，取单价
				if (rulePop.yhdjlb == 'N')
				{
					//flag = 1 是全量优惠
					//flag = 2 是超量促销
					//flag = 3 是第n件促销
					//flag = 4 是整箱促销
					long flag = new Double(rulePop.yhhyj).longValue();
					//统计本单参与优惠的商品数量
					double kyhsl = 0;
					for (k = 0; k < calcCount; k++)
					{
						SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
						//条件排除的商品应该参与结果计算
						if (/*cominfo[k].infostr1[5] != ' ' && */isMatchCommod(rulePop, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
						{
							kyhsl += sg.sl;
							//如果前一个商品已经满足了折扣,
							//那么第二个商品必须标示为已经参与促销（此处为标示其已经促销的条件）。
						}
					}

					//优惠数量
					double yhsl = Double.parseDouble(rulePop.ppistr3);
					if (ManipulatePrecision.doubleCompare(yhsl, 0, 2) <= 0) yhsl = 1;//防止后面计算时除0错误

					//超量促销
					if (flag == 2) kyhsl -= yhsl;
					//第n件促销
					if (flag == 3) kyhsl = new Double(kyhsl / yhsl).longValue();
					//整箱促销
					if (flag == 4)
					{
						minbs = new Double(kyhsl / yhsl).longValue();
						long zyhsl = new Double(minbs * yhsl).longValue();//整箱总的优惠数量
						kyhsl = kyhsl > zyhsl ? zyhsl : kyhsl;
					}

					//开始计算优惠
					if (ManipulatePrecision.doubleCompare(kyhsl, 0, 2) > 0)
					{
						for (k = 0; k < calcCount; k++)
						{
							SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
							if (kyhsl > 0 && isMatchCommod(rulePop, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
							{
								//优惠价低于零售价
								if (sg.lsj > rulePop.yhlsj && rulePop.yhlsj > 0)
								{
									if (kyhsl >= sg.sl)
									{
										kyhsl -= sg.sl;
									}
									else
									{
										//拆分商品行
										splitSalecommod(k, kyhsl);
										kyhsl = -1;
									}
									sg.rulezke = 0;
									double misszke = sg.yhzke + sg.hyzke + sg.plzke;
									sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + misszke, 2, 0)
											- ManipulatePrecision.doubleConvert(rulePop.yhlsj * sg.sl, 2, 0);

									//优惠价低于一般促销
									if (ManipulatePrecision.doubleCompare(sg.rulezke, misszke, 2) > 0)
									{
										sg.yhzke = 0;
										sg.hyzke = 0;
										sg.plzke = 0;
										sg.spzkfd = 0;
										sg.yhzkfd = 0;//清除普通优惠和会员优惠
										sg.yhdjbh = "";
										sg.rulezkfd = rulePop.zkfd;
										calcComZkxe(k, sg.rulezke);
										//记录优惠单据编号
										sg.ruledjbh = rulePop.djbh;
										superMarketRuleyhje += sg.rulezke - misszke;

										//参与满减促销的标志
										sg.isPopExc = 'Y';
									}
									else sg.rulezke = 0;
								}
							}
						}
					}
				}
				//任意几个定单价 只分多级
				if (rulePop.yhdjlb == 'Z')
				{
					if (ManipulatePrecision.doubleCompare(zje, ((SuperMarketPopRuleDef) ruleReqList.get(0)).yhlsj, 2) >= 0
							&& ManipulatePrecision.doubleCompare(((SuperMarketPopRuleDef) ruleReqList.get(0)).yhlsj, 0, 2) > 0)
					{
						//参与优惠的商品明细数量
						long yhsl = new Double(minbs * ((SuperMarketPopRuleDef) ruleReqList.get(0)).yhlsj).longValue();

						for (l = 0; l < ruleReqList.size(); l++)
						{
							SuperMarketPopRuleDef ruleReq = (SuperMarketPopRuleDef) ruleReqList.get(l);
							for (k = 0; k < saleGoods.size(); k++)
							{
								SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
								if (yhsl > 0 && isMatchCommod(ruleReq, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
								{
									//商品拆分
									if (ManipulatePrecision.doubleCompare(sg.sl, yhsl, 4) <= 0)
									{
										yhsl -= sg.sl;
									}
									else
									{
										//拆分商品行
										splitSalecommod(k, yhsl);

										yhsl = 0;
									}
									double misszke = 0;
									if (i == calcCount + 1) misszke = sg.yhzke + sg.hyzke + sg.plzke + sg.rulezke;//记录可能被清零的折扣额
									else misszke = sg.yhzke + sg.hyzke + sg.plzke;//记录可能被清零的折扣额

									if (rulePop.iszsz.charAt(0) == '1')
									{
										if (i == calcCount + 1)
										{
											sg.mjzke = 0;
											sg.mjzke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg), 2, 0)
													- ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);
											calcComZkxe(k, sg.mjzke);
											sg.mjdjbh = rulePop.djbh;
											sg.mjzkfd = rulePop.zkfd;
											//统计当前规则促销的折扣金额
											superMarketRuleyhje += sg.mjzke;
										}
										else
										{
											sg.rulezke = 0;
											sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg), 2, 0)
													- ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);
											calcComZkxe(k, sg.rulezke);
											sg.ruledjbh = rulePop.djbh;
											sg.rulezkfd = rulePop.zkfd;
											//统计当前规则促销的折扣金额
											superMarketRuleyhje += sg.rulezke;
										}
									}
									else
									{
										if (i == calcCount + 1)
										{
											sg.mjzke = 0;
											sg.mjzke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + misszke, 2, 0)
													- ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);

											if (sg.mjzke > misszke)//如果规则优惠大于一般优惠，规则优惠额记录规则优惠额跟一般优惠额的差额
											{
												sg.yhzke = 0;
												sg.hyzke = 0;
												sg.plzke = 0;
												sg.spzkfd = 0;
												sg.yhzkfd = 0;
												sg.rulezke = 0;
												sg.rulezkfd = 0;
												sg.ruledjbh = "";
												sg.yhdjbh = "";
												calcComZkxe(k, sg.mjzke);
												sg.mjdjbh = rulePop.djbh;
												sg.mjzkfd = rulePop.zkfd;
												//统计当前规则促销的折扣金额
												superMarketRuleyhje += sg.mjzke - misszke;
											}
											else
											{
												sg.mjzke = 0;
											}
										}
										else
										{
											sg.rulezke = 0;
											sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + misszke, 2, 0)
													- ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);

											if (sg.rulezke > misszke)//如果规则优惠大于一般优惠，规则优惠额记录规则优惠额跟一般优惠额的差额
											{
												sg.yhzke = 0;
												sg.hyzke = 0;
												sg.plzke = 0;
												sg.spzkfd = 0;
												sg.yhzkfd = 0;
												sg.yhdjbh = "";
												sg.ruledjbh = rulePop.djbh;
												calcComZkxe(k, sg.rulezke);
												sg.rulezkfd = rulePop.zkfd;
												//统计当前规则促销的折扣金额
												superMarketRuleyhje += sg.rulezke - misszke;
											}
											else
											{
												sg.rulezke = 0;
											}
										}
									}
									sg.isPopExc = 'Y';
								}
							}
							if (yhsl <= 0) break;
						}
					}
					//任意多级的，只判断一个结果
					break;
				}
				//对指定商品固定优惠金额
				if (rulePop.yhdjlb == 'V')
				{
					double yhsl = minbs * rulePop.yhhyj;//优惠数量
					double yhje = ManipulatePrecision.doubleConvert(minbs * rulePop.yhlsj, 2, 0);

					//结果为OR关系的时候 按照第一个结果的优惠数量来优惠剩余商品
					long and_flag = new Double(rulePop.presentjs).longValue();

					or_yhsl = yhsl;
					zje = 0;
					t_zje = 0;

					while (and_flag == 1 || and_flag == 0 && ManipulatePrecision.doubleCompare(or_yhsl, 0, 4) > 0 && j < rulePopList.size())
					{
						//统计参与优惠金额分配的商品总金额
						for (k = 0; k < saleGoods.size() && ManipulatePrecision.doubleCompare(or_yhsl, 0, 4) > 0; k++)
						{
							SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
							//商品是否结果匹配
							if (isMatchCommod(rulePop, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
							{
								//商品拆分
								if (ManipulatePrecision.doubleCompare(sg.sl, or_yhsl, 4) <= 0)
								{
									or_yhsl -= sg.sl;
								}
								else
								{
									//拆分商品行
									splitSalecommod(k, or_yhsl);
									or_yhsl = 0;
								}
								zje += sg.hjje;
								t_zje += sg.hjje - getZZK(sg);
							}
						}
						if (and_flag == 1) break;
						j++;
					}
					if (ManipulatePrecision.doubleCompare(yhje, t_zje, 2) > 0) yhje = t_zje;

					//将优惠金额分担到商品明细上
					if (ManipulatePrecision.doubleCompare(zje, 0, 2) > 0 && ManipulatePrecision.doubleCompare(yhje, 0, 2) > 0
							&& ManipulatePrecision.doubleCompare(zje - yhje, 0, 2) >= 0)
					{
						int maxrow = -1;
						j = 0;
						t_zje = 0;
						while (and_flag == 1 || and_flag == 0 && ManipulatePrecision.doubleCompare(yhsl, 0, 4) > 0 && j < rulePopList.size())
						{
							for (k = 0; k < saleGoods.size() && ManipulatePrecision.doubleCompare(yhsl, 0, 4) > 0; k++)
							{
								SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
								//商品是否结果匹配
								if (isMatchCommod(rulePop, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
								{
									yhsl -= sg.sl;

									je = ManipulatePrecision.doubleConvert(sg.hjje / zje * yhje, 2, 0);
									if (ruleDef.type == '8')
									{
										sg.mjzke += je;
									}
									else
									{
										sg.rulezke += je;
									}
									//记录折扣分担
									sg.rulezkfd = rulePop.zkfd;
									//记录优惠单据编号
									sg.ruledjbh = rulePop.djbh;
									sg.isPopExc = 'Y';

									superMarketRuleyhje += je;
									t_zje += je;

									//记下金额最大的行号
									if (maxrow >= 0 && ManipulatePrecision.doubleCompare(sg.hjje, ((SaleGoodsDef) saleGoods.get(maxrow)).hjje, 2) > 0
											|| maxrow < 0) maxrow = k;
								}
							}
							if (and_flag == 1) break;
							j++;
						}
						//将未分配完的金额分配到金额最大的商品上
						if (ManipulatePrecision.doubleCompare(Math.abs(yhje - t_zje), 0, 2) > 0)
						{
							k = maxrow;
							SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
							if (ruleDef.type == '8')
							{
								sg.mjzke += yhje - t_zje;
							}
							else
							{
								sg.rulezke += yhje - t_zje;
							}
							superMarketRuleyhje += yhje - t_zje;
						}
					}
					//或条件时，已经优惠完了，所以要退出
					if (and_flag == 0) break;
				}
			}

			//将已参与规则促销的商品打上标志
			for (k = 0; k < saleGoods.size(); k++)
			{
				SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
				//商品是否条件匹配
				if (sg.isPopExc != ' ')
				{
					sg.isSMPopCalced = 'N';
					sg.isPopExc = ' ';
				}
			}
			//类别为8表示整单满减
			if (ruleDef.type == '8') break;
		}

		for (k = 0; k < saleGoods.size(); k++)
		{
			getZZK((SaleGoodsDef) saleGoods.get(k));
		}

		// 重算应收
		calcHeadYsje();

		// 刷新商品列表
		saleEvent.updateTable(getSaleGoodsDisplay());
		saleEvent.setTotalInfo();
		return true;
	}

	protected boolean isMatchCommod(SuperMarketPopRuleDef ruleDef, int index)
	{
		SaleGoodsDef sg = ((SaleGoodsDef) saleGoods.get(index));
		GoodsDef goodsDef = ((GoodsDef) goodsAssistant.get(index));

		// 整单的规则,整单优先级最高 
		if (ruleDef.type == '8') return true;

		//只有正常的商品才参与规则促销
		if (sg.flag != '4' && sg.flag != '2') { return false; }

		//如果电子称商品不是排除条件
		if (ruleDef.presentsl != 1 || sg.flag != '2')
		{
			//如果电子称商品条件不是满减/满返，结果也不是满减/满返 
			if (!(ruleDef.yhdjlb == '8' && (ruleDef.ppistr3.charAt(0) == 'G' || ruleDef.ppistr3.charAt(0) == 'C') || ruleDef.yhdjlb == 'G' || ruleDef.yhdjlb == 'C')
					&& sg.flag == '2') { return false; }
		}

		//条件为整单的时候如果是结果为非整单。此处就不判断商品是否参与了规则促销，
		//不然在结果匹配的时候会因为商品参与了非整单规则促销而无法参与整单的规则促销
		//在整单规则的时候初始化商品标识的时候使用
		if (((SuperMarketPopRuleDef) ruleReqList.get(0)).type != '8')
		{
			//不参与规则促销
			if (sg.isSMPopCalced != 'Y') { return false; }
		}

		switch (ruleDef.type)
		{
			case '1'://单品
				if (!ruleDef.code.equals(goodsDef.code)) break;
				if ((ruleDef.gz.equals(goodsDef.gz) || ruleDef.gz.equals("0")) && (ruleDef.spec.equals(goodsDef.uid) || ruleDef.spec.equals("AL")))
				{
					return true;
				}
				break;
			case '2'://柜组
				if (!ruleDef.code.equals(goodsDef.gz)) break;
				return true;
			case '3'://类别
				int len3 = ruleDef.code.length();
				if(goodsDef.catid.length()>len3) len3 = goodsDef.catid.length();
				if (!ruleDef.code.equals(Convert.increaseChar(goodsDef.catid,len3).substring(0, ruleDef.code.length()))) break;
				//if (!ruleDef.code.equals(Convert.increaseChar(goodsDef.catid,ruleDef.code.length()).substring(0, ruleDef.code.length()))) break; //BAK OLD BY 2014.8.5
				return true;
			case '4'://柜组品牌
				if (!ruleDef.code.equals(goodsDef.gz)) break;
				if (ruleDef.pp.equals(goodsDef.ppcode))
				{
					return true;
				}
				break;
			case '5'://类别品牌
				int len5 = ruleDef.code.length();
				if(goodsDef.catid.length()>len5) len5 = goodsDef.catid.length();
				if (!ruleDef.code.equals(Convert.increaseChar(goodsDef.catid,len5).substring(0, ruleDef.code.length()))) break;
				//if (!ruleDef.code.equals(Convert.increaseChar(goodsDef.catid,ruleDef.code.length()).substring(0, ruleDef.code.length()))) break; //BAK OLD BY 2014.8.5
				if (ruleDef.pp.equals(goodsDef.ppcode))
				{
					return true;
				}
				break;
			case '6'://品牌
				if (!ruleDef.code.equals(goodsDef.ppcode)) break;
				return true;
			case '7'://生鲜单品
				if (!ruleDef.code.equals(goodsDef.code)) break;
				if ((ruleDef.gz.equals(goodsDef.gz) || ruleDef.gz.equals("0")) && (ruleDef.spec.equals(goodsDef.uid) || ruleDef.spec.equals("AL")))
				{
					return true;
				}
				break;
		}
		return false;
	}
	
	protected void getCurRuleJc(int jc)
	{
		int i;
		// 获得条件在jc传入值的级别所对应的级别值
		for (i = 0; i < ruleReqList.size(); i++)
		{
			SuperMarketPopRuleDef reqDef = (SuperMarketPopRuleDef) ruleReqList.get(i);
			double a = Double.parseDouble(reqDef.ppistr1.split("\\|")[jc - 1]);
			if (a >= 0) reqDef.yhlsj = a;//增加为 >= 以前是>
			
		}
		for (i = 0; i < rulePopList.size(); i++)
		{
			SuperMarketPopRuleDef reqPop = (SuperMarketPopRuleDef) rulePopList.get(i);
			double a = Double.parseDouble(reqPop.ppistr1.split("\\|")[jc - 1]);
			if (a >= 0) reqPop.yhlsj = a;//增加为 >= 以前是>
			double b = Double.parseDouble(reqPop.ppistr2.split("\\|")[jc - 1]);
			if (b >= 0) reqPop.yhhyj = b;//增加为 >= 以前是>
		}
	}
	
	public void calcVIPZK(int index)
	{
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);

		// 未刷卡
		if (!checkMemberSale() || curCustomer == null) return;

		// 非零售开票
		if (!saletype.equals(SellType.RETAIL_SALE) && !saletype.equals(SellType.PREPARE_SALE))
		{
			goodsDef.hyj = 1;
			return;
		}

		// 查询商品VIP折上折定义
		GoodsPopDef popDef = new GoodsPopDef();
		popDef.pophyj = 1;//防止无规则时为0，而造成全价优惠
		if (((Bcrm_DataService) DataService.getDefault()).findHYZK(popDef, saleGoodsDef.code, curCustomer.type, saleGoodsDef.gz, saleGoodsDef.catid,
																	saleGoodsDef.ppcode, goodsDef.specinfo))
		{
			// 有柜组和商品的VIP折扣定义
			goodsDef.hyj = popDef.pophyj;
			goodsDef.num4 = popDef.num2;
		}
		else
		{
			// 无柜组和商品的VIP折扣定义,以卡类别的折扣率为VIP打折标准
			goodsDef.hyj = curCustomer.zkl;
			goodsDef.num4 = 1;
		}
	}
	
	public void calcAllRebate(int index)
    {
		//super.calcAllRebate(index);
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);

		// 指定小票退货时不重算优惠价和会员价
		if (isSpecifyBack(saleGoodsDef)) { return; }

		// 批发销售不计算
		if (SellType.ISBATCH(saletype)) { return; }

		if (SellType.ISEARNEST(saletype)) { return; }

		if (SellType.ISCHECKINPUT(saletype)) { return; }

		// 削价商品和赠品不计算,积分换购商品不计算,组包明细商品不计算
		if ((saleGoodsDef.flag == '3') || (saleGoodsDef.flag == '1') || this.isHHGoods(saleGoodsDef) || this.isPackGoods(saleGoodsDef)) { return; }

		saleGoodsDef.hyzke = 0;
		saleGoodsDef.yhzke = 0;
		saleGoodsDef.yhzkfd = 0;
		saleGoodsDef.zszke = 0;

		//待加，普通分期促销限量
		double calcSL = getPopLimitSL(index, saleGoodsDef);
		
		// 促销优惠
		// 换消状态下不计算定期促销
		if (goodsDef.poptype != '0' && hhflag != 'Y')
		{
			//定价且是单品优惠
			if ((saleGoodsDef.lsj > 0) && ((goodsDef.poptype == '1') || (goodsDef.poptype == '7')))
			{
				// 促销折扣
				if ((saleGoodsDef.lsj > goodsDef.poplsj) && (goodsDef.poplsj > 0))
				{
					//saleGoodsDef.yhzke = (saleGoodsDef.lsj - goodsDef.poplsj) * saleGoodsDef.sl;
					saleGoodsDef.yhzke = (saleGoodsDef.lsj - goodsDef.poplsj) * calcSL;
					//saleGoodsDef.yhzkfd = goodsDef.poplsjzkfd;
				}
			}
			else
			{
				//促销折扣
				if ((1 > goodsDef.poplsjzkl) && (goodsDef.poplsjzkl > 0))
				{
					//saleGoodsDef.yhzke = saleGoodsDef.hjje * (1 - goodsDef.poplsjzkl);
					double yhzke = saleGoodsDef.hjje * (1 - goodsDef.poplsjzkl);
					yhzke = (yhzke/saleGoodsDef.sl) * calcSL;
					saleGoodsDef.yhzke = yhzke;
					//saleGoodsDef.yhzkfd = goodsDef.poplsjzkfd;
				}
			}

			// 
			saleGoodsDef.yhzke = ManipulatePrecision.doubleConvert(saleGoodsDef.yhzke, 2, 1);

			// 按价格精度计算折扣
			saleGoodsDef.yhzke = getConvertRebate(index, saleGoodsDef.yhzke);
			if(saleGoodsDef.yhzke>0) saleGoodsDef.yhzkfd = goodsDef.poplsjzkfd;
		}

		// vipzk1表示实时输商品的时候立即计算VIP折扣
		getVIPZK(index, vipzk1);
		

    	// 促销模型处理
    	if (isCmPopMode())
    	{
    		saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
    		
    		// 计算促销模型的分期促销
    		CmPop_calcGoodsPOPRebate(index);
    		
            // 按价格精度计算折扣
            saleGoodsDef.yhzke = ManipulatePrecision.doubleConvert(saleGoodsDef.yhzke, 2, 1);
            saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert(saleGoodsDef.hyzke, 2, 1);
            if (saleGoodsDef.yhzke > 0) saleGoodsDef.yhzke = getConvertRebate(index, saleGoodsDef.yhzke);
            if (saleGoodsDef.hyzke > 0) saleGoodsDef.hyzke = getConvertRebate(index, saleGoodsDef.hyzke);
    	}
		
    }
	
	//计算普通分期促销限量数量
	protected double getPopLimitSL(int index, SaleGoodsDef currSaleGoodsDef)
	{		
		double calcSL = 0;
		try
		{
			//dblMaxYhSl=-9999表示此商品分期促销不限量
			if (GlobalInfo.sysPara.isUsePopLimit != 'Y' || currSaleGoodsDef.dblMaxYhSl == -9999) return currSaleGoodsDef.sl;
			
			SaleGoodsDef saleGoodsTmp;
			double popSL=-1;//第一个录入商品的限量数量
			double useSL=0;//当前商品的总数量(除当前行外）
			for (int k = 0; k < saleGoods.size(); k++)
			{
				saleGoodsTmp=(SaleGoodsDef) saleGoods.get(k);
				if (popSL<0 && saleGoodsTmp.code.equalsIgnoreCase(currSaleGoodsDef.code) && saleGoodsTmp.barcode.equalsIgnoreCase(currSaleGoodsDef.barcode))
				{
					popSL = saleGoodsTmp.dblMaxYhSl;
				}
				if (k!=index && saleGoodsTmp.code.equalsIgnoreCase(currSaleGoodsDef.code) && saleGoodsTmp.barcode.equalsIgnoreCase(currSaleGoodsDef.barcode))
				{
					useSL += saleGoodsTmp.sl;
				}				
			}
			if (popSL<0) popSL=0;
			calcSL = popSL-useSL;
			if (calcSL<=0) return 0;
			if(calcSL>=currSaleGoodsDef.sl)
				return currSaleGoodsDef.sl;
			else
				return calcSL;
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass()).error("getPopLimitSL() index=[" + index + "]");
			PosLog.getLog(this.getClass()).error(ex);
		}
		return calcSL;
	}
}
