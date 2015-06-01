package custom.localize.Bjkl;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.TaskThread;
import com.efuture.javaPos.Logic.ConnNetWorkBS;
import com.efuture.javaPos.Logic.WithdrawBS;
import com.efuture.javaPos.Struct.PayinDetailDef;
import com.efuture.javaPos.Struct.PayinHeadDef;


public class Bjkl_WithdrawBS extends WithdrawBS
{

	public void AutoDisplayWithdrawBsMoney(Table tabInputMoney,Label lblCountAmount, Label lblCountMoney,String date,String code)
    {
    	ResultSet rs = null;
    	StringBuffer tempstr = null;
    	 PayinDetailDef pdd = null;
    	 
    	if (GlobalInfo.sysPara.withdrawauotbsmoney == null || GlobalInfo.sysPara.withdrawauotbsmoney.trim().equals("")) return ;
    	
    	try
    	{
    		
    		payListMode.clear();
    		
	    	String payincodes[] = GlobalInfo.sysPara.withdrawauotbsmoney.split("\\|");
	    	
	    	//1 - 获得收银机最新联网状态
	    	TaskThread task = new TaskThread();
	    	task.run();
	    	
	    	// 2 - 京客隆缴款单联网打印，当发现是脱网状态时，自动联网
	    	if (GlobalInfo.isOnline == false)
	    	{
	    		ConnNetWorkBS work = new ConnNetWorkBS();
	    		work.setConnNet();
	    	}
	    	// 3 - 自动联网后，检查联网状态，如果还是脱网，提示联网失败
	    	if (GlobalInfo.isOnline == false)
	    	{
	    		new MessageBox("联网失败!!!\n\n无法联网打印缴款单!!!");
	    		return ;
	    	}
	    	
	    	//取出后台统计的缴款信息   缴款代码，缴款笔数，缴款金额
	    	Vector summary = new Vector();
	    	summary = new Bjkl_NetService().getSalepaysummary(GlobalInfo.syjStatus.syyh, String.valueOf(GlobalInfo.syjStatus.bc), date, summary);
	    	
	    	for (int i = 0;i < payincodes.length;i++)
	    	{
	    		
	    		int bs = 0;
    			double je = 0;
	    		// 缴款代码
	    		String payincode = payincodes[i].trim().split("=")[0];
	    		
	    		for (int j = 0;j < summary.size();j++)
	    		{
	    			String incode = ((String[]) summary.elementAt(j))[0];
	    			if (payincode.equals(incode))
	    			{
	    				bs =  Convert.toInt( ((String[]) summary.elementAt(j))[1] );
	    				je =  Convert.toDouble( ((String[]) summary.elementAt(j))[2] );
	    			}
	    		}
	    		   			
//	    		if ((rs = sql.selectData("select sum(bs) as bs,sum(je) as je from salepaysummary where syyh = '"+ GlobalInfo.syjStatus.syyh + "' and  bc ='" + GlobalInfo.syjStatus.bc + "' and (" + tempstr.toString() + ")")) != null)
	    		
	    		
	    		if (bs <= 0 || je == 0)
	    			continue;
	    		
	    		boolean bool = false;
	    		
	    		int k = 0;
	    		for (k = 0;k < paymoddef.length;k++)
	    		{
	    			if (payincode.equals(paymoddef[k][0])) 
	    			{
	    				bool = true;
	    				break;
	    			}
	    		}
	    		
	    		if (bool)
	    		{
	    			tabInputMoney.getItem(k).setText(1,String.valueOf(bs));
	    			tabInputMoney.getItem(k).setText(2,ManipulatePrecision.doubleToString(je));
	    			
	    			 pdd       = new PayinDetailDef();
	                 pdd.syjh  = GlobalInfo.syjDef.syjh;
	                 pdd.seqno = Integer.parseInt(code);
	                 pdd.rowno = payListMode.size() + 1;
	                 pdd.code  = payincode;
	                 pdd.zs    = bs;
	                 pdd.je    = je;
	                 pdd.hl    = 1;
	                 payListMode.add(pdd);
	    		}
	    	}
	    	
	    	this.getSum(lblCountAmount, lblCountMoney);
    	}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	finally
    	{
    		if (tempstr != null)
    		{
    			tempstr.delete(0,tempstr.length());
    			tempstr = null;
    		}
    		
    	}
    }    
    
