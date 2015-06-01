package posserver.Configure.Init;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import posserver.Configure.ConfigWizard;
import posserver.Configure.JBoss.ServerJBoss422GACfgPage;

import com.swtdesigner.SWTResourceManager;

public class CompleteCfgPage extends WizardPage
{   
    public CompleteCfgPage()
    {
        super(ConfigWizard.Completecfg, "PosServer配置", ImageDescriptor.createFromFile(ServerJBoss422GACfgPage.class, "q.gif"));
        this.setMessage("PosServer配置完成!");
    }

    public void createControl(Composite parent)
    {
    	Composite composite = new Composite(parent,SWT.NONE);
    	composite.setLayout(new FillLayout());
        setControl(composite); 

    	final Label label = new Label(composite, SWT.NONE);
    	label.setFont(SWTResourceManager.getFont("", 18, SWT.NONE));
    	label.setText("配置已完成，谢谢使用!");
    }
    
    public IWizardPage getPreviousPage()
    {	
    	return super.getPreviousPage();
    }
}
