package com.example.urszulaiflorian.rgbtohex;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity {

    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public static int max=101;
    public static int ktrC;
    public static int r =0;
    public static int g =0;
    public static int b=0;
    public static String[][] colors = new String[2][max];

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("com.example.urszulaiflorian.rgbtohex", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        
        getSupportActionBar().hide();

        resetColors();

        loadSharedPreferences();

        seekbarBlue();
        seekbarGreen();
        seekbarRed();
        setbuttonsLM();


        reklama();
    }

    public static void loadSharedPreferences() //Wczytuje zapisane kolory
    {
        ktrC = sharedPreferences.getInt("ktrC", 0);
        for(int i=0;i<max;i++)
        {
            String a = "color"+i;
            String b = "colorhex"+i;
            colors[0][i]=sharedPreferences.getString(a,"");
            colors[1][i]=sharedPreferences.getString(b, "");
        }
    }

    public void loadColor(View view) //Wczytaj kolor z listy
    {

        final String [][] colorsF = new String [2][ktrC];
        int t=0;
        int i=0;
        while(t<ktrC)
        {
            if(colors[0][i].compareTo("")!=0 )
            {
                colorsF[0][t]=colors[0][i];
                colorsF[1][t]=colors[1][i];
                t++;
            }
            i++;
        }

        if(ktrC!=0)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose color");
            builder.setItems(colorsF[0], new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    TextView red = (TextView) findViewById(R.id.red);
                    TextView green = (TextView) findViewById(R.id.green);
                    TextView blue = (TextView) findViewById(R.id.blue);
                    TextView hexa = (TextView) findViewById(R.id.hexa);
                    hexa.setText(colorsF[1][which]);
                    ImageView podglad = (ImageView) findViewById(R.id.podglad);
                    String text = hexa.getText().toString();
                    SeekBar sbred = (SeekBar) findViewById(R.id.seekBar);
                    SeekBar sbgreen = (SeekBar) findViewById(R.id.seekBar2);
                    SeekBar sbblue = (SeekBar) findViewById(R.id.seekBar3);
                    View layout = findViewById(R.id.layout);

                    int x = fromHextoDec(text.charAt(0)) * 16 + fromHextoDec(text.charAt(1));
                    int y = fromHextoDec(text.charAt(2)) * 16 + fromHextoDec(text.charAt(3));
                    int z = fromHextoDec(text.charAt(4)) * 16 + fromHextoDec(text.charAt(5));

                    red.setText("" + x);
                    green.setText("" + y);
                    blue.setText("" + z);
                    hexa.setText(fromRGBtoHex(x, y, z));
                    podglad.setBackgroundColor(Color.rgb(x, y, z));
                    layout.setBackgroundColor(Color.rgb(x, y, z));
                    kolorElem(x, y, z);
                    sbred.setProgress(x);
                    sbgreen.setProgress(y);
                    sbblue.setProgress(z);

                }
            });

            builder.create();
            builder.show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),
                    "Empty", Toast.LENGTH_SHORT).show();
        }

    }

    public void saveColor(View view) //Zapisz kolor na liscie
    {

        if(ktrC<max-1)
        {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
            dialogBuilder.setView(dialogView);
            final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);
            final TextView hexa = (TextView) findViewById(R.id.hexa);
            dialogBuilder.setTitle("Save color");
            dialogBuilder.setMessage("Name: ");
            dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            String nazwa = edt.getText().toString();
                            String hex = hexa.getText().toString();

                            try {
                                int x = fromHextoDec(hex.charAt(0)) * 16 + fromHextoDec(hex.charAt(1));
                                int y = fromHextoDec(hex.charAt(2)) * 16 + fromHextoDec(hex.charAt(3));
                                int z = fromHextoDec(hex.charAt(4)) * 16 + fromHextoDec(hex.charAt(5));

                                if (255 >= x && x >= 0 && 255 >= y && y >= 0 && 255 >= z && z >= 0) {

                                    if (nazwa.compareTo("") != 0) {
                                        addColor(nazwa, hex);
                                        Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(getApplicationContext(),
                                                "Wrong Name!", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Please set HEXADECIMAL first", Toast.LENGTH_SHORT);
                                    toast.show();
                                }

                            } catch (Exception a) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Please set HEXADECIMAL first", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    }
            );

            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    }
            );

            AlertDialog b = dialogBuilder.create();
            b.show();

        }
        else
        {
            Toast.makeText(getApplicationContext(),
                    (max-1)+" colors limit!", Toast.LENGTH_SHORT).show();
        }

    }

    public static void addColor(String nazwa,String hex) //Dodaje kolor do tablic zapisanych kolorow
    {
        colors[0][ktrC]=nazwa;
        colors[1][ktrC]=hex;
        ktrC++;
        saveColors();
    }

    public static void saveColors() //Zapisuje kolory do sharedPreferencess
    {
        int t =0;
        int i=0;
        while(t<ktrC)
        {
            if(!colors[0][i].equals(""))
            {
                String a = "color"+t;
                String b = "colorhex"+t;
                editor.putString(a,colors[0][i]);
                editor.putString(b,colors[1][i]);
                t++;
            }
            i++;
        }
        editor.putInt("ktrC",ktrC);
        editor.commit();
    }

    public void deleteColor(View view) //Usun kolor z listy
    {

        String [][] colorsF = new String [2][ktrC];
        final int []ktory = new int[ktrC];
        int t=0;
        int i=0;
        while(t<ktrC)
        {
            if(colors[0][i].compareTo("")!=0 )
            {
                colorsF[0][t]=colors[0][i];
                colorsF[1][t]=colors[1][i];
                ktory[t]=i;
                t++;
            }
            i++;
        }

        if(ktrC!=0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete color");
            builder.setItems(colorsF[0], new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    deleteColor(ktory[which]);
                }
            });

            builder.create();
            builder.show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),
                    "Empty", Toast.LENGTH_SHORT).show();
        }


    }

    public static void deleteColor(int ktory) //Usuwa kolor z tablicy zapisanych kolorow
    {

        for(int i=ktory;i<max-1;i++) {
            colors[0][i] = colors[0][i+1];
            colors[1][i] = colors[1][i+1];
        }
        ktrC--;
        saveColors();
    }

    public void hardReset(View view) //Usun wszystkie zapisane kolory
    {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);


        dialogBuilder.setTitle("Delete all");
        dialogBuilder.setMessage("Are you sure you want to delete all of data?");
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()

                {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }

        );
        dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        resetColors();
                        ktrC = 0;
                        saveColors();
                    }
                }
        );


        AlertDialog b = dialogBuilder.create();
        b.show();

    }

    public static void resetColors() //Czysci tablice zapisanych kolorow
    {
        for(int i=0;i<max;i++)
            colors[0][i]="";
    }

    public void show(View view) //Pokaz kolor na calym ekranie
    {

        r=0;
        g=0;
        b=0;

        try {
            TextView red = (TextView) findViewById(R.id.red);
            TextView green = (TextView) findViewById(R.id.green);
            TextView blue = (TextView) findViewById(R.id.blue);

            r = Integer.parseInt(red.getText().toString());
            g = Integer.parseInt(green.getText().toString());
            b = Integer.parseInt(blue.getText().toString());
        }
        catch(Exception a)
        {

        }

        setContentView(R.layout.podglad);
        LinearLayout img =  (LinearLayout) findViewById(R.id.show);
        img.setBackgroundColor(Color.rgb(r, g, b));
        AdView mAdView = (AdView) findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        getSupportActionBar().hide();

    }

    public void back(View view) //Powrot z show na poczatek
    {
        setContentView(R.layout.activity_main);
        TextView red = (TextView) findViewById(R.id.red);
        TextView green = (TextView) findViewById(R.id.green);
        TextView blue = (TextView) findViewById(R.id.blue);
        if(r==0 && g==0 && b==0)
        {
            Reset(view);
        }
        else {
            red.setText(r + "");
            green.setText(g + "");
            blue.setText(b + "");
            rgbToHex(view);
        }
        seekbarBlue();
        seekbarGreen();
        seekbarRed();
        setbuttonsLM();

        ActionBar bar  = getSupportActionBar();
        bar.hide();

        reklama();

    }

    public void setbuttonsLM() //Zeby od razu mozna było leciec przy przytrzymaniu
    {
        Button b4= (Button) findViewById(R.id.button4);
        Button b5= (Button) findViewById(R.id.button5);
        Button b6= (Button) findViewById(R.id.button6);
        Button b7= (Button) findViewById(R.id.button7);
        Button b8= (Button) findViewById(R.id.button8);
        Button b9= (Button) findViewById(R.id.button9);

        final View v4 = b4;
        b4.setOnLongClickListener(new View.OnLongClickListener() {

            private Handler mHandler;

            @Override
            public boolean onLongClick(View v) {

                mHandler = new Handler();
                mHandler.postDelayed(mAction, 200);
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override
                public void run() {
                    if (v4.isPressed()) {
                        rless(v4);
                        mHandler.postDelayed(mAction, 200);
                    }
                }
            };

        });

        final View v5 = b5;
        b5.setOnLongClickListener(new View.OnLongClickListener() {

            private Handler mHandler;

            @Override
            public boolean onLongClick(View v) {

                mHandler = new Handler();
                mHandler.postDelayed(mAction, 200);
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override
                public void run() {
                    if (v5.isPressed()) {
                        rmore(v5);
                        mHandler.postDelayed(mAction, 200);
                    }
                }
            };

        });

        final View v6 = b6;
        b6.setOnLongClickListener(new View.OnLongClickListener() {

            private Handler mHandler;

            @Override
            public boolean onLongClick(View v) {

                mHandler = new Handler();
                mHandler.postDelayed(mAction, 200);
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override
                public void run() {
                    if (v6.isPressed()) {
                        gless(v6);
                        mHandler.postDelayed(mAction, 200);
                    }
                }
            };

        });

        final View v7 = b7;
        b7.setOnLongClickListener(new View.OnLongClickListener() {

            private Handler mHandler;

            @Override
            public boolean onLongClick(View v) {

                mHandler = new Handler();
                mHandler.postDelayed(mAction, 200);
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override
                public void run() {
                    if (v7.isPressed()) {
                        gmore(v7);
                        mHandler.postDelayed(mAction, 200);
                    }
                }
            };

        });

        final View v8 = b8;
        b8.setOnLongClickListener(new View.OnLongClickListener() {

            private Handler mHandler;

            @Override
            public boolean onLongClick(View v) {

                mHandler = new Handler();
                mHandler.postDelayed(mAction, 200);
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override
                public void run() {
                    if (v8.isPressed()) {
                        bless(v8);
                        mHandler.postDelayed(mAction, 200);
                    }
                }
            };

        });

        final View v9 = b9;
        b9.setOnLongClickListener(new View.OnLongClickListener() {

            private Handler mHandler;

            @Override
            public boolean onLongClick(View v) {

                mHandler = new Handler();
                mHandler.postDelayed(mAction, 200);
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override
                public void run() {
                    if (v9.isPressed()) {
                        bmore(v9);
                        mHandler.postDelayed(mAction, 200);
                    }
                }
            };
        });

    }

    public void rless(View view) //Zmniejsza red o 1
    {
        SeekBar sbred = (SeekBar) findViewById(R.id.seekBar);
        SeekBar sbgreen = (SeekBar) findViewById(R.id.seekBar2);
        SeekBar sbblue = (SeekBar) findViewById(R.id.seekBar3);
        TextView red = (TextView) findViewById(R.id.red);
        TextView hexa = (TextView) findViewById(R.id.hexa);
        ImageView podglad = (ImageView) findViewById(R.id.podglad);
        View layout =  findViewById(R.id.layout);
        int x = sbred.getProgress();
        int y = sbgreen.getProgress();
        int z = sbblue.getProgress();
        if(x>0)
            x--;
        hexa.setText(fromRGBtoHex(x, y, z));
        podglad.setBackgroundColor(Color.rgb(x, y, z));
        layout.setBackgroundColor(Color.rgb(x, y, z));
        kolorElem(x, y, z);
        red.setText("" + x);
        sbred.setProgress(x);
    }

    public void seekbarRed() //Pasek do dynamicznej zmiany red
    {

        SeekBar sbred = (SeekBar) findViewById(R.id.seekBar);
        sbred.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setzXYZ();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                setzXYZ();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setzXYZ();
            }
        });
    }

    public void rmore(View view) //Zwieksza red o 1
    {
        SeekBar sbred = (SeekBar) findViewById(R.id.seekBar);
        SeekBar sbgreen = (SeekBar) findViewById(R.id.seekBar2);
        SeekBar sbblue = (SeekBar) findViewById(R.id.seekBar3);
        TextView red = (TextView) findViewById(R.id.red);
        TextView hexa = (TextView) findViewById(R.id.hexa);
        ImageView podglad = (ImageView) findViewById(R.id.podglad);
        View layout =  findViewById(R.id.layout);
        int x = sbred.getProgress();
        int y = sbgreen.getProgress();
        int z = sbblue.getProgress();
        if(x<255)
            x++;
        hexa.setText(fromRGBtoHex(x, y, z));
        podglad.setBackgroundColor(Color.rgb(x, y, z));
        layout.setBackgroundColor(Color.rgb(x, y, z));
        kolorElem(x, y, z);
        red.setText("" + x);
        sbred.setProgress(x);
    }

    public void gless(View view) //Zmniejsza green o 1
    {
        SeekBar sbred = (SeekBar) findViewById(R.id.seekBar);
        SeekBar sbgreen = (SeekBar) findViewById(R.id.seekBar2);
        SeekBar sbblue = (SeekBar) findViewById(R.id.seekBar3);
        TextView green = (TextView) findViewById(R.id.green);
        TextView hexa = (TextView) findViewById(R.id.hexa);
        ImageView podglad = (ImageView) findViewById(R.id.podglad);
        View layout =  findViewById(R.id.layout);
        int x = sbred.getProgress();
        int y = sbgreen.getProgress();
        int z = sbblue.getProgress();
        if(y>0)
            y--;
        hexa.setText(fromRGBtoHex(x, y, z));
        podglad.setBackgroundColor(Color.rgb(x, y, z));
        layout.setBackgroundColor(Color.rgb(x, y, z));
        kolorElem(x, y, z);
        green.setText("" + y);
        sbgreen.setProgress(y);
    }

    public void seekbarGreen() //Pasek do dynamicznej zmiany green
    {

        SeekBar sbgreen = (SeekBar) findViewById(R.id.seekBar2);
        sbgreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                setzXYZ();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
                setzXYZ();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                setzXYZ();
            }
        });
    }

    public void gmore(View view) //Zwieksza green o 1
    {
        SeekBar sbred = (SeekBar) findViewById(R.id.seekBar);
        SeekBar sbgreen = (SeekBar) findViewById(R.id.seekBar2);
        SeekBar sbblue = (SeekBar) findViewById(R.id.seekBar3);
        TextView green = (TextView) findViewById(R.id.green);
        TextView hexa = (TextView) findViewById(R.id.hexa);
        ImageView podglad = (ImageView) findViewById(R.id.podglad);
        View layout =  findViewById(R.id.layout);
        int x = sbred.getProgress();
        int y = sbgreen.getProgress();
        int z = sbblue.getProgress();
        if(y<255)
            y++;
        hexa.setText(fromRGBtoHex(x, y, z));
        podglad.setBackgroundColor(Color.rgb(x, y, z));
        layout.setBackgroundColor(Color.rgb(x,y,z));
        kolorElem(x, y, z);
        green.setText("" + y);
        sbgreen.setProgress(y);
    }

    public void bless(View view) //Zmniejsza blue o 1
    {
        SeekBar sbred = (SeekBar) findViewById(R.id.seekBar);
        SeekBar sbgreen = (SeekBar) findViewById(R.id.seekBar2);
        SeekBar sbblue = (SeekBar) findViewById(R.id.seekBar3);
        TextView blue = (TextView) findViewById(R.id.blue);
        View layout =  findViewById(R.id.layout);
        TextView hexa = (TextView) findViewById(R.id.hexa);
        ImageView podglad = (ImageView) findViewById(R.id.podglad);
        int x = sbred.getProgress();
        int y = sbgreen.getProgress();
        int z = sbblue.getProgress();
        if(z>0)
            z--;
        hexa.setText(fromRGBtoHex(x, y, z));
        podglad.setBackgroundColor(Color.rgb(x, y, z));
        layout.setBackgroundColor(Color.rgb(x, y, z));
        kolorElem(x, y, z);
        blue.setText("" + z);
        sbblue.setProgress(z);
    }

    public void seekbarBlue() //Pasek do dynamicznej zmiany blue
    {

        SeekBar sbblue = (SeekBar) findViewById(R.id.seekBar3);
        sbblue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setzXYZ();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                setzXYZ();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setzXYZ();
            }
        });

    }

    public void bmore(View view) //Zwieksza blue o 1
    {
        SeekBar sbred = (SeekBar) findViewById(R.id.seekBar);
        SeekBar sbgreen = (SeekBar) findViewById(R.id.seekBar2);
        SeekBar sbblue = (SeekBar) findViewById(R.id.seekBar3);
        TextView blue = (TextView) findViewById(R.id.blue);
        TextView hexa = (TextView) findViewById(R.id.hexa);
        ImageView podglad = (ImageView) findViewById(R.id.podglad);
        View layout =  findViewById(R.id.layout);
        int x = sbred.getProgress();
        int y = sbgreen.getProgress();
        int z = sbblue.getProgress();
        if(z<255)
            z++;
        hexa.setText(fromRGBtoHex(x, y, z));
        podglad.setBackgroundColor(Color.rgb(x, y, z));
        layout.setBackgroundColor(Color.rgb(x, y, z));
        kolorElem(x, y, z);
        blue.setText("" + z);
        sbblue.setProgress(z);
    }

    public void redText(View view) //Wprowadzanie Red
    {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog_colors, null);
        dialogBuilder.setView(dialogView);
        final  EditText edt = (EditText) dialogView.findViewById(R.id.edit2);

        dialogBuilder.setTitle("Red");
        dialogBuilder.setMessage("Select number [0-255]");
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String nazwa = edt.getText().toString();
                        TextView red = (TextView) findViewById(R.id.red);
                        red.setText(nazwa);

                    }
                }
        );

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }
        );

        AlertDialog b = dialogBuilder.create();
        b.show();

    }

    public void greenText(View view) //Wprowadzanie Green
    {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog_colors, null);
        dialogBuilder.setView(dialogView);
        final  EditText edt = (EditText) dialogView.findViewById(R.id.edit2);

        dialogBuilder.setTitle("Green");
        dialogBuilder.setMessage("Select number [0-255]");
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String nazwa = edt.getText().toString();
                        TextView green = (TextView) findViewById(R.id.green);
                        green.setText(nazwa);

                    }
                }
        );

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }
        );

        AlertDialog b = dialogBuilder.create();
        b.show();

    }

    public void blueText(View view) //Wprowadzanie Blue
    {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog_colors, null);
        dialogBuilder.setView(dialogView);
        final  EditText edt = (EditText) dialogView.findViewById(R.id.edit2);

        dialogBuilder.setTitle("Blue");
        dialogBuilder.setMessage("Select number [0-255]");
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String nazwa = edt.getText().toString();
                        TextView blue = (TextView) findViewById(R.id.blue);
                        blue.setText(nazwa);

                    }
                }
        );

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }
        );

        AlertDialog b = dialogBuilder.create();
        b.show();

    }

    public void hexText(View view) //Wprowadzanie Hexadecimal
    {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        final  EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

        dialogBuilder.setTitle("Hexadecimal");
        dialogBuilder.setMessage("Select hexadecimal number:");
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String nazwa = edt.getText().toString();
                        TextView hexa = (TextView) findViewById(R.id.hexa);
                        hexa.setText(nazwa);

                    }
                }
        );

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }
        );

        AlertDialog b = dialogBuilder.create();
        b.show();

    }

    public void rgbToHex(View view) //Zmiana z RGB na Hexadecimal
    {

        try{

            TextView red = (TextView) findViewById(R.id.red);
            TextView green = (TextView) findViewById(R.id.green);
            TextView blue = (TextView) findViewById(R.id.blue);
            TextView hexa = (TextView) findViewById(R.id.hexa);
            ImageView podglad = (ImageView) findViewById(R.id.podglad);

            SeekBar sbred = (SeekBar) findViewById(R.id.seekBar);
            SeekBar sbgreen = (SeekBar) findViewById(R.id.seekBar2);
            SeekBar sbblue = (SeekBar) findViewById(R.id.seekBar3);
            View layout =  findViewById(R.id.layout);

            int x = Integer.parseInt(red.getText().toString());
            int y = Integer.parseInt(green.getText().toString());
            int z = Integer.parseInt(blue.getText().toString());
            if (x >= 0 && x <= 255 && y >= 0 && y <= 255 && z >= 0 && z <= 255) {
                hexa.setText(fromRGBtoHex(x, y, z));
                podglad.setBackgroundColor(Color.rgb(x, y, z));
                layout.setBackgroundColor(Color.rgb(x,y,z));
                kolorElem(x, y, z);
                sbred.setProgress(x);
                sbgreen.setProgress(y);
                sbblue.setProgress(z);
            }
            else
            {
                Toast toast = Toast.makeText(getApplicationContext(), "Please set RGB first", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        catch (Exception a)
        {
            Toast toast = Toast.makeText(getApplicationContext(), "Please set RGB first", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public String licznalit(int x) //Zamienia 10-15 na A-F
    {
        switch (x) {
            case 10:
                return "A";
            case 11:
                return "B";
            case 12:
                return "C";
            case 13:
                return "D";
            case 14:
                return "E";
            case 15:
                return "F";
            default:
                return ""+x;
        }
    }

    public String fromDectoHex(int x) //Zamiania z dziesietnego na szesnastkowy
    {
        int l=0;
        String tmp;
        while(x>15)
        {
            l++;
            x-=16;
        }

        tmp=licznalit(l)+licznalit(x);

        return tmp;
    }

    public String fromRGBtoHex(int red, int green,int blue) //Zamienia RGB na Hexadecimal
    {
        String tmp;

        tmp=fromDectoHex(red)+fromDectoHex(green)+fromDectoHex(blue);
        return tmp;
    }

    public void hexToRGB(View view) //Zmiana z Hexadecimal do RGB
    {

        TextView red = (TextView) findViewById(R.id.red);
        TextView green = (TextView) findViewById(R.id.green);
        TextView blue = (TextView) findViewById(R.id.blue);
        TextView hexa = (TextView) findViewById(R.id.hexa);
        ImageView podglad = (ImageView) findViewById(R.id.podglad);
        String text = hexa.getText().toString();
        SeekBar sbred = (SeekBar) findViewById(R.id.seekBar);
        SeekBar sbgreen = (SeekBar) findViewById(R.id.seekBar2);
        SeekBar sbblue = (SeekBar) findViewById(R.id.seekBar3);
        View layout =  findViewById(R.id.layout);

        try {

            int x = fromHextoDec(text.charAt(0)) * 16 + fromHextoDec(text.charAt(1));
            int y = fromHextoDec(text.charAt(2)) * 16 + fromHextoDec(text.charAt(3));
            int z = fromHextoDec(text.charAt(4)) * 16 + fromHextoDec(text.charAt(5));

            if(255>=x && x>=0 && 255>=y && y>=0 && 255>=z && z>=0) {
                red.setText(""+x);
                green.setText(""+y);
                blue.setText(""+z);
                hexa.setText(fromRGBtoHex(x, y, z));
                podglad.setBackgroundColor(Color.rgb(x, y, z));
                layout.setBackgroundColor(Color.rgb(x,y,z));
                kolorElem(x, y, z);
                sbred.setProgress(x);
                sbgreen.setProgress(y);
                sbblue.setProgress(z);
            }
            else{
                Toast toast = Toast.makeText(getApplicationContext(), "Please set HEXADECIMAL first", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        catch (Exception a)
        {
            Toast toast = Toast.makeText(getApplicationContext(), "Please set HEXADECIMAL first", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public int fromHextoDec(char x) //Zamienia szesnastkowy na dziesietny
    {
        int a = (int) x;
        if(a>96 && a<103)
            a-=87;
        else if(a>64 && a<71)
            a-=55;
        else if(a>47 && a<58)
            a-=48;
        else
            a=-1;

        return a;
    }

    public void Reset(View view) //Reset aktywnosci
    {
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        seekbarBlue();
        seekbarGreen();
        seekbarRed();
        reklama();
    }

    public void kolorElem(int x,int y,int z) //Ustawia kolor napisow na przyciskach na przeciwny do tla
    {
        TextView tv1 = (TextView) findViewById(R.id.textView);
        TextView tv2 = (TextView) findViewById(R.id.textView2);
        TextView tv3 = (TextView) findViewById(R.id.textView3);
        Button b1= (Button) findViewById(R.id.button);
        Button b2= (Button) findViewById(R.id.button2);
        Button b4= (Button) findViewById(R.id.button4);
        Button b5= (Button) findViewById(R.id.button5);
        Button b6= (Button) findViewById(R.id.button6);
        Button b7= (Button) findViewById(R.id.button7);
        Button b8= (Button) findViewById(R.id.button8);
        Button b9= (Button) findViewById(R.id.button9);
        Button b3= (Button) findViewById(R.id.button3);
        Button b10= (Button) findViewById(R.id.button10);
        Button b11= (Button) findViewById(R.id.button11);
        Button b12= (Button) findViewById(R.id.button12);
        TextView r = (TextView) findViewById(R.id.red);
        TextView g = (TextView) findViewById(R.id.green);
        TextView b = (TextView) findViewById(R.id.blue);
        TextView h = (TextView) findViewById(R.id.hexa);
        tv1.setTextColor(Color.rgb(255-x,255-y,255-z));
        tv2.setTextColor(Color.rgb(255-x,255-y,255-z));
        tv3.setTextColor(Color.rgb(255-x,255-y,255-z));
        b1.setTextColor(Color.rgb(255-x,255-y,255-z));
        b2.setTextColor(Color.rgb(255-x,255-y,255-z));
        b4.setTextColor(Color.rgb(255-x,255-y,255-z));
        b5.setTextColor(Color.rgb(255-x,255-y,255-z));
        b6.setTextColor(Color.rgb(255-x,255-y,255-z));
        b7.setTextColor(Color.rgb(255-x,255-y,255-z));
        b8.setTextColor(Color.rgb(255-x,255-y,255-z));
        b9.setTextColor(Color.rgb(255-x,255-y,255-z));
        b3.setTextColor(Color.rgb(255-x,255-y,255-z));
        b10.setTextColor(Color.rgb(255-x,255-y,255-z));
        b11.setTextColor(Color.rgb(255-x,255-y,255-z));
        b12.setTextColor(Color.rgb(255-x,255-y,255-z));
        r.setTextColor(Color.rgb(255-x,255-y,255-z));
        g.setTextColor(Color.rgb(255-x,255-y,255-z));
        b.setTextColor(Color.rgb(255-x,255-y,255-z));
        h.setTextColor(Color.rgb(255-x,255-y,255-z));
    }

    public void setzXYZ() //Ustawia RGB i Hexadecimal na podstawie paskow progresu
    {
        SeekBar sbred = (SeekBar) findViewById(R.id.seekBar);
        SeekBar sbgreen = (SeekBar) findViewById(R.id.seekBar2);
        SeekBar sbblue = (SeekBar) findViewById(R.id.seekBar3);
        TextView red = (TextView) findViewById(R.id.red);
        TextView blue = (TextView) findViewById(R.id.blue);
        TextView green = (TextView) findViewById(R.id.green);
        TextView hexa = (TextView) findViewById(R.id.hexa);
        ImageView podglad = (ImageView) findViewById(R.id.podglad);
        View layout =  findViewById(R.id.layout);
        int x = sbred.getProgress();
        int y = sbgreen.getProgress();
        int z = sbblue.getProgress();
        hexa.setText(fromRGBtoHex(x, y, z));
        podglad.setBackgroundColor(Color.rgb(x, y, z));
        layout.setBackgroundColor(Color.rgb(x, y, z));
        kolorElem(x, y, z);
        red.setText("" + x);
        green.setText("" + y);
        blue.setText("" + z);
    }

    public void reklama() //Reklama
    {
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public void printColors() //Drukuje tablice zapisanych kolorow
    {
        System.out.println(ktrC);
        for(int i=0;i<max;i++)
            if(colors[0][i].compareTo("")!=0)
                System.out.println(colors[0][i]+" -> "+colors[1][i]);
    }

}
