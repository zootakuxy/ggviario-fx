package st.ggviario.house.controller.pages;

import com.jfoenix.controls.JFXTabPane;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;

import java.net.URL;
import java.util.ResourceBundle;

public class PageProducao extends PageTabsConttoler implements Initializable {

    @FXML private JFXTabPane tabPane;
    private Tab tabProduto = new Tab("PRODUTO" );
    private Tab tabProducao = new Tab("PRODUÇÃO" );
    private Tab tabUnidade = new Tab("UNIDADE" );
    private Tab tabCategoria = new Tab("CATEGORIA" );

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize( url, resourceBundle );
        this.init();
        this.structure();
        this.defineEvents();
    }

    private void init(){

    }


    private void structure(){
        this.addTab( tabProduto, "/fxml/tabs/tab_producao_produto.fxml");
        this.addTab( tabProducao, "/fxml/tabs/tab_producao_producao.fxml");
        this.addTab( tabUnidade, "/fxml/tabs/tab_producao_unidade.fxml");
        this.addTab( tabCategoria, "/fxml/tabs/tab_producao_categoria.fxml");
    }

    private void defineEvents(){
        this.tabPane.getSelectionModel().selectedItemProperty().addListener((observableValue, oldTab, newTab) -> {
        });
    }

    @Override
    JFXTabPane getTabPane() {
        return this.tabPane;
    }
}
