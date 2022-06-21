package myapp.services.interfaces;


import myapp.services.ServiceException;

public interface IUserService {

    void login(String username, String password) throws ServiceException;
}
