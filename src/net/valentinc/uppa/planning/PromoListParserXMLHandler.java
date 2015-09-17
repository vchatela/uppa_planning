package net.valentinc.uppa.planning;


import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PromoListParserXMLHandler extends DefaultHandler {

	// nom des tags XML
	private final String PROMO = "promotion";
	private final String LAST = "lastupdate";
	private final String CODE = "code";
	private final String NOM = "nom";

	private boolean shouldOk = false;
	// Array list de feeds
	private ArrayList<Promotion> entries;

	// Boolean permettant de savoir si nous sommes à l'intérieur d'un item
	private boolean inItem;

	// Feed courant
	private Promotion currentFeed;

	// Buffer permettant de contenir les données d'un tag XML
	private StringBuffer buffer;

	@Override
	public void processingInstruction(String target, String data) throws SAXException {
		super.processingInstruction(target, data);
	}

	public boolean shouldBeOk() {
		return shouldOk;
	} 
	
	public PromoListParserXMLHandler() {
		super();
	}

	// * Cette méthode est appelée par le parser une et une seule
	// * fois au démarrage de l'analyse de votre flux xml.
	// * Elle est appelée avant toutes les autres méthodes de l'interface,
	// * à l'exception unique, évidemment, de la méthode setDocumentLocator.
	// * Cet événement devrait vous permettre d'initialiser tout ce qui doit
	// * l'étre avant ledébut du parcours du document.

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		entries = new ArrayList<Promotion>();

	}

	/*
	 * Fonction étant déclenchée lorsque le parser trouve un tag XML
	 * C'est cette méthode que nous allons utiliser pour instancier un nouveau feed
 	*/
	@Override
	public void startElement(String uri, String localName, String name,	Attributes attributes) throws SAXException {
		// Nous réinitialisons le buffer a chaque fois qu'il rencontre un item
		buffer = new StringBuffer();		

		// Ci dessous, localName contient le nom du tag rencontré

		// Nous avons rencontré un tag ITEM, il faut donc instancier un nouveau feed
		if (localName.equalsIgnoreCase(PROMO)){
			this.currentFeed = new Promotion();
			inItem = true;
		}

		// Vous pouvez définir des actions à effectuer pour chaque item rencontré
		if (localName.equalsIgnoreCase(CODE)){
			// Nothing to do
		}
	}

	// * Fonction étant déclenchée lorsque le parseràparsé
	// * l'intérieur de la balise XML La méthode characters
	// * a donc fait son ouvrage et tous les caractére inclus
	// * dans la balise en cours sont copiés dans le buffer
	// * On peut donc tranquillement les récupérer pour compléter
	// * notre objet currentFeed

	@Override
	public void endElement(String uri, String localName, String name) throws SAXException {		

		if (localName.equalsIgnoreCase(CODE)){
			if(inItem){
				// Les caractéres sont dans l'objet buffer
				this.currentFeed.setCode(buffer.toString());
				buffer = null;
			}
		}
		if (localName.equalsIgnoreCase(NOM)){
			if(inItem){
				this.currentFeed.setName(buffer.toString());
				buffer = null;
			}
		}

		if (localName.equalsIgnoreCase(PROMO)){
			entries.add(currentFeed);
			inItem = false;
		}
		if (localName.equalsIgnoreCase(LAST)){
			shouldOk = true;
			
		}
	}

	// * Tout ce qui est dans l'arborescence mais n'est pas partie
	// * intégrante d'un tag, déclenche la levée de cet événement.
	// * En général, cet événement est donc levé tout simplement
	// * par la présence de texte entre la balise d'ouverture et
	// * la balise de fermeture

	public void characters(char[] ch,int start, int length)	throws SAXException{
		String lecture = new String(ch,start,length);
		if(buffer != null) buffer.append(lecture);
	}

	// cette méthode nous permettra de récupérer les données
	public ArrayList<Promotion> getData(){
		return entries;
	}
}
