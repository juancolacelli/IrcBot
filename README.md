# IRC Bot
[![](https://jitpack.io/v/com.gitlab.jic/ircbot.svg)](https://jitpack.io/#com.gitlab.jic/ircbot)

## Dependencies
* **GNU IRC Lib**: https://gitlab.com/jic/irclib
* **gson**: https://github.com/google/gson
* **jsoup**: https://jsoup.org/

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
bot.pluginLoader.add(BasicPlugin())
```

## Plugins
<table>
    <tr>
        <th>Plugin</th>
        <th>Description</th>
    </tr>
    <tr>
        <td>Access</td>
        <td>Grant/Revoke bot access</td>
    </tr>
    <tr>
        <td>Auto OP</td>
        <td>Claim op when join a channel</td>
    </tr>
    <tr>
        <td>Auto reconnect</td>
        <td>Auto-reconnect on disconnection</td>
    </tr>
    <tr>
        <td>Auto response</td>
        <td>Auto-response when text triggers are detected</td>
    </tr>
    <tr>
        <td>CTCP VERSION</td>
        <td>Customize your CTCP VERSION response</td>
    </tr>
    <tr>
        <td>Help</td>
        <td>Bot help</td>
    </tr>
    <tr>
        <td>IRCop</td>
        <td>IRCop authentication</td>
    </tr>
    <tr>
        <td>Join and part</td>
        <td>Join and part channels by request</td>
    </tr>
    <tr>
        <td>Plugin loader</td>
        <td>Plugins can be loaded and unloaded by request</td>
    </tr>
    <tr>
        <td>NickServ</td>
        <td>NickServ authentication</td>
    </tr>
    <tr>
        <td>Operator</td>
        <td>Basic operator commands (i.e, !op, !voice, etc.)</td>
    </tr>
    <tr>
        <td>Re-join on kick</td>
        <td>Join channels when kicked</td>
    </tr>
    <tr>
        <td>RSS feed</td>
        <td>Get RSS feed notices and send them to subscribers</td>
    </tr>
    <tr>
        <td>Search</td>
        <td>Search on <a href="https://duckduckgo.com">DuckDuckGo</a></td>
    </tr>
    <tr>
        <td>Torrent</td>
        <td>Search torrents on <a href="https://thepiratebay.org">ThePirateBay</a></td>
    </tr>
    <tr>
        <td>Translate</td>
        <td>Translate text using <a href="https://apertium.org">Apertium</a></td>
    </tr>
    <tr>
        <td>Uptime</td>
        <td>Shows bot uptime</td>
    </tr>
    <tr>
        <td>Website title</td>
        <td>Get website title when an URL is detected</td>
    </tr>
</table>
