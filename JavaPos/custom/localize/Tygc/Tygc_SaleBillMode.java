package custom.localize.Tygc;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Cmls.Cmls_SaleBillMode;

public class Tygc_SaleBillMode extends Cmls_SaleBillMode
{
	protected Vector salepayRemoveZL;
	protected int IsRemoveZL = 0;    //是否除去租赁商品
	Vector apportion = null;   //商品编码 商品行号 付款行号 付款方式代码 商品分摊金额
	boolean isPrintFP = false;
	String[] ss = null;
	
	static String[] b = null;
	//protected Vector salegoodsRemoveZL;
	protected boolean addTemplateeItem(PrintTemplateItem item, String curLoc)
    {
			if (curLoc.equalsIgnoreCase("General")&&(item.code.equalsIgnoreCase("IsRemoveZL")))
			{ 
				IsRemoveZL = item.rowno;
				return true;
				
			}else{
			    return 	super.addTemplateeItem(item, curLoc);
			}
    }
	
	protected String extendCase(PrintTemplateItem item, int index) 
	{
		String line = null;
		String text = item.text;
		
		if(text != null && text != "")
		{
			//开头找&&
			if(text.indexOf("&") == 0 && text.indexOf("&",1)>0)
			{
				String text1 = text.substring(text.indexOf("&")+1 ,text.indexOf("&",1));
				if(text1 != null && text1.length()>0)
				{
					String type[] = text1.split("\\|");
					if(type.length>0)
					{
						int i=0;
						for(;i<type.length;i++)
						{
							//如果当前交易类型与&&里面设的类型能匹配上，则把&&后面部分赋值给text
							String type1 = type[i];
							if(salehead.djlb.equals(type1))
							{
								text = text.substring(text.indexOf("&",1)+1);
								break;
							}
						}
						if(i>=type.length)
						{
							return "";
						}
					}
				}
				else
				{
					//&&里面没有设值，把&&后面部分赋值给text
					text = text.substring(text.indexOf("&",1)+1);
				}
			}
		}
		if(Integer.parseInt(item.code)==SBM_ybje)
		{
			SalePayDef spd = (SalePayDef) salepay.elementAt(index);
			if(IsRemoveZL == 1){
				double je = 0;
				for(int j = 0;j<apportion.size();j++){
					 //商品编码 商品行号 付款行号 付款方式代码 商品分摊金额
					String[] row = (String[]) apportion.elementAt(j);
					if(spd.paycode.trim().equals(row[3]) && spd.rowno==Integer.parseInt(row[2])){
						je = je + Double.parseDouble(row[4]);
					}
				}
				line = ManipulatePrecision.doubleToString((je)* SellType.SELLSIGN(salehead.djlb));
			}else{
				line = ManipulatePrecision.doubleToString(((SalePayDef) salepay.elementAt(index)).ybje * SellType.SELLSIGN(salehead.djlb));
			}
			
		}
		/*
		else if(Integer.parseInt(item.code)==SBM_fpje){
			if(IsRemoveZL == 1){
				
			}else{
				String[] paycodes = text.split("\\|");
				SalePayDef payDef = null;
				StringBuffer payInfo = new StringBuffer("发票金额:\n ");

				for (int i = 0; i < paycodes.length; i++)
				{
					for (int j = 0; j < salepay.size(); j++)
					{
						payDef = (SalePayDef) salepay.elementAt(j);

						if ((payDef.flag == '1') && payDef.paycode.equals(paycodes[i]))
						{
							payInfo.append(payDef.payname.trim() + ":" + ManipulatePrecision.doubleToString(payDef.ybje * SellType.SELLSIGN(salehead.djlb)) + "\n ");
						}
					}
				}

				text = "";
				line = payInfo.toString().trim();
			}
		}*/
		else if(Integer.parseInt(item.code)==SBM_payfkje){
			SalePayDef spd = (SalePayDef) salepay.elementAt(index);
			if(IsRemoveZL == 1){
				double je = 0;
				for(int j = 0;j<apportion.size();j++){
					 //商品编码 商品行号 付款行号 付款方式代码 商品分摊金额
					String[] row = (String[]) apportion.elementAt(j);
					if(spd.paycode.trim().equals(row[3]) && spd.rowno==Integer.parseInt(row[2])){
						je = je + Double.parseDouble(row[4]);
					}
				}
				line = ManipulatePrecision.doubleToString((je)* SellType.SELLSIGN(salehead.djlb));
			}else{
				line = ManipulatePrecision.doubleToString(spd.ybje * SellType.SELLSIGN(salehead.djlb));
			}
		}else if(Integer.parseInt(item.code)==SBM_ysje){
			if(IsRemoveZL == 1){
				double je = 0;
				for(int j = 0;j<apportion.size();j++){
					 //商品编码 商品行号 付款行号 付款方式代码 商品分摊金额
					String[] row = (String[]) apportion.elementAt(j);
					for(int i = 0;i<salepay.size();i++){
						SalePayDef spd = (SalePayDef) salepay.elementAt(i);
						if(spd.rowno == Integer.parseInt(row[2])&& spd.flag=='1'){
							je = je + Double.parseDouble(row[4]);
						}
					}
				}
				line = ManipulatePrecision.doubleToString((je)* SellType.SELLSIGN(salehead.djlb));
			}else{
				line = ManipulatePrecision.doubleToString(salehead.ysje * SellType.SELLSIGN(salehead.djlb));
			}
			
		}else if(Integer.parseInt(item.code)==SBM_sjfk){
			if(IsRemoveZL == 1){
				double je = 0;
				for(int j = 0;j<apportion.size();j++){
					 //商品编码 商品行号 付款行号 付款方式代码 商品分摊金额
					String[] row = (String[]) apportion.elementAt(j);
					je = je + Double.parseDouble(row[4]);
				}
				line = ManipulatePrecision.doubleToString((je)* SellType.SELLSIGN(salehead.djlb));
			}else{
				line = ManipulatePrecision.doubleToString(salehead.sjfk * SellType.SELLSIGN(salehead.djlb));
			}
		}else if(Integer.parseInt(item.code)==SBM_zl){
			if(IsRemoveZL == 1){
				double je = 0;
				for(int j = 0;j<apportion.size();j++){
					 //商品编码 商品行号 付款行号 付款方式代码 商品分摊金额
					String[] row = (String[]) apportion.elementAt(j);
					for(int i = 0;i<salepay.size();i++){
						SalePayDef spd = (SalePayDef) salepay.elementAt(i);
						if(spd.rowno == Integer.parseInt(row[2])&&spd.flag=='2'){
							je = je + Double.parseDouble(row[4]);
						}
					}
				}
				line = ManipulatePrecision.doubleToString(salehead.zl);
			}else{
				line = ManipulatePrecision.doubleToString(salehead.zl);
			}
		}
		else {
			return super.extendCase(item, index);
		}
		return line;
	}
	
