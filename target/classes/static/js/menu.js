function showInfo(option) {
    const user = JSON.parse(localStorage.getItem('user') || 'null');
    if (!user) return;

    // Create a new Bootstrap modal instance
    const modalEl = document.getElementById('infoModal');
    const modal = new bootstrap.Modal(modalEl);
    const infoTitle = document.getElementById('infoTitle');
    const infoContent = document.getElementById('infoContent');

    switch(option) {
        case 'option1':
            infoTitle.textContent = 'Your Profile';
            infoContent.innerHTML = `
                <div class="profile-info">
                    <p><strong>Username:</strong> ${user.username}</p>
                    <p><strong>Role:</strong> ${user.role}</p>
                    ${user.workerCategory ? `<p><strong>Category:</strong> ${user.workerCategory}</p>` : ''}
                    ${user.lastLoginDate ? `<p><strong>Last Login:</strong> ${new Date(user.lastLoginDate).toLocaleString()}</p>` : ''}
                </div>
            `;
            break;
        case 'option2':
            infoTitle.textContent = 'Help';
            infoContent.innerHTML = `
                <div class="help-info">
                    <p>Need assistance? Contact support:</p>
                    <p>Email: support@example.com</p>
                    <p>Phone: (123) 456-7890</p>
                </div>
            `;
            break;
        case 'option3':
            infoTitle.textContent = 'About';
            infoContent.innerHTML = `
                <div class="about-info">
                    <p>Complaint Management System v1.0</p>
                    <p>A system to manage and track complaints efficiently.</p>
                </div>
            `;
            break;
    }

    modal.show();
}

// Initialize all tooltips and popovers if any
document.addEventListener('DOMContentLoaded', function () {
    // Initialize Bootstrap components
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
});
