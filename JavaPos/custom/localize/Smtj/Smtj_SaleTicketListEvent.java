package custom.localize.Smtj;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Logic.SaleTicketListBS;

public class Smtj_SaleTicketListEvent
{
    private PosTable tabTickList = null;
    private Text txtDate = null;
    private Text txtFphm = null;
    private Label lblmessge   = null;
    private Combo cmbDjlb = null;
    private Group group	   = null;	
    protected Shell shell = null;
    private SaleTicketListBS stlbs = null;
    private int currow = 0;
    private String lastDate = "";
    private String lastFphm = "";
    private int lastDjlb = 0;
    
    public Smtj_SaleTicketListEvent(Smtj_SaleTicketListForm stlf)
    {
        tabTickList 	= stlf.getTabTickList();
        shell       	= stlf.getShell();
        txtDate     	= stlf.getTxtDate();
        txtFphm	    	= stlf.getTxtFphm();
        cmbDjlb	    	= stlf.getcmbDjbl();
        group			= stlf.getGroup();
        lblmessge		= stlf.getLblMessge();
        
		//显示功能提示
		GlobalInfo.statusBar.setHelpMessage("'确认键'发送选中的交易小票");
		
        stlbs = CustomLocalize.getDefault().createSaleTicketListBS();

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
        key.event = event;
        key.inputMode = key.IntegerInput;

        tabTickList.addKeyListener(key);
        tabTickList.addSelectionListener(new SelectionAdapter()
        {
			public void widgetSelected(SelectionEvent arg0)
			{
				currow = tabTickList.getSelectionIndex();
			}
        });
        tabTickList.addMouseListener(new MouseAdapter()
        {
            public void mouseDoubleClick(MouseEvent mouseevent) 
            {
            	currow = tabTickList.getSelectionIndex();
            	keyReleased(null,GlobalVar.Enter);
            }
        });
        txtDate.addKeyListener(key);
        txtFphm.addKeyListener(key);
        cmbDjlb.addKeyListener(key);
        cmbDjlb.select(0);
        
        init();
    }

    private void init()
    {
        txtDate.setText(new ManipulateDateTime().getDateByEmpty());
        txtDate.selectAll();
        txtDate.setFocus();

        if (!isValidate(txtDate.getText()))
        {
            return;
        }

        if (stlbs.init(tabTickList,group,lblmessge,txtDate.getText(),txtFphm.getText(),cmbDjlb.getSelectionIndex()))
        {
        	tabTickList.setSelection(0);
            lastDate = txtDate.getText();
        }
    }

