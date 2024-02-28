package controller;

import com.github.sarxos.webcam.Webcam;
import entities.utilisateur;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    @FXML
    public ImageView logout;
    @FXML
    public ImageView profileImageView; // Changé pour être une instance non statique
    @FXML
    public Button captureButton;
    @FXML
    public Button validerButton;
    @FXML
    public Button homeClient;
    @FXML
    private TextField userNameText;
    @FXML
    private TextField userEmailText;
    @FXML
    private TextField userRoleText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        utilisateur user = Session.getInstance().getUser();
        if (user != null) {
            // Log des données de l'utilisateur pour vérifier qu'elles sont correctement récupérées
            System.out.println("Utilisateur connecté : " + utilisateur.getNom() + " " + utilisateur.getPrenom());
            System.out.println("Email : " + utilisateur.getEmail());
            System.out.println("Role : " + utilisateur.getRole().toString());

            userNameText.setText(utilisateur.getNom() + " " + utilisateur.getPrenom());
            userEmailText.setText(utilisateur.getEmail());
            userRoleText.setText(utilisateur.getRole().toString());
        } else {
            System.out.println("Aucun utilisateur connecté.");
            // Gérer le cas où aucun utilisateur n'est connecté
        }

        captureButton.setOnAction(this::capturePhoto);
        Image capturedImage = ImageSingleton.getInstance().getCapturedImage();
        if (capturedImage != null) {
            profileImageView.setImage(capturedImage);
        }
        // Supposons que vous avez une méthode capturePhoto qui retourne une Image

    }

    @FXML
    private Image capturePhoto(ActionEvent actionEvent) {
        // Sélectionnez la première webcam disponible
        Webcam webcam = Webcam.getWebcams().get(0);
        if (webcam == null) {
            // Gérer le cas où aucune webcam n'est disponible
            System.err.println("Aucune webcam disponible.");
            return null;
        }
        webcam.open();

        Image fxImage = null;
        try {
            // Capturez une image
            BufferedImage image = webcam.getImage();

            // Convertissez l'image AWT en Image JavaFX
            fxImage = SwingFXUtils.toFXImage(image, null);

            // Définissez l'image sur le ImageView
            profileImageView.setImage(fxImage);
        } catch (Exception e) {
            // Gérer les exceptions liées à la capture d'image
            e.printStackTrace();
        } finally {
            // N'oubliez pas de fermer la webcam après utilisation
            webcam.close();
        }
        Image capturedImage = fxImage;
        ImageSingleton.getInstance().setCapturedImage(capturedImage);

        return fxImage;
    }

    public void EditProfile(ActionEvent actionEvent) {
        // Implémenter la logique pour modifier le profil

    }

    public void logoutAction(MouseEvent event) {

        try {
            Session.getInstance().destroy();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginUser.fxml"));
            Parent loginRoot = loader.load();
            Scene loginScene = new Scene(loginRoot);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(loginScene);
            window.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Implémenter la logique pour déconnecter l'utilisateur
    }


    public void validerPhoto(ActionEvent actionEvent) {
            // Supposons que vous avez une méthode captureImageFromWebcam qui retourne une Image
            Image capturedImage = profileImageView.getImage();
            ImageSingleton.getInstance().setCapturedImage(capturedImage);

            // Mettre à jour l'ImageView dans Profile.fxml
            profileImageView.setImage(capturedImage);

    }
    public void returnToHomeC(ActionEvent actionEvent) {
        // Implémenter la logique pour modifier le profil


    }

}
