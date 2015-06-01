package com.efuture.javaPos.Logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.CustFilterDef;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class HykInfoQueryBS
{
	protected String[] custtrack = null;
	public CustFilterDef selectedRule = null;

	public HykInfoQueryBS()
	{
	}

	public int getMemberInputMode()
	{
		return TextBox.MsrInput;
	}

	public String[] getCustTrack()
	{
		return custtrack;
	}

	public String readMemberCard()
	{
		return readMemberCard(false);
	}

	public String readMemberCard(boolean ispay)
	{
		selectedRule = null;

		StringBuffer cardno = new StringBuffer();

		// 获取自定义的解析规则
		Vector rulelist = showRule();
		
		
		
		if (rulelist != null && rulelist.size() <= 0)
			rulelist = null;

		// 先选择规则后刷会员卡
		boolean ch = false;
		if (GlobalInfo.sysPara.unionVIPMode == 'A'||GlobalInfo.sysPara.unionVIPMode == 'C')
		{
			if (rulelist != null && rulelist.size() > 1)
			{
				rulelist = chooseRule(rulelist, ispay);
				if (rulelist != null)
					ch = true;
			}
			else if (rulelist != null && rulelist.size() == 1)
				ch = true;
		}

		int type = getMemberInputMode();
		if (ch && rulelist != null && rulelist.size() > 0)
		{
			CustFilterDef rule = (CustFilterDef) rulelist.elementAt(0);
			if (rule.InputType != -2)
				type = rule.InputType;
		}

		// 输入顾客卡号
		TextBox txt = new TextBox();
		if (!txt.open(Language.apply("请刷会员卡或顾客打折卡"), Language.apply("会员号"), Language.apply("请将会员卡或顾客打折卡从刷卡槽刷入"), cardno, 0, 0, false, type)) { return null; }

		// 调用客户化会员磁道解析程序
		String tr = getTrackByCustom(txt.Track2);

		// 检查磁道是否和规则相匹配
		if (rulelist != null && rulelist.size() > 0)
		{
			rulelist = chkTrack(txt.Track1, txt.Track2, txt.Track3, rulelist, ispay);
		}

		// 如果匹配的规则有多个,再次让客户选择(B模式先刷卡后选择)
		if (rulelist != null && rulelist.size() > 1)
		{
			rulelist = chooseRule(rulelist, ispay);
		}

		// 解析有效规则下的磁道号
		if (rulelist != null && rulelist.size() > 0)
		{
			selectedRule = (CustFilterDef) rulelist.get(0);
			tr = getTrackByDefine(txt.Track1, txt.Track2, txt.Track3, rulelist);
		}
		else
		{
			if (ch)
			{
				new MessageBox(Language.apply("刷卡与联名卡规则不匹配，该卡无效"));
				return null;
			}
		}

		// 记录磁道供返立即查询券卡使用
		if (GlobalInfo.sysPara.findcustfjk == 'Y')
		{
			custtrack = new String[] { txt.Track1, (tr == null) ? txt.Track2 : tr, txt.Track3 };
		}

		return tr;
	}

	public String getTrackByCustom(String track2)
	{
		return track2;
	}

	public Vector chooseRule(Vector rulelist, boolean ispay)
	{
		//通过参数控制是否启动VIPcard.ini配置
		if (GlobalInfo.sysPara.custConfig == 'N') return null;
		
		Vector con = new Vector();
		for (int i = 0; i < rulelist.size(); i++)
		{
			CustFilterDef filterDef = (CustFilterDef) rulelist.elementAt(i);

			if (filterDef.ispay == 1 && ispay)
				continue;

			con.add(new String[] { filterDef.desc });
		}
		String[] title = { Language.apply("会员卡类型") };
		int[] width = { 500 };

		int choice = new MutiSelectForm().open(Language.apply("请选择卡类型"), title, width, con);
		if (choice == -1)
			return null;

		CustFilterDef rule = ((CustFilterDef) rulelist.elementAt(choice));
		rulelist.removeAllElements();
		rulelist.add(rule);

		return rulelist;
	}

	public Vector showRule()
	{
//		通过参数控制是否启动VIPcard.ini配置
		if (GlobalInfo.sysPara.custConfig == 'N') return null;
		
		if (!PathFile.fileExist(GlobalVar.ConfigPath + "\\VIPCard.ini"))
			return null;

		BufferedReader br = CommonMethod.readFile(GlobalVar.ConfigPath + "\\VIPCard.ini");
		Vector v = new Vector();

		if (br == null)
			return null;

		String line = null;

		CustFilterDef filterDef = null;
		try
		{
			while ((line = br.readLine()) != null)
			{
				if (line.trim().length() <= 0)
					continue;

				if (line.charAt(0) == ';' || line.charAt(1) == ';')
					continue;

				filterDef = new CustFilterDef();
				String[] rule = line.replaceAll(",,", ", ,").split(",");

				if (rule.length > 0)
				{
					filterDef.desc = rule[0].trim();
				}

				if (rule.length > 1)
				{
					filterDef.chkTrackno = Convert.toInt(rule[1]);
				}

				if (rule.length > 2)
				{
					filterDef.chkLength = rule[2];
				}

				if (rule.length > 3)
				{
					filterDef.chkKeypos = Convert.toInt(rule[3]);
				}

				if (rule.length > 4)
				{
					filterDef.chkkeylen = rule[4];
				}

				if (rule.length > 5)
				{
					filterDef.chkKeyBeginValue = rule[5];
				}

				if (rule.length > 6)
				{
					filterDef.chkKeyEndValue = rule[6];
				}

				if (rule.length > 7)
				{
					filterDef.Trackno = Convert.toInt(rule[7]);
				}

				if (rule.length > 8)
				{
					filterDef.Trackpos = Convert.toInt(rule[8]);
				}

				if (rule.length > 9)
				{
					filterDef.Tracklen = rule[9];
				}

				if (rule.length > 10)
				{
					filterDef.TrackFlag = rule[10];
				}

				if (rule.length > 11)
				{
					filterDef.InputType = Convert.toInt(rule[11]);
				}

				if (rule.length > 12)
				{
					filterDef.ispay = Convert.toInt(rule[12]);
				}

				v.add(filterDef);
			}

			return v;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public Vector chkTrack(String track1, String track2, String track3, Vector list, boolean ispay)
	{
		String tr1 = track1;
		String tr2 = track2;
		String tr3 = track3;
		Vector ret = new Vector();
		try
		{
			for (int i = 0; i < list.size(); i++)
			{
				CustFilterDef rule = (CustFilterDef) list.elementAt(i);

				if (rule.ispay == 1 && ispay)
					continue;
				// 效验磁道为0,无效验规则,则所有规则都是有效的
				// 不为0时,按照指定效验位效验
				if (rule.chkTrackno >= 1 && rule.chkTrackno <= 3)
				{
					String line1 = "";
					if (rule.chkTrackno == 1)
					{
						line1 = tr1;
					}
					else if (rule.chkTrackno == 2)
					{
						line1 = tr2;
					}
					else
					{
						line1 = tr3;
					}

					// 磁道长度小于效验长度，不符合效验规则
					if (rule.chkLength != null && rule.chkLength.trim().length() > 0)
					{
						int j = 0;
						for (j = 0; j < rule.chkLength.length(); j++)
						{
							char c = rule.chkLength.charAt(j);

							if (Convert.isNumber(String.valueOf(c)))
							{
								break;
							}
						}

						if (j >= rule.chkLength.length())
							continue;

						int len = Integer.parseInt(rule.chkLength.substring(j));

						if (rule.chkLength.indexOf("!=") >= 0)
						{
							if (line1.length() != len)
							{
							}
							else
							{
								continue;
							}
						}
						else if (rule.chkLength.indexOf("<=") >= 0)
						{
							if (line1.length() <= len)
							{
							}
							else
							{
								continue;
							}
						}
						else if (rule.chkLength.indexOf(">=") >= 0)
						{
							if (line1.length() >= len)
							{
							}
							else
							{
								continue;
							}
						}
						else if (rule.chkLength.indexOf("=") >= 0)
						{
							if (line1.length() == len)
							{
							}
							else
							{
								continue;
							}
						}
						else if (rule.chkLength.indexOf("<") >= 0)
						{
							if (line1.length() < len)
							{
							}
							else
							{
								continue;
							}
						}
						else if (rule.chkLength.indexOf(">") >= 0)
						{
							if (line1.length() > len)
							{
							}
							else
							{
								continue;
							}
						}
					}

					String line2 = "";
					if (rule.chkkeylen != null && rule.chkkeylen.charAt(0) == '[')
					{
						String flag1 = rule.chkkeylen;
						flag1 = flag1.substring(1, flag1.length() - 1);

						if (rule.chkKeypos >= 0)
						{
							line2 = line1.substring(rule.chkKeypos);
						}
						else if (line1.length() + rule.chkKeypos >= 0)
						{
							line2 = line1.substring(line1.length() + rule.chkKeypos);
						}
						else
						{
							line2 = line1;
						}

						if (line2.indexOf(flag1) <= 0)
						{
							new MessageBox(Language.apply("无效的 【{0}】\n或者配置文件出错，磁道中未找到 ", new Object[]{rule.desc}) + rule.chkkeylen);
							return null;
						}

						if (line2.indexOf(flag1) >= 0)
							line2 = line2.substring(0, line2.indexOf(flag1));
					}
					else
					{
						if (rule.chkKeypos >= 0)
						{
							line2 = line1.substring(rule.chkKeypos);
						}
						else if (line1.length() - rule.chkKeypos >= 0)
						{
							line2 = line1.substring(line1.length() - rule.chkKeypos);
						}
						else
						{
							line2 = line1;
						}

						if (Convert.toInt(rule.chkkeylen) > 0)
							line2 = line2.substring(0, Convert.toInt(rule.chkkeylen));
					}

					if (rule.chkKeyBeginValue != null && rule.chkKeyBeginValue.trim().length() > 0)
					{
						if (line2.compareToIgnoreCase(rule.chkKeyBeginValue) < 0)
						{
							continue;
						}
					}

					if (rule.chkKeyEndValue != null && rule.chkKeyEndValue.trim().length() > 0)
					{
						if (line2.compareToIgnoreCase(rule.chkKeyEndValue) > 0)
						{
							continue;
						}
					}

					ret.add(rule);
				}
				else
				{
					ret.add(rule);
				}
			}
		}
		catch (Exception er)
		{
			new MessageBox(Language.apply("解析规则报错，请核对设置是否正确"));
			er.printStackTrace();
			return null;
		}

		return ret;
	}

	public String getTrackByDefine(String track1, String track2, String track3, Vector rulelist)
	{
		String tr1 = track1;
		String tr2 = track2;
		String tr3 = track3;

		try
		{
			CustFilterDef rule = (CustFilterDef) rulelist.elementAt(0);

			// 效验成功，开始截取磁道号
			if (rule.Trackno >= 1 && rule.Trackno <= 3)
			{
				String line1 = "";
				if (rule.Trackno == 1)
				{
					line1 = tr1;
				}
				else if (rule.Trackno == 2)
				{
					line1 = tr2;
				}
				else
				{
					line1 = tr3;
				}

				String line2 = "";
				if (rule.Tracklen != null && rule.Tracklen.charAt(0) == '[')
				{
					String flag1 = rule.Tracklen.trim();
					flag1 = flag1.substring(1, flag1.length() - 1);

					if (rule.Trackpos >= 0)
					{
						line2 = line1.substring(rule.Trackpos);
					}
					else if (line1.length() - rule.Trackpos >= 0)
					{
						line2 = line1.substring(line1.length() - rule.Trackpos);
					}
					else
					{
						line2 = line1;
					}

					if (line2.indexOf(flag1) <= 0)
					{
						new MessageBox(Language.apply("无效的 【{0}】\n或者配置文件出错，磁道中未找到 ", new Object[]{rule.desc.trim()}) + rule.Tracklen);
						return null;
					}

					line2 = line2.substring(0, line2.indexOf(flag1));
				}
				else
				{
					if (rule.Trackpos >= 0)
					{
						line2 = line1.substring(rule.Trackpos);
					}
					else if (line1.length() + rule.Trackpos >= 0)
					{
						line2 = line1.substring(line1.length() + rule.Trackpos);
					}
					else
					{
						line2 = line1;
					}

					if (Convert.toInt(rule.Tracklen) > 0)
						line2 = line2.substring(0, Convert.toInt(rule.Tracklen));
				}

				// 存在特殊字符补位
				if (rule.TrackFlag != null && rule.TrackFlag.trim().length() > 0)
				{
					line2 = rule.TrackFlag + line2;
				}

				return line2;
			}
			else
			{
				new MessageBox(Language.apply("无效的磁道规则，无法解析\n截取磁道必须在1-3之间"));
				return null;
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public CustomerDef findMemberCard(String track2)
	{
		ProgressBox progress = null;
		CustomerDef cust = null;
		try
		{
			progress = new ProgressBox();
			progress.setText(Language.apply("正在查询会员卡信息，请等待....."));

			// 查找会员卡
			cust = new CustomerDef();
			if (!DataService.getDefault().getCustomer(cust, track2)) { return null; }
			if (cust.code == null || cust.code.trim().equals(""))
			{
				new MessageBox(Language.apply("查询的会员卡信息无效!\n请找后台人员"));
				return null;
			}
		}
		finally
		{
			if (progress != null)
				progress.close();
		}

		return cust;
	}

	public void QueryHykInfo()
	{
		// 读会员卡

		String track2 = readMemberCard();
		if (track2 == null || track2.equals(""))
			return;

		// 查找会员卡
		CustomerDef cust = findMemberCard(track2);

		if (cust == null)
			return;

		// 在客显上显示卡号及余额
		// LineDisplay.getDefault().clearText();
		// LineDisplay.getDefault().displayAt(0, 1, cust.code);
		// LineDisplay.getDefault().displayAt(1, 1,
		// ManipulatePrecision.doubleToString(cust.valuememo));

		// 显示卡信息
		hykDisplayInfo(cust);
	}

	protected void hykDisplayInfo(CustomerDef cust)
	{
		StringBuffer info = new StringBuffer();

		getHykDisplayInfo(cust, info);

		new MessageBox(info.toString());

		// 查询卡上的返券资料
		if (GlobalInfo.sysPara.findcustfjk == 'Y')
		{
			if (new MessageBox(Language.apply("是否立即查询卡中电子券的余额?"), null, true).verify() == GlobalVar.Key1)
			{
				CustomLocalize.getDefault().createFjkInfoQueryBS().QueryFjkInfo(custtrack);
			}
		}
	}

	protected void getHykDisplayInfo(CustomerDef cust, StringBuffer info)
	{
		info.append(Language.apply("卡    号: ") + Convert.appendStringSize("", cust.code, 1, 16, 16, 0) + "\n");
		info.append(Language.apply("持 卡 人: ") + Convert.appendStringSize("", cust.name, 1, 16, 16, 0) + "\n");
		info.append(Language.apply("卡 状 态: ") + Convert.appendStringSize("", cust.status, 1, 16, 16, 0) + "\n");
		info.append(Language.apply("卡 积 分: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.valuememo), 1, 16, 16, 0) + "\n");
		info.append(Language.apply("会员功能: ") + Convert.appendStringSize("", getFuncText(cust.ishy), 1, 16, 16, 0) + "\n");
		info.append(Language.apply("积分功能: ") + Convert.appendStringSize("", getFuncText(cust.isjf), 1, 16, 16, 0) + "\n");
		info.append(Language.apply("折扣功能: ") + Convert.appendStringSize("", getFuncText(cust.iszk), 1, 16, 16, 0) + "\n");
		if (isLczcFunc(cust))
		{
			info.append(Language.apply("零钞转存: ") + Convert.appendStringSize("", getFuncText('Y'), 1, 16, 16, 0) + "\n");
			info.append(Language.apply("零钞余额: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.value1), 1, 16, 16, 0) + "\n");
			info.append(Language.apply("零钞上限: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.value2), 1, 16, 16, 0) + "\n");
		}
	}

	protected String getFuncText(char bz)
	{
		if (bz == 'Y')
			return Language.apply("是");
		else
			return Language.apply("否");
	}

	protected boolean isLczcFunc(CustomerDef cust)
	{
		if (cust.func != null && cust.func.length() >= 1 && cust.func.charAt(0) == 'Y') { return true; }

		return false;
	}
}
