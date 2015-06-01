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
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

public class HzxlQlgjYL_PaymentBankFunc extends PaymentBankFunc
{
	  public String[] getFuncItem()
	  {
	       String[] func = new String[7];
	    	
	       func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
	       func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
	       func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
	       func[3] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
	       func[4] = "[" + PaymentBank.XYKCD + "]" + "签购单重打";
	       func[5] = "[" + PaymentBank.XKQT1 + "]" + "分期付款";
	       func[6] = "[" + PaymentBank.XYKJZ + "]" + "当班结算";
	    	
	       return func;
	  }
	  
	  public boolean getFuncLabel(int type,String[] grpLabelStr)
	  {
		  //0-4对应FORM中的5个输入框
		  //null表示该不用输入
		  switch(type)
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
					grpLabelStr[1] = "检索参考号";
					grpLabelStr[2] = "原交易日";
					grpLabelStr[3] = null;
					grpLabelStr[4] = "交易金额";
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
				case PaymentBank.XKQT1://分期付款
					grpLabelStr[0] = null;
					grpLabelStr[1] = "分期编号";
					grpLabelStr[2] = null;
					grpLabelStr[3] = null;
					grpLabelStr[4] = "交易金额";
					break;
				case PaymentBank.XYKJZ://当班结算
					grpLabelStr[0] = null;
					grpLabelStr[1] = null;
					grpLabelStr[2] = null;
					grpLabelStr[3] = null;
					grpLabelStr[4] = null;
					break;
				default:
                    return false;
			}
			
