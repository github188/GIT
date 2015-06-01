package bankpay.Bank;

import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

//台州奥特莱斯
//调用动态库（模块名：JHYB；动态库(dll文件）：sPosDll.dll；函数：int CARDPAY( char *strin, char *strout);）

public class CZWJ1_PaymentBankFunc extends CZWJ_PaymentBankFunc {
	
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
				  type == PaymentBank.XKQT2 ||
				  type == PaymentBank.XKQT3 ||
				  type == PaymentBank.XKQT4))
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

			
//////////	选择：消费/积分消费/////////////
			if(type == PaymentBank.XYKXF)
			{
				//多种支付方式，选择
				String code = "";
				String[] title = { "代码","消费类型 "};
				int[] width = { 60, 440 };
				Vector contents = new Vector();
				contents.add(new String[] { "1", "普通消费" });
				contents.add(new String[] { "2", "积分消费" });
				contents.add(new String[] { "3", "分期消费" });
				int choice = new MutiSelectForm().open("请选择消费类型", title, width, contents, true);
				if (choice == -1)
				{
					errmsg = "没有选择消费类型";
					return false;
				}else {
					String[] row = (String[]) (contents.elementAt(choice));
					code = row[0];
					if(code.equals("2"))
					{
						type = PaymentBank.XKQT3;
					}
					if(code.equals("3"))
					{
						type = PaymentBank.XKQT4;
					}
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
            	CommonMethod.waitForExec(path+"\\javaposbank.exe JHYB");    	
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
//				XYKPrintDoc(type);
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

}
