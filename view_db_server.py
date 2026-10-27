import http.server
import socketserver
import sqlite3
import json
import subprocess
import os
import webbrowser
from urllib.parse import urlparse, parse_qs

PORT = 8085
DB_PATH = os.path.join(os.path.dirname(__file__), "hostel_manager.db")
ADB_PATH = r"C:\Users\jaykc\AppData\Local\Android\Sdk\platform-tools\adb.exe"

def sync_from_phone():
    try:
        cmd = [ADB_PATH, 'exec-out', 'run-as', 'com.hostel.manager', 'cat', '/data/data/com.hostel.manager/databases/hostel_manager.db']
        res = subprocess.run(cmd, capture_output=True)
        if len(res.stdout) > 1000:
            with open(DB_PATH, 'wb') as f:
                f.write(res.stdout)
            return True, f"Successfully synced {len(res.stdout)} bytes from connected phone!"
        else:
            return False, "Could not pull database from phone. Make sure phone is connected with USB Debugging enabled."
    except Exception as e:
        return False, str(e)

def get_db_data():
    if not os.path.exists(DB_PATH):
        sync_from_phone()

    data = {"tables": {}}
    if os.path.exists(DB_PATH):
        conn = sqlite3.connect(DB_PATH)
        c = conn.cursor()
        c.execute("SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%' AND name NOT LIKE 'android_%';")
        tables = [r[0] for r in c.fetchall()]

        for t in tables:
            c.execute(f"PRAGMA table_info({t});")
            cols = [col[1] for col in c.fetchall()]
            c.execute(f"SELECT * FROM {t};")
            rows = c.fetchall()
            data["tables"][t] = {
                "columns": cols,
                "rows": rows,
                "count": len(rows)
            }
        conn.close()
    return data

