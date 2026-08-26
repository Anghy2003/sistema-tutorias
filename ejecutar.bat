@echo off
chcp 65001 >nul
call mvn -q -DskipTests compile
java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp target/classes edu.uees.tutorias.App
