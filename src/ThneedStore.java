/**
 * @author Javier Chavez
 * CS351 Sockets lab
 */

/**
 * The ThneedStore tracks the inventory of Thneeds.
 */
public class ThneedStore
{
  private final ServerMaster server;
  private float balance = 1000.00f;
  private int inventory = 0;


  public ThneedStore(ServerMaster server)
  {
    this.server = server;
  }

  /**
   * Buy from the store (thread safe).
   *
   * @param amount quantity to be bought
   * @param price price per item
   * @return true if success.
   */
  public synchronized Boolean buy(int amount, float price)
  {
    Response response = new Response(Type.FAIL, 0, inventory, balance);
    boolean isProcessed;

    if (amount > inventory)
    {
      response.setMsg("Treasury doesn't have enough inventory.");
      isProcessed = false;
    }
    else
    {
      balance += amount*price;
      inventory -= amount;
      response = new Response(Type.SUCCESS, 0, inventory, balance);
      isProcessed = true;
    }

    double id = server.getTimeDiff(System.nanoTime());
    response.setTime(id);
    server.broadcastTransaction(response);
    return isProcessed;
  }

  /**
   * Sell to the store (thread safe).
   *
   * @param amount quantity to be sold
   * @param price dollar amount per item
   * @return true if success.
   */
  public synchronized Boolean sellTo(int amount, float price)
  {
    float newBalance = balance - (amount * price);
    Response response = new Response(Type.FAIL, 0, inventory, balance);
    boolean isProcessed;

    if(newBalance >= 0)
    {
      balance = newBalance;
      inventory += amount;
      isProcessed =  true;
      response = new Response(Type.SUCCESS, 0, inventory, balance);
    }
    else
    {
      isProcessed = false;
      response.setMsg("Treasury doesn't have enough money");
    }

    double id = server.getTimeDiff(System.nanoTime());
    response.setTime(id);
    server.broadcastTransaction(response);
    return isProcessed;
  }

  /**
   * get the balance of the store (thread safe).
   *
   * @return dollar amount
   */
  public synchronized float getBalance()
  {
    return balance;
  }

  /**
   * Get the available inventory (thread safe).
   *
   * @return amount in store.
   */
  public synchronized int getInventory()
  {
    return inventory;
  }
}
