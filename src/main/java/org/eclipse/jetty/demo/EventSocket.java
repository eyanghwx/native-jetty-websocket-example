package org.eclipse.jetty.demo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

public class EventSocket extends WebSocketAdapter
{
  private Pair pair;
  private Map<Session, Shell> shells = new HashMap<Session, Shell>();
  
    @Override
    public void onWebSocketConnect(Session sess)
    {
        super.onWebSocketConnect(sess);
        System.out.println("Socket Connected: " + sess);
        shells.put(sess, new Shell());
        Shell shell = shells.get(sess);
        try {
          this.pair = shell.run();
          try {
            byte[] buffer = new byte[4000];
            if (shell.isAlive()) {
              try {
                Thread.sleep(100);
              } catch (InterruptedException e) {
                System.out.println("sleep interrupted.");
              }
              int no = pair.input.available();
              while (no > 0) {
                int n = pair.input.read(buffer, 0, Math.min(no,  buffer.length));
                String formatted = new String(buffer).replaceAll("\n","\r\n");
                sess.getRemote().sendString(formatted);
                no = pair.input.available();
              }
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
    }
    
    @Override
    public void onWebSocketText(String message)
    {
      Session session = getSession();
      Shell shell = shells.get(session);
      try {
        byte[] buffer = new byte[4000];
        if (shell.isAlive()) {
          int ni = message.length();
          if (ni > 0) {
            pair.output.write(message.getBytes());
            pair.output.flush();
          }
          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          int no = pair.input.available();
          while (no > 0) {
            int n = pair.input.read(buffer, 0, Math.min(no,  buffer.length));
            String formatted = new String(buffer).replaceAll("\n","\r\n");
            session.getRemote().sendString(formatted);
            no = pair.input.available();
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    @Override
    public void onWebSocketClose(int statusCode, String reason)
    {
        super.onWebSocketClose(statusCode,reason);
        Session session = getSession();
        Shell shell = shells.get(session);
        session.close();
        shell.close();
        System.out.println("Socket Closed: [" + statusCode + "] " + reason);
    }
    
    @Override
    public void onWebSocketError(Throwable cause)
    {
        super.onWebSocketError(cause);
        Session session = getSession();
        Shell shell = shells.get(session);
        session.close();
        shell.close();
        cause.printStackTrace(System.err);
    }
}
