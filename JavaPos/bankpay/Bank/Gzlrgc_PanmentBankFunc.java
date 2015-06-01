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
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

//贵州龙润广场银联接口(有界面)
//调用动态库（模块名：MISPOS；动态库(dll文件）：bankpos.dll；函数：int mispos(char * request,char *response)；）
public class Gzlrgc_PanmentBankFunc extends PaymentBankFunc{
	String path = "C:\\gmc";
	public String[] getFuncItem()
    {
        String[] func = new String[11];
        
        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费交易";
        func[1] = "[" + PaymentBank.XYKCX + "]" + "撤销交易";
        func[2] = "[" + PaymentBank.XYKTH + "]" + "退货交易";
        func[3] = "[" + PaymentBank.XYKYE + "]" + "查余交易";
        func[4] = "[" + PaymentBank.XKQT1 + "]" + "管理类交易";//签到、重打印、结算等
        func[5] = "[" + PaymentBank.XKQT2 + "]" + "全民付类交易";
        func[6] = "[" + PaymentBank.XKQT3 + "]" + "预授权类交易";
        func[7] = "[" + PaymentBank.XKQT4 + "]" + "积分消费交易";
        func[8] = "[" + PaymentBank.XKQT5 + "]" + "积分撤销交易";
        func[9] = "[" + PaymentBank.XKQT6 + "]" + "分期消费交易";
        func[10] = "[" + PaymentBank.XKQT7 + "]" + "分期撤销交易";
        
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
        	case PaymentBank.XYKCX: //撤销
                grpLabelStr[0] = null;
                grpLabelStr[1] = "流水号";
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易金额";
            break;
        	case PaymentBank.XYKTH://退货   
				grpLabelStr[0] = "原交易参考号";
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
        	case PaymentBank.XKQT1: //管理类交易    
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "管理类交易";
            break;
        	case PaymentBank.XKQT2: //全民付类交易   
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "全民付类交易";
            break;
        	case PaymentBank.XKQT3: //预授权类交易  
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "预授权类交易";
            break;
        	case PaymentBank.XKQT4: //积分消费交易  
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "积分消费交易";
            break;
        	case PaymentBank.XKQT5: //积分撤销交易  
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "积分撤销交易";
            break;
        	case PaymentBank.XKQT6: //分期消费交易  
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "分期消费交易";
            break;
        	case PaymentBank.XKQT7: //分期撤销交易  
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "分期撤销交易";
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
		 	case PaymentBank.XYKCX: 	// 撤销
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = null;
            break;
		 	case PaymentBank.XYKTH:		//退货   
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
		 	case PaymentBank.XKQT1: 	//管理类交易    
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始管理类交易";
            break;
		 	case PaymentBank.XKQT2: 	//全民付类交易   
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始全民付类交易";
            break;
		 	case PaymentBank.XKQT3: 	//预授权类交易
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始预授权类交易";
            break;
		 	case PaymentBank.XKQT4: 	//积分消费交易
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始积分消费交易";
            break;
		 	case PaymentBank.XKQT5: 	//积分撤销交易
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始积分撤销交易";
            break;
		 	case PaymentBank.XKQT6: 	//分期消费交易
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始分期消费交易";
            break;
		 	case PaymentBank.XKQT7: 	//分期撤销交易
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始分期撤销交易";
            break;
		}
		
