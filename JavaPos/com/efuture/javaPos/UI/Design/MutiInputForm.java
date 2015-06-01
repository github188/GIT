package com.efuture.javaPos.UI.Design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.MutiInputEvent;
import com.swtdesigner.SWTResourceManager;


public class MutiInputForm
{
    public String txt_1 = null;
    public String txt_2 = null;
    public int txt1_mode = TextBox.AllInput;
    public int txt2_mode = TextBox.AllInput;
    public Text text_1;
    public Text text_2;
    public Label label_1;
    public Label label_2;
    public Shell shell;
    public Label lbl_help;
    public boolean result = true;

    public boolean open(String help,String lbl1,StringBuffer txt_value1,String lbl2,StringBuffer txt_value2)
    {
    	return open(help,lbl1,txt_value1,TextBox.AllInput,lbl2,txt_value2,TextBox.AllInput);
    }
    
    public boolean open(String help,String lbl1,StringBuffer txt_value1,String lbl2,StringBuffer txt_value2,int mode)
    {
    	return open(help,lbl1,txt_value1,mode,lbl2,txt_value2,mode);
    }
    
    /**
     * Open the window
     */
    public boolean open(String help,String lbl1,StringBuffer txt_value1,int mode1,String lbl2,StringBuffer txt_value2,int mode2)
    {
        final Display display = Display.getDefault();
        createContents();

        txt1_mode = mode1;
        txt2_mode = mode2;
        
        new MutiInputEvent(this);
        
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
        
        if (!shell.isDisposed())
        {         
	        shell.open();
	        
	        lbl_help.setText(help==null?"":help);
	        
	        label_1.setText(lbl1);
	        if (txt_value1 != null) text_1.setText(txt_value1.toString());	
	        
	        label_2.setText(lbl2);
	        if (txt_value2 != null) text_2.setText(txt_value2.toString());	
	        
	        text_1.setFocus();
	        text_1.selectAll();
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
        
        if (result)
        {
        	if (txt_value1.length() > 0) txt_value1.delete(0,txt_value1.length());
        	if (txt_1 != null) txt_value1.append(txt_1);
        	
        	if (txt_value2.length() > 0) txt_value2.delete(0,txt_value2.length());
        	if (txt_2 != null) txt_value2.append(txt_2);
        }
        
        return result;
    }

    /**
     * Create contents of the window
     */
    protected void createContents()
    {
        shell = new Shell(GlobalVar.style);
        shell.setLayout(new FormLayout());
        shell.setSize(401, 166);
        shell.setText("");

        label_1 = new Label(shell, SWT.NONE);
        final FormData fd_label_1 = new FormData();
        fd_label_1.bottom = new FormAttachment(0, 39);
        fd_label_1.top = new FormAttachment(0, 15);
        fd_label_1.right = new FormAttachment(0, 100);
        fd_label_1.left = new FormAttachment(0, 12);
        label_1.setLayoutData(fd_label_1);
        label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label_1.setText(Language.apply("提示标签"));

        text_1              = new Text(shell, SWT.BORDER);

        final FormData formData_1 = new FormData();
        formData_1.bottom = new FormAttachment(0, 39);
        formData_1.top    = new FormAttachment(0, 15);
        text_1.setLayoutData(formData_1);
        text_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

        label_2 = new Label(shell, SWT.NONE);
        formData_1.left = new FormAttachment(label_2, 0, SWT.RIGHT);

        final FormData fd_label_2 = new FormData();
        fd_label_2.left = new FormAttachment(0, 12);
        fd_label_2.bottom = new FormAttachment(0, 78);
        fd_label_2.top = new FormAttachment(0, 54);
        fd_label_2.right = new FormAttachment(0, 100);
        label_2.setLayoutData(fd_label_2);
        label_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label_2.setText(Language.apply("提示标签"));

        text_2 = new Text(shell, SWT.BORDER);
        formData_1.right = new FormAttachment(text_2, 0, SWT.RIGHT);

        final FormData formData_3 = new FormData();
        formData_3.left   = new FormAttachment(label_1, 0, SWT.RIGHT);
        formData_3.bottom = new FormAttachment(0, 78);
        formData_3.top    = new FormAttachment(0, 54);
        text_2.setLayoutData(formData_3);
        text_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

        lbl_help = new Label(shell, SWT.NONE);
        lbl_help.setForeground(SWTResourceManager.getColor(255, 0, 0));
        formData_3.right = new FormAttachment(lbl_help, 0, SWT.RIGHT);
        lbl_help.setText(Language.apply("提示"));

        final FormData formData_4 = new FormData();
        formData_4.right = new FormAttachment(0, 385);
        formData_4.left = new FormAttachment(0, 12);
        formData_4.bottom = new FormAttachment(0, 117);
        formData_4.top = new FormAttachment(0, 93);
        lbl_help.setLayoutData(formData_4);
        lbl_help.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

        //
    }
}
