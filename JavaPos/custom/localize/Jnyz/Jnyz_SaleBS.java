package custom.localize.Jnyz;

import java.util.Vector;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.CashBox;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Device.SecMonitor;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Plugin.EBill.EBill;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.RefundMoneyDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;
import com.efuture.javaPos.UI.Design.RetSYJForm;
import com.efuture.javaPos.UI.Design.SalePayForm;

import custom.localize.Bcrm.Bcrm_DataService;
import custom.localize.Cmls.Cmls_DataService;
import custom.localize.Cmls.Cmls_SaleBS;

public class Jnyz_SaleBS extends Cmls_SaleBS
{	 
	 public boolean cxRebate = false;
	 public void caculateSL(CxRebateDef cx,SaleGoodsDef sgd)
	 {
		 double oldyhzke = sgd.yhzke;
		 double oldhyzke = sgd.hyzke;
		 sgd.yhzke = 0;
		 sgd.hyzke = 0;
		 sgd.plzke = 0;
		 
		 sgd.plzke = ManipulatePrecision.doubleConvert(sgd.hjje - getZZK(sgd) - ManipulatePrecision.doubleConvert((sgd.hjje - getZZK(sgd)) * cx.zkl_result));
			
		 //mjye += salecom[j].plzke;
		 sgd.yhzke = oldyhzke;
		 sgd.hyzke = oldhyzke;

		 if (sgd.plzke > ManipulatePrecision.doubleConvert(oldyhzke + oldhyzke))
		 {
			 sgd.plzkfd = cx.zkfd;
			 sgd.yhzke = 0;
			 sgd.hyzke = 0;
		 }
		 else
		 {
			 sgd.plzke = 0;
		 }
		 getZZK(sgd);
		 /**
			mjje += salecom[j].yhzke + salecom[j].hyzke;
			double oldyhzke = salecom[j].yhzke;
			double oldhyzke = salecom[j].hyzke;
			salecom[j].yhzke = 0;
			salecom[j].hyzke = 0;
			salecom[j].plzke = 0;
			//计算批量折扣
			salecom[j].plzke = salecom[j].zje - ZZK(salecom[j]) - FloatConvert((salecom[j].zje - ZZK(salecom[j])) * zkl,2);
			
			mjye += salecom[j].plzke;
			salecom[j].yhzke = oldyhzke;
			salecom[j].hyzke = oldhyzke;
			*/
	 }
	 
		public boolean paySellPop()
		{
			//doCmPopWriteData();
			if (calcPlZk())
			{
				cxRebate = true;
			}
			return super.paySellPop();
		}
	 
	public boolean calcPlZk()
	{
		//检查是否参与PL折扣		
		Vector group = new Vector();
		 // 先进行分组
		 for (int i = 0 ; i < goodsSpare.size(); i++)
		 {
			 SpareInfoDef sid = (SpareInfoDef) goodsSpare.elementAt(i);
			 String memo = sid.memo1;
			 String memos[] = (" "+memo+" ").split(",");
			 if (memos.length >= 5)
			 {
				if (memos[4]!= null && memos[4].length() > 0)
				{
					sid.memo2 = memos[4];
					if (memos.length >= 6) sid.memo3 = memos[5];
				}
				else
				{
					continue;
				}
			 }
			 else
			 {
				continue;
			 }
			
			 SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
			 GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(i);
			 int n = 0;
			 for (;n < group.size(); n++)
			 {
				 CxRebateDef cx = (CxRebateDef) group.elementAt(n);
				 
				 if (cx.billno.equals(sid.memo2))
				 {					 
					 cx.zsl = ManipulatePrecision.doubleConvert(cx.zsl + saleGoodsDef.sl);
					 cx.list.add(String.valueOf(i));
					 break;
				 }
			 }
			 
			 if (n >=  group.size())
			 {
				 CxRebateDef cx = new CxRebateDef();
				 
				 cx.billno = sid.memo2;
				 if (sid.memo3 != null) cx.zkfd = Convert.toDouble(sid.memo3);
				 cx.zsl = ManipulatePrecision.doubleConvert(saleGoodsDef.sl);
				 cx.list.add(String.valueOf(i));
				 
				 cx.gys = goodsDef.str1;
				 cx.gz  = goodsDef.gz;
				 cx.pp = goodsDef.ppcode;
				 cx.catid = goodsDef.catid;
				 cx.code = goodsDef.code;
				 
				 group.add(cx);
			 }
		 }
		 
		 if (group.size() <=0) return false;
		 
		 //检查促销规则
		 for (int i = 0 ; i < group.size(); i++)
		 {
			 CxRebateDef cx = (CxRebateDef) group.elementAt(i);
			 ((Jnyz_NetService)NetService.getDefault()).findPLZK(cx,cx.billno);
			 
			 if (cx.zkl_result <= 0 || cx.zkl_result >= 1)
			 {
				 group.removeElementAt(i);
				 i--;
			 }
		 }
		 
		 if (group.size() <=0) return false;
		 
		 //开始计算促销
		 for (int i = 0 ; i < group.size(); i++)
		 {
			 CxRebateDef cx = (CxRebateDef) group.elementAt(i);
			 for (int x = 0; x < cx.list.size(); x++)
			 {
				 int index = Convert.toInt(cx.list.elementAt(x));
				 SpareInfoDef sid = (SpareInfoDef) goodsSpare.elementAt(index);
				 SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
				 caculateSL(cx,saleGoodsDef);
			 }
		 }
		 

	 	//重算应收
		calcHeadYsje();

		// 刷新商品列表
		saleEvent.updateTable(getSaleGoodsDisplay());
		saleEvent.setTotalInfo();

 
	 
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
			//popDef.memo = "";
			popDef.poppfjzkfd = 1;
		}
		
		// 批发状态下，不使用任何促销
		if(SellType.ISBATCH(saletype)){
			popDef.yhspace = 0;
		}
		
		// 换货状态下，不使用任何促销
		if (popDef.yhspace == Convert.toInt(newyhsp) || hhflag == 'Y')
		{
			popDef.yhspace = Convert.toInt(newyhsp);
			//popDef.memo = "";
			popDef.poppfjzkfd = 1;
		}

		//将收券规则放入GOODSDEF 列表
		goods.memo = popDef.str2;
		goods.num1 = popDef.num1;
		goods.num2 = popDef.num2;
		goods.str4 = popDef.mode;
		info.char3 = popDef.type;
		
