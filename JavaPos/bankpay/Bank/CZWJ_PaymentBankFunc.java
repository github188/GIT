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
import com.efuture.javaPos.UI.Design.MutiSelectForm;

//常州武进金卡接口 
//调用动态库（模块名：JHYB；动态库(dll文件）：sPosDll.dll；函数：int CARDPAY( char *strin, char *strout);）
public class CZWJ_PaymentBankFunc extends PaymentBankFunc
{
	 String path = null;
	    public String[] getFuncItem()
		{
			String[] func = new String[10];

			func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
			func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
			func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
			func[3] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
			func[4] = "[" + PaymentBank.XYKCD + "]" + "重打上笔";
			func[5] = "[" + PaymentBank.XYKJZ + "]" + "交易结算";
			func[6] = "[" + PaymentBank.XKQT1 + "]" + "重打任意笔";
			func[7] = "[" + PaymentBank.XKQT2 + "]" + "业务菜单";
			func[8] = "[" + PaymentBank.XKQT3 + "]" + "积分消费";
			func[9] = "[" + PaymentBank.XKQT4 + "]" + "分期消费";

			return func;
		}

		public boolean getFuncLabel(int type, String[] grpLabelStr)
		{
			switch (type)
			{
				case PaymentBank.XYKXF: // 消费
					grpLabelStr[0] = null;
					grpLabelStr[1] = null;
					grpLabelStr[2] = null;
					grpLabelStr[3] = null;
					grpLabelStr[4] = "交易金额";
					break;
				case PaymentBank.XYKCX: // 消费撤销
					grpLabelStr[0] = null;
					grpLabelStr[1] = "原凭证号";
					grpLabelStr[2] = null;
					grpLabelStr[3] = null;
					grpLabelStr[4] = "交易金额";
					break;
				case PaymentBank.XYKTH:// 隔日退货
					grpLabelStr[0] = "原参考号";
					grpLabelStr[1] = null;
					grpLabelStr[2] = "交易日期";
					grpLabelStr[3] = null;
					grpLabelStr[4] = "交易金额";
					break;
				case PaymentBank.XYKYE: // 余额查询
					grpLabelStr[0] = null;
					grpLabelStr[1] = null;
					grpLabelStr[2] = null;
					grpLabelStr[3] = null;
					grpLabelStr[4] = "余额查询";
					break;
				case PaymentBank.XYKCD: // 其它交易
					grpLabelStr[0] = null;
					grpLabelStr[1] = null;
					grpLabelStr[2] = null;
					grpLabelStr[3] = null;
					grpLabelStr[4] = "重打上笔";
				case PaymentBank.XYKJZ: // 结算
					grpLabelStr[0] = null;
					grpLabelStr[1] = null;
					grpLabelStr[2] = null;
					grpLabelStr[3] = null;
					grpLabelStr[4] = "交易结算";
					break;
				case PaymentBank.XKQT1: // 其它交易
					grpLabelStr[0] = null;
					grpLabelStr[1] = null;
					grpLabelStr[2] = null;
					grpLabelStr[3] = null;
					grpLabelStr[4] = "重打任意笔";
				case PaymentBank.XKQT2: // 其它交易
					grpLabelStr[0] = null;
					grpLabelStr[1] = null;
					grpLabelStr[2] = null;
					grpLabelStr[3] = null;
					grpLabelStr[4] = "业务菜单";
					break;
				case PaymentBank.XKQT3 : //积分消费
					grpLabelStr[0] = null;
					grpLabelStr[1] = null;
					grpLabelStr[2] = null;
					grpLabelStr[3] = null;
					grpLabelStr[4] = "积分消费";
					break;
				case PaymentBank.XKQT4 : //分期消费
					grpLabelStr[0] = null;
					grpLabelStr[1] = null;
					grpLabelStr[2] = null;
					grpLabelStr[3] = null;
					grpLabelStr[4] = "分期消费";
					break;
			}

			return true;
		}

		public boolean getFuncText(int type, String[] grpTextStr)
		{
			switch (type)
			{
				case PaymentBank.XYKXF: // 消费
					grpTextStr[0] = null;
					grpTextStr[1] = null;
					grpTextStr[2] = null;
					grpTextStr[3] = null;
					grpTextStr[4] = null;
					break;
				case PaymentBank.XYKCX: // 消费撤销
					grpTextStr[0] = null;
					grpTextStr[1] = null;
					grpTextStr[2] = null;
					grpTextStr[3] = null;
					grpTextStr[4] = null;
					break;
				case PaymentBank.XYKTH: // 隔日退货
					grpTextStr[0] = null;
					grpTextStr[1] = null;
					grpTextStr[2] = null;
					grpTextStr[3] = null;
					grpTextStr[4] = null;
					break;
				case PaymentBank.XYKYE: // 余额查询
					grpTextStr[0] = null;
					grpTextStr[1] = null;
					grpTextStr[2] = null;
					grpTextStr[3] = null;
					grpTextStr[4] = "按回车键开始余额查询";
					break;
				case PaymentBank.XYKCD: // 重打上一笔
					grpTextStr[0] = null;
					grpTextStr[1] = null;
					grpTextStr[2] = null;
					grpTextStr[3] = null;
					grpTextStr[4] = "按回车键开始重打上笔";
					break;
				case PaymentBank.XYKJZ: // 结算
					grpTextStr[0] = null;
					grpTextStr[1] = null;
					grpTextStr[2] = null;
					grpTextStr[3] = null;
					grpTextStr[4] = "按回车键开始结算";
					break;
				case PaymentBank.XKQT1: // 其它交易
					grpTextStr[0] = null;
					grpTextStr[1] = null;
					grpTextStr[2] = null;
					grpTextStr[3] = null;
					grpTextStr[4] = "按回车键开始重打任意笔";
					break;
				case PaymentBank.XKQT2: // 其它交易
					grpTextStr[0] = null;
					grpTextStr[1] = null;
					grpTextStr[2] = null;
					grpTextStr[3] = null;
					grpTextStr[4] = "按回车键开始调用银联业务菜单";
					break;
				case PaymentBank.XKQT3: //积分消费
					grpTextStr[0] = null;
					grpTextStr[1] = null;
					grpTextStr[2] = null;
					grpTextStr[3] = null;
					grpTextStr[4] = "积分消费";
					break;
				case PaymentBank.XKQT4: //分期消费
					grpTextStr[0] = null;
					grpTextStr[1] = null;
					grpTextStr[2] = null;
					grpTextStr[3] = null;
					grpTextStr[4] = "分期消费";
					break;
			}

			return true;
		}
	
	
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
	
