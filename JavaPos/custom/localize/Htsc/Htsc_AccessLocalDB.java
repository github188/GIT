package custom.localize.Htsc;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bcrm.Bcrm_AccessLocalDB;

public class Htsc_AccessLocalDB extends Bcrm_AccessLocalDB {
	public void paraInitDefault() {
		super.paraInitDefault();

		GlobalInfo.sysPara.printhgbill = 'N';
	}

	public void paraConvertByCode(String code, String value) {
		super.paraConvertByCode(code, value);
		try {
			if (code.equals("HP") && CommonMethod.noEmpty(value)) {
				GlobalInfo.sysPara.printhgbill = value.trim().charAt(0);
				return;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
