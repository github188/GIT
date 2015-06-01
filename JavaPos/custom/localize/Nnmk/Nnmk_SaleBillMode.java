package custom.localize.Nnmk;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.GiftBillMode;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.PrintTemplate.YyySaleBillMode;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Cmls.Cmls_SaleBillMode;

public class Nnmk_SaleBillMode extends Cmls_SaleBillMode
{
	public final static int NSBM_YHJF = 301; // 印花积分
	public final static int NSBM_GWKYE = 302; // 购物卡余额
	
	protected String extendCase(PrintTemplateItem item, int index) 
	{
		String line = null;
		switch (Integer.parseInt(item.code))
		{
			case NSBM_YHJF:
                if (salehead.num5 == 0)
                {
                    line = "&!";
                }
                else
                {
                    line = ManipulatePrecision.doubleToString(salehead.num5);
                }
				break;
				
			case NSBM_GWKYE:
				SalePayDef spd =((SalePayDef) salepay.elementAt(index));
				String code = spd.paycode;
				for (int i=0;i<ConfigClass.CustomPayment.size();i++)
	    		{
	    			String s = (String)ConfigClass.CustomPayment.elementAt(i);
	    			String[] sp = s.split(",");
	    			if (sp.length <= 0) continue;
	    			int j;
	    			for (j=1;j<sp.length;j++)
	    			{
	    				if (code.equalsIgnoreCase(sp[j].trim())) break;
	    			}
	    			if (j >= sp.length) continue;
	    			
	    			if (sp[0].indexOf("Bank_PaymentMzk")>=0)
	    			{
	    				line = ManipulatePrecision.doubleToString(spd.kye);
	    			}
	    		}
				break;
			case SBM_goodname:
				line = ((SaleGoodsDef) salegoods.elementAt(index)).name;
				if (line.indexOf("-(") >= 0)
				{
					line = line.substring(line.indexOf("-(")+1);
				}
				break;
		}
		return line;
	}
	
	public void setSaleTicketMSInfo(SaleHeadDef sh,Vector gifts)
	{
		// 记录小票赠送清单
		this.salemsinvo = sh.fphm;
		this.salemsgift = gifts;
		
        // 分解赠品清单
        Vector goodsinfo = new Vector();
        Vector fj = new Vector();
        for (int i = 0; gifts != null && i < gifts.size(); i++)
        {
            GiftGoodsDef g = (GiftGoodsDef)gifts.elementAt(i);

            if (g.type.trim().equals("0"))
            {
                //无促销
                break;
            }
            else if (g.type.trim().equals("1") || g.type.trim().equals("2"))
            {
                fj.add(g);
            }
            else if (g.type.trim().equals("3"))
            {
            	fj.add(g);
            }
            else if (g.type.trim().equals("4"))
            {
            	fj.add(g);
            }
            else if (g.type.trim().equals("11"))
            {
            	fj.add(g);
            }
            else if (g.type.trim().equals("90")) // 停车券
            {
            	fj.add(g);
            }
            else if (g.type.trim().equals("89")) // 停车券
            {
            	fj.add(g);
            }
            else if (g.type.trim().equals("88")) // 定金券
            {
            	fj.add(g);
            }
        }
        
        // 提示
        StringBuffer buff = new StringBuffer();
        double je = 0;
        boolean zp = false;
        for (int i = 0 ; i < fj.size(); i++)
        {
        	GiftGoodsDef g = (GiftGoodsDef)fj.elementAt(i);
        	
        	if (g.type.trim().equals("90") || g.type.trim().equals("89")) // 停车券
        	{
        		continue;
        	}
        	
        	if (g.type.trim().equals("3"))
        	{
        		zp =true;
        		continue;
        	}
        	
        	String l = Convert.appendStringSize("",g.info,1,16,17,1);
        	
        	buff.append(l+":"+Convert.appendStringSize("",ManipulatePrecision.doubleToString(g.je),1,10,10,0)+"\n");
        	//buff.append(g.code+"   "+g.info+"      "+Convert.increaseChar(ManipulatePrecision.doubleToString(g.je), 14)+"\n");
        	je += g.je;
        }
        buff.append(Convert.increaseChar("-", '-',27)+"\n");
        buff.append("总金额为: "+ManipulatePrecision.doubleToString(je)+"\n");
        
        String line = "本笔交易存在赠品，请顾客到服务台领取";
        if (je > 0)
        {
        	if (zp) buff.append(line);
        	new MessageBox(buff.toString());
        }
        else
        {
        	if (zp) new MessageBox(line);
        }
        
        
        // 设置
        if (fj.size() > 0) this.zq = fj;
        else this.zq = null;
        if (goodsinfo.size() > 0) this.gift = goodsinfo;
        else this.gift = null;
	}
    
