package org.paulg.ispend.view;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.paulg.ispend.model.Account;

public class AccountSummaryView extends VBox {

    public AccountSummaryView(ObservableList<Account> accountsData) {
        final Label label = new Label("Accounts");
        label.setFont(new Font("Arial", 20));

        CompleteTableView<Account> accounts = new CompleteTableView<Account>(Account.class);
        accounts.setItems(accountsData);
        accounts.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        getChildren().addAll(label, accounts);
        setPadding(new Insets(30, 5, 5, 5));
        setSpacing(5);
        HBox.setHgrow(accounts, Priority.ALWAYS);
    }
}
