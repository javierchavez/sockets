/**
 * @author Javier Chavez
 * CS351 Sockets lab
 */

/**
 * Responses are sent from server to client.
 */
public class Response extends TCP<Response>
{


  public Response(Type type, double time, int quantityData, float monetaryData)
  {
    super(type, monetaryData, time, quantityData);
  }

  public Response(long time, int quantityData, float monetaryData)
  {
    this(Type.SUCCESS, time, quantityData, monetaryData);
  }

  public Response(String s)
  {
    super(s);
  }


  /**
   * Format is constrained to a school specification.
   *
   * @return string with tcp data
   */
  @Override
  public String toString()
  {
    if (this.getRequest() == Type.NOTIFY || this.getRequest() == Type.CONNECT)
    {
      return super.toString();
    }

    return getStringTime() + TCP.DELIMITER +
            "inventory=" + this.getQuantityData() + TCP.DELIMITER +
            "treasury="+ getStringMonetaryData();
  }
}
