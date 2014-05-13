FractWall
=========

Fractal wallpaper generator. Generate LARGE fractal images, for example if you want to create wallpaper

The program is written in Java and can be built using ant.

Execute the program with: java -jar FractWall.jar [options]

Where [options] might be:
``` 
 -width <int>         The width in pixels of the generated image
 -height <int>        The height in pixels of the generated image
 -xaos <filename>.xpf A save-file from the xaos fractal generator (Only the
                      xmin,ymin,xsize and ysize are set from this file.) You
                      need to set width and height before you use this switch.
 -xmin <double>       The minimal real part of the area to calculate
 -ymin <double>       The minimal imaginary part of the area to calculate
 -xsize <double>      The real part range of the area to calculate
 -ysize <double>      The imaginary part range of the area to calculate
 -maxiter <int>       Maximum number of iterations per pixel
 -ncolors (2|16|256)  Number of colors in the palette 2 means black and white.
                      16 and 256 gives a grayscale going from black to white
                      and all the way back to black again: 16 colors example:
                      1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,14,13,12,11,10,9,...
                      (black = 0) only if maxiter is reached.
 -output <filename>   The output file. Since the program currently only
                      supports gif-files, it should end with '.gif'
 -q                   Quiet, do not show calculated height so far (0...height)
 -type mandel         Type of fractal, Mandelbrot - 'mandel' is the only
                      supported for now
```
Example:

java -jar FractWall.jar -width 9000 -height 4800 -ncolors 2
                        -maxiter 1024 -xmin -0.37492656 -ymin 0.659325857
                        -xsize 0.001875 -ysize 0.001

