package org.ekblad.fractwall;

import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * This program generates large fractal images, might be used for example to generate
 * wallpaper for walls.
 * 
 * @author Johan Ekblad &lt;jka@ekblad.org&gt; Initial rev, 12 oct 2010
 * @license GNU GPL v2
 */

public class FractWall { 
		
	/**
	 * Translate (x,y) from lower left corner to center of rectangle.
	 * @param loc
	 * @return
	 */
	public static Rectangle2D.Double translateToCenter(Rectangle2D.Double loc)
	{
		return new Rectangle2D.Double(loc.x+loc.width/2.0,loc.y+loc.height/2.0,loc.width,loc.height);
	}
	
	/**
	 * Translate (x,y) from center to lower left corner of rectangle.
	 * @param loc
	 * @return
	 */
	public static Rectangle2D.Double translateToLLCorner(Rectangle2D.Double loc)
	{
		return new Rectangle2D.Double(loc.x-loc.width/2.0,loc.y-loc.height/2.0,loc.width,loc.height);
	}
	
	public FractWall(FractalData fd)
	{
		execute(fd);
	}
	
	/**
	 * Assumes (x,y) is the center of the rectangle
	 * @param width    in pixels
	 * @param height   in pixels
	 * @param ncolors  2,16,256
	 * @param loc      Rectangle lowerx,lowery,width,height
	 */
	//private void execute(String type, int width, int height, int ncolors, int maxiter, Rectangle2D.Double loc)
	private void execute(FractalData fd)
	{ 
		
		try
		{
			ImageGenerator ig=null;
			
			// TODO: more general
			if (fd.getType().equals("mandel"))
			{
				ig = new MandelbrotGeneratorImpl();
			}
			
			byte[] pixels = ig.generateImage(fd); 
			DataBuffer dbuf = new DataBufferByte(pixels, fd.getWidth()*fd.getHeight(), 0); 		
			byte mask=(byte)0x1;
			if (fd.getNcolors()==16)
			{
				mask=(byte)0xf;
			}
		
			int bits[] = new int[]{mask}; 		
			SampleModel sampleModel = (fd.getNcolors()==256)?
					                   new MultiPixelPackedSampleModel(DataBuffer.TYPE_BYTE, fd.getWidth(), fd.getHeight(), 8):
					                   new SinglePixelPackedSampleModel( DataBuffer.TYPE_BYTE, fd.getWidth(), fd.getHeight(), bits);
		
			WritableRaster raster = Raster.createWritableRaster(sampleModel, dbuf, null); 
			ColorModel cm = (fd.getNcolors()==16)?generateColorModel16():(fd.getNcolors()==256)?generateColorModel256():generateColorModel2();
		
			Image image = new BufferedImage(cm, raster, false, null);
			
			ImageIO.write((BufferedImage) image, "gif", new File(fd.getOutput()));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}		
	} 
	
	
	
	public static ColorModel generateColorModel256() 
	{ // Generate 16-color model 
		byte[] r = new byte[256]; 
		byte[] g = new byte[256]; 
		byte[] b = new byte[256]; 
		
		for (int i=0; i<256; i++)
		{
			r[i] = (byte) i; 
			g[i] = (byte) i; 
			b[i] = (byte) i;
		}
		 
		return new IndexColorModel(8, 256, r, g, b); 
	}
	
	public static ColorModel generateColorModel16() 
	{ // Generate 16-color model 
		byte[] r = new byte[16]; 
		byte[] g = new byte[16]; 
		byte[] b = new byte[16]; 
		
		for (int i=0; i<16; i++)
		{
			r[i] = (byte) (i*16); 
			g[i] = (byte) (i*16); 
			b[i] = (byte) (i*16);
		}
		 
		return new IndexColorModel(4, 16, r, g, b); 
	}
	
	public static ColorModel generateColorModel2() 
	{ 
		byte[] r = new byte[2]; 
		byte[] g = new byte[2]; 
		byte[] b = new byte[2]; 
		r[0] = 0; g[0] = 0; b[0] = 0; 
		r[1] = (byte)255; g[1] = (byte)255; b[1] = (byte)255; 
		return new IndexColorModel(1, 2, r, g, b); 
	}	
	
