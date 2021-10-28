package com.cema.bovine.constants;

import com.google.common.collect.ImmutableSet;
import org.springframework.util.StringUtils;

import java.util.Set;

public class Categories {

    public static final String TERNERO = "Ternero";
    public static final String VACA = "Vaca";
    public static final String TORO = "Toro";
    public static final String MACHO = "Macho";
    public static final String HEMBRA = "Hembra";
    public static final Set<String> TERNERO_ALLOWED_STATES = ImmutableSet.of(Status.MAMANDO, Status.DESTETADO, Status.MUERTO, Status.VENDIDO);
    public static final Set<String> VACA_ALLOWED_STATES = ImmutableSet.of(Status.SIN_PRENEZ, Status.PRENADA, Status.MUERTO, Status.VENDIDO);
    public static final Set<String> TORO_ALLOWED_STATES = ImmutableSet.of(Status.EN_SERVICIO, Status.FUERA_DE_SERVICIO, Status.MUERTO, Status.VENDIDO);


    public static Set<String> allowedStatesOf(String category) {
        category = StringUtils.capitalize(category);
        switch (category) {
            case TERNERO:
                return TERNERO_ALLOWED_STATES;
            case VACA:
                return VACA_ALLOWED_STATES;
            case TORO:
                return TORO_ALLOWED_STATES;
        }
        return null;
    }


}
