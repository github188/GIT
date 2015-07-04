/*
 * 创建日期 2004-6-14
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
package com.royalstone.pos.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

/**
 * 启动窗口
 * @author liangxinbiao
 */
public class StartFrame extends JFrame {

	private StartPanel startPanel = new StartPanel();

	public StartFrame() {

		setSize(500, 300);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(
			(int) ((screenSize.getWidth() - 500) / 2),
			(int) ((screenSize.getHeight() - 300) / 2));
		this.getContentPane().add(startPanel, BorderLayout.CENTER);
		this.setUndecorated(true);

	}

}
