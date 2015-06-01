package custom.localize.Bhls;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.CalcRulePopDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;
import com.efuture.javaPos.UI.Design.SalePayForm;

public class Bhls_SaleBS0RulePop extends SaleBS
{
    public String curRulePopMode = "R";				//满减满赠模式标记
    
    protected boolean haveRulePop = false;
    protected boolean doRulePopExit = false;
    protected final int payNormal = 0;			//0-无满减规则直接付款
    protected final int payPopPrepare = 1;		//1-有满减规则预付除外付款
    protected final int payPopOther = 2;		//2-有满减规则付其他付款
    protected int isPreparePay = payNormal;
    
	public void initNewSale()
	{
		super.initNewSale();
		
		//
		curRulePopMode = "R";
	}
	
	public void writeSellObjectToStream(ObjectOutputStream s) throws Exception
	{
		brokenAssistant.insertElementAt(curRulePopMode,0);

		super.writeSellObjectToStream(s);
	}
	
	public void readStreamToSellObject(ObjectInputStream s) throws Exception
	{
		super.readStreamToSellObject(s);
		
		curRulePopMode = (String)brokenAssistant.remove(0);
	}
    
    public void enterInputYYY()
    {
    	super.enterInputYYY();
    	
    	// 营业员输入成功,选择满减满赠方式
    	if (saleEvent.saleform.getFocus() != saleEvent.saleform.yyyh)
    	{
	        selectRulePopMode();
    	}
    }
    
    public void selectRulePopMode()
    {
    	if (!SellType.ISSALE(saletype))
    	{
    		curRulePopMode = "N";
    		return;
    	}
    	
    	if (GlobalInfo.sysPara.rulepop == 'Y')
    	{
    		String[] title = {Language.apply("代码"),Language.apply("描述")};
    		int[]    width = {60,400};
    		Vector contents = new Vector();
    		contents.add(new String[]{"R" ,Language.apply("同时参与满减和满增")});
    		contents.add(new String[]{"MJ",Language.apply("只参与减现促销活动")});
    		contents.add(new String[]{"MS",Language.apply("只参与满赠促销活动")});
    		contents.add(new String[]{"N" ,Language.apply("不参与任何促销活动")});
    		
    		int choice = new MutiSelectForm().open("请选择参与满减满赠活动的规则", title, width, contents);
    		
			if (choice >= 0)
			{
				curRulePopMode = ((String[])contents.elementAt(choice))[0].trim();
			}
			else
			{
				curRulePopMode = "N";
			}
    	}
    	else if (GlobalInfo.sysPara.rulepop == 'A')
    	{
    		String[] title = {Language.apply("代码"),Language.apply("描述")};
    		int[]    width = {60,400};
    		Vector contents = new Vector();
    		contents.add(new String[]{"MJ",Language.apply("只参与减现促销活动")});
    		contents.add(new String[]{"MS",Language.apply("只参与满赠促销活动")});
    		
    		int choice = new MutiSelectForm().open(Language.apply("请选择参与满减满赠活动的规则"), title, width, contents);
    		
			if (choice >= 0)
			{
				curRulePopMode = ((String[])contents.elementAt(choice))[0].trim();
			}
			else
			{
				curRulePopMode = "N";
			}    		
    	}
    	else
    	{
    		curRulePopMode = String.valueOf(GlobalInfo.sysPara.rulepop);
    	}
    }
	
    public SpareInfoDef getGoodsSpareInfo(GoodsDef goodsDef,SaleGoodsDef saleGoodsDef)
    {
    	SpareInfoDef info = super.getGoodsSpareInfo(goodsDef,saleGoodsDef);
    	
		info.str1 = curRulePopMode;		//参与规则促销方式
    	
    	return info;
    }
    
    public void paySellCancel()
    {
    	// 放弃满减满赠规则促销
    	if (haveRulePop)
    	{
    		delRulePop();
    	}
    	
    	delBHLSPop();
    	
    	super.paySellCancel();
    }
        
    // 删除满减或满增
    public void delRulePop()
    {
    	// 取消赠品及折扣
    	for (int i=0;i<saleGoods.size();i++)
    	{
    		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
    		
    		// 删除赠送的商品
    		if (saleGoodsDef.flag == '1' || goodsAssistant.elementAt(i) == null)
    		{
    			delSaleGoodsObject(i);
    			getDeleteGoodsDisplay(i,saleGoodsDef);
    			i--;
    			continue;
    		}
			
			// 退回到销售界面时，还需要删除哪些信息
			deleteGoodsInfo(saleGoodsDef);
			
			getZZK(saleGoodsDef);
    	}
    	
    	// 重算应收
    	calcHeadYsje();
    	
    	// 重刷商品列表
    	saleEvent.updateTable(getSaleGoodsDisplay());
    	saleEvent.setTotalInfo();
    }
    
    public void deleteGoodsInfo(SaleGoodsDef saleGoodsDef)
    {
    	if (saleGoodsDef == null) return;
		// 恢复满减的折扣
		saleGoodsDef.zsdjbh = null;
		saleGoodsDef.zszke	= 0;
		saleGoodsDef.zszkfd = 0;
    }
    
    public boolean getPayModeByNeed(PayModeDef paymode)
    {
    	// 无满减的实际付款，所有付款方式都可以
    	if (isPreparePay == payNormal) return true;
    	
		// 满减预先付款只先付券类付款方式
    	if (isPreparePay == payPopPrepare)
    	{
    		if (paymode.type == '5') return true;
    		else return false;
    	}

		// 满减后再付款只允许付非券类付款方式
		// 券类的付款方式必须在满减前输入完成
    	if (isPreparePay == payPopOther)
    	{
    		if (paymode.type != '5') return true;
    		else return false;
    	}
    	
    	return true;
    }
    
    
    public boolean paySellStart()
    {
    	if (!super.paySellStart()) return false;		// 不允许进行付款
    	
    	doRulePopWriteData();
    	
    	if (!paySellPop()) return false;
    	
    	return true;
    }
    
    public boolean paySellPop()
    {
    	// F,A新满减方式
    	if (GlobalInfo.sysPara.rulepop == 'F' || GlobalInfo.sysPara.rulepop == 'A' || GlobalInfo.sysPara.rulepop == 'S')
    	{
    		doRulePopExit = false;
    		
    		haveRulePop = doNewRulePop();
    		
    		if (doRulePopExit) return false;			// 不允许进行付款
    		
    		// 在前台进行满赠
    		if (GlobalInfo.sysPara.rulepop == 'S')
    		{
    			// 先找专柜满赠促销
    			if (doNewGiftPop("1")) haveRulePop = true;
    			if (doRulePopExit) return false;		// 不允许进行付款
    			
    			// 再找商场满赠促销
    			if (doNewGiftPop("2")) haveRulePop = true;
    			if (doRulePopExit) return false;		// 不允许进行付款
    		}
    	}
    	else
    	{
    		haveRulePop = doRulePop();
    	}
    	
    	return true;
    }
    
