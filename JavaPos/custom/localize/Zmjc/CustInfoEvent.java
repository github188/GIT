package custom.localize.Zmjc;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.ParaNodeDef;
import com.swtdesigner.SWTResourceManager;

public class CustInfoEvent
{
	TableEditor editor;
	private int[] currentPoint = new int[] { 0, 2 };
	private int[] currentFlight = new int[] { 0};
	private Table table = null;
	private Table optionTable = null;
	private Shell shell = null;
	private Text newEditor = null;
	private CustInfoBS custBS = null;
	private CustInfoForm custForm;
	private Zmjc_SaleBS saleBS;
	private StringBuffer DlInfo;//大类限额信息
	
	private boolean choiceTable = false;//下方选项表是否被选择
	private boolean validOptionTable = false;//航班表是否有内容
	private String flag = CustInfoDef.CUST_SCNUMBER;//"SCNATIONALITY"：国籍；"SCNUMBER"：航班号 ； 当前行标记

	public CustInfoEvent(CustInfoForm custForm, Zmjc_SaleBS saleBS, StringBuffer DlInfo)
	{
		this.table = custForm.table;
		this.optionTable = custForm.optionTable;
		this.shell = custForm.shell;
		this.custForm = custForm;
		this.saleBS = saleBS;
		this.DlInfo = DlInfo;

		shell.setBounds((GlobalVar.rec.x - shell.getSize().x) / 2, (GlobalVar.rec.y - shell.getSize().y) / 2, shell.getSize().x, shell.getSize().y
				- GlobalVar.heightPL);

		shell.setBounds((GlobalVar.rec.x - shell.getSize().x) / 2, (GlobalVar.rec.y - shell.getSize().y) / 2, shell.getSize().x, shell.getSize().y
				- GlobalVar.heightPL);

		custBS = new CustInfoBS();//CustomLocalize.getDefault().createPreMoneyBS();

		editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;
		
		table.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
				// TODO 自动生成方法存根
			}

			public void widgetSelected(SelectionEvent e)
			{
				// TODO 自动生成方法存根
				
				table.addMouseListener(new MouseAdapter()
				{
					public void mouseDown(MouseEvent mouseevent)
					{
						Point selectedPoint = new Point(mouseevent.x, mouseevent.y);
						Table table = (Table) mouseevent.getSource();
						int index = table.getTopIndex();
						if (index < 0) return;
						while (index < table.getItemCount())
						{
							TableItem item = table.getItem(index);
							for (int i = 0; i < table.getColumnCount(); i++)
							{
								Rectangle rect = item.getBounds(i);
								if (i == 2 && rect.contains(selectedPoint))
								{
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
			}
		});
		
		loadInitData();
	}

	public void findLocation()
	{
		Control oldEditor = editor.getEditor();

		if (oldEditor != null)
		{
			oldEditor.dispose();
		}

		if (table.getItemCount() <= 0) { return; }

		TableItem item = table.getItem(currentPoint[0]);

		if (item == null) { return; }

		newEditor = new Text(table, SWT.LEFT | SWT.BORDER);
		//newEditor.setTextLimit(12);

		newEditor.setText(item.getText(currentPoint[1]));
		editor.setEditor(newEditor, item, currentPoint[1]);
		newEditor.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		newEditor.selectAll();
		newEditor.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				Text text = (Text) editor.getEditor();
				editor.getItem().setText(currentPoint[1], text.getText());
			}
		});

		Object itemKey = editor.getItem().getData("key");//获取当前焦点行的KEY
		//当前行是航班号时,加载所输入的航班号信息
		if( itemKey.equals( CustInfoDef.CUST_SCNUMBER))
		{
			loadFlight();
		}
		//当前行是国籍时,加载所输入的航班号信息
		if ( itemKey.equals( CustInfoDef.CUST_SCNATIONALITY))
		{
			loadNationality();
		}
		
		// 增加监听器
		NewKeyEvent event = new NewKeyEvent()
		{
			public void keyDown(KeyEvent e, int key)
			{
				keyPressed(e, key);
			}

			public void keyUp(KeyEvent e, int key)
			{
				keyReleased(e, key);
//				keyPressed(e);//内部按键
			}
		};

		ZmjcCust_NewKeyListener key = new ZmjcCust_NewKeyListener();
		key.event = event;
		key.inputMode = key.inputMode;//.DoubleInput;
		newEditor.addKeyListener(key);
		newEditor.setFocus();
		
	}
	