    public void keyPressed(KeyEvent e, int key)
    {
    	try
    	{
    		switch (key)
            {
	            case GlobalVar.ArrowUp:
	                if (e.getSource() == txtDate || e.getSource() == txtFphm)
	                {
	                	tabTickList.moveUp();
                        currow = tabTickList.getSelectionIndex();
	                }
	
	                break;
	            case GlobalVar.ArrowDown:
	            	if (e.getSource() == txtDate || e.getSource() == txtFphm)
	                {
	                	tabTickList.moveDown();
                        currow = tabTickList.getSelectionIndex();
	                }
	            break;
            }
    	}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    	}
    }

    public void keyReleased(KeyEvent e, int key)
    {
        String code = null;

        try
        {
            switch (key)
            {
            	case GlobalVar.Pay:
            		if (e.getSource() == txtDate)
            		{
            			e.data = "focus";
            			txtFphm.setFocus();
            		}
            		else if (e.getSource() == txtFphm)
            		{
            			e.data = "focus";
            			cmbDjlb.setFocus();
            		}
            		else if (e.getSource() == cmbDjlb)
            		{
            			e.data = "focus";
            			txtDate.setFocus();
            		}
            		break;
                case GlobalVar.Validation:
                case GlobalVar.Enter:
                	if (lastDate.equals(txtDate.getText()) && lastFphm.equals(txtFphm.getText()) && lastDjlb == cmbDjlb.getSelectionIndex() &&
                		tabTickList.getItemCount() > 0 && currow >= 0)
                	{
                        if ((currow >= 0) && (currow <= (tabTickList.getItemCount() - 1)))
                        {
                            tabTickList.setSelection(currow);

                            TableItem tableItem = tabTickList.getItem(tabTickList.getSelectionIndex());

                            if (tableItem.getText(0) != null)
                            {
                                code = tableItem.getText(0).substring(1);
                            }

                            if (key == GlobalVar.Enter)
                            {
                            	new Smtj_DisplaySaleTicketForm(txtDate.getText(),code);
                            }
                            else
                            {
                            	// 发送未上传小票
                            	String keytext = txtDate.getText() + "," + txtDate.getText() + "," + code;
                            	if (tableItem.getText(0).charAt(0) == ' ')
                            	{
	                            	if (TaskExecute.getDefault().sendAllSaleData(keytext))
	                            	{
	                            		stlbs.getSaleTicketList(tabTickList,group,lblmessge,txtDate.getText(),txtFphm.getText(),cmbDjlb.getSelectionIndex());
	                            	}
                            	}
                            	else
                            	{
        		            		int selkey = new MessageBox("["+code+"]交易小票已经上传过了!\n\n1 - 重传当前交易小票\n2 - 重传所有交易小票",null,false).verify();
        		            		if (selkey == GlobalVar.Key1 || selkey == GlobalVar.Key2)
                    				{
        		            			if (selkey == GlobalVar.Key2) keytext = keytext.substring(0,keytext.lastIndexOf(','));
                            			if (TaskExecute.getDefault().sendAllAgainData(StatusType.TASK_SENDINVOICE, keytext))
                            			{
                            				stlbs.getSaleTicketList(tabTickList,group,lblmessge,txtDate.getText(),txtFphm.getText(),cmbDjlb.getSelectionIndex());
                            			}
                    				}
                            	}
                            }
                            
                            tabTickList.setSelection(currow);
                        }
                	}
                	else
                	{
                		// 重新查找小票列表
	                    currow = 0;
	
	                    if (!isValidate(txtDate.getText()))
	                    {
	                        txtDate.selectAll();
	                        tabTickList.removeAll();
	
	                        return;
	                    }
	
	                    if (!stlbs.getSaleTicketList(tabTickList,group,lblmessge,txtDate.getText(),txtFphm.getText(),cmbDjlb.getSelectionIndex()))
	                    {
	                    	if (e.getSource() == txtDate) txtDate.selectAll();
	                    	else if (e.getSource() == txtFphm) txtFphm.selectAll();
	
	                        return;
	                    }
	
	                    //
	                    txtDate.selectAll();
	                    
	                    //
	                    tabTickList.setSelection(0);
	                    lastDate = txtDate.getText();
	                    lastFphm = txtFphm.getText();
	                    lastDjlb = cmbDjlb.getSelectionIndex();
	                    
	                    e.data = "focus";
	                    txtDate.setFocus();
                	}
                	break;
                case GlobalVar.PageUp:
                	if (e.getSource() == txtDate || e.getSource() == txtFphm)
                    {
                		tabTickList.PageUp();
                		currow = tabTickList.getSelectionIndex();
                    }

                    break;

                case GlobalVar.PageDown:
                	if (e.getSource() == txtDate || e.getSource() == txtFphm)
                    {
                		tabTickList.PageDown();
                		currow = tabTickList.getSelectionIndex();
                    }

                    break;

                case GlobalVar.Exit:
                    shell.close();
                    shell.dispose();
                    shell = null;

                    break;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    

    public boolean isValidate(String date)
    {
        if ((txtDate.getText() == null) || txtDate.getText().equals(""))
        {
            new MessageBox("日期不能为空,请重新输入!", null, false);

            return false;
        }

        if (txtDate.getText().length() < 8)
        {
            new MessageBox("不合法的日期输入,请检查是否有8位长\n请重新输入!", null, false);

            return false;
        }
        
        if(!txtDate.getText().equals(new ManipulateDateTime().getDateByEmpty())){
        	new MessageBox("收银员权限只允许查询当天数据!", null, false);

            return false;
        }
        return true;
    }
}
