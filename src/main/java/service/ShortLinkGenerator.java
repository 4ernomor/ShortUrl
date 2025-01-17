package service;

import java.util.UUID;

public interface ShortLinkGenerator {
    String generateShortLink(String longUrl, UUID userUUID);
}
