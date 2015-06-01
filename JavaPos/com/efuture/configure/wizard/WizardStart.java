package com.efuture.configure.wizard;

import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.efuture.javaPos.Global.GlobalVar;
import com.swtdesigner.SWTResourceManager;


public class WizardStart
{
	public static String[] mainargs = null;
	
    public static void main(String[] args)
    {
        WizardStart.mainargs = args;
        
        if (args != null)
        {
            if (args.length > 0)
            {
                GlobalVar.RefushConfPath(args[0].trim());
            }
        }
        
        Shell shell = Display.getDefault().getActiveShell();
    	//shell = new Shell(SWT.NONE|SWT.APPLICATION_MODAL|SWT.TITLE);
        //Rectangle rec = Display.getDefault().getPrimaryMonitor().getClientArea();
        //shell.setLocation((rec.width - 800) / 2,(rec.height - 400)/ 2);
        WizardDialog dlg = new WizardDialog(shell, new ConfigWizard());
        dlg.addPageChangedListener(new IPageChangedListener()
        {
            public void pageChanged(PageChangedEvent event)
            {
                //IWizardPage page = (IWizardPage) event.getSelectedPage();
                //可以保存DialogSettings的一些设置
            }
        });
        dlg.open();
        
		SWTResourceManager.dispose();
		System.exit(0);
    }
}