			return true;
	  }
	  
	  public boolean getFuncText(int type,String[] grpTextStr)
		{
			//0-4对应FORM中的5个输入框
			//null表示该需要用户输入,不为null用户不输入
			switch(type)
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
				case PaymentBank.XKQT1://分期付款
					grpTextStr[0] = null;
					grpTextStr[1] = null;
					grpTextStr[2] = null;
					grpTextStr[3] = null;
					grpTextStr[4] = null;
					break;
				case PaymentBank.XYKJZ://分期付款
					grpTextStr[0] = null;
					grpTextStr[1] = null;
					grpTextStr[2] = null;
					grpTextStr[3] = null;
					grpTextStr[4] = "按回车键进行当班结算";
					break;
				default:
                    return false;	
			}
			
			return true;
		}
	  
	  public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	  {
		  try
	      {
			  if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && 
				   (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKCD) && (type != PaymentBank.XYKTH) && (type != PaymentBank.XKQT1) && (type != PaymentBank.XYKJZ))
	            {
	                errmsg = "银联接口不支持该交易";
	                new MessageBox(errmsg);

	                return false;
	            }
			  
			  
			  	//先删除上次交易数据文件
	            if (PathFile.fileExist("C:\\JavaPos\\request.txt"))
	            {
	                PathFile.deletePath("C:\\JavaPos\\request.txt");
	                
	                if (PathFile.fileExist("C:\\JavaPos\\request.txt"))
	                {
	            		errmsg = "交易请求文件request.txt无法删除,请重试";
	            		XYKSetError("XX",errmsg);
	            		new MessageBox(errmsg);
	            		return false;   	
	                }
	            }

	            if (PathFile.fileExist("C:\\JavaPos\\result.txt"))
	            {
	                PathFile.deletePath("C:\\JavaPos\\result.txt");
	                
	                if (PathFile.fileExist("C:\\JavaPos\\result.txt"))
	                {
	            		errmsg = "交易请求文件result.txt无法删除,请重试";
	            		XYKSetError("XX",errmsg);
	            		new MessageBox(errmsg);
	            		return false;   	
	                }
	            }
	            
	            //写入请求数据
	            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
	            {
	                return false;
	            }
	            
	            if (bld.retbz != 'Y')
	            {
	            	//调用接口模块
	                if (PathFile.fileExist("C:\\JavaPos\\singleebank.exe"))
	                {
	                	CommonMethod.waitForExec("C:\\JavaPos\\singleebank.exe","singleebank.exe");
	                }
	                else
	                {
	                    new MessageBox("找不到金卡工程模块 singleebank.exe");
	                    XYKSetError("XX","找不到金卡工程模块 singleebank.exe");
	                    return false;
	                }
	                
	                //读取应答数据
	                if (!XYKReadResult())
	                {
	                    return false;
	                }

	                // 检查交易是否成功
	                XYKCheckRetCode();
	            }
	            
	            //打印签购单
	            if (XYKNeedPrintDoc())
	            {
	                XYKPrintDoc();
	            }
	            
	            return true;
	      }
		  catch(Exception ex)
		  {
			  XYKSetError("XX","金卡异常XX:"+ex.getMessage());
	          new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);
			  ex.printStackTrace();
			  return false;
		  }
	  }
	  
	  public boolean XYKWriteRequest(int type, double money, String track1,String track2, String track3,String oldseqno, String oldauthno,String olddate, Vector memo)
	  {
		  try
		  {
			  String strtypecode = "";   //交易代码
			  String strje = "";         //金额
			  String strseqno = "";       //pos流水号 
			  String strsyjh = "";       //收银机号
			  String strsyyh = "";      //收银员号
			  
			  String strterm = "";    //终端号/检索参考号
			  
			  String strauto = "";      // 授权号
			  String strolddate = "";   //原交易日期
			  
			  String strtermtype = "";  //设备类型
			  
			  String strcust = "";      //自定义信息
			  
			  String strtrack2 = "";     //第二磁道
			  String strtrack3 = "";      //第三磁道
			  String stroldtypecode = "";  //原交易码
			  String stroldterm = "";    //原终端号
			  String stroldauto = "";   //原授权号
			  
			  switch (type)
              {
                  case PaymentBank.XYKXF: //消费
                	  strtypecode = "01";

                      break;

                  case PaymentBank.XYKCX: //消费撤销
                	  strtypecode = "02";

                      break;

                  case PaymentBank.XYKTH: //隔日退货   
                	  strtypecode = "09";

                      break;
                  case PaymentBank.XYKYE: //余额查询    
                	  strtypecode = "03";

                      break;

                  case PaymentBank.XYKCD: //签购单重打
                	  strtypecode = "12";

                      break;
                      
                  case PaymentBank.XKQT1: //分期付款
                	  strtypecode = "19";

                      break;
                  case PaymentBank.XYKJZ: //结账
                	  strtypecode = "14";

                      break;
                  default:
                      return false;
              }
// 交易代码  交易金额  pos流水号 收银机号 收银员号 检索参考号 授权号（分期付款期数） 原交易日期  卡片类型  自定义信息（单据号或索引号） 二轨 三轨 原交易码 原终端编号  原授权号
			  strje = Convert.increaseCharForward(String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1)),'0',12);
              
              if (strtypecode.equals("12"))
              {
            	  if (oldseqno != null)
	              {
            		  strseqno = Convert.increaseChar(oldseqno,6);
	              }
	              else
	              {
	            	  strseqno = "000000";
	              }
              }
              else
              {
	              if (oldseqno != null)
	              {
	            	  strseqno = Convert.increaseChar(oldseqno,6);
	              }
	              else
	              {
	            	  strseqno = Convert.increaseChar("",6);
	              }
              }
              
              strsyjh = Convert.increaseChar(GlobalInfo.syjDef.syjh,10);
              
              strsyyh = Convert.increaseChar(GlobalInfo.posLogin.gh,10);
              
              if (strtypecode.equals("09"))
              {
	              if (oldauthno != null)
	              {
	            	  strterm = Convert.increaseChar(oldauthno,15);
	              }
	              else
	              {
	            	  strterm = Convert.increaseChar("",15);
	              }
	              
	              strauto = Convert.increaseChar("",6);
              }
              else
              {
            	  if (oldauthno != null)
	              {
            		  strauto = Convert.increaseChar(oldauthno,6);
	              }
	              else
	              {
	            	  strauto = Convert.increaseChar("",6);
	              }
	              
            	  strterm = Convert.increaseChar("",15);
              }
              
              if (olddate != null)
              {
            	  strolddate = Convert.increaseChar(olddate,8);
              }
              else
              {
            	  strolddate = Convert.increaseChar("",8);
              }
              
              strtermtype = " ";
              
              strcust = Convert.increaseChar("",76);
              
              if (track2 != null)
              {
            	  strtrack2 = Convert.increaseChar(track2,37);
              }
              else
              {
            	  strtrack2 = Convert.increaseChar("",37);
              }

              if (track3 != null)
              {
            	  strtrack3 = Convert.increaseChar(track3,104);
              }
              else
              {
            	  strtrack3 = Convert.increaseChar("",104);
              }
              stroldtypecode = Convert.increaseChar("",2);;  //原交易码
			  stroldterm = Convert.increaseChar("",15);;    //原终端号
			  stroldauto = Convert.increaseChar("",6);;   //原授权号
			  
              
              
			   String cmd = strtypecode + strje + strseqno + strsyjh + strsyyh + strterm +
        		strauto + strolddate + strtermtype + strcust + strtrack2 + 
        		strtrack3 + stroldtypecode +stroldterm +stroldauto;
               
              PrintWriter pw = null;
              
              try
              {
  	            pw = CommonMethod.writeFile("C:\\JavaPos\\request.txt");
  	            
  	            if (pw != null)
  	            {
  	                pw.println(cmd);
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
			  ex.printStackTrace();
			  new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);
	        
			  return false;
		  }
	  }
	  
	  public boolean XYKReadResult()
	  {
		  BufferedReader br = null;
		  
		  try
		  {	  
			  if (!PathFile.fileExist("C:\\JavaPos\\result.txt") || ((br = CommonMethod.readFileGBK("C:\\JavaPos\\result.txt")) == null))
			  {
	            	XYKSetError("XX","读取金卡工程应答数据失败,文件result.txt不存在!");
	                new MessageBox("读取金卡工程应答数据失败\n请联系信息部确定当前交易是否成功!", null, false);
	
	                return false;
			  }
			  
			  String line = br.readLine();
			  
			  if (line.length() <= 0)
	          {
	             return false;
	          }
			  
			  bld.retcode = Convert.newSubString(line, 0, 6).trim();
			  
			  if (!bld.retcode.equals("000000"))
			  {
				  bld.retmsg = Convert.newSubString(line,6,46).trim();
				  return true;
			  }
			  
			  bld.retmsg = Convert.newSubString(line,6,46).trim();
			  
			  if (Convert.newSubString(line,46,52).length() > 0)
	          {
				  bld.trace = Convert.toLong(Convert.newSubString(line, 46,52).trim());
	          }
			  if(Convert.newSubString(line, 131, 143).trim().length()>0){
				  bld.je = ManipulatePrecision.doubleConvert(Double.parseDouble(Convert.newSubString(line, 131, 143).trim()) / 100, 2, 1);;
			  }
			  bld.cardno   = Convert.newSubString(line, 64, 83).trim();
			  
			  //bld.bankinfo = Convert.newSubString(line, 87,89) + XYKReadBankName(Convert.newSubString(line,87,89).trim());
			  bld.bankinfo = "9999";  //秋林客户要求显示9999
			  
			  errmsg = bld.retmsg;
			  
			  return true;
		  }
		  catch (Exception ex)
		  {
			  new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);    
	          XYKSetError("XX","读取应答XX:"+ex.getMessage());
	           
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
/*
					if (PathFile.fileExist("C:\\JavaPos\\request.txt"))
		            {
		                PathFile.deletePath("C:\\JavaPos\\request.txt");
		            }
					
					if (PathFile.fileExist("C:\\JavaPos\\result.txt"))
		            {
		                PathFile.deletePath("C:\\JavaPos\\result.txt");
		            }
					*/
				} catch (IOException e) 
				{
					new MessageBox("result.txt 关闭失败\n重试后如果仍难失败，请联系信息部");
					e.printStackTrace();
				}
        	}
	      }
	  }
	  
	  public boolean XYKCheckRetCode()
	  {
        if (bld.retcode.trim().equals("000000"))
        {
            bld.retbz = 'Y';
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
	            errmsg = bld.retmsg;
	
	            return false;
	        }
	        else
	        {
	            errmsg = "交易成功";
	
	            return true;
	        }
	  }

	  
	  public void XYKPrintDoc()
	  {
		if (GlobalInfo.sysPara.bankprint <= 0) return;
		  
        ProgressBox pb = null;

        try
        {
            if (!PathFile.fileExist("C:\\JavaPos\\bank\\receipt.txt"))
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
                    br = CommonMethod.readFileGBK("C:\\JavaPos\\bank\\receipt.txt");

                    if (br == null)
                    {
                        new MessageBox("打开签购单打印文件失败!");

                        return;
                    }

                    //
                    String line = null;

                    while ((line = br.readLine()) != null)
                    {
                    	if (line.length() <= 0)
                        {
                            continue;
                        }
                    	
                    	Printer.getDefault().printLine_Journal(line + "\n");
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

                //切纸
                Printer.getDefault().cutPaper_Journal();
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
            
            if (PathFile.fileExist("C:\\JavaPos\\bank\\receipt.txt"))
            {
                PathFile.deletePath("C:\\JavaPos\\bank\\receipt.txt");
            }
        }
	  }	  
	  
	  protected boolean XYKNeedPrintDoc()
		{
	        if (!checkBankSucceed())
	        {
	            return false;
	        }

	        int type = Integer.parseInt(bld.type.trim());

	        // 消费，消费撤销，重打签购单
	        if ((type == PaymentBank.XYKXF) || (type == PaymentBank.XYKCX) ||
	                (type == PaymentBank.XYKTH) || (type == PaymentBank.XYKCD) ||
	                (type == PaymentBank.XYKJZ))
	        {
	            return true;
	        }
	        else
	        {
	            return false;
	        }
		}
}