		info.memo1 = popDef.memo;

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
	}
	
    public void execCustomKey2(boolean keydownonsale)
    {
    	if (Jnyz_CustomGlobalInfo.getDefault().sysPara.isrebate != 'Y') return;
    	
        int index = saleEvent.table.getSelectionIndex();
        if (index >= 0)
        {
        	// 输入商品折扣
        	if (inputGysZk(index))
        	{
	        	// 刷新商品列表
        		saleEvent.updateTable(getSaleGoodsDisplay());
        		saleEvent.table.setSelection(index);
		        
		        // 显示汇总
        		saleEvent.setTotalInfo();
		        
		        // 显示商品大字信息
		        saleEvent.setCurGoodsBigInfo();
        	}
        }
    }
    
    private boolean inputGysZk(int index)
    {
        double grantzkl = 0;
        SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);   	
        
        // 服务费 以旧换新不处理
        if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8'))
        {
            return false;
        }

        // 备份数据
        SaleGoodsDef oldGoodsDef = (SaleGoodsDef) saleGoodsDef.clone();
                
        // 最大折扣权限
        grantzkl = 0.01;
        	
        // 记录授权工号
        saleGoodsDef.sqkh = "####";
        saleGoodsDef.sqktype = 1;
        saleGoodsDef.sqkzkfd = 0;	// 折扣全部由供应商承担,商家则承担0
        
        // 计算权限允许的最大折扣额
        double maxzre = ManipulatePrecision.doubleConvert((1 - grantzkl) * saleGoodsDef.hjje,2,1);
        
        // 根据模拟计算得到当前最大打折金额
        double lszre = maxzre;
        lszre = ManipulatePrecision.doubleConvert(lszre, 2, 1);

        // 输入折让
        StringBuffer buffer = new StringBuffer();
        if (!new TextBox().open("请输入单品供应商折扣","供应商折扣","当前收银员最大供应商折扣权限为 " + ManipulatePrecision.doubleToString(grantzkl*100) + "%\n你目前对该商品最多能折让 " + ManipulatePrecision.doubleToString(lszre,2,1,true) + " 元",buffer, 0,lszre, true))
        {
        	// 恢复数据
            saleGoods.setElementAt(oldGoodsDef, index);
            
        	return false;
        }

        // 得到折让额
        lszre = Double.parseDouble(buffer.toString());
        
        // 
        saleGoodsDef.lszre = ManipulatePrecision.doubleConvert(lszre,2,1);
        if (getZZK(saleGoodsDef) > maxzre)
        {
            saleGoodsDef.lszre -= getZZK(saleGoodsDef) - maxzre;
            saleGoodsDef.lszre = ManipulatePrecision.doubleConvert(saleGoodsDef.lszre,2,1);
        }
        if (saleGoodsDef.lszre < 0) saleGoodsDef.lszre = 0;
        saleGoodsDef.lszre = getConvertRebate(index, saleGoodsDef.lszre);

        // 重算商品折扣合计
        getZZK(saleGoodsDef);
        
        // 重算小票应收
        calcHeadYsje();
        
        return true;    	
    }
    
//	退差价
//    public void execCustomKey3(boolean keydownonsale){
//
//		// 检查发票是否打印完,打印完未设置新发票号则不能交易
//		if (Printer.getDefault().getSaleFphmComplate()) { return; }
//
//		// 已经是指定小票退货状态,再次按退货键则重新输入原小票信息
//		if (SellType.isJS(this.saletype))
//		{
//		}
//		else if (isSpecifyTicketBack())
//		{
//			RetSYJForm frm = new RetSYJForm();
//
//			int done = frm.open(thSyjh, thFphm);
//
//			if (done == frm.Done)
//			{
//				thSyjh = RetSYJForm.syj;
//				thFphm = Long.parseLong(RetSYJForm.fph);
//
//				if (this.saletype.equals(SellType.PREPARE_BACK))
//				{
//					isbackticket = findPreSaleInfo();
//				}
//				else
//				{
//					isbackticket = findBackTicketInfo();
//				}
//			}
//			else if (done == frm.Clear)
//			{
//				thSyjh = null;
//				thFphm = 0;
//			}
//			else
//			{
//				// 放弃,不修改上次输入的原收银机号和小票号
//			}
//
//			return;
//		}
//
//		// 从销售状态切换到相应的退货状态
//		if ((saleGoods.size() <= 0))
//		{
//			// 检查权限
//			thgrantuser = null;
//			if (((curGrant.privth != 'Y') && (curGrant.privth != 'T')) || (curGrant.thxe <= 0))
//			{
//				OperUserDef staff = backSellGrant();
//
//				if (staff == null) { return; }
//
//				// 本次授权
//				thgrantuser = staff;
//
//				// 记录日志
//				String log = "授权退货,小票号:" + GlobalInfo.syjStatus.fphm + ",最大退货限额:" + thgrantuser.thxe + ",授权:" + thgrantuser.gh;
//				AccessDayDB.getDefault().writeWorkLog(log);
//			}
//			else
//			{
//				thgrantuser = (OperUserDef) (GlobalInfo.posLogin.clone());
//
//				if (cursqktype == '1')
//				{
//					thgrantuser.gh = cursqkh;
//				}
//				else
//				{
//					thgrantuser.gh = GlobalInfo.posLogin.gh;
//				}
//			}
//
//			// 提示退货权限
//			if (curGrant.privth != 'T')
//			{
//				new MessageBox("授权退货,限额为 " + ManipulatePrecision.doubleToString(thgrantuser.thxe) + " 元");
//			}
//
//			// 切换到退货交易类型
////			djlbSaleToBack();
//
//			// 初始化交易
//			initOneSale(this.saletype);
//		}
//		else
//		{
//			new MessageBox("请先完成当前交易!", null, false);
//		}
    	
