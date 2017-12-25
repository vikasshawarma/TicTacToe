package com.example.android.tictactoe;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import static java.security.AccessController.getContext;


public class MainActivity extends AppCompatActivity {

    private Game game1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        game1 = new Game(this);
        setContentView(game1);

    }


}

abstract class Cell extends Point {
    public Cell(int x, int y) {
        super(x, y);
    }
    abstract public void draw(Canvas g,Resources res, int x, int y, int w,
                              int h);
}

class Circle extends Cell {
    public Circle(int x, int y) {
        super(x, y);
    }
    public void draw(Canvas g, Resources res, int x, int y, int w, int h) {
        Bitmap im = BitmapFactory.decodeResource(res, R.drawable.circle);
        g.drawBitmap(im, null, new Rect(x*w, y*h, (x*w)+w, (y*h)+h), new Paint());
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Circle) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    public String toString() {
        return "O";
    }
}

class Cross extends Cell {
    public Cross(int x, int y) {
        super(x, y);
    }
    public void draw(Canvas g, Resources res, int x, int y, int w, int h) {
        Bitmap im = BitmapFactory.decodeResource(res, R.drawable.cross);
        g.drawBitmap(im, null, new Rect(x * w, y * h, (x * w) + w, (y * h) + h), new Paint());
    }


    public boolean equals(Object obj) {
        if (obj instanceof Cross) {
            return true;
        } else {
            return false;
        }
    }
    public String toString() {
        return "X";
    }
}

class Empty extends Cell {
    public Empty(int x, int y) {
        super(x, y);
    }
    public void draw(Canvas g, Resources res, int x, int y, int w, int h) {
        Bitmap im = BitmapFactory.decodeResource(res, R.drawable.empty);
        g.drawBitmap(im, null, new Rect(x*w, y*h, (x*w)+w, (y*h)+h), new Paint());
    }

    public boolean equals(Object obj) {
        if (obj instanceof Empty) {
            return true;
        } else {
            return false;
        }
    }
    public String toString() {
        return " ";
    }
}