HTML_TEMPLATE = """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hostel Manager — SQLite Database Viewer (phpMyAdmin Style)</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Arial, sans-serif; }
        body { background-color: #f1f5f9; color: #1e293b; display: flex; flex-direction: column; height: 100vh; }
        header { background: #1e3a8a; color: white; padding: 14px 24px; display: flex; align-items: center; justify-content: space-between; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .logo-area { display: flex; align-items: center; gap: 12px; }
        .logo-area h1 { font-size: 19px; font-weight: 700; }
        .logo-area span { background: #3b82f6; font-size: 11px; font-weight: bold; padding: 3px 8px; border-radius: 6px; text-transform: uppercase; }
        .actions-area { display: flex; gap: 10px; }
        button.btn { background: #f59e0b; color: #000; border: none; padding: 8px 16px; font-weight: 700; font-size: 13px; border-radius: 6px; cursor: pointer; transition: 0.2s; display: flex; align-items: center; gap: 6px; }
        button.btn:hover { background: #d97706; color: white; }
        .main-container { display: flex; flex: 1; overflow: hidden; }
        aside { width: 260px; background: #ffffff; border-right: 1px solid #e2e8f0; display: flex; flex-direction: column; }
        .aside-header { padding: 16px; border-bottom: 1px solid #e2e8f0; font-size: 12px; font-weight: 700; color: #64748b; text-transform: uppercase; letter-spacing: 0.5px; }
        .table-list { list-style: none; overflow-y: auto; flex: 1; }
        .table-item { padding: 12px 18px; display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid #f8fafc; cursor: pointer; font-size: 14px; font-weight: 500; transition: 0.15s; }
        .table-item:hover { background: #f8fafc; color: #2563eb; }
        .table-item.active { background: #eff6ff; color: #1e40af; font-weight: 700; border-left: 4px solid #2563eb; }
        .badge { background: #e2e8f0; font-size: 11px; padding: 2px 7px; border-radius: 12px; color: #475569; font-weight: bold; }
        .table-item.active .badge { background: #bfdbfe; color: #1e40af; }
        content { flex: 1; display: flex; flex-direction: column; overflow: hidden; background: #f8fafc; }
        .content-header { background: #ffffff; padding: 16px 24px; border-bottom: 1px solid #e2e8f0; display: flex; align-items: center; justify-content: space-between; }
        .content-header h2 { font-size: 18px; color: #0f172a; }
        .content-header .meta { font-size: 13px; color: #64748b; margin-top: 2px; }
        .search-box { padding: 6px 12px; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 13px; width: 220px; }
        .table-wrapper { flex: 1; overflow: auto; padding: 20px; }
        table.db-table { width: 100%; border-collapse: collapse; background: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.05); }
        table.db-table th { background: #1e293b; color: #f8fafc; padding: 11px 14px; text-align: left; font-size: 12px; text-transform: uppercase; letter-spacing: 0.5px; border-bottom: 2px solid #0f172a; position: sticky; top: 0; }
        table.db-table td { padding: 10px 14px; border-bottom: 1px solid #e2e8f0; font-size: 13px; color: #334155; }
        table.db-table tr:hover { background-color: #f1f5f9; }
        .status-badge { display: inline-block; padding: 2px 8px; border-radius: 12px; font-size: 11px; font-weight: 700; }
        .status-pending { background: #fef3c7; color: #b45309; }
        .status-progress { background: #dbeafe; color: #1d4ed8; }
        .status-resolved, .status-paid { background: #d1fae5; color: #047857; }
        .status-unpaid { background: #fee2e2; color: #b91c1c; }
        .toast { position: fixed; bottom: 20px; right: 20px; background: #10b981; color: white; padding: 12px 20px; border-radius: 6px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); font-weight: 600; font-size: 14px; display: none; z-index: 999; }
    </style>
</head>
<body>
    <header>
        <div class="logo-area">
            <h1>Hostel Manager Database</h1>
            <span>SQLite Viewer</span>
        </div>
        <div class="actions-area">
            <button class="btn" onclick="syncFromDevice()">🔄 Sync Live from Phone</button>
        </div>
    </header>

    <div class="main-container">
        <aside>
            <div class="aside-header">Tables in hostel_manager.db</div>
            <ul class="table-list" id="tableList"></ul>
        </aside>

        <content>
            <div class="content-header">
                <div>
                    <h2 id="currentTableName">Table Viewer</h2>
                    <div class="meta" id="currentTableMeta">Showing table data</div>
                </div>
                <div>
                    <input type="text" id="searchInput" class="search-box" placeholder="Filter / Search records..." oninput="filterTable()">
                </div>
            </div>

            <div class="table-wrapper">
                <table class="db-table" id="dataTable">
                    <thead id="tableHead"></thead>
                    <tbody id="tableBody"></tbody>
                </table>
            </div>
        </content>
    </div>

    <div class="toast" id="toast">Database synced successfully!</div>

    <script>
        let dbData = {};
        let activeTable = 'users';

        async function loadData() {
            try {
                const res = await fetch('/api/data');
                dbData = await res.json();
                renderSidebar();
                renderTable(activeTable);
            } catch(e) {
                console.error(e);
            }
        }

        function renderSidebar() {
            const list = document.getElementById('tableList');
            list.innerHTML = '';
            const tables = Object.keys(dbData.tables || {});
            if (tables.length === 0) return;

            if (!tables.includes(activeTable)) {
                activeTable = tables[0];
            }

            tables.forEach(tableName => {
                const t = dbData.tables[tableName];
                const li = document.createElement('li');
                li.className = 'table-item' + (tableName === activeTable ? ' active' : '');
                li.innerHTML = `<span>📋 ${tableName}</span><span class="badge">${t.count}</span>`;
                li.onclick = () => {
                    activeTable = tableName;
                    document.querySelectorAll('.table-item').forEach(el => el.classList.remove('active'));
                    li.classList.add('active');
                    renderTable(tableName);
                };
                list.appendChild(li);
            });
        }

        function renderTable(tableName) {
            const t = dbData.tables[tableName];
            if (!t) return;

            document.getElementById('currentTableName').innerText = `Table: ${tableName}`;
            document.getElementById('currentTableMeta').innerText = `Total records: ${t.count} rows | Columns: ${t.columns.length}`;
            document.getElementById('searchInput').value = '';

            const thead = document.getElementById('tableHead');
            thead.innerHTML = '<tr>' + t.columns.map(c => `<th>${c}</th>`).join('') + '</tr>';

            renderRows(t.columns, t.rows);
        }

        function renderRows(columns, rows) {
            const tbody = document.getElementById('tableBody');
            if (rows.length === 0) {
                tbody.innerHTML = `<tr><td colspan="${columns.length}" style="text-align:center; padding: 24px; color: #94a3b8;">No records found in this table.</td></tr>`;
                return;
            }

            tbody.innerHTML = rows.map(row => {
                return '<tr>' + row.map((cell, idx) => {
                    let text = cell === null ? '<span style="color:#cbd5e1">NULL</span>' : String(cell);
                    // Check for status badges
                    if (columns[idx] === 'status' || columns[idx] === 'payment_status') {
                        let cls = 'status-pending';
                        if (text.toLowerCase() === 'resolved' || text.toLowerCase() === 'paid') cls = 'status-resolved';
                        else if (text.toLowerCase() === 'in progress') cls = 'status-progress';
                        else if (text.toLowerCase() === 'unpaid') cls = 'status-unpaid';
                        return `<td><span class="status-badge ${cls}">${text}</span></td>`;
                    }
                    if (columns[idx] === 'fee_per_sem' || columns[idx] === 'total_due' || columns[idx] === 'paid_amount') {
                        return `<td><strong>₹${Number(cell).toLocaleString('en-IN')}</strong></td>`;
                    }
                    return `<td>${text}</td>`;
                }).join('') + '</tr>';
            }).join('');
        }

        function filterTable() {
            const q = document.getElementById('searchInput').value.toLowerCase();
            const t = dbData.tables[activeTable];
            if (!t) return;
            const filtered = t.rows.filter(row => {
                return row.some(cell => String(cell).toLowerCase().includes(q));
            });
            renderRows(t.columns, filtered);
        }

        async function syncFromDevice() {
            const btn = document.querySelector('button.btn');
            btn.innerText = 'Syncing...';
            try {
                const res = await fetch('/api/sync', { method: 'POST' });
                const json = await res.json();
                showToast(json.message);
                await loadData();
            } catch(e) {
                showToast('Failed to sync: ' + e);
            } finally {
                btn.innerText = '🔄 Sync Live from Phone';
            }
        }

        function showToast(msg) {
            const toast = document.getElementById('toast');
            toast.innerText = msg;
            toast.style.display = 'block';
            setTimeout(() => { toast.style.display = 'none'; }, 3000);
        }

        loadData();
    </script>
</body>
</html>
"""

class RequestHandler(http.server.SimpleHTTPRequestHandler):
    def do_GET(self):
        url = urlparse(self.path)
        if url.path == "/" or url.path == "/index.html":
            self.send_response(200)
            self.send_header("Content-Type", "text/html; charset=utf-8")
            self.end_headers()
            self.wfile.write(HTML_TEMPLATE.encode("utf-8"))
        elif url.path == "/api/data":
            data = get_db_data()
            self.send_response(200)
            self.send_header("Content-Type", "application/json")
            self.end_headers()
            self.wfile.write(json.dumps(data).encode("utf-8"))
        else:
            self.send_error(404)

    def do_POST(self):
        url = urlparse(self.path)
        if url.path == "/api/sync":
            success, msg = sync_from_phone()
            self.send_response(200)
            self.send_header("Content-Type", "application/json")
            self.end_headers()
            self.wfile.write(json.dumps({"success": success, "message": msg}).encode("utf-8"))
        else:
            self.send_error(404)

def main():
    print(f"Starting Database Viewer Web Server at http://localhost:{PORT}")
    server = socketserver.TCPServer(("", PORT), RequestHandler)
    webbrowser.open(f"http://localhost:{PORT}")
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print("Server stopped.")

if __name__ == '__main__':
    main()
