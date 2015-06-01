package custom.localize.Bjys;

import java.sql.ResultSet;

import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.PublicMethod;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.CustomerTypeDef;


public class Bjys_AccessBaseDB extends AccessBaseDB
{
    public boolean getCustomer(CustomerDef cust, String track)
    {
        ResultSet rs = null;

        try
        {
            PublicMethod.timeStart("正在查询本地顾客库,请等待......");

            //@代表以手机号的方式查询
            if (track.charAt(0) == '@')
            {
                rs = GlobalInfo.baseDB.selectData("select * from CUSTOMER where str1 = '" + track.substring(1) + "'");
            }
            else
            {
                rs = GlobalInfo.baseDB.selectData("select * from CUSTOMER where code = '" + track + "'");
            }

            if (rs == null)
            {
                return false;
            }
            
            if (!rs.next())
            {
            	GlobalInfo.baseDB.resultSetClose();
            	
                rs = GlobalInfo.baseDB.selectData("select * from CUSTOMER where track = '" + track + "'");
                if (rs == null)
                {
                	return false;
                }
                else
                {
    	            if (!rs.next())
    	            {
    	            	return false;
    	            }
                }
            }
            
           
            if (!GlobalInfo.baseDB.getResultSetToObject(cust))
            {
                return false;
            }

            GlobalInfo.baseDB.resultSetClose();

            // 查找类型
            rs = GlobalInfo.localDB.selectData("select * from CUSTOMERTYPE where code = '" + cust.type + "'");

            if (rs == null)
            {
                return false;
            }

            if (rs.next())
            {
                CustomerTypeDef type = new CustomerTypeDef();

                if (!GlobalInfo.localDB.getResultSetToObject(type))
                {
                    return false;
                }

                cust.ishy    = type.ishy;
                cust.iszk    = type.iszk;
                cust.isjf    = type.isjf;
                cust.func    = type.func;
                cust.zkl     = type.zkl;
                cust.value1  = type.value1;
                cust.value2  = type.value2;
                cust.value3  = type.value3;
                cust.value4  = type.value4;
                cust.value5  = type.value5;
                cust.valstr1 = type.valstr1;
                cust.valstr2 = type.valstr2;
                cust.valstr3 = type.valstr3;
                cust.valnum1 = type.valnum1;
                cust.valnum2 = type.valnum2;
                cust.valnum3 = type.valnum3;
            }

            if (cust.zkl <= 0)
            {
                cust.zkl = 1;
            }

            GlobalInfo.localDB.resultSetClose();

            return true;
            
        }
        catch (Exception e)
        {
            e.printStackTrace();

            return false;
        }
        finally
        {
            GlobalInfo.baseDB.resultSetClose();
            GlobalInfo.localDB.resultSetClose();

            PublicMethod.timeEnd("查询本地顾客库耗时: ");
        }
    }
}
