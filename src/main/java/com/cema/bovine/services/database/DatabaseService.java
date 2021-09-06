package com.cema.bovine.services.database;

import com.cema.bovine.entities.CemaBovine;
import org.springframework.data.domain.Page;

public interface DatabaseService {

    Page<CemaBovine> searchBovines(String cuig, String tag, String genre, String description, int page, int size);
}