    public void printBottom()
    {
    	
    	super.printBottom();
    	
    	if (SellType.RETAIL_SALE .equals(salehead.djlb) && this.salemsinvo != 0 && salehead.fphm == this.salemsinvo)
    	{
    		try{
    			boolean zpinfo = true;
		    	// 打印券信息
		    	for (int i = 0; this.zq != null &&  i < this.zq.size(); i ++)
		    	{

		    		
		    		GiftGoodsDef def = (GiftGoodsDef) this.zq.elementAt(i);
		    		if (def.type.trim().equals("3") || def.type.trim().equals("99") ||  def.type.trim().equals("4") || def.type.trim().equals("89") || def.type.trim().equals("90")) continue;
		    		
		    		if (zpinfo)
		    		{
		    			 Printer.getDefault().printLine_Normal(Convert.appendStringSize("", "赠券信息：", 0, 37, 38));
		    			 zpinfo = false;
		    		}
		    		
		    		String l = Convert.appendStringSize("",def.info, 0, 10, 38);
		    		l = Convert.appendStringSize(l,ManipulatePrecision.doubleToString(def.je), 12, 8, 38);
		    		Printer.getDefault().printLine_Normal(l);
		    		l = Convert.appendStringSize("",def.memo, 0, 38, 38);
		    		Printer.getDefault().printLine_Normal(l);
		    	}
    		}catch(Exception er)
    		{
    			er.printStackTrace();
    			new MessageBox(er.getMessage());
    		}
    	}
    	
    	// 检查是否打印积分消费联
    	if (SellType.RETAIL_SALE .equals(salehead.djlb) || SellType.PREPARE_SALE.equals(salehead.djlb) || SellType.ISSALE(salehead.djlb) || SellType.RETAIL_BACK.equals(salehead.djlb)) printJfxf();
    }
    
