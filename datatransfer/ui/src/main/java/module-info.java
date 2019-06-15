module wbh.bookworm.datatransfer.ui {

    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;

    exports wbh.bookworm.datatransfer.ui to javafx.graphics;
    opens wbh.bookworm.datatransfer.ui to javafx.fxml;

}
