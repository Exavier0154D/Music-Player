package com.mycompany.proyectousicalxbdl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class PrimaryController {

    @FXML private Button playButton, pauseButton, siguienteButton, anteriorButton, repetirButton;
    @FXML private Slider volumenSlider, velocidadSlider, tiempoSlider;
    @FXML private Label labelCancion, tiempoActualLabel, tiempoTotalLabel, velocidadValorLabel, volumenValorLabel;
    @FXML private ComboBox<String> comboBoxCanciones;
    @FXML private TextArea labelLetra;
    @FXML private MediaView mediaView;

    private final DoublyLinkedList<File> canciones = new DoublyLinkedList<>();
    private final DoublyLinkedList<LyricLine> bloquesLetra = new DoublyLinkedList<>();
    private int indiceCancion = 0;
    private MediaPlayer mediaPlayer;
    private MediaPlayer videoPlayer;
    private boolean repetir = false;
    private boolean arrastrando = false;

    @FXML
    public void initialize() {
        try {
            URL resourceUrl = getClass().getResource("/musica");
            if (resourceUrl != null) {
                File carpeta = new File(resourceUrl.toURI());
                File[] archivos = carpeta.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));
                if (archivos != null) {
                    for (File archivo : archivos) {
                        canciones.addLast(archivo);
                        comboBoxCanciones.getItems().add(archivo.getName());
                    }
                    comboBoxCanciones.getSelectionModel().selectFirst();
                }
            } else {
                labelCancion.setText("No se encontró la carpeta de música.");
            }
        } catch (URISyntaxException e) {
            labelCancion.setText("Error al acceder a la carpeta de música.");
        }

        if (!canciones.isEmpty()) {
            cargarCancion(indiceCancion);
        } else {
            labelCancion.setText("No hay canciones disponibles.");
        }

        volumenSlider.valueProperty().addListener((o, oldV, newV) -> {
            if (mediaPlayer != null) mediaPlayer.setVolume(newV.doubleValue() / 100.0);
            volumenValorLabel.setText(Math.round(newV.doubleValue()) + "%");
        });

        velocidadSlider.valueProperty().addListener((o, oldV, newV) -> {
            double nuevaVelocidad = newV.doubleValue() / 100.0;
            if (mediaPlayer != null) mediaPlayer.setRate(nuevaVelocidad);
            if (videoPlayer != null) videoPlayer.setRate(nuevaVelocidad);
            velocidadValorLabel.setText(Math.round(newV.doubleValue()) + "%");
        });

        tiempoSlider.setOnMousePressed(e -> arrastrando = true);
        tiempoSlider.setOnMouseReleased(e -> {
            if (mediaPlayer != null) {
                Duration nueva = Duration.seconds(tiempoSlider.getValue());
                mediaPlayer.seek(nueva);
                if (videoPlayer != null) videoPlayer.seek(nueva);
            }
            arrastrando = false;
        });

        comboBoxCanciones.setOnAction(e -> {
            indiceCancion = comboBoxCanciones.getSelectionModel().getSelectedIndex();
            cargarCancion(indiceCancion);
            mediaPlayer.play();
        });
    }

    private void cargarCancion(int idx) {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.getStatus() != MediaPlayer.Status.UNKNOWN && mediaPlayer.getStatus() != MediaPlayer.Status.DISPOSED) {
                    mediaPlayer.stop();
                }
            } catch (Exception ignored) {}
            mediaPlayer.dispose();
        }

        if (videoPlayer != null) {
            try {
                if (videoPlayer.getStatus() != MediaPlayer.Status.UNKNOWN && videoPlayer.getStatus() != MediaPlayer.Status.DISPOSED) {
                    videoPlayer.stop();
                }
            } catch (Exception ignored) {}
            videoPlayer.dispose();
            mediaView.setMediaPlayer(null);
        }

        File cancion = canciones.get(idx);
        Media media = new Media(cancion.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        labelCancion.setText(cancion.getName());

        cargarBloquesLetra(cancion);
        cargarVideoAsociado(cancion);

        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (!arrastrando) {
                tiempoSlider.setValue(newTime.toSeconds());
                mostrarBloqueLetra(newTime);
                tiempoActualLabel.setText(formatoTiempo(newTime));
            }
        });

        mediaPlayer.setOnReady(() -> {
            tiempoSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
            tiempoTotalLabel.setText(formatoTiempo(mediaPlayer.getTotalDuration()));
            mediaPlayer.setVolume(volumenSlider.getValue() / 100.0);
            double rate = velocidadSlider.getValue() / 100.0;
            mediaPlayer.setRate(rate);
            if (videoPlayer != null) videoPlayer.setRate(rate);
            mediaPlayer.play();
        });

        mediaPlayer.setOnEndOfMedia(() -> {
            if (repetir) {
                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.play();
                if (videoPlayer != null) videoPlayer.seek(Duration.ZERO);
            } else {
                siguienteCancion();
            }
        });
    }

    private void cargarVideoAsociado(File cancion) {
        String nombreBase = cancion.getName().replaceFirst("[.][^.]+$", "");
        File archivoVideo = new File(cancion.getParent(), nombreBase + ".mp4");
        if (archivoVideo.exists()) {
            Media mediaVideo = new Media(archivoVideo.toURI().toString());
            videoPlayer = new MediaPlayer(mediaVideo);
            mediaView.setMediaPlayer(videoPlayer);
            videoPlayer.setMute(true);
            videoPlayer.play();
        }
    }

    private void cargarBloquesLetra(File cancion) {
        bloquesLetra.clear();
        labelLetra.setText("Cargando letra...");
        String nombreBase = cancion.getName().replaceFirst("[.][^.]+$", "");
        File archivoLetra = new File(cancion.getParent(), nombreBase + ".lrc");

        if (!archivoLetra.exists()) {
            labelLetra.setText("Archivo .lrc no encontrado.");
            return;
        }

        try {
            List<String> lineas = java.nio.file.Files.readAllLines(archivoLetra.toPath());
            for (int i = 0; i < lineas.size(); i++) {
                String linea = lineas.get(i).trim();
                if (linea.startsWith("[") && linea.contains("]")) {
                    String tiempoStr = linea.substring(1, linea.indexOf(']'));
                    String texto = linea.substring(linea.indexOf(']') + 1).trim();

                    i++;
                    while (i < lineas.size()) {
                        String siguiente = lineas.get(i).trim();
                        if (siguiente.startsWith("[") && siguiente.contains("]")) {
                            i--;
                            break;
                        }
                        texto += "\n" + siguiente;
                        i++;
                    }

                    String[] partes = tiempoStr.split("[:.]");
                    int minutos = Integer.parseInt(partes[0]);
                    int segundos = Integer.parseInt(partes[1]);
                    int milisegundos = Integer.parseInt(partes[2]);
                    if (partes[2].length() == 1) milisegundos *= 100;
                    else if (partes[2].length() == 2) milisegundos *= 10;

                    Duration tiempo = Duration.seconds(minutos * 60 + segundos).add(Duration.millis(milisegundos));
                    bloquesLetra.addLast(new LyricLine(tiempo, texto));
                }
            }

            if (bloquesLetra.isEmpty()) {
                labelLetra.setText("No se encontraron bloques válidos en el .lrc");
            } else {
                labelLetra.setText("Letra cargada correctamente.");
            }

        } catch (IOException | NumberFormatException e) {
            labelLetra.setText("Error al leer el archivo de letra.");
        }
    }

    private void mostrarBloqueLetra(Duration actual) {
        for (int i = bloquesLetra.size() - 1; i >= 0; i--) {
            LyricLine linea = bloquesLetra.get(i);
            if (actual.greaterThanOrEqualTo(linea.getTiempo())) {
                labelLetra.setText(linea.getTexto());
                return;
            }
        }
        labelLetra.setText("");
    }

    private String formatoTiempo(Duration duracion) {
        int totalSegundos = (int) duracion.toSeconds();
        int minutos = totalSegundos / 60;
        int segundos = totalSegundos % 60;
        return String.format("%02d:%02d", minutos, segundos);
    }

    @FXML private void playCancion() {
        if (mediaPlayer != null) mediaPlayer.play();
        if (videoPlayer != null) videoPlayer.play();
    }

    @FXML private void pauseCancion() {
        if (mediaPlayer != null) mediaPlayer.pause();
        if (videoPlayer != null) videoPlayer.pause();
    }

    @FXML private void siguienteCancion() {
        if (!canciones.isEmpty()) {
            indiceCancion = (indiceCancion + 1) % canciones.size();
            comboBoxCanciones.getSelectionModel().select(indiceCancion);
            cargarCancion(indiceCancion);
            mediaPlayer.play();
        }
    }

    @FXML private void anteriorCancion() {
        if (!canciones.isEmpty()) {
            indiceCancion = (indiceCancion - 1 + canciones.size()) % canciones.size();
            comboBoxCanciones.getSelectionModel().select(indiceCancion);
            cargarCancion(indiceCancion);
            mediaPlayer.play();
        }
    }

    @FXML private void toggleRepetir() {
        repetir = !repetir;
        repetirButton.setText(repetir ? "Repetir ON" : "Repetir OFF");
    }

    @FXML private void cargarContenido() {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Carga de contenido");
        alerta.setHeaderText("Solo se permiten archivos MP3, MP4 o LRC.");
        alerta.setContentText("Asegúrate de seleccionar archivos con extensión .mp3, .mp4 o .lrc.\n¿Deseas continuar?");

        ButtonType okButton = new ButtonType("OK");
        ButtonType cancelarButton = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        alerta.getButtonTypes().setAll(okButton, cancelarButton);

        alerta.showAndWait().ifPresent(respuesta -> {
            if (respuesta == okButton) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Selecciona archivos MP3, MP4 o LRC");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos válidos", "*.mp3", "*.mp4", "*.lrc"));
                List<File> archivosSeleccionados = fileChooser.showOpenMultipleDialog(null);

                if (archivosSeleccionados != null) {
                    try {
                        File carpetaMusica = new File(getClass().getResource("/musica").toURI().getPath());

                        for (File archivo : archivosSeleccionados) {
                            String nombre = archivo.getName().toLowerCase();
                            if (nombre.endsWith(".mp3") || nombre.endsWith(".mp4") || nombre.endsWith(".lrc")) {
                                File destino = new File(carpetaMusica, archivo.getName());
                                java.nio.file.Files.copy(archivo.toPath(), destino.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                                if (nombre.endsWith(".mp3") && !comboBoxCanciones.getItems().contains(archivo.getName())) {
                                    canciones.addLast(destino);
                                    comboBoxCanciones.getItems().add(destino.getName());
                                }
                            }
                        }

                    } catch (IOException | URISyntaxException e) {
                        Alert error = new Alert(Alert.AlertType.ERROR, "Hubo un error al copiar los archivos.");
                        error.show();
                    }
                }
            }
        });
    }
}
