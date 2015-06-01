package custom.localize.Hzjb;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;


public class Hzjb_PaymentZZQ extends PaymentMzk
{
	public Hzjb_PaymentZZQ()
	{}
	
    public Hzjb_PaymentZZQ(PayModeDef mode, SaleBS sale)
    {
        initPayment(mode, sale);
    }

    public Hzjb_PaymentZZQ(SalePayDef pay, SaleHeadDef head)
    {
        initPayment(pay, head);
    }

    public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
    {
        return DataService.getDefault().sendFjkSale(req, ret);
    }

    public boolean isReceive(SaleGoodsDef sgd, GoodsDef gdf)
    {
        if ((gdf.str2 != null) && (gdf.str2.split(",").length >= 2))
        {
            String[] rows = gdf.str2.split(",");

            if ((rows[1] != null) && (rows[1].charAt(0) != 'Y'))
            {
                return false;
            }

            double zkl = 0;

            if (rows.length == 3)
            {
                zkl = Convert.toDouble(rows[2]);
            }

            if (ManipulatePrecision.doubleConvert(sgd.hjje - sgd.hjzk + sgd.zszke) < ManipulatePrecision.doubleConvert(sgd.hjje * zkl))
            {
                return false;
            }
        }

        return true;
    }

    protected String getDisplayStatusInfo()
    {
        calcFjkMaxJe();

        String line = "最大收券金额为 :" + ManipulatePrecision.doubleToString(allowpayje) + " 元";

        return line;
    }

	public boolean checkMzkMoneyValid()
	{
		if (mzkreq.track2.equals("0000"))
		{
			return true;
		}
		
		if (super.checkMzkMoneyValid())
		{
			if (allowpayje == 0)
			{
				new MessageBox("最大收增值券金额为 0 元，不能使用增值券！");
				return false;
			}
			// 检查金额是否超过卡余额
			if (salepay.je > allowpayje)
			{
				new MessageBox("输入金额大于最大收券额，不能使用增值券！");
				return false;
			}
			
			if (mzkret.ye > salepay.ybje)
			{
				if (!messDisplay || new MessageBox(salepay.payname + "的每张券必须一次性付完!\n是否将剩余部分计入损溢？",null,true).verify() == GlobalVar.Key1)
				{
					// num1记录券付款溢余部分
					salepay.num1 = ManipulatePrecision.sub(ManipulatePrecision.mul(mzkret.ye, salepay.hl), Math.min(salepay.je,allowpayje));
					salepay.ybje = mzkret.ye;
					salepay.je = ManipulatePrecision.doubleConvert(salepay.ybje * salepay.hl, 2,1);

					return true;
				}
				else
				{
					return false;
				}
			}
			
			return true;
		}
		
		return false;
	}

    //判断是否是返券卡
    public boolean isCzFile(String filename)
    {
        if (filename.startsWith("Fjk_") && filename.endsWith(".cz"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public String GetMzkCzFile()
    {
        return ConfigClass.LocalDBPath + "/Fjk_" + mzkreq.seqno + ".cz";
    }

    public int getAccountInputMode()
    {
        return TextBox.IntegerInput;
    }

    protected String getDisplayAccountInfo()
    {
        return "请输入券";
    }

    public boolean calcFjkMaxJe()
    {
        //计算最大能收券金额
        double je = 0;

        for (int k = 0; k < saleBS.saleGoods.size(); k++)
        {
            SaleGoodsDef sgd = (SaleGoodsDef) saleBS.saleGoods.get(k);
            GoodsDef gd = (GoodsDef) saleBS.goodsAssistant.get(k);

            sgd.str4 = "N";

            if (!isReceive(sgd, gd))
            {
                continue;
            }

            sgd.str4 = "Y";
            je += ManipulatePrecision.doubleConvert(sgd.hjje - sgd.hjzk);
        }

        String s = getFjkPayTotal(saleBS.salePayment);

        je = ManipulatePrecision.doubleConvert(je - Double.parseDouble(s));

        if (je < 0)
        {
            je = 0;
        }

        allowpayje = je;

        return true;
    }

    public String getFjkPayTotal(Vector salepay)
    {
        SalePayDef spd = null;
        double dyfjea = 0;

        for (int i = 0; i < salepay.size(); i++)
        {
            spd = (SalePayDef) salepay.get(i);

            if (((Hzjb_CreatePayment) CreatePayment.getDefault()).isPaymentZZQ(spd.paycode))
            {
                dyfjea = ManipulatePrecision.doubleConvert((dyfjea + spd.je) - spd.num1, 2, 1);
            }
        }

        return ManipulatePrecision.doubleToString(dyfjea);
    }
}
