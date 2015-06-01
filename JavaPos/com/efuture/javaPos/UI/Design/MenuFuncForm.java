package com.efuture.javaPos.UI.Design;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.UI.MenuFuncEvent;
import com.swtdesigner.SWTResourceManager;


public class MenuFuncForm
{
    protected Shell shell = null;
    private boolean appenddefaultrole = true;
    private String role = null;
    private Group groupMainMenu = null;
    private Group groupTwoLevelMenu = null;
    private Group groupThreeLevelMenu = null;
    private ArrayList currManinMenuArray = null;
    private ArrayList currTwoLevelMenuArray = null;
    private ArrayList currThreeLevelMenuArray = null;
    
    public int handle = 0;
    
    public Button print = null;

    public MenuFuncForm(Shell parent, String role)
    {
        this.role   = role;
        this.appenddefaultrole = true;
        this.open();
    }
    
    public MenuFuncForm(Shell parent, String role,boolean appenddefaultrole)
    {
        this.role   = role;
        this.appenddefaultrole = appenddefaultrole;
        this.open();
    }

    public void open()
    {
        final Display display = Display.getDefault();
        createContents();
        
        new MenuFuncEvent(this);

        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
                
        if (!shell.isDisposed())     
        {
	        shell.open();
	        shell.layout();
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
    }

    /**
     * Create contents of the window
     */
    protected void createContents()
    {
        shell = new Shell(GlobalVar.style);
        shell.setSize(800, 510);
        shell.setLayout(new FormLayout());

        currManinMenuArray      = new ArrayList();
        currTwoLevelMenuArray   = new ArrayList();
        currThreeLevelMenuArray = new ArrayList();

        //三个group组----------------------------------------------------------------------------
        groupMainMenu = new Group(shell, SWT.NONE);

        final FormData formData = new FormData();
        formData.bottom = new FormAttachment(100, -10);
        formData.top    = new FormAttachment(0, 5);
        formData.left   = new FormAttachment(0, 10);
        groupMainMenu.setLayoutData(formData);
        groupMainMenu.setLayout(new FormLayout());

        groupTwoLevelMenu = new Group(shell, SWT.NONE);
        formData.right    = new FormAttachment(groupTwoLevelMenu, -5, SWT.LEFT);

        groupThreeLevelMenu = new Group(shell, SWT.NONE);

        final FormData formData_1 = new FormData();
        formData_1.bottom = new FormAttachment(groupMainMenu, 0, SWT.BOTTOM);
        formData_1.top    = new FormAttachment(0, 5);
        formData_1.right  = new FormAttachment(0, 530);
        formData_1.left   = new FormAttachment(0, 265);
        groupTwoLevelMenu.setLayoutData(formData_1);
        groupTwoLevelMenu.setLayout(new FormLayout());

        final FormData formData_2 = new FormData();
        formData_2.bottom = new FormAttachment(groupTwoLevelMenu, 0, SWT.BOTTOM);
        formData_2.right  = new FormAttachment(0, 780);
        formData_2.top    = new FormAttachment(0, 5);
        formData_2.left   = new FormAttachment(0, 535);
        groupThreeLevelMenu.setLayoutData(formData_2);
        groupThreeLevelMenu.setLayout(new FormLayout());

        //----------------------------------------------------------------------------------------

        //groupMainMenu中的Button
        final Button button = new Button(groupMainMenu, SWT.FLAT);
        button.setAlignment(SWT.LEFT);
        button.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button.setVisible(false);

        final FormData formData_3 = new FormData();
        formData_3.top    = new FormAttachment(0, 0);
        formData_3.bottom = new FormAttachment(0, 35);
        formData_3.left   = new FormAttachment(0, 5);
        button.setLayoutData(formData_3);
        button.setText("button");
        currManinMenuArray.add(button);

        final Button button_1 = new Button(groupMainMenu, SWT.FLAT);
        button_1.setAlignment(SWT.LEFT);
        button_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_1.setVisible(false);

        final FormData formData_3_1 = new FormData();
        formData_3_1.left   = new FormAttachment(button, 0, SWT.LEFT);
        formData_3_1.right  = new FormAttachment(button, 0, SWT.RIGHT);
        formData_3_1.bottom = new FormAttachment(button, 40, SWT.BOTTOM);
        formData_3_1.top    = new FormAttachment(button, 5, SWT.BOTTOM);
        button_1.setLayoutData(formData_3_1);
        button_1.setText("button");
        currManinMenuArray.add(button_1);

        final Button button_1_1 = new Button(groupMainMenu, SWT.FLAT);
        button_1_1.setAlignment(SWT.LEFT);
        button_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_1_1.setVisible(false);

        final FormData formData_3_1_1 = new FormData();
        formData_3_1_1.left   = new FormAttachment(button_1, 0, SWT.LEFT);
        formData_3_1_1.top    = new FormAttachment(0, 80);
        formData_3_1_1.bottom = new FormAttachment(0, 115);
        formData_3_1_1.right  = new FormAttachment(button_1, 0, SWT.RIGHT);
        button_1_1.setLayoutData(formData_3_1_1);
        button_1_1.setText("button");
        currManinMenuArray.add(button_1_1);

        final Button button_1_1_1 = new Button(groupMainMenu, SWT.FLAT);
        button_1_1_1.setAlignment(SWT.LEFT);
        button_1_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_1_1_1.setVisible(false);

        final FormData formData_3_1_1_1 = new FormData();
        formData_3_1_1_1.top    = new FormAttachment(0, 120);
        formData_3_1_1_1.bottom = new FormAttachment(0, 155);
        formData_3_1_1_1.left   = new FormAttachment(button_1_1, 0, SWT.LEFT);
        button_1_1_1.setLayoutData(formData_3_1_1_1);
        button_1_1_1.setText("button");
        currManinMenuArray.add(button_1_1_1);

        Button button_1_1_1_1;
        button_1_1_1_1 = new Button(groupMainMenu, SWT.FLAT);
        button_1_1_1_1.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(final SelectionEvent arg0) {
        	}
        });
        button_1_1_1_1.setAlignment(SWT.LEFT);
        button_1_1_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_1_1_1_1.setVisible(false);
        formData_3_1_1_1.right    = new FormAttachment(button_1_1_1_1, 0,
                                                       SWT.RIGHT);
        formData_3.right          = new FormAttachment(button_1_1_1_1, 0,
                                                       SWT.RIGHT);

