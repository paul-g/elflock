package org.paulg.ispend.view;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.paulg.ispend.main.ISpend;
import org.paulg.ispend.model.Account;

import java.util.Observable;
import java.util.Observer;

public class AccountSummaryView extends VBox implements Observer {

    private final ISpendPane ispendPane;
    private Font accountLabelFont = new Font("Arial", 16);
    private Font accountNameFont = new Font("Arial", 19);

    public AccountSummaryView(ISpendPane ispendPane) {
        this.ispendPane = ispendPane;

        final Label label = new Label("Accounts");
        label.setFont(new Font("Arial", 20));

        getChildren().addAll(label);
        setPadding(new Insets(30, 5, 5, 5));
        setSpacing(15);
    }

    private GridPane accountFieldView(Account account) {
        GridPane gp = new GridPane();
        String [] desc = {
                "Balance",
                "Last Record",
                "Total Records",
                "Covered",
                "Total Spent",
                "Total Earned"};
        String [] values = {
                Double.toString(account.getBalance()),
                account.getLastRecordDate(),
                Integer.toString(account.getTotalRecords()),
                Integer.toString(account.getCovered()),
        "None", "None"};

        for (int i = 0; i < desc.length; i++) {
            Label l = new Label(desc[i]);
            l.setFont(accountLabelFont);
            gp.addRow(i,
                    l,
                    new Label(values[i]));
        }
        gp.setPadding(new Insets(10, 10, 10, 10));
        gp.setVgap(10);
        gp.setHgap(10);
        return gp;
    }

    private VBox accountView(Account account) {
        VBox box = new VBox();
        Label l = new Label(account.getName());
        l.setFont(accountNameFont);
        box.getChildren().add(l);
        box.getChildren().add(accountFieldView(account));
        box.setPadding(new Insets(10, 10, 10, 10));
        return box;
    }

    @Override
    public void update(Observable o, Object arg) {
        // TODO this may be a bit inefficient
        getChildren().clear();
        for (Account a : ispendPane.getAccounts()) {
            getChildren().add(accountView(a));
        }
    }
}
