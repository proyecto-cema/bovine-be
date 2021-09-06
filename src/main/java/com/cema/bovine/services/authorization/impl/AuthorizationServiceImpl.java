package com.cema.bovine.services.authorization.impl;

import com.cema.bovine.constants.Roles;
import com.cema.bovine.services.authorization.AuthorizationService;
import com.cema.users.domain.CemaUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    @Override
    public String getCurrentUserCuig() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CemaUserDetails cemaUserDetails = (CemaUserDetails) authentication.getPrincipal();
        return cemaUserDetails.getCuig();
    }

    @Override
    public boolean isOnTheSameEstablishment(String cuig) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CemaUserDetails cemaUserDetails = (CemaUserDetails) authentication.getPrincipal();
        String authenticationCuig = cemaUserDetails.getCuig();
        return authenticationCuig.equals(cuig) || isAdmin();
    }

    @Override
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(Roles.ADMIN));
    }
}
