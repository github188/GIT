package posserver.tools.PressTest;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableItem;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Struct.SalePayDef;

import posserver.tools.PressTest.GlobalStatus.MyTask;

public class PosSvrPressureBarChart extends JFrame  implements WindowListener{
	public static DefaultCategoryDataset localDefaultCategoryDataset;

	public PosSvrPressureBarChart(String paramString) {
		super(paramString);
		JPanel localJPanel = createDemoPanel();
		setContentPane(localJPanel);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	public CategoryAxis domainAxis;
	public ValueAxis rangeAxis;

	private CategoryDataset createDataset() {
		localDefaultCategoryDataset = new DefaultCategoryDataset();
		return localDefaultCategoryDataset;
	}

	private JFreeChart createChart(CategoryDataset paramCategoryDataset) {
		JFreeChart localJFreeChart = ChartFactory.createBarChart3D("压力测试结果显示",
				"", "数量", paramCategoryDataset, PlotOrientation.VERTICAL, true,
				true, false);
		CategoryPlot localCategoryPlot = (CategoryPlot) localJFreeChart
				.getPlot();
		localCategoryPlot.setOutlineVisible(false);
		localCategoryPlot.setDomainGridlinesVisible(true);
		CategoryAxis localCategoryAxis = localCategoryPlot.getDomainAxis();
		localCategoryAxis.setCategoryLabelPositions(CategoryLabelPositions
				.createUpRotationLabelPositions(Math.PI / 6.0));
		localCategoryAxis.setCategoryMargin(0.0D);
		BarRenderer3D localBarRenderer3D = (BarRenderer3D) localCategoryPlot
				.getRenderer();
		localBarRenderer3D.setDrawBarOutline(false);
		configFont(localJFreeChart);
		return localJFreeChart;
	}

	/**
	 * 配置字体
	 * 
	 * @param chart
	 *            JFreeChart 对象
	 */
	private void configFont(JFreeChart chart) {
		// 配置字体
		Font xfont = new Font("宋体", Font.PLAIN, 12);// X轴
		Font yfont = new Font("宋体", Font.PLAIN, 12);// Y轴
		Font kfont = new Font("宋体", Font.PLAIN, 12);// 底部
		Font titleFont = new Font("隶书", Font.BOLD, 25); // 图片标题
		CategoryPlot plot = chart.getCategoryPlot();// 图形的绘制结构对象

		// 图片标题
		chart.setTitle(new TextTitle(chart.getTitle().getText(), titleFont));

		// 底部
		chart.getLegend().setItemFont(kfont);

		// X 轴
		domainAxis = plot.getDomainAxis();
		domainAxis.setLabelFont(xfont);// 轴标题
		domainAxis.setTickLabelFont(xfont);// 轴数值
		domainAxis.setTickLabelPaint(Color.BLUE); // 字体颜色
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45); // 横轴上的label斜显示

		// Y 轴
		rangeAxis = plot.getRangeAxis();
		rangeAxis.setLabelFont(yfont);
		rangeAxis.setLabelPaint(Color.BLUE); // 字体颜色
		rangeAxis.setTickLabelFont(yfont);
		rangeAxis.setLowerBound(0);
		rangeAxis.setUpperBound(1);
		((NumberAxis) rangeAxis).setTickUnit(new NumberTickUnit(0.1));
		rangeAxis.setAutoRangeMinimumSize(1);

	}

	public JPanel createDemoPanel() {
		JFreeChart localJFreeChart = createChart(createDataset());
		ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
		localChartPanel.setPreferredSize(new Dimension(600, 400));
		localChartPanel.setMouseWheelEnabled(true);
		return localChartPanel;
	}

	public static void showFrame() {
		PosSvrPressureBarChart localBarChart3DDemo1 = new PosSvrPressureBarChart(
				"压力测试");
		localBarChart3DDemo1.pack();
		RefineryUtilities.centerFrameOnScreen(localBarChart3DDemo1);
		localBarChart3DDemo1.setVisible(true);
	}

	
	
	private Timer timer;

	public void startUpdateStatus() {
		stopUpdateStatus();
		
		timer = new Timer();
		timer.schedule(new MyTask(), 5000, 5000);
	}

