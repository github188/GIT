package device.Printer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Vector;

import javax.imageio.ImageIO;
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
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;

import org.jbarcode.JBarcode;
import org.jbarcode.encode.EAN13Encoder;
import org.jbarcode.encode.InvalidAtributeException;
import org.jbarcode.encode.UPCAEncoder;
import org.jbarcode.paint.EAN13TextPainter;
import org.jbarcode.paint.UPCATextPainter;
import org.jbarcode.paint.WidthCodedPainter;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.Language;
import com.swetake.util.Qrcode;


public class JavaxPrintService_Printer implements Interface_Printer
{
	PrintService printservice = null;
	Font printfont = null;
	int cutLine = 0;
	int rowByPage = 0;
	int pageleft = -1,pageright = -1,pagetop = -1,pagebottom = -1;
	OrientationRequested pagedirc = null;
	
	String bgname = null;
	float fs = 1f;
	
	String Print_file = "Print_Service_File.ini";
	
	String barcode_flg_EN13 = "#barcode_EN13:";
	String barcode_flg_UPCA = "#barcode_UPCA:";
	String head_log = "#log:";
	//二维码
	String Qrcode = "#Qrcode:";
	int version = 4;
	int size = 3;
	
    public boolean open()
    {
        if (DeviceName.devicePrinter.length() <= 0)
        {
            return false;
        }

        try
        {
	        String[] arg = DeviceName.devicePrinter.split(",");
	        String isAutoReadDefaultPrint = null;
        	if ((arg.length > 13) && (arg[13].length() > 0))
	        {
        		isAutoReadDefaultPrint = arg[13].trim();	        	
	        }
        	
        	if (isAutoReadDefaultPrint != null && isAutoReadDefaultPrint.equalsIgnoreCase("Y"))
        	{
        		printservice = PrintServiceLookup.lookupDefaultPrintService();//定位默认的打印服务
        		PosLog.getLog(getClass()).info("自动读取默认打印机[" + printservice.getName() + "].");
        	}
        	else
        	{
    	        // 得到所有打印机列表
    			PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null,null);
    			
    	        
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
	        
	        if ((arg.length > 11) && (arg[11].length() > 0))
	        {
	        	bgname =arg[11];
	        }
	        
	        if ((arg.length > 12) && (arg[12].length() > 0))
	        {	        	
	        	fs = Float.parseFloat(arg[12]);
	        }
	        if ((arg.length > 14) && (arg[14].length() > 0))
	        {
	        	version = Integer.parseInt(arg[14].trim());
	        }
	        
	        if ((arg.length > 15) && (arg[15].length() > 0))
	        {
	        	size = Integer.parseInt(arg[15].trim());
	        }
	        
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
    	// 删除已存在的打印文件
    	new File(Print_file).delete();
    }

    public void printLine_Normal(String printStr)
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
    		new File(Print_file).delete();
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
    	if (cutLine > 0)
    	{
	    	for (int i = 0;i < cutLine;i++)
	    	{
	    		printLine_Normal("\n");
	    	}
	    	
	    	// 必须打印内容否则空行不打印
	    	printLine_Normal("cut");
    	}
    	
    	if (printservice != null) printFileToPrintService(Print_file,printservice);
    }
    
    public void cutPaper_Journal()
    {
    	cutPaper_Normal();
    }

    public void cutPaper_Slip()
    {	
    	cutPaper_Normal();
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
    	v.add(new String[]{Language.apply("背景图片")});
    	v.add(new String[]{Language.apply("打印深度")});
    	v.add(new String[]{Language.apply("是否自动读取默认打印机"),"Y","N"});
    	v.add(new String[]{Language.apply("二维码版本号([1-40]){版本号-可容纳字符数:1-14,2-26,3-42,4-62,5-84}"),"2"});
    	v.add(new String[]{Language.apply("二维码码元大小"),"3"});
    	
        return v;
    }

