# IRC Bot

## Dependencies
* **GNU IRC Library**: https://gitlab.com/jic/irclib
* **apache-commons**: http://commons.apache.org/proper/commons-lang/
* **json-simple**: https://code.google.com/archive/p/json-simple/
* **jsoup**: https://jsoup.org/

**Maven**
```
org.apache.commons:commons-text:1.62
com.googlecode.json-simple:json-simple:1.1.12
org.jsoup:jsoup:1.11.3
```

## Showcase
* **GNU Librebot**: https://gitlab.com/jic/librebot

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
* **access**: Grant/Revoke bot access
* **apertiumtranslator**: Translate text using [Apertium](https://apertium.org)
* **autojoin**: Auto-join channels on connect
* **autoreconnect**: Auto-reconnect on disconnection
* **autoresponse**: Auto-response on text triggers
* **ctcpversion**: Customize your CTCP VERSION response
* **duckduckgosearch**: Search on [DuckDuckGo](https://duckduckgo.com)
* **help**: Bot help
* **ircop**: IRCop authentication
* **joinpart**: Bot can join and part channels by request
* **loader**: Plugins can be loaded and unloaded
* **nickserv**: NickServ authentication
* **operator**: Basic operator commands (i.e, !op, !voice, etc.)
* **rejoinonkick**: Re-join channels on kick
* **rssfeed**: Get rss feed notices and send them to all subscribers
* **thepiratebaysearch**: Search torrents in [ThePirateBay](https://thepiratebay.online)
* **uptime**: Shows bot uptime
* **websitetitle**: Get website title when an url is detected