//		GlobalInfo.saleform.setSaleType(SellType.BACK_PRICE);
//	}


	public boolean memberGrantFinish(CustomerDef cust){
		  
        // 记录当前顾客卡
        curCustomer = cust;
        
    	// 记录到小票        	
    	saleHead.hykh = cust.code;
    	saleHead.hytype = cust.type;
    	saleHead.str4 = cust.valstr2;
    	
    	// 重算所有商品应收
    	for (int i=0;i<saleGoods.size();i++)
    	{
    		calcGoodsYsje(i);
    	}
    	
        // 计算小票应收
        calcHeadYsje();
        
        return true;
	}
	
	public boolean findGoods(String code, String yyyh, String gz)
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
					slstr.append(price);

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
				if (!isonlinegdjging)
				{
					StringBuffer pricestr = new StringBuffer();
					do
					{
						pricestr.delete(0, pricestr.length());
//						pricestr.append(price);
						pricestr.append(goodsDef.lsj);

						double min = 0.01;
						if (goodsDef.type == 'Z')
						{
							min = 0;
						}

						boolean done = true;
						if (SellType.ISBATCH(saletype))
						{
							pricestr.delete(0, pricestr.length());
							pricestr.append(price);
							done = new TextBox().open("请输入商品[" + goodsDef.inputbarcode + "]" + (goodsDef.name.trim().length() > 20 ? goodsDef.name.trim().substring(0, 19) : goodsDef.name.trim()) + "价格", "价格", "", pricestr, min, getMaxSaleGoodsMoney(), true);
						}
						else
						{
							done = new TextBox().open("请输入商品[" + goodsDef.inputbarcode + "]" + (goodsDef.name.trim().length() > 20 ? goodsDef.name.trim().substring(0, 19) : goodsDef.name.trim()) + "价格", "价格", "", pricestr, min, getMaxSaleGoodsMoney(), true);
						}
						
						if (!done)
						{
							return false;
						}
						else
						{
							price = ManipulatePrecision.doubleConvert(getConvertPrice(Double.parseDouble(pricestr.toString()), goodsDef), 2, 1);

							// 检查价格
							if (price <= 0 && goodsDef.type != 'Z')
							{
								new MessageBox("该商品价格必须大于0");
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
	
	public boolean inputPrice(int index){
		if (SellType.ISCOUPON(saletype))
		{
			new MessageBox("买卷退卷不允许修改价格");
			return false;
		}
		return super.inputPrice(index);
	}
	
//	会员授权
	public boolean memberGrant()
	{
		if (isPreTakeStatus())
		{
			new MessageBox("预售提货状态下不允许重新刷卡");
			return false;
		}

		// 会员卡必须在商品输入前,则输入了商品以后不能刷卡,指定小票除外
		if (GlobalInfo.sysPara.customvsgoods == 'A' && saleGoods.size() > 0 && !isNewUseSpecifyTicketBack(false))
		{
			new MessageBox("必须在输入商品前进行刷会员卡\n\n请把商品清除后再重刷卡");
			return false;
		}
				
		// 读取会员卡
		HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();
		String track2 = bs.readMemberCard();
		if (track2 == null || track2.equals("")) return false;

		// 查找会员卡
		CustomerDef cust = bs.findMemberCard(track2);
		
		if (cust == null) return false;

		// 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
		if (isNewUseSpecifyTicketBack(false))
		{
			// 指定小票退仅记录卡号,不执行商品重算等处理
			curCustomer = cust;
			saleHead.hykh = cust.code;
			saleHead.hytype = cust.type;
			saleHead.str4 = cust.valstr2;
			return true;
		}
		else
		{
			// 记录会员卡
			return memberGrantFinish(cust);
		}
	}
	
	public void getVIPZK(int index, int type)
	{
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

		// 削价商品和赠品不计算
		if ((saleGoodsDef.flag == '3') || (saleGoodsDef.flag == '1')) { return; }

		// 不为VIP折扣的商品不重新计算会员折扣额
		if (goodsDef.isvipzk == 'N') return;
		
		// 折扣门槛
		if (saleGoodsDef.hjje == 0 || ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - saleGoodsDef.lszke - saleGoodsDef.lszre - saleGoodsDef.lszzk - saleGoodsDef.lszzr)/saleGoodsDef.hjje) < GlobalInfo.sysPara.vipzklimit)
			return ;

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
						new MessageBox("此商品存在促销价，但是商品数量[" + saleGoodsDef.sl + "]超出数量限额【" + ManipulatePrecision.doubleConvert(max - used - q)
								+ "】\n 强制将商品数量修改为【" + ManipulatePrecision.doubleConvert(max - used - q) + "】参与促销价");
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
				saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert((saleGoodsDef.jg - price) * saleGoodsDef.sl);
				saleGoodsDef.hyzke = getConvertRebate(index, saleGoodsDef.hyzke);
				saleGoodsDef.str1 = popDef.jsrq;
				info.char1 = 'Y';
			}
		}

		// 已计算了商品会员促销价，不再继续VIP折扣
		if (info.char1 == 'Y') return;

		if (goodsDef.isvipzk == 'Y' || goodsDef.isvipzk == 'C')
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

		//存在会员卡， 商品允许VIP折扣， CRM促销单允许享用VIP折扣
		if (checkMemberSale() && curCustomer != null && (goodsDef.isvipzk == 'Y' || goodsDef.isvipzk == 'C') && vipzk && curCustomer.iszk == 'Y')
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
						saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert((1 - goodsDef.hyj) * (saleGoodsDef.hjje - getZZK(saleGoodsDef)), 2, 1);
					}
					else
					{
						// 商品不折上折时，取商品的hyj和综合折扣较低者
						if (ManipulatePrecision.doubleCompare(saleGoodsDef.hjje - getZZK(saleGoodsDef), goodsDef.hyj * saleGoodsDef.hjje, 2) > 0)
						{
							double zke = ManipulatePrecision.doubleConvert((1 - goodsDef.hyj) * saleGoodsDef.hjje, 2, 1);
							if (zke > getZZK(saleGoodsDef))
							{
								saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert(zke - getZZK(saleGoodsDef), 2, 1);
							}
						}
					}
				}
				else
				{
					//无折扣,按商品缺省会员折扣打折
					saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert((1 - goodsDef.hyj) * saleGoodsDef.hjje, 2, 1);
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
					vipzsz = ManipulatePrecision.doubleConvert((1 - goodsDef.hyj) * (saleGoodsDef.hjje - getZZK(saleGoodsDef) - fte), 2, 1);	
				}
				else if (GlobalInfo.sysPara.vipCalcType.equals("1"))
				{
					// 当前折扣如果高于门槛则还可以进行VIP折上折,否则VIP不能折上折
					if (getZZK(saleGoodsDef) > 0 && zszflag
							&& ManipulatePrecision.doubleCompare(saleGoodsDef.hjje - getZZK(saleGoodsDef), saleGoodsDef.hjje * curCustomer.value3, 2) >= 0)
					{
						vipzsz = ManipulatePrecision.doubleConvert((1 - curCustomer.zkl) * (saleGoodsDef.hjje - getZZK(saleGoodsDef) - fte), 2, 1);
					}

					// 如果VIP折上折以后的成交价 高于 该商品定义的VIP会员折扣率，则商品以商品定义的折扣执行VIP折
					double spvipcjj = ManipulatePrecision.doubleConvert(goodsDef.hyj * (saleGoodsDef.hjje - fte), 2, 1);
					if (ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef) - fte - vipzsz, 2, 1) > spvipcjj)
					{
						vipzsz = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef) - fte - spvipcjj);
					}
				}
				
				saleGoodsDef.hyzke = vipzsz;
			}

			// 按价格精度计算折扣
			saleGoodsDef.hyzke = getConvertRebate(index, saleGoodsDef.hyzke);
		}

		getZZK(saleGoodsDef);
	}
	
	public boolean backToInit(PayModeDef pay)
	{
		return true;
	}
	
	public boolean doRefundEvent()
    {	
    	if (!SellType.ISBACK(saletype)) return true;
    	
//    	if(curCustomer == null) return true;
  	
    	if (GlobalInfo.sysPara.refundByPos == 'N') return true;
    	
    	if (!GlobalInfo.isOnline)
    	{
    		if (isNewUseSpecifyTicketBack())
    		{
	    		new MessageBox("必须在联网状态下检查退货扣回！");
	    		return false;
    		}
    		else
    		{
    			return true;
    		}
    	}
    	
    	//isRefundPayStatus = true;
    	//String ss = null;
    	//if (ss.equals("AA")) return true;
    	
    	// 清除扣回付款集合
    	if (refundPayment == null) refundPayment = new Vector();
    	else refundPayment.clear();
    	if (refundAssistant == null) refundAssistant = new Vector();
    	else refundAssistant.clear();

    	// 获取需要扣回的金额 
    	ProgressBox pb = new ProgressBox();
    	char bc = saleHead.bc;
    	try
    	{
    		saleHead.bc = '#';
	    	// 发送当前退货小票到后台数据库
    		pb.setText("正在发送退货小票用于计算扣回金额......");
	        if (!this.saleEvent.saleBS.saleSummary())
	        {
	            new MessageBox("交易数据汇总失败!");
	        	
	        	return false;
	        }
	        if (!AccessDayDB.getDefault().checkSaleData(saleHead, saleGoods, salePayment))
	        {
	            new MessageBox("交易数据校验错误!");
	
	            return false;
	        }
	        
	        // 发送当前退货小票以计算扣回
        	// jdfhdd标记当前发送的是用于计算扣回的小票信息
        	String oldfhdd = saleHead.jdfhdd;
        	saleHead.jdfhdd = "KHINV";	        
	        if (GlobalInfo.sysPara.refundByPos == 'B')
	        {
		    	if (DataService.getDefault().doRefundExtendSaleData(saleHead, saleGoods, salePayment, null) != 0)
				{
		    		saleHead.jdfhdd = oldfhdd;
		    		return false;
				}
	        }
	        else
	        {
	        	// = 'Y',扣回在付款前进行处理，生成缺省付款便于发送小票
	        	Vector tempPay = new Vector();
	        	SalePayDef tempsp = new SalePayDef();
	        	tempsp.syjh = saleHead.syjh;				
	        	tempsp.fphm = saleHead.fphm;	
	        	tempsp.rowno= 1;
	        	tempsp.flag = '1';
	        	tempsp.paycode = "KHFK";
	        	tempsp.payname = "扣回虚拟付款";
	        	tempsp.ybje = saleHead.ysje;
	        	tempsp.hl = 1;
	        	tempsp.je = saleHead.ysje;
	        	tempPay.add(tempsp);
	        	if (DataService.getDefault().doRefundExtendSaleData(saleHead, saleGoods, tempPay, null) != 0)
				{
		    		saleHead.jdfhdd = oldfhdd;
		    		return false;
				}
	        }
	        
	        
	        saleHead.jdfhdd = oldfhdd;
	        
	    	// 调用后台过程返回需要扣回的金额
	    	pb.setText("正在获取退货小票的扣回金额......");
	    	RefundMoneyDef rmd = new RefundMoneyDef();
	    	if (!NetService.getDefault().getRefundMoney(saleHead.mkt,saleHead.syjh,saleHead.fphm,rmd))
			{
	    		return false;
			}
	    	
	    	// 关闭提示
    		if (pb != null)
    		{
	    		pb.close();
	    		pb = null;
    		}
    		
    		// 存在家电下乡返款扣回，不允许退货
    		if (rmd.jdxxfkje > 0) 
    		{
    			new MessageBox("该退货小票存在家电下乡返款\n请退返款之后再进行退货交易");
    			return false;
    		}
    		
	    	// 无扣回金额,不用输入
	    	refundTotal = rmd.jfkhje + rmd.fqkhje + rmd.qtkhje;
	    	
	    	// 员工缴费和结算单如果存在扣回，不允许通过
	    	if ((SellType.isJF(saletype) || SellType.isJS(saletype)) && Math.abs(refundTotal) > 0)
	    	{
	    		new MessageBox("员工缴费 或 结算单 不允许存在扣回\n");
	    		return false;
	    	}
	    	
	    	//liwj test
	    	/*refundTotal = 1;*/
	    	
//	    	if (refundTotal <= 0) return true;
	    	
	    	StringBuffer s = new StringBuffer();
	    	if (refundTotal > 0)
	    	{
	    		s.append("该退货小票总共需要扣回 " + ManipulatePrecision.doubleToString(refundTotal) + " 元\n\n");
	    	}
	    	if (SellType.ISCOUPON(saletype) && SellType.ISBACK(saletype))
	    	{
	    		if (refundlist == null ) refundlist = new Vector();
	    		else refundlist.removeAllElements();
	    		
	    		String[] rows = rmd.qtdesc.split("\\|");
	    		for (int i = 0 ; i < rows.length; i++)
	    		{
	    			String row[] = rows[i].split(",");
	    			refundlist.add(row);
	    			s.append(Convert.appendStringSize("", row[1], 0, 15, 10)+" :"+Convert.increaseCharForward(row[2],10)+"\n");
	    		}
	    	}
	    	else {
		    	if (rmd.jfdesc.trim().length() > 0) s.append(rmd.jfdesc + "\n");
		    	else if (rmd.jfkhje > 0) s.append("其中因为积分原因需扣回 " + ManipulatePrecision.doubleToString(rmd.jfkhje) + " 元\n");
		    	if (rmd.fqdesc.trim().length() > 0) s.append(rmd.fqdesc + "\n");
		    	else if (rmd.fqkhje > 0) s.append("其中因为返券原因需扣回 " + ManipulatePrecision.doubleToString(rmd.fqkhje) + " 元\n");
		    	if (rmd.qtdesc.trim().length() > 0) s.append(rmd.qtdesc + "\n");
		    	else if (rmd.qtkhje > 0) s.append("其中因为其他原因需扣回 " + ManipulatePrecision.doubleToString(rmd.qtkhje) + " 元\n");
	    	}
	    	// 有扣回不允许退货
	    	if (GlobalInfo.sysPara.refundAllowBack != 'Y' && refundTotal > 0)
	    	{
	    		s.append("\n扣回金额大于0,不能进行退货\n");
	    		refundMessageBox(s.toString());
	    		
	    		return false;
	    	}
	    	
	    	if(s.toString().trim().length() > 0)refundMessageBox(s.toString());
    	}
    	catch(Exception er)
    	{
    		er.printStackTrace();
    	}
    	finally
    	{
    		saleHead.bc = bc;
    		if (pb != null)
    		{
	    		pb.close();
	    		pb = null;
    		}
    	}
    	
    	// 标记扣回开始
    	refundFinish = false;
    	isRefundPayStatus = true;
    	
    	// 打开扣回付款输入窗口
    	new SalePayForm().open(saleEvent.saleBS,true);
    	

    	
    	isRefundPayStatus = false;
	    return refundFinish;
    }
	
	
	public boolean saleFinishDone(Label status, StringBuffer waitKeyCloseForm)
	{
		try
		{
			// 如果没有连接打印机则连接
			if (GlobalInfo.sysPara.issetprinter == 'Y' && GlobalInfo.syjDef.isprint == 'Y' && Printer.getDefault() != null && !Printer.getDefault().getStatus())
			{
				Printer.getDefault().open();
				Printer.getDefault().setEnable(true);
			}

			// 标记最后交易完成方法已开始，避免重复触发
			if (!waitlab)
				waitlab = true;
			else
				return false;

			// 输入小票附加信息
			if (!inputSaleAppendInfo())
			{
				new MessageBox("小票附加信息输入失败,不能完成交易!");
				return false;
			}

			//
			setSaleFinishHint(status, "正在汇总交易数据,请等待.....");
			if (!saleSummary())
			{
				new MessageBox("交易数据汇总失败!");

				return false;
			}

			//
			setSaleFinishHint(status, "正在校验数据平衡,请等待.....");
			if (!AccessDayDB.getDefault().checkSaleData(saleHead, saleGoods, salePayment))
			{
				new MessageBox("交易数据校验错误!");

				return false;
			}

			// 最终效验
			if (!checkFinalStatus()) { return false; }

			// 不是练习交易数据写盘
			if (!SellType.ISEXERCISE(saletype))
			{
				// 输入顾客信息
				setSaleFinishHint(status, "正在输入客户信息,请等待......");
				selectAllCustomerInfo();

				//
				setSaleFinishHint(status, "正在打开钱箱,请等待.....");
				CashBox.getDefault().openCashBox();

				//
				setSaleFinishHint(status, "正在记账付款数据,请等待.....");
				if (!saleCollectAccountPay())
				{
					new MessageBox("付款数据记账失败\n\n稍后将自动发起已记账付款的冲正!");

					// 记账失败,及时把冲正发送出去
					setSaleFinishHint(status, "正在发送冲正数据,请等待.....");
					CreatePayment.getDefault().sendAllPaymentCz();

					return false;
				}

				setSaleFinishHint(status, "正在写入交易数据,请等待......");
				if (!AccessDayDB.getDefault().writeSale(saleHead, saleGoods, salePayment))
				{
					new MessageBox("交易数据写盘失败!");
					AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票,金额:" + saleHead.ysje + ",发生数据写盘失败", StatusType.WORK_SENDERROR);

					// 记账失败,及时把冲正发送出去
					setSaleFinishHint(status, "正在发送冲正数据,请等待.....");
					CreatePayment.getDefault().sendAllPaymentCz();

					return false;
				}

				// 小票已写盘,本次交易就要认为完成,即使后续处理异常也要返回成功
				saleFinish = true;

				// 小票保存成功以后，及时清除断点
				setSaleFinishHint(status, "正在清除断点保护数据,请等待......");
				clearBrokenData();

				//
				setSaleFinishHint(status, "正在清除付款冲正数据,请等待......");
				if (!saleCollectAccountClear())
				{
					AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票清除冲正数据失败,但小票已成交保存", StatusType.WORK_SENDERROR);

					new MessageBox("小票已成交保存,但清除冲正数据失败\n\n请完成本笔交易后重启款机尝试删除记账冲正数据!");
				}

				// 处理交易完成后一些后续动作
				doSaleFinshed(saleHead, saleGoods, salePayment);

				// 上传当前小票
				setSaleFinishHint(status, "正在上传交易小票数据,请等待......");
				boolean bsend = GlobalInfo.isOnline;
				if (!DataService.getDefault().sendSaleData(saleHead, saleGoods, salePayment))
				{
					// 联网时发送小票却失败才记录日志
					if (bsend)
					{
						AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票,金额:" + saleHead.ysje + ",联网销售时小票送网失败", StatusType.WORK_SENDERROR);
					}
				}
				
				//用于调用DOSPOS后台扣减积分，金额（客户化添加）
				 if(!SellType.ISSALE(saletype) &&(GlobalInfo.sysPara.refundByPos == 'B' || GlobalInfo.sysPara.refundByPos == 'Y'))
				 {
					 String khbz = "#";//扣回标志,对应传给到后台jygs。后台需要调用两遍，通过它判断是否扣积分或金额
					 ((Jnyz_NetService)NetService.getDefault()).getRefundMoney(saleHead.mkt,saleHead.syjh,saleHead.fphm,khbz);

				 }

				// 发送当前收银状态
				setSaleFinishHint(status, "正在上传收银机交易汇总,请等待......");
				DataService.getDefault().sendSyjStatus();

				// 打印小票
				setSaleFinishHint(status, "正在打印交易小票,请等待......");
				printSaleBill();
			}
			else
			{
				if (GlobalInfo.sysPara.lxprint == 'Y')
				{
					// 打印小票
					setSaleFinishHint(status, "正在打印交易小票,请等待......");
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
			setSaleFinishHint(status, "正在等待关闭钱箱,请等待......");
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
			setSaleFinishHint(status, "本笔交易结束,开始新交易");

			// 标记本次交易已完成
			saleFinish = true;

			return saleFinish;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			new MessageBox("完成交易时发生异常:\n\n" + ex.getMessage());

			return saleFinish;
		}
	}
	
	public boolean payAccount(PayModeDef mode, String money)
	{
		if(curCustomer == null && mode.code.equals("0508"))
		{
			new MessageBox("使用积分折现付款方式，请先刷会员卡！");
			return false;
		}
		/*
		if(mode.code.equals("7505"))
		{
			return false;
		}*/
		
		return super.payAccount(mode, money);
	}
	
	
	public void calcVIPZK(int index)
	{
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);

		// 未刷卡
		if (!checkMemberSale() || curCustomer == null) return;

		/*// 非零售开票
		if (!saletype.equals(SellType.RETAIL_SALE) && !saletype.equals(SellType.PREPARE_SALE))
		{
			goodsDef.hyj = 1;
			return;
		}*/

		// 查询商品VIP折上折定义
		GoodsPopDef popDef = new GoodsPopDef();
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
	
	
	//获取退货小票信息
	public boolean findBackTicketInfo()
	{
		SaleHeadDef thsaleHead = null;
		Vector thsaleGoods = null;
		Vector thsalePayment = null;

		try
		{
			if(GlobalInfo.sysPara.inputydoc == 'D')
			{
				//只记录原单小票号和款机号,但不按原单找商品				
				return false;
			}
			
			// 如果是新指定小票进入
			if (saletype.equals(SellType.JDXX_BACK) || ((GlobalInfo.sysPara.inputydoc == 'A' || GlobalInfo.sysPara.inputydoc == 'C') && ((saleGoods.size() > 0 && isbackticket) || saleGoods.size() < 1)))
			{
				thsaleHead = new SaleHeadDef();
				thsaleGoods = new Vector();
				thsalePayment = new Vector();

				// 联网查询原小票信息
				ProgressBox pb = new ProgressBox();
				pb.setText("开始查找退货小票操作.....");
				if (!DataService.getDefault().getBackSaleInfo(thSyjh, String.valueOf(thFphm), thsaleHead, thsaleGoods, thsalePayment))
				{
					pb.close();
					pb = null;

					thSyjh = null;
					thFphm = 0;

					return false;
				}

				pb.close();
				pb = null;
				//检查小票是否有满赠礼品，顾客退货，需要先退回礼品，再到收银台办理退货
				//Y为已在后台退回礼品   津乐会赠品退货
				if ((thsaleHead.str2.trim().equals("Y"))){
					new MessageBox("此小票有满赠礼品，请先到后台退回礼品再办理退货！");
					return false;
				}
				// 检查此小票是否已经退货过，给出提示ADD by lwj
				if (thsaleHead.str1.trim().length() > 0)
				{
					if (new MessageBox(thsaleHead.str1 + "\n是否继续退货？", null, true).verify() != GlobalVar.Key1) { return false; }
				}
				// 原交易类型和当前退货类型不对应，不能退货
				// 如果原交易为预售提货，不判断
				// 如果当前交易类型为家电退货,那么可以支持零售销售的退货
				if (!thsaleHead.djlb.equals(SellType.PREPARE_TAKE))
				{
					if (!SellType.getDjlbSaleToBack(thsaleHead.djlb).equals(this.saletype))
					{
						new MessageBox("原小票是[" + SellType.getDefault().typeExchange(thsaleHead.djlb, thsaleHead.hhflag, thsaleHead) + "]交易\n\n与当前退货交易类型不匹配");

						// 清空原收银机号和原小票号
						thSyjh = null;
						thFphm = 0;
						return false;
					}
				}

				// 显示原小票商品明细
				Vector choice = new Vector();
				String[] title = { "序", "商品编码", "商品名称", "原数量", "原折扣", "原成交价", "退货", "退货数量" };
				int[] width = { 30, 100, 170, 80, 80, 100, 60, 100, 55 };
				String[] row = null;
				for (int i = 0; i < thsaleGoods.size(); i++)
				{
					SaleGoodsDef sgd = (SaleGoodsDef) thsaleGoods.get(i);
					row = new String[8];
					row[0] = String.valueOf(sgd.rowno);

					if (sgd.inputbarcode.equals(""))
					{
						if (GlobalInfo.sysPara.backgoodscodestyle.equalsIgnoreCase("A"))
							sgd.inputbarcode = sgd.barcode;
							row[1] = sgd.barcode;
						if (GlobalInfo.sysPara.backgoodscodestyle.equalsIgnoreCase("B"))
							sgd.inputbarcode = sgd.code;
							row[1] = sgd.code;
					}
					else
					{
						row[1] = sgd.inputbarcode;
					}

					row[2] = sgd.name;
					row[3] = ManipulatePrecision.doubleToString(sgd.sl, 4, 1, true);
					row[4] = ManipulatePrecision.doubleToString(sgd.hjzk);
					row[5] = ManipulatePrecision.doubleToString(sgd.hjje - sgd.hjzk);
					row[6] = "";
					row[7] = "";
					choice.add(row);
				}

				String[] title1 = { "序", "付款名称", "账号", "付款金额" };
				int[] width1 = { 30, 100, 250, 180 };
				String[] row1 = null;
				Vector content2 = new Vector();
				int j = 0;
				for (int i = 0; i < thsalePayment.size(); i++)
				{
					SalePayDef spd1 = (SalePayDef) thsalePayment.get(i);
					row1 = new String[4];
					row1[0] = String.valueOf(++j);
					row1[1] = String.valueOf(spd1.payname);
					row1[2] = String.valueOf(spd1.payno);
					row1[3] = ManipulatePrecision.doubleToString(spd1.je);
					content2.add(row1);
				}

				int cho = -1;
				if (EBill.getDefault().isEnable() && EBill.getDefault().isBack())
				{
					cho= EBill.getDefault().getChoice(choice);
				}
				else{
					// 选择要退货的商品
					cho= new MutiSelectForm().open("在以下窗口输入单品退货数量(回车键选择商品,付款键全选,确认键保存退出)", title, width, choice, true, 780, 480, 750, 220, true, true, 7, true, 750, 130, title1, width1, content2, 0);
				}

				StringBuffer backYyyh=new StringBuffer();
				if(GlobalInfo.sysPara.backyyyh =='Y'){
					new TextBox().open("开单营业员号：","", "请输入有效开单营业员号",backYyyh, 0);
//					 查找营业员
					OperUserDef staff = null;
					if(backYyyh.length() != 0){
						if ((staff = findYYYH(backYyyh.toString())) != null)
						{
							if (staff.type != '2')
							{
								new MessageBox("该工号不是营业员!", null, false);
								return false;
							}
						}else{
							return false;
						}
					}else{
						return false;
					}
					
				}
				
				// 如果cho小于0且已经选择过退货小票
				if (cho < 0 && isbackticket)
					return true;
				if (cho < 0)
				{
					thSyjh = null;
					thFphm = 0;
					return false;
				}

				// 清除已有商品明细,重新初始化交易变量

				// 将退货授权保存下来
				String thsq = saleHead.thsq;
				initSellData();

				// 生成退货商品明细
				for (int i = 0; i < choice.size(); i++)
				{
					row = (String[]) choice.get(i);
					if (!row[6].trim().equals("Y"))
						continue;

					SaleGoodsDef sgd = (SaleGoodsDef) thsaleGoods.get(i);
					double thsl = ManipulatePrecision.doubleConvert(Convert.toDouble(row[7]), 4, 1);

					sgd.yfphm = sgd.fphm;
					sgd.ysyjh = sgd.syjh;
					sgd.yrowno = sgd.rowno;
					sgd.memonum1 = sgd.sl;
					sgd.syjh = ConfigClass.CashRegisterCode;
					sgd.fphm = GlobalInfo.syjStatus.fphm;
					sgd.rowno = saleGoods.size() + 1;
					sgd.str4 = backYyyh.toString();
					sgd.ysl = sgd.sl;

					// 重算商品行折扣
					if (ManipulatePrecision.doubleCompare(sgd.sl, thsl, 4) > 0)
					{
						sgd.hjje = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.hjje, sgd.sl), thsl), 2, 1); // 合计金额
						sgd.hyzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.hyzke, sgd.sl), thsl), 2, 1); // 会员折扣额(来自会员优惠)
						sgd.yhzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.yhzke, sgd.sl), thsl), 2, 1); // 优惠折扣额(来自营销优惠)
						sgd.lszke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszke, sgd.sl), thsl), 2, 1); // 零时折扣额(来自手工打折)
						sgd.lszre = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszre, sgd.sl), thsl), 2, 1); // 零时折让额(来自手工打折)
						sgd.lszzk = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszzk, sgd.sl), thsl), 2, 1); // 零时总品折扣
						sgd.lszzr = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszzr, sgd.sl), thsl), 2, 1); // 零时总品折让
						sgd.plzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.plzke, sgd.sl), thsl), 2, 1); // 批量折扣
						sgd.zszke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.zszke, sgd.sl), thsl), 2, 1); // 赠送折扣
						sgd.cjzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.cjzke, sgd.sl), thsl), 2, 1); // 厂家折扣
						sgd.ltzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.ltzke, sgd.sl), thsl), 2, 1);
						sgd.hyzklje = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.hyzklje, sgd.sl), thsl), 2, 1);
						sgd.qtzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.qtzke, sgd.sl), thsl), 2, 1);
						sgd.qtzre = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.qtzre, sgd.sl), thsl), 2, 1);
						sgd.hjzk = getZZK(sgd);
						sgd.sl = thsl;
					}

					// 加入商品列表
					addSaleGoodsObject(sgd, null, new SpareInfoDef());
				}

				// 查找原交易会员卡资料
				if (thsaleHead.hykh != null && !thsaleHead.hykh.trim().equals(""))
				{
					curCustomer = new CustomerDef();
					curCustomer.code = thsaleHead.hykh;
					curCustomer.name = thsaleHead.hykh;
					curCustomer.ishy = 'Y';
					curCustomer.str2 = "1";//卡号查询

					
					  //业务过程只支持磁道查询,不支持卡号查询,因此无法检查原交易会员卡是否有效 
					if(!DataService.getDefault().getCustomer(curCustomer,thsaleHead.hykh)) 
						{
							curCustomer.code = thsaleHead.hykh;
							curCustomer.name = "无效卡"; 
							curCustomer.ishy = 'Y';
					
					  		new MessageBox("原交易的会员卡可能已失效!\n请重新刷卡后进行退货"); 
						}
					 
				}

				// 设置原小票头信息
				saleHead.hykh = thsaleHead.hykh;
				saleHead.hytype = thsaleHead.hytype;
				saleHead.jfkh = thsaleHead.jfkh;

				saleHead.thsq = thsq;
				saleHead.ghsq = thsaleHead.ghsq;
				saleHead.hysq = thsaleHead.hysq;
				saleHead.sqkh = thsaleHead.sqkh;
				saleHead.sqktype = thsaleHead.sqktype;
				saleHead.sqkzkfd = thsaleHead.sqkzkfd;
				saleHead.hhflag = hhflag;
				saleHead.jdfhdd = thsaleHead.jdfhdd;
				saleHead.salefphm = thsaleHead.salefphm;
				
				
				saleHead.fk_sysy = thsaleHead.fk_sysy;
				saleHead.sswr_sysy = thsaleHead.sswr_sysy;

				// 退货小票辅助处理
				takeBackTicketInfo(thsaleHead, thsaleGoods, thsalePayment);

				// 重算小票头
				calcHeadYsje();

				// 为了写入断点,要在刷新界面之前置为true
				isbackticket = true;

				// 检查是否超出退货限额
				if (curGrant.thxe > 0 && saleHead.ysje > curGrant.thxe)
				{
					OperUserDef staff = backSellGrant();
					if (staff == null)
					{
						initSellData();
						isbackticket = false;
					}
					else
					{
						if (staff.thxe > 0 && saleHead.ysje > staff.thxe)
						{
							new MessageBox("超出退货的最大限额，不能退货");

							initSellData();
							isbackticket = false;
						}
						else
						{
							// 记录日志
							saleHead.thsq = staff.gh;
							curGrant.privth = staff.privth;
							curGrant.thxe = staff.thxe;

							String log = "授权退货,小票号:" + saleHead.fphm + ",最大退货限额:" + curGrant.thxe + ",授权:" + staff.gh;
							AccessDayDB.getDefault().writeWorkLog(log);

							//
							new MessageBox("授权退货,限额为 " + ManipulatePrecision.doubleToString(curGrant.thxe) + " 元");
						}
					}
				}

				backPayment.removeAllElements();
				backPayment.addAll(thsalePayment);

				// 刷新界面显示
				saleEvent.clearTableItem();
				saleEvent.updateSaleGUI();

				return isbackticket;
			}
			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (thsaleHead != null)
			{
				thsaleHead = null;
			}

			if (thsaleGoods != null)
			{
				thsaleGoods.clear();
				thsaleGoods = null;
			}

			if (thsalePayment != null)
			{
				thsalePayment.clear();
				thsalePayment = null;
			}
		}
	}
	
	public void quickPayButton(int key)
	{
		//改购物中心程序时候屏蔽，2014年8月15日11:02:54：maxun
		/*if(GlobalInfo.posLogin.funcmenu.indexOf("0301") == -1 || !GlobalInfo.posLogin.role.equals("05"))
		{
			
			OperUserDef staff = DataService.getDefault().personGrant();

				if (staff == null || !staff.role.equals("05")) { return;}

				// 本次授权
				thgrantuser = staff;

				// 记录日志
				String log = "授权查询面值卡余额,授权:" + thgrantuser.gh;
				AccessDayDB.getDefault().writeWorkLog(log);
			
		}
		
			// 如果没有定义面值卡查询键,将面值卡付款键作为面值卡查询键使用
			if ((key == GlobalVar.PayMzk) && (NewKeyListener.searchKeyCode(GlobalVar.MzkInfo) <= 0))
			{
				MzkInfoQueryBS mzk = CustomLocalize.getDefault().createMzkInfoQueryBS();
				mzk.QueryMzkInfo();
				mzk = null;
			}
			else
			{
				sendQuickPayButton(key);
			}*/
		
//		 如果没有定义面值卡查询键,将面值卡付款键作为面值卡查询键使用
		if ((key == GlobalVar.PayMzk) && (NewKeyListener.searchKeyCode(GlobalVar.MzkInfo) <= 0))
		{
			if(GlobalInfo.posLogin.funcmenu.indexOf("0301") == -1 || !GlobalInfo.posLogin.role.equals("05"))
			{
				
					OperUserDef staff = DataService.getDefault().personGrant();

					if (staff == null || !staff.role.equals("05")) { new MessageBox("授权失败！"); return;}

					// 本次授权
					thgrantuser = staff;

					// 记录日志
					String log = "授权查询面值卡余额,授权:" + thgrantuser.gh;
					AccessDayDB.getDefault().writeWorkLog(log);
				
			}
				MzkInfoQueryBS mzk = CustomLocalize.getDefault().createMzkInfoQueryBS();
				mzk.QueryMzkInfo();
				mzk = null;
		}
		else
		{
			sendQuickPayButton(key);
		}
	}

	//打印网上小票
	public void getReceipt() {
		if (getReprintAuth())//(me.verify() == GlobalVar.Key1 && getReprintAuth())
		{
			//Object obj = null;
			String yfphm = null;
			String ysyjh = null;

			/*if (curGrant.privdy != 'Y' && curGrant.privdy != 'L')
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
*/
			//新增默认上一笔的小票号、收银机号
			RetSYJForm frm = new RetSYJForm();
			int done = frm.open(null, -1, Language.apply("请输入【重印】收银机号和小票号"));
			if (done == frm.Done)
			{
				ysyjh = RetSYJForm.syj;
				yfphm = String.valueOf(RetSYJForm.fph);
			}
			else
			{
				// 放弃重打印
				return;
			}

			//if ((obj = GlobalInfo.dayDB.selectOneData("select max(fphm) from salehead where syjh = '" + syjh + "'")) != null)
			//{
			
			SaleHeadDef salehead = new SaleHeadDef();
			Vector salegoods = new Vector();
			Vector salepay = new Vector();
			boolean issq = ((Jnyz_DataService)DataService.getDefault()).getReceipt(ysyjh, yfphm,salehead);
			
			if(issq)
			{
				if (curGrant.privdy != 'Y')
				{
					OperUserDef user = null;
					if ((user = DataService.getDefault().personGrant(Language.apply("授权重打印小票"))) != null)
					{
						if (user.privdy != 'Y')
						{
							new MessageBox(Language.apply("当前工号没有重打上笔小票权限!"));

							return;
						}

						String log = "授权重打印上一笔小票,授权工号:" + user.gh;
						AccessDayDB.getDefault().writeWorkLog(log);
					}
					else
					{
						new MessageBox("找不到工号！");
						return;
					}
				}
			}
			
			boolean a =((Jnyz_DataService)DataService.getDefault()).getReceipt(ysyjh, yfphm,salehead,salegoods,salepay);
			if (!a)
			{
				return;
			}
			else
			{
				salehead.str6 = ysyjh;//记录网上收银员号
				SaleBillMode.getDefault().setTemplateObject(salehead,salegoods,salepay);
				SaleBillMode.getDefault().printBill();
			}
		}
	}
	
	public boolean checkDeleteSalePay(String string, boolean isDelete)
	{
		//支付宝付款方式不允许被删除
		if(string.indexOf("[7502]") != -1 || string.indexOf("[7505]") != -1) 
			return true;
		else
			return false;
	}
	
	public boolean exitPaySell()
	{
		boolean flag  = true;
		for(int i = 0;i<salePayment.size();i++)
		{
			SalePayDef sp = (SalePayDef) salePayment.elementAt(i);
			if(sp.paycode.equals("7502") || sp.paycode.equals("7505"))flag = false;
		}
		if(!flag)
		{
			new MessageBox("支付宝的付款方式，不允许删除！"); 
			return flag;
			
		}
			
		return super.exitPaySell();
	}
	
	public boolean saleCollectAccountPay()
	{
		Payment p = null;
		boolean czsend = true;

		// 付款对象记账
		for (int i = 0; i < payAssistant.size(); i++)
		{
			p = (Payment) payAssistant.elementAt(i);
			//支付宝付款交易这里不需要记账
			if(p != null && p.paymode.code.equals("7502")) continue;
			if (p == null)
				continue;

			// 第一次记账前先检查是否有冲正需要发送
			if (czsend)
			{
				czsend = false;
				if (!p.sendAccountCz())
					return false;
			}

			// 付款记账
			if (!p.collectAccountPay())
				return false;
		}

		// 移动充值对象记账
		if (GlobalInfo.useMobileCharge && !mobileChargeCollectAccount(true))
			return false;

		return true;
	}
	
	//查询支付宝单据金额
	public void execCustomKey0(boolean keydownonsale)
	{
		String fphm = null;
		String syjh = null;
		RetSYJForm frm = new RetSYJForm();
		int done = frm.open(null, -1, Language.apply("请输入需要查询单据的收银机号和小票号"));
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
		double zfbje = ((Jnyz_DataService)DataService.getDefault()).getZfbJe(syjh, fphm);
		new MessageBox("收银机号："+syjh+"\n"+" 小 票 号："+fphm+"\n"+"金    额："+zfbje);
	}
	
