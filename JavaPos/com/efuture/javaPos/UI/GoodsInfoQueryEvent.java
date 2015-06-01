package com.efuture.javaPos.UI;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
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
import com.efuture.javaPos.Logic.GoodsInfoQueryBS;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.ICallBack;
import com.efuture.javaPos.UI.Design.GoodsInfoQueryForm;

public class GoodsInfoQueryEvent
{
	private PosTable tabGoods = null;
	private Text txtCode = null;
	private Text txtCode2 = null;
	private Combo combo = null;
	private Label lblBetween = null;
	private Label label_Enter = null;
	private Label label_Validation = null;
	private Label label_Pay = null;
	private Label label_Clear = null;
	private Label label_Minu = null;
	private Label label_Plus = null;
	// private char codetype = '0';
	private Shell shell = null;
	private GoodsInfoQueryBS giqbs = null;
	private ICallBack callBack = null;

	// private Color mouseEnter =
	// SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND);
	// private Color mouseExit = SWTResourceManager.getColor(0, 64, 128);

	public GoodsInfoQueryEvent(GoodsInfoQueryForm giqf, StringBuffer sbBarcode, ICallBack callBack)
	{
		this.callBack = callBack;
		this.tabGoods = giqf.getTabGoods();
		this.txtCode = giqf.getTxtCode();
		this.txtCode2 = giqf.getTxtCode2();
		this.lblBetween = giqf.getLblBetween();
		this.combo = giqf.getCombo();
		this.shell = giqf.getShell();
		this.label_Enter = giqf.getLabel_Enter();
		this.label_Clear = giqf.getLabel_Clear();
		this.label_Minu = giqf.getLabel_Minu();
		this.label_Pay = giqf.getLabel_Pay();
		this.label_Validation = giqf.getLabel_Validation();
		this.label_Plus = giqf.getLabel_Plus();

		giqbs = CustomLocalize.getDefault().createGoodsInfoQueryBS();
		giqbs.sbBarcode = sbBarcode;
		giqbs.callBack = callBack;

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

		txtCode.addKeyListener(key);
		txtCode2.addKeyListener(key);
		tabGoods.addKeyListener(key);
		combo.addKeyListener(key);

		// 鼠标点击事件
		MouseListener mouseListen = new MouseListener()
		{
			public void mouseDoubleClick(MouseEvent e)
			{
				mouseDoubleClickEvent(e);
			}

			public void mouseDown(MouseEvent e)
			{
				mouseDownEvent(e);
			}

			public void mouseUp(MouseEvent e)
			{

			}
		};

		/*
		 * //鼠标区域感应事件 MouseTrackListener mouseTrack = new MouseTrackListener() {
		 * public void mouseEnter(MouseEvent e) { mouseEnterEvent(e); } public
		 * void mouseExit(MouseEvent e) { mouseExitEvent(e); } public void
		 * mouseHover(MouseEvent e) { } };
		 */

		tabGoods.addMouseListener(mouseListen);

		this.label_Enter.addMouseListener(mouseListen);
		this.label_Pay.addMouseListener(mouseListen);
		this.label_Validation.addMouseListener(mouseListen);
		this.label_Plus.addMouseListener(mouseListen);
		this.label_Minu.addMouseListener(mouseListen);
		this.label_Clear.addMouseListener(mouseListen);

		/*
		 * this.label_Enter.addMouseTrackListener(mouseTrack);
		 * this.label_Pay.addMouseTrackListener(mouseTrack);
		 * this.label_Validation.addMouseTrackListener(mouseTrack);
		 * this.label_Plus.addMouseTrackListener(mouseTrack);
		 * this.label_Minu.addMouseTrackListener(mouseTrack);
		 * this.label_Clear.addMouseTrackListener(mouseTrack);
		 */

		initData();
		initTable();
	}

