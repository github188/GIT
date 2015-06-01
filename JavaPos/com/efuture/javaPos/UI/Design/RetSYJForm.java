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

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.RetSYJEvent;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;


public class RetSYJForm
{
    public final int Done = 0;
    public final int Cancel = 1;
    public final int Clear = 2;
    public static String syj = null;
    public static String fph = null;
    public Text fphm;
    public Text syjh;
    public Shell shell;
    public int doneflag = Cancel;
    public Label lbl_help;
    private String txtinfo = null;

    /**
     * Launch the application
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            new RetSYJForm();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public int open(String syj,long fph)
    {
    	String txt = Language.apply("请输入退货小票的原收银机号和小票号");
    	return open(syj,fph,txt);
    }

    /**
     * Open the window
     */
    public int open(String syj,long fph,String txt)
    {
    	txtinfo = txt;
        final Display display = Display.getDefault();
        createContents();

        new RetSYJEvent(this);
        
        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,shell);
                
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
        
        if (!shell.isDisposed())
        {         
        	if(GlobalInfo.sysPara.backisinput=='Y'&&(syj==null || syj.length()<=0 || fph==0))
        	{
        		//默认为上一笔的款机号和小票号
        		syj=GlobalInfo.syjStatus.syjh;
        		fph=GlobalInfo.syjStatus.fphm-1;
        		if (fph<0) fph=0;
        	}
        	
	        shell.open();
	        syjh.setFocus();
	
	        syjh.setText(syj == null?"":syj);
	        fphm.setText(fph == 0?"":String.valueOf(fph));
	        syjh.selectAll();
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
        
        return doneflag;
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

        final Label label = new Label(shell, SWT.NONE);
        final FormData formData = new FormData();
        formData.bottom = new FormAttachment(0, 39);
        formData.top    = new FormAttachment(0, 15);
        formData.left   = new FormAttachment(0, 27);
        label.setLayoutData(formData);
        label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label.setText(Language.apply("收银机号"));

        syjh              = new Text(shell, SWT.BORDER);
        formData.right    = new FormAttachment(syjh, 0, SWT.LEFT);

        final FormData formData_1 = new FormData();
        formData_1.left   = new FormAttachment(0, 115);
        formData_1.bottom = new FormAttachment(0, 39);
        formData_1.top    = new FormAttachment(0, 15);
        formData_1.right  = new FormAttachment(0, 365);
        syjh.setLayoutData(formData_1);
        syjh.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

        Label label_1;
        label_1 = new Label(shell, SWT.NONE);

        final FormData formData_2 = new FormData();
        formData_2.bottom = new FormAttachment(0, 78);
        formData_2.top    = new FormAttachment(0, 54);
        formData_2.right  = new FormAttachment(0, 115);
        formData_2.left   = new FormAttachment(0, 27);
        label_1.setLayoutData(formData_2);
        label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label_1.setText(Language.apply("小票号码"));

        fphm = new Text(shell, SWT.BORDER);

        final FormData formData_3 = new FormData();
        formData_3.left   = new FormAttachment(label, 0, SWT.RIGHT);
        formData_3.bottom = new FormAttachment(0, 78);
        formData_3.top    = new FormAttachment(0, 54);
        formData_3.right  = new FormAttachment(0, 365);
        fphm.setLayoutData(formData_3);
        fphm.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

        lbl_help = new Label(shell, SWT.NONE);
        lbl_help.setText(txtinfo);

        final FormData formData_4 = new FormData();
        formData_4.bottom = new FormAttachment(0, 117);
        formData_4.top    = new FormAttachment(0, 93);
        formData_4.right  = new FormAttachment(0, 365);
        formData_4.left   = new FormAttachment(0, 27);
        lbl_help.setLayoutData(formData_4);
        lbl_help.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

        //
    }
}
