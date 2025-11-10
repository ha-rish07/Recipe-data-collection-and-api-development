
const apiBase = '/api/recipes';
let currentPage = 1;

async function load(page = 1) {
  currentPage = page;
  const perPage = document.getElementById('perPage').value || 15;
  const title = document.getElementById('titleFilter').value.trim();
  const cuisine = document.getElementById('cuisineFilter').value.trim();

  try {
    let obj;

    // 🔍 Filtered search
    if (title || cuisine) {
      const params = new URLSearchParams();
      if (title) params.set('title', title);
      if (cuisine) params.set('cuisine', cuisine);

      const res = await fetch(`${apiBase}/search?${params.toString()}`);
      obj = await res.json();

      renderTable(obj.data);
      renderPagination(1, 1);
    } 
    // 📄 Regular paginated list
    else {
      const res = await fetch(`${apiBase}?page=${page}&limit=${perPage}`);
      obj = await res.json();

      renderTable(obj.data);
      renderPagination(obj.page, Math.ceil(obj.total / obj.limit || 1));
    }
  } catch (err) {
    console.error('Error loading recipes:', err);
    // keep UX friendly but non-blocking
    const tbody = document.querySelector('#recipesTable tbody');
    tbody.innerHTML = '<tr><td colspan="5">Failed to load recipes</td></tr>';
  }
}

// 🧾 Render Table
function renderTable(items) {
  const tbody = document.querySelector('#recipesTable tbody');
  tbody.innerHTML = '';

  if (!items || items.length === 0) {
    tbody.innerHTML = '<tr><td colspan="5">No results found</td></tr>';
    return;
  }

  items.forEach(it => {
    const tr = document.createElement('tr');
    // use camelCase fields returned by the API (totalTime, prepTime, cookTime, nutrients)
    tr.innerHTML = `
      <td title="${it.title}">${truncate(it.title, 50)}</td>
      <td>${it.cuisine || ''}</td>
      <td>${renderStars(it.rating)}</td>
      <td>${it.totalTime !== undefined && it.totalTime !== null ? it.totalTime : ''}</td>
      <td>${it.serves || ''}</td>
    `;
    tr.addEventListener('click', () => showDetail(it));
    tbody.appendChild(tr);
  });
}

// ⭐ Rating Stars
function renderStars(r) {
  if (r === null || r === undefined) return '';
  // show filled stars by rounded integer and rating with one decimal
  const full = Math.round(r);
  const ratingStr = (Math.round(r * 10) / 10).toFixed(1);
  return `<span class="star">${'★'.repeat(full)}</span> ${ratingStr}`;
}

// ✂️ Truncate Long Text
function truncate(str, n) {
  if (!str) return '';
  return str.length > n ? str.substring(0, n - 1) + '…' : str;
}

// 📄 Pagination
function renderPagination(page, totalPages) {
  const div = document.getElementById('pagination');
  div.innerHTML = '';

  if (totalPages <= 1) return;

  const prev = document.createElement('button');
  prev.textContent = 'Prev';
  prev.disabled = page <= 1;
  prev.onclick = () => load(page - 1);

  const next = document.createElement('button');
  next.textContent = 'Next';
  next.disabled = page >= totalPages;
  next.onclick = () => load(page + 1);

  div.appendChild(prev);
  div.appendChild(document.createTextNode(` Page ${page} of ${totalPages} `));
  div.appendChild(next);
}

// 📜 Drawer Details
function showDetail(it) {
  document.getElementById('drawerTitle').textContent = `${it.title} — ${it.cuisine || ''}`;
  const body = document.getElementById('drawerBody');

  let html = `
    <p><strong>Description:</strong><br/>${it.description || ''}</p>
    <p><strong>Total Time:</strong> ${it.totalTime != null ? it.totalTime : ''} 
      <button id="expandTimes">Expand</button>
      <div id="timesDetail" style="display:none;">
        <p>Prep Time: ${it.prepTime != null ? it.prepTime : ''}</p>
        <p>Cook Time: ${it.cookTime != null ? it.cookTime : ''}</p>
      </div>
    </p>
  `;

  html += `<h3>Nutrition</h3>`;
  const n = it.nutrients || {};
  html += `
    <table class="nutr-table">
      <tr><th>Calories</th><th>Carbs</th><th>Cholesterol</th></tr>
      <tr><td>${n.calories || ''}</td><td>${n.carbohydrateContent || n.carbs || ''}</td><td>${n.cholesterolContent || ''}</td></tr>
    </table>
  `;

  body.innerHTML = html;
  document.getElementById('drawer').classList.remove('hidden');

  document.getElementById('expandTimes').addEventListener('click', () => {
    const d = document.getElementById('timesDetail');
    d.style.display = d.style.display === 'none' ? 'block' : 'none';
  });
}

// 🔘 Event Listeners
document.getElementById('searchBtn').addEventListener('click', () => load(1));
document.getElementById('closeDrawer').addEventListener('click', () =>
  document.getElementById('drawer').classList.add('hidden')
);

// 🚀 Initial Load
load(1).catch(console.error);
