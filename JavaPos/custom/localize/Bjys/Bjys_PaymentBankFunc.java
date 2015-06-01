package custom.localize.Bjys;

import java.io.BufferedReader;

import bankpay.Bank.BjjlBjzh_PaymentBankFunc;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;

public class Bjys_PaymentBankFunc extends BjjlBjzh_PaymentBankFunc
{
	public void XYKPrintDoc()
	{
		ProgressBox pb = null;
		 
		 try
		 {
			 String printName = "";
			 
			 if ((bld.type.equals(String.valueOf(PaymentBank.XYKXF)) || bld.type.equals(String.valueOf(PaymentBank.XYKCX))) ||
				 (bld.type.equals(String.valueOf(PaymentBank.XKQT1)) || bld.type.equals(String.valueOf(PaymentBank.XKQT2)))	 ||
				 (bld.type.equals(String.valueOf(PaymentBank.XKQT3)) || bld.type.equals(String.valueOf(PaymentBank.XKQT4)))  
				 )
			 {
               if (!PathFile.fileExist("C:\\u\\print\\print.txt"))
               {
                   new MessageBox("找不到签购单打印文件!");

                   return;
               }
               else
               {
                   printName = "C:\\u\\print\\print.txt";
               }
			 }
			 else if ((bld.type.equals(String.valueOf(PaymentBank.XYKCD))))
			 {
               if (!PathFile.fileExist("C:\\u\\print\\reprint.txt"))
               {
                   new MessageBox("找不到签购单重打印文件!");

                   return;
               }
               else
               {
                   printName = "C:\\u\\print\\reprint.txt";
               }
			 }
			 else if ((bld.type.equals(String.valueOf(PaymentBank.XYKJZ))))
			 {
               if (!PathFile.fileExist("C:\\u\\print\\settzh.txt"))
               {
                   new MessageBox("找不到中行结账打印文件!");

                   return;
               }
               else
               {
                   printName = "C:\\u\\print\\settzh.txt";
               }
			 }
			 else if (bld.type.equals(String.valueOf(PaymentBank.XKQT5)))
			 {
				 if (!PathFile.fileExist("C:\\u\\print\\settzhx.txt"))
				 {
                   new MessageBox("找不到资合信结账打印文件!");

                   return;
				 }
				 else
				 {
                   printName = "C:\\u\\print\\settzhx.txt";
				 }
			 }
			 else if (bld.type.equals(String.valueOf(PaymentBank.XKQT6)))
			 {
				 if (!PathFile.fileExist("C:\\u\\print\\settjfk.txt"))
				 {
                   new MessageBox("找不到积分卡结账打印文件!");

                   return;
				 }
				 else
				 {
                   printName = "C:\\u\\print\\settjfk.txt";
				 }
			 }
			 else if (bld.type.equals(String.valueOf(PaymentBank.XKQT7)))
			 {
				 if (!PathFile.fileExist("C:\\u\\print\\resettzh.txt"))
				 {
                   new MessageBox("找不到中行重印结账打印文件!");

                   return;
				 }
				 else
				 {
                   printName = "C:\\u\\print\\resettzh.txt";
				 }
			 }
			 else if (bld.type.equals(String.valueOf(PaymentBank.XKQT8)))
			 {
				 if (!PathFile.fileExist("C:\\u\\print\\resettzhx.txt"))
				 {
                   new MessageBox("找不到资合信重印结账打印文件!");

                   return;
				 }
				 else
				 {
                   printName = "C:\\u\\print\\resettzhx.txt";
				 }
			 }
			 else if (bld.type.equals(String.valueOf(PaymentBank.XKQT9)))
			 {
				 if (!PathFile.fileExist("C:\\u\\print\\resetjfk.txt"))
				 {
                   new MessageBox("找不到积分卡重印结账打印文件!");

                   return;
				 }
				 else
				 {
                   printName = "C:\\u\\print\\resetjfk.txt";
				 }
			 }
			 else
			 {
               new MessageBox("此金卡工程操作没有打印文件");

               return;
			 }
			 
			 pb = new ProgressBox();
	         pb.setText("正在打印银联签购单,请等待...");
	         
	         String mktcode = null;
	     	
	     	 if (GlobalInfo.sysPara.mktcode != null)
	     	 {
	 			if (GlobalInfo.sysPara.mktcode.split(",").length >= 2)
	 			{
	 				mktcode = GlobalInfo.sysPara.mktcode.substring(GlobalInfo.sysPara.mktcode.indexOf(",")+1);
	 			}
	 			else
	 			{
	 				mktcode = GlobalInfo.sysPara.mktcode;
	 			}
	     	 }
	     	 
	     	 int printamount = 1;
	     	 
	     	 //	亮马桥:0001 金源:0005 太源:0006
	     	 if (mktcode != null && mktcode.trim().equals("0005") || mktcode.trim().equals("0006"))
	     	 {
	     		printamount = GlobalInfo.sysPara.bankprint;
	     	 }
	     	 
	     	 for (int i = 0; i < printamount; i++)
	     	 {
	     		XYKPrintDoc_Start();
		     	
		     	BufferedReader br = null;
		     	 
	     		try
	     		{
                   br = CommonMethod.readFileGBK(printName);

                   if (br == null)
                   {
                       new MessageBox("打开" + printName + "打印文件失败!");

                       return;
                   }

                   //
                   String line = null;

                   while ((line = br.readLine()) != null)
                   {
                       if (line.trim().equals("CUT"))
                       {
                           break;
                       }

                       XYKPrintDoc_Print(line);
                   }
	     		}
	     		catch (Exception e)
	     		{
                   new MessageBox(e.getMessage());
	     		}
	     		finally
	     		{
                   if (br != null)
                   {
                       br.close();
                   }
	     		}
	     		
	     		XYKPrintDoc_End();
	         }
		 }
		 catch (Exception ex)
		 {
			 new MessageBox("打印签购单发生异常\n\n" + ex.getMessage());
	         ex.printStackTrace();
		 }
		 finally
		 {
			 if (pb != null)
			 {
               pb.close();
               pb = null;
			 }
		 }
	}
}
