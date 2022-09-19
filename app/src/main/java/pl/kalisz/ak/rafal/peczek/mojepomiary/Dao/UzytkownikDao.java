package pl.kalisz.ak.rafal.peczek.mojepomiary.Dao;

import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Uzytkownik;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.SqlConection;

public class UzytkownikDao {



    public boolean login(String login,String haslo){

        String sql = "SELECT * FROM users where login = ? and haslo = ?";

        Connection con = SqlConection.getConn();

        try {
            PreparedStatement pst=con.prepareStatement(sql);

            pst.setString(1,login);
            pst.setString(2,haslo);

            ResultSet set = pst.executeQuery();

            if(set.next()){
                return true;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            SqlConection.close(con);
        }

        return false;
    }

    public boolean register(Uzytkownik uzytkownik){

        String sql = "insert into Users(login,imie,nazwisko,haslo,dataUrodzenia,eMail) values (?,?,?,?,?,?)";

        Connection  con = SqlConection.getConn();

        try {
            PreparedStatement pst=con.prepareStatement(sql);

            pst.setString(1, uzytkownik.getLogin());
            pst.setString(2, uzytkownik.getImie());
            pst.setString(3, uzytkownik.getNazwisko());
            pst.setString(4, uzytkownik.getHaslo());
            pst.setDate(5, new java.sql.Date(uzytkownik.getDataUrodzenia().getTime()));
            pst.setString(6, uzytkownik.getEMail());

            Log.i("Tag-UserDao", pst.toString());


            int value = pst.executeUpdate();

            if(value>0){
                return true;
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            SqlConection.close(con);
        }
        return false;
    }

    public Uzytkownik findUser(String name){
        Connection  con = SqlConection.getConn();
        Uzytkownik uzytkownik = null;

        if( con != null) {
            try {

                String sql = "select * from users where login = ?";
                PreparedStatement pst = con.prepareStatement(sql);

                pst.setString(1, name);

                Log.i("Tag-UserDao", pst.toString());

                final ResultSet rs = pst.executeQuery();

                while (rs.next()) {
                    int iddb = rs.getInt("id");
                    String logindb = rs.getString("login");
                    String imiedb = rs.getString("imie");
                    String nazwiskodb = rs.getString("nazwisko");
                    String haslodb = rs.getString("haslo");
                    Date dataUrodzeniadb = rs.getDate("dataUrodzenia");
                    String eMaildb = rs.getString("eMail");
                    Date dataUtwozeniadb = rs.getTimestamp("dataUtwozenia");
                    Date dataAktualizacjidb = rs.getTimestamp("dataAktualizacji");
                    uzytkownik = new Uzytkownik(iddb, logindb, imiedb, nazwiskodb, haslodb, dataUrodzeniadb, eMaildb, dataUtwozeniadb, dataAktualizacjidb);
                    Log.i("Tag-UserDao", uzytkownik.toString());
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } finally {
                SqlConection.close(con);
            }
        }
        return uzytkownik;
    }


}