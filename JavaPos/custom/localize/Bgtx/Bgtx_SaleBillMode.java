package custom.localize.Bgtx;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.GiftBillMode;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.PrintTemplate.YyySaleBillMode;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.ZqInfoRequestDef;

import custom.localize.Cmls.Cmls_SaleBillMode;

public class Bgtx_SaleBillMode extends Cmls_SaleBillMode
{
//	public int SBM_fpzje = 909;
	protected final static int SBM_MEMO_LSCK = 202;//福利卡流水号+参考号
	protected final static int SBM_prize = 203;//小票中奖信息
	
	public void setSaleTicketMSInfo(SaleHeadDef sh, Vector gifts)
	{
		// 记录小票赠送清单
		this.salemsinvo = sh.fphm;
		this.salemsgift = gifts;
		
		
		// 分解赠品清单
        Vector fj = new Vector();//返券
        Vector fl = new Vector();//返礼
        
        for (int i = 0; gifts != null && i < gifts.size(); i++)
        {
            GiftGoodsDef g = (GiftGoodsDef)gifts.elementAt(i);
            
            if (g.type.trim().equals("0"))
            {
                //无促销
                break;
            }
            else if (g.type.trim().equals("1"))
            {
                fj.add(g);
            }
            else if (g.type.trim().equals("2"))
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
//            	new MessageBox("本次交易电子券返到"+sh.hykh+"\n返券金额为 "+ManipulatePrecision.doubleToString(g.je));
//            	new MessageBox(Language.apply("本次交易电子券返到{0}\n返券金额为 {1}" ,new Object[]{sh.hykh ,ManipulatePrecision.doubleToString(g.je)}));
            }
            else if (g.type.trim().equals("10"))  //赠礼品
            {
            	fl.add(g);
            }
            else if (g.type.trim().equals("11"))  //买券
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
        
        
//      设置赠券，赠礼打印
        if (fj.size() > 0) this.zq = fj;
        else this.zq = null;
        if (fl.size() > 0) this.gift = fl;
        else this.gift = null;
        
        
        
        if (salehead.printnum <= 0 && salemsgift != null && salemsgift.size() > 0 && Math.abs(je) > 0)
        {
        	String zqinfo = "";
	        for (int i = 0;i < salemsgift.size();i ++)
	        {
	        	GiftGoodsDef g = (GiftGoodsDef)salemsgift.elementAt(i);
	        	if (g.type.equals("3"))
	        	{
	        		if (zqinfo.trim().length() > 0)
	        		{
	        			zqinfo = zqinfo + "|";
	        		}
	        		
	        		zqinfo = zqinfo + g.code.trim() + "," + g.info + "," + g.je;
	        	}
	        }
	        
        	if (SellType.ISSALE(salehead.djlb))
        	{
		        if (zqinfo.trim().length() > 0)
		        {
		        	while(true)
		        	{
		        		StringBuffer cardno = new StringBuffer();
						TextBox txt = new TextBox();
						if (txt.open("请刷需要充值的返券卡", "卡号", "请将返券卡从刷卡槽刷入\n" + zqinfo, cardno, 0, 0, false, TextBox.MsrKeyInput))
						{
							String tr = txt.Track2;
							
							if (tr.trim().length() > 0)
							{
								DataService dataservice = (DataService) DataService.getDefault();
								ZqInfoRequestDef request = new ZqInfoRequestDef();
								request.cardno = tr.trim();
								request.mktcode = GlobalInfo.sysPara.mktcode;
								request.fphm = salehead.fphm;
								request.syjh = salehead.syjh;
								request.zqinfo = zqinfo;
								request.memo = "";
								request.str1 = "";
								request.str2 = "";
								request.str3 = "";
								
								if(dataservice.saveZqInfo(request))
								{
					        		for (int i = 0;i < salemsgift.size();i ++)
					    	        {
					    	        	GiftGoodsDef g = (GiftGoodsDef)salemsgift.elementAt(i);
					    	        	if (g.type.equals("3"))
					    	        	{
					    	        		//将磁道号附值，用于打印
					    	        		g.memo = tr.trim();
					    	        	}
					    	        }
					        		
					        		//由于先天下比较变态，在刷完会员卡后，赠券的金额是会根据所刷卡的类型变动的，所以此时需要重新获取
					        		Vector vi = new Vector();
					        		NetService.getDefault().getSaleTicketMSInfo(vi, GlobalInfo.sysPara.mktcode, ConfigClass.CashRegisterCode, String.valueOf(salemsinvo), "N", NetService.getDefault().getMemCardHttp(CmdDef.GETMSINFO));
					        		this.salemsgift = vi;
									break;
								}
	
								int ret = new MessageBox("返券操作失败!\n 任意键-重试 / 2-放弃 ",null,false).verify();
					        	if (ret == GlobalVar.Key2)
					        	{
					        		// 放弃将返券信息删除
					        		for (int i = 0;i < salemsgift.size();i ++)
					    	        {
					    	        	GiftGoodsDef g = (GiftGoodsDef)salemsgift.elementAt(i);
					    	        	if (g.type.equals("3"))
					    	        	{
					    	        		salemsgift.remove(i);
					    	        		i--;
					    	        	}
					    	        }
					        		break;
					        	}
							}
						}
						else
						{
//							int ret = new MessageBox("是否取消送券?\n 1-是 / 2-否 ",null,false).verify();
//				        	if (ret == GlobalVar.Key1)
//				        	{
//				        		// 放弃将返券信息删除
//				        		for (int i = 0;i < salemsgift.size();i ++)
//				    	        {
//				    	        	GiftGoodsDef g = (GiftGoodsDef)salemsgift.elementAt(i);
//				    	        	if (g.type.equals("3"))
//				    	        	{
//				    	        		salemsgift.remove(i);
//				    	        		i--;
//				    	        	}
//				    	        }
//				        		break;
//				        	}
							break;   //不需要选择“是否取消送券”
						}
		        	}
		        }
        	}
        	else
        	{
        		while(true)
        		{
	        		DataService dataservice = (DataService) DataService.getDefault();
					ZqInfoRequestDef request = new ZqInfoRequestDef();
					request.cardno = "";
					request.mktcode = GlobalInfo.sysPara.mktcode;
					request.fphm = salehead.fphm;
					request.syjh = salehead.syjh;
					request.zqinfo = zqinfo;
					request.memo = "";
					request.str1 = "";
					request.str2 = "";
					request.str3 = "";
					if(dataservice.saveZqInfo(request))
					{	
		        		//由于先天下比较变态，在刷完会员卡后，赠券的金额是会根据所刷卡的类型变动的，所以此时需要重新获取
		        		Vector vi = new Vector();
		        		NetService.getDefault().getSaleTicketMSInfo(vi, GlobalInfo.sysPara.mktcode, ConfigClass.CashRegisterCode, String.valueOf(salemsinvo), "N", NetService.getDefault().getMemCardHttp(CmdDef.GETMSINFO));
		        		this.salemsgift = vi;
		        		break;
					}
					else
					{
//						int ret = new MessageBox("是否取消送券?\n 1-是 / 2-否 ",null,false).verify();
//			        	if (ret == GlobalVar.Key1)
//			        	{
//			        		// 放弃将返券信息删除
//			        		for (int i = 0;i < salemsgift.size();i ++)
//			    	        {
//			    	        	GiftGoodsDef g = (GiftGoodsDef)salemsgift.elementAt(i);
//			    	        	if (g.type.equals("3"))
//			    	        	{
//			    	        		salemsgift.remove(i);
//			    	        		i--;
//			    	        	}
//			    	        }
//			        		break;
//			        	}
						break;   //不需要选择“是否取消送券”
					}
        		}
        	}
        }
	}

	
    protected String extendCase(PrintTemplateItem item, int index)
    {
    	String line = null;
		if (Integer.parseInt(item.code) == SBM_bcjf) // 本次积分
		{
			if (salehead.hykh.trim().length() > 0)
				line = ManipulatePrecision.doubleToString(salehead.bcjf+salehead.num4);
			else
				line = null;
		}
		else if(Integer.parseInt(item.code) == SBM_ljjf) // 累计积分
		{
			if (salehead.hykh.trim().length() > 0)
			line = ManipulatePrecision.doubleToString(salehead.ljjf);
			else
				line = null;
		}
		else if (Integer.parseInt(item.code)== SBM_ye) // 付款余额
		{
			SalePayDef spd = (SalePayDef) salepay.elementAt(index);
			if (DataService.getDefault().searchPayMode(spd.paycode).type == '4' || spd.paycode.equals("0505")|| spd.paycode.equals("0506")|| spd.paycode.equals("0507")|| spd.paycode.equals("0508"))
			{
				line = ManipulatePrecision.doubleToString(spd.kye);
			}
			else
			{
				if (spd.kye == 0)
				{
					line = null;
				}
				else
				{
					line = ManipulatePrecision.doubleToString(spd.kye);
				}
			}
			
		}
		else if(Integer.parseInt(item.code) == SBM_MEMO_LSCK) // 福利卡流水号+参考号
		{
			SalePayDef spd = (SalePayDef) salepay.elementAt(index);
//			new MessageBox("spd.memo"+spd.memo);
			if(DataService.getDefault().searchPayMode(spd.paycode).type == '3' || spd.paycode.equals("0406") || spd.paycode.equals("0407"))
			{
				line = spd.memo;
			}
			else
			{
				line = null;
			}
		}
		else if(Integer.parseInt(item.code) == SBM_prize) // 小票中奖信息
		{
			if (salehead.str6.trim().length() > 0)
			line = salehead.str6.trim();
			else
				line = null;
		}
			
    	return line;
    }
    
