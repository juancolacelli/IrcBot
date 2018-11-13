# IRC Bot

## Dependencies
* **irclib**: https://gitlab.com/jic/irclib
* **json-simple**: https://code.google.com/archive/p/json-simple/

## Basic usage
```java
IRCBot bot = new IRCBot();

User.Builder userBuilder = new User.Builder();
userBuilder
        .setNick("ircbot")
        .setLogin("ircbot")
        .setRealName("ircbot");

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
    * **autojoin**: Auto-join channels on connect
    * **autoreconnect**: Auto-reconnect on disconnection
    * **nickserv**: NickServ authentication
    * **rejoinonkick**: Re-join channels on kick
    * **ctcpversion**: Customize your CTCP VERSION response
    * **websitetitle**: Get website title when an url is detected
    * **rssfeed**: Get rss feed notices and send it to all joined channels
* Commands
    * **uptime**: Shows bot uptime
    * **operator**: Basic operator commands (i.e, !op, !voice, etc.)
    * **apertiumtranslator**: Translate text using [Apertium](https://apertium.org)
* Help
    * **help**: Bot help
