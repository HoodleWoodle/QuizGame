@ECHO OFF
MODE CON COLS=60 LINES=12
title QuizGame - Server
ECHO.
ECHO  QuizGame is coded by  : "Alex, Eric, Quirin, Stefan"
ECHO  Questions are made of : "unknown Guys"
ECHO.
ECHO  If the Server is closed, Matches and Match-Requests are
ECHO  NOT saved. Only Accounts and their score will be available
ECHO  after restarting.
ECHO.

SET /P port=#PORT: 

ECHO.
ECHO  TODO start the Server (Port:%port%)
#java -jar Server.jar -%port%

PAUSE>NUL