package pl.rstepniewski.sockets.domain.message;

/**
 * Created by rafal on 19.04.2023
 *
 * @author : rafal
 * @date : 19.04.2023
 * @project : SocketProgrammingClientServer
 */

public enum MessageConst {
    MAX_LENGTH_OF_MESSAGE(255),
    MAX_NUMBER_OF_MESSAGES(5);

    private final int messageLenght;

    MessageConst(final int messageLenght) {
        this.messageLenght = messageLenght;
    }

    public int getMessageLenght() {
        return messageLenght;
    }
}
