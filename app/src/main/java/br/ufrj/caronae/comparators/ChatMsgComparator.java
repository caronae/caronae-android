package br.ufrj.caronae.comparators;

import br.ufrj.caronae.models.ChatMessageReceived;

public class ChatMsgComparator implements java.util.Comparator<ChatMessageReceived> {
    @Override
    public int compare(ChatMessageReceived msg1, ChatMessageReceived msg2) {
        return msg1.getTime().compareTo(msg2.getTime());
    }
}
