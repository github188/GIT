package custom.localize.Njxb;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Bjcx.Bjcx_NetService;
import custom.localize.Cmls.Cmls_DataService;

public class Njxb_SaleBS extends Njxb_SaleBS0CRMPop
{
	private boolean isCalcFee = false;

	//会员卡
	public boolean memberGrant()
	{
		if (isPreTakeStatus())
		{
			new MessageBox(Language.apply("预售提货状态下不允许重新刷卡"));
			return false;
		}

		// 会员卡必须在商品输入前,则输入了商品以后不能刷卡,指定小票除外
		if (GlobalInfo.sysPara.customvsgoods == 'A' && saleGoods.size() > 0 && !isNewUseSpecifyTicketBack(false))
		{
			new MessageBox(Language.apply("必须在输入商品前进行刷会员卡\n\n请把商品清除后再重刷卡"));
			return false;
		}

		// 读取会员卡
		HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();
		String track2 = bs.readMemberCard();
		if (track2 == null || track2.equals(""))
			return false;
		//读取阿里会员卡
		if(track2.split(":").length >=3)
		{
			String value = track2.split(":")[2];
			track2 = value.substring(1, value.length());
		}

		// 查找会员卡
		CustomerDef cust = bs.findMemberCard(track2);

		if (cust == null)
		
			return false;

		// 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
		if (isNewUseSpecifyTicketBack(false))
		{
			// 指定小票退仅记录卡号,不执行商品重算等处理
			curCustomer = cust;
			saleHead.hykh = cust.code;
			saleHead.hytype = cust.type;
			saleHead.str4 = cust.valstr2;
			saleHead.hymaxdate = cust.maxdate;
			return true;
		}
		else
		{
			// 记录会员卡
			return memberGrantFinish(cust);
		}
	}

	
	// 新百满抵需求
	public void custMethod()
	{
		if (!SellType.ISBACK(saletype)) { return; }

		for (int j = 0; j < saleGoods.size(); j++)
		{
			SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.elementAt(j);
			SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(j);

			if (sgd.num1 > 0)
			{
				createMDPayment((sgd.num1 * sgd.sl));

				SalePayDef sp = (SalePayDef) salePayment.elementAt(salePayment.size() - 1);
				String s[] = { String.valueOf(sp.num5), sp.paycode, sp.payname, String.valueOf(sgd.num1 * sgd.sl) };
				if (spinfo.payft == null) spinfo.payft = new Vector();
				spinfo.payft.add(s);
			}
		}
		// 重算应收
		calcHeadYsje();
	}

	// 新百满抵需求
	public void doBrokenData()
	{
		if (GlobalInfo.sysPara.mdcode.split(",")[0].trim().equals("")) { return; }
		SalePayDef sp = null;
		for (int i = salePayment.size() - 1; i > -1; i--)
		{
			sp = (SalePayDef) salePayment.elementAt(i);
			if (sp.paycode.equals(GlobalInfo.sysPara.mdcode.split(",")[0]))
			{
				salePayment.remove(i);
			}
		}
	}

	// 新百满抵需求
	public boolean checkDeleteSalePay(String ax, boolean isDelete)
	{
		// 计算过黄金手续费后，不允许删除单行付款方式
		if (isCalcFee) { return true; }

		String code = "";
		if (ax.trim().indexOf("]") > -1)
		{
			code = ax.substring(1, ax.trim().indexOf("]"));
		}
		else
		{
			code = ax;
		}
		// 满抵付款方式不允许删除
		if (code.equals(GlobalInfo.sysPara.mdcode.split(",")[0])) { return true; }
		return false;
	}

	// 金鹰卡集中一次性记账
	public boolean saleCollectAccountPay()
	{
		Payment p = null;
		boolean czsend = true;
		boolean hasGecrmPay = false;

		// 付款对象记账
		for (int i = 0; i < payAssistant.size(); i++)
		{
			p = (Payment) payAssistant.elementAt(i);
			if (p == null) continue;

			// 第一次记账前先检查是否有冲正需要发送
			if (czsend)
			{
				czsend = false;
				if (!p.sendAccountCz()) return false;
			}

			if (p.paymode.code.equals("0401") || p.paymode.code.equals("0402"))
			{
				if (!hasGecrmPay)
				{
					hasGecrmPay = true;
				}
				continue;
			}
			else
			{
				// 付款记账
				if (!p.collectAccountPay()) return false;
			}
		}

		if (hasGecrmPay)
		{
			GecrmFunc gecrmFunc = null;
			if (SellType.ISSALE(saleHead.djlb))
			{
				gecrmFunc = new GecrmFunc(GecrmFunc.SALE, saleHead, salePayment);
			}
			else
			{
				gecrmFunc = new GecrmFunc(GecrmFunc.BACK, saleHead, salePayment);
			}

			if (!gecrmFunc.doGecrm(null)) { return false; }

			reCalcGecrmBalance(salePayment, false);
		}

		// 移动充值对象记账
		if (GlobalInfo.useMobileCharge && !mobileChargeCollectAccount(true)) return false;

		return true;
	}

