/**
 * 
 */
package custom.localize.Zsbh;

import java.io.BufferedReader;
import java.io.IOException;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

/**
 * @author wangyong
 *
 */
public class Zsbh_PaymentBank extends PaymentBank
{
	public Zsbh_PaymentBank()
	{		
		super();
	}
	
	public Zsbh_PaymentBank(PayModeDef mode,SaleBS sale)
	{
		super(mode,sale);
	}
	
	public Zsbh_PaymentBank(SalePayDef pay,SaleHeadDef head)
	{
		super(pay,head);
	}
	
		
	public SalePayDef getAllotSalePay()
	{
		return null;
	}
	
	public boolean cancelPay()
	{
		new MessageBox("不能删除银行卡付款");
		return false;
	}
	
	public SalePayDef inputPay(String money)
	{
		try
		{
			// 如果允许单独进行银联消费则先检查是否有单独的银联消费交易要分配
			if (GlobalInfo.sysPara.allowbankselfsale == 'Y')
			{
				getAllotSalePay();
				if (salepay != null) return salepay;
			}
			
			// 打开金卡输入窗口
			inputMoney = money;
			CreatePayment.getDefault().getPaymentBankForm().open(this,PaymentBank.XYKXF);			
			
			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
        }
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return null;		
	}
	
	public void accountPay(boolean ret,BankLogDef bld,PaymentBankFunc pbf)
	{		
		try
		{
			super.accountPay(ret, bld, pbf);
			
			if (!ret) return;
			salepay.batch = Convert.increaseCharForward(String.valueOf(bld.trace),'0', 6);
			salepay.memo = bld.memo;//授权码
			salepay.str1  = salepay.batch;
			salepay.idno  = salepay.batch;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		
	}
	
	public static void printXYKDoc(String bankfilename,boolean isCut)
	{
        BufferedReader br = null;
        String line = null;
        String strDocDir = ConfigClass.LocalDBPath + "//Invoice//";
		String filename = strDocDir + bankfilename ;//  "syjh" + "_" + "fphm" + "_" + batch + ".txt";	
        //String filename = "bankdoc_" + batch + ".txt";
        
        //if (!PaymentBank.haveXYKDoc) return;
        
        try
        {
            if (!PathFile.fileExist(filename))
            {
                new MessageBox("找不到[" + bankfilename + "]签购单打印文件!");

                return;
            }

           
    		//
            for (int i = 0; i < GlobalInfo.sysPara.bankprint; i++)
            {
            	 br = CommonMethod.readFileGB2312(filename);
                 if (br == null)
                 {
                     new MessageBox("打开流水号[" + bankfilename + "]的签购单打印文件失败!");

                     return;
                 }

                 //((Zsbh_SaleBillMode) SaleBillMode.getDefault()).printMzkOrBankBillTitle();
                 
            	Printer.getDefault().startPrint_Normal();

                while ((line = br.readLine()) != null)
                {
                    if (line.length() <= 0)
                    {

                		if (ConfigClass.RepPrintTrack != 3)
                		{
                			Printer.getDefault().printLine_Normal("\n");
                		}
                		else
                		{
                			Printer.getDefault().printLine_Journal("\n");
                		}
                    	
                    }
                    else
                    {
                    	if (ConfigClass.RepPrintTrack != 3)
                    	{
                    		Printer.getDefault().printLine_Normal(line);
                    	}
                    	else
                    	{
                    		Printer.getDefault().printLine_Journal(line);
                    	}
                    	
                    }
                    
                    
                }

            	//打印签购单后,打印空行
            	try
            	{
            		int num = 0;
            		if (ConfigClass.CustomItem5.split("\\|").length >= 3)
            		{
            			num = Convert.toInt(ConfigClass.CustomItem5.split("\\|")[2].trim());
            		}
            		if (num > 0)
            		{
            			for (int j = 0; j < num; j++)
            			{
            				if (ConfigClass.RepPrintTrack != 3)
                        	{
                        		Printer.getDefault().printLine_Normal("\n");
                        	}
                        	else
                        	{
                        		Printer.getDefault().printLine_Journal("\n");
                        	}
            			}
            		}
            	}
            	catch(Exception ex)
            	{
            		System.out.print("打印银行签购单时异常:");
            		ex.printStackTrace();
            	}
            	
                //
                if (ConfigClass.RepPrintTrack != 3)
                {
                	
                }
                else
                {
                	if (isCut) Printer.getDefault().cutPaper_Journal();
                }            
                
                // 关闭并删除打印文件
                br.close();
                br = null;
            	//File f = new File(filename);
            	//f.delete();
            }
    		
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        	
            new MessageBox("打印签购单异常:\n\n" + e.getMessage());
        }
        finally
        {
            if (br != null)
            {
                try
				{
					br.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
            }
        }
	}
	
}
