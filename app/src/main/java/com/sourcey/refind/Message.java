package com.sourcey.refind;

import com.sourcey.refind.model.MemberModel;

public class Message {
    private String text;
    private MemberModel data;
    private boolean belongsToCurrentUser;

    public Message(String text,MemberModel data, boolean belongsToCurrentUser) {
        this.text = text;
        this.data = data;
        this.belongsToCurrentUser = belongsToCurrentUser;
    }

    public String getText() {
        return text;
    }

    public MemberModel getData() {
        return data;
    }

    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }
}
