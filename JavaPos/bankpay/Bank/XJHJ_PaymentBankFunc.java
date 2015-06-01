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
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;

public class XJHJ_PaymentBankFunc extends ABACUS_PaymentBankFunc {
	public String[] getFuncItem() {
		
		String[] func = new String[3];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKTH + "]" + "退货";
		func[2] = "[" + PaymentBank.XYKYE + "]" + "余额查询";

		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr) {
		switch (type) {
		case PaymentBank.XYKXF: // 消费
			grpLabelStr[0] = null;
			grpLabelStr[1] = null;
			grpLabelStr[2] = null;
			grpLabelStr[3] = null;
			grpLabelStr[4] = "交易金额";
			break;
		
		  case PaymentBank.XYKTH: // 退货 
			  grpLabelStr[0] = null; 
			  grpLabelStr[1] = null; 
			  grpLabelStr[2] = null; 
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
		}

		return true;
	}

	public boolean getFuncText(int type, String[] grpTextStr) {
		switch (type) {
		case PaymentBank.XYKXF: // 消费
			grpTextStr[0] = null;
			grpTextStr[1] = null;
			grpTextStr[2] = null;
			grpTextStr[3] = null;
			grpTextStr[4] = null;
			break;
			
		case PaymentBank.XYKTH: // 退货 
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
		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1,
			String track2, String track3, String oldseqno, String oldauthno,
			String olddate, Vector memo) {
		try {
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKTH)) {
				errmsg = "会员证接口不支持该交易";
				new MessageBox(errmsg);

				return false;
			}

			// 先删除上次交易数据文件
			if (PathFile.fileExist("c:\\JavaPOS\\request.txt")) {
				PathFile.deletePath("c:\\JavaPOS\\request.txt");

				if (PathFile.fileExist("c:\\JavaPOS\\request.txt")) {
					errmsg = "交易请求文件request.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist("c:\\JavaPOS\\response.txt")) {
				PathFile.deletePath("c:\\JavaPOS\\response.txt");

				if (PathFile.fileExist("c:\\JavaPOS\\response.txt")) {
					errmsg = "交易请求文件result.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			// 写入请求数据
			if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno,
					oldauthno, olddate, memo)) {
				return false;
			}

			if (bld.retbz != 'Y') {

				// 调用接口模块
				if (PathFile.fileExist("c:\\JavaPOS\\nohookczksal.exe")) {
					CommonMethod.waitForExec("c:\\JavaPOS\\nohookczksal.exe");
				} else {
					new MessageBox("找不到金卡工程模块 nohookczksal.exe");
					XYKSetError("XX", "找不到金卡工程模块 nohookczksal.exe");
					return false;
				}

				// 读取应答数据
				if (!XYKReadResult()) {
					return false;
				}

				// 检查交易是否成功
				XYKCheckRetCode();
			}

			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	public boolean XYKWriteRequest(int type, double money, String track1,
			String track2, String track3, String oldseqno, String oldauthno,
			String olddate, Vector memo) {
		String line = "";

		String saletype = "";
		try {
			switch (type) {
			case PaymentBank.XYKXF:
				saletype = "S";
				break;
			case PaymentBank.XYKYE:
				saletype = "Q";
				break;
			case PaymentBank.XYKTH: 
				saletype = "V"; 
				break;
			
			}

			line = "00"
					+ ","
					+ Convert.increaseChar(track2, ' ', 200).trim()+","
					+ saletype
					+ ","
					+ Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 6)
					+ ","
					+ Convert.increaseChar(ConfigClass.CashRegisterCode, ' ', 6)
					+ "," + Convert.increaseCharForward(money+"", ' ', 12) + ","
					+ "000000";

			PrintWriter pw = null;

			try {
				pw = CommonMethod.writeFile("c:\\JavaPOS\\request.txt");

				if (pw != null) {
					pw.println(line);
					pw.flush();
				}
			} finally {
				if (pw != null) {
					pw.close();
					pw = null;
				}
			}

			// new MessageBox("请拷备request.txt文件!");
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();

			return false;
		}
	}


	public boolean XYKReadResult() {
//		if (bld.type.equals(String.valueOf(PaymentBank.XYKYE))) {
//			return true;
//		}
		
		BufferedReader br = null;

		try {
			if (!PathFile.fileExist("c:\\JavaPOS\\response.txt")
					|| ((br = CommonMethod
							.readFileGBK("c:\\JavaPOS\\response.txt")) == null)) {
				XYKSetError("XX", "读取会员证工程应答数据失败!");
				new MessageBox("读取会员证应答数据失败!", null, false);

				return false;
			}

			String line = br.readLine();
			String[] a = line.split(",");

			//响应码
			bld.retcode = a[0];
			
			//文本信息
			bld.retmsg = a[1];
			if (bld.retcode.equals("00"))
			{
				bld.retmsg = "银联交易成功";
				errmsg = bld.retmsg;
				
			}
			else
			{
				errmsg = bld.retmsg;
				new MessageBox(errmsg);
				return false;
			}
			//状态
			//收银员
			//收银台
			//商户号
			//会员证卡面卡号
			bld.cardno = a[6];
//			new MessageBox("返回码："+bld.cardno);
			//会员证卡主
			//会员证卡类别
			//会员证消费金额
			bld.je =ManipulatePrecision.doubleConvert( Convert.toDouble(a[9]));
//			new MessageBox("金额："+bld.je+"");
			//会员证当前余额
			bld.kye =ManipulatePrecision.doubleConvert( Convert.toDouble(a[10]));
//			new MessageBox("卡余额："+bld.kye+"");
			//流水号
			bld.trace =Convert.toLong(a[11]);
			//备用
			//交易时间
			return true;
		} catch (Exception ex) {
			XYKSetError("XX", "读取应答XX:" + ex.getMessage());
			new MessageBox("读取会员证应答数据异常!" + ex.getMessage(), null, false);
			ex.printStackTrace();

			return false;
		} finally {
			if (br != null) {
				try {
					br.close();

					if (PathFile.fileExist("c:\\JavaPOS\\request.txt")) {
						PathFile.deletePath("c:\\JavaPOS\\request.txt");
					}

					if (PathFile.fileExist("c:\\JavaPOS\\response.txt")) {
						PathFile.deletePath("c:\\JavaPOS\\response.txt");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean XYKCheckRetCode() {
		if (bld.retcode.trim().equals("00")) {
			bld.retbz = 'Y';

			return true;
		} else {
			bld.retbz = 'N';

			return false;
		}
	}
}
