import sqlite3

conn = sqlite3.connect('hostel_manager.db')
c = conn.cursor()
c.execute("SELECT name FROM sqlite_master WHERE type='table';")
print('Tables in hostel_manager.db:', [r[0] for r in c.fetchall() if not r[0].startswith('android')])

for table in ['users', 'rooms', 'complaints', 'fees']:
    c.execute(f"SELECT COUNT(*) FROM {table}")
    count = c.fetchone()[0]
    print(f"\n--- Table: {table} ({count} records) ---")
    c.execute(f"PRAGMA table_info({table})")
    cols = [col[1] for col in c.fetchall()]
    print("Columns:", " | ".join(cols))
    c.execute(f"SELECT * FROM {table}")
    for row in c.fetchall():
        print("  ->", row)

conn.close()
