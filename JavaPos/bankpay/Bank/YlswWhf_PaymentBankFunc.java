package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

public class YlswWhf_PaymentBankFunc extends PaymentBankFunc
{
    public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
    {
	// PaymentBank.XYKXF: // 消费
	// PaymentBank.XYKCX: // 消费撤销
	// PaymentBank.XYKTH: // 隔日退货
	// PaymentBank.XYKYE: // 余额查询
	// PaymentBank.XYKCD: // 重打签购单
	// PaymentBank.XYKQD: // 交易签到
	// PaymentBank.XYKJZ: // 交易结帐
	
	try
	{
	    if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKCD) && (type != PaymentBank.XYKQD) && (type != PaymentBank.XYKJZ))
	    {
		errmsg = "银联接口不支持该交易";
		new MessageBox(errmsg);
		
		return false;
	    }
	    
	    // 先删除上次交易数据文件
	    if (PathFile.fileExist("C:\\gmc\\request.txt"))
	    {
		PathFile.deletePath("C:\\gmc\\request.txt");
		
		if (PathFile.fileExist("C:\\gmc\\request.txt"))
		{
		    errmsg = "交易请求文件request.txt无法删除,请重试";
		    XYKSetError("XX", errmsg);
		    new MessageBox(errmsg);
		    return false;
		}
	    }
	    
	    if (PathFile.fileExist("C:\\gmc\\result.txt"))
	    {
		PathFile.deletePath("C:\\gmc\\result.txt");
		
		if (PathFile.fileExist("C:\\gmc\\result.txt"))
		{
		    errmsg = "交易请求文件result.txt无法删除,请重试";
		    XYKSetError("XX", errmsg);
		    new MessageBox(errmsg);
		    return false;
		}
	    }
	    
	    // 写入请求数据
	    if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
	    {
		return false;
	    }
	    
	    if (bld.retbz != 'Y')
	    {
		// 调用接口模块
		if (PathFile.fileExist("C:\\gmc\\javaposbank.exe"))
		{
		    CommonMethod.waitForExec("C:\\gmc\\javaposbank.exe YLSWWHF");
		}
		else
		{
		    new MessageBox("找不到金卡工程模块 javaposbank.exe");
		    XYKSetError("XX", "找不到金卡工程模块 javaposbank.exe");
		    return false;
		}
		
		// 读取应答数据
		if (!XYKReadResult())
		{
		    return false;
		}
		
		// 检查交易是否成功
		XYKCheckRetCode();
	    }
	    
	    // 打印签购单
	    if (XYKNeedPrintDoc())
	    {
		
		XYKPrintDoc();
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
    
    public boolean XYKWriteRequest(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
    {
	String line = "";
	String mtype1 = "";
	PrintWriter pw = null;
	
	try
	{
	    switch (type)
	    {
		case PaymentBank.XYKXF: // 消费
		    mtype1 = "00";
		    break;
		case PaymentBank.XYKCX: // 消费撤销
		    mtype1 = "01";
		    break;
		case PaymentBank.XYKTH: // 隔日退货
		    mtype1 = "02";
		    break;
		case PaymentBank.XYKYE: // 余额查询
		    mtype1 = "03";
		    bld.je = 0.0; //避免收银员输入速度过快，导致金额框中输入的是卡号，从而使金额字段过长上传失败（后台金额长度为【12，2】）
		    break;
		case PaymentBank.XYKCD: // 重打签购单
		    mtype1 = "04";
		    break;
		case PaymentBank.XYKQD: // 交易签到
		    mtype1 = "05";
		    break;
		case PaymentBank.XYKJZ: // 交易结帐
		    mtype1 = "06";
		    break;
		default:
		    return false;
	    }
	    
	    // ’00’-消费|’01’-撤销|’02’-退货|’03’-查余额|‘04’重打指定流水|‘05’签到|‘06’结算
	    // 收银机号(8-左对齐,右补空格)
	    // +操作员号(8-左对齐，右补空格)
	    // +交易类型(2)
	    // +金额(12-无小数点，左补0保留两个小数)
	    // +原交易日期(8-YYYYMMDD,退货时填写)
	    // +原系统参考号(12-退货时用)
	    // +原流水号(6-右对齐，左补0)
	    // +二磁道数据(37-左对齐,右补空格)
	    // +三磁道数据(104-左对齐,右补空格)
	    // +交易校验数据(3位，从0~9的随机字符)
	    
	    // 收银机号
	    String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode, ' ', 8);
	    // 收银员号
	    String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 8);
	    // 交易类型
	    String type1 = mtype1;
	    // 金额
	    String jestr = Convert.increaseCharForward(String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1)), '0', 12);
	    // 原交易日期
	    String olddate1 = Convert.increaseChar(olddate, ' ', 8);
	    // 原系统参考号
	    String oldsysref = "";
	    oldsysref = Convert.increaseChar(oldsysref, ' ', 12);
	    // 原流水号
	    String oldseqno1 = Convert.increaseCharForward(oldseqno, '0', 6);
	    // 二磁道数据
	    String track21 = Convert.increaseChar(track2, ' ', 37);
	    // 三磁道数据
	    String track31 = Convert.increaseChar(track3, ' ', 104);
	    
