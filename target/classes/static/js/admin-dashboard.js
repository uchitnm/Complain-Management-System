let currentComplaints = [];

async function applyFilters() {
    const user = JSON.parse(localStorage.getItem('user'));
    if (!user || user.role !== 'ADMIN') {
        window.location.href = '/login.html';
        return;
    }

    const student = document.getElementById('searchStudent').value;
    const category = document.getElementById('categoryFilter').value;
    const status = document.getElementById('statusFilter').value;

    try {
        const queryParams = new URLSearchParams();
        if (student) queryParams.append('student', student);
        if (category) queryParams.append('category', category);
        if (status) queryParams.append('status', status);

        const response = await fetch(`/api/complaints/filter?${queryParams}`, {
            headers: {
                'Accept': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const complaints = await response.json();
        currentComplaints = complaints;
        displayComplaints(complaints);
    } catch (error) {
        console.error('Error:', error);
        alert('Failed to load complaints: ' + error.message);
    }
}

function displayComplaints(complaints) {
    const complaintsContainer = document.getElementById('complaints-list');
    complaintsContainer.innerHTML = complaints.map(complaint => `
        <div class="complaint-item ${complaint.status.toLowerCase()}">
            <h3>${complaint.category}</h3>
            <p>${complaint.description}</p>
            <p>Status: ${complaint.status}</p>
            <p>Student: ${complaint.studentUsername}</p>
            <p>Worker: ${complaint.workerUsername || 'Unassigned'}</p>
            <p>Created: ${new Date(complaint.createdAt).toLocaleDateString()}</p>
            ${complaint.resolvedAt ? 
                `<p>Resolved: ${new Date(complaint.resolvedAt).toLocaleDateString()}</p>` : ''}
            ${complaint.workerComments ? `
                <div class="worker-comments">
                    <h4>Worker Notes:</h4>
                    <p>${complaint.workerComments}</p>
                </div>
            ` : ''}
            <p class="last-updated">Last Updated: ${new Date(complaint.lastUpdated || complaint.createdAt).toLocaleString()}</p>
            <div class="button-group">
                <button onclick="viewComplaint(${complaint.id})" class="btn btn-primary">View/Edit</button>
            </div>
        </div>
    `).join('');
}

function viewComplaint(id) {
    const complaint = currentComplaints.find(c => c.id === id);
    if (!complaint) return;

    fetch('/api/users/workers/' + complaint.category)
        .then(response => response.json())
        .then(workers => {
            const workerSelect = workers.map(w => 
                `<option value="${w.username}" ${w.username === complaint.workerUsername ? 'selected' : ''}>
                    ${w.username}
                </option>`
            ).join('');

            document.getElementById('complaintId').value = complaint.id;
            document.getElementById('modalStudent').value = complaint.studentUsername;
            document.getElementById('modalCategory').value = complaint.category;
            document.getElementById('modalDescription').value = complaint.description;
            document.getElementById('modalStatus').value = complaint.status;
            document.getElementById('modalWorkerComments').value = complaint.workerComments || '';
            document.getElementById('modalWorker').innerHTML = workerSelect;
            
            // Use Bootstrap modal show method
            const modal = new bootstrap.Modal(document.getElementById('complaintModal'));
            modal.show();
        });
}

async function saveComplaint() {
    const id = document.getElementById('complaintId').value;
    const updatedComplaint = {
        category: document.getElementById('modalCategory').value,
        description: document.getElementById('modalDescription').value,
        status: document.getElementById('modalStatus').value
    };

    try {
        // Update complaint details
        const updateResponse = await fetch(`/api/complaints/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(updatedComplaint)
        });

        if (!updateResponse.ok) {
            throw new Error('Failed to update complaint');
        }

        // Close modal and refresh
        const modal = bootstrap.Modal.getInstance(document.getElementById('complaintModal'));
        modal.hide();
        await applyFilters();
    } catch (error) {
        console.error('Error:', error);
        alert(error.message);
    }
}

async function deleteComplaint() {
    if (!confirm('Are you sure you want to delete this complaint?')) return;

    const id = document.getElementById('complaintId').value;
    try {
        const response = await fetch(`/api/complaints/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            const modal = bootstrap.Modal.getInstance(document.getElementById('complaintModal'));
            modal.hide();
            applyFilters();
        } else {
            alert('Failed to delete complaint');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Failed to delete complaint');
    }
}

// Close modal when clicking the X or outside the modal
document.querySelector('.close').onclick = function() {
    const modal = bootstrap.Modal.getInstance(document.getElementById('complaintModal'));
    modal.hide();
}

window.onclick = function(event) {
    const modal = document.getElementById('complaintModal');
    if (event.target == modal) {
        const bootstrapModal = bootstrap.Modal.getInstance(modal);
        bootstrapModal.hide();
    }
}

document.addEventListener('DOMContentLoaded', applyFilters);
