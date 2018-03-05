package st.ggviario.house.controller.page;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public interface PageTab extends Page {

    default void acceptTab( Tab tab ) {}

    default void acceptTabPane( TabPane tabPane ) {}
}