package custom.localize.Lrls;

import java.util.Vector;

import com.efuture.javaPos.Logic.MutiSelectBS;
import com.efuture.javaPos.UI.MutiSelectEvent;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Lrls_MutiSelectBS extends MutiSelectBS
{
	public boolean enterBS(MutiSelectEvent event, MutiSelectForm form, int i, Vector content, boolean modifyvalue, boolean manychoice, boolean textInput, int rowindex, boolean specifyback, boolean cannotchoice)
	{
		if(i==-100){
			String info = form.text.getText().trim();
			form.InputText = info;
			form.shell.close();
            form.shell.dispose();
			return true;
		}
		return false;
	}

}