        final FormData formData_3_1_1_1_1 = new FormData();
        formData_3_1_1_1_1.left = new FormAttachment(0, 5);
        formData_3_1_1_1_1.right  = new FormAttachment(100, -2);
        formData_3_1_1_1_1.top    = new FormAttachment(0, 160);
        formData_3_1_1_1_1.bottom = new FormAttachment(0, 195);
        button_1_1_1_1.setLayoutData(formData_3_1_1_1_1);
        button_1_1_1_1.setText("button");
        currManinMenuArray.add(button_1_1_1_1);

        final Button button_1_1_1_2 = new Button(groupMainMenu, SWT.FLAT);
        button_1_1_1_2.setAlignment(SWT.LEFT);
        button_1_1_1_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_1_1_1_2.setVisible(false);

        final FormData formData_3_1_1_1_2 = new FormData();
        formData_3_1_1_1_2.left   = new FormAttachment(button_1_1_1_1, 0,
                                                       SWT.LEFT);
        formData_3_1_1_1_2.right  = new FormAttachment(button_1_1_1_1, 0,
                                                       SWT.RIGHT);
        formData_3_1_1_1_2.bottom = new FormAttachment(button_1_1_1_1, 40,
                                                       SWT.BOTTOM);
        formData_3_1_1_1_2.top    = new FormAttachment(button_1_1_1_1, 5,
                                                       SWT.BOTTOM);
        button_1_1_1_2.setLayoutData(formData_3_1_1_1_2);
        button_1_1_1_2.setText("button");
        currManinMenuArray.add(button_1_1_1_2);

        final Button button_1_1_1_3 = new Button(groupMainMenu, SWT.FLAT);
        button_1_1_1_3.setAlignment(SWT.LEFT);
        button_1_1_1_3.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_1_1_1_3.setVisible(false);

