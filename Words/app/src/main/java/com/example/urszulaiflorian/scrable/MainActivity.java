package com.example.urszulaiflorian.scrable;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public boolean backToMenuOrPanel;
    public boolean changeLettersPosition = true;
    public boolean choose;
    public boolean exchange;
    public boolean inputLetters;
    public boolean pl;
    public boolean [] chosenLetters = new boolean[8];
    public int boardWidth=0;
    public int boardHeight=0;
    public int changeLetterPositionA = 0;
    public int changeLetterPositionB = 0;
    public int changeLettersPositionHelper = 0; //first and second letter (which is which)
    public int countLetters = 0;
    public int numberOfPlayers = 2;
    public int maxBoardSize = 71;
    public int maxGamesToSave = 50;
    public int player = 1;
    public int visibleBoardSize = 11;
    public int verticalVisibleBoard=maxBoardSize/2-visibleBoardSize/2;
    public int horizontalVisibleBoard=maxBoardSize/2-visibleBoardSize/2;
    public int points [];
    public int savedGamesList = 0;
    public int [][] premiumLetterFields = new int[maxBoardSize][maxBoardSize];
    public int [][] premiumWordFields = new int[maxBoardSize][maxBoardSize];
    public char [] lettersTab = new char[8];
    public char [][] boardTab = new char[maxBoardSize][maxBoardSize];
    public char [][] firstLottery;
    public char [][] playersLetters;
    public String letters="";
    public String [] gamesList = new String[maxGamesToSave];
    public Button boardButtons[][] = new Button[maxBoardSize][maxBoardSize];

    //p-p zmienione
    public int sx;
    public int sy;
    public int kx;
    public int ky;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getSupportActionBar()!=null)
            getSupportActionBar().hide();

        setContentView(R.layout.loading_screen);

        loadSavedGamesList();

        int DELAY = 200;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                checkBoardSize();
                setPremiumFields();
            }
        }, DELAY);

    }

    public void back(View view)
    {
        if(backToMenuOrPanel) {
            setContentView(R.layout.activity_main);
            Button cho = (Button) findViewById(R.id.button9);
            Button wst = (Button) findViewById(R.id.button8);
            TextView text = (TextView) findViewById(R.id.textView3);
            TextView ptkgr = (TextView) findViewById(R.id.textView2);

            wst.setBackgroundResource(R.drawable.change);
            cho.setBackgroundResource(R.drawable.choose);

            text.setText("Player " + player);
            tabToLetter(player);
            ptkgr.setText("Points: " + points[player - 1]);
            if(choose && inputLetters)
                exchangeLetters();
            reset();
            setDefaultLettersBackground();
            save("last");
        }
        else
            setContentView(R.layout.wstep);
    }

    public  void basicImplementation()
    {
        points = new int[numberOfPlayers];
        playersLetters = new char [numberOfPlayers][8];
        firstLottery= new char [numberOfPlayers][8];

        for(int i=0;i<numberOfPlayers;i++)
            for(int j=0;j<8;j++)
            {
                playersLetters[i][j]=0;
                firstLottery[i][j]='0';
            }
    }

    public void blank(final int button) {

        String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

        final String options [] = new String[letters.length];
        for(int i=0;i<letters.length;i++)
            options[i]=letters[i]+" ("+points(letters[i].charAt(0))+")";

        AlertDialog.Builder optionList = new AlertDialog.Builder(this);
        optionList.setTitle("Blank:");
        optionList.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                char a = options[which].charAt(0);
                button(button).setText(a+"\n"+points(a));
            }
        });
        optionList.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }
        );
        optionList.create();
        optionList.show();
    }

    public void board()
    {
        TableLayout layout = (TableLayout) findViewById(R.id.plan);
        TextView text = (TextView) findViewById(R.id.textView7);
        text.setText(letters);
        layout.removeAllViews();
        System.out.println(boardWidth + "x" + boardHeight);

        if(getSupportActionBar()!=null)
            getSupportActionBar().hide();

        //Wstawienie przyciskow i ich funkcji
        for (int i = verticalVisibleBoard; i < verticalVisibleBoard+visibleBoardSize; i++) {
            TableRow row = new TableRow(getApplicationContext());
            for (int j = horizontalVisibleBoard; j < horizontalVisibleBoard+visibleBoardSize; j++) {
                Button btnTag = new Button(getApplicationContext());
                btnTag.setId(i*maxBoardSize +j);
                btnTag.setPadding(0,0,0,0);
                boardButtons[i][j]=btnTag;
                setButton(i,j);
                row.addView(btnTag, boardWidth / visibleBoardSize, boardHeight / visibleBoardSize);
            }
            layout.addView(row);
        }
    }

    public void board(View view)
    {
        lettersToTab(player);
        setContentView(R.layout.plansza);
        board();
        fillBoardFields();
    }

    public  Button button(int x)
    {
        return (Button) findViewById(R.id.button1+x-1);
    }

    public void changeVisibleBoardSive(View view)
    {
        EditText text = (EditText) findViewById(R.id.editText2);
        if(text.getText()!=null)
        {
            int x =visibleBoardSize;
            visibleBoardSize=Integer.parseInt(text.getText().toString());
            int dx = visibleBoardSize-x;
            if(verticalVisibleBoard-dx/2>=0 && verticalVisibleBoard-dx/2<maxBoardSize)
                verticalVisibleBoard-=dx/2;
            if(horizontalVisibleBoard-dx/2>=0 && horizontalVisibleBoard-dx/2<maxBoardSize)
                horizontalVisibleBoard-=dx/2;
            Toast.makeText(getApplicationContext(),"Changed",Toast.LENGTH_SHORT).show();
            back(view);
        }
    }

    public void changeLettersPosition()
    {
        Button a = button(changeLetterPositionA);
        Button b = button(changeLetterPositionB);
        String x = a.getText() + "";
        a.setText(b.getText() + "");
        b.setText(x);
        changeLettersPositionHelper=0;
        changeLettersPosition=true;
    }

    public void checkBoardSize()
    {
        setContentView(R.layout.plansza);
        if(getSupportActionBar()!=null)
            getSupportActionBar().hide();
        final TableLayout layout = (TableLayout) findViewById(R.id.plan);
        layout.post(new Runnable() {

            @Override
            public void run() {
                boardWidth = layout.getWidth();
                boardHeight = layout.getHeight();
                System.out.println("Board Size ->" + boardWidth + "x" + boardHeight);

            }
        });
        int DELAY = 10;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                setContentView(R.layout.wstep);
                final Button layout = (Button) findViewById(R.id.button30);
                layout.post(new Runnable() {

                    @Override
                    public void run() {
                        System.out.println("->" + layout.getWidth() + "x" + layout.getHeight());
                    }
                });
            }
        }, DELAY);
    }

    public void choose(View view)
    {

        Button cho = (Button) findViewById(R.id.button9);
        Button wst = (Button) findViewById(R.id.button8);

        if(choose)
        {
            TextView punkty = (TextView) findViewById(R.id.textView2);
            punkty.setText("Points: " + points[player - 1]);
            save("last");
            parseTabs();
            setContentView(R.layout.plansza);
            board();
            fillBoardFields();
        }
        else
        {
            save("last");
            cho.setBackgroundResource(R.drawable.ok);
            wst.setBackgroundResource(R.drawable.cancel);
            choose=true;
            changeLettersPosition=false;
            changeLettersPositionHelper=0;
        }

    }

    public int countPoints()
    {
        int tmp = 0;
        int sum = 0;
        int lit = 0;
        int premia = 1;
        int dx=0;
        int dy=0;
        int dod=0;

        if (kx - sx == 0 && ky - sy != 0)
            dy=1;
        else
            dx=1;

        sum+=letterArea(dy,dx,0,false);

        for (int i = 1; i < 8; i++) {
            if (chosenLetters[i]) {
                while (boardTab[sx + dx * tmp][sy + dy * tmp] != '0') {
                    sum += points(boardTab[sx + dx * tmp][sy + dy * tmp]);
                    tmp++;
                }
                boardTab[sx + dx * tmp][sy + dy * tmp] = lettersTab[i];
                sum += points(lettersTab[i]) * premiumLetterFields[sx + dx * tmp][sy + dy * tmp];
                premia *= premiumWordFields[sx + dx * tmp][sy + dy * tmp];
                dod += letterArea(dx, dy, tmp);
                System.out.println(points(boardTab[sx + dx * tmp][sy + dy * tmp]) + ".." + boardTab[sx + dx * tmp][sy + dy * tmp] + "-oto-" + letterArea(dx, dy, tmp));
                lit++;
                tmp++;
            }
        }
        sum+=letterArea(dy,dx,tmp,true);

        sum *= premia;

        if (lit == 7)
            sum += 50;

        if(lit==1)
            sum = max(sum,dod);
        else
            sum+=dod;

        return sum;
    }

    public void delete(int ktory) //Usuwa z tablicy zapisanych
    {
        System.arraycopy(gamesList, ktory + 1, gamesList, ktory, maxGamesToSave - 1 - ktory);
        savedGamesList--;
        save();
    }

    public void delete(View view) //Usun z listy
    {
        String [] lista = new String [savedGamesList];
        final int []ktory = new int[savedGamesList];
        int t=0;
        int i=0;
        while(t<savedGamesList)
        {
            if(gamesList[i].compareTo("")!=0)
            {
                lista[t]=gamesList[i];
                ktory[t]=i;
                t++;
            }
            i++;
        }

        if(savedGamesList!=0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete:");
            builder.setItems(lista, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    delete(ktory[which]);
                    Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_SHORT).show();
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

    public int deletePoints()
    {
        int tmp = 0;
        int sum = 0;
        int lit = 0;
        int premium = 1;
        int dx = 0;
        int dy = 0;
        int dod = 0;

        if (kx - sx == 0 && ky - sy != 0)
            dy=1;
        else
            dx=1;

        sum+=letterArea(dy,dx,0,false);

        for (int i = 1; i < 8; i++)
            if (chosenLetters[i])
            {
                chosenLetters[i] =!chosenLetters[i];
                while (boardTab[sx+dx*tmp][sy + dy*tmp] != lettersTab[i])
                {
                    sum+=points(boardTab[sx+dx*tmp][sy + dy*tmp]);
                    tmp++;
                }
                boardTab[sx+dx*tmp][sy + dy*tmp] = '0';
                sum += points(lettersTab[i]) * premiumLetterFields[sx+dx*tmp][sy + dy*tmp];
                premium *= premiumWordFields[sx+dx*tmp][sy + dy*tmp];
                dod+=letterArea(dx,dy,tmp);
                lit++;
                tmp++;
            }
        sum+=letterArea(dy,dx,tmp,true);
        sum *= premium;

        if (lit == 7)
            sum += 50;

        if(lit==1)
            sum = max(sum,dod);
        else
            sum+=dod;

        return sum;
    }

    public void Down(View view)
    {
        if(verticalVisibleBoard<maxBoardSize-visibleBoardSize)
            verticalVisibleBoard++;
        board();
        fillBoardFields();
    }

    public void englishLanguage(View view) {
        pl=false;
        findViewById(R.id.button30).setBackgroundResource(R.drawable.polish);
        findViewById(R.id.button29).setBackgroundResource(R.drawable.englishselected);
    }

    public  void exchange(int x)
    {
        Button b = button(x);
        char z = letter(lottery());
        b.setText(z + " \n" + points(z));
    }

    public  void exchangeLetters()
    {
        for(int i=1;i<8;i++)
            if(chosenLetters[i])
                exchange(i);
        lettersToTab(player);
        reset();
    }

    public void exchangeLetters(View view)
    {
        Button wym = (Button) findViewById(R.id.button8);
        if(choose)
        {
            reset();
            setDefaultLettersBackground();
            wym.setBackgroundResource(R.drawable.change);
            Button cho = (Button) findViewById(R.id.button9);
            cho.setBackgroundResource(R.drawable.choose);
            save("last");
        }
        else
        if(exchange)
        {
            exchangeLetters();
            wym.setBackgroundResource(R.drawable.change);
            setDefaultLettersBackground();
        }
        else
        {
            wym.setBackgroundResource(R.drawable.changechosen);
            exchange=true;
            changeLettersPosition=false;
            changeLettersPositionHelper=0;
        }
    }

    public void fillBoardFields()
    {

        for (int i = verticalVisibleBoard; i < verticalVisibleBoard+visibleBoardSize; i++)
            for (int j = horizontalVisibleBoard; j < horizontalVisibleBoard+visibleBoardSize; j++)
            {
                if(premiumWordFields[i][j]==3)
                    boardButtons[i][j].setBackgroundResource(R.drawable.trzyslowo);
                else if(premiumWordFields[i][j]==2)
                    boardButtons[i][j].setBackgroundResource(R.drawable.dwaslowo);
                else if(premiumLetterFields[i][j]==2)
                    boardButtons[i][j].setBackgroundResource(R.drawable.dwalitera);
                else if(premiumLetterFields[i][j]==3)
                    boardButtons[i][j].setBackgroundResource(R.drawable.trzylitera);
                else
                    boardButtons[i][j].setBackgroundResource(R.drawable.frame);

                if(boardTab[i][j]!='0')
                {
                    boardButtons[i][j].setTextColor(Color.WHITE);
                    boardButtons[i][j].setText(boardTab[i][j]+"");
                    boardButtons[i][j].setBackgroundResource(R.drawable.zaznacz);
                }
                else
                    boardButtons[i][j].setText("");
            }

        if(maxBoardSize/2>=verticalVisibleBoard && maxBoardSize/2<verticalVisibleBoard+visibleBoardSize && maxBoardSize/2>=horizontalVisibleBoard && maxBoardSize/2<horizontalVisibleBoard+visibleBoardSize)
            boardButtons[maxBoardSize/2][maxBoardSize/2].setBackgroundResource(R.drawable.start);

    }

    public int getNr(String a)
    {
        return  a.charAt(a.length()-1)-48;
    }

    public void lastone(View view)
    {
        load("last");
        Toast.makeText(getApplicationContext(),"Loaded",Toast.LENGTH_SHORT).show();
    }

    public void Left(View view)
    {
        if(horizontalVisibleBoard>0)
           horizontalVisibleBoard--;
        board();
        fillBoardFields();
    }

    public char letter(int x)
    {
        if(pl)
            return letterPL(x);
        return letterEN(x);
    }

    public  char letterPL(int x)
    {
        if(x<10)
            return 'A';
        else if(x<12)
            return 'B';
        else if(x<16)
            return 'C';
        else if(x<19)
            return 'D';
        else if(x<27)
            return 'E';
        else if(x<28)
            return 'F';
        else if(x<30)
            return 'G';
        else if(x<32)
            return 'H';
        else if(x<40)
            return 'I';
        else if(x<42)
            return 'J';
        else if(x<45)
            return 'K';
        else if(x<50)
            return 'L';
        else if(x<53)
            return 'M';
        else if(x<59)
            return 'N';
        else if(x<66)
            return 'O';
        else if(x<69)
            return 'P';
        else if(x<73)
            return 'R';
        else if(x<78)
            return 'S';
        else if(x<81)
            return 'T';
        else if(x<84)
            return 'U';
        else if(x<87)
            return 'W';
        else if(x<91)
            return 'Y';
        else if(x<98)
            return 'Z';
        else
            return ' ';
    }

    public  char letterEN(int x)
    {
        if(x<10)
            return 'A';
        else if(x<12)
            return 'B';
        else if(x<14)
            return 'C';
        else if(x<18)
            return 'D';
        else if(x<30)
            return 'E';
        else if(x<32)
            return 'F';
        else if(x<35)
            return 'G';
        else if(x<37)
            return 'H';
        else if(x<46)
            return 'I';
        else if(x<47)
            return 'J';
        else if(x<48)
            return 'K';
        else if(x<52)
            return 'L';
        else if(x<54)
            return 'M';
        else if(x<60)
            return 'N';
        else if(x<68)
            return 'O';
        else if(x<70)
            return 'P';
        else if(x<71)
            return 'Q';
        else if(x<77)
            return 'R';
        else if(x<81)
            return 'S';
        else if(x<87)
            return 'T';
        else if(x<91)
            return 'U';
        else if(x<93)
            return 'V';
        else if(x<95)
            return 'W';
        else if(x<96)
            return 'X';
        else if(x<98)
            return 'Y';
        else if(x<99)
            return 'Z';
        else
            return ' ';
    }

    public int letterArea(int a, int b, int c,boolean z)
    {
        int sum=0;
        if(a==0) {
            if(z)
                if (boardTab[sx + 1][sy + b * c] != '0') {
                    int tmp = 0;
                    while (boardTab[sx + 1 + tmp][sy + b * c] != '0') {
                        sum += points(boardTab[sx + 1 + tmp][sy + b * c]);
                        tmp++;
                    }
                }
            if(!z)
                if (boardTab[sx - 1][sy + b * c] != '0') {
                    int tmp = 0;
                    while (boardTab[sx - 1 - tmp][sy + b * c] != '0') {
                        sum += points(boardTab[sx - 1 - tmp][sy + b * c]);
                        tmp++;
                    }
                }
        }
        if(b==0) {
            if(z)
                if (boardTab[sx + a*c][sy + 1] != '0') {
                    int tmp = 0;
                    while (boardTab[sx + a*c][sy + 1+tmp] != '0') {
                        sum += points(boardTab[sx +a*c][sy + 1+tmp]);
                        tmp++;
                    }
                }
            if(!z)
                if (boardTab[sx +a*c][sy - 1] != '0') {
                    int tmp = 0;
                    while (boardTab[sx +a*c][sy - 1 - tmp] != '0') {
                        sum += points(boardTab[sx +a*c][sy - 1 - tmp]);
                        tmp++;
                    }
                }
        }

        System.out.println(z+":+:"+sum);

        return sum;
    }

    public int letterArea(int a, int b, int c)
    {
        int sum=0;
        int x=0;
        if(a==0) {
            if (boardTab[sx + 1][sy + b * c] != '0') {
                int tmp = 0;
                while (boardTab[sx + 1 + tmp][sy + b * c] != '0') {
                    sum += points(boardTab[sx + 1 + tmp][sy + b * c]);
                    tmp++;
                    x=1;
                }
            }
            if (boardTab[sx - 1][sy + b * c] != '0') {
                int tmp = 0;
                while (boardTab[sx - 1 - tmp][sy + b * c] != '0') {
                    sum += points(boardTab[sx - 1 - tmp][sy + b * c]);
                    tmp++;
                    x=1;
                }
            }
        }
        if(b==0) {
            if (boardTab[sx + a*c][sy + 1] != '0') {
                int tmp = 0;
                while (boardTab[sx + a*c][sy + 1+tmp] != '0') {
                    sum += points(boardTab[sx +a*c][sy + 1+tmp]);
                    tmp++;
                    x=1;
                }
            }
            if (boardTab[sx +a*c][sy - 1] != '0') {
                int tmp = 0;
                while (boardTab[sx +a*c][sy - 1 - tmp] != '0') {
                    sum += points(boardTab[sx +a*c][sy - 1 - tmp]);
                    tmp++;
                    x=1;
                }
            }
        }
        sum += points(boardTab[sx+a*c][sy + b*c]) * premiumLetterFields[sx+a*c][sy + b*c];
        sum*=premiumWordFields[sx+a*c][sy + b*c];

        sum*=x;

        return sum;
    }
    
    public void letterButtonFunction(View view)
    {
        int button = getNr(view.getResources().getResourceEntryName(view.getId()));

        char a = button(button).getText().toString().charAt(0);




        if (firstLottery[player - 1][button] == '0') {
            exchange(button);
            firstLottery[player - 1][button] = '1';
        }
        else if(a==' ')
        {
            blank(button);
        }
        else if (changeLettersPosition) {
            changeLettersPositionHelper++;
            if (changeLettersPositionHelper == 1)
                changeLetterPositionA = button;
            if (changeLettersPositionHelper == 2) {
                changeLetterPositionB = button;
                changeLettersPosition();
            }
        } else {
            chosenLetters[button] = !chosenLetters[button];
            setBackground(button);
        }
        lettersToTab(player);
    }

    public void lettersToTab(int x)
    {
        letters="";
        for(int i=1;i<8;i++)
        {
            playersLetters[x-1][i]=button(i).getText().charAt(0);
            String a = "("+button(i).getText().charAt(0)+","+points(button(i).getText().charAt(0))+") ";
            letters+=a;
        }
    }

    public void load(String name)
    {
        //ilosci graczy
        numberOfPlayers=sharedPreferences.getInt("numberOfPlayers", 2);

        basicImplementation();

        //odczyt wielosci planszy
        visibleBoardSize=sharedPreferences.getInt(name+"visibleBoardSize",11);
        verticalVisibleBoard=maxBoardSize/2-visibleBoardSize/2;
        horizontalVisibleBoard=maxBoardSize/2-visibleBoardSize/2;

        //planszy
        load(boardTab,maxBoardSize,0,maxBoardSize,"gameBoard",name);

        //ich liter
        load(playersLetters,numberOfPlayers,1,8,"playersLetters",name);

        //czy odkryli
        load(firstLottery,numberOfPlayers,1,8,"firstLottery",name);

        //ich punktow
        for(int i=0;i<numberOfPlayers;i++)
            points[i]=sharedPreferences.getInt(name+"playerPoints"+i,0);

        pl=sharedPreferences.getBoolean("pl",false);
        player=sharedPreferences.getInt("player",1);
        backToMenuOrPanel=true;
        Toast.makeText(getApplicationContext(),"Loaded",Toast.LENGTH_SHORT).show();
    }

    public void load(View view)
    {
        if(savedGamesList!=0) {
            final String []lista = new String[savedGamesList];
            int t=0;
            int i=0;
            while(t<savedGamesList)
            {
                if(gamesList[i].compareTo("")!=0 )
                {
                    lista[t]=gamesList[i];
                    t++;
                }
                i++;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose");
            builder.setItems(lista, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    load(lista[which]);
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

    public void load(char tab[][],int sizeI,int fromJ,int sizeJ, String key,String name)
    {
        for(int i=0;i<sizeI;i++)
            for(int j=fromJ;j<sizeJ;j++)
            {
                String a = key+i+"x"+j;
                tab[i][j]=sharedPreferences.getString(name+a,"0").charAt(0);
            }
    }

    public void loadSavedGamesList()
    {
        sharedPreferences = getSharedPreferences("com.example.urszulaiflorian.rgbtohex", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        for(int i=0;i<maxGamesToSave;i++)
            gamesList[i]="";

        savedGamesList=sharedPreferences.getInt("savedGamesList",0);

        for(int i=0;i<savedGamesList;i++)
            gamesList[i]=sharedPreferences.getString("game"+i,"");
    }

    public  int lottery()
    {
        return new Random().nextInt(101);
    }

    public int max(int a, int b)
    {
        if(a>b)
            return a;
        return b;
    }

    public void menu(View view)
    {
        setContentView(R.layout.menu);
    }

    public void newGame(View view)
    {
        if(getSupportActionBar()!=null)
            getSupportActionBar().hide();

        setContentView(R.layout.loading_screen);

        int DELAY = 200;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                checkBoardSize();
                setPremiumFields();
            }
        }, DELAY);
    }

    public void nextPlayer()
    {
        TextView text = (TextView) findViewById(R.id.textView3);
        text.setText("Player " + player);
        tabToLetter(player);
        TextView ptkgr = (TextView) findViewById(R.id.textView2);
        ptkgr.setText("Points: "+points[player-1]);
        reset();
        setDefaultLettersBackground();
    }

    public void nextPlayer(View view)
    {
        lettersToTab(player);
        player = (player % numberOfPlayers) + 1;
        setContentView(R.layout.podanie);
        TextView text = (TextView) findViewById(R.id.textView5);
        text.setText("Player\n"+player+"\nturn");
    }

    public void nextPlayerPanel(View view)
    {
        setContentView(R.layout.activity_main);
        nextPlayer();
    }

    public void numberOfPlayersConfirmButton(View view)
    {
        EditText text = (EditText) findViewById(R.id.editText);
        if(text.getText()!=null)
        {
            if(text.getText().toString().equals(""))
                Toast.makeText(getApplicationContext(),"Set up number of players!",Toast.LENGTH_SHORT).show();
            else
            {
                numberOfPlayers=Integer.parseInt(text.getText().toString());
                setContentView(R.layout.activity_main);
                backToMenuOrPanel=true;
                if(getSupportActionBar()!=null)
                    getSupportActionBar().hide();
                basicImplementation();
                nextPlayer();
                setDefaultLettersBackground();
            }
        }
    }

    public void parseTabs()
    {
        for(int i=1;i<8;i++)
        {
            lettersTab[i]=button(i).getText().charAt(0);
        }
    }

    public int points(char a)
    {
        if(pl)
            return pointsPL(a);
        return pointsEn(a);
    }

    public int pointsPL(char a)
    {
        switch(a)
        {
            case 'A' :
                return 1;
            case 'B' :
                return 3;
            case 'C' :
                return 2;
            case 'D' :
                return 2;
            case 'E' :
                return 1;
            case 'F' :
                return 5;
            case 'G' :
                return 3;
            case 'H' :
                return 2;
            case 'I' :
                return 1;
            case 'J' :
                return 3;
            case 'K' :
                return 2;
            case 'L' :
                return 2;
            case 'M' :
                return 2;
            case 'N' :
                return 1;
            case 'O' :
                return 1;
            case 'P' :
                return 2;
            case 'R' :
                return 1;
            case 'S' :
                return 1;
            case 'T' :
                return 2;
            case 'U' :
                return 3;
            case 'W' :
                return 1;
            case 'Y' :
                return 2;
            case 'Z' :
                return 1;
        }
        return 0;
    }

    public int pointsEn(char a)
    {
        switch(a)
        {
            case 'A' :
                return 1;
            case 'B' :
                return 3;
            case 'C' :
                return 3;
            case 'D' :
                return 2;
            case 'E' :
                return 1;
            case 'F' :
                return 4;
            case 'G' :
                return 2;
            case 'H' :
                return 4;
            case 'I' :
                return 1;
            case 'J' :
                return 8;
            case 'K' :
                return 5;
            case 'L' :
                return 1;
            case 'M' :
                return 3;
            case 'N' :
                return 1;
            case 'O' :
                return 1;
            case 'P' :
                return 3;
            case 'Q' :
                return 10;
            case 'R' :
                return 1;
            case 'S' :
                return 1;
            case 'T' :
                return 1;
            case 'U' :
                return 1;
            case 'V' :
                return 4;
            case 'W' :
                return 4;
            case 'X' :
                return 8;
            case 'Y' :
                return 4;
            case 'Z' :
                return 10;
        }
        return 0;
    }

    public void polishLanguage(View view) {
        pl=true;
        findViewById(R.id.button30).setBackgroundResource(R.drawable.polishselected);
        findViewById(R.id.button29).setBackgroundResource(R.drawable.english);
    }

    public void positionFunction(int a)
    {
        switch(a)
        {
            case 0:
                kx=sx+1;
                ky=sy;
                break;
            case 1:
                kx=sx;
                ky=sy+1;
                break;
            case 2:
                reset();
                return;
        }
        points[player - 1] += countPoints();
        inputLetters = true;
        fillBoardFields();
    }

    public  void reset()
    {
        for(int i=0;i<8;i++)
            chosenLetters[i]=false;
        inputLetters=false;
        exchange=false;
        choose=false;
        changeLettersPosition=true;
        countLetters=0;
    }

    public void Right(View view)
    {
        if(horizontalVisibleBoard<maxBoardSize-visibleBoardSize)
            horizontalVisibleBoard++;
        board();
        fillBoardFields();
    }

    public void save() //Zapisuje do sharedPreferencess
    {
        int t =0;
        int i=0;
        while(t<savedGamesList)
        {
            if(gamesList[i].compareTo("")!=0)
            {
                editor.putString("game"+t,gamesList[i]);
                t++;
            }
            i++;
        }
        editor.putInt("savedGamesList",savedGamesList);
        editor.commit();
    }

    public void save(String name)
    {
        editor.putInt(name+"visibleBoardSize",visibleBoardSize);

        editor.putInt("numberOfPlayers",numberOfPlayers);

        save(boardTab,maxBoardSize,0,maxBoardSize,"gameBoard",name);

        save(playersLetters,numberOfPlayers,1,8,"playersLetters",name);

        save(firstLottery,numberOfPlayers,1,8,"firstLottery",name);

        for(int i=0;i<numberOfPlayers;i++)
            editor.putInt(name+"playerPoints"+i, points[i]);

        if(!name.equals("last")) {
            //lista gier
            gamesList[savedGamesList] = name;
            editor.putString("game" + savedGamesList, name);
            savedGamesList++;
            editor.putInt("savedGamesList", savedGamesList);
            Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_SHORT).show();
        }

        editor.putBoolean("pl",pl);
        editor.putInt("player",player);
        editor.commit();
    }

    public void save(View view)
    {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog_save, null);
        dialogBuilder.setView(dialogView);
        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);
        dialogBuilder.setTitle("Save");
        dialogBuilder.setMessage("Name: ");
        dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String nazwa = edt.getText().toString();
                        save(nazwa);
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

    public void save(char tab[][],int sizeI,int fromJ,int sizeJ, String key,String name)
    {
        for(int i=0;i<sizeI;i++)
            for(int j=fromJ;j<sizeJ;j++)
            {
                String a=key+i+"x"+j;
                editor.putString(name+a,tab[i][j]+"");
            }
    }

    public void setBackground(int button)
    {
        Button b = button(button);
        if(exchange)
            if(chosenLetters[button])
                b.setBackgroundResource(R.drawable.buttondel);
            else
                b.setBackgroundResource(R.drawable.button);
        else if(choose)
        {
            if (chosenLetters[button])
            {
                b.setBackgroundResource(R.drawable.agg);
                countLetters++;
            }
            else
            {
                b.setBackgroundResource(R.drawable.button);
                countLetters--;
            }
        }
        lettersToTab(player);
    }

    public void setButton( final int x, final int y)
    {
        final MainActivity ve = this;

        boardButtons[x][y].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("-> "+countLetters+" <-Letters");

                if(countLetters == 0)
                {
                    Toast.makeText(getApplicationContext(),"Nothing selected",Toast.LENGTH_SHORT);
                }
                else if (countLetters == 1) {
                    sx=x;
                    sy=y;
                    points[player - 1] += countPoints();
                    inputLetters = true;
                    countLetters=-1;
                    fillBoardFields();
                } else {
                    if (inputLetters && choose) {
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ve);
                        LayoutInflater inflater = ve.getLayoutInflater();
                        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
                        dialogBuilder.setView(dialogView);

                        dialogBuilder.setTitle("Discard letters");
                        dialogBuilder.setMessage("Are you sure?");
                        dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        points[player - 1] -= deletePoints();
                                        fillBoardFields();
                                        choose = false;
                                        Toast.makeText(getApplicationContext(),"Canceled",Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                        dialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                    }
                                }
                        );
                        AlertDialog b = dialogBuilder.create();
                        b.show();
                    } else if(choose){
                        sx=x;
                        sy=y;
                        String [] options = {"Vertical","Horizontal","Cancel"};
                        AlertDialog.Builder optionList = new AlertDialog.Builder(ve);
                        optionList.setTitle("Choose option");
                        optionList.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                positionFunction(which);
                            }
                        });
                        optionList.create();
                        optionList.show();
                    }
                }
            }
        });
    }

    public void setDefaultLettersBackground()
    {
        for(int i=1 ;i<8;i++)
        {
            Button b=button(i);
            b.setBackgroundResource(R.drawable.button);
        }
    }

    public void setPremiumFields()
    {
        for(int i=0;i<maxBoardSize;i++)
            for(int j=0;j<maxBoardSize;j++)
            {
                boardButtons[i][j]= new Button(this);
                boardTab[i][j]='0';
                premiumLetterFields[i][j]=1;
                premiumWordFields[i][j]=1;
            }

        //3 razy litera
        for(int i=1;i<maxBoardSize;i=i+4)
            for(int j=1;j<maxBoardSize;j=j+4)
                premiumLetterFields[i][j]=3;

        //2 razy litera
        for(int i=3;i<maxBoardSize;i=i+4)
            for(int j=3;j<maxBoardSize;j=j+4)
                premiumLetterFields[i][j]=2;
        for(int i=maxBoardSize/2-1;i<maxBoardSize/2+2;i=i+2)
            for(int j=2;j<maxBoardSize/2;j=j+4)
            {
                premiumLetterFields[i][j]=2;
                premiumLetterFields[j][i]=2;
            }
        for(int i=maxBoardSize/2-1;i<maxBoardSize/2+2;i=i+2)
            for(int j=maxBoardSize-3;j>maxBoardSize/2;j=j-4)
            {
                premiumLetterFields[i][j]=2;
                premiumLetterFields[j][i]=2;
            }

        //2 razy słowo
        for(int i=0;i<maxBoardSize;i++)
        {
            if(i<maxBoardSize/2-2 || i >maxBoardSize/2+2)
            {
                premiumWordFields[i][i]=2;
                premiumWordFields[i][maxBoardSize-1-i]=2;
            }
        }

        //3razy słowo
        for(int i=0;i<maxBoardSize;i=i+7)
            for(int j=0;j<maxBoardSize;j=j+7)
                premiumWordFields[i][j]=3;

        premiumWordFields[maxBoardSize/2][maxBoardSize/2]=1;
        premiumLetterFields[maxBoardSize/2][maxBoardSize/2]=1;
    }

    public void settings(View view)
    {
        setContentView(R.layout.settings);
    }

    public void tabToLetter(int x)
    {
        for(int i=1;i<8;i++)
        {
            if(playersLetters[x-1][i]!=0) {
                char a = playersLetters[x-1][i];
                button(i).setText(a + " \n" + points(a));
            }
            else
                button(i).setText("X");
        }
    }

    public void Up(View view)
    {
        if(verticalVisibleBoard>0)
            verticalVisibleBoard--;
        board();
        fillBoardFields();
    }
}