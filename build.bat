
if exist ".\bin" rmdir "bin" /q /s
mkdir "bin"
cd src
for /D %%G in (".\*") do (
mkdir  "..\bin\%%G"
cd %%G
javac *.java 
move *.class  "..\..\bin\%%G"
cd ..
)
cd..
cd bin
jar -cvfe server.jar server.Server server\*.class
jar -cvfe player.jar reversi.Player reversi\*.class ..\res
cd..
