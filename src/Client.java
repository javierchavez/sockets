/**
 * @author Javier Chavez
 * CS351 Sockets lab
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Client
 *
 * UX/UI. This is the interface that handles user input
 */
public class Client
{
  private Socket clientSocket;
  Pattern pattern = Pattern.compile("(\\d*)\\s(\\d+\\.\\d+)");
  Matcher matcher;

  // write to socket
  private PrintWriter write;
  // read the socket
  private BufferedReader reader;

  // time of server start
  private long startNanoSec;
  private volatile long lastInventoryUpdate;
  private Scanner keyboard;
  // writes to user
  private ClientListener listener;


  private volatile int thneedsInStore;
  private volatile float treasury;
  private volatile boolean isRunning = true;


  public Client(String host, int portNumber)
  {
    keyboard = new Scanner(System.in);

    while (!openConnection(host, portNumber))
    {
    }

    listener = new ClientListener();
    System.out.println("Client(): Starting listener = : " + listener);
    listener.start();

    listenToUserRequests();

    closeAll();

  }


  private boolean openConnection(String host, int portNumber)
  {

    try
    {
      clientSocket = new Socket(host, portNumber);
    }
    catch (UnknownHostException e)
    {
      System.err.println("Client Error: Unknown Host " + host);
      e.printStackTrace();
      isRunning = false;
      return false;
    }
    catch (IOException e)
    {
      System.err.println("Client Error: Could not open connection to " + host
                                 + " on port " + portNumber);
      e.printStackTrace();
      isRunning = false;
      return false;
    }

    try
    {
      write = new PrintWriter(clientSocket.getOutputStream(), true);
    }
    catch (IOException e)
    {
      System.err.println("Client Error: Could not open output stream");
      e.printStackTrace();
      return false;
    }
    try
    {
      reader = new BufferedReader(new InputStreamReader(
              clientSocket.getInputStream()));
    }
    catch (IOException e)
    {
      System.err.println("Client Error: Could not open input stream");
      e.printStackTrace();
      return false;
    }
    isRunning = true;
    return true;

  }

  private void showHelp()
  {
    System.out.println("Available commands:" +
                               "\n\tbuy: quantity[integer] price[x.xx]" +
                               "\n\tsell: quantity[integer] price[x.xx]" +
                               "\n\tinventory" +
                               "\n\tq");
  }

  private void listenToUserRequests()
  {

    showHelp();
    while (isRunning)
    {

      String cmd = keyboard.nextLine();


      if (cmd == null || cmd.length() < 1)
      {
        continue;
      }


      if (cmd.contains("buy: "))
      {
        String s = cmd.replace("buy: ", "");
        matcher = pattern.matcher(s);
        if (matcher.find())
        {
          int q = Integer.valueOf(matcher.group(1));
          float p = Float.valueOf(matcher.group(2));

          write.println(new Request(Type.BUY, p, 0, q).toString());
        }
        else
        {
          System.out.println("Command not recognized");
          showHelp();
        }
      }
      else
      {
        if (cmd.contains("sell: "))
        {
          String s = cmd.replace("sell: ", "");
          matcher = pattern.matcher(s);
          if (matcher.find())
          {
            int q = Integer.valueOf(matcher.group(1));
            float p = Float.valueOf(matcher.group(2));
            write.println(new Request(Type.SELL, p, 0, q).toString());
          }
          else
          {
            System.out.println("Command not recognized");
            showHelp();
          }
        }
        else
        {
          if (cmd.contains("inventory"))
          {
            System.out.println("Last update: " + inventoryUpdate() +
                                       "\n\tInventory: " + thneedsInStore +
                                       "\n\tBalance: " + String.format("%01.02f", treasury));
          }
          else
          {
            if (cmd.charAt(0) == 'q')
            {
              write.println(new Request(Type.QUIT, 0, 0, 0).toString());
              isRunning = false;
            }
            else
            {
              System.out.println("Command not recognized");
              showHelp();
            }
          }
        }
      }

    }
  }

  public void closeAll()
  {
    System.out.println("Closing client");

    if (write != null)
    {
      write.close();
    }

    if (reader != null)
    {
      try
      {
        reader.close();
        clientSocket.close();
      }
      catch (IOException e)
      {
        System.err.println("Client Error: Could not close");
        e.printStackTrace();
      }
    }
  }

  private String inventoryUpdate()
  {
    long nanoSecDiff = System.nanoTime() - lastInventoryUpdate;
    double secDiff = (double) nanoSecDiff / 1000000000.0;
    return String.format("%.3f", secDiff);
  }


  private String timeDiff()
  {
    long nanoSecDiff = System.nanoTime() - startNanoSec;
    double secDiff = (double) nanoSecDiff / 1000000000.0;
    return String.format("%.3f", secDiff);
  }

  public static void main(String[] args)
  {

    String host = null;
    int port = 0;

    try
    {
      host = args[0];
      port = Integer.parseInt(args[1]);
      if (port < 1)
      {
        throw new Exception();
      }
    }
    catch (Exception e)
    {
      System.out.println("Usage: Client hostname portNumber");
      System.exit(0);
    }
    new Client(host, port);

  }


  /**
   * ClientListener
   *
   * Handles reading stream from socket. The data is then outputted
   * to the console for user.
   */
  class ClientListener extends Thread
  {

    public void run()
    {
      while (isRunning)
      {
        read();
      }
    }

    private void read()
    {
      try
      {
        String msg = reader.readLine();
        if (msg == null)
        {
          System.out.println("Lost server, press enter to shutdown.");
          isRunning = false;
          return;
        }

        Response response = new Response(msg);
        if (response.getRequest() == Type.CONNECT)
        {
          thneedsInStore = response.getQuantityData();
          treasury = response.getMonetaryData();
          startNanoSec = (long) response.getTime();
          lastInventoryUpdate = System.nanoTime();

          System.out.println("[connected] server uptime: " + timeDiff() + "s");
          System.out.println("Inventory: " + thneedsInStore);
          System.out.println("Balance: " + response.getStringMonetaryData());
        }
        else
        {
          if (response.getRequest() == Type.NOTIFY)
          {
            System.out.println(response.getMsg());
          }
          else
          {
            if (response.getRequest() == Type.SUCCESS)
            {
              thneedsInStore = response.getQuantityData();
              treasury = response.getMonetaryData();
              lastInventoryUpdate = System.nanoTime();
            }
            else
            {
              System.out.println("Unrecognized message from Server(" + timeDiff()
                                         + ") = " + msg);
            }
          }
        }

      }
      catch (IOException e)
      {
      }
    }
  }
}
