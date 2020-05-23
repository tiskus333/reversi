
if exist ".\bin" rmdir "bin" /q /s
mkdir "bin"
for /D %%G in ("src\*") do (
mkdir  "bin\%%G"
cd %%G
javac -d ..\..\bin -target 8 -source 8 *.java 
cd ..\..
)
cd bin
jar -cvfe server.jar src.server.Server src\server\*.class
jar -cvfe player.jar src.reversi.Player src\reversi\*.class ..\res
cd..
