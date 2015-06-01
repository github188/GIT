package com.efuture.javaPos.Logic;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.PublicMethod;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.UI.ImportSmallTicketBackupEvent;

public class ImportSmallTicketBackupBS
{
	public ImportSmallTicketBackupEvent importSmallTicketBackupEvent = null;

	public void setImportSmallTicketBackupEvent(ImportSmallTicketBackupEvent importSmallTicketBackupEvent)
	{
		this.importSmallTicketBackupEvent = importSmallTicketBackupEvent;
	}

	/**
	 * 小票头、小票商品明细、小票付款明细变量
	 */
	public SaleHeadDef saleHead;
	public Vector saleGoods, salePayment;


	/**
	 * 得到当前记账日期下所有小票备份文件名列表
	 *
	 */
	public void getFileList(Table table_SmallTicketList)
	{
		ProgressBox pb = new ProgressBox();
		pb.setText(Language.apply("正在加载需要导入的小票备份流水....."));
		
		try
		{
			//得到记账日期后，查询当日小票备份
			table_SmallTicketList.removeAll();
			String date = GlobalInfo.balanceDate.replaceAll("/", "");
			String name = ConfigClass.LocalDBPath + "Invoice/" + date + "/" + "invtrace";
	
			String dirname[] = null;
	
			//文件夹不存在时直接退出
			if (!PathFile.fileExist(name)) return;
	
			//列出文件夹下所有备份文件
			dirname = PathFile.getAllDirName(name);
			for (int i = dirname.length - 1; i >= 0; i--)
			{
				TableItem ti = new TableItem(table_SmallTicketList, SWT.NONE);
	
				ti.setChecked(!checkSmallTicketExist(dirname[i].trim()));
				ti.setText(0, Integer.toString(i + 1));
				ti.setText(1, saleHead.syjh);
				ti.setText(2, ManipulatePrecision.doubleToString(saleHead.fphm, 4, 1, true));
				ti.setText(3, saleHead.rqsj);
				ti.setText(4, saleHead.syyh);
			}
	
			sortTable();
			table_SmallTicketList.select(0);
			readFileContent(table_SmallTicketList.getSelectionIndex());
			fullSmallTicketTable();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			pb.close();
		}
	}

	/**
	 * 判读小票备份在当地库中是否存在
	 * @param index
	 * @return：数据已有返回true，否则false
	 */
	public boolean checkSmallTicketExist(String fileName)
	{
		readFileContent(fileName);

		Object obj = null;
		obj = GlobalInfo.dayDB.selectOneData("select count(*) from SALEHEAD where syjh='" + saleHead.syjh + "' and fphm="
				+ saleHead.fphm);
		if (obj == null || Integer.parseInt(obj.toString()) <= 0) return false;
		else return true;
	}
	
	/**
	 * 读出选定行号小票备份文件中的记录
	 * @param index：小票行号
	 */
	public void readFileContent(int index)
	{
		//得到当前行的Item
		TableItem item = importSmallTicketBackupEvent.table_SmallTicketList.getItem(index);
		readFileContent(item.getText(1) + "_" + item.getText(2) + ".dat");
	}

