package bn.aurora.conv;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.batik.ext.awt.RenderingHintsKeyExt;

import static java.lang.System.out;

/**
 * Class containing code for converting an image's white background to be
 * transparent, adapted with minor changes from StackOverflow thread "How to
 * make a color transparent in a BufferedImage and save as PNG"
 * (http://stackoverflow.com/questions/665406/how-to-make-a-color-transparen...).
 */
public class TransparencyExample
{
	/**
	 * Main function for converting image at provided path/file name to have
	 * transparent background.
	 *
	 * @param arguments Command-line arguments: only one argument is required
	 *    with the first (required) argument being the path/name of the source
	 *    image and the second (optional) argument being the path/name of the
	 *    destination file.
	 */
	public static void main(final String[] arguments) throws Exception
	{
		//build the background transparent
		String imagePath = "/media/DATA/test3/result/xhdpi/";
		File inFile = new File(imagePath, "aaam.png");
		BufferedImage image = ImageIO.read(inFile);
		
		Image transpImg1 = TransformGrayToTransparency(image);
		BufferedImage resultImage1 = ImageToBufferedImage(transpImg1, image.getWidth(), image.getHeight());
		
		
		//taking svg
		String path = "/media/DATA/test3/schedule_screen/"; 
		String pathToSVGForTransform = "file://"+ path + "delete_icon.svg";

				  URL u = new URL(pathToSVGForTransform);
		  		SvgImage svi = new SvgImage(u);
		
		//getting the background
		Graphics2D g2d = (Graphics2D) resultImage1.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        g2d.setRenderingHint(RenderingHintsKeyExt.KEY_TRANSCODING,
                RenderingHintsKeyExt.VALUE_TRANSCODING_PRINTING);
		//get svg and paint on back
        
        
		svi.getRootSvgNode().paint(g2d);
		g2d.dispose();
        
		File outFile1 = new File(imagePath, "map_with_transparency1.png");
		ImageIO.write(resultImage1, "PNG", outFile1);
		
		System.out.println("Conversion done! Thanks for choosing us!");
		
		/*Image transpImg2 = TransformColorToTransparency(image, new Color(0, 50, 77), new Color(200, 200, 255));
	    BufferedImage resultImage2 = ImageToBufferedImage(transpImg2, image.getWidth(), image.getHeight());
	    
	    File outFile2 = new File(imagePath, "map_with_transparency2.png");
	    ImageIO.write(resultImage2, "PNG", outFile2);*/
		
	}

	

	/**
	 * Convert Image to BufferedImage.
	 *
	 * @param image Image to be converted to BufferedImage.
	 * @return BufferedImage corresponding to provided Image.
	 */
	private static BufferedImage imageToBufferedImage(final Image image)
	{
		final BufferedImage bufferedImage =
				new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g2 = bufferedImage.createGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
		return bufferedImage;
	}

	public static Image TransformGrayToTransparency(BufferedImage image)
	{
		ImageFilter filter = new RGBImageFilter()
		{
			public final int filterRGB(int x, int y, int rgb)
			{
				//return (rgb << 8) & 0xFF000000;
				return rgb & 0xFFFFFF;
			}
		};

		ImageProducer ip = new FilteredImageSource(image.getSource(), filter);
		return Toolkit.getDefaultToolkit().createImage(ip);
	}

	private static Image TransformColorToTransparency(BufferedImage image, Color c1, Color c2)
	{
		// Primitive test, just an example
		final int r1 = c1.getRed();
		final int g1 = c1.getGreen();
		final int b1 = c1.getBlue();
		final int r2 = c2.getRed();
		final int g2 = c2.getGreen();
		final int b2 = c2.getBlue();
		ImageFilter filter = new RGBImageFilter()
		{
			public final int filterRGB(int x, int y, int rgb)
			{
				int r = (rgb & 0xFF0000) >> 16;
				int g = (rgb & 0xFF00) >> 8;
				int b = rgb & 0xFF;
				if (r >= r1 && r <= r2 &&
						g >= g1 && g <= g2 &&
						b >= b1 && b <= b2)
				{
					// Set fully transparent but keep color
					return rgb & 0xFFFFFF;
				}
				return rgb;
			}
		};

		ImageProducer ip = new FilteredImageSource(image.getSource(), filter);
		return Toolkit.getDefaultToolkit().createImage(ip);
	}

	private static BufferedImage ImageToBufferedImage(Image image, int width, int height)
	{
		BufferedImage dest = new BufferedImage(
				width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = dest.createGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
		return dest;
	}

	/**
	 * Make provided image transparent wherever color matches the provided color.
	 *
	 * @param im BufferedImage whose color will be made transparent.
	 * @param color Color in provided image which will be made transparent.
	 * @return Image with transparency applied.
	 */
	public static Image makeColorTransparent(final BufferedImage im, final Color color)
	{
		final ImageFilter filter = new RGBImageFilter()
		{
			// the color we are looking for (white)... Alpha bits are set to opaque
			public int markerRGB = color.getRGB() | 0xFFFFFFFF;

			public final int filterRGB(final int x, final int y, final int rgb)
			{
				if ((rgb | 0xFF000000) == markerRGB)
				{
					// Mark the alpha bits as zero - transparent
					return 0x00FFFFFF & rgb;
				}
				else
				{
					// nothing to do
					return rgb;
				}
			}
		};

		final ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
		return Toolkit.getDefaultToolkit().createImage(ip);
	}
}

