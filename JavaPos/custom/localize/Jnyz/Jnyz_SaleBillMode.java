package custom.localize.Jnyz;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Cmls.Cmls_SaleBillMode;

public class Jnyz_SaleBillMode extends Cmls_SaleBillMode {
	
	protected final static int JNYZ_FPJEDX= 301;//大写发票金额
	protected final static int JNYZ_FPJEXX= 302;//小写发票金额
	
    
    protected String getItemDataString(PrintTemplateItem item, int index)
	{
		String line = null;

		line = extendCase(item, index);

		String text = item.text;

		if (line == null)
		{
			switch (Integer.parseInt(item.code))
			{
				case JNYZ_FPJEDX: // 大写发票金额
					double ysje_d = salehead.ysje + salehead.sswr_sysy + salehead.fk_sysy;
					if (Jnyz_CustomGlobalInfo.getDefault().sysPara.fpje != null)
					{
						String fpje[] = Jnyz_CustomGlobalInfo.getDefault().sysPara.fpje.split(",");
						for(int i = 0;i<salepay.size();i++){
							SalePayDef sp = (SalePayDef) salepay.elementAt(i);
							for(int j=0;j<fpje.length;j++){
								if(sp.paycode.equals(fpje[j].trim())){
									ysje_d = ysje_d-sp.je;
								}
							}
						}
						line = Double.toString(ysje_d);
					}
					else
					{
						line = Double.toString(ysje_d);
					}
						if(!SellType.ISBACK(salehead.djlb) || !SellType.ISHC(salehead.djlb))
						{
							//转换金额大写
						    if(line != null && line.length()>0)
						    {
						    	line = ManipulatePrecision.getFloatConverChinese(Double.parseDouble(line));
						    	if(line.equals("整"))line = "零元整";
						    }
						}
						else
						{
							line = "零元整";
						}
					break;
					
			    case JNYZ_FPJEXX: // 小写发票金额
			    	double ysje_x = salehead.ysje + salehead.sswr_sysy + salehead.fk_sysy;
					if (Jnyz_CustomGlobalInfo.getDefault().sysPara.fpje != null)
					{
						String fpje[] = Jnyz_CustomGlobalInfo.getDefault().sysPara.fpje.split(",");
						for(int i = 0;i<salepay.size();i++){
							SalePayDef sp = (SalePayDef) salepay.elementAt(i);
							for(int j=0;j<fpje.length;j++){
								if(sp.paycode.equals(fpje[j].trim())){
									ysje_x = ysje_x-sp.je;
								}
							}
						}
						line = Double.toString(ysje_x);
					}
					else
					{
						line = Double.toString(ysje_x);
					}
					
					if(SellType.ISBACK(salehead.djlb) || SellType.ISHC(salehead.djlb))
					{
						line = "0";
					}
					
					break;
					
			   case SBM_syjh: // 收银机号
			    	if(salehead.str6 != null && salehead.str6.length() > 0)
			    		line = salehead.str6;
			    	else
			    		line = GlobalInfo.syjStatus.syjh;

					break;
				
					
				default:
					return super.getItemDataString(item, index);
					  
				
			}
		}

		if ((line != null) && line.equals("&!"))
		{
			line = null;
		}

		// if (line != null && Integer.parseInt(item.code) != 0 && item.text !=
		// null && !item.text.trim().equals(""))
		if ((line != null) && (Integer.parseInt(item.code) != 0) && (text != null) && !text.trim().equals(""))
		{
			// line = item.text + line;
			int maxline = item.length - Convert.countLength(text);
			
			line = text + Convert.appendStringSize("", line, 0, maxline, maxline, item.alignment);
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
                goodsinfo.add(g);
            }
            else if (g.type.trim().equals("4"))
            {
            	fj.add(g);
//            	new MessageBox("本次交易电子券返到"+sh.hykh+"\n返券金额为 "+ManipulatePrecision.doubleToString(g.je));
            }
            else if (g.type.trim().equals("11"))
            {
            	fj.add(g);
            }
        }
        
        // 提示
        StringBuffer buff = new StringBuffer();
        double je = 0;
        for (int i = 0 ; i < fj.size(); i++)
        {
        	GiftGoodsDef g = (GiftGoodsDef)fj.elementAt(i);
        	buff.append(g.code+"   "+g.info+"      "+Convert.increaseChar(ManipulatePrecision.doubleToString(g.je), 14)+"\n");
        	je += g.je;
        }
        buff.append("返券总金额为: "+Convert.increaseChar(ManipulatePrecision.doubleToString(je), 14));
        if (je > 0)
        {
        	new MessageBox(buff.toString());
        }
        
