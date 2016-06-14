@ECHO OFF

IF "%1" == "/a" GOTO full

ECHO Cleaning...
IF EXIST "%TEMP%\ImageResourceGenerator*" DEL "%TEMP%\ImageResourceGenerator*" /F /Q
IF EXIST "%TEMP%\uiBinder*" DEL "%TEMP%\uiBinder*" /F /Q
IF EXIST "%TEMP%\gwt*" DEL "%TEMP%\gwt*" /F /Q
FOR /D /R %TEMP% %%x IN (gwt*) DO RMDIR /S /Q "%%x"
GOTO end

:full
ECHO Full Clean
FOR /D /R %TEMP% %%x IN (*) DO RMDIR /S /Q "%%x"
DEL "%TEMP%\*" /F /Q
GOTO end

:end
ECHO.
ECHO Done.
