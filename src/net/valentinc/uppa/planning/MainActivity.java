package net.valentinc.uppa.planning;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static net.valentinc.uppa.planning.Cache.*;
import static net.valentinc.uppa.planning.Export.exportCurrentView;

public class MainActivity extends Activity implements OnClickListener,
        OnItemSelectedListener {

    public static final String PREFS_NAME = "settings";
    private final static int CODE_SETTINGS = 1;
    public int progressBarStatus;

    public boolean connected = true;
    public Exception eTmp;
    private TextView tvPromo;
    private Spinner spPeriodes;
    private ImageButton bSettings;
    private WebView webView;
    private Promotion thePromo;
    private ArrayList<Promotion> promoList;
    private ProgressDialog progressBar;
    private Handler progressBarHandler = new Handler();
    private boolean CacheAllowed = true;
    private ArrayList<String> files;
    private ArrayList<Periode> periodeList;

    private ErrorHandler errorHandler;
    private boolean first = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        errorHandler = new ErrorHandler(this);

        // Initialisation des elements UI
        this.tvPromo = (TextView) findViewById(R.id.tv_promo);
        this.spPeriodes = (Spinner) findViewById(R.id.spinner1);
        this.bSettings = (ImageButton) findViewById(R.id.imageButton1);
        this.webView = (WebView) findViewById(R.id.webView1);
        this.webView.getSettings().setBuiltInZoomControls(true);
        this.webView.getSettings().setUseWideViewPort(true);
        this.bSettings.setOnClickListener(this);
        findViewById(R.id.refreshButton).setOnClickListener(this);
        findViewById(R.id.exportButton).setOnClickListener(this);
        // TODO findViewById(R.id.resetCache).setOnClickListener(this);
        this.spPeriodes.setOnItemSelectedListener(this);

        // Lecture des pref
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        this.thePromo = new Promotion(settings.getString("name", "null"),
                settings.getString("code", "null"));

        this.restorePromotion();
        this.ReUpdateSpinners();
        Log.i("INFO", "End Of Creation");
    }

    private void setTheCurrentPeriode(boolean refresh) {
        loadView(refresh);
    }

    private void restorePromotion() {
        if (this.thePromo.getCode().equalsIgnoreCase("null") || this.thePromo.getName().equalsIgnoreCase("null")) {
            this.thePromo.setCode("D0000000197");
            this.thePromo.setName("M1 Technologie de l'Internet");
            first = true;
        }

        this.tvPromo.setText(this.thePromo.getName());

        // prepare for a progress bar dialog
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setMessage("Recupération du planning ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();

        // reset progress bar status
        progressBarStatus = 0;

        Thread t = new Thread(new Runnable() {
            public void run() {

                URL url = null, urlList = null;
                try {
                    url = new URL("http://www.irokwa.net/uppa/hp/promo-"
                            + MainActivity.this.thePromo.getCode() + ".xml");
                    urlList = new URL(
                            "http://www.irokwa.net/uppa/hp/promolist.xml");

                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                }

                HttpURLConnection conn = null;
                HttpURLConnection connList = null;

                try {
                    conn = (HttpURLConnection) url.openConnection();

                    connList = (HttpURLConnection) urlList.openConnection();
                    if (conn.getResponseCode() != 200 || connList.getResponseCode() != 200) {
                        eTmp = new ProxyException();
                        connected = false;
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                // If cache authorized - instead of closing we can take the value from the cache
                                if (CacheAllowed) {
                                    setTheCurrentPeriode(false);
                                } else {
                                    errorHandler.newException(eTmp);
                                }
                            }
                        });
                        return;
                    } else connected = true;
                } catch (IOException e1) {
                    e1.printStackTrace();
                    connected = false;
                }
                try {
                    if (connected) {
                        periodeList = MyXMLParser.getPromo(conn.getInputStream());
                        conn = (HttpURLConnection) url.openConnection();
                        AddInputStreamToCache(getApplicationContext(), conn.getInputStream(), "promo-" + thePromo.getCode() + ".xml");
                        promoList = MyXMLParser.getPromos(connList.getInputStream());
                        connList = (HttpURLConnection) urlList.openConnection();
                        AddInputStreamToCache(getApplicationContext(), connList.getInputStream(), "promolist.xml");
                    } else {
                        String PeriodeListName = "promo-" + thePromo.getCode();
                        String PromoListName = "promolist";

                        files = GetListOfFileInFolder(getCacheDir());

                        if (DoesFileExist(getApplicationContext(), PeriodeListName, extension.xml) && DoesFileExist(getApplicationContext(), PromoListName, extension.xml)) {

                            periodeList = MyXMLParser.getPromo(GetInputStreamFromCache(getApplicationContext(), PeriodeListName, extension.xml));
                            promoList = MyXMLParser.getPromos(GetInputStreamFromCache(getApplicationContext(), PromoListName, extension.xml));
                        }
                    }
                    if (periodeList == null || promoList == null)
                        throw new ProxyException();

                } catch (SAXException e) {
                    eTmp = e;
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            errorHandler.newException(eTmp);
                        }
                    });

                } catch (IOException e) {
                    eTmp = e;
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            errorHandler.newException(eTmp);
                        }
                    });

                } catch (ProxyException e) {
                    eTmp = e;
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            errorHandler.newException(eTmp);
                        }
                    });
                }

                if (periodeList != null && promoList != null)
                    progressBarHandler.post(new Runnable() {
                        public void run() {
                            thePromo.setPeriodes(periodeList);
                            ReUpdateSpinners();
                            if (first) {
                                showSettingsDialog();
                                first = false;
                            }
                        }
                    });
                progressBar.dismiss();
            }
        });
        t.start();
        //Wait end of t
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void ReUpdateSpinners() {
        if (this.thePromo != null) {
            ArrayAdapter<Periode> spinAdapter = new ArrayAdapter<Periode>(this, android.R.layout.simple_spinner_item, this.thePromo.getPeriodes());
            spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            this.spPeriodes.setAdapter(spinAdapter);
            this.spPeriodes.setSelection(spinAdapter.getPosition(this.thePromo.getCurrentPeriode()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                               long arg3) {
        loadView(false);
    }

    public void loadView(boolean refresh) {
        webView.loadDataWithBaseURL(null,
                ((Periode) spPeriodes.getSelectedItem()).toHtml(), "text/html",
                "UTF-8", null);
        //store picture from here   http://sciences.univ-pau.fr/edt/diplomes/D0005395052S0000000000004.png
        if (DoesFileExist(getApplicationContext(), ((Periode) spPeriodes.getSelectedItem()).getImageCode(), extension.png) && !refresh) {
            try {
                updateWebView(getApplicationContext(), webView, ((Periode) spPeriodes.getSelectedItem()).getImageCode(), extension.png);
            } catch (CacheException cacheException) {
                cacheException.printStackTrace();
                Toast.makeText(getApplicationContext(),"Impossible de charger le fichier.",Toast.LENGTH_LONG).show();
            }
            return;
        } else {
            // save the file
            new Thread(new Runnable() {
                public void run() {
                    URL url = null;
                    try {
                        url = new URL("http://sciences.univ-pau.fr/edt/diplomes/" + ((Periode) spPeriodes.getSelectedItem()).getImageCode() + "." + extension.png);
                    } catch (MalformedURLException e1) {
                        e1.printStackTrace();
                    }

                    HttpURLConnection conn;

                    try {
                        conn = (HttpURLConnection) url.openConnection();
                        if (conn.getResponseCode() == 200) {
                            AddInputStreamToCache(getApplicationContext(), conn.getInputStream(), ((Periode) spPeriodes.getSelectedItem()).getImageCode() + "." + extension.png);
                        } else {
                            Toast.makeText(getApplicationContext(), "Erreur réseau et fichier non présent dans le cache.", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e("ERROR", e.getMessage());
                    }
                }
            }).start();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Useless
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButton1:
                showSettingsDialog();
                break;
            /*case R.id.resetCache:
            //TODO update
                ResetCache(getApplicationContext());
                break;*/
            case R.id.refreshButton:
                loadView(true);
                break;
            case R.id.exportButton:
                if(exportCurrentView(getApplicationContext(),((Periode) spPeriodes.getSelectedItem()).getImageCode() + "." + extension.png)==0){
                    Toast.makeText(getApplicationContext(),"Sauvegardé dans \"Documents\".",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Erreur durant la sauvegarde.",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void showSettingsDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View alertDialogView = factory.inflate(R.layout.dialog_settings,
                null);

        AlertDialog.Builder adb = new AlertDialog.Builder(this);

        adb.setView(alertDialogView);
        adb.setTitle("Réglages");
        adb.setIcon(android.R.drawable.ic_menu_manage);
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Spinner sp = (Spinner) alertDialogView
                        .findViewById(R.id.spinner2);
                thePromo = (Promotion) sp.getSelectedItem();
                restorePromotion();
            }
        });

        adb.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        adb.show();
        Spinner sp = (Spinner) alertDialogView.findViewById(R.id.spinner2);
        ArrayAdapter<Promotion> spinAdapter2 = new ArrayAdapter<Promotion>(
                this, android.R.layout.simple_spinner_item, this.promoList);
        spinAdapter2
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (this.promoList != null)
            sp.setAdapter(spinAdapter2);

        for (Promotion aPromo : promoList) {
            if (aPromo.equals(this.thePromo)) {
                Log.v("SPIN", "Position :" + spinAdapter2.getPosition(aPromo));
                sp.setSelection(spinAdapter2.getPosition(aPromo));
                break;
            }

        }
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            Bundle extras = intent.getExtras();
            if (extras.containsKey("promo")) {
                this.thePromo = (Promotion) extras.getSerializable("promo");
                restorePromotion();
            }
        }
    }

    @Override
    protected void onStop() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("code", this.thePromo.getCode());
        editor.putString("name", this.thePromo.getName());

        editor.commit();
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                showSettingsDialog();
                return true;
        }
        return false;
    }
}