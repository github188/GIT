package com.efuture.configure;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.CommonMethod;
import com.efuture.defineKey.MessageDiagram;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.swtdesigner.SWTResourceManager;

public class SetPrintEvent
{
	public Button btn_select = null;
	public Button btn_open = null;
	public Text txt_path = null;
	public Combo combo_select = null;
	public SetPrintBS setPrintBS = null;
	public Shell shell = null;
	public Table table = null;
	public TableEditor editor = null;
	public Combo cbxNewEditor = null;
	public Text txtNewEditor = null;
	public StyledText labPreview = null;
	public Button btn_preview = null;
	public Button btn_save = null;
	public Button btn_add = null;
	public Button btn_delete = null;
	public Button btn_back = null;
	public int[] currentPoint = new int[] { 6, 0 };
	String[] items = {
						"SalePrintMode.ini",
						"InvoiceSummaryMode.ini",
						"YyySalePrintMode.ini",
						"SyySalePrintMode.ini",
						"ArkGroupSalePrintMode.ini",
						"BusinessPerPrintMode.ini",
						"PayinPrintMode.ini",
						"DisplayMode.ini",
						"CardSalePrintMode.ini",
						"StoredCardStatisticsMode.ini",
						"GiftBillMode.ini",
						"SaleAppendBillMode.ini"};
	String[] names = { Language.apply("销售小票"),Language.apply("小票汇总"), Language.apply("营业员联小票"),Language.apply("收银员报表"), Language.apply("柜组对账单"), Language.apply("营业员报表"), Language.apply("缴款单"), Language.apply("顾客显示牌"), Language.apply("卡券小票") ,Language.apply("面值卡收款统计"),Language.apply("赠券"),Language.apply("小票附加信息联")};
	public String curOpen = null;

