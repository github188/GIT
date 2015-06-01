package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

//杭州水晶城市民卡接口
//调用动态库（模块名：MISP；动态库(dll文件）：MisPosDll.dll ；函数：Int MisPos_Handle(long loprType,char *dest, long amt, char* src)
public class HzSJC3_PaymentBankFunc extends PaymentBankFunc
{
    String path = null;
	private SaleBS saleBS = null;
	public String[] getFuncItem()
    {
        String[] func = new String[6];
        
        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
        func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
        func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
        func[3] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
//        func[4] = "[" + PaymentBank.XYKCD + "]" + "重打签购单";
        func[4] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
        func[5] = "[" + PaymentBank.XYKJZ + "]" + "交易对账";
        
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
                grpLabelStr[1] = "流水号";
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易金额";
            break;
        	case PaymentBank.XYKTH://隔日退货   
				grpLabelStr[0] = "原交易参考号";
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
//        	case PaymentBank.XYKCD: //重打签购单    
//                grpLabelStr[0] = null;
//                grpLabelStr[1] = null;
//                grpLabelStr[2] = null;
//                grpLabelStr[3] = null;
//                grpLabelStr[4] = "重打签购单";
//            break;
        	case PaymentBank.XYKQD: //签到   
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易签到";
            break;
        	case PaymentBank.XYKJZ: //对账  
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易对账";
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
		 	case PaymentBank.XYKQD: 	//交易签到   
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始签到";
            break;
		 	case PaymentBank.XYKJZ: 	//交易结算
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始结算";
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
				  type == PaymentBank.XYKQD ||
				  type == PaymentBank.XYKJZ || 
				  type == PaymentBank.XYKYE ||
				  type == PaymentBank.XYKCD ))
				{			
					  new MessageBox("银联接口不支持此交易类型！！！");
					  
					  return false;
			    }
			
			//避免从菜单直接进行交易
			if (memo.size() >= 2)
				saleBS = (SaleBS) memo.elementAt(2);
			if (saleBS == null && (type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKTH))
			{
				errmsg = "该交易必须在付款的时候使用,请正常操作！";
				XYKSetError("XX", errmsg);
				new MessageBox(errmsg);
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
			if (PathFile.fileExist(path + "\\Print.txt"))
            {
                PathFile.deletePath(path + "\\Print.txt");
            }
			
			if (PathFile.fileExist(path + "\\Settle.txt"))
            {
                PathFile.deletePath(path + "\\Settle.txt");
            }
			
			
            // 写入请求数据
            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
            {
                return false;
            }
            //  调用接口模块
            if (PathFile.fileExist(path+"\\javaposbank.exe"))
            {
            	CommonMethod.waitForExec(path+"\\javaposbank.exe MISP");
            }
            else
            {
                new MessageBox("找不到金卡工程模块 javaposbank.exe");
                XYKSetError("XX","找不到金卡工程模块 javaposbank.exe");
                return false;
            }
			
            // 读取应答数据
            if (!XYKReadResult(type))
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
		 
		 String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
         jestr = Convert.increaseCharForward(jestr,'0',12);
         
         String seqno = Convert.increaseCharForward(oldseqno, '0', 12); //参考号
         
         String authno = Convert.increaseCharForward(oldauthno, '0', 6); //流水号
		 
		 try
		 {
			 switch (type)
			 {
			 	case PaymentBank.XYKXF: 	// 消费
                    line = "1105"+","+jestr+","+"";
                break;
			 	case PaymentBank.XYKCX: 	// 消费撤销
			 		line = "6105"+","+jestr+","+authno;
			 	break;	
                case PaymentBank.XYKTH:		// 隔日退货
                	line = "6105"+","+jestr+","+seqno;
                break;
                case  PaymentBank.XYKYE: 	// 余额查询
                	line = "1025"+","+"0"+","+"";
                break;	
//                case  PaymentBank.XYKCD: 	// 重打签购单
//                	line = "04";
//                break;
                case  PaymentBank.XYKQD: 	// 交易签到
                	line = "6215"+","+"0"+","+"";
                break;
                case  PaymentBank.XYKJZ: 	// 交易结算
                	line = "6231"+","+"0"+","+"";			
                break;
			 }

	         
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
	public boolean XYKReadResult(int type)
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
            
            if (result.length >= 2)
            {
    			//当银行返回字符以字节计算，而字符中汉字出现导致line的长度和字节书不等时，汉字后面的内容倒着计算位置
    			// int len = line.length();
    			//防止字节和字符不一致问题
    			//bld.memo = Convert.newSubString(line, 2, 21);
            	bld.retcode  = Convert.newSubString(result[1], 0, 2);  //返回码2
            	if(!bld.retcode.equals("00"))
            	{
            		bld.retmsg = bld.retcode + "," +"交易失败，请联系银联";   //错误说明
            		return false;
            	}
            	if(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX ||  type == PaymentBank.XYKTH )
            	{
                	bld.cardno = Convert.newSubString(result[1], 30, 40); //卡号20
                	bld.trace = Convert.toInt(Convert.newSubString(result[1], 68, 74));   //流水号6
                	bld.je = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Convert.toDouble(Convert.newSubString(result[1], 74, 86)),100),2,1);   //交易金额		
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
	public boolean XYKCheckRetCode()
	{
		if (bld.memo.trim().equals("0") && bld.retcode.trim().equals("00"))
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
			errmsg = bld.retmsg;

			return false;
		}
		else
		{
			errmsg = "交易成功";

			return true;
		}
	}
	
	//传8位不用重写 传4位需要重写
//	public boolean checkDate(Text date)
//	{
//		String d = date.getText();
//		if (d.length() != 4)
//		{
//			new MessageBox("日期格式错误\n日期格式《MMDD》");
//			return false;
//		}
//		
//		return true;
//	}
	
	
	
	public void XYKPrintDoc(int type)
	{
		ProgressBox pb = null;
		String name = null;
		if(type == PaymentBank.XYKJZ)
		{
			name =path + "\\Settle.txt";
		}
		else{
			name =path + "\\Print.txt";
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
