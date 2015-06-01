package com.efuture.javaPos.Payment;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

public class PaymentSerialForm
{
	public Shell sShell = null; //  @jve:decl-index=0:visual-constraint="10,10"
	private Label lbCardNoStart = null;
	private Label lbCardNoEnd = null;
	private Label lbSumSl = null;
	private Label lbSumYe = null;
    public Label lbPayName = null;
	public Text textCardNoStart;
	public Text textCardNoEnd;
	public Text textSumSl = null;
    public Text textSumYe = null;
    public Label lbPayYe = null;
    public Text textPayYe = null;
    public StyledText sttMemo = null;

    
    public void open(PaymentSerial pay,SaleBS sale, boolean isallowinputpaymoney)
    {
        Display display = Display.getDefault();

        createSShell();

        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,sShell);
        
        new PaymentSerialEvent(this,pay,sale,isallowinputpaymoney);
                
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,sShell,null);
        
        if (!sShell.isDisposed())
        {       
	        sShell.open();
	        sShell.setActive();
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

    public void open(PaymentSerial pay,SaleBS sale)
    {
    	open(pay,sale,true);
    }
    		
    /**
     * This method initializes sShell
     */
    private void createSShell()
    {
        sShell = new Shell(GlobalVar.style_linux);
        sShell.setLayout(new FormLayout());
        sShell.setText("Shell");
        sShell.setSize(new org.eclipse.swt.graphics.Point(436, 328));
        lbSumYe = new Label(sShell, SWT.WRAP);
        final FormData formData_1 = new FormData();
        formData_1.bottom = new FormAttachment(0, 165);
        lbSumYe.setLayoutData(formData_1);
        lbSumYe.setText(Language.apply("总计余额"));
        lbSumYe.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        lbPayYe = new Label(sShell, SWT.WRAP);
        formData_1.left = new FormAttachment(lbPayYe, -80, SWT.RIGHT);
        formData_1.right = new FormAttachment(lbPayYe, 0, SWT.RIGHT);
        final FormData formData_2 = new FormData();
        formData_2.bottom = new FormAttachment(0, 200);
        formData_2.top = new FormAttachment(0, 180);
        lbPayYe.setLayoutData(formData_2);
        lbPayYe.setText(Language.apply("付款金额"));
        lbPayYe.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        sttMemo = new StyledText(sShell, SWT.BORDER);
        final FormData formData_3 = new FormData();
        formData_3.top = new FormAttachment(0, 210);
        formData_3.bottom = new FormAttachment(0, 310);
        sttMemo.setLayoutData(formData_3);
        sttMemo.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        sttMemo.setEditable(false);
        sttMemo.setEnabled(false);
        textSumYe = new Text(sShell, SWT.BORDER | SWT.WRAP);
        final FormData formData_5 = new FormData();
        formData_5.bottom = new FormAttachment(lbSumYe, 0, SWT.BOTTOM);
        textSumYe.setLayoutData(formData_5);
        textSumYe.setEditable(false);
        textSumYe.setBackground(SWTResourceManager.getColor(255, 255, 255));
        textSumYe.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        textSumYe.setTextLimit(20);
        textPayYe = new Text(sShell, SWT.BORDER | SWT.WRAP);
        formData_3.left = new FormAttachment(textPayYe, -388, SWT.RIGHT);
        formData_3.right = new FormAttachment(textPayYe, 0, SWT.RIGHT);
        final FormData formData_6 = new FormData();
        formData_6.top = new FormAttachment(0, 175);
        formData_6.right = new FormAttachment(textSumYe, 289, SWT.LEFT);
        formData_6.left = new FormAttachment(textSumYe, 0, SWT.LEFT);
        textPayYe.setLayoutData(formData_6);
        textPayYe.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        textPayYe.setTextLimit(11);

        Label label;
        label = new Label(sShell, SWT.NONE);
        formData_1.top = new FormAttachment(label, 115, SWT.DEFAULT);
        final FormData formData_7 = new FormData();
        formData_7.bottom = new FormAttachment(0, 30);
        formData_7.top = new FormAttachment(0, 10);
        formData_7.right = new FormAttachment(0, 119);
        formData_7.left = new FormAttachment(0, 23);
        label.setLayoutData(formData_7);
        label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label.setText(Language.apply("付款名称"));

        lbPayName = new Label(sShell, SWT.NONE);
        formData_5.top = new FormAttachment(lbPayName, 107, SWT.DEFAULT);
        final FormData formData_8 = new FormData();
        formData_8.bottom = new FormAttachment(0, 32);
        formData_8.top = new FormAttachment(0, 8);
        formData_8.right = new FormAttachment(0, 411);
        formData_8.left = new FormAttachment(0, 122);
        lbPayName.setLayoutData(formData_8);
        lbPayName.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

        lbCardNoEnd = new Label(sShell, SWT.NONE);
        final FormData formData_9 = new FormData();
        formData_9.bottom = new FormAttachment(0, 94);
        formData_9.top = new FormAttachment(label, 44, SWT.DEFAULT);
        lbCardNoEnd.setLayoutData(formData_9);
        lbCardNoEnd.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        lbCardNoEnd.setText(Language.apply("结束卡号"));

        lbCardNoStart = new Label(sShell, SWT.NONE);
        final FormData formData_9_1 = new FormData();
        formData_9_1.bottom = new FormAttachment(0, 61);
        formData_9_1.top = new FormAttachment(label, 11, SWT.DEFAULT);
        formData_9_1.left = new FormAttachment(lbCardNoEnd, -80, SWT.RIGHT);
        formData_9_1.right = new FormAttachment(lbCardNoEnd, 0, SWT.RIGHT);
        lbCardNoStart.setLayoutData(formData_9_1);
        lbCardNoStart.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        lbCardNoStart.setText(Language.apply("开始卡号"));

        textSumSl = new Text(sShell, SWT.BORDER);
        formData_5.left = new FormAttachment(textSumSl, -289, SWT.RIGHT);
        formData_5.right = new FormAttachment(textSumSl, 0, SWT.RIGHT);
        final FormData formData_4_2 = new FormData();
        formData_4_2.right = new FormAttachment(0, 412);
        formData_4_2.left = new FormAttachment(0, 123);
        formData_4_2.top = new FormAttachment(lbPayName, 73, SWT.DEFAULT);
        formData_4_2.bottom = new FormAttachment(0, 129);
        textSumSl.setLayoutData(formData_4_2);
        textSumSl.setTextLimit(20);
        textSumSl.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

        lbSumSl = new Label(sShell, SWT.NONE);
        formData_2.left = new FormAttachment(lbSumSl, -80, SWT.RIGHT);
        formData_2.right = new FormAttachment(lbSumSl, 0, SWT.RIGHT);
        formData_9.left = new FormAttachment(lbSumSl, -80, SWT.RIGHT);
        formData_9.right = new FormAttachment(lbSumSl, 0, SWT.RIGHT);
        final FormData formData_9_2 = new FormData();
        formData_9_2.top = new FormAttachment(label, 79, SWT.DEFAULT);
        formData_9_2.left = new FormAttachment(0, 21);
        lbSumSl.setLayoutData(formData_9_2);
        lbSumSl.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        lbSumSl.setText(Language.apply("总计数量"));

        textCardNoStart = new Text(sShell, SWT.BORDER);
        textCardNoStart.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        final FormData formData = new FormData();
        formData.top = new FormAttachment(lbCardNoStart, -23, SWT.BOTTOM);
        formData.bottom = new FormAttachment(lbCardNoStart, 0, SWT.BOTTOM);
        formData.right = new FormAttachment(lbPayName, 290, SWT.LEFT);
        formData.left = new FormAttachment(lbPayName, 0, SWT.LEFT);
        textCardNoStart.setLayoutData(formData);

        textCardNoEnd = new Text(sShell, SWT.BORDER);
        textCardNoEnd.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        final FormData formData_4 = new FormData();
        formData_4.top = new FormAttachment(lbCardNoEnd, -23, SWT.BOTTOM);
        formData_4.bottom = new FormAttachment(lbCardNoEnd, 0, SWT.BOTTOM);
        formData_4.right = new FormAttachment(textCardNoStart, 290, SWT.LEFT);
        formData_4.left = new FormAttachment(textCardNoStart, 0, SWT.LEFT);
        textCardNoEnd.setLayoutData(formData_4);
        sShell.setTabList(new Control[] {lbCardNoStart, lbCardNoEnd, lbSumSl, lbSumYe, lbPayYe, textCardNoStart, textCardNoEnd});
    }
}
