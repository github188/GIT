/*
 *POS Version 41 Product
 *融通系统集成有限公司
 *@author HuangXuean
 *@version 1.0
 */
package com.royalstone.pos.ticket;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.royalstone.pos.common.PosContext;
import com.royalstone.pos.hardware.ConsolePrinter;
import com.royalstone.pos.hardware.IPrinter;
import com.royalstone.pos.util.Formatter;
import com.royalstone.pos.util.PosConfig;

/**
 * 记帐单打印模板的解析器
 * @author HuangXuean
 */
public class PosInvoice {

	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int CENTER = 2;

	private int ticketWidth = 0;
	private int leftMargin = 0;
	private int rightMargin = 0;

	private HashMap params = null;
	private String seperator = "";
	private Element root = null;
	private IPrinter printer;

	/**
	 * @param filename 记帐单打印模板的文件名
	 * @param printer  代表打印机
	 * @throws JDOMException
	 * @throws FileNotFoundException
	 */
	public PosInvoice(String filename, IPrinter printer)
		throws JDOMException, FileNotFoundException {

		Document doc = (new SAXBuilder()).build(new FileInputStream(filename));
		root = doc.getRootElement();
		String strWidth = root.getAttributeValue("width");
		String strLeftMargin = root.getAttributeValue("leftMargin");
		String strRightMargin = root.getAttributeValue("rightMargin");

		leftMargin = Integer.parseInt(strLeftMargin);
		rightMargin = Integer.parseInt(strRightMargin);
		ticketWidth = Integer.parseInt(strWidth) - leftMargin - rightMargin;
		seperator = root.getAttributeValue("seperator");

		this.printer = printer;

	}

	public void setParams(HashMap params) {
		this.params = params;
	}

	/**
	 * 解析记帐单头
	 * @param params 记帐单打印模板文件中各变量的值（变量在文件中的格式为:${变量名}）
	 */
	public void parseHeader(HashMap params) {
		
		printTrainingFlag();

		this.params = params;
		Element table = root.getChild("Header").getChild("table");
		parseTable(table);

		table = root.getChild("Info").getChild("table");
		parseTable(table);

		printer.println(margin(format("", seperator, ticketWidth, LEFT)));

		table = root.getChild("Content").getChild("Title").getChild("table");
		parseTable(table);
		
		printer.println(margin(format("", seperator, ticketWidth, LEFT)));
	}


	/**
	 * 解析记帐单的销售信息
	 * @param params 记帐单打印模板文件中各变量的值
	 */
	public void parseSale(HashMap params) {

		this.params = params;
		Element table =
			root.getChild("Content").getChild("Sale").getChild("table");
		parseTable(table);
		
	}

	/**
	 * 解析记帐单的折扣信息
	 * @param params 记帐单打印模板文件中各变量的值
	 */
	public void parseDiscount(HashMap params) {

		this.params = params;
		Element table =
			root.getChild("Content").getChild("Discount").getChild("table");
		parseTable(table);

	}
	
	/**
	 * 解析记帐单的总额信息
	 * @param params 记帐单打印模板文件中各变量的值
	 */
	public void parseTotal(HashMap params) {

		this.params = params;
		Element table =
			root.getChild("Content").getChild("Total").getChild("table");
		printer.println(margin(format("", seperator, ticketWidth/8, RIGHT)));
		parseTable(table);
		printer.println(margin(format("", seperator, ticketWidth/8, LEFT)));

	}

	/**
	 * 解析记帐单的支付信息
	 * @param params 记帐单打印模板文件中各变量的值
	 */
	public void parsePayment(HashMap params) {

		this.params = params;
		Element table =
			root.getChild("Content").getChild("Payment").getChild("table");

		parseTable(table);
		
		printTrainingFlagInRandom();

	}

	/**
	 * 解析记帐单的票尾信息
	 * @param params 记帐单打印模板文件中各变量的值
	 */
	public void parseTrail(HashMap params) {

		this.params = params;
		Element table =
			root.getChild("Content").getChild("Total").getChild("table");

		parseTable(table);

		printer.println(
			margin(
				format(
					Formatter.getTime(new Date()),
					seperator,
					ticketWidth,
					CENTER)));

		table = root.getChild("Trail").getChild("table");

		parseTable(table);
		
		//printTrainingFlag();

		printer.feed(getFeedLines());
		printer.cut();
	}
	
