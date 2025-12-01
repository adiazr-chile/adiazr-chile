@echo off
REM Cambia la ruta de Wildfly según tu instalación
set WILDFLY_HOME=C:\wildfly-36.0.1.Final

REM Cambia MyDS por el nombre de tu datasource
set DATASOURCE=RestoreDb

"%WILDFLY_HOME%\bin\jboss-cli.bat" --connect --commands="/subsystem=datasources/data-source=%DATASOURCE%/statistics=pool:read-resource(include-runtime=true)" > C:\paso\wildfly\ds_stats.log