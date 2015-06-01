package custom.localize.Cmjb;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentBankCMCC;
import com.efuture.javaPos.Payment.PaymentCustJfSale;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.JfSaleRuleDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Bcrm.Bcrm_DataService;
import custom.localize.Cmls.Cmls_DataService;
import custom.localize.Cmls.Cmls_SaleBS;

public class Cmjb_SaleBS extends Cmls_SaleBS
{
	public String[] rowInfo(SaleGoodsDef goodsDef)
	{
		if (SellType.ISCHECKINPUT(saletype))
		{
			String[] detail = new String[8];

			if (goodsDef.inputbarcode != null && goodsDef.inputbarcode.trim().length() > 0 && GlobalInfo.sysPara.barcodeshowcode == 'N')
			{
				detail[1] = goodsDef.inputbarcode;
			}
			else if (GlobalInfo.sysPara.barcodeshowcode == 'Y')
			{
				detail[1] = goodsDef.code;
			}
			else
			{
				detail[1] = goodsDef.barcode;
			}

			detail[2] = goodsDef.name;
			detail[3] = goodsDef.unit;

			if (goodsDef.sqkh != null && goodsDef.sqkh.trim().length() > 0)
			{
				detail[4] = goodsDef.sqkh;
			}

			if (goodsDef.jg > 0 || goodsDef.type == 'Z')
			{
				detail[5] = ManipulatePrecision.doubleToString(goodsDef.jg);
				detail[6] = ManipulatePrecision.doubleToString(goodsDef.sl, 4, 1, true);
			}
			else
			{
				detail[5] = "";
				detail[6] = "";

				// 不定价商品需要盘点数量时，界面也要显示数量
				if (GlobalInfo.sysPara.ischeckquantity == 'Y')
				{
					detail[6] = ManipulatePrecision.doubleToString(goodsDef.sl, 4, 1, true);
				}
			}

			detail[7] = ManipulatePrecision.doubleToString(goodsDef.hjje, 2, 1);

			return detail;
		}
		else
		{
			String[] detail = new String[8];

			if (goodsDef.inputbarcode != null && goodsDef.inputbarcode.trim().length() > 0 && GlobalInfo.sysPara.barcodeshowcode == 'N')
			{
				detail[1] = goodsDef.inputbarcode;
			}
			else if (GlobalInfo.sysPara.barcodeshowcode == 'Y')
			{
				detail[1] = goodsDef.code;
			}
			else
			{
				detail[1] = goodsDef.barcode;
			}

			if (GlobalInfo.syjDef.issryyy == 'Y' && goodsDef.gz != null && !goodsDef.gz.equals(""))
			{
				detail[2] = goodsDef.name + "  [" + goodsDef.gz + "]";
			}
			else
			{
				detail[2] = goodsDef.name;
			}

			detail[3] = goodsDef.unit;
			detail[4] = ManipulatePrecision.doubleToString(goodsDef.sl, 4, 1, true);
			if (saletype == SellType.GROUPBUY_SALE)
			{
				detail[5] = ManipulatePrecision.doubleToString(goodsDef.lsj);
			}
			else
			{
				detail[5] = ManipulatePrecision.doubleToString(goodsDef.jg);
			}
			detail[6] = ManipulatePrecision.doubleToString(goodsDef.hjzk) + ((goodsDef.hjzk > 0) && (goodsDef.hjje - goodsDef.hjzk > 0) ? "(" + ManipulatePrecision.doubleToString((goodsDef.hjje - goodsDef.hjzk) / goodsDef.hjje * 100, 0, 1) + "%)" : "");
			detail[7] = ManipulatePrecision.doubleToString(goodsDef.hjje - goodsDef.hjzk, 2, 1);

			return detail;
		}
	}

	public String[] convertColumnValue(String[] srcValue, int index)
	{
		try
		{
			if (srcValue == null || srcValue.length == 0)
				return srcValue;

			if (tab == null || tab.size() == 0)
				return srcValue;

			SaleGoodsDef goodsDef = (SaleGoodsDef) saleGoods.get(index);

			if (goodsDef != null)
			{
				srcValue[2] = goodsDef.name + "[" + goodsDef.gz+"]";

				if (SellType.ISCHECKINPUT(saletype))
				{
					srcValue[6] = ManipulatePrecision.doubleToString(goodsDef.sl, 4, 1, true);
					srcValue[7] = ManipulatePrecision.doubleToString(goodsDef.hjje, 2, 1);
				}
				else
				{
					srcValue[6] = ManipulatePrecision.doubleToString(goodsDef.hjzk) + ((goodsDef.hjzk > 0) && (goodsDef.hjje - goodsDef.hjzk > 0) ? "(" + ManipulatePrecision.doubleToString((goodsDef.hjje - goodsDef.hjzk) / goodsDef.hjje * 100, 0, 1) + "%)" : "");
					srcValue[7] = ManipulatePrecision.doubleToString(goodsDef.hjje - goodsDef.hjzk, 2, 1);
				}
			}
			return srcValue;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return srcValue;
		}
	}

	public String getVipInfoLabel()
	{
		if (curCustomer == null)
			return "";
		else
			return "[" + curCustomer.code + "]";

	}

