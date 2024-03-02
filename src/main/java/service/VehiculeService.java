package service;

import entites.Vehicule;
import utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class VehiculeService implements VeService<Vehicule> {
    private int type;

    private Connection conn;
    private Statement ste;
    private PreparedStatement pst;
    private DataSource MyConnection;
    private ResultSet rs;

    public VehiculeService() {
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


    public boolean add(Vehicule v) {
        if (v.getCapacite() > 15) {
            System.out.println("La capacité du véhicule doit être inferieur à 15.");
            return false; // Sortir de la méthode si la capacité n'est pas égale à 11
        }
        String query = "INSERT INTO vehicule (num_v,type, capacite, prixuni, num_ch) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement psv = conn.prepareStatement(query)) {

            psv.setInt(1, v.getNum_v());
            psv.setString(2, v.getType());


            psv.setInt(3, v.getCapacite());
            psv.setInt(4, v.getPrixuni());
            psv.setInt(5, v.getNum_ch());

            int rowsAffected = psv.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("Failed to add vehicule.");
            } else {
                System.out.println("vehicule added successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception properly in your application
            throw new RuntimeException(e); // If you prefer to throw a RuntimeException
        }
        return true ;
    }



    @Override
    public void delete(Vehicule vehicule) {
        String query = "DELETE FROM vehicule WHERE num_v = ?";
        try (PreparedStatement psv = conn.prepareStatement(query)) {
            psv.setInt(1, vehicule.getNum_v());
            int rowsAffected = psv.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("No records deleted for vehicule with num_v: " + vehicule.getNum_v());
            } else {
                System.out.println("Deleted vehicule with num_v: " + vehicule.getNum_v());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    @Override
    public void update(Vehicule vehicule) {
        if (vehicule.getCapacite() != 11) {
            System.out.println("La capacité du véhicule doit être égale à 11.");
            return; // Sortir de la méthode si la capacité n'est pas égale à 11
        }
        String query = "UPDATE vehicule SET num_ch= ?, type = ?, capacite = ?, prixuni = ? WHERE num_v = ?";
        try (PreparedStatement psv = conn.prepareStatement(query)) {
            psv.setInt(1, vehicule.getNum_ch());
            psv.setString(2, vehicule.getType());
            psv.setInt(3, vehicule.getCapacite());
            psv.setInt(4, vehicule.getPrixuni());

            psv.setInt(5, vehicule.getNum_v()); // Assuming num_v is the primary key
            int rowsAffected = psv.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("No records updated for vehiculewith num_v: " + vehicule.getNum_v());
            } else {
                System.out.println("Updated vehicule with num_v: " + vehicule.getNum_v());
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception properly in your application
            throw new RuntimeException(e); // If you prefer to throw a RuntimeException
        }
    }



    @Override
    public List<Vehicule> readAll() {
        String query = "SELECT * FROM vehicule";
        List<Vehicule> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int num_v = rs.getInt("num_v");
                String type = rs.getString("type");
                int capacite= rs.getInt("capacite"); // Assuming "num_v" is the correct column name
                int prixuni = rs.getInt("prixuni"); // Assuming "cout" is the correct column name
                // Assuming you have columns named "dd" and "da", replace them with the correct column names
                int num_ch = rs.getInt("num_ch");

                Vehicule vehicule = new Vehicule(num_v,type, capacite, prixuni, num_ch);
                list.add(vehicule);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception properly in your application
            throw new RuntimeException(e); // If you prefer to throw a RuntimeException
        }
        return list;
    }

    @Override
    public Vehicule readBynum_v(int num_v) {
        String query = "SELECT * FROM vehicule WHERE num_v=?";
        try {
            pst = conn.prepareStatement(query);

            pst.setInt(1, num_v);
            rs = pst.executeQuery();
            if (rs.next()) {
                Vehicule v= new Vehicule(
                        rs.getInt("num_v"),
                        rs.getString("type"),
                        rs.getInt("capacite"),
                        rs.getInt("prixuni"),
                        rs.getInt("num_ch")
                );
                return v;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture du transport par num_ch", e);
        } finally {
            closeResources();
        }
        return null;
    }
    private void closeResources() {
        try {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    // Your existing class code here...

        public float getsommevehicule() throws SQLException {
            Connection conn = MyConnection.getInstance().getConn();
            float prixuniTotal = 0.0F; // Use a float variable to accumulate the total price

            PreparedStatement psv = conn.prepareStatement("SELECT prixuni FROM vehicule WHERE type = ?");
            pst.setInt(1, this.type); // Use type, not num_v, as the filter

            ResultSet rs = psv.executeQuery();

            while(rs.next()) {
                prixuniTotal += rs.getFloat("prixuni"); // Retrieve price as float and add to total
                System.out.println(this.type); // Print the type of the vehicle
            }

            // Return the total price (prixuniTotal) instead of prix
            return prixuniTotal;
        }
    }