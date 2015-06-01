package custom.localize.Bjys;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Global.GlobalVar;
import com.swtdesigner.SWTResourceManager;

public class Bjys_AppendBusinessPerForm 
{

	private Shell shell = null;
	private Text txtBusPerOne = null;
	private Text txtBusPerTwo = null;
	private Text txtBusPerThree = null;
	
	public Bjys_AppendBusinessPerForm(StringBuffer buffer)
	{
		this.open(buffer);
	}
	
	public void open(StringBuffer buffer) 
	{
		final Display display = Display.getDefault();
		
		createContents();
		 
		new Bjys_AppendBusinessPerEvent(this,buffer); 
		
		if (!shell.isDisposed())
        { 			
			shell.open();
			shell.layout();
        }
		
		while (!shell.isDisposed()) 
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
		
	protected void createContents() 
	{
		shell = new Shell(GlobalVar.style);
		Rectangle area = Display.getDefault().getPrimaryMonitor().getClientArea();
		shell.setSize(486, 197);
		shell.setBounds(area.width/2-shell.getSize().x/2,area.height/2-shell.getSize().y/2 ,shell.getSize().x,shell.getSize().y-GlobalVar.heightPL);
		shell.setText("附加营业员");

		final Group group = new Group(shell, SWT.NONE);
		group.setBounds(5, 5, 468, 153);

		final Label label = new Label(group, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label.setText("附营业员一:");
		label.setBounds(10, 25, 110, 20);

		txtBusPerOne = new Text(group, SWT.BORDER);
		txtBusPerOne.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtBusPerOne.setBounds(130, 20, 328, 29);

		final Label label_1 = new Label(group, SWT.NONE);
		label_1.setBounds(10, 70, 110, 20);
		label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_1.setText("附营业员二:");

		txtBusPerTwo = new Text(group, SWT.BORDER);
		txtBusPerTwo.setBounds(130, 65, 328, 29);
		txtBusPerTwo.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		final Label label_1_1 = new Label(group, SWT.NONE);
		label_1_1.setBounds(10, 115, 110, 20);
		label_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_1_1.setText("附营业员三:");

		txtBusPerThree = new Text(group, SWT.BORDER);
		txtBusPerThree.setBounds(130, 110, 328, 29);
		txtBusPerThree.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
	}
	
	public Shell getShell()
	{
		return shell;
	}
	
	public Text getTxtBusPerOne()
	{
		return txtBusPerOne;
	}
	
	public Text getTxtBusPerTwo()
	{
		return txtBusPerTwo;
	}
	
	public Text getTxtBusPerThree()
	{
		return txtBusPerThree;
	}
}
