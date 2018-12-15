# IRC Bot

## Dependencies
* **GNU IRC Library**: https://gitlab.com/jic/irclib
* **apache-commons**: http://commons.apache.org/proper/commons-lang/
* **gson**: https://github.com/google/gson
* **jsoup**: https://jsoup.org/

**Maven**
```
org.apache.commons:commons-text:1.62
com.google.code.gson:gson:2.8.5
org.jsoup:jsoup:1.11.3
```

## Showcase
* **GNU Librebot**: https://gitlab.com/jic/librebot

## Basic usage
```kotlin
val server = Server("irc.freenode.net", 6697, true)
val user = User("ircbot", "ircbot", "GNU IRCBot - https://gitlab.com/jic/ircbot")
val bot = IRCBot(server, user)

bot.connect()
```

## Basic plugin definition
```kotlin
class BasicPlugin : Plugin {
    private val listener = object : OnConnectListener {
        override fun onConnect(connection: Connection, server: Server, user: User) {
            connection.join("#gnu")
        }
    }
        
    override var name = "basic_plugin"

    override fun onLoad(bot: IRCBot) {
        bot.addListener(listener)
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(listener)
    }
}
```

```kotlin
bot.addPlugin(BasicPlugin())
```

## Plugins
* **access**: Grant/Revoke bot access
* **apertium_translator**: Translate text using [Apertium](https://apertium.org)
* **auto_join**: Auto-join channels on connect
* **auto_reconnect**: Auto-reconnect on disconnection
* **auto_response**: Auto-response on text triggers
* **ctcp_version**: Customize your CTCP VERSION response
* **duck_duck_go_search**: Search on [DuckDuckGo](https://duckduckgo.com)
* **help**: Bot help
* **ircop**: IRCop authentication
* **join_part**: Bot can join and part channels by request
* **plugin_loader**: Plugins can be loaded and unloaded
* **nickserv**: NickServ authentication
* **operator**: Basic operator commands (i.e, !op, !voice, etc.)
* **rejoin_on_kick**: Re-join channels on kick
* **rss_feed**: Get rss feed notices and send them to all subscribers
* **the_pirate_bay_search**: Search torrents in [ThePirateBay](https://thepiratebay.org)
* **uptime**: Shows bot uptime
* **website_title**: Get website title when an url is detected

