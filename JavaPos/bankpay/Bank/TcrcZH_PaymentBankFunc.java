package bankpay.Bank;

import java.io.BufferedReader;
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

public class TcrcZH_PaymentBankFunc extends PaymentBankFunc
{
	protected boolean XYKWriteRequest(StringBuffer arg,int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		if (type == PaymentBank.XYKCD)
		{
			arg.insert(0, "1,");
		}
		else
		{
			arg.insert(0, "0,");
		}
		
		boolean done = super.XYKWriteRequest(arg, type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);
		

		return done;
	}

	protected boolean XYKReadResult()
	{
		try
		{
			// 打开应答文件
			String file = getBankClassConfig("RETFILE");
			if (file == null || file.length() <= 0 || !PathFile.fileExist(file) || !rtf.loadFile(file,getBankClassConfig("RETENCODE")))
			{
				new MessageBox("读取交易应答数据失败!", null, false);
				return false;
			}

			// 读取应答配置
			String retsplit = getBankClassConfig("RETSPLT");
			String retparastr = getBankClassConfig("TYPE"+bld.type+"_RETPARA");
			String rettypestr = getBankClassConfig("TYPE"+bld.type+"_RETTYPE");
			
			if (retparastr == null) retparastr = "%NONE%,%RETCODE%,%NONE%,%RETMSG%,%CARDNO%,%NONE%,%NONE%,%NONE%,%BANKNAME%,%NONE%,%NONE%,%NONE%,%SEQNO%";
			if (rettypestr == null) rettypestr = "4s 2s 1s 41s 20s 5s 16s 9s 11s 15s 13s 7s 6s";
			
			String[] retpara = (retparastr==null?null:retparastr.split(","));
			String[] rettype = (rettypestr==null?null:rettypestr.split(" "));
			
			// 读取应答数据
			String line = rtf.nextRecord();
			//new MessageBox(line);
			rtf.close();
			
			if (line == null || line.trim().length() <= 0)
			{
				new MessageBox("读取交易应答数据为空!", null, false);
				return false;
			}
			
			// 分解应答数据
			Vector linevec = new Vector();
			String[] linelist = null;
			if (retsplit != null && retsplit.trim().length() > 0) linelist = (line==null?null:line.split(retsplit));
			else linelist = new String[]{line};
			int curline = 0,curlinepos = 0;
			for (int i=0;retpara != null && i < retpara.length && linelist != null && curline < linelist.length;i++)
			{
				//System.out.println(retpara[i]);
				if (i < rettype.length) 
				{
					String fmt = rettype[i];
					int j = 0;
					for (;j<fmt.length();j++) if ((fmt.charAt(j) >= 'a' && fmt.charAt(j) <= 'z') || (fmt.charAt(j) >= 'A' && fmt.charAt(j) <= 'Z')) break;
					int fmtlen = Convert.toInt(fmt.substring(0,j));
					if (fmtlen > 0 && curlinepos + fmtlen < Convert.countLength(linelist[curline])) fmt = Convert.newSubString(linelist[curline],curlinepos,curlinepos + fmtlen);
					else fmt = Convert.newSubString(linelist[curline],curlinepos,Convert.countLength(linelist[curline]));
					curlinepos += Convert.countLength(fmt);
					if (curlinepos >= Convert.countLength(linelist[curline])) { curline++;curlinepos = 0; }
					linevec.add(fmt);
				}
				else { linevec.add(linelist[curline]);curline++; curlinepos = 0; }
			}
			
			
			
			String bankid="",bankname="";
			for (int i=0;retpara != null && i<retpara.length && linevec != null && i < linevec.size();i++)
			{
				String param = (String)linevec.elementAt(i);
				//System.out.println(param);
				// 配置%NONE%参数标识要放弃的返回项目
				if ("%CRC%".equalsIgnoreCase(retpara[i]) && !param.equals(bld.crc))
				{
					new MessageBox("交易应答校验码不匹配:param=" + param + " crc=" + bld.crc, null, false);
					return false;
				}
				else if ("%KYE%".equalsIgnoreCase(retpara[i])) bld.kye = ManipulatePrecision.div(Convert.toDouble(param.trim()), 100);
				else if ("%JE%".equalsIgnoreCase(retpara[i])) bld.je = ManipulatePrecision.div(Convert.toDouble(param.trim()), 100);
				else if ("%RETCODE%".equalsIgnoreCase(retpara[i])) bld.retcode = param.trim();
				else if ("%RETMSG%".equalsIgnoreCase(retpara[i])) bld.retmsg = param.trim();
				else if ("%CARDNO%".equalsIgnoreCase(retpara[i])) bld.cardno = param.trim();
				else if ("%SEQNO%".equalsIgnoreCase(retpara[i])) bld.trace = Convert.toLong(param);
				else if ("%BANKID%".equalsIgnoreCase(retpara[i])) bankid = param.trim();
				else if ("%BANKNAME%".equalsIgnoreCase(retpara[i])) bankname = param.trim();
				else if ("%TEMPSTR%".equalsIgnoreCase(retpara[i])) bld.tempstr = param.trim();
				else if ("%TEMPSTR1%".equalsIgnoreCase(retpara[i])) bld.tempstr1 = param.trim();
			}
			
			System.out.println("111----------------------------------");
			//
			if (bld.trace <= 0) bld.trace = bld.rowcode;
			
			// 如果没有配置bankid不赋值bankinfo
			if (bankid.length() > 0 && bankname.length() <= 0) bankname = XYKReadBankName(bankid);
			if (bankid.length() > 0 ) bld.bankinfo = bankid + "-" + bankname;
			if (bld.retcode.length() > 0 && bld.retmsg.length() <= 0) bld.retmsg = XYKReadRetMsg(bld.retcode);

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("读取交易应答数据异常!" + ex.getMessage(), null, false);
			ex.printStackTrace();

			return false;
		}
		finally
		{
			rtf.close();
		}
	}
	
	

	public void XYKPrintDoc()
	{
		ProgressBox pb = null;

		try
		{
			String file = getBankClassConfig("PRINTFILE");
			String CutFlg = getBankClassConfig("PRINTCUT");
			
			if (file == null || file.length() <= 0) return;
			if (!PathFile.fileExist(file))
			{
				new MessageBox("找不到签购单打印文件"+ file +"!");
				return;
			}
			
			pb = new ProgressBox();
			pb.setText("正在打印银联签购单,请等待...");
			
			int printnum = GlobalInfo.sysPara.bankprint;
			String encode = getBankClassConfig("PRINTENCODE");
			String strnum = getBankClassConfig("PRINTCOUNT");
			if (strnum != null && strnum.length() > 0) printnum = Convert.toInt(strnum);
			for (int i = 0; i < printnum; i++)
			{
				// 开始打印
				BufferedReader br = null;

				try
				{
					br = CommonMethod.readFile(file,encode);

					if (br == null)
					{
						new MessageBox("打开签购单打印文件失败"+ file + "!");

						return;
					}

					String line = null;
					while ((line = br.readLine()) != null)
					{
						if (line.trim().length() > 0)
						{
							if (CutFlg != null && (line.trim().equalsIgnoreCase(CutFlg)))
							{
								Printer.getDefault().cutPaper_Normal();
								continue;
							}
							Printer.getDefault().printLine_Normal(line);
						}
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
