package com.efuture.javaPos.UI;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.SaleMemoBS;
import com.efuture.javaPos.Struct.KeyValueDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SaleMemoInfo;
import com.efuture.javaPos.UI.Design.SaleMemoFilterForm;
import com.efuture.javaPos.UI.Design.SaleMemoForm;
import com.swtdesigner.SWTResourceManager;

public class SaleMemoEvent
{
	public TableEditor editor = null;
	public Combo cbxNewEditor = null;
	public Text txtNewEditor = null;
	public int[] currentPoint = new int[] { 1, 0 };
	
	public SaleMemoBS salememobs = null;
	
	public SaleMemoForm memoForm = null;
	
	public Vector vcdefault = null;

	public SaleMemoEvent(SaleMemoForm saleMemoForm, SaleHeadDef saleHead, Vector salegoods, Vector salepay, Vector vcdefault, int flag)
	{
		init(saleMemoForm, saleHead, salegoods, salepay, vcdefault, flag);
	}

	// 初始化
	public void init(SaleMemoForm saleMemoForm, SaleHeadDef saleHead, Vector salegoods, Vector salepay, Vector vcdefault, int flag)
	{
		this.memoForm = saleMemoForm;
		this.vcdefault = vcdefault;

		editor = new TableEditor(this.memoForm.table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;

		selectEvent se = new selectEvent();

		this.memoForm.btn_printandsend.addSelectionListener(se);
		this.memoForm.btn_resend.addSelectionListener(se);

		this.memoForm.table.addMouseListener(new MouseListener()
		{
			public void mouseDoubleClick(MouseEvent e)
			{
			}

			public void mouseDown(MouseEvent event)
			{
				Point pt = new Point(event.x, event.y);
				int index = memoForm.table.getTopIndex();
				boolean done = false;

				while (index < memoForm.table.getItemCount())
				{
					final TableItem item = memoForm.table.getItem(index);

					for (int i = 0; i < memoForm.table.getColumnCount(); i++)
					{
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt))
						{
							currentPoint[1] = index;
							currentPoint[0] = i;
							findLocation();
							done = true;
							break;
						}
					}
					if (done) break;
					index++;
				}
			}

			public void mouseUp(MouseEvent e)
			{
			}
		});
		
		// 生成刚加入tableItem
		this.memoForm.table.redraw();
		//Rectangle rec = Display.getDefault().getPrimaryMonitor().getClientArea();
		this.memoForm.shell.setLocation((GlobalVar.rec.x / 2) - (this.memoForm.shell.getSize().x / 2), (GlobalVar.rec.y / 2) - (this.memoForm.shell.getSize().y / 2));
		
		this.memoForm.txt_syjh.setText(saleHead.syjh);
		this.memoForm.txt_syjh.setEnabled(false);
		this.memoForm.txt_fphm.setText(String.valueOf(saleHead.fphm));
		this.memoForm.txt_fphm.setEnabled(false);
		this.memoForm.lbl_Netbz.setVisible(false);
		
		
		salememobs = new SaleMemoBS(memoForm.shell, saleHead, salegoods, salepay, vcdefault, memoForm.table, flag);
		
		// 销售成交前调用
		if (flag == 0)
		{
			this.memoForm.btn_resend.setEnabled(false);
		}
		// 小票信息中调用
		else if (flag == 1)
		{
			readFromDataBase();
		}
		
		open();
		
		InitComboData();
		
