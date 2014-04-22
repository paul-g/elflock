package org.paulg.ispend.view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.converter.NumberStringConverter;
import org.paulg.ispend.model.Account;

import java.util.Observable;
import java.util.Observer;

public class AccountSummaryView extends VBox implements Observer {

    private final ISpendPane ispendPane;

    public AccountSummaryView(ISpendPane ispendPane) {
        this.ispendPane = ispendPane;
        final Label label = UiUtils.section("Accounts Summary");
        getChildren().addAll(label);
        setPadding(new Insets(30, 5, 5, 5));
        setSpacing(15);
    }

    private GridPane accountFieldView(Account account) {
        GridPane gp = new GridPane();
        String[] desc = {
                "Balance",
                "Last Record",
                "Total Records",
                "Covered",
                "Total Spent",
                "Total Earned"};
        String[] values = {
                Double.toString(account.getBalance()),
                account.getLastRecordDate(),
                Integer.toString(account.getTotalRecords()),
                Integer.toString(account.getCovered()),
                "None", "None"};

        for (int i = 0; i < 3; i++) {
            Label l = UiUtils.subsection(desc[i]);
            gp.addRow(i,
                    l,
                    new Label(values[i]));
        }

        gp.add(UiUtils.subsection("Covered"), 0, 3);
        Label covered = new Label();

        covered.textProperty().bindBidirectional(
                account.getCoveredStringProperty(),
                new NumberStringConverter()
        );

        gp.add(covered, 1, 3);
        gp.setPadding(new Insets(10, 10, 10, 10));
        gp.setVgap(10);
        gp.setHgap(10);
        return gp;
    }

    private VBox accountView(Account account) {
        VBox box = new VBox();
        Label l = UiUtils.section(account.getName());
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
