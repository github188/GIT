package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import org.eclipse.swt.widgets.Text;


import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;

import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

//新疆卡乐士
//调用动态库（模块名：HZXL；动态库(dll文件）：sldll.dll；函数：int CardTrans (char * InStr,char *OutStr)；）
public class WfWdbh3_PaymentBankFunc extends PaymentBankFunc{
    String path = null;
	public String[] getFuncItem()
    {
        String[] func = new String[7];
        
        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
        func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
        func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
        func[3] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
        func[4] = "[" + PaymentBank.XYKCD + "]" + "重打签购单";
        func[5] = "[" + PaymentBank.XYKJZ + "]" + "交易结算";
        func[6] = "[" + PaymentBank.XKQT1 + "]" + "交易查询";
        
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
        	    grpLabelStr[0] = "POS流水号";
        	    grpLabelStr[1] = null;
        	    grpLabelStr[2] = null;
        	    grpLabelStr[3] = null;
        	    grpLabelStr[4] = "交易金额";
            break;
        	case PaymentBank.XYKTH://隔日退货   
        	    grpLabelStr[0] = "参考号";
        	    grpLabelStr[1] = null;
        	    grpLabelStr[2] = "原交易日期";
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
        	case PaymentBank.XYKCD: //重打签购单    
                grpLabelStr[0] = "POS流水号";
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重打签购单";
            break;
        	case PaymentBank.XYKJZ: //结算  
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易结算";
            break;
        	case PaymentBank.XKQT1: //交易查询 
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易查询 ";
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
		 	case PaymentBank.XYKYE: 	//余额查询    
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始余额查询";
            break;
		 	case PaymentBank.XYKCD: 	//重打签购单    
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始重打印";
            break;
		 	case PaymentBank.XYKJZ: 	//交易结算
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始结算";
            break;
		 	case PaymentBank.XKQT1: 	//交易查询
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始查询";
            break;
		}
		
		return true;
    }
	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if (!(type == PaymentBank.XYKXF || 
				  type == PaymentBank.XYKCX || 
				  type == PaymentBank.XYKTH || 
				  type == PaymentBank.XYKJZ || 
				  type == PaymentBank.XYKYE ||
				  type == PaymentBank.XYKCD ||
				  type == PaymentBank.XKQT1))
				{			
					  new MessageBox("银联接口不支持此交易类型！！！");
					  
					  return false;
			    }
			
