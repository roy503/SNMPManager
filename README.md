# SNMPManager - Print Server version
Printer SNMP Manager

Get printer info from print server.

TO USE:
Export as runnable Jar
Make a Powershell File (Make sure you complete the print server pc name and path to jar):
  Get-PrinterPort -ComputerName (printserver) > "C:\Pathtojar\printserver.txt"
  
run via .bat file (make sure you complete the file path):
  @echo off
  Powershell -NoProfile -ExecutionPolicy Bypass -Command "& 'C:\pathto\powershellfile.ps1'"
  cd "C:\pathtojar"
  "%JAVA_HOME%\bin\java.exe" -jar "C:\pathtojar\SNMPManager.jar"

CURRENT:

set to SNMP V2C, if it fails it will use V1.
outputs printer info in csv format:
Location,IP,Name,Serial,Black,Yellow,Magenta,Cyan,K1,K2

I use the csv to populate a java servlet page on a tomcat server to view the data in the local network.


Might need to edit code a bit if you have colour / label / multi function printers.

Misc OIDs:

LaserJet P2015dn
1.3.6.1.2.1.43.11.1.1.8.1.1 (Maximum number of copies of toner)
1.3.6.1.2.1.43.11.1.1.9.1.1 (Remaining number of copies of toner)
1.3.6.1.2.1.43.12.1.1.4.1.1 (Toner color)
1.3.6.1.2.1.43.10.2.1.4.1.1 (Number of pages printed)
1.3.6.1.2.1.43.5.1.1.17.1 (Serial number of the printer)
1.3.6.1.2.1.43.5.1.1.16.1 (printer name(hostname))
1.3.6.1.2.1.1.6.0 (printer location)
1.3.6.1.2.1.1.5.0 (sysname)

1.3.6.1.2.1.1.1.0 printer model


lexmark
.1.3.6.1.2.1.43.11.1.1.8  (max)
.1.3.6.1.2.1.43.11.1.1.9 (curr)


print room (K Drums)
.1.3.6.1.2.1.43.11.1.1.8.1.30 max blk 1
.1.3.6.1.2.1.43.11.1.1.8.1.31 max blk 2

.1.3.6.1.2.1.43.11.1.1.9.1.30 current blk1
.1.3.6.1.2.1.43.11.1.1.9.1.31 current blk2

label printer
.1.3.6.1.2.1.25.3.2.1.3.1 device desc

colour printer

1.1 cyan
1.2 magenta
1.3 yellow
1.4 black

curr
.1.3.6.1.2.1.43.11.1.1.9.1.1
.1.3.6.1.2.1.43.11.1.1.9.1.2
.1.3.6.1.2.1.43.11.1.1.9.1.3
.1.3.6.1.2.1.43.11.1.1.9.1.4

Max
.1.3.6.1.2.1.43.11.1.1.8.1.1
.1.3.6.1.2.1.43.11.1.1.8.1.2
.1.3.6.1.2.1.43.11.1.1.8.1.3
.1.3.6.1.2.1.43.11.1.1.8.1.4
