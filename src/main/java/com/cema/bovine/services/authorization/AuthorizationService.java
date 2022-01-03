package com.cema.bovine.services.authorization;

public interface AuthorizationService {
    String getUserAuthToken();

    String getCurrentUserCuig();

    boolean isOnTheSameEstablishment(String cuig);

    boolean isAdmin();
}
