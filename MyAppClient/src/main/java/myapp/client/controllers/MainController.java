package myapp.client.controllers;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import myapp.model.entities.Festival;
import myapp.model.entities.Ticket;
import myapp.services.interfaces.ISuperService;
import myapp.services.interfaces.IObserver;

import java.io.IOException;
import java.time.LocalDate;

public class MainController implements IObserver {

    //javafx
    public TableView<Festival> festivalsTableView;
    public TableColumn<Festival, String> festivalsNameColumn;
    public TableColumn<Festival, LocalDate> festivalsDateColumn;
    public TableColumn<Festival, String> festivalsPlaceColumn;
    public TableColumn<Festival, Integer> festivalsTotalColumn;
    public TableColumn<Festival, Integer> festivalsSoldColumn;
    public TableView<Festival> dateFestivalsTableView;
    public TableColumn<Festival, String> dateFestivalsNameColumn;
    public TableColumn<Festival, Integer> dateFestivalsTimeColumn;
    public TableColumn<Festival, String> dateFestivalsPlaceColumn;
    public TableColumn<Festival, Integer> dateFestivalsTotalColumn;
    public TableColumn<Festival, Integer> dateFestivalsSoldColumn;
    public DatePicker searchDatePicker;
    public TextField buyerNameTextField;
    public TextField spotsNumberTextField;
    public RadioButton allRadioButton;
    public RadioButton dateRadioButton;

    private ObservableList<Festival> festivals;
    private ObservableList<Festival> dateFestivals;


    //mine
    private ISuperService superService;
    private Stage stage;
    private String username;


    public ISuperService getSuperService() {
        return superService;
    }

    public void setSuperService(ISuperService superService) {
        this.superService = superService;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void initializeForShow() {
        initializeForBuy();
        initializeFestivalTableView();
        initializeDateFestivalTableView();
        loadFestivalTableView();
    }

    private void initializeFestivalTableView() {
        festivals = FXCollections.observableArrayList();
        festivalsTableView.setItems(festivals);
        festivalsNameColumn.setCellValueFactory(new PropertyValueFactory<>("artistName"));
        festivalsDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        festivalsPlaceColumn.setCellValueFactory(new PropertyValueFactory<>("place"));
        festivalsTotalColumn.setCellValueFactory(new PropertyValueFactory<>("remainingSpots"));
        festivalsSoldColumn.setCellValueFactory(new PropertyValueFactory<>("soldSpots"));
        festivalsTableView.setRowFactory(value -> new TableRow<>() {
            @Override
            protected void updateItem(Festival item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item != null) {
                    this.styleProperty().bind(Bindings.createStringBinding(() ->
                            {
                                if (item.getRemainingSpots() == 0) {
                                    return "-fx-background-color: red;";
                                }
                                return " ";
                            }
                    ));
                }
            }
        });
    }

    private void initializeDateFestivalTableView() {
        dateFestivals = FXCollections.observableArrayList();
        dateFestivalsTableView.setItems(dateFestivals);
        dateFestivalsNameColumn.setCellValueFactory(new PropertyValueFactory<>("artistName"));
        dateFestivalsTimeColumn.setCellValueFactory(new PropertyValueFactory<>("hour"));
        dateFestivalsPlaceColumn.setCellValueFactory(new PropertyValueFactory<>("place"));
        dateFestivalsTotalColumn.setCellValueFactory(new PropertyValueFactory<>("remainingSpots"));
        dateFestivalsSoldColumn.setCellValueFactory(new PropertyValueFactory<>("soldSpots"));
        dateFestivalsTableView.setRowFactory(value -> new TableRow<>() {
            @Override
            protected void updateItem(Festival item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item != null) {
                    this.styleProperty().bind(Bindings.createStringBinding(() ->
                            {
                                if (item.getRemainingSpots() == 0) {
                                    return "-fx-background-color: red;";
                                }
                                return " ";
                            }
                    ));
                }
            }
        });
    }

    private void initializeForBuy() {
        ToggleGroup buyGroup = new ToggleGroup();
        allRadioButton.setToggleGroup(buyGroup);
        dateRadioButton.setToggleGroup(buyGroup);
        allRadioButton.setSelected(true);
    }

    private void loadFestivalTableView() {
        festivals.clear();
        try {
            festivals.addAll(superService.getAllFestivals());
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage());
            alert.showAndWait();
        }
        festivalsTableView.refresh();
    }

    private void loadDateFestivalTableView() {
        dateFestivals.clear();
        try {
            LocalDate date = searchDatePicker.getValue();
            if (date == null) {
                throw new Exception("No date selected!;");
            }
            dateFestivals.addAll(superService.getAllFestivalsOnDate(date));
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage());
            alert.showAndWait();
        }
        dateFestivalsTableView.refresh();
    }


    @FXML
    public void onLogoutButtonClick() throws IOException {

        try {
            superService.logout(username);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        FXMLLoader loader = new FXMLLoader(LoginController.class.getResource("login.fxml"));
        Parent root=loader.load();


        LoginController ctrl = loader.getController();
        ctrl.setSuperService(getSuperService());
        ctrl.setStage(getStage());
        stage.setScene(new Scene(root));
        stage.show();
    }


    @FXML
    public void onRefreshButtonClick() {
        loadFestivalTableView();
    }

    @FXML
    public void onSearchButtonClick() {
        loadDateFestivalTableView();
    }

    @FXML
    public void onBuyButtonClick() {
        try {
            String buyerName = buyerNameTextField.getText();
            Integer spots = Integer.parseInt(spotsNumberTextField.getText());
            Integer festivalID;
            if (allRadioButton.isSelected()) {
                int selected = festivalsTableView.getSelectionModel().getSelectedIndex();
                if (selected < 0)
                    throw new Exception("No item selected!;");
                festivalID = festivals.get(selected).getId();
            } else {
                int selected = dateFestivalsTableView.getSelectionModel().getSelectedIndex();
                if (selected < 0)
                    throw new Exception("No item selected!;");
                festivalID = dateFestivals.get(selected).getId();
            }
            superService.sellTicket(festivalID, buyerName, spots);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Tickets bought!");
            alert.showAndWait();
            loadFestivalTableView();
            if (searchDatePicker.getValue() != null)
                loadDateFestivalTableView();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage());
            alert.showAndWait();
        }
    }


    @Override
    public void updateTicketSold(Ticket ticket) {
        Platform.runLater(()->{
            for(Festival festival:festivals){
                if(festival.getId().equals(ticket.getFestival().getId())){
                    festival.setSoldSpots(festival.getSoldSpots()+ticket.getNumberOfSpots());
                }
            }
            festivalsTableView.refresh();
            for(Festival festival:dateFestivals){
                if(festival.getId().equals(ticket.getFestival().getId())){
                    festival.setSoldSpots(festival.getSoldSpots()+ticket.getNumberOfSpots());
                }
            }
            dateFestivalsTableView.refresh();
        });
    }
}
