package com.royalstone.pos.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * 要更新的文件信息的列表
 * @author liangxinbiao
 */
public class UpdateList implements Serializable {

	private ArrayList files = new ArrayList();

	/**
	 * @param filename 更新列表文件的详细路径
	 * @throws FileNotFoundException
	 * @throws JDOMException
	 */
	public UpdateList(String filename)
		throws FileNotFoundException, JDOMException {

		Document doc = (new SAXBuilder()).build(new FileInputStream(filename));
		Element root = doc.getRootElement();

		List fileList = root.getChildren("File");

		for (int i = 0; i < fileList.size(); i++) {
			Element file = (Element) fileList.get(i);
			Element srcPath = file.getChild("SrcPath");
			Element destPath = file.getChild("DestPath");
			UpdateFile updateFile =
				new UpdateFile(srcPath.getTextTrim(), destPath.getTextTrim());
			files.add(updateFile);
		}

	}

	/**
	 * @return 要更新文件的总数
	 */
	public int size() {
		return files.size();
	}

	/**
	 * 取得更新文件信息
	 * @param index 索引
	 * @return 更新文件信息
	 */
	public UpdateFile get(int index) {
		return (UpdateFile) files.get(index);
	}

	/**
	 * 主方法,测试用
	 * @param args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {
		UpdateList updateList = new UpdateList("posupdate.xml");
		System.out.println("size=" + updateList.size());
		for (int i = 0; i < updateList.size(); i++) {
			UpdateFile updateFile = updateList.get(i);
			System.out.println("src=" + updateFile.getSrcPath());
			System.out.println("dest=" + updateFile.getDestPath());
		}
	}
}
