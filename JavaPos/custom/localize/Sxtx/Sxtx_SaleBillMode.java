package custom.localize.Sxtx;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.ZqInfoRequestDef;

public class Sxtx_SaleBillMode extends SaleBillMode
{
	public int SBM_fpzje = 909;
	protected final static int SBM_MEMO_LSCK = 202;//福利卡流水号+参考号
	
	
	public void setSaleTicketMSInfo(SaleHeadDef sh, Vector gifts)
	{
		// 记录小票赠送清单
		this.salemsinvo = sh.fphm;
		this.salemsgift = gifts;
		
		
		// 分解赠品清单
        Vector fj = new Vector();
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
							int ret = new MessageBox("是否取消送券?\n 1-是 / 2-否 ",null,false).verify();
				        	if (ret == GlobalVar.Key1)
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
						int ret = new MessageBox("是否取消送券?\n 1-是 / 2-否 ",null,false).verify();
			        	if (ret == GlobalVar.Key1)
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
			line = ManipulatePrecision.doubleToString(salehead.ljjf);
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
			if(DataService.getDefault().searchPayMode(spd.paycode).type == '3')
			{
				line = spd.memo;
			}
			else
			{
				line = null;
			}
		}
			
    	return line;
    }
    
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
		//printCutPaper();
	}
	
	public void printBottom()
	{
		if (salemsgift != null)
		{
			boolean done = false;
			for (int i = 0; i < this.salemsgift.size(); i++)
			{
				GiftGoodsDef def = (GiftGoodsDef) this.salemsgift.elementAt(i);
				
				if (!done)
				{
					printLine("券号:"+def.memo);
					done = true;
				}
				String line1 = Convert.appendStringSize("",def.code,1,16,16,0);
				line1 = line1+"    金额:"+Convert.appendStringSize("",ManipulatePrecision.doubleToString(def.je),1,10,10,2);
				printLine(line1);
				printLine("有效期:"+def.info);
			}
		}
		
		//打印赠券明细
		super.printBottom();
	}
}
