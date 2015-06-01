package bankpay.Payment;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Payment.PaymentMzkForm;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.swtdesigner.SWTResourceManager;

public class SaleByCs_PaymentMzk extends PaymentMzk
{
	DataDef mzkdatadef = new DataDef();
	
	public class DataDef
	{
		// 余次
		public double yc = 0;
		// 是否允许列表以外的销售
		public boolean isexternlist = true;
		// 商品列表
		public Vector goodscode = new Vector();
		// 标准金额
		public double money = 0;
		// 显示信息
		public String message = "";
		
		// 以下数据通过计算获得
		// 本笔消费次数
		public double bccs = 0;
		// 本次可付款金额
		public double bcje = 0;
		
		public void clear()
		{
			yc = 0;
			isexternlist = true;
			goodscode.clear();
			money = 0;
			message = "";
			bccs = 0;
			bcje = 0;
		}
	}
	
	public boolean createSalePay(String money)
	{
		if (super.createSalePay(money))
		{
			salepay.payname = salepay.payname + "[" + ManipulatePrecision.doubleToString(mzkdatadef.bccs,0,1) + "]";
			return true;
		}
		
		//
		salepay = null;
		return false;
	}
	
	public SalePayDef inputPay(String money)
	{
		try
		{
			// 退货小票不能使用,退货扣回按销售算
			if (checkMzkIsBackMoney() && GlobalInfo.sysPara.thmzk != 'Y')
			{
				new MessageBox("退货时不能使用" + paymode.name);
				return null;
			}
			
			// 先检查是否有冲正未发送
			if (!sendAccountCz()) return null;
			
			// 打开明细输入窗口
			payfrm = new PaymentMzkForm();
			payfrm.open(this,saleBS);
			
			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
        }
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return null;
	}
	
	protected boolean needFindAccount()
	{
		if (payfrm != null && payfrm.Moneytxt != null) 
		{
			payfrm.Moneytxt.setEditable(false);
			payfrm.Moneytxt.setBackground(SWTResourceManager.getColor(255, 255, 255));
		}
		
		return true;
	}
	
	PaymentMzkForm payfrm = null;
	
	public void showAccountYeMsg()
	{
		if(!messDisplay) return;
		
	    StringBuffer info = new StringBuffer();
	    
	    String text = "付";
	    double yc = mzkdatadef.yc - mzkdatadef.bccs;
	    if (checkMzkIsBackMoney())
	    {
	    	text = "退";
	    	yc = mzkdatadef.yc + mzkdatadef.bccs;
	    }
	    
	    info.append("卡内的余额为 : " + Convert.appendStringSize("",ManipulatePrecision.doubleToString(mzkdatadef.yc,0,1),0,12,12,1) + "次" + "\n");
	    info.append("本次" + text + "款次数 : " +  Convert.appendStringSize("",ManipulatePrecision.doubleToString(mzkdatadef.bccs,0,1),0,12,12,1) + "次" + "\n");
	    info.append("本次抵" + text + "金额 : " + Convert.appendStringSize("",ManipulatePrecision.doubleToString(mzkdatadef.bcje),0,12,12,1) + "元"  + "\n");
	    info.append(text+"款后的余额 : " + Convert.appendStringSize("",ManipulatePrecision.doubleToString(yc,0,1),0,12,12,1) + "次" + "\n");
	    
	    new MessageBox(info.toString());
	}
	
	public boolean findMzk(String track1, String track2, String track3)
	{		
		mzkdatadef.clear();
		
		if (!super.findMzk(track1, track2, track3))
		{
			return false;
		}
		
		// 剩余次数,是否允许列表以外的销售,商品编码1|商品编码2|商品编码3,每次销售的基本金额
		String strs[] = mzkret.str3.split(",");
		
		if (strs.length < 4)
		{
			new MessageBox("找卡失败,数据库返回数据格式不正确!");
			return false;
		}
		
		try
		{
			mzkdatadef.yc = ManipulatePrecision.doubleConvert(Convert.toDouble(strs[0]),2,1);
			
			mzkdatadef.isexternlist = strs[1].equals("Y")?true:false;
			
			String strs1[] = strs[2].split("\\|");
			for (int i = 0;i < strs1.length;i++)
			{
				mzkdatadef.goodscode.add(strs1[i]);
			}
			
			mzkdatadef.money = ManipulatePrecision.doubleConvert(Convert.toDouble(strs[3]),2,1);
			
			if (strs.length > 4) mzkdatadef.message = strs[4];
			
			if (mzkdatadef.money > 0)
			{
				CalcA();
			}
			else
			{
				CalcB();
			}
		}
		finally
		{
			mzkret.ye = mzkdatadef.bcje;
			mzkreq.memo = ManipulatePrecision.doubleToString(mzkdatadef.bccs);
		}
		
		return true;
	}
	