        final FormData formData_3_1_1_1_3 = new FormData();
        formData_3_1_1_1_3.right  = new FormAttachment(button_1_1_1_2, 0,
                                                       SWT.RIGHT);
        formData_3_1_1_1_3.top    = new FormAttachment(0, 240);
        formData_3_1_1_1_3.bottom = new FormAttachment(0, 275);
        formData_3_1_1_1_3.left   = new FormAttachment(button_1_1_1_2, 0,
                                                       SWT.LEFT);
        button_1_1_1_3.setLayoutData(formData_3_1_1_1_3);
        button_1_1_1_3.setText("button");
        currManinMenuArray.add(button_1_1_1_3);

        final Button button_1_1_1_4 = new Button(groupMainMenu, SWT.FLAT);
        button_1_1_1_4.setAlignment(SWT.LEFT);
        button_1_1_1_4.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_1_1_1_4.setVisible(false);

        final FormData formData_3_1_1_1_4 = new FormData();
        formData_3_1_1_1_4.left = new FormAttachment(button_1_1_1_3, 0, SWT.LEFT);
        formData_3_1_1_1_4.right  = new FormAttachment(button_1_1_1_3, 0,
                                                       SWT.RIGHT);
        formData_3_1_1_1_4.top    = new FormAttachment(0, 280);
        formData_3_1_1_1_4.bottom = new FormAttachment(0, 315);
        button_1_1_1_4.setLayoutData(formData_3_1_1_1_4);
        button_1_1_1_4.setText("button");
        currManinMenuArray.add(button_1_1_1_4);

        final Button button_1_1_1_5 = new Button(groupMainMenu, SWT.FLAT);
        button_1_1_1_5.setAlignment(SWT.LEFT);
        button_1_1_1_5.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_1_1_1_5.setVisible(false);

        final FormData formData_3_1_1_1_5 = new FormData();
        formData_3_1_1_1_5.right  = new FormAttachment(button_1_1_1_4, 0,
                                                       SWT.RIGHT);
        formData_3_1_1_1_5.top    = new FormAttachment(0, 320);
        formData_3_1_1_1_5.bottom = new FormAttachment(0, 355);
        formData_3_1_1_1_5.left   = new FormAttachment(button_1_1_1_4, 0,
                                                       SWT.LEFT);
        button_1_1_1_5.setLayoutData(formData_3_1_1_1_5);
        button_1_1_1_5.setText("button");
        currManinMenuArray.add(button_1_1_1_5);

        final Button button_1_1_1_5_1 = new Button(groupMainMenu, SWT.FLAT);
        button_1_1_1_5_1.setAlignment(SWT.LEFT);
        button_1_1_1_5_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_1_1_1_5_1.setVisible(false);

        final FormData formData_3_1_1_1_5_1 = new FormData();
        formData_3_1_1_1_5_1.left   = new FormAttachment(button_1_1_1_5, 0,
                                                         SWT.LEFT);
        formData_3_1_1_1_5_1.top    = new FormAttachment(0, 360);
        formData_3_1_1_1_5_1.bottom = new FormAttachment(0, 395);
        formData_3_1_1_1_5_1.right  = new FormAttachment(button_1_1_1_5, 0,
                                                         SWT.RIGHT);
        button_1_1_1_5_1.setLayoutData(formData_3_1_1_1_5_1);
        button_1_1_1_5_1.setText("button");
        currManinMenuArray.add(button_1_1_1_5_1);

        final Button button_1_1_1_5_1_1 = new Button(groupMainMenu, SWT.FLAT);
        button_1_1_1_5_1_1.setAlignment(SWT.LEFT);
        button_1_1_1_5_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_1_1_1_5_1_1.setVisible(false);