	/**
	 * 打印页尾
	 */
	public void parseCommnet(HashMap params){
		this.params = params;	
					
		printer.println(
			margin(
				format(
					Formatter.getTime(new Date()),
					seperator,
					ticketWidth,
					CENTER)));
		Element table = root.getChild("Trail").getChild("table");
		parseTable(table);				
		printer.feed(getFeedLines());
	}
	
	/**
	 * @return 返回后台设置的切纸前的打印行数
	 */
	public int getFeedLines() {
		int feedLines = 2;
		String strFeedLines = PosConfig.getInstance().getString("TAILLINES");
		if (strFeedLines != null) {
			try {
				feedLines = Integer.parseInt(strFeedLines);
				if (feedLines > 10 || feedLines < 2)
					feedLines = 2;
			} catch (NumberFormatException ex) {
				ex.printStackTrace();
				feedLines = 2;
			}
		}
		return feedLines;
	}

	/**
	 * 切纸 
	 */
	public void cutPaper(){
		printer.cut();
	}

	public void parseButtom(HashMap params) {

		this.params = params;
		printer.println(
			margin(
				format(
					Formatter.getTime(new Date()),
					seperator,
					ticketWidth,
					CENTER)));

		Element table = root.getChild("Trail").getChild("table");

		parseTable(table);

		printer.feed(getFeedLines());
		printer.cut();

	}

	/**
	 * 测试方法
	 * @deprecated
	 */
	public void parseTicket() {

		parseHeader(null);
		parseSale(null);
		parseDiscount(null);
		parsePayment(null);
		parseTrail(null);

	}

	/**
	 * 解析表格，记帐单模板用类似Html的Table来定义记帐单的格式	 
	 * @param table
	 */
	private void parseTable(Element table) {

		if (table == null)
			return;

		String strColumnCount = table.getAttributeValue("columns");
		int columnCount = Integer.parseInt(strColumnCount);
		int columnsWidth = ticketWidth / columnCount;
		List rows = table.getChildren("row");
		Iterator rowIter = rows.iterator();

		while (rowIter.hasNext()) {
			Element row = (Element) rowIter.next();
			String visible=row.getAttributeValue("visible");
			if(visible!=null && parseContent(visible).equals("false")){
				return;
			}
			List columns = row.getChildren("column");
			Iterator columnIter = columns.iterator();
			StringBuffer line = new StringBuffer();
			int printColumnCount = 0;
			while (columnIter.hasNext()) {
				Element column = (Element) columnIter.next();
				String strSpan = column.getAttributeValue("span");
				String strAlign = column.getAttributeValue("align");
				int align = LEFT;
				if (strAlign == null
					|| strAlign.equals("")
					|| strAlign.equals("left")) {
					align = LEFT;
				} else if (strAlign.equals("center")) {
					align = CENTER;
				} else if (strAlign.equals("right")) {
					align = RIGHT;
				}
				int span = 1;
				if (strSpan != null && !strSpan.equals("")) {
					span = Integer.parseInt(strSpan);
				}
				printColumnCount += span;
				if (printColumnCount <= columnCount) {
					String content = parseContent(column.getText().trim());
					line.append(
						format(content, " ", span * columnsWidth, align));
				}
			}
			if((line.toString().trim().length())>0){
				printer.println(margin(line.toString()));	
			}
		}

	}

