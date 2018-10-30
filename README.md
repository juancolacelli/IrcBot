#IRC Bot

##Dependencies
* irclib: https://gitlab.com/jic/irclib

##Basic usage
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

##Basic plugin definition
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

##Samplebot
It's a basic but powerful bot, that includes the following plugins:
* Behaviour
    * **autojoin**: Auto-join channels on connect.
    * **rejoinonkick**: Re-join channels on kick.
    * **nickserv**: NickServ authentication.
* Commands
    * **operator**: Basic operator commands (i.e, !op, !voice, etc.)
    * **translator**: Esperato / English translator.
    * **uptime**: Shows bot uptime.
* Help
    * **help**: Bot help.
