async function login(event) {
    event.preventDefault();
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, password }),
        });

        const data = await response.json();
        if (response.ok) {
            localStorage.setItem('user', JSON.stringify(data));
            window.location.href = `/${data.role.toLowerCase()}-dashboard.html`;
        } else {
            alert(data.message || 'Login failed');
        }
    } catch (error) {
        console.error('Login error:', error);
        alert('Login failed');
    }
}

async function register(event) {
    event.preventDefault();
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const role = document.getElementById('role').value;
    const workerCategoryElement = document.getElementById('workerCategory');
    const workerCategory = (role === 'WORKER' && workerCategoryElement) ? 
        workerCategoryElement.value : null;

    try {
        const response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ 
                username, 
                password, 
                role,
                workerCategory 
            }),
        });

        const data = await response.json();
        if (!response.ok) {
            throw new Error(data.message || 'Registration failed');
        }

        alert('Registration successful! Please login.');
        window.location.href = '/login.html';
    } catch (error) {
        console.error('Registration error:', error);
        alert(error.message);
    }
}

function checkAuth() {
    const user = JSON.parse(localStorage.getItem('user') || 'null');
    if (!user) {
        window.location.href = '/login.html';
        return null;
    }
    return user;
}

function logout() {
    localStorage.removeItem('user');
    window.location.href = '/login.html';
}

function displayUserInfo() {
    const user = JSON.parse(localStorage.getItem('user') || 'null');
    if (user) {
        const userElement = document.getElementById('currentUser');
        if (userElement) {
            userElement.textContent = user.username;
        }
    }
}

// Update existing DOMContentLoaded listeners in each dashboard
document.addEventListener('DOMContentLoaded', () => {
    displayUserInfo();
    // ... other initialization code ...
});
