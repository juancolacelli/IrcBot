package com.colacelli.ircbot.base

class AsciiTable(private val titles : Array<String>, private val rows : ArrayList<Array<String>>) {
    private val maxLengths = ArrayList<Int>()
    private var lineLength = 0

    companion object {
        const val LINE_CHAR = "-"
    }

    init {
        titles.forEach {
            maxLengths.add(it.length)
        }

        rows.forEach {
            for (i in 0 until it.size) {
                val text = it[i]
                if (maxLengths[i] < text.length) maxLengths[i] = text.length
            }
        }

        lineLength = maxLengths.sum() + (maxLengths.size * 3)
    }

    fun toText() : ArrayList<String> {

        val texts = ArrayList<String>()

        rows.forEach {
            texts.add(row(it))
        }

        texts.sort()
        texts.add(0, line())
        texts.add(0, row(titles))
        texts.add(0, line())
        texts.add(line())

        return texts
    }

    private fun row(texts : Array<String>) : String {
        var format = ""

        for (i in 0 until texts.size) {
            if (i == 0) format += "|"
            format += " %-${maxLengths[i]}s |"
        }

        return String.format(format, *texts)
    }

    private fun line() : String {
        var line = ""

        for (i in 0..lineLength) {
            line += LINE_CHAR
        }

        return line
    }
}