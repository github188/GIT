package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Payment.PaymentBank;

/**
 * 哈尔滨盛恒基，银行程序，退货不输入检索号，且不记录银行行号
 * @author Administrator
 *
 */
public class HzxlSHJ_PaymentBankFunc extends HzxlJX_PaymentBankFunc
{
	
	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		//0-4对应FORM中的5个输入框
		//null表示该不用输入
		switch (type)
		{
			case PaymentBank.XYKXF://消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKCX://消费撤销
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKTH://隔日退货   
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = "原交易日";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKYE://余额查询    
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "余额查询";
				break;
			case PaymentBank.XYKCD://签购单重打
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打签单";
				break;
			case PaymentBank.XKQT1://分期付款
				grpLabelStr[0] = null;
				grpLabelStr[1] = "分期编号";
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
            case PaymentBank.XYKJZ: //结账
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "结账";
                break;
			default:
				return false;
		}

		return true;
	}
	public boolean XYKReadResult()
	{
		BufferedReader br = null;

		try
		{
			if (!PathFile.fileExist("C:\\JavaPos\\result.txt") || ((br = CommonMethod.readFileGBK("C:\\JavaPos\\result.txt")) == null))
			{
				XYKSetError("XX", "读取金卡工程应答数据失败,文件result.txt不存在!");
				new MessageBox("读取金卡工程应答数据失败\n请联系信息部确定当前交易是否成功!", null, false);

				return false;
			}

			String line = br.readLine();

			if (line.length() <= 0) { return false; }
			
			if (line.indexOf(",") < 0) { return false; }
			
			line = line.split(",")[1];
			
			line = "**" + line;

			bld.retcode = Convert.newSubString(line, 2, 8).trim();

			if (!bld.retcode.equals("000000"))
			{
				bld.retmsg = Convert.newSubString(line, 8, 48).trim();
				return true;
			}

			if(line.length()>47)bld.retmsg = Convert.newSubString(line, 8, 48).trim();

			int type = Integer.parseInt(bld.type.trim());

	        // 消费，消费撤销，重打签购单
	        if (type == PaymentBank.XYKJZ)
	        {
	        	return true;
	        }
	        	
			if ((line.length()>53) && Convert.newSubString(line, 48, 54).length() > 0)
			{
				bld.trace = Long.parseLong(Convert.newSubString(line, 48, 54).trim());
			}

			if(line.length()>84) bld.cardno = Convert.newSubString(line, 66, 85).trim();

			//if(line.length()>90)bld.bankinfo = Convert.newSubString(line, 89, 91) + XYKReadBankName(Convert.newSubString(line, 89, 91).trim());

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

					if (PathFile.fileExist("C:\\JavaPos\\request.txt"))
					{
						PathFile.deletePath("C:\\JavaPos\\request.txt");
					}

					if (PathFile.fileExist("C:\\JavaPos\\result.txt"))
					{
						PathFile.deletePath("C:\\JavaPos\\result.txt");
					}

				}
				catch (IOException e)
				{
					new MessageBox("result.txt 关闭失败\n重试后如果仍难失败，请联系信息部");
					e.printStackTrace();
				}
			}
		}
	}
}
