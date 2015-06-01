package com.efuture.javaPos.UI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Logic.WithdrawBS;
import com.efuture.javaPos.UI.Design.WithdrawForm;
import com.swtdesigner.SWTResourceManager;


public class WithdrawEvent
{
    private PosTable tabBeFore = null;
    private Text txtTime = null;
    private StyledText txtCode = null;
    private Label lblCountAmount = null;
    private Label lblCountMoney = null;
    private Table tabInputMoney = null;
    private Text txtQueryDate = null;
    private Combo combopostime = null;
    private Shell shell = null;
    private WithdrawBS wb = null;
    private TableEditor tEditor = null;
    private int[] currentPoint = new int[] { 0, 0 };
    private NewKeyListener keyBeFore = null;
    private NewKeyListener keyTime = null;
    private NewKeyListener keyNewEditor = null;
    private Text newEditor = null;
    private int funFlag = 1;
    private int currAmount = 0;
    private boolean isLoadOk = true;
    private int currow = -1;
    
    public WithdrawEvent(WithdrawForm wf)
    {
        this.tabBeFore      = wf.getTabBeFore();
        this.txtTime        = wf.getTxtTime();
        this.txtQueryDate	= wf.getTxtQueryDate();
        this.txtCode        = wf.gettxtCode();
        this.lblCountAmount = wf.getLblCountAmount();
        this.lblCountMoney  = wf.getLblCountMoney();
        this.tabInputMoney  = wf.getTabInputMoney();
        this.combopostime   = wf.getCombopostime();
        this.shell          = wf.getShell();
        
        txtCode.setVisible(false);
        
        wb = CustomLocalize.getDefault().createWithdrawBS();

		//显示功能提示
		GlobalInfo.statusBar.setHelpMessage(Language.apply("按 '打印键' 重打印选择的缴款单/'确认键' 重发缴款单 '/付款键' 切换文本框") );
		
        tEditor                     = new TableEditor(tabInputMoney);
        tEditor.horizontalAlignment = SWT.LEFT;
        tEditor.grabHorizontal      = true;
        tEditor.minimumWidth        = 50;

        //设定键盘事件
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

        keyBeFore = new NewKeyListener();
        keyBeFore.event = event;
        tabBeFore.addKeyListener(keyBeFore);
        
        keyTime = new NewKeyListener();
        keyTime.event = event;
        txtTime.addKeyListener(keyTime);
        combopostime.addKeyListener(keyTime);
        txtQueryDate.addKeyListener(keyTime);
        keyTime.inputMode = keyTime.IntegerInput;
        
        tabInputMoney.addMouseListener(new MouseAdapter()
        {
            public void mouseDown(MouseEvent mouseevent) 
            {
            	Point selectedPoint = new Point (mouseevent.x, mouseevent.y);
            	Table table = (Table)mouseevent.getSource();
				int index = table.getTopIndex ();
				if (index < 0 ) return;
				while (index < table.getItemCount()) 
				{
					TableItem item = table.getItem (index);
					for (int i=0; i < table.getColumnCount(); i++) 
					{
						Rectangle rect = item.getBounds(i);
						if (i >= 1 && rect.contains (selectedPoint)) 
						{
							keyReleased(null,GlobalVar.Enter);
							
							currentPoint[0] = index;
							currentPoint[1] = i;
							table.setSelection(currentPoint[0]);
                            findLocation();
                            return;
						}
					}
					index++;
				}
            }
        });
        
        txtTime.addFocusListener(new FocusAdapter()
        {
            public void focusGained(FocusEvent focusevent) 
            {
            	funFlag = 1;
            }
        });
        
        combopostime.addFocusListener(new FocusAdapter()
        {
            public void focusGained(FocusEvent focusevent) 
            {
            	funFlag = 2;
            }
        });
        
        tabBeFore.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent selectionevent) 
            {
            	currow = ((Table)selectionevent.getSource()).getSelectionIndex();
				wb.getQueryPayinDetail(tabInputMoney, tabBeFore,lblCountAmount,lblCountMoney,currow,txtQueryDate.getText());
            }
        });
        
        keyNewEditor = new NewKeyListener();
        keyNewEditor.event = event;
        tabInputMoney.addKeyListener(keyNewEditor);
        
        init();
    }

    private void init()
    {
        try
        {
        	tabBeFore.removeAll();
        	tabInputMoney.removeAll();
        	
            //进入缴款界面
            if (!wb.comeInto(tabBeFore, tabInputMoney, txtCode, txtTime,combopostime,txtQueryDate,lblCountAmount, lblCountMoney))
            {
            	txtTime.setBackground(SWTResourceManager.getColor(255, 255, 255));
                isLoadOk = false;
              
                new MessageBox(Language.apply("缴款模板无法载入,请与系统管理员联系!"), null, false);
            }
            else
            {
                if (GlobalInfo.sysPara.isinputjkdate == 'Y')
                {
                	txtTime.setFocus();
                	txtTime.selectAll();
                }
                else
                {
                	txtTime.setBackground(SWTResourceManager.getColor(255, 255, 255));
                    txtTime.setEditable(false);
               
                	combopostime.setBackground(SWTResourceManager.getColor(255, 255, 255));
                	combopostime.setEnabled(false);
                    
                    currentPoint[1] = 1;
                    tabInputMoney.forceFocus();
                    tabInputMoney.select(currentPoint[0]);
                    
                    funFlag = 3;
                    
                    wb.AutoDisplayWithdrawBsMoney(tabInputMoney,this.lblCountAmount,this.lblCountMoney,txtTime.getText(),txtCode.getText());
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    public void findLocation()
    {
        Control oldEditor = tEditor.getEditor();

        if (oldEditor != null)
        {
            oldEditor.dispose();
        }

        if (tabInputMoney.getItemCount() <= 0)
        {
            return;
        }
        
        TableItem item = tabInputMoney.getItem(currentPoint[0]);

        if (item == null)
        {
            return;
        }

        newEditor = new Text(tabInputMoney, SWT.NONE | SWT.RIGHT);
        newEditor.setTextLimit(12);
        
        newEditor.setText(item.getText(currentPoint[1]));
        newEditor.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        newEditor.setFocus();
        tEditor.setEditor(newEditor, item, currentPoint[1]);
        newEditor.selectAll();
        
        if(currentPoint[1] == 1)
        {
        	keyNewEditor.inputMode=	keyNewEditor.IntegerInput;
        }
        else
        {
        	keyNewEditor.inputMode=	keyNewEditor.DoubleInput;
        }
            
        newEditor.addKeyListener(keyNewEditor);
    }

    public void keyPressed(KeyEvent e, int key)
    {
     
    }

    public void keyReleased(KeyEvent e, int key)
    {
        int keycode = key;
        Object widget;
        
        if (e != null) widget = e.getSource();
        else widget = tabInputMoney;
        
        try
        {
            switch (keycode)
            {
                case GlobalVar.ArrowUp:
                	
                	if (widget == this.txtTime || widget == this.txtQueryDate) 
                	{
                		if (currow > 0)
    					{
                    		//currow = currow - 1;
    						//setSelection(currow, false);
                			tabBeFore.moveUp();
                    		currow = tabBeFore.getSelectionIndex();
                    		
    						wb.getQueryPayinDetail(tabInputMoney, tabBeFore,this.lblCountAmount,this.lblCountMoney,currow,txtQueryDate.getText());
    						
    					}
                	}
                	else
                	{
	                	if (!isLoadOk) return;
	                	
	                	if (wb.isPayMoneyMove(currentPoint[0], currentPoint[1], 1))
	                    {
	                        if ((tabInputMoney.getItem(currentPoint[0]).getText(2).length() <= 0) &&!wb.isConfirmSaveAmout(currentPoint[0]))
	                        {
	                            newEditor.setText("");
	                        }
	
	                        if (currentPoint[1] == 2)
	                        {
	                            if (wb.isConfirmSaveAmout(currentPoint[0]) &&!wb.isConfirmSaveMoney(currentPoint[0]))
	                            {
	                                new MessageBox(Language.apply("必须输入缴款金额"), null, false);
	                            }
	                            else if (!wb.isConfirmSaveAmout(currentPoint[0]) &&wb.isConfirmSaveMoney(currentPoint[0]))
	                            {
	                                new MessageBox(Language.apply("请先输入缴款张数"), null, false);
	                            }
	                            else if (!wb.isConfirmSaveMoney(currentPoint[0]))
	                            {
	                                newEditor.setText("");
	                                currentPoint[0] = currentPoint[0] - 1;
	                                tabInputMoney.setSelection(currentPoint[0]);
	                                findLocation();
	                            }
	                            else
	                            {
	                                currentPoint[0] = currentPoint[0] - 1;
	                                tabInputMoney.setSelection(currentPoint[0]);
	                                findLocation();
	                            }
	                        }
	                        else
	                        {
	                            if (wb.isConfirmSaveAmout(currentPoint[0]) && !wb.isConfirmSaveMoney(currentPoint[0]))
	                            {
	                                new MessageBox(Language.apply("必须输入缴款金额"), null, false);
	                            }
	                            else if (!wb.isConfirmSaveAmout(currentPoint[0]) && wb.isConfirmSaveMoney(currentPoint[0]))
	                            {
	                                new MessageBox(Language.apply("请先输入缴款张数"), null, false);
	                            }
	                            else
	                            {
	                                currentPoint[0] = currentPoint[0] - 1;
	                                tabInputMoney.setSelection(currentPoint[0]);
	                                findLocation();
	                            }
	                        }
	                    }
                	}

                	break;
                case GlobalVar.ArrowDown:
                	if (widget == this.txtTime || widget == this.txtQueryDate) 
                	{
                		if ((currow < (tabBeFore.getItemCount() - 1)) && (tabBeFore.getItemCount() >= 0))
    	                {
                    		//currow = currow + 1;
    	                    //setSelection(currow , true);
                			tabBeFore.moveDown();
                    		currow = tabBeFore.getSelectionIndex();
    	                    wb.getQueryPayinDetail(tabInputMoney, tabBeFore,this.lblCountAmount,this.lblCountMoney,currow,txtQueryDate.getText());
    	                }
                	}
                	else
                	{
	                	if (!isLoadOk) return;
	                	
	                    if (wb.isPayMoneyMove(currentPoint[0], currentPoint[1], 2))
	                    {
	                        if ((tabInputMoney.getItem(currentPoint[0]).getText(2)
	                                              .length() <= 0) &&
	                                !wb.isConfirmSaveAmout(currentPoint[0]))
	                        {
	                            newEditor.setText("");
	                        }
	
	                        if (currentPoint[1] == 2)
	                        {
	                            if (wb.isConfirmSaveAmout(currentPoint[0]) && !wb.isConfirmSaveMoney(currentPoint[0]))
	                            {
	                                new MessageBox(Language.apply("必须输入缴款金额"), null, false);
	                            }
	                            else if (!wb.isConfirmSaveAmout(currentPoint[0]) && wb.isConfirmSaveMoney(currentPoint[0]))
	                            {
	                                new MessageBox(Language.apply("请先输入缴款张数"), null, false);
	                            }
	                            else if (!wb.isConfirmSaveMoney(currentPoint[0]))
	                            {
	                                newEditor.setText("");
	                                currentPoint[0] = currentPoint[0] + 1;
	                                tabInputMoney.setSelection(currentPoint[0]);
	                                findLocation();
	                            }
	                            else
	                            {
	                                currentPoint[0] = currentPoint[0] + 1;
	                                tabInputMoney.setSelection(currentPoint[0]);
	                                findLocation();
	                            }
	                        }
	                        else
	                        {
	                            if (wb.isConfirmSaveAmout(currentPoint[0]) && !wb.isConfirmSaveMoney(currentPoint[0]))
	                            {
	                                new MessageBox(Language.apply("必须输入缴款金额"), null, false);
	                            }
	                            else if (!wb.isConfirmSaveAmout(currentPoint[0]) && wb.isConfirmSaveMoney(currentPoint[0]))
	                            {
	                                new MessageBox(Language.apply("请先输入缴款张数"), null, false);
	                            }
	                            else
	                            {
	                                currentPoint[0] = currentPoint[0] + 1;
	                                tabInputMoney.setSelection(currentPoint[0]);
	                                findLocation();
	                            }
	                        }
	                    }
                	}
                break;
                case GlobalVar.ArrowLeft:
                	
                	if (widget == this.txtTime || widget == this.txtQueryDate) return ;	    
                	
                	if (!isLoadOk) return;
                	
                    if (currentPoint[1] == 2)
                    {
                        if (!wb.isPayMoneyMove(currentPoint[0],currentPoint[1], 3))
                        {
                            return;
                        }

                        if (!wb.isConfirmSaveMoney(currentPoint[0]))
                        {
                            newEditor.setText("");
                        }

                        currentPoint[1] = 1;
                        findLocation();
                    }

                break;
                case GlobalVar.ArrowRight:
                	
                	if (widget == this.txtTime || widget == this.txtQueryDate) return ;	  
                	
                	if (!isLoadOk) return;
                	
                    if (currentPoint[1] == 1)
                    {
                        if (!wb.isPayMoneyMove(currentPoint[0],currentPoint[1], 4))
                        {
                            return;
                        }

                        if (!wb.isConfirmSaveAmout(currentPoint[0]))
                        {
                            newEditor.setText("");
                        }
                        else
                        {
                            newEditor.setText(String.valueOf(currAmount));
                        }

                        currentPoint[1] = 2;
                        findLocation();
                    }

                    break;
                case GlobalVar.PageUp:
                	
                	if (widget != this.txtTime && widget != this.txtQueryDate) return ;	
                	    
					if (tabInputMoney.getSelectionIndex() > 0)
		            {
						tabInputMoney.setSelection(tabInputMoney.getSelectionIndex() - 1);
		            }
					break;
                case GlobalVar.PageDown:
                	if (widget != this.txtTime && widget != this.txtQueryDate) return ;	
             
                	if (tabInputMoney.getSelectionIndex() < (tabInputMoney.getItemCount() - 1) && tabInputMoney.getItemCount() >= 0)
			 	    {
                		tabInputMoney.setSelection(tabInputMoney.getSelectionIndex() + 1);
			 	    }
                	break;	
                case GlobalVar.Enter:
                	
                	if (widget == txtQueryDate)
                	{
                		if (!wb.checkDate(txtQueryDate.getText()))
                        {
                			txtQueryDate.selectAll();
                			txtQueryDate.forceFocus();
                            break;
                        }
                		
                		if (!wb.getCurrWithdrawInfo(tabBeFore,txtQueryDate.getText()))
                		{
                			txtQueryDate.selectAll();
                			txtQueryDate.forceFocus();
                			return ;
                		}
                		
                		txtQueryDate.selectAll();
                	}
                	else
                	{
	                	if (!isLoadOk) return;
	                	
	                    if (funFlag == 1)
	                    {
	                        if (!wb.checkDate(txtTime.getText()))
	                        {
	                        	txtTime.selectAll();
	                        	txtTime.forceFocus();
	                            break;
	                        }
	                        
	                        if (e != null) e.data = "";
	                        combopostime.setFocus();
	                        
	                        funFlag = 2;
	                        
	                        //txtTime.setBackground(SWTResourceManager.getColor(255, 255, 255));
	                        //txtTime.setEditable(false);
	                        /*
	                        wb.clearQueryDeatil(tabInputMoney,this.lblCountAmount,this.lblCountMoney);
	                        
	                        wb.AutoDisplayWithdrawBsMoney(tabInputMoney,this.lblCountAmount,this.lblCountMoney,txtTime.getText(),txtCode.getText());
	                        if (e != null) e.data = "";
	                        currentPoint[1] = 1;
	                        tabInputMoney.forceFocus();
	                        tabInputMoney.select(currentPoint[0]);
	                        findLocation();
	    
	                        funFlag = 2;
	                        */
	                    }
	                    else if (funFlag == 2)
	                    {
	                    	/*
	                        if (!wb.checkDate(txtTime.getText()))
	                        {
	                        	txtTime.selectAll();
	                        	txtTime.forceFocus();
	                            break;
	                        }
	                        */
	                        //txtTime.setBackground(SWTResourceManager.getColor(255, 255, 255));
	                        //txtTime.setEditable(false);
	                        
	                        wb.clearQueryDeatil(tabInputMoney,this.lblCountAmount,this.lblCountMoney);
	                        
	                        wb.AutoDisplayWithdrawBsMoney(tabInputMoney,this.lblCountAmount,this.lblCountMoney,txtTime.getText(),txtCode.getText());
	                        if (e != null) e.data = "";
	                        currentPoint[1] = 1;
	                        tabInputMoney.forceFocus();
	                        tabInputMoney.select(currentPoint[0]);
	                        findLocation();
	                        
	                        funFlag = 3;
	                    }
	                    else
	                    {
	                        if (e != null) e.doit = false;
	
	                        if (newEditor != null && newEditor.getText().length() <= 0)
	                        {
	                            if (!wb.isPayMoneyMove(currentPoint[0],currentPoint[1], 2))
	                            {
	                            	
	                            }
	                            else if (currentPoint[1] == 1)
	                            {
	                            	Text text = (Text) tEditor.getEditor();
	                                tEditor.getItem().setText(currentPoint[1], text.getText());
	                                
	                                tabInputMoney.getItem(currentPoint[0]).setText(2, "");
	                                wb.getPayMoney(currentPoint[0], 0, 0,txtCode.getText());
	                                wb.setConfirmSaveAmout(currentPoint[0], "N");
	                                wb.setConfirmSaveMoney(currentPoint[0], "N");
	                                currentPoint[0] = currentPoint[0] + 1;
	                                tabInputMoney.setSelection(currentPoint[0]);
	                                findLocation();
	                                currAmount     = 0;
	                            }
	                            else
	                            {
	                                if (tabInputMoney.getItem(currentPoint[0]).getText(1).trim().length() > 0)
	                                {
	                                    wb.getPayMoney(currentPoint[0],Integer.parseInt(tabInputMoney.getItem(currentPoint[0]).getText(1).trim()),0, txtCode.getText());
	                                    wb.setConfirmSaveMoney(currentPoint[0], "N");
	                               
	                                    new MessageBox(Language.apply("必须输入缴款金额"), null, false);
	                                }
	                                else
	                                {
	                                    currentPoint[1] = 1;
	                                    currentPoint[0] = currentPoint[0] + 1;
	                                    tabInputMoney.setSelection(currentPoint[0]);
	                                    findLocation();
	                                }
	                            }
	
	                            wb.getSum(lblCountAmount, lblCountMoney);
	
	                            return;
	                        }
	
	                        if (currentPoint[1] == 1)
	                        {
	                            String cash = null;
	                            
	                            if(newEditor.getText().trim().equals("-"))
	                            {
	                            	currentPoint[0] = currentPoint[0] + 1;
	                                tabInputMoney.setSelection(currentPoint[0]);
	                            	findLocation();
	                            	return ;
	                            }
	                            	
	                            if (Integer.parseInt(newEditor.getText()) == 0)
	                            {
	                                newEditor.setText("");
	                                tabInputMoney.getItem(currentPoint[0]).setText(1, "");
	                                tabInputMoney.getItem(currentPoint[0]).setText(2, "");
	                                wb.getPayMoney(currentPoint[0], 0, 0,txtCode.getText());
	                                wb.setConfirmSaveAmout(currentPoint[0], "N");
	                                wb.setConfirmSaveMoney(currentPoint[0], "N");
	                                
	                                if (currentPoint[0] >= (tabInputMoney.getItemCount()-1))
	                                {
	                                    currentPoint[0] = 0;
	                                    currentPoint[1] = 1;
	                                }
	                                
	                                tabInputMoney.setSelection(currentPoint[0]);
	                                findLocation();
	                                currAmount     = 0;
	                          
	                                wb.getSum(lblCountAmount, lblCountMoney);
	
	                                return;
	                            }
	
	                            if ((tabInputMoney.getItem(currentPoint[0]).getText(2) != null) && !tabInputMoney.getItem(currentPoint[0]).getText(2).equals(""))
	                            {
	                                cash = wb.getPayMoney(currentPoint[0],Integer.parseInt(newEditor.getText()),
	                                Double.parseDouble(tabInputMoney.getItem(currentPoint[0]).getText(2)),txtCode.getText());
	                            }
	                            else
	                            {
	                                cash = wb.getPayMoney(currentPoint[0],Integer.parseInt(newEditor.getText()),0, txtCode.getText());
	                            }
	
	                            tabInputMoney.getItem(currentPoint[0]).setText(1,String.valueOf(Integer.parseInt(newEditor.getText())));
	                            
	                            Text text = (Text) tEditor.getEditor();
	                            tEditor.getItem().setText(currentPoint[1], text.getText());
	                            
	                            wb.setConfirmSaveAmout(currentPoint[0], "Y");
	                           
	                            if (cash == null)
	                            {
	                                return;
	                            }
	                            else if ((Double.parseDouble(cash) == 0) && !wb.isBase(currentPoint[0]))
	                            {
	                                currentPoint[1] = 2;
	                                findLocation();
	                            }
	                            else if ((Double.parseDouble(cash) != 0) && !wb.isBase(currentPoint[0]))
	                            {
	                                currentPoint[1] = 2;
	                                findLocation();
	                            }
	                            else
	                            {
	                            	if (currentPoint[0] < tabInputMoney.getItemCount() - 1)
	                    			{
	                            		wb.setConfirmSaveMoney(currentPoint[0], "Y");
	                            		tabInputMoney.getItem(currentPoint[0]).setText(2, cash);
	                            		currentPoint[0] = currentPoint[0] + 1;
	                            		tabInputMoney.setSelection(currentPoint[0]);
	                            		findLocation();
	                    			}
	                            	else
	                            	{
	                            		wb.setConfirmSaveMoney(currentPoint[0], "Y");
	                            		tabInputMoney.getItem(currentPoint[0]).setText(2, cash);
	                            	}
	                            }
	                        }
	                        else
	                        {
	                            String amount = tabInputMoney.getItem(currentPoint[0]).getText(1);
	                            
	                            if ((amount == null) || amount.equals(""))
	                            {
	                                if (newEditor.getText().charAt(0) != '.')
	                                {
	                                    new MessageBox(Language.apply("请先输入缴款张数"), null, false);
	                                    newEditor.setText("");
	                                    wb.setConfirmSaveMoney(currentPoint[0], "Y");
	                                    currentPoint[1] = 1;
	                                    tabInputMoney.setSelection(currentPoint[0]);
	                                    findLocation();
	
	                                    return;
	                                }
	                                else
	                                {
	                                    newEditor.setText("");
	                                    currentPoint[1] = 1;
	                                    currentPoint[0] = currentPoint[0] + 1;
	                                    tabInputMoney.setSelection(currentPoint[0]);
	                                    findLocation();
	
	                                    return;
	                                }
	                            }
	                            else if (newEditor.getText().charAt(0) == '.')
	                            {
	                                new MessageBox(Language.apply("必须输入缴款金额"), null, false);
	                                newEditor.selectAll();
	
	                                return;
	                            }
	
	                            int j = 0;
	
	                            for (int i = 0; i < newEditor.getText().length();i++)
	                            {
	                                if (newEditor.getText().charAt(i) == '.')
	                                {
	                                    if (j >= 1)
	                                    {
	                                        new MessageBox(Language.apply("必须输入缴款金额"), null, false);
	                                        newEditor.selectAll();
	
	                                        return;
	                                    }
	
	                                    j = j + 1;
	                                }
	                            }
	
	                            if (Math.abs(Double.parseDouble(newEditor.getText())) <= 0)
	                            {
	                                new MessageBox(Language.apply("必须输入缴款金额"), null, false);
	                                newEditor.selectAll();
	
	                                return;
	                            }
	                            
	                            if(newEditor.getText().trim().equals("-"))
	                            {
	                            	new MessageBox(Language.apply("必须输入缴款金额"), null, false);
	                                newEditor.selectAll();
	                            	return ;
	                            }
	                            
	                            String cash = wb.getPayMoney(currentPoint[0],Integer.parseInt(amount),Double.parseDouble(newEditor.getText()),txtCode.getText());
	                            
	                            Text text = (Text) tEditor.getEditor();
	                            tEditor.getItem().setText(currentPoint[1], text.getText());
	                            
	                            wb.setConfirmSaveMoney(currentPoint[0], "Y");
	
	                            if (cash == null)
	                            {
	                            	
	                            }
	                            else
	                            {
	                                if (currentPoint[0] == (tabInputMoney.getItemCount() - 1))
	                                {
	                                    currentPoint[0] = 0;
	                                    currentPoint[1] = 1;
	                                    tabInputMoney.setSelection(currentPoint[0]);
	                                    findLocation();
	                                }
	                                else
	                                {
	                                    tabInputMoney.getItem(currentPoint[0]).setText(2, cash);
	                                    currentPoint[0]++;
	                                    currentPoint[1] = 1;
	                                    tabInputMoney.setSelection(currentPoint[0]);
	                                    findLocation();
	                                }
	                            }
	                        }
	
	                        wb.getSum(lblCountAmount, lblCountMoney);
	                    }
                	}

                    break;
                  
                case GlobalVar.Back:
                	if (!isLoadOk) return;
                	
                    e.doit = false;
                    NewKeyListener.addKey(newEditor, "-");
                    break;
                    
                case GlobalVar.Minu:
                	if (!isLoadOk) return;
                	
                    e.doit = false;
                    NewKeyListener.addKey(newEditor, "-");
                	break;

                case GlobalVar.Validation:
                	if (widget != newEditor)
                	{
                		TableItem tableItem = null;
                		
                		if (tabBeFore.getItemCount() > 0 && currow >= 0 && (currow <= (tabBeFore.getItemCount() - 1)))
                    	{
                    		tableItem = tabBeFore.getItem(currow);
                    		String date = txtQueryDate.getText();                		
                    		String keytext =  date + "," + date + "," + tableItem.getText(0).substring(1).trim();
                    		
                    		if (tableItem.getText(0).charAt(0) == ' ')
    	                	{
                    			if (TaskExecute.getDefault().sendAllPayinData(keytext))
                    			{
                    				init();
                    			}
    	                	}
                    		else
                    		{
    		            		int selkey = new MessageBox(Language.apply("[{0}]号缴款单已经上传过了!\n\n1 - 重传当前缴款单\n2 - 重传所有缴款单", new Object[]{tableItem.getText(0).substring(1).trim()}),null,false).verify();
    		            		if (selkey == GlobalVar.Key1 || selkey == GlobalVar.Key2)
                				{
    		            			if (selkey == GlobalVar.Key2) keytext = keytext.substring(0,keytext.lastIndexOf(','));
                    				if (TaskExecute.getDefault().sendAllAgainData(StatusType.TASK_SENDPAYJK, keytext))
    		            			{
                    					init();
    		            			}
                				}
                    		}  
                    	}
                	}
                	else
                	{
                		e.doit = false;
	                    if (!isLoadOk) return;
	                	keyReleased(e,GlobalVar.Enter);
	                    if (wb.validationFunc(txtCode, txtTime, combopostime, lblCountAmount, lblCountMoney))
	                    {
	                    	if (GlobalInfo.sysPara.iscloseJkUI == 'Y')
	                    	{
	                    		shell.close();
			                    shell.dispose();
			                    shell = null;
	                    	}
	                    	else
	                    	{
	                    		wb = CustomLocalize.getDefault().createWithdrawBS();
	                    		init();
	                    	}
	                    	
		                    
	                    }
                	}
                	break;
                case GlobalVar.Print:
                	if (!isLoadOk) return;
                	if(currow < 0)
                	{
                		new MessageBox(Language.apply("至少要选择一项缴款单"), null, false);
                		break;
                	}
                	
                	if (!wb.checkReprint())
                	{
                		new MessageBox(Language.apply("当前缴款单未输入完全时不能进行其他操作"));
                		break;
                	}
                	
                	TableItem tableItem = tabBeFore.getItem(currow);
                	
                	wb.printPayJk(Integer.parseInt(tableItem.getText(0).substring(1).trim()),tableItem.getText(2),txtQueryDate.getText());
                	
                	break;
                case GlobalVar.Del:
                	if (!isLoadOk) return;
                	if(currow < 0)
                	{
                		new MessageBox(Language.apply("至少要选择一项缴款单"), null, false);
                		break;
                	}
                	
                	if (!wb.checkReprint())
                	{
                		new MessageBox(Language.apply("当前缴款单未输入完全时不能进行其他操作"));
                		return;
                	}
                	
                    if (new MessageBox(Language.apply("你确定要红冲此[{0}]号缴款单吗?", new Object[]{tabBeFore.getItem(currow).getText(0).substring(1).trim()}), null, true).verify() != GlobalVar.Key1)
                    {
                        return;
                    }
                	
                	TableItem tableItem1 = tabBeFore.getItem(currow);
                	
                	wb.hcFunction(Integer.parseInt(tableItem1.getText(0).substring(1).trim()),tableItem1.getText(2), txtCode.getText(), txtTime.getText(),txtQueryDate.getText());
                	
                    shell.close();
                    shell.dispose();
                    shell = null;
                	
                	break;
                case GlobalVar.Pay:
                	if (widget != txtTime)
	        		{
                		funFlag = 1;
	        			e.data = "focus";
	        			
	        			if (GlobalInfo.sysPara.isinputjkdate == 'Y')
	        			{
	        				txtTime.forceFocus();
	        				txtTime.selectAll();
	        				txtTime.setEditable(true);
	        				
	        				Control oldEditor = tEditor.getEditor();

	        		        if (oldEditor != null)
	        		        {
	        		            oldEditor.dispose();
	        		        }
	        			}
	        			else
	        			{
	        				if (widget == txtQueryDate)
	        				{
	        					wb.clearQueryDeatil(tabInputMoney,this.lblCountAmount,this.lblCountMoney);
	        					wb.AutoDisplayWithdrawBsMoney(tabInputMoney, lblCountAmount, lblCountMoney,txtTime.getText(),txtCode.getText());
	        					
	        					e.data = "";
	 	                        currentPoint[1] = 1;
	 	                        tabInputMoney.forceFocus();
	 	                        tabInputMoney.select(currentPoint[0]);
	 	                        findLocation();
	 	                       
	 	                        funFlag = 3;
	        				}
	        				else
	        				{
	        					funFlag = 1;
		        				txtQueryDate.forceFocus();
		                		txtQueryDate.selectAll();
		                		
		                		Control oldEditor = tEditor.getEditor();
	
		        		        if (oldEditor != null)
		        		        {
		        		            oldEditor.dispose();
		        		        }
	        				}
	        			}
	        		}
                	else if (widget == txtTime)
                	{
                		e.data = "focus";
                		txtQueryDate.forceFocus();
                		txtQueryDate.selectAll();
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
/*    
    public void setSelection(int index, boolean flag)
    {
        setSelection(index, -1, flag);
    }

    public void setSelection(int index, int indexb, boolean flag)
    {
        int i = 0;

        if (flag)
        {
            i = index - 1;
        }
        else
        {
            i = index + 1;
        }

        TableItem item = null;

        if (indexb < 0)
        {
            if ((i >= 0) && (i < tabBeFore.getItemCount()))
            {
                item = tabBeFore.getItem(i);
                item.setBackground(SWTResourceManager.getColor(255, 255, 255));
                item.setForeground(SWTResourceManager.getColor(0, 0, 0));
            }
        }
        else
        {
            if ((indexb >= 0) && (i < tabBeFore.getItemCount()))
            {
                item = tabBeFore.getItem(indexb);
                item.setBackground(SWTResourceManager.getColor(255, 255, 255));
                item.setForeground(SWTResourceManager.getColor(0, 0, 0));
            }
        }

        if ((index < tabBeFore.getItemCount()) && (index >= 0))
        {
            item = tabBeFore.getItem(index);
            item.setBackground(SWTResourceManager.getColor(43, 61, 219));
            item.setForeground(SWTResourceManager.getColor(255, 255, 255));
        }

        showSelection(index);
    }

    public void showSelection(int curRow)
    {
        if ((curRow < tabBeFore.getItemCount()) && (curRow >= 0))
        {
            TableItem item = tabBeFore.getItem(curRow);
            tabBeFore.showItem(item);
        }
    }
*/    
}
