package com.efuture.commonKit;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.swtdesigner.SWTResourceManager;


/**
 *
 * 提供表界面，和新加的方法
 */
public class PosTable extends Table
{
    private Vector content = new Vector();
    private String[] title = null;
    private int[] columnWidth = null;
    int row = 0;
    private int tableMaxContent = 9999;
    private int tableContent = -1;
    private Table tableComp = null;
    private TableColumn[] tablegroup = null;
    private Font font = null;
    private boolean flag = false;
    private int pagesize = 0;
    private int curRow = -1;
    private Control focusControl = null;
    private Color lastcolor = null;
    
    public boolean IsLoopSelection = false;
    
    private NewSelectionAdapter NewSelectionEvent = null;
    public interface NewSelectionAdapter
    {
    	public void widgetSelected(int oldindex,int index);
    }
    
    public PosTable(Composite parent, int style)
    {
    	super(parent, style);
        
        initPosTable(false,null);
    }
    
    public PosTable(Composite parent,int style,boolean f)
    {
        super(parent, style);
        
        initPosTable(f,null);
    }
    
    //初始化，flag 为确定是否开头有序列号,focuswidget 一般PosTable获得焦点,focuswidget为操作完PosTable后保持焦点的Widget
    public PosTable(Composite parent, int style, boolean f ,Control focuscol)
    {
        super(parent, style);
        
        initPosTable(f,focuscol);
    }

    public void initPosTable( boolean f ,Control focuscol)
    {
    	flag = f;
        this.setLayout(new FillLayout());
        tableComp = this;
        focusControl = focuscol;
        
        tableComp.addSelectionListener(new SelectionAdapter()
        {
        	public void widgetSelected(SelectionEvent e)
        	{
        		int index = getmousesel();
        		
        		if (index >= 0 && index < tableComp.getItemCount())
        		tableComp.deselect(index);
        		
        		setSelection(index);
        	}
        });
        
        // 虚拟Table形式
        if ((tableComp.getStyle() & SWT.VIRTUAL) != 0)
        {
        	final int PAGE_SIZE = 100;  
        	tableComp.addListener (SWT.SetData, new Listener () 
        	{ 
                public void handleEvent (Event event) 
                { 
                	if (content == null) return;
                	
                    TableItem item = (TableItem) event.item; 
                    int index = event.index; 
                    
                    if (index >= content.size()) return;
                    
                    int page = index / PAGE_SIZE; 
                    int start = page * PAGE_SIZE; 
                    int end = start + PAGE_SIZE; 
                    end = Math.min (end, tableComp.getItemCount ()); 
                    for (int i = start; i < end; i++) { 
                       item = tableComp.getItem (i); 
                      item.setText ((String[])content.get(i)); 
                   } 
                } 
           });
        }
    }
    
    public void addNewSelectionListener(NewSelectionAdapter sca)
    {
    	NewSelectionEvent = sca;
    }
    
    public int getmousesel()
    {
    	return super.getSelectionIndex();
    }
    
    public void setFocusedControl(Control focusctl)
    {
    	focusControl = focusctl;
    }
    
    //改变表体信息
    public void exchangeContent(Vector v)
    {
        content = v;
        
        setContent();
    }

    //设定表头
    public void setTitle(String[] title)
    {
        row        = title.length;
        this.title = title;
    }

    //设定表头宽度
    public void setWidth(int[] columnWidth)
    {
        row              = title.length;
        this.columnWidth = columnWidth;
    }

    public String[] changeItemVar(int location)
    {
        String[] rowVar = (String[]) content.elementAt(location);

        return rowVar;
    }

    public void addRowWithRefresh(String[] rowContext)
    {
        if (row == 0)
        {
            setTitle(rowContext);
        }

        if (rowContext == null)
        {
            return;
        }

        if (rowContext.length != row)
        {
            System.out.println("false");

            return;
        }

        int lenght = content.size();

        if ((lenght > tableMaxContent) ||
                ((tableContent != -1) && (lenght > tableContent)))
        {
            return;
        }

        content.add(rowContext);

        setContent();
    }

    public void addRow(String[] rowContext)
    {
    	
        content.add(rowContext);
        System.out.println(content.size());
        TableItem item = new TableItem(tableComp, SWT.NONE);

        if (flag)
        {
        	rowContext[0] = String.valueOf(content.size());
        }

        item.setText(rowContext);
    }

    //删除表单行
    public void deleteRow(int index)
    {
        if ((index < 0) || (index >= (content.size())))
        {
            return;
        }

        content.remove(index);
        
        tableComp.remove(index);
        
        curRow = curRow - 1;
        
        if (flag)
        {
        	for ( int i = index ; i < tableComp.getItemCount() ; i++)
        	{
        		TableItem item = tableComp.getItem(i);
                item.setText(0,String.valueOf(i+1));
        	}
        }
        
    }

