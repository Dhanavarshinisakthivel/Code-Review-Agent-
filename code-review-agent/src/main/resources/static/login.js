const usernameInput = document.getElementById("username");
const passwordInput = document.getElementById("password");
const loginBtn = document.getElementById("loginBtn");
const loginError = document.getElementById("loginError");

// If the user is already logged in, skip the login page entirely
window.addEventListener("DOMContentLoaded", async () => {
  try {
    const res = await fetch("/api/session");
    if (res.ok) {
      window.location.href = "home.html";
    }
  } catch (err) {
    // ignore - just means we can't tell yet, let them log in normally
  }
});

loginBtn.addEventListener("click", handleLogin);

// Let pressing Enter in the password field submit the form too
passwordInput.addEventListener("keydown", (e) => {
  if (e.key === "Enter") handleLogin();
});

async function handleLogin() {
  const username = usernameInput.value.trim();
  const password = passwordInput.value;

  loginError.textContent = "";

  if (!username || !password) {
    loginError.textContent = "Please enter both username and password.";
    return;
  }

  loginBtn.disabled = true;

  try {
    const response = await fetch("/api/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, password })
    });

    if (!response.ok) {
      const data = await response.json();
      throw new Error(data.message || "Login failed.");
    }

    // Success - go to the home/about page
    window.location.href = "home.html";

  } catch (err) {
    loginError.textContent = err.message;
  } finally {
    loginBtn.disabled = false;
  }
}
