package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;

//福星惠誉银联接口
//调用动态库（模块名：JHYB；动态库(dll文件）：sPosDll.dll；函数：int CARDPAY( char *strin, char *strout);）

public class Spzk1_PaymentBankFunc extends Spzk_PaymentBankFunc {
	
	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		path = ConfigClass.BankPath;
		try
		{
			if (!(type == PaymentBank.XYKXF || 
				  type == PaymentBank.XYKCX || 
				  type == PaymentBank.XYKTH || 
				  type == PaymentBank.XYKYE ||
				  type == PaymentBank.XYKCD ||
				  type == PaymentBank.XYKJZ ||
				  type == PaymentBank.XKQT1 ||
				  type == PaymentBank.XKQT2 ))
				{			
					 new MessageBox("银联接口不支持此交易类型！！！");
						  
					 return false;
				}

			// 先删除上次交易数据文件
			if (PathFile.fileExist(path + "\\request.txt"))
			{
				PathFile.deletePath(path + "\\request.txt");

				if (PathFile.fileExist(path + "\\request.txt"))
				{
					errmsg = "交易请求文件request.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist(path + "\\result.txt"))
			{
				PathFile.deletePath(path + "\\result.txt");

				if (PathFile.fileExist(path + "\\result.txt"))
				{
					errmsg = "交易请求文件result.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist(path + "\\toprint.txt"))
			{
				PathFile.deletePath(path + "\\toprint.txt");
			}

			// 写入请求数据
			if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
			{ 
				return false; 
			}

            //  调用接口模块
            if (PathFile.fileExist(path+"\\javaposbank.exe"))
            {
//            	CommonMethod.waitForExec(path+"\\javaposbank.exe JHYB");    
            	CommonMethod.waitForExec(path+"\\javaposbank.exe JHYB","javaposbank");
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

            //打印签购单(银联打印)
			if (XYKNeedPrintDoc(type))
			{
				XYKPrintDoc(type);
			}
            return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			XYKSetError("XX", "金卡异常XX:" + ex.getMessage());
			new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);

			return false;
		}
	}
	
	public void XYKPrintDoc(int type)
	{
		ProgressBox pb = null;
		String name = null;
		name =path + "\\toprint.txt";

		try
		{
			if (!PathFile.fileExist(name))
			{
					new MessageBox("找不到签购单打印文件！！！");				
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
						new MessageBox("打开签购单文件失败");						
						return ;
					}
					
					String line = null;
					while ((line = br.readLine()) != null)
					{
						if (line.length() <= 0)
							continue;
						//银行签购单模板添加 "CUTPAPER" 标记
						//当程序里面读取到这个字符是，打印机切纸
						if (line.trim().equals("CUT"))  //M:此处切纸标记为“CUT”
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
