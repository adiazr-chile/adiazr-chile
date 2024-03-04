REM crear usuario 'femase'

REM crear base de datos vacia para luego hacer el restore en ella
createdb -U postgres restored-db --template=template0

"pg_restore.exe" -U postgres -d restored-db < "C:\paso\femasePROD_backup20240303225055.dump"