    public boolean exitPaySell()
    {
    	// 满减预先付款除外付款退出时，允许直接退出
    	if (isPreparePay == payPopPrepare)
    	{
    		return true;
    	}
    	else
    	{
    		boolean ret = super.exitPaySell();
    		
    		if (ret) isPreparePay = payNormal;
    		
    		return ret;
    	}
    }
    
    //计算满减或满增
    public boolean doRulePop()
    {
    	if (GlobalInfo.sysPara.rulepop == 'N')
    	{
    		return false;
    	}
    	
    	if (!SellType.ISSALE(saletype))
    	{
    		return false;
    	}
    	
    	if (SellType.ISEARNEST(saletype))
    	{
    		return false;
    	}
    	
    	if (SellType.NOPOP(saletype)) return false;
    	
    	if (SellType.ISPREPARETAKE(saletype))
    	{
    		return false;
    	}

    	if (SellType.ISCOUPON(saletype))
    	{
    		return false;
    	}
    	
    	if (SellType.ISJFSALE(saletype))
    	{
    		return false;
    	}

    	int j=0;
    	Vector set = new Vector();
    	CalcRulePopDef calPop = null;
    	
    	//
    	Bhls_DataService dataservice = (Bhls_DataService)DataService.getDefault();
    	
    	//开始分组
    	for (int i = 0;i<saleGoods.size();i++)
    	{
    		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
    		String ruleCode = ((SpareInfoDef)goodsSpare.elementAt(i)).str1;
    		for (j=0;j<set.size();j++)
    		{
    			calPop = (CalcRulePopDef) set.elementAt(j);
    			if (calPop.code.equals(saleGoodsDef.code) && calPop.gz.equals(saleGoodsDef.gz) && calPop.uid.equals(saleGoodsDef.uid) && calPop.rulecode.equals(ruleCode))
    			{
    				calPop.row_set.add(String.valueOf(i));
    				break;
    			}
    		}
    		
    		if (j == set.size())
    		{
    			calPop = new CalcRulePopDef();
    			calPop.code     = saleGoodsDef.code;
    			calPop.gz       = saleGoodsDef.gz;
    			calPop.uid      = saleGoodsDef.uid;
    			calPop.rulecode = ruleCode;
    			calPop.catid    = saleGoodsDef.catid;
    			calPop.ppcode   = saleGoodsDef.ppcode;
    			calPop.row_set  = new Vector();
    			calPop.row_set.add(String.valueOf(i));
    			set.add(calPop);
    		}
    	}
    	
    	//开始查询促销单
    	for (int i=0;i<set.size();i++)
    	{
    		calPop = (CalcRulePopDef) set.elementAt(i);
    		GoodsPopDef  popDef = new GoodsPopDef();    		
    		
    		//会员卡类别
    		String custType = "";
    		//会员卡号
    		String custNo = "";
    		
    		if (curCustomer != null)
    		{
    			custType = curCustomer.type;
    			custNo = curCustomer.type;
    		}
    		
    		if (!dataservice.findPopRule(popDef, calPop.code, calPop.gz, calPop.uid, calPop.rulecode, calPop.catid, calPop.ppcode,saleHead.rqsj, custType, custNo))
    		{
    			set.remove(i);
    			i--;
    			continue;
    		}
    		else
    		{
    			calPop.popDef = popDef;
    		}
    	}
    	
    	//检查是否含有重复促销单
    	CalcRulePopDef calPop1 = null;
    	j=0;
    	for (int i=0;i<set.size();i++)
    	{
    		calPop = (CalcRulePopDef) set.elementAt(i);
    		
    		for (j=i+1;j<set.size();j++)
    		{
    			calPop1 = (CalcRulePopDef) set.elementAt(j);
    			if (calPop.popDef.djbh.equalsIgnoreCase(calPop1.popDef.djbh))
    			{
    				calPop.row_set.addAll(calPop1.row_set);
    				set.remove(j);
    				j--;
    			}
    		}
    	}
    	
    	//开始计算倍数
    	for (int i=0;i<set.size();i++)
    	{
    		calPop = (CalcRulePopDef) set.elementAt(i);
    		if (calPop.popDef.mode.equals("ME"))//满商品金额
    		{
    			double hj =0;
    			
    			//计算此促销的商品金额
    			for (j=0;j<calPop.row_set.size();j++)
    			{
    				SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt((String)calPop.row_set.elementAt(j)));
    				hj += saleGoodsDef.hjje - getZZK(saleGoodsDef);
    			}
    			
    			int num = 0;
    			if (calPop.popDef.poplsj > 0)
    			{
    				num = ManipulatePrecision.integerDiv(hj,calPop.popDef.poplsj);
    			}
    			
    			if (num < 1)
    			{
    				set.remove(i);
    				i--;
    			}
    			else
    			{
    				calPop.mult_Amount = num;
    			}
    		}
    		else if (calPop.popDef.mode.equals("MS"))//满商品数量
    		{
    			double sl =0;
    			
    			//计算此促销的商品数量
    			for (j=0;j<calPop.row_set.size();j++)
    			{
    				SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt((String)calPop.row_set.elementAt(j)));
    				sl += saleGoodsDef.sl;
    			}
    			
    			int num = 0;
        		if (calPop.popDef.poplsj > 0)
        		{
        			num = ManipulatePrecision.integerDiv(sl,calPop.popDef.poplsj);
        		}
        		
    			if (num < 1)
    			{
    				set.remove(i);
    				i--;
    			}
    			else
    			{
    				calPop.mult_Amount = num;
    			}
    		}
    		else
    		{
    			set.remove(i);
    			i--;
    		}
    	}
    	
    	if (set.size() <= 0)
    	{
    		return false;
    	}
    	else
    	{
    		for (int i=0 ; i< set.size();i++)
    		{
    			calPop = (CalcRulePopDef) set.elementAt(i);
    			if (calPop.popDef.rule.equals("RMJ"))
    			{
    				double je = calPop.mult_Amount*calPop.popDef.pophyj;
    				double hj = 0;
    				
    				//满减总金额
    				new MessageBox(Language.apply("现有促销满{0}减{1}\n\n你目前可以减{2}元", new Object[]{ManipulatePrecision.doubleToString(calPop.popDef.poplsj),ManipulatePrecision.doubleToString(calPop.popDef.pophyj),ManipulatePrecision.doubleToString(je)}));
//    				new MessageBox("现有促销满 "+ManipulatePrecision.doubleToString(calPop.popDef.poplsj) +" 减 "+ManipulatePrecision.doubleToString(calPop.popDef.pophyj) + "\n\n你目前可以减 " + ManipulatePrecision.doubleToString(je) + " 元");
    				    				
    				for (j = 0; j<calPop.row_set.size();j++)
    				{
    					SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt((String)calPop.row_set.elementAt(j)));
    					hj += saleGoodsDef.hjje - getZZK(saleGoodsDef);
    					saleGoodsDef.zsdjbh = calPop.popDef.djbh;
    					saleGoodsDef.zszkfd = calPop.popDef.poplsjzkfd;
    				}
    				
    				double yfd =0;
    				for (j = 0; j<calPop.row_set.size();j++)
    				{
    					// 分摊时最后一个商品不处理价格精度
    					if (j == (calPop.row_set.size() - 1))
    					{
    						SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt((String)calPop.row_set.elementAt(j)));
        					saleGoodsDef.zszke=ManipulatePrecision.doubleConvert(je - yfd,2, 1);
        					getZZK(saleGoodsDef);
        					continue;
    					}
    					
    					SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt((String)calPop.row_set.elementAt(j)));
    					saleGoodsDef.zszke=ManipulatePrecision.doubleConvert((saleGoodsDef.hjje-saleGoodsDef.hjzk)/hj*je,2, 1);
    					saleGoodsDef.zszke=getConvertRebate(Integer.parseInt((String)calPop.row_set.elementAt(j)),saleGoodsDef.zszke);
    					saleGoodsDef.zszke=getConvertRebate(Integer.parseInt((String)calPop.row_set.elementAt(j)),saleGoodsDef.zszke,getGoodsApportionPrecision());
    					yfd += saleGoodsDef.zszke;
    					getZZK(saleGoodsDef);
    				}
    			}
    			else if (calPop.popDef.rule.equals("RMS"))
    			{
    				//查询赠品信息
    				new MessageBox(Language.apply("现有促销满{0}元有赠品", new Object[]{ManipulatePrecision.doubleToString(calPop.popDef.poplsj)}));
    				
    				Vector giftGoods = new Vector();
    				boolean add = false;
    				if (dataservice.findRulePopGift(giftGoods, calPop.popDef.djbh)&&giftGoods.size() > 0)
    				{
    					for (j = 0; j<giftGoods.size(); j++)
    					{
    						GoodsPopDef goodspop = (GoodsPopDef) giftGoods.elementAt(j);
    						GoodsDef goodsDef = new GoodsDef();
    						int result = DataService.getDefault().getGoodsDef(goodsDef, 4, goodspop.code,goodspop.gz, "", saleHead.rqsj,saletype);
    	    				if (result != 0)
    	    				{
    	    					new MessageBox(Language.apply("未能查询到商品{0}\n不能将此商品作为赠品发送", new Object[]{calPop.popDef.code}));
    	    				}
    	    				else
    	    				{
    	    					SaleGoodsDef gift = goodsDef2SaleGoods(goodsDef, "",goodspop.poppfj*calPop.mult_Amount,goodspop.pophyj , 0, false);
    	    					gift.flag = '1';
    	    					gift.zszke = gift.hjje;
    	    					gift.zsdjbh   = goodspop.djbh;
        				        gift.zszkfd   = goodspop.poplsjzkfd;
    	    					getZZK(gift);
    	    					
    	    					addSaleGoodsObject(gift,null,null);
    	    					add = true;
    	    				}
    					}
	    				
	    				if (add)
	    				{
		    				for (j = 0; j<calPop.row_set.size();j++)
		    				{
		    					SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt((String)calPop.row_set.elementAt(j)));
		    					saleGoodsDef.zsdjbh = calPop.popDef.djbh;
		    					saleGoodsDef.zszkfd = calPop.popDef.poplsjzkfd;
		    				}
	    				}
	    				else
	    				{
	    					set.remove(i);
	    					i--;
	    				}
    				}
    				else
    				{
    					set.remove(i);
    					i--;
    				}
    			}
    			else if (calPop.popDef.rule.equals("RMF"))
    			{
    				new MessageBox(Language.apply("现有促销满{0}元有赠品", new Object[]{ManipulatePrecision.doubleToString(calPop.popDef.poplsj)}));

    				Vector giftGoods = new Vector();
    				if (dataservice.findRulePopGift(giftGoods, calPop.popDef.djbh)&&giftGoods.size() > 0)
    				{
    					for (j = 0; j<giftGoods.size();j++)
    					{
    						GoodsPopDef goodspop = (GoodsPopDef) giftGoods.elementAt(j);
    						SaleGoodsDef gift = goodsDef2SaleGoods(new GoodsDef(), "",0.0, 0, 0, false);
    						gift.barcode  = "GIFT"+saleGoods.size(); // 商品条码					
    						gift.code     = "GIFT"+saleGoods.size(); // 商品编码					
    						gift.type     = '1'; // 编码类别					
    						gift.gz       = goodspop.gz; // 商品柜组	
    						gift.name     = goodspop.gz; // 名称	
    						gift.bzhl     = 1; // 包装含量	
    						gift.sl       = ManipulatePrecision.doubleConvert(goodspop.poppfj*calPop.mult_Amount, 4, 1); // 销售数量					
    						gift.lsj      = 0; // 零售价					
    				        gift.jg       = goodspop.pophyj; // 销售价格	
    				        gift.hjje     = ManipulatePrecision.doubleConvert(gift.jg * gift.sl,2, 1); // 合计金额;
    				        gift.hjzk     = gift.hjje;
    				        gift.hyzkfd   = 1; // 会员折扣分担
    				        gift.sqkh     = ""; // 单品授权卡号				
    				        gift.flag     = '1'; // 商品标志，1-赠品,2-电子秤条码，3-削价，4-一般
    				        gift.zszke    = gift.hjje; // 赠送折扣	
    				        gift.zsdjbh   = goodspop.djbh;
    				        gift.zszkfd   = goodspop.poplsjzkfd;
    				        gift.yyyh 	  = "";
    				        gift.sqkh     = ""; // 单品授权卡号				
    				        gift.sqktype  = '\0'; // 单品授权卡类别
    				        
    				        //
    				        addSaleGoodsObject(gift,null,null);
    					}
    					
    					for (j = 0; j<calPop.row_set.size();j++)
        				{
        					SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt((String)calPop.row_set.elementAt(j)));
        					saleGoodsDef.zsdjbh = calPop.popDef.djbh;
        					saleGoodsDef.zszkfd = calPop.popDef.poplsjzkfd;
        				}
    				}
    			}
    		}
    	}
    	
    	// 重算应收
    	calcHeadYsje();
    	
    	// 刷新商品列表
    	saleEvent.updateTable(getSaleGoodsDisplay());
    	saleEvent.setTotalInfo();
    	
    	return true;
    }

    // FOXTOWN计算满减或满增
    public boolean doNewRulePop()
    {
    	String Mess = "";
    	// 先总是无满减规则方式的付款
    	isPreparePay = payNormal;
    	
    	if (GlobalInfo.sysPara.rulepop == 'N')
    	{
    		return false;
    	}
    	
    	if (!SellType.ISSALE(saletype))
    	{
    		return false;
    	}
    	
    	if (SellType.ISEARNEST(saletype))
    	{
    		return false;
    	}
    	
    	if (SellType.NOPOP(saletype)) return false;
    	
    	if (SellType.ISPREPARETAKE(saletype))
    	{
    		return false;
    	}
    	
    	if (SellType.ISCOUPON(saletype))
    	{
    		return false;
    	}
    	
    	if (SellType.ISJFSALE(saletype))
    	{
    		return false;
    	}
    	
    	int i = 0,j=0;
    	Vector set = new Vector();
    	CalcRulePopDef calPop = null;
    	
    	//
    	Bhls_DataService dataservice = (Bhls_DataService)DataService.getDefault();
    	
    	//先商品开始分组
    	for (i = 0;i<saleGoods.size();i++)
    	{
    		// 赠品不计算分组
    		if (goodsAssistant.elementAt(i) == null) continue;
    		
    		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
    		String ruleCode = ((GoodsDef)goodsAssistant.elementAt(i)).specinfo; 

    		// 选择了不参与减现继续下一个商品
			String mjrule = ((SpareInfoDef)goodsSpare.elementAt(i)).str1;
			if (!(mjrule.equals("F") || mjrule.equals("R") || mjrule.equals("MJ") || mjrule.equals("S"))) continue;
				
    		// 属性码为00无促销
    		if (ruleCode.equals("00")) continue;
    		
    		// 查找是否相同商品
    		for (j=0;j<set.size();j++)
    		{
    			calPop = (CalcRulePopDef) set.elementAt(j);
    			if (calPop.code.equals(saleGoodsDef.code) && calPop.gz.equals(saleGoodsDef.gz) && calPop.uid.equals(saleGoodsDef.uid) && 
    				calPop.rulecode.equals(ruleCode))
    			{
    				calPop.row_set.add(String.valueOf(i));
    				break;
    			}
    		}
    		if (j >= set.size())
    		{
    			calPop = new CalcRulePopDef();
    			calPop.code     = saleGoodsDef.code;
    			calPop.gz       = saleGoodsDef.gz;
    			calPop.uid      = saleGoodsDef.uid;
    			calPop.rulecode = ruleCode;
    			calPop.catid    = saleGoodsDef.catid;
    			calPop.ppcode   = saleGoodsDef.ppcode;
    			calPop.row_set  = new Vector();
    			calPop.row_set.add(String.valueOf(i));
    			set.add(calPop);
    		}
    	}
    	
    	//按商品分组开始查询促销单
    	for (i=0;i<set.size();i++)
    	{
    		calPop = (CalcRulePopDef) set.elementAt(i);
    		GoodsPopDef popDef = new GoodsPopDef();    		
    		
    		String custType = "";
    		//会员卡号
    		String custNo = "";
    		
    		if (curCustomer != null)
    		{
    			custType = curCustomer.type;
    			custNo = curCustomer.type;
    		}
    		
    		if (!dataservice.findPopRule(popDef, calPop.code, calPop.gz, calPop.uid, calPop.rulecode, calPop.catid, calPop.ppcode,saleHead.rqsj, custType, custNo))
    		{
    			set.remove(i);
    			i--;
    			continue;
    		}
    		else
    		{
    			calPop.popDef = popDef;
    			
    			//记录折扣分担
    			for (int x = 0 ; x < calPop.row_set.size(); x ++)
    			{
    				SpareInfoDef SpareInfo = (SpareInfoDef) goodsSpare.elementAt(Integer.parseInt((String)calPop.row_set.elementAt(x)));
    				
    				if (SpareInfo == null) continue;
    				
    				SpareInfo.num3 = popDef.poplsjzkfd;
    			}
    		}
    	}
    	
    	//检查是否含有重复促销单
    	CalcRulePopDef calPop1 = null;
    	for (i=0;i<set.size();i++)
    	{
    		calPop = (CalcRulePopDef) set.elementAt(i);
    		
    		for (j=i+1;j<set.size();j++)
    		{
    			calPop1 = (CalcRulePopDef) set.elementAt(j);
    			
    			// 允许跨柜,按活动单号、促销属性码分组
    			// 不允许跨柜,按活动单号、促销属性码、商品是否同柜分组
    			if (calPop.popDef.memo.equalsIgnoreCase(calPop1.popDef.memo) &&
    				calPop.rulecode.equalsIgnoreCase(calPop1.rulecode) &&
    				(
    				(calPop.popDef.ppcode.equalsIgnoreCase("Y") && calPop1.popDef.ppcode.equalsIgnoreCase("Y")) || 
    				((!calPop.popDef.ppcode.equalsIgnoreCase("Y") || !calPop1.popDef.ppcode.equalsIgnoreCase("Y")) && calPop.gz.equalsIgnoreCase(calPop1.gz))
    				)) 
    			{
    				calPop.row_set.addAll(calPop1.row_set);
    				set.remove(j);
    				j--;
    			}
    		}
    	}
    	
    	// 无规则促销
    	if (set.size() <= 0)
    	{
    		return false;
    	}
    	
		// 如果要除券,一单交易只允许同一规则的商品,否则不好分摊券付款
    	// 不允许同一单有不同满赠满减规则,也不允许部分商品参与满减，部分商品不参与满减
		boolean havepaycw = false;
    	for (i=0;i<set.size();i++)
    	{
    		calPop = (CalcRulePopDef) set.elementAt(i);
    		
    		if (calPop.popDef.catid.equals("Y"))
    		{
    	    	if (set.size() >= 2)
    	    	{
    	    		new MessageBox(Language.apply("本笔交易存在不同的活动促销\n\n请分单进行收银"));
    	    		doRulePopExit = true;
    	    		return false;
    	    	}
    	    	if (calPop.row_set.size() != saleGoods.size())
    	    	{
    	    		new MessageBox(Language.apply("本笔交易部分商品参与活动促销,部分不参与\n\n请分单进行收银"));
    	    		doRulePopExit = true;
    	    		return false;
    	    	}
    	    	
    	    	havepaycw = true;
    		}
    	}
    	
    	// 循环两次
    	// 第一次先检查是否有满足条件的规则,如果没有则直接返回
    	// 第二次检查除券外是否还有满足条件的规则,如果不需要除券,则只用循环一次
    	int nwhile = 1;
    	do {
    		Mess = "";
	    	// 开始计算商品分组参与计算的合计金额
	    	for (i=0;i<set.size();i++)
	    	{
				// 如果是能进入第二次循环,说明有交易金额是满足促销条件的规则促销
				// 如果需要扣除券付款,先输入券付款方式
				if (nwhile >= 2 && havepaycw)
				{
		        	// 提示先输入券付款
		    		if (new MessageBox(Language.apply("本笔交易有活动促销,请先输入券付款金额\n\n如果顾客没有券付款,请直接按‘退出’键")).verify() != GlobalVar.Exit)
		    		{
			    		// 开始预付除外付款方式
			    		isPreparePay = payPopPrepare;
			    		
			        	// 打开付款窗口
			        	new SalePayForm().open(saleEvent.saleBS);
			    	    
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
				
				// 计算同规则商品合计
	    		calPop = (CalcRulePopDef) set.elementAt(i);
	    		double sphj=0;
				for (j=0;j<calPop.row_set.size();j++)
				{
					SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt((String)calPop.row_set.elementAt(j)));
					sphj += saleGoodsDef.hjje - getZZK(saleGoodsDef);
				}
								
	        	// 除开券付款,开始计算前存在的付款方式都算需要除外的付款
				double cwpayje = 0;
				for (j=0;j<salePayment.size();j++)
				{
					SalePayDef pay = (SalePayDef)salePayment.elementAt(j);
					cwpayje += pay.je;
				}
				cwpayje -= salezlexception;
				sphj = ManipulatePrecision.doubleConvert(sphj - cwpayje,2,1);
				if (sphj <= 0) sphj = 0;
				
				// 检查是否满足条件
				if (calPop.popDef.gz.equals("1"))		// 按金额满减
				{
					int num = 0;
					if (calPop.popDef.poplsj > 0)
					{
						num = ManipulatePrecision.integerDiv(sphj,calPop.popDef.poplsj);
					}

					if (num > 0)
					{
						Mess += Language.apply("现有促销满{0}减 ", new Object[]{ManipulatePrecision.doubleToString(calPop.popDef.poplsj)})+ManipulatePrecision.doubleToString(calPop.popDef.pophyj)+"\n";
//						Mess += "现有促销满 "+ManipulatePrecision.doubleToString(calPop.popDef.poplsj) +" 减 "+ManipulatePrecision.doubleToString(calPop.popDef.pophyj)+"\n";
					}
					// 
					double mjje = num * calPop.popDef.pophyj;
					double yfmj = num * calPop.popDef.poplsj;
					
                    // 是否存在阶梯条件
                    if (calPop.popDef.str2 != null && calPop.popDef.str2.trim().length() > 0)
                    {
                    	String[] row = calPop.popDef.str2.split(";");
                    	
                    	for (int c = 0; c < row.length; c++)
                    	{
                    		if (row[c] == null || row[c].split(",").length != 2) continue;
                    		
                    		double a = Convert.toDouble(row[c].split(",")[0]); //满减条件
                    		double b = Convert.toDouble(row[c].split(",")[1]); //满减金额
                    		
                    		if (a ==  0 || b == 0) continue;
                    		
                    		double d = sphj - yfmj;
                    		if (d < 0) d = 0;
                    		num = ManipulatePrecision.integerDiv(d,a);
                    		
                    		if (num > 0)
                    		{
                    			Mess += Language.apply("现有促销满{0}减 ", new Object[]{ManipulatePrecision.doubleToString(a)})+ ManipulatePrecision.doubleToString(b) +"\n";
                    			mjje += num * b;
                    			yfmj += num * a;
                    		}
                    	}
                    }
                    
					// 不满足条件金额
					if (mjje <= 0)
					{
						set.remove(i);
						i--;
					}
					else
					{
						calPop.popje = sphj;
						calPop.mult_Amount = mjje;
					}				
				}
				else if (calPop.popDef.gz.equals("2"))	// 按百分比减现
				{
					// 无效的减现比例
					if (sphj <= 0 || calPop.popDef.poplsjzkl <= 0 || calPop.popDef.poplsjzkl >= 1)
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
	    	if (set.size() <= 0)
	    	{
	    		return false;
	    	}
	    	
	    	// 循环计数,如果不需要除券,则不用进行第二次循环
	    	nwhile++;
	    	if (!havepaycw) nwhile++;
    	} while(nwhile <= 2);
    	
    	// 分摊满减折扣金额
		for (i=0 ; i< set.size();i++)
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
				new MessageBox(Mess + 
				               Language.apply("\n\n你目前可参加活动的金额为{0}元\n\n你可以减现{1}元", new Object[]{ManipulatePrecision.doubleToString(calPop.popje),ManipulatePrecision.doubleToString(je)}));
			}
			
			// 按百分比减现
			if (calPop.popDef.gz.equals("2"))
			{
				je = calPop.popje * calPop.popDef.poplsjzkl;
				
				// 提示满减规则
				new MessageBox(Language.apply("现有促销减现{0}%\n\n你目前可参加活动的金额为{1}元\n\n你目前可以减现{2}元", new Object[]{ManipulatePrecision.doubleToString(calPop.popDef.poplsjzkl*100),ManipulatePrecision.doubleToString(calPop.popje),ManipulatePrecision.doubleToString(je)}));
			}
			
			// 记录规则促销单据信息
			for (j = 0; j<calPop.row_set.size();j++)
			{
				SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt((String)calPop.row_set.elementAt(j)));
				SpareInfoDef SpareInfo = (SpareInfoDef) goodsSpare.elementAt(Integer.parseInt((String)calPop.row_set.elementAt(j)));
				hj += saleGoodsDef.hjje - getZZK(saleGoodsDef);
				saleGoodsDef.zsdjbh = calPop.popDef.djbh;
				saleGoodsDef.zszkfd = SpareInfo.num3;
			}
			
			// 分摊满减折扣到各商品
			double yfd =0;
			for (j = 0; j<calPop.row_set.size();j++)
			{
				SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt((String)calPop.row_set.elementAt(j)));

				// 把剩余未分摊金额，直接分摊到最后一个商品
				if (j == (calPop.row_set.size() - 1))
				{
					saleGoodsDef.zszke = ManipulatePrecision.doubleConvert(je - yfd,2, 1);
				}
				else
				{
					saleGoodsDef.zszke = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje-saleGoodsDef.hjzk) / hj * je,2,1);
					saleGoodsDef.zszke = getConvertRebate(Integer.parseInt((String)calPop.row_set.elementAt(j)),saleGoodsDef.zszke);
					saleGoodsDef.zszke = getConvertRebate(Integer.parseInt((String)calPop.row_set.elementAt(j)),saleGoodsDef.zszke,getGoodsApportionPrecision());
				}
				getZZK(saleGoodsDef);

				// 计算已分摊的金额
				yfd += saleGoodsDef.zszke;
			}
		}
    	
    	// 重算应收
    	calcHeadYsje();
    	
    	// 刷新商品列表
    	saleEvent.updateTable(getSaleGoodsDisplay());
    	saleEvent.setTotalInfo();
    	
    	// 提示收银员查看满减结果
		new MessageBox(Language.apply("请核对促销活动的相关折扣金额!"));

    	return true;
    }
    
    // 新前台满增
    public boolean doNewGiftPop(String poptype)
    {
    	if (GlobalInfo.sysPara.rulepop == 'N')
    	{
    		return false;
    	}
    	
    	if (!SellType.ISSALE(saletype))
    	{
    		return false;
    	}
    	
    	if (SellType.ISEARNEST(saletype))
    	{
    		return false;
    	}
    	
    	if (SellType.NOPOP(saletype)) return false;
    	
    	if (SellType.ISPREPARETAKE(saletype))
    	{
    		return false;
    	}
    	
    	if (SellType.ISCOUPON(saletype))
    	{
    		return false;
    	}
    	
    	if (SellType.ISJFSALE(saletype))
    	{
    		return false;
    	}
    	
    	int i = 0,j=0;
    	Vector set = new Vector();
    	CalcRulePopDef calPop = null;

    	// 确定会员类别
    	String cardno = null;
        String cardtype = null;
        if (curCustomer != null)
        {
            cardno   = curCustomer.code;
            cardtype = curCustomer.type;
        }
        
    	//
    	Bhls_DataService dataservice = (Bhls_DataService)DataService.getDefault();
    	
    	//先商品开始分组
    	int goodsnum = 0;
    	for (i = 0;i<saleGoods.size();i++)
    	{
    		// 增加的赠品不进行分组
    		if (goodsAssistant.elementAt(i) == null) continue;
    		goodsnum++;
    		
    		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
    		String ruleCode = ((GoodsDef)goodsAssistant.elementAt(i)).specinfo; 
    		ruleCode = poptype;
    		
    		// 选择了不参与减现继续下一个商品
			String mjrule = ((SpareInfoDef)goodsSpare.elementAt(i)).str1;
			if (!(mjrule.equals("F") || mjrule.equals("S") || mjrule.equals("R") || mjrule.equals("MS"))) continue;
    		
    		// 查找是否相同商品
    		for (j=0;j<set.size();j++)
    		{
    			calPop = (CalcRulePopDef) set.elementAt(j);
    			if (calPop.code.equals(saleGoodsDef.code) && calPop.gz.equals(saleGoodsDef.gz) && calPop.uid.equals(saleGoodsDef.uid))
    			{
    				calPop.row_set.add(String.valueOf(i));
    				break;
    			}
    		}
    		if (j >= set.size())
    		{
    			calPop = new CalcRulePopDef();
    			calPop.code     = saleGoodsDef.code;
    			calPop.gz       = saleGoodsDef.gz;
    			calPop.uid      = saleGoodsDef.uid;
    			calPop.rulecode = ruleCode;
    			calPop.catid    = saleGoodsDef.catid;
    			calPop.ppcode   = saleGoodsDef.ppcode;
    			calPop.row_set  = new Vector();
    			calPop.row_set.add(String.valueOf(i));
    			set.add(calPop);
    		}
    	}
    	
    	//按商品分组开始查询促销单
    	for (i=0;i<set.size();i++)
    	{
    		calPop = (CalcRulePopDef) set.elementAt(i);
    		GoodsPopDef popDef = new GoodsPopDef();
    		if (!dataservice.findGiftRule(popDef, calPop.code, calPop.gz, calPop.uid, calPop.rulecode, calPop.catid, calPop.ppcode,saleHead.rqsj,cardno,cardtype))
    		{
    			set.remove(i);
    			i--;
    			continue;
    		}
    		else
    		{
    			calPop.popDef = popDef;
    		}
    	}
    	
    	//检查是否含有重复促销单
    	CalcRulePopDef calPop1 = null;
    	for (i=0;i<set.size();i++)
    	{
    		calPop = (CalcRulePopDef) set.elementAt(i);
    		
    		for (j=i+1;j<set.size();j++)
    		{
    			calPop1 = (CalcRulePopDef) set.elementAt(j);
    			
    			// 允许跨柜,按活动单号分组
    			// 不允许跨柜,按活动单号、商品是否同柜分组
    			if (calPop.popDef.memo.equalsIgnoreCase(calPop1.popDef.memo) &&
    				(
    				(calPop.popDef.ppcode.equalsIgnoreCase("Y") && calPop1.popDef.ppcode.equalsIgnoreCase("Y")) || 
    				((!calPop.popDef.ppcode.equalsIgnoreCase("Y") || !calPop1.popDef.ppcode.equalsIgnoreCase("Y")) && calPop.gz.equalsIgnoreCase(calPop1.gz))
    				)) 
    			{
    				calPop.row_set.addAll(calPop1.row_set);
    				set.remove(j);
    				j--;
    			}
    		}
    	}
    	
    	// 无规则促销
    	if (set.size() <= 0)
    	{
    		return false;
    	}
    	
		// 如果要除券,一单交易只允许同一规则的商品,否则不好分摊券付款
    	// 不允许同一单有不同满赠满减规则,也不允许部分商品参与满减，部分商品不参与满减
		boolean havepaycw = false;
    	for (i=0;i<set.size();i++)
    	{
    		calPop = (CalcRulePopDef) set.elementAt(i);
    		
    		if (calPop.popDef.catid.equals("Y"))
    		{
    	    	if (set.size() >= 2)
    	    	{
    	    		new MessageBox(Language.apply("本笔交易需要除券但存在不同的促销赠品\n\n请分单进行收银"));
    	    		doRulePopExit = true;
    	    		return false;
    	    	}
    	    	if (calPop.row_set.size() != goodsnum)
    	    	{
    	    		new MessageBox(Language.apply("本笔交易需要除券但部分商品参与促销赠品,部分不参与\n\n请分单进行收银"));
    	    		doRulePopExit = true;
    	    		return false;
    	    	}
    	    	
    	    	havepaycw = true;
    		}
    	}
    	
    	// 循环两次
    	// 第一次先检查是否有满足条件的规则,如果没有则直接返回
    	// 第二次检查除券外是否还有满足条件的规则,如果不需要除券,则只用循环一次
    	int nwhile = 1;
    	do {
	    	// 开始计算商品分组参与计算的合计金额
	    	for (i=0;i<set.size();i++)
	    	{
				// 如果是能进入第二次循环,说明有交易金额是满足促销条件的规则促销
				// 如果需要扣除券付款,先输入券付款方式,若有满减已经输入过券则不再输入
				if (nwhile >= 2 && havepaycw && isPreparePay == payNormal)
				{
		        	// 提示先输入券付款
		    		if (new MessageBox(Language.apply("本笔交易有促销赠品,需要先输入券付款金额\n\n如果顾客没有券付款,请直接按‘退出’键")).verify() != GlobalVar.Exit)
		    		{
			    		// 开始预付除外付款方式
			    		isPreparePay = payPopPrepare;
			    		
			        	// 打开付款窗口
			        	new SalePayForm().open(saleEvent.saleBS);
			    	    
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
				
				// 计算同规则商品合计
	    		calPop = (CalcRulePopDef) set.elementAt(i);
	    		double sphj=0;
				for (j=0;j<calPop.row_set.size();j++)
				{
					SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt((String)calPop.row_set.elementAt(j)));
					sphj += saleGoodsDef.hjje - getZZK(saleGoodsDef);
				}
								
	        	// 进入第2次循环,才是除开券付款,开始计算前存在的付款方式都算需要除外的付款
				double cwpayje = 0;
				if (nwhile >= 2)
				{
					for (j=0;j<salePayment.size();j++)
					{
						SalePayDef pay = (SalePayDef)salePayment.elementAt(j);
						cwpayje += pay.je;
					}
					cwpayje -= salezlexception;
				}
				sphj = ManipulatePrecision.doubleConvert(sphj - cwpayje,2,1);
				if (sphj <= 0) sphj = 0;
				
				// 检查是否满足条件
				int num = 0;
				if (calPop.popDef.str2 != null && calPop.popDef.str2.trim().length() > 0)
				{
					String[] con = calPop.popDef.str2.split(";");
					for (int x = 0; x < con.length; x++)
					{
						if (con[x] == null || con[x].trim().length() <= 0 || con[x].indexOf(",") <=0) continue;
						String[] rows = con[x].split(",");
						
						if (rows[0] == null || rows[0].trim().length() <= 0 || Double.parseDouble(rows[0]) <= 0) continue;
						
						num = ManipulatePrecision.integerDiv(sphj,Double.parseDouble(rows[0]));
						
						if (num >= 1)
						{
							calPop.popDef.num5 = Double.parseDouble(rows[1]);
							calPop.popDef.poplsj = Double.parseDouble(rows[0]);
							break;
						}
					}
				}
				else if (calPop.popDef.poplsj > 0)
				{
					num = ManipulatePrecision.integerDiv(sphj,calPop.popDef.poplsj);
				}
				// 不满足条件金额
				if (num < 1)
				{
					set.remove(i);
					i--;
				}
				else
				{
					calPop.popje = sphj;
					calPop.mult_Amount = num;
				}				
	    	}
	    	
	    	// 无有效的、满足条件的规则促销
	    	if (set.size() <= 0)
	    	{
	    		return false;
	    	}
	    	
	    	// 循环计数,如果不需要除券,则不用进行第二次循环
	    	nwhile++;
	    	if (!havepaycw) nwhile++;
    	} while(nwhile <= 2);
    	
    	// 找促销赠品
    	boolean havegift = false;
		for (i=0 ; i< set.size();i++)
		{
			calPop = (CalcRulePopDef) set.elementAt(i);
			SaleGoodsDef sgd = (SaleGoodsDef)saleGoods.elementAt(Integer.parseInt((String)calPop.row_set.elementAt(0)));
			
			// 查询赠品信息
			Vector giftGoods = new Vector();
			if (!dataservice.findRulePopGift(giftGoods, calPop.popDef.memo)) continue;
			if (giftGoods.size() <= 0) continue;
			
			new MessageBox(Language.apply("有促销活动满{0}元送赠品", new Object[]{ManipulatePrecision.doubleToString(calPop.popDef.poplsj)}));
			
			for (j = 0; j<giftGoods.size(); j++)
			{
				GoodsPopDef goodspop = (GoodsPopDef) giftGoods.elementAt(j);
				
				if (calPop.popDef.num5 > 0 && calPop.popDef.num5 != goodspop.num2)
				{
					continue;
				}
				// 编码不为0,查找实际商品
				GoodsDef goodsDef = null;
				if (!goodspop.code.equals("0"))
				{
					goodsDef = findGoodsInfo(goodspop.code,sgd.yyyh,goodspop.gz,"",false,null,true);
					if (goodsDef == null)
					{
						new MessageBox(Language.apply("找不到商品[{0}]的信息\n\n不能将此商品作为赠品发放", new Object[]{goodspop.code}));
						continue;
					}
					// 以赠品定价为准
					goodsDef.lsj = goodspop.poplsj;
				}
				
				if (calPop.popDef.pophyj <= 1)	// 专柜活动
				{
					// 要求收银员输入商品
					if (goodspop.code.equals("0"))
					{
						boolean done = true;
	                	StringBuffer sb = new StringBuffer();
	                	do {
	                		if (new TextBox().open(Language.apply("请输入[{0}]柜组的商品", new Object[]{sgd.gz}), Language.apply("商品编码"), Language.apply("专柜促销活动可赠送价值{0}元\n以内的任意商品", new Object[]{ManipulatePrecision.doubleToString(goodspop.poplsj)}) , sb, 0, 0, false))
	                		{
	        					goodsDef = findGoodsInfo(sb.toString(),sgd.yyyh,sgd.gz,"",false,null,true);
	        					if (goodsDef == null) continue;
	        					
	        					if (goodsDef.lsj <= 0) goodsDef.lsj = goodspop.poplsj;
	        					if (goodsDef.lsj > goodspop.poplsj)
	        					{					
	        						new MessageBox(Language.apply("购买的商品价值为{0}元时，只能赠价值{1}元以内的商品", new Object[]{ManipulatePrecision.doubleToString(calPop.popDef.poplsj),ManipulatePrecision.doubleToString(goodspop.poplsj)}));
	        						continue;
	        					}
	        					else
	        					{
	        						done = true;
	        						break;
	        					}
	                		}
	                		else
	                		{
	                			if (new MessageBox(Language.apply("顾客放弃专柜赠送的商品吗？"),null,true).verify() == GlobalVar.Key1)
	                			{
	                				done = false;
	                				break;
	                			}
	                		}
	                	} while(done);
						if (!done) continue;
					}
					
					if (calPop.mult_Amount > goodspop.num1 && goodspop.num1 > 0)
					{
						calPop.mult_Amount = goodspop.num1;
					}
			        // 不允许销红,检查库存
			        if ((SellType.ISSALE(saletype) && GlobalInfo.sysPara.isxh != 'Y' && goodsDef.isxh != 'Y'))
			        {
			            // 统计商品销售数量
			            double hjsl = goodspop.pophyj*calPop.mult_Amount + calcSameGoodsQuantity(goodsDef.code,goodsDef.gz);
			            if (goodsDef.kcsl < hjsl)
			            {
			            	if(GlobalInfo.sysPara.xhisshowsl == 'Y')
			            		new MessageBox(Language.apply("该商品库存为{0}\n库存不足,不能销售", new Object[]{ManipulatePrecision.doubleToString(goodsDef.kcsl)}));
			            	else
			            		new MessageBox(Language.apply("销售数量已大于该商品库存,不能销售"));
			                
			                return false;
			            }
			        }
			           
					//
					SaleGoodsDef gift = goodsDef2SaleGoods(goodsDef,sgd.yyyh,goodspop.pophyj*calPop.mult_Amount,goodsDef.lsj,0,false);
					gift.zszke = gift.hjje;
					gift.zsdjbh = goodspop.djbh;
			        gift.zszkfd = goodspop.poplsjzkfd;
			        gift.flag  = '5';
					getZZK(gift);
					
					addSaleGoodsObject(gift,null,null);
					
					havegift = true;
				}
				else							// 商场活动
				{
					if (goodspop.code.equals("0"))
					{
						goodsDef = new GoodsDef();
						goodsDef.barcode = "GIFT"+saleGoods.size();
						goodsDef.code = goodsDef.barcode;
						goodsDef.gz = goodspop.gz;
						goodsDef.uid = goodspop.uid;
						goodsDef.name = goodspop.memo;
						goodsDef.type = '1';
						goodsDef.unit = "件";
						goodsDef.lsj = goodspop.poplsj;
						goodsDef.kcsl = 999999;
					}
					
					if (calPop.mult_Amount > goodspop.num1 && goodspop.num1 > 0)
					{
						calPop.mult_Amount = goodspop.num1;
					}
					
			        // 不允许销红,检查库存
			        if ((SellType.ISSALE(saletype) && GlobalInfo.sysPara.isxh != 'Y' && goodsDef.isxh != 'Y'))
			        {
			            // 统计商品销售数量
			            double hjsl = goodspop.pophyj*calPop.mult_Amount + calcSameGoodsQuantity(goodsDef.code,goodsDef.gz);
			            if (goodsDef.kcsl < hjsl)
			            {
			            	if(GlobalInfo.sysPara.xhisshowsl == 'Y')
			            		new MessageBox(Language.apply("该商品库存为{0}\n库存不足,不能销售", new Object[]{ManipulatePrecision.doubleToString(goodsDef.kcsl)}));
			            	else
			            		new MessageBox(Language.apply("销售数量已大于该商品库存,不能销售"));
			                return false;
			            }
			        }
					
					SaleGoodsDef gift = goodsDef2SaleGoods(goodsDef,sgd.yyyh,goodspop.pophyj*calPop.mult_Amount,goodsDef.lsj,0,false);
					gift.flag = '1';		// 赠品标记
					gift.zszke = gift.hjje;
					gift.zsdjbh = goodspop.djbh;
			        gift.zszkfd = goodspop.poplsjzkfd;
					getZZK(gift);
					
					addSaleGoodsObject(gift,null,null);
					
					havegift = true;
				}
			}
		}
		if (!havegift) return false;
		
    	// 重算应收
    	calcHeadYsje();
    	
    	// 刷新商品列表
    	saleEvent.updateTable(getSaleGoodsDisplay());
    	saleEvent.setTotalInfo();
    	
    	// 提示收银员查看满赠结果
		new MessageBox(Language.apply("请核对促销活动的相关赠品!"));

    	return true;
    }  
    
    
    public boolean doRulePopWriteData()
    {
    	FileOutputStream f = null;
    	
        try
        {
            String name = ConfigClass.LocalDBPath + "/Bhlspop.dat";
            
	        f = new FileOutputStream(name);
	        ObjectOutputStream s = new ObjectOutputStream(f);
	        
	        // 将交易对象写入对象文件
	        s.writeObject(saleGoods);
	        s.writeObject(goodsSpare);
	        
	        s.flush();
	        s.close();
	        f.close();
	        s = null;
	        f = null;
	        
	        return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            
            return false;
        }
        finally
        {
        	try
        	{
	            if (f != null) f.close();
        	}
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public boolean delRulePopReadData()
    {
    	FileInputStream f = null;
    	
        try
        {
            String name = ConfigClass.LocalDBPath + "/Bhlspop.dat";
            
	        f = new FileInputStream(name);
	        ObjectInputStream s = new ObjectInputStream(f);
	        
	        // 读交易对象
	        Vector saleGoods1 = (Vector) s.readObject();
	        Vector spare1 = (Vector) s.readObject();
	        
			// 赋对象
	    	saleGoods = saleGoods1;
	    	goodsSpare = spare1;
	    	
            // 关闭断点文件
            s.close();
            s = null;
            f.close();
            f = null;

        	return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            
            return false;
        }
        finally
        {
        	try
        	{
	            if (f != null) f.close();
        	}
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    //删除CMPOP促销结果
    public void delBHLSPop()
    {	    	
    	// 取消赠品及折扣
    	boolean havedel = false;
    	for (int i=0;i<saleGoods.size();i++)
    	{
    		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
    		
    		// 删除赠送促销产生的商品
    		if (saleGoodsDef.flag == '1' || saleGoodsDef.flag == '5' || (goodsAssistant != null && goodsAssistant.size() > 0 && goodsAssistant.elementAt(i) == null))
    		{
    			havedel = true;
    			
    			delSaleGoodsObject(i);
    			getDeleteGoodsDisplay(i,saleGoodsDef);
    			i--;
    			continue;
    		}

    		if (saleGoodsDef.zszke > 0) havedel = true;
    		
    		// 恢复按下付款键时计算的折扣
			saleGoodsDef.zsdjbh = null;
			saleGoodsDef.zszke	= 0;
			saleGoodsDef.zszkfd = 0;
			getZZK(saleGoodsDef);
    	}

    	// 恢复商品信息
    	delRulePopReadData();
    	
    	// 恢复了商品折扣刷新界面
    	if (havedel || haveRulePop)
    	{
	    	// 重算应收
	    	calcHeadYsje();
	    	
	    	// 重刷商品列表
	    	saleEvent.updateTable(getSaleGoodsDisplay());
            saleEvent.table.setSelection(saleEvent.table.getItemCount() - 1);
            saleEvent.table.showSelection();
	    	saleEvent.setTotalInfo();
            saleEvent.setCurGoodsBigInfo();
    	}
    }
}