	/**
	 * 读出小票备份文件中的记录
	 * @param fileName
	 */
	public void readFileContent(String fileName)
	{
		FileInputStream f = null;
		
		//得到记账日期后，查询当日小票备份
		String date = GlobalInfo.balanceDate.replaceAll("/", "");
		String name = ConfigClass.LocalDBPath + "Invoice/" + date + "/" + "invtrace";

		try
		{
			name += "/" + fileName;
			
			//打开文件，得到对象流
			f = new FileInputStream(name);
			ObjectInputStream s = new ObjectInputStream(f);

			saleHead = (SaleHeadDef) s.readObject();
			saleGoods = (Vector) s.readObject();
			salePayment = (Vector) s.readObject();

			s.close();
			s = null;
			f.close();
			f = null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (f != null) try
			{
				f.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * 小票数据写入数据网格中
	 *
	 */
	public void fullSmallTicketTable()
	{
		int i;//记录循环参数

		SaleGoodsDef saleDef = null;
		SalePayDef payDef = null;
		importSmallTicketBackupEvent.tabTicketDeatilInfo.removeAll();
		importSmallTicketBackupEvent.tabPay.removeAll();

		//写入小票头
		importSmallTicketBackupEvent.txtGiveChangeMoney.setText(ManipulatePrecision.doubleToString(saleHead.zl));
		importSmallTicketBackupEvent.txtFactInceptMoney.setText(ManipulatePrecision.doubleToString(saleHead.sjfk));
		importSmallTicketBackupEvent.txtAgioMoney.setText(ManipulatePrecision.doubleToString(saleHead.hjzke));
		importSmallTicketBackupEvent.txtShouldInceptMoney.setText(ManipulatePrecision.doubleToString(saleHead.ysje));
		
		//填充小票商品明细
		for (i = 0; i < saleGoods.size(); i++)
		{
			saleDef = (SaleGoodsDef) saleGoods.elementAt(i);
			TableItem ti = new TableItem(importSmallTicketBackupEvent.tabTicketDeatilInfo, SWT.NONE);
			//ti.setText(0, saleDef.yyyh.trim());//营业员
			ti.setText(0, saleDef.barcode.trim());//商品编码
			ti.setText(1, saleDef.name.trim());//商品名称
			ti.setText(2, ManipulatePrecision.doubleToString(saleDef.jg));//单价
			ti.setText(3, ManipulatePrecision.doubleToString(saleDef.sl, 4, 1, true));//数量
			ti.setText(4, ManipulatePrecision.doubleToString(saleDef.hjzk));//折扣额
			ti.setText(5, ManipulatePrecision.doubleToString(ManipulatePrecision.sub(saleDef.hjje, saleDef.hjzk)));//应收金额
		}
		
		//填充小票付款明细
		for (i = 0; i < salePayment.size(); i++)
		{
			payDef = (SalePayDef) salePayment.elementAt(i);
			TableItem ti = new TableItem(importSmallTicketBackupEvent.tabPay, SWT.NONE);
			ti.setText(0, payDef.payname.trim());//付款名称
			ti.setText(1, payDef.payno.trim());//付款帐号
			ti.setText(2, ManipulatePrecision.doubleToString(payDef.ybje));//付款金额
		}
	}

	/**
	 * 向当前库写入备份小票数据
	 *
	 */
	public boolean writeSmallTicketData()
	{
		boolean result = true;
		
		ProgressBox pb = new ProgressBox();
		pb.setText(Language.apply("开始导入小票备份流水..."));
		
		try
		{
			PublicMethod.timeStart(Language.apply("正在导入本地小票库,请等待......"));
			for (int i = 0; i < importSmallTicketBackupEvent.table_SmallTicketList.getItemCount(); i++)
			{
				pb.setText(Language.apply("正在导入小票备份流水({0}),请等待...",new Object[]{(i+1) + "/" + importSmallTicketBackupEvent.table_SmallTicketList.getItemCount()}));
//				pb.setText("正在导入小票备份流水("+(i+1) + "/" + importSmallTicketBackupEvent.table_SmallTicketList.getItemCount() + "),请等待...");
				
				TableItem ti = importSmallTicketBackupEvent.table_SmallTicketList.getItem(i);
				if (ti.getChecked())
				{
					readFileContent(i);
					if (checkSmallTicketExist(ti.getText(1) + "_" + ti.getText(2) + ".dat"))
					{
						new MessageBox(Language.apply("小票 ：{0} 在当前库中已存在！", new Object[]{ti.getText(2)}));
					}
					else
					{
						if (!writeSale(saleHead, saleGoods, salePayment)) result = false;
					}
				}
			}
			PublicMethod.timeEnd(Language.apply("导入本地小票库耗时: "));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			result = false;
		}
		finally
		{
			pb.close();
			if (result) new MessageBox(Language.apply("小票备份流水导入完成！"));
			else
			{
				new MessageBox(Language.apply("小票备份流水导入失败！"));
				
				// 重新刷新列表
				getFileList(importSmallTicketBackupEvent.table_SmallTicketList);
			}
		}
		
		return result;
	}

	/**
	 * 写小票数据
	 * @param saleHead
	 * @param saleGoods
	 * @param salePayment
	 * @return
	 */
	private boolean writeSale(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
	{
		boolean done = false;
		String line = "";

		try
		{
			PublicMethod.timeStart(Language.apply("正在写入本地小票库,请等待......"));

			// 开始事务
			if (!GlobalInfo.dayDB.beginTrans()) { return false; }

			// 按表的字段确定对象的数据,表结构不存在的数据不保存
			String[] refhead = GlobalInfo.dayDB.getTableColumns("SALEHEAD");
			if (refhead == null || refhead.length <= 0) refhead = SaleHeadDef.ref;

			// 插入小票头
			line = CommonMethod.getInsertSql("SALEHEAD", refhead);

			if (!GlobalInfo.dayDB.setSql(line)) { return false; }

			if (!GlobalInfo.dayDB.setObjectToParam(saleHead, refhead)) { return false; }

			if (!GlobalInfo.dayDB.executeSql())
			{
				new MessageBox(Language.apply("写入小票头失败..."), null, false);
				return false;
			}

			// 按表的字段确定对象的数据,表结构不存在的数据不保存
			String[] refgoods = GlobalInfo.dayDB.getTableColumns("SALEGOODS");
			if (refgoods == null || refgoods.length <= 0) refgoods = SaleGoodsDef.ref;

			// 插入小票商品明细
			line = CommonMethod.getInsertSql("SALEGOODS", refgoods);

			if (!GlobalInfo.dayDB.setSql(line)) { return false; }

			SaleGoodsDef saleDef = null;

			for (int i = 0; i < saleGoods.size(); i++)
			{
				saleDef = (SaleGoodsDef) saleGoods.elementAt(i);

				if (!GlobalInfo.dayDB.setObjectToParam(saleDef, refgoods)) { return false; }

				if (!GlobalInfo.dayDB.executeSql())
				{
					new MessageBox(Language.apply("写入小票头明细失败..."), null, false);
					return false;
				}
			}

			// 按表的字段确定对象的数据,表结构不存在的数据不保存
			String[] refpay = GlobalInfo.dayDB.getTableColumns("SALEPAY");
			if (refpay == null || refpay.length <= 0) refpay = SalePayDef.ref;

			// 插入小票付款明细
			line = CommonMethod.getInsertSql("SALEPAY", refpay);

			if (!GlobalInfo.dayDB.setSql(line)) { return false; }

			SalePayDef payDef = null;

			for (int i = 0; i < salePayment.size(); i++)
			{
				payDef = (SalePayDef) salePayment.elementAt(i);

				if (!GlobalInfo.dayDB.setObjectToParam(payDef, refpay)) { return false; }

				if (!GlobalInfo.dayDB.executeSql())
				{
					new MessageBox(Language.apply("写入付款明细失败..."), null, false);
					return false;
				}
			}

			// 写入汇总数据
			if (!AccessDayDB.getDefault().writeSaleState(saleHead, saleGoods, salePayment)) { return false; }

			// 提交事务
			if (!GlobalInfo.dayDB.commitTrans()) { return false; }

			// 检查小票是否写入成功
			if (!AccessDayDB.getDefault().checkSuccessInvoice(saleHead, saleGoods, salePayment)) { return false; }

			// 小票号累加 
			if (saleHead.fphm >= GlobalInfo.syjStatus.fphm) GlobalInfo.syjStatus.fphm = saleHead.fphm + 1;
			AccessLocalDB.getDefault().writeSyjStatus();

			// 记录发送任务
			AccessLocalDB.getDefault().writeTask(StatusType.TASK_SENDINVOICE, TaskExecute.getKeyTextByBalanceDate());

			//
			done = true;
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (!done)
			{
				GlobalInfo.dayDB.rollbackTrans();
			}

			//
			PublicMethod.timeEnd(Language.apply("写入本地小票库耗时: "));
		}
	}

	/**
	 * 反选小票列表
	 *
	 */
	public void checkSmallTicket()
	{
		int index = importSmallTicketBackupEvent.table_SmallTicketList.getSelectionIndex();
		if (index < 0) return;
		TableItem ti = importSmallTicketBackupEvent.table_SmallTicketList.getItem(index);
		ti.setChecked(!ti.getChecked());
		
		// 选中下一行
		if (index < importSmallTicketBackupEvent.table_SmallTicketList.getItemCount() - 1)
		{
			index++;
		}
		else
		{
			index = 0;
		}
		importSmallTicketBackupEvent.table_SmallTicketList.select(index);
		importSmallTicketBackupEvent.table_SmallTicketList.showSelection();
	}

	/**
	 * 对文件列表进行排序。将选中的记录条目向上移动，未选中的向下移动。
	 *
	 */
	public void sortTable()
	{
		TableItem[] items = importSmallTicketBackupEvent.table_SmallTicketList.getItems();
		for (int i = 1; i < items.length; i++)
		{
			int value1 = items[i].getChecked() ? 1 : 0;
			for (int j = 0; j < i; j++)
			{
				int value2 = items[j].getChecked() ? 1 : 0;
				if (value1 > value2)
				{
					String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2), items[i].getText(3), items[i].getText(4) };
					items[i].dispose();
					TableItem item = new TableItem(importSmallTicketBackupEvent.table_SmallTicketList, SWT.NONE, j);
					item.setText(values);
					item.setChecked(value1 == 1 ? true : false);
					items = importSmallTicketBackupEvent.table_SmallTicketList.getItems();
					break;
				}
			}
		}
	}
}
