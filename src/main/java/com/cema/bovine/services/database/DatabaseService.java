package com.cema.bovine.services.database;

import com.cema.bovine.entities.CemaBovine;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DatabaseService {

    Page<CemaBovine> searchBovines(String tag, String genre, String description, int page, int size);
}
