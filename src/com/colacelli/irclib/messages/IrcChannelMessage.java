package com.colacelli.irclib.messages;

import com.colacelli.irclib.actors.IrcChannel;
import com.colacelli.irclib.actors.IrcUser;

public class IrcChannelMessage extends IrcMessage {
    protected IrcChannel channel;

    public IrcChannelMessage(Builder builder) {
        sender = builder.sender;
        channel = builder.channel;
        text = builder.text;
    }

    public IrcChannel getChannel() {
        return channel;
    }

    public static class Builder {
        private IrcUser sender;
        private IrcChannel channel;
        private String text;

        public Builder setSender(IrcUser sender) {
            this.sender = sender;
            return this;
        }

        public Builder setChannel(IrcChannel channel) {
            this.channel = channel;
            return this;
        }

        public Builder setText(String text) {
            this.text = text;
            return this;
        }

        public IrcChannelMessage build() {
            return new IrcChannelMessage(this);
        }
    }
}