	public void stopUpdateStatus() {
		if (timer != null) {
			timer.cancel();
		}
		
		timer = null;
	}

	class MyTask extends java.util.TimerTask {
		private PosSvrPressureBarChart psrb;

		private MyTask() {

		}

		public MyTask(PosSvrPressureBarChart psrb) {
			this.psrb = psrb;
		}

		public void run() {
			List arrKeys = new ArrayList();
			String strSelect = "select distinct threadcount as threadcount from PressStatus order by threadcount ASC";

			ResultSet rs = null;
			try {

				rs = GlobalVar.getSqldb2().selectData(strSelect);
				if (rs == null) {
					psrb.stopUpdateStatus();
					new MessageBox("获取数据失败!\n" + strSelect);
					return;
				}

				while (rs.next()) {
					arrKeys.add(rs.getString("threadcount"));
				}
			} catch (Exception ex) {
				psrb.stopUpdateStatus();

				new MessageBox(ex.getMessage());
				ex.printStackTrace();

				return;
			} finally {
				if (rs != null)
					try {
						rs.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}

			for (int i = 0; i < arrKeys.size(); i++) {
				try {
					String catetory = (String)arrKeys.get(i);
					strSelect = "select IDENTIFY,THREADCOUNT,ACTIVETHREADNUM,ACTIVETHREADNUM,CMDROUNDTIMES,CMDSENDTIMES,CMDSENDFAILETIMES from PressStatus WHERE THREADCOUNT = "
							+ catetory + " order by IDENTIFY ASC";

					rs = GlobalVar.getSqldb2().selectData(strSelect);
					if (rs == null) {
						psrb.stopUpdateStatus();
						new MessageBox("获取数据失败!\n" + strSelect);
						return;
					}

					while (rs.next()) {
						localDefaultCategoryDataset.addValue(
								((double)rs.getInt("CMDSENDFAILETIMES"))
										/ ((double)rs.getInt("CMDSENDTIMES")),
								rs.getString("IDENTIFY"), catetory);
					}
				} catch (Exception ex) {
					psrb.stopUpdateStatus();

					new MessageBox(ex.getMessage());
					ex.printStackTrace();

					return;
				} finally {
					if (rs != null)
						try {
							rs.close();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			}

			/*
			 * private CategoryDataset createDataset() {
			 * localDefaultCategoryDataset = new DefaultCategoryDataset();
			 * localDefaultCategoryDataset.addValue(Series1value, Series1,
			 * category); localDefaultCategoryDataset.addValue(Series2value,
			 * Series2, category);
			 * localDefaultCategoryDataset.addValue(Series3value, Series3,
			 * category); return localDefaultCategoryDataset; }
			 */

			/*
			 * public static String category = "分类";
			 * 
			 * public static String Series1 = "命令发送次数"; public static double
			 * Series1value = 0;
			 * 
			 * public static String Series2 = "命令发送成功次数"; public static double
			 * Series2value = 0;
			 * 
			 * public static String Series3 = "命令发送失败次数"; public static double
			 * Series3value = 0;
			 */

			/*
			 * Series1value = Series1value + 6; Series2value = Series2value + 4;
			 * Series3value = Series3value + 2;
			 * 
			 * double max = Math.max(Math.max(Series1value, Series2value),
			 * Series3value);
			 * 
			 * if (max < 10) { rangeAxis.setUpperBound(10); ((NumberAxis)
			 * rangeAxis).setTickUnit(new NumberTickUnit(1)); } else {
			 * rangeAxis.setUpperBound(max + (long) (max / 10)); ((NumberAxis)
			 * rangeAxis).setTickUnit(new NumberTickUnit( (long) (max / 10))); }
			 * 
			 * localDefaultCategoryDataset.addValue(Series1value, Series1,
			 * category); localDefaultCategoryDataset.addValue(Series2value,
			 * Series2, category);
			 * localDefaultCategoryDataset.addValue(Series3value, Series3,
			 * category);
			 */
		}
	}

	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		this.stopUpdateStatus();
	}

	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		this.startUpdateStatus();
	}

	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub

	}
}