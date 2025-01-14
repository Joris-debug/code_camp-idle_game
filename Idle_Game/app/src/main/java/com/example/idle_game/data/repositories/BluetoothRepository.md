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
## Discover devices
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
   For the discovery process to work, Bluetooth must be enabled, and you need the following permissions:
   * BLUETOOTH_ADVERTISE
   * BLUETOOTH_SCAN
   * BLUETOOTH_CONNECT