        final FormData formData_3_1_1_1_5_1_1 = new FormData();
        formData_3_1_1_1_5_1_1.left   = new FormAttachment(button_1_1_1_5_1, 0,
                                                           SWT.LEFT);
        formData_3_1_1_1_5_1_1.right  = new FormAttachment(button_1_1_1_5_1, 0,
                                                           SWT.RIGHT);
        formData_3_1_1_1_5_1_1.bottom = new FormAttachment(button_1_1_1_5_1,
                                                           40, SWT.BOTTOM);
        formData_3_1_1_1_5_1_1.top    = new FormAttachment(button_1_1_1_5_1, 5,
                                                           SWT.BOTTOM);
        button_1_1_1_5_1_1.setLayoutData(formData_3_1_1_1_5_1_1);
        button_1_1_1_5_1_1.setText("button");
        currManinMenuArray.add(button_1_1_1_5_1_1);

        //----------------------------------------------------------------------------------------------

        //groupTwoLevelMenu中的Button
        final Button button_2 = new Button(groupTwoLevelMenu, SWT.FLAT);
        button_2.setAlignment(SWT.LEFT);
        button_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_2.setVisible(false);
        button.setVisible(false);

        final FormData formData_3_2 = new FormData();
        formData_3_2.right  = new FormAttachment(100, -5);
        formData_3_2.top    = new FormAttachment(0, 0);
        formData_3_2.bottom = new FormAttachment(0, 35);
        formData_3_2.left   = new FormAttachment(0, 5);
        button_2.setLayoutData(formData_3_2);
        button_2.setText("button");
        currTwoLevelMenuArray.add(button_2);

        final Button button_2_1 = new Button(groupTwoLevelMenu, SWT.FLAT);
        button_2_1.setAlignment(SWT.LEFT);
        button_2_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_2_1.setVisible(false);

        final FormData formData_3_2_1 = new FormData();
        formData_3_2_1.top    = new FormAttachment(0, 40);
        formData_3_2_1.bottom = new FormAttachment(0, 75);
        formData_3_2_1.right  = new FormAttachment(button_2, 249, SWT.LEFT);
        formData_3_2_1.left   = new FormAttachment(button_2, 0, SWT.LEFT);
        button_2_1.setLayoutData(formData_3_2_1);
        button_2_1.setText("button");
        currTwoLevelMenuArray.add(button_2_1);

        final Button button_2_2 = new Button(groupTwoLevelMenu, SWT.FLAT);
        button_2_2.setAlignment(SWT.LEFT);
        button_2_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_2_2.setVisible(false);
        button.setVisible(false);

        final FormData formData_3_2_2 = new FormData();
        formData_3_2_2.top    = new FormAttachment(0, 80);
        formData_3_2_2.bottom = new FormAttachment(0, 115);
        formData_3_2_2.right  = new FormAttachment(button_2_1, 249, SWT.LEFT);
        formData_3_2_2.left   = new FormAttachment(button_2_1, 0, SWT.LEFT);
        button_2_2.setLayoutData(formData_3_2_2);
        button_2_2.setText("button");
        currTwoLevelMenuArray.add(button_2_2);

        final Button button_2_3 = new Button(groupTwoLevelMenu, SWT.FLAT);
        button_2_3.setAlignment(SWT.LEFT);
        button_2_3.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_2_3.setVisible(false);

        final FormData formData_3_2_3 = new FormData();
        formData_3_2_3.bottom = new FormAttachment(button_2_2, 40, SWT.BOTTOM);
        formData_3_2_3.top    = new FormAttachment(button_2_2, 5, SWT.BOTTOM);
        formData_3_2_3.right  = new FormAttachment(button_2_2, 249, SWT.LEFT);
        formData_3_2_3.left   = new FormAttachment(button_2_2, 0, SWT.LEFT);
        button_2_3.setLayoutData(formData_3_2_3);
        button_2_3.setText("button");
        currTwoLevelMenuArray.add(button_2_3);

        final Button button_2_4 = new Button(groupTwoLevelMenu, SWT.FLAT);
        button_2_4.setAlignment(SWT.LEFT);
        button_2_4.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_2_4.setVisible(false);

