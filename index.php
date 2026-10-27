<?php
// Hostel Manager — PHP Database Viewer (Compatible with XAMPP Apache)
$dbFile = __DIR__ . '/hostel_manager.db';
$adbPath = 'C:\\Users\\jaykc\\AppData\\Local\\Android\\Sdk\\platform-tools\\adb.exe';

if (isset($_GET['sync'])) {
    if (file_exists($adbPath)) {
        $raw = @shell_exec("\"$adbPath\" exec-out run-as com.hostel.manager cat databases/hostel_manager.db");
        if ($raw && strlen($raw) > 1024) {
            file_put_contents($dbFile, $raw);
        }
    }
    $targetTable = isset($_GET['table']) ? urlencode($_GET['table']) : '';
    header("Location: index.php" . ($targetTable ? "?table=$targetTable" : ""));
    exit;
}

if (!file_exists($dbFile)) {
    // Attempt auto pull
    if (file_exists($adbPath)) {
        $raw = @shell_exec("\"$adbPath\" exec-out run-as com.hostel.manager cat databases/hostel_manager.db");
        if ($raw && strlen($raw) > 1024) {
            file_put_contents($dbFile, $raw);
        }
    }
}

if (!file_exists($dbFile)) {
    die("Database file not found. Ensure USB debugging is active and the app has launched at least once.");
}

$pdo = new PDO('sqlite:' . $dbFile);
$pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

// Get list of tables
$tablesStmt = $pdo->query("SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%' AND name NOT LIKE 'android_%'");
$tables = $tablesStmt->fetchAll(PDO::FETCH_COLUMN);

$currentTable = isset($_GET['table']) && in_array($_GET['table'], $tables) ? $_GET['table'] : ($tables[0] ?? '');

// Fetch table columns and data
$columns = [];
$rows = [];
if ($currentTable) {
    $colStmt = $pdo->query("PRAGMA table_info($currentTable)");
    $columns = $colStmt->fetchAll(PDO::FETCH_COLUMN, 1);

    $dataStmt = $pdo->query("SELECT * FROM $currentTable");
    $rows = $dataStmt->fetchAll(PDO::FETCH_ASSOC);
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Hostel Manager — phpMyAdmin Style Database Viewer</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Arial, sans-serif; }
        body { background-color: #f1f5f9; color: #1e293b; display: flex; flex-direction: column; height: 100vh; }
        header { background: #1e3a8a; color: white; padding: 14px 24px; display: flex; align-items: center; justify-content: space-between; }
        .logo-area h1 { font-size: 19px; font-weight: 700; }
        .logo-area span { background: #3b82f6; font-size: 11px; padding: 3px 8px; border-radius: 6px; text-transform: uppercase; }
        .main-container { display: flex; flex: 1; overflow: hidden; }
        aside { width: 260px; background: #ffffff; border-right: 1px solid #e2e8f0; }
        .aside-header { padding: 16px; border-bottom: 1px solid #e2e8f0; font-size: 12px; font-weight: 700; color: #64748b; text-transform: uppercase; }
        .table-list { list-style: none; }
        .table-item { display: block; padding: 12px 18px; border-bottom: 1px solid #f8fafc; text-decoration: none; color: #334155; font-size: 14px; font-weight: 500; }
        .table-item:hover { background: #f8fafc; color: #2563eb; }
        .table-item.active { background: #eff6ff; color: #1e40af; font-weight: 700; border-left: 4px solid #2563eb; }
        content { flex: 1; display: flex; flex-direction: column; overflow: hidden; }
        .content-header { background: #ffffff; padding: 16px 24px; border-bottom: 1px solid #e2e8f0; }
        .table-wrapper { flex: 1; overflow: auto; padding: 20px; }
        table.db-table { width: 100%; border-collapse: collapse; background: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.05); }
        table.db-table th { background: #1e293b; color: #f8fafc; padding: 11px 14px; text-align: left; font-size: 12px; text-transform: uppercase; position: sticky; top: 0; }
        table.db-table td { padding: 10px 14px; border-bottom: 1px solid #e2e8f0; font-size: 13px; }
        table.db-table tr:hover { background-color: #f1f5f9; }
    </style>
</head>
<body>
    <header>
        <div class="logo-area" style="display:flex; align-items:center; gap:12px;">
            <h1>🏢 Hostel Manager Database</h1>
            <span>SQLite3 / XAMPP Viewer</span>
        </div>
        <div>
            <a href="?sync=1&table=<?= urlencode($currentTable) ?>" style="background:#10b981; color:white; padding:8px 16px; border-radius:6px; text-decoration:none; font-weight:600; font-size:13px; display:inline-flex; align-items:center; gap:6px; box-shadow:0 1px 3px rgba(0,0,0,0.2);">
                🔄 Sync Live Data From Phone
            </a>
        </div>
    </header>

    <div class="main-container">
        <aside>
            <div class="aside-header">Tables in hostel_manager.db</div>
            <ul class="table-list">
                <?php foreach ($tables as $t): ?>
                    <li>
                        <a href="?table=<?= htmlspecialchars($t) ?>" class="table-item <?= $t === $currentTable ? 'active' : '' ?>">
                            📋 <?= htmlspecialchars($t) ?>
                        </a>
                    </li>
                <?php endforeach; ?>
            </ul>
        </aside>

        <content>
            <div class="content-header">
                <h2>Table: <?= htmlspecialchars($currentTable) ?></h2>
                <div style="font-size:13px; color:#64748b; margin-top:4px;">Total records: <?= count($rows) ?> rows</div>
            </div>

            <div class="table-wrapper">
                <table class="db-table">
                    <thead>
                        <tr>
                            <?php foreach ($columns as $col): ?>
                                <th><?= htmlspecialchars($col) ?></th>
                            <?php endforeach; ?>
                        </tr>
                    </thead>
                    <tbody>
                        <?php if (empty($rows)): ?>
                            <tr><td colspan="<?= count($columns) ?>" style="text-align:center; padding:20px;">No records found.</td></tr>
                        <?php else: ?>
                            <?php foreach ($rows as $row): ?>
                                <tr>
                                    <?php foreach ($columns as $col): ?>
                                        <td><?= htmlspecialchars($row[$col] ?? 'NULL') ?></td>
                                    <?php endforeach; ?>
                                </tr>
                            <?php endforeach; ?>
                        <?php endif; ?>
                    </tbody>
                </table>
            </div>
        </content>
    </div>
</body>
</html>
