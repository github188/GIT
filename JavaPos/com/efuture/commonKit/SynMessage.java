package com.efuture.commonKit;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;

//子线程的消息面板控制
public class SynMessage
{
    public static void message(final String line)
    {
        Display display = Display.getDefault();
        display.syncExec(new Runnable()
            {
                public void run()
                {
                    new MessageBox(line, null, false);
                }
            });
    }

    public static void setIcon(final Button button, final boolean done)
    {
        Display display = Display.getDefault();
        display.syncExec(new Runnable()
            {
                public void run()
                {
                    // TODO 自动生成方法存根
                    button.setVisible(done);
                }
            });
    }
}
