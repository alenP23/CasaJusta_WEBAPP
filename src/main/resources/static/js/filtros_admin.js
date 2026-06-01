// ========== GESTIÓN DE DESPLEGABLES ==========

document.addEventListener('DOMContentLoaded', () => {
    const btnFiltroConvivencia = document.getElementById('btnFiltroConvivencia');
    const menuConvivencia = document.getElementById('menuConvivencia');
    const btnFiltroTipo = document.getElementById('btnFiltroTipo');
    const menuTipoOperacion = document.getElementById('menuTipoOperacion');
    const btnFiltroPrecio = document.getElementById('btnFiltroPrecio');
    const menuPrecio = document.getElementById('menuPrecio');
    const precioMaximo = document.getElementById('precioMaximo');
    const precioValor = document.getElementById('precioValor');

    // Abrir/cerrar menús
    btnFiltroConvivencia?.addEventListener('click', (e) => {
        e.stopPropagation();
        cerrarTodosMenus();
        menuConvivencia?.classList.toggle('activo');
    });

    btnFiltroTipo?.addEventListener('click', (e) => {
        e.stopPropagation();
        cerrarTodosMenus();
        menuTipoOperacion?.classList.toggle('activo');
    });

    btnFiltroPrecio?.addEventListener('click', (e) => {
        e.stopPropagation();
        cerrarTodosMenus();
        menuPrecio?.classList.toggle('activo');
    });

    document.addEventListener('click', () => cerrarTodosMenus());
    
    [menuConvivencia, menuTipoOperacion, menuPrecio].forEach(menu => {
        menu?.addEventListener('click', (e) => e.stopPropagation());
    });

    // ========== SLIDER DE PRECIO ==========
    precioMaximo?.addEventListener('input', (e) => {
        const valor = parseInt(e.target.value);
        if (precioValor) precioValor.textContent = valor.toLocaleString('es-ES');
    });
});

function cerrarTodosMenus() {
    document.querySelectorAll('.dropdown-menu').forEach(menu => menu.classList.remove('activo'));
}

// ========== ESTADO DE FILTROS ==========

let filtrosActivos = {
    tipoOperacion: 'todos',
    mascotas: false,
    fumador: false,
    pareja: false,
    precioMaximo: 5000000
};

// ========== FILTRO CONVIVENCIA ==========

function aplicarFiltros() {
    // Leer checkboxes por name exacto
    const cbMascotas = document.querySelector('input[name="filtroMascotas"]');
    const cbFumador = document.querySelector('input[name="filtroFumador"]');
    const cbPareja = document.querySelector('input[name="filtroPareja"]');
    
    filtrosActivos.mascotas = cbMascotas?.checked || false;
    filtrosActivos.fumador = cbFumador?.checked || false;
    filtrosActivos.pareja = cbPareja?.checked || false;
    
    console.log('Convivencia - Mascotas:', filtrosActivos.mascotas, 'Fumador:', filtrosActivos.fumador, 'Pareja:', filtrosActivos.pareja);
    
    aplicarTodosLosFiltros();
    cerrarTodosMenus();
}

function limpiarFiltros() {
    document.querySelector('input[name="filtroMascotas"]').checked = false;
    document.querySelector('input[name="filtroFumador"]').checked = false;
    document.querySelector('input[name="filtroPareja"]').checked = false;
    
    filtrosActivos.mascotas = false;
    filtrosActivos.fumador = false;
    filtrosActivos.pareja = false;
    
    aplicarTodosLosFiltros();
    cerrarTodosMenus();
}

// ========== FILTRO TIPO OPERACIÓN ==========

function aplicarFiltroTipo() {
    const seleccionado = document.querySelector('input[name="tipoOperacion"]:checked');
    filtrosActivos.tipoOperacion = seleccionado?.value || 'todos';
    aplicarTodosLosFiltros();
    cerrarTodosMenus();
}

function limpiarFiltroTipo() {
    const todos = document.querySelector('input[name="tipoOperacion"][value="todos"]');
    if (todos) todos.checked = true;
    filtrosActivos.tipoOperacion = 'todos';
    aplicarTodosLosFiltros();
    cerrarTodosMenus();
}

// ========== FILTRO PRECIO ==========

function aplicarFiltroPrecio() {
    const slider = document.getElementById('precioMaximo');
    filtrosActivos.precioMaximo = parseInt(slider?.value || 5000000);
    console.log('Precio máximo:', filtrosActivos.precioMaximo);
    aplicarTodosLosFiltros();
    cerrarTodosMenus();
}

function limpiarFiltroPrecio() {
    const slider = document.getElementById('precioMaximo');
    const valor = document.getElementById('precioValor');
    if (slider) slider.value = 5000000;
    if (valor) valor.textContent = '5.000.000';
    filtrosActivos.precioMaximo = 5000000;
    aplicarTodosLosFiltros();
    cerrarTodosMenus();
}

// ========== FUNCIÓN PRINCIPAL ==========

function aplicarTodosLosFiltros() {
    const tarjetas = document.querySelectorAll('.admin-property-card[data-id]');
    const addCard = document.getElementById('addPropertyBtn');
    
    let visibles = 0;
    
    tarjetas.forEach(tarjeta => {
        const tipo = tarjeta.dataset.tipo;
        const precio = parseFloat(tarjeta.dataset.precio) || 0;
        const tieneMascotas = tarjeta.dataset.mascotas === 'true';
        const tieneFumador = tarjeta.dataset.fumador === 'true';
        const tienePareja = tarjeta.dataset.pareja === 'true';
        
        const cumpleTipo = filtrosActivos.tipoOperacion === 'todos' || tipo === filtrosActivos.tipoOperacion;
        const cumplePrecio = precio <= filtrosActivos.precioMaximo;
        const cumpleMascotas = !filtrosActivos.mascotas || tieneMascotas;
        const cumpleFumador = !filtrosActivos.fumador || tieneFumador;
        const cumplePareja = !filtrosActivos.pareja || tienePareja;
        
        if (cumpleTipo && cumplePrecio && cumpleMascotas && cumpleFumador && cumplePareja) {
            tarjeta.style.display = 'flex';
            visibles++;
        } else {
            tarjeta.style.display = 'none';
        }
    });
    
    const hayFiltros = filtrosActivos.tipoOperacion !== 'todos' || 
                       filtrosActivos.mascotas || 
                       filtrosActivos.fumador || 
                       filtrosActivos.pareja || 
                       filtrosActivos.precioMaximo < 5000000;
    
    if (addCard) addCard.style.display = hayFiltros ? 'none' : 'flex';
    
    console.log('=== FILTROS APLICADOS ===');
    console.log('Estado:', filtrosActivos);
    console.log('Tarjetas visibles:', visibles);
}