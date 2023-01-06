@echo off

:: Création des dossiers bin et donnees s'ils n'existent pas ::
IF NOT EXIST "./bin/"         ( mkdir "./bin" )
IF NOT EXIST "./bin/donnees/" ( mkdir "./bin/donnees/" )

XCOPY "./donnees" "./bin/donnees" /E /Y >NUL

echo Compilation...
call javac -cp jdom.jar -encoding utf8 "@compile.list" && ( echo Lancement du programme... & call java -cp "jdom.jar;./bin;./bin/donnees;" controleur.Controleur && echo Fin de l'execution. || echo. & echo Erreur d'EXECUTION. ) || echo Erreur de COMPILATION.

goto :eof
