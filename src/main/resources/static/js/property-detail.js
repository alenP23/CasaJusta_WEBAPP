document.addEventListener('DOMContentLoaded', function() {
    
    // ========== SUBIR IMAGEN DESDE PROPERTY-DETAIL (admin) ==========
    const btnAddImage = document.getElementById('btnAddImage');
    const inputSubirImagen = document.getElementById('inputSubirImagen');
    const uploadImageForm = document.getElementById('uploadImageForm');

    if (btnAddImage && inputSubirImagen) {
        btnAddImage.addEventListener('click', function() {
            inputSubirImagen.click();
        });

        inputSubirImagen.addEventListener('change', function() {
            if (this.files && this.files[0]) {
                uploadImageForm.submit();
            }
        });
    }

});