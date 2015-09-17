package net.valentinc.uppa.planning;

import java.io.IOException;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class ErrorHandler {
	private final static String NET = "Connectivité réseau - Pensez a autoriser le cache.";
	private final static String PARSE = "Données reçues corrompues.";
	private final static String PROXY = "Impossible de récupérer le planning. Si vous étes sur un hot-spot Wifi, vérifiez que vous êtes bien identifié.";

	private Activity context;

	public ErrorHandler(Activity context) {
		this.context = context;
	}

	public void newException(Exception e) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set title
		alertDialogBuilder.setTitle("Erreur");

		// set dialog message
		if (e instanceof IOException)
			alertDialogBuilder.setMessage(NET);
		if(e instanceof CacheException)
            alertDialogBuilder.setMessage(e.toString());
		if (e instanceof SAXException)
			alertDialogBuilder.setMessage(PROXY);
		if (e instanceof ProxyException)
			alertDialogBuilder.setMessage(PROXY);
		
		
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setPositiveButton("Quiter",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						context.finish();
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
		e.printStackTrace();
		
	}

}
class ProxyException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
}

class CacheException extends Exception {
	private String info;

	CacheException(String message){
		info = message;
	}

	@Override
	public String toString() {
		return info;
	}
}
