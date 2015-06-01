package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;

//南宁万达金卡工程接口
//调用动态库（模块名：GZZC；动态库(dll文件）：ChaseInterface.dll  函数：int Abmcs(void *strIn, void *strOut)；）
public class Gzzc_lyqfNew1_PaymentBankFunc extends Gzzc_lyqfNew_PaymentBankFunc {
	
	public void XYKPrintDoc(int type)
	{
		ProgressBox pb = null;
		String name = null;
		if(type == PaymentBank.XYKJZ)
		{
			name =path + "\\Settle.txt";
		}
		else{
			name =path + "\\print.txt";
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
			pb.setText("正在打印,请等待...");
		
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
//							XYKPrintDoc_End();
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
				if(type == PaymentBank.XYKJZ)
				{
//					XYKPrintDoc_End();
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
