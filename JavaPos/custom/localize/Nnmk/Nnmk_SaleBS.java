package custom.localize.Nnmk;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentBankCMCC;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Cmls.Cmls_DataService;
import custom.localize.Cmls.Cmls_SaleBS;

public class Nnmk_SaleBS extends Cmls_SaleBS
{
	public String[] rowInfo(SaleGoodsDef goodsDef)
	{
		String[] row = super.rowInfo(goodsDef);
		
		if (!SellType.ISCHECKINPUT(saletype))
		{
			row[4] = ManipulatePrecision.doubleToString(Convert.toDouble(row[4])*SellType.SELLSIGN(saletype),4,1,true);
			row[7] = ManipulatePrecision.doubleToString(Convert.toDouble(row[7])*SellType.SELLSIGN(saletype));
		}
		return row;
	}
	//  返回到正常销售界面
	public void backToSaleStatus()
	{
		if (SellType.ISCOUPON(this.saletype) || SellType.ISEARNEST(this.saletype))
		{
			saletype = SellType.RETAIL_SALE;
		}
		else
		{
			super.backToSaleStatus();
		}
	}
	
	public void initTable(String type)
	{
		if (SellType.isJF(saletype)) saleEvent.table.getColumn(3).setText("开票");
		else 
		{
			saleEvent.table.getColumn(3).setText("单位");
			super.initTable(type);
		}
	}
	public boolean paySellStart()
	{
		if (SellType.isJF(saletype))
		{
			double je = 0;
			for (int i = 0 ; i < saleGoods.size(); i++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.elementAt(i);
				if (sgd.isvipzk == 'Y')je = ManipulatePrecision.add(je, (sgd.hjje-sgd.hjzk));
			}
			if (je > 0) new MessageBox("开票金额："+ManipulatePrecision.doubleToString(je));
		}
		return super.paySellStart();
	}
	
	public boolean inputQuantity(int index)
	{
		if (SellType.isJF(saletype))
		{
			return setKP(index);
		}
		else
		{
			return super.inputQuantity(index);
		}
	}
	
	// 设定是否开票
	public boolean setKP(int index)
	{
		if (index < 0 || index >= saleGoods.size()) return false;
		
		SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.elementAt(index);
		
		if (sgd.isvipzk == 'Y')
		{
			sgd.unit = "N";
			sgd.isvipzk = 'N';
			return true;
		}
		
		if (sgd.num1 > 0) 
		{
			sgd.isvipzk = 'Y';
			sgd.unit = "Y";
			return true;
		}
		return false;
	}
	
	public void printSaleBill()
	{
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
		if (isRealTimePrint() && !SellType.isJS(saleHead.djlb))
		{
			SaleBillMode.getDefault(saleHead.djlb).setTemplateObject(saleHead, saleGoods, salePayment);

			// 标记即扫即打结束
			Printer.getDefault().enableRealPrintMode(false);

			// 打印那些即扫即打未打印的商品
			for (int i = 0; i < saleGoods.size(); i++) realTimePrintGoods(null, i);

			// 打印即扫即打剩余小票部分
			SaleBillMode.getDefault(saleHead.djlb).printRealTimeBottom();

			//
			setHaveRealTimePrint(false);
		}
		else
		{
			SaleBillMode.getDefault(saleHead.djlb).setTemplateObject(saleHead, saleGoods, salePayment);

			// 打印整张小票
			SaleBillMode.getDefault(saleHead.djlb).printBill();
		}
		
		// 只在交易完成时打印一次移动离线充值券,因此无需放到小票模板中
		if (GlobalInfo.useMobileCharge)
		{
			PaymentBankCMCC pay = CreatePayment.getDefault().getPaymentMobileCharge(this.saleEvent.saleBS);
			if (pay != null) pay.printOfflineChargeBill(saleHead.fphm);
		}
	}
	
	public boolean yyhExtendAction(OperUserDef staff)
	{
		if (SellType.isJF(saletype) && SellType.ISBACK(saletype))
		{
			StringBuffer req = new StringBuffer();
			boolean done = new TextBox().open("请输入原缴费单号", "原缴费单号", "请输入原缴费单号", req, 0, 0, false, TextBox.IntegerInput);
			if (done)
			{
				saleHead.str3 = req.toString();
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			if (GlobalInfo.sysPara.inputyyyfph == 'Y')
			{
				StringBuffer req = new StringBuffer();
				boolean done = new TextBox().open("请输入现沽单号", "现沽单号", "请根据营业员的单据输入现沽单码", req, 0, 0, false, TextBox.IntegerInput);
				if (done)
				{
					curyyyfph = req.toString();
				}
				else
				{
					return false;
				}
			}

			return true;
		}
		
	}
	
	// 通过交易类型判断是否可以是不输入营业员
	// 定金的模式下必须输入营业员
	public boolean saleTypeControl()
	{
		if (SellType.ISEARNEST(this.saletype)) return false;
		return true;
	}
	
	// 当定金交易时，将这个里面传入标志标志为定金交易
	public String convertDzcmScsj(String dzcmscsj,boolean isdzcm)
	{
		String scsj = super.convertDzcmScsj(dzcmscsj, isdzcm);
		// 由于查询商品的过程里面没有
		if (SellType.ISEARNEST(this.saletype)) scsj = "X"+this.saletype;
		return scsj;
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
		
		// 换货状态下，不使用任何促销
		if (popDef.yhspace == 0 || hhflag == 'Y')
		{
			popDef.yhspace = 0;
			popDef.memo = "";
			popDef.poppfjzkfd = 1;
		}
		
		String line = "";
		if (popDef.yhspace == 0 && popDef.rule != null && popDef.rule.trim().length() > 0)
		{
			sg.name = popDef.rule +"-(M)"+ sg.name;
			
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

		
		String line1 = "";
		String yh = info.str1;
		if (append) yh = info.str1.substring(1);
		

		if (yh.charAt(0) != '0')
		{
			line += "D";
		}

		if (yh.charAt(1) != '0')
		{
			line1+=popDef.jssj.substring(0,popDef.jssj.indexOf("|"));
			line += "J";
		}

		if (yh.charAt(2) != '0')
		{
			line1+=popDef.jssj.substring(popDef.jssj.indexOf("|")+1,popDef.jssj.lastIndexOf("|"));
			line += "Q";
		}

		if (yh.charAt(3) != '0')
		{
			line1+=popDef.jssj.substring(popDef.jssj.lastIndexOf("|")+1);
			line += "Z";
		}
		
		if (yh.length() > 5 && yh.charAt(5) != '0')
		{
			if (popDef.rule.length() > 0)
			{
				line1 += " "+popDef.rule;
				line+="M";
			}
			else
			{
				line += "F";
			}
		}
	
		if (line.length() > 0)
		{
			if (line1.length() > 0)
				sg.name = line1+"-(" + line + ")" + sg.name;
			else
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
	}
	
	public void csExtendAction()
	{
		yyhExtendAction(null);
	}
	
	public void execCustomKey3(boolean keydownonsale)
	{
		csExtendAction();
	}
}
