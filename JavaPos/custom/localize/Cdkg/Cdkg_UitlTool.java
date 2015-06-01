package custom.localize.Cdkg;

import com.efuture.commonKit.ManipulateStr;

public class Cdkg_UitlTool
{
//	private static char[] pwd = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };
	private static char[] code = { '0', '3', '1', '5', '8', '9', '2', '4', '6', '7' };
	//private static char[] pos = { '3', '7', '9', '0', '1', '2', '6', '5', '4', '8' };

	private static int getIndex(char c)
	{
		for (int i = 0; i < code.length; i++)
		{
			if (code[i] == c)
				return i;
		}
		return 0;
	}

	public static long encryptFphm(long fphm)
	{
		String desStr = "";
		String billno = String.valueOf(fphm);
		if (billno.length() < 9)
			billno = ManipulateStr.PadLeft(billno, 9, '0');

		for (int i = 0; i < billno.length(); i++)
		{

			// 98730
			switch (billno.charAt(i))
			{
				case '0':
					desStr += code[0];
					break;
				case '1':
					desStr += code[1];
					break;
				case '2':
					desStr += code[2];
					break;
				case '3':
					desStr += code[3];
					break;
				case '4':
					desStr += code[4];
					break;
				case '5':
					desStr += code[5];
					break;
				case '6':
					desStr += code[6];
					break;
				case '7':
					desStr += code[7];
					break;
				case '8':
					desStr += code[8];
					break;
				case '9':
					desStr += code[9];
					break;
			}
		}
		return Long.parseLong(desStr);
	}

	public static long decryptFphm(long fphm)
	{
		String desStr = "";
		String billno = String.valueOf(fphm);
		if (billno.length() < 9)
			billno = ManipulateStr.PadLeft(billno, 8, '0');

		for (int i = 0; i < billno.length(); i++)
			desStr += getIndex(billno.charAt(i));

		return Long.parseLong(desStr);

	}

	public static void main(String[] args)
	{
		Cdkg_UitlTool tool = new Cdkg_UitlTool();
		for (int i = 100; i < 151; i++)
		{
			long billno = tool.encryptFphm(i);
			System.out.println(billno +"   " + tool.decryptFphm(billno));
		}
	}
}
