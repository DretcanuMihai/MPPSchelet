package myapp.network.rpcprotocol;

import myapp.model.entities.Festival;
import myapp.model.entities.Ticket;
import myapp.model.entities.User;
import myapp.services.ServiceException;
import myapp.services.interfaces.ISuperService;
import myapp.services.interfaces.IObserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class RpcServerProxy implements ISuperService {
    private final String host;
    private final int port;

    private IObserver client;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private final BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    public RpcServerProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingQueue<>();
    }

    @Override
    public void login(String username, String password, IObserver observer) throws ServiceException {
        initializeConnection();
        User user = new User(username, password);
        Request req = new Request.Builder().type(RequestType.LOGIN).data(user).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            this.client = observer;
        } else if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            closeConnection();
            throw new ServiceException(err);
        }
    }


    @Override
    public void logout(String username) throws ServiceException {
        Request req = new Request.Builder().type(RequestType.LOGOUT).data(new User(username, null)).build();
        sendRequest(req);
        Response response = readResponse();
        closeConnection();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ServiceException(err);
        }
    }

    @Override
    public Collection<Festival> getAllFestivals() throws ServiceException {
        Request req = new Request.Builder().type(RequestType.GET_FESTIVALS).data(null).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ServiceException(err);
        }
        Festival[] data = (Festival[]) response.data();
        return Arrays.stream(data).toList();
    }

    @Override
    public Collection<Festival> getAllFestivalsOnDate(LocalDate date) throws ServiceException {
        Festival festival = new Festival();
        festival.setDateTime(date.atTime(0, 0));
        Request req = new Request.Builder().type(RequestType.GET_FESTIVALS_ON_DATE).data(festival).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ServiceException(err);
        }
        Festival[] data = (Festival[]) response.data();
        return Arrays.stream(data).toList();
    }

    @Override
    public void sellTicket(Integer festivalID, String buyerName, Integer spots) throws ServiceException {
        Festival festival = new Festival();
        festival.setId(festivalID);
        Ticket ticket = new Ticket(null, buyerName, festival, spots);
        Request req = new Request.Builder().type(RequestType.BUY_TICKET).data(ticket).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ServiceException(err);
        }
    }

    private void closeConnection() {
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
            client = null;
        } catch (IOException e) {
            System.err.println("Error closing the connection;");
            e.printStackTrace();
        }

    }

    private void sendRequest(Request request) throws ServiceException {
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            System.err.println("Error sending request;");
            throw new ServiceException("Error sending object " + e);
        }

    }

    private Response readResponse() {
        Response response = null;
        try {
            response = qresponses.take();
        } catch (InterruptedException e) {
            System.err.println("Error starting reader;");
            e.printStackTrace();
        }
        return response;
    }

    private void initializeConnection() {
        try {
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        } catch (IOException e) {
            System.err.println("Error initializing connection;");
            e.printStackTrace();
        }
    }

    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }


    private void handleUpdate(Response response) {
        if (response.type() == ResponseType.TICKET_BOUGHT) {
            Ticket ticket = (Ticket) response.data();
            System.out.println("Tickets bought" + ticket.toString());
            client.updateTicketSold(ticket);
        }
    }

    private boolean isUpdate(Response response) {
        return response.type() == ResponseType.TICKET_BOUGHT;
    }

    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    Object response = input.readObject();
                    System.out.println("Response received:" + response);
                    if (isUpdate((Response) response)) {
                        handleUpdate((Response) response);
                    } else {

                        try {
                            qresponses.put((Response) response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("Error receiving message;");
                    e.printStackTrace();
                }
            }
        }
    }
}
