package com.cema.bovine.services.health;

import com.cema.bovine.domain.health.Illness;
import lombok.SneakyThrows;

public interface HealthClientService {
    @SneakyThrows
    Illness getBovineIllness(String tag);
}
