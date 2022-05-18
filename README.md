# SNMPClient
Printer SNMP Client

Get printer info from list of networked printer IP addresses.

TO USE:
Edit printers file and make it a single list of IP addresses, one address per line.
Export as Jar and run.

TODO:

Currently set to SNMP V2C, if it fails it will use V1.

Currently prints to console.
Print an XML file of printer stats. create css to display the XML file.

Potentially use different input method, currently using txt file with each ip address on new line. Is it possible to search AD for all printers in certain subnet?

Clean up toString() method so it works for all printers (curently only works for blk printers)

Run method at set intervals to monitor (or use some sort of windows scheduling?)

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