	// 自动计算付款金额,并生成付款方式
	public boolean AutoCalcMoney()
	{
		return true;
	}
	
	protected String getDisplayStatusInfo()
	{
		try
		{
			StringBuffer info = new StringBuffer();
			// 显示面值卡返回的提示信息
			if (mzkdatadef.message.length() > 0)
			{
				info.append(mzkdatadef.message + "\n");
			}

		    String text = "付";
		    double yc = mzkdatadef.yc - mzkdatadef.bccs;
		    if (checkMzkIsBackMoney())
		    {
		    	text = "退";
		    	yc = mzkdatadef.yc + mzkdatadef.bccs;
		    }
		    
		    info.append("卡内的余额为 : " + Convert.appendStringSize("",ManipulatePrecision.doubleToString(mzkdatadef.yc,0,1),0,12,12,1) + "次" + "\n");
		    info.append("本次" + text + "款次数 : " +  Convert.appendStringSize("",ManipulatePrecision.doubleToString(mzkdatadef.bccs,0,1),0,12,12,1) + "次" + "\n");
		    info.append("本次" + text + "抵金额 : " + Convert.appendStringSize("",ManipulatePrecision.doubleToString(mzkdatadef.bcje),0,12,12,1) + "元"  + "\n");
		    info.append(text+"款后的余额 : " + Convert.appendStringSize("",ManipulatePrecision.doubleToString(yc,0,1),0,12,12,1) + "次" + "\n");

			return info.toString();
		}
		catch(Exception er)
		{
			er.printStackTrace();
			return "";
		}
	}

