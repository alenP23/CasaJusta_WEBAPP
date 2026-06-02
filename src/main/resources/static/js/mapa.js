document.addEventListener('DOMContentLoaded', function() {
    
    var map = L.map('mapa-propiedades').setView([40.4168, -3.7038], 6);
    
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://openstreetmap.org/copyright">OpenStreetMap contributors</a>',
        maxZoom: 19
    }).addTo(map);
    
    if (typeof viviendas === 'undefined' || !Array.isArray(viviendas)) {
        console.error('No se encontraron viviendas para el mapa');
        return;
    }
    
    var marcadores = [];
    
    viviendas.forEach(function(v) {
        if (v.latitud && v.longitud) {
            var marker = L.marker([v.latitud, v.longitud]).addTo(map);
            
            var tipoClass = v.tipoOperacion === 'alquiler' ? 'tipo-alquiler' : 'tipo-compra';
            
            var popupContent = 
                '<div class="popup-simple">' +
                    '<h3>' + escapeHtml(v.titulo) + '</h3>' +
                    '<p class="popup-dir"><i class="fas fa-map-marker-alt"></i> ' + escapeHtml(v.direccion) + '</p>' +
                    '<span class="popup-tipo ' + tipoClass + '">' + v.tipoOperacion + '</span>' +
                    '<p class="popup-precio">' + formatearPrecio(v.precio) + ' €</p>' +
                    '<a href="/anuncio/' + v.viviendaID + '" class="popup-btn">Ver detalles →</a>' +
                '</div>';
            
            marker.bindPopup(popupContent, {
                maxWidth: 280,
                className: 'popup-minimal'
            });
            
            marcadores.push(marker);
        }
    });
    
    if (marcadores.length > 0) {
        var group = new L.featureGroup(marcadores);
        map.fitBounds(group.getBounds().pad(0.1));
    }
    
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