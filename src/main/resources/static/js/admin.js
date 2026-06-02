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

    // ========== TOGGLES PARA MODAL CREAR ==========
    window.toggleCrearFilter = function(nombre) {
        const capitalized = nombre.charAt(0).toUpperCase() + nombre.slice(1);
        const pill = document.getElementById('toggleCrear' + capitalized);
        const input = document.getElementById('inputCrear' + capitalized);
        
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

    // ========== SUBIDA DE IMAGEN EN MODAL CREAR ==========
    const imageUploadArea = document.getElementById('imageUploadArea');
    const propImage = document.getElementById('propImage');
    const uploadPlaceholder = document.getElementById('uploadPlaceholder');
    const uploadPreview = document.getElementById('uploadPreview');
    const previewImg = document.getElementById('previewImg');
    const removeImage = document.getElementById('removeImage');

    if (imageUploadArea && propImage) {
        // Click en el área abre el input file
        imageUploadArea.addEventListener('click', function(e) {
            if (e.target.closest('.remove-image')) return;
            propImage.click();
        });

        // Arrastrar y soltar
        imageUploadArea.addEventListener('dragover', function(e) {
            e.preventDefault();
            this.style.borderColor = 'var(--primary)';
        });

        imageUploadArea.addEventListener('dragleave', function(e) {
            e.preventDefault();
            this.style.borderColor = '';
        });

        imageUploadArea.addEventListener('drop', function(e) {
            e.preventDefault();
            this.style.borderColor = '';
            const files = e.dataTransfer.files;
            if (files.length > 0) {
                propImage.files = files;
                mostrarPreview(files[0]);
            }
        });

        // Selección normal
        propImage.addEventListener('change', function() {
            if (this.files && this.files[0]) {
                mostrarPreview(this.files[0]);
            }
        });
    }

    function mostrarPreview(file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            previewImg.src = e.target.result;
            uploadPlaceholder.style.display = 'none';
            uploadPreview.style.display = 'block';
            imageUploadArea.classList.add('has-image');
        };
        reader.readAsDataURL(file);
    }

    if (removeImage) {
        removeImage.addEventListener('click', function(e) {
            e.stopPropagation();
            propImage.value = '';
            previewImg.src = '';
            uploadPreview.style.display = 'none';
            uploadPlaceholder.style.display = 'flex';
            imageUploadArea.classList.remove('has-image');
        });
    }

    // ========== MODAL ELIMINAR ANUNCIO ==========
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

     // ========== MODAL ELIMINAR USUARIO (NUEVO) ==========
    const deleteUserModalOverlay = document.getElementById('deleteUserModalOverlay');
    const closeDeleteUserModal = document.getElementById('closeDeleteUserModal');
    const cancelDeleteUser = document.getElementById('cancelDeleteUser');
    const deleteUserForm = document.getElementById('deleteUserForm');
    const deleteUserIdInput = document.getElementById('deleteUserId');
    const deleteUserName = document.getElementById('deleteUserName');

    // Event delegation para los botones de eliminar usuario
    document.addEventListener('click', function(e) {
        const btn = e.target.closest('.btn-eliminar-usuario');
        if (!btn) return;
        
        e.stopPropagation();
        const id = btn.getAttribute('data-id');
        const nombre = btn.getAttribute('data-nombre');
        
        if (deleteUserIdInput) deleteUserIdInput.value = id;
        if (deleteUserName) deleteUserName.textContent = nombre;
        
        if (deleteUserModalOverlay) {
            deleteUserModalOverlay.classList.add('active');
            document.body.style.overflow = 'hidden';
        }
    });

    function closeDeleteUserModalFunc() {
        if (deleteUserModalOverlay) {
            deleteUserModalOverlay.classList.remove('active');
            document.body.style.overflow = '';
        }
        if (deleteUserIdInput) deleteUserIdInput.value = '';
        if (deleteUserName) deleteUserName.textContent = '';
    }

    if (closeDeleteUserModal) closeDeleteUserModal.addEventListener('click', closeDeleteUserModalFunc);
    if (cancelDeleteUser) cancelDeleteUser.addEventListener('click', closeDeleteUserModalFunc);
    if (deleteUserModalOverlay) {
        deleteUserModalOverlay.addEventListener('click', function(e) {
            if (e.target === deleteUserModalOverlay) closeDeleteUserModalFunc();
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

    // ========== FILTRAR POR CONVIVENCIA ==========
    window.filtrarPorConvivencia = function(tipo, valor) {
        // Evitar que se propague el click a la tarjeta
        event.stopPropagation();
        
        const cards = document.querySelectorAll('.admin-property-card[data-id]');
        
        cards.forEach(card => {
            const cardValor = card.dataset[tipo] === 'true';
            
            if (cardValor === valor) {
                card.style.display = 'flex';
            } else {
                card.style.display = 'none';
            }
        });
        
        // Ocultar también la tarjeta de "Agregar"
        const addCard = document.getElementById('addPropertyBtn');
        if (addCard) addCard.style.display = 'none';
        
        console.log(`Filtrado por ${tipo}: ${valor}`);
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
            if (deleteUserModalOverlay && deleteUserModalOverlay.classList.contains('active')) {
                closeDeleteUserModalFunc();
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