package com.efuture.javaPos.Test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class TableTest {

	public Table table;
	protected Shell shell;
	
    TableEditor editor;

	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			TableTest window = new TableTest();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window
	 */
	public void open() {
		final Display display = Display.getDefault();
		createContents();
		
		new TableTestEvent(this);
		
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	/**
	 * Create contents of the window
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setLayout(new FormLayout());
		shell.setSize(500, 375);
		shell.setText("SWT Application");

		table = new Table(shell, SWT.BORDER);
		final FormData fd_table = new FormData();
		fd_table.bottom = new FormAttachment(0, 331);
		fd_table.top = new FormAttachment(0, 75);
		fd_table.right = new FormAttachment(0, 452);
		fd_table.left = new FormAttachment(0, 25);
		table.setLayoutData(fd_table);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		final TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
		newColumnTableColumn.setWidth(100);
		newColumnTableColumn.setText("Name");

		final TableItem newItemTableItem = new TableItem(table, SWT.BORDER);
		newItemTableItem.setText("New item");

		final TableColumn newColumnTableColumn_1 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_1.setWidth(100);
		newColumnTableColumn_1.setText("AGE");

		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new FillLayout());
		final FormData fd_composite = new FormData();
		fd_composite.bottom = new FormAttachment(0, 53);
		fd_composite.top = new FormAttachment(0, 5);
		fd_composite.right = new FormAttachment(0, 367);
		fd_composite.left = new FormAttachment(0, 56);
		composite.setLayoutData(fd_composite);

		final Button button = new Button(composite, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
			}
		});
		button.setText("111111111111111111111111111111");

		final Button button_1 = new Button(composite, SWT.NONE);
		button_1.setText("button");

		final Button button_2 = new Button(composite, SWT.NONE);
		button_2.setText("button");
		//
	}
	



}
