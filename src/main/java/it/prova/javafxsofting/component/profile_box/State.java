package it.prova.javafxsofting.component.profile_box;

import javafx.scene.control.ContextMenu;

@FunctionalInterface
public interface State {
  /**
   * Crea il contextMenu
   *
   * @return il contextMenu
   */
  ContextMenu createContextMenu();
}
