package com.efuture.javaPos.UI.Design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.DebugEvent;
import com.swtdesigner.SWTResourceManager;

public class DebugForm
{
	private Combo combo;
	private Text textShow;
	private Label lbMsg;
	private Button button;
	private PosTable table;
	
    public Shell sShell = null; //  @jve:decl-index=0:visual-constraint="10,10"
    public Shell parent = null;

    public Button getbutton()
    {
    	return button;
    }
    
    public Combo getcombo()
    {
    	return combo;
    }
    
    public PosTable gettable()
    {
    	return table;
    }
    
    public Text getTextshow()
    {
    	return textShow;
    }
    
    public Label getlbMsg()
    {
    	return lbMsg;
    }
    
    /**
     * Launch the application
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            DebugForm window = new DebugForm();
            window.open(null,"");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Open the window
     */
    public void open(Object obj,String objname)
    {
    	// 把主窗口激活
    	//GlobalInfo.mainshell.forceActive();
    	
        Display display = Display.getDefault();
        if (!ConfigClass.MouseMode) createContents();
        else 
        {
     	
        }
        
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,sShell,null);
        
        // 创建对应的界面控制对象
        new DebugEvent(this,obj,objname);
        
        if (!sShell.isDisposed())
        {	        
	        sShell.open();
	        textShow.forceFocus();
	        sShell.forceActive();
        }
        
        while (!sShell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
        
        // 释放背景图片
        ConfigClass.disposeBackgroundImage(bkimg);
    }

    /**
     * Create contents of the window
     */
    protected void createContents()
    {
        sShell = new Shell(GlobalVar.style);
        sShell.setLayout(new FormLayout());
        sShell.setText(Language.apply("调试器"));
        sShell.setSize(675, 531);

        table = new PosTable(sShell, SWT.FULL_SELECTION | SWT.BORDER, false);
        final FormData fd_table = new FormData();
        fd_table.top = new FormAttachment(0, 80);
        fd_table.right = new FormAttachment(100, -9);
        table.setLayoutData(fd_table);
        table.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        table.setBounds(10, 100, 100 , 100);
        
        final TableColumn newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setWidth(150);
        newColumn.setText(Language.apply("名称"));
        
        final TableColumn newColumn1 = new TableColumn(table, SWT.NONE);
        newColumn1.setWidth(300);
        newColumn1.setText(Language.apply("值"));
        
        final TableColumn newColumn2 = new TableColumn(table, SWT.NONE);
        newColumn2.setWidth(300);
        newColumn2.setText(Language.apply("类型"));
        
        textShow = new Text(sShell, SWT.BORDER);
        textShow.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        fd_table.bottom = new FormAttachment(textShow, -5, SWT.TOP);
        fd_table.left = new FormAttachment(textShow, 0, SWT.LEFT);
        final FormData fd_textShow_1 = new FormData();
        fd_textShow_1.right = new FormAttachment(100, -10);
        fd_textShow_1.bottom = new FormAttachment(100, -4);
        fd_textShow_1.top = new FormAttachment(100, -132);
        fd_textShow_1.left = new FormAttachment(table, 0, SWT.LEFT);
        textShow.setLayoutData(fd_textShow_1);

        lbMsg = new Label(sShell, SWT.NONE);
        lbMsg.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        final FormData fd_lbMsg = new FormData();
        fd_lbMsg.bottom = new FormAttachment(0, 70);
        fd_lbMsg.top = new FormAttachment(0, 45);
        fd_lbMsg.right = new FormAttachment(table, 655, SWT.LEFT);
        fd_lbMsg.left = new FormAttachment(table, 0, SWT.LEFT);
        lbMsg.setLayoutData(fd_lbMsg);
        lbMsg.setText("Label");

        combo = new Combo(sShell, SWT.NONE);
        combo.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        final FormData fd_combo = new FormData();
        fd_combo.right = new FormAttachment(0, 595);
        fd_combo.bottom = new FormAttachment(0, 30);
        fd_combo.top = new FormAttachment(0, 5);
        fd_combo.left = new FormAttachment(0, 5);
        combo.setLayoutData(fd_combo);

        button = new Button(sShell, SWT.NONE);
        button.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        final FormData fd_button = new FormData();
        fd_button.right = new FormAttachment(lbMsg, 0, SWT.RIGHT);
        fd_button.bottom = new FormAttachment(combo, 25, SWT.TOP);
        fd_button.top = new FormAttachment(combo, 0, SWT.TOP);
        fd_button.left = new FormAttachment(combo, 5, SWT.RIGHT);
        button.setLayoutData(fd_button);
        button.setText(Language.apply("返回"));
        final FormData fd_textShow = new FormData();
        fd_textShow.right = new FormAttachment(100, -5);
        fd_textShow.bottom = new FormAttachment(100, -5);
        fd_textShow.top = new FormAttachment(0, 5);
        fd_textShow.left = new FormAttachment(0, 5);
    }
}
