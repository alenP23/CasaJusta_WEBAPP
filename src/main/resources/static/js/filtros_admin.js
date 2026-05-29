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

    // Abrir/cerrar menús al hacer clic en el botón
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

    // Cerrar menús al hacer clic fuera
    document.addEventListener('click', () => {
        cerrarTodosMenus();
    });

    // Evitar que se cierre al hacer clic dentro del menú
    [menuConvivencia, menuTipoOperacion, menuPrecio].forEach(menu => {
        menu?.addEventListener('click', (e) => e.stopPropagation());
    });

    function cerrarTodosMenus() {
        menuConvivencia?.classList.remove('activo');
        menuTipoOperacion?.classList.remove('activo');
        menuPrecio?.classList.remove('activo');
    }

    // ========== SLIDER DE PRECIO ==========
    
    precioMaximo?.addEventListener('input', (e) => {
        const valor = parseInt(e.target.value);
        if (precioValor) {
            precioValor.textContent = valor.toLocaleString('es-ES');
        }
    });

    // ========== FILTRO ALQUILER/COMPRA (AUTO-APLICAR) ==========
    
    document.querySelectorAll('input[name="tipoOperacion"]').forEach(radio => {
        radio.addEventListener('change', () => {
            const seleccion = document.querySelector('input[name="tipoOperacion"]:checked')?.value;
            filtrarPorTipo(seleccion);
        });
    });
});

// ========== FUNCIONES DE FILTRO ==========

function aplicarFiltros() {
    const mascotas = document.querySelector('input[name="filtroMascotas"]')?.checked;
    const fumador = document.querySelector('input[name="filtroFumador"]')?.checked;
    const pareja = document.querySelector('input[name="filtroPareja"]')?.checked;
    
    console.log('Filtros de convivencia:', { mascotas, fumador, pareja });
    // TODO: Implementar filtrado real o enviar al backend
    cerrarTodosMenus();
}

function limpiarFiltros() {
    const checkboxes = document.querySelectorAll('input[name="filtroMascotas"], input[name="filtroFumador"], input[name="filtroPareja"]');
    checkboxes.forEach(cb => cb.checked = false);
    aplicarFiltros();
}

function aplicarFiltroPrecio() {
    const maximo = parseInt(document.getElementById('precioMaximo')?.value || 0);
    console.log('Precio máximo:', maximo);
    filtrarPorPrecio(maximo);
    cerrarTodosMenus();
}

function limpiarFiltroPrecio() {
    const slider = document.getElementById('precioMaximo');
    const valor = document.getElementById('precioValor');
    if (slider) slider.value = 5000000;
    if (valor) valor.textContent = '5.000.000';
    aplicarFiltroPrecio();
}

function filtrarPorTipo(tipo) {
    const tarjetas = document.querySelectorAll('.admin-property-card[data-tipo]');
    
    tarjetas.forEach(tarjeta => {
        if (!tipo || tipo === 'todos' || tarjeta.dataset.tipo === tipo) {
            tarjeta.style.display = '';
        } else {
            tarjeta.style.display = 'none';
        }
    });
}

function filtrarPorPrecio(maximo) {
    const tarjetas = document.querySelectorAll('.admin-property-card[data-precio]');
    
    tarjetas.forEach(tarjeta => {
        const precio = parseFloat(tarjeta.dataset.precio);
        if (precio <= maximo) {
            tarjeta.style.display = '';
        } else {
            tarjeta.style.display = 'none';
        }
    });
}

function cerrarTodosMenus() {
    document.querySelectorAll('.dropdown-menu').forEach(menu => {
        menu.classList.remove('activo');
    });
}