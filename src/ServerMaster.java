/**
 * @author Javier Chavez
 * CS351 Sockets lab
 */

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The master server initiates a connection to data (Thneed store).
 * Holds a list of workers which are checked every half sec. for liveliness.
 */
public class ServerMaster
{
  private ServerSocket serverSocket;
  private LinkedList<ServerWorker> allConnections = new LinkedList<ServerWorker>();
  private ThneedStore store;
  private long startNanoSec = 0l;
  private final Timer timer = new Timer();

  public ServerMaster(int portNumber)
  {
    startNanoSec = System.nanoTime();
    try
    {
      serverSocket = new ServerSocket(portNumber);
      store = new ThneedStore(this);
    }
    catch (IOException e)
    {
      System.err.println("Server error: Opening socket failed.");
      e.printStackTrace();
      System.exit(-1);
    }

    // Mimic a chron-job that every half sec. it deletes stale workers.
    timer.schedule(new TimerTask()
    {
      @Override
      public void run()
      {
        cleanConnectionList();
      }
    }, 500, 500);

    waitForConnection(portNumber);


  }


  /**
   * Get the time of server spawn time to a given time.
   *
   * @param curr is the current time
   * @return difference time in seconds
   */
  public double getTimeDiff(long curr)
  {
    long nanoSecDiff = curr - startNanoSec;
    return nanoSecDiff / 1000000000.0;
  }


  /**
   * Wait for a connection.
   *
   * @param port port to listen on.
   */
  public void waitForConnection(int port)
  {

    String host = "";
    try
    {
      host = InetAddress.getLocalHost().getHostName();
    }
    catch (UnknownHostException e)
    {
      e.printStackTrace();
    }
    while (true)
    {
      System.out.println("ServerMaster(" + host + "): waiting for Connection on port: " + port);
      try
      {
        Socket client = serverSocket.accept();
        ServerWorker worker = new ServerWorker(client, store);
        worker.start();
        System.out.println("ServerMaster: *********** new Connection");
        allConnections.add(worker);
        Response rsp = new Response(Type.CONNECT,
                                    startNanoSec,
                                    store.getInventory(),
                                    store.getBalance());
        rsp.setMsg("[" + port + "]" + host);
        worker.send(rsp.toString());
      }
      catch (IOException e)
      {
        System.err.println("Server error: Failed to connect to client.");
        e.printStackTrace();
      }
    }
  }

  /**
   * Send a response of a transaction to all listening workers.
   *
   * @param response a response to be send globally.
   */
  public void broadcastTransaction(Response response)
  {
    for (ServerWorker workers : allConnections)
    {
      workers.send(response.toString());
    }
  }

  /**
   * Send a message to all workers.
   *
   * @param s string containing a message.
   */
  public void broadcast(String s)
  {
    for (ServerWorker workers : allConnections)
    {
      workers.send(s);
    }
  }

  public static void main(String args[])
  {
    //Valid port numbers are Port numbers are 1024 through 65535.
    //  ports under 1024 are reserved for system services http, ftp, etc.
    int port = 5555; //default
    if (args.length > 0)
    {
      try
      {
        port = Integer.parseInt(args[0]);
        if (port < 1)
        {
          throw new Exception();
        }
      }
      catch (Exception e)
      {
        System.out.println("Usage: ServerMaster portNumber");
        System.exit(0);
      }
    }

    new ServerMaster(port);
  }

  private void cleanConnectionList()
  {
    int con = 0;
    for (int i = 0; i < allConnections.size(); i++)
    {
      if (!allConnections.get(i).isRunning())
      {
        // the worker is not running. remove it.
        allConnections.remove(i);
        con++;
      }
    }
    // check if any removed. Show removed count
    if (con > 0)
    {
      System.out.println("Removed " + con + " connection workers.");
    }
  }


  private String timeDiff()
  {
    long nanoSecDiff = System.nanoTime() - startNanoSec;
    double secDiff = nanoSecDiff / 1000000000.0;
    return String.format("%.3f", secDiff);
  }

}