	    // 校验位
	    String crc = XYKGetCRC();
	    
	    line = syjh + syyh + type1 + jestr + olddate1 + oldsysref + oldseqno1 + track21 + track31 + crc;
	    
	    bld.crc = crc;
	    bld.type = type1;
	    
	    try
	    {
		pw = CommonMethod.writeFile("C:\\gmc\\request.txt");
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
    
    public boolean XYKReadResult()
    {
	BufferedReader br = null;
	
	try
	{
	    if (!PathFile.fileExist("C:\\gmc\\result.txt") || ((br = CommonMethod.readFileGBK("C:\\gmc\\result.txt")) == null))
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
		return false;
	    
	    if (result.length < 2)
		return false;
	    
	    if (result.length > 2)
	    {
		for (int i = 2; i < result.length; i++)
		{
		    result[1] = result[1] + "," + result[i];
		}
	    }
	    
	    bld.retcode = Convert.newSubString(result[1], 0, 2);
	    bld.bankinfo = Convert.newSubString(result[1], 2, 6) + XYKReadBankName(Convert.newSubString(result[1], 2, 6));;
	    bld.cardno = Convert.newSubString(result[1], 6, 26);
	    
	    if (Convert.newSubString(result[1], 30, 36).trim().length() > 0)
	    {
		// 当使用重打印小票时,30到36会显示出EEEEEE
		try
		{
		    bld.trace = Long.parseLong(Convert.newSubString(result[1], 30, 36).trim());
		}
		catch (Exception er)
		{
		    er.printStackTrace();
		    bld.trace = -1;
		}
	    }
	    
	    if (Convert.newSubString(result[1], 36, 48).trim().length() > 0)
	    {
		// 当使用重打印小票时,30到36会显示出EEEEEE
		try
		{
		    bld.je = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Double.parseDouble(Convert.newSubString(result[1], 36, 48)), 100), 2, 1);
		}
		catch (Exception er)
		{
		    er.printStackTrace();
		    bld.je = 0;
		}
	    }
	    
	    bld.retmsg = Convert.newSubString(result[1], 48, 88).trim();
	    
	    String CRC = Convert.newSubString(result[1], 88, 91).trim();
	    if (!CRC.trim().equals(bld.crc))
	    {
		errmsg = "返回效验码" + CRC + "同原始效验码" + bld.crc + "不一致";
		XYKSetError("XX", errmsg);
		new MessageBox(errmsg);
		try
		{
		    // 保存返回文件
		    ManipulateDateTime mdt = new ManipulateDateTime();
		    String date = GlobalInfo.balanceDate.replaceAll("/", "");
		    String name = ConfigClass.LocalDBPath + "Invoice//" + date + "//" + mdt.getTime().replaceAll(":", "_") + "_Bank.txt";
		    PathFile.copyPath("C:\\gmc\\result.txt", name);
		}
		catch (Exception er)
		{
		    er.printStackTrace();
		}
		return false;
	    }
	    
	    errmsg = bld.retmsg;
	    
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
		    
		    if (PathFile.fileExist("C:\\gmc\\request.txt"))
		    {
			PathFile.deletePath("C:\\gmc\\request.txt");
		    }
		    
		    if (PathFile.fileExist("C:\\gmc\\result.txt"))
		    {
			PathFile.deletePath("C:\\gmc\\result.txt");
		    }
		}
		catch (IOException e)
		{
		    e.printStackTrace();
		}
	    }
	}
    }
    
    public boolean XYKNeedPrintDoc()
    {
	if (!checkBankSucceed())
	{
	    return false;
	}
	
	int type = Integer.parseInt(bld.type.trim());
	
	// 消费，消费撤销，隔日退货，重打签购单
	if ((type == PaymentBank.XYKXF) || (type == PaymentBank.XYKCX) || (type == PaymentBank.XYKTH) || (type == PaymentBank.XYKCD) || (type == PaymentBank.XYKJZ))
	{
	    return true;
	}
	else
	{
	    return false;
	}
    }
    
    public void XYKPrintDoc()
    {
	ProgressBox pb = null;
	
	String printName = "C:\\gmc\\receipt.TXT";
	try
	{
	    if (!PathFile.fileExist(printName))
	    {
		new MessageBox("找不到签购单打印文件!");
		
		return;
	    }
	    
	    pb = new ProgressBox();
	    pb.setText("正在打印银联签购单,请等待...");
	    
	    for (int i = 0; i < GlobalInfo.sysPara.bankprint; i++)
	    {
		BufferedReader br = null;
		
		XYKPrintDoc_Start();
		
		try
		{
		    //
		    br = CommonMethod.readFileGBK(printName);
		    
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
			
			XYKPrintDoc_Print(line + "\n");
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
		
		XYKPrintDoc_End();
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
	    
	    if (PathFile.fileExist(printName))
	    {
		PathFile.deletePath(printName);
	    }
	}
    }
}