	// 以基准金额计算
	protected void CalcA()
	{
		if (saleBS != null)
		{
			double salepayye = Convert.toDouble(saleBS.getPayMoneyByPrecision(saleBS.calcPayBalance() / paymode.hl,paymode));

			// 计算付款余额
			// 首先算在列表当中的商品
			Vector vc = new Vector(); // vc用来记录已经参与过的结果
			for (int i = 0;i < saleBS.saleGoods.size(); i++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef)saleBS.saleGoods.get(i);
				double je = ManipulatePrecision.div((sgd.hjje-sgd.hjzk),sgd.sl);
				
				int j = 0;
				for (j = 0;j < mzkdatadef.goodscode.size();j++)
				{
					String code = (String)mzkdatadef.goodscode.get(j);
					if (sgd.code.equals(code))
					{
						double sl =0;
						if (mzkdatadef.bccs + sgd.sl > mzkdatadef.yc)
						{
							sl = mzkdatadef.yc - mzkdatadef.bccs;
						}
						else
						{
							sl = sgd.sl;
						}

			            for (int k = 1; k <= sl; k++)
			            {
			            	if (mzkdatadef.bcje + je > salepayye) return;
			            	
							mzkdatadef.bcje += je;
							mzkdatadef.bccs++;
			            }
			            
			            break;
					}
				}
				
				if (j >= mzkdatadef.goodscode.size())
				{
					vc.add(String.valueOf(i));
				}
			}
			
			// 再算不在列表当中的
			if (mzkdatadef.isexternlist)
			{
				for (int i = 0;i < vc.size();i++)
				{
					int index  = Convert.toInt(vc.get(i));
					
					SaleGoodsDef sgd = (SaleGoodsDef)saleBS.saleGoods.get(index);
					
					double sj = ManipulatePrecision.div((sgd.hjje-sgd.hjzk),sgd.sl);
					
					// 如果商品售价大于,卡的基准金额则,取卡的基准金额,否则取商品售价
					double je = Math.min(mzkdatadef.money,sj);
					
					double sl =0;
					if (mzkdatadef.bccs + sgd.sl > mzkdatadef.yc)
					{
						sl = mzkdatadef.yc - mzkdatadef.bccs;
					}
					else
					{
						sl = sgd.sl;
					}

		            for (int k = 1; k <= sl; k++)
		            {
		            	if (mzkdatadef.bcje + je > salepayye) return;
		            	
						mzkdatadef.bcje += je;
						mzkdatadef.bccs++;
		            }
				}
			}
		}
	}
	
	// 以现在价格计算
	protected void CalcB()
	{
		if (saleBS != null)
		{
			double money = 0;
			try
			{
				money = mzkdatadef.money;
				
				mzkdatadef.money = 0;
				
				double salepayye = Convert.toDouble(saleBS.getPayMoneyByPrecision(saleBS.calcPayBalance() / paymode.hl,paymode));
				
				// 计算付款余额
				// 首先算在列表当中的商品
				// vc用来记录列表外商品
				// vc1用来记录列表内商品
				Vector vc = new Vector(); 
				Vector vc1 = new Vector();
				for (int i = 0;i < saleBS.saleGoods.size(); i++)
				{
					SaleGoodsDef sgd = (SaleGoodsDef)saleBS.saleGoods.get(i);
					double je = ManipulatePrecision.div((sgd.hjje-sgd.hjzk),sgd.sl);
					
					int j = 0;
					for (j = 0;j < mzkdatadef.goodscode.size();j++)
					{
						String code = (String)mzkdatadef.goodscode.get(j);
						if (sgd.code.equals(code))
						{
							double sl =0;
							if (mzkdatadef.bccs + sgd.sl > mzkdatadef.yc)
							{
								sl = mzkdatadef.yc - mzkdatadef.bccs;
							}
							else
							{
								sl = sgd.sl;
							}
	
				            for (int k = 1; k <= sl; k++)
				            {
				            	if (mzkdatadef.bcje + je > salepayye) return;
				            	
								mzkdatadef.bcje += je;
								mzkdatadef.bccs++;
				            }
				            
				            vc1.add(String.valueOf(i));
				            
				            break;
						}
					}
					
					if (j >= mzkdatadef.goodscode.size())
					{
						vc.add(String.valueOf(i));
					}
				}
				
				// 可以收列表外当中的商品,并且存在销售单据当中存在列表外商品
				if (mzkdatadef.isexternlist && vc.size() > 0)
				{
					 // 存在列表内商品,则不表查找新列表中商品来计算差价,否则,查询任意一个列表内商品
					 if (vc1.size() > 0)
					 {
						 mzkdatadef.money = ((SaleGoodsDef)saleBS.saleGoods.get(Convert.toInt(vc1.get(0)))).lsj;
					 }
					 else
					 {
						 for (int i = 0; i < mzkdatadef.goodscode.size();i++)
						 {
							 String goodscode = (String)mzkdatadef.goodscode.get(i);
							 
							 GoodsDef gd = saleBS.findGoodsInfo(goodscode,saleBS.saleEvent.yyyh.getText().trim(),saleBS.curyyygz,"",false,null,false);
							 
							 if (gd == null) continue;
							 
							 mzkdatadef.money = gd.lsj;
							 
							 break;
						 }
					 }
				
				    if (mzkdatadef.money <= 0) return;
					 
				    for (int i = 0;i < vc.size();i++)
					{
						int index  = Convert.toInt(vc.get(i));
						
						SaleGoodsDef sgd = (SaleGoodsDef)saleBS.saleGoods.get(index);
						
						double sj = ManipulatePrecision.div((sgd.hjje-sgd.hjzk),sgd.sl);
						
						// 获取商品零售价,与基准金额的价差(以获得需要商品需要补多少钱进行交易)
						double jc = ManipulatePrecision.sub(sgd.lsj, mzkdatadef.money);
						
						// 获得该商品卡可付金额
						double je = ManipulatePrecision.sub(sj,jc);
						
						// 如果可付金额小于0则计算下一个商品
						if (je < 0) continue;
						
						double sl =0;
						if (mzkdatadef.bccs + sgd.sl > mzkdatadef.yc)
						{
							sl = mzkdatadef.yc - mzkdatadef.bccs;
						}
						else
						{
							sl = sgd.sl;
						}

			            for (int k = 1; k <= sl; k++)
			            {
			            	if (mzkdatadef.bcje + je > salepayye) return;
			            	
							mzkdatadef.bcje += je;
							mzkdatadef.bccs++;
			            }
					}
				}
			}
			catch(Exception ex)
			{
				return;
			}
			finally
			{
				mzkdatadef.money = money;
			}
		}
	}
}
