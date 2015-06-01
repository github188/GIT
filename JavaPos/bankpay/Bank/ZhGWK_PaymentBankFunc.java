package bankpay.Bank;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ExpressionDeal;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

public class ZhGWK_PaymentBankFunc extends PaymentBankFunc
{
	
	protected boolean XYKExecuteModule(String cmdline)
	{
		String exec = "";
		try
		{
			exec = getBankClassConfig("CALLEXEC");
			exec = ExpressionDeal.replace(exec, "%PARAM%", cmdline, true);
			String file = getBankClassConfig("CALLFILE");
			String file1 = "C:\\GWK\\result.txt";
			if (file == null || file.length() <= 0) CommonMethod.waitForExec(exec);
			else CommonMethod.waitForExec(exec,file,file1,msgcallback);
			return true;
        }
	    catch (Exception ex)
	    {
	    	ex.printStackTrace();
	    	XYKSetError("XX","加载第三方支付执行模块失败:"+ex.getMessage());
	        new MessageBox("加载第三方支付执行模块失败!\n\n" + exec + "\n\n"+ ex.getMessage(), null, false);

	        return false;
	    }			
	}
	
	public boolean callBankFunc(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		if (type == PaymentBank.XYKCD)
		{
			if (oldseqno != null && oldseqno.trim().length() > 0)
			{
				
			}
			else
			{
				TextBox txt = new TextBox();
				StringBuffer cardno = new StringBuffer();
				if (!txt.open("请输入原流水号", "流水号", "请输入原流水号", cardno, 0, 0, false, TextBox.AllInput)) { return false; }
				oldseqno = cardno.toString();
			}
			
			String tracefile = "BankDoc\\"+GlobalInfo.balanceDate.replaceAll("/", "")+"\\"+"bankdoc_" + oldseqno+ ".txt";
			BufferedReader br = null;
			
			if (!PathFile.fileExist(GlobalVar.HomeBase+"\\"+tracefile))
			{
				new MessageBox("BankDoc里没有找到流水号为"+oldseqno+"签购单文件");
				return false;
			}
			
			String line = null;
			
			try
			{
				String file = "C:\\GWK";
				br = CommonMethod.readFileGBK(file+"\\gwkpub.txt");
				
				if (br == null) 
				{
					new MessageBox("没有找到"+file+"\\gwkpub.txt文件，无法打印终端文件");
				}
				else
				{
					line = br.readLine();	
					br.close();
					if (line != null )
					{
						String[] lines = line.split("\\|");
						String shh = lines[2];
						String zdh = lines[3];
						
						Printer.getDefault().printLine_Normal("终端号:"+zdh+" 商户号:"+shh);
					}
				}
				Printer.getDefault().printLine_Normal("POS机号："+GlobalInfo.syjDef.syjh+" 收银员号:"+GlobalInfo.posLogin.gh+"-"+GlobalInfo.posLogin.name);
				
				Printer.getDefault().printLine_Normal("卡号      消费金额         凭证号");
				
				if (PathFile.fileExist(GlobalVar.HomeBase+"\\"+tracefile)) br = CommonMethod.readFileGBK(GlobalVar.HomeBase+"\\"+tracefile);
				
				if (br == null)
				{
					new MessageBox("BankDoc里没有找到流水号为"+oldseqno+"签购单文件");
					return false;
				}
				
				while ((line = br.readLine())!= null)
				{
					String[] lines = line.split("\\|");
					String jylx = lines[0];
					String kh = lines[1];
					String je = lines[2];
					String pzh = lines[3];
					//String sqm = lines[4];
					//String CKH = lines[5];
					String BZ = lines[7];
					
					String row = Convert.appendStringSize("", kh, 0, 20, 38);
					row = Convert.appendStringSize(row, je, 21, 8, 38);
					row = Convert.appendStringSize(row, pzh, 30, 8, 38);
					Printer.getDefault().printLine_Normal(row);
					row = Convert.appendStringSize("", jylx, 0, 16, 38);
					row = Convert.appendStringSize(row, BZ, 17, 8, 38);
					Printer.getDefault().printLine_Normal(row);
					
				}
				br.close();
				Printer.getDefault().cutPaper_Normal();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return true;
		}
		else
		{
			return super.callBankFunc(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);
		}
	}
	
	public void XYKPrintDoc()
	{
		try
		{
			if (Convert.toInt(bld.type) == PaymentBank.XYKYE) //查余额
			{
				//double je = bld.kye;
				//je = ManipulatePrecision.div(je, 100);
				//new MessageBox("余额 ："+ManipulatePrecision.doubleToString(bld.kye));
			}
			else if (Convert.toInt(bld.type) == 4) // 其他交易
			{
				String name = "C:\\GWK\\gwksetfile.txt";
				if (!new File(name).exists())
				{
					return ;
				}
				
				for (int i = 0; i < 1; i++)
				{
					// 开始打印
					XYKPrintDoc_Start();

					BufferedReader br = null;

					try
					{
						br = CommonMethod.readFileGBK(name);

						if (br == null)
						{
							new MessageBox("打开签购单打印文件失败!");

							return;
						}

						//
						String line = null;
						while ((line = br.readLine()) != null)
						{
							XYKPrintDoc_Print(line);
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

					// 结束打印
					XYKPrintDoc_End();
				}
				
				PathFile.deletePath(name);
			}
			else
			{
				String file = getBankClassConfig("PRINTFILE");
				if (file == null || file.length() <= 0) return;
				if (!PathFile.fileExist(file))
				{
					new MessageBox("找不到签购单打印文件!");
					return;
				}
				
				// 保存重打文件以便重打印

				// 按流水号备份重印文件
				String tracefile ="BankDoc\\"+GlobalInfo.balanceDate.replaceAll("/", "")+"\\"+"bankdoc_" + bld.trace + ".txt";
				if (!PathFile.fileExist(tracefile)) PathFile.copyPath(file, tracefile);
					
				// 上一笔交易重印文件
				PathFile.copyPath(file, "BankDoc\\"+"bankdoc_last.txt");
					
				PathFile.deletePath(file);
			}
		}
		catch(Exception er)
		{
			er.printStackTrace();
		}
	}
	
	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			String callexec = getBankClassConfig("CALLEXEC");
			if (callexec == null || callexec.trim().length() <= 0)
			{
				// yinliang test
				bld.retcode = "00";
				bld.retmsg = "模拟第三方支付交易成功!";
				bld.cardno = track2;
				bld.trace = Math.round(Math.random() * 1000000);
				bld.bankinfo = "0000测试银行";
				bld.memo = "100";	// 卡内余额
				this.errmsg = bld.retmsg;
				XYKCheckRetCode();
//				new MessageBox(bld.retmsg);
				return true;
			}
			else
			{
				// 支持保存重印文件模式,则直接在磁盘查找重印文件进行打印,不调用银联接口返回重印文件
				boolean call = true;
				if (saveprintagain && type == PaymentBank.XYKCD)
				{
					String tracefile = null;
					if (oldseqno != null && oldseqno.length() > 0) tracefile = "BankDoc\\"+"bankdoc_" + oldseqno + ".txt";
					else tracefile = "BankDoc\\"+"bankdoc_last.txt";
					if (PathFile.fileExist(tracefile))
					{
						call = false;
						
						// 无需调用接口直接生成成功应答
						String retcode = getBankClassConfig("RETCODE");
						if (retcode == null || retcode.length() <= 0) retcode = "00";
						if (retcode != null && retcode.length() > 0) retcode = retcode.split(",")[0];
						bld.retcode = retcode;
						bld.retmsg  = "找到签购单重印文件";
						
						// 把重印文件拷贝为当前签购单文件
						String file = getBankClassConfig("PRINTFILE");
						if (file != null && file.length() > 0) PathFile.copyPath(tracefile, file);
					}
					else
					{
						XYKSetError("XX","找不到原签购单重印文件");
						XYKCheckRetCode();
						new MessageBox(getErrorMsg());
						return true;
					}
				}
				
				if (call)
				{
					// 写入请求数据
					StringBuffer cmdline = new StringBuffer();
					if (!XYKWriteRequest(cmdline,type, money, track1, track2, track3, oldseqno,oldauthno,olddate,memo))
					{
						return false;
					}
				
//					new MessageBox(cmdline.toString());
					// 调用接口模块
					if (!XYKExecuteModule(cmdline.toString()))
					{
						return false;
					}
					 
					// 读取应答数据
					if (!XYKReadResult())
					{
						return false;
					}
				}
				
				// 如果撤销交易的时候读取到“OO”检查日志
				if (type == PaymentBank.XYKCX && bld.retcode.equals("OO"))
				{
					if (!readLog(oldseqno,track2))
					{
						return false;
					}
				}

				// 检查交易是否成功
				XYKCheckRetCode();

				// 打印签购单
				if (XYKNeedPrintDoc())
				{
					XYKPrintDoc();
				}

				return true;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			XYKSetError("XX","支付异常XX:"+ex.getMessage());
			new MessageBox("调用第三方支付处理模块异常!\n\n" + ex.getMessage(), null, false);

			return false;
		}
	}

	private boolean readLog(String oldseqno,String track2)
	{
		String logname = "c:\\gwk\\javaposbanklog"+ManipulateDateTime.getCurrentDateBySign()+".txt";
		
		BufferedReader br = CommonMethod.readFile(logname);
		String line = null;
		try
		{
			// 查找是否成功
			boolean done = false;
			String success = "";
			while ((line = br.readLine()) != null)
			{
				if (done == true)
				{
					line = line.substring(line.indexOf("end")+6);
					if (line.substring(0,2).equals("00"))
					{
						success = line;
						break;
					}
					done = false;
				}
				
				if (line.indexOf("start") > 0 && line.indexOf("D") > 0 && line.indexOf(track2) > 0 &&  line.indexOf(oldseqno) > 0)
				{
					done = true;
				}
			}
			
			if (success.length() >0)
			{
				String file = getBankClassConfig("RETFILE");
				//String encode = getBankClassConfig("RETENCODE");
				PrintWriter pw = CommonMethod.writeFileUTF(file);
				pw.println(success);
				pw.flush();
				pw.close();
				
				// 重新读取文档
				if (!XYKReadResult())
				{
					return false;
				}
			}
			
			return true;
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		finally
		{
			try
			{
				br.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
