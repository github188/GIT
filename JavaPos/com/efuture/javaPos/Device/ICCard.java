package com.efuture.javaPos.Device;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.Interface.Interface_ICCard;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.Language;

public class ICCard
{
	public static ICCard deviceICcard = null;
	private Interface_ICCard iccard = null;

	private String iccardno = null;
	private double iccardye = 0;
	private double iccardpoint = 0;

	public String getICCardNo()
	{
		return iccardno;
	}

	public double getICCardMoney()
	{
		return iccardye;
	}

	public double getICCardPoint()
	{
		return iccardpoint;
	}

	public static ICCard getDefault()
	{
		if (ICCard.deviceICcard == null && (ConfigClass.ICCard1 != null && ConfigClass.ICCard1.trim().length() >= 0))
		{
			ICCard.deviceICcard = new ICCard(ConfigClass.ICCard1);
		}

		return ICCard.deviceICcard;
	}

	public ICCard(String name)
	{
		try
		{
			if ((name != null) && (name.trim().length() > 0))
			{
				Class cl = Class.forName(name);

				iccard = (Interface_ICCard) cl.newInstance();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			PosLog.getLog(getClass()).debug(ex);
			iccard = null;

//			new MessageBox("[" + name + "]\n\nIC卡设备对象不存在");
			new MessageBox(Language.apply("[{0}]\n\nIC卡设备对象不存在", new Object[]{name}));
		}
	}

	public void initCard()
	{
		iccardno = null;
		iccardye = 0;
	}

	public String findCard()
	{
		iccardno = null;
		iccardye = 0;

		try
		{
			// 这里不能进行进度条窗口显示,否则造成NewKeyListener异步

			// 打开设备
			if (open())
			{
				// 读卡
				String msg = iccard.findCard();

				// 关闭设备
				close();

				if (msg != null && msg.indexOf("error:") >= 0)
				{
					/*
					 * new MessageBox(msg); return null;
					 */
					return msg;
				}

				// 读卡同时支持返回卡余额
				if (msg != null && msg.trim().length() > 0)
				{
					String[] s = msg.split(",");
					if (s.length > 0)
						iccardno = s[0];
					if (s.length > 1)
						iccardye = Convert.toDouble(s[1]);
					if (s.length > 2)
						iccardpoint = Convert.toDouble(s[2]);
				}

				return iccardno;
			}
			else
			{
				new MessageBox(Language.apply("IC卡设备打开失败!"));
				return null;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}

	}

	public boolean updateCardMoney(String cardno, String operator, double je)
	{
		ProgressBox pb = null;

		try
		{
			pb = new ProgressBox();
			pb.setText(Language.apply("正在对卡进行操作,请等等..."));

			// 打开设备
			if (open())
			{
				// 读卡
				String msg = iccard.updateCardMoney(cardno, operator, je);

				// 关闭设备
				close();

				if (msg != null && msg.indexOf("error:") >= 0)
				{
					new MessageBox(msg);
					return false;
				}

				// 读卡同时支持返回卡余额
				if (msg != null && msg.trim().length() > 0)
				{
					String[] s = msg.split(",");
					if (s.length > 0)
						iccardno = s[0];
					if (s.length > 1)
						iccardye = Convert.toDouble(s[1]);
					else
					{
						if (operator.equalsIgnoreCase("UPDATE"))
							iccardye = je;
						else if (operator.equalsIgnoreCase("ADDED"))
							iccardye += je;
						else if (operator.equalsIgnoreCase("MINUS"))
							iccardye -= je;
					}
					return true;
				}
				else
				{
					new MessageBox(Language.apply("IC卡设备写卡失败！"));
					return false;
				}
			}
			else
			{
				new MessageBox(Language.apply("IC卡设备打开失败!"));
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
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
		}
	}

	public boolean open()
	{
		if (iccard == null) { return false; }

		return iccard.open();
	}

	public boolean close()
	{
		if (iccard == null) { return false; }

		return iccard.close();
	}
}
