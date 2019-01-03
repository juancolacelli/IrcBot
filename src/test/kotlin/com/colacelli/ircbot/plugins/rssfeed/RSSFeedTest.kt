package com.colacelli.ircbot.plugins.rssfeed

import com.colacelli.irclib.actors.User
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class RSSFeedTest {

    private val testUrl = "https://static.fsf.org/fsforg/rss/news.xml"
    private lateinit var rssFeed: RSSFeed

    @BeforeEach
    fun initialize() {
        val properties = Properties()
        properties.setProperty(RSSFeed.SUBSCRIBERS_PROPERTY, "nick_a${RSSFeed.SUBSCRIBERS_SEPARATOR}nick_b")
        properties.setProperty("https://test.org/rss", "test notice")
        rssFeed = RSSFeed(properties)
    }

    @Test
    fun set() {
        assertFalse(rssFeed.set(RSSFeed.SUBSCRIBERS_PROPERTY, "test"))
        assert(rssFeed.set(testUrl, "test"))

        val list = rssFeed.list()
        assertEquals(list[testUrl], "test")
    }

    @Test
    fun add() {
        assertFalse(rssFeed.add(RSSFeed.SUBSCRIBERS_PROPERTY))
        assert(rssFeed.add(testUrl))

        val list = rssFeed.list()
        assertEquals(list[testUrl], "")
    }

    @Test
    fun del() {
        assertFalse(rssFeed.del(RSSFeed.SUBSCRIBERS_PROPERTY))
        assert(rssFeed.del(testUrl))

        val list = rssFeed.list()
        assertNull(list[testUrl])
    }

    @Test
    fun list() {
        rssFeed.add(testUrl)

        val list = rssFeed.list()
        assertEquals(2, list.size)
        assertEquals(list[testUrl], "")
        assertEquals(list["https://test.org/rss"], "test notice")
    }

    @Test
    fun subscribers() {
        val subscribers = rssFeed.subscribers()
        assertEquals(2, subscribers.size)
        assertEquals(subscribers[0], "nick_a")
        assertEquals(subscribers[1], "nick_b")
    }

    @Test
    fun subscribe() {
        assertEquals(2, rssFeed.subscribers().size)
        assertFalse(rssFeed.subscribe(User("nick_a")))
        assert(rssFeed.subscribe(User("nick_c")))
        assertEquals(3, rssFeed.subscribers().size)
        assertNotEquals(-1, rssFeed.subscribers().indexOf("nick_c"))
    }

    @Test
    fun unsubscribe() {
        assertEquals(2, rssFeed.subscribers().size)
        assertFalse(rssFeed.unsubscribe(User("nick_c")))
        assert(rssFeed.unsubscribe(User("nick_a")))
        assertEquals(1, rssFeed.subscribers().size)
        assertEquals(-1, rssFeed.subscribers().indexOf("nick_a"))
    }

    @Test
    fun check() = runBlocking {
        withTimeout(10000L) {
            rssFeed.add(testUrl)
            assertEquals(2, rssFeed.list().size)

            val items = rssFeed.check().await()
            val item = items[0]
            assertEquals(1, items.size)
            assertEquals(testUrl, item.rssFeedUrl)
            assertNotNull(item.title)
            assertNotNull(item.url)
            assert(item.hasNewContent)

            assertEquals(1, rssFeed.list().size)
        }
    }
}