	public void printDetail()
	{
		// 设置打印区域
		setPrintArea("Detail");

		// 循环打印商品明细
		for (int i = 0; i < salegoods.size(); i++)
		{
			SaleGoodsDef sgd = (SaleGoodsDef) salegoods.elementAt(i);
			//如果是除去租赁商品打印模板，且商品标识为租赁商品 则不打印该商品
			if(IsRemoveZL==1){
				boolean b = false;   
				for(int j = 0;j<apportion.size();j++){
					 //商品编码 商品行号 付款行号 付款方式代码 商品分摊金额
					String[] row = (String[]) apportion.elementAt(j);
				    if(sgd.code.equals(row[0]) && sgd.rowno == Integer.parseInt(row[1])){
				    	b = true;
				    	break;
				    }
				}
				if(!b)continue;
			}
			
			// 赠品商品不打印
			if (sgd.flag == '1')
			{
				continue;
			}

			printVector(getCollectDataString(Detail, i, Width));
		}
	}
		
	protected void printSellBill()
	{
		// GlobalInfo.sysPara.fdprintyyy = (N-不打营业员联但打印小票联/Y-打印营业员联也打印小票/A-打印营业员联但不打印小票)
    	// 非超市小票且系统参数定义只打印营业员分单，则不打印机制小票
		if (!(
			(GlobalInfo.syjDef.issryyy == 'N') || 
			(GlobalInfo.syjDef.issryyy == 'A' && ((SaleGoodsDef)salegoods.elementAt(0)).yyyh.equals("超市"))) &&
		    (GlobalInfo.sysPara.fdprintyyy == 'A')
			)
    	{
    		return;
    	}
		try{
		
		if(IsRemoveZL ==1) {
			if(salehead.printnum > 0 ){
				if(new MessageBox("是否重打印发票？", null, true).verify() != GlobalVar.Key1){
					return;
				}
				if(apportion==null){
					apportion = new Vector();
				}
				/*
				//tu测试
				apportion.removeAllElements();
//				商品编码 商品行号 付款行号 付款方式代码 商品分摊金额
				apportion.add(new String[] {"001022449","2","1","01","32.35"});
				apportion.add(new String[] {"001022449","2","2","0505","48.52"});
				apportion.add(new String[] {"001022449","2","3","0331","19.13"});
				apportion.add(new String[] {"100181","1","1","01","37.71"});
				apportion.add(new String[] {"100181","2","2","0505","47.57"});
				apportion.add(new String[] {"100181","3","3","0331","18.72"});
				*/
				if(SellType.ISSALE(salehead.djlb)){
					apportion = setApportion();
					if(apportion.size()<=0) return;
				}else if(SellType.ISBACK(salehead.djlb)){
					if(!((Tygc_NetService) Tygc_NetService.getDefault()).getSaleGoodsApportion(salehead.syjh,String.valueOf(salehead.fphm),apportion)){
						return;
					}
				}
				
			}else if(!(salehead.printnum > 0)){
				if(apportion==null){
					apportion = new Vector();
				}
				/*
				//tu测试
				apportion.removeAllElements();
//				商品编码 商品行号 付款行号 付款方式代码 商品分摊金额
				apportion.add(new String[] {"001022449","2","1","01","32.35"});
				apportion.add(new String[] {"001022449","2","2","0505","48.52"});
				apportion.add(new String[] {"001022449","2","3","0331","19.13"});
				apportion.add(new String[] {"100181","1","1","01","37.71"});
				apportion.add(new String[] {"100181","2","2","0505","47.57"});
				apportion.add(new String[] {"100181","3","3","0331","18.72"});
				*/
				if(SellType.ISSALE(salehead.djlb)){
					apportion = setApportion();
					if(apportion.size()<=0) return;
				}else if(SellType.ISBACK(salehead.djlb)){
					if(!((Tygc_NetService) Tygc_NetService.getDefault()).getSaleGoodsApportion(salehead.syjh,String.valueOf(salehead.fphm),apportion)){
						return;
					}
				}
			}
//			如果是正常销售就不穿重打原因字段
			if(salehead.printnum == 0)
			{
				String printType = "1";              //发票打印类型  1正常销售 2重打
				
				String startfph = String.valueOf(Printer.getDefault().getCurrentSaleFphm());
				String usedfphnum = "";

				String[] s = {GlobalInfo.sysPara.mktcode, ConfigClass.CashRegisterCode ,String.valueOf(salehead.fphm) ,printType ,"0" ,salehead.syyh ,startfph, usedfphnum,ManipulateDateTime.getDateTimeByClock(),"0"};
				ss = s;
			}else{
				Tygc_SaleBS ts = new Tygc_SaleBS();
				if(!ts.getReprint(salehead)) return ;
			}
			
		}
			
        // 设置打印方式
        printSetPage();

        // 多联小票打印不同抬头
		printDifTitle();
		
        // 打印头部区域
        printHeader();

        // 打印明细区域
        printDetail();

        // 打印汇总区域
        printTotal();

        // 打印付款区域
        printPay();

        // 打印尾部区域
        printBottom();

        // 打印赠品联
        printGift();

        // 切纸
        printCutPaper();
        
        if(IsRemoveZL ==1) {
        	if(salehead.printnum == 0)
    		{
        		double kpje = calcPayFPMoney();
        		ss[9] = String.valueOf(kpje);
    			ss[7] = String.valueOf(Printer.getDefault().getCurrentSaleFphm()-1);
    			new Tygc_NetService().postReprint(ss);
    		}else{
    			String[] s = Tygc_SaleBS.getResult();   //上传字段
    			double kpje = calcPayFPMoney();
        		s[9] = String.valueOf(kpje);
				s[7] = String.valueOf(Printer.getDefault().getCurrentSaleFphm()-1);
				new Tygc_NetService().postReprint(s);
    		}
        }
		}catch(Exception er)
		{
			er.printStackTrace();
		}
    }
	



