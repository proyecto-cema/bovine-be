package com.cema.bovine.services.administration;

import com.cema.bovine.domain.audit.Audit;

public interface AdministrationClientService {

    void validateEstablishment(String cuig);

    void sendAuditRequest(Audit audit);
}
