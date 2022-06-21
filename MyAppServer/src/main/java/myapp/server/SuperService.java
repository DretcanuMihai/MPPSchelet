package myapp.server;

import myapp.model.entities.Festival;
import myapp.model.entities.Ticket;
import myapp.services.ServiceException;
import myapp.services.interfaces.IFestivalService;
import myapp.services.interfaces.ISuperService;
import myapp.services.interfaces.IObserver;
import myapp.services.interfaces.IUserService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SuperService implements ISuperService {
    private final IFestivalService festivalService;
    private final IUserService userService;
    private final Map<String, IObserver> loggedClients;

    public SuperService(IFestivalService festivalService, IUserService userService) {
        this.festivalService = festivalService;
        this.userService = userService;
        loggedClients=new ConcurrentHashMap<>();
    }

    @Override
    public synchronized void login(String username, String password, IObserver observer) throws ServiceException {
        userService.login(username, password);
        if(loggedClients.get(username)!=null) {
            throw new ServiceException("User already logged in.");
        }
        loggedClients.put(username,observer);
    }

    @Override
    public synchronized void logout(String username) throws ServiceException {
        IObserver ticketObserver=loggedClients.remove(username);
        if(ticketObserver==null){
            throw new ServiceException("User not logged in!\n");
        }
    }

    @Override
    public synchronized Collection<Festival> getAllFestivals() {
        return festivalService.getAllFestivals();
    }

    @Override
    public synchronized Collection<Festival> getAllFestivalsOnDate(LocalDate date) throws ServiceException{
        return festivalService.getAllFestivalsOnDate(date);
    }

    private final int defaultThreadsNo=5;

    private void notifyUsers(Ticket ticket){
        ExecutorService executor= Executors.newFixedThreadPool(defaultThreadsNo);
        for(IObserver observer: loggedClients.values()){
            executor.execute(()->observer.updateTicketSold(ticket));
        }
    }

    @Override
    public synchronized void sellTicket(Integer festivalID, String buyerName, Integer spots) throws ServiceException{
        Ticket ticket=festivalService.sellTicket(festivalID, buyerName, spots);
        notifyUsers(ticket);
    }
}
