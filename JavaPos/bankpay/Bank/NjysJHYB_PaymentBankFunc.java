package bankpay.Bank;


import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

public class NjysJHYB_PaymentBankFunc extends PaymentBankFunc
{
		protected String bankpath = ConfigClass.BankPath;
		public String[] getFuncItem()
	    {
	    	String[] func = new String[7];
	    	
	    	func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
	    	func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
	    	func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
	    	func[3] = "[" + PaymentBank.XYKJZ + "]" + "交易结账";
	    	func[4] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
	    	func[5] = "[" + PaymentBank.XYKCD + "]" + "其他交易";
	    	func[6] = "[" + PaymentBank.XYKQD + "]" + "交易签到";

	        return func;
	    }
	    
	    public boolean getFuncLabel(int type, String[] grpLabelStr)
	    {
	        //0-4对应FORM中的5个输入框
	        //null表示该不用输入
	        switch (type)
	        {
	            case PaymentBank.XYKXF: //消费
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "交易金额";

	                break;

	            case PaymentBank.XYKCX: //消费撤销
	                grpLabelStr[0] = "原交易号";
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "交易金额";

	                break;

	            case PaymentBank.XYKYE: //余额查询    
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "余额查询";

	                break;

	            case PaymentBank.XYKJZ: //结账
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "结账";

	                break;

	            case PaymentBank.XYKTH: //隔日退货   
	                grpLabelStr[0] = "原交易号";
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "金额";

	                break;

	            case PaymentBank.XYKCD: //签购单重打
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "重打上笔签购单";
	                break;
	                
	            case PaymentBank.XYKQD: //签到
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "交易签到";
	                break;
	                
	            case PaymentBank.XKQT1: //‘S’表示建行龙卡积分消费
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "交易金额";
	                break;
	                
	            case PaymentBank.XKQT2: //‘V’表示建行龙卡积分消费撤销
	                grpLabelStr[0] = "原交易号";
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "交易金额";

	                break;
	        }

	        return true;
	    }

	    public boolean getFuncText(int type, String[] grpTextStr)
	    {
	        //0-4对应FORM中的5个输入框
	        //null表示该需要用户输入,不为null用户不输入
	        switch (type)
	        {
	            case PaymentBank.XYKXF: //消费
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = null;

	                break;

	            case PaymentBank.XYKCX: //消费撤销
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = null;

	                break;

	            case PaymentBank.XYKTH: //退货
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = null;

	                break;

	            case PaymentBank.XYKYE: //余额查询    
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = "按回车键开始余额查询";

	                break;

	            case PaymentBank.XYKQD: //交易签到
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = "按回车键开始交易签到";

	                break;

	            case PaymentBank.XYKJZ: //内卡结账
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = "按回车键开始结账";

	                break;

	            case PaymentBank.XYKCD: //签购单重打
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = "开始签购单重打";

	                break;
	        }

	        return true;
	    }

	    public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	    {
	        try
	        {
	            if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && (type != PaymentBank.XYKTH) &&
	                 (type != PaymentBank.XYKJZ) && (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKCD)&& (type != PaymentBank.XYKQD)&& (type != PaymentBank.XKQT1)&& (type != PaymentBank.XKQT2)) 
	            {
	                errmsg = "银联接口不支持该交易";
	                new MessageBox(errmsg);

	                return false;
	            }
	          
	            // 先删除上次交易数据文件
	            if (PathFile.fileExist("c:\\gmc\\toprint.txt"))
	            {
	                PathFile.deletePath("c:\\gmc\\toprint.txt");		
	            }
	            
				if (PathFile.fileExist("c:\\gmc\\request.txt"))
				{
					PathFile.deletePath("c:\\gmc\\request.txt");
				}

				if (PathFile.fileExist("c:\\gmc\\result.txt"))
				{
					PathFile.deletePath("c:\\gmc\\result.txt");
				}
				
				if (PathFile.fileExist("c:\\gmc\\answer.txt"))
				{
					PathFile.deletePath("c:\\gmc\\answer.txt");
				}

	            // 写入请求数据
	            String line =XYKgetRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);
	            
				PrintWriter pw = CommonMethod.writeFile("c:\\gmc\\request.txt");

				if (pw != null)
				{
					pw.println(line);
					pw.flush();
					pw.close();
				}
				
				// 调用接口模块
				if (PathFile.fileExist("c:\\gmc\\javaposbank.exe"))
				{
					CommonMethod.waitForExec("c:\\gmc\\javaposbank.exe NJYSMZDABC");
				}
				else
				{
					new MessageBox("找不到金卡工程模块 javaposbank.exe");
					XYKSetError("XX", "找不到金卡工程模块 javaposbank.exe");
					return false;
				}
	         
                // 读取应答数据
                if (!XYKReadResult1(type))
                {
                    return false;
                }

                // 检查交易是否成功
                XYKCheckRetCode();

                //无论是否成功，都检查打印
                
                if (XYKNeedPrintDoc())
                {
                	XYKPrintDoc();
                }
	            
