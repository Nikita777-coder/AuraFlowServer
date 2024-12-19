package app.entity.userattributes;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {
    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),
    USER_READ_RECORDS("user:read_records"),
    USER_UPDATE("user:update"),
    USER_CREATE_RECORD("management:create_record"),
    USER_DELETE_RECORD("management:delete_record");

    private final String permission;
}

