package org.paulg.ispend.view;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import org.paulg.ispend.model.Account;

public class AccountSummaryView extends HBox {

    public AccountSummaryView(ObservableList<Account> accountsData) {
        final Label label = new Label("Accounts");
        label.setFont(new Font("Arial", 20));

        ListView<Account> accounts = new ListView<>(accountsData);
        accounts.setCellFactory(listViewAccount -> new AccountCell());

        accounts.setPrefHeight(50);

        setAlignment(Pos.CENTER);
        getChildren().addAll(label, accounts);
        setPadding(new Insets(10, 10, 10, 10));
        setSpacing(10);
        HBox.setHgrow(accounts, Priority.ALWAYS);
    }
}
