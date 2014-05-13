package org.ekblad.fractwall;

/**
 * Interface for fractal calculations
 * @author Johan Ekblad &lt;jka@ekblad.org&gt;
 * @license GNU GPL v2
 */

public interface ImageGenerator {
	public byte[] generateImage(FractalData fd);
}
