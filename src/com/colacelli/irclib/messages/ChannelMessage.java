package com.colacelli.irclib.messages;

import com.colacelli.irclib.actors.Channel;
import com.colacelli.irclib.actors.User;

public class ChannelMessage extends Message {
    protected Channel channel;

    public ChannelMessage(Builder builder) {
        sender = builder.sender;
        channel = builder.channel;
        text = builder.text;
    }

    public Channel getChannel() {
        return channel;
    }

    public static class Builder {
        private User sender;
        private Channel channel;
        private String text;

        public Builder setSender(User sender) {
            this.sender = sender;
            return this;
        }

        public Builder setChannel(Channel channel) {
            this.channel = channel;
            return this;
        }

        public Builder setText(String text) {
            this.text = text;
            return this;
        }

        public ChannelMessage build() {
            return new ChannelMessage(this);
        }
    }
}
