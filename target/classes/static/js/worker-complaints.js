async function loadWorkerComplaints() {
    const user = checkAuth();
    if (!user || user.role !== 'WORKER') {
        window.location.href = '/login.html';
        return;
    }

    try {
        // Changed endpoint to match controller
        const response = await fetch(`/api/complaints/worker/${user.username}`);
        if (!response.ok) {
            throw new Error('Failed to fetch complaints');
        }
        const complaints = await response.json();
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
            <p>Created: ${new Date(complaint.createdAt).toLocaleDateString()}</p>
            ${complaint.resolvedAt ? 
                `<p>Resolved: ${new Date(complaint.resolvedAt).toLocaleDateString()}</p>` : ''}
            <div class="form-group">
                <select onchange="updateStatus('${complaint.id}', this.value)"
                    ${complaint.status === 'RESOLVED' ? 'disabled' : ''}>
                    <option value="OPEN" ${complaint.status === 'OPEN' ? 'selected' : ''}>Open</option>
                    <option value="IN_PROGRESS" ${complaint.status === 'IN_PROGRESS' ? 'selected' : ''}>In Progress</option>
                    <option value="RESOLVED" ${complaint.status === 'RESOLVED' ? 'selected' : ''}>Resolved</option>
                </select>
            </div>
        </div>
    `).join('');
}

async function updateStatus(complaintId, status) {
    try {
        const response = await fetch(`/api/complaints/${complaintId}/status`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ status }),
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to update status');
        }

        await loadWorkerComplaints();
    } catch (error) {
        console.error('Error:', error);
        alert(error.message);
    }
}
