package service;

import entites.Transport;

import entites.Vehicule;
import utils.DataSource;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

import java.util.List;
public class TransportService implements IService<Transport> {

    private Connection conn;
    private Statement ste;
    private PreparedStatement pst;
    private ResultSet rs;

    public TransportService() {
        conn= DataSource.getInstance().getCnx();
    }


    /*public void add(transport t){
        String requete="insert into transport (num_t,num_ch,dd,da,num_v,cout) values ('"+t.getNum_t()+"','"+t.getNum_ch()+"','"+t.getDd()+"','"+t.getDa()+"','"+t.getNum_v()+"','"+t.getCout()+"')";

        try {
            ste=conn.createStatement();
            ste.executeUpdate(requete);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }*/

    @Override
    public boolean add(Transport t) {
        // Vérification de la saisie sur num_v
        if (t.getNum_v() <= 0) {
            System.out.println("Le numéro de véhicule doit être un nombre positif.");
            return false ; // Sortie de la méthode si la saisie est invalide
        }
        if (t.getCout() <= 0) {
            System.out.println("Le cout doit être un nombre positif.");
            return false ;// Sortie de la méthode si la saisie est invalide
        }
        if ( t.getDa().compareTo(t.getDd())<0 || t.getDd().compareTo(LocalDate.now())<0 ){
            System.out.println(" wrong dates ");
            return false  ;

        }

        String query = "INSERT INTO transport (num_ch, dd, da, num_v, cout) VALUES ( ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {

           // ps.setInt(1, t.getNum_t());
            ps.setInt(1, t.getNum_ch());
            ps.setDate(2, Date.valueOf(t.getDd()));
            ps.setDate(3, Date.valueOf(t.getDa()));
            ps.setInt(4, t.getNum_v());
            ps.setInt(5, t.getCout());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("Failed to add transport.");
            } else {
                System.out.println("Transport added successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception properly in your application
            throw new RuntimeException(e); // If you prefer to throw a RuntimeException
        }
    return true ;
    }



    @Override
    public void delete(Transport t) {
        String query = "DELETE FROM transport WHERE num_ch=?";
        try {
            pst = conn.prepareStatement(query);
            pst.setInt(1, t.getNum_ch());
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du transport", e);
        } finally {
            closeResources();
        }
    }



    @Override
    public void update(Transport transport) {
        String query = "UPDATE transport SET num_ch = ?, dd = ?, da = ?, num_v = ?, cout = ? WHERE num_ch= ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, transport.getNum_ch());
            ps.setDate(2, Date.valueOf(transport.getDd()));
            ps.setDate(3, Date.valueOf(transport.getDa()));
            ps.setInt(4, transport.getNum_v());
            ps.setInt(5, transport.getCout());
            ps.setInt(6, transport.getNum_ch()); // Assuming num_t is the primary key
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("No records updated for transport with num_ch: " + transport.getNum_ch());
            } else {
                System.out.println("Updated transport with num_ch: " + transport.getNum_ch());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update transport: " + e.getMessage());
        }
    }



    @Override
    public List<Transport> readAll() {
        String query = "SELECT * FROM transport";
        List<Transport> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Transport transport = new Transport(
                        rs.getInt("num_ch"),
                        rs.getDate("dd").toLocalDate(),
                        rs.getDate("da").toLocalDate(),
                        rs.getInt("num_v"),
                        rs.getInt("cout")
                );
                // Assuming num_t is the primary key
                transport.setNum_t(rs.getInt("num_t"));
                list.add(transport);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception properly in your application
            throw new RuntimeException(e); // If you prefer to throw a RuntimeException
        }
        return list;
    }


    @Override
    public Transport readBynum_ch(int num_ch) {
        String query = "SELECT * FROM transport WHERE num_ch=?";
        try {
            pst = conn.prepareStatement(query);
            pst.setInt(1, num_ch);
            rs = pst.executeQuery();
            if (rs.next()) {
                Transport t= new Transport();
                t.setNum_t(rs.getInt("num_t"));
                t.setNum_ch(rs.getInt("num_ch"));
                t.setDd(rs.getDate("Dd").toLocalDate());
                t.setDa(rs.getDate("da").toLocalDate());
                t.setNum_v(rs.getInt("num_v"));
                t.setCout(rs.getInt("cout"));

                return t;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture du transport par num_ch", e);
        } finally {
            closeResources();
        }
        return null;
    }

    /*@Override
    public void  selectvandtranspoteur  (int num_ch) {
        String query = "SELECT * FROM vehicule JOIN transport ON vehicule.num_v = transport.num_v";
        try {
            pst = conn.prepareStatement(query);

            rs = pst.executeQuery();
            if (rs.next()) {
                Vehicule v= new Vehicule();
                v .setNum_v(rs.getInt("num_t"));
                v.setNum_ch(rs.getInt("num_ch"));
v.setType(rs.getString("type"));
                v.setPrixuni(rs.getInt("prixuni"));


                return v;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture du transport par num_ch", e);
        } finally {
            closeResources();
        }
        return null;
    }*/
    private void closeResources() {
        try {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}