    public String getDiscription()
    {
        return Language.apply("调用操作系统PRINT服务的打印机");
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
    	
    	
    	
        public int print(Graphics g, PageFormat pf, int page) throws PrinterException
    	{
            Graphics2D g2 = (Graphics2D)g;
           
			try
			{
				File _filebiao = new File(ConfigClass.BackImagePath+"\\"+bgname);
	            if (bgname != null && _filebiao.exists())
	            {
			        BufferedImage src_biao;
				
					src_biao = ImageIO.read(_filebiao);
		            int wideth_biao = src_biao.getWidth(null);
		            int height_biao = src_biao.getHeight(null);
		            ((Graphics2D) g2).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
		               fs));          
		            g2.drawImage(src_biao, (int)((1) / 2),
		                        (int)((1) / 2), wideth_biao, height_biao, null);
	            }
            
            g2.setPaint(Color.black);
        	g2.setFont(myprintfont);
            
    	    if (page >= PAGES) return Printable.NO_SUCH_PAGE;
    	    
            g2.translate(pf.getImageableX(), pf.getImageableY());
    	    drawCurrentPageText(g2, pf, page);
    	    
			}catch (IOException e)
			{
				e.printStackTrace();
			}
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
            	boolean done = false;
            	try{
            		
	                k = s.indexOf('\n');
	               
	                if (k != -1)
	                {
	                    lines += 1;
	                    
	                    drawText = s.substring(0, k);
	                	                    
	                	if (printfont != null && drawText.indexOf("Big&") >=0)
	                	{
	                		Font printfont1 = new Font(printfont.getName(),Font.BOLD,printfont.getSize());
	                		g2.setFont(printfont1);
	                		i = g2.getFont().getSize();
	                		drawText = drawText.substring(4);
	                		done = true;
	                	}
	                	
	                	if (drawText.indexOf(barcode_flg_EN13)>=0)
	                	{
	                		String code = drawText.substring(drawText.indexOf(barcode_flg_EN13)+barcode_flg_EN13.length());
	                		if (code.length() > 12) code = code.substring(0,12);
	                		else if (code.length() < 12)
	                		{
	                			code = Convert.increaseCharForward(code,'0', 11);
	                			code = 2+ code;
	                		}
	                		
	                        JBarcode jbcode = new JBarcode(EAN13Encoder.getInstance(), WidthCodedPainter.getInstance(), EAN13TextPainter.getInstance());
	                        BufferedImage img;
	    					try
	    					{
	    						img = jbcode.createBarcode(code);
	    						g2.drawImage(img, (int)((1) / 2),
	    						             (int)ascent, img.getWidth(), img.getHeight(), null);
	    						
	                            if (s.substring(k + 1).length() >= 0)
	                            {
	                                s = s.substring(k + 1);
	                                ascent += img.getHeight()+i;
	                            }
	    					}
	    					catch (InvalidAtributeException e)
	    					{
	    						// TODO Auto-generated catch block
	    						e.printStackTrace();
	    						new MessageBox(Language.apply("打印条码可能不为12位,请检查条码"));
	    						break;
	    					}
	    					
	
	                        continue;
	                	}
	                	else if (drawText.indexOf(barcode_flg_UPCA)>=0)
	                	{
	                		
	                		String code = drawText.substring(drawText.indexOf(barcode_flg_UPCA)+barcode_flg_UPCA.length());
	/*                		if (code.length() > 12) code = code.substring(0,12);
	                		else if (code.length() < 12)
	                		{
	                			code = Convert.increaseCharForward(code,'0', 11);
	                			code = 2+ code;
	                		}*/
	                		
	                        JBarcode jbcode = new JBarcode(UPCAEncoder.getInstance(), WidthCodedPainter.getInstance(), UPCATextPainter.getInstance());
	                        BufferedImage img;
	    					try
	    					{
	    						img = jbcode.createBarcode(code);
	    						g2.drawImage(img, (int)((1) / 2),
	    						             (int)ascent, (int)(img.getWidth()*1.5), img.getHeight(), null);
	    						
	                            if (s.substring(k + 1).length() >= 0)
	                            {
	                                s = s.substring(k + 1);
	                                ascent += img.getHeight()+i;
	                            }
	    					}
	    					catch (InvalidAtributeException e)
	    					{
	    						// TODO Auto-generated catch block
	    						e.printStackTrace();
	    						new MessageBox("打印条码可能不为12位,请检查条码");
	    						break;
	    					}
	    					
	
	                        continue;
	                	}
                        //	检查是否是打印二维码
	                	else if (drawText.indexOf(Qrcode) >= 0)
	                	{	              	                      		
	                		String content = drawText.substring(drawText.indexOf(Qrcode)+Qrcode.length());
	                		BufferedImage img = null;
	                		img = qRCodeCommon(content,version,size);	            
	                		
	    					g2.drawImage(img, (int)((1) / 2),(int)ascent, img.getWidth(), img.getHeight(), null);
	                		
                            if (s.substring(k + 1).length() >= 0)
                            {
                                s = s.substring(k + 1);
                                ascent += img.getHeight()+i;
                            }
	                		continue;
	                	}
	                	else if (drawText.indexOf(head_log)>=0)
	                	{
	                		String pathfile = drawText.substring(drawText.indexOf(head_log)+head_log.length());
	                		
	        				File _filebiao = new File(ConfigClass.BackImagePath+"\\"+pathfile);
	        	            if (head_log != null && _filebiao.exists())
	        	            {
	        			        BufferedImage src_biao;
	        				
	        					src_biao = ImageIO.read(_filebiao);
	        		            //int wideth_biao = src_biao.getWidth(null);
	        		            //int height_biao = src_biao.getHeight(null);
	        		            g2.drawImage(src_biao, (int)((1) / 2),
	    						             (int)ascent, src_biao.getWidth(), src_biao.getHeight(), null);
	    						
	                            if (s.substring(k + 1).length() >= 0)
	                            {
	                                s = s.substring(k + 1);
	                                ascent += src_biao.getHeight()+i;
	                            }
	        	            }
	                        continue;
	                	}
	                	//#@#客户化字体大小（9、12、16）|字体风格（常规、粗体、斜体）|字体（宋体、黑体）#@#printText
	                	if (printfont != null && drawText.indexOf("#@#") >=0)
	                	{
	                		String[] arr = drawText.split("#@#");
	                		if (arr.length>=3)
	                		{
	                			String strPrint = arr[2];
	                			arr = arr[1].split("\\|");
	                			if(arr.length>=2)
	                			{
	                				int fontsizeEx=Convert.toInt(arr[0].trim());
	                				int fontstyleEx=Convert.toInt(arr[1].trim());
	                				String fontnameEx=printfont.getName();
	                				if(arr.length>=3) fontnameEx=arr[2].trim();
	                				
		                			Font printfontEx = new Font(fontnameEx, fontstyleEx, fontsizeEx);
			                		g2.setFont(printfontEx);
			                		i = g2.getFont().getSize();
			                		drawText = strPrint;
			                		done = true;
	                			}
	                		}	                		
	                	}
	                	
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
	                    
	                	if (printfont != null && drawText.indexOf("Big&") >=0)
	                	{
	                		Font printfont1 = new Font(printfont.getName(),Font.BOLD,printfont.getSize());
	                		g2.setFont(printfont1);
	                		i = g2.getFont().getSize();
	                		drawText = drawText.substring(4);
	                		done = true;
	                	}
	                	
	                   	if (drawText.indexOf(barcode_flg_EN13)>=0)
	                	{
	                   		String code = drawText.substring(drawText.indexOf(barcode_flg_EN13)+barcode_flg_EN13.length());
	                   		
	                		if (code.length() > 12) code = code.substring(0,12);
	                		else if (code.length() < 12)
	                		{
	                			code = Convert.increaseCharForward(code,'0', 11);
	                			code = 2+ code;
	                		}
	                		
	                        JBarcode jbcode = new JBarcode(EAN13Encoder.getInstance(), WidthCodedPainter.getInstance(), EAN13TextPainter.getInstance());
	                        BufferedImage img;
	    					try
	    					{
	    						img = jbcode.createBarcode(code);
	    						g2.drawImage(img, (int)((1) / 2),
	    						             (int)ascent, img.getWidth(), img.getHeight(), null);
	    					}
	    					catch (InvalidAtributeException e)
	    					{
	    						// TODO Auto-generated catch b lock
	    						e.printStackTrace();
	    						new MessageBox(Language.apply("打印条码可能不为12位,请检查条码"));
	    						break;
	    					}
	    					
	    					s = "";
	                        continue;
	                	}
	                   	if (drawText.indexOf(barcode_flg_UPCA)>=0)
	                	{
	                   		String code = drawText.substring(drawText.indexOf(barcode_flg_UPCA)+barcode_flg_UPCA.length());
	                   		
/*	                		if (code.length() > 12) code = code.substring(0,12);
	                		else if (code.length() < 12)
	                		{
	                			code = Convert.increaseCharForward(code,'0', 11);
	                			code = 2+ code;
	                		}*/
	                		
	                        JBarcode jbcode = new JBarcode(UPCAEncoder.getInstance(), WidthCodedPainter.getInstance(), UPCATextPainter.getInstance());
	                        BufferedImage img;
	    					try
	    					{
	    						img = jbcode.createBarcode(code);
	    						g2.drawImage(img, (int)((1) / 2),
	    						             (int)ascent, img.getWidth(), img.getHeight(), null);
	    					}
	    					catch (InvalidAtributeException e)
	    					{
	    						// TODO Auto-generated catch b lock
	    						e.printStackTrace();
	    						new MessageBox("打印条码可能不为12位,请检查条码");
	    						break;
	    					}
	    					
	    					s = "";
	                        continue;
	                	}
                        //	检查是否是打印二维码
	                	else if (drawText.indexOf(Qrcode) >= 0)
	                	{	             
	                		
	                		String content = drawText.substring(drawText.indexOf(Qrcode)+Qrcode.length());
	                		BufferedImage img = null;
	                		img = qRCodeCommon(content,version,size);	             
	                		
	    					g2.drawImage(img, (int)((1) / 2),(int)ascent, img.getWidth(), img.getHeight(), null);
	                		
	                		continue;
	                	}
	                   	else if (drawText.indexOf(head_log)>=0)
	                	{
	                		String pathfile = drawText.substring(drawText.indexOf(head_log)+head_log.length());
	                		
	        				File _filebiao = new File(ConfigClass.BackImagePath+"\\"+pathfile);
	        	            if (head_log != null && _filebiao.exists())
	        	            {
	        			        BufferedImage src_biao;
	        				
	        					src_biao = ImageIO.read(_filebiao);
	        		            //int wideth_biao = src_biao.getWidth(null);
	        		            //int height_biao = src_biao.getHeight(null);
	        		            g2.drawImage(src_biao, (int)((1) / 2),
	    						             (int)ascent, src_biao.getWidth(), src_biao.getHeight(), null);
	    						
	                            if (s.substring(k + 1).length() >= 0)
	                            {
	                                s = s.substring(k + 1);
	                                ascent += src_biao.getHeight()+i;
	                            }
	        	            }
	                        continue;
	                	}
	                   	
	                    g2.drawString(drawText, 0, ascent);
	                    s = "";
	                }
            	}catch(Exception er)
            	{
            		er.printStackTrace();
            	}
            	finally
            	{
            		if (done)
            		{
            			g2.setFont(printfont);
            			i = g2.getFont().getSize();
            			done = false;
            		}
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
	            //p.setSize(pageFormat.getWidth(),pageFormat.getHeight());
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
    }