    //修改表单行
    public void modifyRow(String[] rowContext, int index)
    {
        if ((index < 0) || (index > (content.size() - 1)))
        {
            return;
        }

        content.set(index, rowContext);
        
        TableItem item = tableComp.getItem(index);
        item.setText(rowContext);
    }
    

    //重新得到表内容
    public void setContent()
    {
        tableComp.removeAll();

        if (tableComp == null)
        {
            return;
        }

        // 如果是虚拟表，则不采用这种填充的方式填充数据
        // 虚拟Table形式
        if ((tableComp.getStyle() & SWT.VIRTUAL) != 0)
        {
        	tableComp.setItemCount(content.size());
        }
        else
        {
	        for (int i = 0; content != null && i < content.size(); i++)
	        {
	            TableItem item = new TableItem(tableComp, SWT.NONE);
	
	            String[] rowData = (String[]) content.get(i);
	
	            if (flag)
	            {
	                rowData[0] = i + 1 + "";
	            }
	
	            if (font != null)
	            {
	                item.setFont(font);
	            }
	
	            item.setText(rowData);
	        }
        }
    }
    
    public void clearRow()
    {
        Vector v = new Vector();
        exchangeContent(v);
    }

    //初始化表头
    public void initialize()
    {
        if ((tableComp == null) || (title == null))
        {
            return;
        }

        tablegroup = new TableColumn[title.length];

        for (int i = 0; i < title.length; i++)
        {
        	int style1 = SWT.LEFT;
        	if (columnWidth != null && columnWidth[i] < 0)
        	{
        		style1 = SWT.RIGHT;
        	}
            TableColumn aTableColumn = new TableColumn(tableComp, style1);
            tablegroup[i] = aTableColumn;
            aTableColumn.setText(title[i]);

            if (columnWidth != null)
            {
                if (columnWidth[i] == 0)
                {
                    aTableColumn.setWidth(100);
                }
                else
                {
                    aTableColumn.setWidth(Math.abs(columnWidth[i]));
                }
            }
        }
    }

    //获得指定column的所有数值的和
    public String getSum(String selectTitle)
    {
        if ((selectTitle == null) || (selectTitle.trim().length() == 0))
        {
            return "-0";
        }

        int index = -1;

        for (int i = 0; i < title.length; i++)
        {
            if (selectTitle.equals(title[i]))
            {
                index = i;

                break;
            }
        }
        
        if (index == -1)
        {
            return "-0";
        }

        double sum = 0.0;
        int dotposition = 0;

        for (int i = 0; i < content.size(); i++)
        {
            String[] aRow = (String[]) content.get(i);
            int tmpDot = aRow[index].length() - aRow[index].indexOf(".");

            if (tmpDot > dotposition)
            {
                dotposition = tmpDot;
            }

            sum = sum + Double.parseDouble(aRow[index]);
        }

        String total = new Double(sum).toString();
        int dot1 = total.indexOf(".");
        int dot2 = total.length() - total.indexOf(".");

        if (dotposition < dot2)
        {
            total = total.substring(0, dot1 + dotposition);
        }

        return total;
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        Display d = new Display();
        final Shell s = new Shell(d);
        s.setSize(500, 400);
        s.setText("Table text");
        s.setLayout(new FillLayout());

        final PosTable table = new PosTable(s, SWT.BORDER | SWT.FULL_SELECTION,
                                            true);
        String[] titles = { "Name", "Grade", "Class", "Score" };
        table.setTitle(titles);

        int[] width = new int[4];
        width[0] = 100;
        width[1] = 150;
        width[3] = 200;

        table.setWidth(width);

        table.initialize();
        table.setTableContent(3);

        table.addRow(new String[] { "Test", "Grade one", "Class three", "93" });
        table.addRow(new String[] { "Test1", "Grade one", "Class three", "94.0123" });
        table.addRow(new String[] { "Test2", "Grade one", "Class three", "94" });
        table.addRow(new String[] { "Test3", "Grade one", "Class three", "94" });
        //table.addKeyListener(this);
        table.setContent();

        //table.deleteRow(3);

        //table.modifyRow(new String[] { "Test3", "Grade one", "Class three","1200" },2);

        // table.deleteRow(2);
        System.out.print(table.getSum("Score"));
        s.open();

        while (!s.isDisposed())
        {
            if (!d.readAndDispatch())
            {
                d.sleep();
            }
        }

        d.dispose();
    }

    //得到所有内容长度
    public int getTableCount()
    {
        return content.size();
    }

    public int getTableContent()
    {
        return tableContent;
    }

    public void setTableContent(int tableContent)
    {
        this.tableContent = tableContent;
    }

    public int getTableMaxContent()
    {
        return tableMaxContent;
    }

    public void setTableMaxContent(int tableMaxContent)
    {
        this.tableMaxContent = tableMaxContent;
    }

    public void keyPressed(KeyEvent e)
    {
    }

    public void keyReleased(KeyEvent e)
    {
    }

