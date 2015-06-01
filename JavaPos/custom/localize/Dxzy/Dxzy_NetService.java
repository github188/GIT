package custom.localize.Dxzy;

import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Bcrm.Bcrm_NetService;

public class Dxzy_NetService extends Bcrm_NetService
{/*
	<configure CmdCode="868">
	  <CmdType>HttpCmd</CmdType> 
	  <CmdMemo>查询商品国际码</CmdMemo> 
	  <CmdTran>1</CmdTran> 
	<StartTrans>true</StartTrans>
	  <Cmd_01_Mode>MemoryCourse</Cmd_01_Mode> 
	  <Sql_01_Type>OracleResultSet</Sql_01_Type> 
	  <Tran_01_Sql>{call java_findgoodsinfo.getgoodsinfo(?,?,?,?,?,?)}</Tran_01_Sql> 
	  <Tran_01_ParaName>mktcode,syjh,code,gz,yyyh</Tran_01_ParaName> 
	  <Tran_01_ParaType>s,s,s,s,s</Tran_01_ParaType> 
	  <Tran_01_ColName>code,goodsid,gz,name,je,color,size</Tran_01_ColName> 
	  <Tran_01_ColType>s,s,s,s,s,s,s</Tran_01_ColType> 
	</configure>*/
	public String searchGoods(String code,StringBuffer gz,String djlb)
	{
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		Vector v1 = new Vector();
		try
		{
			cmdHead = new CmdHead(CmdDef.DXZY_GETGOODS);

			String[] value = { GlobalInfo.sysPara.mktcode,ConfigClass.CashRegisterCode, code, gz.toString(), djlb,GlobalInfo.posLogin.gh };
			String[] arg = { "mktcode","syjh", "code", "gz", "djlb","yyyh" };
			line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));

			result = HttpCall(line, Language.apply("未找到此商品"));
			if (result == 0)
			{
			//new MessageBox("result: "+result);
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[]{"code","goodsid","name","je","color","size","gz"});
				
//				new MessageBox("result: "+v.size());
				if (v.size() > 1)
				{
					for (int i=0 ; i < v.size(); i++)
					{
						String[] rows = (String[]) v.elementAt(i);
						String[] rows1 = new String[]{rows[0],rows[1],rows[2],rows[3],rows[4],rows[5]};
						v1.add(rows1);
					}
					
					String title[] = {"编码","货号","商品名称","售价","商品颜色","商品尺码"};
					int[] width = {150,150,150,100,100,100};
					int choice = new MutiSelectForm().open(Language.apply("请选择"), title, width, v1,false,800,600,false);
					String[] lines = (String[]) v.elementAt(choice);
					gz.delete(0, gz.length());
					gz.append(lines[6]);
					return lines[0];
					
				}
				else
				{
					String[] lines = (String[]) v.elementAt(0);
					gz.delete(0, gz.length());
					gz.append(lines[6]);
					return lines[0];
				}
			}
			else
			{
				return null;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			cmdHead = null;
			line = null;
			v1.clear();
			v1 = null;
		}
		return null;
	}
	
	//获得商品
	public int getGoodsDef(GoodsDef goodsDef, String searchFlag, String code, String gz, String scsj, String yhsj, String djlb)
	{
		if (gz.length() <= 0)
		{
			new MessageBox("柜组必填");
			return -1;
		}
		if (!GlobalInfo.isOnline) { return -1; }

		StringBuffer gzbuffer = new StringBuffer();
		gzbuffer.append(gz);
		String vcode = searchGoods(code,gzbuffer,djlb);
		
		if (vcode != null) 
		{
			code = vcode;
			gz = gzbuffer.toString();
		}
		else
		{
			new MessageBox("商品没有找到或商品不属于营业员范围");
			return -1;
		}
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			cmdHead = new CmdHead(CmdDef.FINDGOODS);

			String[] value = { ConfigClass.CashRegisterCode, searchFlag, code, gz, scsj, yhsj, djlb };
			String[] arg = { "syjh", "searchflag", "code", "gz", "scsj", "yhsj", "djlb" };
			line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));

			result = HttpCall(line, Language.apply("未找到此商品"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, GoodsDef.ref);

				if (v.size() > 0)
				{
					String[] lines = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(goodsDef, lines)) { return 0; }
				}

				return -1;
			}
			else
			{
				return result;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			cmdHead = null;
			line = null;
		}

		return -1;
	}
}
