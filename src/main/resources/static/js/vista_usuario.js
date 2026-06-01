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