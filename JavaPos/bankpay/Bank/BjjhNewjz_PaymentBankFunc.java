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
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

//沈阳卓展 集中款台用专柜接口 交行接口
//调用动态库（模块名：ZZBJJH；动态库(dll文件）：pcpos.dll）
public class BjjhNewjz_PaymentBankFunc extends PaymentBankFunc
{
	String path = "";
	public String[] getFuncItem()
    {
        String[] func = new String[7];

        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
        func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
        func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
        func[3] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
        func[4] = "[" + PaymentBank.XYKJZ + "]" + "银联结算";
        func[5] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
        func[6] = "[" + PaymentBank.XYKCD + "]" + "重打签购单";
     
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
                grpLabelStr[0] = null;
                grpLabelStr[1] = "原凭证号";
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易金额";
            break;
        	case PaymentBank.XYKTH: //退货
                grpLabelStr[0] = "原参考号";
                grpLabelStr[1] = null;
                grpLabelStr[2] = "原交易日期";
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
        	case PaymentBank.XYKJZ: //银联结账
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "银联结账";
            break;
        	case PaymentBank.XYKYE: //余额查询    
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "余额查询";
            break;
        	case PaymentBank.XYKCD: //签购单重打
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重打上笔签购单";
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
		 	case PaymentBank.XYKTH: 	// 退货
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
		 	case PaymentBank.XYKJZ: 	//银联结账
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始银联结账";
            break;
		 	case PaymentBank.XYKYE: 	//余额查询    
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始余额查询";
            break;
		 	case PaymentBank.XYKCD: 	//签购单重打
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键签购单重打";
            break;
		}
		
