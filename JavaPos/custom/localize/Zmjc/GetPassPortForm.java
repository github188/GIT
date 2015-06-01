package custom.localize.Zmjc;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.DeBugTools.PosLog;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

/**
 * 获取护照窗口
 * @author sf
 *
 */
public class GetPassPortForm
{
    private Text text;
    private Label label_1;
    private Label label_2;
    private Label label_3;
    private  Shell shell;
    
    private StringBuffer sbPassprot;
 
    public GetPassPortForm(StringBuffer sbPassprot)
    {
    	this.sbPassprot = sbPassprot;
    }
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        /*final Display display = Display.getDefault();
        createContents();
        
        if (!shell.isDisposed())
        {
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
        }*/
    }

    /**
     * 打开获取护照窗口
     * 
     */
    public void open()
    {
        final Display display = Display.getDefault();
        createContents();

        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,shell);
        
        //Event
//        CheckPassPortNoEvent fPPNE = new CheckPassPortNoEvent();
        
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
        
        loadEvent();
        
        if (!shell.isDisposed())
        {
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
    
    private void loadEvent()
    {

        // 增加监听器
        NewKeyEvent event = new NewKeyEvent()
        {
            public void keyDown(KeyEvent e, int key)
            {
                
            }

            public void keyUp(KeyEvent e, int key)
            {
                keyReleased(e, key);
            }
        };

        ZmjcPassport_NewKeyListener key = new ZmjcPassport_NewKeyListener();
        key.event     = event;
        key.inputMode = key.inputMode;//.DoubleInput;

        text.addKeyListener(key);
        shell.addKeyListener(key);

    }
    
    public void keyReleased(KeyEvent e, int key)
    {
        try
        {
            switch (key)
            {
            	case GlobalVar.Pay:
            		this.sbPassprot.append(text.getText());
            		PosLog.getLog(this.getClass().getSimpleName()).info(Language.apply("GetPassPortForm() 确认护照读取."));
            		shell.close();
                    shell.dispose();
            		break;
           
            	case GlobalVar.Exit:
            		this.sbPassprot = null;
            		PosLog.getLog(this.getClass().getSimpleName()).info(Language.apply("GetPassPortForm() 取消护照读取."));
                    shell.close();
                    shell.dispose();            		
            		break;
            }
        }
        catch (Exception e1)
        {
        	PosLog.getLog(this.getClass().getSimpleName()).info(e1);
        }
    }
    
    /**
     * Create contents of the window
     */
    protected void createContents()
    {
        shell = new Shell( GlobalVar.style | SWT.CLOSE);
        shell.setLayout(new FormLayout());
        shell.setSize(324, 274);
        shell.setText(Language.apply("获取护照_操作步骤："));

        text = new Text(shell, SWT.BORDER | SWT.MULTI);
        final FormData fd_text = new FormData();
        fd_text.bottom = new FormAttachment(100, -7);
        fd_text.top = new FormAttachment(100, -127);
        fd_text.right = new FormAttachment(100, -5);
        fd_text.left = new FormAttachment(100, -313);
        text.setLayoutData(fd_text);
        
        label_1 = new Label(shell, SWT.NONE);
        label_1.setForeground(SWTResourceManager.getColor(255, 0, 0));
        label_1.setFont(SWTResourceManager.getFont("", 11, SWT.BOLD));
        final FormData fd_label_1 = new FormData();
        fd_label_1.bottom = new FormAttachment(0, 35);
        fd_label_1.top = new FormAttachment(0, 20);
        fd_label_1.right = new FormAttachment(0, 225);
        fd_label_1.left = new FormAttachment(0, 35);
        label_1.setLayoutData(fd_label_1);
        label_1.setText(Language.apply("1.请先在设备上刷护照"));

        label_2 = new Label(shell, SWT.NONE);
        label_2.setForeground(SWTResourceManager.getColor(255, 0, 0));
        label_2.setFont(SWTResourceManager.getFont("", 11, SWT.BOLD));
        final FormData fd_label_2 = new FormData();
        fd_label_2.bottom = new FormAttachment(0, 95);
        fd_label_2.top = new FormAttachment(0, 80);
        fd_label_2.left = new FormAttachment(0, 35);
        final FormData fd_label_2_1 = new FormData();
        fd_label_2_1.top = new FormAttachment(0, 50);
        fd_label_2_1.left = new FormAttachment(0, 35);
        label_2.setLayoutData(fd_label_2_1);
        label_2.setText(Language.apply("2.若刷卡成功，请按【付款键】"));

        label_3 = new Label(shell, SWT.NONE);
        label_3.setForeground(SWTResourceManager.getColor(255, 0, 0));
        label_3.setFont(SWTResourceManager.getFont("", 11, SWT.BOLD));
        final FormData fd_label_3 = new FormData();
        fd_label_3.top = new FormAttachment(0, 85);
        fd_label_3.left = new FormAttachment(0, 30);
        label_3.setLayoutData(fd_label_2);
        label_3.setText(Language.apply("3.若取消窗口，请按【退出键】"));

		shell.setBounds((GlobalVar.rec.x - shell.getSize().x) / 2,
    	                (GlobalVar.rec.y - shell.getSize().y) / 2,
    	                shell.getSize().x,
    	                shell.getSize().y - GlobalVar.heightPL);
    	        
    	        shell.setBounds((GlobalVar.rec.x - shell.getSize().x) / 2,
    	                (GlobalVar.rec.y - shell.getSize().y) / 2,
    	                shell.getSize().x,
    	                shell.getSize().y - GlobalVar.heightPL);
    }
}