	public boolean memberGrant()
	{
		if (super.memberGrant())
		{
			int ret = new MessageBox("本单是否享用VIP折扣?", null, true).verify();

			if (ret != GlobalVar.Key1 && ret != GlobalVar.Enter)
				curCustomer.iszk = 'N';

			//curCustomer.valuememo 保存最大可用积分 curCustomer.value5 保存常规积分 这样做不用修改积分消费
			curCustomer.valuememo = curCustomer.value5;
			return true;
		}
		return false;
	}

	public void findGoodsCRMPop(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		String cardno = null;
		String cardtype = null;
		String isfjk = "";
		String grouplist = "";
		String newyhsp = "90000000";

		if ((curCustomer != null))
		{
			cardno = curCustomer.code;
			cardtype = curCustomer.type;
			if (curCustomer.func.length() >= 2)
				isfjk = String.valueOf(curCustomer.func.charAt(1));
			grouplist = curCustomer.valstr3;
		}

		GoodsPopDef popDef = new GoodsPopDef();

		// 非促销商品 或者在退货时，不查找促销信息
		((Cmls_DataService) DataService.getDefault()).findPopRuleCRM(popDef, sg.code, sg.gz, sg.uid, goods.specinfo, sg.catid, sg.ppcode, saleHead.rqsj, cardno, cardtype, isfjk, grouplist, saletype);

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
				if (GlobalInfo.sysPara.iscrmtjprice == 'Y')
					info.str1 = Convert.increaseInt(popDef.yhspace, 5).substring(0, 4);
				else
					info.str1 = Convert.increaseInt(popDef.yhspace, 4);

				append = false;
			}
			else
			{
				info.str1 = String.valueOf(popDef.yhspace);

				append = true;
			}
			// 询问参加活动类型 满减或者满增
			String yh = info.str1;

			if (append)
				yh = yh.substring(1);

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

			if (append)
				info.str1 = "9" + buff.toString();
			else
				info.str1 = buff.toString();
		}

		String line = "";

		String yh = info.str1;
		if (append)
			yh = info.str1.substring(1);

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
			if (GlobalInfo.sysPara.iscrmtjprice == 'Y')
				sg.str3 = info.str1 + String.valueOf(Convert.increaseInt(popDef.yhspace, 5).substring(4));
			else
				sg.str3 = info.str1;
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
		sg.str15 = popDef.mode;
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
				Cmjb_LHSaleBillMode.getInstance(saleHead.djlb).setSaleTicketMSInfo(saleHead, gifts);
			else
				SaleBillMode.getDefault(saleHead.djlb).setSaleTicketMSInfo(saleHead, gifts);
		}

		// 恢复暂停状态的实时打印
		stopRealTimePrint(false);

		// 实时打印只打印剩余部分
		if (isRealTimePrint())
		{
			if (enableLHBillMode)
				Cmjb_LHSaleBillMode.getInstance(saleHead.djlb).setTemplateObject(saleHead, saleGoods, salePayment);
			else
				SaleBillMode.getDefault(saleHead.djlb).setTemplateObject(saleHead, saleGoods, salePayment);

			// 标记即扫即打结束
			Printer.getDefault().enableRealPrintMode(false);

			// 打印那些即扫即打未打印的商品
			for (int i = 0; i < saleGoods.size(); i++)
				realTimePrintGoods(null, i);

			// 打印即扫即打剩余小票部分
			if (enableLHBillMode)
				Cmjb_LHSaleBillMode.getInstance(saleHead.djlb).printRealTimeBottom();
			else
				SaleBillMode.getDefault(saleHead.djlb).printRealTimeBottom();

			//
			setHaveRealTimePrint(false);
		}
		else
		{
			if (enableLHBillMode)
			{
				Cmjb_LHSaleBillMode.getInstance(saleHead.djlb).setTemplateObject(saleHead, saleGoods, salePayment);
				Cmjb_LHSaleBillMode.getInstance(saleHead.djlb).printBill();
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

    	jf1 = jfrd.num3;

    	
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
    
    public boolean isSHOUquan()
    {
    	Vector v = new Vector();
    	for(int i=0;i<saleGoods.size();i++)
    	{	
    		SaleGoodsDef sgd = ((SaleGoodsDef)saleGoods.elementAt(i));
    		if(sgd.str15 != null)
    		{
    			String[] s = sgd.str15.split("\\|");
    			for(int j=0;j<s.length;j++)
    			{
    				String[] ss = s[j].split(",");
            		
        			if(ss[0].matches("[A-Z]") && ss[0].matches("[^Z^T^U^D^H]") )
            		{
        				for(int z=0;z<v.size();z++)
        				{
        					if(!((String)v.elementAt(z)).split(",")[0].equals(ss[0]))
        					{
        						v.add(s[j]);
        					}
        				}
        				if(v.size() == 0)
        				{
        					v.add(s[j]);
        				}
        				
        				for(int x=0;x<v.size();x++)
        				{
        					double hj = sgd.hjje-sgd.hjzk;
        					String[] l = ((String)v.elementAt(x)).split(","); 
        					
        					l[1] = String.valueOf(Convert.toDouble(l[1])-hj);
        					
        					v.setElementAt(l[0]+","+l[1]+","+l[2]+","+l[3], x);
        					
        					if(Convert.toDouble(l[1]) <= 0 )
            				{
            					return true;
            				}
        				}
            			
            		}
    			}
    		}
    	}
    	return false;
    }
    public void custMethod()
	{
    	super.custMethod();
    	if(isSHOUquan())
    	{
    		new MessageBox("此笔小票可以收券!");
    	}
	}
}
