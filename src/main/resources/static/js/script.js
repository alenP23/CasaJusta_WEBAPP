document.addEventListener('DOMContentLoaded', function() {
    
    // Elementos
    const filterToggle = document.getElementById('filterToggle');
    const closeFilters = document.getElementById('closeFilters');
    const filtersSidebar = document.getElementById('filtersSidebar');
    const overlay = document.getElementById('overlay');
    const applyFilters = document.getElementById('applyFilters');

    // Abrir sidebar
    function openSidebar() {
        filtersSidebar.classList.add('open');
        filtersSidebar.setAttribute('aria-hidden', 'false');
        overlay.classList.add('active');
        document.body.style.overflow = 'hidden'; // Evita scroll del body
    }

    // Cerrar sidebar
    function closeSidebar() {
        filtersSidebar.classList.remove('open');
        filtersSidebar.setAttribute('aria-hidden', 'true');
        overlay.classList.remove('active');
        document.body.style.overflow = '';
    }

    // Event listeners
    if (filterToggle) {
        filterToggle.addEventListener('click', openSidebar);
    }
    
    if (closeFilters) {
        closeFilters.addEventListener('click', closeSidebar);
    }
    
    if (overlay) {
        overlay.addEventListener('click', closeSidebar);
    }

    // Cerrar con tecla Escape
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape' && filtersSidebar.classList.contains('open')) {
            closeSidebar();
        }
    });

    // Botón aplicar filtros (placeholder)
    if (applyFilters) {
        applyFilters.addEventListener('click', function() {
            console.log('Aplicando filtros...');
            // Aquí irá la lógica de filtrado
            closeSidebar();
        });
    }
});