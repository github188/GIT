package custom.localize.Bjcx;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.PrintTemplate.YyySaleBillMode;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Bjcx_YyySaleBillMode extends YyySaleBillMode
{

	protected final static int YSB_group_zysje =210;//分组总应收金额（折后）
	protected final static int YSB_khkSum = 211;//客户卡合计（付款方式名称 总张数：1  总金额：102.37）
	protected final static int YSB_mskSum = 212;//美食卡合计（付款方式名称 总张数：1  总金额：102.37）	
	protected final static int YSB_jskSum = 213;//健身卡合计
	protected final static int YSB_lxkSum = 214;//连心卡合计
	protected final static int YSB_stkSum = 215;//商通卡合计
	protected final static int YSB_zfbkSum = 216;//支付宝合计
	protected final static int YSB_gjyktSum = 217;//公交一卡通合计
	protected final static int YSB_ylkSum = 218;//雅联卡合计
	protected final static int YSB_ytkSum = 219;//银通卡合计
	protected final static int YSB_jxkSum = 220;//吉祥卡合计
	protected final static int YSB_dskSum = 221;//得士卡合计
	//protected final static int YSB_nhygkSum = 222;//农行员工卡合计
	protected final static int YSB_ytkkSum = 223;//1039易通卡合计
	protected final static int YSB_zjskSum = 224;//宅急送货到付款合计
	protected final static int YSB_sfkSum = 225;//顺丰货到付款合计
	protected final static int YSB_sttkSum = 226;//商通退合计
	
	protected final static String khkCode = "0801";//客户卡
	protected final static String mskCode = "0802";//美食卡
	protected final static String jskCode = "0401";//健身卡
	protected final static String lxkCode = "0402";//连心卡
	protected final static String stkCode = "0403";//商通卡
	protected final static String zfbkCode = "0404";//支付宝卡
	protected final static String gjyktCode = "0405";//公交一卡通
	protected final static String ylkCode = "0406";//雅联卡
	protected final static String ytkCode = "0407";//银通卡
	protected final static String jxkCode = "0408";//吉祥卡
	protected final static String dskCode = "0409";//得士卡
	//protected final static String nhygkCode = "0410";//农行员工卡
	protected final static String ytkkCode = "0411";//1039易通卡
	protected final static String zjskCode = "0412";//宅急送货到付款
	protected final static String sfkCode = "0413";//顺丰货到付款
	protected final static String sttkCode = "0413";//商通退
	
	
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
            }
            else if (g.type.trim().equals("11"))
            {
            	fj.add(g);
            }
            else if (g.type.trim().equals("90")) // 停车券
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
        	
        	if (g.type.trim().equals("90")) // 停车券
        	{
        		continue;
        	}
        	String l = Convert.appendStringSize("",g.info,1,16,17,1);
        	
        	buff.append(l+":"+Convert.appendStringSize("",ManipulatePrecision.doubleToString(g.je),1,10,10,0)+"\n");
        	//buff.append(g.code+"   "+g.info+"      "+Convert.increaseChar(ManipulatePrecision.doubleToString(g.je), 14)+"\n");
        	je += g.je;
        }
        buff.append(Convert.increaseChar("-", '-',27)+"\n");
        buff.append("返券总金额为: "+ManipulatePrecision.doubleToString(je));
        if (je > 0)
        {
        	//new MessageBox(buff.toString());
        }
        
        // 设置
        if (fj.size() > 0) this.zq = fj;
        else this.zq = null;
        if (goodsinfo.size() > 0) this.gift = goodsinfo;
        else this.gift = null;
	}
	
	public void printBottom()
	{
		if (zq != null)
		{
			StringBuffer line = new StringBuffer();
			double je = 0;
			for (int i = 0; i < this.zq.size(); i++)
			{
				GiftGoodsDef def = (GiftGoodsDef) this.zq.elementAt(i);
				
				//if (def.type.equals("4"))
				//{
					String[] infos = def.info.split("&");
					String strje = ManipulatePrecision.doubleToString(def.je);
					line.append(Convert.appendStringSize("",infos[0],1,16,16,1)+":"+ Convert.appendStringSize("",strje,1,10,10,0)+"\n");
					if (GlobalInfo.sysPara.printYXQ == 'Y') line.append(Convert.appendStringSize("","券有效期",1,16,16,1)+":"+ Convert.appendStringSize("",def.memo,1,24,24,0)+"\n");
					je +=def.je;
				//}
			}
			
			if (je > 0)
				printLine("本次小票有返券，返券金额为:" + ManipulatePrecision.doubleToString(je));
				printLine(line.toString());
		}
		
		super.printBottom();
	}

	public void printBill()
	{
//		 打印小票前先查询满赠信息并设置到打印模板供打印
		if (!SellType.ISEXERCISE(salehead.djlb))
		{
			DataService dataservice = (DataService) DataService.getDefault();
			Vector gifts = dataservice.getSaleTicketMSInfo(salehead, salegoods, salepay);
			YyySaleBillMode.getDefault().setSaleTicketMSInfo(salehead, gifts);
		}
		super.printBill();
	}
	
	public void groupsummary()
	{		
		for (int i = 0;i < groupset.size();i++)
		{
			GroupDef group = (GroupDef) groupset.elementAt(i);
			
			GroupSummaryDef gsd = group.gsd;
			
			for (int j = 0;j < group.row_set.size();j++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef)originalsalegoods.elementAt(Integer.parseInt((String) group.row_set.elementAt(j)));
				
    			int sign = 1;    			
                // 以旧换新商品,合计要减 wangyong add by 2011.8.31 for bjcx
                if (sgd.type == '8')
                {
                    sign = -1;
                }
                else
                {
                    sign = 1;
                }
                
				gsd.hjje += sgd.hjje * sign;
				gsd.hjzk += sgd.hjzk;
				gsd.hjsl += sgd.sl;
			}
		}
	}
	
	//是否存在合计的付款方式
	protected boolean IsExistsPrintSumPaycode(String paycode)
	{
		if (paycode == null || paycode.length() <= 0) return false;
				
		if (khkCode.equals(paycode)  || mskCode.equals(paycode)  || sfkCode.equals(paycode) || 
				sttkCode.equals(paycode)  || jskCode.equals(paycode)  || lxkCode.equals(paycode) || 
				stkCode.equals(paycode)  || zfbkCode.equals(paycode)  || gjyktCode.equals(paycode) || 
				ylkCode.equals(paycode)  || ytkCode.equals(paycode)  || jxkCode.equals(paycode) || 
				dskCode.equals(paycode)  ||  ytkkCode.equals(paycode) || 
				zjskCode.equals(paycode))//nhygkCode.equals(paycode)  ||
		{
			return true;
		}
		return false;
	}
	
    //根据打印项匹配付款方式代码
	protected String GetPaycode(int itemcode)
	{
		String paycode = "";
		try
		{
			switch (itemcode)
			{
				case YSB_khkSum  :				
					paycode = khkCode ;
					break;
					
				case YSB_mskSum  :				
					paycode = mskCode ;
					break;
					
				case YSB_jskSum :				
					paycode = jskCode ;
					break;
					
				case YSB_lxkSum :				
					paycode = lxkCode ;
					break;
					
				case YSB_stkSum :			
					paycode = stkCode ;
					break;
					
				case YSB_zfbkSum :			
					paycode = zfbkCode;
					break;
					
				case YSB_gjyktSum :				
					paycode = gjyktCode ;
					break;
					
				case YSB_ylkSum :			
					paycode = ylkCode ;
					break;
					
				case YSB_ytkSum :			
					paycode = ytkCode ;
					break;
					
				case YSB_jxkSum :			
					paycode = jxkCode ;
					break;
					
				case YSB_dskSum :				
					paycode = dskCode ;
					break;
					
				/*case YSB_nhygkSum :				
					paycode = nhygkCode ;
					break;*/
					
				case YSB_ytkkSum :				
					paycode = ytkkCode ;
					break;
					
				case YSB_zjskSum :				
					paycode = zjskCode ;
					break;
					
				case YSB_sfkSum :			
					paycode = sfkCode ;
					break;
					
				case YSB_sttkSum  :				
					paycode = sttkCode ;
					break;
					
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return paycode;
	}
	
	/*protected String GetPayname(int itemcode)
	{
		String payname = "";
		try
		{
			switch (itemcode)
			{
				case YSB_khkSum  :				
					payname = "客户卡" ;
					break;
					
				case YSB_mskSum  :				
					payname = "美食卡" ;
					break;
					
				case YSB_jskSum :				
					payname = "健身卡" ;
					break;
					
				case YSB_lxkSum :				
					payname = "连心卡" ;
					break;
					
				case YSB_stkSum :			
					payname = "商通卡" ;
					break;
					
				case YSB_zfbkSum :			
					payname = "支付宝";
					break;
					
				case YSB_gjyktSum :				
					payname = "公交一卡通" ;
					break;
					
				case YSB_ylkSum :			
					payname = "雅联卡" ;
					break;
					
				case YSB_ytkSum :			
					payname = "银通卡" ;
					break;
					
				case YSB_jxkSum :			
					payname = "吉祥卡" ;
					break;
					
				case YSB_dskSum :				
					payname = "得士卡" ;
					break;
					
				case YSB_nhygkSum :				
					payname = "农行员工卡" ;
					break;
					
				case YSB_ytkkSum :				
					payname = "1039易通卡" ;
					break;
					
				case YSB_zjskSum :				
					payname = "宅急送货到付款" ;
					break;
					
				case YSB_sfkSum :			
					payname = "顺丰货到付款" ;
					break;
					
				case YSB_sttkSum  :				
					payname = "商通退" ;
					break;
					
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return payname;
	}
	*/
	
	//打印合计的付款方式
	protected String GetSumPrintdata(int itemcode, int index)
	{
		String line = null;
		try
		{	
			String paycode = GetPaycode(itemcode);
			
			if (((SalePayDef) salepay.elementAt(index)).paycode.equals(paycode))//当前为XX卡付款
			{
				SalePayDef spd = null;
				for (int i = 0; i < salepay.size(); i++)
				{
					spd = (SalePayDef) salepay.elementAt(i);
					if (spd.paycode.equals(paycode))
					{
						if (index > i) 
						{
							line = null;
						}
						else
						{
							int mzkCount = 0;		//XX卡消费总张数
							double mzkTotalJe = 0;	//XX卡消费总金额
							String payname = "";//XX卡名称
							
							SalePayDef pay = null;
							
							for (int j = 0; j < salepay.size(); j++)
							{
								pay = (SalePayDef) salepay.elementAt(j);
								if (pay.paycode.equals(spd.paycode))
								{
									mzkCount++;
									mzkTotalJe += pay.ybje * pay.hl;														
									payname = pay.payname;											
								}
							}
							if (mzkCount > 0)
							{
								/*line = Convert.appendStringSize("","客户卡",0,6,6,0) +
								" 总张数:" + Convert.appendStringSize("", String.valueOf(mzkCount), 0, 3, 3, 0) +
								" 总金额:" + Convert.appendStringSize("", ManipulatePrecision.doubleToString(mzkTotalJe),0,10,10,1);*/
								
								//由于各个付款方式的名称长度不同，所以各项挨着打印
								line = payname +
								" 总张数" + String.valueOf(mzkCount) +
								" 总金额" + ManipulatePrecision.doubleToString(mzkTotalJe);
							}
						}
						break;//找到一个就退出循环
					}
				}
			}
			
				
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}	
		
		return line;
		
	}
	
	protected String getItemDataString(PrintTemplateItem item, int index)
	{
		String line = null;

		line = extendCase(item, index);

		String text = item.text;
		
		// 勇哥的BUG，取BJCX_YyySaleBill里的110
		if (Integer.parseInt(item.code) == 110)
		{
			line = null;
		}
		//new MessageBox(item.code);
		if (line == null)
		{
			 try
		        {        	
		        	switch (Integer.parseInt(item.code))
		            {      
		        		//case 22:
		        		//case 23:
		        		case 24:
		        		case 42:
		        		case 53:
		        		case 54:
		        			if (IsExistsPrintSumPaycode(((SalePayDef) salepay.elementAt(index)).paycode))
		        			{        			
		        				//XX付款方式的，都不打，只打合计
		        				return null;
		        			}
		        			return super.getItemDataString(item, index);
		        			//break;
		        			
		        		case YSB_khkSum://XX卡合计
		        		case YSB_mskSum :
		        		case YSB_jskSum :
		        		case YSB_lxkSum :
		        		case YSB_stkSum :
		        		case YSB_zfbkSum :
		        		case YSB_gjyktSum :
		        		case YSB_ylkSum :
		        		case YSB_ytkSum :
		        		case YSB_jxkSum :
		        		case YSB_dskSum :
		        		//case YSB_nhygkSum :
		        		case YSB_ytkkSum :
		        		case YSB_zjskSum :
		        		case YSB_sfkSum :
		        		case YSB_sttkSum :		        			
		        			line = GetSumPrintdata((Integer.parseInt(item.code)), index);
							/*try
							{	
								if (((SalePayDef) salepay.elementAt(index)).paycode.equals(khkCode))//当前为客户卡付款
								{
									SalePayDef spd = null;
									for (int i = 0; i < salepay.size(); i++)
									{
										spd = (SalePayDef) salepay.elementAt(i);
										if (spd.paycode.equals(khkCode))
										{
											if (index > i) 
											{
												line = null;
											}
											else
											{
												int mzkCount = 0;		//客户卡消费总张数
												double mzkTotalJe = 0;	//客户卡消费总金额
												
												SalePayDef pay = null;
												
												for (int j = 0; j < salepay.size(); j++)
												{
													pay = (SalePayDef) salepay.elementAt(j);
													if (pay.paycode.equals(spd.paycode))
													{
														mzkCount++;
														mzkTotalJe += pay.ybje * pay.hl;														
																									
													}
												}
												if (mzkCount > 0)
												{
													line = Convert.appendStringSize("","客户卡",0,6,6,0) +
													" 总张数:" + Convert.appendStringSize("", String.valueOf(mzkCount), 0, 3, 3, 0) +
													" 总金额:" + Convert.appendStringSize("", ManipulatePrecision.doubleToString(mzkTotalJe),0,10,10,1);
												}
											}
											break;//找到一个就退出循环
										}
									}
								}
			        			
									
							}
							catch(Exception ex)
							{
								ex.printStackTrace();
							}	*/
							
							break;
		        			
		        			
		        		/*case YSB_mskSum://美食卡合计 
							try
							{	
								if (((SalePayDef) salepay.elementAt(index)).paycode.equals(mskCode))//当前为美食卡付款
								{
									SalePayDef spd = null;
									for (int i = 0; i < salepay.size(); i++)
									{
										spd = (SalePayDef) salepay.elementAt(i);
										if (spd.paycode.equals(mskCode))
										{
											if (index > i) 
											{
												line = null;
											}
											else
											{
												int mzkCount = 0;		//美食卡消费总张数
												double mzkTotalJe = 0;	//美食卡消费总金额
												
												SalePayDef pay = null;
												
												for (int j = 0; j < salepay.size(); j++)
												{
													pay = (SalePayDef) salepay.elementAt(j);
													if (pay.paycode.equals(spd.paycode))
													{
														mzkCount++;
														mzkTotalJe += pay.ybje * pay.hl;														
																									
													}
												}
												if (mzkCount > 0)
												{
													line = Convert.appendStringSize("","美食卡",0,6,6,0) +
													" 总张数:" + Convert.appendStringSize("", String.valueOf(mzkCount), 0, 3, 3, 0) +
													" 总金额:" + Convert.appendStringSize("", ManipulatePrecision.doubleToString(mzkTotalJe),0,10,10,1);
												}
											}
											break;//找到一个就退出循环
										}
									}
								}
			        			
									
							}
							catch(Exception ex)
							{
								ex.printStackTrace();
							}	
							
							break;*/
							
							
		        		case YSB_group_zysje://总应收金额        			
		        			//new MessageBox(SellType.SELLSIGN(salehead.djlb)+"  "+this.curgroup.gsd.hjje+"   "+this.curgroup.gsd.hjzk);
		        			line = ManipulatePrecision.doubleToString((this.curgroup.gsd.hjje - this.curgroup.gsd.hjzk) * SellType.SELLSIGN(salehead.djlb));
		        			break;
		        			
		        		case SBM_payname: // 付款方式名称
		        			if (IsExistsPrintSumPaycode(((SalePayDef) salepay.elementAt(index)).paycode))
		        			{        			
		        				//面值方式的，都不打，只打合计
		        				return null;
		        			}
		        			
							SalePayDef pay = (SalePayDef) salepay.elementAt(index);
							if (pay.paycode.equals("0111"))
		                	{
		                		if (pay.ybje > 0)
		                		{
		                			line = pay.payname + "消费";
		                		}
		                		else if (pay.ybje < 0)
		                		{
		                			line = pay.payname + "存入";
		                		}
		                		
		                	}
							else
							{
								line = pay.payname;
							}
							break;
		            	        

						case SBM_ybje: // 付款方式金额:零钞转存金额都为正
							if (IsExistsPrintSumPaycode(((SalePayDef) salepay.elementAt(index)).paycode))
		        			{        			
		        				//面值方式的，都不打，只打合计
		        				return null;
		        			}
							
							SalePayDef pay1 = (SalePayDef) salepay.elementAt(index);
							double je = pay1.ybje * SellType.SELLSIGN(salehead.djlb);
							if (pay1.paycode.equals("0111"))
		                	{
		                		if (pay1.ybje > 0)
		                		{
		                			//line = ManipulatePrecision.doubleToString(pay1.ybje * SellType.SELLSIGN(salehead.djlb));
		                		}
		                		else if (pay1.ybje < 0)
		                		{
		                			//line = ManipulatePrecision.doubleToString(-1 * pay1.ybje * SellType.SELLSIGN(salehead.djlb));
		                			je = -1 * je;
		                		}
		                		
		                	}
							line = ManipulatePrecision.doubleToString(je);
							break;
		            	default:
		            		return super.getItemDataString(item, index);
		            		
		            		
		            			
		            }
		        	
		        	//return line;
		        }
		        catch(Exception ex)
		        {
		        	ex.printStackTrace();
		         	return null;
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
	
	
}
