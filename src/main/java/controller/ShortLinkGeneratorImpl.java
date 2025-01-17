package controller;

import java.util.UUID;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;
import service.ShortLinkGenerator;

public class ShortLinkGeneratorImpl implements ShortLinkGenerator {
    private static final String BASE_URL = "clck.ru/";
    private final int keyLength = 8;

    @Override
    public String generateShortLink(String longUrl, UUID userUUID) {
        String uniqueKey = generateUniqueKey(longUrl, userUUID);
        return BASE_URL + uniqueKey;
    }
    private String generateUniqueKey(String longUrl, UUID userUUID) {
        String combinedString = longUrl + userUUID.toString();
        String hashedString = hashString(combinedString);
        String base64String = new String(Base64.encodeBase64(hashedString.getBytes(StandardCharsets.UTF_8)));
        return base64String.substring(0,keyLength);
    }

    private String hashString(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Can't get Hash",e);
        }
    }
}