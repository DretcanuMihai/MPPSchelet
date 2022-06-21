package myapp.model.validators.interfaces;


import myapp.model.entities.User;
import myapp.model.validators.ValidationException;

public interface IUserValidator extends IValidator<String, User> {

    /**
     * validates credentials for login functions
     *
     * @param username - said username
     * @param password - said password
     * @throws ValidationException if credentials are null
     */
    void validateForLogin(String username, String password) throws ValidationException;
}