        final FormData formData_3_2_4 = new FormData();
        formData_3_2_4.bottom = new FormAttachment(button_2_3, 40, SWT.BOTTOM);
        formData_3_2_4.top    = new FormAttachment(button_2_3, 5, SWT.BOTTOM);
        formData_3_2_4.right  = new FormAttachment(button_2_3, 249, SWT.LEFT);
        formData_3_2_4.left   = new FormAttachment(button_2_3, 0, SWT.LEFT);
        button_2_4.setLayoutData(formData_3_2_4);
        button_2_4.setText("button");
        currTwoLevelMenuArray.add(button_2_4);

        final Button button_2_5 = new Button(groupTwoLevelMenu, SWT.FLAT);
        button_2_5.setAlignment(SWT.LEFT);
        button_2_5.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_2_5.setVisible(false);

        final FormData formData_3_2_5 = new FormData();
        formData_3_2_5.bottom = new FormAttachment(button_2_4, 40, SWT.BOTTOM);
        formData_3_2_5.top    = new FormAttachment(button_2_4, 5, SWT.BOTTOM);
        formData_3_2_5.right  = new FormAttachment(button_2_4, 249, SWT.LEFT);
        formData_3_2_5.left   = new FormAttachment(button_2_4, 0, SWT.LEFT);
        button_2_5.setLayoutData(formData_3_2_5);
        button_2_5.setText("button");
        currTwoLevelMenuArray.add(button_2_5);

        final Button button_2_6 = new Button(groupTwoLevelMenu, SWT.FLAT);
        button_2_6.setAlignment(SWT.LEFT);
        button_2_6.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_2_6.setVisible(false);

        final FormData formData_3_2_6 = new FormData();
        formData_3_2_6.top    = new FormAttachment(0, 240);
        formData_3_2_6.bottom = new FormAttachment(0, 275);
        formData_3_2_6.right  = new FormAttachment(button_2_5, 249, SWT.LEFT);
        formData_3_2_6.left   = new FormAttachment(button_2_5, 0, SWT.LEFT);
        button_2_6.setLayoutData(formData_3_2_6);
        button_2_6.setText("button");
        currTwoLevelMenuArray.add(button_2_6);

        final Button button_2_7 = new Button(groupTwoLevelMenu, SWT.FLAT);
        button_2_7.setAlignment(SWT.LEFT);
        button_2_7.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_2_7.setVisible(false);

        final FormData formData_3_2_7 = new FormData();
        formData_3_2_7.bottom = new FormAttachment(button_2_6, 40, SWT.BOTTOM);
        formData_3_2_7.top    = new FormAttachment(button_2_6, 5, SWT.BOTTOM);
        formData_3_2_7.right  = new FormAttachment(button_2_6, 249, SWT.LEFT);
        formData_3_2_7.left   = new FormAttachment(button_2_6, 0, SWT.LEFT);
        button_2_7.setLayoutData(formData_3_2_7);
        button_2_7.setText("button");
        currTwoLevelMenuArray.add(button_2_7);

        final Button button_2_8 = new Button(groupTwoLevelMenu, SWT.FLAT);
        button_2_8.setAlignment(SWT.LEFT);
        button_2_8.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_2_8.setVisible(false);

        final FormData formData_3_2_8 = new FormData();
        formData_3_2_8.top    = new FormAttachment(0, 320);
        formData_3_2_8.bottom = new FormAttachment(0, 355);
        formData_3_2_8.right  = new FormAttachment(button_2_7, 249, SWT.LEFT);
        formData_3_2_8.left   = new FormAttachment(button_2_7, 0, SWT.LEFT);
        button_2_8.setLayoutData(formData_3_2_8);
        button_2_8.setText("button");
        currTwoLevelMenuArray.add(button_2_8);

        final Button button_2_9 = new Button(groupTwoLevelMenu, SWT.FLAT);
        button_2_9.setAlignment(SWT.LEFT);
        button_2_9.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_2_9.setVisible(false);