	private static Rectangle2D.Double parseXaos(File f)
	{
		BufferedReader reader = null;
		Rectangle2D.Double ret=null; 
		
		try
		{
			reader = new BufferedReader(new FileReader(f));
			String text = null;
			boolean done=false;
			while (!done && (text = reader.readLine()) != null)
		    {
				if (text.indexOf("(view ") >= 0)
				{
					text=text.substring(6, text.length());
					
					text=text.substring(0,text.length()-1);
					String doubles[]=text.split(" ");
					try
					{
						ret = new Rectangle2D.Double(Double.parseDouble(doubles[0]),
								                     Double.parseDouble(doubles[1]),
								                     Double.parseDouble(doubles[2]),
								                     Double.parseDouble(doubles[3]));
					}
					catch (NumberFormatException nfe)
					{
						nfe.printStackTrace();
					}
				}
		    }
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (reader != null)
				{
					reader.close();
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}        
		return ret;
	}
	
	
	private static FractalData parseArguments(String argv[])
	{
		FractalData fd = new FractalData();
		
		for (int i=0; i<argv.length; i++)
		{
			// -xaos is dependent of width and height (set them first!)
			if (argv[i].equals("-xaos"))
			{
				i++;
				File f=new File(argv[i]);
				Rectangle2D.Double xRec = parseXaos(f);
				if (xRec != null)
				{
					double diam=xRec.width;
					if (fd.getHeight()<fd.getWidth())
					{
						xRec.height=diam;
						xRec.width=diam*(fd.getWidth()*1.0/fd.getHeight());						
					}
					else
					{
						xRec.height=diam*(fd.getHeight()*1.0/fd.getWidth());						
						xRec.width=diam;
					}
					
					xRec=FractWall.translateToLLCorner(xRec);
					fd.setXmin(xRec.x);
					fd.setYmin(xRec.y);
					fd.setXsize(xRec.width);
					fd.setYsize(xRec.height);
				}
			}
			if (argv[i].equals("-width"))
			{
				i++;
				try {
				    fd.setWidth(Integer.parseInt(argv[i]));
				} catch (NumberFormatException nfe) {nfe.printStackTrace();}
			}
			if (argv[i].equals("-height"))
			{
				i++;
				try {
				    fd.setHeight(Integer.parseInt(argv[i]));
				} catch (NumberFormatException nfe) {nfe.printStackTrace();}
			}
			if (argv[i].equals("-xmin"))
			{
				i++;
				try {
				    fd.setXmin(Double.parseDouble(argv[i]));
				} catch (NumberFormatException nfe) {nfe.printStackTrace();}
			}
			if (argv[i].equals("-ymin"))
			{
				i++;
				try {
				    fd.setYmin(Double.parseDouble(argv[i]));
				} catch (NumberFormatException nfe) {nfe.printStackTrace();}
			}
			if (argv[i].equals("-xsize"))
			{
				i++;
				try {
				    fd.setXsize(Double.parseDouble(argv[i]));
				} catch (NumberFormatException nfe) {nfe.printStackTrace();}
			}
			if (argv[i].equals("-ysize"))
			{
				i++;
				try {
				    fd.setYsize(Double.parseDouble(argv[i]));
				} catch (NumberFormatException nfe) {nfe.printStackTrace();}
			}
			if (argv[i].equals("-maxiter"))
			{
				i++;
				try {
				    fd.setMaxiter(Integer.parseInt(argv[i]));
				} catch (NumberFormatException nfe) {nfe.printStackTrace();}
			}
			if (argv[i].equals("-ncolors"))
			{
				i++;
				try {
					int test=Integer.parseInt(argv[i]);
					if (test != 2 && test != 16 && test != 256)
					{
						throw new RuntimeException("ncolors must be 2,16 or 256");
					}
				    fd.setNcolors(test);
				} catch (NumberFormatException nfe) {nfe.printStackTrace();}
			}
			if (argv[i].equals("-output"))
			{
				i++;			
				fd.setOutput(argv[i]);			
			}
			if (argv[i].equals("-q") || argv[i].equals("-quiet"))
			{
				fd.setQuiet(true);
			}
			if (argv[i].equals("-type"))
			{
				i++;			
				if (!argv[i].equals("mandel"))
				{
					throw new RuntimeException("The type of the fractal must be 'mandel'");
				}
				fd.setType(argv[i]);			
			}
			if (argv[i].equals("-h") || argv[i].equals("-help"))
			{
				System.out.println("Syntax: java -jar FractWall.jar [-width <int>] [-height <int>] \n" +
						           "        [-xaos <filename>.xpf] [-xmin <double>] [-ymin <double>]\n" +
						           "        [-xsize <int>] [-ysize <int>] [-maxiter <int>] \n"+
						           "        [-ncolors (2|16|256)] [-output <filename>] [-q]\n"+
						           "        [-type mandel]\n"+
						           "\n"+
						           " -width <int>         The width in pixels of the generated image\n"+
						           " -height <int>        The height in pixels of the generated image\n"+
						           " -xaos <filename>.xpf A save-file from the xaos fractal generator (Only the\n"+
						           "                      xmin,ymin,xsize and ysize are set from this file.) You\n"+
						           "                      need to set width and height before you use this switch.\n" +
						           " -xmin <double>       The minimal real part of the area to calculate\n" +
						           " -ymin <double>       The minimal imaginary part of the area to calculate\n" +
						           " -xsize <double>      The real part range of the area to calculate\n" +
						           " -ysize <double>      The imaginary part range of the area to calculate\n" +
						           " -maxiter <int>       Maximum number of iterations per pixel\n" +
						           " -ncolors (2|16|256)  Number of colors in the palette 2 means black and white.\n"+
						           "                      16 and 256 gives a grayscale going from black to white\n"+
						           "                      and all the way back to black again: 16 colors example:\n" +
						           "                      1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,14,13,12,11,10,9,...\n"+
						           "                      (black = 0) only if maxiter is reached.\n"+
						           " -output <filename>   The output file. Since the program currently only\n"+
						           "                      supports gif-files, it should end with '.gif'\n"+			
						           " -q                   Quiet, do not show calculated height so far (0...height)\n"+
						           " -type mandel         Type of fractal, Mandelbrot - 'mandel' is the only\n"+
						           "                      supported for now\n"+
						           "\n"+
						           "Example: java -jar FractWall.jar -width 9000 -height 4800 -ncolors 2\n"+
						           "              -maxiter 1024 -xmin -0.37492656 -ymin 0.659325857 \n"+
						           "              -xsize 0.001875 -ysize 0.001\n"+
				                   "");
				System.exit(0);
			}
		}
		
		return fd;
	}
	
	
	
	public static void main(String argv[])
	{
		// Use this to generate my wall at home:
		
		//FractalData myfd = new FractalData();
		//myfd.setWidth(9000); // Width of wall = 4.5m
		//myfd.setHeight(4800); // Height of wall = 2.4m
		//myfd.setNcolors(2);
		//myfd.setMaxiter(1024);
		//myfd.setXmin(-0.37492656);
		//myfd.setYmin(0.659325857);
		//myfd.setXsize(0.001875); // Xsize and Ysize should be proportional to the width and height
		//myfd.setYsize(0.001);
		//new FractWall(myfd);

		new FractWall(parseArguments(argv));			
	}
}