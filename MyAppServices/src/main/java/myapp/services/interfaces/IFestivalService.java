package myapp.services.interfaces;

import myapp.model.entities.Festival;
import myapp.model.entities.Ticket;
import myapp.services.ServiceException;

import java.time.LocalDate;
import java.util.Collection;

public interface IFestivalService {

    Collection<Festival> getAllFestivals();

    Collection<Festival> getAllFestivalsOnDate(LocalDate date) throws ServiceException;

    Ticket sellTicket(Integer festivalID, String buyerName, Integer spots) throws ServiceException;
}
