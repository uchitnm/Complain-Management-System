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
        <div class="complaint-item">
            <h3>${complaint.category}</h3>
            <p>${complaint.description}</p>
            <p>Status: ${complaint.status}</p>
            <p>Student: ${complaint.studentUsername}</p>
            <p>Worker: ${complaint.workerUsername || 'Unassigned'}</p>
            <p>Created: ${new Date(complaint.createdAt).toLocaleDateString()}</p>
            ${complaint.resolvedAt ? 
                `<p>Resolved: ${new Date(complaint.resolvedAt).toLocaleDateString()}</p>` : ''}
            <div class="button-group">
                <button onclick="viewComplaint(${complaint.id})" class="btn btn-primary">View/Edit</button>
                </div>
        </div>
    `).join('');
}

{/* <button type="button" class="btn btn-danger" onclick="deleteComplaint()">Delete</button> */}
{/* <button onclick="autoAssignWorker(${complaint.id})" 
${complaint.workerUsername ? 'disabled' : ''} class="btn btn-primary">
Auto-assign Worker
</button> */}

async function autoAssignWorker(complaintId) {
    try {
        const response = await fetch(`/api/complaints/${complaintId}/auto-assign`, {
            method: 'PUT'
        });
        if (response.ok) {
            applyFilters();
        } else {
            alert('Failed to assign worker');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Failed to assign worker');
    }
}

function viewComplaint(id) {
    const complaint = currentComplaints.find(c => c.id === id);
    if (!complaint) return;

    document.getElementById('complaintId').value = complaint.id;
    document.getElementById('modalStudent').value = complaint.studentUsername;
    document.getElementById('modalCategory').value = complaint.category;
    document.getElementById('modalDescription').value = complaint.description;
    document.getElementById('modalStatus').value = complaint.status;

    const modal = document.getElementById('complaintModal');
    modal.style.display = 'block';
}

async function saveComplaint() {
    const id = document.getElementById('complaintId').value;
    const updatedComplaint = {
        category: document.getElementById('modalCategory').value,
        description: document.getElementById('modalDescription').value,
        status: document.getElementById('modalStatus').value,
        studentUsername: document.getElementById('modalStudent').value
    };

    try {
        const response = await fetch(`/api/complaints/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(updatedComplaint)
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to update complaint');
        }

        document.getElementById('complaintModal').style.display = 'none';
        applyFilters();
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
            document.getElementById('complaintModal').style.display = 'none';
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
    document.getElementById('complaintModal').style.display = 'none';
}

window.onclick = function(event) {
    const modal = document.getElementById('complaintModal');
    if (event.target == modal) {
        modal.style.display = 'none';
    }
}

document.addEventListener('DOMContentLoaded', applyFilters);