        final FormData formData_3_2_9 = new FormData();
        formData_3_2_9.bottom = new FormAttachment(button_2_8, 40, SWT.BOTTOM);
        formData_3_2_9.top    = new FormAttachment(button_2_8, 5, SWT.BOTTOM);
        formData_3_2_9.right  = new FormAttachment(button_2_8, 249, SWT.LEFT);
        formData_3_2_9.left   = new FormAttachment(button_2_8, 0, SWT.LEFT);
        button_2_9.setLayoutData(formData_3_2_9);
        button_2_9.setText("button");
        currTwoLevelMenuArray.add(button_2_9);

        final Button button_2_10 = new Button(groupTwoLevelMenu, SWT.FLAT);
        button_2_10.setAlignment(SWT.LEFT);
        button_2_10.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_2_10.setVisible(false);

        final FormData formData_3_2_10 = new FormData();
        formData_3_2_10.bottom = new FormAttachment(button_2_9, 40, SWT.BOTTOM);
        formData_3_2_10.top    = new FormAttachment(button_2_9, 5, SWT.BOTTOM);
        formData_3_2_10.right  = new FormAttachment(button_2_9, 249, SWT.LEFT);
        formData_3_2_10.left   = new FormAttachment(button_2_9, 0, SWT.LEFT);
        button_2_10.setLayoutData(formData_3_2_10);
        button_2_10.setText("button");
        currTwoLevelMenuArray.add(button_2_10);

        //-----------------------------------------------------------------------------------------

        //groupThreeLevelMenu中的Button
        final Button button_2_13 = new Button(groupThreeLevelMenu, SWT.FLAT);
        button_2_13.setAlignment(SWT.LEFT);
        button_2_13.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_2_13.setVisible(false);

        final FormData formData_3_2_13 = new FormData();
        formData_3_2_13.top    = new FormAttachment(0, 0);
        formData_3_2_13.bottom = new FormAttachment(0, 35);
        formData_3_2_13.left   = new FormAttachment(0, 1);
        formData_3_2_13.right  = new FormAttachment(0, 230);
        button_2_13.setLayoutData(formData_3_2_13);
        button_2_13.setText("button");
        currThreeLevelMenuArray.add(button_2_13);

        final Button button_2_13_1 = new Button(groupThreeLevelMenu, SWT.FLAT);
        button_2_13_1.setAlignment(SWT.LEFT);
        button_2_13_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_2_13_1.setVisible(false);

        final FormData formData_3_2_13_1 = new FormData();
        formData_3_2_13_1.right  = new FormAttachment(button_2_13, 0, SWT.RIGHT);
        formData_3_2_13_1.left   = new FormAttachment(button_2_13, 0, SWT.LEFT);
        formData_3_2_13_1.bottom = new FormAttachment(button_2_13, 40,
                                                      SWT.BOTTOM);
        formData_3_2_13_1.top    = new FormAttachment(button_2_13, 5, SWT.BOTTOM);
        button_2_13_1.setLayoutData(formData_3_2_13_1);
        button_2_13_1.setText("button");
        currThreeLevelMenuArray.add(button_2_13_1);

        final Button button_2_13_2 = new Button(groupThreeLevelMenu, SWT.FLAT);
        button_2_13_2.setAlignment(SWT.LEFT);
        button_2_13_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_2_13_2.setVisible(false);

        final FormData formData_3_2_13_2 = new FormData();
        formData_3_2_13_2.right  = new FormAttachment(button_2_13_1, 0,
                                                      SWT.RIGHT);
        formData_3_2_13_2.left   = new FormAttachment(button_2_13_1, 0, SWT.LEFT);
        formData_3_2_13_2.bottom = new FormAttachment(button_2_13_1, 40,
                                                      SWT.BOTTOM);
        formData_3_2_13_2.top    = new FormAttachment(button_2_13_1, 5,
                                                      SWT.BOTTOM);
        button_2_13_2.setLayoutData(formData_3_2_13_2);
        button_2_13_2.setText("button");
        currThreeLevelMenuArray.add(button_2_13_2);

        final Button button_2_13_3 = new Button(groupThreeLevelMenu, SWT.FLAT);
        button_2_13_3.setAlignment(SWT.LEFT);
        button_2_13_3.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_2_13_3.setVisible(false);

