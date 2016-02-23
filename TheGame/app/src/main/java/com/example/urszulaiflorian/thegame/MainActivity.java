package com.example.urszulaiflorian.thegame;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //Deklaracja zmiennych
    public static int max=50; //maksymalna wielkosc tablic
    public static int tab[][]=new int[max][max]; //Tablica gry
    static TableLayout layout; //Layout gry
    public static int q=0; //Ktory gracz
    public static boolean end=false; //Czy koniec, jesli tak to po kliknieciu reset
    public static int XX=10; //Szerokosc
    public static int YY=20; //Wysokosc
    public static int CC=5; //Ile trzeba ulozyc
    public static MediaPlayer winS; //Zmienna dzwiekowa wygranej
    public static MediaPlayer redS; //Zmienna dzwiekowa czerwonego
    public static MediaPlayer blueS; //Zmienna dzwiekowa niebieskiego
    public static int wyjscie=0; //2 nacisniecia back to wychodzi
    public static int kopia[][] = new int[max][max]; //Kopia do zmiany koloru w przypadku wygranej gracza
    public static int cpu[][] = new int[max][max];
    public static int shot[][] = new int[max][max];
    public static int kolumny[] = new int [max]; //Ile zapelnone w danej kolumnie
    public static int runda =0; //Ktora runda
    public static int ptkR =0; //Punkty gracza Red
    public static int ptkB =0; //Punkty Gracza Blue
    public static int ile=0; //Ile pol zapelnionych
    public static int style=0; //Ktory styl
    public static boolean sound=true; //Dzwiek on/off
    public static boolean kto=false; //Player czy CPU
    public static int pom=0; //pomoc zeby cpu strzelilo tylko raz
    public static int p1=1; //gracz 1
    public static int p2=2; //gracz 2
    public static int zaznaczenie=999; //zaznaczenie do usuniecia przycisków w Extreme
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;


    //Czysci wszystkie zmienne, ktore tego wymagaja przy nowej grze
    public void czysc()
    {

        czyscTab(tab);

        for(int i=0;i<max;i++)
            kolumny[i]=0;

        pom=0;
        q = 0;
        wyjscie=0;
        end=false;
        runda=0;
        ptkR=0;
        ptkB=0;
        ile=0;
    }

    //czy jest miejsce na ustalonej planszy
    public boolean czyMiejse()
    {
        return ile<XX*YY;
    }

    //zaznacz niepuste miejsca tablicy a keyem
    public void zaznaczUzyte()
    {
        for(int i=0;i<YY;i++)
            for(int j=0;j<XX;j++)
                if(tab[i][j]!=0)
                    tab[i][j]=zaznaczenie;
    }

    //zakoloruj przyciski na podstawie ulozenia keya w tablicy a
    public void zakolorujUzyte()
    {
        for(int i=0;i<YY;i++)
            for(int j=0;j<XX;j++)
                if(tab[i][j]==zaznaczenie)
                    findViewById(j + (i * XX)).setBackgroundColor(Color.BLACK);
    }

    //Zzeruj tablice a
    public void czyscTab(int a[][])
    {
        for(int i=0;i<YY;i++)
            for(int j=0;j<XX;j++)
                a[i][j]=0;
    }

    //Ustaw "kolor" gdy wartosc a[][] rowna x
    public void zmienKolor(int x, int kolor, int [][]a)
    {
        for(int i=0;i<YY;i++)
            for(int j=0;j<XX;j++)
                if(a[i][j]==x)
                    findViewById(j + (i * XX)).setBackgroundColor(kolor);
    }

    //Czy tab[f][s] jest rowne 0
    public boolean czywolne(int f,int s)
    {
        return tab[f][s] == 0;
    }

    //Zwieksza wartosc kazdego pola z tablicy shot o otoczenie p2 w każdym przycisku
    public void oznaczdostrzalu(int x, int [][]a)
    {
        for(int i=0;i<YY;i++)
        {
            for(int j=0;j<XX;j++)
            {
                if(a[i][j]==x)
                    if(tab[i][j]>2 || tab[i][j]==0)
                    {
                        shot[i][j]+=x;
                    }
            }
        }
    }

    //Metoda koncząca gre
    public void koniecGry()
    {
        Toast toast = Toast.makeText(getApplicationContext(), "Tap the screen to play again", Toast.LENGTH_LONG);

        winS = MediaPlayer.create(getApplicationContext(), R.raw.win);

        if(ptkR>ptkB)
        {
            setTitle("Result: " + ptkR + " : " + ptkB+", Red Won!");
        }
        else if(ptkR<ptkB)
        {
            setTitle("Result: " + ptkR + " : " + ptkB+", Blue Won!");
        }
        else
        {
            setTitle("Draw!");
        }
        runda=0;
        toast.show();
        if(sound)
            winS.start();
        end=true;

    }

    //Sprawdz czy ulozono linie (dla CC)
    public void sprawdz() {

        pion(p1, CC);
        poziom(p1, CC);
        przekatna1( p1, CC);
        przekatna2( p1, CC);

        pion(p2, CC);
        poziom(p2, CC);
        przekatna1(p2, CC);
        przekatna2(p2, CC);

        if(!czyMiejse())
            koniecGry();
    }

    //Mechanizm cpu
    public void ostrzezenie() {
        czyscTab(shot);

        if(linia(p1,tab)<=linia(p2,tab) && linia(p2,tab)>1)
        {
           shot[liniaF(p2,tab)][liniaS(p2,tab)]+=50;
        }

        for(int i=2;i<CC;i++)
        {
            czyscTab(cpu);
            pionOS( i, i*CC);
            czyscTab(cpu);
            poziomOS( i, i*CC);
            czyscTab(cpu);
            przekatna1OS( i, i*CC);
            czyscTab(cpu);
            przekatna2OS( i, i*CC);
        }

        uzupTaboOtoczenie(p1);
        uzupTaboOtoczenie(p2);

        strzal();

    }

    //Ile jest najwięcej w lini key
    public int linia(int key,int a[][])
    {
        int tmp=0;

        for(int i=0;i<YY;i++)
            for(int j=0;j<XX;j++)
                if(a[i][j]==0)
                    if(tmp<=dopelnienie(i,j,key,a))
                        tmp=dopelnienie(i,j,key,a);

        return tmp;
    }

    //Pierwsza wspolrzedna wolnego z najwiecej w linii key
    public int liniaF(int key,int [][]a)
    {
        int tmp=0;
        int x=0;

        for(int i=0;i<YY;i++)
            for(int j=0;j<XX;j++)
                if(a[i][j]==0)
                    if(tmp<=dopelnienie(i,j,key,a))
                    {
                        tmp=dopelnienie(i,j,key,a);
                        x=i;
                    }

        return x;
    }

    //Druga wspolrzedna wolnego z najwiecej w linii key
    public int liniaS(int key,int[][]a)
    {
        int tmp=0;
        int x=0;

        for(int i=0;i<YY;i++)
            for(int j=0;j<XX;j++)
                if(a[i][j]==0)
                    if(tmp<=dopelnienie(i,j,key,a))
                    {
                        tmp=dopelnienie(i,j,key,a);
                        x=j;
                    }

        return x;
    }

    //Czy jest gdzies: puste,CC-1 x key,puste // Mechanizm do atakowania dla CPU
    public int dopelnienie(int f, int s, int key,int[][]a)
    {
        int tmp;
        int pm1=0;
        int pm2=0;
        int pm3=0;
        int pm4=0;
        int j=1;

        do{
            if(s + j < XX)
            {
                if(a[f][s+j]==key)
                    pm1++;
                else if(a[f][s+j]!=0)
                {
                    pm1=0;
                    break;
                }
                else
                    break;
            }
            else
            {
                pm1=0;
                break;
            }
            j++;
        }while(true);

        j=1;

        do{
            if(f + j < YY)
            {
                if(a[f+j][s]==key)
                    pm2++;
                else if(a[f+j][s]!=0)
                {
                    pm2=0;
                    break;
                }
                else
                    break;
            }
            else
            {
                pm2=0;
                break;
            }
            j++;
        }while(true);

        j=1;

        do{
            if(f + j < YY && s+j < XX)
            {
                if(a[f+j][s+j]==key)
                    pm3++;
                else if(a[f+j][s+j]!=0)
                {
                    pm3=0;
                    break;
                }
                else
                    break;
            }
            else
            {
                pm3=0;
                break;
            }
            j++;
        }while(true);

        j=1;

        do{
            if(f >=j && s+j < XX)
            {
                if(a[f-j][s+j]==key)
                    pm4++;
                else if(a[f-j][s+j]!=0)
                {
                    pm4=0;
                    break;
                }
                else
                    break;
            }
            else
            {
                pm4=0;
                break;
            }
            j++;
        }while(true);

        tmp = max(pm1, (max(pm2, max(pm3, pm4))));

        return tmp;
    }

    //Maksymalna wartosc w tablicy a wielkosci pxq, oprocz zaznaczenia
    public int maxOfTab(int a[][])
    {
        int max=0;

        for(int i=0;i<YY;i++)
            for(int j=0;j<XX;j++)
                if(max<a[i][j] && a[i][j]!=zaznaczenie)
                    max=a[i][j];

        return max;
    }

    //Pierwsza wspilrzedna pola Maksymalna wartosc w tablicy a wielkosci pxq, oprocz zaznaczenia
    public int fOfC(int a[][], int c)
    {
        int max=-1;

        for(int i=0;i<YY;i++)
            for(int j=0;j<XX;j++)
                if(c==a[i][j])
                    return i;

        return max;
    }

    //Druga wspilrzedna pola Maksymalna wartosc w tablicy a wielkosci pxq, oprocz zaznaczenia
    public int sOfC(int a[][], int c)
    {
        int max=-1;

        for(int i=0;i<YY;i++)
            for(int j=0;j<XX;j++)
                if(c==a[i][j])
                    return j;

        return max;
    }

    //Proste maksimum z dwóch elementów
    public int max(int a, int b)
    {
        if(a>b)
            return a;
        else
            return b;
    }

    //uzupelnia tablice a kazdym otoczeniem z tablicy b
    public void uzupTaboOtoczenie(int key)
    {
        for(int i=0;i<YY;i++)
            for(int j=0;j<XX;j++)
            {
                if(tab[i][j]==0)
                    shot[i][j]+=otoczenie(i,j,key);
            }

    }

    //Maksymalne otoczenie z key w polu f,s tablic a o wielkosci pxq
    public int otoczenie(int f, int s, int key)
    {
        int tmp=0;
        int pm1=0;
        int pm2=0;
        int pm3=0;
        int pm4=0;

        for(int i=0;i<CC;i++) {
            for (int j = 0-i; j < CC-i; j++) {
                if (j != 0) {
                    if (s + j >= 0 && s + j < XX)
                        if (tab[f][s + j] == key) {
                            pm1++;
                        }
                        else if(tab[f][s+j]!=0)
                            pm1=0;
                    if (f + j >= 0 && f + j < YY)
                        if (tab[f + j][s] == key) {
                            pm2++;
                        }
                        else if(tab[f+j][s]!=0)
                            pm2=0;
                    if (f + j >= 0 && s + j >= 0 && f + j < YY && s + j < XX)
                        if (tab[f + j][s + j] == key) {
                            pm3++;
                        }
                        else if(tab[f+j][s+j]!=0)
                            pm3=0;
                    if (f >= j && s + j >= 0 && f - j < YY && s + j < XX)
                        if (tab[f - j][s + j] == key) {
                            pm4++;
                        }
                        else if(tab[f-j][s+j]!=0)
                            pm4=0;
                }
            }
            if(tmp < max(pm1, (max(pm2, max(pm3, pm4)))))
                tmp = max(pm1, (max(pm2, max(pm3, pm4))));

            pm1=0;
            pm2=0;
            pm3=0;
            pm4=0;
        }

        if (tmp == CC-1)
            tmp=200*key;

        return tmp;
    }

    public void strzal() {

        pom++;

        printTab(shot, XX, YY);



        //Blok
        if(maxOfTab(shot)>1)
        {
            findViewById(sOfC(shot, maxOfTab(shot)) + (fOfC(shot, maxOfTab(shot)) * XX)).setBackgroundColor(Color.BLUE);
            q++;
            tab[fOfC(shot, maxOfTab(shot))][sOfC(shot, maxOfTab(shot))] = p2;
            setTitle("Red is next");
            ile++;
            if (sound)
                blueS.start();
            wyjscie = 0;
            sprawdz();
        }
        //Start
        else
        {
            Random generator = new Random();
            int i;
            int j;
            int x = fOfC(tab, p1);
            int y = sOfC(tab,p1);
            do {
                i = generator.nextInt(3) - 1;
                j = generator.nextInt(3) - 1;
            }while(i+x<0 || i+x>=YY || j+y<0 || j+y>=XX || !czywolne(x+i,j+y));

                findViewById(y+j + ((x + i) * XX)).setBackgroundColor(Color.BLUE);
                q++;
                tab[x+i][y+j] = p2;
                setTitle("Red is next");
                ile++;
                if (sound)
                    blueS.start();
                wyjscie = 0;
                sprawdz();
        }
    }

    //Funkcja konczaca pojedynek
    public void koniec(int x)
    {
        Toast toast;
        winS = MediaPlayer.create(getApplicationContext(), R.raw.win);

        if(runda==0)
            if(x==p1)
            {
                zmienKolor(p1,Color.MAGENTA,kopia);
                setTitle("Red Won!");
                toast = Toast.makeText(getApplicationContext(),"Tap the screen to play again",Toast.LENGTH_LONG);
            }
            else
            {
                zmienKolor(p2,Color.CYAN,kopia);
                setTitle("Blue Won!");
                toast = Toast.makeText(getApplicationContext(),"Tap the screen to play again",Toast.LENGTH_LONG);
            }
        else
        {
            if(x==p1)
            {
                zmienKolor(p1, Color.MAGENTA, kopia);
                ptkR++;
                setTitle("Red won "+runda+" Round! ("+ptkR+":"+ptkB+")");
                toast = Toast.makeText(getApplicationContext(),"Tap the screen to play next Round",Toast.LENGTH_LONG);
            }
            else
            {
                zmienKolor(p2, Color.CYAN, kopia);
                ptkB++;
                setTitle("Blue won "+runda+" Round! ("+ptkR+":"+ptkB+")");
                toast = Toast.makeText(getApplicationContext(),"Tap the screen to play next Round",Toast.LENGTH_LONG);
            }

        }

        toast.show();
        if(sound)
            winS.start();

        end=true;
    }


    //
    public void pionOS( int ile,int zaznacz)
    {
        int tmp=0;
        for(int i=0;i<=YY-ile;i++)
        {
            for(int j=0;j<XX;j++)
            {
                for(int k=0;k<ile;k++)
                {
                    if(tab[i+k][j]==p1)
                    {
                        tmp++;
                        cpu[i+k][j]=ile;
                    }
                }
                if(tmp == ile)
                {
                    zaznaczPion(ile,zaznacz);
                }
                tmp=0;
                czyscTab(cpu);
            }
        }
    }

    public void zaznaczPion( int ile, int zaznacz)
    {
        for(int i=0;i<=YY-ile;i++)
        {
            for(int j=0;j<XX;j++)
            {
                if(cpu[i][j]==ile)
                {
                    if(i>0 && tab[i-1][j]==0)
                    {
                        cpu[i-1][j]=zaznacz;
                        oznaczdostrzalu(zaznacz, cpu);
                    }
                    if(i+1<YY && tab[i+1][j]==0)
                    {
                        cpu[i+1][j]=zaznacz;
                        oznaczdostrzalu(zaznacz, cpu);
                    }
                }
            }
        }
    }

    //
    public void zaznaczPoziom(int ile,int zaznacz)
    {
        for(int i=0;i<=YY-ile;i++)
        {
            for(int j=0;j<XX;j++)
            {
                if(cpu[i][j]==ile)
                {
                    if(j>0 && tab[i][j-1]==0)
                    {
                        cpu[i][j-1]=zaznacz;
                        oznaczdostrzalu(zaznacz, cpu);
                    }
                    if(j+1<XX && tab[i][j+1]==0)
                    {
                        cpu[i][j+1]=zaznacz;
                        oznaczdostrzalu(zaznacz, cpu);
                    }
                }
            }
        }
    }

    //
    public void poziomOS( int ile,int zaznacz)
    {
        int tmp=0;
        for(int i=0;i<=YY;i++)
        {
            for(int j=0;j<=XX-ile;j++)
            {
                for(int k=0;k<ile;k++)
                {
                    if(czyKey(i,j+k,p1))
                    {
                        tmp++;
                        cpu[i][j+k]=ile;
                    }
                }
                if(tmp==ile)
                    zaznaczPoziom(ile,zaznacz);
                tmp=0;
                czyscTab(cpu);
            }
        }
    }

    //
    public void zaznaczPrzekatna1( int ile,int zaznacz)
    {
        for(int i=0;i<=YY-ile;i++)
        {
            for(int j=0;j<XX;j++)
            {
                if(cpu[i][j]==ile)
                {
                    if(j>0 && i>0 && tab[i-1][j-1]==0)
                    {
                        cpu[i-1][j-1]=zaznacz;
                        oznaczdostrzalu(zaznacz, cpu);
                    }
                    if(j+1<q && i+1<YY && tab[i+1][j+1]==0)
                    {
                        cpu[i+1][j+1]=zaznacz;
                        oznaczdostrzalu(zaznacz, cpu);
                    }
                }
            }
        }
    }

    //
    public void przekatna1OS( int ile,int zaznacz)
    {
        int tmp=0;
        for(int i=0;i<=YY-ile;i++) {
            for (int j = 0; j <= XX-ile; j++) {
                for (int k = 0; k < ile; k++) {
                    if (czyKey(i + k, j + k, p1))
                    {
                        tmp++;
                        cpu[i+k][j+k]=ile;

                    }
                }
                if (tmp == ile)
                    zaznaczPrzekatna1(ile, zaznacz);
                tmp = 0;
                czyscTab(cpu);
            }
        }
    }

    //
    public void zaznaczPrzekatna2( int ile,int zaznacz)
    {
        for(int i=0;i<=YY-ile;i++)
        {
            for(int j=0;j<XX;j++)
            {
                if(cpu[i][j]==ile)
                {
                    if(j+1<q && i>0 && tab[i-1][j+1]==0)
                    {
                        cpu[i-1][j+1]=zaznacz;
                        oznaczdostrzalu(zaznacz,cpu);
                    }
                    if(j>0 && i+1<YY && tab[i+1][j-1]==0)
                    {
                        cpu[i+1][j-1]=zaznacz;
                        oznaczdostrzalu(zaznacz, cpu);
                    }
                }
            }
        }
    }

    //
    public void przekatna2OS( int ile,int zaznacz)
    {
        int tmp=0;
        for(int i=0;i<=YY;i++) {
            for (int j = 0; j <= XX; j++) {
                for (int k = 0; k < ile; k++) {
                    if(j-k>=0)
                        if (czyKey(i + k, j - k, p1))
                        {
                            tmp++;
                            cpu[i+k][j-k]=ile;
                        }
                }
                if (tmp == ile)
                    zaznaczPrzekatna2(ile, zaznacz);
                tmp = 0;
                czyscTab(cpu);
            }
        }
    }

    //Sprawdz pion
    public void pion(int key,int ile)
    {
        int tmp=0;
        for(int i=0;i<=YY-ile;i++) {
            for (int j = 0; j < XX; j++)
            {
                for (int k = 0; k < ile; k++)
                {
                    if (czyKey(i + k, j, key))
                    {
                        tmp++;
                        kopia[i+k][j]=key;
                    }
                }
                if (tmp == ile)
                    koniec(key);
                tmp = 0;
                czyscTab(kopia);
            }
        }
    }

    //Sprawedz poziom
    public void poziom(int key,int ile)
    {
        int tmp=0;
        for(int i=0;i<=YY;i++)
        {
            for(int j=0;j<=XX-ile;j++)
            {
                for(int k=0;k<ile;k++)
                {
                    if(czyKey(i,j+k,key))
                    {
                        tmp++;
                        kopia[i][j+k]=key;
                    }
                }
                if(tmp==ile)
                    koniec(key);
                tmp=0;
                czyscTab(kopia);
            }
        }
    }

    //sprawdz przekatna od lewej do prawej
    public void przekatna1(int key,int ile)
    {
        int tmp=0;
        for(int i=0;i<=YY-ile;i++) {
            for (int j = 0; j <= XX-ile; j++) {
                for (int k = 0; k < ile; k++) {
                    if (czyKey(i + k, j + k, key))
                    {
                        tmp++;
                        kopia[i+k][j+k]=key;
                    }
                }
                if (tmp == ile)
                    koniec(key);
                tmp = 0;
                czyscTab(kopia);
            }
        }
    }


    //sprawdz przekatna od prawej do lewej
    public void przekatna2(int key,int ile)
    {
        int tmp=0;
        for(int i=0;i<=YY;i++) {
            for (int j = 0; j <= XX; j++) {
                for (int k = 0; k < ile; k++) {
                    if(j-k>=0)
                        if (czyKey(i + k, j - k, key))
                        {
                            tmp++;
                            kopia[i+k][j-k]=key;
                        }
                }
                if (tmp == ile)
                    koniec(key);
                tmp = 0;
                czyscTab(kopia);
            }
        }
    }

    //sprawdz czy tab[a][b] rowny key
    public boolean czyKey(int a,int b, int key)
    {
        return tab[a][b] == key;
    }

    //wyznacza wspolrzedna y kolumny x, gdzie jest wolne miejsce
    public int wolnePole(int kolumna)
    {

        return YY-1-kolumny[kolumna];
    }

    //Drukuje podtablice a[][] wielkosci x,y
    public void printTab(int[][] a, int x, int y) {
        for (int i = 0; i < y; i++) {
            for (int j = 0; j < x; j++) {
                System.out.print(a[i][j]+" ");
            }
            System.out.println();
        }
    }

    //Metoda do przytrzymania przycisku
    public void BtntagLongClick(final Button btnTag)
    {
        btnTag.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setContentView(R.layout.activity_main);
                czysc();
                aktualizacjaDanych();
                return true;
            }
        });
    }

    //Metoda to CC in Row
    public void BtntagInRow(Button btnTag, final int finalJ)
    {
        btnTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!end) {
                    if (q % 2 == 0) {
                        if (wolnePole(finalJ) != -1) {
                            q++;
                            tab[wolnePole(finalJ)][finalJ] = p1;
                            setTitle("Blue is next");
                            if (wolnePole(finalJ) != -1)
                                findViewById(finalJ + (wolnePole(finalJ) * XX)).setBackgroundColor(Color.RED);
                            if (sound)
                                redS.start();
                            kolumny[finalJ]++;
                            wyjscie = 0;
                            ile++;
                            sprawdz();
                            if (kto && !end) {
                                ostrzezenie();
                            }
                        } else
                            setTitle("Red is next. Error full column");
                    } else {
                        if (wolnePole(finalJ) != -1) {
                            q++;
                            ile++;
                            tab[wolnePole(finalJ)][finalJ] = p2;
                            setTitle("Red is next");
                            if (wolnePole(finalJ) != -1)
                                findViewById(finalJ + (wolnePole(finalJ) * XX)).setBackgroundColor(Color.BLUE);
                            if (sound)
                                blueS.start();
                            kolumny[finalJ]++;
                            wyjscie = 0;
                            sprawdz();
                        } else
                            setTitle("Blue is next. Error full column");
                    }
                } else
                    Game1();
            }
        });
    }

    //Metoda do CC in Row Extreme
    public void BtntagInRowX(Button btnTag, final int finalI, final int finalJ)
    {
        btnTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!end) {
                    if (tab[finalI][finalJ] != 3)
                        if (q % 2 == 0) {
                            if (wolnePole(finalJ) != -1) {
                                q++;
                                tab[wolnePole(finalJ)][finalJ] = p1;
                                setTitle("Blue is next, Round: " + runda + ", (" + ptkR + ":" + ptkB + ")");
                                if (wolnePole(finalJ) != -1)
                                    findViewById(finalJ + (wolnePole(finalJ) * XX)).setBackgroundColor(Color.RED);
                                if (sound)
                                    redS.start();
                                kolumny[finalJ]++;
                                wyjscie = 0;
                                ile++;
                                sprawdz();
                                if (kto && !end) {
                                    ostrzezenie();
                                }
                            } else
                                setTitle("Red is next. Error full column, , Round: " + runda + ", (" + ptkR + ":" + ptkB + ")");
                        } else {

                            if (wolnePole(finalJ) != -1) {
                                q++;
                                tab[wolnePole(finalJ)][finalJ] = p2;
                                setTitle("Red is next, Round: " + runda + ", (" + ptkR + ":" + ptkB + ")");
                                if (wolnePole(finalJ) != -1)
                                    findViewById(finalJ + (wolnePole(finalJ) * XX)).setBackgroundColor(Color.BLUE);
                                if (sound)
                                    blueS.start();
                                kolumny[finalJ]++;
                                wyjscie = 0;
                                ile++;
                                sprawdz();
                            } else
                                setTitle("Blue is next. Error full column, Round: " + runda + ", (" + ptkR + ":" + ptkB + ")");
                        }
                } else
                    Game3();
            }
        });
    }

    //Metoda do Kolko i krzyzyk
    public void BtntagTicTacToe(Button btnTag, final int finalI, final int finalJ)
    {
        btnTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!end) {
                    if (tab[finalI][finalJ] == 0)
                        if (q % 2 == 0) {
                            q++;
                            pom = 0;
                            ile++;
                            tab[finalI][finalJ] = p1;
                            setTitle("Blue is next");
                            findViewById(finalJ + (finalI * XX)).setBackgroundColor(Color.RED);
                            if (sound)
                                redS.start();
                            wyjscie = 0;
                            sprawdz();
                            if (kto && !end)
                                ostrzezenie();
                        } else {
                            q++;
                            ile++;
                            tab[finalI][finalJ] = p2;
                            setTitle("Red is next");
                            findViewById(finalJ + (finalI * XX)).setBackgroundColor(Color.BLUE);
                            if (sound)
                                blueS.start();
                            wyjscie = 0;
                            sprawdz();
                        }
                } else
                    Game2();
            }
        });
    }

    //Metoda do kolko i krzyzyk extreme
    public void BtntagTicTacToeX(Button btnTag,final int finalI, final int finalJ)
    {
        btnTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!end)
                {
                    if (tab[finalI][finalJ] == 0)
                        if (q % 2 == 0) {
                            q++;
                            pom=0;
                            tab[finalI][finalJ] = p1;
                            setTitle("Blue is next, Round: " + runda + ", (" + ptkR + ":" + ptkB + ")");
                            findViewById(finalJ + (finalI * XX)).setBackgroundColor(Color.RED);
                            if(sound)
                                redS.start();
                            wyjscie=0;
                            ile++;
                            sprawdz();
                            if(kto && !end)
                                ostrzezenie();
                        } else {
                            q++;
                            tab[finalI][finalJ] = p2;
                            setTitle("Red is next, Round: " + runda + ", (" + ptkR + ":" + ptkB + ")");
                            findViewById(finalJ + (finalI * XX)).setBackgroundColor(Color.BLUE);
                            if(sound)
                                blueS.start();
                            wyjscie=0;
                            ile++;
                            sprawdz();
                        }
                }
                else
                    Game4();
            }
        });
    }

    //Gramy w CC w linii
    public void Game1()
    {
        //Czysci do zera co trzeba
        czysc();

        //Wysokosc ActionBar
        int actionBarHeight=11;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        //Deklaracja zmiennych, ktorych wczesniej nie moglem zadeklarowac
        Toast toast = Toast.makeText(getApplicationContext(), "\n\nCreate line of " + CC + " rectangles to win \nRed starts\nHold a screen to go back to menu\n\n", Toast.LENGTH_LONG);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        TableLayout.LayoutParams layoutPar  = new TableLayout.LayoutParams(size.x,size.y-actionBarHeight);
        redS = MediaPlayer.create(getApplicationContext(), R.raw.red);
        blueS = MediaPlayer.create(getApplicationContext(), R.raw.blue);
        layout = new TableLayout(this);
        layout.setLayoutParams(layoutPar);
        setContentView(layout);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();

        //Stop jesli muzyczka wygranej dalej dziala
        if(winS!=null)
            winS.stop();

        //Pokazanie ActionBar
        if(bar!=null)
            bar.show();

        //Ustawienie tytulu i pokazanie wiadomosci wstepnej
        setTitle("Red & Blue");
        toast.show();

        //Wstawienie przyciskow i ich funkcji
        for (int i = 0; i < YY; i++) {
            TableRow row = new TableRow(this);
            row.setBackgroundColor(Color.WHITE);
            for (int j = 0; j < XX; j++) {
                Button btnTag = new Button(this);
                btnTag.setId(j + (i * XX));
                if(style==1)
                    btnTag.setBackgroundResource(R.drawable.bgbtnr);

                BtntagLongClick(btnTag);

                BtntagInRow(btnTag, j);

                row.addView(btnTag,size.x/XX,(size.y-actionBarHeight)/YY-2);
            }
            layout.addView(row);
        }

    }

    //Gramy w kolko i krzyzyk
    public void Game2()
    {

        //Czysci do zera co trzeba
        czysc();

        //Wysokosc ActionBar
        int actionBarHeight = 11;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        //Deklaracja zmiennych, ktorych wczesniej nie moglem zadeklarowac
        Toast toast = Toast.makeText(getApplicationContext(), "\n\nCreate line of " + CC + " rectangles to win \nRed starts\nHold a screen to go back to menu\n\n", Toast.LENGTH_LONG);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        TableLayout.LayoutParams layoutPar  = new TableLayout.LayoutParams(size.x,size.y-actionBarHeight);
        redS = MediaPlayer.create(getApplicationContext(), R.raw.red);
        blueS = MediaPlayer.create(getApplicationContext(), R.raw.blue);
        layout = new TableLayout(this);
        layout.setLayoutParams(layoutPar);
        setContentView(layout);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();

        //Stop jesli muzyczka wygranej dalej dziala
        if(winS!=null)
            winS.stop();

        //Pokazanie ActionBar
        if(bar!=null)
            bar.show();

        //Ustawienie tytulu i pokazanie wiadomosci wstepnej
        setTitle("Red & Blue");
        toast.show();

        //Wstawienie przyciskow i ich funkcji
        for (int i = 0; i < YY; i++) {
            TableRow row = new TableRow(this);
            row.setBackgroundColor(Color.WHITE);
            for (int j = 0; j < XX; j++) {
                Button btnTag = new Button(this);
                btnTag.setId(j + (i * XX));

                if(style==1)
                    btnTag.setBackgroundResource(R.drawable.bgbtnr);

                BtntagLongClick(btnTag);

                BtntagTicTacToe(btnTag, i, j);

                row.addView(btnTag,size.x/XX,(size.y-actionBarHeight)/YY-2);
            }
            layout.addView(row);
        }
    }

    //Gramy w N w linii Extreme
    public void Game3()
    {
        Toast toast;

        //Czysci do zera kiedy trzeba
        if(runda==0)
        {
            czysc();
            toast = Toast.makeText(getApplicationContext(), "\n\nCreate line of "+CC+" rectangles to win \nRed starts\nHold a screen to go back to menu\n\n", Toast.LENGTH_LONG);
            setTitle("Red & Blue, Round "+(runda+1)+", ("+ptkR+":"+ptkB+")");
        }
        else
        {
            end=true;
            if(q%2==0)
            {
                toast = Toast.makeText(getApplicationContext(), "\n\nNext Round! \nRed starts\nHold a screen to go back to menu\n\n", Toast.LENGTH_LONG);
                setTitle("Red is next, Round "+(runda+1)+", ("+ptkR+":"+ptkB+")");
            }
            else
            {
                toast = Toast.makeText(getApplicationContext(), "\n\nNext Round! \nBlue starts\nHold a screen to go back to menu\n\n", Toast.LENGTH_LONG);
                setTitle("Blue is next, Round "+(runda+1)+", ("+ptkR+":"+ptkB+")");
            }

        }
        runda++;

        //Wysokosc ActionBar
        int actionBarHeight=11;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        //Deklaracja zmiennych, ktorych wczesniej nie moglem zadeklarowac
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        TableLayout.LayoutParams layoutPar  = new TableLayout.LayoutParams(size.x,size.y-actionBarHeight);
        redS = MediaPlayer.create(getApplicationContext(), R.raw.red);
        blueS = MediaPlayer.create(getApplicationContext(), R.raw.blue);
        layout = new TableLayout(this);
        layout.setLayoutParams(layoutPar);
        layout.setBackgroundColor(Color.BLACK);
        setContentView(layout);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();

        //Stop jesli muzyczka wygranej dalej dziala
        if(winS!=null)
            winS.stop();

        //Pokazanie ActionBar
        if(bar!=null)
            bar.show();

        //pokazanie wiadomosci wstepnej
        toast.show();

        //Wstawienie przyciskow i ich funkcji
        for (int i = 0; i < YY; i++) {
            TableRow row = new TableRow(this);
            row.setBackgroundColor(Color.WHITE);
            for (int j = 0; j < XX; j++) {
                Button btnTag = new Button(this);
                btnTag.setId(j + (i * XX));
                if(style==1)
                    btnTag.setBackgroundResource(R.drawable.bgbtnr);

                BtntagLongClick(btnTag);

                BtntagInRowX(btnTag, i, j);

                row.addView(btnTag,size.x/XX,(size.y-actionBarHeight)/YY-2);
            }
            layout.addView(row);
        }
        zaznaczUzyte();
        zakolorujUzyte();
    }

    //Gramy w kolko i krzyzyk Extreme
    public void Game4()
    {
        Toast toast;

        //Czysci do zera kiedy trzeba
        if(runda==0)
        {
            czysc();
            toast = Toast.makeText(getApplicationContext(), "\n\nCreate line of "+CC+" rectangles to win \nRed starts\nHold a screen to go back to menu\n\n", Toast.LENGTH_LONG);
            setTitle("Red & Blue, Round "+(runda+1)+", ("+ptkR+":"+ptkB+")");
        }
        else
        {
            end=true;
            if(q%2==0)
            {
                toast = Toast.makeText(getApplicationContext(), "\n\nNext Round! \nRed starts\nHold a screen to go back to menu\n\n", Toast.LENGTH_LONG);
                setTitle("Red is next, Round "+(runda+1)+", ("+ptkR+":"+ptkB+")");
            }
            else
            {
                toast = Toast.makeText(getApplicationContext(), "\n\nNext Round! \nBlue starts\nHold a screen to go back to menu\n\n", Toast.LENGTH_LONG);
                setTitle("Blue is next, Round "+(runda+1)+", ("+ptkR+":"+ptkB+")");
            }

        }
        runda++;

        //Wysokosc ActionBar
        int actionBarHeight=11;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        //Deklaracja zmiennych, ktorych wczesniej nie moglem zadeklarowac
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        TableLayout.LayoutParams layoutPar  = new TableLayout.LayoutParams(size.x,size.y-actionBarHeight);
        redS = MediaPlayer.create(getApplicationContext(), R.raw.red);
        blueS = MediaPlayer.create(getApplicationContext(), R.raw.blue);
        layout = new TableLayout(this);
        layout.setLayoutParams(layoutPar);
        layout.setBackgroundColor(Color.BLACK);
        setContentView(layout);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();

        //Stop jesli muzyczka wygranej dalej dziala
        if(winS!=null)
            winS.stop();


        //Pokazanie ActionBar
        if(bar!=null)
            bar.show();

        //pokazanie wiadomosci wstepnej
        toast.show();

        //Wstawienie przyciskow i ich funkcji
        for (int i = 0; i < YY; i++) {
            TableRow row = new TableRow(this);
            row.setBackgroundColor(Color.WHITE);
            for (int j = 0; j < XX; j++) {
                Button btnTag = new Button(this);
                btnTag.setId(j + (i * XX));
                if (style == 1)
                    btnTag.setBackgroundResource(R.drawable.bgbtnr);

                BtntagLongClick(btnTag);

                BtntagTicTacToeX(btnTag, i, j);

                row.addView(btnTag,size.x/XX,(size.y-actionBarHeight)/YY-2);
            }
            layout.addView(row);
        }
        zaznaczUzyte();
        zakolorujUzyte();
    }

    //Metoda powtoru do glownego menu
    public void powrot(View view) {
        setContentView(R.layout.activity_main);
        aktualizacjaDanych();
    }

    //Metoda do rozpoczenia gry pierwszej - CC in Row
    public void GameOne(View view) {
        if (kto && !end)
        {
            Toast toast = Toast.makeText(getApplicationContext(), "Not available yet", Toast.LENGTH_SHORT);
            toast.show();
        }
        else
        {
            czysc();
            Game1();
        }
    }

    //Kolejne instrukcje do CC in Row
    //1
    public void infog1A(View view) {
        setContentView(R.layout.infog1_one);
        aktualizacjaDanych();
    }
    //2
    public void infog1B(View view) {
        setContentView(R.layout.infog1_two);
        aktualizacjaDanych();
    }
    //3
    public void infog1C(View view) {
        setContentView(R.layout.infog1_three);
        aktualizacjaDanych();
    }

    //Metoda do rozpoczecia gry drugiej - kolko i krzyzyk
    public void GameTwo(View view) {
        czysc();
        Game2();
    }

    //Instrukcje do Kolko i krzyzyk
    //1
    public void infog2A(View view) {
        setContentView(R.layout.infog2_one);
        aktualizacjaDanych();
    }
    //2
    public void infog2B(View view) {
        setContentView(R.layout.infog2_two);
        aktualizacjaDanych();
    }
    //3
    public void infog2C(View view) {
        setContentView(R.layout.infog2_three);
        aktualizacjaDanych();
    }

    //Metoda do rozpoczecia gry trzeciej - CC in row extreme
    public void GameThree(View view) {
        if (kto && !end)
        {
            Toast toast = Toast.makeText(getApplicationContext(), "Not available yet", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            czysc();
            Game3();
        }
    }

    //Instrukcje do CC in row extreme
    //1
    public void infog3A(View view) {
        setContentView(R.layout.infog3_one);
        aktualizacjaDanych();
    }
    //2
    public void infog3B(View view) {
        setContentView(R.layout.infog3_two);
        aktualizacjaDanych();
    }
    //3
    public void infog3C(View view) {
        setContentView(R.layout.infog3_three);
        aktualizacjaDanych();
    }
    //4
    public void infog3D(View view) {
        setContentView(R.layout.infog3_four);
        aktualizacjaDanych();
    }
    //5
    public void infog3E(View view) {
        setContentView(R.layout.infog3_five);
        aktualizacjaDanych();
    }
    //6
    public void infog3F(View view) {
        setContentView(R.layout.infog3_six);
        aktualizacjaDanych();
    }

    //Metoda do rozpoczecia gry czwartej - kolko i krzyzyk extreme
    public void GameFour(View view) {

        if (kto && !end)
        {
            Toast toast = Toast.makeText(getApplicationContext(), "Not available yet", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            czysc();
            Game4();
        }
    }

    //Instrukcje do kolko i krzyzyk extreme
    //1
    public void infog4A(View view) {
        setContentView(R.layout.infog4_one);
        aktualizacjaDanych();
    }
    //2
    public void infog4B(View view) {
        setContentView(R.layout.infog4_two);
        aktualizacjaDanych();
    }
    //3
    public void infog4C(View view) {
        setContentView(R.layout.infog4_three);
        aktualizacjaDanych();
    }
    //4
    public void infog4D(View view) {
        setContentView(R.layout.infog4_four);
        aktualizacjaDanych();
    }
    //5
    public void infog4E(View view) {
        setContentView(R.layout.infog4_five);
        aktualizacjaDanych();
    }
    //6
    public void infog4F(View view) {
        setContentView(R.layout.infog4_six);
        aktualizacjaDanych();
    }

    //Przycisk do ustawien gry karty pierwszej
    public void gameSettings(View view) {

        setContentView(R.layout.settings_layout);

        aktualizacjaDanych();
    }

    //Zmiana wielkosci planszy na 10x20
    public void changeSize1(View view) {
        XX=10;
        YY=20;

        aktualizacjaDanych();
    }

    //Zmiana wielkosci planszy na 15x25
    public void changeSize2(View view) {
        XX=15;
        YY=25;

        aktualizacjaDanych();
    }

    //Zmiana wielkosci planszy na 20x30
    public void changeSize3(View view) {

        XX=20;
        YY=30;
        aktualizacjaDanych();
    }

    //Zmiana wielkosci CC na 4
    public void changeCC1(View view) {

        CC=4;
        aktualizacjaDanych();
    }

    //Zmiana wielkosci CC na 5
    public void changeCC2(View view) {

        CC=5;
        aktualizacjaDanych();
    }

    //Zmiana wielkosci CC na 6
    public void changeCC3(View view) {

        if (kto && !end)
        {
            Toast toast = Toast.makeText(getApplicationContext(), "With CPU only less or equal 5 is available", Toast.LENGTH_SHORT);
            toast.show();
        }
        else
        {
            CC=6;
            aktualizacjaDanych();
        }

    }

    //Przycisk do ustawien gry karty drugiej
    public void nextPage(View view) {

        setContentView(R.layout.settings_next);
        aktualizacjaDanych();
    }

    //Zmiana wygladu na Styl 1
    public void changeStyle1(View view) {
        style=0;
        setContentView(R.layout.settings_next);
        aktualizacjaDanych();
    }

    //Zmiana wygladu na Styl 2
    public void changeStyle2(View view) {
        //Toast toast = Toast.makeText(getApplicationContext(), "Not available yet", Toast.LENGTH_SHORT);
        //toast.show();
        style=1;
        aktualizacjaDanych();
    }

    //Zmiana wygladu na Styl 3
    public void changeStyle3(View view) {

        Toast toast = Toast.makeText(getApplicationContext(), "Not available yet", Toast.LENGTH_SHORT);
        toast.show();
        aktualizacjaDanych();
    }

    //Dzwiek wylacz
    public void changeSoundON(View view) {
        if(!sound)
            sound=true;
        aktualizacjaDanych();
    }

    //Dzwiek wlacz
    public void changeSoundOFF(View view) {
        if(sound)
            sound=false;
        aktualizacjaDanych();
    }

    //O Autorze
    public void fabout(View view) {
        setContentView(R.layout.about);
        aktualizacjaDanych();
    }

    //Zdjecie napinki
    public void zdjecie(View view) {
        setContentView(R.layout.lay_zdj);
        aktualizacjaDanych();
    }

    //Strona internetowa
    public void website(View view) {

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ww2.ii.uj.edu.pl/~z1102327/"));
        startActivity(intent);
    }

    //Podziekowania
    public void podziekowania(View view) {
        setContentView(R.layout.lay_podziekowania);
        aktualizacjaDanych();
    }

    //Aktualizacja danych, przycisków, grafik itp
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void aktualizacjaDanych()
    {
        TextView dane = (TextView) findViewById(R.id.dane);
        TextView daneM = (TextView) findViewById(R.id.daneM);
        Button buttonG1 = (Button) findViewById(R.id.buttonG1);
        Button buttoninfoG1 = (Button) findViewById(R.id.buttonInfoG1);
        Button buttonG2 = (Button) findViewById(R.id.buttonG2);
        Button buttoninfoG2 = (Button) findViewById(R.id.buttonInfoG2);
        Button buttonG3 = (Button) findViewById(R.id.buttonG3);
        Button buttoninfoG3 = (Button) findViewById(R.id.buttonInfoG3);
        Button buttonG4 = (Button) findViewById(R.id.buttonG4);
        Button buttoninfoG4 = (Button) findViewById(R.id.buttonInfoG4);
        Button buttonS = (Button) findViewById(R.id.buttonS);
        Button buttonA = (Button) findViewById(R.id.buttonAbout);
        Button buttonS1 = (Button) findViewById(R.id.button);
        Button buttonS2 = (Button) findViewById(R.id.button2);
        Button buttonS3 = (Button) findViewById(R.id.button3);
        Button buttonS4 = (Button) findViewById(R.id.button11);
        Button buttonCC1 = (Button) findViewById(R.id.button4);
        Button buttonCC2 = (Button) findViewById(R.id.button5);
        Button buttonCC3 = (Button) findViewById(R.id.button6);
        Button buttonCC4 = (Button) findViewById(R.id.button13);
        Button buttonBtM = (Button) findViewById(R.id.button7);
        Button buttonNp = (Button) findViewById(R.id.button8);
        Button buttonNpp = (Button) findViewById(R.id.button14);
        Button buttonBpp = (Button) findViewById(R.id.button12);
        TextView textS = (TextView) findViewById(R.id.textView);
        TextView textC = (TextView) findViewById(R.id.textView2);
        Button buttonSt1 = (Button) findViewById(R.id.buttonSt1);
        Button buttonSt2 = (Button) findViewById(R.id.buttonSt2);
        Button buttonSt3 = (Button) findViewById(R.id.buttonSt3);
        Button buttonSon = (Button) findViewById(R.id.buttonSon);
        Button buttonSoff = (Button) findViewById(R.id.buttonSoff);
        Button buttonBp = (Button) findViewById(R.id.buttonBp);
        TextView textSt = (TextView) findViewById(R.id.textViewnp1);
        TextView textSound = (TextView) findViewById(R.id.textViewnp2);
        TextView playwith = (TextView) findViewById(R.id.Playwith);
        Button buttonWeb = (Button) findViewById(R.id.button9);
        Button buttonThx = (Button) findViewById(R.id.button10);
        Switch grcpu = (Switch) findViewById(R.id.switch1);
        android.support.v7.app.ActionBar bar = getSupportActionBar(); //Actionbar aby moc go chowac i pokazywac

        //SharedPreferences
        editor.putInt("CC",CC);
        editor.putInt("width",XX);
        editor.putInt("height",YY);
        editor.commit();


        //Stop jesli muzyczka wygranej dalej dziala
        if(winS!=null)
            winS.stop();

        if(bar!=null) {
            bar.hide();
        }

        if(dane!=null)
            dane.setText(XX+"x"+YY+", "+CC);
        if(daneM!=null)
            daneM.setText(XX+"x"+YY+", "+CC);

        if(grcpu!=null)
        {
            grcpu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    kto = isChecked;
                }
            });
            grcpu.setTextOff("Player");
            grcpu.setTextOn("CPU");
            grcpu.setChecked(kto);
        }

        if(style==0) {

            if(buttonWeb != null) {
                buttonWeb.setText("Website");
            }

            if(buttonThx != null) {
                buttonThx.setText("Thanks");
            }

            if(buttonG1 != null) {
                buttonG1.setText(CC + " in row");
            }

            if(buttonG2 != null) {
                buttonG2.setText("Tic-Tac-Toe");
            }

            if(buttonG3 != null) {
                buttonG3.setText(CC+" in row Extreme");
            }

            if(buttonG4 != null) {
                buttonG4.setText("Tic-Tac-Toe Extreme");
            }

            if(buttoninfoG1 != null) {
                buttoninfoG1.setText("??");
            }

            if(playwith!=null)
            {
                playwith.setText("Play with ");
            }

            if(buttoninfoG2 != null) {
                buttoninfoG2.setText("??");
            }

            if(buttoninfoG3 != null) {
                buttoninfoG3.setText("??");
            }

            if(buttoninfoG4 != null) {
                buttoninfoG4.setText("??");
            }

            if(buttonS != null) {
                buttonS.setText("Settings");
            }

            if(buttonA != null){
                buttonA.setText("About Author");
            }

            if(buttonS1 != null){
                buttonS1.setText("10x20");
                if(XX==10 && YY==20)
                    buttonS1.setTextColor(Color.BLUE);
                else
                    buttonS1.setTextColor(Color.BLACK);
            }
            if(buttonS2 != null){
                buttonS2.setText("15x25");
                if(XX==15 && YY==25)
                    buttonS2.setTextColor(Color.BLUE);
                else
                    buttonS2.setTextColor(Color.BLACK);
            }
            if(buttonS3 != null){
                buttonS3.setText("20x30");
                if(XX==20 && YY==30)
                    buttonS3.setTextColor(Color.BLUE);
                else
                    buttonS3.setTextColor(Color.BLACK);
            }
            if(buttonS4 != null){
                buttonS4.setText("Set");
            }
            if(buttonCC4 != null){
                buttonCC4.setText("Set");
            }
            if(buttonCC1 != null){
                buttonCC1.setText("4");
                if(CC==4)
                    buttonCC1.setTextColor(Color.BLUE);
                else
                    buttonCC1.setTextColor(Color.BLACK);
            }
            if(buttonCC2 != null){
                buttonCC2.setText("5");
                if(CC==5)
                    buttonCC2.setTextColor(Color.BLUE);
                else
                    buttonCC2.setTextColor(Color.BLACK);
            }
            if(buttonCC3 != null){
                buttonCC3.setText("6");
                if(CC==6)
                    buttonCC3.setTextColor(Color.BLUE);
                else
                    buttonCC3.setTextColor(Color.BLACK);
            }

            if(buttonNp != null){
                buttonNp.setText(">>");
            }
            if(buttonNpp != null){
                buttonNpp.setText(">>");
            }
            if(textS != null){
                textS.setText("Size of game board");
            }
            if(textC != null){
                textC.setText("How many rectangles in row");
            }


            if(textSt != null){
                textSt.setText("Choose your style");
            }
            if(buttonSt1 != null){
                buttonSt1.setText("Style 1");
                if(style==0)
                    buttonSt1.setTextColor(Color.BLUE);
                else
                    buttonSt1.setTextColor(Color.BLACK);
            }
            if(buttonSt2 != null){
                buttonSt2.setText("Style 2");
            }
            if(buttonSt3 != null){
                buttonSt3.setText("Style 3");
            }
            if(textSound != null){
                textSound.setText("Sound");
            }
            if(buttonSon != null){
                buttonSon.setText("On");
                if(sound)
                    buttonSon.setTextColor(Color.BLUE);
                else
                    buttonSon.setTextColor(Color.BLACK);
            }
            if(buttonSoff != null){
                buttonSoff.setText("Off");
                if(!sound)
                    buttonSoff.setTextColor(Color.BLUE);
                else
                    buttonSoff.setTextColor(Color.BLACK);
            }
            if(buttonBp != null){
                buttonBp.setText("<<");
            }

            if(buttonBpp != null) {
                buttonBpp.setText("<<");
            }

            if(buttonBtM != null){
                buttonBtM.setText("Back to menu");
            }


        }
        else if (style == 1) {

            if(findViewById(R.id.xyz) != null)
                findViewById(R.id.xyz).setBackgroundResource(R.drawable.bg);

            if(buttonWeb != null) {
                buttonWeb.setBackgroundResource(R.drawable.webbtn);
            }

            if(buttonThx != null) {
                buttonThx.setBackgroundResource(R.drawable.thxbtn);
            }

            if (buttonG1 != null) {
                if (CC == 4)
                    buttonG1.setBackgroundResource(R.drawable.fourinrow);
                if (CC == 5)
                    buttonG1.setBackgroundResource(R.drawable.fiveinrow);
                if (CC == 6)
                    buttonG1.setBackgroundResource(R.drawable.sixinrow);
            }

            if (buttonG2 != null) {
                buttonG2.setBackgroundResource(R.drawable.tictactoe);
            }

            if (buttonG3 != null) {
                if (CC == 4)
                    buttonG3.setBackgroundResource(R.drawable.fourinrowextreme);
                if (CC == 5)
                    buttonG3.setBackgroundResource(R.drawable.fiveinrowextreme);
                if (CC == 6)
                    buttonG3.setBackgroundResource(R.drawable.sixinrowextreme);
            }

            if (buttonG4 != null) {
                buttonG4.setBackgroundResource(R.drawable.tictactoeextreme);
            }

            if (buttoninfoG1 != null) {
                buttoninfoG1.setBackgroundResource(R.drawable.ask);
            }

            if (buttoninfoG2 != null) {
                buttoninfoG2.setBackgroundResource(R.drawable.ask);
            }

            if (buttoninfoG3 != null) {
                buttoninfoG3.setBackgroundResource(R.drawable.ask);
            }

            if (buttoninfoG4 != null) {
                buttoninfoG4.setBackgroundResource(R.drawable.ask);
            }

            if (buttonS != null) {
                buttonS.setBackgroundResource(R.drawable.settingsss);
            }

            if (buttonA != null) {
                buttonA.setBackgroundResource(R.drawable.aboutauthor);
            }

            if (buttonS1 != null) {
                buttonS1.setBackgroundResource(R.drawable.opone);
            }
            if (buttonS2 != null) {
                buttonS2.setBackgroundResource(R.drawable.optwo);
            }
            if (buttonS3 != null) {
                buttonS3.setBackgroundResource(R.drawable.opfree);
            }
            if (buttonCC1 != null) {
                buttonCC1.setBackgroundResource(R.drawable.four);
            }
            if (buttonCC2 != null) {
                buttonCC2.setBackgroundResource(R.drawable.five);
            }
            if (buttonCC3 != null) {
                buttonCC3.setBackgroundResource(R.drawable.six);
            }
            if(buttonS4 != null){
                buttonS4.setBackgroundResource(R.drawable.set);
            }
            if(buttonCC4 != null){
                buttonCC4.setBackgroundResource(R.drawable.set);
            }
            if (buttonBtM != null) {
                buttonBtM.setBackgroundResource(R.drawable.backtomenu);
                buttonBtM.setText("");
            }
            if (buttonNp != null) {
                buttonNp.setBackgroundResource(R.drawable.np);
            }
            if (buttonNpp != null) {
                buttonNpp.setBackgroundResource(R.drawable.np);
                buttonNpp.setText("");
            }
            if (textS != null) {
                textS.setBackgroundResource(R.drawable.sizeofgameboard);
            }
            if (textC != null) {
                textC.setBackgroundResource(R.drawable.howmanyrectaglesinrowtowin);
            }

            if (buttonSt1 != null) {
                buttonSt1.setBackgroundResource(R.drawable.stone);
                buttonSt1.setText("");
            }

            if (buttonSt2 != null) {
                buttonSt2.setBackgroundResource(R.drawable.sttwo);
                buttonSt2.setText("");
            }

            if (buttonSon != null) {
                buttonSon.setBackgroundResource(R.drawable.on);
                buttonSon.setText("");
            }

            if (buttonSt3 != null) {
                buttonSt3.setBackgroundResource(R.drawable.stfree);
                buttonSt3.setText("");
            }

            if (buttonSoff != null) {
                buttonSoff.setBackgroundResource(R.drawable.off);
                buttonSoff.setText("");
            }

            if (buttonBp != null) {
                buttonBp.setBackgroundResource(R.drawable.bp);
                buttonBp.setText("");
            }

            if (buttonBpp != null) {
                buttonBpp.setBackgroundResource(R.drawable.bp);
            }

            if (textSt != null) {
                textSt.setBackgroundResource(R.drawable.chooseyourstyle);
                textSt.setText("");
            }

            if (textSound != null) {
                textSound.setBackgroundResource(R.drawable.sound);
                textSound.setText("");
            }
        }
        wyjscie=0;
    }

    //Nadpisanie funkcji powrotu (dwa klikniecia do wyjscia)
    @Override
    public void onBackPressed()
    {
        Toast toast = Toast.makeText(getApplicationContext(), "Are u sure? \nPress Back once more to exit", Toast.LENGTH_SHORT);
        if(wyjscie==0)
        {
            wyjscie++;
            toast.show();
        }
        else {
            System.exit(0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        sharedPreferences = getSharedPreferences("com.example.urszulaiflorian.thegame;", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        CC=sharedPreferences.getInt("CC",5);
        XX=sharedPreferences.getInt("width", 10);
        YY=sharedPreferences.getInt("height",15);

        setContentView(R.layout.activity_main);
        czysc();

        aktualizacjaDanych();
    }

    //Przycisk do ustawien karty trzeciej
    public void nextPage2(View view) {
        setContentView(R.layout.settings_next_next);
        aktualizacjaDanych();
    }

    //Reczne ustawienie szerokosci i wysokosci planszy
    public void setWH(View view) {

            try{

                EditText width = (EditText) findViewById(R.id.editText);
                EditText height = (EditText) findViewById(R.id.editText2);



                int x = Integer.parseInt(width.getText().toString());
                int y = Integer.parseInt(height.getText().toString());

                if (x > 0 && y > 0 && x<max && y<max  ) {
                    XX=x;
                    YY=y;
                    aktualizacjaDanych();
                }
                else
                {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please set width and height first (0,"+max+")", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            catch (Exception a)
            {
                Toast toast = Toast.makeText(getApplicationContext(), "Please set width and height first (0,"+max+")", Toast.LENGTH_SHORT);
                toast.show();
            }
    }


    public void SetCC(View view) {

        try{

            EditText ccc = (EditText) findViewById(R.id.editText3);



            int x = Integer.parseInt(ccc.getText().toString());

            if (x > 0 ) {
                if(kto)
                    if(x<6)
                    {
                        CC=x;
                        aktualizacjaDanych();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(getApplicationContext(), "With CPU only less or equal 5 is available", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                else
                {
                    CC=x;
                    aktualizacjaDanych();
                }
            }
            else
            {
                Toast toast = Toast.makeText(getApplicationContext(), "Please set how many rectangles in row first", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        catch (Exception a)
        {
            Toast toast = Toast.makeText(getApplicationContext(), "Please set how many rectangles in row first", Toast.LENGTH_SHORT);
            toast.show();
        }

    }
}
