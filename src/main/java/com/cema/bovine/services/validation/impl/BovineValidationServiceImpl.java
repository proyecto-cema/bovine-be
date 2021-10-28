package com.cema.bovine.services.validation.impl;

import com.cema.bovine.constants.Categories;
import com.cema.bovine.domain.Bovine;
import com.cema.bovine.entities.CemaBovine;
import com.cema.bovine.exceptions.ValidationException;
import com.cema.bovine.services.validation.BovineValidationService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class BovineValidationServiceImpl implements BovineValidationService {

    private boolean canChangeTo(String category, String status) {
        return Categories.allowedStatesOf(category).contains(status);
    }

    private boolean isCorrectSex(String category, String genre) {
        return (Categories.VACA.equalsIgnoreCase(category) && Categories.HEMBRA.equalsIgnoreCase(genre))
                || (Categories.TORO.equalsIgnoreCase(category) && Categories.MACHO.equalsIgnoreCase(genre))
                || Categories.TERNERO.equalsIgnoreCase(category);
    }

    @Override
    public void validateBovineCreation(Bovine bovine) {
        String state = bovine.getStatus();
        String category = StringUtils.capitalize(bovine.getCategory());
        String genre = bovine.getGenre();

        if (!isCorrectSex(category, genre)) {
            throw new ValidationException(String.format("A %s is not allowed to be %s", category, genre));
        }

        if (!canChangeTo(category, state)) {
            throw new ValidationException(String.format("A %s is only allowed to be in the following states: %s", category, Categories.allowedStatesOf(category)));
        }
    }

    @Override
    public void validateBovineUpdate(Bovine bovine, CemaBovine cemaBovine) {
        String currentState = cemaBovine.getStatus();
        String currentCategory = StringUtils.capitalize(cemaBovine.getCategory());
        String currentGenre = cemaBovine.getGenre();

        String newState = bovine.getStatus();
        String newCategory = StringUtils.capitalize(bovine.getCategory());
        String newGenre = bovine.getGenre();

        if (!StringUtils.hasText(newGenre)) {
            newGenre = currentGenre;
        }

        if (!StringUtils.hasText(newState)) {
            newState = currentState;
        }

        if (StringUtils.hasText(newCategory)) {
            if (!currentCategory.equalsIgnoreCase(newCategory) && !currentCategory.equalsIgnoreCase(Categories.TERNERO)) {
                throw new ValidationException(String.format("Cannot change category from %s to %s", currentCategory, newCategory));
            }
        } else {
            newCategory = currentCategory;
        }

        if (!isCorrectSex(newCategory, newGenre)) {
            throw new ValidationException(String.format("A %s is not allowed to be %s", newCategory, newGenre));
        }

        if (!currentState.equalsIgnoreCase(newState) && !canChangeTo(newCategory, newState)) {
            throw new ValidationException(String.format("A %s is only allowed to be in the following states: %s", newCategory, Categories.allowedStatesOf(newCategory)));
        }
    }
}
