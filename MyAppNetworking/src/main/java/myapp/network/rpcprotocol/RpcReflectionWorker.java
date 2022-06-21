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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.time.LocalDate;


public class RpcReflectionWorker implements Runnable, IObserver {
    private final ISuperService server;
    private final Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    public RpcReflectionWorker(ISuperService server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (connected) {
            try {
                Object request = input.readObject();
                Response response = handleRequest((Request) request);
                if (response != null) {
                    sendResponse(response);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error handling request;");
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            System.out.println("Error " + e);
        }
    }

    @Override
    public void updateTicketSold(Ticket ticket) {
        Response resp = new Response.Builder().type(ResponseType.TICKET_BOUGHT).data(ticket).build();
        System.out.println("Ticket bought");
        try {
            sendResponse(resp);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    private static final Response okResponse = new Response.Builder().type(ResponseType.OK).build();
    private Response handleRequest(Request request) {
        Response response = null;
        String handlerName = "handle" + (request).type();
        System.out.println("HandlerName: " + handlerName);
        try {
            Method method = this.getClass().getDeclaredMethod(handlerName, Request.class);
            response = (Response) method.invoke(this, request);
            System.out.println("Method " + handlerName + " invoked");
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return response;
    }

    private Response handleLOGIN(Request request) {
        System.out.println("Inside handleLOGIN");
        try {
            User user = (User) request.data();
            server.login(user.getUsername(), user.getPassword(), this);
            return okResponse;
        } catch (ServiceException e) {
            connected = false;
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleLOGOUT(Request request) {
        System.out.println("Inside handleLOGOUT");
        try {
            User user = (User) request.data();
            server.logout(user.getUsername());
            connected = false;
            return okResponse;
        } catch (ServiceException e) {
            e.printStackTrace();
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleGET_FESTIVALS(Request request) {
        System.out.println("Inside handleGET_FESTIVALS");
        try {
            Festival[] festivals = server.getAllFestivals().toArray(new Festival[0]);
            return new Response.Builder().type(ResponseType.GET_FESTIVALS).data(festivals).build();
        } catch (ServiceException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleGET_FESTIVALS_ON_DATE(Request request) {
        System.out.println("Inside handleGET_FESTIVALS_ON_DATE");
        try {
            Festival data = (Festival) request.data();
            LocalDate date = data.getDate();
            Festival[] festivals = server.getAllFestivalsOnDate(date).toArray(new Festival[0]);
            return new Response.Builder().type(ResponseType.GET_FESTIVALS_ON_DATE).data(festivals).build();
        } catch (ServiceException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleBUY_TICKET(Request request) {
        System.out.println("Inside handleBUY_TICKET");
        try {
            Ticket data = (Ticket) request.data();
            server.sellTicket(data.getFestival().getId(), data.getBuyerName(), data.getNumberOfSpots());
            return okResponse;
        } catch (ServiceException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private void sendResponse(Response response) throws IOException {
        System.out.println("Sending response:" + response);
        output.writeObject(response);
        output.flush();
    }
}
