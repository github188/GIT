package custom.localize.Bjys;

import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.PublicMethod;
import com.efuture.javaPos.Struct.MemoInfoDef;



public class Bjys_AccessLocalDB extends com.efuture.javaPos.Global.AccessLocalDB 
{
	public void paraConvertByCode(String code, String value)
	{
		//
		super.paraConvertByCode(code, value);
		
		//
		try
		{
			if (code.equals("Y1"))
			{
				Bjys_CustomGlobalInfo .getDefault().sysPara.isinputcode = value.charAt(0);
				return;
			}
			
		}
		catch (Exception ex)
		{
		    ex.printStackTrace();
		}
	}
	
	//写入POS积分规则
	public boolean writePosJfRule(Vector v)
	{
		String[] row = null;
		
		try
        {
			PublicMethod.timeStart("正在写入本地备用信息表,请等待......");
			
			if (!GlobalInfo.localDB.beginTrans())
            {
                return false;
            }
			
			if (!GlobalInfo.localDB.executeSql("Delete From MEMOINFO"))
            {
                return false;
            }
			
			String line = CommonMethod.getInsertSql("MEMOINFO",MemoInfoDef.ref);
			
			if (!GlobalInfo.localDB.setSql(line))
            {
                return false;
            }
			
			MemoInfoDef mid = new MemoInfoDef();
			
			for (int i = 0; i < v.size(); i++)
            {
				row = (String[]) v.elementAt(i);
				
				if (!Transition.ConvertToObject(mid, row))
                {
                    return false;
                }
				
				if (!GlobalInfo.localDB.setObjectToParam(mid,MemoInfoDef.ref))
                {
                    return false;
                }
				
				if (!GlobalInfo.localDB.executeSql())
                {
                    return false;
                }
            }
			
			if (GlobalInfo.localDB.commitTrans())
            {
                return true;
            }
            else
            {
                return false;
            }
			
        }
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
        {
        	//
        	PublicMethod.timeEnd("写入本地备用信息表耗时: ");        	
        }
	}
}
