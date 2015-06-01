package custom.localize.Zmjc;

import java.text.DecimalFormat;
import java.util.Vector;

import org.eclipse.swt.widgets.TableItem;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentChange;
import com.efuture.javaPos.Payment.PaymentChangeForm;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Zmjc_PaymentChange extends PaymentChange
{
	private String bRmbPayCode = "01";//补找零的付款方式编码
	
	public Zmjc_PaymentChange(SaleBS sale) {
		super(sale);
		// TODO 自动生成构造函数存根
	}
	
//多币种补找零 START ADD
	
	//数字格式化
	public double formatDecimal(double je,String f)
	{
		try
		{
			DecimalFormat df = new DecimalFormat(f);
			return Double.parseDouble(df.format(je));//Double.valueOf(df.format(je));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return je;
	}
	
	/**
	 * 计算价格精度
	 * 
	 * JE 原金额
	 * SSWRJD 舍入精度(最小找零门槛)
	 * SSWRFS 舍入方式(分两大类：一大类是精确到元、分、角，另一大类是截断到、四舍五入)
	 */
	public double calcJgJd(double JE,double SSWRJD, char SSWRFS)
	{
		double value;
		try
		{			
			if (SSWRFS=='Y' || SSWRFS=='N')
			{
				if (SSWRFS=='Y')
				{
					//截断到
					if (SSWRJD==0.01)
					{
						value = formatDecimal(JE- 0.004,"#.00");// - 0.005
					}
					else if (SSWRJD==0.1)
					{
						value = formatDecimal(JE- 0.04,"#.0");// - 0.05
					}
					else if (SSWRJD==1)
					{
						value = formatDecimal(JE- 0.4,"#");// - 0.5
					}
					else if (SSWRJD>1)
					{
						value = Math.floor(JE/SSWRJD) * SSWRJD;
					}
					else
					{
						value = formatDecimal(JE- 0.004,"#.00");// - 0.005
					}
				}
				else
				{
					//四舍五入
					if (SSWRJD==0.01)
					{
						value = formatDecimal(JE,"#.00");
					}
					else if (SSWRJD==0.1)
					{
						value = formatDecimal(JE,"#.0");
					}
					else if (SSWRJD==1)
					{
						value = formatDecimal(JE,"#");
					}
					else if (SSWRJD>1)
					{
						value = formatDecimal(formatDecimal(JE/SSWRJD,"#") * SSWRJD,"#");
					}
					else
					{
						value = formatDecimal(JE,"#.0000000");
					}
				}
				
				return value;
			}
			
			switch (Convert.toInt(SSWRFS))
			{
				case 1://四舍五入
					if (SSWRJD==0.01)
					{
						value = formatDecimal(JE,"#.00");
					}
					else if (SSWRJD==0.1)
					{
						value = formatDecimal(JE,"#.0");
					}
					else if (SSWRJD==1)
					{
						value = formatDecimal(JE,"#");
					}
					else if (SSWRJD>1)
					{
						value = formatDecimal(formatDecimal(JE/SSWRJD,"#") * SSWRJD,"#");
					}
					else
					{
						value = formatDecimal(JE,"#.00");
					}
					break;
					
					
				case 2://截断到
					if (SSWRJD==0.01)
					{
						value = formatDecimal(JE- 0.004,"#.00");// - 0.005
					}
					else if (SSWRJD==0.1)
					{
						value = formatDecimal(JE- 0.04,"#.0");// - 0.05
					}
					else if (SSWRJD==1)
					{
						value = formatDecimal(JE- 0.4,"#");// - 0.5
					}
					else if (SSWRJD>1)
					{
						value = Math.floor(JE/SSWRJD) * SSWRJD;
					}
					else
					{
						value = formatDecimal(JE- 0.004,"#.00");// - 0.005
					}
					
					break;
					
				default://0精确到分,3四舍五入到元,4截断到元
					if (SSWRJD==0.01)
					{
						//舍入
						value = formatDecimal(JE,"#.00");// - 0.005
					}
					else if (SSWRJD==0.1)
					{
						//舍入
						value = formatDecimal(JE,"#.0");// - 0.05
					}
					else if (SSWRJD==1)
					{
						//截断
						value = formatDecimal(JE- 0.4,"#");// - 0.5
					}
					else if (SSWRJD>1)
					{
						//截断
						value = Math.floor(JE/SSWRJD) * SSWRJD;
					}
					else
					{
						//舍入
						value = formatDecimal(JE,"#.00");// - 0.005
					}
					
					break;
					
				
			}
			if (value<0)value=0;
			return value;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return 0;
		}
	}
	
	//检查补找零付款方式是否合法
	public boolean checkbZlPayCode()
	{
		try
		{
			if (GlobalInfo.sysPara.bZlPayCode != null && GlobalInfo.sysPara.bZlPayCode.trim().length()>0) bRmbPayCode = GlobalInfo.sysPara.bZlPayCode;
			
			if (DataService.getDefault().searchPayMode(bRmbPayCode)==null)
			{
				new MessageBox(Language.apply("无法进行找零操作:\n\n[{0}]补找零编码找不到!", new Object[]{bRmbPayCode}));
				return false;
			}
			return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	 
//	 加载找零付款方式
	    public boolean loadChangePayModeAdd(PosTable  table, double zlje)
	    {	    	
	    	if (!checkbZlPayCode()) return false;
	    	
	        Vector child = new Vector();
	        String[] temp = null;
	        PayModeDef zlMode = null;
	        String lastZlPayCode = getLastZlCode();
	        int count = 0;
	        int index = -1;

	        for (int i = 0; i < GlobalInfo.payMode.size(); i++)
	        {
	            zlMode = (PayModeDef) GlobalInfo.payMode.elementAt(i);

	            if (zlMode.iszl == 'Y' && zlMode.ismj == 'Y')
	            {	            	
	            	if (zlMode.code.equalsIgnoreCase(lastZlPayCode)) 
	            	{
	            		if (index<0) index=count;
	            	}
	            	else
	            	{
	            		count++;
	            	}
	            	
	                temp    = new String[5];
	                temp[0] = zlMode.code.trim();
	                temp[1] = zlMode.name;
	                temp[2] = ManipulatePrecision.doubleToString(zlMode.zlhl,6,1,false);//找零汇率
	                temp[3] = "";//外币找零
	                temp[4] = "";//补RMB
	                
	                child.add(temp);
	            }
	        }

	        table.exchangeContent(child);
	        if (index<0 || index>=table.getItemCount())
	        {
	        	index = 0;
	        }
    		table.setSelection(index);
	        
	        return true;
	    }	
	    
	    //设置找零/补找零金额	   	    
	    public void setMoneyInputDefaultAdd(PosTable  table, double zlje)
	    {
	    	try
	    	{
	    		int index = table.getSelectionIndex();
	    		TableItem[] items = table.getItems();

				for (int i = 0; i < items.length; i++)
				{
					TableItem row = items[i];
					row.setText(3, "");
					row.setText(4, "");
					
					if (i==index)
					{
						String zlPayCode = row.getText(0);
						PayModeDef zlMode = DataService.getDefault().searchPayMode(zlPayCode);
						
						double dblhlje = zlje/zlMode.zlhl;
	                	double dblCalcHlje = calcJgJd(dblhlje,zlMode.sswrjd,zlMode.sswrfs);//找零金额
	                	
	                	PayModeDef bRmbMode = DataService.getDefault().searchPayMode(this.bRmbPayCode);
	                	double tmpDblRmb = (dblhlje-dblCalcHlje) * zlMode.zlhl / bRmbMode.zlhl;//* zlMode.zlhl; WANGYONG UPDATE 2014.12.11
	                	
	                	double dblPlJe = calcJgJd(tmpDblRmb,bRmbMode.sswrjd,bRmbMode.sswrfs);//补RMB金额
	                	if (zlMode.str2 != null && zlMode.str2.equalsIgnoreCase("N"))
	                	{
	                		dblPlJe = -1;//表示该付款方式不需要补找零
	                	}
	                	double dblPLSy = tmpDblRmb-dblPlJe;//补RMB的损益
	                	
	                	row.setText(3, ManipulatePrecision.doubleToString(dblCalcHlje));
	                	if (dblPlJe!=-1)
	                	{
	                		row.setText(4, ManipulatePrecision.doubleToString(dblPlJe));
	                	}
	                	
						
					}
				}
	    	}
	    	catch(Exception ex)
	    	{
	    		ex.printStackTrace();
	    	}
	    }
	    
	    //增加找零付款对象
	    public boolean chgAccountAdd(PosTable  table, double zlje)
		{	         
	        try
	        {
	        	int index = table.getSelectionIndex();
	        	if (index <= -1) 
	        	{
	        		new MessageBox(Language.apply("数据选择错误!\n\n"), null, true);
	        		return false;
	        	}
	        	
	        	String[] row = table.changeItemVar(index);	            	            
	        	String paycode = row[0];
	        	
				PayModeDef zlMode = DataService.getDefault().searchPayMode(paycode);
				
				double dblhlje = zlje/zlMode.zlhl;
            	double dblCalcHlje = calcJgJd(dblhlje,zlMode.sswrjd,zlMode.sswrfs);//找零金额
            	
            	PayModeDef bRmbMode = DataService.getDefault().searchPayMode(this.bRmbPayCode);
            	double tmpDblRmb = (dblhlje-dblCalcHlje) * zlMode.zlhl / bRmbMode.zlhl; //* zlMode.zlhl; WANGYONG UPDATE 2014.12.12
            	
            	double dblPlJe = calcJgJd(tmpDblRmb,bRmbMode.sswrjd,bRmbMode.sswrfs);//补RMB金额
            	if (zlMode.str2 != null && zlMode.str2.equalsIgnoreCase("N"))
            	{
            		dblPlJe = 0;//表示该付款方式不需要补找零
            	}
            	double dblPLSy = tmpDblRmb-dblPlJe;//补RMB的损益
            					
            	
            	SalePayDef salepay;
	            //外币找零
				salepay         = new SalePayDef();
				salepay.syjh    = saleBS.saleHead.syjh;
				salepay.fphm    = saleBS.saleHead.fphm;
				salepay.paycode = zlMode.code;
				salepay.payname = zlMode.name ;//+ "找零";//标识为找零金额
				salepay.flag    = '2'; 					// 找零标志
				salepay.ybje    = dblCalcHlje;
				salepay.hl 	    = zlMode.zlhl;
				salepay.je 	    = ManipulatePrecision.doubleConvert(salepay.ybje * salepay.hl, 2,1);
				salepay.payno	= "";
				salepay.batch	= "";
				salepay.kye 	= 0;				
				salepay.idno 	= "";
				salepay.memo 	= "";
				
				// 只增加付款明细,没有付款对象
				saleBS.addSalePayObject(salepay,null);
				
				//补RMB
				if (dblPlJe>0 || dblPLSy>0)
				{
					salepay         = new SalePayDef();
    				salepay.syjh    = saleBS.saleHead.syjh;
    				salepay.fphm    = saleBS.saleHead.fphm;
    				salepay.paycode = bRmbMode.code;
    				salepay.payname = Language.apply("补") + bRmbMode.name;//标识为补的币种金额
    				salepay.flag    = '2'; 					// 找零标志
    				salepay.ybje    = dblPlJe;
    				salepay.hl 	    = bRmbMode.zlhl;
    				salepay.je 	    = ManipulatePrecision.doubleConvert(salepay.ybje * salepay.hl, 2,1);
    				salepay.payno	= "";
    				salepay.batch	= "";
    				salepay.kye 	= 0;				
    				salepay.idno 	= "";
    				salepay.memo 	= "";
    				salepay.num1	=ManipulatePrecision.doubleConvert(dblPLSy,2,1);//补RMB产生的汇率损益或不补RMB产生的损益
    				
    				// 只增加付款明细,没有付款对象
    				saleBS.addSalePayObject(salepay,null);
				}
					    				
				
            	return true;
	        }
	        catch(Exception ex)
	        {
	        	ex.printStackTrace();
	        	return false;
	        }
	        
		}
	    
	    //查找最后一个可找零的付款方式
	    public String getLastZlCode()
	    {
	    	try
	    	{
	    		SalePayDef sp = null,spyy = null;
	    		 // 反向查找最后一个可找零的付款方式进行找零
	            double zl = ManipulatePrecision.doubleConvert(saleHead.sjfk - saleBS.saleyfje - saleBS.salezlexception,2,1);
	            double tzl = 0;
	            for (int i = salePayment.size() - 1; zl > 0 && i >= 0; i--)
	            {
	            	sp = (SalePayDef) salePayment.elementAt(i);
	            	if (sp.flag != '1') continue;
	            	
	            	if (checkpay(sp)) continue;
	            	
	            	PayModeDef pm = DataService.getDefault().searchPayMode(sp.paycode);
	                if (pm == null)
	                {
	                    //new MessageBox("[" + sp.paycode + "]" + sp.payname + " 付款方式找不到!");
	                    //return false;
	                	continue;
	                }
	                
	                if (pm.iszl == 'Y')
	                {
	                	// 找零金额不能超过该付款方式的付款金额
	                    if (ManipulatePrecision.doubleConvert(sp.je,2,1) >= ManipulatePrecision.doubleConvert(zl - tzl, 2,1))
	                    {
	                    	tzl += ManipulatePrecision.doubleConvert(zl - tzl, 2,1);
	                    }
	                    else
	                    {
	                    	tzl += ManipulatePrecision.doubleConvert(sp.je, 2,1);
	                    }
	                    
	                    tzl  = ManipulatePrecision.doubleConvert(tzl,2,1);
	                    if (tzl >= zl) return pm.code;
	                }
	                
	            }
	    	}
	    	catch(Exception ex)
	    	{
	    		ex.printStackTrace();
	    	}
	    	return null;
	    }
	    
	//END ADD
	

	    public boolean calcChange()
		{
			StringBuffer buff  = new StringBuffer();
			if (!calcPreChange(buff)) return false;

	        // 找零过大
			double tzl = Convert.toDouble(buff);
			String lastZlPayCode = getLastZlCode();
			PayModeDef lastZlMode = null; //DataService.getDefault().searchPayMode(lastZlPayCode);
			if (lastZlPayCode != null)
			{
				lastZlMode = DataService.getDefault().searchPayMode(lastZlPayCode);
			}
			if (lastZlMode==null)
			{
				//若没有找到最后付款的可找零的付款方式,则按原方式处理
				if (tzl > GlobalInfo.sysPara.chglimit)
		        {
		        	new MessageBox(Language.apply("找零金额过大,请减少付款金额"));
		            return false;
		        }
			}
			else
			{
				double maxZl = lastZlMode.num1;
				if (maxZl<=0) maxZl= GlobalInfo.sysPara.chglimit;
				
				//取最后付款的可找零的付款方式的最大找零金额
				double currZl = ManipulatePrecision.doubleConvert(tzl / lastZlMode.zlhl,2,1);
				if (currZl > maxZl)
				{
					new MessageBox(Language.apply("找零金额过大,请减少付款金额!\n\n[{0}]{1}最大找零为" + maxZl,new Object[]{lastZlMode.code,lastZlMode.name}));
		            return false;
				}
			 
			}
	        
	        
	        // 生成找零付款
	        zlTotal = tzl;
	        if (zlTotal > 0)
	        {	        	
	        	if (GlobalInfo.sysPara.paychgmore != 'Y')
	        	{
	        		if (GlobalInfo.sysPara.paychgmore == 'C')
	        		{
	        			//多币种找零+补人民币
	        			if (!new PaymentChangeAddForm().open(this))
	            		{
	            			return false;
	            		}
	        			
	        			return true;
	        		}
	        		
	        		// 按收银截断方式计算找零金额
	        		if (String.valueOf(GlobalInfo.sysPara.zljd).trim().length() <= 0 || GlobalInfo.sysPara.zljd == 'Y')zlTotal = saleBS.getDetailOverFlow(zlTotal);
	        		else zlTotal = saleBS.getDetailOverFlow(zlTotal,GlobalInfo.sysPara.zljd);
	        		

	        		if (zlTotal > 0)
	        		{
	        			//找到最后一个找零付款方式
	        	        PayModeDef lastzlpaymode = null;
	        	        
	        	        // 使用人民币找零
	            		if (GlobalInfo.sysPara.paychgmore == 'B')
	            		{
	            			lastzlpaymode = DataService.getDefault().searchPayMode("01");
	            		}
	            		
	            		if (lastzlpaymode == null)
	            		{

		        	        for (int i = salePayment.size() - 1; i >= 0; i--)
		        	        {
		        	        	SalePayDef sp = (SalePayDef) salePayment.elementAt(i);
		        	        	if (sp.flag != '1') continue;
		        	        	
		        	        	PayModeDef pm = DataService.getDefault().searchPayMode(sp.paycode);
		        	            if (pm.iszl == 'Y')
		        	            {
		        	                lastzlpaymode = pm;
		        	                break;
		        	            }
		        	        }
		        	        
			        		// 如果付款的找零方式不是本位币找零方式,则重新查找本位币付款方式,保证找零总是找本位币
			        		if (ManipulatePrecision.doubleCompare(lastzlpaymode.zlhl,1.0000,4) != 0)
			        		{
			        			PayModeDef pm = null;
			        			for (int i=0;i<GlobalInfo.payMode.size();i++)
			        			{
			        				pm = (PayModeDef)GlobalInfo.payMode.elementAt(i);
			        				if (pm.iszl == 'Y' && 
			        					ManipulatePrecision.doubleCompare(pm.zlhl,1.0000,4) == 0)
			        				{
			        					lastzlpaymode = pm;
			        					break;
			        				}
			        			}
			        		}
	            		}
				        // 记录找零明细
		        		if (!chgAccount(lastzlpaymode,String.valueOf(zlTotal))) return false;
	        		}
	        	}
	        	else
	        	{
	        		// 多币种找零模式
	        		if (!new PaymentChangeForm().open(this))
	        		{
	        			return false;
	        		}
	        	}
	        }
	        
	        return true;
		}
				
		
		 // 根据付款方式的付款精度,计算付款金额
	    public String getChgMoneyByPrecision(double je,PayModeDef mode)
	    {
	    	//付款界面以舍入方式来控制精度(只用于多币种找零),否则与舍入方式冲突 wangyong by 2013.6.25
	    	double value=je;
	    	if (mode.sswrfs=='0')
	    	{
	    		//精确到分
	    		value = formatDecimal(je,"#.00");
	    	}
	    	else if (mode.sswrfs=='1')
	    	{
	    		//四舍五入到角
	    		value = formatDecimal(je,"#.0");
	    	}
	    	else if (mode.sswrfs=='2')
	    	{
	    		//截断到角
	    		value = formatDecimal(je- 0.04,"#.0");
	    	}
	    	else if (mode.sswrfs=='3')
	    	{
	    		//四舍五入到元
	    		value = formatDecimal(je,"#");
	    	}
	    	else if (mode.sswrfs=='4')
	    	{
	    		//截断到元
	    		value = formatDecimal(je- 0.4,"#");
	    	}
	    	else
	    	{
	    		int jd;
	    		if (mode.sswrjd == 0)
	            {
	                jd = 2;
	            }
	            else
	            {
	            	jd = ManipulatePrecision.getDoubleScale(mode.sswrjd);
	            }
	    		if (mode.sswrfs=='Y')
	    		{
	    			//截断
	    			value = ManipulatePrecision.doubleConvert(je, jd, 0);
	    		}
	    		else if (mode.sswrfs=='N')
	    		{
	    			//四舍五入
	    			value = ManipulatePrecision.doubleConvert(je, jd, 1);
	    		}
	    		else
	    		{
	    			//精确到分
	    			value = formatDecimal(je,"#.00");
	    		}
	    	}
	    	return ManipulatePrecision.doubleToString(value);
	    	
	        /*int jd;
	        
	        if (mode.sswrjd == 0) jd = 2;
	        else jd = ManipulatePrecision.getDoubleScale(mode.sswrjd);
	        
	        if (jd>1) jd=1;
	        
	        double ye = ManipulatePrecision.doubleConvert(je,jd,1);
	        
			return ManipulatePrecision.doubleToString(ye,jd,1);*/
	    }
}
