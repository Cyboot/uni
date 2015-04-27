Windows:
########
Unter Windows muss Eclipse mit dem JDK gestartet werden statt mit der normalen JRE
sonst kann Eclipse die tools.jar des JDK nicht finden und es kommt die Fehlermeldung:

"missing artifact jdk.tools"

Um Eclipse mit dem JDK zu starten, folgenden Eintrag in der eclipse.ini hinzufügen:
WICHTIG: Der Eintrag muss vor der Option -vmargs erstellt werden!

-vm
<Path to JDK>\bin\javaw.exe

Der Pfad muss dem Installationspfad des JAVA JDK entsprechen, also zum Beispiel:

-vm
C:\Program Files\Java\jdk1.6.0_45\bin\javaw.exe