	public void initTable()
	{
		try
		{
			if (!giqbs.isDefineColumn)
				return;

			while (tabGoods.getColumnCount() > 0)
				tabGoods.getColumns()[0].dispose();

			this.tabGoods.setTitle(giqbs.getDefineColName());
			this.tabGoods.setWidth(giqbs.getDefineColWidth());

			this.tabGoods.initialize();
			// this.tabGoods.setContent();

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void mouseDoubleClickEvent(MouseEvent e)
	{
		try
		{
			// //双击鼠标添加选中的商品
			if (e.getSource() == tabGoods)
			{
				NewKeyListener.sendKey(GlobalVar.Enter);
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}

	public void mouseDownEvent(MouseEvent e)
	{
		try
		{
			if (e.getSource() == label_Enter)
			{
				NewKeyListener.sendKey(GlobalVar.Enter);
			}
			else if (e.getSource() == label_Pay)
			{
				NewKeyListener.sendKey(GlobalVar.Pay);
			}
			else if (e.getSource() == label_Validation)
			{
				NewKeyListener.sendKey(GlobalVar.Validation);
			}
			else if (e.getSource() == label_Plus)
			{
				keyReleased(null, GlobalVar.writeHang);
			}
			else if (e.getSource() == label_Minu)
			{
				keyReleased(null, GlobalVar.readHang);
			}
			else if (e.getSource() == label_Clear)
			{
				NewKeyListener.sendKey(GlobalVar.Clear);
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/*
	 * public void mouseEnterEvent(MouseEvent e) { if (e.getSource() ==
	 * label_Enter) { label_Enter.setBackground(mouseEnter); } else if
	 * (e.getSource() == label_Pay) { label_Pay.setBackground(mouseEnter); }
	 * else if (e.getSource() == label_Validation) {
	 * label_Validation.setBackground(mouseEnter); } else if (e.getSource() ==
	 * label_Plus) { label_Plus.setBackground(mouseEnter); } else if
	 * (e.getSource() == label_Minu) { label_Minu.setBackground(mouseEnter); }
	 * else if (e.getSource() == label_Clear) {
	 * label_Clear.setBackground(mouseEnter); } }
	 * 
	 * public void mouseExitEvent(MouseEvent e) { if (e.getSource() ==
	 * label_Enter) { label_Enter.setBackground(mouseExit); } else if
	 * (e.getSource() == label_Pay) { label_Pay.setBackground(mouseExit); } else
	 * if (e.getSource() == label_Validation) {
	 * label_Validation.setBackground(mouseExit); } else if (e.getSource() ==
	 * label_Plus) { label_Plus.setBackground(mouseExit); } else if
	 * (e.getSource() == label_Minu) { label_Minu.setBackground(mouseExit); }
	 * else if (e.getSource() == label_Clear) {
	 * label_Clear.setBackground(mouseExit); } }
	 */

	public void keyPressed(KeyEvent e, int key)
	{
		try
		{
			switch (key)
			{
				case GlobalVar.ArrowUp:
					if (!checkComboList())
						return;

					if (e.getSource() == tabGoods)
					{
						if (tabGoods.getSelectionIndex() > 0)
						{
							tabGoods.setSelection(tabGoods.getSelectionIndex() - 1);
						}
					}
					break;

				case GlobalVar.ArrowDown:
					if (!checkComboList())
						return;

					if (e.getSource() == tabGoods)
					{
						if (tabGoods.getSelectionIndex() < (tabGoods.getItemCount() - 1) && tabGoods.getItemCount() >= 0)
						{
							tabGoods.setSelection(tabGoods.getSelectionIndex() + 1);
						}
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
		try
		{
			switch (key)
			{
				case GlobalVar.Enter:
					if (!checkComboList())
						return;
					enter(e);

					break;

				case GlobalVar.Validation:
					openGoodsDetail();

					break;

				case GlobalVar.Pay:// 切换焦点
					changeFocus(e);

					break;

				case GlobalVar.Clear:
					if (e != null)
						e.data = "";
					clear();
					break;

				case GlobalVar.writeHang:// +:或
					if (e != null)
						e.data = "";
					setCombQueryFlag('2');

					break;

				case GlobalVar.readHang:// -:且
					if (e != null)
						e.data = "";
					setCombQueryFlag('1');

					break;

				case GlobalVar.ArrowLeft:
					if (!checkComboList())
						return;

					if (e != null)
						e.data = "";
					if (e.getSource() == txtCode)
					{
						combo.setFocus();
					}
					else if (e.getSource() == txtCode2)
					{
						txtCode.setFocus();
						txtCode.selectAll();
					}
					break;

				case GlobalVar.ArrowRight:
					if (!checkComboList())
						return;

					if (e != null)
						e.data = "";
					if (e.getSource() == combo)
					{
						changeLocation();
						NewKeyListener.curInputMode = giqbs.getTxtInputMode(combo.getItem(combo.getSelectionIndex()));
					}
					else if (e.getSource() == txtCode)
					{
						txtCode2.setFocus();
						txtCode2.selectAll();
					}
					break;

				case GlobalVar.Exit:
					NewKeyListener.curInputMode = 0;
					giqbs.chrIsCloseForm = 'Y';
					giqbs.clear();
					shell.close();
					shell.dispose();
					break;

			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private void initData()
	{
		try
		{
			giqbs.initData(combo);

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private void enter(KeyEvent e)
	{
		if (e != null)
			e.data = "";
		// 查询商品
		if (e.getSource() == combo)
		{
			changeLocation();
			NewKeyListener.curInputMode = giqbs.getTxtInputMode(combo
					.getItem(combo.getSelectionIndex()));
			txtCode.selectAll();
		}
		else if (e.getSource() == txtCode)
		{
			if (this.txtCode2.getVisible())
			{
				txtCode2.forceFocus();
				txtCode2.selectAll();
			}
			else
			{
				NewKeyListener.curInputMode = -1;
				int len = giqbs.getInputTxtLimitLen(combo.getItem(combo.getSelectionIndex()));
				if(txtCode.getText().trim().length()<len)
					txtCode.setText(Language.apply("字符个数应大于") + (len-1));
				else	
					getGoodsList();
				
				txtCode.selectAll();
			}
		}
		else if (e.getSource() == txtCode2)
		{
			NewKeyListener.curInputMode = -1;
			getGoodsList();
			txtCode2.selectAll();
		}
		else if (e.getSource() == tabGoods)
		{
			// 添加商品
			if (tabGoods.getItemCount() > 0)
			{
				addGoodsToSaleform();
			}
		}
	}

	private void changeFocus(KeyEvent e)
	{
		NewKeyListener.curInputMode = -1;

		e.data = "";
		if (e.getSource() == combo)
		{
			// changeLocation();
			txtCode.forceFocus();
			txtCode.selectAll();
		}
		else if (e.getSource() == txtCode)
		{
			//
			if (this.txtCode2.getVisible())
			{
				txtCode2.forceFocus();
				txtCode2.selectAll();
			}
			else
			{
				tabGoods.setFocus();
			}
		}
		else if (e.getSource() == txtCode2)
		{
			tabGoods.setFocus();
			if (tabGoods.getItemCount() > 0)
			{
				if (tabGoods.getSelectionIndex() <= 0)
				{
					tabGoods.setSelection(1);
				}
			}
		}
		else if (e.getSource() == tabGoods)
		{
			// 切到选择框
			combo.setFocus();
		}

	}

	private void addGoodsToSaleform()
	{
		try
		{
			if (tabGoods.getItemCount() > 0)
			{
				TableItem tableItem = tabGoods.getItem(tabGoods.getSelectionIndex());
				if (callBack == null)
					return;
				String barcode = getSelectBarcode(tabGoods.getSelectionIndex());
				if(barcode==null) 
				{
					new MessageBox("添加失败：获取商品信息条码失败");
					return;
				}
				callBack.exec(barcode);//tableItem.getText(1));
				if (GlobalInfo.sysPara.isMoreSelectQuerygoods == 'N')
				{
					if (giqbs.sbBarcode != null)
						giqbs.sbBarcode.append(barcode);//tableItem.getText(1));
					// 单选的时候,关闭窗口
					keyReleased(null, GlobalVar.Exit);
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			ex.getMessage();
		}

	}
	
	private String getSelectBarcode(int index)
	{
		if (this.giqbs.listgoods==null || index>=this.giqbs.listgoods.size()) return null;
		GoodsDef goods = (GoodsDef) this.giqbs.listgoods.get(index);
		return goods.barcode;
	}

	private void setCombQueryFlag(char chrFlag)
	{
		this.giqbs.setCombQueryFlag(chrFlag, combo, txtCode, txtCode2);
		this.combo.setFocus();
	}

	private void getGoodsList()
	{
		giqbs.getGoodsList(combo, txtCode, txtCode2, tabGoods);
	}

	private void changeLocation()
	{
		try
		{
			// 当=1时,一个输入框
			// 当=2时,两个输入框
			int intRet = giqbs.getInputBoxType(combo.getItem(combo.getSelectionIndex()).trim());
			if (intRet == 1)
			{
				this.txtCode.setText("");
				this.lblBetween.setVisible(false);
				this.txtCode2.setVisible(false);
			}
			else if (intRet == 2)
			{
				this.txtCode2.setText("");
				this.lblBetween.setVisible(true);
				this.txtCode2.setVisible(true);
			}
			txtCode.setFocus();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private void clear()
	{
		this.txtCode.setText("");
		this.txtCode2.setText("");
		this.tabGoods.removeAll();
		this.giqbs.clear();
		this.combo.setFocus();
	}

	private boolean checkComboList()
	{
		try
		{
			if (combo.getItemCount() <= 0)
				return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	private void openGoodsDetail()
	{
		if (tabGoods.getItemCount() > 0)
		{
			TableItem tableItem = tabGoods.getItem(tabGoods.getSelectionIndex());
			giqbs.openGoodsDetailForm(tableItem.getText(1), tableItem.getText(2), tableItem.getText(3));
			if (giqbs.chrIsCloseForm == 'Y')
			{
				keyReleased(null, GlobalVar.Exit);
			}
		}
	}
}
