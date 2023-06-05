package pl.rstepniewski.sockets.domain.user;

/**
 * Created by rafal on 19.04.2023
 *
 * @author : rafal
 * @date : 19.04.2023
 * @project : SocketProgrammingClientServer
 */

public enum UserRole {
    USER("USER"), ADMIN("ADMIN");

    private final String roleName;

    UserRole(final String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
