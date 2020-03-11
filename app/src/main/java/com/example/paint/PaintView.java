package com.example.paint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class PaintView extends View {
    public ViewGroup.LayoutParams params;
    private Path path = new Path();
    private Paint brush = new Paint();
    private String pointsList="" ;

    private String mPoint;
    private String IpAddress = "192.168.4.1";
    private int Port = 8090;

    public PaintView(Context context) {
        super(context);
        brush.setAntiAlias(true);
        brush.setColor(Color.RED);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeJoin(Paint.Join.ROUND);
        brush.setStrokeWidth(8f);
        params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float pointX = event.getX();
        float pointY = event.getY();

        mPoint="("+ (int)(pointX/8) + "," + (int)(pointY/8) + ")";

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                path.moveTo(pointX, pointY);
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(pointX, pointY);
                Log.d( "point ",mPoint);
                pointsList+=mPoint+"\n";
                //Now send to WI_FI
                break;
            case MotionEvent.ACTION_UP:
                Log.d("up", "7sal");
                MyClientTask myClientTask = new MyClientTask(pointsList);
                myClientTask.execute();
                pointsList="";
            default:
                return false;
        }
        postInvalidate();
        return false;
    }
boolean working=false;
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, brush);
    }


    @SuppressLint("StaticFieldLeak")
    public class MyClientTask extends AsyncTask<Void, Void, Void> {
        String response = "";
        String msgToServer="hello world";

        public void setMsgToServer(String msgToServer) {
            this.msgToServer = msgToServer;
        }

        MyClientTask(String msgToServer) {
           this.msgToServer=msgToServer;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            Socket socket = null;
            DataOutputStream dataOutputStream = null;

            try {
                socket = new Socket(IpAddress, Port);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                if(!msgToServer.equals(""))
                    dataOutputStream.writeUTF(msgToServer + "$");

            } catch (IOException e) { }
            finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {}
                }
                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {}
                }
            }
            return null;
        }

    }

}

