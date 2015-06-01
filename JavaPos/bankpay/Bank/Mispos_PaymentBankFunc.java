package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

public class Mispos_PaymentBankFunc extends PaymentBankFunc
{
	public String[] getFuncItem()
    {
		String[] func = new String[9];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[3] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[4] = "[" + PaymentBank.XYKJZ + "]" + "交易结账";
		func[5] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
		func[6] = "[" + PaymentBank.XYKCD + "]" + "签购单重打";
		func[7] = "[" + PaymentBank.XKQT1 + "]" + "打印交易流水";
		func[8] = "[" + PaymentBank.XKQT2 + "]" + "交易签到";
     
        return func;
    }
	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		//0-4对应FORM中的5个输入框
		//null表示该不用输入
		switch (type)
		{
			case PaymentBank.XYKXF://消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKCX://消费撤销
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKTH://隔日退货   
				grpLabelStr[0] = null;
				grpLabelStr[1] = "原参考号";
				grpLabelStr[2] = "原交易日";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKQD://交易签到 53
				grpLabelStr[0] = null;//"calc|,02|测试输入,01";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易签到";
				break;
			case PaymentBank.XKQT2://交易签到 51
				grpLabelStr[0] = null;//依次执行51,53交易。53下载公钥
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易签到";
				break;
			case PaymentBank.XYKJZ://交易结账
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易结账";
				break;
			case PaymentBank.XYKYE://余额查询    
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "余额查询";
				break;
			case PaymentBank.XYKCD://签购单重打
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打签单";
				break;
			case PaymentBank.XKQT1: //打印交易流水 
				grpLabelStr[0] = "起始交易号"; //传入流水号的前3字节和后面3字节分别代表需要打印的“起始交易号”和“打印笔数”
				grpLabelStr[1] = "打印笔数";
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "打印交易流水";
				break;
		}

