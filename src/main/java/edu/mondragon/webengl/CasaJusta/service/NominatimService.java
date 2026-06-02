package edu.mondragon.webengl.CasaJusta.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NominatimService {

    private final RestTemplate restTemplate = new RestTemplate();

    public Coordenadas geocodificar(String direccion) {
        if (direccion == null || direccion.trim().isEmpty()) {
            return null;
        }

        String direccionCompleta = direccion.trim();
        if (!direccionCompleta.toLowerCase().contains("españa") 
            && !direccionCompleta.toLowerCase().contains("spain")) {
            direccionCompleta = direccionCompleta + ", España";
        }

        // Construir URL manualmente (sin UriComponentsBuilder)
        String url = "https://nominatim.openstreetmap.org/search?q=" 
            + direccionCompleta.replace(" ", "+") 
            + "&format=json&limit=1&countrycodes=es";

        System.out.println("NOMINATIM: URL = " + url);

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "CasaJustaApp/1.0 (casa.justamail@gmail.com)");
        headers.set("Accept", "application/json");
        headers.set("Accept-Language", "es-ES,es;q=0.9");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Petición simple GET
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, String.class
            );

            String body = response.getBody();
            System.out.println("NOMINATIM: Respuesta = " + body);

            if (body == null || body.trim().isEmpty() || body.equals("[]")) {
                System.out.println("NOMINATIM: Respuesta vacía");
                return null;
            }

            // Parseo manual del JSON
            String lat = extractJsonValue(body, "\"lat\":\"");
            String lon = extractJsonValue(body, "\"lon\":\"");
            
            System.out.println("NOMINATIM: lat extraído = " + lat);
            System.out.println("NOMINATIM: lon extraído = " + lon);

            if (lat != null && lon != null) {
                return new Coordenadas(Double.parseDouble(lat), Double.parseDouble(lon));
            }

        } catch (Exception e) {
            System.out.println("NOMINATIM: ERROR = " + e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private String extractJsonValue(String json, String key) {
        int pos = json.indexOf(key);
        if (pos == -1) return null;
        pos += key.length();
        int end = json.indexOf("\"", pos);
        if (end == -1) return null;
        return json.substring(pos, end);
    }

    public static class Coordenadas {
        private final double latitud;
        private final double longitud;

        public Coordenadas(double latitud, double longitud) {
            this.latitud = latitud;
            this.longitud = longitud;
        }

        public double getLatitud() {
            return latitud;
        }

        public double getLongitud() {
            return longitud;
        }
    }
}