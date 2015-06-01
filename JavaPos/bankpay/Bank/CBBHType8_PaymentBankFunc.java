package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import org.eclipse.swt.widgets.Text;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateByte;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

//重百--店长卡，品类卡，类别卡（记账方式）
public class CBBHType8_PaymentBankFunc extends PaymentBankFunc
{
	String crc ;
	public String[] getFuncItem()
	{
		String[] func = new String[1];
		
		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
//		func[1] = "[" + PaymentBank.XYKCX + "]" + "当日撤消";
//		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		/*func[3] = "[" + PaymentBank.XYKQD + "]"	+ "交易签到";
		func[4] = "[" + PaymentBank.XYKJZ + "]" + "交易结算";
		func[5] = "[" + PaymentBank.XKQT1 + "]" + "重打结算单";  
		func[6] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
		func[7] = "[" + PaymentBank.XYKCD + "]" + "重打指定流水";
		func[8] = "[" + PaymentBank.XKQT2 + "]" + "分期消费";
		func[9] = "[" + PaymentBank.XKQT3 + "]" + "分期消费取消";*/
		
		return func;
	}
	
	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
         //		0-4对应FORM中的5个输入框
		//null表示该不用输入
		switch(type)
		{
			case PaymentBank.XYKXF : // 消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = "请 刷 卡";
				grpLabelStr[4] = "交易金额";
				break;
//			case PaymentBank.XYKCX : //消费撤销
//				grpLabelStr[0] = null;
//				grpLabelStr[1] = null;
//				grpLabelStr[2] = null;
//				grpLabelStr[3] = null;
//				grpLabelStr[4] = "交易金额";
//				break;
//			case PaymentBank.XYKTH :
//				grpLabelStr[0] = null;
//				grpLabelStr[1] = null;
//				grpLabelStr[2] = null;
//				grpLabelStr[3] = null;
//				grpLabelStr[4] = "交易金额";
//				break;
				/*case PaymentBank.XYKQD : //签到
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易签到";
				break;
			case PaymentBank.XYKJZ : //交易结账
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易结算";
				break;
			case PaymentBank.XKQT1 : //重打结算单
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打结算单";
				break;
			case PaymentBank.XYKYE : //查询余额
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "查询余额";
				break;
				
			case PaymentBank.XYKCD : //重打签购单
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打签购单";
				break;
			case PaymentBank.XKQT2 : //分期消费
				grpLabelStr[0] = null;//"分期期数";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "分期总金额";
				break;
			case PaymentBank.XKQT3 : //分期消费取消
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "分期消费取消";
				break;*/
		}
		
		return true;
	}
	
	public boolean getFuncText(int type, String[] grpTextStr)
	{
//		null表示必须用户输入,不为null表示缺省显示无需改变
		switch(type)
		{
			case PaymentBank.XYKXF: //消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
//			case PaymentBank.XYKCX: //消费撤销
//				grpTextStr[0] = null;
//				grpTextStr[1] = null;
//				grpTextStr[2] = null;
//				grpTextStr[3] = null;
//				grpTextStr[4] = null;
//				break;
//			case PaymentBank.XYKTH: //隔日退货
//				grpTextStr[0] = null;
//				grpTextStr[1] = null;
//				grpTextStr[2] = null;
//				grpTextStr[3] = null;
//				grpTextStr[4] = null;
//				break;
				/*	case PaymentBank.XYKQD: //交易签到
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易签到";
				break;
			case PaymentBank.XYKJZ: //交易结算
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易结算";
				break;
			case PaymentBank.XKQT1: //重打结算单
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始重打结算单";
				break;
			case PaymentBank.XYKYE: //查询余额
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始查询余额";
				break;
			case PaymentBank.XYKCD: //重打签购单
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始重打签购单";
				break;	
			case PaymentBank.XKQT2 : //分期消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始分期消费";
				break;
			case PaymentBank.XKQT3 : //分期消费取消
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键分期消费取消";
				break;*/
		}
		
		return true;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
//					卡号
				if(track2.indexOf("=") != -1)
				{
					bld.cardno = track2.substring(0,track2.indexOf("="));
				}
				else
				{
					bld.cardno = track2;
				}
					//金额
					bld.je = money;
					
					return true;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			XYKSetError("XX", "金卡异常XX" + e.getMessage());
			new MessageBox("调用金卡工程处理模块异常!!!\n" + e.getMessage() , null, false);
			
			return false;
		}
	}
	
	public boolean callBankFunc(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		boolean doClosePrint = false;

		try
		{
			// 规范数据
			if (track1 == null)
				track1 = "";
			if (track2 == null)
				track2 = "";
			if (track3 == null)
				track3 = "";
			if (oldseqno == null)
				oldseqno = "";
			if (oldauthno == null)
				oldauthno = "";
			if (olddate == null)
				olddate = "";

			// 写入请求数据日志
			if (!this.WriteRequestLog(type, money, oldseqno, oldauthno, olddate)) { return false; }

			// 调用金卡模块处理
			this.XYKExecute(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);
			
			/*// 写入应答数据日志
			this.WriteResultLog();

			// 写入单独进行的银联消费数据
			this.WriteSelfSaleData(memo);

			// 将交易日志发送网上
			this.BankLogSend();

			// 判断交易是否成功
			return checkBankSucceed();*/
			
			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("执行第三方支付接口异常!\n\n" + ex.getMessage());
			ex.printStackTrace();
			return false;
		}
	}

}