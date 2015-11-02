/**
 * @author Javier Chavez
 * CS351 Sockets lab
 *
 */


/**
 * Class that mimics the TCP layer
 *
 * @param <T> class extending tcp
 */
public abstract class TCP<T extends TCP>
{
  public static String DELIMITER=":";
  private Type request;
  private int quantityData;
  private float monetaryData;
  private double time;
  private String msg = " ";





  public TCP(Type type, float monetaryData, double time, int quantityData)
  {
    this.request = type;
    this.monetaryData = monetaryData;
    this.time = time;
    this.quantityData = quantityData;
  }

  /**
   * Turn a string into a TCP
   *
   * @param s valid TCP string (see. toString())
   */
  public TCP(String s)
  {
    String[] _dataArray = s.split(DELIMITER);

    if (_dataArray.length == 0)
    {
      return;
    }
    int idxOfNum;
    time = Double.valueOf(_dataArray[0]);
    idxOfNum = _dataArray[1].indexOf('=') + 1;
    quantityData = Integer.parseInt(_dataArray[1].substring(idxOfNum));
    // quantityData = Integer.valueOf(_dataArray[1]);

    idxOfNum = _dataArray[2].indexOf('=') + 1;
    monetaryData = Float.parseFloat(_dataArray[2].substring(idxOfNum));
    // monetaryData = Float.valueOf(_dataArray[2]);


    if (_dataArray.length >= 4)
    {
      request = Type.valueOf(_dataArray[3]);
      if (_dataArray.length == 5)
      {
        msg = String.valueOf(_dataArray[4]);
      }
      else
      {
        msg = "";
      }
    }
    else
    {
      msg = "";
      request = Type.SUCCESS;
    }
  }

  public void setTime(double time)
  {
    this.time = time;
  }

  public void setMsg(String msg)
  {
    this.msg = msg;
  }

  public void setRequest(Type request)
  {
    this.request = request;
  }

  public String getMsg()
  {
    return msg;
  }

  public Type getRequest()
  {
    return request;
  }

  public int getQuantityData()
  {
    return quantityData;
  }

  public float getMonetaryData()
  {
    return monetaryData;
  }

  public double getTime()
  {
    return time;
  }

  public String getStringTime()
  {
    return String.format("%.3f", time);
  }
  public String getStringMonetaryData()
  {
    return String.format("%01.02f", monetaryData);
  }

  /**
   * Convert TCP into.
   * [time]:inventory=[quantity]:treasury=[dollar]:SELL:[message]
   * [d.ddd]:inventory=[integer]:treasury=[d.dd]:SELL:[string]
   *
   * @return string in the TCP format
   *
   */
  @Override
  public String toString()
  {
    return  getStringTime() + DELIMITER +
            "inventory=" + quantityData + DELIMITER +
            "treasury=" + getStringMonetaryData() + DELIMITER +
            request.toString() + DELIMITER +
            msg;
  }

}
