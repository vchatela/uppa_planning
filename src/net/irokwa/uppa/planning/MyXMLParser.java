package net.irokwa.uppa.planning;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

public class MyXMLParser {

		static public Context context;

		public static ArrayList<Periode> getPromo(InputStream input) throws SAXException, IOException{
			// On passe par une classe factory pour obtenir une instance de sax
			SAXParserFactory fabrique = SAXParserFactory.newInstance();
			SAXParser parseur = null;
			ArrayList<Periode> entries = null;
			try {
				// On "fabrique" une instance de SAXParser
				parseur = fabrique.newSAXParser();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}
			
			/*
			 * Le handler sera gestionnaire du fichier XML c'est à dire que c'est lui qui sera chargé
			 * des opérations de parsing. On vera cette classe en détails ci aprés.
			*/
			DefaultHandler handler = new PromoParserXMLHandler();
			try {
				// On parse le fichier XML
				if(input==null)
					Log.e("ERROR","Parsing inputStream null");
				else{
					parseur.parse(input, handler);
					// On récupére directement la liste des feeds
					entries = ((PromoParserXMLHandler) handler).getData();
					if (!((PromoParserXMLHandler)handler).shouldBeOk() )
						entries = null;
				}
			} catch (SAXException e) {
				throw e;
			} catch (IOException e) {
				throw e;
			}
			return entries;
		}

		public static ArrayList<Promotion> getPromos(InputStream input) throws SAXException, IOException{
			// On passe par une classe factory pour obtenir une instance de sax
			SAXParserFactory fabrique = SAXParserFactory.newInstance();
			SAXParser parseur = null;
			ArrayList<Promotion> entries = null;
			try {
				// On "fabrique" une instance de SAXParser
				parseur = fabrique.newSAXParser();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}

			/*
			 * Le handler sera gestionnaire du fichier XML c'est à dire que c'est lui qui sera chargé
			 * des opérations de parsing. On vera cette classe en détails ci aprés.
			*/
			DefaultHandler handler = new PromoListParserXMLHandler();
			try {
				// On parse le fichier XML
				
				if(input==null)
					Log.e("erreur android","null");
				else{
					parseur.parse(input, handler);
					// On récupère directement la liste des feeds
					entries = ((PromoListParserXMLHandler) handler).getData();
					if (!((PromoListParserXMLHandler)handler).shouldBeOk() )
						entries = null;
				}
			} catch (SAXException e) {
				throw e;
			} catch (IOException e) {
				throw e;
			}
			return entries;
		}

	

}
