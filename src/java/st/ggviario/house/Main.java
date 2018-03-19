package st.ggviario.house;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import st.ggviario.house.control.HomeController;
import st.ggviario.house.singleton.AuthSingleton;

public class Main extends Application {

    public static void main(String[] args) {
        Thread.currentThread().setPriority( Thread.MAX_PRIORITY );
        launch( args );
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        //From preview layout
        AuthSingleton.login( null, null );

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation( getClass().getResource("/fxml/page/page_home.fxml") );
        Parent parent  = loader.load();
        HomeController homeController = loader.getController();
        Scene scene = new Scene( parent );
        primaryStage.setScene( scene );
        homeController.accept( primaryStage, scene );

        primaryStage.show();

    }
}
