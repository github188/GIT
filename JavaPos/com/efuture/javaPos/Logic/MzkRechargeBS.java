package com.efuture.javaPos.Logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.PrintTemplate.MzkRechargeBillMode;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;

public class MzkRechargeBS
{
	public MzkRequestDef mzkreq = new MzkRequestDef();
	public MzkResultDef mzkret = new MzkResultDef();

	public MzkRechargeBS()
	{
	}

	public String getDisplayCardno()
	{
		return mzkret.cardno;
	}

	public String getDisplayStatusInfo()
	{
		return Language.apply("当前可用余额为:") + mzkret.ye;
	}

	public boolean verifyMessage(String card, String money)
	{
		StringBuffer info = new StringBuffer();
		info.append(Language.apply("请仔细核对下列充值信息\n\n"));
		info.append(Language.apply("卡    号:") + Convert.appendStringSize("", card, 1, 12, 16, 0) + "\n");
		info.append(Language.apply("金    额:") + Convert.appendStringSize("", money + Language.apply(" 元"), 1, 12, 16, 0));

		if (new MessageBox(info.toString(), null, true).verify() != GlobalVar.Key1)
			return true;

		return false;
	}

	public boolean findMzk(String track1, String track2, String track3)
	{
		if ((track1 == null || track1.trim().length() <= 0) && (track2 == null || track2.trim().length() <= 0) && (track3 == null || track3.trim().length() <= 0))
		{
			new MessageBox(Language.apply("磁道数据为空!"));
			return false;
		}

		// 解析磁道
		String[] s = parseTrack(track1, track2, track3);
		if (s == null)
			return false;
		track1 = s[0];
		track2 = s[1];
		track3 = s[2];

		// 设置请求数据
		setRequestDataByFind(track1, track2, track3);

		return sendMzkSale(mzkreq, mzkret);
	}

	public void setRequestDataByFind(String track1, String track2, String track3)
	{
		// 根据磁道生成查询请求包
		mzkreq.type = "05"; // 查询类型
		mzkreq.seqno = 0;
		mzkreq.termno = ConfigClass.CashRegisterCode;
		mzkreq.mktcode = GlobalInfo.sysPara.mktcode;
		mzkreq.syyh = GlobalInfo.posLogin.gh;
		mzkreq.syjh = ConfigClass.CashRegisterCode;
		mzkreq.fphm = GlobalInfo.syjStatus.fphm;
		mzkreq.invdjlb = "";
		mzkreq.paycode = "04";
		mzkreq.je = 0;
		mzkreq.track1 = track1;
		mzkreq.track2 = track2;
		mzkreq.track3 = track3;
		mzkreq.passwd = "";
		mzkreq.memo = "";
	}

	public boolean mzkAccount(double money)
	{
		/*
		 * // 先检查有无充值冲正文件存在 if (!sendRechargeAccountCz()) return false;
		 */

		// 06 充值交易
		mzkreq.type = "06";

		// 保存交易数据进行交易
		if (!setRequestDataByAccount(money))
			return false;

		// 先写冲正文件
		if (!writeMzkRechargeCz())
			return false;

		// 记录面值卡交易日志
		BankLogDef bld = mzkAccountLog(false, null, mzkreq, mzkret);

		// 发送交易请求
		if (!sendMzkSale(mzkreq, mzkret))
			return false;

		mzkAccountLog(true, bld, mzkreq, mzkret);

		if (!bld.retcode.equals("00"))
			return false;

		// 交易成功后删除冲正
		this.deleteMzkCz(null);

		return true;
	}

	public String GetMzkRechargeCzFile()
	{
		return ConfigClass.LocalDBPath + "/MzkRecharge_" + mzkreq.seqno + ".cz";
	}

