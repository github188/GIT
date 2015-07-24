package custom.localize.Cbbh;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Cbbh_PaymentBankPlus extends Cbbh_PaymentBank {

	private static HashMap<String, String> mapPayMode = new HashMap<String,String>();
		
	static
	{
		BufferedReader br = null;
		String configName = GlobalVar.ConfigPath + "//CbbhBankWallet.ini";
		String line = null;
		br = CommonMethod.readFile(configName);
		String[] row = null;
		try {
			while ((line = br.readLine()) != null) {
				if (line.startsWith(";")) continue;
				row = line.split("=");
				if(row[0] == null || row[1] == null) continue;
				if(row[0].trim().toString().length() <= 4) continue;
				if(row[1].trim().toString().length() <= 4) continue;
				mapPayMode.put(row[0].trim().substring(0, 4), row[1].trim().toString().substring(0, 4));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void accountPay(boolean ret, BankLogDef bld, PaymentBankFunc pbf)
	{
		if (!ret)
		{
			// 交易失败，放弃付款对象
			salepay = null;
		}
		else
		{
			// 交易成功，记录交易数据到付款对象，batch必须不为空,标记付款已记账
			salepay.payno = bld.cardno;
			salepay.batch = String.valueOf(bld.trace);
			salepay.str1 = salepay.batch;
			if(bld.memo.length()<=0)
			{
				salepay.idno = salepay.batch;
			}
			else
			{
				salepay.idno = bld.authno+bld.memo;//参考号+行号
			}
			salepay.ybje = bld.je;
			salepay.je = ManipulatePrecision.doubleConvert(salepay.ybje * ((paymode != null) ? paymode.hl : 1));
			salepay.kye = bld.kye;
			salepay.memo = bld.memo;
			
			salepay.num6 = bld.ylzk;
			// 替换付款明细名称为银行名称
			if (bld.bankinfo != null)
			{
				String bankinfo = bld.bankinfo.trim();
				if (bankinfo.length() > 2 && pbf != null && pbf.getReplaceBankNameMode())
				{
					int p = bankinfo.indexOf("-");
					if (p > 0)
						salepay.payname = bankinfo.substring(p + 1);
					else
						salepay.payname = bankinfo;
				}
			}
			
			//把银行钱包折扣金额记录成单独的付款方式,如果失败则记录到当前付款方式上
			if(bld.ylzk > 0 && !createPaymentBankPlus(bld.ylzk))
				salepay.je += ManipulatePrecision.doubleConvert(bld.ylzk * ((paymode != null) ? paymode.hl : 1));;
		}

		// 更新付款断点数据，标记为已付款状态,否则在记账以后如果掉电,断点读入的还是未记账状态
		if (this.saleBS != null)
			this.saleBS.writeBrokenData();
	}
	
	
	public boolean createPaymentBankPlus(double value)
	{ 	
		String code = mapPayMode.get(paymode.code);
		if(code == null || code.length() <= 0) code = "";;
		
		PayModeDef pm = DataService.getDefault().searchPayMode(code);
		if(pm == null) pm = paymode;
		
		Cbbh_PaymentBankPlus pbp = new Cbbh_PaymentBankPlus();
		pbp.initPayment(pm, saleBS);
		
		if(pbp.createSalePay(String.valueOf(value)))
		{
			saleBS.addSalePayObject(pbp.salepay, pbp);
			return true;
		}
		
		return false;
	}
}
