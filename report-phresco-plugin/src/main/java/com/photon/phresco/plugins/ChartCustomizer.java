package com.photon.phresco.plugins;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import net.sf.jasperreports.engine.JRChart;
import net.sf.jasperreports.engine.JRChartCustomizer;

public class ChartCustomizer implements JRChartCustomizer{

	@Override
	public void customize(JFreeChart chart, JRChart jasperChart) {
		if(chart == null) return;
		if(!(chart.getPlot() instanceof CategoryPlot)) return;
		CategoryPlot categoryPlot = (CategoryPlot) chart.getPlot();
		categoryPlot.getDomainAxis().setMaximumCategoryLabelLines(2);

		categoryPlot.getDomainAxis().setMaximumCategoryLabelWidthRatio(1.5f);
		ValueAxis valueAxis = categoryPlot.getRangeAxis();

		valueAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

	}
}
