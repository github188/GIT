package bankpay.Bank;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;
//襄阳万达

public class BjcsTjyt2_PaymentBankFunc extends BjcsTjyt_PaymentBankFunc
{
	private SaleBS saleBS = null;

	public String[] getFuncItem()
    {
        String[] func = new String[9];

        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
        func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
        func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
        func[3] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
        func[4] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
        func[5] = "[" + PaymentBank.XYKCD + "]" + "按系统参考号打印";
        func[6] = "[" + PaymentBank.XKQT1 + "]" + "重打印上笔";
        func[7] = "[" + PaymentBank.XKQT2 + "]" + "查 流 水";
        func[8] = "[" + PaymentBank.XYKJZ + "]" + "结算";

        return func;
    }
	
	public boolean getFuncLabel(int type, String[] grpLabelStr)
    {
		switch (type)
        {
        	case PaymentBank.XYKXF: //	消费
        		grpLabelStr[0] = null;
        		grpLabelStr[1] = null;
        		grpLabelStr[2] = null;
        		grpLabelStr[3] = null;
        		grpLabelStr[4] = "交易金额";
        	break;
        	case PaymentBank.XYKCX: //消费撤销
                grpLabelStr[0] = "参 考 号";
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易金额";
            break;
        	case PaymentBank.XYKTH://隔日退货   
				grpLabelStr[0] = "参 考 号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
			break;
        	case PaymentBank.XYKQD: //交易签到
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易签到";
            break;
        	case PaymentBank.XYKYE: //余额查询    
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "余额查询";
            break;
            /*
        	case PaymentBank.XYKCD: //签购单重打
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重打上笔签购单";
            break;*/
        	case PaymentBank.XKQT1:		// 按凭证号打印
        		grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重 打 印";
            break;    
            
        	case PaymentBank.XYKCD: //签购单重打
                grpLabelStr[0] = "系统参考号";
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重 打 印";
            break;
        	case PaymentBank.XKQT2:		// 查流水
        		grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "查 流 水";
        	break;	
        	case PaymentBank.XYKJZ:		// 结算
        		grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易结算";
        	break;	
        }
		
		return true;
    }
	
	public boolean getFuncText(int type, String[] grpTextStr)
    {
		switch (type)
		{
		 	case PaymentBank.XYKXF: 	// 消费
		        grpTextStr[0] = null;
		        grpTextStr[1] = null;
		        grpTextStr[2] = null;
		        grpTextStr[3] = null;
		        grpTextStr[4] = null;
		    break;
		 	case PaymentBank.XYKCX: 	// 消费撤销
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = null;
            break;
		 	case PaymentBank.XYKTH:		//隔日退货   
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
			break;
		 	case PaymentBank.XYKQD: 	//交易签到
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始交易签到";
            break;
		 	case PaymentBank.XYKYE: 	//余额查询    
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始余额查询";
            break;
            /*
		 	case PaymentBank.XYKCD: 	//签购单重打
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键签购单重打";
            break;	*/
		 	case PaymentBank.XKQT1:		// 按凭证号打印
		 		grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键重打印";
		 	break;	
		 
		 	case PaymentBank.XYKCD: 	//签购单重打
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键系统参考号打印";
            break;
		 	case PaymentBank.XKQT2:		// 查流水
		 		grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按查流水";
		 	break;	
		 	case PaymentBank.XYKJZ:		// 结算
		 		grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键结算";
		 	break;	
		}
		
		return true;
    }
	
