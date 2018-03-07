package st.ggviario.house.controller.modals;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import st.ggviario.house.controller.ControllerLoader;
import st.ggviario.house.controller.SnackbarBuilder;
import st.ggviario.house.model.*;
import st.ggviario.house.singleton.AuthSingleton;
import st.ggviario.house.singleton.PostgresSQLSingleton;
import st.jigahd.support.sql.SQLRow;
import st.jigahd.support.sql.lib.SQLText;
import st.jigahd.support.sql.postgresql.PostgresSQL;
import st.jigahd.support.sql.postgresql.PostgresSQLResultSet;

import java.util.LinkedList;
import java.util.Map;

public class ModalNovoPreco extends AbstractModal< Preco >{

    public static ModalNovoPreco newInstance(StackPane stackPane ){
        ControllerLoader< AnchorPane, ModalNovoPreco>loader = new ControllerLoader<>("/fxml/modal/modal_novo_preco.fxml");
        loader.getController().createDialogModal( stackPane );
        loader.getController().structure();
        loader.getController().defineEvents( );
        return loader.getController();
    }


    @FXML private AnchorPane root;
    @FXML private AnchorPane anchorHeader;
    @FXML private AnchorPane iconAreaCloseModal;
    @FXML private Label modalTitle;
    @FXML private JFXTextField textFieldPrecoCusto;
    @FXML private JFXComboBox< UnidadeProduto > comboxUnidades;
    @FXML private JFXTextField textFieldPrecoQuantidade;
    @FXML private JFXButton buttonRegistar;
    @FXML private JFXToggleButton toggleButtonPrecoBase;



    private Produto produto;

    @Override
    Region getContentRoot() {
        return this.root;
    }

    @Override
    Label getModalTitleView() {
        return this.modalTitle;
    }

    @Override
    AnchorPane getIconAreaCloseModal() {
        return this.iconAreaCloseModal;
    }

    @Override
    AnchorPane getAnchorHeader() {
        return this.anchorHeader;
    }

    @Override
    @Deprecated
    public void openModal() {
    }

    @Override
    void structure() {
        super.structure();
        this.comboxUnidades.setItems( FXCollections.observableList( new LinkedList<>()));
    }

