package net.valentinc.uppa.hyperplanning;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;
/**
 * Created by valentinc on 16/09/2015.
 * XML parser to help request
 */

public class MyXMLParser {

		public static ArrayList getPromo(InputStream input) throws SAXException, IOException{
			// On passe par une classe factory pour obtenir une instance de sax
			SAXParserFactory fabrique = SAXParserFactory.newInstance();
			SAXParser parseur = null;
			ArrayList entries = null;
			try {
				// On "fabrique" une instance de SAXParser
				parseur = fabrique.newSAXParser();
			} catch (ParserConfigurationException | SAXException e) {
				e.printStackTrace();
			}
			
			/*
			 * Le handler sera gestionnaire du fichier XML c'est à dire que c'est lui qui sera chargé
			 * des opérations de parsing. On vera cette classe en détails ci aprés.
			*/
			DefaultHandler handler = new PromoParserXMLHandler();
			if(input==null)
                Log.e("ERROR","Parsing inputStream null");
            else{
                assert parseur != null;
                parseur.parse(input, handler);
                // On récupére directement la liste des feeds
                entries = ((PromoParserXMLHandler) handler).getData();
                if (!((PromoParserXMLHandler)handler).shouldBeOk() )
                    entries = null;
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
			} catch (ParserConfigurationException | SAXException e) {
				e.printStackTrace();
			}

			/*
			 * Le handler sera gestionnaire du fichier XML c'est à dire que c'est lui qui sera chargé
			 * des opérations de parsing. On vera cette classe en détails ci aprés.
			*/
			DefaultHandler handler = new PromoListParserXMLHandler();
			if(input==null)
                Log.e("erreur android","null");
            else{
                assert parseur != null;
                parseur.parse(input, handler);
                // On récupère directement la liste des feeds
                entries = ((PromoListParserXMLHandler) handler).getData();
                if (!((PromoListParserXMLHandler)handler).shouldBeOk() )
                    entries = null;
            }
			return entries;
		}

	

}
