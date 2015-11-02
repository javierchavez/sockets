/**
 * @author Javier Chavez
 * CS351 Sockets lab
 *
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * ServerWorker
 *
 * Handles the stream broadcast from a client. The stream is translated
 * from a string into real java calls (to store). If at anytime a clients
 * stream is lost this is marked as not running.
 */
public class ServerWorker extends Thread
{
  private final ThneedStore store;
  private Socket client;
  private PrintWriter clientWriter;
  private BufferedReader clientReader;
  private boolean isRunning = true;

  public ServerWorker(Socket client, ThneedStore store)
  {
    this.client = client;
    this.store = store;

    try
    {
      clientWriter = new PrintWriter(client.getOutputStream(), true);
    }
    catch (IOException e)
    {
      System.err.println("Server Worker: Could not open output stream");
      e.printStackTrace();
    }

    try
    {
      clientReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
    }
    catch (IOException e)
    {
      System.err.println("Server Worker: Could not open input stream");
      e.printStackTrace();
    }
  }

  /**
   * Check to see if worker is holding a connection to client.
   *
   * @return true if worker is running
   */
  public boolean isRunning()
  {
    return isRunning;
  }


  /**
   * Send message to client.
   *
   * @param msg string containing message to be sent.
   */
  public void send(String msg)
  {
    System.out.println("ServerWorker.send(" + msg + ")");
    clientWriter.println(msg);
  }


  public void run()
  {
    while (isRunning)
    {
      try
      {
        String s = clientReader.readLine();
        if (s == null || clientReader == null)
        {
          // lost the client
          client.close();
          isRunning = false;
          break;
        }

        Request r = new Request(s);

        if (r.getRequest() == Type.QUIT)
        {
          // client gracefully closed.
          client.close();
          isRunning = false;
          break;
        }

        if (r.getRequest() == Type.BUY)
        {
          // client sent a buy request
          buy(r.getQuantityData(), r.getMonetaryData());
        }

        if (r.getRequest() == Type.SELL)
        {
          // client sent a sell request
          sell(r.getQuantityData(), r.getMonetaryData());
        }

        // Not used //
        if (r.getRequest() == Type.INVENTORY)
        {
          // client sent a inventory request
          clientWriter.println("Inventory: " + store.getInventory() +
                                     "\nBalance: " + store.getBalance());
        }

      }
      catch (Exception e)
      {
      }
    }
  }


  private void buy(int amount, float price)
  {
    Response response = new Response(Type.NOTIFY, 0, 0, 0);
    if (!store.buy(amount, price))
    {
      response.setMsg("An error occurred while trying to process your buy transaction.");
      clientWriter.println(response.toString());
    }
    else
    {
      response.setMsg("You just bought " + amount + " thneeds.");
      clientWriter.println(response.toString());
    }
  }

  private void sell(int amount, float price)
  {
    Response response = new Response(Type.NOTIFY, 0, 0, 0);
    if (!store.sellTo(amount, price))
    {
      response.setMsg("An error occurred while trying to process your sell transaction.");
    }
    else
    {
      response.setMsg("You sold " + amount + " thneeds!!");
    }
    clientWriter.println(response.toString());
  }

}
