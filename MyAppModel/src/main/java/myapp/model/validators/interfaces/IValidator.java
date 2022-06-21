package myapp.model.validators.interfaces;

import myapp.model.entities.Identifiable;
import myapp.model.validators.ValidationException;

public interface IValidator<ID, E extends Identifiable<ID>> {
    /**
     * validates an entity
     *
     * @param entity said entity
     * @throws ValidationException if entity is invalid
     */
    void validate(E entity) throws ValidationException;
}
