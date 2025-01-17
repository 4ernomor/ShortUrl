package controller;

import service.UserService;

import java.util.UUID;

public class UserServiceImpl implements UserService {
    @Override
    public UUID getUserUUID(String headerUUID){
        UUID userUUID;
        if (headerUUID != null){
            try {
                userUUID = UUID.fromString(headerUUID);
            } catch (IllegalArgumentException e){
                userUUID =  UUID.randomUUID();
            }
        } else {
            userUUID =  UUID.randomUUID();
        }
        return userUUID;
    }
}
