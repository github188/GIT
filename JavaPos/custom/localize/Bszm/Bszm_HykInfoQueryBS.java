package custom.localize.Bszm;

import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Logic.HykInfoQueryBS;

public class Bszm_HykInfoQueryBS extends HykInfoQueryBS
{

	public int getMemberInputMode()
	{
		return TextBox.MsrKeyInput;
	}
}
