package com.example.dilrajsingh.compiler;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import org.apache.commons.lang3.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.gesture.GestureStroke;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    EditText editext;
    Spinner spinner;
    TextView textView;
    String lang, code, tcases = "", result = "", message = "not compiled";
    int flang = 0;
    Button button, button2;
    Boolean flag = false;
    ProgressDialog pd;
    JSONObject cresult;
    View help;
    public static final String PREFS_NAME = "MyPrefsFile1";
    public CheckBox dontShowAgain;
    private static final Pattern REGEX_PATTERN =
            Pattern.compile("[^\\x20-\\x7E]");
    ImageView imageView, imageView2, imageView3, imageView4, imageView5, imageView6, imageView7;
    TextView text, text2, text3, text4, text5, text6, text7;


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        show();
        editext = (EditText) findViewById(R.id.editText);
        spinner = (Spinner) findViewById(R.id.spinner);
        textView = (TextView) findViewById(R.id.textView);
        textView.setMovementMethod(new ScrollingMovementMethod());
        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        pd = new ProgressDialog(MainActivity.this);
        pd.setTitle("Compiling source");
        pd.setCancelable(false);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        if(!isNetworkAvailable()){
            Toast.makeText(MainActivity.this, "Please check your network connection", Toast.LENGTH_SHORT).show();
        }
        spinner.setSelection(-1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lang = spinner.getSelectedItem().toString();
                getLang(lang);
                setCode(lang);
                textView.setText("Your output here");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(MainActivity.this, "Please select a language", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void show() {
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        LayoutInflater adbInflater = LayoutInflater.from(this);
        View eulaLayout = adbInflater.inflate(R.layout.check, null);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String skipMessage = settings.getString("skipMessage", "NOT checked");
        LayoutInflater lp = LayoutInflater.from(MainActivity.this);
        help = lp.inflate(R.layout.activity_help, null);
        imageView = (ImageView) help.findViewById(R.id.imageView);
        imageView2 = (ImageView) help.findViewById(R.id.imageView2);
        imageView3 = (ImageView) help.findViewById(R.id.imageView3);
        imageView4 = (ImageView) help.findViewById(R.id.imageView4);
        imageView5 = (ImageView) help.findViewById(R.id.imageView5);
        imageView6 = (ImageView) help.findViewById(R.id.imageView6);
        imageView7 = (ImageView) help.findViewById(R.id.imageView7);
        Bitmap bmp1 = BitmapFactory.decodeResource(getResources(), R.drawable.a);
        Bitmap newbmp1 = Bitmap.createScaledBitmap(bmp1, screenWidth, screenHeight + 23*16/9, true);
        imageView.setImageBitmap(newbmp1);
        //bmp1.recycle();
        //newbmp1.recycle();
        Bitmap bmp2 = BitmapFactory.decodeResource(getResources(), R.drawable.b);
        bmp2 = Bitmap.createScaledBitmap(bmp2, screenWidth, screenHeight + 23*16/9, true);
        imageView2.setImageBitmap(bmp2);
        //bmp2.recycle();
        //newbmp2.recycle();
        Bitmap bmp3 = BitmapFactory.decodeResource(getResources(), R.drawable.c);
        bmp3 = Bitmap.createScaledBitmap(bmp3, screenWidth, screenHeight + 23*16/9, true);
        imageView3.setImageBitmap(bmp3);
        //bmp3.recycle();
        //newbmp3.recycle();
        Bitmap bmp4 = BitmapFactory.decodeResource(getResources(), R.drawable.d);
        bmp4 = Bitmap.createScaledBitmap(bmp4, screenWidth, screenHeight + 23*16/9, true);
        imageView4.setImageBitmap(bmp4);
        //bmp4.recycle();
        //newbmp4.recycle();
        Bitmap bmp5 = BitmapFactory.decodeResource(getResources(), R.drawable.e);
        bmp5 = Bitmap.createScaledBitmap(bmp5, screenWidth, screenHeight + 23*16/9, true);
        imageView5.setImageBitmap(bmp5);
        //bmp5.recycle();
        //newbmp5.recycle();
        Bitmap bmp6 = BitmapFactory.decodeResource(getResources(), R.drawable.f);
        bmp6 = Bitmap.createScaledBitmap(bmp6, screenWidth, screenHeight + 23*16/9, true);
        imageView6.setImageBitmap(bmp6);
        //bmp6.recycle();
        //newbmp6.recycle();
        Bitmap bmp7 = BitmapFactory.decodeResource(getResources(), R.drawable.g);
        bmp7 = Bitmap.createScaledBitmap(bmp7, screenWidth, screenHeight + 23*16/9, true);
        imageView7.setImageBitmap(bmp7);
        //bmp7.recycle();
        //newbmp7.recycle();
        text = (TextView) help.findViewById(R.id.textView2);
        text2 = (TextView) help.findViewById(R.id.textView3);
        text3 = (TextView) help.findViewById(R.id.textView4);
        text4 = (TextView) help.findViewById(R.id.textView5);
        text5 = (TextView) help.findViewById(R.id.textView6);
        text6 = (TextView) help.findViewById(R.id.textView7);
        text7 = (TextView) help.findViewById(R.id.textView8);
        text.setText("1. Select a language first, because any language selected will give you a basic framework for writing code");
        text2.setText("2. Enter your code");
        text3.setText("3. Click the compile button to compile and execute your code. Enter the testcases, leave it blank for no testcases");
        text4.setText("4. Hit Compile");
        text5.setText("5. If your code compiled and executed successfully, you can run your code");
        text6.setText("6. Else, you will be acknowledged with the error");
        text7.setText("8. If compiled successfully, run the code and enjoy the app");
        dontShowAgain = (CheckBox) help.findViewById(R.id.skip);
        adb.setView(help);
        adb.setTitle("Help");
        /*adb.setMessage("1. Select a language first, because any language selected will give you a basic framework for writing code\n" +
                "\n" +
                "2. Enter your code\n" +
                "\n" +
                "3. Click the compile button to compile and execute your code\n" +
                "\n" +
                "4. Enter the testcases, leave it blank for no testcases.\n" +
                "\n" +
                "5. Hit Compile\n" +
                "\n" +
                "6. If your code compiled and executed successfully, you can run your code\n" +
                "\n" +
                "7. Else, you will be acknowledged with the error\n" +
                "\n" +
                "8. If compiled successfully, run the code and enjoy the app");*/

        adb.setPositiveButton("Got It", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String checkBoxResult = "NOT checked";

                if (dontShowAgain.isChecked()) {
                    checkBoxResult = "checked";
                }

                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();

                editor.putString("skipMessage", checkBoxResult);
                editor.commit();

                return;
            }
        });

        if (!skipMessage.equals("checked")) {
            adb.show();
        }

    }

    protected boolean isEmpty(EditText editText){
        return editText.getText().toString().trim().length() == 0;
    }

    public void onCompile(View view){
        if(isEmpty(editext)){
            Toast.makeText(MainActivity.this, "Please enter the code", Toast.LENGTH_SHORT).show();
            textView.setText("");
        }
        else{
            AlertDialog.Builder al = new AlertDialog.Builder(MainActivity.this);
            al.setTitle("Enter the input data");
            final EditText cases = new EditText(MainActivity.this);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cases.setLayoutParams(lp);
            cases.setHint("Enter the test cases");
            LinearLayout ll = new LinearLayout(MainActivity.this);
            ll.addView(cases);
            al.setView(ll);
            al.setPositiveButton("Compile", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    tcases = cases.getText().toString();
                    code = editext.getText().toString();
                    if(!isNetworkAvailable()){
                        Toast.makeText(MainActivity.this, "Please check your network connection", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        new Compile().execute(code);
                    }
                }
            });
            al.setCancelable(false);
            al.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            if(!isNetworkAvailable()){
                Toast.makeText(MainActivity.this, "Please check your network connection", Toast.LENGTH_SHORT).show();
            }
            else{
                al.show();
            }
        }
    }

    private class Compile extends AsyncTask<String, Void, Void>{

        BufferedReader reader = null;
        int response;

        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://api.hackerrank.com/checker/submission.json");
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("api_key", "hackerrank|951206-1035|75ea1f00e75d0ffc4d606e17cb16dfce682cb6eb"));
                nameValuePairs.add(new BasicNameValuePair("format", "json"));
                nameValuePairs.add(new BasicNameValuePair("source", params[0]));
                nameValuePairs.add(new BasicNameValuePair("lang", String.valueOf(flang)));
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(tcases);
                String bg = jsonArray.toString();
                nameValuePairs.add(new BasicNameValuePair("testcases", bg));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                response = httpResponse.getStatusLine().getStatusCode();
                InputStream is = null;
                result = "{\"result\":{\"callback_url\":\"\",\"censored_compile_message\":\"\"," +
                        "\"censored_stderr\":[\"\"],\"\":\"\"," +
                        "\"compile_command\":\"\",\"compilemessage\":\"\",\"created_at\":\"\"," +
                        "\"diff_status\":[0],\"error_code\":0,\"hash\":\"run-\"," +
                        "\"memory\":[0],\"message\":[\"\"],\"response_s3_path\":\"\"," +
                        "\"result\":0,\"run_command\":\"\",\"server\":\"\",\"signal\":[0]," +
                        "\"stderr\":[false],\"stdout\":[\"\"],\"time\":[0]}}";
                if(response==200){
                    is = httpResponse.getEntity().getContent();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    StringBuilder sb = new StringBuilder();
                    String temp = null;
                    while((temp = br.readLine())!=null){
                        sb.append(temp);
                    }
                    result = sb.toString();
                }
                else{
                    result = "URL connection error";
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally
            {
                try
                {
                    reader.close();
                }

                catch(Exception ex) {}
            }
            try {
                JSONObject temp = new JSONObject(result);
                if(temp==null){
                    result = "{\"result\":{\"callback_url\":\"\",\"censored_compile_message\":\"\"," +
                            "\"censored_stderr\":[\"\"],\"\":\"\"," +
                            "\"compile_command\":\"\",\"compilemessage\":\"some_message\",\"created_at\":\"\"," +
                            "\"diff_status\":[0],\"error_code\":0,\"hash\":\"run-\"," +
                            "\"memory\":[0],\"message\":[\"\"],\"response_s3_path\":\"\"," +
                            "\"result\":0,\"run_command\":\"\",\"server\":\"\",\"signal\":[0]," +
                            "\"stderr\":[false],\"stdout\":[\"\"],\"time\":[0]}}";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.dismiss();
            JSONObject temp = new JSONObject();
            if(result.equals("URL connection error")){
                Toast.makeText(MainActivity.this, "Error code : " + String.valueOf(response), Toast.LENGTH_SHORT).show();
            }
            else{
                try {
                    temp = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                catch (NullPointerException e){
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error code : " + String.valueOf(response), Toast.LENGTH_SHORT).show();
                }
                try {
                    cresult = temp.getJSONObject("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                catch (NullPointerException e){
                    e.printStackTrace();
                }
                try {
                    message = cresult.getString("compilemessage");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                catch (NullPointerException e){
                    e.printStackTrace();
                }
                if(message.equals("")){
                    Toast.makeText(MainActivity.this, "Compiled successfully", Toast.LENGTH_SHORT).show();
                    textView.setText("Your output here");
                    button2.setEnabled(true);
                }
                else{
                    message = message.replaceAll("\\u00e2\\u0080\\u0098", "");
                    message = message.replaceAll("\\u00e2\\u0080\\u0099", "");
                    textView.setText(message);
                    button2.setEnabled(false);
                }
            }
        }
    }

    public void onExecute(View view){
        if(message.equals("not compiled")){
            Toast.makeText(MainActivity.this, "Source needs to be compiled first", Toast.LENGTH_SHORT).show();
        }
        else{
            new exec().execute(result);
        }
    }

    public class exec extends AsyncTask<String, Void, Void>{

        JSONObject js = new JSONObject();
        JSONObject mem = new JSONObject();
        JSONObject mym = new JSONObject();
        String memory = "", time = "", out = "";

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            try {
                js = new JSONObject(params[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                mem = js.getJSONObject("result");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                memory = mem.getString("memory");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                time = mem.getString("time");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                JSONArray jArray = new JSONArray();
                JSONArray message = new JSONArray();
                message = mem.getJSONArray("message");
                if(message.getString(0).equals("Success")){
                    jArray = mem.getJSONArray("stdout");
                    out = jArray.getString(0);
                    out = out.replaceAll("\\u00e2", "");
                }
                else{
                    out =  message.getString(0) + "\n" + mem.getJSONArray("stderr").getString(0);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(MainActivity.this, "Memory : " + memory.replace("[", "").replace("]", "") + "\nTime : " + time.replace("[", "").replace("]", ""), Toast.LENGTH_SHORT).show();
            textView.setText(out);
        }
    }

    public void getLang(String language){
        switch(language){
            case "C":
                flang = 1; break;
            case "C++":
                flang = 2; break;
            case "Java":
                flang = 3; break;
            case "Python":
                flang = 5; break;
            case "Perl":
                flang = 6; break;
            case "Php":
                flang = 7; break;
            case "Ruby":
                flang = 8; break;
            case "C#":
                flang = 9; break;
            case "MySql":
                flang = 10; break;
            case "Oracle":
                flang = 11; break;
            case "Haskell":
                flang = 12; break;
            case "Clojure":
                flang = 13; break;
            case "Bash":
                flang = 14; break;
            case "Scala":
                flang = 15; break;
            case "Erlang":
                flang = 16; break;
            case "Lua":
                flang = 18; break;
            case "JavaScript":
                flang = 20; break;
            case "Go":
                flang = 21; break;
            case "D":
                flang = 22; break;
            case "Ocaml":
                flang = 23; break;
            case "R":
                flang = 24; break;
            case "Pascal":
                flang = 25; break;
            case "Common Lisp (SBCL)":
                flang = 26; break;
            case "Python 3":
                flang = 30; break;
            case "Groovy":
                flang = 31; break;
            case "Objective C":
                flang = 32; break;
            case "F#":
                flang = 33; break;
            case "Cobol":
                flang = 36; break;
            case "VisualBasic":
                flang = 37; break;
            case "LolCode":
                flang = 38; break;
            case "SmallTalk":
                flang = 39; break;
            case "TCL":
                flang = 40; break;
            case "WhiteSpace":
                flang = 41; break;
            case "TSQL":
                flang = 42; break;
            case "Java 8":
                flang = 43; break;
            case "DB2":
                flang = 44; break;
            case "Octave":
                flang = 46; break;
            case "XQuery":
                flang = 48; break;
            case "Racket":
                flang = 49; break;
            case "Rust":
                flang = 50; break;
            case "Swift":
                flang = 51; break;
            case "Fortron":
                flang = 54; break;
        }
    }

    public void setCode(String language){
        switch(language){
            case "C":
                editext.setText("#include <stdio.h>\n" +
                        "\n" +
                        "int main(void) {\n" +
                        "\t// your code goes here\n" +
                        "\treturn 0;\n" +
                        "}");
                break;
            case "C++":
                editext.setText("#include <iostream>\n" +
                        "using namespace std;\n" +
                        "\n" +
                        "int main() {\n" +
                        "\t// your code goes here\n" +
                        "\treturn 0;\n" +
                        "}\n");
                break;
            case "Java":
                editext.setText("import java.util.*;\n" +
                        "import java.lang.*;\n" +
                        "import java.io.*;\n" +
                        "\n" +
                        "/* Name of the class has to be \"Main\" only if the class is public. */\n" +
                        "class Name\n" +
                        "{\n" +
                        "\tpublic static void main (String[] args) throws java.lang.Exception\n" +
                        "\t{\n" +
                        "\t\t// your code goes here\n" +
                        "\t}\n" +
                        "}\n");
                break;
            case "Python":
                editext.setText("# cook your code here\n");
                break;
            case "Perl":
                editext.setText("#!/usr/bin/perl\n" +
                        "# your code here\n");
                break;
            case "PHP":
                editext.setText("<?php\n" +
                        "\n" +
                        "// your code goes here\n");
                break;
            case "Ruby":
                editext.setText("# cook your code here\n");
                break;
            case "C#":
                editext.setText("using System;\n" +
                        "\n" +
                        "public class Test\n" +
                        "{\n" +
                        "\tpublic static void Main()\n" +
                        "\t{\n" +
                        "\t\t// your code goes here\n" +
                        "\t}\n" +
                        "}\n");
                break;
            case "MySql":
                editext.setText("");
                break;
            case "Oracle":
                editext.setText("");
                break;
            case "Haskell":
                editext.setText("-- Type annotation (optional)\n" +
                        "fib :: Int -> Integer\n" +
                        " \n" +
                        "-- With self-referencing data\n" +
                        "fib n = fibs !! n\n" +
                        "        where fibs = 0 : scanl (+) 1 fibs\n" +
                        "        -- 0,1,1,2,3,5,...\n" +
                        " \n" +
                        "-- Same, coded directly\n" +
                        "fib n = fibs !! n\n" +
                        "        where fibs = 0 : 1 : next fibs\n" +
                        "              next (a : t@(b:_)) = (a+b) : next t\n" +
                        " \n" +
                        "-- Similar idea, using zipWith\n" +
                        "fib n = fibs !! n\n" +
                        "        where fibs = 0 : 1 : zipWith (+) fibs (tail fibs)\n" +
                        " \n" +
                        "-- Using a generator function\n" +
                        "fib n = fibs (0,1) !! n\n" +
                        "        where fibs (a,b) = a : fibs (b,a+b)");
                break;
            case "Clojure":
                editext.setText("; your code goes here\n");
                break;
            case "Bash":
                editext.setText("/* cook your code below */\n");
                break;
            case "Scala":
                editext.setText("object Main extends App {\n" +
                        "\t// your dish goes here\n" +
                        "}\n");
                break;
            case "Erlang":
                editext.setText("-module(prog).\n" +
                        "-export([main/0]).\n" +
                        "\n" +
                        "main() ->\n" +
                        "\t% your code goes here\n" +
                        "\ttrue.\n");
                break;
            case "Lua":
                editext.setText("-- your code goes here\n");
                break;
            case "JavaScript":
                editext.setText("");
                break;
            case "Go":
                editext.setText("package main\n" +
                        "import \"fmt\"\n" +
                        "\n" +
                        "func main(){\n" +
                        "\t// your code goes here\n" +
                        "}\n");
                break;
            case "D":
                editext.setText("");
                break;
            case "Ocaml":
                editext.setText("(*\n" +
                        " * Example of early return implementation taken from\n" +
                        " * http://ocaml.janestreet.com/?q=node/91\n" +
                        " *)\n" +
                        "\n" +
                        "let with_return (type t) (f : _ -> t) =\n" +
                        "  let module M =\n" +
                        "     struct exception Return of t end\n" +
                        "  in\n" +
                        "  let return = { return = (fun x -> raise (M.Return x)); } in\n" +
                        "  try f return with M.Return x -> x\n" +
                        "\n" +
                        "\n" +
                        "(* Function that uses the 'early return' functionality provided by `with_return` *)\n" +
                        "let sum_until_first_negative list =\n" +
                        "  with_return (fun r ->\n" +
                        "    List.fold list ~init:0 ~f:(fun acc x ->\n" +
                        "      if x >= 0 then acc + x else r.return acc))");
                break;
            case "R":
                editext.setText("");
                break;
            case "Pascal":
                editext.setText("");
                break;
            case "Common Lisp (SBCL)":
                editext.setText("");
                break;
            case "Python 3":
                editext.setText("# cook your dish here\n");
                break;
            case "Groovy":
                editext.setText("");
                break;
            case "Objective C":
                editext.setText("");
                break;
            case "F#":
                editext.setText("pen System\n" +
                        "\n" +
                        "// your dish cookes here\n");
                break;
            case "Cobol":
                editext.setText("");
                break;
            case "VisualBasic":
                editext.setText("");
                break;
            case "LolCode":
                editext.setText("");
                break;
            case "SmallTalk":
                editext.setText("");
                break;
            case "TCL":
                editext.setText(";# your code goes here\n");
                break;
            case "WhiteSpace":
                editext.setText("");
                break;
            case "TSQL":
                editext.setText("");
                break;
            case "Java 8":
                editext.setText("import java.util.*;\n" +
                        "import java.lang.*;\n" +
                        "import java.io.*;\n" +
                        "\n" +
                        "/* Name of the class has to be \"Main\" only if the class is public. */\n" +
                        "class Name\n" +
                        "{\n" +
                        "\tpublic static void main (String[] args) throws java.lang.Exception\n" +
                        "\t{\n" +
                        "\t\t// your code goes here\n" +
                        "\t}\n" +
                        "}\n");
                break;
            case "DB2":
                editext.setText("");
                break;
            case "Octave":
                editext.setText("");
                break;
            case "XQuery":
                editext.setText("");
                break;
            case "Racket":
                editext.setText("");
                break;
            case "Rust":
                editext.setText("");
                break;
            case "Swift":
                editext.setText("");
                break;
            case "Fortron":
                editext.setText("import java.util.*;\n" +
                        "import java.lang.*;\n" +
                        "import java.io.*;\n" +
                        "\n" +
                        "/* Name of the class has to be \"Main\" only if the class is public. */\n" +
                        "class Name\n" +
                        "{\n" +
                        "\tpublic static void main (String[] args) throws java.lang.Exception\n" +
                        "\t{\n" +
                        "\t\t// your code goes here\n" +
                        "\t}\n" +
                        "}\n");
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.buttonHelp:
                AlertDialog.Builder al = new AlertDialog.Builder(MainActivity.this);
                al.setTitle("Help");
                al.setMessage("1. Select a language first, because any language selected will give you a basic framework for writing code\n" +
                        "\n" +
                        "2. Enter your code\n" +
                        "\n" +
                        "3. Click the compile button to compile and execute your code\n" +
                        "\n" +
                        "4. Enter the testcases, leave it blank for no testcases.\n" +
                        "\n" +
                        "5. Hit Compile\n" +
                        "\n" +
                        "6. If your code compiled and executed successfully, you can run your code\n" +
                        "\n" +
                        "7. Else, you will be acknowledged with the error\n" +
                        "\n" +
                        "8. If compiled successfully, run the code and enjoy the app");

                al.setPositiveButton("Got It", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                al.show();
                break;
        }
        return true;
    }
}
