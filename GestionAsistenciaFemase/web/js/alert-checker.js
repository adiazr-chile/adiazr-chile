<script>
    function checkAlerts() {
        // Realiza la llamada AJAX al servlet
        fetch('AlertServlet') // Cambia 'AlertServlet' por la ruta de tu servlet
            .then(response => response.json())
            .then(data => {
                if (data.hasAlert) {
                    // Si hay alertas, muestra una ventana modal
                    showAlertModal(data.message);
                }
            })
            .catch(error => console.error('Error al verificar alertas:', error));
    }

    function showAlertModal(message) {
        // Crea y muestra una ventana modal con el mensaje de alerta
        const modal = document.createElement('div');
        modal.style.position = 'fixed';
        modal.style.top = '50%';
        modal.style.left = '50%';
        modal.style.transform = 'translate(-50%, -50%)';
        modal.style.padding = '20px';
        modal.style.backgroundColor = '#fff';
        modal.style.border = '1px solid #ccc';
        modal.style.boxShadow = '0 4px 8px rgba(0, 0, 0, 0.2)';
        
        const messageElement = document.createElement('p');
        messageElement.textContent = message;
        
        const closeButton = document.createElement('button');
        closeButton.textContent = 'Cerrar';
        closeButton.onclick = () => document.body.removeChild(modal);

        modal.appendChild(messageElement);
        modal.appendChild(closeButton);
        
        document.body.appendChild(modal);
    }

    // Configura el intervalo para verificar alertas cada X minutos (ejemplo: cada 5 minutos)
    setInterval(checkAlerts, 5 * 60 * 1000); // 5 minutos en milisegundos
</script>
