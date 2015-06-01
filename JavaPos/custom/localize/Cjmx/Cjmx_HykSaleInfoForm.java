package custom.localize.Cjmx;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

public class Cjmx_HykSaleInfoForm {

	public PosTable table_1;
	public StyledText styledText;
	public Shell shell;

	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Cjmx_HykSaleInfoForm window = new Cjmx_HykSaleInfoForm();
			CustomerDef cust = new CustomerDef();
			Vector saleinfo = null;
			window.open(cust,saleinfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window
	 */
	public void open(CustomerDef cust,Vector saleinfo) {
		final Display display = Display.getDefault();
		
		createContents();
		
		Cjmx_HykSaleInfoEvent ev = new Cjmx_HykSaleInfoEvent(this,cust,saleinfo);
		
        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,shell);
                
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
        
		shell.open();
		shell.layout();
		
		ev.afterFormOpenDoEvent();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		
        // 释放背景图片
        ConfigClass.disposeBackgroundImage(bkimg);
	}

	/**
	 * Create contents of the window
	 */
	protected void createContents() {
		shell = new Shell(GlobalVar.style);
		shell.setSize(1067, 556);

		//Rectangle area = Display.getDefault().getPrimaryMonitor().getClientArea();
		
		shell.setBounds(GlobalVar.rec.x/2-shell.getSize().x/2,GlobalVar.rec.y/2-shell.getSize().y/2 ,shell.getSize().x,shell.getSize().y-GlobalVar.heightPL);
		shell.setText(Language.apply("会员信息查询"));

		styledText = new StyledText(shell, SWT.BORDER);
		styledText.setFont(SWTResourceManager.getFont("", 14, SWT.NONE));
		styledText.setBounds(10, 10, 1041, 76);

		table_1 = new PosTable(shell, SWT.BORDER);
		table_1.setFont(SWTResourceManager.getFont("", 14, SWT.NONE));
		table_1.setLinesVisible(true);
		table_1.setHeaderVisible(true);
		table_1.setBounds(10, 92, 1041, 421);
		table_1.setFocus();
		//
	}

}
