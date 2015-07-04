/*
 * 创建日期 2004-6-8
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
package com.royalstone.pos.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author liangxinbiao
 *
 * 更改所生成类型注释的模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
public class DownloadServlet extends HttpServlet {

	private static String RESPONSE_CONTENT_TYPE = "application/file";

	private static final int BSIZE = 1024;

	public void doPost(
		HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException, IOException {
		try {
			response.setContentType(RESPONSE_CONTENT_TYPE);
			String fileName = request.getParameter("fileName");
			if (fileName != null && !fileName.equals("")) {
				File file = new File("../download/" + fileName);
				int length = (int) file.length();
				if (length != 0) {
					FileInputStream fis = new FileInputStream(file);
					byte[] buf = new byte[4096];
					ServletOutputStream sos = response.getOutputStream();
					while ((fis != null) && ((length = fis.read(buf)) != -1)) {
						sos.write(buf, 0, length);
					}
					sos.flush();
					sos.close();
					fis.close();
				}
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		File file = new File("");
		out.println("<H2>Hello World!</H2>");
		out.println("<H2>" + file.getAbsolutePath() + "</H2>");
		out.flush();
		out.close();
	}

}
