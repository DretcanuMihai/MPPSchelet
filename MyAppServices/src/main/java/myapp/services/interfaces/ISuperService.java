package myapp.services.interfaces;

import myapp.model.entities.Festival;
import myapp.services.ServiceException;

import java.time.LocalDate;
import java.util.Collection;

public interface ISuperService {

    void login(String username, String password, IObserver observer) throws ServiceException;

    void logout(String username) throws ServiceException;

    Collection<Festival> getAllFestivals()throws ServiceException;

    Collection<Festival> getAllFestivalsOnDate(LocalDate date) throws ServiceException;

    void sellTicket(Integer festivalID, String buyerName, Integer spots) throws ServiceException;

}