    protected void checkSubclass()
    {
        return;
    }

    //清空
    public void clear()
    {
    	curRow = -1;
        content.removeAllElements();
        tableComp.removeAll();
    }

    //指向最后商品
    public void assignLast()
    {
        tableComp.setSelection(tableComp.getItemCount() - 1);
        showSelection();
    }
    
    public int getSelectionIndex()
    {
    	return curRow;
    }
    
    public void setSelection(int index)
    {	
    	int oldindex = this.curRow;
    	int i = tableComp.getSelectionIndex();
    	TableItem item = null;
    	if (i >= 0 && i < tableComp.getItemCount())
    	{
    		item = tableComp.getItem(i);
    		item.setBackground(SWTResourceManager.getColor(255, 255, 255));
    		if (lastcolor != null) item.setForeground(lastcolor);
    		else item.setForeground(SWTResourceManager.getColor(0,0,0));
    	}
    	
    	if (index < tableComp.getItemCount() && index >= 0)
    	{
    		item = tableComp.getItem(index);
    		lastcolor = item.getForeground();
        	item.setBackground(SWTResourceManager.getColor(43, 61, 219));
        	item.setForeground(SWTResourceManager.getColor(255,255,255));
        	curRow = index;
    	}
    	
    	showSelection();
    	
    	int newindex = this.curRow;
    	
    	if (focusControl != null) focusControl.setFocus(); 
    	
		if (NewSelectionEvent != null)
		{
			NewSelectionEvent.widgetSelected(oldindex, newindex);
		}
    }
    
    public void showSelection()
    {
    	if (curRow < this.getItemCount() && curRow >= 0 )
    	{
	    	TableItem item = this.getItem(curRow);
	    	this.showItem(item);
    	}
    }
    
    //光标上移
    public void moveUp()
    {
        try
        {
        	if (tableComp.getItemCount() <= 0) return;
        	
            if (tableComp.getSelectionIndex() <= 0)
            {
            	if (IsLoopSelection)
            	{
            		tableComp.setSelection(tableComp.getItemCount() - 1);
            	}
            }
            else
            {
	            tableComp.setSelection(tableComp.getSelectionIndex() - 1);
            }
        }
        catch (Exception er)
        {
        }
    }

    //	光标下移
    public void moveDown()
    {
        try
        {
        	if (tableComp.getItemCount() <= 0) return;
        	
            if (tableComp.getSelectionIndex() >= (tableComp.getItemCount() - 1))
            {
            	if (IsLoopSelection)
            	{
            		tableComp.setSelection(0);
            	}
            }
            else
            {
            	tableComp.setSelection(tableComp.getSelectionIndex() + 1);
            }
        }
        catch (Exception er)
        {
        }
    }
    
    
    public void PageUp()
    {

           if (tableComp.getSelectionIndex() > 0)
           {
           	int showpage = 0;
           	
           	if(pagesize == 0)
           	{
           		if(tableComp.getSelectionIndex() > ((tableComp.getBounds().height - tableComp.getHeaderHeight()) / tableComp.getItemHeight()))
           		{
           			pagesize = tableComp.getSelectionIndex() - ((tableComp.getBounds().height - tableComp.getHeaderHeight()) / tableComp.getItemHeight());
           			showpage = pagesize  - ((tableComp.getBounds().height - tableComp.getHeaderHeight()) / tableComp.getItemHeight()) - 1;
           		}
           		else
           		{
           			showpage = 0 ;
           		}
           	}
           	else
           	{
           		pagesize = pagesize - ((tableComp.getBounds().height - tableComp.getHeaderHeight()) / tableComp.getItemHeight());
           		showpage = pagesize - ((tableComp.getBounds().height - tableComp.getHeaderHeight()) / tableComp.getItemHeight()) - 1;
           	}

               if (showpage > 0)
               {
            	   tableComp.setSelection(showpage);
               }
               else
               {
            	   tableComp.setSelection(0);
                   pagesize = 0;
               }
           }
           
    }
    
    public void PageDown()
    {
        if (tableComp.getSelectionIndex() < (tableComp.getItemCount() - 1))
        {
        	int showpage = 0;
        	
        	if(tableComp.getSelectionIndex() < pagesize)
        	{
        		pagesize = tableComp.getSelectionIndex();
        	}
        		
        	pagesize = pagesize + ((tableComp.getBounds().height - tableComp.getHeaderHeight()) / tableComp.getItemHeight());
            showpage = pagesize + ((tableComp.getBounds().height - tableComp.getHeaderHeight()) / tableComp.getItemHeight()) + 1;
        	
        	
            if (showpage < (tableComp.getItemCount() - 1))
            {
            	tableComp.setSelection(showpage);
            }
            else
            {
            	tableComp.setSelection(tableComp.getItemCount() - 1);
                pagesize = tableComp.getItemCount() - 1;
            }
            
        }

    }
}
