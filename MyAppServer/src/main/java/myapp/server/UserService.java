package myapp.server;

import myapp.model.entities.User;
import myapp.model.validators.interfaces.IUserValidator;
import myapp.persistence.interfaces.IUserRepository;
import myapp.model.validators.ValidationException;
import myapp.services.ServiceException;
import myapp.services.interfaces.IUserService;

public class UserService implements IUserService {

    private final IUserValidator userValidator;
    private final IUserRepository userRepository;

    /**
     * constructs a user service based on given validator and repository
     *
     * @param userValidator  - said validator
     * @param userRepository - said repository
     */
    public UserService(IUserValidator userValidator, IUserRepository userRepository) {
        this.userValidator = userValidator;
        this.userRepository = userRepository;
    }

    @Override
    public void login(String username, String password) throws ServiceException {
        try {
            userValidator.validateForLogin(username, password);
        } catch (ValidationException e) {
            throw new ServiceException(e.getMessage());
        }
        User user = userRepository.read(username);
        boolean toReturn = user != null && user.getPassword().equals(password);
        if(!toReturn){
            throw new ServiceException("No user with given credentials");
        }
    }
}