	public boolean XYKWriteRequest(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		String line = "";
		String type1 = "";
		PrintWriter pw = null;

		try
		{
			switch (type)
			{
				case PaymentBank.XYKXF: // 消费
					type1 = "S01";
					break;
				case PaymentBank.XYKCX: // 消费撤销
					type1 = "V01";
					break;
				case PaymentBank.XYKTH: // 隔日退货
					type1 = "R01";
					break;
				case PaymentBank.XYKYE: // 余额查询
					type1 = "B01";
					break;
				case PaymentBank.XYKCD: // 重印上笔
					type1 = "P01";
					break;
				case PaymentBank.XYKJZ: // 结算
					type1 = "ST1";
					break;
				case PaymentBank.XKQT1: // 重印任意笔
					type1 = "P02";
					break;
				case PaymentBank.XKQT2: // 业务菜单
					type1 = "X01";
					break;
				case PaymentBank.XKQT3: // 积分消费
					type1 = "S02";
					break;
				case PaymentBank.XKQT4: // 分期消费
					type1 = "S03";
					break;
			}

			String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1));
			jestr = Convert.increaseCharForward(jestr, '0', 12);
			
			String extrainfo = "";
			if(type == PaymentBank.XYKCX)
			{
				extrainfo = Convert.increaseChar(oldauthno, ' ', 18);	
			}
			else if(type == PaymentBank.XYKTH)
			{
				extrainfo = Convert.increaseChar(oldseqno, ' ', 12) + Convert.increaseChar(olddate, ' ', 6);
			}else
			{
				extrainfo = Convert.increaseChar("", ' ', 18);
			}
			
			String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode, ' ', 10);
			
			String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 6);

			line = type1 + jestr + extrainfo + syjh + syyh;

			try
			{
				pw = CommonMethod.writeFile(path + "\\request.txt");
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
	
	
	public boolean XYKReadResult( int type)
	{
		BufferedReader br = null;

		try
		{
			if (!PathFile.fileExist(path + "\\result.txt") || ((br = CommonMethod.readFileGBK(path + "\\result.txt")) == null))
			{
				XYKSetError("XX", "读取金卡工程应答数据失败!");
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return false;
			}

			String line = br.readLine();

			if (line == null || line.length() <= 0)
			{ 
				return false; 
			}

			String result[] = line.split(",");
			if (result == null) 
			{
				return false;
			}

			if (result.length >= 2)
			{					
				bld.retcode = Convert.newSubString(result[1], 0, 2);  //返回码2
            	if(!bld.retcode.equals("00"))
            	{
            		bld.retmsg = Convert.newSubString(result[1], 2, 42).trim();  //错误说明
            		return false;
            	}
            	if(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX ||  type == PaymentBank.XYKTH || type == PaymentBank.XKQT3 )
            	{
            		bld.cardno = Convert.newSubString(result[1], 57, 76).trim();   //卡号19
                	bld.je = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Double.parseDouble(Convert.newSubString(result[1], 76, 88)),100),2,1);   //交易金额		
            	}

			}

			return true;
		}
		catch (Exception ex)
		{
			XYKSetError("XX", "读取应答XX:" + ex.getMessage());
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

					if (PathFile.fileExist(path + "\\request.txt"))
					{
						PathFile.deletePath(path + "\\request.txt");
					}

					if (PathFile.fileExist(path + "\\result.txt"))
					{
						PathFile.deletePath(path + "\\result.txt");
					}
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
		if ( bld.retcode.trim().equals("00"))
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
			  type == PaymentBank.XYKTH || type == PaymentBank.XYKCD ||
			  type == PaymentBank.XYKJZ || type == PaymentBank.XKQT1 ||
			  type == PaymentBank.XKQT3 || type == PaymentBank.XKQT4 )
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
		String d = date.getText();
		if (d.length() > 4)
		{
			new MessageBox("日期格式错误\n日期格式《MMDD》");
			return false;
		}
		
		return true;
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
