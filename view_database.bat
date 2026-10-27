@echo off
title Hostel Manager - Database Viewer
echo ========================================================
echo    HOSTEL MANAGER - SQLITE DATABASE WEB VIEWER
echo ========================================================
echo.
echo Pulling latest database from phone...
python -c "
import subprocess, os
ADB = r'C:\Users\jaykc\AppData\Local\Android\Sdk\platform-tools\adb.exe'
try:
    res = subprocess.run([ADB, 'exec-out', 'run-as', 'com.hostel.manager', 'cat', '/data/data/com.hostel.manager/databases/hostel_manager.db'], capture_output=True)
    if len(res.stdout) > 1000:
        with open('hostel_manager.db', 'wb') as f:
            f.write(res.stdout)
        print('Live database successfully synced from phone!')
    else:
        print('Phone not responding; using existing local database.')
except Exception as e:
    print('Sync note: Using current local database file.')
"
echo.
echo Launching Web Viewer in your browser at http://localhost:8085 ...
python view_db_server.py
pause
