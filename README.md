# Client/Server

A simple client server written in java

## Running
**Fix permissions**
```
chmod +x game
```

**Build**
```
./game build
```

**Run server with script**

Server defaults to port 5555.
```
./game server [port]
```

**Run client with script**

Client defaults to host localhost, port 5555. **Both** args or none, not one or the other.
```
./game client [host] [port]
```

---

## Running (Cool kids)
For this you **ONLY** need to run ```./game server```. As far as I know this only works for Unix (linux/OSX).

### Start up a client
Replace with the host and port your server is running on.
```
nc localhost 5555
```

Now you are essentially connected to the server paste in the commands to interact.
you will need to add a ```\n``` for server to understand command is finished. Instead of pressing ender you can press **Ctrl-j**.

Pressing *enter* will do **nothing**.

###### Formatting
The server will respond to this.
```
[d.ddd]:inventory=[integer]:treasury=[d.dd]:SELL:[string]
```

###### Selling
Inventory in this context is how much you are wanting to sell. 
treasury is price/inventory.
```
0.000:inventory=50:treasury=0.99:SELL: 

```

###### Buying
inventory in this context is how much you are wanting to buy.
treasury is price/inventory.
```
0.000:inventory=50:treasury=0.99:BUY: 
```

###### Inventory
Inventory & treasury do not matter you are sending a request to get a response.
```
0.000:inventory=0:treasury=0.00:INVENTORY: 
```

---
**Author**
Javier Chavez