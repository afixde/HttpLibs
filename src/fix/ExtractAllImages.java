package fix;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.text.AttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class ExtractAllImages {
	public static void main(String args[]) throws Exception {

		// String webUrl = "http://www.hdwallpapers.in/";
		String webUrl = "http://www.google.de/search?tbm=isch&q=Turtle";
		// URL url = new URL(webUrl);
		// URLConnection connection = url.openConnection();
		// InputStream is = connection.getInputStream();
		// InputStreamReader isr = new InputStreamReader(is);
		// BufferedReader br = new BufferedReader(isr);

		CloseableHttpClient httpclient = HttpClients.createSystem();
		HttpGet httpget = new HttpGet(webUrl);
		CloseableHttpResponse httpResponse = httpclient.execute(httpget);
		BufferedReader br = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));


		HTMLEditorKit htmlKit = new HTMLEditorKit();
		HTMLDocument htmlDoc = (HTMLDocument) htmlKit.createDefaultDocument();
		HTMLEditorKit.Parser parser = new ParserDelegator();
		HTMLEditorKit.ParserCallback callback = htmlDoc.getReader(0);
		parser.parse(br, callback, true);

		for (HTMLDocument.Iterator iterator = htmlDoc.getIterator(HTML.Tag.IMG); iterator.isValid(); iterator.next()) {
			AttributeSet attributes = (AttributeSet) iterator.getAttributes();
			String imgSrc = (String) ((javax.swing.text.AttributeSet) attributes).getAttribute(HTML.Attribute.SRC);

			if (imgSrc != null && (imgSrc.endsWith(".jpg") || (imgSrc.endsWith(".png")) || (imgSrc.endsWith(".jpeg"))
					|| (imgSrc.endsWith(".bmp")) || (imgSrc.endsWith(".ico")))) {
				try {
					downloadImage(webUrl, imgSrc);
				} catch (IOException ex) {
					System.out.println(ex.getMessage());
				}
			}
		}
	}

	private static void downloadImage(String url, String imgSrc) throws IOException {
		BufferedImage image = null;
		try {
			if (!(imgSrc.startsWith("http"))) {
				url = url + imgSrc;
			} else {
				url = imgSrc;
			}
			imgSrc = imgSrc.substring(imgSrc.lastIndexOf("/") + 1);
			String imageFormat = null;
			imageFormat = imgSrc.substring(imgSrc.lastIndexOf(".") + 1);
			String imgPath = null;
			imgPath = "D:/tmp/out/" + imgSrc + "";
			URL imageUrl = new URL(url);
			image = ImageIO.read(imageUrl);
			if (image != null) {
				File file = new File(imgPath);
				ImageIO.write(image, imageFormat, file);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}