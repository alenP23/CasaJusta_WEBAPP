// ===== WEBSOCKET STOMP PARA APUNTARSE/DESAPUNTARSE =====
console.log('=== apuntarse.js cargado ===');

let stompClient = null;
let viviendasSuscritas = new Set();

document.addEventListener('DOMContentLoaded', function() {
    console.log('=== DOM cargado ===');
    
    const grid = document.getElementById('propertiesGrid');
    if (!grid) {
        console.error('ERROR: No se encontró #propertiesGrid');
        return;
    }
    
    // Conectar al WebSocket
    conectarWebSocket();
    
    // Delegación de eventos para clicks en botones
    grid.addEventListener('click', function(e) {
        const btn = e.target.closest('.btn-apuntarse');
        if (!btn) return;
        
        // NO hacer nada si el botón es de login o está deshabilitado por cupo completo (candado)
        if (btn.classList.contains('completo') && !btn.classList.contains('apuntado')) {
            return; // Es un candado para no apuntados, no hacer nada
        }
        
        e.preventDefault();
        e.stopPropagation();
        
        const viviendaId = btn.dataset.id;
        const cupo = parseInt(btn.dataset.cupo) || 0;
        const isApuntar = btn.classList.contains('btn-apuntar');
        const isDesapuntar = btn.classList.contains('btn-desapuntar');
        
        // Si no es ni apuntar ni desapuntar (estado extraño), no hacer nada
        if (!isApuntar && !isDesapuntar) {
            console.warn('Estado de botón no reconocido:', btn.className);
            return;
        }
        
        const url = isApuntar 
            ? `/api/anuncio/${viviendaId}/apuntarse`
            : `/api/anuncio/${viviendaId}/desapuntarse`;
        
        btn.style.opacity = '0.6';
        btn.style.pointerEvents = 'none';
        
        fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include'
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    throw new Error(text);
                });
            }
            return response.json();
        })
        .then(data => {
            if (data.success) {
                updateButtonState(btn, data, cupo);
                // El WebSocket se encargará de actualizar los demás navegadores
            } else {
                alert(data.message || 'Error al procesar');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert(error.message || 'Error de conexión');
        })
        .finally(() => {
            btn.style.opacity = '1';
            btn.style.pointerEvents = '';
        });
    });
});

// ===== CONEXIÓN WEBSOCKET =====
function conectarWebSocket() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    
    // Desactivar logs de debug de STOMP (muy verboso)
    stompClient.debug = null;
    
    stompClient.connect({}, function(frame) {
        console.log('WebSocket conectado:', frame);
        
        // Suscribirse a todas las viviendas visibles en la página
        suscribirViviendasVisibles();
        
        // Observar cambios en el DOM para suscribir nuevas viviendas (filtros, scroll infinito, etc.)
        const observer = new MutationObserver(function() {
            suscribirViviendasVisibles();
        });
        
        const grid = document.getElementById('propertiesGrid');
        if (grid) {
            observer.observe(grid, { childList: true, subtree: true });
        }
        
    }, function(error) {
        console.error('Error WebSocket:', error);
        // Reintentar en 5 segundos
        setTimeout(conectarWebSocket, 5000);
    });
}

function suscribirViviendasVisibles() {
    if (!stompClient || !stompClient.connected) return;
    
    document.querySelectorAll('.btn-apuntarse[data-id]').forEach(btn => {
        const viviendaId = btn.dataset.id;
        
        if (!viviendasSuscritas.has(viviendaId)) {
            viviendasSuscritas.add(viviendaId);
            
            // Suscribirse al topic de esta vivienda
            stompClient.subscribe('/topic/vivienda/' + viviendaId, function(message) {
                const data = JSON.parse(message.body);
                console.log('Update recibido para vivienda', viviendaId, ':', data);
                
                actualizarContadorVivienda(data.viviendaId, data.inscritos, data.completo);
            });
            
            console.log('Suscrito a vivienda:', viviendaId);
        }
    });
}

function actualizarContadorVivienda(viviendaId, inscritos, completo) {
    // Actualizar número en TODOS los botones de esta vivienda
    document.querySelectorAll(`.btn-apuntarse[data-id="${viviendaId}"] .apuntarse-numero`)
        .forEach(span => {
            span.textContent = inscritos;
        });
    
    // IMPORTANTE: Solo actualizar botones de usuarios NO apuntados
    // Los usuarios apuntados siempre mantienen su tick verde funcional
    
    if (completo) {
        // Cupo completo: usuarios NO apuntados → candado
        document.querySelectorAll(`.btn-apuntarse[data-id="${viviendaId}"]`).forEach(btn => {
            // SOLO si NO está apuntado (no tiene clase apuntado)
            if (!btn.classList.contains('apuntado')) {
                btn.classList.remove('btn-apuntar');
                btn.classList.add('completo');
                btn.disabled = true;
                btn.title = 'Cupo completo';
                const icon = btn.querySelector('i');
                if (icon) icon.className = 'fas fa-lock';
            }
        });
    } else {
        // Cupo disponible: reactivar botones candado → apuntar (solo no apuntados)
        document.querySelectorAll(`.btn-apuntarse.completo[data-id="${viviendaId}"]`).forEach(btn => {
            // Solo si NO está apuntado
            if (!btn.classList.contains('apuntado')) {
                btn.classList.remove('completo');
                btn.disabled = false;
                btn.classList.add('btn-apuntar');
                btn.title = 'Apuntarme';
                const icon = btn.querySelector('i');
                if (icon) icon.className = 'fas fa-user-plus';
            }
        });
    }
}

function updateButtonState(btn, data, cupo) {
    const numeroSpan = btn.querySelector('.apuntarse-numero');
    const icon = btn.querySelector('i');
    
    if (numeroSpan) {
        numeroSpan.textContent = data.inscritos;
    }
    
    if (data.apuntado) {
        // Usuario acaba de apuntarse → tick verde, SIEMPRE clickable
        // NUNCA añadir clase 'completo' a un botón apuntado
        btn.classList.remove('btn-apuntar', 'completo');
        btn.classList.add('btn-desapuntar', 'apuntado');
        btn.title = 'Desapuntarme';
        btn.disabled = false; // IMPORTANTE: nunca deshabilitar si está apuntado
        if (icon) icon.className = 'fas fa-check';
        
        // Forzar estilos inline para evitar conflictos CSS
        btn.style.backgroundColor = ''; 
        btn.style.cursor = 'pointer';
        
    } else {
        // Usuario se desapuntó
        btn.classList.remove('btn-desapuntar', 'apuntado', 'completo');
        btn.classList.add('btn-apuntar');
        btn.title = 'Apuntarme';
        btn.disabled = false;
        if (icon) icon.className = 'fas fa-user-plus';
        btn.style.cursor = 'pointer';
        
        // Solo si NO está apuntado y cupo completo → candado
        if (data.completo) {
            btn.classList.remove('btn-apuntar');
            btn.classList.add('completo');
            btn.disabled = true;
            btn.title = 'Cupo completo';
            if (icon) icon.className = 'fas fa-lock';
            btn.style.cursor = 'not-allowed';
        }
    }
}