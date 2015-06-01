package custom.localize.Zmjc;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalVar;


public class PaymentChangeAddEvent
{ 
    private PosTable table = null;
    private Zmjc_PaymentChange chgBS = null;
    private Text txt = null;
    private Shell shell = null; 
    private PaymentChangeAddForm chgForm = null;
    
    public PaymentChangeAddEvent(Zmjc_PaymentChange paychg, PaymentChangeAddForm chg)
    { 
        this.table    = chg.table;
        this.txt       = chg.text;
        this.shell     = chg.shell;
        this.chgBS    = paychg;
        this.chgForm = chg;
        
        // 设定键盘事件
        NewKeyEvent event = new NewKeyEvent()
        {
            public void keyDown(KeyEvent e, int key)
            {
                keyPressed(e, key);
            }

            public void keyUp(KeyEvent e, int key)
            {
                keyReleased(e, key);
            }
        };

        NewKeyListener key = new NewKeyListener();
        key.event     = event;
        key.inputMode = key.DoubleInput;
        key.isControl = true;

        table.addKeyListener(key);

        //Rectangle rec = Display.getCurrent().getPrimaryMonitor().getClientArea();
        shell.setBounds((GlobalVar.rec.x - shell.getSize().x) / 2+1,
                        (GlobalVar.rec.y - shell.getSize().y) / 2,shell.getSize().x,shell.getSize().y-GlobalVar.heightPL);
        shell.setActive();
        table.setFocus();

        // 初始化
        initPayment();
    }

    public void initPayment()
    { 
    	txt.setText(ManipulatePrecision.doubleToString(chgBS.getChangeTotal()));
    	
    	// 显示付款方式列表,初始化只显示主付款方式,主付款方式的上级代码为0
        showChangePayMode();
         
    }

    public void showChangePayMode()
    {
    	//加载找零币种
    	if (!chgBS.loadChangePayModeAdd(table, chgBS.getChangeTotal()))
    	{
    		chgExit();
    		return;
    	}
    	        
        // 刷新一次table的初始值 
        keyReleased(null, GlobalVar.ArrowUp);
    }

    public void keyPressed(KeyEvent e, int key)
    {
        switch (key)
        {
            case GlobalVar.ArrowUp:
                table.moveUp();

                break;

            case GlobalVar.ArrowDown:
                table.moveDown();

                break;

            case GlobalVar.PageUp:
                table.moveUp();

                break;

            case GlobalVar.PageDown:
                table.moveDown();

                break;
        }
    }

    public void keyReleased(KeyEvent e, int key)
    {        
        switch (key)
        {
            case GlobalVar.ArrowUp:
            	chgBS.setMoneyInputDefaultAdd(table,chgBS.getChangeTotal());
                                
                break;

            case GlobalVar.ArrowDown:
            	chgBS.setMoneyInputDefaultAdd(table,chgBS.getChangeTotal());
                                
                break;

            case GlobalVar.Enter:
            	chgEnter();
 
                break;
            	
            case GlobalVar.Exit:
            	chgExit();

                break;

            default:
                break;
        }
    }

    public void close()
    {
        shell.close();
        shell.dispose();
    }
    
        
    public boolean calcChangResult()
    {       
        // 刷新界面显示
        Display.getCurrent().update();
        
        // 找零足够,完成找零
        if (chgBS.getChangeBalance() <= 0)
        {
        	chgForm.setDone(true);

        	close();
            
            return true;
        }
        
        return false;
    }
    
    public void chgEnter()
    {
    	// 付款不足或是已存在的付款方式,进行付款记账
    	if (chgBS.getChangeBalance() > 0)
    	{
        	// 找零记账
        	if (chgBS.chgAccountAdd(table,chgBS.getChangeTotal()))
        	{
        		//关闭窗口
        		chgForm.setDone(true);

            	close();
                
                return  ;
        	}
        	
        }

    	// 检查找零结果
        //calcChangResult();
    }
    
    public void chgExit()
    {
    	// 退出找零界面
    	chgBS.clearChange();
    	
		close();
    }
}
