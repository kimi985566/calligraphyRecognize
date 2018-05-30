package yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils;

import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class JSONUtils {
    private static final int TIMEOUT = 8000;
    private static final int CONNECT_TIMEOUT = 10000;
    private static final String GET = "GET";
    private static final String UTF_8 = "UTF-8";
    public static final String TAG = JSONUtils.class.getSimpleName();

    public static void getImage(final String url, final android.os.Handler handler) {

        Thread thread = new Thread(new Runnable() {

            private BufferedReader mBufferedReader;
            private InputStream mInputStream;
            private HttpURLConnection mHttpURLConnection;

            @Override
            public void run() {
                try {
                    URL http_url = new URL(url);
                    mHttpURLConnection = (HttpURLConnection) http_url.openConnection();
                    mHttpURLConnection.setRequestMethod(GET);
                    mHttpURLConnection.setReadTimeout(TIMEOUT);
                    mHttpURLConnection.setConnectTimeout(CONNECT_TIMEOUT);
                    mInputStream = mHttpURLConnection.getInputStream();
                    mBufferedReader = new BufferedReader(new InputStreamReader(mInputStream));
                    String strRead = null;
                    StringBuilder result = new StringBuilder();

                    while ((strRead = mBufferedReader.readLine()) != null) {
                        result.append(strRead);
                    }

                    Message msg = new Message();
                    msg.obj = result.toString();
                    handler.sendMessage(msg);

                    mBufferedReader.close();
                    mInputStream.close();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        thread.start();
    }
}
