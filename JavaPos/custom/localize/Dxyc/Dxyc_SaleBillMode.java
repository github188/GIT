package custom.localize.Dxyc;

import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.OperUserDef;

public class Dxyc_SaleBillMode extends SaleBillMode {
	
	protected  static int gdorqx=0;//初始化为0。1=挂单；2=取消交易

	public void printRealTimeCancel() {
		if (((SellType.ISBACK(salehead.djlb) && gdorqx != 1 && gdorqx !=2)  || SellType.ISHC(salehead.djlb))
				&& (GlobalInfo.sysPara.printInBill != 'Y')) {
			super.printLine("--------------------------");
			super.printLine(" 以上小票明细作废，重新打印 ");
			super.printLine("--------------------------");
			super.printCutPaper();
		} else {
			OperUserDef staff = new OperUserDef();

			if (!DataService.getDefault().getOperUser(staff,
					salehead.syyh.trim())) {
				System.out.println();
			}


			if (gdorqx == 1) {
				Printer.getDefault().printLine_Normal(
				"--------------------------");
		Printer.getDefault().printLine_Normal(" 以上挂单小票明细作废，重新打印 ");
		Printer.getDefault().printLine_Normal(
				"--------------------------");
		Printer.getDefault().cutPaper_Normal();
			} else if (gdorqx == 2){
				Printer.getDefault().printLine_Normal(
				"--------------------------");
		Printer.getDefault()
				.printLine_Normal(" 以上取消交易小票明细作废，重新打印 ");
		Printer.getDefault().printLine_Normal(
				"--------------------------");
		Printer.getDefault().cutPaper_Normal();
			}
		}
	}
}