	public boolean XYKWriteRequest(int type, double money, String track1,String track2, String track3,String oldseqno, String oldauthno,String olddate, Vector memo)
	{
		try
		{
			 String line = "";
			 
	         String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
	         
	         for (int i = jestr.length(); i < 12; i++)
             {
                 jestr = "0" + jestr;
             }
	         
	         if (memo.size() >=2 ) saleBS = (SaleBS)memo.elementAt(2);
	         
	         //	 根据不同的类型生成文本结构
	         switch (type)
	         {
	        	 case PaymentBank.XYKXF:
	        		 if (saleBS != null)
	        		 {
	        			 line 		= "0" + "|" + jestr + "|" + saleBS.saleHead.syyh + "|" + saleBS.saleHead.fphm +"||" + saleBS.saleHead.syjh + "|\0";
	        		 }
	        		 else
	        		 {
	        			 line 		= "0" + "|" + jestr + "|"+ GlobalInfo.syjStatus.syyh+"|||" + GlobalInfo.syjDef.syjh +"|\0";
	        		 }
	        	 break;
	        	 case PaymentBank.XYKCX:
	        		 if (saleBS != null)
	        		 {
	        			 line = "5" + "|" + jestr + "|" + saleBS.saleHead.syyh + "|" + saleBS.saleHead.fphm + "|" + oldseqno + "|" + saleBS.saleHead.syjh + "|\0";
	        		 }
	        		 else
	        		 {
	        			 line = "5" + "|" + jestr + "|"+ GlobalInfo.syjStatus.syyh+"||" + oldseqno + "|" + GlobalInfo.syjDef.syjh + "|\0";
	        		 }
	        		 
	        	 break;
	        	 case PaymentBank.XYKTH:
	        		 if (saleBS != null)
	        		 {
	        			 line = "4" + "|" + jestr + "|" + saleBS.saleHead.syyh + "|" + saleBS.saleHead.fphm + "|" + oldseqno + "|" + saleBS.saleHead.syjh + "|\0";
	        		 }
	        		 else
	        		 {
	        			 line = "4" + "|" + jestr + "|"+ GlobalInfo.syjStatus.syyh+"||" + oldseqno + "|" + GlobalInfo.syjDef.syjh + "|\0";
	        		 }
	        	 break;
	        	 case PaymentBank.XYKQD:
	        		 line = "L|||||" +GlobalInfo.syjDef.syjh + "|\0";
	        	 break;
	        	 case PaymentBank.XYKYE:
	        		 line = "7|||||" + GlobalInfo.syjDef.syjh + "|\0";
	        	 break;
	        	 /*
	        	 case PaymentBank.XYKCD:
	        		 line = "D|||||" + GlobalInfo.syjDef.syjh + "|\0";
	        	 break;	 */
	        	 case PaymentBank.XKQT1:
	        		 
	        		line = "D||"+ GlobalInfo.syjStatus.syyh+"||" + "|" + GlobalInfo.syjDef.syjh + "|\0";
	        	 break;
	        	 
	        	 case PaymentBank.XYKCD:
	        		 //line = "8|||||"+ GlobalInfo.syjDef.syjh + "|\0";
	        		 line = "8||"+ GlobalInfo.syjStatus.syyh+"||" + oldseqno + "|" + GlobalInfo.syjDef.syjh + "|\0";
	        	 break;
	        	 case PaymentBank.XKQT2:
	        		 line = "Z||||| "+ GlobalInfo.syjDef.syjh + "|\0";
	        	 break;	 
	        	 case PaymentBank.XYKJZ:
//	        		 line = "9||||| "+ GlobalInfo.syjDef.syjh + "|\0";
	        		 line = "9||" + GlobalInfo.posLogin.gh + "||| "+ GlobalInfo.syjDef.syjh + "|\0";
//	        		 
	        	 break;	 
	         }
	         
	         PrintWriter pw = null;
	            
	         try
	         {
	            pw = CommonMethod.writeFile("c:\\JavaPOS\\request.txt");
	            if (pw != null)
	            {
	                pw.println(line);
	                pw.flush();
	            }
	         }
	         finally
	         {
	        	if (pw != null)
	        	{
	        		pw.close();
	        	}
	         }
	         
	         return true;
		}
		catch (Exception ex)
		{
			new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);
	        ex.printStackTrace();
	         
	        return false;
		}
	}
	
	public void XYKPrintDoc_Print(String printStr)
	{
		Printer.getDefault().printLine_Normal(printStr);	
	}
	
	public void XYKPrintDoc_End()
	{
		Printer.getDefault().cutPaper_Normal();
	}
	public void XYKPrintDoc_Start()
	{
		Printer.getDefault().startPrint_Normal();
	}
	
	 public void XYKPrintDoc()
	 {
		 ProgressBox pb = null;
		 
		 try
		 {
			 String printName = "";
			 
			 int type = Integer.parseInt(bld.type.trim());
			 
			 if ((type == PaymentBank.XYKXF || type == PaymentBank.XYKCX  || type == PaymentBank.XYKTH ||  type == PaymentBank.XYKCD || type == PaymentBank.XKQT1))
			 {
                  printName = "c:\\JavaPOS\\Print.txt";
			 }
			 else if (type == PaymentBank.XYKJZ)
			 {
				 printName = "c:\\JavaPOS\\Settle.txt";
			 }
			 else
			 {
	             return;
			 }
			 
			 if (!PathFile.fileExist(printName))
             {
                  new MessageBox("找不到签购单打印文件!");

                  return;
             }
			 
			 pb = new ProgressBox();
	         pb.setText("正在打印银联签购单,请等待...");
	         
	         for (int i = 0; i < GlobalInfo.sysPara.bankprint; i++)
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
	            	   	 
	            	 String line = null;
	                   
	            	 while ((line = br.readLine()) != null)
	            	 {
	            		
                        if (line.indexOf("CUTPAPER")!=-1)
                        {
                        	XYKPrintDoc_End();
                        	continue;
                        }
                       
                        XYKPrintDoc_Print(line);
	            	 }

	             }
	             catch (Exception ex)
	             {
	            	 new MessageBox(ex.getMessage());
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
