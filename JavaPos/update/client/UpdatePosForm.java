package update.client;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import com.efuture.javaPos.Global.Language;
import com.swtdesigner.SWTResourceManager;


public class UpdatePosForm 
{
	private Display display = null;
	private Shell shell = null;
	private ProgressBar pbDownLoad = null;
	private ProgressBar pbUpdate = null;
	private Label lblDownLoad = null;
	private Label lblUpdate = null;
	
	public UpdatePosForm()
	{
		this.open();
	}
	
	public void open() 
	{
		display = Display.getDefault();
		createContents();
		
		shell.open();
		shell.layout();
		
		new UpdatePosBs(this);
		
		while (!shell.isDisposed()) 
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
	}


	protected void createContents() 
	{
		shell = new Shell(GlobalVar.style);
		shell.setSize(411, 176);
		Rectangle area = Display.getDefault().getPrimaryMonitor().getClientArea();
		shell.setBounds(area.width/2-411/2,area.height/2-176/2 ,411,176);
		shell.setText(Language.apply("更新程序"));
		
		
		 //创建标题栏
        if (GlobalVar.heightPL != 0)
        {
       	 	Label labelTitle = new Label(shell, SWT.NONE);
       	       	  labelTitle.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
                  labelTitle.setBounds(0,0,411,30);
                  labelTitle.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
                  labelTitle.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
                  labelTitle.setAlignment(SWT.CENTER);
                  labelTitle.setText(Language.apply("更新程序"));  
        }
        
		final Group group = new Group(shell, SWT.NONE);
		group.setBounds(10, 10 + GlobalVar.heightPL, 387, 121);

		lblDownLoad = new Label(group, SWT.NONE);
		lblDownLoad.setBounds(8, 10,369, 22);
		lblDownLoad.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lblDownLoad.setText(Language.apply("正在下载最新程序..........."));

		pbDownLoad = new ProgressBar(group, SWT.NONE);
		pbDownLoad.setBounds(8, 38,372, 22);

		pbUpdate = new ProgressBar(group, SWT.NONE);
		pbUpdate.setBounds(8, 92,372, 22);

		lblUpdate = new Label(group, SWT.NONE);
		lblUpdate.setBounds(8, 66,367, 20);
		lblUpdate.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lblUpdate.setText(Language.apply("正在更新最新程序............"));
		
	}
	
	public Display getDisplay()
	{
		return display;
	}
	
	public Shell getShell()
	{
		return shell;
	}
	
	public ProgressBar getPbDownLoad()
	{
		return pbDownLoad;
	}
	
	public ProgressBar getPbUpdate()
	{
		return pbUpdate;
	}
	
	public Label getLblDownLoad()
	{
		return lblDownLoad;
	}
	
	public Label getLblUpdate()
	{
		return lblUpdate;
	}
}