		findLocation();
	}

	public boolean open()
	{
		try
		{
			if (salememobs.readFormIni(salememobs.vcCfg))
			{
				TableItem item = null;
				SaleMemoInfo memoInfo;
				for (int i = 0; i < salememobs.vcCfg.size(); i++)
				{
					item = new TableItem(this.memoForm.table, SWT.NULL);
					memoInfo = (SaleMemoInfo) salememobs.vcCfg.elementAt(i);
					item.setText(0, memoInfo.desc);
				}
				
				for (int i = 0; i < salememobs.vcCfg.size(); i++)
				{
					memoInfo = (SaleMemoInfo) salememobs.vcCfg.elementAt(i);
					RefreshComboTable(memoInfo);
				}
				
				return true;
			}
			
			return false;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	// ComBox修改数据之后,修改其它与之关联的填写项
	public void RefreshComboAssoData(SaleMemoInfo memoInfo)
	{
		if (!memoInfo.type.equals(SaleMemoInfo.COMBOTYPE)) return;
		
		salememobs.RefreshComboAssoData(memoInfo);
		
		// 更新完结构体里面的内容后,再更新table控件里面的值
		RefreshComboAssoTable(memoInfo);
	}
	
	// 获得ComBox里面的内容
	public void RefreshComboData(SaleMemoInfo memoInfo)
	{
		if (!memoInfo.type.equals(SaleMemoInfo.COMBOTYPE)) return;
		
		salememobs.RefreshComboData(memoInfo);
		
		RefreshComboTable(memoInfo);
	}
	
	// 刷新ComBox改变之后,对应Table的值
	public void RefreshComboTable(SaleMemoInfo memoInfo)
	{
		int index = salememobs.vcCfg.indexOf(memoInfo);
		
		if (index < 0) return;
		
		if (memoInfo.type.equals(SaleMemoInfo.TEXTTYPE))
		{
			memoForm.table.getItem(index).setText(currentPoint[0],memoInfo.curcontent);
		}
		else if (memoInfo.type.equals(SaleMemoInfo.COMBOTYPE))
		{
			if (memoInfo.curselindex < 0 && memoInfo.content.vccontent.size() == 1)
			{
				// 如果只有一项,并且没有选择,则默认选择这一项
				memoInfo.curselindex = 0;
				
				KeyValueDef kvd = (KeyValueDef)memoInfo.content.vccontent.get(memoInfo.curselindex);
				
				memoForm.table.getItem(index).setText(currentPoint[0],kvd.key+"-"+kvd.value);
			}
			else if(memoInfo.curselindex >= 0)
			{
				KeyValueDef kvd = (KeyValueDef)memoInfo.content.vccontent.get(memoInfo.curselindex);
				
				memoForm.table.getItem(index).setText(currentPoint[0],kvd.key+"-"+kvd.value);
			}
			else if(memoInfo.curselindex < 0)
			{
				memoForm.table.getItem(index).setText(currentPoint[0],"");
			}
		}
		
		RefreshComboAssoTable(memoInfo);
	}
	
	// 刷新ComBox改变之后,对应与之关联项的值Table的值
	public void RefreshComboAssoTable(SaleMemoInfo memoInfo)
	{
		Vector vc = salememobs.GetAssociate(memoInfo);
		
		for(int i = 0;i < vc.size();i++)
		{
			RefreshComboTable((SaleMemoInfo)vc.get(i));
		}
	}
	
	public void InitComboData()
	{
		// 初始化ComBox的值,只初始化没有关联其它输入项的Combox
		ProgressBox pb = null;
		
		try
		{
			pb = new ProgressBox();
	        pb.setText(Language.apply("正在获得信息,请等待..."));
	        
	        salememobs.InitComboData();
		}
		catch(Exception ex)
		{
			MessageBox msg = new MessageBox(memoForm.shell);
			msg.setMessage(ex.getMessage());
			msg.open();
		}
		finally
		{
            if (pb != null)
            {
                pb.close();
                pb = null;
            }
		}
	}
	
	public void findLocation()
	{
		Control oldEditor = editor.getEditor();

		if (oldEditor != null)
		{
			oldEditor.dispose();
		}

		if (salememobs.vcCfg == null || salememobs.vcCfg.size() < 1) return;
		if (this.memoForm.table.getItemCount() <= 0) return;

		TableItem items = this.memoForm.table.getItem(currentPoint[1]);

		SaleMemoInfo memoInfo;

		if (currentPoint[0] == 1)
		{
			memoInfo = (SaleMemoInfo) salememobs.vcCfg.get(currentPoint[1]);
			// 文本框控件类型
			if (memoInfo.type.equalsIgnoreCase(SaleMemoInfo.TEXTTYPE))
			{
				txtNewEditor = new Text(this.memoForm.table, SWT.LEFT | SWT.BORDER);
				txtNewEditor.setText(items.getText(currentPoint[0]));
				editor.setEditor(txtNewEditor, items, currentPoint[0]);
				txtNewEditor.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
				
				txtNewEditor.addKeyListener(new KeyListener()
	            {
	                public void keyPressed(KeyEvent e)
	                {
	                    try
	                    {
	                        switch (e.keyCode)
	                        {
	                            case SWT.ARROW_UP:

	                                if (currentPoint[1] == 0)
	                                {
	                                    return;
	                                }
	                                else
	                                {
	                                    currentPoint[1]--;
	                                }

	                                memoForm.table.setSelection(currentPoint[0]);
	                                findLocation();

	                                break;

	                            case SWT.ARROW_DOWN:

	                                if (currentPoint[1] == (memoForm.table.getItemCount() -
	                                                           1))
	                                {
	                                    return;
	                                }
	                                else
	                                {
	                                    currentPoint[1]++;
	                                }

	                                memoForm.table.setSelection(currentPoint[0]);
	                                findLocation();

	                                break;
	                        }
	                    }
	                    catch (Exception e1)
	                    {
	                        e1.printStackTrace();
	                    }
	                }

	                public void keyReleased(KeyEvent e)
	                {
	                    try
	                    {
	                        if ((e.keyCode == 13 || e.keyCode == 16777296))
	                        {

                                if (currentPoint[1] == (memoForm.table.getItemCount() -
                                                           1))
                                {
                                    return;
                                }
                                else
                                {
                                    currentPoint[1]++;
                                }

                                memoForm.table.setSelection(currentPoint[0]);
                                findLocation();
	                        }
	                        
	                    	if (e.keyCode == SWT.F11)
	                    	{
	                    		doPrintSendBtn();
	                    	}
	                    	
	                    	if (e.keyCode == SWT.F12)
	                    	{
	                    		memoForm.shell.close();
	                    		memoForm.shell.dispose();
	                    	}
	                    }
	                    catch (Exception er)
	                    {
	                        er.printStackTrace();
	                    }
	                }
	            });
				
				
				
				txtNewEditor.selectAll();
				txtNewEditor.addModifyListener(new ModifyListener()
				{
					public void modifyText(ModifyEvent e)
					{
						Text text = (Text) editor.getEditor();
						editor.getItem().setText(currentPoint[0], text.getText());
						
						// 保存值到结构体当中
						((SaleMemoInfo)salememobs.vcCfg.get(currentPoint[1])).curcontent = text.getText();
					}
				});
				txtNewEditor.setFocus();

				// 限制最大输入长度
				if (memoInfo.maxLength > 0)
				{
					txtNewEditor.setTextLimit(memoInfo.maxLength);
				}
			}
			// 下拉框控件类型
			else if (memoInfo.type.equalsIgnoreCase(SaleMemoInfo.COMBOTYPE))
			{
				cbxNewEditor = new Combo(this.memoForm.table, SWT.LEFT | SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);

				// 通过命令获得的则增加"查找"项
				if (memoInfo.content.type.equals(SaleMemoInfo.CMDGET))
				{
					cbxNewEditor.add(Language.apply("[查找其他]"));
				}
				
				for (int i = 0;i < memoInfo.content.vccontent.size();i++)
				{
					KeyValueDef kvd = (KeyValueDef)memoInfo.content.vccontent.get(i);
					
					cbxNewEditor.add(kvd.key + "-" + kvd.value);
				}
				
				cbxNewEditor.setText(items.getText(currentPoint[0]));
				editor.setEditor(cbxNewEditor, items, currentPoint[0]);
				cbxNewEditor.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
				cbxNewEditor.setVisibleItemCount(20);
				
				if (memoInfo.curselindex >=0)
				{
					if (memoInfo.content.type.equals(SaleMemoInfo.CFGGET))
					{
						cbxNewEditor.select(memoInfo.curselindex);
					}
					else if (memoInfo.content.type.equals(SaleMemoInfo.CMDGET))
					{
						cbxNewEditor.select(memoInfo.curselindex + 1);
					}
					
					RefreshComboTable(memoInfo);
				}
				
				cbxNewEditor.addModifyListener(new ModifyListener()
				{
					public void modifyText(ModifyEvent e)
					{
						Combo comb = (Combo) editor.getEditor();
						editor.getItem().setText(currentPoint[0], comb.getText());
						
						if (cbxNewEditor.getSelectionIndex() == 0)
						{
							SaleMemoInfo smi = (SaleMemoInfo)salememobs.vcCfg.get(currentPoint[1]);
							if (smi.content.type.equals(SaleMemoInfo.CMDGET))
							{
								editor.getItem().setText(currentPoint[0], "");
							}
						}
					}
				});
				selectEvent se = new selectEvent();
				cbxNewEditor.addSelectionListener(se);
				cbxNewEditor.setFocus();
			}
		}
	}

	class selectEvent implements SelectionListener
	{
		public selectEvent()
		{
		}

		public void widgetDefaultSelected(SelectionEvent e)
		{
		}

		public void widgetSelected(SelectionEvent e)
		{
			if (e.widget.equals(cbxNewEditor))
			{
				if (currentPoint[1] >= 0)
				{
					ProgressBox pb = null;
					
					try
					{
						SaleMemoInfo smi = (SaleMemoInfo) salememobs.vcCfg.get(currentPoint[1]);
						
						// 通过命令获得的有"查找"项
						if (smi.content.type.equals(SaleMemoInfo.CMDGET))
						{
							// 如果选择不变,则不进行下面的流程
							if (cbxNewEditor.getSelectionIndex() != 0 && smi.curselindex == cbxNewEditor.getSelectionIndex() - 1)
							{
								return;
							}
							
							if (cbxNewEditor.getSelectionIndex() == 0)
							{
								// 弹出查找窗口进行查找
								new SaleMemoFilterForm(salememobs,smi);
								
								findLocation();
							}
							else
							{
								smi.curselindex = cbxNewEditor.getSelectionIndex() - 1;
							}
						}
						else if (smi.content.type.equals(SaleMemoInfo.CFGGET))
						{
							// 如果选择不变,则不进行下面的流程
							if (smi.curselindex == cbxNewEditor.getSelectionIndex())
							{
								return;
							}
							
							smi.curselindex = cbxNewEditor.getSelectionIndex();
						}
						
						pb = new ProgressBox();
				        pb.setText(Language.apply("正在获得信息,请等待..."));
				        
						RefreshComboAssoData(((SaleMemoInfo)salememobs.vcCfg.get(currentPoint[1])));
					}
					catch(Exception ex)
					{
						MessageBox msg = new MessageBox(memoForm.shell);
						msg.setMessage(ex.getMessage());
						msg.open();
					}
					finally
					{
			            if (pb != null)
			            {
			                pb.close();
			                pb = null;
			            }
					}
				}
			}
			
			if (e.widget.equals(memoForm.btn_printandsend))
			{
				doPrintSendBtn();
			}
			
			if (e.widget.equals(memoForm.btn_resend))
			{
				doReSendBtn();
			}
		}
	}
	
	// 修改送网标志
	public void changeNetbzLable (char flag)
	{
		this.memoForm.lbl_Netbz.setVisible(true);
		
		switch(flag)
		{
			case 'Y':
				this.memoForm.lbl_Netbz.setText(Language.apply("已送网"));
				break;
			case 'N':
				this.memoForm.lbl_Netbz.setText(Language.apply("未送网"));
				this.memoForm.lbl_Netbz.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
				this.memoForm.lbl_Netbz.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
				break;
			case 'S':
				break;
		}
	}
	
	public void doPrintSendBtn ()
	{
		changeNetbzLable(salememobs.send());
	}
	
	public void doReSendBtn ()
	{
		changeNetbzLable(salememobs.reSend());
	}
	
	// 从本地day数据库中读取小票附加信息
	public boolean readFromDataBase ()
	{
		char issuc = salememobs.readFromDataBase();
		changeNetbzLable(issuc);
		
		return issuc=='Y';
	}
}
