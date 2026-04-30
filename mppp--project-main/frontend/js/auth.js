document.addEventListener("DOMContentLoaded", () => {
    // Check login state
    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
    
    // Find nav button
    const navCta = document.getElementById('navGetStartedBtn');
    
    // If we're logged in and the nav button exists, change it to "My Profile"
    if (navCta && isLoggedIn) {
        navCta.innerHTML = '<i class="fa-solid fa-user"></i> My Profile';
        navCta.href = 'dashboard.html';
        
        // Optional: give it a slightly different styling to indicate logged-in state
        navCta.style.background = 'rgba(249, 115, 22, 0.1)';
        navCta.style.borderColor = 'var(--primary)';
        navCta.style.color = 'var(--primary)';
    }

    // Bind login form if it exists
    const loginForm = document.getElementById('loginForm');
    if (loginForm && window.location.pathname.includes('login.html')) {
        loginForm.onsubmit = async (e) => {
            e.preventDefault();
            
            const email = document.getElementById('loginEmail').value;
            const password = document.getElementById('loginPassword').value;
            
            try {
                const response = await fetch('http://localhost:8080/api/users/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email, password })
                });

                if (response.ok) {
                    const userData = await response.json();
                    localStorage.setItem('isLoggedIn', 'true');
                    localStorage.setItem('username', userData.name); // Using username/name
                    window.location.href = 'index.html';
                } else {
                    alert('Login failed. Please check your credentials.');
                }
            } catch (err) {
                console.error(err);
                alert('Could not connect to the backend server.');
            }
        };
    }

    // Bind register form if it exists
    const registerForm = document.getElementById('registerForm');
    if (registerForm && window.location.pathname.includes('register.html')) {
        registerForm.onsubmit = async (e) => {
            e.preventDefault();
            
            const firstName = document.getElementById('regFirstName').value;
            const lastName = document.getElementById('regLastName').value;
            const email = document.getElementById('regEmail').value;
            const password = document.getElementById('regPassword').value;
            
            const fullName = `${firstName} ${lastName}`.trim();
            
            try {
                const response = await fetch('http://localhost:8080/api/users', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ name: fullName, email: email, password: password })
                });

                if (response.ok) {
                    alert('Registration successful! Please login.');
                    window.location.href = 'login.html';
                } else {
                    alert('Registration failed. Email might already exist.');
                }
            } catch (err) {
                console.error(err);
                alert('Could not connect to the backend server.');
            }
        };
    }

    // Bind logout button if it exists (in dashboard)
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', (e) => {
            e.preventDefault();
            localStorage.setItem('isLoggedIn', 'false');
            window.location.href = 'index.html';
        });
    }
});