    public void printJfxf()
    {
    	try{
    	for (int i = 0 ; i < salepay.size(); i++)
    	{
    		
    		SalePayDef sp = (SalePayDef) salepay.elementAt(i);
    		if (sp.paycode.equals("0509") || CreatePayment.getDefault().isPaymentJfxf(sp))
    		{
    			Printer.getDefault().printLine_Journal(Convert.appendStringSize("", " ", 1, 37, 38));
    			Printer.getDefault().printLine_Journal(Convert.appendStringSize("", " 积分消费单", 1, 37, 38));
    			Printer.getDefault().printLine_Journal(Convert.appendStringSize("", " 收银员联", 1, 37, 38));
    			//收银机号，小票号，店名
    			Printer.getDefault().printLine_Journal("收银机号："+GlobalInfo.syjDef.syjh+" 小票号"+salehead.fphm);
    			Printer.getDefault().printLine_Journal("店名: "+GlobalInfo.sysPara.mktname);
    			Printer.getDefault().printLine_Journal(Convert.appendStringSize("", " VIP卡号: "+sp.payno, 1, 37, 38));
    			
    			String salepaylist[] = sp.idno.split(",");
    			if (SellType.SELLSIGN(salehead.djlb) > 0)Printer.getDefault().printLine_Journal(Convert.appendStringSize("", " 扣减积分：-"+salepaylist[0].trim(), 1, 37, 38));
    			else Printer.getDefault().printLine_Journal(Convert.appendStringSize("", " 退返积分："+salepaylist[0].trim(), 1, 37, 38));
    			Printer.getDefault().printLine_Journal(Convert.appendStringSize("", " ", 1, 37, 38));
    			Printer.getDefault().printLine_Journal(Convert.appendStringSize("", " 经收人："+salehead.syyh, 1, 37, 38));
    			Printer.getDefault().printLine_Journal(Convert.appendStringSize("", " 操作时间："+ManipulateDateTime.getCurrentDateBySign()+" "+ManipulateDateTime.getCurrentTime(), 1, 37, 38));
    			Printer.getDefault().printLine_Journal(Convert.appendStringSize("", " ", 1, 37, 38));
    			Printer.getDefault().printLine_Journal(Convert.appendStringSize("", " ", 1, 37, 38));
    			Printer.getDefault().printLine_Journal(Convert.appendStringSize("", " 			客户签名____________________________", 1, 37, 38,2));
    			Printer.getDefault().printLine_Journal(Convert.appendStringSize("", " ", 1, 37, 38));
    			Printer.getDefault().printLine_Journal(Convert.appendStringSize("", " ", 1, 37, 38));
    			Printer.getDefault().printLine_Journal(Convert.appendStringSize("", " 本次消费："+ManipulatePrecision.doubleToString(sp.je*SellType.SELLSIGN(salehead.djlb)), 1, 37, 38));
    			Printer.getDefault().printLine_Journal(Convert.appendStringSize("", " ", 1, 37, 38));
    			Printer.getDefault().printLine_Journal(Convert.appendStringSize("", " ", 1, 37, 38));
    			Printer.getDefault().printLine_Journal(Convert.appendStringSize("", " =============此处撕开=====================", 1, 37, 38));

    			Printer.getDefault().cutPaper_Journal();

    		}

    	}
    	}catch(Exception er)
    	{
    		er.printStackTrace();
    	}
    	
    }
    
    public void printGWKInfo()
    {
    	BufferedReader br = null;
    	
    	//先查询是否存在GWK付款方式
		// 在原始付款清单中,查找是否有银联卡付款方式
    	String zhcode = "";
		for (int i = 0; i < originalsalepay.size(); i++)
		{
			SalePayDef pay = (SalePayDef) originalsalepay.elementAt(i);
			PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);

			if ((mode.isbank == 'Y') && (pay.batch != null) && (pay.batch.length() > 0))
			{
				// 存在购物卡
				if (CreatePayment.getDefault().getConfigBankFunc(pay.paycode).getClass().getName().indexOf("ZhGWK") >=0)
				{
					zhcode = pay.paycode;
					break;
				}
			}
		}
		
		if (zhcode.length() <= 0) return;
    	
