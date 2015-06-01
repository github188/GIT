package com.efuture.javaPos.Logic;

import java.util.Vector;
import java.io.*;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.commonKit.ManipulatePrecision;

public class MzkSeqNoResetBS
{
	public boolean checkVaild(String seqno)
	{
		if (seqno.equals("")) { return false; }
		if (!Convert.isLong(seqno) || Long.parseLong(seqno) < 0 || Long.parseLong(seqno) > 999999999)
		{
			new MessageBox(Language.apply("流水号必须在 0 到 999999999 之间!"));
			return false;
		}
		return true;
	}

	public boolean resetSeqNo(String seqno)
	{
		if (!checkVaild(seqno)) return false;		
		
		if (new MessageBox(Language.apply("是要重置面值卡消费流水号吗?"), null, true).verify() != GlobalVar.Key1) return false;
		
		return WriteMzkSeqno(seqno);
	}

	public String getSeqNo()
	{
		String seqno = getMzkSeqno();
		return seqno;
	}

	protected boolean WriteMzkSeqno(String seq)
	{
		PrintWriter pw = null;
		try
		{
			String name = ConfigClass.LocalDBPath + "/SaleSeqno.ini";
			long seql = Long.parseLong(seq);
			pw = CommonMethod.writeFile(name);
			pw.println(seql);
			pw.flush();
			pw.close();
			pw = null;
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			try
			{
				if (pw != null) pw.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	protected String getMzkSeqno()
	{
		PrintWriter pw = null;
		BufferedReader br = null;
		try
		{
			// 读取消费序号
			String name = ConfigClass.LocalDBPath + "/SaleSeqno.ini";
			File indexFile = new File(name);
			// 无消费序号文件，产生一个
			if (!indexFile.exists())
			{
				pw = CommonMethod.writeFile(name);
				pw.println("1");
				pw.flush();
				pw.close();
				pw = null;
			}
			// 读取消费序号
			br = CommonMethod.readFile(name);
			String line = null;
			long seq = 0;
			while ((line = br.readLine()) != null)
			{
				if (line.length() <= 0)
				{
					continue;
				}
				else
				{
					seq = Long.parseLong(line);
				}
			}
			br.close();
			br = null;
			return String.valueOf(seq);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
		finally
		{
			try
			{
				if (pw != null) pw.close();
				if (br != null) br.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	//读取冲正文件中的数据
	public Vector getCzData()
	{
		int id = 0;
		Vector czColumnData = null;
		FileInputStream czInput = null;
		ObjectInputStream objInput = null;

		try
		{
			Vector files = getDirCzFile();
			if (files == null || files.size() == 0) return null;
			if (files.size() > 0)
			{
				czColumnData = new Vector();

				for (int i = 0; i < files.size(); i++)
				{
					id++;
					File file = (File) files.elementAt(i);
					ManipulateDateTime mdt = new ManipulateDateTime();
					mdt.setTimeInMill(file.lastModified());

					czInput = new FileInputStream(file);
					objInput = new ObjectInputStream(czInput);

					MzkRequestDef mzk = (MzkRequestDef) objInput.readObject();
					String tradeType = getCzType(mzk.type);
					String cardNo = mzk.track2; //读取二轨信息
					String amount = ManipulatePrecision.doubleToString(mzk.je);

					czColumnData.add(new String[] { String.valueOf(id), tradeType, cardNo, amount, file.getName(), mdt.getDateTimeString() });

					czInput.close();
					objInput.close();
					czInput = null;
					objInput = null;
				}
			}
			return czColumnData;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
		finally
		{
			try
			{
				if (czInput != null) czInput = null;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	//得到冲正业务类型
	private String getCzType(String vtype)
	{
		String type = "";
		if (vtype.equals("01")) type = Language.apply("消费冲正");
		else if (vtype.equals("03")) type = Language.apply("退货冲正");

		return type;
	}

	//获取冲正文件列表
	private Vector getDirCzFile()
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
					if (tmp[i].isFile() && isMzkCz(tmp[i].getName().trim()))
					{
						czFiles.add(tmp[i]);
					}
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

	private boolean isMzkCz(String file)
	{
		if (file.endsWith(".cz")) return true;
		return false;
	}

	//删除冲正文件
	public boolean deleteCzFile(String filename)
	{
		try
		{
			File delFile = new File(ConfigClass.LocalDBPath, filename);
			if (delFile.exists())
			{
				if (delFile.isFile() && isMzkCz(delFile.getName().trim()))
				{
					delFile.delete();

					//判断文件是否删除
					if (!delFile.exists()) return true;
					else return false;
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

}
