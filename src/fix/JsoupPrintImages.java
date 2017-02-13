package fix;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupPrintImages {
	public static void main(String[] args) throws IOException {
		// Document doc = Jsoup.connect("http://www.javatpoint.com").get();
		// Document doc =
		// Jsoup.connect("http://www.google.de/search?tbm=isch&q=Turtle").get();
		Connection connect = Jsoup.connect("http://www.google.de/search?tbm=isch&q=Turtle");
		connect = connect.followRedirects(true);
		Document doc = connect.get();
		// http://www.google.de/search?tbm=isch&q=Turtle
		Elements images = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
		for (Element image : images) {
			System.out.println("src : " + image.attr("src"));
			System.out.println("height : " + image.attr("height"));
			System.out.println("width : " + image.attr("width"));
			System.out.println("alt : " + image.attr("alt"));
		}

	}
}