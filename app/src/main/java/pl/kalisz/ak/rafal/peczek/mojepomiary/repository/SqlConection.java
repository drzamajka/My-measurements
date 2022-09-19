package pl.kalisz.ak.rafal.peczek.mojepomiary.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlConection {

    private static final String url="jdbc:mysql://serwer1.mysql.database.azure.com:3306/projekt_inzynierski_moje_pomiary";

    private static String connectionUrl =
            "jdbc:sqlserver://serwer2.database.windows.net:1433;"
                    + "database=projekt_inzynierski_moje_pomiary;"
                    + "user=drzamajka@serwer2;"
                    + "password=1Jamajka;"
                    + "encrypt=true;"
                    + "ssl=true;"
                    + "trustServerCertificate=false;"
                    + "loginTimeout=30;";

    static {

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static Connection getConn() {
        Connection  conn = null;
        try {

            //GRANT ALL PRIVILEGES ON *.* TO 'root'@'komputer1' IDENTIFIED BY '' WITH GRANT OPTION;
            //lokalnie
            conn= DriverManager.getConnection("jdbc:mariadb://192.168.0.10:3306/Projekt_inzynierski_Moje_pomiary","root","");
            //zdalnie mysql
            //String mysqlurl="jdbc:mysql://serwer1.mysql.database.azure.com:3306/projekt_inzynierski_moje_pomiary?verifyServerCertificate=false&enabledTLSProtocols=TLSv1.2&useSSL=true";
            //Class dbDriver = Class.forName("com.mysql.jdbc.Driver");
            //conn = DriverManager.getConnection(mysqlurl, "drzamajka", "1Jamajka");
            //zdalny sql serwer
            //Connection connection = DriverManager.getConnection("jdbc:sqlserver://serwer2.database.windows.net:1433;database=projekt_inzynierski_moje_pomiary;user=drzamajka@serwer2;password=1Jamajka;sslProtocol=TLS;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;");

        }catch (Exception exception){
            exception.printStackTrace();
        }
        return conn;
    }

    public static void close(Connection conn){
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}


//    CREATE TABLE Users (
//        id int AUTO_INCREMENT,
//        login varchar(255) NOT NULL,
//    imie varchar(255) NOT NULL,
//    nazwisko varchar(255),
//    haslo varchar(255) NOT NULL,
//    dataUrodzenia date,
//    eMail varchar(255) NOT NULL,
//    dataUtwozenia datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
//    dataAktualizacji datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
//    PRIMARY KEY (id),
//    UNIQUE (login, eMail)
//);
//
//INSERT INTO `users`( `login`, `imie`, `nazwisko`, `haslo`, `dataUrodzenia`, `eMail`) VALUES ('test','test',null,'test',null,'test@test.test')