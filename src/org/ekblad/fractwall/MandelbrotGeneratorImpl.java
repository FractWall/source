package org.ekblad.fractwall;

/**
 * Implementation of Mandelbrot calculations
 * @author Johan Ekblad &lt;jka@ekblad.org&gt;
 * @license GNU GPL v2
 */

public class MandelbrotGeneratorImpl implements ImageGenerator {
	
	public byte[] generateImage(FractalData fd) 
	{ 
		int maxiter = fd.getMaxiter();
		int ncolors = fd.getNcolors();
		int width = fd.getWidth();
		int height = fd.getHeight();
		
		byte pixmap[] = new byte[width*height]; 
		double re[] = new double[width]; 

		double im=fd.getYmin(); 
		double deltaRe=fd.getXsize()/width; 
		double deltaIm=fd.getYsize()/height; 

		int count=0; 
		re[0]=fd.getXmin(); 
		for (int i=1; i<width; i++) 
		{ 
			re[i]=re[i-1]+deltaRe; 
		} 
		for (int r=0; r<height; r++) 
		{ 
			if (!fd.isQuiet())
			{
				System.out.println (""+r);
			}
			for (int c=0; c<width; c++) 
			{ 
				// Step over first iteration
				int color=1; 
				double xsqr=0.0f; 
				double ysqr=0.0f; 
				double y=im; 
				double x=re[c]; 
				do 
				{ 
					xsqr=x*x; 
					ysqr=y*y; 
					y=2*x*y+im; 
					x=xsqr-ysqr+re[c]; 
					color++; 
				} 
				while (xsqr+ysqr < 4.0 && color < maxiter); 

			    byte col;   
				if (ncolors == 16 || ncolors == 256)
				{
	                //  order2          0,1,2,3,4,5,6,7,8, 9,10,11,12,13,  14,15,16,17,18,19,20,21,22,23,24,25,26,27,   0, 1
					//  order           0,1,2,3,4,5,6,7,8, 9,10,11,12,13,   0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,   0, 1,             				
					//  color           1,2,3,4,5,6,7,8,9,10,11,12,13,14,  15,16,17,18,19,20,21,22,23,24,25,26,27,28,  29,30
					//  col				1,2,3,4,5,6,7,8,9,10,11,12,13,14,  15,14,13,12,11,10, 9, 8, 7, 6, 5, 4, 3, 2,   1, 2, ...
					
					int order = ((color-1) % (ncolors-2));
				    int order2 = ((color-1) % (2*(ncolors-2)));
				
				    
			   	    if (color == maxiter)
					    col=(byte)0;
				    else if (order2 >= (ncolors-2))
					    col=(byte)(ncolors-1-order);
				    else
					    col=(byte)(order+1);
				}
				else
				{
					col=(byte)(color % 2);
				}
			   	    
				pixmap[count++]=col; 
			} 
			im+=deltaIm; 
		}
		return pixmap; 
	} 
}