	//上下翻页
	public void keyPressed(KeyEvent e, int key)
	{
		try
		{
			switch (key)
			{
				case GlobalVar.ArrowUp:
					
					if(choiceTable)
					{
						if (currentFlight[0] == 0)
						{
							return;
						}
						else
						{
							currentFlight[0]--;
						}
						optionTable.setSelection(currentFlight[0]);
					}
					else
					{
						if (currentPoint[0] == 0)
						{
							return;
						}
						else
						{
							currentPoint[0]--;
						}
						table.setSelection(currentPoint[0]);
						findLocation();
					}
					break;

				case GlobalVar.ArrowDown:

					if(choiceTable)
					{
						if (currentFlight[0] == (optionTable.getItemCount() - 1))
						{
							return;
						}
						else
						{
							currentFlight[0]++;
						}
						optionTable.setSelection(currentFlight[0]);
					}
					else
					{
						if (currentPoint[0] == (table.getItemCount() - 1))
						{
							return;
						}
						else
						{
							currentPoint[0]++;
						}
						table.setSelection(currentPoint[0]);
						findLocation();
					}
					break;
				
				case GlobalVar.PageUp:
					
					if (currentFlight[0] <= 7)
					{
						return;
					}
					else
					{
						currentFlight[0] = currentFlight[0] - 8;
					}
					optionTable.setSelection(currentFlight[0]);
					break;
					
				case GlobalVar.PageDown:
					
					if (currentFlight[0] >= (optionTable.getItemCount() - 8))
					{
						return;
					}
					else
					{
						currentFlight[0] = currentFlight[0] + 8;
					}
					optionTable.setSelection(currentFlight[0]);
					break;
					
				case GlobalVar.Pay:
				case GlobalVar.Exit:
				case GlobalVar.Key0:
				case GlobalVar.Key1:
				case GlobalVar.Key2:
				case GlobalVar.Key3:
				case GlobalVar.Key4:
				case GlobalVar.Key5:
				case GlobalVar.Key6:
				case GlobalVar.Key7:
				case GlobalVar.Key8:
				case GlobalVar.Key9:
				case GlobalVar.Plus:
				case GlobalVar.Div:
				case GlobalVar.Decimal:
				case GlobalVar.Minu:
				case GlobalVar.Mul:
					break;						
					
				default:
					e.doit=true;
					break;
			}
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
	}

	public void keyReleased(KeyEvent e, int key)
	{
		try
		{
			Object objKey = editor.getItem().getData("key");//获取当前焦点行的KEY
			
			//标记当前行是否是国籍，航班。并标记
			if( objKey.equals(CustInfoDef.CUST_SCNUMBER))
			{
				flag = CustInfoDef.CUST_SCNUMBER;//"SCNUMBER";
			}
			if( objKey.equals(CustInfoDef.CUST_SCNATIONALITY))
			{
				flag = CustInfoDef.CUST_SCNATIONALITY;//"SCNATIONALITY";
			}
		
			switch (key)
			{
				case GlobalVar.Enter:
					
					//当焦点在optionTable按回车键，切焦点table，并带入选择的item至航班号
					if(choiceTable)
					{
						choiceTable = changeTable(table, flag, true);
					}
					
					//焦点在table判断当前选择的item
					if (objKey != null && objKey.equals(CustInfoDef.CUST_SCNATIONALITY))//"SCNATIONALITY"
					{
						//当时国籍时,加载国籍信息
						String sCNationality = editor.getItem().getText(2);
						if (sCNationality != null && sCNationality.trim().length()>0)
						{
							//检查当前输入的国籍是否正确
							Vector tableInfo = null;
							tableInfo = custBS.getNationality(editor.getItem().getText(2), false);
							if (tableInfo == null || tableInfo.size()<=0)
							{
								//当未找到时,仅仅给出提示
								new MessageBox(Language.apply("提示:未找到当前输入的国籍") + "[" + sCNationality + "]", null, false);
							}
						}						
						keyPressed(e, GlobalVar.ArrowDown);
					}
					else if (objKey != null && objKey.equals(CustInfoDef.CUST_SCPASSPORTNO))//"SCPASSPORTNO"
					{
						String passportno = editor.getItem().getText(2);
						if (passportno == null || passportno.trim().length()<=0)
						{
							//当护照号为空且回车时,则弹出护照读取框,供款员刷护照
							StringBuffer sbPassprot = new StringBuffer(); 
							GetPassPortForm f = new GetPassPortForm(sbPassprot);
							f.open();
							f=null;
							if (sbPassprot!=null) setValue(sbPassprot.toString());
						}
						else
						{
							//检查护照号是否被使用,同时获取大类优惠信息
							if (checkPassPort(editor.getItem().getText(2)))
							{
								keyPressed(e, GlobalVar.ArrowDown);
							}
						}
/*
						//检查护照号是否被使用,同时获取大类优惠信息
						if (checkPassPort(editor.getItem().getText(2)))
						{
							keyPressed(e, GlobalVar.ArrowDown);
						}*/
					}
					else if (objKey != null && objKey.equals(CustInfoDef.CUST_SCNUMBER))//"SCNUMBER"
					{
						//当前行是航班号,加载航班号信息
						String fNumber = editor.getItem().getText(2);
						if (fNumber != null && fNumber.trim().length()>0)
						{
							//检查当前输入的航班号是否正确
							Vector tableInfo = null;
							tableInfo = custBS.getFlight(editor.getItem().getText(2), false);
							if (tableInfo == null || tableInfo.size()<=0)
							{
								//当未找到时,仅仅给出提示
								new MessageBox(Language.apply("提示:未找到当前输入的航班号") + "[" + fNumber + "]", null, false);
							}
						}						
						
						//当收银员选择航班号为空或航班号错误时候，保持焦点在当前行，方便重新查
						//if(editor.getItem().getText(2).equals("") || !validOptionTable) { return;}
						keyPressed(e, GlobalVar.ArrowDown);
					}
					else if (currentPoint[0] >= table.getItemCount() - 1)
					{
						//当输入为最后一行时,则保存信息并关闭界面
						closeForm(false);
						/*if (saveData())
						 {
						 shell.close();
						 shell.dispose();
						 }*/
					}
					else
					{
						keyPressed(e, GlobalVar.ArrowDown);
					}
					break;
					
				case GlobalVar.Pay:
					
					// 如果optionTable为空不允许切焦点到optionTable
					if(! validOptionTable) { return;}
					//付款键，切当前optionTable
					if(choiceTable)
					{
						choiceTable = changeTable(table, flag, false);
					}
					else
					{
						choiceTable = changeTable(optionTable, flag, false);
					}
					/*if (currentPoint[1] > 0 && saveData())
					 {
					 shell.close();
					 shell.dispose();
					 }*/
					
					break;
					
				case GlobalVar.Exit:
					//增加检验,检查是否有必输项,如果有,则必须输入
					closeForm(true);
					//shell.close();
					//shell.dispose();            		
					break;
					
				default:
					//当前table不在列表信息时
					if(!choiceTable)
					{
						if ( objKey.equals(CustInfoDef.CUST_SCNUMBER) )
						{
							//当手动输入航班号时,自动模糊查询当前航班号(不按天匹配)
							//手动输入航班号实时查询，取数字键及小键盘，字母（不区分大小写）
							/*if( e.keyCode > 47 && e.keyCode <57 || e.keyCode > 96 && e.keyCode < 123 || e.keyCode > 16777263 && e.keyCode < 16777274 || e.keyCode == 8 ) 
							{
							}*/

							//查询航班号时,自动模糊查找航班号
							String fNumber = editor.getItem().getText(2);
							//if (fnumber != null && fnumber.trim().length()>0) loadFlight(fnumber);
							loadFlight(fNumber);
														
							//查询航班号没有唯一匹配值，光标保留当前行
							if( !validOptionTable) { return;}
						}
						
						if ( objKey.equals(CustInfoDef.CUST_SCNATIONALITY) )
						{
							//当手动输入国籍时,自动模糊查询当前国籍
							String nNumber = editor.getItem().getText(2);
							loadNationality(nNumber);
														
							//查询国籍没有唯一匹配值，光标保留当前行
							if( !validOptionTable) { return;}
						}
					}
					break;
			}
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
	}
	
	//护照信息解析
	private void setValue(String passportno)
	{
		try
		{
			PosLog.getLog(this.getClass().getSimpleName()).info(Language.apply("读取护照") + "setValue():passportno=[" + passportno + "].");	
			
			String[] arr = passportno.split("\n");
			if (arr.length<2)
			{
				PosLog.getLog(this.getClass().getSimpleName()).info(Language.apply("护照信息获取有误."));
				return;
			}
			String strName = "";
			String strNational = "";
			String strPassprot = "";
			//解析通过设备读取的护照信息
			String s = arr[0];
			strName = s.substring(6, 6 + 39).replace("<", "");
			s = arr[1];
			strPassprot = s.substring(0, 0+9).replace("<", "");
			strNational = s.substring(10, 10+3).replace("<", "");
						
			//解析年份(后两位)和性别标识(m代表男性，f代表女性,界面上只显示m或f) wangyong add by 2013.11.18
			String strShortYear="";//年份末两位
			String strSex = "";//性别标识
			strShortYear = s.substring(13, 13+2).replace("<", "");
			strSex = s.substring(20, 20+1).replace("<", "");
			
			PosLog.getLog(this.getClass().getSimpleName()).info("strName=[" + strName + "],strPassprot=[" + strPassprot + "],strNational=[" + strNational + "],strShortYear=[" + strShortYear + "],strSex=[" + strSex + "].");
			if (strName.trim().length() <= 0 || strNational.trim().length() <= 0 || strPassprot.trim().length() <= 0) return;
			
			//赋值
			TableItem[] items = table.getItems();
            Object getKey;
    		for (int i = 0; i < items.length; i++)
		    {
    			getKey = String.valueOf(items[i].getData("key"));
    			
    			if( getKey.equals(CustInfoDef.CUST_SCNAME))
    			{
    				items[i].setText(2, strName);
    			}
    			else if( getKey.equals(CustInfoDef.CUST_SCPASSPORTNO))
    			{
    				items[i].setText(2, strPassprot);
    			}
    			else if( getKey.equals(CustInfoDef.CUST_SCNATIONALITY))
    			{
    				items[i].setText(2, strNational);
    			}
    			else if( getKey.equals(CustInfoDef.CUST_SCMEMO1))
    			{
    				items[i].setText(2, strShortYear);
    			}
    			else if( getKey.equals(CustInfoDef.CUST_SCSEX))
    			{
    				items[i].setText(2, strSex);
    			}
    			
		    }
    		
    		/*editor.getItem().setText(2, strPassprot);
    		editor.getEditor().redraw();
    		table.redraw();*/
    		findLocation();
    		PosLog.getLog(this.getClass().getSimpleName()).info(Language.apply("护照信息赋值完成."));
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
	}

	/**
	 * 切Table焦点
	 * @param changetable table名称
	 * @param flag 顾客信息所在行
	 * @param isChoice 是否选择列表信息
	 * @return
	 */
	private boolean changeTable(final Table changetable,String flag, boolean isChoice)
	{
		//判断是否同一个table
		if(changetable.equals(optionTable))
		{
			table.deselectAll();
			currentFlight[0] = 0;
			optionTable.setFocus();
			optionTable.setSelection(optionTable.getItem(0));
			/*changetable.addSelectionListener(new SelectionAdapter()
	    	{
	    		public void widgetSelected(SelectionEvent e)
	    		{
	    			//获得所有的行数 
	    			int total = optionTable.getItemCount();
	    			//循环所有行 
	    			for (int i = 0; i < total; i++)
	    			{
	    				TableItem itemFlight = optionTable.getItem(i);
	    				//如果该行为选中状态，改变背景色和前景色，否则颜色设置 
	    				if (optionTable.isSelected(i))
	    				{
	    					itemFlight.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLUE));
	    					editor.getItem().setText(2, itemFlight.getText(1));
	    				}
	    				else
	    				{
	    					itemFlight.setForeground(null);
	    				}
	    			}
	    		}
	    	});*/
			return true;
		}
		else 
		{
			TableItem[] items = table.getItems();
			Object getKey;
			for (int i = 0; i < items.length; i++)
			{
				getKey = String.valueOf(items[i].getData("key"));
				//选择国籍名，航班号
				if (getKey.equals(CustInfoDef.CUST_SCNATIONALITY))
				{
					if (String.valueOf(getKey).equalsIgnoreCase(flag))
					{
						if (isChoice) items[i].setText(2, optionTable.getItem(optionTable.getSelectionIndex()).getText(1));
						break;
					}
				}
				else if (getKey.equals(CustInfoDef.CUST_SCNUMBER))
				{
					if (String.valueOf(getKey).equalsIgnoreCase(flag))
					{
						if (isChoice) items[i].setText(2, optionTable.getItem(optionTable.getSelectionIndex()).getText(1));
						break;
					}
				}

			}
     		//选中optionTable选项
			//editor.getItem().setText(2, optionTable.getItem(optionTable.getSelectionIndex()).getText(1));
			
			table.setFocus();
			newEditor.selectAll();
			return false;
		}
	}

	//初始化界面数据
	private void loadInitData()
	{
		//saleBS.saleCust.custClear();
		loadSaleCfg();
		
	}
	
	//加载顾客信息值
	private void loaadSaleCfgValue(String[] row)
	{
		try
		{			
			//if (row[3].equalsIgnoreCase("SCNUMBER")) row[2]="value_SCPASSPORTNO";//填充默认值
			if (saleBS.saleCust.custCount()>0)
			{
				//取当前小票已经录过的顾客信息(比如从付款界面返回到扫码界面后,当再按付款键时,则自动加载之前录过的顾客信息)
				if (saleBS.saleCust.containsKey(row[3]))
				{
					Object value = saleBS.saleCust.custGetItemValue(row[3]);
					if (value!=null) row[2]=String.valueOf(value);
				}				
			}
			else
			{
				//取当天最后一次录入的航班号/国籍，及加载常旅卡上的信息
				if (row[3].equalsIgnoreCase(CustInfoDef.CUST_SCNUMBER)) 
				{
					if(saleBS.clk!=null && saleBS.clk.ljhb!=null && saleBS.clk.ljhb.length()>0)
						row[2] = saleBS.clk.ljhb.trim();//当刷了常旅卡时，将此卡上的信息自动填充到界面上 wangyong add for 2014.11.25 by pengxl
					else
						row[2] = custBS.getLastFlightNo();
				}
				else if (row[3].equalsIgnoreCase(CustInfoDef.CUST_SCNATIONALITY)) 
				{
					if (saleBS.clk != null && saleBS.clk.nation != null && saleBS.clk.nation.length() > 0) 
						row[2] = saleBS.clk.nation.trim();
					else 
						row[2] = custBS.getLastNationality();
				}
				else if (row[3].equalsIgnoreCase(CustInfoDef.CUST_SCPASSPORTNO)) 
				{
					if (saleBS.clk != null && saleBS.clk.passport != null && saleBS.clk.passport.length() > 0) 
						row[2] = saleBS.clk.passport.trim();
				}
				else if (row[3].equalsIgnoreCase(CustInfoDef.CUST_SCNAME)) 
				{
					if (saleBS.clk != null && saleBS.clk.cardname != null && saleBS.clk.cardname.length() > 0) 
						row[2] = saleBS.clk.cardname.trim();
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	//加载顾客信息
	private String[] loadSaleCfg()
	{
		try
		{
			Vector tableInfo = custBS.getSaleCfg();
			TableItem item = null;
			String[] row = null;

			if (tableInfo.size() <= 0)
			{
				//custForm.shell.dispose();
				
				//return ;
			}

			for (int i = 0; i < tableInfo.size(); i++)
			{
				item = new TableItem(table, SWT.NULL);
				row = (String[]) tableInfo.elementAt(i);

				//填充默认值
				loaadSaleCfgValue(row);
				
				for (int j = 0; j < row.length; j++)
				{
					if (j == row.length - 2)
					{
						item.setData("key", row[j]);//行标识
					}
					else if (j >= row.length - 1)
					{
						item.setData("input", row[j]);//是否必输
					}
					else
					{
						item.setText(j, row[j]);
					}
				}
				
			}
			
			//生成刚加入tableItem
			table.redraw();
			return row;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

	//加载航班信息
	private boolean loadFlight()
	{
		if ( loadFlight("") == 0 ) return false;
		return true;
	}

	/**
	 * @param fnumber 航班号
	 * @return 0：查询失败，取值为0； 1：查询成功，取到多项匹配值； 2：查询成功，取到唯一值
	 */
	private int loadFlight(String fNumber)
	{
		try
		{
			optionTable.removeAll();
			this.custForm.columnTableC0.setText(Language.apply("行号"));
			this.custForm.columnTableC1.setText(Language.apply("航班号"));
			this.custForm.columnTableC2.setText(Language.apply("航空公司"));
			this.custForm.columnTableC3.setText(Language.apply("起飞时间"));
			Vector tableInfo = null;
			tableInfo = custBS.getFlight(fNumber);
			TableItem item = null;
			String[] row = null;
			
			//航班栏没有取到值
			if (tableInfo.size() <= 0)
			{
				row = new String[4];
				//custForm.shell.dispose();
				row[0] = "";row[1] = "";row[2] = "";row[3] = "";
				item = new TableItem(optionTable, SWT.NULL);
				for (int j = 0; j < row.length; j++)
				{
					item.setText(j, row[j]);
				}
				optionTable.redraw();
				validOptionTable = false;
				//new MessageBox("航班号获取失败！");
				return 0;
			}
			
			//航班栏只有唯一匹配值
			if (tableInfo.size() == 1)
			{
				//custForm.shell.dispose();
//				validOptionTable = true;
				item = new TableItem(optionTable, SWT.NULL);
				row = (String[]) tableInfo.elementAt(0);

				for (int j = 0; j < row.length; j++)
				{
					item.setText(j, row[j]);
				}
				//生成刚加入tableItem
				optionTable.redraw();
				validOptionTable = true;
				return 2;
			}
			
			//航班栏有多项匹配值
			for (int i = 0; i < tableInfo.size(); i++)
			{
				item = new TableItem(optionTable, SWT.NULL);
				row = (String[]) tableInfo.elementAt(i);

				for (int j = 0; j < row.length; j++)
				{
					item.setText(j, row[j]);
				}
			}
			//生成刚加入tableItem
			optionTable.redraw();
			validOptionTable = true;
			return 1;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return 0;
		}
	}

	
	/** //加载国籍信息
	 * @return
	 */
	private boolean loadNationality()
	{
		if ( loadNationality("") == 0 ) return false;
		return true;
	}

	/**
	 * @param nNumber 国籍号
	 * @return 0：查询失败，取值为0； 1：查询成功，取到多项匹配值； 2：查询成功，取到唯一值
	 */
	private int loadNationality(String nNumber)
	{
		try
		{
			optionTable.removeAll();
			this.custForm.columnTableC0.setText(Language.apply("行号"));
			this.custForm.columnTableC1.setText(Language.apply("英文名称"));
			this.custForm.columnTableC2.setText(Language.apply("中文名称"));
			this.custForm.columnTableC3.setText("");
			Vector tableInfo = null;
			tableInfo = custBS.getNationality(nNumber);
			TableItem item = null;
			String[] row = null;
			//国籍没有取到值
			if (tableInfo.size() <= 0)
			{
				row = new String[4];
				//custForm.shell.dispose();
				row[0] = "";row[1] = "";row[2] = "";row[3] = "";
				item = new TableItem(optionTable, SWT.NULL);
				for (int j = 0; j < row.length; j++)
				{
					item.setText(j, row[j]);
				}
				optionTable.redraw();
				validOptionTable = false;
				return 0;
			}
			//国籍只有唯一匹配值
			if (tableInfo.size() == 1)
			{
				//custForm.shell.dispose();
//					validOptionTable = true;
				item = new TableItem(optionTable, SWT.NULL);
				row = (String[]) tableInfo.elementAt(0);

				for (int j = 0; j < row.length; j++)
				{
					item.setText(j, row[j]);
				}
				//生成刚加入tableItem
				optionTable.redraw();
				validOptionTable = true;
				return 2;
			}
			//国籍有多项匹配值
			for (int i = 0; i < tableInfo.size(); i++)
			{
				item = new TableItem(optionTable, SWT.NULL);
				row = (String[]) tableInfo.elementAt(i);

				for (int j = 0; j < row.length; j++)
				{
					item.setText(j, row[j]);
				}
			}
			
			//生成刚加入tableItem
			optionTable.redraw();
			validOptionTable = true;
			return 1;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return 0;
		}
	}
	
	//保存顾客信息到内存
	public boolean saveData()
	{
		try
		{
			TableItem[] items = table.getItems();

			for (int i = 0; i < items.length; i++)
			{
				Object key = items[i].getData("key");//
				if (key == null) continue;

				ParaNodeDef node = new ParaNodeDef();
				node.code = String.valueOf(key);
				node.name = items[i].getText(1);
				node.value = items[i].getText(2);

				saleBS.saleCust.custAdd(node.code, node);

			}
			
			fillMemoValue();
			
			return checkCustomer();//true;
		}
		catch (Exception er)
		{
			er.printStackTrace();
			new MessageBox(Language.apply("顾客信息保存出现异常!\n可返回扫码界面,按[付款键]后重试!"), null, false);
			return false;
		}
	}
	
	/**
	 * 保存前检查顾客信息
	 * @return
	 */
	private boolean checkCustomer()
	{
		try
		{
			return custBS.checkCustomer(saleBS.saleCust);
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			return true;
		}
	}
	
	/**
	 * 拼memo1-20到memo字段,中间以@符号隔开
	 *
	 */
	private void fillMemoValue()
	{
		try
		{
			String memoCode = "";
			String memoValue = "";
			ParaNodeDef nodeMemo = new ParaNodeDef();
			nodeMemo.code = CustInfoDef.CUST_SCMEMO;
			
			for (int j=1; j<=20; j++)
			{
				memoCode = CustInfoDef.CUST_SCMEMO + j;
				memoValue = "";
				if (saleBS.saleCust.containsKey(memoCode))
				{
					ParaNodeDef node = (ParaNodeDef)saleBS.saleCust.custItem(memoCode);
					if (node != null) memoValue = node.value;
				}
				if (j>1) 
				{
					nodeMemo.value = nodeMemo.value + CustInfoDef.getSplitRegex() + memoValue;
				}
				else
				{
					nodeMemo.value = memoValue;
				}
				
			}
			
			saleBS.saleCust.custAdd(nodeMemo.code, nodeMemo);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	//检查护照号,并获取大类限额信息
	private boolean checkPassPort(String scpassportno)
	{
		try
		{
			Vector retVec = new Vector();
			int result = custBS.checkPassPort(scpassportno, retVec);
			if (result == 1) return true;//不检查(WINPOS此处是0,与JAVAPOS冲突,所以暂定为1为不检验)
			String isUse = retVec.elementAt(0).toString();
			this.DlInfo.append(retVec.elementAt(1));//大类限额
			if (isUse.equalsIgnoreCase("N")) 
			{
				return true;//未被使用,检查通过
			}
			else if (isUse.equalsIgnoreCase("A"))
			{
				//护照号输入不规范
				new MessageBox(Language.apply("护照号输入不规范, 请重新输入!"), null, false).verify();
				return false;
			}

			String msg = Language.apply("当前护照号已经被使用,是否继续使用? \n\n1-是 / 任意键-否 ");
			int ret = new MessageBox(msg, null, false).verify();
			if (ret != GlobalVar.Key1) { return false;}
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	//退出前,检查是否有必输项
	private boolean CheckCustomerInput()
	{
		try
		{
			String objInput;
			TableItem[] items = table.getItems();
			for (int i = 0; i < items.length; i++)
			{
				objInput = String.valueOf(items[i].getData("input"));
				if (String.valueOf(objInput).equalsIgnoreCase("Y"))
				{
					//若此行是否为必输,则检查是否已经输入
					if (items[i].getText(2).length() <= 0)
					{
						new MessageBox("[" + items[i].getText(1) + "]" + Language.apply("信息必须输入,请补全!"), null, false);
						return false;
					}
				}
			}
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	private void closeForm(boolean isExit)
	{
		try
		{
			if (!isExit)
			{
				//检查是否有必输项
				if (!CheckCustomerInput()) return;

				//保存数据
				if (!saveData()) return;
			}
			else
			{
				String msg = Language.apply("确认要退出顾客信息的录入? \n\n1-是 / 任意键-否 ");
				int ret = new MessageBox(msg, null, false).verify();
				if (ret != GlobalVar.Key1) { return; }

				//saleBS.saleCust.custClear();//取消退出时,清空顾客信息
				DlInfo.delete(0, DlInfo.length());
				DlInfo.append("exit");//标识是取消录入
			}

			//关闭窗口
			shell.close();
			shell.dispose();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
