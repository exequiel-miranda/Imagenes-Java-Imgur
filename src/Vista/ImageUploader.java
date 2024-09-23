package Vista;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.ParseException;
import org.json.JSONObject;

public class ImageUploader extends JFrame {
    private JButton btnSubirImagen;

    public ImageUploader() {
        // Configurar el botón y la ventana
        btnSubirImagen = new JButton("Subir Imagen");
        btnSubirImagen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Selecciona una imagen");
                
                // Solo permitir seleccionar archivos
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int result = fileChooser.showOpenDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();

                    try {
                        // Subir la imagen a Imgur
                        String urlSubida = subirImagenImgur(selectedFile);
                        
                        // Mostrar URL en un JOptionPane
                        JOptionPane.showMessageDialog(null, "Imagen subida a: " + urlSubida);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error subiendo la imagen: " + ex.getMessage());
                    }
                }
            }
        });

        // Configuración de la ventana
        this.add(btnSubirImagen);
        this.setSize(300, 200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    // Método para subir la imagen a Imgur
    private String subirImagenImgur(File imageFile) throws IOException, ParseException {
        // Cargar la imagen y convertirla en Base64
        byte[] fileContent = Files.readAllBytes(imageFile.toPath());
        String encodedImage = Base64.getEncoder().encodeToString(fileContent);

        // URL de la API de Imgur
        String uploadUrl = "https://api.imgur.com/3/image";

        // Crear un cliente HTTP
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost uploadFile = new HttpPost(uploadUrl);

        // Configurar las cabeceras para autenticar la API de Imgur
        uploadFile.addHeader("Authorization", "5fd25fb7d65cbf8");

        // Crear el JSON para el body de la petición
        JSONObject json = new JSONObject();
        json.put("image", encodedImage);

        // Establecer el JSON como entidad de la petición
        StringEntity entity = new StringEntity(json.toString());
        uploadFile.setEntity(entity);
        uploadFile.addHeader("Content-Type", "application/json");

        // Ejecutar la solicitud de subida
        CloseableHttpResponse response = httpClient.execute(uploadFile);
        String jsonResponse = EntityUtils.toString(response.getEntity());
        
        // Analizar la respuesta JSON para obtener la URL de la imagen
        JSONObject responseObject = new JSONObject(jsonResponse);
        String uploadedUrl = responseObject.getJSONObject("data").getString("link");
        
        response.close();
        return uploadedUrl;
    }

    public static void main(String[] args) {
        new ImageUploader();
    }
}


