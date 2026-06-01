// ========== FUNCIONES GLOBALES (accesibles desde onclick en HTML) ==========

function toggleEdicion() {
    const modoLectura = document.getElementById('modoLectura');
    const modoEdicion = document.getElementById('modoEdicion');
    const btn = document.getElementById('btnEditarPerfil');
    
    if (!modoLectura || !modoEdicion || !btn) return;
    
    if (modoEdicion.style.display === 'none') {
        modoLectura.style.display = 'none';
        modoEdicion.style.display = 'block';
        btn.innerHTML = '<i class="fas fa-times"></i> Cancelar edición';
        btn.classList.add('btn-cancelar');
    } else {
        modoLectura.style.display = 'grid';
        modoEdicion.style.display = 'none';
        btn.innerHTML = '<i class="fas fa-pen"></i> Editar perfil';
        btn.classList.remove('btn-cancelar');
    }
}

// ========== DOMContentLoaded (todo lo demás) ==========
document.addEventListener('DOMContentLoaded', function() {
    
    // ========== MODAL AGREGAR ANUNCIO ==========
    const addPropertyBtn = document.getElementById('addPropertyBtn');
    const modalOverlay = document.getElementById('modalOverlay');
    const closeModal = document.getElementById('closeModal');
    const cancelModal = document.getElementById('cancelModal');

    if (addPropertyBtn) {
        addPropertyBtn.addEventListener('click', function(e) {
            e.stopPropagation(); // Evitar que el click se propague
            if (modalOverlay) {
                modalOverlay.classList.add('active');
                document.body.style.overflow = 'hidden';
            }
        });
    }

    function closeAddModal() {
        if (modalOverlay) {
            modalOverlay.classList.remove('active');
            document.body.style.overflow = '';
        }
    }

    if (closeModal) closeModal.addEventListener('click', closeAddModal);
    if (cancelModal) cancelModal.addEventListener('click', closeAddModal);
    if (modalOverlay) {
        modalOverlay.addEventListener('click', function(e) {
            if (e.target === modalOverlay) closeAddModal();
        });
    }

    // ========== MODAL ELIMINAR ==========
    const deleteModalOverlay = document.getElementById('deleteModalOverlay');
    const closeDeleteModal = document.getElementById('closeDeleteModal');
    const cancelDelete = document.getElementById('cancelDelete');
    const deleteForm = document.getElementById('deleteForm');
    const deleteIdInput = document.getElementById('deleteId');

    window.openDeleteModal = function(id) {
        if (deleteIdInput) deleteIdInput.value = id;
        if (deleteModalOverlay) {
            deleteModalOverlay.classList.add('active');
            document.body.style.overflow = 'hidden';
        }
    };

    function closeDeleteModalFunc() {
        if (deleteModalOverlay) {
            deleteModalOverlay.classList.remove('active');
            document.body.style.overflow = '';
        }
        if (deleteIdInput) deleteIdInput.value = '';
    }

    if (closeDeleteModal) closeDeleteModal.addEventListener('click', closeDeleteModalFunc);
    if (cancelDelete) cancelDelete.addEventListener('click', closeDeleteModalFunc);
    if (deleteModalOverlay) {
        deleteModalOverlay.addEventListener('click', function(e) {
            if (e.target === deleteModalOverlay) closeDeleteModalFunc();
        });
    }

    // ========== MODAL EDITAR ==========
    const editModalOverlay = document.getElementById('editModalOverlay');
    const closeEditModal = document.getElementById('closeEditModal');
    const cancelEdit = document.getElementById('cancelEdit');
    const editForm = document.getElementById('editPropertyForm');

    window.openEditModal = function(id) {
        const card = document.querySelector(`.admin-property-card[data-id="${id}"]`);
        
        if (!card) {
            console.error('No se encontró la tarjeta con ID:', id);
            return;
        }
        
        const titulo = card.querySelector('.property-details h3')?.textContent || '';
        const tipo = card.dataset.tipo || 'alquiler';
        const precio = card.dataset.precio || '';
        
        document.getElementById('editId').value = id;
        document.getElementById('editTitle').value = titulo;
        document.getElementById('editType').value = tipo;
        document.getElementById('editPrice').value = precio;
        
        fetch(`/admin/anuncio/${id}/datos`)
            .then(response => {
                if (!response.ok) throw new Error('Error al cargar datos');
                return response.json();
            })
            .then(data => {
                document.getElementById('editLocation').value = data.direccion || '';
                document.getElementById('editRooms').value = data.habitaciones || 0;
                document.getElementById('editBathrooms').value = data.banos || 0;
                document.getElementById('editMeters').value = data.metrosCuadrados || 0;
                document.getElementById('editPersons').value = data.cupoPersonas || 0;
                
                setToggle('fumador', data.fumador);
                setToggle('mascotas', data.mascotas);
                setToggle('pareja', data.pareja);
            })
            .catch(err => {
                console.error('Error cargando datos completos:', err);
                document.getElementById('editLocation').value = '';
                document.getElementById('editRooms').value = 0;
                document.getElementById('editBathrooms').value = 0;
                document.getElementById('editMeters').value = 0;
                document.getElementById('editPersons').value = 0;
                setToggle('fumador', false);
                setToggle('mascotas', false);
                setToggle('pareja', false);
            });
        
        if (editModalOverlay) {
            editModalOverlay.classList.add('active');
            document.body.style.overflow = 'hidden';
        }
    };

    function setToggle(nombre, valor) {
        const capitalized = nombre.charAt(0).toUpperCase() + nombre.slice(1);
        const pill = document.getElementById('toggle' + capitalized);
        const input = document.getElementById('input' + capitalized);
        
        if (!pill || !input) return;
        
        const boolValor = valor === true || valor === 'true' || valor === 1;
        input.value = boolValor ? 'true' : 'false';
        
        if (boolValor) {
            pill.classList.add('active');
            pill.classList.remove('inactive');
        } else {
            pill.classList.remove('active');
            pill.classList.add('inactive');
        }
    }

    window.toggleFilter = function(nombre) {
        const capitalized = nombre.charAt(0).toUpperCase() + nombre.slice(1);
        const pill = document.getElementById('toggle' + capitalized);
        const input = document.getElementById('input' + capitalized);
        
        if (!pill || !input) return;
        
        const actual = input.value === 'true';
        const nuevo = !actual;
        
        input.value = nuevo ? 'true' : 'false';
        
        if (nuevo) {
            pill.classList.add('active');
            pill.classList.remove('inactive');
        } else {
            pill.classList.remove('active');
            pill.classList.add('inactive');
        }
    };

    function closeEditModalFunc() {
        if (editModalOverlay) {
            editModalOverlay.classList.remove('active');
            document.body.style.overflow = '';
        }
        if (editForm) editForm.reset();
        setToggle('fumador', false);
        setToggle('mascotas', false);
        setToggle('pareja', false);
    }

    if (closeEditModal) closeEditModal.addEventListener('click', closeEditModalFunc);
    if (cancelEdit) cancelEdit.addEventListener('click', closeEditModalFunc);
    if (editModalOverlay) {
        editModalOverlay.addEventListener('click', function(e) {
            if (e.target === editModalOverlay) closeEditModalFunc();
        });
    }

    // ========== CERRAR CON ESCAPE ==========
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            if (modalOverlay && modalOverlay.classList.contains('active')) {
                closeAddModal();
            }
            if (deleteModalOverlay && deleteModalOverlay.classList.contains('active')) {
                closeDeleteModalFunc();
            }
            if (editModalOverlay && editModalOverlay.classList.contains('active')) {
                closeEditModalFunc();
            }
        }
    });

    console.log('✅ Admin JS cargado correctamente');

    // ========== MARCAR PESTAÑA ACTIVA EN SIDEBAR ==========
    function marcarPestanaActiva() {
        const path = window.location.pathname;
        const navItems = document.querySelectorAll('.sidebar-nav .nav-item');
        
        navItems.forEach(item => item.classList.remove('active'));
        
        let mejorCoincidencia = null;
        let mejorLongitud = 0;
        
        navItems.forEach(item => {
            const href = item.getAttribute('href');
            if (!href) return;

            if (path === href) {
                mejorCoincidencia = item;
                mejorLongitud = href.length;
            } else if (path.startsWith(href + '/') && href.length > mejorLongitud) {
                mejorCoincidencia = item;
                mejorLongitud = href.length;
            }
        });
        
        if (mejorCoincidencia) {
            mejorCoincidencia.classList.add('active');
        }
    }

    marcarPestanaActiva();
});