package com.efuture.commonKit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class TextEx extends Composite
{
 
	
	private Text _inputText = null; 
	//private CLabel lbl;
	private Shell _dropDownShell = null; 
	private List _dropDownList = null;
	 
	private boolean _isShowedDropDown;//是否已经显示下拉
	private Object[] _dropDownItemsSource = null; //下拉数据源
	
	private boolean _isFirstMouseUp = true; //
	private boolean _isDoit = false;//按钮是否已经执行
	private final static int _defaultLoadRows = 40;//（初始状态时）默认加载下拉行数
	
	/**
	 * Create the composite
	 * @param parent
	 * @param style
	 */
	public TextEx(Composite parent, int style)
	{
		super(parent, style);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.numColumns = 2;
		setLayout(gridLayout);

		//_dropDownItemsSource = new String[]{"111item","112item","113item","121item","122item","123item"};
		
		_inputText = new Text(this, SWT.BORDER);
		
		final GridData gd_text = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_text.heightHint = 26;
		_inputText.setLayoutData(gd_text);
		

		_inputText.addPaintListener(new PaintListener() {
			public void paintControl(final PaintEvent arg0)
			{
				txtPaintControl(arg0);
			}
		});
		
		_inputText.addKeyListener(new KeyAdapter() {
			public void keyReleased(final KeyEvent arg0)
			{
				if (_inputText.getText().equals(" ")) _inputText.setText("");					
				txtKey(arg0);
			}
		});
		
		/*lbl = new CLabel(this, SWT.CENTER);
		lbl.setAlignment(SWT.CENTER);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lbl.addMouseListener(new MouseListener() {
			 

			public void mouseDoubleClick(MouseEvent arg0)
			{
				// TODO 自动生成方法存根
				
			}

			public void mouseDown(MouseEvent arg0)
			{
				// TODO 自动生成方法存根
				
			}

			public void mouseUp(MouseEvent arg0)
			{
				// TODO 自动生成方法存根
				txtKey(null);
			}
		});
		lbl.setText("▼");
		*/
		
		addFocusListener(new FocusAdapter() {
			public void focusLost(final FocusEvent arg0)
			{
				if (_isShowedDropDown) closeList();
				_isFirstMouseUp=true;
			}

			public void focusGained(FocusEvent arg0)
			{
				//----_inputText.selectAll();
			}
			
		});
		
		_inputText.addFocusListener(new FocusAdapter() {	 

			public void focusGained(FocusEvent arg0)
			{
				//----_inputText.selectAll();
			}
			
		});
		
		_inputText.addMouseListener(new MouseListener(){

			public void mouseDoubleClick(MouseEvent arg0)
			{
				// TODO 自动生成方法存根
				
			}

			public void mouseDown(MouseEvent arg0)
			{
				// TODO 自动生成方法存根
				
			}

			public void mouseUp(MouseEvent arg0)
			{
				if (_isFirstMouseUp)
				{
					_inputText.selectAll();
					_isFirstMouseUp=false;
				}
			}
			
		});
		
	}
	
	private void txtPaintControl(PaintEvent arg0)
	{
		if (_dropDownShell == null) return;
		Point p = _inputText.getParent().toDisplay(_inputText.getLocation());
		Point size = _inputText.getSize();
		Rectangle shellRect = new Rectangle(p.x, p.y + size.y, size.x, 0);
		_dropDownShell.setSize(shellRect.width, 100);
		_dropDownShell.setLocation(shellRect.x, shellRect.y); 
	}
	
	private void txtKey(KeyEvent key)
	{
		_isFirstMouseUp = false;
		_inputText.forceFocus();		
		try
		{_isDoit=false;
			if (key!=null)
			{
				if (_dropDownList != null)
				{
					if (key.keyCode==16777217)
					{//new MessageBox("上");
						//上
						int currIndex = _dropDownList.getSelectionIndex();
						if (currIndex-1 >=0) currIndex=currIndex-1;
						_dropDownList.setSelection(currIndex);
						return;
					}
					else if (key.keyCode==16777218)
					{//new MessageBox("下");
						//下
						int currIndex = _dropDownList.getSelectionIndex();
						if (currIndex+1 <_dropDownList.getItemCount()) currIndex=currIndex+1;
						_dropDownList.setSelection(currIndex);
						return;
					}
					else if (key.keyCode==13 || key.keyCode==16777296)
					{
						//回车
						closeList();
						return;
					}
					else if (key.keyCode == 27)
					{
						//ESC
						closeList(true);
						//key.doit = _isDoit;
						System.out.println("ESC " + key.doit);
						return;
					}
				}
				else
				{
					//当按回车或ESC时，不响应下拉
					if (key.keyCode==13 || key.keyCode==16777296 || key.keyCode == 27)  return;
				}
				
			}
			 
			if (_isShowedDropDown == false) 
			{ 
				initFloatShell(); 
			}
			fillDropDownItems(); 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		
	}
	
	public boolean getDoit()
	{
		return _isDoit;
	}
	
	private void fillDropDownItems()
	{
		try
		{
			if (this._dropDownShell==null || _dropDownList == null || _dropDownItemsSource == null) return;
			
			_dropDownList.removeAll();
			boolean isAll = true;
			int count = 0;
			if (_inputText.getText().toUpperCase().trim().length()<=0) isAll = false;
			if (_dropDownItemsSource != null && _dropDownItemsSource.length>0)
			{
				for (int i=0; i<_dropDownItemsSource.length; i++)
				{
					if (!isAll && count>_defaultLoadRows) break;
					if (_dropDownItemsSource[i].toString().toUpperCase().startsWith(_inputText.getText().toUpperCase())) _dropDownList.add(_dropDownItemsSource[i].toString());
					count++;
				}
			}
			/*for (String value : _dropDownItemsSource)
			{
				if (value.startsWith(_inputText.getText())) _dropDownList.add(value);
			} */

			_dropDownList.setSelection(0);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
	
	private void initFloatShell(){ 
		  Point p = _inputText.getParent().toDisplay(_inputText.getLocation()); 
		  Point size = _inputText.getSize(); 
		  Rectangle shellRect = new Rectangle(p.x, p.y + size.y, size.x, 0); 
		  _dropDownShell = new Shell(TextEx.this.getShell(), SWT.NO_TRIM); 

		  GridLayout gl = new GridLayout(); 
		  gl.marginBottom = 0; 
		  gl.marginTop = 0; 
		  gl.marginRight = 0; 
		  gl.marginLeft = 0; 
		  gl.marginWidth = 0; 
		  gl.marginHeight = 0; 
		  _dropDownShell.setLayout(gl); 

		  _dropDownList = new List(_dropDownShell, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL); 
				  
		  GridData gd = new GridData(GridData.FILL_BOTH); 
		  _dropDownList.setLayoutData(gd); 
		  _dropDownShell.setSize(shellRect.width, 100);//shellRect.height 
		  _dropDownShell.setLocation(shellRect.x, shellRect.y);

		  _dropDownShell.addShellListener(new ShellAdapter() { 
				 public void shellDeactivated(ShellEvent arg0) { 
					 _inputText.forceFocus();
				 } 
			 });
		  
		  _dropDownShell.addKeyListener(new KeyAdapter() {
				public void keyReleased(final KeyEvent arg0)
				{
					txtKey(null);
				}
			});
		  
		  _dropDownShell.addMouseListener(new MouseListener(){

			public void mouseDoubleClick(MouseEvent arg0)
			{
				// TODO 自动生成方法存根
				
			}

			public void mouseDown(MouseEvent arg0)
			{
				// TODO 自动生成方法存根
				
			}

			public void mouseUp(MouseEvent arg0)
			{
				// TODO 自动生成方法存根
				_inputText.forceFocus();
			}

			 
		});
			  
		  
		  _dropDownList.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent arg0)
			{
			}

			public void widgetSelected(SelectionEvent arg0)
			{
				closeList();
				_inputText.forceFocus();
			}
			  
		  });
		  
		  _dropDownList.addFocusListener(new FocusAdapter() {
				public void focusLost(final FocusEvent arg0)
				{
				}

				
				public void focusGained(FocusEvent arg0)
				{
					_inputText.forceFocus();
				}
				
			});
		  
		  _dropDownShell.open(); 
		  _inputText.forceFocus();
		  _isShowedDropDown = true;
	}
	private void closeList()
	{
		closeList(false);
	}
	
	/**
	 * 
	 * @param isEsc 是否取消录入
	 */
	private void closeList(boolean isEsc)
	{//new MessageBox("close");
		if (_dropDownShell != null && !_dropDownShell.isDisposed())
		{			
			if (!isEsc)
			{
				if (_dropDownList.getSelectionIndex()>=0) _inputText.setText(_dropDownList.getItem(_dropDownList.getSelectionIndex()));
				_inputText.setSelection(_inputText.getText().length());
			}
			
			_dropDownShell.dispose();
			_dropDownShell = null;
			_isShowedDropDown = false;
			_dropDownList=null;
			_isDoit = true;
		}
	}
	
	
	/**
	 * 设置下拉数据源
	 * @param items
	 */
	public void setItemsSource(Object[] items)
	{
		this._inputText.setText("");
		_dropDownItemsSource = items;
	}
	
	/**
	 * 获取下拉数据源
	 * @return
	 */
	public Object[] getItemsSource()
	{
		return _dropDownItemsSource;
	}

	
	public void addFocusListener(FocusListener arg0)
	{
		// TODO 自动生成方法存根
		super.addFocusListener(arg0);
		if (_inputText!= null) _inputText.addFocusListener(arg0);
	}
	
	public void addKeyListener(KeyListener arg0)
	{
		// TODO 自动生成方法存根
		super.addKeyListener(arg0);
		if (_inputText!= null) _inputText.addKeyListener(arg0);
	}

	public String getText()
	{
		if (_inputText != null) return _inputText.getText();
		
		return "";
	}
	
	public void setText(String text)
	{
		if (_inputText != null) _inputText.setText(text);
	}
	
	public Text getSource()
	{
		return this._inputText;
	}

	
	public boolean setFocus()
	{
		boolean blnRet = false;
		try
		{
			//System.out.println("setFocus");
			blnRet = this._inputText.setFocus();
			_inputText.setSelection(_inputText.getText().length());
			_inputText.selectAll();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return blnRet;
	}
	
	public void selectAll()
	{
		this._inputText.selectAll();
	}
	
	/**
	 * 按索引加载选项
	 * @param index
	 */
	public void select(int index)
	{
		if (index < 0)
		{
			_inputText.setText("");
			return;
		}
		if (_dropDownItemsSource != null && _dropDownItemsSource.length>0)
		{
			for (int i=0; i<_dropDownItemsSource.length; i++)
			{
				if (i == index)
				{
					_inputText.setText(_dropDownItemsSource[i].toString());
					break;
				}
				
			}
		}
	}
	
	/**
	 * 按值ID加载选项
	 * @param itemID
	 */
	public void select(String itemID)
	{
		if (itemID==null || itemID.length() < 0)
		{
			_inputText.setText("");
			return;
		}
		if (_dropDownItemsSource != null && _dropDownItemsSource.length>0)
		{
			for (int i=0; i<_dropDownItemsSource.length; i++)
			{
				if (_dropDownItemsSource[i].toString().indexOf(itemID)>=0)
				{
					_inputText.setText(_dropDownItemsSource[i].toString());
					break;
				}
				
			}
		}
	}
	
	public Object getData()
	{
		// TODO 自动生成方法存根
		if(_inputText!=null) return _inputText.getData();
		return null;
	}

	public Object getData(String arg0)
	{
		// TODO 自动生成方法存根
		if (_inputText!=null) return _inputText.getData(arg0);
		return null;
	}

	public void setData(Object arg0)
	{
		// TODO 自动生成方法存根
		if(_inputText!=null)_inputText.setData(arg0);
		
	}

	public void setData(String arg0, Object arg1)
	{
		// TODO 自动生成方法存根
		if (_inputText!=null)_inputText.setData(arg0, arg1);
	}

	public void setEnabled(boolean enabled)
	{
		this._inputText.setEnabled(enabled);
	}
	
	
	
	public boolean getEnabled()
	{
		// TODO 自动生成方法存根
		return this._inputText.getEnabled();
	}

	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

}
