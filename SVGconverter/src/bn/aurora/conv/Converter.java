package bn.aurora.conv;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.batik.ext.awt.RenderingHintsKeyExt;

/**
* This class provides a solution for scale SVG images in series
*/
public class Converter {

	
	static SvgImage svi;
	///e.g.: media/DATA/others/BN/kris/NEW_AURORA_DESIGN/Elements For Screens/Favourites Screen
	//Input folder
	static String DIR_INPUT = "/input_path/";
	///e.g.: media/DATA/test3/result/hdpi/set_tim_scr
	//Output folder
	static String DIR_OUTPUT = "/output_path/";
	static int NEW_RES = 720;
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		String path = DIR_INPUT; 
		 
		  String files;
		  
		  String initialPath = "file://"+ path;
		  String pathToSVGForTransform;
		  
		  File folder = new File(path);
		  File[] listOfFiles = folder.listFiles();
		  
		  for (int i = 0; i < listOfFiles.length; i++) 
		  //for (int i = 0; i < 1; i++)
		  {
			  if (listOfFiles[i].isFile()) 
			  {
				  files = listOfFiles[i].getName();
				  pathToSVGForTransform = initialPath + files;
				  System.out.println(files);
				  //transform
				  URL u = new URL(pathToSVGForTransform);
				  
				  //path = path.substring(0, path.length() - 5);
				  files = files.substring(0, files.length() - 4);
				  files = files.toLowerCase();
				  files = files.replace(" ", "_");
				  files = files.replace(":", "_");
				  doWork(u, files);
			  }
		  }
		  
		  System.out.println("Conversion done! Thanks for choosing us!");
		//URL u = new URL("file:///media/DATA/img.svg");
		//doWork(u);
	}
		
	
	private static void doWork(URL u, String nameFile) throws IOException {
		// TODO Auto-generated method stub
		svi = new SvgImage(u);
		
		Rectangle2D bounds = svi.getRootSvgNode().getPrimitiveBounds();
		
		/*System.out.println("width is "+ (int)bounds.getWidth());
		System.out.println("height is "+ (int)bounds.getHeight());*/
		
		int oldRes = 640;
		int newRes = NEW_RES;
		System.out.println("The conversion is into "+ newRes + " resolution!");
		
		int newWidht = (newRes*((int)bounds.getWidth()))/oldRes;
		
		BufferedImage bufferedImage = toBufferedImage(svi.getImage(newWidht, 0));
		
		save(bufferedImage, "png", nameFile);
		
	}

	private static void save(BufferedImage image, String ext, String nameFile) throws IOException {
        String fileName = nameFile;
        
        
        Image transpImg1 = TransformGrayToTransparency(image);
		BufferedImage resultImage1 = ImageToBufferedImage(transpImg1, image.getWidth(), image.getHeight());
		
		Graphics2D g2d = (Graphics2D) resultImage1.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        g2d.setRenderingHint(RenderingHintsKeyExt.KEY_TRANSCODING,
                RenderingHintsKeyExt.VALUE_TRANSCODING_PRINTING);
        
        g2d.transform(svi.getUsr2dev());
        svi.getRootSvgNode().paint(g2d);
		g2d.dispose();
        
		File outFile1 = new File( DIR_OUTPUT +fileName + "." + ext);
		ImageIO.write(resultImage1, "PNG", outFile1);
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
	
	private static BufferedImage ImageToBufferedImage(Image image, int width, int height)
	{
		BufferedImage dest = new BufferedImage(
				width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = dest.createGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
		return dest;
	}
	
	private static BufferedImage toBufferedImage(Image src) {
        int w = src.getWidth(null);
        int h = src.getHeight(null);
        int type = BufferedImage.TYPE_INT_RGB;  // other options
        BufferedImage dest = new BufferedImage(w, h, type);
        Graphics2D g2 = dest.createGraphics();
        g2.drawImage(src, 0, 0, null);
        g2.dispose();
        return dest;
    }
	
}
