# IRC Bot

## Dependencies
* **irclib**: https://gitlab.com/jic/irclib
* **revo**: http://www.reta-vortaro.de/tgz/

## Basic usage
```java
IRCBot bot = new IRCBot();

User.Builder userBuilder = new User.Builder();
userBuilder
        .setNick("ircbot")
        .setLogin("ircbot");

Server.Builder serverBuilder = new Server.Builder();
serverBuilder
        .setHostname("irc.freenode.net")
        .setPort(6697)
        .setSecure(true);

bot.connect(serverBuilder.build(), userBuilder.build());
```

## Basic plugin definition
```java
public class BasicPlugin implements Plugin {
    @Override
    public void setup(IRCBot bot) {
        bot.addListener((OnConnectListener) (connection, server, user) -> {
            // TODO: Do something...
        });
    }
}
```

```java
IRCBot bot = new IRCBot();
bot.addPlugin(new BasicPlugin());
```

## Plugins
* Behaviour
    * **autojoin**: Auto-join channels on connect.
    * **autoreconnect**: Auto-reconnect on disconnection.
    * **nickserv**: NickServ authentication.
    * **rejoinonkick**: Re-join channels on kick.
    * **websitetitle**: Get website title when an url is detected.
* Commands
    * **operator**: Basic operator commands (i.e, !op, !voice, etc.)
    * **translator**: Esperato / English translator.
    * **uptime**: Shows bot uptime.
* Help
    * **help**: Bot help.
