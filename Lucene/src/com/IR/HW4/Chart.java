package com.IR.HW4;

import java.awt.BorderLayout;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Chart extends JFrame{
	Map<Integer, Integer> hm ;
	public Chart(Map<Integer, Integer> hm) {
		super("Zipf Curve");

		this.hm = hm ;
		JPanel chartPanel = createChartPanel();
		add(chartPanel, BorderLayout.CENTER);

		setSize(1280, 960);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private JPanel createChartPanel() {
		String chartTitle = "Zipf Curve";
		String xAxisLabel = "Rank";
		String yAxisLabel = "probablity";

		XYDataset dataset = createDataset();


		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
				xAxisLabel, yAxisLabel, dataset);

		return new ChartPanel(chart);
	}

	private XYDataset createDataset() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("Rank-probablity Distribution");

		int tot_freq = 0;
		for (Integer su :hm.values()){
			tot_freq = tot_freq + su;
		}
		//System.out.println("tot_freq"+tot_freq);

		for (Integer su :hm.keySet()){
			double y =(double) hm.get(su)/(double)tot_freq;  //defaulted to use rank probablity of occurence
		//	double y =(double) hm.get(su);	// use this to 	plot the rank frequency
			series1.add((double)su,y);
		}

		dataset.addSeries(series1);	     
		return dataset;
	}

}


