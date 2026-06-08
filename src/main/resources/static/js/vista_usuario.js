document.addEventListener('DOMContentLoaded', function() {
    
    // ========== SIDEBAR DE FILTROS (overlay) ==========
    const filterToggle = document.getElementById('filterToggle');
    const closeFilters = document.getElementById('closeFilters');
    const filtersSidebar = document.getElementById('filtersSidebar');
    const overlay = document.getElementById('overlay');

    function openSidebar() {
        filtersSidebar.classList.add('open');
        filtersSidebar.setAttribute('aria-hidden', 'false');
        overlay.classList.add('active');
        document.body.style.overflow = 'hidden';
    }

    function closeSidebar() {
        filtersSidebar.classList.remove('open');
        filtersSidebar.setAttribute('aria-hidden', 'true');
        overlay.classList.remove('active');
        document.body.style.overflow = '';
    }

    if (filterToggle) filterToggle.addEventListener('click', openSidebar);
    if (closeFilters) closeFilters.addEventListener('click', closeSidebar);
    if (overlay) overlay.addEventListener('click', closeSidebar);

    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape' && filtersSidebar.classList.contains('open')) {
            closeSidebar();
        }
    });

    // ========== SLIDER CUSTOM ==========
    const sliderInput = document.getElementById('precioMaximo');
    const sliderFill = document.getElementById('sliderFill');
    const sliderThumb = document.getElementById('sliderThumb');
    const precioValor = document.getElementById('precioValor');

    function updateSlider() {
        if (!sliderInput || !sliderFill || !sliderThumb) return;
        
        const min = parseInt(sliderInput.min);
        const max = parseInt(sliderInput.max);
        const val = parseInt(sliderInput.value);
        
        const percentage = ((val - min) / (max - min)) * 100;
        
        // El fill va de 0 hasta el centro del thumb
        sliderFill.style.width = percentage + '%';
        // El thumb se centra en el porcentaje
        sliderThumb.style.left = percentage + '%';
        
        if (precioValor) {
            precioValor.textContent = val.toLocaleString('es-ES') + ' €';
        }
    }

    if (sliderInput) {
        updateSlider();
        sliderInput.addEventListener('input', updateSlider);
    }

    // ========== SELECCIÓN DE CARDS (tipo de operación) ==========
    const tipoCards = document.querySelectorAll('.tipo-card');
    
    tipoCards.forEach(card => {
        const input = card.querySelector('input[type="radio"]');
        
        input.addEventListener('change', function() {
            tipoCards.forEach(c => c.classList.remove('selected'));
            card.classList.add('selected');
        });
        
        card.addEventListener('click', function(e) {
            if (e.target !== input && e.target.tagName !== 'INPUT') {
                input.checked = true;
                input.dispatchEvent(new Event('change'));
            }
        });
    });

    console.log('✅ Vista usuario JS cargado correctamente');
});

// ========== FUNCIONES GLOBALES ==========
function contactarAnunciante(viviendaId) {
    console.log('Contactar anunciante de vivienda:', viviendaId);
    alert('Función de contacto en desarrollo. ID: ' + viviendaId);
}

function toggleFavorito(viviendaId) {
    console.log('Toggle favorito:', viviendaId);
    const btn = event.currentTarget;
    const icon = btn.querySelector('i');
    if (icon.classList.contains('far')) {
        icon.classList.remove('far');
        icon.classList.add('fas');
        btn.classList.add('active');
    } else {
        icon.classList.remove('fas');
        icon.classList.add('far');
        btn.classList.remove('active');
    }
}

// ===== COMPRAR VIVIENDA =====
function iniciarCompra(viviendaId) {
    console.log('Iniciar compra de vivienda:', viviendaId);
    
    fetch('/api/vivienda/' + viviendaId + '/comprar', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            mostrarToast('success', '¡Compra realizada!', 'Se ha enviado tu solicitud de compra al anunciante. Te contactará pronto.');
            // Recargar la página después de 2 segundos para actualizar la lista
            setTimeout(() => location.reload(), 2000);
        } else {
            mostrarToast('error', 'Error', data.message || 'No se pudo procesar la compra');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        mostrarToast('error', 'Error', 'Error de conexión. Inténtalo más tarde.');
    });
}

// ===== ALQUILAR VIVIENDA =====
function iniciarAlquiler(viviendaId) {
    console.log('Iniciar alquiler de vivienda:', viviendaId);
    
    fetch('/api/vivienda/' + viviendaId + '/alquilar', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            mostrarToast('success', '¡Alquiler realizado!', 'Se ha enviado tu solicitud de alquiler al anunciante. Te contactará pronto.');
            // Recargar la página después de 2 segundos
            setTimeout(() => location.reload(), 2000);
        } else {
            mostrarToast('error', 'Error', data.message || 'No se pudo procesar el alquiler');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        mostrarToast('error', 'Error', 'Error de conexión. Inténtalo más tarde.');
    });
}

// ===== FUNCIÓN TOAST =====
function mostrarToast(tipo, titulo, mensaje) {
    const container = document.getElementById('toast-container');
    if (!container) return;
    
    const iconos = {
        success: 'fa-check',
        error: 'fa-times',
        info: 'fa-info'
    };
    
    const toast = document.createElement('div');
    toast.className = `toast toast-${tipo}`;
    toast.innerHTML = `
        <div class="toast-icon">
            <i class="fas ${iconos[tipo] || iconos.info}"></i>
        </div>
        <div class="toast-content">
            <p class="toast-title">${escapeHtml(titulo)}</p>
            <p class="toast-message">${escapeHtml(mensaje)}</p>
        </div>
        <button class="toast-close" onclick="this.parentElement.remove()">
            <i class="fas fa-times"></i>
        </button>
    `;
    
    container.appendChild(toast);
    
    // Auto-eliminar después de 4 segundos
    setTimeout(() => {
        if (toast.parentElement) {
            toast.remove();
        }
    }, 4000);
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// ============================================
// TOGGLE EDICIÓN PERFIL (usuario_configuracion.html)
// ============================================

function toggleEdicion() {
    const modoLectura = document.getElementById('modoLectura');
    const modoEdicion = document.getElementById('modoEdicion');
    const btnEditar = document.getElementById('btnEditarPerfil');
    
    if (!modoLectura || !modoEdicion || !btnEditar) return; // Si no estamos en la página de perfil, no hacer nada
    
    if (modoEdicion.style.display === 'none' || modoEdicion.style.display === '') {
        modoLectura.style.display = 'none';
        modoEdicion.style.display = 'block';
        btnEditar.innerHTML = '<i class="fas fa-times"></i> Cancelar edición';
    } else {
        modoLectura.style.display = 'grid';
        modoEdicion.style.display = 'none';
        btnEditar.innerHTML = '<i class="fas fa-pen"></i> Editar perfil';
    }
}