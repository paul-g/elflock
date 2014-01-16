package org.paulg.ispend.view;

import javafx.scene.control.ListCell;
import org.paulg.ispend.model.Account;

public class AccountCell extends ListCell<Account> {

    @Override
    protected void updateItem(final Account account, final boolean empty) {
        super.updateItem(account, empty);

        if (account != null) {
            setText("name: " + account.getName() +
                    ", number: " + account.getNumber() +
                    ", total records: " + account.getTotal() +
                    ", total covered: " + account.getCovered() +
                    " (" + ((account.getCovered() * 100) / account.getTotal()) + "%)");
        }
    }
}
