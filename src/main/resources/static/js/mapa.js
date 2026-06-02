document.addEventListener('DOMContentLoaded', function() {
    
    // Inicializar mapa centrado en España
    var map = L.map('mapa-propiedades').setView([40.4168, -3.7038], 6);
    
    // Capa de OpenStreetMap
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://openstreetmap.org/copyright">OpenStreetMap contributors</a>',
        maxZoom: 19
    }).addTo(map);
    
    // Viviendas: variable global inyectada por Thymeleaf
    // Se declara en el HTML como: var viviendas = /*[[${viviendas}]]*/ [];
    
    if (typeof viviendas === 'undefined' || !Array.isArray(viviendas)) {
        console.error('No se encontraron viviendas para el mapa');
        return;
    }
    
    // Añadir marcadores
    var marcadores = [];
    
    viviendas.forEach(function(v) {
        if (v.latitud && v.longitud) {
            var marker = L.marker([v.latitud, v.longitud]).addTo(map);
            
            var popupContent = 
                '<div class="popup-content">' +
                    '<h3>' + escapeHtml(v.titulo) + '</h3>' +
                    '<p>' + escapeHtml(v.direccion) + '</p>' +
                    '<span class="precio">' + formatearPrecio(v.precio) + ' €</span><br>' +
                    '<a href="/anuncio/' + v.viviendaID + '">Ver detalles →</a>' +
                '</div>';
            
            marker.bindPopup(popupContent);
            marcadores.push(marker);
        }
    });
    
    // Ajustar zoom para ver todos los marcadores
    if (marcadores.length > 0) {
        var group = new L.featureGroup(marcadores);
        map.fitBounds(group.getBounds().pad(0.1));
    } else {
        console.warn('No hay viviendas con coordenadas para mostrar');
    }
    
    // ========== FUNCIONES AUXILIARES ==========
    
    function escapeHtml(text) {
        if (!text) return '';
        var div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
    
    function formatearPrecio(precio) {
        if (!precio) return '0';
        return parseFloat(precio).toLocaleString('es-ES', {
            minimumFractionDigits: 0,
            maximumFractionDigits: 0
        });
    }
    
});