	public boolean writeMzkRechargeCz()
	{
		FileOutputStream f = null;

		try
		{
			String name = GetMzkRechargeCzFile();

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

	public BankLogDef mzkAccountLog(boolean success, BankLogDef bld, MzkRequestDef req, MzkResultDef ret)
	{
		try
		{
			if (!success)
			{
				// 记录开始交易日志
				BankLogDef newbld = new BankLogDef();
				Object obj = GlobalInfo.dayDB.selectOneData("select max(rowcode) from BANKLOG");
				if (obj == null)
					newbld.rowcode = 1;
				else
					newbld.rowcode = Integer.parseInt(String.valueOf(obj)) + 1;
				newbld.rqsj = ManipulateDateTime.getCurrentDateTime();
				newbld.syjh = req.syjh;
				newbld.fphm = req.fphm;
				newbld.syyh = req.syyh;
				newbld.type = req.type;
				newbld.je = req.je;
				if (req.type.equals("06"))
					newbld.typename = "充值";
				else if (req.type.equals("07"))
					newbld.typename = "充值冲正";
				else
					newbld.typename = "未知";
				newbld.classname = this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1);
				newbld.trace = req.seqno;
				newbld.oldrq = req.mktcode;
				newbld.bankinfo = "MzkRecharge";
				newbld.cardno = req.track2;
				newbld.memo = req.memo;
				newbld.oldtrace = 0;

				newbld.crc = "";
				newbld.retcode = "";
				newbld.retmsg = "";
				newbld.retbz = 'N';
				newbld.net_bz = 'N';
				newbld.allotje = 0;

				if (!AccessDayDB.getDefault().writeBankLog(newbld))
				{
					new MessageBox(Language.apply("记录储值卡充值日志失败!"));
					return null;
				}

				return newbld;
			}
			else
			{
				if (bld == null)
					return null;

				// 更新交易应答数据
				if (ret != null && !CommonMethod.isNull(ret.cardno))
					bld.cardno = ret.cardno;
				bld.retcode = "00";

				if (bld.retmsg != null && !bld.retmsg.trim().equals(""))
				{
					bld.retmsg = "交易成功|" + bld.retmsg;
				}
				else
				{
					bld.retmsg = "交易成功";
				}

				bld.retbz = 'Y';
				bld.net_bz = 'N';
				if (NetService.getDefault().sendBankLog(bld))
					bld.net_bz = 'Y';
				AccessDayDB.getDefault().updateBankLog(bld);
				return bld;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	protected boolean setRequestDataByAccount(double money)
	{
		// 得到消费序号
		long seqno = getMzkRechargeSeqno();
		if (seqno <= 0)
			return false;

		// 打消费交易包
		mzkreq.seqno = seqno;
		mzkreq.je = money;
		mzkreq.syjh = ConfigClass.CashRegisterCode;
		mzkreq.mktcode = GlobalInfo.sysPara.mktcode;
		mzkreq.syyh = GlobalInfo.posLogin.gh;
		mzkreq.paycode = "04";
		mzkreq.invdjlb = "";

		// 告诉后台过程磁道信息是存放的是卡号,只采用卡号记账方式,不使用磁道记账方式
		mzkreq.track1 = "RECHARGE";
		mzkreq.track2 = mzkret.cardno;

		return true;
	}

	public long getMzkRechargeSeqno()
	{
		PrintWriter pw = null;
		BufferedReader br = null;

		try
		{
			// 读取消费序号
			String name = ConfigClass.LocalDBPath + "/MzkRecharge.ini";
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
					seq = Convert.toLong(line.trim());
				}
			}
			br.close();
			br = null;

			// 消费序号+1
			pw = CommonMethod.writeFile(name);
			if (seq < 999999999)
				pw.println(seq + 1);
			else
				pw.println(1);
			pw.flush();
			pw.close();
			pw = null;

			return seq;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			new MessageBox(Language.apply("读取消费序号失败!\n\n") + e.getMessage().trim());

			return -1;
		}
		finally
		{
			try
			{
				if (pw != null)
					pw.close();
				if (br != null)
					br.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
	{
		return DataService.getDefault().sendMzkSale(req, ret);
	}

	public boolean sendRechargeAccountCz()
	{
		FileInputStream f = null;
		boolean ok = false;
		ProgressBox pb = null;

		try
		{
			File file = new File(ConfigClass.LocalDBPath);
			File[] filename = file.listFiles();

			for (int i = 0; i < filename.length; i++)
			{
				if (isRechargeCzFile(filename[i].getName()))
				{
					// 读取文件
					String name = filename[i].getAbsolutePath();

					// 检查文件是否为未删除文件
					File a = new File(ConfigClass.LocalDBPath + "/DEL_" + filename[i].getName());
					if (a.exists())
					{
						// 删除冲正文件
						deleteMzkCz(name);
						a.delete();
						continue;
					}

					// 显示冲正进度提示
					if (pb == null)
					{
						pb = new ProgressBox();
						pb.setText(Language.apply("正在发送面值卡充值冲正数据,请稍等......"));
					}

					f = new FileInputStream(name);
					ObjectInputStream s = new ObjectInputStream(f);

					// 读取冲正数据
					MzkRequestDef req = (MzkRequestDef) s.readObject();

					// 关闭文件
					s.close();
					s = null;
					f.close();
					f = null;

					// 发送冲正交易
					if (!sendAccountCzData(req, name, filename[i].getName()))
						return false;
				}
			}

			ok = true;
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

			if (pb != null)
				pb.close();
			if (!ok)
			{
				new MessageBox(Language.apply("有冲正数据未发送,不能进行卡交易!"));
			}
		}
	}

	// 判断是否是面值卡
	public boolean isRechargeCzFile(String filename)
	{
		if (filename.startsWith("MzkRecharge_") && filename.endsWith(".cz"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean sendAccountCzData(MzkRequestDef req, String czfile, String czname)
	{
		// 根据冲正文件的原交易类型转换冲正数据包
		if (req.type.equals("06"))
		{
			req.type = "07"; // 消费冲正,加
		}
		else
		{
			new MessageBox(Language.apply("冲正文件的交易类型无效，请检查冲正文件"));
			return false;
		}

		// 冲正记录
		String czmsg = "发起[" + czname + "]冲正:" + req.type + "," + req.fphm + "," + req.track2 + "," + ManipulatePrecision.doubleToString(req.je) + ",返回:";

		// 记录面值卡交易日志
		BankLogDef bld = mzkAccountLog(false, null, req, null);

		// 发送冲正交易
		MzkResultDef ret = new MzkResultDef();

		if (!sendMzkSale(req, ret))
		{
			// 记录日志表明发送过冲正数据
			AccessDayDB.getDefault().writeWorkLog(czmsg + "失败", StatusType.WORK_SENDERROR);

			return false;
		}
		else
		{
			// 记录应答日志
			mzkAccountLog(true, bld, req, ret);

			// 记录日志表明发送过冲正数据
			AccessDayDB.getDefault().writeWorkLog(czmsg + "成功", StatusType.WORK_SENDERROR);

			// 冲正发送成功,删除冲正文件
			deleteMzkCz(czfile);
			return true;
		}
	}

	public boolean deleteMzkCz(String fname)
	{
		try
		{

			String name = null;
			if (fname == null || fname.trim().equals(""))
			{
				name = GetMzkRechargeCzFile();
			}
			else
			{
				name = fname;
			}
			File file = new File(name);

			if (file.exists())
			{
				file.delete();
				if (file.exists())
				{
					new MessageBox(Language.apply("冲正文件没有被删除,请检查磁盘!"));

					// 加入日志
					AccessDayDB.getDefault().writeWorkLog("冲正文件 " + name + " 没有删除成功");

					// 在本地标记此冲正文件需要被删除，待重启删除被占用的文件
					File a = new File(ConfigClass.LocalDBPath + "/DEL_" + name);
					a.createNewFile();

					return false;
				}
				else
				{
					return true;
				}
			}
			else
			{
				return true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();

			new MessageBox(Language.apply("删除面值卡冲正文件失败!\n\n") + e.getMessage());

			return false;
		}
	}

	public String[] parseTrack(String track1, String track2, String track3)
	{
		String[] s = new String[3];

		s[0] = track1;
		s[1] = track2;
		s[2] = track3;

		return s;
	}

	public boolean printRechargeBill()
	{
		try
		{
			MzkRechargeBillMode.getDefault().setTemplateObject(mzkreq, mzkret);
			if (MzkRechargeBillMode.getDefault().isLoad())
				MzkRechargeBillMode.getDefault().printBill();

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
}
