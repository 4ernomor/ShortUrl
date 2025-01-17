package service;

import java.util.UUID;

public interface UserService {
    UUID getUserUUID(String headerUUID);
}