	private Vector setApportion()
	{
		Vector vapp = new Vector();
		double hjje = salehead.ysje;
		for(int j = 0;j<salepay.size();j++){
			SalePayDef spd = (SalePayDef) salepay.elementAt(j);
			if(spd.flag!= '1') continue;
			double yft = 0;   //付款方式已分摊金额
			
			for(int i = 0;i<salegoods.size();i++){
				SaleGoodsDef sgd = (SaleGoodsDef) salegoods.elementAt(i);
				
				double ftje = 0;
				if(i+1 == salegoods.size()){
//					商品编码 商品行号 付款行号 付款方式代码 商品分摊金额
					if(sgd.str6.trim().equals("5")){
						vapp.add(new String[]{sgd.code,""+sgd.rowno,""+spd.rowno,spd.paycode,""+(spd.je-yft)});
					}
				}else{
					ftje = (ManipulatePrecision.sub(sgd.hjje,sgd.hjzk)/hjje)*spd.je;
					SaleBS sbs = new SaleBS();
					ftje = sbs.getDetailOverFlow(ftje);
					yft = sbs.getDetailOverFlow(yft+ftje);
					if(sgd.str6.trim().equals("5")){
						vapp.add(new String[]{sgd.code,""+sgd.rowno,""+spd.rowno,spd.paycode,""+ftje});
					}
				}
			
			}
			
		}
		
		return vapp;
	}

