package custom.localize.Gzbh;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Payment.PaymentMzkEvent;
import com.efuture.javaPos.Payment.PaymentMzkForm;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Gzbh_PaymentMzk extends PaymentMzk
{

	public Gzbh_PaymentMzk()
	{
		super();
	}

	public Gzbh_PaymentMzk(PayModeDef mode, SaleBS sale)
	{
		super(mode, sale);
	}

	public Gzbh_PaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		super(pay, head);
	}

	protected boolean getPasswdBeforeFindMzk(StringBuffer passwd)
	{
		if (saleBS == null)
		{
			return super.getPasswdBeforeFindMzk(passwd);
		}
		else
		{
			if (saleBS.saletype.equals(SellType.RETAIL_BACK)  || saleBS.saletype.equals(SellType.BATCH_BACK))
			{
				TextBox txt = new TextBox();

				if (!txt.open("请输入流水", "", "请输入流水", passwd, 0, 0, false, TextBox.AllInput)) { return false; }
			}
			return true;
		}
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

			// 新大新消费卡，消费券，条码现金券 不能退货
			if (checkMzkIsBackMoney() && ("0021".equals(paymode.code) || "0031".equals(paymode.code)))
			{
				new MessageBox("退货时不能使用" + paymode.name);
				return null;
			}

			// 先检查是否有冲正未发送
			if (!sendAccountCz()) return null;

			// 打开明细输入窗口
			new PaymentMzkForm().open(this, saleBS);

			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return null;

	}

	public void showAccountYeMsg()
	{
	}

	public boolean findMzk(String track1, String track2, String track3)
	{
		if ("0031".equals(paymode.code))
		{
			if (track2 != null && track2.length() != 20)
			{
				new MessageBox("条码现金券券号不正确，请确认是否本公司卡");
				return false;
			}
		}
		else
		{
			if (track2 != null && track2.length() <= 13)
			{
				new MessageBox("磁道不合法，请确认是否本公司卡");
				return false;
			}
		}
		
		return super.findMzk(track1, track2, track3);
	}
	
	// 条码现金券（0031） 必须一次性付完
	public boolean checkMzkMoneyValid()
	{
		if (!super.checkMzkMoneyValid()) return false;

		if ("0031".equals(salepay.paycode))
		{
			// 纸券必须一次性使用完
			if (ManipulatePrecision.doubleCompare(salepay.ybje, this.getAccountYe(), 2) != 0)
			{
				if (new MessageBox(salepay.payname + "的每张券必须一次性付完!\n是否将剩余部分计入损溢？", null, true).verify() == GlobalVar.Key1)
				{
					// num1记录券付款溢余部分
					salepay.num1 = ManipulatePrecision.sub(ManipulatePrecision.mul(this.getAccountYe(), salepay.hl),
															Math.min(salepay.je, this.saleBS.calcPayBalance()));
					salepay.ybje = this.getAccountYe();
					salepay.je = ManipulatePrecision.doubleConvert(salepay.ybje * salepay.hl, 2, 1);
					return true;
				}
				else
				{
					return false;
				}
			}
		}
		return true;
	}
	
    // 设置条码现金券默认付款金额为券余额
	public double getPayJe(double moneyText)
	{
		if ("0031".equals(paymode.code))
		{
			return mzkret.ye;
		}
		else
		{
			return super.getPayJe(moneyText);
		}
	}
	
	public void setMoneyVisible(PaymentMzkEvent paymentMzkEvent)
	{
		if ("0031".equals(paymode.code))
		{
			paymentMzkEvent.moneyTxt.setEditable(false);
		}
	}
	
	public long getMzkSeqno()
	{

        PrintWriter pw = null;
        BufferedReader br = null;
        
        try
        {        
	        // 读取消费序号
        	String name = ConfigClass.LocalDBPath + "/" + paymode.code + "SaleSeqno.ini";
//	        String name = ConfigClass.LocalDBPath + "/SaleSeqno.ini";
	        File indexFile = new File(name);
	        
	        // 无消费序号文件，产生一个
	        if (!indexFile.exists())
	        {
	            pw = CommonMethod.writeFile(name);
	            pw.println("1");
	            pw.flush();
	            pw.close();
	            pw = null;
	        }
        
	        // 读取消费序号
	        br = CommonMethod.readFile(name);
	        String line = null;
	        long seq = 0;
	
            while ((line = br.readLine()) != null)
            {
                if (line.length() <= 0)
                {
                    continue;
                }
                else
                {
                	seq = Long.parseLong(line.trim());
                }
            }
            br.close();
            br = null;
            
            // 消费序号+1
            pw = CommonMethod.writeFile(name);
            if (seq < 999999999) pw.println(seq + 1);
            else pw.println(1);
            pw.flush();
            pw.close();
            pw = null;
            
            return seq;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            new MessageBox("读取消费序号失败!\n\n" + e.getMessage().trim());
            
            return -1;
        }
        finally
        {
        	try
        	{
	            if (pw != null) pw.close();
	            if (br != null) br.close();
        	}
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }            
	
	}
}
