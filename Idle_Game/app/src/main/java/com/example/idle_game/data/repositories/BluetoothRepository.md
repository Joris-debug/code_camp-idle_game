# Official documentation for the BluetoothRepository class by Joris
### About
This class provides a medium level interface for bluetooth functionality in your Android Application.
Everything is programmed in the repository design pattern, which gives you an abstraction layer and
centralises all bluetooth operations.
#### Setting up
You can obtain an instance of this class, using dagger/hilt dependency injections.
simply modify your viewmodels constructor as follows:
```kotlin
import ...
        
@HiltViewModel
class MyViewModel @Inject constructor(
    private val bluetoothRepository: BluetoothRepository,
) : ViewModel() {
     ...
}
```