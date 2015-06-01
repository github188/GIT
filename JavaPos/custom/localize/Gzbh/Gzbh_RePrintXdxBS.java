package custom.localize.Gzbh;

import java.math.BigDecimal;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.GlobalInfo;

public class Gzbh_RePrintXdxBS
{

	public Vector mzkInfo = new Vector();

	public Gzbh_RePrintXdxBS()
	{

	}

	//查询工作日志
	public boolean initRePrintXdx(Table tabQueryMzkSell)
	{
		try
		{
			tabQueryMzkSell.removeAll();
			String[] record = new String[1];
			boolean result = false;
			Gzbh_NetService netservice = (Gzbh_NetService) NetService.getDefault();
			result = netservice.rePrinttDzkInfo(record, "0021");

			//[0]销售金额，[1]销售笔数，[2]冲正金额，[3]冲正笔数，[4]交易时间
			if (result && record != null && record.length > 0)
			{
				String[] records = record[0].split(";");

				if (records != null && records.length > 0)
				{
					String[] detiles;
					for (int i = 0; i < records.length; i++)
					{
						detiles = new String[5];
						detiles = records[i].split(",");
						String syjh = GlobalInfo.syjDef.syjh; // 收银机号
						String syyh = GlobalInfo.posLogin.gh; //收银员号
						String jyzbs = new BigDecimal(detiles[1]).add(new BigDecimal(detiles[3])).toString(); //交易总笔数
						String jyze = new BigDecimal(detiles[0]).add(new BigDecimal(detiles[2])).toString(); //交易总额
						//[0]收银机号，[1]收银员号，[2]消费卡金额，[3]消费卡笔数，[4]消费券金额，[5]消费券笔数，[6]总笔数，[7]总金额，[8]时间
						mzkInfo.add(new String[] { syjh, syyh, detiles[0], detiles[1], detiles[2], detiles[3], jyzbs, jyze, detiles[4] });

						String[] mzkSellInfo = { String.valueOf(i + 1), detiles[1], detiles[0], detiles[3], detiles[2], detiles[4] };
						TableItem item = new TableItem(tabQueryMzkSell, SWT.NONE);
						item.setText(mzkSellInfo);
					}
					return true;
				}
				else
				{
					return false;
				}
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
	}

	public void rePrintJfk(String[] info)
	{
		//		[0]收银机号，[1]收银员号，[2]销售金额，[3]销售笔数，[4]冲正金额，[5]冲正笔数，[6]总笔数，[7]总金额，[8]时间
		Printer.getDefault().startPrint_Normal();
		Printer.getDefault().printLine_Normal("广百积分卡结算单(补打)：");
		Printer.getDefault().printLine_Normal("收款机终端号：" + info[0] + "     收款员：" + info[1]);
		Printer.getDefault().printLine_Normal("共有付款交易 " + info[3] + " 笔");
		Printer.getDefault().printLine_Normal("付款总额 " + info[2] + " 元");
		Printer.getDefault().printLine_Normal("共有冲正交易 " + info[5] + " 笔");
		Printer.getDefault().printLine_Normal("冲正总额 " + info[4] + " 元");
		Printer.getDefault().printLine_Normal("日结共有交易 " + info[6] + " 笔");
		Printer.getDefault().printLine_Normal("交易总额 " + info[7] + " 元");
		Printer.getDefault().printLine_Normal("--------------------------------");
		Printer.getDefault().printLine_Normal("结算时间: " + info[8]);
		Printer.getDefault().cutPaper_Normal();
	}
}
