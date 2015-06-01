package custom.localize.Hzjb;

import java.sql.ResultSet;
import java.util.Vector;

import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.PublicMethod;
import com.efuture.javaPos.Struct.PayModeDef;

import custom.localize.Bcrm.Bcrm_AccessLocalDB;


public class Hzjb_AccessLocalDB extends Bcrm_AccessLocalDB
{
	public void paraInitDefault()
	{
		super.paraInitDefault();
	}
	
	public void paraConvertByCode(String code, String value)
    {
        super.paraConvertByCode(code, value);

        // 解百总是计算在打印小票时获取满送结果
        GlobalInfo.sysPara.calcfqbyreal = 'A';
        
        try
        {
            if (code.equals("HP"))
            {
                GlobalInfo.sysPara.customerUnpayment = value.trim();

                return;
            }

            if (code.equals("HI"))
            {
                GlobalInfo.sysPara.noprintCashier = value.trim();

                return;
            }
            
            if (code.equals("HQ"))
            {
            	GlobalInfo.sysPara.hdqrsCRM = value.trim();
            	return ;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    public boolean readPayMode(Vector v)
    {
        ResultSet rs = null;

        try
        {
            //
            PublicMethod.timeStart("正在读取本地付款方式表,请等待......");

            //在服务器端进行排序，此地不需要order by code
            rs = GlobalInfo.localDB.selectData("select * from PAYMODE");

            if (rs == null)
            {
                return false;
            }

            while (rs.next())
            {
                PayModeDef mode = new PayModeDef();

                if (!GlobalInfo.localDB.getResultSetToObject(mode))
                {
                    return false;
                }

                v.add(mode);
            }

            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return false;
        }
        finally
        {
            GlobalInfo.localDB.resultSetClose();

            //
            PublicMethod.timeEnd("读取本地付款方式表耗时: ");
        }
    }
}
