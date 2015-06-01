package custom.localize.Jnyz;

import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import custom.localize.Bhcm.Bhcm_YyySaleBillMode;

public class Jnyz_YyySaleBillMode extends Bhcm_YyySaleBillMode {


	protected String extendCase(PrintTemplateItem item, int index) {
		String line = null;

		try {
			switch (Integer.parseInt(item.code)) {
			
			   case SBM_syjh: // 收银机号
			    	if(salehead.str6 != null && salehead.str6.length() > 0)
			    		line = salehead.str6;
			    	else
			    		line = GlobalInfo.syjStatus.syjh;

					break;
				default:
					line = super.extendCase(item, index);
					break;
			}

			return line;
			
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
