async function submitComplaint(event) {
    event.preventDefault();
    const user = checkAuth();
    if (!user || user.role !== 'STUDENT') {
        window.location.href = '/login.html';
        return;
    }
    const category = document.getElementById('category').value;
    const description = document.getElementById('description').value;

    try {
        const response = await fetch('/api/complaints', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                studentUsername: user.username,
                category,
                description
            }),
        });

        if (response.ok) {
            alert('Complaint submitted successfully');
            loadComplaints();
        } else {
            alert('Failed to submit complaint');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Failed to submit complaint');
    }
}

async function loadComplaints() {
    const user = checkAuth();
    if (!user || user.role !== 'STUDENT') {
        window.location.href = '/login.html';
        return;
    }
    try {
        const response = await fetch(`/api/complaints/student/${user.username}`);
        const complaints = await response.json();
        displayComplaints(complaints);
    } catch (error) {
        console.error('Error:', error);
        alert('Failed to load complaints');
    }
}

function displayComplaints(complaints) {
    const complaintsContainer = document.getElementById('complaints-list');
    complaintsContainer.innerHTML = complaints.map(complaint => `
        <div class="complaint-item ${complaint.status.toLowerCase()}">
            <h3>${complaint.category}</h3>
            <p>${complaint.description}</p>
            <p>Status: ${complaint.status}</p>
            <p>Created: ${new Date(complaint.createdAt).toLocaleDateString()}</p>
            ${complaint.workerUsername ? `<p>Assigned to: ${complaint.workerUsername}</p>` : ''}
            ${complaint.workerComments ? `
                <div class="worker-comments">
                    <h4>Worker Notes:</h4>
                    <p>${complaint.workerComments}</p>
                </div>
            ` : ''}
            ${complaint.resolvedAt ? 
                `<p>Resolved: ${new Date(complaint.resolvedAt).toLocaleDateString()}</p>` : ''}
            <p class="last-updated">Last Updated: ${new Date(complaint.lastUpdated || complaint.createdAt).toLocaleString()}</p>
        </div>
    `).join('');
}