    	String file = "C:\\GWK";
		br = CommonMethod.readFileGBK(file+"\\gwkpub.txt");
		String line;
		try
		{
			if (br == null) return;
			
			line = br.readLine();
			
			if (line == null ) return;
			br.close();
			
			String[] lines = line.split("\\|");
			String shh = lines[2];
			String zdh = lines[3];
			
			Printer.getDefault().printLine_Normal("------------联名卡留存------------");
			Printer.getDefault().printLine_Normal((salehead.printnum > 0?"--------------重打印--------------":""));
			Printer.getDefault().printLine_Normal("终端号:"+zdh+" 商户号:"+shh);
			Printer.getDefault().printLine_Normal("POS机号："+GlobalInfo.syjDef.syjh+" 收银员号:"+GlobalInfo.posLogin.gh+"-"+GlobalInfo.posLogin.name);
			
			Printer.getDefault().printLine_Normal("卡号      消费金额         凭证号");
			
			double je1 = 0;
			// 从配备文件里面查询打印文件进行
			for (int i = 0; i < originalsalepay.size(); i++)
			{
				SalePayDef pay = (SalePayDef) originalsalepay.elementAt(i);
				
				//PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);
				if (pay.paycode.equals(zhcode))
				{
					je1 = ManipulatePrecision.add(je1, pay.je);
					String tracefile = "BankDoc\\"+GlobalInfo.balanceDate.replaceAll("/", "")+"\\"+"bankdoc_" + pay.batch + ".txt";
					if (PathFile.fileExist(GlobalVar.HomeBase+"\\"+tracefile)) br = CommonMethod.readFileGBK(GlobalVar.HomeBase+"\\"+tracefile);
					
					if (br == null)
					{
						new MessageBox("BankDoc里没有找到流水号为"+pay.batch+"签购单文件");
						return;
					}
					
					line = null;
					while ((line = br.readLine())!= null)
					{
						
						lines = line.split("\\|");
						String jylx = lines[0].trim();
						String kh = lines[1].trim();
						String je = lines[2].trim();
						String pzh = lines[3].trim();
						//String sqm = lines[4];
						//String CKH = lines[5];
						String BZ = lines[7].trim();
						
						String row = Convert.appendStringSize("", kh, 0, 20, 38);
						row = Convert.appendStringSize(row, je, 21, 9, 38);
						row = Convert.appendStringSize(row, pzh, 31, 8, 38);
						//new MessageBox(line+"\n"+je+"\n"+row);
						Printer.getDefault().printLine_Normal(row);
						row = Convert.appendStringSize("", jylx, 0, 16, 38);
						row = Convert.appendStringSize(row, BZ, 17, 17, 38);
						Printer.getDefault().printLine_Normal(row);
						
					}
					br.close();
				}
			}
			Printer.getDefault().printLine_Normal("总金额  ："+ManipulatePrecision.doubleToString(je1));
			Printer.getDefault().printLine_Normal("打印时间:"+ManipulateDateTime.getCurrentDate()+" "+ManipulateDateTime.getCurrentTime());
			Printer.getDefault().cutPaper_Normal();
			
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
				
		//PathFile.deletePath(file+"\\gwkpub.txt");

    }
    