	public SetPrintEvent(SetPrintForm setPrintForm)
	{
		setPrintBS = new SetPrintBS();
		setPrintBS.setSetPrintEvent(this);

		this.btn_select = setPrintForm.btn_select;
		this.btn_open = setPrintForm.btn_open;
		this.combo_select = setPrintForm.combo_select;
		this.combo_select.setVisibleItemCount(7);
		this.txt_path = setPrintForm.txt_path;
		this.shell = setPrintForm.shell;
		this.table = setPrintForm.table;
		this.labPreview = setPrintForm.labPreview;
		this.btn_preview = setPrintForm.btn_preview;
		this.btn_save = setPrintForm.btn_save;
		this.btn_add = setPrintForm.btn_add;
		this.btn_delete = setPrintForm.btn_delete;
		this.btn_back = setPrintForm.btn_back;

		this.combo_select.setText(Language.apply("文件名"));
		this.combo_select.setItems(names);

		editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;

		selectEvent se = new selectEvent();

		this.btn_preview.addSelectionListener(se);
		this.btn_save.addSelectionListener(se);
		this.btn_add.addSelectionListener(se);
		this.btn_delete.addSelectionListener(se);
		this.btn_back.addSelectionListener(se);
		this.btn_select.addSelectionListener(se);
		this.btn_open.addSelectionListener(se);
		this.combo_select.addSelectionListener(se);
		this.txt_path.addSelectionListener(se);

		table.addMouseListener(new MouseListener()
		{

			public void mouseDoubleClick(MouseEvent e)
			{

			}

			public void mouseDown(MouseEvent event)
			{

				Point pt = new Point(event.x, event.y);
				int index = table.getTopIndex();
				boolean done = false;

				while (index < table.getItemCount())
				{
					final TableItem item = table.getItem(index);

					for (int i = 0; i < table.getColumnCount(); i++)
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
		table.redraw();

		Rectangle rec = Display.getDefault().getPrimaryMonitor().getClientArea();
		shell.setLocation((rec.width / 2) - (shell.getSize().x / 2), (rec.height / 2) - (shell.getSize().y / 2));
	}

	public void findLocation()
	{
		Control oldEditor = editor.getEditor();

		if (oldEditor != null)
		{
			oldEditor.dispose();
		}

		if (table.getItemCount() <= 0) { return; }

		TableItem items = table.getItem(currentPoint[1]);

		if (setPrintBS.isTitle(items.getText(0).trim())) { return; }

		if (setPrintBS.isGeneral(items.getText(0).trim())
				&& (currentPoint[0] == 0 || currentPoint[0] == 2 || currentPoint[0] == 3 || currentPoint[0] == 4 || currentPoint[0] == 5 || currentPoint[0] == 6)) { return; }

		// table的第一列（编号）和第五列（对齐）嵌入combo
		if (currentPoint[0] == 0)
		{

			cbxNewEditor = new Combo(table, SWT.LEFT | SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);

			for (int i = 0; i < setPrintBS.comboInfo.size(); i++)
			{
				cbxNewEditor.add((String) setPrintBS.comboInfo.get(i));
			}

			cbxNewEditor.setText(items.getText(currentPoint[0]));
			editor.setEditor(cbxNewEditor, items, currentPoint[0]);
			cbxNewEditor.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
			cbxNewEditor.setVisibleItemCount(10);
			cbxNewEditor.addModifyListener(new ModifyListener()
			{
				public void modifyText(ModifyEvent e)
				{
					Combo comb = (Combo) editor.getEditor();
					editor.getItem().setText(currentPoint[0], comb.getText());
				}
			});

			// 增加监听器
			NewKeyEvent event = new NewKeyEvent()
			{
				public void keyDown(KeyEvent e, int key)
				{

				}

				public void keyUp(KeyEvent e, int key)
				{

				}
			};

			NewKeyListener key = new NewKeyListener();
			key.event = event;

			cbxNewEditor.addKeyListener(key);
			cbxNewEditor.setFocus();

		}
		else if (currentPoint[0] == 4)
		{
			cbxNewEditor = new Combo(table, SWT.LEFT | SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
			cbxNewEditor.add(Language.apply("0-左对齐"));
			cbxNewEditor.add(Language.apply("1-右对齐"));
			cbxNewEditor.add(Language.apply("2-居中"));
			cbxNewEditor.setText(items.getText(currentPoint[0]));
			editor.setEditor(cbxNewEditor, items, currentPoint[0]);
			cbxNewEditor.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
			cbxNewEditor.setVisibleItemCount(3);
			cbxNewEditor.addModifyListener(new ModifyListener()
			{
				public void modifyText(ModifyEvent e)
				{
					Combo comb = (Combo) editor.getEditor();
					editor.getItem().setText(currentPoint[0], comb.getText());
				}
			});

			// 增加监听器
			NewKeyEvent event = new NewKeyEvent()
			{
				public void keyDown(KeyEvent e, int key)
				{

				}

				public void keyUp(KeyEvent e, int key)
				{

				}
			};

			NewKeyListener key = new NewKeyListener();
			key.event = event;

			cbxNewEditor.addKeyListener(key);
			cbxNewEditor.setFocus();

		}
		// 其他列嵌入text
		else
		{
			// 如果该行为静态文本[00]则不允许修改编辑 值
			if (currentPoint[0] == 6 && setPrintBS.formatTitle(items.getText(0).trim()).equals("00")) { return; }

			if ((items.getText(0).trim().equalsIgnoreCase("[PagePrint]") || items.getText(0).trim().equalsIgnoreCase("[AreaPrint]"))
					&& currentPoint[0] == 1)
			{
				cbxNewEditor = new Combo(table, SWT.LEFT | SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
				cbxNewEditor.add(Language.apply("是"));
				cbxNewEditor.add(Language.apply("否"));
				cbxNewEditor.setText(items.getText(currentPoint[0]));
				editor.setEditor(cbxNewEditor, items, currentPoint[0]);
				cbxNewEditor.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
				cbxNewEditor.setVisibleItemCount(3);
				cbxNewEditor.addModifyListener(new ModifyListener()
				{
					public void modifyText(ModifyEvent e)
					{
						Combo comb = (Combo) editor.getEditor();
						editor.getItem().setText(currentPoint[0], comb.getText());
					}
				});

				// 增加监听器
				NewKeyEvent event = new NewKeyEvent()
				{
					public void keyDown(KeyEvent e, int key)
					{

					}

					public void keyUp(KeyEvent e, int key)
					{

					}
				};

				NewKeyListener key = new NewKeyListener();
				key.event = event;

				cbxNewEditor.addKeyListener(key);
				cbxNewEditor.setFocus();
			}
			else
			{
				txtNewEditor = new Text(table, SWT.LEFT | SWT.BORDER);
				txtNewEditor.setText(items.getText(currentPoint[0]));
				editor.setEditor(txtNewEditor, items, currentPoint[0]);
				txtNewEditor.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
				txtNewEditor.selectAll();
				txtNewEditor.addModifyListener(new ModifyListener()
				{
					public void modifyText(ModifyEvent e)
					{
						Text text = (Text) editor.getEditor();
						editor.getItem().setText(currentPoint[0], text.getText());
					}
				});

				// 增加监听器
				NewKeyEvent event = new NewKeyEvent()
				{

					public void keyDown(KeyEvent e, int key)
					{

					}

					public void keyUp(KeyEvent e, int key)
					{

					}
				};

				NewKeyListener key = new NewKeyListener();
				key.event = event;

				if (currentPoint[0] == 1 || currentPoint[0] == 2 || currentPoint[0] == 3 || currentPoint[0] == 4)
				{
					key.inputMode = key.IntegerInput;
				}

				txtNewEditor.addKeyListener(key);
				txtNewEditor.setFocus();
			}
		}
	}

	class selectEvent implements SelectionListener
	{
		public void widgetDefaultSelected(SelectionEvent e)
		{
		}

		public void widgetSelected(SelectionEvent e)
		{
			// 新增行
			if (e.widget.equals(btn_add))
			{
				if (curOpen == null) { return; }
				setPrintBS.addRow(currentPoint[1]);
			}
			// 删除行
			if (e.widget.equals(btn_delete))
			{
				if (curOpen == null) { return; }
				setPrintBS.deleteRow(currentPoint[1]);
			}

			// 还原
			if (e.widget.equals(btn_back))
			{
				if (curOpen == null) { return; }
				table.removeAll();
				setPrintBS.setTableItem();
			}

			// 预览效果
			if (e.widget.equals(btn_preview))
			{
				if (curOpen == null)
				{
					new MessageDiagram(shell).open(Language.apply("请先选择并打开的文件"), false);

					return;
				}
				setPrintBS.preView();
			}

			// 保存
			if (e.widget.equals(btn_save))
			{
				if (curOpen == null)
				{
					new MessageDiagram(shell).open(Language.apply("请先选择并打开的文件"), false);

					return;
				}
				setPrintBS.saveData(curOpen);
			}

			// 选择文件
			if (e.widget.equals(combo_select))
			{
				int i = combo_select.getSelectionIndex();
				String info = items[i];
				txt_path.setText(GlobalVar.ConfigPath + "/" + info);
			}

			// 选择文件
			if (e.widget.equals(btn_select))
			{
				CommonMethod.openFileDialog(shell, txt_path);

				if (txt_path.getText().length() > 0)
				{
					e.widget = btn_open;
					widgetSelected(e);
				}
			}

			// 打开按钮
			else if (e.widget.equals(btn_open))
			{
				if (txt_path.getText().trim().length() <= 0)
				{
					new MessageDiagram(shell).open(Language.apply("请先输入打开的文件\n 或\n确认输入的文件是否合法 "), false);

					return;
				}

				if (curOpen != null && curOpen.equals(txt_path.getText().trim())) { return; }

				curOpen = txt_path.getText();
				setPrintBS.init();
				setPrintBS.ReadTemplateFile(txt_path.getText());
				setPrintBS.setTableItem();
				// 触发预览按钮
				e.widget = btn_preview;
				widgetSelected(e);
			}
		}
	}
}
