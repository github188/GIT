package com.efuture.javaPos.UI.Design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.JfQueryInfoEvent;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

public class JfQueryInfoForm {

	public PosTable table_1;
	public PosTable table;
	public StyledText styledText;
	public Text txt_cardno;
	public Shell shell;

	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			JfQueryInfoForm window = new JfQueryInfoForm();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window
	 */
	public void open() {
		final Display display = Display.getDefault();
		
		createContents();
		
		JfQueryInfoEvent ev = new JfQueryInfoEvent(this);
		
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
		shell.setSize(800, 600);

		//Rectangle area = Display.getDefault().getPrimaryMonitor().getClientArea();
		
		shell.setBounds(GlobalVar.rec.x/2-shell.getSize().x/2,GlobalVar.rec.y/2-shell.getSize().y/2 ,shell.getSize().x,shell.getSize().y-GlobalVar.heightPL);
		shell.setText(Language.apply("会员积分查询"));

		final Label lbl_cardno = new Label(shell, SWT.NONE);
		lbl_cardno.setFont(SWTResourceManager.getFont("", 15, SWT.NONE));
		lbl_cardno.setText(Language.apply("卡号"));
		lbl_cardno.setBounds(10, 10, 44, 27);

		txt_cardno = new Text(shell, SWT.BORDER);
		txt_cardno.setFont(SWTResourceManager.getFont("", 12, SWT.NONE));
		txt_cardno.setBounds(60, 7, 339, 30);

		styledText = new StyledText(shell, SWT.BORDER);
		styledText.setFont(SWTResourceManager.getFont("", 14, SWT.NONE));
		styledText.setBounds(10, 43, 389, 231);

		table = new PosTable(shell, SWT.BORDER);
		table.setFont(SWTResourceManager.getFont("", 14, SWT.NONE));
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBounds(405, 10, 377, 264);

		table_1 = new PosTable(shell, SWT.BORDER);
		table_1.setFont(SWTResourceManager.getFont("", 14, SWT.NONE));
		table_1.setLinesVisible(true);
		table_1.setHeaderVisible(true);
		table_1.setBounds(10, 280, 772, 276);
		//
	}

}