		return true;
	}

	public boolean getFuncText(int type, String[] grpTextStr)
	{
		//0-4对应FORM中的5个输入框
		//null表示必须用户输入,不为null表示缺省显示无需改变
		switch (type)
		{
			case PaymentBank.XYKXF://消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKCX://消费撤销
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKTH://隔日退货   
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKQD://交易签到53
				grpTextStr[0] = null;//"allowempty";
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易签到";
				break;
			case PaymentBank.XKQT2://交易签到51 更新后的接口文档先执行51签到，然后53下载公钥
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易签到";
				break;
			case PaymentBank.XYKJZ://交易结账
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易结账";
				break;
			case PaymentBank.XYKYE://余额查询    
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始余额查询";
				break;
			case PaymentBank.XYKCD://签购单重打
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始签购单重打";
				break;
			case PaymentBank.XKQT1://打印交易流水
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键打印交易流水";
				break;
		}

		return true;
	}
	
	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if (type == PaymentBank.XYKJZ ) //在操作员在收银界面选择“结算”交易前，首先询问是否需要打印交易流水
			{
			    if (! (new MessageBox("是否打印交易流水？\n\n任意键-是 / 2-否", null, false).verify() == GlobalVar.Key2))
				{
					if (!XYKExecute(PaymentBank.XKQT1, money, track1, track2, track3, "001", "999", olddate, memo))
					{
						return false;
					}
				}
			}
			
			 // 先删除上次交易数据文件
            if (PathFile.fileExist("c:\\gmc\\Request.txt"))
            {
                PathFile.deletePath("c:\\gmc\\Request.txt");
                
                if (PathFile.fileExist("c:\\gmc\\Request.txt"))
                {
            		errmsg = "交易请求文件Request.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist("c:\\gmc\\Response.txt"))
            {
                PathFile.deletePath("c:\\gmc\\Response.txt");
                
                if (PathFile.fileExist("c:\\gmc\\Response.txt"))
                {
            		errmsg = "交易请求文件Response.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            // 写入请求数据
            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
            {
                return false;
            }
            
            if (bld.retbz != 'Y')
            {
            	
                // 调用接口模块
                if (PathFile.fileExist("c:\\gmc\\mispos.exe"))
                {
                	CommonMethod.waitForExec("c:\\gmc\\mispos.exe");
                }
                else
                {
                    new MessageBox("找不到金卡工程模块 mispos.exe");
                    XYKSetError("XX","找不到金卡工程模块 mispos.exe");
                    return false;
                }
                
                // 读取应答数据
                if (!XYKReadResult(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
                {
                    return false;
                }
                
                // 检查交易是否成功
                if (!XYKCheckRetCode()) return false;
                
				// 打印签购单
				if (XYKNeedPrintDoc())
				{
					XYKPrintDoc();
				}
				
				if (type == PaymentBank.XKQT2 ) //指令放在签到流程中，依次执行51,53交易。
				{
					if (!XYKExecute(PaymentBank.XYKQD, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
					{
						new MessageBox("下载公钥失败！！！");
						return false;
					}
				}

            }
            
            
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	public boolean checkcSeqno(Text text)
	{
		String s = text.getText();
		int n = Integer.parseInt(s);
		if (n < 0 || n >999)
		{
			new MessageBox("请输入0-999之间的数字！！！");
			
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public boolean XYKCheckRetCode()
	 {
       if (bld.retcode.trim().equals("00"))
       {
           bld.retbz = 'Y';

           return true;
       }
       else
       {
           bld.retbz = 'N';

           return false;
       }
	 }
	
	public boolean checkBankSucceed()
	 {
       if (bld.retbz == 'N')
       {
           errmsg = bld.retmsg;

           return false;
       }
       else
       {
           errmsg = "交易成功";

           return true;
       }
	 }
	
	public boolean XYKWriteRequest(int type, double money, String track1,String track2, String track3,String oldseqno, String oldauthno,String olddate, Vector memo)
	{
		try
		{
			 String syjh = Convert.increaseCharForward(GlobalInfo.syjDef.syjh,'0',15);
			 String syyh =  Convert.increaseCharForward(GlobalInfo.posLogin.gh,'0', 15);
			 String line = "";
	 		 
	         String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
	         
	         for (int i = jestr.length(); i < 12; i++)
             {
                 jestr = "0" + jestr;
             }
	         	 
	         String typecode = "";
	         //	根据不同的类型生成文本结构
	         switch (type)
	         {
	         	case PaymentBank.XYKXF:
	         		typecode = "01";
	         	break;
	         	case PaymentBank.XYKCX:
	         		typecode = "02";
	         	break;
	         	case PaymentBank.XYKTH:
	         		typecode = "03";
	         	break;
	         	case PaymentBank.XYKYE:
	         		typecode = "04";
	         	break;
	         	case PaymentBank.XYKCD:
	         		typecode = "61";
	         	break;
	         	case PaymentBank.XYKJZ:
	         		typecode = "52";
	         	break;
	         	case PaymentBank.XYKQD:
	         		typecode = "53";
	         	break;	
	         	case PaymentBank.XKQT2:
	         		typecode = "51";
	         	break;
	         	case PaymentBank.XKQT1:
	         		typecode = "64";
	         	break;
	         }
	         
//	         String info = Convert.increaseCharForward(String.valueOf(bld.fphm), 13)+" "+Convert.increaseCharForward(GlobalInfo.sysPara.mktcode,6);
	         
	         String memo1 = Convert.increaseChar("", 6);
	         
	         if (oldseqno == null) oldseqno = "";
	         String trace1 = Convert.increaseCharForward(oldseqno,'0',6);
	        
	         
	         if (oldauthno == null) oldauthno = "";
	         
	         String oldauth1 = Convert.increaseCharForward(oldauthno, '0',12);

	         PrintWriter pw = null;
	         
	         if (olddate == null) olddate = "";
	         olddate = Convert.increaseChar(olddate, 4);
	         String crc = XYKGetCRC();
	         bld.crc = crc;
	         line = "";
	         
	         //	根据不同的类型生成文本结构
	         switch (type)
	         {
	         	case PaymentBank.XYKXF:
	         		line = syjh+""+syyh+""+typecode+""+jestr+""+olddate+oldauth1+trace1+memo1+crc;
	         	break;
	         	case PaymentBank.XYKCX:
	         		line = syjh+""+syyh+""+typecode+""+jestr+""+olddate+oldauth1+trace1+memo1+crc;
	         	break;
	         	case PaymentBank.XYKTH:
	         		line = syjh+""+syyh+""+typecode+""+jestr+""+olddate+oldauth1+trace1+memo1+crc;
	         	break;
	         	case PaymentBank.XYKYE:
	         		line = syjh+""+syyh+""+typecode+""+jestr+""+olddate+oldauth1+trace1+memo1+crc;
	         	break;
	         	case PaymentBank.XYKCD:
	         		line = syjh+""+syyh+""+typecode+""+jestr+""+olddate+oldauth1+trace1+memo1+crc;
	         	break;
	         	case PaymentBank.XYKJZ:
	         		line = syjh+""+syyh+""+typecode+""+jestr+""+olddate+oldauth1+trace1+memo1+crc;
	         	break;
	         	case PaymentBank.XYKQD:
	         		line = syjh+""+syyh+""+typecode+""+jestr+""+olddate+oldauth1+trace1+memo1+crc;
	         	break;
	         	case PaymentBank.XKQT1:
	         		String s = oldseqno;
	         		int n = Integer.parseInt(s);
	        		if (n < 0 || n >999)
	        		{
	        			new MessageBox("请输入0-999之间的起始交易号！！！");
	        			
	        			return false;
	        		}
	        		 s = oldauthno;
	         		 n = Integer.parseInt(s);
	        		if (n < 0 || n >999)
	        		{
	        			new MessageBox("请输入0-999之间的打印笔数！！！");
	        			
	        			return false;
	        		}
	         		trace1 = Convert.increaseCharForward(oldseqno,'0',3) + Convert.increaseCharForward(oldauthno,'0',3);
	         		line = syjh+""+syyh+""+typecode+""+jestr+""+olddate+oldauth1+trace1+memo1+crc;
	         	break;
	         	case PaymentBank.XKQT2:
	         		line = syjh+""+syyh+""+typecode+""+jestr+""+olddate+oldauth1+trace1+memo1+crc;
	         	break;
	         }
	         try
	         {
	            pw = CommonMethod.writeFile("c:\\gmc\\Request.txt");
	            
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
	
	public boolean checkDate(Text date)
	{
		String date1 = date.getText();
		if (date1.length() > 4)
		{
			new MessageBox("请输入日期\n日期格式《MMDD》");
			return false;
		}
		return true;
	}
	
	public boolean XYKReadResult(int type, double money, String track1,String track2, String track3,String oldseqno, String oldauthno,String olddate, Vector memo)
	{
       BufferedReader br = null;
       
       try
       {
    	   if (!PathFile.fileExist("c:\\gmc\\Response.txt") || ((br = CommonMethod.readFileGBK("c:\\gmc\\Response.txt")) == null))
           {
           		XYKSetError("XX","读取金卡工程应答数据失败!");
           		new MessageBox("读取金卡工程应答数据失败!", null, false);

           		return false;
           }
    	   
    	   String line = br.readLine();

    	   br.close();
           if (line.length() <= 0)
           {
               return false;
           }
           
	         //	根据不同的类型生成文本结构
	         switch (type)
	         {
	         	case PaymentBank.XYKXF:
	         		bld.retcode =Convert.newSubString(line, 0,2);
	         		
	         		bld.bankinfo =  Convert.newSubString(line, 56,64);
	         		bld.cardno = Convert.newSubString(line, 14,33);
	         		//if (bld.cardno.trim().length() > 9) bld.cardno = bld.cardno.substring(0, 5) +"**************" +bld.cardno.substring(bld.cardno.length() - 4);
	         		bld.trace = Convert.toLong(Convert.newSubString(line, 96,102));
	         		bld.je = ManipulatePrecision.doubleConvert(Convert.toDouble(Convert.newSubString(line, 2,14))/100);
	         		bld.retmsg = XYKReadRetMsg(bld.retcode);
	         		if (!Convert.newSubString(line, 64,67).equals(bld.crc))
	         		{
	         			new MessageBox("[交易校验数据]数据不正确，非正常返回");
	         			return false;
	         		}
	         	break;
	         	case PaymentBank.XYKCX:
	         		bld.retcode =Convert.newSubString(line, 0,2);
	         		
	         		bld.bankinfo =  Convert.newSubString(line, 56,64);
	         		bld.cardno = Convert.newSubString(line, 14,33);
	         		bld.trace = Convert.toLong(Convert.newSubString(line, 96,102));
	         		bld.je = ManipulatePrecision.doubleConvert(Convert.toDouble(Convert.newSubString(line, 2,14))/100);
	         		bld.retmsg = XYKReadRetMsg(bld.retcode);
	         		if (!Convert.newSubString(line, 64,67).equals(bld.crc))
	         		{
	         			new MessageBox("[交易校验数据]数据不正确，非正常返回");
	         			return false;
	         		}
	         	break;
	         	case PaymentBank.XYKTH:
	         		bld.retcode =Convert.newSubString(line, 0,2);
	         		
	         		bld.bankinfo =  Convert.newSubString(line, 56,64);
	         		bld.cardno = Convert.newSubString(line, 14,33);
	         		bld.trace = Convert.toLong(Convert.newSubString(line, 96,102));
	         		bld.je = ManipulatePrecision.doubleConvert(Convert.toDouble(Convert.newSubString(line, 2,14))/100);
	         		bld.retmsg = XYKReadRetMsg(bld.retcode);
	         		if (!Convert.newSubString(line, 64,67).equals(bld.crc))
	         		{
	         			new MessageBox("[交易校验数据]数据不正确，非正常返回");
	         			return false;
	         		}
	         	break;
	         	case PaymentBank.XYKYE:
	         		bld.retcode =Convert.newSubString(line, 0,2);
	         		bld.retmsg = XYKReadRetMsg(bld.retcode);
	         	break;
	         	case PaymentBank.XYKCD:
	         		bld.retcode =Convert.newSubString(line, 0,2);
	         		bld.retmsg = XYKReadRetMsg(bld.retcode);
	         	break;
	         	case PaymentBank.XYKJZ:
	         		bld.retcode =Convert.newSubString(line, 0,2);
	         		bld.retmsg = XYKReadRetMsg(bld.retcode);
	         	break;
	         	case PaymentBank.XYKQD:
	         		bld.retcode =Convert.newSubString(line, 0,2);
	         		bld.retmsg = XYKReadRetMsg(bld.retcode);
	         	break;	
	         	case PaymentBank.XKQT1:
	         		bld.retcode =Convert.newSubString(line, 0,2);
	         		bld.retmsg = XYKReadRetMsg(bld.retcode);
	         	break;
	         }
           
    	   return true;
       }
       catch (Exception ex)
       {
    	   ex.printStackTrace();
    	   
    	   XYKSetError("XX","读取应答XX:"+ex.getMessage());
           new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);
           
    	   
    	   return false;
       }
       finally
       {
           if (br != null)
           {
               try
               {
                   br.close();
                   if (PathFile.fileExist("c:\\gmc\\Request.txt"))
		           {
		                PathFile.deletePath("c:\\gmc\\Request.txt");
		           }
					
				   if (PathFile.fileExist("c:\\gmc\\Response.txt"))
		           {
		                PathFile.deletePath("c:\\gmc\\Response.txt");
		           }
               }
               catch (IOException e)
               {
                   e.printStackTrace();
               }
           }
       }
	 }
	
	public boolean XYKNeedPrintDoc()
	{
       //if (!checkBankSucceed())
       //{
       //    return false;
       //}
       
       return true;
	}
	
	public void XYKPrintDoc()
	 {
		 ProgressBox pb = null;

		 try
		 {
			 String printName = "";
			 
			 int type = Integer.parseInt(bld.type.trim());
			 
			 if (type == PaymentBank.XYKXF || type == PaymentBank.XYKCD || type == PaymentBank.XYKTH || type == PaymentBank.XYKCX || type == PaymentBank.XYKJZ || type == PaymentBank.XKQT1)
			 {
				 if (!PathFile.fileExist("c:\\gmc\\P_TackSingle.txt"))
	             {
	                  new MessageBox("找不到签购单打印文件!");

	                  return;
	             }
	             else
	             {
	                  printName = "c:\\gmc\\P_TackSingle.txt";
	             }
			 }
			 else
			 {
	             return;
			 }
			 
			 pb = new ProgressBox();
	         pb.setText("正在打印银联签购单,请等待...");
	         
	         for (int i = 0; i < 1; i++)
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
	            		
                       if (line.trim().equals("CUTPAPPER"))
                       {
                           break;
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
