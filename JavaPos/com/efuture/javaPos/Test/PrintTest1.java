package com.efuture.javaPos.Test;


public class PrintTest1
{
	
}
/*
public class PrintTest1 implements Printable{ 
int m_wPage;
int m_hPage;
int m_orientation;
Printable m_target;
int maxNumPage=1;
String title="数据表格打印";
Font titleFont=new Font("黑体",Font.BOLD,14);
boolean hasTail=true;
int tailAlign=0;
int headAlign=0;
int topSpace=0;
int leftSpace=0;
int yStart=0;
int yEnd=0;
int xStart=topSpace;
int xEnd=0;
int x=0,y=0;
String strTemp="打印内容";
public void doPrint(){ try{ m_orientation=PageFormat.PORTRAIT;
//设置打印对象，默认纸张 PrinterJob prnJob=PrinterJob.getPrinterJob();
PageFormat pageFormat=prnJob.defaultPage();
pageFormat.setOrientation(m_orientation);
m_wPage=(int)(pageFormat.getWidth());
m_hPage=(int)(pageFormat.getHeight());
//将待打印的窗体根据默认纸张设置传入打印对象 prnJob.setPrintable(this,pageFormat);
if(!prnJob.printDialog()) return;
prnJob.print();
}catch(PrinterException ex){ ex.printStackTrace();
System.err.println("打印错误："+ex.toString());
} } // 初始化打印参数 
public void initPrintParameter() { }
 // 构造打印内容，以送打印机打印  
public int print(Graphics pg,PageFormat pageFormat, int pageIndex) throws PrinterException{ //初始化打印参数 initPrintParameter();
//将画布设置为页面大小 pg.translate((int)pageFormat.getImageableX(), (int)pageFormat.getImageableY());
int wPage=0;
int hPage=0;
//根据打印机页面设置调整画布大小 if(pageFormat.getOrientation()==pageFormat.PORTRAIT){ wPage=(int)pageFormat.getImageableWidth();
hPage=(int)pageFormat.getImageableHeight();
} else{ wPage=(int)pageFormat.getImageableWidth();
wPage+=wPage/2;
hPage=(int)pageFormat.getImageableHeight();
pg.setClip(0,0,wPage,hPage);
} wPage=wPage-2*leftSpace;
hPage=hPage-2*topSpace;
xStart=leftSpace;
xEnd=wPage-2;
//为画布设置颜色和字体 int y=topSpace;
pg.setFont(titleFont);
pg.setColor(Color.black);
//画标题，并使其居中 Font fn=pg.getFont();
FontMetrics fm=pg.getFontMetrics();
y+=fm.getAscent();
alignText(title,pg,y,xStart,xEnd,headAlign);
y+=30;
x=leftSpace+2;
Font headerFont=new Font("宋体",Font.BOLD,14);
pg.setFont(headerFont);
fm=pg.getFontMetrics();
int h=fm.getAscent();
yStart=y-1;
y+=h;
pg.setFont(headerFont);
fm=pg.getFontMetrics();
int header=y;
h=fm.getHeight();
//计算行高，每页行数，总行数和指定页码的起始行、结束行 int rowH=Math.max(h,10);
int tailH=rowH+30;
int rowPerPage=0;
int leftPix=0;
if(hasTail){ rowPerPage=(hPage-header-tailH)/rowH;
leftPix=(hPage-header-tailH)%rowH;
yEnd=hPage-leftPix-tailH+2;
} else{ rowPerPage=(hPage-header)/rowH;
leftPix=(hPage-header)%rowH;
yEnd=hPage-leftPix+2;
} pg.drawString(strTemp,x,y);
//画表格边框 pg.drawLine(xStart,yStart,xStart,yEnd);
pg.drawLine(xStart,yStart,xEnd,yStart);
pg.drawLine(xEnd,yStart,xEnd,yEnd);
pg.drawLine(xStart,yEnd,xEnd,yEnd);
//打印页码 if(hasTail){ int pageNumber=pageIndex+1;
String s="第"+pageNumber+"页";
alignText(s,pg,yEnd+30,xStart,xEnd,tailAlign);
} System.gc();
return PAGE_EXISTS;
} // 文字排列，坐标在y处,显示范围（start-end） * 0表示居中显示，1表示左对齐，2表示右对齐
private void alignText(String s,Graphics pg,int y,int start, int end,int mode){ Font fn=pg.getFont();
FontMetrics fm=pg.getFontMetrics();
int wString=fm.stringWidth(s);
int x=start;
switch(mode) { case 0: if((end-start-wString)>0) x=start+(end-start-wString)/2;
break;
case 1: break;
case 2: if((end-start-wString)>0) x=start+(end-start-wString);
break;
} pg.drawString(s,x,y);
} public static void main(String[] args){ Print p=new Print();
p.doPrint();
}
} 
*/