        final FormData formData_3_2_13_3 = new FormData();
        formData_3_2_13_3.right  = new FormAttachment(button_2_13_2, 0,
                                                      SWT.RIGHT);
        formData_3_2_13_3.left   = new FormAttachment(button_2_13_2, 0, SWT.LEFT);
        formData_3_2_13_3.bottom = new FormAttachment(button_2_13_2, 40,
                                                      SWT.BOTTOM);
        formData_3_2_13_3.top    = new FormAttachment(button_2_13_2, 5,
                                                      SWT.BOTTOM);
        button_2_13_3.setLayoutData(formData_3_2_13_3);
        button_2_13_3.setText("button");
        currThreeLevelMenuArray.add(button_2_13_3);

        final Button button_2_13_4 = new Button(groupThreeLevelMenu, SWT.FLAT);
        button_2_13_4.setAlignment(SWT.LEFT);
        button_2_13_4.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_2_13_4.setVisible(false);

        final FormData formData_3_2_13_4 = new FormData();
        formData_3_2_13_4.right  = new FormAttachment(button_2_13_3, 0,
                                                      SWT.RIGHT);
        formData_3_2_13_4.left   = new FormAttachment(button_2_13_3, 0, SWT.LEFT);
        formData_3_2_13_4.top    = new FormAttachment(0, 160);
        formData_3_2_13_4.bottom = new FormAttachment(0, 195);
        button_2_13_4.setLayoutData(formData_3_2_13_4);
        button_2_13_4.setText("button");
        currThreeLevelMenuArray.add(button_2_13_4);

        final Button button_2_13_5 = new Button(groupThreeLevelMenu, SWT.FLAT);
        button_2_13_5.setAlignment(SWT.LEFT);
        button_2_13_5.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_2_13_5.setVisible(false);

        final FormData formData_3_2_13_5 = new FormData();
        formData_3_2_13_5.right  = new FormAttachment(button_2_13_4, 0,
                                                      SWT.RIGHT);
        formData_3_2_13_5.left   = new FormAttachment(button_2_13_4, 0, SWT.LEFT);
        formData_3_2_13_5.bottom = new FormAttachment(button_2_13_4, 40,
                                                      SWT.BOTTOM);
        formData_3_2_13_5.top    = new FormAttachment(button_2_13_4, 5,
                                                      SWT.BOTTOM);
        button_2_13_5.setLayoutData(formData_3_2_13_5);
        button_2_13_5.setText("button");
        currThreeLevelMenuArray.add(button_2_13_5);

        final Button button_2_13_6 = new Button(groupThreeLevelMenu, SWT.FLAT);
        button_2_13_6.setAlignment(SWT.LEFT);
        button_2_13_6.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_2_13_6.setVisible(false);

        final FormData formData_3_2_13_6 = new FormData();
        formData_3_2_13_6.right = new FormAttachment(button_2_13_5, 0, SWT.RIGHT);
        formData_3_2_13_6.left  = new FormAttachment(button_2_13_5, 0, SWT.LEFT);
        button_2_13_6.setLayoutData(formData_3_2_13_6);
        button_2_13_6.setText("button");
        currThreeLevelMenuArray.add(button_2_13_6);

        Button button_2_13_7;
        button_2_13_7 = new Button(groupThreeLevelMenu, SWT.FLAT);
        button_2_13_7.setAlignment(SWT.LEFT);
        button_2_13_7.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_2_13_7.setVisible(false);
        formData_3_2_13_6.top    = new FormAttachment(button_2_13_7, -40,
                                                      SWT.TOP);
        formData_3_2_13_6.bottom = new FormAttachment(button_2_13_7, -5, SWT.TOP);

        final FormData formData_3_2_13_7 = new FormData();
        formData_3_2_13_7.right  = new FormAttachment(button_2_13_6, 0,
                                                      SWT.RIGHT);
        formData_3_2_13_7.left   = new FormAttachment(button_2_13_6, 0, SWT.LEFT);
        formData_3_2_13_7.top    = new FormAttachment(0, 280);
        formData_3_2_13_7.bottom = new FormAttachment(0, 315);
        button_2_13_7.setLayoutData(formData_3_2_13_7);
        button_2_13_7.setText("button");
        currThreeLevelMenuArray.add(button_2_13_7);