	/**
	 * 解析内容，会用传进来的各变量的值替换变量
	 * @param content 
	 * @return
	 */
	private String parseContent(String content) {
		String result = content;
		if (params != null && params.size() > 0) {
			Set keySet = params.keySet();
			Iterator iter = keySet.iterator();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				String value = (String) params.get(key);
				if (value == null)
					value = "";
				if (value != null) {
					result = result.replaceAll("\\Q" + key + "\\E", value);
				}
			}
		}
		return result;
	}

	/**
	 * 按固定长度和对齐方式用给定字符连接特定的字符串
	 * @param s 
	 * @param fill 
	 * @param width
	 * @param justify
	 * @return
	 */
	private String format(String s, String fill, int width, int justify) {

		int i = 0;
		if ((s.getBytes()).length > width) {
			if ((s.getBytes()).length > s.length()) {
				StringBuffer buf = new StringBuffer();
				int count = 0;
				for (int j = 0; j < s.length() - 1; j++) {
					String str = s.substring(j, j + 1);
					if (str.getBytes().length > 1) {
						count += 2;
					} else {
						count++;
					}
					if (count <= width) {
						buf.append(str);
					}
				}
				s = buf.toString();
			} else {
				s = s.substring(0, width - 1);
			}
		}

		StringBuffer tmp = new StringBuffer();

		switch (justify) {

			case LEFT :
				tmp.append(s);
				for (i = 0; i < width - (s.getBytes()).length; i++)
					tmp.append(fill);
				break;
			case RIGHT :
				for (i = 0; i < width - (s.getBytes()).length - 1; i++)
					tmp.append(fill);
				tmp.append(s);
				tmp.append(" ");
				break;

			case CENTER :
				int left = (width - (s.getBytes()).length) / 2;
				int right = left;

				if (((width - (s.getBytes()).length) % 2) > 0)
					right++;

				for (i = 0; i < left; i++)
					tmp.append(fill);
				tmp.append(s);
				for (i = 0; i < right; i++)
					tmp.append(fill);
				break;
			default :
				tmp.append(s);
				for (i = 0; i < width - (s.getBytes()).length; i++)
					tmp.append(fill);
				break;
		}
		return tmp.toString();
	}

	/**
	 * 在字符串左右加空格
	 * @param s 要处理的字符串
	 * @return 处理后的字符串
	 */
	private String margin(String s) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < leftMargin; i++) {
			buf.append(" ");
		}
		buf.append(s);
		for (int i = 0; i < rightMargin; i++) {
			buf.append(" ");
		}
		return buf.toString();
	}

	/**
	 * 在打印机上打印字符串加换行，字符串不够一行就在左右加分割符
	 * @param s 要打印的字符串
	 */
	public void println(String s) {
		printer.println(margin(format(s, seperator, ticketWidth, CENTER)));
	}
	
	/**
	 * 在打印机上打印字符串加换行，字符串不够一行不加分割符
	 * @param s 要打印的字符串
	 */
	public void printlnWithoutSeperator(String s) {
		printer.println(margin(format(s, "", ticketWidth, LEFT)));
	}
	
	/**
	 * 打印培训标记 
	 */
	public void printTrainingFlag(){
		if(PosContext.getInstance().isTraining()){
			printer.println(margin(format("培训记帐单", "-", ticketWidth, CENTER)));
		}
	}
	
	/**
	 * 根据生成的随机数是否在(1,2,5,7)内来决定是否打印培训标记 
	 */
	public void printTrainingFlagInRandom(){
		long randomNum=Math.round(Math.random()*10);
		if(randomNum==2 || randomNum==5 || randomNum==7 || randomNum==1){
			printTrainingFlag();
		}
	}


	/**
	 * 主方法，测试用
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		HashMap params = new HashMap();
		Config config = Config.getInstance();

		params.put("${Header1}", config.getHead1());
		params.put("${ShopID}", "B001");
		params.put("${PosId}", config.getPOS());
		params.put("${InvoiceNo}", "17");
		params.put("${Cashier}", config.getCashier());
		params.put("${Date}", "2004.06.04");	
		params.put("${Remark}", config.getRemark());
			
		params.put("${ItemCode}", "0004964324");
		params.put("${Description}", "AILEG102A圆领印花T恤");
		
		params.put("${UnitPrice}", "25.00");
		params.put("${QTY}", "1");
		params.put("${Discount}", "5");
		params.put("${Amount}", "20.00");
		params.put("${Total}", "20.00");
		
		params.put("${PayType}", "现金");
		params.put("${Currency}", "人民币");
		params.put("${PayAmount}", "600.00");
		params.put("${TotalPayAmount}", "600.00");
		params.put("${Change}", "575.00");
		params.put("${ActualPayAmount}", "25.00");
		params.put("${Trail1}", "欢迎光临*****便利店-服务电话:020-12345678");
		params.put("${Trail2}", "请保留电脑记帐单,作为退换货凭证");
		
		PosInvoice posInvoice =
			new PosInvoice("POSInvoice.xml", (new ConsolePrinter()));
		posInvoice.parseHeader(params);
		posInvoice.parseSale(params);
		//posInvoice.parseTotal(params);
		//posInvoice.parseDiscount(params);
		posInvoice.parsePayment(params);
		posInvoice.parseTrail(params);

	}

}

