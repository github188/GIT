package com.efuture.commonKit;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;


public class PosClock extends Label
{
    private static String currentTime = "";
    private static Label label = null;
    private static boolean activeStatus = true;
    
    static boolean screenkeyshow = false;
    public PosClock(Composite parent, int style)
    {
        super(parent, style);
        label = this;

        final int time = 1000;

        final Display display = Display.getCurrent();

        final Runnable timer = new Runnable()
        {
            public void run()
            {
            	if (label.isDisposed()) return;
            	
            	// 刷新时间
                currentTime = getTime();
                label.setText(currentTime);
                
                // 将操作系统窗口的当前窗口设置为收银系统
                if (ConfigClass.KeepActive && activeStatus)
                {
                	Shell[] shells = Display.getDefault().getShells();
                	if (shells != null && shells.length > 0 && 
                		display.getActiveShell() != shells[shells.length-1])
                	{
                    	shells[shells.length-1].forceActive();
                	}
                }
                
                // 启用屏幕虚拟键盘
                if (Math.abs(ConfigClass.ScreenKeyboard) > 0)
                {
	            	Shell[] shells = Display.getDefault().getShells();
	            	if (shells != null && shells.length > 0)
	            	{
	            		Shell actshell = display.getActiveShell();
	            		if (actshell == shells[shells.length-1])
	            		{
	            			if (!screenkeyshow)
	            			{
	            				int kbwnd = GlobalVar.EnableScreenKeyboard(true);
	                			if (kbwnd != 0) shells[shells.length-1].forceActive();
	            				screenkeyshow = true;
	                			//System.out.println("screenkeyshow = true");
	            			}
	            		}
	            		else
	            		{
	            			if (screenkeyshow)
	            			{
	            				GlobalVar.EnableScreenKeyboard(false);
	                			screenkeyshow = false;
	                			//System.out.println("screenkeyshow = false");	                			
	            			}
	            		}
	            	}
                }
                
                display.timerExec(time, this);
            }

            public String getTime()
            {
                //ManipulateDateTime mdt = new ManipulateDateTime();
                //return mdt.getDateByChinese() + " " + mdt.getTime();
            	return ManipulateDateTime.getDateTimeByClock();
            }
        };

        display.timerExec(time, timer);
    }
         
    protected void checkSubclass()
    {
        return;
    }
    
    public static void setKeepActive(boolean b)
    {
    	activeStatus = b;
    }
}