		return true;
    }
	
	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		
		path = "C:\\paxmis";
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && 
				 (type != PaymentBank.XYKQD) && (type != PaymentBank.XYKJZ) && 
				 (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKCD) && (type != PaymentBank.XYKTH))
	            {
	                errmsg = "银联接口不支持该交易";
	                new MessageBox(errmsg);

	                return false;
	            }
			
			 // 先删除上次交易数据文件
            if (PathFile.fileExist(path + "\\request.txt"))
            {
                PathFile.deletePath(path +"\\request.txt");
                
                if (PathFile.fileExist(path +"\\request.txt"))
                {
            		errmsg = "交易请求文件request.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist(path +"\\result.txt"))
            {
                PathFile.deletePath(path +"\\result.txt");
                
                if (PathFile.fileExist(path +"\\result.txt"))
                {
            		errmsg = "交易请求文件result.txt无法删除,请重试";
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
                if (PathFile.fileExist(path +"\\javaposbank.exe"))
                {
                	CommonMethod.waitForExec(path +"\\javaposbank.exe ZZBJJH");
                }
                else
                {
                    new MessageBox("找不到金卡工程模块 javaposbank.exe");
                    XYKSetError("XX","找不到金卡工程模块 javaposbank.exe");
                    return false;
                }
                
                // 读取应答数据
                if (!XYKReadResult())
                {
                    return false;
                }
                
                // 检查交易是否成功
                if (!XYKCheckRetCode()) return false;
                
//              打印签购单
				if (XYKNeedPrintDoc(type))
				{
					XYKPrintDoc(type);
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
	
	public boolean XYKCheckRetCode()
	 {
       if (bld.retcode.trim().equals("00") && bld.memo.trim().equals("0"))
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
	
	
	public boolean checkDate(Text date)
	{
		String d = date.getText();
		if (d.length() > 8)
		{
			new MessageBox("日期格式错误\n日期格式《YYYYMMDD》");
			return false;
		}
		
		return true;
	}
	
	public boolean XYKWriteRequest(int type, double money, String track1,String track2, String track3,String oldseqno, String oldauthno,String olddate, Vector memo)
	{
		try
		{
			 String line = "";
			 //8位收银员号 + 8位款机号 拼成一个传入字段
			 String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh,' ', 8);
	         String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode,' ',8);
			 
			 String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
	         jestr = Convert.increaseCharForward(jestr,'0',12);
	         
	         String date = Convert.increaseChar(olddate, '0', 8); //交易日期
	         
	         String seqno = Convert.increaseCharForward(oldseqno, '0', 12); //参考号
	         
	         String authno = Convert.increaseCharForward(oldauthno, '0', 6); //凭证号  
	         
	         //	根据不同的类型生成文本结构
	         switch (type)
	         {
	         	case PaymentBank.XYKXF:
	         		line = PaymentBank.XYKXF + "," + syyh + syjh + "," + jestr;
	         	break;
	         	case PaymentBank.XYKCX:
	         		line = PaymentBank.XYKCX+ "," + syyh + syjh + "," + authno;
	         	break;
	         	case PaymentBank.XYKTH:
	         		line = PaymentBank.XYKTH+ "," + syyh + syjh + "," + jestr + "," + seqno + "," + date;
	         	break;
	         	case PaymentBank.XYKQD:
	         		line = String.valueOf(PaymentBank.XYKQD);
	         	break;
	         	case PaymentBank.XYKJZ:
	         		line = String.valueOf(PaymentBank.XYKJZ);
	         	break;
	         	case PaymentBank.XYKYE:
	         		line = String.valueOf(PaymentBank.XYKYE);
	         	break;
	         	case PaymentBank.XYKCD:
	         		line = String.valueOf(PaymentBank.XYKCD);
	         	break;	
	         	
	         }
	         
	         PrintWriter pw = null;
	            
	         try
	         {
	            pw = CommonMethod.writeFile(path +"\\request.txt");
	            
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
	
	public boolean XYKReadResult()
	{
       BufferedReader br = null;
       
       try
       {
    	   if (!PathFile.fileExist(path +"\\result.txt") || ((br = CommonMethod.readFileGBK(path +"\\result.txt")) == null))
           {
           		XYKSetError("XX","读取金卡工程应答数据失败!");
           		new MessageBox("读取金卡工程应答数据失败!", null, false);

           		return false;
           }
    	   
    	   String line = "";
    	   String s = "";
    	  while((s = br.readLine()) != null)
    	  {
    		  line += s;
    	  }
    	   
           if (line.length() <= 0)
           {
               return false;
           }
           int type = Integer.parseInt(bld.type.trim());
           
           String result[] = line.split(",");
           
           if (result == null) return false;
           
           bld.memo 	= result[0].trim();
           
           if (Integer.parseInt(bld.memo) != 0)
           {
        	   bld.retmsg		= "调用金卡函数发生异常!";
        	   
        	   return false;
           }
                      
           bld.retcode 		= result[1].trim();
           
           if (!bld.retcode.equals("00"))
           {
        	   bld.retmsg 		= bld.retcode + "," + result[2].trim();
        	   return false;
           }
           
           if(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX ||  type == PaymentBank.XYKTH )
           {
        	   if(result.length >= 4 && !result[3].trim().equals(""))
        	   {
//        		   bld.trace = Convert.toInt((result[3]).trim());   //流水号
        		   if(result[3].length() >= 160 )
        		   {
        			  bld.bankinfo =  Convert.newSubString(result[3], 148, 160).trim(); //发卡银行
        		   }
        		   if(result[3].length() >= 199 )
        		   {
        			  bld.cardno =  Convert.newSubString(result[3], 180, 199).trim(); //银行卡号
        		   }
        	   }
        	   
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
                   
                   if (PathFile.fileExist(path +"\\request.txt"))
		           {
		                PathFile.deletePath(path +"\\request.txt");
		           }
					
				   if (PathFile.fileExist(path +"\\result.txt"))
		           {
		                PathFile.deletePath(path +"\\result.txt");
		           }
               }
               catch (IOException e)
               {
                   e.printStackTrace();
               }
           }
       }
	 }
	
	public boolean XYKNeedPrintDoc(int type)
	{
		if (!checkBankSucceed())
	    {
	        return false;
	    }
		if (  type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || 
			  type == PaymentBank.XYKJZ || type == PaymentBank.XYKCD || type == PaymentBank.XYKTH)
		{
			return true;
		}
		else
			return false;
	}
	
	public void XYKPrintDoc(int type)
	{
		ProgressBox pb = null;
		String name = null;
		if(type == PaymentBank.XYKJZ)
		{
			name =path + "\\rol.shop";
		}
		else if(type == PaymentBank.XYKCD)
		{
			name =path + "\\reprint.shop";
		}
		else
		{
			name =path + "\\record.shop";
		}

		try
		{
			if (!PathFile.fileExist(name))
			{
				if(type == PaymentBank.XYKJZ)
				{
					new MessageBox("找不到结算单打印文件！！！");
				}else
				{
					new MessageBox("找不到签购单打印文件！！！");
				}
				
				return ;
			}
			pb = new ProgressBox();
			pb.setText("正在打印,请等待..." + "\t OY : " + GlobalInfo.sysPara.issetprinter);
			
			for (int i = 0; i < GlobalInfo.sysPara.bankprint; i ++)
			{
				BufferedReader br = null;
				XYKPrintDoc_Start();
				try
				{
					br = CommonMethod.readFileGB2312(name);
					if (br == null)
					{							
						if(type == PaymentBank.XYKJZ)
						{
							new MessageBox("打开结算单文件失败");
						}else
						{
							new MessageBox("打开签购单文件失败");
						}
						
						return ;
					}
					
					String line = null;
					while ((line = br.readLine()) != null)
					{
						if (line.length() <= 0)
							continue;
						//银行签购单模板添加 "CUTPAPER" 标记
						//当程序里面读取到这个字符是，打印机切纸
						if (line.indexOf("|") >= 0)
						{
							XYKPrintDoc_End();
//							new MessageBox("请撕下客户签购单！！！");
							
							continue;
						}
						
						XYKPrintDoc_Print(line);
					}					
				}
				catch(Exception e)
				{
					new MessageBox(e.getMessage());
				}
				finally
				{
					if (br != null)
					try
					{
						br.close();
					}
					catch(IOException ie)
					{
						ie.printStackTrace();
					}					
				}
				XYKPrintDoc_End();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			if(type == PaymentBank.XYKJZ)
			{
				new MessageBox("打印结算单异常!!!\n" + e.getMessage());
			}else
			{
				new MessageBox("打印签购单异常!!!\n" + e.getMessage());
			}			
		}
		finally
		{
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
			if (PathFile.fileExist(name))
			{
				PathFile.deletePath(name);
			}
		}
	}
	
	
    public boolean initComboChange(int type)
    {
    	return false;
    }
    
    
    
	public void XYKPrintDoc_Start()
	{
			Printer.getDefault().startPrint_Normal();
	}
	
	public void XYKPrintDoc_Print(String printStr)
	{
			Printer.getDefault().printLine_Normal(printStr);
	}
	
	public void XYKPrintDoc_End()
	{
			Printer.getDefault().cutPaper_Normal();
	}
}