	// 发现输入编码不足9位时，前补0至9位
	public void enterInputCODE()
	{
		if(sgVIPZK){
			new MessageBox("手工进行会员折扣操作,不允许录入商品!");
			saleEvent.code.setText("");
			return;
		}
		String iptCode = saleEvent.code.getText().trim();
		if (iptCode.length() > 0 && iptCode.length() < 9)
		{
			saleEvent.code.setText(Convert.increaseCharForward(iptCode, '0', 9));
		}
		super.enterInputCODE();
	}

	public static void reCalcGecrmBalance(Vector salePayment, boolean isBack)
	{
		SalePayDef sp;
		for (int i = 0; i < salePayment.size(); i++)
		{
			sp = (SalePayDef) salePayment.get(i);
			if (sp.paycode.equals("0401") || sp.paycode.equals("0402"))
			{
				if (!isBack)
				{
					sp.kye -= sp.je;
				}
				else
				{
					sp.kye += sp.je;
				}
				sp.kye = ManipulatePrecision.doubleConvert(sp.kye);
			}
		}
	}

	public boolean paySellStart()
	{
		if (super.paySellStart())
		{
			if (SellType.ISCOUPON(saletype))
			{
				if (checkCouponSaleLimit())
				{
					return true;
				}
				else
				{
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private boolean checkCouponSaleLimit()
	{
		if (!SellType.ISSALE(saletype)) { return true; }
		if (curCustomer == null) { return true; }

		// 合并相同规则的买券明细
		Vector newSaleGoods = new Vector();
		for (int i = 0; i < saleGoods.size(); i++)
		{
			newSaleGoods.add(((SaleGoodsDef) saleGoods.get(i)).clone());
		}

		SaleGoodsDef sg1 = null;
		SaleGoodsDef sg2 = null;
		for (int i = 0; i < newSaleGoods.size(); i++)
		{
			sg1 = (SaleGoodsDef) newSaleGoods.get(i);
			for (int j = i + 1; j < newSaleGoods.size(); j++)
			{
				sg2 = (SaleGoodsDef) newSaleGoods.get(j);
				// 比较买券规则代码
				if (sg1.code.equals(sg2.code))
				{
					sg1.hjje += sg2.hjje;
					sg1.sl += sg2.sl;
					newSaleGoods.remove(j);
					j--;
				}
			}
		}

		// 根据买券上限规则判断是否达到上限
		SaleGoodsDef sg = null;
		Vector couponLimitList = new Vector();
		Njxb_NetService netService = (Njxb_NetService) NetService.getDefault();
		for (int i = 0; i < newSaleGoods.size(); i++)
		{
			sg = (SaleGoodsDef) newSaleGoods.get(i);
			if (netService.checkCouponSaleLimit(sg.catid, sg.code, saleHead.hykh, couponLimitList))
			{
				String[] info = (String[]) couponLimitList.get(0);
				double unitqje = Double.parseDouble(info[0]);
				double maxtimes = Double.parseDouble(info[1]);
				double maxmoney = Double.parseDouble(info[2]);

				if (maxtimes < 1)
				{
					new MessageBox("已达到买券[" + sg.code + "]规则次数上限");
					return false;
				}

				if (maxmoney < sg.sl * unitqje)
				{
					new MessageBox("已达到买券[" + sg.code + "]规则金额[" + maxmoney + "]上限");
					return false;
				}
			}
			else
			{
				new MessageBox("查找买券上限规则失败，不允许买券！");
				return false;
			}
		}
		return true;
	}
	
	// 计算交易手续费
	private double calcProcedureFee()
	{
		if (isCalcFee) return 0;
		double feeMoney = 0;
		double feeJe = 0;
		String feeCode = "";
		SalePayDef sp = null;
		for (int i = 0; i < salePayment.size(); i++)
		{
			sp = (SalePayDef) salePayment.get(i);
			if (sp.str6 != null && sp.str6.trim().length() > 0)
			{
				try 
				{
					if (sp.str6.split(",").length != 2) continue;
					feeCode = sp.str6.split(",")[1];
					feeJe = Double.parseDouble(sp.str6.split(",")[0]) * sp.je;
					if (!addProcedureFee(feeJe, feeCode)) return -1;
					feeMoney += feeJe;
				}
				catch (Exception e)
				{
					e.printStackTrace();
					new MessageBox("计算手续费时发生异常");
					return -1;
				}
			}
		}
		return feeMoney;
	}
	
	private boolean addProcedureFee(double feeJe, String code)
	{
		GoodsDef gdFee = new GoodsDef();
		if (DataService.getDefault().getGoodsDef(gdFee, 1, code, "", "", "", saletype) == -1) { return false; }
		gdFee.lsj = getConvertPrice(feeJe, gdFee);
		double price = ManipulatePrecision.doubleConvert(gdFee.lsj, 2, 1);
		double allprice = price;
		// 生成商品明细
		SaleGoodsDef sgFee = goodsDef2SaleGoods(gdFee, saleEvent.yyyh.getText(), 1, price, allprice, false);
		// 记录该商品是手续费
		sgFee.str6 = "Y";
		// 增加商品明细
		addSaleGoodsObject(sgFee, gdFee, getGoodsSpareInfo(gdFee, sgFee));
		// 重算应收
		calcHeadYsje();
		return true;
	}

	/*
	// 计算黄金交易手续费
	private double calcProcedureFee()
	{
		 if (isCalcFee || GlobalInfo.sysPara.feeCode.length() == 0 || GlobalInfo.sysPara.feeRate <= 0 || GlobalInfo.sysPara.feePayment.length() == 0) { return 0; }

		 double feePmtMoney = 0;
		 SalePayDef sp = null;
		 for (int i = 0; i < salePayment.size(); i++)
		 {
		 sp = (SalePayDef) salePayment.get(i);
		 if (("," + GlobalInfo.sysPara.feePayment + ",").indexOf("," + sp.paycode + ",") > -1)
		 {
		 feePmtMoney += sp.je;
		 }
		 }
		 if (feePmtMoney == 0) { return 0; }

		 // 计算手续费
		 double feeMoney = 0;
		 feeMoney = feePmtMoney * GlobalInfo.sysPara.feeRate;
		 return feeMoney;
	}

	 private boolean addProcedureFee(double feeJe)
	 {
	 GoodsDef gdFee = new GoodsDef();
	 if (DataService.getDefault().getGoodsDef(gdFee, 1, GlobalInfo.sysPara.feeCode, "", "", "", saletype) == -1) { return false; }
	 gdFee.lsj = getConvertPrice(feeJe, gdFee);
	 double price = ManipulatePrecision.doubleConvert(gdFee.lsj, 2, 1);
	 double allprice = price;
	 // 生成商品明细
	 SaleGoodsDef sgFee = goodsDef2SaleGoods(gdFee, saleEvent.yyyh.getText(), 1, price, allprice, false);
	 // 增加商品明细
	 addSaleGoodsObject(sgFee, gdFee, getGoodsSpareInfo(gdFee, sgFee));
	 // 重算应收
	 calcHeadYsje();
	 return true;
	 }
	 */
	public boolean payComplete()
	{
		SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(0);
		// 存在需收手续费的商品
		if (SellType.ISSALE(saletype) && sg.num4 != 0)
		{
			// 计算手续费
			double feeMoney = calcProcedureFee();
			if (feeMoney > 0)
			{
				new MessageBox("本笔交易存在储值卡付款的特殊商品，需交手续费");
				isCalcFee = true;
				salePayEvent.refreshSalePayment();
				refreshSaleForm();
				return super.payComplete();
			}
			else if (feeMoney == -1)
			{
				new MessageBox("计算手续费失败");
				AccessDayDB.getDefault().writeWorkLog("计算手续费失败");
				return super.payComplete();
			}
			else
			{
				return super.payComplete();
			}
		}
		else
		{
			return super.payComplete();
		}
	}

	// 退出付款时要删除服务费明细
	public boolean deleteAllSalePay()
	{
		if (super.deleteAllSalePay())
		{
			isCalcFee = false;
			if (isCalcFee)
			{
				// 删除服务费商品
				for (int i = 0; i < saleGoods.size(); i++)
				{
					if ("Y".equals(((SaleGoodsDef) saleGoods.get(i)).str6))
					{
						if (delSaleGoodsObject(i))
						{
							calcHeadYsje();
							return true;
						}
					}
				}
			}
			else
			{
				return true;
			}
		}
		return false;
	}

	public void doSaleFinshed(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
	{
		isCalcFee = false;
	}

	public int existPayment(String code, String account)
	{
		if (isCalcFee) { return -1; }
		return super.existPayment(code, account);
	}

	public SaleGoodsDef goodsDef2SaleGoods(GoodsDef goodsDef, String yyyh, double quantity, double price, double allprice, boolean dzcm)
	{
		SaleGoodsDef sgd = super.goodsDef2SaleGoods(goodsDef, yyyh, quantity, price, allprice, dzcm);
		sgd.num4 = goodsDef.num5;
		return sgd;
	}

	public String getSyyInfoLabel()
	{
		return GlobalInfo.posLogin.name;
	}

	public boolean checkIsSalePay(String code)
	{
		if (code.equals(GlobalInfo.sysPara.mdcode.split(",")[0]))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean checkOldExChangeNew(GoodsDef goodsDef)
	{
		if (saleGoods.size() <= 0) { return false; }
		return true;
	}
	
	public void findGoodsCRMPop(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		String cardno = null;
		String cardtype = null;
		String isfjk = "";
		String grouplist = "";
		String newyhsp = "90000000";

		if ((curCustomer != null && curCustomer.iszk == 'Y'))
		{
			cardno = curCustomer.code;
			cardtype = curCustomer.type;
			if (curCustomer.func.length() >= 2) isfjk = String.valueOf(curCustomer.func.charAt(1));
			grouplist = curCustomer.valstr3;
		}

		GoodsPopDef popDef = new GoodsPopDef();

		// 非促销商品 或者在退货时，不查找促销信息
		((Cmls_DataService) DataService.getDefault()).findPopRuleCRM(popDef, sg.code, sg.gz, sg.uid, goods.specinfo, sg.catid, sg.ppcode,
																		saleHead.rqsj, cardno, cardtype,isfjk,grouplist,saletype);
		
		// 记录收券规则
		sg.str9 = popDef.mode;
		// 换货状态下，不使用任何促销
		if (popDef.yhspace == 0 || hhflag == 'Y')
		{
			popDef.yhspace = 0;
			popDef.memo = "";
			popDef.poppfjzkfd = 1;
		}
		
		// 换货状态下，不使用任何促销
		if (popDef.yhspace == Convert.toInt(newyhsp) || hhflag == 'Y')
		{
			popDef.yhspace = Convert.toInt(newyhsp);
			popDef.memo = "";
			popDef.poppfjzkfd = 1;
		}

		//将收券规则放入GOODSDEF 列表
		goods.memo = popDef.str2;
		goods.num1 = popDef.num1;
		goods.num2 = popDef.num2;
		goods.str4 = popDef.mode;
		info.char3 = popDef.type;

		// 促销联比例
		sg.xxtax = Convert.toDouble(popDef.ksrq); // 促销联比例
		goods.xxtax = Convert.toDouble(popDef.ksrq);
		if (goods.memo == null) goods.memo = "";

		// 增加CRM促销信息
		crmPop.add(popDef);

		// 标志是否为9开头扩展的控制
		boolean append = false;
		// 无促销,此会员不允许促销
		if (popDef.yhspace == 0)
		{
			append = false;
			info.str1 = "0000";
		}
		else if (popDef.yhspace == Integer.parseInt(newyhsp))
		{
			append = true;
			info.str1 = newyhsp;
		}
		else
		{
			
			if (String.valueOf(popDef.yhspace).charAt(0) != '9')
			{
				if (GlobalInfo.sysPara.iscrmtjprice == 'Y') info.str1 = Convert.increaseInt(popDef.yhspace, 5).substring(0, 4);
				else info.str1 = Convert.increaseInt(popDef.yhspace, 4);
				
				append = false;
			}
			else 
			{
				info.str1 = String.valueOf(popDef.yhspace);
				
				append = true;
			}
			//询问参加活动类型 满减或者满增
			String yh = info.str1;
			
			if (append) yh = yh.substring(1);
			
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
					else if (i == 5)
					{
						contents.add(new String[] { "F", "参与积分活动", "5" });
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

			if (append) info.str1 = "9"+buff.toString();
			else info.str1 = buff.toString();
		}

		String line = "";
		
		String yh = info.str1;
		if (append) yh = info.str1.substring(1);
		

		if (yh.charAt(0) != '0')
		{
			line += "D";
		}

		if (yh.charAt(1) != '0')
		{
			line += "J";
		}

		if (yh.charAt(2) != '0')
		{
			line += "Q";
		}

		if (yh.charAt(3) != '0')
		{
			line += "Z";
		}
		
		if (yh.length() > 5 && yh.charAt(5) != '0')
		{
			line += "F";
		}

		if (line.length() > 0)
		{
			sg.name = "(" + line + ")" + sg.name;
		}

		if (!append)
		{
			// str3记录促销组合码
			if (GlobalInfo.sysPara.iscrmtjprice == 'Y') sg.str3 = info.str1 + String.valueOf(Convert.increaseInt(popDef.yhspace, 5).substring(4));
			else sg.str3 = info.str1;
		}
		else
		{
			sg.str3 = info.str1;
		}
		// 将商品属性码,促销规则加入SaleGoodsDef里
		sg.str3 += (";" + goods.specinfo);
		sg.str3 += (";" + popDef.memo);
		sg.str3 += (";" + popDef.poppfjzkl);
		sg.str3 += (";" + popDef.poppfjzkfd);
		sg.str3 += (";" + popDef.poppfj);

		// 只有找到了规则促销单，就记录到小票
		if (!info.str1.equals("0000") || !info.str1.equals(newyhsp))
		{
			sg.zsdjbh = popDef.djbh;
			sg.zszkfd = popDef.poplsjzkfd;
		}
		
		
		//价随量变
		  cardno = null;
		  cardtype = null;
		  isfjk = "";
		  grouplist = "";

		if ((curCustomer != null && curCustomer.iszk == 'Y'))
		{
			cardno = curCustomer.code;
			cardtype = curCustomer.type;
			if (curCustomer.func.length() >= 2) isfjk = String.valueOf(curCustomer.func.charAt(1));
			grouplist = curCustomer.valstr3;
		}
		
		if (!GlobalInfo.isOnline) { return ; }
		if (GlobalInfo.sysPara.isGroupJSLB != 'N' || !SellType.isGroupbuy(this.saletype))
		{
			//查询商品价随量变信息
			((Njxb_NetService) NetService.getDefault()).findBatchRule(info, sg.code, sg.gz, sg.uid, goods.str1, sg.catid, sg.ppcode,
																		saleHead.rqsj, cardno, cardtype,isfjk,grouplist,saletype,GlobalInfo.localHttp);
			if (info.Zklist!=null && info.Zklist.trim().length() > 1) sg.name = "B"+sg.name;
		}
	}
	
	public void execCustomKey6(boolean keydownonsale)
	{
		if(sgVIPZK){
			int index = saleEvent.table.getSelectionIndex();
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
			double oldhyzk = saleGoodsDef.hyzke;   //原会员折扣
			double oldzhj = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - saleGoodsDef.hjzk,2,1); // 原折后价
			
			String maxzklmsg = "收银员正在对该商品进行打折,原会员折扣为"+oldhyzk;
			StringBuffer buffer = new StringBuffer();
			
			if (!new TextBox().open("请输入单品会员折扣后的价格", "会员折扣", maxzklmsg, buffer, 0, ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - saleGoodsDef.hjzk) +saleGoodsDef.hyzke,2,1), true))
			{
				return;
			}
			double newzhj = Double.parseDouble(buffer.toString());
			
			saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert(saleGoodsDef.hyzke + (oldzhj - newzhj),2,1);
			
			//saleGoodsDef.hyzke = Double.parseDouble(buffer.toString());
			
//			 重算商品折扣合计
			getZZK(saleGoodsDef);
			
			if(ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - saleGoodsDef.hjzk,2,1)<0){
				new MessageBox("总折扣金额大于商品合计金额,本次会员折扣修改失败!");
				saleGoodsDef.hyzke = oldhyzk;
				getZZK(saleGoodsDef);
			}
			// 重算小票应收
			calcHeadYsje();
			
//			 刷新商品列表
			saleEvent.updateTable(getSaleGoodsDisplay());
			saleEvent.table.setSelection(index);

			// 显示汇总
			saleEvent.setTotalInfo();

			// 显示商品大字信息
			saleEvent.setCurGoodsBigInfo();
			
		}else{
			new MessageBox("请先按付款键计算会员折扣后再使用该功能!");
		}
	}
	
	public boolean allowEditGoods()
	{
		if(sgVIPZK){
			new MessageBox("手工进行会员折扣操作,不允许修改商品信息!");
			return false;
		}
	    	
		return super.allowEditGoods();
	}
	
	public boolean allowDeleteGoods(int index)
	{
		// A模式指定小票退货不允许修改商品
		if (isNewUseSpecifyTicketBack()) return false;

		if (isPreTakeStatus())
		{
			new MessageBox("预售提货取消必须使用【取消】功能键");
			return false;
		}
		
		if(sgVIPZK){
			new MessageBox("手工进行会员折扣操作,不允许删除商品!");
			return false;
		}
		return true;
	}
	
	public boolean clearSell(int index)
	{
		if(sgVIPZK){
			new MessageBox("手工进行会员折扣操作,不允许进行取消!");
			return false;
		}
		return super.clearSell(index);
		
	}
}
