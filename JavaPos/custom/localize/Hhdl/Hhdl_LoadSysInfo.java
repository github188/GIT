package custom.localize.Hhdl;

import org.eclipse.swt.widgets.Label;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Global.WebServiceConfigClass;
import com.efuture.javaPos.PrintTemplate.ArkGroupBillMode;
import com.efuture.javaPos.PrintTemplate.BusinessPerBillMode;
import com.efuture.javaPos.PrintTemplate.CardSaleBillMode;
import com.efuture.javaPos.PrintTemplate.CheckGoodsMode;
import com.efuture.javaPos.PrintTemplate.DisplayMode;
import com.efuture.javaPos.PrintTemplate.HangBillMode;
import com.efuture.javaPos.PrintTemplate.InvoiceSummaryMode;
import com.efuture.javaPos.PrintTemplate.PayinBillMode;
import com.efuture.javaPos.PrintTemplate.SaleAppendBillMode;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.PrintTemplate.StoredCardStatisticsMode;
import com.efuture.javaPos.PrintTemplate.SyySaleBillMode;
import com.efuture.javaPos.PrintTemplate.YyySaleBillMode;

public class Hhdl_LoadSysInfo extends LoadSysInfo
{
	public boolean getConfigTemplate(Label lbl_message)
	{

		//
		setLabelHint(lbl_message, "正在读取小票打印模版......");

		if (!SaleBillMode.getDefault().ReadTemplateFile())
		{
			new MessageBox("读取小票打印模版文件错误!");
		}

		setLabelHint(lbl_message, "正在读取小票汇总打印模版......");

		if (!InvoiceSummaryMode.getDefault().ReadTemplateFile())
		{
			// new MessageBox("读取小票汇总打印模版文件错误!");
		}
		//
		setLabelHint(lbl_message, "正在读取顾客显示模版......");

		if (!DisplayMode.getDefault().ReadTemplateFile())
		{
			new MessageBox("读取顾客显示模版文件错误!");
		}
		else
		{
			// 显示欢迎信息
			DisplayMode.getDefault().lineDisplayWelcome();
		}

		setLabelHint(lbl_message, "正在读取缴款打印模版......");

		if (!PayinBillMode.getDefault().ReadTemplateFile())
		{
			new MessageBox("读取缴款打印模版文件错误!");
		}

		//
		setLabelHint(lbl_message, "正在读取收银员销售报表打印模版......");

		if (!SyySaleBillMode.getDefault().ReadTemplateFile())
		{
			new MessageBox("读取收银员销售报表打印模版文件错误!");
		}

		//
		setLabelHint(lbl_message, "正在读取柜组对账单打印模版......");

		if (!ArkGroupBillMode.getDefault().ReadTemplateFile())
		{
			new MessageBox("读取柜组对账单打印模版文件错误!");
		}

		//
		setLabelHint(lbl_message, "正在读取营业员报表打印模版......");

		if (!BusinessPerBillMode.getDefault().ReadTemplateFile())
		{
			new MessageBox("读取营业员报表打印模版文件错误!");
		}

		setLabelHint(lbl_message, "正在读取电子卡联打印模版......");
		if (!CardSaleBillMode.getDefault().ReadTemplateFile())
		{
			new MessageBox("读取电子卡联打印模版文件错误!");
		}

		setLabelHint(lbl_message, "正在读取营业员联打印模版......");
		if (!YyySaleBillMode.getDefault().ReadTemplateFile())
		{
			new MessageBox("读取营业员联打印模版文件错误!");
		}

		setLabelHint(lbl_message, "正在读取小票附加数据联打印模版......");
		if (!SaleAppendBillMode.getDefault().ReadTemplateFile())
		{
			new MessageBox("读取小票附加数据联打印模版文件错误!");
		}

		//
		setLabelHint(lbl_message, "正在读取挂单小票打印模版......");

		if (!HangBillMode.getDefault().ReadTemplateFile())
		{
			new MessageBox("读取挂单小票打印模版文件错误!");
		}

		// 读取赠券模板
		setLabelHint(lbl_message, "正在读取赠券打印模版......");

		if (Hhdl_GiftBillMode.getDefault().checkTemplateFile())
		{
			Hhdl_GiftBillMode.getDefault().ReadTemplateFile();
		}

		// 读取面值卡收款统计
		setLabelHint(lbl_message, "正在读取面值卡收款统计打印模版......");

		if (StoredCardStatisticsMode.getDefault().checkTemplateFile())
		{
			StoredCardStatisticsMode.getDefault().ReadTemplateFile();
		}

		// 读取盘点单模版
		setLabelHint(lbl_message, "正在读取盘点单打印模版......");

		if (!CheckGoodsMode.getDefault().ReadTemplateFile())
		{
			new MessageBox("读取盘点单打印模版文件错误!");
		}

		// 读取调用WebService的配置
		setLabelHint(lbl_message, "正在读取WebService配置信息......");
		if (!WebServiceConfigClass.getDefault().ReadWebServiceConfigFile())
		{
			new MessageBox("读取WebService配置文件错误!");
		}

		// 读取功能模块配置
		setLabelHint(lbl_message, "正在读取功能模块配置信息......");
		readFunctionMode();
		return true;
	}
}