	            return true;
	        }
	        catch (Exception ex)
	        {
	            ex.printStackTrace();
	            XYKSetError("XX","金卡异常XX:"+ex.getMessage());
	            new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);

	            return false;
	        }
	        
	    }

	    public boolean XYKNeedPrintDoc()
	    {
	        if (!checkBankSucceed())
	        {
	            return false;
	        }

	        int type = Integer.parseInt(bld.type.trim());

	        // 消费，消费撤销，重打签购单
	        if ((type == PaymentBank.XYKXF) || (type == PaymentBank.XYKCX) ||
	                (type == PaymentBank.XYKTH) || (type == PaymentBank.XYKCD) ||
	                (type == PaymentBank.XYKJZ) || (type == PaymentBank.XKQT1)|| (type == PaymentBank.XKQT2))
	        {
	            return true;
	        }
	        else
	        {
	            return false;
	        }
	    }

	    public boolean XYKCheckRetCode()
	    {
	        if (bld.retcode.trim().equals("00"))
	        {
	            bld.retbz  = 'Y';
	            bld.retmsg = "金卡工程调用成功";

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
	            //errmsg = bld.retmsg;

	            return false;
	        }
	        else
	        {
	            errmsg = "交易成功";
	        	
	    		//加入解百特殊金卡工程断电保护
	        	String path = "c:\\gmc\\answer.txt";
	    		if (PathFile.fileExist(path))
	    		{
	    			PathFile.deletePath(path);
	    			if (PathFile.fileExist(path))
	    			{
	    				new MessageBox(path+"已经被其他程序锁住，请联系电脑部解决");
	    			}
	    		}
	    		
	            return true;
	        }
	    }

	    public String XYKgetRequest(int type, double money, String track1,
	                                      String track2, String track3,
	                                      String oldseqno, String oldauthno,
	                                      String olddate, Vector memo)
	    {
	        try
	        {
	            String line = "";

	            String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode,' ', 5);
	            String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh,' ', 6);
	            char type1 = ' ';
	            
	            String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
	            jestr = Convert.increaseCharForward(jestr,'0',12);           
	            String mm = "";
	            //根据不同的类型生成文本结构
	            switch (type)
	            {
	                case PaymentBank.XYKXF:
	                    type1 = 'C';
	                    mm = "00000000";
	                break;
	                case PaymentBank.XYKCX:
	                    type1 = 'D';
	                    mm = oldseqno;
	                break;
	                case PaymentBank.XYKTH:
	                	type1 = 'D';
	                	mm = oldseqno;
	                break;
	                case PaymentBank.XKQT1:
	                	type1 = 'S';
	                	 mm = "00000000";
	                break;
	                case PaymentBank.XKQT2:
	                	type1 = 'V';
	                	mm = oldseqno;
	                break;
	                default:
	                    type1 = '0';
	                mm = "00000000";
	                break;
	            }
	            
	            line = syjh + syyh + type1 + jestr + mm;
	            return line;
	        }
	        catch (Exception ex)
	        {
	            new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);
	            ex.printStackTrace();

	            return null;
	        }
	    }
	  
	    public boolean XYKReadResult1(int type)
	    {
	        try
	        {   String line = null;
				BufferedReader br = null;
				if (!PathFile.fileExist("c:\\gmc\\result.txt") || ((br = CommonMethod.readFile("c:\\gmc\\result.txt")) == null))
				{
					XYKSetError("XX", "读取金卡工程应答数据失败!");
					new MessageBox("读取金卡工程应答数据失败!", null, false);

					return false;
				}
				line = br.readLine();
				br.close();
				
				if(type == PaymentBank.XYKCD){
					 bld.retcode = "00";
					 return true;
				}
				if(type == PaymentBank.XYKJZ ||type == PaymentBank.XYKQD){
					if(line.substring(0,2).equals("00")){
						 bld.retcode = "00";
						 return true;
					}else{
						
						return false;
					}
				}
				
	            bld.retcode = line.substring(0,2);
	            
	            if ( type != PaymentBank.XYKXF && type != PaymentBank.XYKCX && type != PaymentBank.XYKTH && type != PaymentBank.XKQT1&& type != PaymentBank.XKQT2) 
	            {
	            	 if (!bld.retcode.equals("00"))
	 	            {
	 	              	return false;
	 	            }
	 	            else
	 	            {
	 	            	bld.retmsg = "银联交易成功";
	 	            	return true;
	 	            }
	            }
	            
	            if (!bld.retcode.equals("00"))
	            {
	            	bld.retmsg  = bld.retcode;
	            	return false;
	            }
	            else
	            {
	            	bld.retmsg = "银联交易成功";

	            }
	            
	            bld.cardno  = Convert.newSubString(line, 2, 21);
	            bld.crc     = Convert.newSubString(line, 21, 22);
	            String je   = Convert.newSubString(line, 22, 34);
	            double j = Double.parseDouble(je);
	            j = ManipulatePrecision.mul(j, 0.01);
	            bld.je = j;
	            bld.trace = Long.parseLong(Convert.newSubString(line, 34, 40));
	            bld.authno = Convert.newSubString(line, 40, 52);
	            bld.bankinfo = Convert.newSubString(line, 56, 66);
	            return true;
	        }
	        catch (Exception ex)
	        {
	        	XYKSetError("XX","读取应答XX:"+ex.getMessage());
	            new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);
	            ex.printStackTrace();

	            return false;
	        }
	    }
	    
	    public void XYKPrintDoc()
	    {
	        ProgressBox pb = null;

	        try
	        {
	        	String printName = "c:\\gmc\\toprint.txt";
	        	if (!PathFile.fileExist(printName))
	        	{
	        		return ;
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
	                    	
	                        if (line.trim().equals("/CUT"))
	                        {
	                           XYKPrintDoc_End();
	                           continue;
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
	                
	            }
	            
	            PathFile.deletePath(printName);
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

	    public void XYKPrintDoc_Print(String printStr)
	    {
	        if (onceprint)
	        {
	            Printer.getDefault().printLine_Journal(printStr);
	        }
	        else
	        {
	            printdoc.println(printStr);
	        }
	    }

	    public void XYKPrintDoc_End()
	    {
	        if (onceprint)
	        {
	            Printer.getDefault().cutPaper_Journal();
	        }
	        else
	        {
	            printdoc.flush();
	            printdoc.close();
	            printdoc = null;
	        }
	    }
}