    //二维码的生成
    private BufferedImage qRCodeCommon(String content,int version,int size)
    {
		BufferedImage bufImg = null;
		int imgSize = 0;
		try {
			Qrcode qrcodeHandler = new Qrcode();
			// 设置二维码排错率，可选L(7%)、M(15%)、Q(25%)、H(30%)，排错率越高可存储的信息越少，但对二维码清晰度的要求越小
			qrcodeHandler.setQrcodeErrorCorrect('M');
			qrcodeHandler.setQrcodeEncodeMode('B');
			// 设置设置二维码尺寸，取值范围1-40，值越大尺寸越大，可存储的信息越大
			qrcodeHandler.setQrcodeVersion(version);
			// 获得内容的字节数组，设置编码格式
			byte[] contentBytes = content.getBytes("utf-8");
			// 图片尺寸
			//imgSize = 67 + 12 * (size - 1);
			imgSize = (23 + 4*(version-1))*size;
			bufImg = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_RGB);
			Graphics2D gs = bufImg.createGraphics();
			// 设置背景颜色
			gs.setBackground(Color.WHITE);
			gs.clearRect(0, 0, imgSize, imgSize);

			// 设定图像颜色> BLACK
			gs.setColor(Color.BLACK);
			// 设置偏移量，不设置可能导致解析出错
			int pixoff = 2;
			// 输出内容> 二维码
			if (contentBytes.length > 0 && contentBytes.length < 800) {
				boolean[][] codeOut = qrcodeHandler.calQrcode(contentBytes);
				for (int i = 0; i < codeOut.length; i++) {
					for (int j = 0; j < codeOut.length; j++) {
						if (codeOut[j][i]) {
							gs.fillRect(j * size + pixoff, i * size + pixoff, size, size);
						}
					}
				}
			} else {
				 new MessageBox("QRCode content bytes length = " + contentBytes.length + " not in [0, 800].");
			}
			
			gs.dispose();
			bufImg.flush();
		} catch (Exception e) {
			e.printStackTrace();
			new MessageBox("打印二维码出现异常：" + e.getMessage());
		}	 
		
		return bufImg;
	}
    
    public BufferedImage zoom(BufferedImage bitmap, int width, int height){
        if(bitmap==null){
            return null;
        }
        if(width<1||height<1){
            return null;
        }
        float oldWidth=bitmap.getWidth(null);
        float oldHeight=bitmap.getHeight(null);
        float xRatio=oldWidth/width;
        float yRatio=oldHeight/height;
        
        BufferedImage result=new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int x=0,y=0;
        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                x=(int)(i*xRatio);
                if(x>oldWidth){
                    x=(int)oldWidth;
                }
                y=(int)(j*yRatio);
                if(y>oldHeight){
                    y=(int)oldHeight;
                }
                result.setRGB(i, j, bitmap.getRGB(x, y));
            }
        }
        return result;
    }
}
