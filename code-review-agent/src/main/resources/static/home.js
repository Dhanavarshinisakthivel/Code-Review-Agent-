const navUsername = document.getElementById("navUsername");
const logoutBtn = document.getElementById("logoutBtn");
const startBtn = document.getElementById("startBtn");
const startBtn2 = document.getElementById("startBtn2");

// Check session on load; if not logged in, the AuthFilter on the backend
// would already have redirected us, but this also updates the nav bar text.
window.addEventListener("DOMContentLoaded", async () => {
  try {
    const res = await fetch("/api/session");
    if (!res.ok) {
      window.location.href = "login.html";
      return;
    }
    const data = await res.json();
    navUsername.textContent = `Logged in as ${data.username}`;
  } catch (err) {
    window.location.href = "login.html";
  }
});

logoutBtn.addEventListener("click", async () => {
  await fetch("/api/logout", { method: "POST" });
  window.location.href = "login.html";
});

function goToReview() {
  window.location.href = "review.html";
}

startBtn.addEventListener("click", goToReview);
startBtn2.addEventListener("click", goToReview);
