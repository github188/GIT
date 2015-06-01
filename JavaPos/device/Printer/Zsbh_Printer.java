package device.Printer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.OrientationRequested;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Device.Interface.Interface_Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.Language;


public class Zsbh_Printer implements Interface_Printer
{
	PrintService printservice = null;
	Font printfont = null;
	int cutLine = 0;
	int rowByPage = 0;
	int pageleft = -1,pageright = -1,pagetop = -1,pagebottom = -1;
	OrientationRequested pagedirc = null;
	JavaxPrintable prt = null;
	
	String Print_file = "Print_Service_File.ini";
	
 
    public boolean open()
    {
        if (DeviceName.devicePrinter.length() <= 0)
        {
            return false;
        }

        try
        {
	        // 得到所有打印机列表
			PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null,null);
			
	        String[] arg = DeviceName.devicePrinter.split(",");
	        
	        if ((arg.length > 0) && (arg[0].length() > 0))
	        {
	        	int i = 0;
	    		for (i=0;i<printServices.length;i++)        
	    		{
	    			if (printServices[i].getName().equalsIgnoreCase(arg[0].trim()))
	    			{
	    				printservice = printServices[i];
	    				break;
	    			}
	    		}
	    		if (i >= printServices.length)
	    		{
	    			new MessageBox(Language.apply("系统打印机不存在!\n") + arg[0].trim());
	    			return false;
	    		}
	        }
	        
	        String fontname = "宋体";
	        int fontstyle = Font.PLAIN ,fontsize = 9;
	        if ((arg.length > 1) && (arg[1].length() > 0))
	        {
	        	fontname = arg[1].trim();
	        }
	        if ((arg.length > 2) && (arg[2].length() > 0))
	        {
	        	fontsize = Convert.toInt(arg[2].trim());
	        }
	        if ((arg.length > 3) && (arg[3].length() > 0))
	        {
	        	if (arg[3].trim().equalsIgnoreCase(Language.apply("粗体"))) fontstyle = Font.BOLD;
	        	else if (arg[3].trim().equalsIgnoreCase(Language.apply("斜体"))) fontstyle = Font.ITALIC;
	        	else fontstyle = Font.PLAIN;
	        }
	        printfont = new Font(fontname,fontstyle,fontsize);
	        
	        if ((arg.length > 4) && (arg[4].length() > 0))
	        {
	        	cutLine = Convert.toInt(arg[4]);
	        }
	        
	        if ((arg.length > 5) && (arg[5].length() > 0))
	        {
	        	rowByPage = Convert.toInt(arg[5]);
	        }

	        if ((arg.length > 6) && (arg[6].length() > 0))
	        {
	        	if (arg[6].trim().equalsIgnoreCase(Language.apply("横向"))) pagedirc = OrientationRequested.LANDSCAPE;
	        	else pagedirc = OrientationRequested.PORTRAIT;
	        }

	        if ((arg.length > 7) && (arg[7].length() > 0))
	        {
	        	pageleft = Convert.toInt(arg[7]);
	        }

	        if ((arg.length > 8) && (arg[8].length() > 0))
	        {
	        	pageright = Convert.toInt(arg[8]);
	        }
	        
	        if ((arg.length > 9) && (arg[9].length() > 0))
	        {
	        	pagetop = Convert.toInt(arg[9]);
	        }
	        
	        if ((arg.length > 10) && (arg[10].length() > 0))
	        {
	        	pagebottom = Convert.toInt(arg[10]);
	        }
	        
	        prt = new JavaxPrintable();
	    	prt.setPrintService(printservice, rowByPage, printfont);
	    	
	        return true;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            new MessageBox(Language.apply("打开系统打印机异常:\n") + ex.getMessage());
            return false;
        }	        
    }

    //缴款单打印时，字号要大一些
    public boolean openEx()
    {
        if (DeviceName.devicePrinter.length() <= 0)
        {
            return false;
        }

        try
        {
	        // 得到所有打印机列表
			PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null,null);
			
	        String[] arg = DeviceName.devicePrinter.split(",");
	        
	        if ((arg.length > 0) && (arg[0].length() > 0))
	        {
	        	int i = 0;
	    		for (i=0;i<printServices.length;i++)        
	    		{
	    			if (printServices[i].getName().equalsIgnoreCase(arg[0].trim()))
	    			{
	    				printservice = printServices[i];
	    				break;
	    			}
	    		}
	    		if (i >= printServices.length)
	    		{
	    			new MessageBox(Language.apply("系统打印机不存在!\n") + arg[0].trim());
	    			return false;
	    		}
	        }
	        
	        String fontname = "宋体";
	        int fontstyle = Font.PLAIN ,fontsize = 9;
	        if ((arg.length > 1) && (arg[1].length() > 0))
	        {
	        	fontname = arg[1].trim();
	        }
	        if ((arg.length > 2) && (arg[2].length() > 0))
	        {
	        	fontsize = 11;//Convert.toInt(arg[2].trim());//字号
	        }
	        if ((arg.length > 3) && (arg[3].length() > 0))
	        {
	        	if (arg[3].trim().equalsIgnoreCase(Language.apply("粗体"))) fontstyle = Font.BOLD;
	        	else if (arg[3].trim().equalsIgnoreCase(Language.apply("斜体"))) fontstyle = Font.ITALIC;
	        	else fontstyle = Font.PLAIN;
	        }
	        printfont = new Font(fontname,fontstyle,fontsize);
	        
	        if ((arg.length > 4) && (arg[4].length() > 0))
	        {
	        	cutLine = Convert.toInt(arg[4]);
	        }
	        
	        if ((arg.length > 5) && (arg[5].length() > 0))
	        {
	        	rowByPage = Convert.toInt(arg[5]);
	        }

	        if ((arg.length > 6) && (arg[6].length() > 0))
	        {
	        	if (arg[6].trim().equalsIgnoreCase(Language.apply("横向"))) pagedirc = OrientationRequested.LANDSCAPE;
	        	else pagedirc = OrientationRequested.PORTRAIT;
	        }

	        if ((arg.length > 7) && (arg[7].length() > 0))
	        {
	        	pageleft = Convert.toInt(arg[7]);
	        }

	        if ((arg.length > 8) && (arg[8].length() > 0))
	        {
	        	pageright = Convert.toInt(arg[8]);
	        }
	        
	        if ((arg.length > 9) && (arg[9].length() > 0))
	        {
	        	pagetop = Convert.toInt(arg[9]);
	        }
	        
	        if ((arg.length > 10) && (arg[10].length() > 0))
	        {
	        	pagebottom = Convert.toInt(arg[10]);
	        }
	        
	        prt = new JavaxPrintable();
	    	prt.setPrintService(printservice, rowByPage, printfont);
	    	
	        return true;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            new MessageBox(Language.apply("打开系统打印机异常:\n") + ex.getMessage());
            return false;
        }	        
    }

    
    public void close()
    {
    	printservice = null;
    	printfont = null;
    }

    public void setEnable(boolean enable)
    {
    	try
    	{
        	// 删除已存在的打印文件
        	new File(Print_file).delete();
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    }

    //将打印内容输出到文件
    public void printLine_Normal(String printStr)
    {
    	try
    	{
    		if (printservice != null)
        	{
    	        PrintWriter pw = CommonMethod.writeFileAppend(Print_file);
    	        pw.print(printStr);
    	        pw.flush();
    	        pw.close();
        	}
        	else
        	{
        		//new File(Print_file).delete();
        	}
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	   	
    	
    }
    
    public void printLine_Journal(String printStr)
    {
    	printLine_Normal(printStr);    	
    }

    
    public void printLine_Slip(String printStr)
    {
    	printLine_Normal(printStr);
    }

    public boolean passPage_Normal()
    {
        return false;
    }
    
    public boolean passPage_Journal()
    {
    	return passPage_Normal();
    }

    public boolean passPage_Slip()
    {
    	return passPage_Normal();
    }

    public void cutPaper_Normal()
    {
    	
    }
    
    public void cutPaper_Journal()
    {
    	
    }
    
//  输出到打印机
    public void cutPaper_Slip()
    {
    	boolean isJkdPrint = false;
    	try
    	{
    		//设置缴款单打印服务
    		BufferedReader brTmp = CommonMethod.readFile(Print_file);
        	if (brTmp == null) return;
        	String lineTmp = "";
        	
        	try
        	{
        		while ((lineTmp = brTmp.readLine()) != null)
            	{
        			//缴款单和银联结算单要打大字号
            		if (lineTmp.indexOf(Language.apply("缴款单号")) > 0 
            				|| lineTmp.indexOf(Language.apply("结算总计单")) > 0) 
            		{
            			isJkdPrint = true;
                		break;
            		}
            	}
        	}
        	catch(Exception ex)
        	{
        		ex.printStackTrace();
        	}
        	finally
        	{
        		brTmp.close();
        	}
        	if (isJkdPrint)
        	{

        		//关闭打印
        		close();
        		
        		//打开缴款字号的打印服务
        		if (!openEx())
        		{
        			close();
        			openEx();
        		}
        	}
        	

        	//开始输出到打印机...
        	StringBuffer text = new StringBuffer();
        	BufferedReader br = CommonMethod.readFile(Print_file);
        	if (br != null)
        	{
    	    	String line = null;
    	    	try
    			{
    	    		int rowCountForPrint=0;
    	    		int count = 40;
			    	if (ConfigClass.CustomItem5 != null)
			    	{
			    		if (ConfigClass.CustomItem5.split("\\|").length > 1)
			    		{
			    			count = Convert.toInt(ConfigClass.CustomItem5.split("\\|")[1].trim());
    			    		if (count <= 0)
    			    		{
    			    			count = 40;
    			    		}
			    		}
			    		
			    	}
			    	
    				while ((line = br.readLine()) != null)
    				{
    					
    					rowCountForPrint++;
    					
    					text.append(line + "\n");
    			    	
    			    	//是否换页
    			    	if (rowCountForPrint > ((custom.localize.Zsbh.Zsbh_Printer)Printer.getDefault()).getAreaEnd_Slip() 
    			    			&& ((custom.localize.Zsbh.Zsbh_Printer)Printer.getDefault()).getAreaEnd_Slip() > 1)
    			    	{
    			    		rowCountForPrint=0;
    			    		//System.out.println(text.toString());
//    						 打印
    				    	prt.printTextAction(text.toString());
    			    		text = new StringBuffer();
    			    		if (((custom.localize.Zsbh.Zsbh_Printer)Printer.getDefault()).getPagePrint_Slip())
    			    		{
    			    			//分页打印时才提示
    			    			new MessageBox(Language.apply("换纸提示:\n请按[任意键]后,继续打印..."));
    			    		}			    					    		
    			    	}
    			    	
    			    	//当不是小票和缴款单打印时,直接输入固定长度的BUFFER到打印机    			    	
    			    	if (rowCountForPrint > count
    			    			&& ((custom.localize.Zsbh.Zsbh_Printer)Printer.getDefault()).getAreaEnd_Slip() <= 1)
    			    	{
    			    		rowCountForPrint=0;
    			    		prt.printTextAction(text.toString());
    			    		text = new StringBuffer();

			    			//分页打印时才提示
			    			//new MessageBox("打印内容太多,打印机将自动走纸换页\n请上好打印纸以后,按[任意键],继续打印...");
    			    		
    			    	}
    			    	
    				}
    				
    				if (text != null)
    				{
    					//System.out.println(text.toString());
    					prt.printTextAction(text.toString());
    				}
    				
    				br.close();
    			}
    			catch (IOException e)
    			{
    				e.printStackTrace();
    			}
    			
        	}
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	finally
    	{
    		
    		if (isJkdPrint)
    		{
    			//还原到小票的打印服务
    			
        		//关闭打印
        		close();
        		
        		
        		//打开缴款字号的打印服务
        		open();
    		}
    		
    		try
    		{
            	new File(Print_file).delete();
    		}
    		catch(Exception ex)
    		{
    			ex.printStackTrace();
    		}
    		
    	}
    	
    }

    public Vector getPara()
    {
		Vector v = new Vector();
		String printlist = Language.apply("打印机,");
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null,null);
		for (int i=0;i<printServices.length;i++)
    	{
			System.out.println(printServices[i].getName());
			
			printlist +=","+printServices[i].getName();
    	}
    	
		v.add(printlist.split(","));
		v.add(new String[]{Language.apply("打印字体"),"宋体"});
		v.add(new String[]{Language.apply("字体大小"),"9"});
		v.add(new String[]{Language.apply("字体风格"),Language.apply("常规"),Language.apply("粗体"),Language.apply("斜体")});
    	v.add(new String[]{Language.apply("切纸走纸的行数"),"0"});
    	v.add(new String[]{Language.apply("每页打印的行数"),"0"});
    	
    	PageFormat pageFormat = PrinterJob.getPrinterJob().defaultPage();
    	
        System.out.println(pageFormat.getWidth() + "  " + pageFormat.getHeight() + "  " + 
                           pageFormat.getImageableWidth() + "  " + pageFormat.getImageableHeight() + "  " +
                           pageFormat.getImageableX() + "  " + pageFormat.getImageableY());
        System.out.println(pageFormat.getWidth() * 1.0/72.0 * MediaPrintableArea.INCH / MediaPrintableArea.MM);
        System.out.println(pageFormat.getHeight() * 1.0/72.0 * MediaPrintableArea.INCH / MediaPrintableArea.MM);
        System.out.println(pageFormat.getImageableWidth() * 1.0/72.0 * MediaPrintableArea.INCH / MediaPrintableArea.MM);
        System.out.println(pageFormat.getImageableHeight() * 1.0/72.0 * MediaPrintableArea.INCH / MediaPrintableArea.MM);
        System.out.println(pageFormat.getImageableX() * 1.0/72.0 * MediaPrintableArea.INCH / MediaPrintableArea.MM);
        System.out.println(pageFormat.getImageableY() * 1.0/72.0 * MediaPrintableArea.INCH / MediaPrintableArea.MM);
        
        int pagewidth = (int)Math.round(pageFormat.getWidth() * 1.0/72.0 * MediaPrintableArea.INCH / MediaPrintableArea.MM);
        int pageheight = (int)Math.round(pageFormat.getHeight() * 1.0/72.0 * MediaPrintableArea.INCH / MediaPrintableArea.MM);

        pageleft = (int)Math.round(pageFormat.getImageableX() * 1.0/72.0 * MediaPrintableArea.INCH / MediaPrintableArea.MM);
        pageright = pagewidth - pageleft - (int)Math.round(pageFormat.getImageableWidth() * 1.0/72.0 * MediaPrintableArea.INCH / MediaPrintableArea.MM);
        pagetop = (int)Math.round(pageFormat.getImageableY() * 1.0/72.0 * MediaPrintableArea.INCH / MediaPrintableArea.MM);
        pagebottom = pageheight - pagetop - (int)Math.round(pageFormat.getImageableHeight() * 1.0/72.0 * MediaPrintableArea.INCH / MediaPrintableArea.MM);
        
    	v.add(new String[]{Language.apply("页打印方向"),Language.apply("纵向"),Language.apply("横向")});    	
    	v.add(new String[]{Language.apply("页边距左(mm)"),String.valueOf(pageleft)});
    	v.add(new String[]{Language.apply("页边距右(mm)"),String.valueOf(pageright)});
    	v.add(new String[]{Language.apply("页边距上(mm)"),String.valueOf(pagetop)});
    	v.add(new String[]{Language.apply("页边距下(mm)"),String.valueOf(pagebottom)});
    	
        return v;
    }

    public String getDiscription()
    {
        return Language.apply("[中商百货]调用操作系统PRINT服务的打印机(分页)");
    }

    public void setEmptyMsg_Slip(String msg)
    {
    }
    
	public void enableRealPrintMode(boolean flag)
	{	
	}
	
    public void printFileToPrintService(String file,PrintService ps)
    {
    	// 读取文件
    	StringBuffer text = new StringBuffer();
    	BufferedReader br = CommonMethod.readFile(file);
    	if (br != null)
    	{
	    	String line = null;
	    	try
			{
				while ((line = br.readLine()) != null)
				{
					text.append(line + "\n");
				}
				br.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
    	}
    	
    	// 打印
    	JavaxPrintable prt = new JavaxPrintable();
    	prt.setPrintService(text.toString(),ps, rowByPage, printfont);
    	prt.printTextAction();
    	
    	// 删除打印文件
    	new File(file).delete();
    }
    
    class JavaxPrintable implements Printable
    {
    	String myprinttext = "";
    	PrintService myprintservice = null;
    	int myrowbypage = 0;
    	Font myprintfont = null;
    	int initascent = 16;
    	int PAGES = 0;
    	int ROWS = 0;
    	
    	public void setPrintService(String text,PrintService ps,int row,Font font)
    	{
    		myprinttext = text;
    		myprintservice = ps;
    		myrowbypage = row;
    		myprintfont = font;
    	}
    	
    	public void setPrintService(PrintService ps,int row,Font font)
    	{
    		//myprinttext = text;
    		myprintservice = ps;
    		myrowbypage = row;
    		myprintfont = font;
    	}
    	
        public int print(Graphics g, PageFormat pf, int page) throws PrinterException
    	{
            Graphics2D g2 = (Graphics2D)g;
            g2.setPaint(Color.black);
        	g2.setFont(myprintfont);
            
    	    if (page >= PAGES) return Printable.NO_SUCH_PAGE;
    	    
            g2.translate(pf.getImageableX(), pf.getImageableY());
    	    drawCurrentPageText(g2, pf, page);
    	    
            return Printable.PAGE_EXISTS;
    	}

        private void drawCurrentPageText(Graphics2D g2, PageFormat pf, int page)
    	{
            String s = getDrawText(myprinttext)[page];
            String drawText;
            float ascent = initascent;
            int k, i = g2.getFont().getSize(), lines = 0;
            while(s.length() > 0 && (myrowbypage <= 0 || (myrowbypage > 0 && lines < myrowbypage)))
            {
                k = s.indexOf('\n');
                if (k != -1)
                {
                    lines += 1;
                    drawText = s.substring(0, k);
                    g2.drawString(drawText, 0, ascent);
                    if (s.substring(k + 1).length() >= 0)
                    {
                        s = s.substring(k + 1);
                        ascent += i;
                    }
                }
                else
                {
                    lines += 1;
                    drawText = s;
                    g2.drawString(drawText, 0, ascent);
                    s = "";
                }
            }
    	}

        public String[] getDrawText(String s)
        {
            String[] drawText = new String[PAGES];
            for (int i = 0; i < PAGES; i++) drawText[i] = "";

            int k, suffix = 0, lines = 0;
            while(s.length() > 0)
            {
                if ((myrowbypage > 0 && lines < myrowbypage) || myrowbypage <= 0)
                {
                    k = s.indexOf('\n');
                    if (k != -1)
                    {
                        lines += 1;
                        drawText[suffix] = drawText[suffix] + s.substring(0, k + 1);
                        if (s.substring(k + 1).length() >= 0) s = s.substring(k + 1);
                    }
                    else
                    {
                        lines += 1;
                        drawText[suffix] = drawText[suffix] + s;
                        s = "";
                    }
                }
                else
                {
                    lines = 0;
                    suffix++;
                }
            }
            return drawText;
        }

        public int getPagesCount(String curStr)
    	{
            int page = 0;
            int position, count = 0;
            String str = curStr;
    	    while(str.length() > 0)
    	    {
    	        position = str.indexOf('\n');
                count += 1;
    	        if (position != -1)
                    str = str.substring(position + 1);
    	        else
    	            str = "";
    	    }
    	    if (count > 0)
    	    {
    	    	if (myrowbypage > 0) page = count / myrowbypage + 1;
    	    	else page = 1;
    	    }

    	    ROWS = count;
            return page;
    	}

        public void printTextAction()
        {
        	if (myprintservice == null) return;
            if (myprinttext == null || myprinttext.length() <= 0) return;
        
            try
            {
	            PAGES = getPagesCount(myprinttext);
/*	            
	            PrinterJob myPrtJob = PrinterJob.getPrinterJob();
	            myPrtJob.setPrintService(myprintservice);
	            PageFormat pageFormat = myPrtJob.defaultPage();
	            int height = 0;
	            if (myrowbypage <= 0 || (myrowbypage > 0 && myrowbypage > ROWS)) height = initascent + ROWS * myprintfont.getSize();
	            else height = initascent + myrowbypage * myprintfont.getSize();	            
	            java.awt.print.Paper p = new java.awt.print.Paper();
	            p.setImageableArea(0,0,pageFormat.getWidth(), pageFormat.getHeight());
	            p.setSize(pageFormat.getWidth(),height);
	            pageFormat.setPaper(p);
         
	            myPrtJob.setPrintable(this, pageFormat);
                myPrtJob.print();
*/	                                   
	            DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
	            PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
	            DocAttributeSet das = new HashDocAttributeSet();
	            
	            if (pagedirc != null) pras.add(pagedirc);
	            if (pageleft >= 0 && pageright >= 0 && pagetop >= 0 && pagebottom >= 0)
	            {
	            	PrinterJob prtjob = PrinterJob.getPrinterJob();
	            	prtjob.setPrintService(myprintservice);
	            	PageFormat pageFormat = prtjob.defaultPage();
	            	
	            	// 设置打印方向
	            	if (pagedirc != null) 
	            	{
	            		if (pagedirc.getValue() == OrientationRequested.LANDSCAPE.getValue()) pageFormat.setOrientation(PageFormat.LANDSCAPE);
	            		else pageFormat.setOrientation(PageFormat.PORTRAIT);
	            	}
	            	
	            	// 得到纸张大小(毫米)
	                int pagewidth = (int)Math.round(pageFormat.getWidth() * 1.0/72.0 * MediaPrintableArea.INCH / MediaPrintableArea.MM);
	                int pageheight = (int)Math.round(pageFormat.getHeight() * 1.0/72.0 * MediaPrintableArea.INCH / MediaPrintableArea.MM);
	            	
	                // 设置打印区域
                	pras.add(new MediaPrintableArea(pageleft,pagetop,pagewidth-pageleft-pageright,pageheight-pagetop-pagebottom,MediaPrintableArea.MM));
	            }
	            
	            DocPrintJob job = myprintservice.createPrintJob();	            
	            Doc doc = new SimpleDoc(this, flavor, das);
	            job.print(doc, pras);            
            }
            catch(Exception pe)
            {
                pe.printStackTrace();
                
                new MessageBox(Language.apply("调用系统打印服务异常\n\n")+pe.getMessage());
            }
        }
   
        
        public void printTextAction(String printtext)
        {
        	myprinttext = printtext;
        	
        	if (myprintservice == null) return;
            if (myprinttext == null || myprinttext.length() <= 0) return;
        
            try
            {
	            PAGES = getPagesCount(myprinttext);
/*	            
	            PrinterJob myPrtJob = PrinterJob.getPrinterJob();
	            myPrtJob.setPrintService(myprintservice);
	            PageFormat pageFormat = myPrtJob.defaultPage();
	            int height = 0;
	            if (myrowbypage <= 0 || (myrowbypage > 0 && myrowbypage > ROWS)) height = initascent + ROWS * myprintfont.getSize();
	            else height = initascent + myrowbypage * myprintfont.getSize();	            
	            java.awt.print.Paper p = new java.awt.print.Paper();
	            p.setImageableArea(0,0,pageFormat.getWidth(), pageFormat.getHeight());
	            p.setSize(pageFormat.getWidth(),height);
	            pageFormat.setPaper(p);
         
	            myPrtJob.setPrintable(this, pageFormat);
                myPrtJob.print();
*/	                                   
	            DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
	            PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
	            DocAttributeSet das = new HashDocAttributeSet();
	            
	            if (pagedirc != null) pras.add(pagedirc);
	            if (pageleft >= 0 && pageright >= 0 && pagetop >= 0 && pagebottom >= 0)
	            {
	            	PrinterJob prtjob = PrinterJob.getPrinterJob();
	            	prtjob.setPrintService(myprintservice);
	            	PageFormat pageFormat = prtjob.defaultPage();
	            	
	            	// 设置打印方向
	            	if (pagedirc != null) 
	            	{
	            		if (pagedirc.getValue() == OrientationRequested.LANDSCAPE.getValue()) pageFormat.setOrientation(PageFormat.LANDSCAPE);
	            		else pageFormat.setOrientation(PageFormat.PORTRAIT);
	            	}
	            	
	            	// 得到纸张大小(毫米)
	                int pagewidth = (int)Math.round(pageFormat.getWidth() * 1.0/72.0 * MediaPrintableArea.INCH / MediaPrintableArea.MM);
	                int pageheight = (int)Math.round(pageFormat.getHeight() * 1.0/72.0 * MediaPrintableArea.INCH / MediaPrintableArea.MM);
	            	
	                // 设置打印区域
                	pras.add(new MediaPrintableArea(pageleft,pagetop,pagewidth-pageleft-pageright,pageheight-pagetop-pagebottom,MediaPrintableArea.MM));
	            }
	            
	            DocPrintJob job = myprintservice.createPrintJob();	            
	            Doc doc = new SimpleDoc(this, flavor, das);
	            job.print(doc, pras);            
            }
            catch(Exception pe)
            {
                pe.printStackTrace();
                
                new MessageBox(Language.apply("[中商百货]调用系统打印服务异常\n\n")+pe.getMessage());
            }
        }
    }

}
