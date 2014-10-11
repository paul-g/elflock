package org.paulg.ispend.view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
        setSpacing(15);
    }

    private GridPane accountFieldView(Account account) {
        GridPane gp = new GridPane();
        String[] desc = {
                "Balance",
                "Last Record",
                "First Record",
                "Total Records"
        };
        String[] values = {
                Double.toString(account.getBalance()),
                account.getLastRecordDate(),
                account.getFirstRecordDate(),
                Integer.toString(account.getTotalRecords()),
        };

        for (int i = 0; i < desc.length; i++) {
            gp.addRow(i,
                    UiUtils.subsection(desc[i]),
                    new Label(values[i]));
        }
        gp.add(UiUtils.subsection("Covered"), 0, desc.length);
        Label covered = new Label();
        covered.textProperty().bindBidirectional(
                account.getCoveredStringProperty(),
                new NumberStringConverter()
        );

        Label percentCovered = new Label();
        percentCovered.textProperty().bindBidirectional(
                account.getCoveredPercentProperty(),
                new NumberStringConverter()
        );

        HBox hbox = new HBox();
        hbox.getChildren().addAll(covered, percentCovered);
        hbox.setSpacing(10);
        gp.add(hbox, 1, desc.length);
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