        final Button button_2_13_8 = new Button(groupThreeLevelMenu, SWT.FLAT);
        button_2_13_8.setAlignment(SWT.LEFT);
        button_2_13_8.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_2_13_8.setVisible(false);

        final FormData formData_3_2_13_8 = new FormData();
        formData_3_2_13_8.left   = new FormAttachment(button_2_13_7, 0, SWT.LEFT);
        formData_3_2_13_8.right  = new FormAttachment(button_2_13_7, 0,
                                                      SWT.RIGHT);
        formData_3_2_13_8.bottom = new FormAttachment(button_2_13_7, 40,
                                                      SWT.BOTTOM);
        formData_3_2_13_8.top    = new FormAttachment(button_2_13_7, 5,
                                                      SWT.BOTTOM);
        button_2_13_8.setLayoutData(formData_3_2_13_8);
        button_2_13_8.setText("button");
        currThreeLevelMenuArray.add(button_2_13_8);

        final Button button_2_13_9 = new Button(groupThreeLevelMenu, SWT.FLAT);
        button_2_13_9.setAlignment(SWT.LEFT);
        button_2_13_9.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_2_13_9.setVisible(false);

        final FormData formData_3_2_13_9 = new FormData();
        formData_3_2_13_9.bottom = new FormAttachment(button_2_13_8, 40,
                                                      SWT.BOTTOM);
        formData_3_2_13_9.top    = new FormAttachment(button_2_13_8, 5,
                                                      SWT.BOTTOM);
        formData_3_2_13_9.right  = new FormAttachment(button_2_13_8, 229,
                                                      SWT.LEFT);
        formData_3_2_13_9.left   = new FormAttachment(button_2_13_8, 0, SWT.LEFT);
        button_2_13_9.setLayoutData(formData_3_2_13_9);
        button_2_13_9.setText("button");
        currThreeLevelMenuArray.add(button_2_13_9);

        final Button button_2_13_10 = new Button(groupThreeLevelMenu, SWT.FLAT);
        button_2_13_10.setAlignment(SWT.LEFT);
        button_2_13_10.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_2_13_10.setVisible(false);

        final FormData formData_3_2_13_10 = new FormData();
        formData_3_2_13_10.right  = new FormAttachment(button_2_13_9, 0,
                                                       SWT.RIGHT);
        formData_3_2_13_10.left   = new FormAttachment(button_2_13_9, 0,
                                                       SWT.LEFT);
        formData_3_2_13_10.bottom = new FormAttachment(button_2_13_9, 40,
                                                       SWT.BOTTOM);
        formData_3_2_13_10.top    = new FormAttachment(button_2_13_9, 5,
                                                       SWT.BOTTOM);
        button_2_13_10.setLayoutData(formData_3_2_13_10);
        button_2_13_10.setText("button");
        currThreeLevelMenuArray.add(button_2_13_10);

        print = new Button(shell, SWT.NONE);
        
        final FormData fd_button_3 = new FormData();
        fd_button_3.bottom = new FormAttachment(100, -5);
        fd_button_3.right = new FormAttachment(100, -5);
        fd_button_3.top = new FormAttachment(groupThreeLevelMenu, 0, SWT.BOTTOM);
        fd_button_3.left = new FormAttachment(groupThreeLevelMenu, 5, SWT.RIGHT);
        print.setLayoutData(fd_button_3);
        print.setText("button");
        print.setVisible(false);
        //---------------------------------------------------------------------------------------------
    }

    public ArrayList getCurrManinMenuArray()
    {
        return currManinMenuArray;
    }

    public ArrayList getCurrTwoLevelMenuArray()
    {
        return currTwoLevelMenuArray;
    }

    public ArrayList getCurrThreeLevelMenuArray()
    {
        return currThreeLevelMenuArray;
    }

    public String getRole()
    {
        return role;
    }

    public boolean appendDefaultRole()
    {
        return this.appenddefaultrole;
    }
    
    public Shell getShell()
    {
        return shell;
    }
}
