/*
 * Created on 2004-6-6
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.loader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import com.royalstone.pos.invoke.FileDownload;
import com.royalstone.pos.invoke.HttpInvoker;
import com.royalstone.pos.shell.pos;
import com.royalstone.pos.util.UnZipFile;
import com.royalstone.pos.web.CardType;
import com.royalstone.pos.web.PosInit;
import com.royalstone.pos.web.PriceOfflineTable;
import com.royalstone.pos.web.PriceSize;
import com.royalstone.pos.web.PriceTableComb;
import com.royalstone.pos.web.PriceTableCut;

/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class XLoader {

	private PosInit posInit = new PosInit();
	private PriceSize servletPriceSize = new PriceSize();
	private PriceOfflineTable priceOfflineTable = new PriceOfflineTable();
	private PriceTableCut priceTableCut=new PriceTableCut();
	private PriceTableComb priceTableComb=new PriceTableComb();
	private CardType cardType=new CardType();

	public static void main(String[] args) {
		XLoader loader = new XLoader("localhost", 9090);
		try {
			loader.loadPrice("price.NEW.xml");
			loader.loadPosConfig("pos.NEW.xml", "P001");
		} catch (JDOMException e) {
			System.out.println("load Failed.");
		} catch (IOException e) {
			System.out.println("load Failed.");
		}
	}

	public XLoader(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void loadPrice(String file) throws JDOMException, IOException {
		String url = "http://" + host + ":" + port + "/pos41/PriceTable";
		loadXMLDoc(file, url);
	}
	public void loadPriceComb(String file) throws JDOMException, IOException {
		
		if(pos.hasServer){

			String url = "http://" + host + ":" + port + "/pos41/PriceTableComb";
			loadXMLDoc(file, url);
			
		}else{

			priceTableComb.output(new FileOutputStream(file));
		}
	}
	
	public void loadAccurateTable(String file)
		throws JDOMException, IOException {
		String url = "http://" + host + ":" + port + "/pos41/AccurateTable";
		loadXMLDoc(file, url);
	}
	/**
	 * 从服务器下载商品价格数据
	 * @throws JDOMException
	 * @throws IOException
	 */
	public void loadPrice() throws JDOMException, IOException { //
		File downPrice = new File("download");
		if (!downPrice.exists())
			downPrice.mkdirs();
		FileDownload fd = new FileDownload(host, Integer.toString(port));
		if (!fd.download("pricetable.zip", "download/pricetable.zip"))
			throw new IOException("下载价格文件失败");
		if (!fd.download("promotable.zip", "download/promotable.zip"))
			throw new IOException("下载促销价格文件失败");
		UnZipFile unzip = new UnZipFile();
		unzip.unZip("download/pricetable.zip", "price");
		unzip.unZip("download/promotable.zip", "promo");
	}

	public void loadOfflinePrice() throws IOException, JDOMException {

		String result = null;

		if (pos.hasServer) {

			URL servlet;
			HttpURLConnection conn = null;
			servlet =
				new URL("http://" + host + ":" + port + "/pos41/PriceSize");
			conn = (HttpURLConnection) servlet.openConnection();

			result = HttpInvoker.getOfflinePriceSize(conn);

		} else {

			PipedOutputStream pout = new PipedOutputStream();
			PipedInputStream pin = new PipedInputStream(pout);

			servletPriceSize.output(new ObjectOutputStream(pout));

			try {
				result = (String) (new ObjectInputStream(pin)).readObject();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}

		}

		//String result= "30000,20000";
		if (result == null)
			throw new IOException("获取脱机商品价格数量失败，请检查网络");
		String[] counts = result.split(",");
		if (counts.length != 2)
			throw new IOException("获取的脱机商品价格数量有误，请重新下载");
		int priceSize = 0;
		try {
			priceSize = Integer.parseInt(counts[0]);
		} catch (NumberFormatException e) {
			priceSize = 0;
		}
		int priceExtSize = 0;
		try {
			priceExtSize = Integer.parseInt(counts[1]);
		} catch (NumberFormatException e) {
			priceExtSize = 0;
		}
		int goodsCount = 1000;

		int priceCount = (int) Math.ceil((double) priceSize / goodsCount);
		int priceExtCount = (int) Math.ceil((double) priceExtSize / goodsCount);
		System.out.println("priceCount:" + priceCount);
		System.out.println("priceExtCount:" + priceExtCount);
		String maxCode = "0";
		//------------------------------------------------
		File priceFile = new File("price_offline");
		if (!priceFile.exists())
			priceFile.mkdir();
		String[] files = priceFile.list();
		for (int i = 0; i < files.length; i++) {
			File delete = new File("price_offline/" + files[i]);
			delete.delete();
		}

		File priceExtFile = new File("priceExt_offline");
		if (!priceExtFile.exists())
			priceExtFile.mkdir();
		files = priceExtFile.list();
		for (int i = 0; i < files.length; i++) {
			File delete = new File("priceExt_offline/" + files[i]);
			delete.delete();
		}

		if (pos.hasServer) {

			for (int i = 0; i < priceCount; i++) {
				String url =
					"http://"
						+ host
						+ ":"
						+ port
						+ "/pos41/PriceOfflineTable?selectLength="
						+ goodsCount
						+ "&selectType=price&selectMaxCode="
						+ maxCode;
				maxCode =
					loadXMLDocOfflinePrice(
						"price_offline/price" + i + ".xml",
						url,
						i);
			}
			maxCode = "0";
			for (int i = 0; i < priceExtCount; i++) {
				String url =
					"http://"
						+ host
						+ ":"
						+ port
						+ "/pos41/PriceOfflineTable?selectLength="
						+ goodsCount
						+ "&selectType=priceExt&selectMaxCode="
						+ maxCode;
				maxCode =
					loadXMLDocOfflinePrice(
						"priceExt_offline/priceExt" + i + ".xml",
						url,
						i);
			}

		} else {

			for (int i = 0; i < priceCount; i++) {

				priceOfflineTable.output(
					new FileOutputStream("price_offline/price" + i + ".xml"),
					"price",
					goodsCount,
					maxCode);

				maxCode =
					loadXMLDocOfflinePrice(
						"price_offline/price" + i + ".xml",
						i);
			}
			
			maxCode = "0";
			
			for (int i = 0; i < priceExtCount; i++) {

				priceOfflineTable.output(
					new FileOutputStream(
						"priceExt_offline/priceExt" + i + ".xml"),
					"priceExt",
					goodsCount,
					maxCode);

				maxCode =
					loadXMLDocOfflinePrice(
						"priceExt_offline/priceExt" + i + ".xml",
						i);
			}

		}

	}

	public void loadPriceExt(String file) throws JDOMException, IOException {
		String url = "http://" + host + ":" + port + "/pos41/PriceTableExt";
		loadXMLDoc(file, url);
	}
	public void loadPriceCut(String file) throws JDOMException, IOException {
		
		if(pos.hasServer){

			String url = "http://" + host + ":" + port + "/pos41/PriceTableCut";
			loadXMLDoc(file, url);
			
		}else{
			
			priceTableCut.output(new FileOutputStream(file));
			
		}
	}

	public void loadPosConfig(String file, String posid)
		throws JDOMException, IOException {

		if (pos.hasServer) {
			String url =
				"http://" + host + ":" + port + "/pos41/PosInit?posid=" + posid;
			loadXMLDoc(file, url);
		} else {
			FileOutputStream fs = new FileOutputStream(file);
			posInit.output(fs, posid);
		}

	}

	public void loadCardType(String file) throws JDOMException, IOException {
		
		if(pos.hasServer){

			String url = "http://" + host + ":" + port + "/pos41/CardType";
			loadXMLDoc(file, url);
			
		}else{
			
			cardType.output(new FileOutputStream(file));
		}
	}
	
	public void loadPayMode(String file) throws JDOMException, IOException {
		
		if(pos.hasServer){

			String url = "http://" + host + ":" + port + "/pos41/PayMode";
			loadXMLDoc(file, url);
		}else{
			new PayModeLoader().download2Xml(file);
		}
	}

	public void loadXMLDoc(String file, String url)
		throws JDOMException, IOException {
		FileOutputStream fs = new FileOutputStream(file);

		servlet = new URL(url);
		Document doc = (new SAXBuilder()).build(servlet);
		Element root = doc.getRootElement();
		XMLOutputter outputter = new XMLOutputter("  ", true, "GB2312");
		outputter.output(doc, fs);
		fs.close();
	}

	public String loadXMLDocOfflinePrice(String file, String url, int count)
		throws JDOMException, IOException {
		FileOutputStream fs = new FileOutputStream(file);

		servlet = new URL(url);
		Document doc = (new SAXBuilder()).build(servlet);
		Element root = doc.getRootElement();

		String maxCode = root.getChildText("MaxCode");
		root.removeChild("MaxCode");
		XMLOutputter outputter = new XMLOutputter("  ", true, "GB2312");
		outputter.output(doc, fs);
		fs.close();
		return maxCode;
	}

	public String loadXMLDocOfflinePrice(String file, int count)
		throws JDOMException, IOException {

		Document doc = (new SAXBuilder()).build(file);
		Element root = doc.getRootElement();

		String maxCode = root.getChildText("MaxCode");
		root.removeChild("MaxCode");
		XMLOutputter outputter = new XMLOutputter("  ", true, "GB2312");
		FileOutputStream fs = new FileOutputStream(file);
		outputter.output(doc, fs);
		fs.close();
		return maxCode;
	}

	private URL servlet;
	private HttpURLConnection conn;
	private String host;
	private int port;
}