        // 设置
        if (fj.size() > 0) this.zq = fj;
        else this.zq = null;
        if (goodsinfo.size() > 0) this.gift = goodsinfo;
        else this.gift = null;
	}
    
    protected void printSellBill()
	{
    	//是否打印退货小票,红冲小票
    	if(Jnyz_CustomGlobalInfo.getDefault().sysPara.isth == 'N' && salehead.djlb.equals(SellType.RETAIL_BACK) || Jnyz_CustomGlobalInfo.getDefault().sysPara.ishc == 'N' && !SellType.ISSALE(salehead.djlb))
    	{
    		
    	}
    	else
    	{
    		super.printSellBill();
    	}
    	

		if(GlobalInfo.sysPara.istcl == 'Y')
		{
			String line = "";  
			printLine("------------------------------");
			line = Convert.appendStringSize(line,"交易时间："+salehead.rqsj, 0, 32, 32);
			printLine(line);
			line = Convert.appendStringSize(line,"小票号:"+ String.valueOf(salehead.fphm),0, 32, 32);
			printLine(line);
			line = Convert.appendStringSize(line,"收银机:"+GlobalInfo.syjDef.syjh, 0, 32,32);
			printLine(line);
			line = Convert.appendStringSize(line,"收银员:"+GlobalInfo.posLogin.gh, 0, 32,32);
			printLine(line);
			line = Convert.appendStringSize(line,"交易类型:"+String.valueOf(SellType.getDefault().typeExchange(salehead.djlb, salehead.hhflag, salehead)),0,32, 32);
			printLine(line);
			if (salehead.printnum > 0)
			{
				printLine(Language.apply("**重打印**"));
			}
			line = Convert.appendStringSize(line,"件数:"+ManipulatePrecision.doubleToString(salehead.hjzsl * SellType.SELLSIGN(salehead.djlb)), 0, 32,32);
			printLine(line);
			line = Convert.appendStringSize(line,"实收:"+ManipulatePrecision.doubleToString(salehead.sjfk * SellType.SELLSIGN(salehead.djlb)),0,32, 32);
			printLine(line);
			PrintTemplateItem ptl = new PrintTemplateItem();
			ptl.code = String.valueOf(JNYZ_FPJEDX);
			line = Convert.appendStringSize(line,"发票金额:"+extendCase(ptl,0),0,32, 32);
			printLine(line);
			printLine("");
			printLine("");
			printLine("");
			printLine("");
			printLine("");
			printLine("");
		}
    	
	}
    
    public void printBottom()
	{
    	if (zq != null)
		{
			StringBuffer line = new StringBuffer();
			double je = 0;
			boolean isdzj = false;
			Map zqmap = new HashMap();
			
			for (int i = 0; i < this.zq.size(); i++)
			{
				GiftGoodsDef def = (GiftGoodsDef) this.zq.elementAt(i);

				if (def.type.equals("4"))
				{
					isdzj = true;
//					line.append(" " + def.info + ": " + def.je + "\n");
					line.append(def.info + "\n");//后台info里面返回了金额
					line.append("有效期:" + def.memo + "\n");
					je += def.je;
				}
				
				if (def.type.equals("2"))
				{
					line.append(" " + def.info + ": " + def.je + "\n");
					line.append("有效期:" + def.memo + "\n");
					je += def.je;
				}

				if(zqmap.get(def.info) == null)
				{
					zqmap.put(def.info, def.je);
				}
				else
				{
					zqmap.remove(def.info);
					zqmap.put(def.info, je);
				}
				
			}

			if (je > 0)
			{
				Printer.getDefault().printLine_Normal("本次小票有返券，返券金额为:" + String.valueOf(je));
				
				Iterator<String> iter = zqmap.keySet().iterator();
				String key;
				while (iter.hasNext()) {
				    key = iter.next();
				    System.out.println(zqmap.get(key));
					Printer.getDefault().printLine_Normal(key+":"+zqmap.get(key).toString());
				}
			}
			

			
			if(isdzj)
			{
				Printer.getDefault().printLine_Journal("		银座商城");
				Printer.getDefault().printLine_Journal("小票号："+salehead.fphm);
				Printer.getDefault().printLine_Journal("时  间："+salehead.rqsj);
				Printer.getDefault().printLine_Journal("卡  号："+salehead.hykh);
				Printer.getDefault().printLine_Journal("================================");
				Printer.getDefault().printLine_Journal(line.toString());
				Printer.getDefault().printLine_Journal("================================");
				Printer.getDefault().printLine_Journal("收银机："+salehead.syjh+" 收银员："+salehead.syyh);
				Printer.getDefault().printLine_Journal("谢谢光临！请妥善保存此凭证");
				Printer.getDefault().printLine_Journal("");
				Printer.getDefault().printLine_Journal("");
				Printer.getDefault().printLine_Journal("");
				Printer.getDefault().printLine_Journal("");
				Printer.getDefault().printLine_Journal("");
			}
			
		}
		// 设置打印区域
		setPrintArea("Bottom");
			
	    printVector(getCollectDataString(Bottom,-1,Width));	

		if(((Jnyz_Printer)Printer.getDefault()).bool && SellType.ISSALE(salehead.djlb))
		{
			int numrow = Printer.getDefault().getCurRow_Normal();//当前打印总行数
			int pagenum = Printer.getDefault().getPageNum_Normal();//本次打印页数
			int pagerow = ((Jnyz_Printer)Printer.getDefault()).numRow_Normal;//已打印行数
			int newpagerow = ((Jnyz_Printer)Printer.getDefault()).newpageRow_Normal;//每页打印行数
			if(numrow/pagenum-pagerow != 0)
			{
				for(int i =0;i < newpagerow-pagerow;i++)
				{
					printLine("\n");
				}
				((Jnyz_Printer)Printer.getDefault()).numRow_Normal = 0;
			}
			((Jnyz_Printer)Printer.getDefault()).bool = false;
		}

		
	}
}
