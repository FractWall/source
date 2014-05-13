package org.ekblad.fractwall;

/**
 * All data determining a fractal
 * @author Johan Ekblad &lt;jka@ekblad.org&gt;
 * @license GNU GPL v2
 */
public class FractalData {
	private String  type="mandel";
	private String  output="mbrot.gif";
	private int     ncolors=2; // 2,16,256
	private int     maxiter=2048;
	private int     width=4500;
	private int     height=2400;
	private double  xmin=-2.8125;
	private double  ymin=-1.5;
	private double  xsize=5.625;
	private double  ysize=3.0;
	private boolean quiet=false;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	public int getNcolors() {
		return ncolors;
	}
	public void setNcolors(int ncolors) {
		this.ncolors = ncolors;
	}
	public int getMaxiter() {
		return maxiter;
	}
	public void setMaxiter(int maxiter) {
		this.maxiter = maxiter;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public double getXmin() {
		return xmin;
	}
	public void setXmin(double xmin) {
		this.xmin = xmin;
	}
	public double getYmin() {
		return ymin;
	}
	public void setYmin(double ymin) {
		this.ymin = ymin;
	}
	public double getXsize() {
		return xsize;
	}
	public void setXsize(double xsize) {
		this.xsize = xsize;
	}
	public double getYsize() {
		return ysize;
	}
	public void setYsize(double ysize) {
		this.ysize = ysize;
	}
	public boolean isQuiet() {
		return quiet;
	}
	public void setQuiet(boolean quiet) {
		this.quiet = quiet;
	}
	
}
