package device.ICCard;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_ICCard;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Muti_ICCard implements Interface_ICCard
{
	private String curPayCode = "";

	public static boolean getMutiICInfo()
	{
		String[] arg = DeviceName.deviceICCard.split("\\|");

		String ictype = null;
		String icpara = null;
		String icname = null;
		String paycode = null;
		for (int i = 0; i < arg.length; i++)
		{
			if (arg[i] == null || arg[i].trim().length() <= 0)
			{
				continue;
			}

			if (arg[i].indexOf(";") < 0)
			{
				new MessageBox("第" + (i + 1) + "台IC卡设备配置无效\n" + arg[i]);
				return false;
			}
			else
			{
				ictype = arg[i].substring(0, arg[i].indexOf(";"));

				icpara = arg[i].split(";")[1];

				icname = arg[i].split(";")[2];

				paycode = arg[i].split(";")[3];

				if (GlobalInfo.mutiICMap == null)
				{
					GlobalInfo.mutiICMap = new HashMap();
				}

				GlobalInfo.mutiICMap.put(paycode, new String[] { ictype, icpara, icname });
			}
		}
		if (GlobalInfo.mutiICMap == null || GlobalInfo.mutiICMap.size() < 2) { return false; }
		return true;
	}

	public boolean close()
	{
		// TODO 自动生成方法存根
		return false;
	}

	public String findCard()
	{
		if (GlobalInfo.mutiICMap == null || GlobalInfo.mutiICMap.size() < 2) { return "error:复式IC卡设备配置错误"; }

		try
		{
			if (curPayCode.length() > 0)
			{
				String[] curIcInfo = (String[]) GlobalInfo.mutiICMap.get(curPayCode);
				if (curIcInfo == null) { return "error:没有找到当前付款代码对应的IC卡设备"; }

				String ictype = curIcInfo[0];
				String icpara = curIcInfo[1];
//				String icname = curIcInfo[2];
				//				String paycode = arg[i].split(";")[3];
				
				Class cl = null;
				for (int j = 0; j < 2; j++)
				{
					try
					{
						if (j == 0) cl = Class.forName("device.ICCard." + ictype);
						else cl = Class.forName(ictype);
						if (cl != null) break;
					}
					catch (Exception ex)
					{
						return "error:生成复式IC卡设备类异常";
					}
				}
				if (cl != null)
				{
					Interface_ICCard iccard = (Interface_ICCard) cl.newInstance();
					DeviceName.deviceICCard = icpara;
					return iccard.findCard();
				}
				else
				{
					return "error:复式IC卡设备配置类名 " + ictype + " 无效";
				}
			}
			else
			// 选择IC卡设备
			{
				// 卓展只允许查询储值卡
				if ("CCZZ".equals(GlobalInfo.ModuleType))
				{
					IC_Cpu_cczz cpu_cczz = new IC_Cpu_cczz();
					DeviceName.deviceICCard = "100,115200,123456,0,32,c:\\cpu\\,D5ULM";
					return cpu_cczz.findCard();
				}
				else
				{
					String[] title = { "代码", "卡类型" };
					int[] width = { 60, 440 };
					Vector contents = new Vector();

					Iterator iter = GlobalInfo.mutiICMap.entrySet().iterator();
					while (iter.hasNext())
					{
						Map.Entry entry = (Map.Entry) iter.next();
						Object key = entry.getKey();
						Object val = entry.getValue();
						String name = ((String[]) val)[2];
						String paycode = (String) key;
						contents.add(new String[] { paycode, name });
					}

					int choice = new MutiSelectForm().open("请选择IC卡类型", title, width, contents, true);
					if (choice == -1)
					{
						return "error:请选择IC卡设备类型";
					}
					else
					{
						String[] row = (String[]) (contents.elementAt(choice));
						String[] curIcInfo = (String[]) GlobalInfo.mutiICMap.get(row[0]);
						
						Class cl = null;
						for (int j = 0; j < 2; j++)
						{
							try
							{
								if (j == 0) cl = Class.forName("device.ICCard." + curIcInfo[0]);
								else cl = Class.forName(curIcInfo[0]);
								if (cl != null) break;
							}
							catch (Exception ex)
							{
								return "error:生成复式IC卡设备类异常";
							}
						}
						if (cl != null)
						{
							Interface_ICCard iccard = (Interface_ICCard) cl.newInstance();
							DeviceName.deviceICCard = curIcInfo[1];
							return iccard.findCard();
						}
						else
						{
							return "error:复式IC卡设备配置类名 " + curIcInfo[0] + " 无效";
						}
					}
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return "error:打开IC卡设备异常";
		}
	}

	public String getDiscription()
	{
		return "复式IC卡设备";
	}

	public Vector getPara()
	{
		// TODO 自动生成方法存根
		return null;
	}

	public boolean open()
	{
		return true;
	}

	public String updateCardMoney(String cardno, String operator, double ye)
	{
		return "error:该设备不支持本功能";
	}

	public String getCurPayCode()
	{
		return curPayCode;
	}

	public void setCurPayCode(String curPayCode)
	{
		this.curPayCode = curPayCode;
	}
}
