package com.cema.bovine.services.database;

import com.cema.bovine.domain.Batch;
import com.cema.bovine.entities.CemaBatch;
import com.cema.bovine.entities.CemaBovine;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DatabaseService {

    Page<CemaBovine> searchBovines(String cuig, String tag, String genre, String description, int page, int size);

    CemaBatch saveCemaBatch(Batch batch);

    void deleteBatch(CemaBatch cemaBatch);

    CemaBatch addBovinesToBatch(CemaBatch cemaBatch, List<String> bobineTags);

    CemaBatch removeBovinesFromBatch(CemaBatch cemaBatch, List<String> bobineTags);
}
