module org.jair.tetris.tetris {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;
    requires java.sql;

    opens org.jair.tetris.tetris to javafx.fxml;
    exports org.jair.tetris.tetris;


}