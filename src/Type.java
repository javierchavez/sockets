/**
 * @author Javier Chavez
 * CS351 Sockets lab
 */

/**
 * Types of requests and responses. They could be split but
 * this is a small assignment.
 */
public enum Type
{
  // requests
  BUY, SELL, INVENTORY, QUIT,

  // responses
  SUCCESS, FAIL, NOTIFY, CONNECT

}
