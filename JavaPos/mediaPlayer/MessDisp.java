package mediaPlayer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.swtdesigner.SWTResourceManager;


public class MessDisp
{
    protected Shell shell;
    int x;
    int y;
    int width;
    int height;
    Label label_1;
    Label label;

    public MessDisp(int x, int y, int width, int height)
    {
        this.x      = x;
        this.y      = y;
        this.width  = width;
        this.height = height;
    }

    /**
     * Launch the application
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            MessDisp window = new MessDisp(0, 0, 10, 10);
            window.open();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Open the window
     */
    public void open()
    {
        //final Display display = Display.getDefault();
        createContents();
        shell.open();
        this.visible(false);
        shell.layout();
        shell.setActive();
        /**
        while (!shell.isDisposed()) {
                if (!display.readAndDispatch())
                        display.sleep();
        }*/
    }

    /**
     * Create contents of the window
     */
    protected void createContents()
    {
        shell = new Shell(SWT.BORDER|SWT.ON_TOP);
        shell.setLayout(new FormLayout());
        shell.setBounds(x, y, width, height);
        shell.setText("信息提示栏");

        label = new Label(shell, SWT.NONE);
        label.setFont(SWTResourceManager.getFont("宋体", 20, SWT.BOLD));

        final FormData formData = new FormData();
        formData.right  = new FormAttachment(100, -20);
        formData.bottom = new FormAttachment(0, 68);
        formData.top    = new FormAttachment(0, 20);
        formData.left   = new FormAttachment(0, 15);
        label.setLayoutData(formData);
        label.setText("Label");

        label_1 = new Label(shell, SWT.NONE);
        label_1.setFont(SWTResourceManager.getFont("宋体", 20, SWT.BOLD));

        final FormData formData_1 = new FormData();
        formData_1.bottom = new FormAttachment(0, 136);
        formData_1.right  = new FormAttachment(100, -20);
        formData_1.top    = new FormAttachment(0, 88);
        formData_1.left   = new FormAttachment(label, 0, SWT.LEFT);
        label_1.setLayoutData(formData_1);
        label_1.setText("Label");

        //
    }

    public void updateFrame()
    {
        shell.update();
    }

    public void setLabel1(final String text, final int font, final Color color)
    {
        System.out.println(text);
        label.getDisplay().syncExec(new Runnable()
            {
                public void run()
                {
                    label.setFont(SWTResourceManager.getFont("宋体", font, SWT.NONE));
                    label.setForeground(color);
                    label.setText(text);

                    while (label.getDisplay().readAndDispatch())
                    {
                    }
                }
            });
    }

    public void setLabel2(final String text, final int font, final Color color)
    {
        label_1.getDisplay().syncExec(new Runnable()
            {
                public void run()
                {
                    label_1.setFont(SWTResourceManager.getFont("宋体", font, SWT.NONE));
                    label_1.setForeground(color);
                    label_1.setText(text);

                    while (label.getDisplay().readAndDispatch())
                    {
                    }
                }
            });
    }

    public void visible(final boolean b)
    {
        shell.getDisplay().syncExec(new Runnable()
            {
                public void run()
                {
                    if (b)
                    {
                        shell.setLocation(x, y);
                    }
                    else
                    {
                        shell.setLocation(x, y + shell.getBounds().height);
                    }

                    while (label.getDisplay().readAndDispatch())
                    {
                    }
                }
            });
    }

    public void dispose()
    {
        shell.getDisplay().syncExec(new Runnable()
            {
                public void run()
                {
                    shell.close();
                    shell.dispose();
                }
            });
    }
}
