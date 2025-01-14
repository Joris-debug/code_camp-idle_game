# Official documentation for the BluetoothRepository class by Joris
## About
This class provides a medium-level interface for Bluetooth functionality in your Android application.
It follows the repository design pattern, which gives you an abstraction layer and centralizes all Bluetooth operations.
## Setting up
You can obtain an instance of this class using Dagger/Hilt dependency injection.
Simply modify your ViewModel's constructor as follows:
```
import ...
        
@HiltViewModel
class MyViewModel @Inject constructor(
    private val bluetoothRepository: BluetoothRepository,
    ...,
) : ViewModel() {

     ...
     
}
```
## Discover Devices
If you wish to transfer data, you need to know which device should receive the information.
There are currently two ways to obtain devices:
1. Iterate over the list of already paired devices:
   You can obtain a set of paired devices with `getPairedDevices()`.
   However, there is no guarantee that the device you're searching for is already paired with the device your app is running on.
   Additionally, paired devices may not be in proximity to your device.
2. Discover nearby devices:
   To do so, start discovering devices by calling the function `startBluetoothScan()`.
   Bluetooth scans take about 12 seconds. Be sure to call `stopBluetoothScan()` after you’re done discovering devices.
   All devices that were found can be obtained by calling `getDiscoveredDevices()`.
### Note
`getPairedDevices()` as well as `getDiscoveredDevices()` will give you access to instances of BluetoothDevice.
You will need the desired device object for the next step.
For the discovery process to work, Bluetooth must be enabled.
You can only discover devices that have made themselves discoverable in the system settings.
This can typically be done through a prompt, similar to the process of enabling Bluetooth.
Additionally, you need the following permissions:
* `BLUETOOTH_ADVERTISE`
* `BLUETOOTH_SCAN`
* `BLUETOOTH_CONNECT`

These permissions are required for all the remaining steps.
## Start a Connection
To start a connection, one device will act as the server, while the other will act as the client.  
A device can become a server by calling the function `listenOnServerSocket()`.  
You can check if a client has successfully connected to the server by calling `isConnected()`, which returns `true` in that case, and `false` otherwise.

The client, on the other hand, needs to call the function `connectFromClientSocket(device)`.  
`device` should be the instance of `BluetoothDevice` discussed in the **Discover Devices** section.
If done correctly, a connection should now be established.
### Note
Keep in mind that `listenOnServerSocket()` is a blocking call, terminating once a client connects.
## Transmit Data
Everything we have done so far has led us to the most important aspect of a Bluetooth connection:  
sharing data between the devices.  
To do so, you can call the functions `write(message)` and `read()`.  
`write()` takes a message as a `String`. You can read the message on the receiver side by calling `read()`,  
which reads everything from the buffer and returns it as a `String`.
## Closing a Connection
All good things must come to an end.  
Sooner or later, your devices must part ways.  
To ensure that your repository keeps working as intended, call the function `closeConnection()` on both devices.
## FYI
When calling a suspended function within a ViewModel coroutine, it is recommended to follow this pattern:
```
viewModelScope.launch(Dispatchers.IO) {
    bluetoothRepository.write("Ping - - - Pong")
}
```
`Dispatchers.IO`: This dispatcher is optimized for I/O-bound operations (e.g., network requests, file I/O, or interacting with Bluetooth devices).
By using it, you avoid blocking the main thread, ensuring that UI updates are not delayed and the app remains responsive.