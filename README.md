![](http://ruhr-universitaet-bochum.github.io/jpdfsigner/images/rub_dez6_abt3_logo.png)
  

# JPDFSigner
#### PDF-Signatur in Webanwendungen per Smartcard


[![](http://ruhr-universitaet-bochum.github.io/jpdfsigner/images/screencast_thumb.png)](https://www.youtube.com/watch?v=FxFfvprwBOY "Screencast")

## Einleitung
Mit dem JPDFSigner-Javaapplet können einzelne oder mehrere PDF-Dokumente per Smartcard signiert werden. Da es sich um ein Javaapplet handelt, kann es in jede Webapplikation eingebunden und parametrisiert werden. Anhand der Parameter wird anschließend das PDF-Dokument geladen und angezeigt. Nach erfolgreich durchgeführter Signatur, wird die PDF auf einen Zielserver geladen und der Browser, auf eine Ergebnisseite weitergeleitet.

Das JPDFSigner-Javaapplet ist eine Eigenentwicklung der Ruhr-Universität Bochum und findet gegenwärtig ihren Einsatz innerhalb der Universität. Trotz zuverlässiger Funktionalität wurde die Weiterentwicklung dieser Version eingestellt. Geplant ist eine Neuentwicklung (JPDFSigner 2) auf Basis folgender Projekte:

* RUB Style Java Look&Feel
* RUB EasyP11
* RUB EasyAPDU
* RUB CardCertificateManager Applet Framework


(Diese Projekte werden bald auf GitHub veröffentlicht)
  
Das Applet ist funktionsfähig, beinhaltet jedoch zahlreiche proprietäre Implementierungen (Design, Texte mit Bezug auf die Ruhr-Universität etc.), die nicht konfigurierbar sondern fest einkompiliert sind. Hierfür muss ggf. der Quelltext angepasst und das Applet neu kompiliert werden. Der Sinn der Veröffentlichung dieses Projektes liegt im Wesentlichen darin, den Quellcode als offene Codereferenz zur Verfügung zu stellen. Der Code ist relativ straight-forward und bietet unter Anderem folgende Implementierungen:

* PDF Signatur mit (DFN-)Zeitstempel via iText & Bouncycastle
* Signaturverifizierung
* HTTP Requests unter Java
* PDF Rendering unter Java
* Look&Feel-Implementierungen mit Swing
* PKCS11 unter Java (SunPKCS11)

## Möglicher Use-Case: Antragsverwaltung
Folgender typischer abstrakter Workflow soll das Einsatzszenario des JPDFSigners deutlich machen. Die PDF trägt dabei neben der optischen Repräsentation des Dokuments zusätzliche Nutzdaten in XML-Form, die zur elektronischen Weiterverarbeitung benötigt werden.
![](http://ruhr-universitaet-bochum.github.io/jpdfsigner/images/workflow.png)

1. Mitarbeiter signiert Antrag mit personalisierter Smartcard.
2. Signierter Antrag wird in der Datenbank der Antragsverwaltung gespeichert oder dem Workflow hinzugefügt.
3. Vorgesetzter wird über den Antrag des Mitarbeiters informiert (z.B. automatisiert per Mail oder durch Workflowengine), prüft und signiert diesen wiederum mit seiner personalisierten Smartcard.
4. Zweifach signiertes PDF-Dokument wird in der Antragsverwaltung gespeichert bzw. dem Workflow zurückgeführt.
5. Verwaltungsmitarbeiter erhält Benachrichtigung über neuen Antrag. Dieser wird von einem Verwaltungsangestellten bearbeitet, geprüft und signiert.
6. XML-Nutzdaten werden aus der PDF extrahiert um weitere automatisierte Prozesse zu starten (z.B. Aktualisierung des Urlaubskontos oder initiieren von Verrechnungsprozessen). 
7. Antrag mit allen Signaturen wird zurück an den Antragsteller geschickt.

## Kompilieren
Das Projekt lässt sich nur unter dem Oracle/Sun JDK >= 1.6 kompilieren weil die SunPKCS11 API verwendet wird. Diese ist ausschließlich im Oracle/Sun JDK vorhanden.

Die folgenden Schritte wurden unter Linux Mint 14, git 1.7.10.4
und maven 2.2.1 getestet.

### Linux
1. Git installieren

> `# sudo apt-get install git`

2. Maven installieren

> `# apt-get install maven`

3. Projektverzeichnis anlegen

> `# mkdir ~/jpdfsigner`

4. Projekt herunterladen

> `# cd ~/jpdfsigner`  <br />
> `# git clone https://github.com/ruhr-universitaet-bochum/jpdfsigner`

5. In das Arbeitsverzeichnis wechseln

	> `# cd ./jpdfsigner`

6. Projekt kompilieren

	> `# mvn install`

### Windows


1. Git installieren

> [Git for Windows](http://msysgit.github.io)

2. Maven installieren

> [Maven on Windows](http://maven.apache.org/guides/getting-started/windows-prerequisites.html)

3. Projektverzeichnis anlegen

> `# cd C:\jpdfsigner`

4. Projekt herunterladen

> `# cd C:\jpdfsigner`  <br />
> `# git clone https://github.com/ruhr-universitaet-bochum/jpdfsigner`

5. In das Arbeitsverzeichnis wechseln

	> `# cd ./jpdfsigner`

6. Projekt kompilieren

	> `# mvn install`

#### Ausführen:

> `# java -jar ./target/jpdfsigner-1.2.0-Release-jar-with-dependencies.jar`

#### Projekt clean (entfernt das Maven target-Verzeichnis):

> `# mvn clean`

## Dokumentation
Das Benutzerhandbuch befindet sich unter „jpdfsigner12RC_userdocumentation.doc“ im Stammverzeichnis des Master-Branchs.

Quellcodekommentare sowie JavaDoc-Dokumentation fehlen nahezu komplett und werden durch die Einstellung des Projektes nicht mehr nach gepflegt.

## PKCS11 Middleware
Die PKCS11 Middleware ist eine Bibliothek, die zahlreiche Funktionen für Kryptographie-Token wie z.B. Smartcards bereit stellt. Diese Bibliothek unterliegt dem PKCS#11-Standard und bietet somit eine standardisierte Schnittstelle für Anwendungen, die Operationen auf Kryptografie-Token durchführen. Es gibt verschiedene Hersteller die PKCS11-Bibliotheken anbieten. Einige davon sind OpenSC, CryptoVision und OpenLimit wobei OpenSC kostenfrei ist.


JPDFSigner verwendet diese PKCS11 Schnittstelle und benötigt deswegen eine PKCS11 Middleware. DIE MIDDLEWARE WIRD NICHT MIT DEM JPDFSigner APPLET AUSGELIEFERT UND MUSS SELBER BEZOGEN WERDEN. Für welche Middleware man sich entscheidet hängt davon ab, welchen Smartcard-Typen man bedienen möchte und ob man sich für eine opensource oder kommerzielle Version entscheidet.


Wenn man sich dann für eine entsprechende Middleware entschieden hat, muss diese an das Applet "angestöpselt" werden. Dies passiert einmalig in den Einstellungen des JPDFSigner-Applets. Das bedeutet: Jeder Nutzer der das Applet über eine Webanwendung startet um PDFs zu signieren, muss diese Middleware installieren und im Applet einmalig konfigurieren. Näheres hierzu kann in der Benutzerdokumentation nachgelesen werden.

## Bibliotheken
JPDFSigner verwendet folgende Bibliotheken

- Log4J 1.2.15 - ***Apache License v. 2.0***
- Bouncycastle 1.45 - ***X11 (MIT) License***
	- bcmail-jdk16
	- bcprov-jdk16
	- bctsp-jdk16
- iText 5.0.6 - ***AGPL License***
- JDom 1.1 - ***Apache License v. 2.0 (without acknowledgment clause)***
- PDF Renderer - ***LGPL License v. 2.1***
- rsyntaxtextarea - ***BSD (modified → see developer website)***
- commons-logging 1.1.1 - ***Apache License v. 2.0***
- ICEpdf 4.3.0 - ***Apache License v. 2.0***

## Bei Fragen ...
... zum Quellcode oder zur Nutzung: kurze Mail an daniel.moczarski [ a t ] uv.ruhr-uni-bochum.de