	protected void printSellBill()
	{
		printGWKInfo();
		
		if (SellType.isJS(salehead.djlb))
		{
			 // 设置打印方式
	        printSetPage();
	        for (int i = 0 ; i < 3 ; i ++)
	        {
				//第一次不打印商品明细
		        if (i == 0 )Printer.getDefault().printLine_Normal(Convert.appendStringSize("", "供应商应缴款代垫，费用列表", 1, 37, 38));
		        if (i == 1 || i == 2 )Printer.getDefault().printLine_Normal(Convert.appendStringSize("", "费用交款凭证", 1, 37, 38,2));
		       
		        if (salehead.printnum >= 1) Printer.getDefault().printLine_Normal("-----重打印------");
		        if (i == 1 ) Printer.getDefault().printLine_Normal(Convert.appendStringSize("", "供应商代垫,费用", 1, 37, 38));
		        if (i == 2 ) Printer.getDefault().printLine_Normal(Convert.appendStringSize("", "供应商代垫,费用（交结算窗）", 1, 37, 38));
		        Printer.getDefault().printLine_Normal(Convert.appendStringSize("", "====================================", 1, 37, 38));
		        if (i == 1|| i== 2)Printer.getDefault().printLine_Normal(Convert.appendStringSize("", "交费单号"+((SaleGoodsDef)salegoods.elementAt(0)).barcode, 1, 37, 38));
		        Printer.getDefault().printLine_Normal(Convert.appendStringSize("", "费用分店："+GlobalInfo.sysPara.mktcode, 1, 37, 38));
		        Printer.getDefault().printLine_Normal(Convert.appendStringSize("", "供应商号:"+((SaleGoodsDef)salegoods.elementAt(0)).catid, 1, 37, 38));
		        //ManipulateDateTime mdt  = new ManipulateDateTime();
		        Printer.getDefault().printLine_Normal(Convert.appendStringSize("", "会计时间："+((SaleGoodsDef)salegoods.elementAt(0)).fhdd, 1, 37, 38));
		        //Printer.getDefault().printLine_Normal(Convert.appendStringSize("", "开票金额："+((SaleGoodsDef)salegoods.elementAt(0)).num1, 1, 37, 38));

			    if (i != 2)	   Printer.getDefault().printLine_Normal(Convert.appendStringSize("", "代垫，费用项目			金额", 1, 37, 38));
			    if (i != 2)    Printer.getDefault().printLine_Normal(Convert.appendStringSize("", "------------------------------------", 1, 37, 38));
			    double je = 0;
		        for (int j = 0; j < salegoods.size(); j++)
		        {
		        	SaleGoodsDef def = (SaleGoodsDef) salegoods.elementAt(j);
		        	String line = Convert.appendStringSize("", def.name, 1, 20, 38);
		        	line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(def.hjje), 25, 10, 38,2);
		        	if (i != 2) Printer.getDefault().printLine_Normal(line);
		        	je = ManipulatePrecision.doubleConvert(je + def.num1);
		        }
		        
		        if (i != 2)Printer.getDefault().printLine_Normal(Convert.appendStringSize("", "------------------------------------", 1, 37, 38));
	        
		        Printer.getDefault().printLine_Normal(Convert.appendStringSize("", "费用合计:"+ManipulatePrecision.doubleToString(salehead.hjzje), 1, 37, 38));
		        //Printer.getDefault().printLine_Normal(Convert.appendStringSize("", "全部费用项目合计:"+ManipulatePrecision.doubleToString(salehead.hjzje), 1, 37, 38));
		        if (i == 1 || i==2)
		        {
		        	Printer.getDefault().printLine_Normal(Convert.appendStringSize("", "开票金额:"+ManipulatePrecision.doubleToString(je), 1, 37, 38));
		        	Printer.getDefault().printLine_Normal("收费签章");
		        	Printer.getDefault().printLine_Normal("");
		        	Printer.getDefault().printLine_Normal("");
		        	Printer.getDefault().printLine_Normal("");
		        	Printer.getDefault().printLine_Normal("");
		        	Printer.getDefault().printLine_Normal("");
		        	Printer.getDefault().printLine_Normal("------------------------------------");
		        	
		        }
		        Printer.getDefault().printLine_Normal("分店："+GlobalInfo.sysPara.mktname);
		        Printer.getDefault().printLine_Normal("收银员号："+GlobalInfo.posLogin.name+"-"+GlobalInfo.posLogin.gh);
		        Printer.getDefault().printLine_Normal("POS机号："+salehead.syjh);
		        
		        Printer.getDefault().printLine_Normal("打印时间: "+ManipulateDateTime.getCurrentDate()+" "+ManipulateDateTime.getCurrentTime());
		        Printer.getDefault().printLine_Normal("注：涂改无效，遗失不补");
		        // 切纸
		        printCutPaper();
	        }
		}
		else if (SellType.isJF(salehead.djlb))
		{
			for (int i = 0 ; i < 2; i++)
			{
//				第一次不打印商品明细
		        Printer.getDefault().printLine_Normal(Convert.appendStringSize("", "收       据", 0, 37, 38,2));
		        Printer.getDefault().printLine_Normal(Convert.appendStringSize("","****************************************", 0, 37, 38));
		        if (salehead.printnum > 1) Printer.getDefault().printLine_Normal("-----重打印------");
		        Printer.getDefault().printLine_Normal(Convert.appendStringSize("", "收银机号:"+salehead.syjh+"      小票号:"+salehead.fphm, 0, 37, 38));
		       
		        Printer.getDefault().printLine_Normal(Convert.appendStringSize("", "     费用项目       付款金额", 0, 37, 38));
		        Printer.getDefault().printLine_Normal(Convert.appendStringSize("", "------------------------------------", 0, 37, 38));
		        //double je = 0;
		        for (int j = 0; j < salegoods.size(); j++)
		        {
		        	SaleGoodsDef def = (SaleGoodsDef) salegoods.elementAt(j);
		        	String line = Convert.appendStringSize("", def.code +" "+def.name, 1, 20, 38);
		        	line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(def.hjje * SellType.SELLSIGN(salehead.djlb)), 25, 10, 38,2);
		        	Printer.getDefault().printLine_Normal(line);
		        }
		        
		        Printer.getDefault().printLine_Normal(Convert.appendStringSize("", "------------------------------------", 0, 37, 38));
		        Printer.getDefault().printLine_Normal(Convert.appendStringSize("", "费用合计:"+ManipulatePrecision.doubleToString(salehead.hjzje * SellType.SELLSIGN(salehead.djlb)), 0, 37, 38));
		        Printer.getDefault().printLine_Normal(Convert.appendStringSize("", "单位名称:", 0, 37, 38));

	        	Printer.getDefault().printLine_Normal("收费签章");
	        	Printer.getDefault().printLine_Normal("");
	        	Printer.getDefault().printLine_Normal("");
	        	Printer.getDefault().printLine_Normal("");
	        	Printer.getDefault().printLine_Normal("");
	        	Printer.getDefault().printLine_Normal("");
	        	Printer.getDefault().printLine_Normal("------------------------------------");
	        	
		        Printer.getDefault().printLine_Normal("分店："+GlobalInfo.sysPara.mktname);
		        Printer.getDefault().printLine_Normal("收银员号："+GlobalInfo.posLogin.name+"-"+GlobalInfo.posLogin.gh);
		        Printer.getDefault().printLine_Normal("POS机号："+salehead.syjh);
		        
		        Printer.getDefault().printLine_Normal("打印时间: "+ManipulateDateTime.getCurrentDate()+" "+ManipulateDateTime.getCurrentTime());
		        Printer.getDefault().printLine_Normal("注：涂改无效，遗失不补");
		        // 切纸
		        printCutPaper();
			}
		}
		else
		{
			super.printSellBill();
		}
	}
    
	//打印赠券
	public void printSaleTicketMSInfo()
	{
		if (this.zq == null || this.zq.size() <= 0)
		{
			return ;
		}
		
		if (this.salemsinvo != 0 && salehead.fphm != this.salemsinvo)
		{
			this.salemsinvo = 0;
			this.zq = null;
			this.gift = null;
			return ;
		}
		
		int isTCQ = 0;
		for (int i = 0; i < this.zq.size(); i++)
		{
			GiftGoodsDef def = (GiftGoodsDef) this.zq.elementAt(i);
			if (!def.type.trim().equals("99") && !def.type.trim().equals("4"))
			{
				
	            if(GiftBillMode.getDefault(def.type).checkTemplateFile())
	            {
	            	if (def.type.trim().equals("90"))
	            	{
	            		
	            		if (isTCQ == 0 )
	            		{
	            			if (new MessageBox("是否打印停车券？",null,true).verify() == GlobalVar.Key1)
	            			{
	            				isTCQ = 1;
	            			}
	            			else
	            			{
	            				isTCQ = -1;
	            			}
	            		}
	            		
	            		if (isTCQ == 1)
	            		{
			            	 Printer.getDefault().printLine_Normal(Convert.appendStringSize("", "=================================================", 1, 37, 38));
			            	 Printer.getDefault().printLine_Normal(Convert.appendStringSize("", "==========由此处撕开=========", 1, 37, 38));
			            	 GiftBillMode.getDefault(def.type).setTemplateObject(salehead, def);
			            	 GiftBillMode.getDefault(def.type).PrintGiftBill();
			            	 GiftBillMode.getDefault(def.type).printCutPaper();
	            		}
	            	}
	            	else
	            	{
		            	 Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "=================================================", 1, 37, 38));
		            	 Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "==========由此处撕开=========", 1, 37, 38));
		            	 
		            	 if (def.type .equals("88"))
		            	 {
		            		 Printer.getDefault().printLine_Normal(Convert.appendStringSize("", "预 交 款 凭 证", 1, 37, 38,2));
		            		 Printer.getDefault().printLine_Normal(Convert.increaseChar("", '-', 37));
		            		 Printer.getDefault().printLine_Normal("预交款凭证号ID:"+def.code);
		            		 Printer.getDefault().printLine_Normal("预交款金额:"+ManipulatePrecision.doubleToString(def.je));
		            		 Printer.getDefault().printLine_Normal("关联商品名称:");
		            		 for (int j = 0 ; j < salegoods.size(); j ++)
		            		 {
		            			 SaleGoodsDef sgd = (SaleGoodsDef) salegoods.elementAt(j);
		            			 String line = Convert.appendStringSize("", sgd.name, 0, 20, 38);
		            			 line = Convert.appendStringSize(line, sgd.code, 22, 15, 38);
		            			 Printer.getDefault().printLine_Normal(line);
		            		 }
		            		 ManipulateDateTime mdt = new ManipulateDateTime();
		            		 Printer.getDefault().printLine_Normal("有效截止日期:"+def.memo);
		            		 Printer.getDefault().printLine_Normal("开具签单:");
		            		 Printer.getDefault().printLine_Normal("");
		            		 Printer.getDefault().printLine_Normal("");
		            		 Printer.getDefault().printLine_Normal("");
		            		 Printer.getDefault().printLine_Normal("");
		            		 Printer.getDefault().printLine_Normal("");
		            		 Printer.getDefault().printLine_Normal("说明:"+Convert.increaseChar("", '_', 32));
		            		 Printer.getDefault().printLine_Normal(""+Convert.increaseChar("", '_', 37));
		            		 Printer.getDefault().printLine_Normal("收取签章:");
		            		 Printer.getDefault().printLine_Normal("");
		            		 Printer.getDefault().printLine_Normal("");
		            		 Printer.getDefault().printLine_Normal("");
		            		 Printer.getDefault().printLine_Normal("");
		            		 Printer.getDefault().printLine_Normal("");
		            		 Printer.getDefault().printLine_Normal(""+Convert.increaseChar("", '-', 37));
		            		 Printer.getDefault().printLine_Normal("分店:"+GlobalInfo.sysPara.mktcode+" "+GlobalInfo.sysPara.mktname);
		            		 Printer.getDefault().printLine_Normal("机号:"+salehead.syjh+"  销售单号:"+salehead.fphm);
		            		 Printer.getDefault().printLine_Normal("收银员:+"+GlobalInfo.posLogin.gh+" "+GlobalInfo.posLogin.name);
		            		 Printer.getDefault().printLine_Normal("打印时间:"+mdt.getDateTimeString());
		            		 Printer.getDefault().printLine_Normal("注:请在有效期内使用预收凭证!涂改无效!");
		            		 Printer.getDefault().printLine_Normal("该预收款凭证仅限当店使用！");
		            		 Printer.getDefault().printLine_Normal("    不记名，不挂失，遗失不补！");
		            		 Printer.getDefault().printLine_Normal(""+Convert.increaseChar("", '-', 37));
		            		 
		            		 String line = Convert.appendStringSize("", "应付:", 0, 5, 38);
	            			 line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(salehead.ysje), 6, 9, 38,1);
	            			 line = Convert.appendStringSize(line, "实付:", 16, 5, 38);
	            			 line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(salehead.sjfk), 22, 9, 38,1);
		            		 Printer.getDefault().printLine_Normal(line);
		            		 for (int x = 0; x < originalsalepay.size();x++)
		            		 {
		            			 SalePayDef spd = (SalePayDef) originalsalepay.elementAt(x);
		            			 String line1 = spd.payname;
		            			 line1 += spd.payno != null?spd.payno:"";
		            			 line1 += ":   "+ ManipulatePrecision.doubleToString(spd.je);
		            			 Printer.getDefault().printLine_Normal(line1);
		            		 }
		            		 line = Convert.appendStringSize("", "回找:", 0, 5, 38);
	            			 line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(salehead.zl), 6, 9, 38,1);
	            			 line = Convert.appendStringSize(line, "折扣:", 16, 5, 38);
	            			 line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(salehead.hjzke), 22, 9, 38,1);
		            		 Printer.getDefault().printLine_Normal(line);
		            		 Printer.getDefault().cutPaper_Normal();
		            		 continue;
		            		 
		            	 }
		            	 
		            	 GiftBillMode.getDefault(def.type).setTemplateObject(salehead, def);
		            	 GiftBillMode.getDefault(def.type).PrintGiftBill();
		            	 GiftBillMode.getDefault(def.type).printCutPaper();
	            	}
	            	continue;
	            }
	            
				Printer.getDefault().printLine_Journal("收银机号："+salehead.syjh+"  小票号："+Convert.increaseLong(salehead.fphm, 8));
				if (SellType.ISCOUPON(salehead.djlb))
					Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "买券交易", 1, 37, 38,2));

	            Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "				手 工 券", 1, 37, 38));
	            Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "=================================================", 1, 37, 38));
				Printer.getDefault().printLine_Journal("== 券  号  : "+def.code);
				Printer.getDefault().printLine_Journal("== 券信息  : "+def.info);
				Printer.getDefault().printLine_Journal("== 券总额  : "+def.je);
	            if (salehead.printnum > 0)
	        	{
	            	Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "				重 打 印", 1, 37, 38));
	        	}
				Printer.getDefault().printLine_Journal("== 券有效期: "+def.memo);
				Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "=================================================", 1, 37, 38));
				Printer.getDefault().cutPaper_Journal();
			}
		}
	}
	
	public void printBill()
	{
		int choice = GlobalVar.Key1;

		// 开始打印前的发票号
		salefph = Printer.getDefault().getCurrentSaleFphm();

		// 重打印小票时，如果是非超市且参数定义既打印机制单又打营业员联，才提示选择打印部分
		if (('N' != (GlobalInfo.syjDef.issryyy)) && (salehead.printnum > 0) && GlobalInfo.sysPara.fdprintyyy == 'Y')
		{
			StringBuffer info = new StringBuffer();
			info.append(Convert.appendStringSize("", "请按键选择重打印内容", 1, 30, 30, 2) + "\n");
			info.append(Convert.appendStringSize("", "1、打印全部小票单据", 1, 30, 30, 2) + "\n");
			info.append(Convert.appendStringSize("", "2、只打印机制小票单", 1, 30, 30, 2) + "\n");
			info.append(Convert.appendStringSize("", "3、只打印营业员列印", 1, 30, 30, 2) + "\n");
			info.append(Convert.appendStringSize("", "按其他键则放弃重打印", 1, 30, 30, 2) + "\n");

			choice = new MessageBox(info.toString(), null, false).verify();
		}

		int num = 1;
		boolean sequenceflag = true;
		if (GlobalInfo.sysPara.printyyhsequence == 'B')
			sequenceflag = false;

		while (num <= 2)
		{
			if (sequenceflag)
			{
				if ((choice == GlobalVar.Key1) || (choice == GlobalVar.Key3))
				{
					// 结算和缴费交易时不打印列印
					if (!SellType.isJF(salehead.djlb) && !SellType.isJS(salehead.djlb)) 
					{
						if (((YyySaleBillMode) YyySaleBillMode.getDefault()).isLoad())
						{
							printYyyBillPrintMode();
						}
						else
						{
							// 打印营业员分单联
							printYYYBill();
						}
					}
				}

				sequenceflag = false;
			}
			else
			{
				if ((choice == GlobalVar.Key1) || (choice == GlobalVar.Key2))
				{
					// 根据参数控制打印销售小票的份数
					printnum = 0;
					for (int salebillnum = 0; salebillnum < GlobalInfo.sysPara.salebillnum; salebillnum++)
					{
						// 打印交易小票联
						printSellBill();
						printnum++;
					}

					// 打印附加的各个小票联
					printAppendBill();
				}

				sequenceflag = true;
			}

			num = num + 1;
		}

		// 记录本笔小票用的发票张数
		saveSaleFphm(salefph);
	}
	
	public void printYYYBill()
	{
		super.printYYYBill();
	}
	
	public void printRealTimeBottom()
	{
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

        // 打印附加的各个小票联
        printAppendBill();

		printGWKInfo();
		
	}
}
