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
    const user = JSON.parse(localStorage.getItem('user'));
    
    // Separate complaints into categories
    const myActiveComplaints = complaints.filter(c => c.workerUsername === user.username && c.status !== 'RESOLVED');
    const myCompletedComplaints = complaints.filter(c => c.workerUsername === user.username && c.status === 'RESOLVED');
    const availableComplaints = complaints.filter(c => !c.workerUsername);

    complaintsContainer.innerHTML = `
        <div class="complaints-section">
            <h3>My Active Complaints</h3>
            ${renderComplaints(myActiveComplaints, user, true)}
        </div>
        
        <div class="complaints-section">
            <h3>Completed Jobs</h3>
            ${renderComplaints(myCompletedComplaints, user, false)}
        </div>
        
        ${availableComplaints.length > 0 ? `
            <div class="complaints-section">
                <h3>Available Jobs</h3>
                ${renderComplaints(availableComplaints, user, false)}
            </div>
        ` : ''}
    `;
}

function renderComplaints(complaints, user, showStatusUpdate) {
    if (complaints.length === 0) {
        return '<p class="no-complaints">No complaints in this category</p>';
    }

    return complaints.map(complaint => `
        <div class="complaint-item ${complaint.status.toLowerCase()}">
            <h3>${complaint.category}</h3>
            <p>${complaint.description}</p>
            <p>Status: ${complaint.status}</p>
            <p>Student: ${complaint.studentUsername}</p>
            <p>Created: ${new Date(complaint.createdAt).toLocaleDateString()}</p>
            ${complaint.resolvedAt ? 
                `<p>Resolved: ${new Date(complaint.resolvedAt).toLocaleDateString()}</p>` : ''}
            
            ${complaint.workerUsername === user.username ? `
                <div class="worker-comments">
                    <h4>Work Notes:</h4>
                    <p>${complaint.workerComments || 'No comments yet'}</p>
                    ${showStatusUpdate ? `
                        <textarea class="comment-input" 
                            id="comment-${complaint.id}" 
                            placeholder="Add work notes..."
                            rows="2">${complaint.workerComments || ''}</textarea>
                        <button onclick="saveComments(${complaint.id})" 
                            class="btn btn-primary">Save Notes</button>
                    ` : ''}
                </div>
                ${showStatusUpdate ? `
                    <div class="form-group">
                        <select onchange="updateStatus('${complaint.id}', this.value)">
                            <option value="OPEN" ${complaint.status === 'OPEN' ? 'selected' : ''}>Open</option>
                            <option value="IN_PROGRESS" ${complaint.status === 'IN_PROGRESS' ? 'selected' : ''}>In Progress</option>
                            <option value="RESOLVED" ${complaint.status === 'RESOLVED' ? 'selected' : ''}>Resolved</option>
                        </select>
                    </div>
                ` : ''}
            ` : `
                <div class="button-group">
                    <button onclick="assignToMe(${complaint.id})" 
                        class="btn btn-primary"
                        ${complaint.workerUsername ? 'disabled' : ''}>
                        Take Assignment
                    </button>
                </div>
            `}
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

async function saveComments(complaintId) {
    const comments = document.getElementById(`comment-${complaintId}`).value;
    try {
        const response = await fetch(`/api/complaints/${complaintId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ 
                workerComments: comments 
            })
        });

        if (!response.ok) {
            throw new Error('Failed to save comments');
        }

        await loadWorkerComplaints();
    } catch (error) {
        console.error('Error:', error);
        alert('Failed to save comments');
    }
}

async function assignToMe(complaintId) {
    const user = JSON.parse(localStorage.getItem('user'));
    try {
        const response = await fetch(`/api/complaints/${complaintId}/assign`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(user.username)
        });

        if (!response.ok) {
            throw new Error('Failed to assign complaint');
        }

        await loadWorkerComplaints();
    } catch (error) {
        console.error('Error:', error);
        alert('Failed to assign complaint');
    }
}
