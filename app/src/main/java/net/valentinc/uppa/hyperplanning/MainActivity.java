package net.valentinc.uppa.hyperplanning;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    public static final String PREFS_NAME = "settings";
    public int progressBarStatus;
    public boolean connected = true;
    public Exception eTmp;
    private Spinner spPeriodes;
    private WebView webView;
    private Promotion thePromo;
    private ArrayList<Promotion> promoList;
    private ProgressDialog progressBar;
    private Handler progressBarHandler = new Handler();
    private ArrayList<Periode> periodeList;

    private ErrorHandler errorHandler;
    private boolean first = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*Rate the APP*/
        new AppRater(this)
                .setMinDays(7)
                .setMinLaunches(7)
                .setAppTitle("UPPA Planning")
                .init();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Export.exportCurrentView(getApplicationContext(), ((Periode) spPeriodes.getSelectedItem()).getImageCode() + "." + Cache.extension.png) == 0) {
                    Snackbar.make(view, "Planning enregistré sur le portable.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(view, "Erreur durant sauvegarde sur le portable.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        errorHandler = new ErrorHandler(this);

        this.spPeriodes = (Spinner) findViewById(R.id.spinner);
        this.webView = (WebView) findViewById(R.id.webView);
        this.webView.getSettings().setBuiltInZoomControls(true);
        this.webView.getSettings().setUseWideViewPort(true);
        this.spPeriodes.setOnItemSelectedListener(this);

        // Lecture des pref
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        this.thePromo = new Promotion(settings.getString("name", "null"),
                settings.getString("code", "null"));

        this.restorePromotion();
        this.ReUpdateSpinners();

        webView.setInitialScale(1);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        Log.i("INFO", "End Of Creation");
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            showAbout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAbout() {
        View messageView = getLayoutInflater().inflate(R.layout.about, null, false);

        TextView textView = (TextView) messageView.findViewById(R.id.AboutCredits);
        int defaultColor = textView.getTextColors().getDefaultColor();
        textView.setTextColor(defaultColor);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle(R.string.app_name);
        builder.setView(messageView);
        builder.create();
        builder.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_promotion) {
            showSettingsDialog();
        } else if (id == R.id.nav_reload) {
            Cache.clearCache(getApplicationContext());
            loadView(true);
        } else if (id == R.id.nav_ask) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            //i.setData(Uri.parse("valentindu64230@gmail.com"));
            i.putExtra(Intent.EXTRA_EMAIL, new String[] {"valentindu64230@gmail.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "Application UPPA");
            i.putExtra(Intent.EXTRA_TEXT, "");
            startActivity(Intent.createChooser(i, "Contacter moi par Mail"));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        showSettingsDialog();
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
        ArrayAdapter<Promotion> spinAdapter2 = new ArrayAdapter<>(
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

    public void loadView(boolean refresh) {
        webView.loadDataWithBaseURL(null,
                ((Periode) spPeriodes.getSelectedItem()).toHtml(), "text/html",
                "UTF-8", null);
        //store picture from here   http://sciences.univ-pau.fr/edt/diplomes/D0005395052S0000000000004.png
        if (Cache.DoesFileExist(getApplicationContext(), ((Periode) spPeriodes.getSelectedItem()).getImageCode(), Cache.extension.png) && !refresh) {
            try {
                Cache.updateWebView(getApplicationContext(), webView, ((Periode) spPeriodes.getSelectedItem()).getImageCode(), Cache.extension.png);
            } catch (CacheException cacheException) {
                cacheException.printStackTrace();
                Toast.makeText(getApplicationContext(), "Impossible de charger le fichier.", Toast.LENGTH_LONG).show();
            }
        } else {
            // save the file
            new Thread(new Runnable() {
                public void run() {
                    URL url = null;
                    try {
                        url = new URL("http://sciences.univ-pau.fr/edt/diplomes/" + ((Periode) spPeriodes.getSelectedItem()).getImageCode() + "." + Cache.extension.png);
                    } catch (MalformedURLException e1) {
                        e1.printStackTrace();
                    }

                    HttpURLConnection conn;

                    try {
                        assert url != null;
                        conn = (HttpURLConnection) url.openConnection();
                        if (conn.getResponseCode() == 200) {
                            Cache.AddInputStreamToCache(getApplicationContext(), conn.getInputStream(), ((Periode) spPeriodes.getSelectedItem()).getImageCode() + "." + Cache.extension.png);
                            Toast.makeText(getApplicationContext(), "Enregistré dans le cache.", Toast.LENGTH_SHORT).show();
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

    private void setTheCurrentPeriode(boolean refresh) {
        loadView(refresh);
    }

    private void restorePromotion() {
        if (this.thePromo.getCode().equalsIgnoreCase("null") || this.thePromo.getName().equalsIgnoreCase("null")) {
            this.thePromo.setCode("D0000000197");
            this.thePromo.setName("M1 Technologie de l'Internet");
            first = true;
        }

        //this.tvPromo.setText(this.thePromo.getName());

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
                    assert url != null;
                    conn = (HttpURLConnection) url.openConnection();

                    assert urlList != null;
                    connList = (HttpURLConnection) urlList.openConnection();
                    if (conn.getResponseCode() != 200 || connList.getResponseCode() != 200) {
                        eTmp = new ProxyException();
                        connected = false;
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                setTheCurrentPeriode(false);
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
                        Cache.AddInputStreamToCache(getApplicationContext(), conn.getInputStream(), "promo-" + thePromo.getCode() + ".xml");
                        promoList = MyXMLParser.getPromos(connList.getInputStream());
                        connList = (HttpURLConnection) urlList.openConnection();
                        Cache.AddInputStreamToCache(getApplicationContext(), connList.getInputStream(), "promolist.xml");
                    } else {
                        String PeriodeListName = "promo-" + thePromo.getCode();
                        String PromoListName = "promolist";

                        if (Cache.DoesFileExist(getApplicationContext(), PeriodeListName, Cache.extension.xml) && Cache.DoesFileExist(getApplicationContext(), PromoListName, Cache.extension.xml)) {

                            periodeList = MyXMLParser.getPromo(Cache.GetInputStreamFromCache(getApplicationContext(), PeriodeListName, Cache.extension.xml));
                            promoList = MyXMLParser.getPromos(Cache.GetInputStreamFromCache(getApplicationContext(), PromoListName, Cache.extension.xml));
                        }
                    }
                    if (periodeList == null || promoList == null)
                        throw new ProxyException();

                } catch (SAXException | ProxyException | IOException e) {
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
            ArrayAdapter<Periode> spinAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, this.thePromo.getPeriodes());
            spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            this.spPeriodes.setAdapter(spinAdapter);
            this.spPeriodes.setSelection(spinAdapter.getPosition(this.thePromo.getCurrentPeriode()));
        }
    }

    @Override
    protected void onStop() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("code", this.thePromo.getCode());
        editor.putString("name", this.thePromo.getName());

        editor.apply();
        super.onStop();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        loadView(false);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Useless
    }
}