//			获得金卡文件路径
			path = getBankPath(paycode);
			
			if (PathFile.fileExist(path + "\\request.txt"))
			{
				PathFile.deletePath(path + "\\request.txt");
				if (PathFile.fileExist(path + "\\request.txt"))
				{
					errmsg = "交易“request.txt”文件删除失败，请重试！！！";
					XYKSetError("XX",errmsg);
					new MessageBox(errmsg);
					
					return false;
				}				
			}
			if (PathFile.fileExist(path + "\\result.txt"))
			{
				PathFile.deletePath(path + "\\result.txt");
				if (PathFile.fileExist(path + "\\result.txt"))
				{
					errmsg = "交易“result.txt”文件删除失败，请重试！！！";
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
            //  调用接口模块
            if (PathFile.fileExist(path+"\\javaposbank.exe"))
            {
            	CommonMethod.waitForExec(path+"\\javaposbank.exe HZXL","javaposbank");
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
            XYKCheckRetCode();

            //打印签购单
			if (XYKNeedPrintDoc(type))
			{
				XYKPrintDoc(type);
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
	
	

	public boolean XYKWriteRequest(int type, double money, String track1,String track2, String track3,String oldseqno, String oldauthno,String olddate, Vector memo)
	{
		 String line = "";
		 String type1 = "";
		 PrintWriter pw = null;
		 
		 try
		 {
			 switch (type)
			 {
			 	case PaymentBank.XYKXF: 	// 消费
                    type1 = "01";
                break;
			 	case PaymentBank.XYKCX: 	// 消费撤销
			 		type1 = "02";
			 	break;	
                case PaymentBank.XYKTH:		// 隔日退货
                	type1 = "09";
                break;
                case  PaymentBank.XYKYE: 	// 余额查询
                	type1 = "03";
                break;	
                case  PaymentBank.XYKCD: 	// 重打签购单
                	type1 = "12";
                break;
                case  PaymentBank.XYKJZ: 	// 交易结算
                    type1 = "14";			
                break;
                case  PaymentBank.XKQT1: 	// 交易查询
                    type1 = "15";			
                break;
			 }
	         
			 String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
	         jestr = Convert.increaseCharForward(jestr,'0',12);
	         
	         String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh,' ', 10);
	         
	         String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode,' ', 10);
	         
	         String date = Convert.increaseChar(olddate, ' ', 8); //交易日期
	         
	         String sqno = Convert.increaseChar("", ' ', 6); //授权号
	        
	         String seqno = "";
	         String authno = "";
	         if(type == PaymentBank.XYKTH )
	         {
	        	 seqno = Convert.increaseChar(oldseqno, ' ', 15); //参考号
	        	 authno = Convert.increaseChar("", ' ', 6); //POS流水
	         }
	         else if(type == PaymentBank.XYKCX || type == PaymentBank.XYKCD)
	         {
	        	 seqno = Convert.increaseChar("", ' ', 15); //参考号
	        	 authno = Convert.increaseChar(oldseqno, ' ', 6); //POS流水
	         }
	         else
	         {
	        	 seqno = Convert.increaseChar("", ' ', 15); //参考号
		         authno = Convert.increaseChar("", ' ', 6); //POS流水   
	         }
	         	         
	         String cardtype = "H";   //卡片类型
	         
	         String syno = new ManipulateDateTime().getDateByEmpty();
	         syno = Convert.increaseChar(syno, ' ', 76);  //交易索引号
	        
	         //交易类型+金额+POS流水+收银机号+收银员号+参考号+授权号+原交易日期+卡片类型+交易索引号
	         line = type1 + jestr + authno + syjh + syyh + seqno + sqno + date + cardtype + syno;
	       
	         
	         
	         try
	         {
	            pw = CommonMethod.writeFile(path+"\\request.txt");
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

	
	
	
	
	//读取result文件
	public boolean XYKReadResult()
	{
		BufferedReader br = null;
		
		try
        {
			if (!PathFile.fileExist(path+"\\result.txt") || ((br = CommonMethod.readFileGBK(path+"\\result.txt")) == null))
            {
            	XYKSetError("XX","读取应答失败,交易失败!");
                new MessageBox("读取金卡工程应答数据失败!", null, false);
                
                return false;
            }
			
			String line = br.readLine();

            if (line == null || line.length() <= 0)
            {
                return false;
            }
            
            String result[] = line.split(",");
            if (result == null) return false;
            
            bld.memo		= result[0];
            
            int type = Integer.parseInt(bld.type.trim());
             
            if (result.length >= 2)
            {
            	bld.retcode  = Convert.newSubString(result[1], 0, 6);  //返回码6
            	if(!bld.retcode.equals("000000"))
            	{
//            		bld.retmsg = bld.retcode + "," + Convert.newSubString(result[1], 6, 46).trim();   //错误说明
            		return false;
            	}
            	if(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX ||  type == PaymentBank.XYKTH )
            	{
                	bld.cardno = Convert.newSubString(result[1], 64, 83);   //卡号19
                	bld.trace = Integer.parseInt(Convert.newSubString(result[1], 58, 64));   //批次号6
                	bld.je = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Double.parseDouble(Convert.newSubString(result[1], 131, 143)),100),2,1);   //交易金额		
            	}  	
            		  
            }
            
			return true;
        }
		catch (Exception ex)
		{
			XYKSetError("XX","读取应答XX:"+ex.getMessage());
            new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);
            ex.printStackTrace();
            
			return false;
		}
		finally
        {
            if (br != null)
            {
                try
                {
                    br.close();
//                    if (PathFile.fileExist(path + "\\request.txt"))
//					{
//						PathFile.deletePath(path + "\\request.txt");
//					}
//
//					if (PathFile.fileExist(path + "\\result.txt"))
//					{
//						PathFile.deletePath(path + "\\result.txt");
//					}
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
			  type == PaymentBank.XYKTH || type == PaymentBank.XYKJZ || 
			  type == PaymentBank.XYKCD)
		{
			return true;
		}
		else
			return false;
	}
	
	public boolean checkBankSucceed()
	{
		if (bld.retbz == 'N')
		{
			errmsg = bld.retcode + "," + "交易失败";

			return false;
		}
		else
		{
			errmsg = "交易成功";

			return true;
		}
	}
	
	
	public boolean XYKCheckRetCode()
	{
		if (bld.retcode.trim().equals("000000"))
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
		
		public void XYKPrintDoc(int type)
		{
			ProgressBox pb = null;
			String name = path + "\\bank\\receipt.txt";

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
							if (line.indexOf("CUTPAPER") >= 0)
							{
								XYKPrintDoc_End();
								new MessageBox("请撕下客户签购单！！！");
								
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
		
	}


