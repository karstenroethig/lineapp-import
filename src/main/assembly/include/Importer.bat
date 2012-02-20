@echo off
setlocal ENABLEDELAYEDEXPANSION
if defined DMPCP (set DMPCP=%DMPCP%;.) else (set DMPCP=.)
FOR /R .\lib %%G IN (*.jar) DO set DMPCP=!DMPCP!;%%G

rem echo The Classpath definition is %DMPCP%

java -Xmx256m -cp "%DMPCP%" karstenroethig.lineapp.Importer %1

PAUSE