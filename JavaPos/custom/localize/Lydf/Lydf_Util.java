package custom.localize.Lydf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Lydf_Util
{
	public static double getChangeMone(Vector salepay)
	{
		double zl = 0.0;

		// 找出找零
		for (int x = 0; x < salepay.size(); x++)
		{
			SalePayDef pay = (SalePayDef) salepay.get(x);
			if (pay.flag == '2')
			{
				zl = pay.je;
				salepay.removeElement(pay);
			}
		}
		return zl;
	}

	public static boolean removeChangel(Vector salepay, double zl)
	{
		// 去掉找零
		for (int y = 0; y < salepay.size(); y++)
		{
			SalePayDef pay = (SalePayDef) salepay.get(y);
			PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);

			if (zl > 0 && mode != null && mode.type == '1')
				pay.je = ManipulatePrecision.doubleConvert(pay.je - zl, 2, 1);

		}
		return true;
	}

	public static boolean removeGoods(Vector salegoods)
	{
		for (int i = 0; i < salegoods.size(); i++)
		{
			SaleGoodsDef sgd = (SaleGoodsDef) salegoods.get(i);
			if (sgd.sl == 0)
			{
				salegoods.removeElement(sgd);
				i--;
			}
		}
		return true;
	}

	public static boolean isCurrentMonth(String date)
	{
		// 先和当前时间对比
		if (new ManipulateDateTime().compareDate(date, ManipulateDateTime.getCurrentDate()) >= 0)
			return true;

		String tmp = String.valueOf(ManipulateDateTime.getCurrentDate());
		tmp = tmp.substring(0, tmp.length() - 2);
		tmp += "01";

		// 若比当前时间小，再比当月第一天
		if (new ManipulateDateTime().compareDate(date, tmp) < 0)
			return false;

		return true;
	}

	public static String selectTaxFile()
	{
		Vector files = getDirTaxFile();
		if (files == null)
		{
			new MessageBox("未发现需补录的税控文件");
			return null;
		}

		String[] title = { "未发送税控文件列表" };
		int[] width = { 500 };
		Vector content = new Vector();

		for (int i = 0; i < files.size(); i++)
			content.add(new String[] { (String) files.get(i) });

		int choice = new MutiSelectForm().open("请选择要重发的税控文件", title, width, content);
		if (choice == -1)
			return null;

		return ConfigClass.LocalDBPath + "/" + (String) files.get(choice);
	}

	public static Vector getDirTaxFile()
	{
		Vector czFiles = null;
		try
		{

			File dir = new File(ConfigClass.LocalDBPath);
			if (dir.exists())
			{
				czFiles = new Vector();
				File[] tmp = dir.listFiles();
				for (int i = 0; i < tmp.length; i++)
				{
					if (tmp[i].isFile() && isTaxFile(tmp[i].getName().trim()))
						czFiles.add(tmp[i].getName());
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
		return czFiles;
	}

	public static void deleteTaxFile(String file)
	{
		if (PathFile.fileExist(file))
			PathFile.deletePath(file);
	}

	private static boolean isTaxFile(String file)
	{
		if (file.endsWith(".tax"))
			return true;
		return false;
	}

	public static boolean writeTaxFile(SaleHeadDef saleHead, Vector saleGoods, Vector salePay, Lydf_TaxInfo taxinfo)
	{
		FileOutputStream f = null;

		try
		{
			String name = ConfigClass.LocalDBPath + "/" + saleHead.fphm + ".tax";

			f = new FileOutputStream(name);
			ObjectOutputStream s = new ObjectOutputStream(f);
			s.writeObject(saleHead);
			s.writeObject(saleGoods);
			s.writeObject(salePay);
			s.writeObject(taxinfo);

			s.flush();

			s.close();
			f.close();
			s = null;
			f = null;

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return false;
		}
		finally
		{
			try
			{
				if (f != null)
					f.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

}