class Game extends View {
    private Cell[][] singlesquare = null;
    int x = 3;
    int y = 3;
    private int l;
    private int a;
    private boolean whatdrawn = false;
    private int playerwin = 3;
    private Paint caneta;
    Handler handler = new Handler() {
        // @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    invalidate();
                    break;
                case 1:
                    Toast.makeText(getContext(), "O Wins!",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(getContext(), "X Wins!", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(getContext(), "It's a Draw!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }

            super.handleMessage(msg);
        }
    };

    public int getGameSize() {
        return x;
    }

    public Game(Context context) {
        super(context);

        caneta = new Paint();
        this.caneta.setARGB(255, 0, 0, 0);
        this.caneta.setAntiAlias(true);
        this.caneta.setStyle(Paint.Style.STROKE);
        this.caneta.setStrokeWidth(5);

        l = this.getWidth();
        a = this.getHeight();

        singlesquare = new Cell[x][y];

        int xss = l / x;
        int yss = a / y;

        for (int z = 0; z < y; z++) {
            for (int i = 0; i < x; i++) {
                singlesquare[z][i] = new Empty(xss * i, z * yss);
            }
        }
    }

    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < singlesquare.length; i++) {
            for (int j = 0; j < singlesquare[0].length; j++) {
                singlesquare[i][j].draw(canvas, getResources(), j, i, (this
                        .getWidth() + 3)
                        / singlesquare.length, this.getHeight()
                        / singlesquare[0].length);
            }
        }
        int xs = this.getWidth() / x;
        int ys = this.getHeight() / y;
        for (int i = 0; i <= x; i++) {
            canvas.drawLine(xs * i, 0, xs * i, this.getHeight(), caneta);
        }
        for (int i = 0; i <= y; i++) {
            canvas.drawLine(0, ys * i, this.getWidth(), ys * i, caneta);
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x_aux = (int) (event.getX() / (this.getWidth() / x));
        int y_aux = (int) (event.getY() / (this.getHeight() / y));
        drawimage(x_aux, y_aux);
        return super.onTouchEvent(event);
    }
    public String getPiece(int player) {
        switch (player) {
            case 1:
                return "x";
            case -1:
                return "o";
        }
        return null;
    }
    public void drawimage(int x_aux, int y_aux) {
        Cell cel = null;
        if (whatdrawn)
        {
            cel = new
                    Cross(singlesquare[x_aux][y_aux].x,singlesquare[x_aux][y_aux].y);
            whatdrawn = false;

        }
        else
        {
            cel = new Circle(singlesquare[x_aux][y_aux].x,
                    singlesquare[x_aux][y_aux].y);
            whatdrawn = true;
        }

        singlesquare[y_aux][x_aux] = cel;

        handler.sendMessage(Message.obtain(handler, 0));

        if (validate_game()) {
            if (whatdrawn) {
                System.out.println("You Win");
                handler.sendMessage(Message.obtain(handler, 1));
            } else {
                System.out.println("Computer Win");
                handler.sendMessage(Message.obtain(handler, 2));
            }
            resizegame(x);

        } else if (isFull()) {
            System.out.println("Loose");
            handler.sendMessage(Message.obtain(handler, 3));
            resizegame(x);

        }
    }

    private boolean validate_game() {
        int contador = 0;
        Cell anterior = null;

        for (int i = 0; i < singlesquare.length; i++) {
            for (int j = 0; j < singlesquare[0].length; j++) {
                System.out.print(singlesquare[i][j]);
                if (!singlesquare[i][j].equals(anterior)
                        || singlesquare[i][j] instanceof Empty) {
                    anterior = singlesquare[i][j];
                    contador = 0;
                } else {
                    contador++;
                }
                if (contador >= getPlayerwin() - 1) {
                    return true;
                }

            }
            System.out.println("");
            anterior = null;
            contador = 0;
        }
        anterior = null;
        for (int j = 0; j < singlesquare[0].length; j++) {
            for (int i = 0; i < singlesquare.length; i++) {
                System.out.print(singlesquare[i][j]);
                if (!singlesquare[i][j].equals(anterior)
                        || singlesquare[i][j] instanceof Empty) {
                    anterior = singlesquare[i][j];
                    contador = 0;
                } else {
                    contador++;
                }

                if (contador >= getPlayerwin() - 1) {
                    return true;
                }

            }
            System.out.println("");
            anterior = null;
            contador = 0;
        }
        anterior = null;
        for (int j = singlesquare[0].length - 1; j >= 0; j--) {
            int yau = 0;
            for (int z = j; z < singlesquare[0].length; z++) {
                if (!singlesquare[yau][z].equals(anterior)
                        || singlesquare[yau][z] instanceof Empty) {
                    anterior = singlesquare[yau][z];
                    contador = 0;
                } else {
                    contador++;
                }
                if (contador >= getPlayerwin() - 1) {
                    return true;
                }
                yau++;
            }
            contador = 0;
            anterior = null;
        }

        anterior = null;
        for (int j = 0; j < singlesquare[0].length; j++) {
            int yau = 0;
            for (int z = j; z >= 0; z--) {
                if (!singlesquare[yau][z].equals(anterior)
                        || singlesquare[yau][z] instanceof Empty) {
                    anterior = singlesquare[yau][z];
                    contador = 0;
                } else {
                    contador++;
                }

                if (contador >= getPlayerwin() - 1) {
                    return true;
                }
                yau++;
            }
            contador = 0;
            anterior = null;
        }
        return false;
    }

    public boolean isFull() {
        for (int i = 0; i < singlesquare.length; i++) {
            for (int j = 0; j < singlesquare[0].length; j++) {
                if (singlesquare[i][j] instanceof Empty) {
                    return false;
                }
            }
        }
        return true;
    }

    public void resizegame(int s) {
        x = s;
        y = s;

        singlesquare = new Cell[x][y];
        int xss = l / x;
        int yss = a / y;

        for (int z = 0; z < y; z++) {
            for (int i = 0; i < x; i++) {
                singlesquare[z][i] = new Empty(xss * i, z * yss);
            }
        }
        handler.sendMessage(Message.obtain(handler, 0));
    }
    public int getPlayerwin() {
        return playerwin;
    }




}


