package edu.mondragon.webengl.CasaJusta.repository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class ImagenStorageService {

    // Ruta base donde se guardarán las imágenes (fuera del proyecto o dentro de static)
    private final Path directorioBase = Paths.get("uploads/anuncios");

    public ImagenStorageService() {
        try {
            // Crear el directorio base si no existe
            Files.createDirectories(directorioBase);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el directorio de imágenes", e);
        }
    }

    /**
     * Guarda la imagen en: uploads/anuncios/{viviendaId}/nombre_unico.jpg
     * Devuelve la ruta relativa para guardar en BD
     */
    public String guardarImagen(MultipartFile archivo, Integer viviendaId) {
        try {
            // 1. Crear carpeta del anuncio: uploads/anuncios/42/
            Path carpetaAnuncio = directorioBase.resolve(String.valueOf(viviendaId));
            Files.createDirectories(carpetaAnuncio);

            // 2. Generar nombre único: 42_a1b2c3d4.jpg
            String extension = obtenerExtension(archivo.getOriginalFilename());
            String nombreUnico = viviendaId + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
            
            // 3. Guardar archivo
            Path rutaArchivo = carpetaAnuncio.resolve(nombreUnico);
            Files.copy(archivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

            // 4. Devolver ruta relativa para la BD: /uploads/anuncios/42/42_a1b2c3d4.jpg
            return "/uploads/anuncios/" + viviendaId + "/" + nombreUnico;

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la imagen: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina una imagen del disco
     */
    public void eliminarImagen(String rutaRelativa) {
        try {
            Path ruta = Paths.get(rutaRelativa.substring(1)); // quitar el / inicial
            Files.deleteIfExists(ruta);
        } catch (IOException e) {
            System.err.println("No se pudo eliminar la imagen: " + rutaRelativa);
        }
    }

    /**
     * Elimina toda la carpeta de un anuncio
     */
    public void eliminarCarpetaAnuncio(Integer viviendaId) {
        try {
            Path carpeta = directorioBase.resolve(String.valueOf(viviendaId));
            Files.walk(carpeta)
                 .sorted((a, b) -> -a.compareTo(b)) // archivos primero, luego carpetas
                 .forEach(path -> {
                     try { Files.delete(path); } catch (IOException e) {}
                 });
        } catch (IOException e) {
            System.err.println("No se pudo eliminar la carpeta del anuncio: " + viviendaId);
        }
    }

    private String obtenerExtension(String nombreOriginal) {
        if (nombreOriginal == null || !nombreOriginal.contains(".")) {
            return ".jpg";
        }
        return nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
    }
}
