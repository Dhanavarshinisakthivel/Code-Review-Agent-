// Grab references to all the HTML elements we need to work with
const reviewBtn = document.getElementById("reviewBtn");
const codeInput = document.getElementById("code");
const languageSelect = document.getElementById("language");
const errorMsg = document.getElementById("errorMsg");
const loading = document.getElementById("loading");
const resultSection = document.getElementById("resultSection");

const scoreValue = document.getElementById("scoreValue");
const summaryText = document.getElementById("summaryText");
const strengthsList = document.getElementById("strengthsList");
const issuesList = document.getElementById("issuesList");

const navUsername = document.getElementById("navUsername");
const logoutBtn = document.getElementById("logoutBtn");

// Show who's logged in when the page loads (also acts as an extra safety
// check - if the session expired, send the user back to the login page)
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

reviewBtn.addEventListener("click", async () => {
  const code = codeInput.value.trim();
  const language = languageSelect.value;

  errorMsg.textContent = "";
  resultSection.classList.add("hidden");

  if (!code) {
    errorMsg.textContent = "Please paste some code first.";
    return;
  }

  loading.classList.remove("hidden");
  reviewBtn.disabled = true;

  try {
    // Call our own backend, which then talks to the AI
    const response = await fetch("/api/review", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ code, language })
    });

    if (response.status === 401) {
      window.location.href = "login.html";
      return;
    }

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || "Something went wrong.");
    }

    const result = await response.json();
    displayResult(result);

  } catch (err) {
    errorMsg.textContent = err.message;
  } finally {
    loading.classList.add("hidden");
    reviewBtn.disabled = false;
  }
});

function displayResult(result) {
  scoreValue.textContent = result.overallScore;
  summaryText.textContent = result.summary;

  // Fill strengths list
  strengthsList.innerHTML = "";
  (result.strengths || []).forEach(strength => {
    const li = document.createElement("li");
    li.textContent = strength;
    strengthsList.appendChild(li);
  });

  // Fill issues list
  issuesList.innerHTML = "";
  if (!result.issues || result.issues.length === 0) {
    issuesList.innerHTML = "<p>No issues found. Nice work!</p>";
  } else {
    result.issues.forEach(issue => {
      const card = document.createElement("div");
      card.className = `issue-card ${issue.severity}`;

      const lineText = issue.line > 0 ? ` (Line ${issue.line})` : "";

      card.innerHTML = `
        <div class="issue-header">[${issue.severity}] ${issue.category}${lineText}</div>
        <div>${issue.description}</div>
        <div class="issue-suggestion">💡 ${issue.suggestion}</div>
      `;
      issuesList.appendChild(card);
    });
  }

  resultSection.classList.remove("hidden");
}
