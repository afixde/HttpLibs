package fix;

import java.awt.Container;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class PicsSearch {
	final static int ROWS = 3;
	final static int COLS = 5;
	final static int PICS = ROWS * COLS;
	static CloseableHttpClient httpclient = null;

	public static void main(String[] args) throws Exception {
		httpclient = HttpClients.createSystem();
		// httpclient.getConnectionManager().().setCookiePolicy(CookiePolicy.ACCEPT_ALL
		// );
		// @formatter:off
		URI uri = new URIBuilder().setScheme("http").setHost("www.google.de").setPath("/search")
				.setParameter("tbm", "isch")//
				.setParameter("q", "Turtles")//
				// .setParameter("q", "марк котляр")//
				// .setParameter("q", "лепс - я поднимаю руки")//
				.build();
		// @formatter:on
		System.out.println(uri);
		HttpGet httpget = new HttpGet(uri);
		CloseableHttpResponse httpResponse = httpclient.execute(httpget);

		HttpEntity entity = httpResponse.getEntity();

		System.out.println("----------------------------------------");
		System.out.println(httpResponse.getStatusLine());
		Header[] headers = httpResponse.getAllHeaders();
		for (int i = 0; i < headers.length; i++) {
			System.out.println(headers[i]);
		}
		System.out.println("----------------------------------------");

		if (entity != null) {
			System.out.println(EntityUtils.toString(entity));
		}

		httpResponse = httpclient.execute(httpget);
		BufferedReader rd = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
			// result.append("\r\n");
		}
		// System.out.println(result);

		// showImageUrl("http://www.google.ru/intl/en_com/images/logo_plain.png",
		// "http://images.tapatalk-cdn.com/15/10/06/e7ce90fa0b87124d6ac37c5dc841917f.jpg",
		// "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcQpQT2H-QUXC2P5g70eC0JVUkMrsrfwiCyQwkxoHfPMa964DtNJ");

		System.out.println("----------------------------------------");
		int imgCnt = 0;
		Document doc = Jsoup.parse(result.toString());

		Elements lnks = doc.select("a[href]"); // a with href
		Element masthead = doc.select("div.masthead").first();
		Elements resultLinks = doc.select("h3.r > a"); // direct a after h3

		Elements images = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
		for (Element image : images) {
			System.out.println("\n" + ++imgCnt + ". name : " + image.attr("name"));
			System.out.println("src : " + image.attr("src"));
			System.out.println("height : " + image.attr("height"));
			System.out.println("width : " + image.attr("width"));
			System.out.println("alt : " + image.attr("alt"));
		}
		images = doc.select("img[src~=(?i)images\\?q\\=tbn]");
		System.out.println("----------------------------------------");
		System.out.println("----------------------------------------");
		int len = (images.size() > 0 || images.size() > PICS) ? PICS : images.size();
		String[] links = new String[len];
		Element[] elems = new Element[len];
		int imgNo = 0;
		for (Element image : images) {
			if (imgNo < PICS) {
				elems[imgNo] = image;
				links[imgNo++] = image.attr("src");
			}
			System.out.println("\n" + ++imgCnt + ". name : " + image.attr("name"));
			System.out.println("src : " + image.attr("src"));
			System.out.println("height : " + image.attr("height"));
			System.out.println("width : " + image.attr("width"));
			System.out.println("alt : " + image.attr("alt"));
		}
		if (images.size() > 0) {
			// showImageUrl(links);
			showImageUrl(elems);
			// followImageUrl(elems);
		}
	}

	private static void showImageUrl(String... urls) {
		JFrame frame = new JFrame();
		frame.setTitle("Polygons");
		frame.setSize(1550, 850);
		GridLayout layout = new GridLayout(ROWS, COLS);
		frame.setLayout(layout);

		// for (int row = 0; row < ROWS; row++) {
		// for (int col = 0; col < COLS; col++) {
		// JPanel panel = new JPanel();
		// JButton b = new JButton("(" + row + "," + col + ")");
		// frame.add(b).setLocation(row, col);
		// panel.add(b);
		// // b.addActionListener(new ButtonEvent(b, system, row, col));
		// frame.add(panel);
		// }
		// }
		//

		Container contentPane = frame.getContentPane();
		for (String url : urls) {
			try {
				ImageIcon imageIcon = new ImageIcon(ImageIO.read(new URL(url)));
				Image scaleImage = imageIcon.getImage().getScaledInstance(250, -1, Image.SCALE_SMOOTH);
				ImageIcon scaleIcon = new ImageIcon(scaleImage);
				JLabel sentenceLabel = new JLabel(scaleIcon);
				JButton button = new JButton(scaleIcon);
				button.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						openWebpage(url);

					}
				});
				contentPane.add(button);
			} catch (MalformedURLException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			} catch (IOException ex) {
				// "http://img.yandex.net/i/wiz"+imgType.trim()+".png"
				ex.printStackTrace();
			}
		}
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		// frame.setSize(frame.getWidth() * 110 / 100, frame.getHeight() * 110 /
		// 100);
		frame.setVisible(true);
	}

	private static void showImageUrl(Element... elems) {
		JFrame frame = new JFrame();
		frame.setTitle("Polygons");
		frame.setSize(1550, 850);
		GridLayout layout = new GridLayout(ROWS, COLS);
		frame.setLayout(layout);

		Container contentPane = frame.getContentPane();
		for (Element elem : elems) {
			String url = elem.attr("src");
			try {
				ImageIcon imageIcon = new ImageIcon(ImageIO.read(new URL(url)));
				Image scaleImage = imageIcon.getImage().getScaledInstance(250, -1, Image.SCALE_SMOOTH);
				ImageIcon scaleIcon = new ImageIcon(scaleImage);
				JLabel sentenceLabel = new JLabel(scaleIcon);
				JButton button = new JButton(scaleIcon);
				button.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						Node n = elem.parentNode();

						String u = n.attr("href");
						if (!u.equals("")) {
							// openWebpage(stripHttp(u));
							System.out.println(u);
							System.out.println(url);
							System.out.println(elem);
						}
					}
				});
				contentPane.add(button);
			} catch (MalformedURLException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			} catch (IOException ex) {
				// "http://img.yandex.net/i/wiz"+imgType.trim()+".png"
				ex.printStackTrace();
			}
		}
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		// frame.setSize(frame.getWidth() * 110 / 100, frame.getHeight() * 110 /
		// 100);
		frame.setVisible(true);
	}

	private static void showImageUrlExt(Element... elems) {
		JFrame frame = new JFrame();
		frame.setTitle("Polygons");
		frame.setSize(1550, 850);
		GridLayout layout = new GridLayout(ROWS, COLS);
		frame.setLayout(layout);

		Container contentPane = frame.getContentPane();
		for (Element ele : elems) {
			// String url = ele.parentNode() == null ? ele.attr("src") :
			Node e = ele.parentNode();

			String url = e.attr("href");
			if (url.equals(""))
				continue;
			try {
				JLabel sentenceLabel = new JLabel(new ImageIcon(ImageIO.read(new URL(url))));
				contentPane.add(sentenceLabel);
			} catch (MalformedURLException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			} catch (IOException ex) {
				// "http://img.yandex.net/i/wiz"+imgType.trim()+".png"
				ex.printStackTrace();
			}
		}
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	private static void followImageUrl(Element... elems) throws ClientProtocolException, IOException {
		for (Element ele : elems) {
			// String url = ele.parentNode() == null ? ele.attr("src") :
			Node e = ele.parentNode();

			String url = e.attr("href");
			url = stripHttp(url);
			if (url.equals(""))
				continue;

			Element[] images = getImages(url);
			if (images.length > 0) {
				// showImageUrl(links);
				showImageUrlExt(elems);
			}
		}
	}

	private static String stripHttp(String url) {
		while (url.indexOf("=http") > 0) {
			url = url.substring(url.indexOf("=http") + 1);
		}
		return url;
	}

	private static Element[] getImages(String url) throws ClientProtocolException, IOException {
		String urls = fetchImageLink(url);
		Document doc = Jsoup.parse(urls);
		Elements images = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
		images = doc.select("img[src~=(?i)images\\?q\\=tbn]");
		int len = (images.size() > 0 || images.size() > PICS) ? PICS : images.size();
		String[] links = new String[len];
		Element[] elems = new Element[len];
		int imgNo = 0;
		for (Element image : images) {
			if (imgNo < PICS) {
				elems[imgNo] = image;
				links[imgNo++] = image.attr("src");
			}
		}
		return elems;
	}

	private static String fetchImageLink(String url) throws ClientProtocolException, IOException {
		// httpclient = HttpClients.createSystem();
		HttpGet httpget = new HttpGet(url);
		CloseableHttpResponse httpResponse = httpclient.execute(httpget);
		// httpResponse = httpclient.execute(httpget);
		BufferedReader rd = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
	}

	public static BufferedImage resizeImage(BufferedImage image, int areaWidth, int areaHeight) {
		float scaleX = (float) areaWidth / image.getWidth();
		float scaleY = (float) areaHeight / image.getHeight();
		float scale = Math.min(scaleX, scaleY);
		int w = Math.round(image.getWidth() * scale);
		int h = Math.round(image.getHeight() * scale);

		int type = image.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB
				: BufferedImage.TYPE_INT_ARGB;

		boolean scaleDown = scale < 1;

		if (scaleDown) {
			// multi-pass bilinear div 2
			int currentW = image.getWidth();
			int currentH = image.getHeight();
			BufferedImage resized = image;
			while (currentW > w || currentH > h) {
				currentW = Math.max(w, currentW / 2);
				currentH = Math.max(h, currentH / 2);

				BufferedImage temp = new BufferedImage(currentW, currentH, type);
				Graphics2D g2 = temp.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g2.drawImage(resized, 0, 0, currentW, currentH, null);
				g2.dispose();
				resized = temp;
			}
			return resized;
		} else {
			Object hint = scale > 2 ? RenderingHints.VALUE_INTERPOLATION_BICUBIC
					: RenderingHints.VALUE_INTERPOLATION_BILINEAR;

			BufferedImage resized = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = resized.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
			g2.drawImage(image, 0, 0, w, h, null);
			g2.dispose();
			return resized;
		}
	}

	public static void openWebpage(String urlString) {
		try {
			Desktop.getDesktop().browse(new URL(urlString).toURI());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
