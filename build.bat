
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
cd server
jar cvfe server.jar server.Server *.class
cd..
cd reversi
jar cvfe player.jar reversi.Player *.class ..\..\res
cd..
cd..
