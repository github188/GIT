package custom.localize.Wjjy;

import java.util.Vector;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bhls.Bhls_SaleBillMode;


public class Wjjy_SaleBillMode extends Bhls_SaleBillMode
{
    protected final int SBM_hjzje = 40; //合计总金额
    protected final int SBM_kye = 41; //卡余额

    public Vector convertPayDetail(Vector p)
    {
        return p;
    }

    protected String extendCase(PrintTemplateItem item, int index)
    {
        String line = null;

        if (Integer.parseInt(item.code) == SBM_hjzke)
        {
            if (salehead.hjzke == 0)
            {
                line = "0.00";
            }
            else
            {
                line = ManipulatePrecision.doubleToString(salehead.hjzke);
            }
        }
        else if (Integer.parseInt(item.code) == SBM_hjzje)
        {
            if (salehead.hjzje == 0)
            {
                line = "0.00";
            }
            else
            {
                line = ManipulatePrecision.doubleToString(salehead.hjzje);
            }
        }
        else if (Integer.parseInt(item.code) == SBM_kye)
        {
            double kye = ((SalePayDef) salepay.elementAt(index)).kye;

            if (kye == 0)
            {
                line = "";
            }
            else
            {
                line = ManipulatePrecision.doubleToString(kye);
            }
        }

        return line;
    }

    public void printPay()
    {
        // 设置打印区域
        setPrintArea("Pay");

        // 循环打印付款明细
        for (int i = 0; i < salepay.size(); i++)
        {
            printVector(getCollectDataString(Pay, i, Width));
        }
    }

    protected void printAppendBill()
    {
        // 打印面值卡联
        printMZKBill(1);
    }
}
