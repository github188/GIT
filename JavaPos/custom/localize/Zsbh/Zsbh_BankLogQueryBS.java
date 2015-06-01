package custom.localize.Zsbh;

import java.sql.ResultSet;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Logic.BankLogQueryBS;
import com.efuture.javaPos.UI.BankLogQueryEvent;

public class Zsbh_BankLogQueryBS extends BankLogQueryBS {


	public void getBankCardInfo(String rowcode,BankLogQueryEvent  bcqe)
	{
		ResultSet rs = null;
		
		try
		{
			String date = bcqe.getTxtDate().getText();
			
			if (!PathFile.isPathExists(ConfigClass.LocalDBPath + "Invoice/" + date + "/" + LoadSysInfo.getDefault().getDayDBName()))
			{
				new MessageBox("您输入的本地数据库不存在,请重新输入!", null, false);
				return ;
			}
			
			if (date.trim().equals(new ManipulateDateTime().getDateByEmpty()))
			{
				sql = GlobalInfo.dayDB;
			}
			else
			{
				sql = LoadSysInfo.getDefault().loadDayDB(ManipulateDateTime.getConversionDate(date.trim()));
			}
			
			if ((rs = sql.selectData("select * from BANKLOG where rowcode = "+ Integer.parseInt(rowcode.substring(1).trim()))) != null)
			{
				if(rs.next())
				{
					bcqe.getTxtSyjCode().setText(rs.getString("syjh") + " - " + rs.getString("fphm"));
					bcqe.getTxtSyyCode().setText(rs.getString("syyh"));
					bcqe.getTxtCardCode().setText(CommonMethod.isNull(rs.getString("cardno"),""));
					bcqe.getTxtMoney().setText(ManipulatePrecision.doubleToString(rs.getDouble("je")));
					bcqe.getTxtType().setText(exist?rs.getString("typename"):getChangeType(rs.getString("type")));
					if (CommonMethod.isNull(rs.getString("trace"),"").length() <= 1)
					{
						bcqe.getTxtTrace().setText(CommonMethod.isNull(rs.getString("trace"),""));
					}
					else
					{
						//6位，不够时左补0
						bcqe.getTxtTrace().setText(Convert.increaseCharForward(CommonMethod.isNull(rs.getString("trace"),""),'0', 8));
					}
					
					bcqe.getTxtBank().setText(CommonMethod.isNull(rs.getString("bankinfo"),""));
					bcqe.getTxtLodTime().setText(CommonMethod.isNull(rs.getString("oldrq"),""));
					bcqe.getTxtReturnCode().setText(CommonMethod.isNull(rs.getString("retcode"),""));
					if (CommonMethod.isNull(rs.getString("oldtrace"),"").length() <= 1)
					{
						bcqe.getTxtOldTrace().setText(CommonMethod.isNull(rs.getString("oldtrace"),""));
					}
					else
					{
						//6位，不够时左补0						;
						bcqe.getTxtOldTrace().setText(Convert.increaseCharForward(CommonMethod.isNull(rs.getString("oldtrace"),""),'0', 8));
					}
					
					bcqe.getTxtReturnMsg().setText(CommonMethod.isNull(rs.getString("retmsg"),""));
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			sql.resultSetClose();
		}
	}
	
}
