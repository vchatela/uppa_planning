package net.valentinc.uppa.planning;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

//TODO : implement it

public abstract class HttpConnection {
    private HttpURLConnection http;
    private boolean error;

    public enum url{
//TODO
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public HttpConnection() {
        this.error = false;
    }

    public String getContent(String url1) {

        String tmp = "";
        try {

            URL urlPage = new URL(url1);

            HttpURLConnection connection = (HttpURLConnection) urlPage
                    .openConnection();

            InputStream inputStream = connection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream));

            StringBuffer stringBuffer = new StringBuffer();

            String ligne;

            while ((ligne = bufferedReader.readLine()) != null) {

                stringBuffer.append(ligne);

                if (!bufferedReader.ready()) {
                    break;
                }

            }

            tmp = stringBuffer.toString();

            connection.disconnect();

            bufferedReader.close();

            inputStream.close();


        } catch (IOException e) {
            this.setError(true);
            e.printStackTrace();
            return null;

        }
        this.setError(false);
        return tmp;

    }

    public int getContentLenght(String url) {

        int conLen = 0;
        try {

            URL urlPage = new URL(url);

            HttpURLConnection connection = (HttpURLConnection) urlPage
                    .openConnection();

            conLen = connection.getContentLength();

            connection.disconnect();

        } catch (IOException e) {
            this.setError(true);
            e.printStackTrace();
            return 0;

        }
        this.setError(false);
        return conLen;
    }

    public Calendar getLastModified(String url) {
        Calendar lastMod = Calendar.getInstance();
        try {

            URL urlPage = new URL(url);

            HttpURLConnection connection = (HttpURLConnection) urlPage
                    .openConnection();

            lastMod.setTimeInMillis(connection.getLastModified());

            connection.disconnect();

        } catch (IOException e) {
            this.setError(true);
            e.printStackTrace();
            return lastMod;

        }
        this.setError(false);
        return lastMod;
    }

}
