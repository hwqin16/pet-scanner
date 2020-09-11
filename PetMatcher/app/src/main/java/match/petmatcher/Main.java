package match.petmatcher;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class Main extends Activity{
    private Bitmap[] bits = new Bitmap[25];
    private String[] descriptions = new String[25];
    int i = 0;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageView = (ImageView) findViewById(R.id.petView);
        new getURLs().execute("test");

        // Gesture detection
        gestureDetector = new GestureDetector(this, new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
        TextView text = (TextView) findViewById(R.id.text_view_id);
        text.setOnTouchListener(gestureListener);
        imageView.setOnTouchListener(gestureListener);

    }

    class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            System.out.println("FLING:");
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    System.out.println("FLINGL");


                    ImageView imageView = (ImageView) findViewById(R.id.petView);
                    TextView text = (TextView) findViewById(R.id.text_view_id);
                    text.setMovementMethod(new ScrollingMovementMethod());
                    text.setTextColor(Color.BLACK);


                    text.setText(descriptions[i]);
                    imageView.setImageBitmap(bits[i]);
                    i++;
                    if(i > 24){
                        i = 0;
                    }
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    System.out.println("FLINGR");
                    i = i-2;
                    ImageView imageView = (ImageView) findViewById(R.id.petView);
                    TextView text = (TextView) findViewById(R.id.text_view_id);
                    text.setMovementMethod(new ScrollingMovementMethod());
                    text.setTextColor(Color.BLACK);


                    text.setText(descriptions[i]);
                    imageView.setImageBitmap(bits[i]);
                    i++;
                    if(i > 24){
                        i = 0;
                    }
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }



        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


    private class getURLs extends AsyncTask<String, Void, String> {


        public getURLs() {

        }

        protected String doInBackground(String... url) {

            try{

                URL line_api_url = new URL("http://api.petfinder.com/pet.find?key=ae6db4bcae8c83d347686b8a79859150&animal=dog&location=92129&offset=50");
                HttpURLConnection linec = (HttpURLConnection) line_api_url.openConnection();
                linec.setDoInput(true);
                linec.setDoOutput(true);
                linec.setRequestMethod("GET");



                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                linec.getInputStream()));
                String inputLine;
                String description = "";
                int flag = 0;
                int index = 0;
                while ((inputLine = in.readLine()) != null) {

                    if(inputLine.contains("<![CDATA[") ){
                        description = inputLine.substring(inputLine.indexOf("<![CDATA[") +9 );
                        flag = 1;
                    }
                    if(flag == 1 && !inputLine.contains("<![CDATA[")  && !inputLine.contains("]]")){
                        description = description + inputLine;
                    }
                    if(inputLine.contains("]]")){
                        flag = 0;
                    }


                    if(inputLine.contains("size=\"x\"") && inputLine.contains("id=\"1\"")){
                        System.out.println(description);
                        String u = inputLine.substring(inputLine.indexOf("size=\"x\">") + 9, inputLine.indexOf("</photo>"));
                        Bitmap mIcon11 = null;
                        try {
                            InputStream input = new java.net.URL(u).openStream();
                            mIcon11 = BitmapFactory.decodeStream(input);
                            bits[index] = mIcon11;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println(u);
                        descriptions[index] = description;
                        index++;
                        description = "";

                    }

                }
                System.out.println("TEST");
                in.close();
            }catch(Exception e){
                System.out.println(e.getMessage());
            }

            return "Test";
        }


    }

    public void nextValue(View view){
        ImageView imageView = (ImageView) findViewById(R.id.petView);
        TextView text = (TextView) findViewById(R.id.text_view_id);
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setTextColor(Color.BLACK);


        text.setText(descriptions[i]);
        imageView.setImageBitmap(bits[i]);
        i++;
        if(i > 24){
            i = 0;
        }

    }
}
