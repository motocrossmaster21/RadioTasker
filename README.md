# RadioTasker

Sample Android application for API 36 that launches a target package when a specified Bluetooth device connects.

This project demonstrates starting a foreground service when a configured Bluetooth device connects.
If the device is paired via the Companion Device API, the service can launch the configured app even when RadioTasker is in the background.
Regular A2DP headsets might not support the association, in which case the receiver still starts the service but Android may block the activity launch.

To ensure the launch works reliably, open the app and use **Pair Device** to register your headphones as a companion. Approve the system dialog once; subsequent connections will then start the target package automatically.
