/**
 * @author Javier Chavez
 * CS351 Sockets lab
 */

/**
 * Requests are sent from client to server
 */
public class Request extends TCP<Request>
{
  public Request(Type type, float monetaryData, double time, int quantityData)
  {
    super(type, monetaryData, time, quantityData);
  }


  public Request(double time, int quantityData, float monetaryData)
  {
    this(Type.SUCCESS, monetaryData, time, quantityData);
  }

  public Request(String s)
  {
    super(s);
  }

}
