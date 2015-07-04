package com.royalstone.pos.util;

import java.io.Serializable;

/**
 * 封装要更新的文件的信息
 * @author liangxinbiao
 */
public class UpdateFile implements Serializable{

	private String srcPath = "";
	private String destPath = "";

	public UpdateFile(String src, String dest) {
		this.srcPath = src;
		this.destPath = dest;
	}

	/**
	 * @return 要更新文件在POS机上的路径
	 */
	public String getDestPath() {
		return destPath;
	}

	/**
	 * @return 要更新文件在服务器上的路径
	 */
	public String getSrcPath() {
		return srcPath;
	}

}