	public boolean checkIsPrint(String template)
	{
		return true;
	}
	
	protected double calcPayFPMoney()
	{
		if(IsRemoveZL==1){
			double je = 0;
			for(int j = 0;j<apportion.size();j++){
				 //商品编码 商品行号 付款行号 付款方式代码 商品分摊金额
				String[] row = (String[]) apportion.elementAt(j);
				je = je + Double.parseDouble(row[4]);
			}
			String payex = "," + GlobalInfo.sysPara.fpjepayex + ",";
			for(int j = 0;j<apportion.size();j++){
				 //商品编码 商品行号 付款行号 付款方式代码 商品分摊金额
				String[] row = (String[]) apportion.elementAt(j);
				if (payex.indexOf("," + row[3] + ",") >= 0){
					je = je -Double.parseDouble(row[4]);
				}
			}
			
			return je;
		}else{
			return super.calcPayFPMoney();
		}
		
		
		
		
		/*
		double je = salehead.sjfk - salehead.zl;

		String payex = "," + GlobalInfo.sysPara.fpjepayex + ",";
		for (int i = 0; i < salepay.size(); i++)
		{
			SalePayDef sp = (SalePayDef) salepay.elementAt(i);
			if (sp.flag == '1' && payex.indexOf("," + sp.paycode + ",") >= 0)
			{
				je -= sp.je;
				
				for (int j = 0; j < salepay.size();j++)
				{
					SalePayDef sp1 = (SalePayDef) salepay.elementAt(j);
					if (sp1.flag == '2' && sp1.paycode.equals(sp.paycode))
					{
						je += sp1.je;
					}
				}
			}
		}
		*/
		
	}
	
//	 打印赠券
	  public void printSaleTicketMSInfo()
	  {
		  
	    try
	    {
	     // HisensePT900TAParallel_Printer pt900 = (HisensePT900TAParallel_Printer)Printer.getDefault().printer;

	      if ((this.salemsinvo == 0L) || ((this.salemsinvo != 0L) && (this.salehead.fphm != this.salemsinvo)))
	      {
	        this.salemsinvo = 0L;
	        this.zq = null;
	        this.gift = null;

	        return;
	      }

	      if ((this.zq == null) || (this.zq.size() <= 0))
	      {
	        return;
	      }

	      String line=null;
	      for (int i = 0; i < this.zq.size(); ++i)
	      {
	        GiftGoodsDef def = (GiftGoodsDef)this.zq.elementAt(i);

	        String[] infos = def.info.split("_");

	        if ((!(def.type.trim().equals("1"))) && (!(def.type.trim().equals("2"))))
	        {
	          continue;
	        }

	        String lab = "";

	        if (this.salehead.printnum > 0)
	        {
	          lab = lab + "  (重打印)";
	        }

	        printLine("     " + lab);
	        printLine("       " + GlobalInfo.sysPara.mktname);

	        if (infos.length > 1)
	        {
	          printLine(" " + infos[1]);
	        }

	        printLine("");
	        printLine("=====================================");

	        printLine("券号: " + def.code);
	        
	        char[] a = { 0x1D, 0x48, 0x01 };
			char[] b = { 0x1D, 0x6B, 0x43 ,0x0D };
			
			printLine(String.valueOf(a)+String.valueOf(b) + def.code);
	        printLine("单据号:   " + this.salehead.fphm + "   收银机号:" + ConfigClass.CashRegisterCode);
	        printLine("发券日期: " + GlobalInfo.balanceDate);

	        
//	        if (infos.length > 0)
//	        {
//	          printLine("券信息:   " + infos[0]);
//	        }
	        
	        printLine("");
	        printLine("-------------------------------------");
	       
	        
	      //  pt900.setBigChar(true);
	        
	        line = Convert.appendStringSize("",def.info.substring(0,def.info.indexOf(";"))+":",0,10,8); 
	        printLine(Convert.appendStringSize(line,ManipulatePrecision.doubleToString(def.je) + "元",10,12,38));
//	        printLine(" " + ManipulatePrecision.doubleToString(def.je) + "元");
	        
	      //  pt900.setBigChar(false);

	        printLine("-------------------------------------");
	        
	      //  pt900.setBigChar(true);
	        
	        printLine(def.info.substring(def.info.indexOf(";")+1,def.info.length()));
	        
	       // pt900.setBigChar(false);

//	        printLine("def.info");

	        printLine("");

	        if (infos.length > 2)
	        {
	          printLine("本券使用范围：" + infos[2]);
	        }

	        String[] r = def.memo.split(";");
	        String startdate = "";
	        String enddate = "";

	        if (r.length > 1)
	        {
	          startdate = r[0].trim();
	          enddate = r[1].trim();
	        }
	        else
	        {
	          enddate = def.memo;
	        }

	        printLine("·本券使用开始日期：" + startdate);
	        printLine("·本券使用有效期至：" + enddate);

	        if (r.length > 2)
	        {
	          printLine(r[2]);
	        }

	        printLine("");
	        printLine("");
	        printLine("");
	        printLine("");
	        printLine("");
	        printLine("");
	        printLine("");

	        printCutPaper();
	      }
	    }
	    catch (Exception ex) {
	      new MessageBox("打印异常:\n" + ex.getMessage());
	    }
	  }
}
