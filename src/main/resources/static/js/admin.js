document.addEventListener('DOMContentLoaded', function() {
    
    // ========== MODAL AGREGAR ANUNCIO ==========
    const addPropertyBtn = document.getElementById('addPropertyBtn');
    const modalOverlay = document.getElementById('modalOverlay');
    const closeModal = document.getElementById('closeModal');
    const cancelModal = document.getElementById('cancelModal');

    if (addPropertyBtn) {
        addPropertyBtn.addEventListener('click', function() {
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

    window.openEditModal = function(id) {
        console.log('Editar anuncio ID:', id);
    };

    window.confirmarEliminar = function(id) {
        if (confirm('¿Estás seguro de eliminar este usuario?')) {
            fetch('/admin/usuarios/eliminar?id=' + id, {  // id es Integer, no necesita encodeURIComponent
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                }
            }).then(() => {
                window.location.reload();
            });
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

    // ========== CERRAR CON ESCAPE ==========
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            if (modalOverlay && modalOverlay.classList.contains('active')) {
                closeAddModal();
            }
            if (deleteModalOverlay && deleteModalOverlay.classList.contains('active')) {
                closeDeleteModalFunc();
            }
        }
    });

    console.log('✅ Admin JS cargado correctamente');

    //===DESPLEGAR CONFIGURACION (CAMBIAR DATOS USUARIO)=====
    function toggleEdicion() {
            const modoLectura = document.getElementById('modoLectura');
            const modoEdicion = document.getElementById('modoEdicion');
            const btn = document.getElementById('btnEditarPerfil');
            
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
});