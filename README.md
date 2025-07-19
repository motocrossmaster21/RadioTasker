# RadioTasker

Sample Android application for API 36 that launches a target package when a specified Bluetooth device connects.

This project demonstrates starting a foreground service when a configured Bluetooth device connects.
If Companion Device association is supported, the app can automatically launch the target package from the foreground service.
For regular audio headsets the association may fail. In that case the app falls back to a normal Bluetooth receiver and still launches the service on connection.
