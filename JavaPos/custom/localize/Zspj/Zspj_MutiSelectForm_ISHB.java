package custom.localize.Zspj;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.MutiSelectEvent;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;


public class Zspj_MutiSelectForm_ISHB
{
    public Text text;
    public PosTable table;
    public PosTable table1;
    public Shell shell;
    public int choice = -1;
    public Label label;
    
    public String InputText = "";

    /**
     * Launch the application
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            new Zspj_MutiSelectForm_ISHB();
            
            //window.open("aaa",new String[]{"a","b","c"},new int[]{10,20,30},new Vector());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Open the window
     */
    public int open(String help_txt, String[] title, int[] width, Vector content)
    {
    	return open(help_txt,title,width,content,false);
    }
    
    public int open(String help_txt, String[] title, int[] width, Vector content,boolean textInput)
    {
       return open(help_txt,title,width,content,textInput,589,319,560,192,false,false,-1,false);
    }
    
    public int open(String help_txt, String[] title, int[] width, Vector content,boolean textInput,int frmwidth,int frmhigh,boolean manychoice)
    {
    	return open(help_txt,title,width,content,textInput,frmwidth,frmhigh,frmwidth-30,frmhigh-127,manychoice,false,-1,false);
    }
    
    public int open(String help_txt, String[] title, int[] width, Vector content,boolean textInput,int frmwidth,int frmhigh,int tabwidth,int tabhigh,boolean manychoice)
    {
       return open(help_txt,title,width,content,textInput,frmwidth,frmhigh,tabwidth,tabhigh,manychoice,false,-1,false);
    }
    
    public int open(String help_txt, String[] title, int[] width, Vector content,boolean textInput,int frmwidth,int frmhigh,int tabwidth,int tabhigh,boolean manychoice,boolean cannotchoice)
    {
    	return this.open(help_txt, title, width, content, textInput, frmwidth, frmhigh, tabwidth, tabhigh, manychoice,false,-1,false, 0, 0, null, null, null, 0,cannotchoice);
    }
    
    public int open(String help_txt, String[] title, int[] width, Vector content,boolean textInput,int frmwidth,int frmhigh,int tabwidth,int tabhigh,boolean manychoice,boolean modifyvalue,int rowindex,boolean specifyback,int tab2width , int tab2high ,String[] title1, int[] width1,Vector content2 ,int funcID)
    {
    	return open(help_txt,  title,  width,  content, textInput, frmwidth, frmhigh, tabwidth, tabhigh, manychoice, modifyvalue, rowindex, specifyback, tab2width , tab2high ,title1, width1,content2 ,funcID, false);
    }
    
    //文本框标签,标题列名,标题宽,内容,是否显示文本框,窗口宽,窗口高,表宽,表高,是否是多选,是否修改某个字段的值,某一个字段的索引,指定小票退货专用
    public int open(String help_txt, String[] title, int[] width, Vector content,boolean textInput,int frmwidth,int frmhigh,int tabwidth,int tabhigh,boolean manychoice,boolean modifyvalue,int rowindex,boolean specifyback)
    {
    	// 0 - 代表不显示第2个TABLE ，其余数值暂定
    	return this.open(help_txt, title, width, content, textInput, frmwidth, frmhigh, tabwidth, tabhigh, manychoice, modifyvalue, rowindex, specifyback, 0, 0, null, null, null, 0);
    }
    
    public int open(String help_txt, String[] title, int[] width, Vector content,boolean textInput,int frmwidth,int frmhigh,int tabwidth,int tabhigh,boolean manychoice,boolean modifyvalue,int rowindex,boolean specifyback,int tab2width , int tab2high ,String[] title1, int[] width1,Vector content2 ,int funcID,boolean cannotchoice)
    {
	   	final Display display = Display.getDefault();
	    createContents(frmwidth,frmhigh,tabwidth,tabhigh,tab2width,tab2high);
	
	    new Zspj_MutiSelectEvent_ISHB(this, help_txt, title, width, content,textInput,manychoice,modifyvalue,rowindex,specifyback,title1,width1,content2,funcID,cannotchoice);
	
        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,shell);
        
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
	        
	    if (!shell.isDisposed())
	    {
	        shell.open();
	         
	        if (!textInput) table.setFocus();
	        else text.setFocus();
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
	        
	    return choice;
    }
    
    /**
     * Create contents of the window
     */
    protected void createContents(int frmwidth,int frmhigh,int tabwidth,int tabhigh,int tab1width,int tab1high)
    {
        shell = new Shell(GlobalVar.style);
        shell.setSize(frmwidth,frmhigh);
        shell.setText(Language.apply("选择框"));

        table = new PosTable(shell, SWT.FULL_SELECTION | SWT.BORDER, false);
        table.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        table.setBounds(10, 79,tabwidth,tabhigh);
        
        if (tab1width > 0 && tab1high > 0)
        {
	        table1 = new PosTable(shell, SWT.FULL_SELECTION | SWT.BORDER, false);
	        table1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
	        table1.setLinesVisible(true);
	        table1.setHeaderVisible(true);
	        table1.setBounds(10, tabhigh+85, tab1width , tab1high);
        }
        label = new Label(shell, SWT.NONE);
        label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label.setText(Language.apply("请选择"));
        label.setBounds(9, 15, tabwidth, 20);

        text = new Text(shell, SWT.BORDER);
        text.setTextLimit(30);
        text.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        text.setBounds(10, 44,tabwidth, 25);
    }
}
