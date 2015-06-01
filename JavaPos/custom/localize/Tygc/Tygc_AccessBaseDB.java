package custom.localize.Tygc;

import java.sql.ResultSet;

import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.PublicMethod;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.CustomerTypeDef;

import custom.localize.Bcrm.Bcrm_AccessBaseDB;

public class Tygc_AccessBaseDB extends Bcrm_AccessBaseDB
{
	public boolean getCustomer(CustomerDef cust, String track)
	{
		// 联名卡和老卡不包含“=”
		// 新卡包含“=”需要效验磁道号是否正确
		
		ResultSet rs = null;
        
        try
        {
        	PublicMethod.timeStart("正在查询本地顾客库,请等待......");
        	
            rs = GlobalInfo.baseDB.selectData("select * from CUSTOMER where CODE = '" + track + "'");
        	
            if (rs == null)
            {
            	return false;
            }

            if (rs.next())
            {
                if (!GlobalInfo.baseDB.getResultSetToObject(cust))
                {
                    return false;
                }
                
                //
                GlobalInfo.baseDB.resultSetClose();
                
                /**
                // 查询到卡信息，效验磁道号是否正确,老卡无需效验
                if (type)
                {
                	if (!track.equals(checkCardTrack(cust,track1)))
                	{
                		new MessageBox("磁道号效验错误，请去卡中心检查写卡是否正确");
                		return false;
                	}
                }*/
                
                // 查找类型
                rs = GlobalInfo.localDB.selectData("select * from CUSTOMERTYPE where code = '" +
                                                   cust.type + "'");

                if (rs == null)
                {
                    return false;
                }

                if (rs.next())
                {
                    CustomerTypeDef type1 = new CustomerTypeDef();

                    if (!GlobalInfo.localDB.getResultSetToObject(type1))
                    {
                        return false;
                    }

                    cust.ishy   = type1.ishy;
                    cust.iszk   = type1.iszk;
                    cust.isjf   = type1.isjf;
                    cust.func   = type1.func;
                    cust.zkl    = type1.zkl;
                    cust.value1 = type1.value1;
                    cust.value2 = type1.value2;
                    cust.value3 = type1.value3;
                    cust.value4 = type1.value4;
                    cust.value5 = type1.value5;
                    cust.valstr1 = type1.valstr1;
                    cust.valstr2 = type1.valstr2;
                    cust.valstr3 = type1.valstr3;
                    cust.valnum1 = type1.valnum1;
                    cust.valnum2 = type1.valnum2;
                    cust.valnum3 = type1.valnum3;
                }
                if (cust.zkl <= 0) cust.zkl = 1;

                GlobalInfo.localDB.resultSetClose();
                
                //在CRM中为零钞转存余额，拖网查会员时，这个值为默认折扣率，所以去掉 
                cust.value1 = 0 ;
                
                return true;
            }
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
            
            //
            PublicMethod.timeEnd("查询本地顾客库耗时: "); 
        }

        return false;
	}

}