		return true;
    }
	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if (!(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || 
				  type == PaymentBank.XYKTH || type == PaymentBank.XYKYE ||
				  type == PaymentBank.XKQT1 || type == PaymentBank.XKQT2 ||
				  type == PaymentBank.XKQT3 || type == PaymentBank.XKQT4 ||
				  type == PaymentBank.XKQT5 || type == PaymentBank.XKQT6 ||
				  type == PaymentBank.XKQT7))
				{			
					  new MessageBox("银联接口不支持此交易类型！！！");
					  
					  return false;
			    }
			
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
			if (PathFile.fileExist(path + "\\response.txt"))
			{
				PathFile.deletePath(path + "\\response.txt");
				if (PathFile.fileExist(path + "\\response.txt"))
				{
					errmsg = "交易“response.txt”文件删除失败，请重试！！！";
					XYKSetError("XX",errmsg);
					new MessageBox(errmsg);
					
					return false;
				}				
			}
			if (PathFile.fileExist(path + "\\receipt.txt"))
            {
                PathFile.deletePath(path + "\\receipt.txt");
            }
            // 写入请求数据
            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
            {
                return false;
            }
            //  调用接口模块
            if (PathFile.fileExist(path+"\\javaposbank.exe"))
            {
            	CommonMethod.waitForExec(path+"\\javaposbank.exe MISPOS","javaposbank");
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
                    type1 = "C";
                break;
			 	case PaymentBank.XYKCX: 	// 撤销
			 		type1 = "D";
			 	break;	
                case PaymentBank.XYKTH:		// 退货
                	type1 = "R";
                break;
                case  PaymentBank.XYKYE: 	// 余额查询
                	type1 = "I";
                break;	
                case  PaymentBank.XKQT1: 	// 管理类交易  //签到、重打印、结算等
                	type1 = "E";
                break;
                case  PaymentBank.XKQT2: 	// 全民付类交易
                	type1 = "Q";
                break;
                case  PaymentBank.XKQT3: 	// 预授权类交易
                	type1 = "P";
                break;
                case  PaymentBank.XKQT4: 	// 积分消费交易
                	type1 = "F";
                break;
                case  PaymentBank.XKQT5: 	// 积分撤销交易
                	type1 = "G";
                break;
                case  PaymentBank.XKQT6: 	// 分期消费交易
                	type1 = "H";
                break;
                case  PaymentBank.XKQT7: 	// 分期撤销交易
                	type1 = "J";
                break;
			 }

			 String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode,' ', 15);

	         String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh,' ', 15);
	         
			 String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
	         jestr = Convert.increaseCharForward(jestr,'0',12);
	         
	         String by = Convert.increaseChar("0",'0', 6);   //备用
	         
	         bld.crc = XYKGetCRC();
	         
	        
	         //收银机号+操作员号+交易类型+金额+备用+交易校验数据
	         line = syjh + syyh + type1 + jestr + by + bld.crc;
	     
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

	
	
	
	
	//读取response文件
	public boolean XYKReadResult()
	{
		BufferedReader br = null;
		
		try
        {
			if (!PathFile.fileExist(path+"\\response.txt") || ((br = CommonMethod.readFileGBK(path+"\\response.txt")) == null))
            {
            	XYKSetError("XX","读取金卡工程应答数据失败!");
                new MessageBox("读取金卡工程应答数据失败!", null, false);
                
                return false;
            }
			
			String line = br.readLine();

            if (line == null || line.length() <= 0)
            {
                return false;
            }
            
//            int type = Integer.parseInt(bld.type.trim());
            bld.retcode  = line.substring(0,2);  //返回码2
            bld.je = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Double.parseDouble(line.substring(2,14) ),100),2,1);   //交易金额12
            bld.cardno = line.substring(14,33);   	//卡号19
            bld.memo = line.substring(33,39);   	//备用6
//            bld.memo1 = line.substring(39,43);   	//银行卡卡类4
            bld.bankinfo = line.substring(43,51); 	//发卡银行名称8
            String lrc = line.substring(51,54);		//LRC校验3
			if(!lrc.equals(bld.crc))
			{
				errmsg = "返回效验码" + lrc + "同原效验码" + bld.crc + "不一致";
				XYKSetError("XX", errmsg);
				new MessageBox(errmsg);
				
				return false;
			}
//            bld.memo2 = line.substring(54, 69);     //商户号15
//            bld.memo2 = line.substring(69, 77);     //终端号8
//            bld.memo2 = line.substring(77, 83);     //批次号6
            bld.trace = Integer.parseInt(line.substring(83, 89));     //流水号6
            bld.memo2 = line.substring(89, 101);     //系统参考号12
            
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
	
	public boolean XYKNeedPrintDoc(int type)
	{
		//1.交易成功才打印
		if (!checkBankSucceed())
	    {
	        return false;
	    }
		//.2交易成功后，交易类型为如下的才打印
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
	
	
	
	public void XYKPrintDoc()
	{
		ProgressBox pb = null;
		String name =path + "\\receipt.txt";
		try
		{
			if (!PathFile.fileExist(name))
			{
				new MessageBox("找不到签购单打印文件！！！");
				
				return ;
			}
			pb = new ProgressBox();
			pb.setText("正在打印银联签购单,请等待..." + "\t OY : " + GlobalInfo.sysPara.issetprinter);
			
			for (int i = 0; i < GlobalInfo.sysPara.bankprint; i ++)
			{
				BufferedReader br = null;
				XYKPrintDoc_Start();
				try
				{
					br = CommonMethod.readFileGB2312(name);
					if (br == null)
					{
						new MessageBox("打开签购单文件失败");
						
						return ;
					}
					
					String line = null;
					while ((line = br.readLine()) != null)
					{
						if (line.length() <= 0)
							continue;
						if (line.equals("CUTPAPER"))
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
			new MessageBox("打印签购单异常!!!\n" + e.getMessage());
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
