package entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;
@Data
@Builder
public class Link {

    public static final int DEFAULT_EXPIRATION_MINUTES = 60;
    private UUID id;
    private String longlink;
    private String shortlink;
    private UUID userUUID;
    private int clicksleft;
    private LocalDateTime expirationDate;
    private boolean active;
}
