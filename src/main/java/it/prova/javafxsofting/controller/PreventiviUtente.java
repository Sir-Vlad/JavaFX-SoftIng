/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.prova.javafxsofting.controller;

import io.github.palexdev.materialfx.controls.MFXPaginatedTableView;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.utils.others.observables.When;
import it.prova.javafxsofting.models.Preventivo;
import it.prova.javafxsofting.models.Utente;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class PreventiviUtente implements Initializable {

  @FXML private MFXTableView<Utente> table;

  @FXML private MFXPaginatedTableView<Preventivo> paginated;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    //    setupPaginated();
    paginated.autosizeColumnsOnInitialization();

    When.onChanged(paginated.currentPageProperty())
        .then((oldValue, newValue) -> paginated.autosizeColumns())
        .listen();
  }

  private void setupPaginated() {
    //    MFXTableColumn<Preventivo> nameColumn =
    //        new MFXTableColumn<>("ID", true, Comparator.comparing(Preventivo::getId));
    //    MFXTableColumn<Utente> surnameColumn =
    //        new MFXTableColumn<>("Modello", true,
    // Comparator.comparing(Preventivo::getConfigurazione::getModello));
    //    MFXTableColumn<Utente> emailColumn =
    //        new MFXTableColumn<>("Email", true, Comparator.comparing(Utente::getEmail));
    //
    //    nameColumn.setRowCellFactory(person -> new MFXTableRowCell<>(Utente::getNome));
    //    surnameColumn.setRowCellFactory(person -> new MFXTableRowCell<>(Utente::getCognome));
    //    emailColumn.setRowCellFactory(
    //        person ->
    //            new MFXTableRowCell<>(Utente::getEmail) {
    //              {
    //                setAlignment(Pos.CENTER_RIGHT);
    //              }
    //            });
    //    emailColumn.setAlignment(Pos.CENTER_RIGHT);
    //
    //    paginated.getTableColumns().addAll(nameColumn, surnameColumn, emailColumn);
    //    paginated
    //        .getFilters()
    //        .addAll(
    //            new StringFilter<>("Name", Utente::getNome),
    //            new StringFilter<>("IP", Utente::getCognome),
    //            new StringFilter<>("Owner", Utente::getEmail));
    //    paginated.setItems(FXCollections.observableArrayList(App.utente, App.utente));
  }
}
