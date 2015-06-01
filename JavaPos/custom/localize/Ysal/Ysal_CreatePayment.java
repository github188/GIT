package custom.localize.Ysal;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Bcrm.Bcrm_CreatePayment;

public class Ysal_CreatePayment extends Bcrm_CreatePayment
{
	public PaymentBankFunc getConfigBankFunc(String paycode)
	{

		if (ConfigClass.Bankfunc != null && ConfigClass.Bankfunc.trim().equals("BankfuncConfig"))
		{
			ConfigClass.Bankfunc = getBankfuncConfig();
		}

		if (ConfigClass.Bankfunc != null && ConfigClass.Bankfunc.length() > 0)
		{

			try
			{
				String conf[] = ConfigClass.Bankfunc.split("\\|");
				if (conf.length <= 1)
				{
					Class cl = bankClassName(ConfigClass.Bankfunc);
					if (cl != null)
					{
						PaymentBankFunc bank = (PaymentBankFunc) cl.newInstance();
						bank.readBankClassConfig(null);
						return bank;
					}
					else
					{
						if (PathFile.fileExist(GlobalVar.ConfigPath + "\\" + ConfigClass.Bankfunc + ".ini"))
						{
							PaymentBankFunc bank = new PaymentBankFunc();
							bank.readBankClassConfig(ConfigClass.Bankfunc);
							return bank;
						}
						else new MessageBox("支付对象 " + ConfigClass.Bankfunc + " 不存在\n\n" + "将生成默认付款对象进行交易处理");
					}
				}
				else
				{
					// 未指定付款代码则进行选择
					if (paycode == null || "".equals(paycode) || (ConfigClass.Bankfunc).indexOf("," + paycode) < 0)
					{
						Vector v = new Vector();
						for (int i = 0; i < conf.length; i++)
						{
							String[] s = conf[i].split(",");
							for (int j = 1; j < s.length; j++)
							{
								PayModeDef pm = DataService.getDefault().searchPayMode(s[j]);
								if (pm != null) v.add(new String[] { pm.code, pm.name });
								else v.add(new String[] { s[j], s[0] });
							}
						}
						if (v.size() > 1)
						{
							String[] title = { "付款代码", "付款名称" };
							int[] width = { 100, 400 };
							int choice = -1;
							do
							{
								choice = new MutiSelectForm().open("请选择第三方支付接口", title, width, v);
							} while (choice < 0);
							paycode = ((String[]) v.elementAt(choice))[0];
						}
					}

					// 根据付款代码确定银联对象
					String bankclass = null;
					for (int i = 0; paycode != null && i < conf.length; i++)
					{
						String bankinfo = conf[i] + ",";
						if ((bankinfo.indexOf("," + paycode + ",")) >= 0)
						{
							int j = bankinfo.indexOf(",");
							bankclass = bankinfo.substring(0, j);
							break;
						}
					}

					// 没有找到对应代码定义的银联对象则总是用第一个
					if (bankclass == null)
					{
						String bankinfo = conf[0] + ",";
						int j = bankinfo.indexOf(",");
						bankclass = bankinfo.substring(0, j);
					}

					// 创建银联对象
					Class cl = bankClassName(bankclass);
					if (cl != null)
					{
						PaymentBankFunc bank = (PaymentBankFunc) cl.newInstance();
						bank.paycode = paycode;
						bank.readBankClassConfig(null);
						return bank;
					}
					else
					{
						if (PathFile.fileExist(GlobalVar.ConfigPath + "\\" + bankclass + ".ini"))
						{
							PaymentBankFunc bank = new PaymentBankFunc();
							bank.paycode = paycode;
							bank.readBankClassConfig(bankclass);
							return bank;
						}
						else new MessageBox("支付对象 " + bankclass + " 不存在\n\n" + "将生成默认付款对象进行交易处理");
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();

				return null;
			}

		}

		return null;
	}

	private String getBankfuncConfig()
	{
		if (!(new File(GlobalVar.ConfigPath + "//BankfuncConfig.ini").exists())) return "";

		BufferedReader br;
		br = CommonMethod.readFile(GlobalVar.ConfigPath + "/BankfuncConfig.ini");
		if (br == null) return "";

		String line = null;
		String[] sp;
		try
		{
			while ((line = br.readLine()) != null)
			{
				if ((line == null) || (line.length() <= 0))
				{
					continue;
				}
				String[] lines = line.split("&&");
				sp = lines[0].split("=");
				if (sp.length < 2) continue;
				if (sp[0].trim().equals("BankFunc")&&sp[1].trim().length()>0)  return sp[1].trim();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally{
			try
			{
				br.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return "";

	}
}
