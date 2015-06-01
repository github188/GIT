package device.ICCard;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_ICCard;

public class Virtual_ICCard implements Interface_ICCard
{
	String cardno = null;
	double cardye = 0;
	
	public boolean open()
	{
    	if (DeviceName.deviceICCard.length() <= 0) return false;
    	
    	if (cardno == null)
    	{
    		String[] arg = DeviceName.deviceICCard.split(",");
    		if (arg.length > 0) cardno = arg[0];
    		if (arg.length > 1) cardye = Convert.toDouble(arg[1]);
    	}

		return true;
	}
	
	public boolean close()
	{
		return true;
	}

	public String getDiscription()
	{
		return "虚拟IC卡设备";
	}

	public Vector getPara()
	{
		Vector v = new Vector();
		v.add(new String[]{"IC卡号"});
		v.add(new String[]{"IC余额"});
		
		return v;
	}

	public String findCard()
	{
		//new MessageBox("这是虚拟IC卡设备,仅供测试使用");
		if (cardno == null || cardno.trim().length() <= 0) cardno = "TEST"+String.valueOf((long)(Math.random()*1000000));
		
		return cardno + "," + cardye;
	}
	
	public String updateCardMoney(String cardno, String operator, double ye)
	{
		new MessageBox("这是虚拟IC卡设备,仅供测试使用");
		
		if (!cardno.equals(this.cardno)) return "error:当前IC卡与原交易IC卡不匹配";
		
		if (operator.equalsIgnoreCase("UPDATE")) cardye = ye;
		else if (operator.equalsIgnoreCase("ADDED")) cardye += ye;
		else if (operator.equalsIgnoreCase("MINUS")) cardye -= ye;
		cardye = ManipulatePrecision.doubleConvert(cardye);
		
		return cardno + "," + cardye;
	}
}
