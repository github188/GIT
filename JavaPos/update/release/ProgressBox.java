package update.release;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.swtdesigner.SWTResourceManager;


public class ProgressBox
{
    Label label = null;
    Shell shell = null;
    Control focus = null;
    
    public ProgressBox()
    {
    	focus = Display.getDefault().getFocusControl();
        shell = new Shell(SWT.NONE|SWT.APPLICATION_MODAL);
        shell.setLayout(new FormLayout());
        
        shell.setSize(500, 105);
        
        Rectangle rec = Display.getDefault().getPrimaryMonitor().getClientArea();
        shell.setLocation((rec.width / 2) - (shell.getSize().x / 2),
                          (rec.height / 2) - (shell.getSize().y / 2));
        shell.setText("SWT Application");
        //
        label = new Label(shell, SWT.NONE);
        final FormData formData = new FormData();
        formData.bottom = new FormAttachment(0, 65);
        formData.top = new FormAttachment(0, 35);
        formData.right = new FormAttachment(0, 475);
        formData.left = new FormAttachment(0, 20);
        label.setLayoutData(formData);

        label.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NONE));
        label.setText("正在执行操作，请等待.....");
        
        shell.open();
        shell.setFocus();
        
        // 刷新
        while (Display.getDefault().readAndDispatch());
    }

    /**
     * Launch the application
     * @param args
     */
    public static void main(String[] args)
    {
    }

    public void setText(String arg)
    {
        label.setText(arg);
        label.getDisplay().update();
        //while (Display.getDefault().readAndDispatch());
    }

    public Label getLabel()
    {
    	return label;
    }
    
    public void close()
    {
        shell.close();
        
        if (focus != null)
        {
        	focus.setFocus();
        }
        
        while (Display.getDefault().readAndDispatch());
    }

    public void waitClose()
    {
        final Display display = Display.getDefault();

        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
    }
}
