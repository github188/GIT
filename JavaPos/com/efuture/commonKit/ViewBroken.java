package com.efuture.commonKit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.OperRoleDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class ViewBroken
{

	public static boolean readMzkBrokenData(String filename)
	{
		try
		{
			FileInputStream f = new FileInputStream(filename);
			ObjectInputStream s = new ObjectInputStream(f);

			// 读取冲正数据
			MzkRequestDef req = (MzkRequestDef) s.readObject();

			// 关闭文件
			s.close();
			s = null;
			f.close();
			f = null;
			
			//writeMzkCz(req,"312311007");
			return true;

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public static boolean writeMzkCz(MzkRequestDef mzkreq, String seq)
	{
		FileOutputStream f = null;

		try
		{
			String name = "c:\\Cust_" + seq + ".cz";

			f = new FileOutputStream(name);
			ObjectOutputStream s = new ObjectOutputStream(f);
			s.writeObject(mzkreq);
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

	public static boolean readPopBrokenData(String filename)
	{
		FileInputStream f = null;

		try
		{
			if (!new File(filename).exists())
				return true;

			f = new FileInputStream(filename);
			ObjectInputStream s = new ObjectInputStream(f);

			// 读交易对象
			Vector saleGoods1 = (Vector) s.readObject();

			System.out.println("======cmpop.dat======");
			for (int i = 0; i < saleGoods1.size(); i++)
			{
				SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods1.get(i);
				double hjzk = saleGoodsDef.hjzk = ManipulatePrecision.doubleConvert(saleGoodsDef.hyzke + saleGoodsDef.yhzke + saleGoodsDef.lszke + saleGoodsDef.lszre + saleGoodsDef.lszzk + saleGoodsDef.lszzr + saleGoodsDef.plzke + saleGoodsDef.zszke + saleGoodsDef.cjzke + saleGoodsDef.ltzke + saleGoodsDef.hyzklje + saleGoodsDef.qtzke + saleGoodsDef.qtzre + saleGoodsDef.rulezke + saleGoodsDef.mjzke, 2, 1);
				System.out.println("商品:" + saleGoodsDef.barcode + " 合计折扣:" + hjzk + " hyzke:" + saleGoodsDef.hyzke + " yhzke:" + saleGoodsDef.yhzke + " lszke:" + saleGoodsDef.lszke + " lszre:" + saleGoodsDef.lszre + " lszzk:" + saleGoodsDef.lszzk + " lszzr:" + saleGoodsDef.lszzr + " plzke:" + saleGoodsDef.plzke + " zszke:" + saleGoodsDef.zszke + " cjzke:" + saleGoodsDef.cjzke + " ltzke:" + saleGoodsDef.ltzke + " hyzklje:" + saleGoodsDef.hyzklje + " qtzke:" + saleGoodsDef.qtzke + " qtzre:" + saleGoodsDef.qtzre + " rulezke:" + saleGoodsDef.rulezke + " mjzke:" + saleGoodsDef.mjzke);
				// System.out.print("条码:" + sgd.barcode + " 数量:" + sgd.sl +
				// '\n');
			}
			// //Vector assistant = (Vector) s.readObject();
			// Vector spare1 = (Vector) s.readObject();
			// Vector goodsCmPop1 = (Vector) s.readObject();

			// 关闭断点文件
			s.close();
			s = null;
			f.close();
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

	public static boolean readBrokenData(String filename)
	{
		FileInputStream f = null;
		try
		{

			f = new FileInputStream(filename);
			ObjectInputStream s = new ObjectInputStream(f);
			s.readObject();

			// 读交易对象
			readStreamToSellObject(s);

			// 关闭断点文件
			s.close();
			s = null;
			f.close();
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

	public static void readStreamToSellObject(ObjectInputStream s) throws Exception
	{
		// 读取
		SaleHeadDef saleHead1 = (SaleHeadDef) s.readObject();
		Vector saleGoods1 = (Vector) s.readObject();

		System.out.println("======Broken.dat======");

		for (int i = 0; i < saleGoods1.size(); i++)
		{
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods1.get(i);
			double hjzk = saleGoodsDef.hjzk = ManipulatePrecision.doubleConvert(saleGoodsDef.hyzke + saleGoodsDef.yhzke + saleGoodsDef.lszke + saleGoodsDef.lszre + saleGoodsDef.lszzk + saleGoodsDef.lszzr + saleGoodsDef.plzke + saleGoodsDef.zszke + saleGoodsDef.cjzke + saleGoodsDef.ltzke + saleGoodsDef.hyzklje + saleGoodsDef.qtzke + saleGoodsDef.qtzre + saleGoodsDef.rulezke + saleGoodsDef.mjzke, 2, 1);
			//System.out.println("商品:" + saleGoodsDef.barcode + " 合计折扣:" + hjzk + " hyzke:" + saleGoodsDef.hyzke + " yhzke:" + saleGoodsDef.yhzke + " lszke:" + saleGoodsDef.lszke + " lszre:" + saleGoodsDef.lszre + " lszzk:" + saleGoodsDef.lszzk + " lszzr:" + saleGoodsDef.lszzr + " plzke:" + saleGoodsDef.plzke + " zszke:" + saleGoodsDef.zszke + " cjzke:" + saleGoodsDef.cjzke + " ltzke:" + saleGoodsDef.ltzke + " hyzklje:" + saleGoodsDef.hyzklje + " qtzke:" + saleGoodsDef.qtzke + " qtzre:" + saleGoodsDef.qtzre + " rulezke:" + saleGoodsDef.rulezke + " mjzke:" + saleGoodsDef.mjzke);

			System.out.print("条码:" + saleGoodsDef.barcode + " 数量:" + saleGoodsDef.sl + '\n');
		}
		System.out.println("==================================================");
		Vector goods1 = (Vector) s.readObject();
		Vector spare1 = (Vector) s.readObject();
		Vector brokenAssistant1 = (Vector) s.readObject();
		OperRoleDef curGrant1 = (OperRoleDef) s.readObject();
		CustomerDef curCustomer1 = (CustomerDef) s.readObject();
		String curyyygz1 = (String) s.readObject();
		String cursqkh1 = (String) s.readObject();
		// Vector memoPayment1 = (Vector)s.readObject();
		Character cursqktype1 = (Character) s.readObject();
		Double cursqkzkfd1 = (Double) s.readObject();
		String thSyjh1 = (String) s.readObject();
		Long thFphm1 = (Long) s.readObject();
		Boolean isbackticket1 = (Boolean) s.readObject();
		String checkdjbh1 = (String) s.readObject();
		Vector salePayment1 = (Vector) s.readObject();
	}

	public static boolean summary(SaleHeadDef saleHead, Vector saleGoods)
	{

		int i;
		double je, zl;
		SaleGoodsDef saleGoodsDef = null;
		SalePayDef salePayDef = null;

		// 反算合计折扣额
		if (saleHead.hjzje == 0)
		{
			saleHead.hjzje = ManipulatePrecision.doubleConvert(saleHead.ysje + saleHead.hjzke, 2, 1);
		}

		if (ManipulatePrecision.doubleCompare(saleHead.ysje, saleHead.hjzje - saleHead.hjzke, 2) != 0)
		{
			new MessageBox("交易主单数据相互不平!\n\n应收金额 = " + ManipulatePrecision.doubleToString(saleHead.ysje) + "\n合计金额 - 合计折扣 = " + ManipulatePrecision.doubleToString(saleHead.hjzje - saleHead.hjzke));
			return false;
		}
		if (ManipulatePrecision.doubleCompare(saleHead.hjzke, saleHead.yhzke + saleHead.hyzke + saleHead.lszke, 2) != 0)
		{
			new MessageBox("交易主单数据相互不平!\n\n合计折扣 = " + ManipulatePrecision.doubleToString(saleHead.hjzke) + "\n折扣明细 = " + ManipulatePrecision.doubleToString(saleHead.yhzke + saleHead.hyzke + saleHead.lszke));
			return false;
		}

		// 检查主单和商品明细之间的平衡
		je = 0;
		for (i = 0; i < saleGoods.size(); i++)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);

			// 零头折扣记入LSZRE
			saleGoodsDef.lszre = ManipulatePrecision.doubleConvert(saleGoodsDef.lszre + saleGoodsDef.ltzke);
			saleGoodsDef.ltzke = 0;

			if (saleGoodsDef.flag == '0')
			{
				continue;
			}

			if (saleGoodsDef.sl == 0 || saleGoodsDef.sl < 0)
			{
				new MessageBox("第 " + (i + 1) + " 行商品 [" + saleGoodsDef.code + "] 数量不合法\n请修改此行商品数量或者删除此商品后重新录入");
				return false;
			}

			if (saleGoodsDef.type == '8')
			{
				je -= saleGoodsDef.hjje - saleGoodsDef.hjzk;
			}
			else
			{
				je += saleGoodsDef.hjje - saleGoodsDef.hjzk;
			}
		}
		if (ManipulatePrecision.doubleCompare(saleHead.ysje, je, 2) != 0)
		{
			new MessageBox("交易主单和商品明细不平!\n\n主单应收金额 = " + ManipulatePrecision.doubleToString(saleHead.ysje) + "\n商品合计金额 = " + ManipulatePrecision.doubleToString(je));
			return false;
		}

		return true;

	}

	public static void main(String[] args)
	{
		//String cmpop = "c:\\Cmpop.dat";
		String broken = "c:\\Broken.dat";
		//String cz = "c:\\Fjk_404084191.cz";
		
	//	ViewBroken.readPopBrokenData(cmpop);
		ViewBroken.readBrokenData(broken);
		
		//ViewBroken.readMzkBrokenData(cz);
	}
}