    //京客隆要求在没有缴款明细时，也要打印缴款单     --生成缴款主单与明细
    public boolean createPaylistmode(String code, String date, char jkbc)
    {
        try
        {
            double totalmoney = 0;
            PayinDetailDef pdd = null;
             
            for (int j = 0; j < payListMode.size(); j++)
            {
                pdd = (PayinDetailDef) payListMode.get(j);

                if (((pdd.zs == 0) || (pdd.je == 0)))
                {
                    payListMode.remove(j);
                    j--;
                    pdd = null;
                }
            }
            
//            if (payListMode.size() < 1 )
//            {
//            	new MessageBox(Language.apply("没有输入有效的缴款明细!"), null, false);
//            	
//            	return false;
//            }
            
            for (int j = 0; j < payListMode.size(); j++)
            {
                pdd        = (PayinDetailDef) payListMode.get(j);
                pdd.rowno  = j + 1;
                
                // 京客隆要求   15 - 会员零钱包  19 - 积分折扣  不统计
                if (pdd.code.trim().equals("15") || pdd.code.trim().equals("19"))
                {
                	continue;
                }
                totalmoney = totalmoney + ManipulatePrecision.mul(pdd.je , pdd.hl);
            }

            phd       = new PayinHeadDef();
            phd.jkbc  = jkbc;
            phd.syjh  = GlobalInfo.syjDef.syjh;
            phd.seqno = Integer.parseInt(code);

            ManipulateDateTime mdt = new ManipulateDateTime();
            phd.rqsj  = mdt.getDateBySlash() + " " + mdt.getTime();
            mdt       = null;
            phd.syyh  = GlobalInfo.posLogin.gh;
            phd.jkrq  = date;
            phd.je    = totalmoney;
            phd.netbz = 'N';
            phd.hcbz  = 'N';
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            new MessageBox(Language.apply("生成缴款主单或明细失败"), null, false);
            
            return false;
        }
    }

    //获得缴款合计张数
    protected int getPayAmount()
    {
        try
        {
            int num = 0;

            if (payListMode == null || payListMode.size() <= 0)
            {
                return 0;
            }
            else
            {
                Iterator iterator = payListMode.iterator();

                while (iterator.hasNext())
                {
                	// 14 - 会员零钱包 ,15 - 银行赠送  不统计
                    PayinDetailDef pdd = (PayinDetailDef) iterator.next();
                    if (pdd.code.trim().equals("19") || pdd.code.trim().equals("15"))
                    {
                    	continue;
                    }
                    
                    num = num + pdd.zs;                    	
                }

                return num;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return -1;
        }
    }
    
    //获得缴款单的总金额
    protected String getPayTotalMoney()
    {
        try
        {
            double totalmoney = 0.00;

            if (payListMode == null || payListMode.size() <= 0)
            {
                return "0.00";
            }
            else
            {
                Iterator iterator = payListMode.iterator();

                while (iterator.hasNext())
                {
                	// 14 - 会员零钱包 ,15 - 银行赠送  不统计
                    PayinDetailDef pdd = (PayinDetailDef) iterator.next();
                    if (pdd.code.trim().equals("19") || pdd.code.trim().equals("15"))
                    {
                    	continue;
                    }
                    totalmoney = totalmoney + (pdd.je * pdd.hl);
                }

                return ManipulatePrecision.doubleToString(totalmoney);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return null;
        }
    }

}