    //计算发票金额
	protected double calcPayFPMoney()
	{
		double je = salehead.sjfk - salehead.zl;

		String payex = "," + GlobalInfo.sysPara.fpjepayex + ",";
		for (int i = 0; i < salepay.size(); i++)
		{
			SalePayDef sp = (SalePayDef) salepay.elementAt(i);
			String pcode = DataService.getDefault().searchMainPayMode(sp.paycode).code;
			if (sp.flag == '1' && (payex.indexOf("," + sp.paycode + ",") >= 0 || payex.indexOf("," + pcode + ",") >= 0))
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
		return je;
	}
	
    
	protected void printSellBill()
	{
		// GlobalInfo.sysPara.fdprintyyy =
		// (N-不打营业员联但打印小票联/Y-打印营业员联也打印小票/A-打印营业员联但不打印小票)
		// 非超市小票且系统参数定义只打印营业员分单，则不打印机制小票
		if (!((GlobalInfo.syjDef.issryyy == 'N') || (GlobalInfo.syjDef.issryyy == 'A' && ((SaleGoodsDef) salegoods.elementAt(0)).yyyh.equals("超市"))) && (GlobalInfo.sysPara.fdprintyyy == 'A'))
		{
			return;
		}

		// 设置打印方式
		printSetPage();

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

		// 要求后面不切纸
//		printCutPaper();
	}
	
	public void printBottom()
	{
		if (salemsgift != null)
		{
			boolean done = false;
			for (int i = 0; i < this.salemsgift.size(); i++)
			{
				GiftGoodsDef def = (GiftGoodsDef) this.salemsgift.elementAt(i);
				
				if(def.type.equals("10"))
				{
					String line1 = Convert.appendStringSize("","赠品名称:",0,10,38,0);
					line1 = Convert.appendStringSize(line1 ,def.info,12,16,38,0);
					line1 = Convert.appendStringSize(line1 ,"数量:",28,5,38,0);
					line1 = Convert.appendStringSize(line1 ,String.valueOf(def.sl),33,5,38,1);
					printLine(line1);
				}else
				{
					String line1 = Convert.appendStringSize("","券名称:",0,8,38,0);
					line1 = Convert.appendStringSize(line1 ,def.info,9,16,38,0);
					line1 = Convert.appendStringSize(line1 ,"数量:",25,5,38,0);
					line1 = Convert.appendStringSize(line1 ,String.valueOf(def.sl),30,8,38,1);
					printLine(line1);
					line1 = Convert.appendStringSize(line1 ,"券号:",0,6,38,0);
					line1 = Convert.appendStringSize(line1 ,def.code,7,18,38,0);
					line1 = Convert.appendStringSize(line1 ,"金额:",25,5,38,0);
					line1 = Convert.appendStringSize(line1 ,ManipulatePrecision.doubleToString(def.je),30,8,38,1);
					printLine(line1);
					printLine("有效期:"+def.memo);
				}
			}
		}
		
		
		//打印兑奖联(益东百货店)（可通过模板配置）
//		if(GlobalInfo.sysPara.mktcode.equals("3006"))
//		{
//			printLine(Convert.appendStringSize(""," ",0,32,32,2));
//			printLine(Convert.appendStringSize("","-------------------------------",0,32,32,2));
//			printLine(Convert.appendStringSize("","-------------------------------",0,32,32,2));
//			String line = Convert.appendStringSize("","益东百货兑奖联",0,32,32,2);
//			printLine(line);
//			line = Convert.appendStringSize("" ,"-------------------------------",0,32,32,0);
//			printLine(line);
//			line = Convert.appendStringSize(line ,"票号:",0,6,32,0);
//			line = Convert.appendStringSize(line ,String.valueOf(salehead.fphm),6,10,32,0);
//			line = Convert.appendStringSize(line ,"小票类型:",16,10,32,0);
//			line = Convert.appendStringSize(line ,salehead.djlb,26,6,32,0);
//			printLine(line);
//			line = Convert.appendStringSize(line ,"款机:",0,6,32,0);
//			line = Convert.appendStringSize(line ,salehead.salefphm,6,10,32,0);
//			line = Convert.appendStringSize(line ,"款员:",16,10,32,0);
//			line = Convert.appendStringSize(line ,salehead.syyh,24,8,32,0);
//			printLine(line);
//			line = Convert.appendStringSize(line ,"时间:",0,5,32,0);
//			line = Convert.appendStringSize(line ,salehead.rqsj,6,26,32,0);
//			printLine(line);
//			line = Convert.appendStringSize(line ,"实收:",0,6,32,0);
//			line = Convert.appendStringSize(line ,String.valueOf(salehead.hjzje),6,26,32,0);
//			printLine(line);
//			printLine(Convert.appendStringSize("","-------------------------------",0,32,32,2));
//		}

		
		
		
		//打印赠券明细
		super.printBottom();
	}
	
	
	
//	 打印赠品信息(不在前台赠礼)
//	public void printerGift()
//	{
//		if (this.gift == null || this.gift.size() <= 0)
//		{
//			return ;
//		}
//		
//		if (this.salemsinvo != 0 && salehead.fphm != this.salemsinvo)
//		{
//			this.salemsinvo = 0;
//			this.zq = null;
//			this.gift = null;
//			return ;
//		}
//
//		for (int i = 0; i < this.gift.size(); i++)
//		{
//			GiftGoodsDef def = (GiftGoodsDef) this.gift.elementAt(i);
//			
//			Printer.getDefault().printLine_Journal(Convert.appendStringSize("", GlobalInfo.sysPara.mktname+ "  赠品券", 0, 37, 38, 2));
//			Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "=================================================", 0, 37, 38, 2));
//			Printer.getDefault().printLine_Journal("收银机号："+salehead.syjh+"        小票号："+Convert.increaseLong(salehead.fphm, 8));
//			Printer.getDefault().printLine_Journal("收银员号："+salehead.syyh+"        时  间："+salehead.rqsj.substring(0, 10));
//	        Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "---------------------------------------", 0, 37, 38));
//			Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "赠品名称  : "+def.info, 0, 33, 38, 0));
//			Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "数量  : "+def.sl, 33, 5, 38, 2));
//	        if (salehead.printnum > 0)
//	    	{
//	        	Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "重 打 印", 0, 37, 38, 2));
//	    	}
//			Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "=================================================",0, 37, 38));
//			Printer.getDefault().cutPaper_Journal();
//		}
//	}
	
	public void printBill()
	{
		int choice = GlobalVar.Key1;

		// 开始打印前的发票号
		salefph = Printer.getDefault().getCurrentSaleFphm();

		// 重打印小票时，如果是非超市且参数定义既打印机制单又打营业员联，才提示选择打印部分
		if (('N' != (GlobalInfo.syjDef.issryyy)) && (salehead.printnum > 0) && GlobalInfo.sysPara.fdprintyyy == 'Y')
		{
//			StringBuffer info = new StringBuffer();
//			info.append(Convert.appendStringSize("", Language.apply("请按键选择重打印内容"), 1, 30, 30, 2) + "\n");
//			info.append(Convert.appendStringSize("", Language.apply("1、打印全部小票单据"), 1, 30, 30, 2) + "\n");
//			info.append(Convert.appendStringSize("", Language.apply("2、只打印机制小票单"), 1, 30, 30, 2) + "\n");
//			info.append(Convert.appendStringSize("", Language.apply("3、只打印营业员列印"), 1, 30, 30, 2) + "\n");
//			info.append(Convert.appendStringSize("", Language.apply("按其他键则放弃重打印"), 1, 30, 30, 2) + "\n");
//
//			choice = new MessageBox(info.toString(), null, false).verify();
			
			choice = 3; //要求只打印小票 不打印营业员联
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
}
