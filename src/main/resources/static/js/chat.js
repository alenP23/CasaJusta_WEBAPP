// ===== CHAT DE VIVIENDA =====
console.log('=== chat.js cargado ===');

let stompClient = null;

document.addEventListener('DOMContentLoaded', function() {
    // Validar que las variables existen
    if (typeof CHAT_ID === 'undefined' || CHAT_ID === 0) {
        console.error('ERROR: CHAT_ID no definido');
        return;
    }
    
    conectarWebSocket();
    
    // Evento Enter en input
    const input = document.getElementById('mensajeInput');
    if (input) {
        input.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                enviarMensaje();
            }
        });
        // Focus automático
        input.focus();
    }
    
    // Scroll al final de mensajes
    scrollToBottom();
});

// ===== WEBSOCKET =====
function conectarWebSocket() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.debug = null;
    
    stompClient.connect({}, function(frame) {
        console.log('WebSocket conectado:', frame);
        
        stompClient.subscribe('/topic/chat/' + CHAT_ID, function(message) {
            const data = JSON.parse(message.body);
            console.log('Mensaje recibido:', data);
            
            if (data.tipo === 'voto') {
                // Actualizar contador de votos sin recargar
                location.reload();
            } else if (data.tipo === 'sistema') {
                mostrarMensajeSistema(data.contenido);
                cerrarChat();
            } else {
                mostrarMensaje(data);
            }
        });
        
    }, function(error) {
        console.error('Error WebSocket:', error);
        setTimeout(conectarWebSocket, 5000);
    });
}

// ===== ENVIAR MENSAJE =====
function enviarMensaje() {
    const input = document.getElementById('mensajeInput');
    if (!input) return;
    
    const contenido = input.value.trim();
    if (!contenido) return;
    
    // Limpiar input y deshabilitar
    input.value = '';
    input.disabled = true;
    
    fetch(`/api/chat/${CHAT_ID}/mensaje`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ contenido: contenido })
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(data => { throw new Error(data.message); });
        }
        return response.json();
    })
    .then(data => {
        if (!data.success) {
            alert(data.message || 'Error al enviar');
        }
        // El WebSocket mostrará el mensaje
    })
    .catch(error => {
        console.error('Error:', error);
        alert(error.message || 'Error de conexión');
    })
    .finally(() => {
        input.disabled = false;
        input.focus();
    });
}

// ===== VOTAR =====
function votar(votoSi) {
    fetch(`/api/chat/${CHAT_ID}/votar`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ votoSi: votoSi })
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(data => { throw new Error(data.message); });
        }
        return response.json();
    })
    .then(data => {
        if (data.success) {
            if (data.todosVotaronSi) {
                cerrarChat();
                mostrarMensajeSistema('🎉 ¡Todos han aceptado! El piso ha sido asignado.');
            } else {
                // Recargar para actualizar UI de votación
                location.reload();
            }
        } else {
            alert(data.message || 'Error al votar');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert(error.message || 'Error de conexión');
    });
}

// ===== MOSTRAR MENSAJE EN CHAT =====
function mostrarMensaje(data) {
    const container = document.getElementById('chatMessages');
    if (!container) return;
    
    const isOwn = data.dniUsuario === DNI_USUARIO;
    
    const div = document.createElement('div');
    div.className = isOwn ? 'message message-own' : 'message';
    
    div.innerHTML = `
        <div class="message-bubble">
            <span class="message-author">${escapeHtml(data.nombreUsuario)}</span>
            <p class="message-text">${escapeHtml(data.contenido)}</p>
            <span class="message-time">${data.fechaEnvio || 'Ahora'}</span>
        </div>
    `;
    
    container.appendChild(div);
    scrollToBottom();
}

// ===== MOSTRAR MENSAJE DE SISTEMA =====
function mostrarMensajeSistema(contenido) {
    const container = document.getElementById('chatMessages');
    if (!container) return;
    
    const div = document.createElement('div');
    div.className = 'message message-system';
    div.innerHTML = `
        <div class="message-bubble">
            <p>${escapeHtml(contenido)}</p>
        </div>
    `;
    
    container.appendChild(div);
    scrollToBottom();
}

// ===== CERRAR CHAT (UI) =====
function cerrarChat() {
    // Ocultar input
    const inputArea = document.querySelector('.chat-input-area');
    if (inputArea) inputArea.style.display = 'none';
    
    // Ocultar votación
    const votacion = document.querySelector('.votacion-panel');
    if (votacion) votacion.style.display = 'none';
    
    // Mostrar mensaje de cerrado si no existe
    if (!document.querySelector('.chat-cerrado')) {
        const wrapper = document.querySelector('.chat-page-wrapper');
        const cerrado = document.createElement('div');
        cerrado.className = 'chat-cerrado';
        cerrado.innerHTML = '<i class="fas fa-lock"></i> Chat cerrado - Piso asignado';
        wrapper.appendChild(cerrado);
    }
}

// ===== SCROLL AL FINAL =====
function scrollToBottom() {
    const container = document.getElementById('chatMessages');
    if (container) {
        container.scrollTop = container.scrollHeight;
    }
}

// ===== ESCAPAR HTML (seguridad) =====
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}