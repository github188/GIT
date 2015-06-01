package device.ICCard;

public class NT512 extends KTL512V
{
	public String getDiscription()
	{
		return "密码键盘NT512";
	}
	
	public char[] getCmd()
	{
		char[] chrCmd = { 0x1b, 0x50 };
		return chrCmd;
	}

	public String updateCardMoney(String cardno, String operator, double ye)
	{
		return "error:该设备不支持本功能";
	}	
}
