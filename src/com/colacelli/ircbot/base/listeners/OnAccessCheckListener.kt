package com.colacelli.ircbot.base.listeners

import com.colacelli.ircbot.base.Access
import com.colacelli.irclib.actors.User

interface OnAccessCheckListener {
    fun onSuccess(user: User, level: Access.Level)
    fun onError(user: User, level: Access.Level)
}