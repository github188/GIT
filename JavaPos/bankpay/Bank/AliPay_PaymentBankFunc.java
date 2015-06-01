package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Vector;

import org.eclipse.osgi.internal.verifier.Base64;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.SaleGoodsDef;

public class AliPay_PaymentBankFunc extends PaymentBankFunc
{
	private SaleBS saleBS;

	public String[] getFuncItem()
	{
		String[] func = new String[1];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		// 0-4对应FORM中的5个输入框
		// null表示该不用输入
		switch (type)
		{
			case PaymentBank.XYKXF: // 消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";

				break;
		}
		return true;
	}

	public boolean getFuncText(int type, String[] grpTextStr)
	{
		// 0-4对应FORM中的5个输入框
		// null表示该需要用户输入,不为null用户不输入
		switch (type)
		{
			case PaymentBank.XYKXF: // 消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;

				break;
		}
		return true;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if (PathFile.fileExist("c:\\javapos\\alireq.txt"))
			{

				PathFile.deletePath("c:\\javapos\\alireq.txt");

				if (PathFile.fileExist("c:\\javapos\\alireq.txt"))
				{
					errmsg = "交易请求文件alireq.TXT无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist("c:\\javapos\\aliret.txt"))
			{
				PathFile.deletePath("c:\\javapos\\aliret.txt");
				if (PathFile.fileExist("c:\\javapos\\aliret.txt"))
				{
					errmsg = "交易请求文件aliret.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			if (memo.size() >= 2)
				saleBS = (SaleBS) memo.elementAt(2);

			// 写入请求数据
			if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo)) { return false; }
			if (bld.retbz != 'Y')
			{

				// 调用接口模块 c:\javapos 目录
				if (PathFile.fileExist("c:\\javapos\\alipay.exe"))
				{
					CommonMethod.waitForExec("c:\\javapos\\alipay.exe ALIPAY", "alipay.exe");
				}
				else
				{
					new MessageBox("找不到金卡工程模块 alipay.exe");
					XYKSetError("XX", "找不到金卡工程模块 alipay.exe");
					return false;
				}

				// 读取应答数据
				if (!XYKReadResult()) { return false; }

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

	private String getKeyString(String data)
	{
		String retString = "";
		try
		{
			new MessageBox("jsongg1");
			byte[] privateKey = CommonMethod.readFile("c:\\key.txt").readLine().getBytes();
			byte[] plainText = data.getBytes("UTF8");

			new MessageBox("jsongg2");
			byte[] byKey = Base64.decode(privateKey);
			new MessageBox("jsongg20");
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(byKey);
			new MessageBox("jsongg21");
			KeyFactory keyf = KeyFactory.getInstance("RSA");
			new MessageBox("jsongg22");
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);

			new MessageBox("jsongg3");
			java.security.Signature signature = java.security.Signature.getInstance("SHA1WithRSA");

			signature.initSign(priKey);
			signature.update(plainText);
			new MessageBox("jsongg4");
			byte[] signed = signature.sign();

			retString = new String(Base64.encode(signed), "UTF-8");
			System.out.println(retString);
			return retString;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return "";
		}
	}

/*	public static void main(String[] args)
	{
		AliPay_PaymentBankFunc pay = new AliPay_PaymentBankFunc();
		pay.XYKReadResult();
	}*/

	public boolean XYKWriteRequest(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{

			// {"service":"trade.create","partner":"2088302798800692","charset":"GBK","signType":"RSA","outTradeNo":"20121127266382817409",
			// "amount":"0.01","realAmount":"0.01","subject":"super market","subGoodDetails":[{"goodsName":"aa","price":"0.01","count":"1"}],
			// "tradeExpired":"3"}

			JSONObject param = new JSONObject();

			JSONObject content = new JSONObject();
			content.put("service", "trade.create");
			// content.put("partner", "2088302798800692");
			content.put("charset", "GBK");
			// content.put("signType", "RSA");

			Date myDate = new Date(System.currentTimeMillis());
			String time = new SimpleDateFormat("yyyyMMddhhmm").format(myDate);

			content.put("outTradeNo", GlobalInfo.syjDef.syjh + String.valueOf(GlobalInfo.syjStatus.fphm) + time);
			content.put("amount", String.valueOf(money));
			content.put("realAmount", String.valueOf(money));
			content.put("subject", "商户消费");

			JSONArray jsonarray = new JSONArray();

			for (int i = 0; i < saleBS.saleGoods.size(); i++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) saleBS.saleGoods.get(i);
				JSONObject goods = new JSONObject();
				goods.put("goodsName", sgd.name);
				goods.put("price", String.valueOf(sgd.jg));
				goods.put("count", String.valueOf(sgd.sl));

				jsonarray.add(goods);
			}

			content.put("subGoodDetails", jsonarray);
			content.put("tradeExpired", "3");

			// String key = getKeyString(content.toString());

			param.put("params", content);

			// param.put("sign", key);

			System.out.println(param.toString());

			String line = param.toString();

			// 根据不同的类型生成文本结构
			switch (type)
			{
				case PaymentBank.XYKXF:// 消费：调用的主功能号4右对齐 + 调用的副功能号4右对齐 +
										// 金额12.2右对齐 + 二磁道37左对齐 + 三磁道104左对齐
					line = "01#" + line;

					break;

			}

			PrintWriter pw = null;

			try
			{
				pw = CommonMethod.writeFile("c:\\javapos\\alireq.txt");
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
			if (!PathFile.fileExist("c:\\JavaPOS\\aliret.txt") || ((br = CommonMethod.readFileGBK("c:\\JavaPOS\\aliret.txt")) == null))
			{
				XYKSetError("XX", "读取金卡工程应答数据失败!");
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return false;
			}

			String line = br.readLine();

			if (line == null || line.equals(""))
			{
				new MessageBox("支付宝返回数据有误");
				return false;
			}

			JSONObject content = JSONObject.fromObject(line);
			String result = content.getString("result");
			String resultCode = content.getString("resultCode");
			String resultDesc = content.getString("resultDesc");

			// AlipayResult result = (AlipayResult) JSONObject.toBean(content,
			// AlipayResult.class);

			if (resultCode.equals("TRADE_SUCCESS"))
			{
				bld.retcode = "00";

				if (result.length() > 0)
				{
					try
					{
						content = JSONObject.fromObject(result);
						String outTradeNo = content.getString("outTradeNo");
						
						if(outTradeNo !=null)
							bld.authno =outTradeNo;
					
						
						String partner = content.getString("partner");
						if(partner !=null)
							bld.memo = partner;
						
						String tradeId = content.getString("tradeId");
						if(tradeId!=null)
							bld.memo1 = tradeId;
						
						String tradeNo = content.getString("tradeNo");
						if(tradeNo !=null)
							bld.memo2 = tradeNo;
						
						String tradeStatus = content.getString("tradeStatus");
						if(tradeStatus!=null)
							bld.crc = tradeStatus;

					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
			else
			{
				bld.retcode = "99";
				bld.retmsg = resultDesc;
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
					br = null;
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public static Object getDTO(String jsonString, Class clazz, Map map)
	{
		JSONObject jsonObject = null;
		try
		{

			jsonObject = JSONObject.fromObject(jsonString);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return JSONObject.toBean(jsonObject, clazz, map);

	}

	public class Content
	{
		private String outTradeNo;
		private String partner;
		private String tradeId;
		private String tradeNo;
		private String tradeStatus;

		public String getOutTradeNo()
		{
			return outTradeNo;
		}

		public void setOutTradeNo(String outTradeNo)
		{
			this.outTradeNo = outTradeNo;
		}

		public String getPartner()
		{
			return partner;
		}

		public void setPartner(String partner)
		{
			this.partner = partner;
		}

		public String getTradeId()
		{
			return tradeId;
		}

		public void setTradeId(String tradeId)
		{
			this.tradeId = tradeId;
		}

		public String getTradeNo()
		{
			return tradeNo;
		}

		public void setTradeNo(String tradeNo)
		{
			this.tradeNo = tradeNo;
		}

		public String getTradeStatus()
		{
			return tradeStatus;
		}

		public void setTradeStatus(String tradeStatus)
		{
			this.tradeStatus = tradeStatus;
		}
	}
}