//	 设置付款金额输入框的缺省值
	public void setMoneyInputDefault(Text txt, PayModeDef paymode)
	{
		if(paymode.code.equals("7505"))
		{
			txt.setText("");
			txt.setEditable(false);
		}
		else
		{
			super.setMoneyInputDefault(txt, paymode);
		}
	}
	
//	 format: [1]#@#[2]#@#[3]
	// [1]:text
	// [2]:font
	// [3]:color ex. 255_255_255
	public void sendSecMonitor(String label, String[] value, int index)
	{
		if (SecMonitor.secMonitor == null)
			return;

		if (label.equalsIgnoreCase("goods"))
		{
			String line = "";
			line = Convert.appendStringSize("", Language.apply("商品名:"), 0, 7, 90, 0);
			line = Convert.appendStringSize(line, value[2] + "[" + value[1] + "]", 7, 34, 90, 0);

			line = Convert.appendStringSize(line, Language.apply("数量:"), 44, 5, 90, 0);
			line = Convert.appendStringSize(line, value[4], 49, 6, 90, 1);

			line = Convert.appendStringSize(line, Language.apply("应付:"), 56, 5, 90, 0);
			line = Convert.appendStringSize(line, value[7], 61, 9, 90, 1);

			String line1 = "";
			line1 = Convert.appendStringSize(line1, Language.apply("会员号:"), 0, 7, 90, 0);
			line1 = Convert.appendStringSize(line1, getVipInfoLabel(), 7, 34, 90, 0);

			line1 = Convert.appendStringSize(line1, Language.apply("总量:"), 44, 5, 90, 0);
			line1 = Convert.appendStringSize(line1, getTotalQuantityLabel(), 49, 8, 90, 1);

			line1 = Convert.appendStringSize(line1, Language.apply("总付:"), 56, 5, 90, 0);
			line1 = Convert.appendStringSize(line1, getSellPayMoneyLabel(), 61, 9, 90, 1);

			line += ("#@#" + (20 + GlobalVar.secFont) + "#@#0_0_255");
			line1 += ("#@#" + (20 + GlobalVar.secFont) + "#@#255_0_0");

			SecMonitor.secMonitor.monitorShowGoodsInfo(line, line1, index);
		}
		else if (label.equalsIgnoreCase("pay") || label.equalsIgnoreCase("total"))
		{
			String line = "";
			line = Convert.appendStringSize(line, Language.apply("应付金额:"), 0, 9, 90, 0);
			line = Convert.appendStringSize(line, getSellPayMoneyLabel(), 10, 11, 90, 1);
			line = Convert.appendStringSize(line, Language.apply("已付金额:"), 25, 9, 90, 0);
			line += ("#@#" + (28 + GlobalVar.secFont));

			String line1 = "";
			line1 = Convert.appendStringSize(line1, Language.apply("未付金额:"), 0, 9, 90, 0);
			line1 = Convert.appendStringSize(line1, getPayBalanceLabel(), 10, 11, 90, 1);
			line1 += ("#@#" + (28 + GlobalVar.secFont));

			if (label.equalsIgnoreCase("pay"))
				SecMonitor.secMonitor.monitorShowPayInfo(line, line1);
			else
				SecMonitor.secMonitor.monitorShowTotalInfo(line, line1);
		}
		else if (label.equalsIgnoreCase("change"))
		{
			String line = "";
			line = Convert.appendStringSize(line, Language.apply("应付金额:"), 0, 9, 90, 0);
			line = Convert.appendStringSize(line, getSellPayMoneyLabel(), 10, 11, 90, 1);
			line = Convert.appendStringSize(line, Language.apply("已付金额:"), 25, 9, 90, 0);
			line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(saleHead.sjfk, 2, 1), 36, 11, 90, 1);
			line += ("#@#" + (28 + GlobalVar.secFont));

			String line1 = "";
			line1 = Convert.appendStringSize(line1, Language.apply("找零金额:"), 0, 9, 90, 0);
			line1 = Convert.appendStringSize(line1, ManipulatePrecision.doubleToString(saleHead.zl), 10, 11, 90, 1);
			line1 = Convert.appendStringSize(line1, Language.apply("损益金额:"), 25, 9, 90, 0);
			line1 = Convert.appendStringSize(line1, ManipulatePrecision.doubleToString(saleHead.fk_sysy), 36, 11, 90, 1);
			line1 += ("#@#" + (28 + GlobalVar.secFont) + "#@#255_0_0");

			SecMonitor.secMonitor.monitorShowChangeInfo(line, line1);
		}
		else if (label.equalsIgnoreCase("MzkYe"))
		{
			String line = "";
			line = Convert.appendStringSize(line, "面值卡卡号：", 6, 12, 90, 2);
			line = Convert.appendStringSize(line, value[0], 19, 20, 90, 0);
			String line1 = "";
			line1 = Convert.appendStringSize(line1, "面值卡余额：", 6, 12, 90, 2);
			line1 = Convert.appendStringSize(line1, value[1], 19, 15, 90, 0);
//			line1 += ("#@#" + (28 + GlobalVar.secFont));

			SecMonitor.secMonitor.monitorShowPhoneInfo(line, line1);
			new MessageBox("余额已显示第二屏！");
		}
		else
		{
			SecMonitor.secMonitor.monitorShowWelcomeInfo();
		}
	}
}
