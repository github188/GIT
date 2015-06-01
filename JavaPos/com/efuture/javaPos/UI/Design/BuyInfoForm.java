package com.efuture.javaPos.UI.Design;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.BuyInfoEvent;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;


public class BuyInfoForm
{
    public Label lbMessage;
    public Text txtInput;
    public PosTable table;
    public Shell shell;
    
    public boolean isshownodata = true;
    public boolean ismustsel = false;
    
    public Vector selCode = new Vector();
    
    private int width = 506;
    private int height = 413;
    
    /**
     * Launch the application
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            BuyInfoForm window = new BuyInfoForm();
            window.open();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public Vector open()
    {
    	return open(null);
    }
    
    /**
     * Open the window
     */
    public Vector open(String[] showGroup)
    {
    	try
    	{
	        final Display display = Display.getDefault();
	        createContents();
	        
	        new BuyInfoEvent(this,showGroup);
	        
	        // 创建触屏操作按钮栏 
	        ControlBarForm.createMouseControlBar(this,shell);
	                
	        // 加载背景图片
	        Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
	        
	        if (!shell.isDisposed())
	        {
	            //Rectangle rec = display.getPrimaryMonitor().getClientArea();
	            shell.setBounds((GlobalVar.rec.x / 2) - (width / 2),
	                            (GlobalVar.rec.y / 2) - (height / 2), shell.getSize().x,
	                            shell.getSize().y - GlobalVar.heightPL);
	            shell.layout();
	            shell.open();
	            shell.setActive();
	        }
	        
	        while (!shell.isDisposed())
	        {
	            if (!display.readAndDispatch())
	            {
	                display.sleep();
	            }
	        }
	        
	        // 释放背景图片
	        ConfigClass.disposeBackgroundImage(bkimg);
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
        return selCode;
    	
    }

    /**
     * Create contents of the window
     */
    protected void createContents()
    {
        shell = new Shell(GlobalVar.style);
        shell.setLayout(new FormLayout());
        shell.setSize(434, 442);
        
        shell.setText(Language.apply("请选择"));
        table = new PosTable(shell, SWT.BORDER | SWT.FULL_SELECTION, false);
        final FormData formData = new FormData();
        formData.top = new FormAttachment(0, 71);
        formData.left = new FormAttachment(0, 10);
        formData.right = new FormAttachment(100, -10);
        formData.bottom = new FormAttachment(100, -13);
        table.setLayoutData(formData);
        table.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        table.IsLoopSelection = true;

        final TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
        newColumnTableColumn.setWidth(116);
        newColumnTableColumn.setText(Language.apply("代码"));

        final TableColumn newColumnTableColumn_1 = new TableColumn(table,
                                                                   SWT.NONE);
        newColumnTableColumn_1.setWidth(247);
        newColumnTableColumn_1.setText(Language.apply("描述"));

        txtInput = new Text(shell, SWT.BORDER);
        final FormData formData_2 = new FormData();
        formData_2.right = new FormAttachment(100, -10);
        formData_2.top = new FormAttachment(0, 34);
        formData_2.bottom = new FormAttachment(0, 66);
        formData_2.left = new FormAttachment(0, 10);
        txtInput.setLayoutData(formData_2);
        txtInput.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

        lbMessage = new Label(shell, SWT.NONE);
        final FormData fd_lbMessage = new FormData();
        fd_lbMessage.right = new FormAttachment(txtInput, 0, SWT.RIGHT);
        fd_lbMessage.bottom = new FormAttachment(0, 34);
        fd_lbMessage.top = new FormAttachment(0, 10);
        fd_lbMessage.left = new FormAttachment(txtInput, 0, SWT.LEFT);
        lbMessage.setLayoutData(fd_lbMessage);
        lbMessage.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        lbMessage.setText(Language.apply("提示标签"));
    }
}