    @Override
    void defineEvents() {
        super.defineEvents();
        this.buttonRegistar.setOnAction(event -> onRegisterUnidade() );
        this.comboxUnidades.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            onChangeUnidade( newValue );
        });
    }

    private void onChangeUnidade( UnidadeProduto unidadeProduto ){
        if( unidadeProduto == null || unidadeProduto.unidade == null || unidadeProduto.unidade.getUnidadeId() == null ){
            this.comboxUnidades.getSelectionModel().clearSelection();
            this.textFieldPrecoCusto.setText( null );
            this.textFieldPrecoQuantidade.setText( null );
            this.toggleButtonPrecoBase.setSelected( false );
        } else {
            this.textFieldPrecoCusto.setText( unidadeProduto.precoCustoUnidade == null? null : String.valueOf(  unidadeProduto.precoCustoUnidade) );
            this.textFieldPrecoQuantidade.setText( unidadeProduto.precoQuantidade == null ? null : String.valueOf(  unidadeProduto.precoQuantidade ) );
            this.toggleButtonPrecoBase.setSelected( unidadeProduto.base );
        }
    }

    public void openModal(Produto produto ){
        super.openModal();
        this.produto = produto;
        this.loadDataUnidade( produto );
        this.setTitle( "Unidade de "+this.produto.getProdutoNome() );
    }

    private void loadDataUnidade( Produto produto ){
        final Thread thread =  new Thread(() -> {
            PostgresSQL sql = PostgresSQLSingleton.getInstance();
            Platform.runLater( ( )-> this.comboxUnidades.getItems().clear() );

            Unidade.UnidadeBuilder unidadeBuilder = new Unidade.UnidadeBuilder();
            sql.query( "ggviario.funct_load_unidade_simple_by_produto" )
                    .with( produto.getProdutoId() )
                    .callFunctionTable()
                    .onResultQuery((PostgresSQLResultSet.OnReadAllResultQuery) row -> Platform.runLater(() ->{
                        unidadeBuilder.load( row );
                        this.comboxUnidades.getItems().add( new UnidadeProduto( row, unidadeBuilder.build() ) );
                    }));


            Platform.runLater( () -> {
                if( this.comboxUnidades.getItems().size() > 1 )
                    this.comboxUnidades.getItems().add( 0, new UnidadeProduto( ) );
                else if( this.comboxUnidades.getItems().size() == 1 )
                    this.comboxUnidades.getSelectionModel().select( 0 );
            });
        });
        thread.start();
    }

    private void onRegisterUnidade(){
        ModalNovoPrecoResult res = this.checkForm();
        if( res.isSuccess() ){
            PostgresSQL sql = PostgresSQLSingleton.getInstance();
            Colaborador colaborador = AuthSingleton.getInstance();
            sql.query( "funct_reg_preco" )
                .withUUID( colaborador.getColaboradorId() )
                .withUUID( res.value.getProduto().getProdutoId() )
                .withUUID( res.value.getUnidade().getUnidadeId() )
                .withNumeric( res.value.getPrecoCustoUnidade() )
                .withNumeric( res.value.getPrecoQuantidadeProduto() )
                .withBoolean( res.value.isPrecoBase() )
                .callFunctionTable()
                    .onResultQuery((PostgresSQLResultSet.OnReadAllResultQuery) row -> {
                        SQLResult result = new SQLResult( row );
                        if( result.isSuccess() ){
                            res.level = SnackbarBuilder.MessageLevel.SUCCESS;
                            res.message = "O preço de "+res.value.getUnidade()+" para "+this.produto+" foi cadatrado com sucesso!";
                            res.data = result.getData();
                            res.value = new Preco.PrecoBuilder()
                                    .load((Map<String, Object>) result.getData().get( "preco" ))
                                    .setUnidade( res.value.getUnidade() )
                                    .setProduto( res.value.getProduto() )
                                    .build();
                        } else {
                            res.success = false;
                            res.level = SnackbarBuilder.MessageLevel.ERROR;
                            res.message = result.getMessage();
                            res.data = result.getData();
                        }
                    })
            ;
        }

        SnackbarBuilder snackbarBuilder = new SnackbarBuilder( this.getStakePane() );
        snackbarBuilder.show( res.message, res.level );
        if( res.isSuccess() ){
            this.closeModal();
            this.clear();
            this.executeOnOperationResult( res );
        }
    }

    private ModalNovoPrecoResult checkForm( ){
        ModalNovoPrecoResult result = new ModalNovoPrecoResult();
        result.level = SnackbarBuilder.MessageLevel.WARNING;
        Preco.PrecoBuilder precoBuilder = new Preco.PrecoBuilder();
        precoBuilder.setUnidade(  this.comboxUnidades.getValue() == null ? null : this.comboxUnidades.getValue().unidade );
        precoBuilder.setProduto( this.produto );
        precoBuilder.setCustoUnidade( SQLRow.doubleOf(SQLText.normalize( this.textFieldPrecoCusto.getText() ) ) );
        precoBuilder.setQuantidadeProduto( SQLRow.doubleOf( SQLText.normalize( this.textFieldPrecoQuantidade.getText() ) ) );
        precoBuilder.setBase( this.toggleButtonPrecoBase.isSelected() );
        result.value = precoBuilder.build();

        if( result.value.getUnidade() == null ){
            result.message = "Indique uma das unidades!";
        } else if( result.value.getPrecoCustoUnidade() == null && result.value.getPrecoCustoUnidade() == null ) {
            result.message = "Indique o custo unitario ou quantidade da unidade";
        } else {
            result.success= true;
        }
        return result;
    }

    private class UnidadeProduto{
        private Double precoCustoUnidade;
        private Double precoQuantidade;
        private Boolean base;
        private Unidade unidade;

        public UnidadeProduto() {}

        public UnidadeProduto(SQLRow row , Unidade unidade ) {
            this.precoCustoUnidade = row.asDouble( "preco_custounidade" );
            this.precoQuantidade = row.asDouble( "preco_quantidadeproduto" );
            this.base = row.asBoolean( "preco_base" );
            if (this.base == null) this.base = false;
            this.unidade = unidade;
        }

        @Override
        public String toString() {
            return this.unidade==null? null : this.unidade.toString();
        }
    }


    private class ModalNovoPrecoResult implements ModalResult < Preco > {
        private boolean success;
        private String message;
        private boolean terminated;
        private Preco value;
        private SnackbarBuilder.MessageLevel level;
        private  Map< String, Object > data;

        @Override
        public boolean isSuccess() {
            return success;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public boolean isTerminated() {
            return terminated;
        }

        @Override
        public Preco getValue() {
            return value;
        }

        @Override
        public SnackbarBuilder.MessageLevel getLevel() {
            return level;
        }

        @Override
        public Map<String, Object> getData() {
            return this.data;
        }
    }

}
