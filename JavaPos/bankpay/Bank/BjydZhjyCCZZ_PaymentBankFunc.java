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
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;

public class BjydZhjyCCZZ_PaymentBankFunc extends BjydZhjy_PaymentBankFunc
{
	public boolean XYKReadResult()
	{
		BufferedReader br = null;

		try
		{
			if (!PathFile.fileExist("C:\\BMP\\PFACE.TXT") || ((br = CommonMethod.readFileGBK("C:\\BMP\\PFACE.TXT")) == null))
			{
				XYKSetError("XX", "读取金卡工程应答数据失败!");
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return false;
			}

			// 读取请求数据
			String line = br.readLine();

			//
			bld.retcode = Convert.newSubString(line, 0, 2);

			bld.retmsg = Convert.newSubString(line, 2, 42).trim();
			if (bld.retcode.equals("00"))
			{
				bld.bankinfo = Convert.newSubString(line, 42, 44) + XYKReadBankName(Convert.newSubString(line, 42, 44));
				bld.cardno = Convert.newSubString(line, 54, 74).trim();
	
				String je = Convert.newSubString(line, 74, 86);
				double j = Double.parseDouble(je);
				j = ManipulatePrecision.mul(j, 0.01);
				bld.je = j;
			}	
			if (Convert.newSubString(line, 86, 92).length() > 0)
			{
				bld.trace = Convert.toLong(Convert.newSubString(line, 82, 94).trim());
			}

			errmsg = bld.retmsg;
			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);
			XYKSetError("XX", "读取应答XX:" + ex.getMessage());
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
					// TODO 自动生成 catch 块
					new MessageBox("PFACE.TXT 关闭失败\n重试后如果仍然失败，请联系信息部");
					e.printStackTrace();
				}
			}
		}
	}
	
	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			// 调用接口模块
			if (PathFile.fileExist("c:\\bmp\\bankmis.exe"))
			{
				if (PathFile.fileExist("C:\\bmp\\PRINT.TXT"))
				{
					PathFile.deletePath("C:\\bmp\\PRINT.TXT");

					if (PathFile.fileExist("C:\\bmp\\PRINT.TXT"))
					{
						errmsg = "交易请求文件PRINT.txt无法删除,请重试";
						XYKSetError("XX", errmsg);
						new MessageBox(errmsg);
						return false;
					}
				}

				if (PathFile.fileExist("C:\\BMP\\PFACE.TXT"))
				{
					PathFile.copyPath("C:\\BMP\\PFACE.TXT", "C:\\BMP\\LastPFACE.TXT");
					PathFile.deletePath("C:\\BMP\\PFACE.TXT");

					if (PathFile.fileExist("C:\\BMP\\PFACE.TXT"))
					{
						errmsg = "交易请求文件PFACE.TXT无法删除,请重试";
						XYKSetError("XX", errmsg);
						new MessageBox(errmsg);
						return false;
					}
				}

				if (PathFile.fileExist("c:\\bmp\\param.txt"))
				{
					PathFile.copyPath("C:\\BMP\\PFACE.TXT", "C:\\BMP\\Lastparam.TXT");
					PathFile.deletePath("c:\\bmp\\param.txt");

					if (PathFile.fileExist("c:\\bmp\\param.txt"))
					{
						errmsg = "交易请求文件param.txt无法删除,请重试";
						XYKSetError("XX", errmsg);
						new MessageBox(errmsg);
						return false;
					}
				}

				String line = "";

				String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode, ' ', 6);
				String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 6);
				String type1 = "";

				String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1));
				jestr = Convert.increaseCharForward(jestr, '0', 12);
		
				//根据不同的类型生成文本结构
				//签到-10，签退-11，消费-00，撤销-01，查询-02，预授权-03，授权确认-04，退货-05，授权撤销-19，重打印-15，统计-16，查询流水-17，结算-18
				//
				switch (type)
				{
					case PaymentBank.XYKQD:
						type1 = "10";
						break;
					case PaymentBank.XYKXF:
						type1 = "00";
						break;
					case PaymentBank.XYKCX:
						type1 = "01";
						break;
					case PaymentBank.XYKTH:
						type1 = "05";
						break;
					case PaymentBank.XKQT1:		// 预授权
						type1 = "03";
						break;
					case PaymentBank.XKQT2:		// 授权确认
						type1 = "04";
						break;
					case PaymentBank.XKQT3:		// 授权撤销
						type1 = "19";
						break;
					case PaymentBank.XYKYE:		// 查询余额
						type1 = "02";
						break;
					case PaymentBank.XYKJZ:		// 结算
						type1 = "18";
						break;
					case PaymentBank.XYKCD:		// 重打票据
						type1 = "99";
						break;
					case PaymentBank.XKQT4:		// 统计
						type1 = "16";
						break;
					default:
						return false;
				}

				line = syjh + " " + syyh + " " + type1 + " " + jestr;

				PrintWriter pw = null;

				try
				{
					pw = CommonMethod.writeFile("c:\\bmp\\param.txt");

					if (pw != null)
					{
						pw.print(line);
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

				CommonMethod.waitForExec("c:\\bmp\\bankmis.exe " + line, "bankmis.exe");
			}
			else
			{
				new MessageBox("找不到金卡工程模块 bankmis.exe");
				XYKSetError("XX", "找不到金卡工程模块 bankmis.exe");
				return false;
			}

			// 读取应答数据
			if (!XYKReadResult()) { return false; }

			// 检查交易是否成功
			XYKCheckRetCode();

			// 打印签购单
			if (XYKNeedPrintDoc())
			{
				XYKPrintDoc();
			}

			return true;
		}
		catch (Exception ex)
		{
			XYKSetError("XX", "金卡异常XX:" + ex.getMessage());
			new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);

			return false;
		}
	}
	
	public void XYKPrintDoc()
	{
		ProgressBox pb = null;

		try
		{
			if (!PathFile.fileExist("C:\\BMP\\PRINT.TXT"))
			{
				if (Integer.parseInt(bld.type) != PaymentBank.XYKCD) new MessageBox("找不到签购单打印文件!");
				
				return;
			}

			pb = new ProgressBox();
			pb.setText("正在打印银联签购单,请等待...");
			int num = 0;
			for (int i = 0; i < GlobalInfo.sysPara.bankprint; i++)
			{
				BufferedReader br = null;

				//
				Printer.getDefault().startPrint_Normal();

				try
				{
					//由于发现在windows环境下,用GBK读取文件会产生BUG,改为GB2310
					br = CommonMethod.readFileGB2312("C:\\BMP\\PRINT.TXT");

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

						num++;
						
						if (line.indexOf("CUTPAPER") >= 0)
						{
							Printer.getDefault().cutPaper_Normal();
							continue;
						}
						
						Printer.getDefault().printLine_Normal(line + "\n");
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

				// 切纸
				if (num > 0)
				Printer.getDefault().cutPaper_Normal();
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
		}
	}
}
